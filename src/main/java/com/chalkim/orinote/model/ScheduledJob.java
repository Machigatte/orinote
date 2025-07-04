package com.chalkim.orinote.model;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduledJob {
    @NotNull
    private Long id;
    @NotBlank
    private String jobName;

    @NotBlank
    // TODO: Validate Quartz cron expression format
    private String cron;
    @NotNull
    private Boolean enabled;
}
