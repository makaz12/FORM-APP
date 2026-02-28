package com.formapp;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Form {
    private final UUID id;
    private String name;
    private String description;
    private List<Field> fields;
    private final Instant createdAt;
    private Instant updatedAt;
    
    //new-form constructor

    public Form(String name, String description, List<Field> fields){
        this(UUID.randomUUID(), name, description, fields, Instant.now(), Instant.now());
    }

    //full construcotr

    public Form(UUID id, String name, String description, List<Field> fields, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");

        nameValidation(name);
        descriptionNormalization(description);
        fieldsValidation(fields);
        this.createdAt = Objects.requireNonNull(createdAt, "Creation timestamp required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update timestamp required");
    }

    //update timestamp method
    private void touch(){
        this.updatedAt = Instant.now();
    }

    //Name validation 
    //Rejects null and blank names
    private void nameValidation(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("The form must have a name");
        }
        this.name = name.trim();
    }

    //description Normalization:
    // converts null and/or blank discriptions to empty string
     private void descriptionNormalization(String description) {
        if(description ==null || description.isBlank()){
            this.description = "";
        }
        else{
        this.description = description.trim();
        }
    }

    // Field Validation:
    // reject null, empty and null elements in list of fields
    private void fieldsValidation(List<Field> fields) {
        if(fields == null || fields.isEmpty()){
            throw new IllegalArgumentException("Add at least one field");
        }
        else if(fields.contains(null)){
            throw new IllegalArgumentException("A field cannot be null");
        }
        this.fields = new ArrayList<>(fields);
    }

    //addField method
    public void addField(Field field){
        //adds field if not null
        if (field != null) {
            fields.add(field);
        }  else {
        throw new IllegalArgumentException("Field cannot be null");
        }
        touch();
    }

    //removeField method
    public void removeField(Field field) {
    if (field == null) {
        throw new IllegalArgumentException("Null field rejected");
    }

    int index = -1;
    //define index
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




    //getters and setters

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        nameValidation(name);
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descString){
        descriptionNormalization(descString);
        touch();
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setFields(List<Field> fields){
        fieldsValidation(fields);
        touch();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    
    

}
