package com.openisle.controller;

import com.openisle.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final OpenAiService openAiService;

    @PostMapping("/format")
    public ResponseEntity<Map<String, String>> format(@RequestBody Map<String, String> req) {
        String text = req.get("text");
        if (text == null) {
            return ResponseEntity.badRequest().build();
        }
        return openAiService.formatMarkdown(text)
                .map(t -> ResponseEntity.ok(Map.of("content", t)))
                .orElse(ResponseEntity.status(500).build());
    }
}
