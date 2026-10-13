# Form App

Java 17 CLI for creating, editing, searching, deleting, displaying, and exporting personalized forms.

## Build

```sh
mvn package
```

## Run

```sh
java -jar target/form-app-0.1.0.jar
```

Forms are stored in `data/forms.db` (SQLite) and survive application restarts. Set the `formapp.db` Java system property to use a different database path:

```sh
java -Dformapp.db=my-forms.db -jar target/form-app-0.1.0.jar list
```

## Commands

```text
form create [--name <name>] [--desc <description>]
form list
form show <id-or-prefix>
form search <query>
form edit <id-or-prefix> [--name <name>] [--desc <description>]
form delete <id-or-prefix> [--yes]
form export-html <id-or-prefix> [--output <path>]
```

HTML exports are written to `exports/` by default. The `CHOICE` and `MULTICHOICE` field constraints are comma-separated option labels. Other constraints are shown as guidance below the generated input.
