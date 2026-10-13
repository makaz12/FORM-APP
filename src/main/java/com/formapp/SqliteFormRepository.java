package com.formapp;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** SQLite-backed form storage. Each operation uses its own short-lived connection. */
public final class SqliteFormRepository implements FormRepository {
    private final String jdbcUrl;

    public SqliteFormRepository(Path databasePath) {
        Objects.requireNonNull(databasePath, "databasePath is required");
        Path absolutePath = databasePath.toAbsolutePath();
        try {
            if (absolutePath.getParent() != null) Files.createDirectories(absolutePath.getParent());
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create database directory: " + ex.getMessage(), ex);
        }
        this.jdbcUrl = "jdbc:sqlite:" + absolutePath;
        initialize();
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void initialize() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS forms ("
                    + "id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, "
                    + "created_at TEXT NOT NULL, updated_at TEXT NOT NULL)");
            statement.execute("CREATE TABLE IF NOT EXISTS fields ("
                    + "id TEXT PRIMARY KEY, form_id TEXT NOT NULL REFERENCES forms(id) ON DELETE CASCADE, "
                    + "position INTEGER NOT NULL, label TEXT NOT NULL, type TEXT NOT NULL, "
                    + "required INTEGER NOT NULL, constraints_text TEXT NOT NULL, UNIQUE(form_id, position))");
        } catch (SQLException ex) {
            throw storageError("initialize database", ex);
        }
    }

    @Override
    public void save(Form form) {
        Objects.requireNonNull(form, "form is required");
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO forms(id,name,description,created_at,updated_at) VALUES(?,?,?,?,?) "
                                + "ON CONFLICT(id) DO UPDATE SET name=excluded.name, description=excluded.description, "
                                + "updated_at=excluded.updated_at")) {
                    statement.setString(1, form.getId().toString());
                    statement.setString(2, form.getName());
                    statement.setString(3, form.getDescription());
                    statement.setString(4, form.getCreatedAt().toString());
                    statement.setString(5, form.getUpdatedAt().toString());
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM fields WHERE form_id=?")) {
                    statement.setString(1, form.getId().toString());
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO fields(id,form_id,position,label,type,required,constraints_text) VALUES(?,?,?,?,?,?,?)")) {
                    int position = 0;
                    for (Field field : form.getFields()) {
                        statement.setString(1, field.getId().toString());
                        statement.setString(2, form.getId().toString());
                        statement.setInt(3, position++);
                        statement.setString(4, field.getLabel());
                        statement.setString(5, field.getType());
                        statement.setInt(6, field.isRequired() ? 1 : 0);
                        statement.setString(7, field.getConstraints());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw storageError("save form", ex);
        }
    }

    @Override
    public Optional<Form> findById(UUID id) {
        Objects.requireNonNull(id, "id is required");
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(
                "SELECT id,name,description,created_at,updated_at FROM forms WHERE id=?")) {
            statement.setString(1, id.toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(readForm(connection, result)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw storageError("load form", ex);
        }
    }

    @Override
    public List<Form> findAll() {
        List<Form> forms = new ArrayList<>();
        try (Connection connection = connect(); Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT id,name,description,created_at,updated_at FROM forms ORDER BY rowid")) {
            while (result.next()) forms.add(readForm(connection, result));
            return forms;
        } catch (SQLException ex) {
            throw storageError("list forms", ex);
        }
    }

    private Form readForm(Connection connection, ResultSet result) throws SQLException {
        UUID id = UUID.fromString(result.getString("id"));
        List<Field> fields = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id,label,type,required,constraints_text FROM fields WHERE form_id=? ORDER BY position")) {
            statement.setString(1, id.toString());
            try (ResultSet fieldRows = statement.executeQuery()) {
                while (fieldRows.next()) {
                    fields.add(new Field(UUID.fromString(fieldRows.getString("id")), fieldRows.getString("label"),
                            fieldRows.getString("type"), fieldRows.getInt("required") != 0,
                            fieldRows.getString("constraints_text")));
                }
            }
        }
        return new Form(id, result.getString("name"), result.getString("description"), fields,
                Instant.parse(result.getString("created_at")), Instant.parse(result.getString("updated_at")));
    }

    @Override
    public List<Form> search(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return findAll().stream().filter(form -> (form.getName() + " " + form.getDescription())
                .toLowerCase(Locale.ROOT).contains(normalized)).toList();
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "id is required");
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("DELETE FROM forms WHERE id=?")) {
            statement.setString(1, id.toString());
            return statement.executeUpdate() != 0;
        } catch (SQLException ex) {
            throw storageError("delete form", ex);
        }
    }

    private IllegalStateException storageError(String action, SQLException cause) {
        return new IllegalStateException("Could not " + action + ": " + cause.getMessage(), cause);
    }
}
