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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.SummaryDao;
import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.exception.SummaryNotFoundException;
import com.chalkim.orinote.mapper.SummaryMapper;
import com.chalkim.orinote.model.Note;
import com.chalkim.orinote.model.Summary;
import com.chalkim.orinote.service.NoteService;
import com.chalkim.orinote.service.SummaryService;

@Service
public class SummaryServiceImpl implements SummaryService {
    private final SummaryDao summaryDao;

    private final NoteService noteService;
    private final ChatClient chatClient;

    private final SummaryMapper summaryMapper;

    @Value("classpath:/prompts/summarize-prompt.st")
    private Resource summarizePrompt;

    public SummaryServiceImpl(SummaryDao summaryDao, NoteService noteService, ChatClient.Builder chatClientBuilder,
            SummaryMapper summaryMapper) {
        this.summaryDao = summaryDao;
        this.noteService = noteService;
        this.chatClient = chatClientBuilder.build();
        this.summaryMapper = summaryMapper;
    }

    private Summary generateSummary(List<Note> notes) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(summarizePrompt);

        Prompt prompt = systemPromptTemplate.create(Map.of("notes", notes));

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
    public Summary generateSummaryBetween(Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }

        List<Note> notes = noteService.getNotesBetween(from, to);
        if (notes.isEmpty()) {
            throw new IllegalArgumentException("No notes found between " + from + " and " + to);
        }

        Summary summary = generateSummary(notes);
        summary.setStartAt(from);
        summary.setEndAt(to);
        return summaryDao.createSummary(summaryMapper.summaryToSummaryCreateDto(summary));
    }

    @Override
    public List<Summary> getAllSummaries() {
        return summaryDao.getAllSummaries();
    }

    @Override
    @Transactional
    public Summary saveSummary(SummaryCreateDto dto) {
        return summaryDao.createSummary(dto);
    }

    @Override
    public Summary getSummaryById(Long id) {
        try {
            return summaryDao.getSummaryById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new SummaryNotFoundException("Summary with ID " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void patchSummary(Long id, SummaryUpdateDto dto) {
        boolean exists = summaryDao.existsById(id);
        if (!exists) {
            throw new SummaryNotFoundException("Summary with ID " + id + " not found");
        }

        summaryDao.updateSummary(id, dto);
    }

    @Override
    @Transactional
    public void softDeleteSummary(Long id) {
        int rows = summaryDao.softDeleteSummary(id);
        if (rows == 0) {
            throw new SummaryNotFoundException("Summary with ID " + id + " not found");
        }
    }

    @Override
    public List<Summary> getSummaryCreatedBetween(Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }
        return summaryDao.getSummaryCreatedBetween(from, to);
    }
}
