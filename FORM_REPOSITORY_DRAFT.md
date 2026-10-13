# Form Repository Draft

## Purpose

`FormRepository` is the persistence boundary for forms. The rest of the application should depend on this interface instead of talking directly to SQLite or files.

## Responsibilities

`FormRepository` should be responsible for:
- Saving a form
- Loading a form by ID
- Listing all forms
- Searching forms by text
- Deleting a form by ID

`FormRepository` should not be responsible for:
- CLI prompts
- Output formatting
- Business validation that belongs in `Form` or application services

## Draft Operations

### `save(Form form)`

- Stores a new form or updates an existing one with the same ID.
- Input: a valid `Form`
- Output: `void`
- Error case: reject `null`

### `findById(UUID id)`

- Looks up one form by exact ID
- Input: `UUID`
- Output: `Optional<Form>`
- Error case: reject `null`

### `findAll()`

- Returns every stored form
- Input: none
- Output: `List<Form>`
- Recommended behavior: return an empty list if none exist, not `null`

### `search(String query)`

- Performs a case-insensitive substring search across `name` and `description`
- Input: search text
- Output: `List<Form>`
- Recommended behavior:
  - Trim the query
  - Return matches in a stable order
  - Decide whether a blank query means "all forms" or invalid input

### `deleteById(UUID id)`

- Deletes the form with that ID
- Input: `UUID`
- Output: `boolean`
- Recommended behavior: return `false` when nothing was deleted

## Recommended Contract

- `void save(Form form)`
- `Optional<Form> findById(UUID id)`
- `List<Form> findAll()`
- `List<Form> search(String query)`
- `boolean deleteById(UUID id)`

## Behavior Rules To Confirm

- `save` means both create and update
- `findById` returns `Optional.empty()` when missing
- `findAll` and `search` never return `null`
- `deleteById` returns `false` when nothing was deleted
- `search` checks `name` and `description` only, not fields
