package com.chalkim.orinote.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;

public interface SummaryService {
    /**
     * 生成总结
     * @param notes 要生成总结的笔记列表
     * @return 生成的总结对象
     */
    Optional<Summary> generateSummaryBetween(Instant from, Instant to);

    /**
     * 保存一条手动输入的总结。
     * @param summary 要保存的总结对象
     * @return 保存后的总结对象
     */
    Summary saveSummary(SummaryCreateDto dto);

    /**
     * 根据 ID 获取总结。
     * @param id 总结的唯一标识符
     * @return 包含总结的 Optional 对象，如果不存在则返回空
     */
    Optional<Summary> getSummaryById(Long id);

    /**
     * 获取所有总结。
     * @return 所有总结的列表
     */
    List<Summary> getAllSummaries();

    /**
     * 获取指定时间范围内的总结。
     * @param from 起始时间
     * @param to 结束时间
     * @return 指定时间范围内的总结列表
     */
    List<Summary> getSummaryCreatedBetween(Instant from, Instant to);

    /**
     * 更新总结。
     * @param id 要更新的总结 ID
     * @param dto 包含更新信息的 DTO 对象
     */
    void updateSummary(Long id, SummaryUpdateDto dto);

    /**
     * 逻辑删除总结。
     * @param id 要删除的总结 ID
     */
    void softDeleteSummary(Long id);
}
