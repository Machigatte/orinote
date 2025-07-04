package com.chalkim.orinote.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;
import com.chalkim.orinote.service.SummaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/summaries")
@Tag(name = "Summary API", description = "管理总结的增删查改接口")
public class SummaryController {
    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @Operation(summary = "根据时间范围生成总结", description = "传入起止时间，生成该时间段内的笔记总结")
    @GetMapping("/generate")
    public Optional<Summary> generateSummaryBetween(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return summaryService.generateSummaryBetween(from, to);
    }

    @Operation(summary = "保存一条总结")
    @PostMapping
    public Summary saveSummary(@RequestBody SummaryCreateDto dto) {
        System.out.println(dto);
        return summaryService.saveSummary(dto);
    }

    @Operation(summary = "列出所有总结")
    @GetMapping
    public List<Summary> getAllSummaries() {
        return summaryService.getAllSummaries();
    }

    @Operation(summary = "根据ID获取总结")
    @GetMapping("/{id}")
    public Summary getSummaryById(@PathVariable("id") Long id) {
        return summaryService.getSummaryById(id);
    }

    @Operation(summary = "根据ID更新总结")
    @PatchMapping("/{id}")
    public void updateSummary(@PathVariable("id") Long id, @RequestBody SummaryUpdateDto dto) {
        summaryService.updateSummary(id, dto);
    }

    @Operation(summary = "根据ID删除总结")
    @DeleteMapping("/{id}")
    public void softDeleteSummary(@PathVariable("id") Long id) {
        summaryService.softDeleteSummary(id);
    }

    @Operation(summary = "获取指定时间范围内的总结", description = "传入起止时间，获取该时间段内的所有总结")
    @GetMapping("/range")
    public List<Summary> getSummariesBetween(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return summaryService.getSummaryCreatedBetween(from, to);
    }
}
