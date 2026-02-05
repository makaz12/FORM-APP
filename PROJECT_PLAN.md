# Project Planning: Personalized Forms App (Java)

## 1) Goals & Scope
**Goal:** Build a Java application to create, edit, search, delete, and display personalized forms. Initial interface is terminal/CLI. Windows Service support is optional/phase 2.

**In scope (Phase 1):**
- CRUD for forms
- Search forms
- Display form details
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
- `type` (Enum: TEXT, NUMBER, DATE, SELECT, CHECKBOX, etc.)
- `required` (boolean)
- `constraints` (Map<String, String> or typed class)

**ValidationRule (optional)**
- `type` (Enum)
- `params` (Map)

---

## 4) Architecture Plan
Layered design to keep logic independent from the interface:

1. **Domain Layer**
   - Entities and validation logic
2. **Application Layer**
   - Use cases: CreateForm, EditForm, DeleteForm, SearchForms, ShowForm
3. **Persistence Layer**
   - `FormRepository` interface + implementation
4. **Presentation Layer**
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

**Recommendation:** Start with JSON for Phase 1. Upgrade to SQLite if search or data volume demands it.

---

## 6) CLI Command Specification
Example command set (final set TBD):

- `form create`
- `form edit <id>`
- `form delete <id>`
- `form list`
- `form show <id>`
- `form search <query>`

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
- **CLI tests**: parsing and command routing

---

## 9) Milestones
1. Requirements & data model finalized
2. Persistence layer built (JSON)
3. CLI CRUD + search
4. Error handling and validation
5. Optional service wrapper

---

## 10) Next Decisions Needed
- Confirm data model details (fields + constraints)
- Pick persistence (JSON vs SQLite)
- Decide if Windows Service needs an API or just scheduled tasks
