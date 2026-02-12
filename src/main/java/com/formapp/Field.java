package com.formapp;

import java.util.Objects;
import java.util.UUID;

public class Field {
    private final UUID id;
    private String label;
    private String type;
    private boolean required;
    private String constraints;

        public Field(String label, String type) {
        this(UUID.randomUUID(), label, type, false, "");
    }
    
    public Field(String label, String type, boolean required, String constraints) {
        this(UUID.randomUUID(), label, type, required, constraints);
    }

    //full constructor
     public Field(UUID id, String label, String type, boolean required, String constraints) {
        this.id = Objects.requireNonNull(id, "id is required");
        setLabel(label);
        setType(type);
        setRequired(required);
        setConstraints(constraints);
    }

    //getters and setters
    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if(label == null || label.isBlank()){
            throw new IllegalArgumentException("field label cannot be blank");
        }
        this.label = label.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if(type == null || type.isBlank()){
            throw new IllegalArgumentException("field type cannot be blank");
        }
        this.type = type.trim();
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        if(constraints == null){
            this.constraints = "";
        }
        else{
            this.constraints = constraints.trim();
        }
        
    }

    

    

    

    

    
    
}
