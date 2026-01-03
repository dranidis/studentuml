# Plan: Extract Template Method for edit() in AbstractClassGR

## Investigation

### Current State

Three classes have nearly identical `edit()` methods:

-   `ClassGR.edit()` (extends AbstractClassGR)
-   `ConceptualClassGR.edit()` (extends AbstractClassGR)
-   `InterfaceGR.edit()` (extends GraphicalElement directly)

### Common Pattern

All three follow this exact structure:

1. Get repository from context
2. Get original domain object from `this`
3. Create appropriate editor
4. Show editor dialog and get new domain object
5. Return false if user cancelled
6. **Name conflict handling** (Pattern 2 - silent merge):
    - If name changed AND new name exists in repository AND new name not blank:
        - Replace graphical element's reference with existing domain object
        - Remove original domain object if it had blank name
    - Else:
        - Create UndoableEdit
        - Update repository
        - Post undo edit
7. Notify model changed
8. Reload SystemWideObjectNamePool
9. Return true

### Variations (must be abstracted)

| Aspect            | ClassGR              | ConceptualClassGR             | InterfaceGR             |
| ----------------- | -------------------- | ----------------------------- | ----------------------- |
| Domain type       | DesignClass          | ConceptualClass               | Interface               |
| Editor type       | ClassEditor          | ConceptualClassEditor         | InterfaceEditor         |
| Edit type         | EditDCDClassEdit     | EditCCDClassEdit              | EditInterfaceEdit       |
| Getter            | getDesignClass()     | getConceptualClass()          | getInterface()          |
| Setter            | setDesignClass()     | setConceptualClass()          | setInterface()          |
| Repository get    | getDesignClass(name) | getConceptualClass(name)      | getInterface(name)      |
| Repository remove | removeClass(obj)     | removeConceptualClass(obj)    | removeInterface(obj)    |
| Repository edit   | editClass(old, new)  | editConceptualClass(old, new) | editInterface(old, new) |

## Design Decisions

### Approach: Template Method Pattern in AbstractClassGR

**Decision**: Add template method to `AbstractClassGR` that can be used by `ClassGR` and `ConceptualClassGR`.

**Rationale**:

-   `ClassGR` and `ConceptualClassGR` already extend `AbstractClassGR`
-   They share the same pattern and similar semantics (both are classes)
-   `InterfaceGR` intentionally extends `GraphicalElement` directly (different semantics)
-   While Interface could benefit, forcing it into AbstractClassGR violates SRP

### Abstract Methods Needed

Subclasses must implement these hook methods:

```java
protected abstract T getClassifier(); // Get domain object
protected abstract void setClassifier(T classifier); // Set domain object
protected abstract AbstractClassEditor<T> createEditor(CentralRepository repo);
protected abstract T getClassifierFromRepository(CentralRepository repo, String name);
protected abstract void removeClassifierFromRepository(CentralRepository repo, T classifier);
protected abstract void editClassifierInRepository(CentralRepository repo, T original, T edited);
protected abstract UndoableEdit createEditUndoableEdit(T original, T edited, DiagramModel model);
```

### Generic Type Parameter

`AbstractClassGR` will need to be generic: `AbstractClassGR<T extends AbstractClass>`

-   `ClassGR extends AbstractClassGR<DesignClass>`
-   `ConceptualClassGR extends AbstractClassGR<ConceptualClass>`

## Affected Components

### Files to Modify

1. **AbstractClassGR.java**
    - Add generic type parameter `<T extends AbstractClass>`
    - Add template method `edit(EditContext)` with full logic
    - Add 7 abstract hook methods
2. **ClassGR.java**

    - Change to `ClassGR extends AbstractClassGR<DesignClass>`
    - Remove `edit()` method (now inherited)
    - Implement 7 abstract hook methods

3. **ConceptualClassGR.java**

    - Change to `ConceptualClassGR extends AbstractClassGR<ConceptualClass>`
    - Remove `edit()` method (now inherited)
    - Implement 7 abstract hook methods

4. **InterfaceGR.java**
    - No changes (keep separate for now)
    - Could be refactored later if we create a common parent for all classifiers

### New Abstract Class Needed

Need to create abstract editor class for the hook method return type:

```java
public abstract class AbstractClassEditor<T extends Classifier> {
    public abstract T editDialog(T original, Component parent);
}
```

Then make `ClassEditor`, `ConceptualClassEditor`, `InterfaceEditor` extend it.

## TODO Tasks

-   [ ] Task 1: Create `AbstractClassEditor<T>` base class
    -   Create abstract class with generic type
    -   Add abstract `editDialog()` method
    -   Update ClassEditor, ConceptualClassEditor to extend it
-   [ ] Task 2: Make `AbstractClassGR` generic
    -   Add generic type parameter `<T extends AbstractClass>`
    -   Update field `protected AbstractClass abstractClass` to `protected T abstractClass`
    -   Update all methods using `AbstractClass` to use `T`
-   [ ] Task 3: Add template method and hook methods to AbstractClassGR

    -   Add 7 abstract hook methods
    -   Add template method `edit(EditContext)` with full pattern logic
    -   Add comprehensive javadoc explaining the pattern

-   [ ] Task 4: Refactor ClassGR

    -   Update class declaration to use generic
    -   Remove `edit()` method
    -   Implement 7 hook methods
    -   Run tests

