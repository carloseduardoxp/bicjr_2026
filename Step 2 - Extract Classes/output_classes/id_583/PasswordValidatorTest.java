package com.openisle.service;

import com.openisle.model.PasswordStrength;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void lowStrengthRequiresSixChars() {
        PasswordValidator validator = new PasswordValidator(PasswordStrength.LOW);

        assertThrows(IllegalArgumentException.class, () -> validator.validate("12345"));
        assertDoesNotThrow(() -> validator.validate("123456"));
    }

    @Test
    void mediumStrengthRules() {
        PasswordValidator validator = new PasswordValidator(PasswordStrength.MEDIUM);

        assertThrows(IllegalArgumentException.class, () -> validator.validate("abc123"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("abcdefgh"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("12345678"));
        assertDoesNotThrow(() -> validator.validate("abcd1234"));
    }

    @Test
    void highStrengthRules() {
        PasswordValidator validator = new PasswordValidator(PasswordStrength.HIGH);

        assertThrows(IllegalArgumentException.class, () -> validator.validate("Abc123$"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("abcd1234$xyz"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("ABCD1234$XYZ"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("AbcdABCDabcd"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate("Abcd1234abcd"));
        assertDoesNotThrow(() -> validator.validate("Abcd1234$xyz"));
    }
}
