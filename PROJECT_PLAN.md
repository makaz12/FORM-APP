# Project Planning: Personalized Forms App (Java)

## 1) Goals & Scope
**Goal:** Build a Java application to create, edit, search, delete, display, and export personalized forms. Initial interface is terminal/CLI. Windows Service support is optional/phase 2.

**In scope (Phase 1):**
- CRUD for forms
- Search forms
- Display form details
- Export saved forms as tangible HTML files
- Local persistence
- CLI interface

**Out of scope (Phase 1):**
- UI/GUI
- Web/API interface (unless needed for service)
- Multi-user auth

---

## 2) Requirements (Acceptance Criteria)
### Functional
- Create a form with name, description, and fields.
- Edit an existing form (name, description, fields).
- Search forms by name and description.
- Delete a form by ID.
- Display a form by ID with all field details.
- Generate an HTML file from a saved form definition.

### Non‑Functional
- Runs in a terminal without GUI.
- Data persists between runs.
- Clear error handling for invalid input and missing IDs.
- Modular architecture for future UI/service additions.

---

## 3) Domain Model (Draft)
### Entities
**Form**
- `id` (UUID)
- `name` (String)
- `description` (String)
- `fields` (List<Field>)
- `createdAt` (Instant)
- `updatedAt` (Instant)

**Field**
- `id` (UUID)
- `label` (String)
- `type` (User-defined; free-form to allow full control)
- `required` (boolean)
- `constraints` (User-defined; free-form to allow full control)

**ValidationRule (optional)**
- User-defined as needed by the form creator

---

## 4) Architecture Plan
Layered design to keep logic independent from the interface:

1. **Domain Layer**
   - Entities and validation logic
2. **Application Layer**
   - Use cases: CreateForm, EditForm, DeleteForm, SearchForms, ShowForm
3. **Persistence Layer**
   - `FormRepository` interface + implementation
4. **Rendering / Export Layer**
   - HTML generator that converts a saved `Form` into a standalone `.html` artifact
5. **Presentation Layer**
   - CLI parsing and prompts

This allows swapping CLI → service without rewriting domain logic.

---

## 5) Persistence Decision Record (PDR)
**Option A: JSON file**
- ✅ Simple, minimal dependencies
- ✅ Easy to inspect/edit
- ❌ Less efficient for large datasets

**Option B: SQLite**
- ✅ Query support and indexing
- ✅ Better scalability
- ❌ Requires dependency and schema

**Decision:** Use SQLite for persistence.

---

## 6) CLI Command Specification
Example command set (final set TBD):

- `form create`
- `form edit <id>`
- `form delete <id>`
- `form list`
- `form show <id>`
- `form search <query>`
- `form export-html <id>`

### Example interaction
```
$ form create
Name: Customer Feedback
Description: Form for collecting feedback
Add field? (y/n): y
Field label: Rating
Field type: NUMBER
Required? (y/n): y
Add field? (y/n): n
Form created with ID: <uuid>
```

---

## 7) Error Handling & Validation
- Missing ID → show “not found” message.
- Invalid field type → show valid options.
- Required name/fields missing → prompt again.

---

## 8) Testing Strategy
- **Unit tests**: validation rules, field creation
- **Repository tests**: save/load forms
- **Exporter tests**: verify generated HTML structure and field-type mapping
- **CLI tests**: parsing and command routing

---

## 9) Milestones
1. Requirements & data model finalized
2. Persistence layer built (SQLite)
3. Application/service layer built
4. HTML export layer built
5. CLI CRUD + search + export
6. Error handling and validation
7. Optional service wrapper

---

## 10) Next Decisions Needed (Resolved)
- Field types and constraints are user-defined to allow full control by the form creator.
- Persistence: SQLite.
- First tangible export format: HTML.
- Windows Service expectation: API service.
