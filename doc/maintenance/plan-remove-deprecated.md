# Plan: Remove Deprecated API Calls

## Investigation

After applying the Template Method pattern and polymorphic edit() methods, several editor constructors and methods have been deprecated in favor of a cleaner Editor<T> pattern. These deprecated APIs are still being used extensively in test files and need to be removed.

### Deprecated Patterns

**Pattern 1: Deprecated Editor Constructors**
Many ClassifierEditor subclasses have deprecated constructors that take both the domain object and repository:

```java
// DEPRECATED
ActorEditor editor = new ActorEditor(actor, repository);
Actor result = editor.getActor();

// NEW PATTERN
ActorEditor editor = new ActorEditor(repository);
Actor result = editor.editDialog(actor, parent);
```

**Pattern 2: Deprecated initialize() methods**
Some editors have deprecated `initialize()` methods that should be replaced with `editDialog()`:

```java
// DEPRECATED
ObjectEditor editor = new ObjectEditor(object, repository);
editor.initialize(object);

// NEW PATTERN
ObjectEditor editor = new ObjectEditor(repository);
SDObject result = editor.editDialog(object, parent);
```

**Pattern 3: Deprecated EditContext class**
The `EditContext` class in `controller` package is now a deprecated placeholder. The real class is in `editing` package.

### Deprecated APIs Found

**ClassifierEditor subclasses with deprecated constructors:**

1. `ActorEditor(Actor, CentralRepository)` - Use `ActorEditor(CentralRepository)` + `editDialog(Actor, Component)`
2. `ClassEditor(DesignClass, CentralRepository)` - Use `ClassEditor(CentralRepository)` + `editDialog(DesignClass, Component)`
3. `InterfaceEditor(Interface, CentralRepository)` - Use `InterfaceEditor(CentralRepository)` + `editDialog(Interface, Component)`
4. `ConceptualClassEditor(ConceptualClass, CentralRepository)` - Use `ConceptualClassEditor(CentralRepository)` + `editDialog(ConceptualClass, Component)`

**RoleClassifierEditor subclasses with deprecated constructors:** 5. `ObjectEditor(SDObject, CentralRepository)` - Use `ObjectEditor(CentralRepository)` + `editDialog(SDObject, Component)` 6. `MultiObjectEditor(MultiObject, CentralRepository)` - Use `MultiObjectEditor(CentralRepository)` + `editDialog(MultiObject, Component)` 7. `ActorInstanceEditor(ActorInstance, CentralRepository)` - Use `ActorInstanceEditor(CentralRepository)` + `editDialog(ActorInstance, Component)` 8. `SystemInstanceEditor(SystemInstance, CentralRepository)` - Use `SystemInstanceEditor(CentralRepository)` + `editDialog(SystemInstance, Component)` 9. `ObjectNodeEditor(ObjectNode, CentralRepository)` - Use `ObjectNodeEditor(CentralRepository)` + `editDialog(ObjectNode, Component)`

**Association Editors with deprecated methods:** 10. `AssociationEditor.editDialog(Association, Component, ActionListener)` - Use `editDialog(Association, Component)` 11. `CCDAssociationEditor.editDialog(Association, Component, ActionListener)` - Use `editDialog(Association, Component)` 12. `ConceptualAssociationClassEditor.editDialog(...)` - Old signature 13. `DesignAssociationClassEditor.editDialog(...)` - Old signature

**Other deprecated methods:** 14. `ObjectNodeEditor.setSelectListener(ActionListener)` - No-op method, remove calls 15. `CallMessageEditor` - Has deprecated methods 16. `UCExtendEditor` - Has deprecated methods

**Deprecated placeholder class:** 17. `EditContext` in controller package - Just a placeholder, can be deleted

## Affected Components

### Test Files to Update (11 files)

1. **ActorEditorTest.java** - 7 usages of deprecated constructor
2. **ClassEditorTest.java** - Multiple usages
3. **InterfaceEditorTest.java** - Multiple usages
4. **ConceptualClassEditorTest.java** - Multiple usages
5. **ObjectEditorTest.java** - Multiple usages
6. **MultiObjectEditorTest.java** - Multiple usages
7. **ActorInstanceEditorTest.java** - Multiple usages
8. **SystemInstanceEditorTest.java** - Multiple usages
9. **ObjectNodeEditorTest.java** - Multiple usages
10. **CallMessageEditorTest.java** - Potential deprecated usage
11. **UCExtendEditorTest.java** - Potential deprecated usage

