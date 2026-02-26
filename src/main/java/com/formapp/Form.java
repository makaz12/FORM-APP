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
    

    public Form(String name, String descriotion, ArrayList<Field> fields){
        this(UUID.randomUUID(), name, descriotion, fields, createdAt, updatedAt);
    }

    public Form(UUID id, String name, String description, ArrayList<Field> fields, Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fields = fields;
        createdAt = createdAt;
        updatedAt = updatedAt;
    }
}
