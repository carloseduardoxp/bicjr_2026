package com.openisle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ImageUploader implementation using Tencent Cloud COS.
 * For simplicity this demo just returns a URL composed of the base URL and file name.
 */
@Service
public class CosImageUploader extends ImageUploader {

    private final String baseUrl;

    public CosImageUploader(@Value("${cos.base-url:https://example.com}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String upload(byte[] data, String filename) {
        // In a real implementation you would call COS SDK here
        return baseUrl + "/" + filename;
    }
}