### Production Code Files

Most deprecated methods are not used in production code (already migrated to polymorphic edit()). Only need to:

1. Delete deprecated methods/constructors from editor classes
2. Delete `EditContext.java` placeholder in controller package

## Design Decisions

### Decision 1: Update Tests First or Delete Deprecated Code First?

**Options:**
A) Update all test files first, then delete deprecated code
B) Delete deprecated code and fix compilation errors
C) Do both incrementally (one editor class at a time)

**Decision:** Choose Option A - Update tests first

-   Reasons:
    -   Tests verify the new pattern works correctly
    -   Can run tests after each change to verify no regressions
    -   Safer approach - deprecated code acts as reference during migration
    -   Clear two-phase process: migrate callers, then remove deprecated APIs

### Decision 2: How to Handle showDialog() Mock in Tests?

Tests currently mock constructors or showDialog(). With new pattern:

```java
// OLD: Mock via anonymous subclass in constructor
ActorEditor editor = new ActorEditor(actor, repository) {
    @Override
    public boolean showDialog(Component parent, String title) {
        return true; // Mock OK
    }
};

// NEW: Still use anonymous subclass, but with new constructor
ActorEditor editor = new ActorEditor(repository) {
    @Override
    public boolean showDialog(Component parent, String title) {
        return true; // Mock OK
    }
};
Actor result = editor.editDialog(actor, null);
```

**Decision:** Keep anonymous subclass pattern for mocking

-   Reasons:
    -   Minimal changes to test structure
    -   No need for mocking framework
    -   Clear and explicit what's being mocked

### Decision 3: Handle editDialog() Return Values

The new `editDialog()` pattern returns `null` if cancelled, or the edited object if OK.

```java
// Tests need to check return value
Actor result = editor.editDialog(actor, null);
assertNotNull("Should return edited actor", result);
assertEquals("Name should match", "ExpectedName", result.getName());
```

**Decision:** Update assertions to check editDialog() return value

-   Most tests currently call `editor.getActor()` etc.
-   Need to change to check `editDialog()` return value

## TODO Tasks

### Phase 1: Update ClassifierEditor Test Files (4 files)

-   [ ] Task 1.1: Update ActorEditorTest.java

    -   Replace 7 usages of `new ActorEditor(actor, repository)`
    -   Update to use `new ActorEditor(repository)` + `editDialog(actor, null)`
    -   Update assertions to check editDialog() return value
    -   Run tests to verify

