package com.chalkim.orinote.dto;

import com.chalkim.orinote.validator.ValidCron;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduledJobDto {
    @NotBlank
    private String jobName;

    @NotBlank
    @ValidCron
    private String cron;

    @NotNull
    private Boolean enabled;
}
