package com.formapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Renders a saved form definition as a self-contained HTML file. */
public final class HtmlFormExporter {
    public Path export(Form form, Path output) {
        Objects.requireNonNull(form, "form is required");
        Objects.requireNonNull(output, "output is required");
        Path destination = output.toAbsolutePath().normalize();
        try {
            Path parent = destination.getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(destination, render(form), StandardCharsets.UTF_8);
            return destination;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not export HTML: " + ex.getMessage(), ex);
        }
    }

    public String render(Form form) {
        Objects.requireNonNull(form, "form is required");
        StringBuilder html = new StringBuilder("<!doctype html>\n<html lang=\"en\">\n<head>\n")
                .append("<meta charset=\"utf-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
                .append("<title>").append(escape(form.getName())).append("</title>\n")
                .append("<style>body{font:16px system-ui,sans-serif;background:#f4f6fa;color:#172033;margin:0;padding:3rem 1rem}main{max-width:680px;margin:auto;background:white;padding:2.5rem;border-radius:18px;box-shadow:0 12px 40px #18243a14}h1{margin-top:0}p{color:#596579}.field{margin:1.4rem 0}label{display:block;font-weight:600;margin-bottom:.45rem}input,select,textarea{box-sizing:border-box;width:100%;padding:.75rem;border:1px solid #cbd2df;border-radius:8px;font:inherit}input[type=checkbox]{width:auto}button{border:0;border-radius:8px;background:#3157d5;color:white;padding:.8rem 1.4rem;font:inherit;font-weight:600;cursor:pointer}.hint{font-size:.9rem;color:#68748a}</style>\n")
                .append("</head>\n<body>\n<main>\n<h1>").append(escape(form.getName())).append("</h1>\n");
        if (!form.getDescription().isBlank()) html.append("<p>").append(escape(form.getDescription())).append("</p>\n");
        html.append("<form>\n");
        for (Field field : form.getFields()) appendField(html, field);
        html.append("<button type=\"submit\">Submit</button>\n</form>\n</main>\n</body>\n</html>\n");
        return html.toString();
    }

    private void appendField(StringBuilder html, Field field) {
        String id = "field-" + field.getId();
        String type = field.getType().trim().toUpperCase(Locale.ROOT);
        String required = field.isRequired() ? " required" : "";
        html.append("<div class=\"field\">\n<label for=\"").append(id).append("\">")
                .append(escape(field.getLabel())).append(field.isRequired() ? " <span aria-label=\"required\">*</span>" : "")
                .append("</label>\n");
        if (type.equals("MULTICHOICE")) {
            html.append("<select id=\"").append(id).append("\" name=\"").append(id).append("\" multiple").append(required).append(">\n");
            appendOptions(html, field.getConstraints());
            html.append("</select>\n");
        } else if (type.equals("CHOICE")) {
            html.append("<select id=\"").append(id).append("\" name=\"").append(id).append("\"").append(required).append(">\n<option value=\"\">Choose an option</option>\n");
            appendOptions(html, field.getConstraints());
            html.append("</select>\n");
        } else if (type.equals("BOOLEAN")) {
            html.append("<input id=\"").append(id).append("\" name=\"").append(id).append("\" type=\"checkbox\">");
        } else {
            String htmlType = switch (type) {
                case "NUMBER" -> "number";
                case "DATE" -> "date";
                case "EMAIL" -> "email";
                case "PHONE" -> "tel";
                case "URL" -> "url";
                default -> "text";
            };
            html.append("<input id=\"").append(id).append("\" name=\"").append(id).append("\" type=\"").append(htmlType).append("\"").append(required).append(">");
        }
        if (!field.getConstraints().isBlank() && !type.equals("CHOICE") && !type.equals("MULTICHOICE")) {
            html.append("\n<div class=\"hint\">").append(escape(field.getConstraints())).append("</div>");
        }
        html.append("\n</div>\n");
    }

    private void appendOptions(StringBuilder html, String constraints) {
        for (String option : constraints.split(",")) {
            String value = option.trim();
            if (!value.isEmpty()) html.append("<option value=\"").append(escape(value)).append("\">").append(escape(value)).append("</option>\n");
        }
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static Path defaultPath(Form form) {
        String slug = form.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) slug = "form";
        return Path.of("exports", slug + "-" + form.getId().toString().substring(0, 8) + ".html");
    }
}
