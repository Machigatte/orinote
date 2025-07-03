package com.chalkim.orinote.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.SummaryDao;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.Summary;
import com.chalkim.orinote.service.NoteService;
import com.chalkim.orinote.service.SummaryService;

@Service
public class SummaryServiceImpl implements SummaryService{
    private final SummaryDao summaryDao;

    private final NoteService noteService;
    private final ChatClient chatClient;

    @Value("classpath:/prompts/summarize-prompt.st")
    private Resource summarizePrompt;

    public SummaryServiceImpl(SummaryDao summaryDao, NoteService noteService, ChatClient.Builder chatClientBuilder) {
        this.summaryDao = summaryDao;
        this.noteService = noteService;
        this.chatClient = chatClientBuilder.build();
    }

    private Summary generateSummary(List<Note> notes) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(summarizePrompt);

        Prompt prompt = systemPromptTemplate.create(Map.of("notes", notes));;

        System.out.println("Prompt: " + prompt);
        
        String content = this.chatClient.prompt(prompt).call().content();

        Summary summary = new Summary();
        summary.setCreatedAt(Instant.now());
        summary.setTitle("自动生成的总结");
        summary.setContent(content);

        return summary;
    }

    @Override
    @Transactional
    public Optional<Summary> generateSummaryBetween(Instant from, Instant to) {
        List<Note> notes = noteService.getNotesBetween(from, to);
        if (notes.isEmpty()) {
            return Optional.empty();
        } else {
            Summary summary = generateSummary(notes);
            return Optional.of(summaryDao.createSummary(summary.getTitle(), summary.getContent(), summary.getStartAt(), summary.getEndAt()));
        }
    }

    @Override
    public List<Summary> getAllSummaries() {
        return summaryDao.getAllSummaries();
    }

    @Override
    @Transactional
    public Summary saveSummary(Summary summary) {
        return summaryDao.createSummary(summary.getTitle(), summary.getContent(), summary.getStartAt(), summary.getEndAt());
    }

    @Override
    public Optional<Summary> getSummaryById(Long id) {
        Summary summary = summaryDao.getSummaryById(id);
        if (summary != null) {
            return Optional.of(summary);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void updateSummary(Long id, String title, String content) {
        Summary existingSummary = summaryDao.getSummaryById(id);
        if (existingSummary != null) {
            summaryDao.updateSummary(id, title, content);
        } else {
            throw new RuntimeException("Summary not found");
        }
    }

    @Override
    @Transactional
    public void softDeleteSummary(Long id) {
        Summary summary = summaryDao.getSummaryById(id);
        if (summary != null) {
            summaryDao.softDeleteSummary(id);
        } else {
            throw new RuntimeException("Summary not found");
        }
    }

    @Override
    public List<Summary> getSummaryCreatedBetween(Instant from, Instant to) {
        return summaryDao.getSummaryCreatedBetween(from, to);
    }
}