-   [ ] Task 5: Refactor ConceptualClassGR

    -   Update class declaration to use generic
    -   Remove `edit()` method
    -   Implement 7 hook methods
    -   Run tests

-   [ ] Task 6: Run full test suite and verify

## Implementation Notes

### Hook Method Implementations

**ClassGR hook methods**:

```java
@Override
protected DesignClass getClassifier() {
    return getDesignClass();
}

@Override
protected void setClassifier(DesignClass classifier) {
    setDesignClass(classifier);
}

@Override
protected AbstractClassEditor<DesignClass> createEditor(CentralRepository repo) {
    return new ClassEditor(repo);
}

@Override
protected DesignClass getClassifierFromRepository(CentralRepository repo, String name) {
    return repo.getDesignClass(name);
}

@Override
protected void removeClassifierFromRepository(CentralRepository repo, DesignClass classifier) {
    repo.removeClass(classifier);
}

@Override
protected void editClassifierInRepository(CentralRepository repo, DesignClass original, DesignClass edited) {
    repo.editClass(original, edited);
}

@Override
protected UndoableEdit createEditUndoableEdit(DesignClass original, DesignClass edited, DiagramModel model) {
    return new EditDCDClassEdit(original, edited, model);
}
```

**ConceptualClassGR hook methods**: Similar pattern with ConceptualClass types.

### Benefits

1. **DRY**: 40+ lines of duplicated code → single template method
2. **Maintainability**: Bug fixes in one place
3. **Consistency**: Guaranteed same behavior across subclasses
4. **Extensibility**: Easy to add new class types
5. **Testability**: Can test template logic once in AbstractClassGR

### Risks

-   Generic type parameters add complexity
-   Must ensure all subclasses implement hooks correctly
-   Need to maintain backwards compatibility with existing code

## Testing Strategy

1. Run existing tests for ClassGR and ConceptualClassGR (should still pass)
2. Verify edit() behavior unchanged for both classes
3. Verify undo/redo still works
4. Verify repository synchronization still works

## Alternative Considered

**Interface Inclusion**: Could create `AbstractClassifierGR<T extends Classifier>` as common parent for AbstractClassGR and InterfaceGR. Decided against because:

-   Interface and Class have different rendering logic (methods vs attributes/methods)
-   Different field layouts and visual representations
-   Would require extensive refactoring of drawing logic
-   Current separation is architecturally sound

## Implementation Summary

### Refactoring Complete ✅

Successfully eliminated **84 lines of duplicate code** (42 lines × 2 classes) by implementing the Template Method pattern.

**Changes Made:**

1. **AbstractClassGR.java** - Made generic with template method:

    - Changed to `AbstractClassGR<T extends AbstractClass & Copyable<T>>`
    - Added final template method `edit(EditContext context)` (56 lines including javadoc)
    - Added 7 abstract hook methods for subclass customization
    - Intersection type constraint ensures T works with Editor<T> interface

2. **ClassGR.java** - Refactored to use template method:

    - Updated to `extends AbstractClassGR<DesignClass>`
    - **Removed 42-line edit() method**
    - Implemented 7 hook methods (7 concise methods, 1-2 lines each):
        - `getClassifierFromThis()` → `return getDesignClass()`
        - `setClassifierInThis()` → `setDesignClass(classifier)`
        - `createEditor()` → `return new ClassEditor(repository)`
        - `getClassifierFromRepository()` → `repository.getDesignClass(name)`
        - `removeClassifierFromRepository()` → `repository.removeClass(classifier)`
        - `editClassifierInRepository()` → `repository.editClass(original, edited)`
        - `createEditUndoableEdit()` → `new EditDCDClassEdit(original, edited, model)`

3. **ConceptualClassGR.java** - Refactored to use template method:
    - Updated to `extends AbstractClassGR<ConceptualClass>`
    - **Removed 42-line edit() method**
    - Implemented 7 hook methods (same pattern as ClassGR):
        - `getClassifierFromThis()` → `return getConceptualClass()`
        - `setClassifierInThis()` → `setConceptualClass(classifier)`
        - `createEditor()` → `return new ConceptualClassEditor(repository)`
        - `getClassifierFromRepository()` → `repository.getConceptualClass(name)`
        - `removeClassifierFromRepository()` → `repository.removeConceptualClass(classifier)`
        - `editClassifierInRepository()` → `repository.editConceptualClass(original, edited)`
        - `createEditUndoableEdit()` → `new EditCCDClassEdit(original, edited, model)`

**Test Results:**

-   ✅ **All 352 tests passed** (0 failures, 0 errors, 1 skipped)
-   Verified with `mvn clean test`
-   No behavioral changes detected
-   Both ClassGR and ConceptualClassGR work correctly with template method

**Benefits Achieved:**

1. **DRY Principle**: Single implementation of Pattern 2 (name conflict resolution)
2. **Maintainability**: Bug fixes only need to be applied in one place
3. **Consistency**: Guaranteed identical behavior across DCD and CCD
4. **Type Safety**: Generic constraints enforce correct usage at compile time
5. **Extensibility**: Easy to add new class types by extending AbstractClassGR

**Architecture Decision:**

-   InterfaceGR kept separate (extends GraphicalElement directly) due to different semantics (no attributes, different rendering)
-   Editor<T> interface already existed in codebase - no new abstractions needed
-   Copyable<T> constraint required for Editor<T> compatibility
