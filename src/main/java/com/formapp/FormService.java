package com.formapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.nio.file.Path;

public class FormService {
    private static final String[] FIELD_TYPES = {
            "TEXT", "NUMBER", "BOOLEAN", "DATE", "EMAIL", "PHONE", "URL", "CHOICE", "MULTICHOICE"
    };

    private final FormRepository repository;
    private final Scanner scanner;

    public FormService(FormRepository repository, Scanner scanner) {
        this.repository = repository;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        Path database = Path.of(System.getProperty("formapp.db", "data/forms.db"));
        new FormService(new SqliteFormRepository(database), new Scanner(System.in)).run(args);
    }

    public void run(String[] args) {
        if (args.length > 0) {
            runCommand(args);
        } else {
            runMenu();
        }
    }

    private void runMenu() {
        System.out.println("Form App");

        while (true) {
            System.out.println();
            System.out.println("1. Create form");
            System.out.println("2. List forms");
            System.out.println("3. Show form");
            System.out.println("4. Search forms");
            System.out.println("5. Edit form");
            System.out.println("6. Delete form");
            System.out.println("7. Quit");
            System.out.println("8. Export form as HTML");

            try {
                String choice = prompt("Choose an option").toLowerCase(Locale.ROOT);
                switch (choice) {
                    case "1":
                    case "create":
                        createForm(null, null);
                        break;
                    case "2":
                    case "list":
                        listForms(repository.findAll());
                        break;
                    case "3":
                    case "show":
                        showForm(prompt("Form ID or prefix"));
                        break;
                    case "4":
                    case "search":
                        searchForms(prompt("Search query"));
                        break;
                    case "5":
                    case "edit":
                        editFormInteractive(prompt("Form ID or prefix"));
                        break;
                    case "6":
                    case "delete":
                        deleteForm(prompt("Form ID or prefix"), false);
                        break;
                    case "7":
                    case "quit":
                    case "exit":
                        System.out.println("Goodbye.");
                        return;
                    case "8":
                    case "export-html":
                        exportHtml(prompt("Form ID or prefix"), null);
                        break;
                    default:
                        System.out.println("Unknown option.");
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void runCommand(String[] rawArgs) {
        String[] args = stripFormPrefix(rawArgs);
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase(Locale.ROOT);

        try {
            Map<String, String> flags = parseFlags(args);
            switch (command) {
                case "create":
                    createForm(flags.get("--name"), flags.get("--desc"));
                    break;
                case "list":
                    listForms(repository.findAll());
                    break;
                case "show":
                    requireArgument(args, 1, "Usage: form show <id>");
                    showForm(args[1]);
                    break;
                case "search":
                    requireArgument(args, 1, "Usage: form search <query>");
                    searchForms(joinFrom(args, 1));
                    break;
                case "edit":
                    requireArgument(args, 1, "Usage: form edit <id> [--name <name>] [--desc <description>]");
                    editForm(args[1], flags);
                    break;
                case "delete":
                    requireArgument(args, 1, "Usage: form delete <id> [--yes]");
                    deleteForm(args[1], flags.containsKey("--yes"));
                    break;
                case "export-html":
                    requireArgument(args, 1, "Usage: form export-html <id> [--output <path>]");
                    exportHtml(args[1], flags.containsKey("--output") ? Path.of(flags.get("--output")) : null);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    printHelp();
                    break;
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private Form createForm(String providedName, String providedDescription) {
        String name = isBlank(providedName) ? promptRequired("Form name") : providedName.trim();
        String description = providedDescription == null ? prompt("Description (optional)") : providedDescription.trim();

        List<Field> fields = new ArrayList<>();
        do {
            fields.add(readField(null));
        } while (confirm("Add another field?"));

        Form form = new Form(name, description, fields);
        System.out.println();
        printForm(form);

        if (confirm("Save this form?")) {
            repository.save(form);
            System.out.println("Saved form " + form.getId());
        } else {
            System.out.println("Form was not saved.");
        }

        return form;
    }

    private void editForm(String idInput, Map<String, String> flags) {
        Form form = resolveForm(idInput);
        boolean changed = false;

        if (flags.containsKey("--name")) {
            form.setName(flags.get("--name"));
            changed = true;
        }

        if (flags.containsKey("--desc")) {
            form.setDescription(flags.get("--desc"));
            changed = true;
        }

        if (changed) {
            repository.save(form);
            System.out.println("Updated form " + form.getId());
        } else {
            editFormInteractive(idInput);
        }
    }

    private void editFormInteractive(String idInput) {
        Form form = resolveForm(idInput);

        while (true) {
            System.out.println();
            System.out.println("Editing: " + form.getName());
            System.out.println("1. Edit name");
            System.out.println("2. Edit description");
            System.out.println("3. Add field");
            System.out.println("4. Edit field");
            System.out.println("5. Remove field");
            System.out.println("6. Reorder fields");
            System.out.println("7. Show form");
            System.out.println("8. Done");

            String choice = prompt("Choose an option").toLowerCase(Locale.ROOT);
            switch (choice) {
                case "1":
                    form.setName(promptRequired("New name"));
                    repository.save(form);
                    break;
                case "2":
                    form.setDescription(prompt("New description"));
                    repository.save(form);
                    break;
                case "3":
                    form.addField(readField(null));
                    repository.save(form);
                    break;
                case "4":
                    editField(form);
                    repository.save(form);
                    break;
                case "5":
                    removeField(form);
                    repository.save(form);
                    break;
                case "6":
                    reorderFields(form);
                    repository.save(form);
                    break;
                case "7":
                    printForm(form);
                    break;
                case "8":
                case "done":
                case "exit":
                    System.out.println("Updated form " + form.getId());
                    return;
                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }
    }

    private Field readField(Field current) {
        String label = promptWithDefault("Field label", current == null ? null : current.getLabel(), true);
        String typeLabel = "Field type " + String.join("/", FIELD_TYPES);
        String type = promptWithDefault(typeLabel, current == null ? null : current.getType(), true);
        boolean required = confirmWithDefault("Required?", current == null ? null : current.isRequired());
        String constraints = promptWithDefault("Constraints", current == null ? null : current.getConstraints(), false);

        if (current == null) {
            return new Field(label, type, required, constraints);
        }

        return new Field(current.getId(), label, type, required, constraints);
    }

    private void editField(Form form) {
        printFields(form);
        int index = resolveFieldIndex(form, prompt("Field number or ID prefix"));
        List<Field> fields = new ArrayList<>(form.getFields());
        fields.set(index, readField(fields.get(index)));
        form.setFields(fields);
    }

    private void removeField(Form form) {
        printFields(form);
        int index = resolveFieldIndex(form, prompt("Field number or ID prefix"));
        form.removeField(index);
    }

    private void reorderFields(Form form) {
        printFields(form);
        int fromIndex = resolveFieldIndex(form, prompt("Move field number or ID prefix"));
        int toIndex = promptIndex("New position", form.getFields().size()) - 1;
        List<Field> fields = new ArrayList<>(form.getFields());
        Field moved = fields.remove(fromIndex);
        fields.add(toIndex, moved);
        form.setFields(fields);
    }

    private void deleteForm(String idInput, boolean skipConfirm) {
        Form form = resolveForm(idInput);
        if (!skipConfirm && !confirm("Are you sure you want to delete " + form.getName() + "?")) {
            System.out.println("Delete cancelled.");
            return;
        }

        repository.deleteById(form.getId());
        System.out.println("Deleted form " + form.getId());
    }

    private void showForm(String idInput) {
        printForm(resolveForm(idInput));
    }

    private void exportHtml(String idInput, Path output) {
        Form form = resolveForm(idInput);
        Path destination = output == null ? HtmlFormExporter.defaultPath(form) : output;
        Path written = new HtmlFormExporter().export(form, destination);
        System.out.println("Exported HTML to " + written);
    }

    private void searchForms(String query) {
        listForms(repository.search(query));
    }

    private void listForms(List<Form> forms) {
        if (forms.isEmpty()) {
            System.out.println("No forms found.");
            return;
        }

        for (Form form : forms) {
            System.out.println(form.getId() + " | " + form.getName() + " | "
                    + shortDescription(form.getDescription()) + " | fields: " + form.getFields().size());
        }
    }

    private void printForm(Form form) {
        System.out.println("ID: " + form.getId());
        System.out.println("Name: " + form.getName());
        System.out.println("Description: " + form.getDescription());
        System.out.println("Created: " + form.getCreatedAt());
        System.out.println("Updated: " + form.getUpdatedAt());
        printFields(form);
    }

    private void printFields(Form form) {
        System.out.println("Fields:");
        List<Field> fields = form.getFields();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            System.out.println((i + 1) + ". " + field.getLabel()
                    + " | id: " + field.getId()
                    + " | type: " + field.getType()
                    + " | required: " + (field.isRequired() ? "yes" : "no")
                    + " | constraints: " + field.getConstraints());
        }
    }

    private Form resolveForm(String idInput) {
        String normalized = idInput.trim().toLowerCase(Locale.ROOT);
        List<Form> matches = new ArrayList<>();

        for (Form form : repository.findAll()) {
            String id = form.getId().toString().toLowerCase(Locale.ROOT);
            if (id.equals(normalized) || id.startsWith(normalized)) {
                matches.add(form);
            }
        }

        if (matches.isEmpty()) {
            throw new IllegalArgumentException("No form found for ID " + idInput);
        }

        if (matches.size() > 1) {
            listForms(matches);
            throw new IllegalArgumentException("ID prefix is ambiguous. Enter a longer prefix or full UUID.");
        }

        return matches.get(0);
    }

    private int resolveFieldIndex(Form form, String input) {
        try {
            return parseIndex(input, form.getFields().size()) - 1;
        } catch (IllegalArgumentException ignored) {
            String normalized = input.trim().toLowerCase(Locale.ROOT);
            List<Integer> matches = new ArrayList<>();

            for (int i = 0; i < form.getFields().size(); i++) {
                String id = form.getFields().get(i).getId().toString().toLowerCase(Locale.ROOT);
                if (id.equals(normalized) || id.startsWith(normalized)) {
                    matches.add(i);
                }
            }

            if (matches.isEmpty()) {
                throw new IllegalArgumentException("No field found for " + input);
            }
            if (matches.size() > 1) {
                throw new IllegalArgumentException("Field ID prefix is ambiguous.");
            }
            return matches.get(0);
        }
    }

    private int promptIndex(String label, int max) {
        while (true) {
            try {
                return parseIndex(prompt(label), max);
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private int parseIndex(String input, int max) {
        try {
            int index = Integer.parseInt(input.trim());
            if (index >= 1 && index <= max) {
                return index;
            }
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("Enter a number from 1 to " + max + ".");
    }

    private boolean confirm(String label) {
        return confirmWithDefault(label, null);
    }

    private boolean confirmWithDefault(String label, Boolean defaultValue) {
        while (true) {
            String suffix = defaultValue == null ? " (y/n)" : " (y/n, default " + (defaultValue ? "y" : "n") + ")";
            String answer = prompt(label + suffix).toLowerCase(Locale.ROOT);

            if (answer.isBlank() && defaultValue != null) {
                return defaultValue;
            }
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }

            System.out.println("Please answer y or n.");
        }
    }

    private String promptRequired(String label) {
        return promptWithDefault(label, null, true);
    }

    private String promptWithDefault(String label, String defaultValue, boolean required) {
        while (true) {
            String promptLabel = defaultValue == null ? label : label + " [" + defaultValue + "]";
            String value = prompt(promptLabel);

            if (!value.isBlank()) {
                return value;
            }
            if (defaultValue != null) {
                return defaultValue;
            }
            if (!required) {
                return "";
            }

            System.out.println("This value is required.");
        }
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private String shortDescription(String description) {
        if (description == null || description.isBlank()) {
            return "";
        }

        String trimmed = description.trim();
        return trimmed.length() <= 60 ? trimmed : trimmed.substring(0, 57) + "...";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String[] stripFormPrefix(String[] rawArgs) {
        if (rawArgs.length == 0 || !rawArgs[0].equalsIgnoreCase("form")) {
            return rawArgs;
        }

        String[] stripped = new String[rawArgs.length - 1];
        System.arraycopy(rawArgs, 1, stripped, 0, stripped.length);
        return stripped;
    }

    private Map<String, String> parseFlags(String[] args) {
        Map<String, String> flags = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("--")) {
                continue;
            }

            if (args[i].equals("--yes")) {
                flags.put(args[i], "true");
            } else if (i + 1 < args.length) {
                flags.put(args[i], args[++i]);
            } else {
                throw new IllegalArgumentException("Missing value for " + args[i]);
            }
        }
        return flags;
    }

    private void requireArgument(String[] args, int index, String usage) {
        if (args.length <= index || args[index].startsWith("--")) {
            throw new IllegalArgumentException(usage);
        }
    }

    private String joinFrom(String[] args, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < args.length && !args[i].startsWith("--"); i++) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private void printHelp() {
        System.out.println("Usage:");
        System.out.println("  form create [--name <name>] [--desc <description>]");
        System.out.println("  form list");
        System.out.println("  form show <id>");
        System.out.println("  form search <query>");
        System.out.println("  form edit <id> [--name <name>] [--desc <description>]");
        System.out.println("  form delete <id> [--yes]");
        System.out.println("  form export-html <id> [--output <path>]");
    }
}
