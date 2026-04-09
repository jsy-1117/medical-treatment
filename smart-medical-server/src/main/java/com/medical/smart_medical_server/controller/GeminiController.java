package com.medical.smart_medical_server.controller;

import com.medical.smart_medical_server.DTO.GeminiChatDTO;
import com.medical.smart_medical_server.VO.GeminiChatVO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 智能导诊控制器
 * 基于 Gemini AI 提供智能导诊服务
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    /**
     * 智能对话接口
     *
     * @param chatDTO 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public Result<GeminiChatVO> chat(@Valid @RequestBody GeminiChatDTO chatDTO) {
        log.info("收到智能导诊请求: {}", chatDTO.getMessage());
        GeminiChatVO response = geminiService.chat(chatDTO);
        return Result.success(response);
    }

    /**
     * 科室推荐接口
     *
     * @param symptoms 症状描述
     * @return 推荐的科室和建议
     */
    @PostMapping("/recommend")
    public Result<GeminiChatVO> recommendDepartment(@RequestParam String symptoms) {
        log.info("收到科室推荐请求，症状: {}", symptoms);
        GeminiChatVO response = geminiService.recommendDepartment(symptoms);
        return Result.success(response);
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("AI 导诊服务运行正常");
    }
}