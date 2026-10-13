# Project Status

## Implemented

- Domain models validate names, labels, field lists, and timestamps.
- The CLI supports interactive create/edit flows and command-line list, show, search, edit, and delete operations.
- Forms and ordered fields persist in SQLite through `FormRepository`; database location defaults to `data/forms.db` and can be changed with `-Dformapp.db=<path>`.
- `form export-html` creates a standalone HTML form with escaped content and inputs for the supported field types.
- Maven packaging includes the SQLite driver so the generated application JAR can run with `java -jar`.

## Build and Run

Build with `mvn package`; start the CLI with `java -jar target/form-app-0.1.0.jar`. Version `0.1.0` compiled and packaged successfully with tests skipped. The runnable JAR also launched and displayed CLI help and an empty form list.
