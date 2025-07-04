package com.chalkim.orinote.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chalkim.orinote.dao.ScheduledJobDao;
import com.chalkim.orinote.exception.ScheduledJobNotFoundException;
import com.chalkim.orinote.model.ScheduledJob;
import com.chalkim.orinote.service.ScheduledJobService;

@Service
public class ScheduledJobServiceImpl implements ScheduledJobService{

    private final ScheduledJobDao scheduledJobDao;

    public ScheduledJobServiceImpl(ScheduledJobDao scheduledJobDao) {
        this.scheduledJobDao = scheduledJobDao;
    }

    @Override
    public ScheduledJob createJob(String jobName, String cron) {
        ScheduledJob job = new ScheduledJob();
        job.setJobName(jobName);
        job.setCron(cron);
        job.setEnabled(true);
        return scheduledJobDao.createJob(job);
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

    @Override
    public void patchJob(Long id, String jobName, String cron) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        ScheduledJob job = scheduledJobDao.getJobById(id);
        job.setJobName(jobName);
        job.setCron(cron);
        scheduledJobDao.updateJob(id, job);
    }

    @Override
    public void deleteJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.deleteJob(id);
    }

    @Override
    public void enableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.enableJob(id);
    }

    @Override
    public void disableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.disableJob(id);
    }
}
