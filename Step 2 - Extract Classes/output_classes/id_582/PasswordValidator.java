package com.openisle.service;

import com.openisle.model.PasswordStrength;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordValidator {
    private final PasswordStrength strength;

    public PasswordValidator(@Value("${app.password.strength:LOW}") PasswordStrength strength) {
        this.strength = strength;
    }

    public void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        switch (strength) {
            case MEDIUM:
                checkMedium(password);
                break;
            case HIGH:
                checkHigh(password);
                break;
            default:
                // LOW, nothing beyond non-empty
        }
    }

    private void checkMedium(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain letters and numbers");
        }
    }

    private void checkHigh(String password) {
        if (password.length() < 12) {
            throw new IllegalArgumentException("Password must be at least 12 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain uppercase letters");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain lowercase letters");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain numbers");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Password must contain special characters");
        }
    }
}
