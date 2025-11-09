package org.example.travel.service;

import org.springframework.stereotype.Service;
import org.json.JSONObject;

@Service
public class AIResultFormatter {

    /**
     * 格式化大语言模型生成的旅行计划结果。
     * @param rawResult 大语言模型返回的原始结果（字符串形式）。
     * @return 格式化后的 JSON 对象，包含交通、住宿、景点和餐厅信息。
     */
    public JSONObject formatAIResult(String rawResult) {
        // 示例解析逻辑，假设原始结果是一个结构化的字符串
        JSONObject formattedResult = new JSONObject();

        // 假设原始结果包含交通、住宿、景点和餐厅信息的段落
        String[] sections = rawResult.split("\n\n");
        for (String section : sections) {
            if (section.startsWith("交通:")) {
                formattedResult.put("transportation", section.substring(3).trim());
            } else if (section.startsWith("住宿:")) {
                formattedResult.put("accommodation", section.substring(3).trim());
            } else if (section.startsWith("景点:")) {
                formattedResult.put("attractions", section.substring(3).trim());
            } else if (section.startsWith("餐厅:")) {
                formattedResult.put("restaurants", section.substring(3).trim());
            }
        }

        return formattedResult;
    }
}