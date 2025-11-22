package com.chalkim.orinote.service;

import java.time.Instant;
import java.util.List;

import com.chalkim.orinote.dto.note.CreateNoteDto;
import com.chalkim.orinote.dto.note.UpdateNoteDto;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.User;
import reactor.core.publisher.Flux;

public interface NoteService {

    /**
     * 创建新的笔记
     */
    Note createNote(CreateNoteDto dto, User user);

    /**
     * 根据ID获取笔记
     */
    Note getNoteById(Long id, User user);

    /**
     * 获取所有笔记
     */
    List<Note> getAllNotes(User user);
    
    /**
     * 高级查询笔记
     * @param from 起始时间(可选)
     * @param to 结束时间(可选)
     * @param type 笔记类型(可选)
     * @param keyword 搜索关键字(可选)
     * @return 符合条件的笔记列表
     */
    List<Note> searchNotes(Instant from, Instant to, Integer type, String keyword, User user);

    /**
     * 更新笔记
     */
    Note updateNote(Long id, UpdateNoteDto dto, User user);

    /**
     * 总结笔记
     */
    Note summarizeNote(Long id, User user);

    /**
     * 总结笔记（流式）
     */
    Flux<String> summarizeNoteStream(Long id, User user);

    /**
     * 逻辑删除笔记
     */
    void softDeleteNote(Long id, User user);

    /**
     * 归档笔记
     */
    Note archiveNote(Long id, User user);
}
