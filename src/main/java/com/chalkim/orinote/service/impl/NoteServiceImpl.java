package com.chalkim.orinote.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chalkim.orinote.dto.note.CreateNoteDto;
import com.chalkim.orinote.dto.note.UpdateNoteDto;
import com.chalkim.orinote.exception.ResourceConflictException;
import com.chalkim.orinote.exception.ResourceNotFoundException;
import com.chalkim.orinote.mapper.NoteMapper;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.repository.NoteRepository;
import com.chalkim.orinote.service.NoteService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Validated
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final ChatModel chatModel;

    @Transactional
    public Note createNote(@Valid @NotNull CreateNoteDto dto, User user) {
        Note note = noteMapper.toEntity(dto);
        note.setUser(user);
        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public Note getNoteById(@NotNull Long id, User user) {
        return noteRepository.findByIdAndUserAndIsDeletedFalse(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes(User user) {
        return noteRepository.findAllByUserAndIsDeletedFalseOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Note> searchNotes(Instant from, Instant to, Integer type, String keyword, User user) {
        return noteRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("user"), user));
            predicates = cb.and(predicates, cb.isFalse(root.get("isDeleted")));

            if (from != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("head"), pattern),
                        cb.like(root.get("body"), pattern),
                        cb.like(root.get("tail"), pattern),
                        cb.like(root.get("summary"), pattern)
                ));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return predicates;
        });
    }

    @Transactional
    public Note updateNote(@NotNull Long id, @Valid @NotNull UpdateNoteDto dto, User user) {
        Note note = getNoteById(id, user);

        if (note.getArchivedAt() != null) {
            throw new ResourceConflictException("Cannot update an archived note");
        }

        note.setTitle(dto.getTitle());
        note.setType(dto.getType());
        note.setHead(dto.getHead());
        note.setBody(dto.getBody());
        note.setTail(dto.getTail());
        note.setSummary(dto.getSummary());
        return noteRepository.save(note);
    }

    @Transactional
    public Note archiveNote(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (note.getArchivedAt() == null) {
            note.setArchivedAt(java.time.Instant.now());
            return noteRepository.save(note);
        }
        return note;
    }

    @Transactional
    public Note summarizeNote(@NotNull Long id, User user) {

        Note note = getNoteById(id, user);

        if (note.getArchivedAt() != null) {
            throw new ResourceConflictException("Cannot summarize an archived note");
        }

        String result = ChatClient.create(chatModel).prompt()
                .user(u -> u
                        .text("请为下面文本生成中文分析：{note}")
                        .param("note", note)
                    )
                .call()
                .content();
        note.setSummary(result);
        return noteRepository.save(note);
    }

    @Transactional
    public Flux<String> summarizeNoteStream(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (note.getArchivedAt() != null) {
            throw new ResourceConflictException("Cannot summarize an archived note");
        }

        // 1. 创建原始的冷流 (Cold Stream)
        Flux<String> rawChatStream = ChatClient.create(chatModel).prompt()
                .user(u -> u
                        .text("请为下面文本生成约200字的中文分析：{note} 输出格式为纯文本，不要使用markdown。")
                        .param("note", note.toPrompt())
                )
                .stream()
                .content();

        // 2. 转换为热流 (Hot Stream)，需要两个订阅者才会启动
        // autoConnect(2) 意味着只有当有两个订阅者准备好时，上游 (ChatClient) 才会启动。
        Flux<String> sharedStream = rawChatStream.publish().autoConnect(2);

        // --- 消费者 B：保存流 ---
        // 3. 订阅流，收集内容，然后执行保存
        Mono<Note> saveOperation = sharedStream
                .collect(Collectors.joining())
                .flatMap(fullSummary -> {
                    note.setSummary(fullSummary);
                    return Mono.fromCallable(() -> noteRepository.save(note))
                            .subscribeOn(Schedulers.boundedElastic());
                });

        // 4. 立即订阅保存流，让它等待共享流启动
        // 注意：这里的 .subscribe() 是非阻塞的。
        saveOperation.subscribe(
                savedNote -> System.out.println("Note saved successfully!"), // 成功回调
                error -> System.err.println("Error saving note: " + error.getMessage()) // 错误处理
        );

        // --- 消费者 A：输出流 ---
        // 5. 将共享流返回给 Controller，它将是第二个订阅者，从而触发上游启动。
        return sharedStream;
    }

    @Transactional
    public void softDeleteNote(@NotNull Long id, User user) {
        Note note = getNoteById(id, user);
        if (!note.getIsDeleted()) {
            note.setIsDeleted(true);
            noteRepository.save(note);
        }
    }
}
