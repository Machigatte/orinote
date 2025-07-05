package com.chalkim.orinote.service;

import java.time.Instant;
import java.util.List;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;

public interface SummaryService {
    /**
     * 生成总结
     * @param from 起始时间（必须早于结束时间）
     * @param to 结束时间
     * @return 生成的总结对象
     */
    Summary generateSummaryBetween(Instant from, Instant to);

    /**
     * 保存一条手动输入的总结。
     * @param dto 包含总结数据的 DTO 对象
     * @return 保存后的总结对象
     */
    Summary saveSummary(SummaryCreateDto dto);

    /**
     * 根据 ID 获取总结。
     * @param id 总结的唯一标识符
     * @return 如果找到，返回总结对象；否则抛出异常
     */
    Summary getSummaryById(Long id);

    /**
     * 获取所有总结。
     * @return 未被删除的总结列表
     */
    List<Summary> getAllSummaries();

    /**
     * 获取指定时间范围内的总结。
     * @param from 起始时间（必须早于结束时间）
     * @param to 结束时间
     * @return 指定时间范围内的总结列表
     */
    List<Summary> getSummaryCreatedBetween(Instant from, Instant to);

    /**
     * 更新总结。
     * @param id 总结的唯一标识符
     * @param dto 包含更新数据的 DTO 对象
     */
    void updateSummary(Long id, SummaryUpdateDto dto);

    /**
     * 逻辑删除总结。
     * @param id 总结的唯一标识符
     */
    void softDeleteSummary(Long id);

}
