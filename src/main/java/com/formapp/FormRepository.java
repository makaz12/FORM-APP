package com.formapp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormRepository {
    void save(Form form);
    Optional<Form> findById(UUID id);
    List<Form> findAll();
    List<Form> search(String query);
    boolean deleteById(UUID id);

}
