package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.medical.smart_medical_server.DTO.DoctorQueryDTO;
import com.medical.smart_medical_server.DTO.GeminiChatDTO;
import com.medical.smart_medical_server.VO.DepartmentVO;
import com.medical.smart_medical_server.VO.DoctorVO;
import com.medical.smart_medical_server.VO.GeminiChatVO;
import com.medical.smart_medical_server.VO.ScheduleLLMVO;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.service.DoctorScheduleService;
import com.medical.smart_medical_server.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI 导诊服务实现（DeepSeek/Gemini）
 * 支持从 Redis 缓存读取科室和医生信息，动态增强提示词
 */
@Slf4j
@Service
public class GeminiServiceImpl implements GeminiService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.max-tokens}")
    private Integer maxTokens;

    @Value("${deepseek.temperature}")
    private Double temperature;

    @Value("${deepseek.system-prompt}")
    private String systemPrompt;

    private final OkHttpClient httpClient;
    private final Gson gson;

    // 注入科室和医生服务（已有 Redis 缓存）
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorScheduleService scheduleService;

    public GeminiServiceImpl() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    @Override
    public GeminiChatVO chat(GeminiChatDTO chatDTO) {
        try {
            log.info("发送消息到 AI API: {}", chatDTO.getMessage());

            // 构建请求体（包含动态上下文）
            JsonObject requestBody = buildRequestBody(chatDTO);

            // 发送 HTTP 请求
            String responseText = sendRequest(requestBody);

            // 解析响应
            return parseResponse(responseText);

        } catch (Exception e) {
            log.error("调用 AI API 失败", e);
            throw new BusinessException("AI 服务暂时不可用，请稍后重试");
        }
    }

    @Override
    public GeminiChatVO recommendDepartment(String symptoms) {
        String prompt = String.format(
                "患者症状描述：%s\n\n请根据以上症状，推荐最合适的就诊科室，并简要说明理由。如果症状严重或紧急，请明确提醒。",
                symptoms
        );

        GeminiChatDTO chatDTO = new GeminiChatDTO();
        chatDTO.setMessage(prompt);

        return chat(chatDTO);
    }

    /**
     * 获取动态上下文（不使用缓存注解，子方法分别缓存）
     */
    @Override
    public String getDynamicContext() {
        StringBuilder context = new StringBuilder();

        // 各子方法已有独立缓存
        context.append(buildDepartmentContext());
        context.append(buildDoctorContext());
        context.append(buildScheduleContext());

        log.debug("动态上下文长度: {} 字符", context.length());
        return context.toString();
    }

    /**
     * 构建科室上下文（仅公开信息）
     */
    private String buildDepartmentContext() {
        StringBuilder sb = new StringBuilder();

        try {
            List<DepartmentVO> departments = departmentService.getAllDepartments();
            if (departments != null && !departments.isEmpty()) {
                sb.append("\n\n# 本院科室信息（共 ").append(departments.size()).append(" 个科室）\n");

                for (DepartmentVO dept : departments) {
                    // ✅ 只使用安全字段：deptName, deptDesc, location
                    sb.append(String.format("- **%s**：%s%s\n",
                            safeString(dept.getDeptName(), "未命名科室"),
                            safeString(dept.getDeptDesc(), "暂无描述"),
                            dept.getLocation() != null ? "（位置：" + dept.getLocation() + "）" : ""
                    ));
                }
            }
        } catch (Exception e) {
            log.warn("获取科室信息失败，跳过科室上下文", e);
        }

        return sb.toString();
    }

    /**
     * 构建医生上下文（仅公开信息，限制数量）
     */
    private String buildDoctorContext() {
        StringBuilder sb = new StringBuilder();

        try {
            DoctorQueryDTO queryDTO = new DoctorQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(30); // 限制最多30个医生

            IPage<DoctorVO> doctorPage = doctorService.queryDoctorPage(queryDTO);

            if (doctorPage != null && !doctorPage.getRecords().isEmpty()) {
                sb.append("\n# 坐诊医生（共 ").append(doctorPage.getTotal()).append(" 位）\n");

                for (DoctorVO doctor : doctorPage.getRecords()) {
                    // ✅ 安全字段：name, deptName, jobTitle, introduction（截断）
                    // ❌ 不传：id, deptId, username, password, phone, createTime

                    String intro = truncate(doctor.getIntroduction(), 40);

                    sb.append(String.format("- %s（%s），%s科%s\n",
                            safeString(doctor.getName(), "医生"),
                            safeString(doctor.getJobTitle(), "医师"),
                            safeString(doctor.getDeptName(), "综合"),
                            intro != null ? "，擅长：" + intro : ""
                    ));
                }
            }
        } catch (Exception e) {
            log.warn("获取医生信息失败，跳过医生上下文", e);
        }

        return sb.toString();
    }

    /**
     * ✅ 新增：构建排班上下文（号源信息）
     */
    private String buildScheduleContext() {
        StringBuilder sb = new StringBuilder();

        try {
            List<ScheduleLLMVO> schedules = scheduleService.getAvailableSchedulesForLLM(30);

            if (schedules != null && !schedules.isEmpty()) {
                sb.append("\n# 近期可预约号源（未来7天）\n");

                // 按日期分组展示
                LocalDate currentDate = null;
                for (ScheduleLLMVO schedule : schedules) {
                    // 日期分隔
                    if (!schedule.getWorkDate().equals(currentDate)) {
                        currentDate = schedule.getWorkDate();
                        sb.append("\n## ").append(schedule.getFormattedDate()).append("\n");
                    }

                    // 排班信息（仅公开字段）
                    sb.append(String.format("- %s %s（%s），%s科，%s\n",
                            schedule.getShiftTypeDesc(),
                            safeString(schedule.getDoctorName(), "医生"),
                            safeString(schedule.getDoctorJobTitle(), "医师"),
                            safeString(schedule.getDeptName(), "综合"),
                            schedule.getQuotaDesc()
                    ));
                }

                sb.append("\n提示：以上为实时号源信息，具体请以预约页面为准。\n");
            } else {
                sb.append("\n# 号源信息\n");
                sb.append("暂无近期可预约的号源，请稍后查看或联系前台。\n");
            }
        } catch (Exception e) {
            log.warn("获取排班信息失败，跳过排班上下文", e);
        }

        return sb.toString();
    }

    /**
     * 构建 API 请求体
     */
    private JsonObject buildRequestBody(GeminiChatDTO chatDTO) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("max_tokens", maxTokens);
        requestBody.addProperty("temperature", temperature);

        JsonArray messages = new JsonArray();

        // ✅ 拼接静态提示词 + 动态上下文（科室、医生信息）
        String dynamicContext = getDynamicContext();
        String fullSystemPrompt = systemPrompt + dynamicContext;

        log.debug("完整提示词长度: {} 字符", fullSystemPrompt.length());

        // 系统消息
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", fullSystemPrompt);
        messages.add(systemMessage);

        // 历史消息
        if (chatDTO.getHistory() != null && !chatDTO.getHistory().isEmpty()) {
            for (GeminiChatDTO.ChatMessage msg : chatDTO.getHistory()) {
                JsonObject historyMessage = new JsonObject();
                historyMessage.addProperty("role", msg.getRole().equals("user") ? "user" : "assistant");
                historyMessage.addProperty("content", msg.getContent());
                messages.add(historyMessage);
            }
        }

        // 当前用户消息
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", chatDTO.getMessage());
        messages.add(userMessage);

        requestBody.add("messages", messages);

        return requestBody;
    }

    /**
     * 发送 HTTP 请求到 AI API
     */
    private String sendRequest(JsonObject requestBody) throws IOException {
        log.info("请求 URL: {}", apiUrl);

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                log.error("AI API 请求失败: {}", response.code());
                log.error("错误响应: {}", responseBody);
                throw new BusinessException("AI 服务请求失败");
            }

            log.debug("AI API 响应: {}", responseBody);
            return responseBody;
        }
    }

    /**
     * 解析 API 响应
     */
    private GeminiChatVO parseResponse(String responseText) {
        JsonObject jsonResponse = gson.fromJson(responseText, JsonObject.class);

        String reply = "";
        if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
            JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
            if (choice.has("message")) {
                JsonObject message = choice.getAsJsonObject("message");
                reply = message.get("content").getAsString();
            }
        }

        String recommendedDepartment = extractDepartment(reply);
        Boolean urgent = checkUrgency(reply);

        return GeminiChatVO.builder()
                .reply(reply)
                .recommendedDepartment(recommendedDepartment)
                .urgent(urgent)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 从回复中提取推荐科室
     */
    private String extractDepartment(String reply) {
        String[] departments = {
                "内科", "外科", "儿科", "妇产科", "骨科", "眼科", "耳鼻喉科",
                "口腔科", "皮肤科", "神经内科", "心血管内科", "消化内科", "呼吸内科",
                "泌尿外科", "急诊科", "中医科", "神经科", "心血管科"
        };

        for (String dept : departments) {
            if (reply.contains(dept)) {
                return dept;
            }
        }
        return null;
    }

    /**
     * 检测是否紧急情况
     */
    private Boolean checkUrgency(String reply) {
        String[] urgentKeywords = {"紧急", "立即", "马上", "尽快", "急诊", "危险", "120", "急救"};
        for (String keyword : urgentKeywords) {
            if (reply.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // ========== 工具方法 ==========

    /**
     * 安全获取字符串，防止 null
     */
    private String safeString(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * 截断字符串
     */
    private String truncate(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}