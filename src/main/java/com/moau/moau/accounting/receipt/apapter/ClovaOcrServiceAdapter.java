package com.moau.moau.accounting.receipt.apapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.moau.moau.accounting.receipt.port.OcrServicePort;
import com.moau.moau.accounting.receipt.dto.response.AiReceiptResult;
import com.moau.moau.accounting.receipt.port.StorageServicePort;
import com.moau.moau.accounting.receipt.domain.OcrStatus;
import com.moau.moau.accounting.receipt.domain.Receipt;
import com.moau.moau.accounting.receipt.repository.ReceiptRepository;
import com.moau.moau.accounting.receipt.util.ReceiptSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class ClovaOcrServiceAdapter implements OcrServicePort {

    private final WebClient webClient;
    private final ReceiptRepository receiptRepository;
    private final StorageServicePort storageServicePort;
    private final OpenAiClient openAiClient;

    @Value("${app.naver.ocr.api-url}")
    private String ocrApiUrl;
    @Value("${app.naver.ocr.secret-key}")
    private String ocrSecretKey;

    @Override
    @Transactional
    public void requestOcr(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId).orElse(null);
        if (receipt == null) {
            log.warn("[OCR] Receipt not found: {}", receiptId);
            return;
        }

        log.info("[OCR] Starting OCR processing for receiptId: {}", receiptId);
        int maxRetries = 3;
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            attempt++;
            try {
                log.info("[OCR] Attempt {}/{} for receiptId: {}", attempt, maxRetries, receiptId);

                String temporaryGetUrl = storageServicePort.createPresignedGetUrl(receipt.getS3Key());

                Map<String, Object> requestBody = Map.of(
                        "images", List.of(Map.of(
                                "format", "jpg",
                                "name", "receipt",
                                "url", temporaryGetUrl
                        )),
                        "lang", "ko",
                        "requestId", UUID.randomUUID().toString(),
                        "version", "V2",
                        "timestamp", System.currentTimeMillis()
                );

                JsonNode response = webClient.post()
                        .uri(ocrApiUrl)
                        .header("X-OCR-SECRET", ocrSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                String fullText = extractFullText(response);
                String sanitizedText = ReceiptSanitizer.sanitize(fullText);
                AiReceiptResult aiResult = openAiClient.extractReceiptData(sanitizedText);

                receipt.updateOcrResult(
                        OcrStatus.SUCCESS,
                        aiResult.merchantName(),
                        aiResult.amount(),
                        aiResult.date(),
                        aiResult.paymentMethod()
                );
                log.info("[OCR] Success on attempt {}/{} for receiptId: {} -> {}",
                        attempt, maxRetries, receiptId, aiResult);
                receiptRepository.save(receipt);
                return; // 성공 시 즉시 반환

            } catch (Exception e) {
                lastException = e;
                log.warn("[OCR] Attempt {}/{} failed for receiptId: {}. Error: {}",
                        attempt, maxRetries, receiptId, e.getMessage());

                if (attempt < maxRetries) {
                    try {
                        // 재시도 간격 (1초, 2초, ...)
                        Thread.sleep(attempt * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("[OCR] Interrupted during retry delay for receiptId: {}", receiptId);
                        break;
                    }
                }
            }
        }

        // 모든 재시도 실패
        log.error("[OCR] All {} attempts failed for receiptId: {}", maxRetries, receiptId, lastException);
        receipt.updateOcrResult(OcrStatus.FAILED, null, null, null, null);
        receiptRepository.save(receipt);
    }

    private String extractFullText(JsonNode response) {
        if (response == null || !response.has("images")) {
            throw new RuntimeException("Invalid OCR response");
        }
        StringBuilder sb = new StringBuilder();
        JsonNode fields = response.get("images").get(0).get("fields");
        for (JsonNode field : fields) {
            sb.append(field.get("inferText").asText()).append(" ");
        }
        return sb.toString().trim();
    }
}