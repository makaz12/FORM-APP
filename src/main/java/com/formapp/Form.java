package com.formapp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Form {
    private final UUID id;
    private String name;
    private String description;
    private List<Field> fields;
    private final Instant createdAt;
    private  Instant updatedAt;
    
    //new-form constructor

    public Form(String name, String description, List<Field> fields){
        this(UUID.randomUUID(), name, description, fields, Instant.now(), Instant.now());
    }

    //full construcotr

    public Form(UUID id, String name, String description, List<Field> fields, Instant createdAt, Instant updatedAt) {
        this.id = id;
        setName(name);
        setDescription(description);
        setFields(fields);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //getters and setters

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("The form must have a name");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description ==null || description.isBlank()){
            this.description = "";
        }
        else{
        this.description = description.trim();
        }
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        if(fields == null || fields.isEmpty()){
            throw new IllegalArgumentException("Add at least one field");
        }
        else if(fields.contains(null)){
            throw new IllegalArgumentException("A field cannot be null")
        }
        this.fields = fields;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    

}