-   [ ] Task 1.2: Update ClassEditorTest.java

    -   Replace usages of `new ClassEditor(designClass, repository)`
    -   Update to use `new ClassEditor(repository)` + `editDialog(designClass, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 1.3: Update InterfaceEditorTest.java

    -   Replace usages of `new InterfaceEditor(interface, repository)`
    -   Update to use `new InterfaceEditor(repository)` + `editDialog(interface, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 1.4: Update ConceptualClassEditorTest.java
    -   Replace usages of `new ConceptualClassEditor(conceptualClass, repository)`
    -   Update to use `new ConceptualClassEditor(repository)` + `editDialog(conceptualClass, null)`
    -   Update assertions
    -   Run tests to verify

### Phase 2: Update RoleClassifierEditor Test Files (5 files)

-   [ ] Task 2.1: Update ObjectEditorTest.java

    -   Replace usages of `new ObjectEditor(object, repository)`
    -   Update to use `new ObjectEditor(repository)` + `editDialog(object, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 2.2: Update MultiObjectEditorTest.java

    -   Replace usages of `new MultiObjectEditor(multiObject, repository)`
    -   Update to use `new MultiObjectEditor(repository)` + `editDialog(multiObject, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 2.3: Update ActorInstanceEditorTest.java

    -   Replace usages of `new ActorInstanceEditor(actorInstance, repository)`
    -   Update to use `new ActorInstanceEditor(repository)` + `editDialog(actorInstance, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 2.4: Update SystemInstanceEditorTest.java

    -   Replace usages of `new SystemInstanceEditor(systemInstance, repository)`
    -   Update to use `new SystemInstanceEditor(repository)` + `editDialog(systemInstance, null)`
    -   Update assertions
    -   Run tests to verify

-   [ ] Task 2.5: Update ObjectNodeEditorTest.java
    -   Replace usages of `new ObjectNodeEditor(objectNode, repository)`
    -   Update to use `new ObjectNodeEditor(repository)` + `editDialog(objectNode, null)`
    -   Remove any calls to deprecated `setSelectListener()`
    -   Update assertions
    -   Run tests to verify

### Phase 3: Update Other Editor Test Files (2 files)

-   [ ] Task 3.1: Update CallMessageEditorTest.java

    -   Check for deprecated method usage
    -   Update if needed
    -   Run tests to verify

-   [ ] Task 3.2: Update UCExtendEditorTest.java
    -   Check for deprecated method usage
    -   Update if needed
    -   Run tests to verify

### Phase 4: Run Full Test Suite

-   [ ] Task 4.1: Compile all tests

    -   Run: `mvn test-compile`
    -   Fix any compilation errors

-   [ ] Task 4.2: Run full test suite
    -   Run: `mvn test`
    -   Verify all tests pass

### Phase 5: Remove Deprecated Code from Production

-   [ ] Task 5.1: Remove deprecated constructors from ClassifierEditor subclasses

    -   Delete deprecated constructor from `ActorEditor`
    -   Delete deprecated constructor from `ClassEditor`
    -   Delete deprecated constructor from `InterfaceEditor`
    -   Delete deprecated constructor from `ConceptualClassEditor`

-   [ ] Task 5.2: Remove deprecated constructors/methods from RoleClassifierEditor subclasses

    -   Delete deprecated constructor from `ObjectEditor`
    -   Delete deprecated `initialize()` method from `ObjectEditor`
    -   Delete deprecated constructor from `MultiObjectEditor`
    -   Delete deprecated `initialize()` method from `MultiObjectEditor`
    -   Delete deprecated constructor from `ActorInstanceEditor`
    -   Delete deprecated `initialize()` method from `ActorInstanceEditor`
    -   Delete deprecated constructor from `SystemInstanceEditor`
    -   Delete deprecated `initialize()` method from `SystemInstanceEditor`
    -   Delete deprecated constructor from `ObjectNodeEditor`
    -   Delete deprecated `setSelectListener()` method from `ObjectNodeEditor`

-   [ ] Task 5.3: Remove deprecated methods from Association editors

    -   Delete deprecated `editDialog()` signature from `AssociationEditor`
    -   Delete deprecated `editDialog()` signature from `CCDAssociationEditor`
    -   Check and remove deprecated methods from `ConceptualAssociationClassEditor`
    -   Check and remove deprecated methods from `DesignAssociationClassEditor`

-   [ ] Task 5.4: Remove deprecated methods from other editors

    -   Check and remove deprecated methods from `CallMessageEditor`
    -   Check and remove deprecated methods from `UCExtendEditor`

-   [ ] Task 5.5: Delete deprecated EditContext placeholder
    -   Delete file: `src/main/java/edu/city/studentuml/controller/EditContext.java`

### Phase 6: Final Verification

-   [ ] Task 6.1: Compile production code

    -   Run: `mvn compile`
    -   Should succeed with no warnings

-   [ ] Task 6.2: Run full test suite

    -   Run: `mvn test`
    -   All tests should pass

-   [ ] Task 6.3: Search for @Deprecated annotations

    -   Run: `grep -r "@Deprecated" src/main/java/`
    -   Should find no deprecated code in production (excluding legitimate external API deprecations)

-   [ ] Task 6.4: Search for deprecated usage
    -   Run: `mvn compile -Xlint:deprecation`
    -   Should report no deprecation warnings

## Implementation Summary

[To be filled at completion]

## Benefits

1. **Cleaner API**: Single responsibility - editors edit, don't store state
2. **Testability**: Easier to test with Editor<T> pattern
3. **Consistency**: All editors follow same pattern
4. **Maintainability**: Less code, single pattern to understand
5. **Aligns with Template Method**: Completes the polymorphic edit() migration
