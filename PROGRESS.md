# Progress Snapshot

## Current Status
- Domain model draft is in place for `Field` and `Form`.
- Core constructor and validation flows are implemented.
- `Form` mutation methods (`addField`, `removeField`) are implemented with timestamp updates.

## Completed Work
1. `Field` class (`src/main/java/com/formapp/Field.java`)
- Properties implemented: `id`, `label`, `type`, `required`, `constraints`.
- Constructor overloads implemented:
  - `(String label, String type)`
  - `(String label, String type, boolean required, String constraints)`
  - `(UUID id, String label, String type, boolean required, String constraints)`
- Validation/normalization implemented:
  - `id` required
  - `label` non-null/non-blank + trimmed
  - `type` non-null/non-blank + trimmed
  - `constraints` normalized to `""` when null, otherwise trimmed

2. `Form` class (`src/main/java/com/formapp/Form.java`)
- Properties implemented: `id`, `name`, `description`, `fields`, `createdAt`, `updatedAt`.
- Constructor strategy implemented:
  - New-form constructor auto-generates `UUID` and timestamps.
  - Full constructor supports rehydration with explicit timestamps.
- Validation/normalization implemented:
  - `name` required and trimmed
  - `description` optional; null/blank normalized to `""`
  - `fields` required, non-empty, and cannot contain `null`
- Encapsulation implemented:
  - `fields` stored as defensive copy
  - `getFields()` returns unmodifiable list
- Mutation policy implemented:
  - `touch()` updates `updatedAt` on mutating operations
  - `setName`, `setDescription`, `setFields`, `addField`, `removeField` all update timestamp
- Field removal APIs implemented:
  - `removeField(Field field)` resolves by field `id` and delegates
  - `removeField(int index)` validates index, enforces non-empty invariant, removes, updates timestamp

## Key Decisions Confirmed
- IDs are `UUID` for both `Form` and `Field`.
- `Form.fields` is `List<Field>` (not `ArrayList<Field>` in API).
- `description` is optional.
- Domain logic stays in model classes; CLI input/prompt logic stays outside.

## Open / Next Steps
1. Add unit tests for `Field` and `Form` validation + mutation behavior.
2. Decide whether to keep or remove sample classes:
- `src/main/java/com/formapp/Field_Sample.java`
- `src/main/java/com/formapp/Form_Sample.java`
3. Build CLI/service methods that call model APIs (instead of embedding user input in domain).
4. Optional cleanup: naming/comments/formatting consistency in current classes.

## Repo Note
- Auto-commit workflow is currently blocked by a `.git` permission (`index.lock` denied) issue in this environment.
