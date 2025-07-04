package com.chalkim.orinote.controller;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.chalkim.orinote.dto.SummaryCreateDto;
import com.chalkim.orinote.dto.SummaryUpdateDto;
import com.chalkim.orinote.model.Summary;
import com.chalkim.orinote.service.SummaryService;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Validated
@RestController
@RequestMapping("/summaries")
@Tag(name = "Summary API", description = "管理总结的增删查改接口")
public class SummaryController {
    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @Operation(summary = "根据时间范围生成总结", description = "传入起止时间，生成该时间段内的笔记总结")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功生成总结", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Summary.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/generate")
    public Summary generateSummaryBetween(
            @RequestParam("from") @NotNull Instant from,
            @RequestParam("to") @NotNull Instant to) {
        return summaryService.generateSummaryBetween(from, to);
    }

    @Operation(summary = "保存一条总结", description = "创建一条新的总结并返回创建的总结")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "成功创建总结", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Summary.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Summary> saveSummary(@RequestBody @Valid SummaryCreateDto dto) {
        Summary saved = summaryService.saveSummary(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "列出所有总结", description = "返回所有总结的列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回总结列表", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Summary.class)))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<Summary> getAllSummaries() {
        return summaryService.getAllSummaries();
    }

    @Operation(summary = "根据ID获取总结", description = "通过总结ID获取总结详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回总结详情", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Summary.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的总结", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Summary getSummaryById(@PathVariable("id") Long id) {
        return summaryService.getSummaryById(id);
    }

    @Operation(summary = "根据ID更新总结", description = "更新指定ID的总结")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功更新总结"),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的总结", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchSummary(@PathVariable("id") Long id, @RequestBody @Valid SummaryUpdateDto dto) {
        summaryService.patchSummary(id, dto);
    }

    @Operation(summary = "根据ID删除总结", description = "软删除指定ID的总结")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "成功删除总结"),
            @ApiResponse(responseCode = "404", description = "未找到指定ID的总结", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public void softDeleteSummary(@PathVariable("id") Long id) {
        summaryService.softDeleteSummary(id);
    }

    @Operation(summary = "获取指定时间范围内的总结", description = "传入起止时间，获取该时间段内的所有总结")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功返回总结列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Summary.class))),
            @ApiResponse(responseCode = "400", description = "请求参数无效", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/range")
    public List<Summary> getSummariesBetween(
            @RequestParam("from") @NotNull Instant from,
            @RequestParam("to") @NotNull Instant to) {
        return summaryService.getSummaryCreatedBetween(from, to);
    }
}
