package com.chalkim.orinote.service;

import java.util.List;

import com.chalkim.orinote.model.ScheduledJob;

public interface ScheduledJobService {
    /**
     * 创建新的定时任务
     * @param jobName 任务名称
     * @param cron 表达式
     * @return 创建的定时任务对象
     */
    ScheduledJob createJob(String jobName, String cron);


    /**
     * 根据ID获取定时任务
     * @param id 任务ID
     * @return 定时任务对象
     */
    ScheduledJob getJobById(Long id);

    /**
     * 获取所有定时任务
     * @return 定时任务列表
     */
    List<ScheduledJob> getAllJobs();

    /**
     * 获取启用的定时任务
     * @return 启用的定时任务列表
     */
    List<ScheduledJob> getEnabledJobs();

    /**
     * 获取禁用的定时任务
     * @return 禁用的定时任务列表
     */
    List<ScheduledJob> getDisabledJobs();

    /**
     * 更新定时任务
     * @param id 任务ID
     * @param jobName 新的任务名称
     * @param cron 新的cron表达式
     */
    void patchJob(Long id, String jobName, String cron);

    /**
     * 删除定时任务
     * @param id 任务ID
     */
    void deleteJob(Long id);

    /**
     * 启用定时任务
     * @param id 任务ID
     */
    void enableJob(Long id);

    /**
     * 禁用定时任务
     * @param id 任务ID
     */
    void disableJob(Long id);
}
