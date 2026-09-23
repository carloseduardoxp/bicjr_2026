package com.openisle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AvatarGenerator {

    @Value("${app.avatar.base-url:https://api.dicebear.com/6.x}")
    private String baseUrl;

    @Value("${app.avatar.style:identicon}")
    private String style;

    @Value("${app.avatar.size:64}")
    private int size;

    public String generate(String seed) {
        String encoded = URLEncoder.encode(seed, StandardCharsets.UTF_8);
        return String.format("%s/%s/png?seed=%s&size=%d", baseUrl, style, encoded, size);
    }
}
