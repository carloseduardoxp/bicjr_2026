package com.openisle.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CosImageUploaderTest {
    @Test
    void uploadReturnsUrl() {
        CosImageUploader uploader = new CosImageUploader("http://cos.example.com");
        String url = uploader.upload("data".getBytes(), "img.png");
        assertEquals("http://cos.example.com/img.png", url);
    }
}
