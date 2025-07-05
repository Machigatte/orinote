package com.chalkim.orinote.service;

import java.util.List;

import com.chalkim.orinote.dto.ScheduledJobDto;
import com.chalkim.orinote.model.ScheduledJob;

public interface ScheduledJobService {
    /**
     * 创建新的定时任务
     * @param dto 包含任务数据的 DTO 对象
     * @return 创建的定时任务对象
     */
    ScheduledJob createJob(ScheduledJobDto dto);

    /**
     * 根据ID获取定时任务
     * @param id 定时任务的唯一标识符
     * @return 如果找到，返回定时任务对象；否则抛出异常
     */
    ScheduledJob getJobById(Long id);

    /**
     * 获取所有定时任务
     * @return 未被删除的定时任务列表
     */
    List<ScheduledJob> getAllJobs();

    /**
     * 获取启用的定时任务
     * @return 启用状态的定时任务列表
     */
    List<ScheduledJob> getEnabledJobs();

    /**
     * 获取禁用的定时任务
     * @return 禁用状态的定时任务列表
     */
    List<ScheduledJob> getDisabledJobs();

    /**
     * 更新定时任务
     * @param id 定时任务的唯一标识符
     * @param dto 包含更新数据的 DTO 对象
     */
    void updateJob(Long id, ScheduledJobDto dto);

    /**
     * 删除定时任务
     * @param id 定时任务的唯一标识符
     */
    void deleteJob(Long id);

    /**
     * 启用定时任务
     * @param id 定时任务的唯一标识符
     */
    void enableJob(Long id);

    /**
     * 禁用定时任务
     * @param id 定时任务的唯一标识符
     */
    void disableJob(Long id);
}
