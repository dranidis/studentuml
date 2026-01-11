# Phase 6 Analysis: TypedEntityEditor Hierarchy

## Overview

The `TypedEntityEditor<T, D>` hierarchy represents a different pattern than the editors we've migrated so far. After analyzing the codebase, **I recommend NOT migrating this hierarchy to the Editor<T> interface pattern** for the following reasons:

## Key Differences from Migrated Editors

### 1. **Different Architecture Pattern**

**Migrated Editors (ClassifierEditor, etc.)**:

-   Edit a single domain object (Actor, DesignClass, ConceptualClass, Interface)
-   Pattern: `Editor<DomainObject>` with `editDialog(DomainObject, Component)`
-   Simple: initialize UI from object, return edited object or null

**TypedEntityEditor Hierarchy**:

-   Edits **graphical wrappers** (SDObjectGR, MultiObjectGR, ActorInstanceGR)
-   Manages **two separate concerns**:
    1. **Entity data** (name, type) - the D type parameter
    2. **Type management** (add/edit/delete types via repository) - the T type parameter
-   Complex interaction: Type selection triggers repository operations
-   Returns: Name (String) + Type (T), not a composite domain object

### 2. **Graphical Wrapper Dependency**

TypedEntityEditor subclasses are tightly coupled to graphical representations:

```java
public class ObjectEditor extends TypedEntityEditor<DesignClass, SDObject> {
    private SDObjectGR objectGR;  // Graphical wrapper

    public ObjectEditor(SDObjectGR obj, CentralRepository cr) {
        super(cr);
        this.objectGR = obj;  // Stores graphical element
        initialize();
    }

    public void initialize() {
        SDObject object = objectGR.getSDObject();  // Extracts domain object
        setCurrentType(object.getDesignClass());
        nameField.setText(object.getName());
        initializeTypeComboBox();
    }
}
```

**Why this matters:**

-   The `initialize()` method reads from the graphical wrapper
-   The constructor signature includes the graphical wrapper
-   The editor stores a reference to the graphical wrapper
-   This is **Pattern 1** from our original analysis - graphical wrapper dependency

### 3. **Controller Usage Pattern**

Controllers don't follow the "edit and return" pattern:

```java
public void editSDObject(SDObjectGR object) {
    ObjectEditor objectEditor = new ObjectEditor(object, repository);
    SDObject originalObject = object.getSDObject();

    if (!objectEditor.showDialog(parentComponent, "Object Editor")) {
        return;  // Cancelled
    }

    // Controller manually constructs new object from editor getters
    SDObject newObject = new SDObject(
        objectEditor.getObjectName(),      // String getter
        objectEditor.getDesignClass()      // Type getter
    );

    // Controller handles the actual editing logic
    // ...
}
```

**Key observations:**

-   Editor doesn't return a domain object
-   Editor provides separate getters: `getObjectName()` + `getDesignClass()`
-   **Controller is responsible** for constructing the new domain object
-   This is fundamentally different from `editDialog()` pattern

### 4. **Type Management Side Effects**

TypedEntityEditor has **repository mutation side effects** during editing:

-   "Add..." button creates new types in repository
-   "Edit..." button modifies existing types in repository
-   "Delete" button removes types from repository
-   These operations happen **during** the dialog interaction, not just at OK

**Contrast with migrated editors:**

-   ClassifierEditor, UCExtendEditor, CallMessageEditor are **pure**
-   They only read from repository for autocomplete/validation
-   No repository mutations until controller applies changes
-   This purity makes the `editDialog()` pattern work cleanly

### 5. **Domain Object Characteristics**

The domain objects don't fully support the `Copyable<T>` pattern:

**SDObject, MultiObject, ActorInstance**:

-   Have `clone()` methods (good!)
-   But constructors require **two parameters**: name + type
-   The "edited object" is really a **tuple** (name, type), not a single copyable entity
-   Type (DesignClass, Actor) is managed separately via complex UI

**Example:**

```java
// Cannot do this cleanly:
SDObject editDialog(SDObject original, Component parent)

// Because the editor manages:
// 1. Name field (simple)
// 2. Type combo box with add/edit/delete (complex repository interaction)
```

## Migration Challenges

If we were to migrate TypedEntityEditor to `Editor<D>`:

### Challenge 1: Graphical Wrapper Removal

```java
// Current:
public ObjectEditor(SDObjectGR obj, CentralRepository cr)

// Migrated would need:
public ObjectEditor(CentralRepository cr)
SDObject editDialog(SDObject original, Component parent)

// But initialize() needs the graphical wrapper!
// Would need to pass SDObjectGR to editDialog too - defeats the purpose
```

### Challenge 2: Type Management Complexity

```java
SDObject editDialog(SDObject original, Component parent) {
    // Initialize name from original
    nameField.setText(original.getName());

    // Initialize type - but this triggers complex UI:
    // - Type combo box
    // - Add/Edit/Delete buttons
    // - Repository mutations during dialog interaction

    // How to return the edited object when type management
    // has already mutated the repository?

    if (!showDialog(parent, "Object Editor")) return null;

    // Must construct new object from separate parts
    return new SDObject(getEntityName(), getCurrentType());
}
```

### Challenge 3: Controller Refactoring Complexity

Controllers use undo/redo pattern with separate tracking:

```java
// Current pattern:
ObjectEdit undoEdit = new ObjectEdit(undoObject, originalObject.getDesignClass().getName());

// Would need significant restructuring to work with:
SDObject newObject = editor.editDialog(originalObject, parentComponent);
```

## Recommendation: Keep Current Design

**Recommendation:** Do NOT migrate TypedEntityEditor hierarchy to Editor<T> interface.

**Reasons:**

1. **Different architectural pattern** - manages types + entities, not just domain objects
2. **Graphical wrapper dependency** - fundamental to current design
3. **Repository side effects** - type management mutates repository during editing
4. **Complex two-part return** - returns name + type, not a single domain object
5. **Significant controller refactoring** - undo/redo system tightly coupled to current pattern
6. **No clear benefit** - current pattern works well for this use case

## Subclasses Analysis

All 5 subclasses follow the same pattern:

1. **ObjectEditor** - Edits SDObjectGR (Sequence Diagram objects)
2. **MultiObjectEditor** - Edits MultiObjectGR (Multiple instances in SD)
3. **ActorInstanceEditor** - Edits ActorInstanceGR (Actor instances in SD)
4. **SystemInstanceEditor** - Edits SystemInstanceGR (System instances in SSD)
5. **ObjectNodeEditor** - Edits ObjectNodeGR (Object nodes in Activity Diagrams)

All have:

-   Graphical wrapper in constructor
-   `initialize()` method reading from graphical wrapper
-   Separate getters for name and type
-   Same controller usage pattern

## Conclusion

The Editor<T> interface pattern was designed for **simple domain object editors** that:

-   Accept a domain object
-   Initialize UI from it
-   Return edited domain object or null
-   Have no side effects

TypedEntityEditor is a **complex type-managed editor** that:

-   Accepts graphical wrappers
-   Manages types via repository operations
-   Returns multiple values via separate getters
-   Has repository mutation side effects

These are **different concerns** that warrant **different patterns**. The current TypedEntityEditor design is appropriate for its use case and should not be forced into the Editor<T> interface pattern.

## Next Steps

-   Mark Phase 6 as complete (analysis done, migration not recommended)
-   Proceed to Phase 7: Handle special cases (AssociationEditor analysis)
