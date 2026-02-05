# Minimal Test Plan (Phase 1)

This plan defines the first test cases to implement once coding begins. Scope is CLI + repository behavior with SQLite.

## Unit Tests
1. Form validation
- Creating a form with missing name should fail.
- Creating a form with empty field list should fail.

2. Field validation
- Field label is required.
- `required` defaults to false.
- Type accepts any non-empty string.

3. ID prefix matching
- Exact UUID resolves.
- Unique prefix resolves.
- Ambiguous prefix returns multiple matches.
- Unknown prefix returns not found.

## Repository Tests (SQLite)
1. Save and load a form
- Save a form with 2 fields.
- Load by id returns identical form data.

2. Update a form
- Update name/description and confirm persisted changes.
- Update fields (add/remove/reorder) and confirm persisted changes.

3. Delete a form
- Deleting removes form and all fields (cascade).
- Deleting missing ID returns not found.

4. Search
- Case-insensitive substring match on name.
- Case-insensitive substring match on description.
- No matches returns empty list.

## CLI Behavior Tests
1. Create flow
- Interactive prompts collect all required values.
- Optional `constraints` accepts blank.
- Summary confirmation required to save.

2. Edit flow
- Non-interactive flags (`--name`, `--desc`) update without prompts.
- Interactive field edit supports add/edit/remove/reorder.

3. Delete flow
- Prompts for confirmation unless `--yes` is provided.

4. Show/List/Search
- `show` prints full UUID and all fields with index.
- `list` shows truncated description (<= 60 chars).
- `search` output matches `list` formatting.

## First Test Implementation Order
1. Form + Field validation unit tests
2. Repository save/load/update/delete
3. Search repository tests
4. CLI create/edit/delete flows
5. CLI list/show/search formatting
