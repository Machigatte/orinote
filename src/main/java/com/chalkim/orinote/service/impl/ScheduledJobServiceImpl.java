package com.chalkim.orinote.service.impl;

import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.ScheduledJobDao;
import com.chalkim.orinote.dto.ScheduledJobDto;
import com.chalkim.orinote.exception.ScheduledJobNotFoundException;
import com.chalkim.orinote.job.GenerateSummaryJob;
import com.chalkim.orinote.model.ScheduledJob;
import com.chalkim.orinote.service.ScheduledJobService;

import jakarta.annotation.PostConstruct;

@Service
public class ScheduledJobServiceImpl implements ScheduledJobService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledJobServiceImpl.class);

    private final ScheduledJobDao scheduledJobDao;
    private final Scheduler scheduler;

    public ScheduledJobServiceImpl(ScheduledJobDao scheduledJobDao, Scheduler scheduler) {
        this.scheduledJobDao = scheduledJobDao;
        this.scheduler = scheduler;
    }

    @PostConstruct
    private void scheduleJobsFromDB() {
        List<ScheduledJob> jobs = scheduledJobDao.getEnabledJobs();
        for (ScheduledJob job : jobs) {
            try {
                scheduleJob(job);
            } catch (SchedulerException e) {
                log.error("调度任务失败: {}", job.getJobName(), e);
            }
        }
    }

    private void scheduleJob(ScheduledJob job) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(GenerateSummaryJob.class)
                .withIdentity(job.getId() + "_detail", "DEFAULT")
                .usingJobData("jobName", job.getJobName())
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getId() + "_trigger", "DEFAULT")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void rescheduleJob(Long jobId, String newCron) throws SchedulerException {
        String triggerName = jobId + "_trigger";
        String group = "DEFAULT";

        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, group);

        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                .build();

        // 调用 Quartz API 重新调度
        Date rescheduleDate = scheduler.rescheduleJob(triggerKey, newTrigger);

        if (rescheduleDate == null) {
            throw new SchedulerException("Trigger with key " + triggerKey + " not found.");
        }
    }

    @Transactional
    @Override
    public ScheduledJob createJob(ScheduledJobDto dto) {
        try {
            ScheduledJob job = scheduledJobDao.createJob(dto);
            ;
            scheduleJob(job);
            return job;
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule job: " + dto.getJobName(), e);
        }
    }

    @Override
    public ScheduledJob getJobById(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        return scheduledJobDao.getJobById(id);
    }

    @Override
    public List<ScheduledJob> getAllJobs() {
        return scheduledJobDao.getAllJobs();
    }

    @Override
    public List<ScheduledJob> getEnabledJobs() {
        return scheduledJobDao.getEnabledJobs();
    }

    @Override
    public List<ScheduledJob> getDisabledJobs() {
        return scheduledJobDao.getDisabledJobs();
    }

    @Transactional
    @Override
    public void updateJob(Long id, ScheduledJobDto dto) {
        ScheduledJob oldJob = scheduledJobDao.getJobById(id);
        if (oldJob == null) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        try {
            if (!oldJob.getCron().equals(dto.getCron())) {
                rescheduleJob(id, dto.getCron());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to reschedule job with ID " + id, e);
        }
        scheduledJobDao.updateJob(id, dto);
    }

    @Transactional
    @Override
    public void deleteJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.deleteJob(id);
        try {
            JobKey jobKey = JobKey.jobKey(id + "_detail", "DEFAULT");
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to delete job with ID " + id, e);
        }
    }

    @Transactional
    @Override
    public void enableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.enableJob(id);
        ScheduledJob job = scheduledJobDao.getJobById(id);
        try {
            scheduleJob(job);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to enable and schedule job with ID " + id, e);
        }
    }

    @Transactional
    @Override
    public void disableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.disableJob(id);
        try {
            JobKey jobKey = JobKey.jobKey(id + "_detail", "DEFAULT");
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to disable job with ID " + id, e);
        }
    }
}
