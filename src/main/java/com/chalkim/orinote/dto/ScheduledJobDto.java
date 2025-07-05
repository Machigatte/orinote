package com.chalkim.orinote.dto;

import com.chalkim.orinote.validator.ValidCron;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduledJobDto {
    @NotBlank
    @Schema(
        description = "定时任务的名称，用于标识和描述该任务。",
        example = "DailyBackupJob"
    )
    private String jobName;

    @NotBlank
    @ValidCron
    @Schema(
        description = "Cron 表达式，用于定时任务调度。例如：'0 0 12 * * ?' 表示每天中午12点。",
        example = "\"0 0 12 * * ?\""
    )
    private String cron;

    @NotNull
    @Schema(
        description = "任务是否启用。启用时，任务会按照 Cron 表达式定时执行；禁用时，任务不会执行。",
        example = "true"
    )
    private Boolean enabled;
}
