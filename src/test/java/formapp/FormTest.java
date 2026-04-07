package com.formapp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FormTest {

    @Test
    void shouldThrowWhenNameIsBlank() {
        List<Field> fields = List.of(new Field("Age", "NUMBER"));

        assertThrows(IllegalArgumentException.class, () ->
                new Form("   ", "", fields)
        );
    }
}
