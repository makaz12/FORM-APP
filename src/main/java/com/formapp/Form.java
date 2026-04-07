package com.formapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.Instant;

public class Form {
    private final UUID id;
    private String name;
    private String description;
    private List<Field> fields;
    private final Instant createdAt;
    private Instant updatedAt;
    
    public Form(String name, String description, List<Field> fields) {
        this(UUID.randomUUID(), name, description, fields, Instant.now(), Instant.now());
    }

    public Form(UUID id, String name, String description, List<Field> fields, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        setNameInternal(name);
        setDescriptionInternal(description);
        setFieldsInternal(fields);
        this.createdAt = Objects.requireNonNull(createdAt, "Creation timestamp required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update timestamp required");
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private void setNameInternal(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The form must have a name");
        }
        this.name = name.trim();
    }

    private void setDescriptionInternal(String description) {
        if (description == null || description.isBlank()) {
            this.description = "";
            return;
        }

        this.description = description.trim();
    }

    private void setFieldsInternal(List<Field> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("Add at least one field");
        }
        for (Field field : fields) {
            if (field == null) {
                throw new IllegalArgumentException("A field cannot be null");
            }
        }

        this.fields = new ArrayList<>(fields);
    }

    public void addField(Field field) {
        if (field != null) {
            fields.add(field);
        } else {
            throw new IllegalArgumentException("Field cannot be null");
        }
        touch();
    }

    public void removeField(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Null field rejected");
        }

        int index = -1;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getId().equals(field.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("No such field exists");
        }

        removeField(index);
    }

    public void removeField(int index) {
        if (index < 0 || index >= fields.size()) {
            throw new IllegalArgumentException("Invalid field index");
        }
        if (fields.size() == 1) {
            throw new IllegalStateException("Form must have at least one field");
        }

        fields.remove(index);
        touch();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setNameInternal(name);
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        setDescriptionInternal(description);
        touch();
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setFields(List<Field> fields) {
        setFieldsInternal(fields);
        touch();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
