package com.chalkim.orinote.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.chalkim.orinote.dto.NoteCreateDto;
import com.chalkim.orinote.dto.NoteUpdateDto;
import com.chalkim.orinote.model.Note;

public interface NoteService {

    /**
     * 创建新的笔记
     * @param note 要创建的笔记对象
     * @return 创建的笔记对象
     */
    Note saveNote(NoteCreateDto dto);

    /**
     * 根据ID获取笔记
     * @param id 笔记ID
     * @return 笔记对象
     */
    Note getNoteById(Long id);

    /**
     * 获取所有笔记
     * @return 笔记列表
     */
    List<Note> getAllNotes();

    /**
     * 获取指定时间范围内的笔记
     * @param from 起始时间
     * @param to 结束时间
     * @return 笔记列表
     */
    List<Note> getNotesBetween(Instant from, Instant to);

    /**
     * 更新笔记
     * @param id 笔记ID
     * @param title 新标题
     * @param content 新内容
     */
    void updateNote(Long id, NoteUpdateDto updateDto);

    /**
     * 逻辑删除笔记
     * @param id 笔记ID
     */
    void softDeleteNote(Long id);
}
