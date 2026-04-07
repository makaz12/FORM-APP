package com.formapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldTest {

    @Test
    void shouldTrimValuesAndNormalizeNullConstraints() {
        Field field = new Field("  Email  ", "  TEXT  ", true, null);

        assertEquals("Email", field.getLabel());
        assertEquals("TEXT", field.getType());
        assertEquals("", field.getConstraints());
    }

    @Test
    void shouldThrowWhenLabelIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Field("   ", "TEXT"));
    }

    @Test
    void shouldThrowWhenTypeIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Field("Email", "   "));
    }
}
