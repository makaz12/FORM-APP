package com.formapp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Form_Sample {
    private final UUID id;
    private String name;
    private String description;
    private final List<Field_Sample> fields;
    private final Instant createdAt;
    private Instant updatedAt;

    public Form_Sample(String name, String description, List<Field_Sample> fields) {
        this(UUID.randomUUID(), name, description, fields, Instant.now(), Instant.now());
    }

    public Form_Sample(UUID id, String name, String description, List<Field_Sample> fields, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
        validateName(name);
        this.name = name.trim();
        this.description = normalizeDescription(description);
        validateFields(fields);
        this.fields = new ArrayList<>(fields);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name.trim();
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = normalizeDescription(description);
        touch();
    }

    public List<Field_Sample> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setFields(List<Field_Sample> fields) {
        validateFields(fields);
        this.fields.clear();
        this.fields.addAll(fields);
        touch();
    }

    public void addField(Field_Sample field) {
        this.fields.add(Objects.requireNonNull(field, "field is required"));
        touch();
    }

    public void removeField(int index) {
        if (this.fields.size() <= 1) {
            throw new IllegalStateException("form must contain at least one field");
        }
        this.fields.remove(index);
        touch();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void validateFields(List<Field_Sample> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("at least one field is required");
        }
        if (fields.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("fields cannot contain nulls");
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }

    private String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }
}
