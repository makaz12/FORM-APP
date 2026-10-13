# CLI Specification

This document defines the command-line behavior for the Form App.

## Global Rules
- IDs are displayed as full UUIDs.
- ID input accepts full UUIDs or unique, case-insensitive prefixes. If a prefix is ambiguous, show matching forms and prompt for a longer prefix or full UUID.
- Search is a case-insensitive substring match across `name` and `description`.

## Suggested Field Types
Show suggestions, but accept any string:
- TEXT
- NUMBER
- BOOLEAN
- DATE
- EMAIL
- PHONE
- URL
- CHOICE
- MULTICHOICE

If the user enters a value outside the list, accept and store it as-is.

## Commands

### `form create`
Interactive by default. Optional flags: `--name`, `--desc`.

Flow:
- Prompt for missing name/description.
- Field loop:
  - label
  - type (show suggested list)
  - required (y/n)
  - constraints (optional free-form string; allow blank)
- Show summary and confirm save.

### `form edit <id>`
Accepts UUID or prefix. Optional flags: `--name`, `--desc` for non-interactive updates.

If no flags are provided, start interactive flow with options to:
- Edit name
- Edit description
- Edit fields
- Reorder fields

Field editing options:
- Add field
- Edit field (by index or id prefix)
- Remove field
- Reorder fields (move by index)

### `form delete <id>`
Accepts UUID or prefix. Confirm with `Are you sure? (y/n)` unless `--yes` is provided.

On success:
- `Deleted form <uuid>`

### `form show <id>`
Accepts UUID or prefix. Displays:
- ID, name, description, created/updated timestamps
- Field list with index, label, type, required, constraints

### `form list`
Displays each form with:
- ID (full UUID)
- name
- short description (truncate to 60 chars)
- field count

### `form search <query>`
Case-insensitive substring match across name + description. Output format matches `form list`.

### `form export-html <id> [--output <path>]`
Writes a standalone UTF-8 HTML form. Without `--output`, the file is written under `exports/` using the form name and ID prefix. For `CHOICE` and `MULTICHOICE`, constraints are comma-separated option labels.
