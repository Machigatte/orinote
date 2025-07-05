package com.chalkim.orinote.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.ScheduledJobDao;
import com.chalkim.orinote.dto.ScheduledJobDto;
import com.chalkim.orinote.exception.ScheduledJobNotFoundException;
import com.chalkim.orinote.model.ScheduledJob;
import com.chalkim.orinote.service.ScheduledJobService;

@Service
public class ScheduledJobServiceImpl implements ScheduledJobService{

    private final ScheduledJobDao scheduledJobDao;

    public ScheduledJobServiceImpl(ScheduledJobDao scheduledJobDao) {
        this.scheduledJobDao = scheduledJobDao;
    }

    @Transactional
    @Override
    public ScheduledJob createJob(ScheduledJobDto dto) {
        return scheduledJobDao.createJob(dto);
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
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
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
    }

    @Transactional
    @Override
    public void enableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.enableJob(id);
    }

    @Transactional
    @Override
    public void disableJob(Long id) {
        if (!scheduledJobDao.existsById(id)) {
            throw new ScheduledJobNotFoundException("Scheduled job with ID " + id + " not found.");
        }
        scheduledJobDao.disableJob(id);
    }
}
