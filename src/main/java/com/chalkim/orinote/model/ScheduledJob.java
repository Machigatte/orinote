package com.chalkim.orinote.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduledJob {
    private Long id;
    private String jobName;
    private String cron;
    private Boolean enabled;
}
