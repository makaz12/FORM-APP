package com.formapp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Form {
    private final UUID id;
    private String name;
    private String description;
    private ArrayList<Field> fields;
    private final Instant createdAt;
    private  Instant updatedAt;
    
    //new-form constructor

    public Form(String name, String description, ArrayList<Field> fields){
        this(UUID.randomUUID(), name, null, fields, Instant.now(), Instant.now());
    }

    //full construcotr

    public Form(UUID id, String name, String description, ArrayList<Field> fields, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fields = fields;
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
            throw new IllegalAccessError("The form must have a name");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        if(fields == null || fields.isEmpty()){
            throw new IllegalAccessError("Add at least one field");
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
