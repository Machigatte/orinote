package com.chalkim.orinote.job;

import java.time.Instant;
import java.util.Date;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalkim.orinote.service.SummaryService;

public class GenerateSummaryJob implements Job{
    private static final Logger logger = LoggerFactory.getLogger(GenerateSummaryJob.class);

    private final SummaryService summaryService;

    public GenerateSummaryJob(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @Override
    public void execute(org.quartz.JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        logger.info("Executing GenerateSummaryJob({}) at " + new java.util.Date(), jobName);
        
        try {
            // TODO: 不应该使用cron表达式，无法判断总结摘要的区间，以下方法产生的区间不稳定。
            Date from = context.getPreviousFireTime(); // 上一次任务执行时间
            Date to = context.getFireTime();           // 本次任务执行时间

            if (from != null) {
                summaryService.generateSummaryBetween(from.toInstant(), to.toInstant());
                logger.info("Summary generated successfully.");
            } else {
                logger.warn("First execution, no previous fire time available. Skipping summary generation.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error in GenerateSummaryJob", e);
        }
    }
}
