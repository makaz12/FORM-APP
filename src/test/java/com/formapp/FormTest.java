package com.formapp;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormTest {

    @Test
    void shouldTrimAndNormalizeConstructorValues() {
        Form form = new Form("  Intake Form  ", "   ", List.of(new Field("Age", "NUMBER")));

        assertEquals("Intake Form", form.getName());
        assertEquals("", form.getDescription());
        assertEquals(1, form.getFields().size());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        List<Field> fields = List.of(new Field("Age", "NUMBER"));

        assertThrows(IllegalArgumentException.class, () ->
                new Form("   ", "", fields)
        );
    }

    @Test
    void shouldThrowWhenFieldsAreMissing() {
        assertThrows(IllegalArgumentException.class, () -> new Form("Survey", "", List.of()));
    }

    @Test
    void shouldProtectFieldsFromExternalMutation() {
        List<Field> fields = List.of(new Field("Age", "NUMBER"));
        Form form = new Form("Survey", "", fields);

        assertThrows(UnsupportedOperationException.class, () -> form.getFields().add(new Field("Email", "TEXT")));
    }

    @Test
    void shouldAddAndRemoveFieldsById() {
        Field first = new Field("Age", "NUMBER");
        Field second = new Field("Email", "TEXT");
        Form form = new Form("Survey", "", List.of(first));

        form.addField(second);
        form.removeField(new Field(second.getId(), "Other Label", "OTHER", false, ""));

        assertEquals(1, form.getFields().size());
        assertEquals(first.getId(), form.getFields().get(0).getId());
    }

    @Test
    void shouldRejectRemovingLastField() {
        Form form = new Form("Survey", "", List.of(new Field("Age", "NUMBER")));

        assertThrows(IllegalStateException.class, () -> form.removeField(0));
    }

    @Test
    void shouldUpdateTimestampWhenMutated() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-01T00:00:01Z");
        Form form = new Form(UUID.randomUUID(), "Survey", "", List.of(new Field("Age", "NUMBER")), createdAt, updatedAt);

        form.setDescription("Updated");

        assertTrue(form.getUpdatedAt().isAfter(updatedAt));
    }
}
