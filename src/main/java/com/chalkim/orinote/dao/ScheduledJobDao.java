package com.chalkim.orinote.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chalkim.orinote.model.ScheduledJob;

@Repository
public class ScheduledJobDao {

    private final JdbcTemplate jdbc;

    public ScheduledJobDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM scheduled_jobs WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public ScheduledJob getJobById(Long id) {
        String sql = "SELECT * FROM scheduled_jobs WHERE id = ?";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(ScheduledJob.class), id);
    }

    public List<ScheduledJob> getAllJobs() {
        String sql = "SELECT * FROM scheduled_jobs ORDER BY id";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(ScheduledJob.class));
    }

    public List<ScheduledJob> getEnabledJobs() {
        String sql = "SELECT * FROM scheduled_jobs WHERE enabled = true ORDER BY id";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(ScheduledJob.class));
    }

    public List<ScheduledJob> getDisabledJobs() {
        String sql = "SELECT * FROM scheduled_jobs WHERE enabled = false ORDER BY id";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(ScheduledJob.class));
    }

    public ScheduledJob createJob(ScheduledJob job) {
        String sql = "INSERT INTO scheduled_jobs (job_name, cron, enabled) VALUES (?, ?, ?) RETURNING *";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(ScheduledJob.class), 
                                    job.getJobName(), job.getCron(), job.getEnabled());
    }

    public int updateJob(Long id, ScheduledJob job) {
        String sql = "UPDATE scheduled_jobs SET job_name = COALESCE(?, job_name), cron = COALESCE(?, cron), enabled = COALESCE(?, enabled) WHERE id = ?";
        return jdbc.update(sql, job.getJobName(), job.getCron(), job.getEnabled(), id);
    }

    public int deleteJob(Long id) {
        String sql = "DELETE FROM scheduled_jobs WHERE id = ?";
        return jdbc.update(sql, id);
    }

    public int enableJob(Long id) {
        String sql = "UPDATE scheduled_jobs SET enabled = true WHERE id = ?";
        return jdbc.update(sql, id);
    }

    public int disableJob(Long id) {
        String sql = "UPDATE scheduled_jobs SET enabled = false WHERE id = ?";
        return jdbc.update(sql, id);
    }
}
