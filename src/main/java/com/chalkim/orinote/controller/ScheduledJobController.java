package com.chalkim.orinote.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.model.ScheduledJob;
import com.chalkim.orinote.service.ScheduledJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Validated
@RestController
@RequestMapping("/jobs")
@Tag(name = "ScheduledJob API", description = "管理定时任务的增删查改接口")
public class ScheduledJobController {

    private final ScheduledJobService scheduledJobService;

    public ScheduledJobController(ScheduledJobService scheduledJobService) {
        this.scheduledJobService = scheduledJobService;
    }
    
    @Operation(summary = "列出定时任务", description = "根据参数返回所有定时任务或仅启用的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回定时任务列表", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ScheduledJob.class)))),
    })
    @GetMapping
    public List<ScheduledJob> getJobs(@RequestParam(name = "enabled", required = false) Boolean enabled) {
        if (enabled == null) {
            return scheduledJobService.getAllJobs();
        } else if (enabled) {
            return scheduledJobService.getEnabledJobs();
        } else {
            return scheduledJobService.getDisabledJobs();
        }
    }

    @Operation(summary = "获取定时任务详情", description = "根据ID获取指定的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功返回定时任务", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScheduledJob.class))),
        @ApiResponse(responseCode = "404", description = "定时任务未找到", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{id}")
    public ScheduledJob getJobById(@PathVariable("id") Long id) {
        return scheduledJobService.getJobById(id);
    }

    @Operation(summary = "创建定时任务", description = "创建一个新的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "成功创建定时任务", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScheduledJob.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduledJob createJob(@RequestBody @Valid ScheduledJob job) {
        return scheduledJobService.createJob(job.getJobName(), job.getCron());
    }

    @Operation(summary = "更新定时任务", description = "根据ID更新指定的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功更新定时任务", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScheduledJob.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "定时任务未找到", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}")
    public void patchJob(@PathVariable("id") Long id, @RequestBody @Valid ScheduledJob job) {
        scheduledJobService.patchJob(id, job.getJobName(), job.getCron());
    }

    @Operation(summary = "删除定时任务", description = "根据ID删除指定的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功删除定时任务"),
        @ApiResponse(responseCode = "404", description = "定时任务未找到", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable("id") Long id) {
        scheduledJobService.deleteJob(id);
    }

    @Operation(summary = "启用定时任务", description = "根据ID启用指定的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功启用定时任务"),
        @ApiResponse(responseCode = "404", description = "定时任务未找到", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}/enable")
    public void enableJob(@PathVariable("id") Long id) {
        scheduledJobService.enableJob(id);
    }

    @Operation(summary = "禁用定时任务", description = "根据ID禁用指定的定时任务")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "成功禁用定时任务"),
        @ApiResponse(responseCode = "404", description = "定时任务未找到", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{id}/disable")
    public void disableJob(@PathVariable("id") Long id) {
        scheduledJobService.disableJob(id);
    }
}
