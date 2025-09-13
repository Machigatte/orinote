package com.chalkim.orinote.service;

import com.chalkim.orinote.dto.SearchNoteDto;
import java.time.Instant;
import java.util.List;

import com.chalkim.orinote.dto.NoteDto;
import com.chalkim.orinote.model.Note;

public interface NoteService {

    /**
     * 创建新的笔记
     * @param dto 包含笔记数据的 DTO 对象
     * @return 创建的笔记对象
     */
    Note saveNote(NoteDto dto);

    /**
     * 根据ID获取笔记
     * @param id 笔记的唯一标识符
     * @return 如果找到，返回笔记对象；否则抛出异常
     */
    Note getNoteById(Long id);

    /**
     * 获取所有笔记
     * @return 未被删除的笔记列表
     */
    List<Note> getAllNotes();
    
    /**
     * 高级查询笔记
     * @param from 起始时间(可选)
     * @param to 结束时间(可选)
     * @param noteType 笔记类型(可选)
     * @param keyword 搜索关键字(可选)
     * @return 符合条件的笔记列表
     */
    List<Note> searchNotes(SearchNoteDto searchDto);

    /**
     * 更新笔记
     * @param id 笔记的唯一标识符
     * @param updateDto 包含更新数据的 DTO 对象
     */
    void updateNote(Long id, NoteDto updateDto);

    /**
     * 分析笔记
     * @param id 笔记的唯一标识符
     */
    void analyseNote(Long id);

    /**
     * 逻辑删除笔记
     * @param id 笔记的唯一标识符
     */
    void softDeleteNote(Long id);

    /**
     * 归档笔记
     * @param id 笔记的唯一标识符
     * @param updateDto 包含更新数据的 DTO 对象
     */
    void archiveNote(Long id, NoteDto updateDto);

    /**
     * 分析笔记内容
     * @param id 笔记ID
     * @param content 笔记内容
     */

     void analyseNote(Long id, NoteDto updateDto);


    /**
     * 归档笔记
     * @param id 笔记的唯一标识符
     */
    Note archiveNote(Long id);
}
