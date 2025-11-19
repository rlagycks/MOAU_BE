package com.moau.moau.accounting.receipt.apapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moau.moau.accounting.receipt.dto.response.AiReceiptResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${app.openai.api-key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // [삭제됨] public record AiReceiptResult(...) { ... }  <-- 이 부분을 지우세요!

    public AiReceiptResult extractReceiptData(String sanitizedText) {
        try {
            // ... (기존 프롬프트 및 요청 로직 동일) ...
            String systemPrompt = """
                You are an expert receipt parser. Extract the following fields from the receipt text provided by the user:
                
                1. merchantName (String): The name of the store. Exclude business types like 'Ltd', 'Corp', 'Inc'.
                2. amount (Long): The total amount paid. Only numbers.
                3. date (String): The transaction date in 'YYYY-MM-DD' format.
                4. paymentMethod (String): The payment method (e.g., 'Credit Card', 'Cash', 'Shinhan Card', 'Samsung Pay'). If specific card company is shown, use it. If unknown, return null.
                
                Return the result strictly in JSON format.
                Example: {"merchantName": "Starbucks", "amount": 4500, "date": "2025-11-18", "paymentMethod": "Shinhan Card"}
                """;

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", sanitizedText)
                    ),
                    "temperature", 0.1
            );

            JsonNode response = webClient.post()
                    .uri(OPENAI_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String contentString = response.get("choices").get(0).get("message").get("content").asText();
            JsonNode contentJson = objectMapper.readTree(contentString);

            String merchantName = contentJson.has("merchantName") ? contentJson.get("merchantName").asText() : "미확인 가맹점";
            Long amount = contentJson.has("amount") ? contentJson.get("amount").asLong() : 0L;
            String dateStr = contentJson.has("date") ? contentJson.get("date").asText() : null;

            String paymentMethod = contentJson.has("paymentMethod") && !contentJson.get("paymentMethod").isNull()
                    ? contentJson.get("paymentMethod").asText()
                    : null;

            LocalDate date = null;
            if (dateStr != null && !dateStr.equalsIgnoreCase("null")) {
                try {
                    date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                } catch (Exception e) {
                    date = LocalDate.now();
                }
            }

            return new AiReceiptResult(merchantName, amount, date, paymentMethod);

        } catch (Exception e) {
            log.error("[AI] Extraction failed: {}", e.getMessage());
            return new AiReceiptResult("분석 실패", 0L, LocalDate.now(), null);
        }
    }
}