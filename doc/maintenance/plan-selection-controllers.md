# Plan: Refactor Selection Controller Edit Methods

## Goal

Extract common patterns from `edit*()` methods in selection controllers into a template method or policy pattern to reduce code duplication and improve maintainability.

## Investigation

### Current Situation

After the recent refactoring of editors (plan-editors.md, plan-editor-interface.md), all diagram editors now implement the `Editor<T>` interface with the `editDialog(T element, Component parent)` method. This creates an opportunity to identify and extract common patterns in the selection controller `edit*()` methods.

### Controllers Analyzed

1. **SelectionController** (base class)

    - `editUMLNote()`

2. **DCDSelectionController** (Design Class Diagram)

    - `editClass()`
    - `editInterface()`
    - `editAssociation()`
    - `editDependency()`
    - `editAssociationClass()`

3. **CCDSelectionController** (Conceptual Class Diagram)

    - `editClass()`
    - `editAssociation()`
    - `editAssociationClass()`

4. **ADSelectionController** (Activity Diagram)

    - `editControlFlow()`
    - `editObjectFlow()`
    - `editActionNode()`
    - `editObjectNode()`
    - `editActivityNode()`
    - `editDecisionNode()`

5. **UCDSelectionController** (Use Case Diagram)

    - `editActor()`
    - `editUseCase()`
    - `editSystem()`
    - `editExtend()`

6. **SDSelectionController** (Sequence Diagram)

    - `editSDObject()`
    - `editMultiObject()`

7. **SSDSelectionController** (System Sequence Diagram)

    - `editSystemInstance()`

8. **AbstractSDSelectionController** (base for SD/SSD)
    - `editActorInstance()`
    - `editCallMessage()`
    - `editReturnMessage()`

### Identified Patterns

#### Pattern 1: Simple Domain Edit (COMPLETE - Template Method Implemented) ✅

**Structure:**

```java
private void editXXX(XXXGR graphicalElement) {
    // 1. Extract domain object from graphical wrapper
    NodeComponent originalObject = graphicalElement.getComponent();

    // 2. Show StringEditorDialog for simple name editing
    StringEditorDialog dialog = new StringEditorDialog(parentComponent, "Title", "Label:", originalObject.getName());

    // 3. Handle cancellation
    if (!dialog.showDialog()) {
        return;
    }

    // 4. Clone original for undo (before modification)
    NodeComponent undoObject = originalObject.clone();

    // 5. Apply changes to domain object
    originalObject.setName(dialog.getText());

    // 6. Create and post undo edit
    UndoableEdit edit = new EditXXXEdit(originalObject, undoObject, model);
    parentComponent.getUndoSupport().postEdit(edit);

    // 7. Notify observers
    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**REFACTORED TO:** Template method in `NodeComponentGR.editNameWithDialog()` with `UndoableEditFactory` interface

**Actual Pattern 1 Examples (COMPLETE):**

-   `ADSelectionController.editActivityNode()` ✅ - Migrated to ActivityNodeGR.edit()
-   `ADSelectionController.editDecisionNode()` ✅ - Migrated to DecisionNodeGR.edit()
-   `ADSelectionController.editActionNode()` ✅ - Migrated to ActionNodeGR.edit()

**NOT Pattern 1 (Incorrectly Classified):**

-   ❌ `DCDSelectionController.editAssociationClass()` - Complex editor with attributes/methods (Pattern 5)
-   ❌ `CCDSelectionController.editAssociationClass()` - Complex editor with attributes/methods (Pattern 5)
-   ❌ `UCDSelectionController.editExtend()` - Extension points list editor (Pattern 5)
-   ❌ `SelectionController.editUMLNote()` - JTextArea multi-line editor (Pattern 6)

**Key Steps:**

1. Get domain object from graphical element
2. Show editor dialog
3. Handle cancellation
4. Clone original for undo
5. Apply changes
6. Post undo edit
7. Notify model changed

#### Pattern 2: Name Conflict Check (Classifiers)

**Structure:**

```java
private void editClassifier(ClassifierGR graphicalElement) {
    CentralRepository repository = model.getCentralRepository();
    Classifier originalClassifier = graphicalElement.getClassifier();
    ClassifierEditor editor = new ClassifierEditor(repository);

    // Show dialog
    Classifier newClassifier = editor.editDialog(originalClassifier, parentComponent);
    if (newClassifier == null) {
        return;
    }

    // Check for name conflicts
    if (!originalClassifier.getName().equals(newClassifier.getName())
            && repository.getClassifier(newClassifier.getName()) != null
            && !newClassifier.getName().equals("")) {

        // CONFLICT HANDLING:
        // Option A: Replace graphical reference with existing classifier
        graphicalElement.setClassifier(repository.getClassifier(newClassifier.getName()));

        // Remove original if it was unnamed
        if (originalClassifier.getName().equals("")) {
            repository.removeClassifier(originalClassifier);
        }

    } else {
        // NO CONFLICT: Normal edit
        UndoableEdit edit = new EditClassifierEdit(originalClassifier, newClassifier, model);
        repository.editClassifier(originalClassifier, newClassifier);
        parentComponent.getUndoSupport().postEdit(edit);
    }

    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**Examples:**

-   `DCDSelectionController.editClass()` - Handles DesignClass name conflicts
-   `DCDSelectionController.editInterface()` - Handles Interface name conflicts
-   `CCDSelectionController.editClass()` - Handles ConceptualClass name conflicts
-   `UCDSelectionController.editActor()` - Shows error message instead of replacing
-   `UCDSelectionController.editUseCase()` - Shows error message instead of replacing
-   `UCDSelectionController.editSystem()` - Shows error message instead of replacing

**Key Steps:**

1. Get repository and domain object
2. Show editor dialog
3. Handle cancellation
4. **Check if name changed and conflicts with existing element**
5. **If conflict: Either replace reference OR show error**
6. If no conflict: Create undo edit and apply changes
7. Notify model changed

**Variation:**

-   **DCD/CCD**: Replace graphical reference with existing element
-   **UCD**: Show error message and reject edit

#### Pattern 3: TypedEntity Edit with CompoundEdit

**Structure:**

```java
private void editTypedEntity(TypedEntityGR graphicalElement) {
    CentralRepository repository = model.getCentralRepository();
    TypedEntity originalEntity = graphicalElement.getTypedEntity();

    // Create editor with initial result
    TypedEntityEditor editor = new TypedEntityEditor(repository);
    TypedEntityEditResult<Type, TypedEntity> initialResult =
        new TypedEntityEditResult<>(originalEntity, new ArrayList<>());

    // Show dialog
    TypedEntityEditResult<Type, TypedEntity> result =
        editor.editDialog(initialResult, parentComponent);

    if (result == null) {
        return;
    }

    TypedEntity newEntity = result.getDomainObject();

    // Setup undo
    TypedEntity undoEntity = originalEntity.clone();
    TypedEntityEdit undoEdit = new TypedEntityEdit(undoEntity, originalEntity.getType().getName());

    // Create compound edit for type operations + domain edit
    CompoundEdit compoundEdit = new CompoundEdit();

    // Apply type operations (may create/edit/remove types in repository)
    TypeRepositoryOperations<Type> typeOps = new TypeRepositoryOperations<>();
    for (TypeOperation<Type> typeOp : result.getTypeOperations()) {
        typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
    }

    // Handle name conflicts (similar to Pattern 2)
    if (!originalEntity.getName().equals(newEntity.getName())
            && repository.getEntity(newEntity.getName()) != null
            && !newEntity.getName().equals("")) {

        // Ask user if they want to refer to existing entity
        int response = JOptionPane.showConfirmDialog(...);
        if (response == JOptionPane.YES_OPTION) {
            graphicalElement.setTypedEntity(repository.getEntity(newEntity.getName()));
            if (originalEntity.getName().equals("")) {
                repository.removeEntity(originalEntity);
            }
        }
    } else {
        // Apply domain edit
        repository.editEntity(originalEntity, newEntity);
        TypedEntityEdit originalEdit = new TypedEntityEdit(originalEntity, originalEntity.getType().getName());
        compoundEdit.addEdit(new EditTypedEntityEdit(originalEdit, undoEdit, model));
    }

    // Post compound edit
    compoundEdit.end();
    if (!compoundEdit.isInProgress() && compoundEdit.canUndo()) {
        parentComponent.getUndoSupport().postEdit(compoundEdit);
    }

    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**Examples:**

-   `SDSelectionController.editSDObject()` - SDObject with DesignClass type
-   `SDSelectionController.editMultiObject()` - MultiObject with DesignClass type
-   `SSDSelectionController.editSystemInstance()` - SystemInstance with System type
-   `AbstractSDSelectionController.editActorInstance()` - ActorInstance with Actor type
-   `ADSelectionController.editObjectNode()` - ObjectNode with DesignClass type (slightly different)

**Key Steps:**

1. Get repository and domain object
2. Create `TypedEntityEditResult` with initial entity
3. Show editor dialog (returns result with type operations)
4. Handle cancellation
5. Setup undo state
6. **Create CompoundEdit to group multiple operations**
7. **Apply type operations first (may create new types in repository)**
8. Check for name conflicts (optional)
9. Apply domain edit
10. Post compound edit
11. Notify model changed

**Special Case:**

-   `ADSelectionController.editObjectNode()` - Validates that name or type is not empty before proceeding

#### Pattern 4: Validation-Heavy Edit

**Structure:**

```java
private void editWithValidation(XXXGR graphicalElement) {
    DomainObject originalObject = graphicalElement.getDomainObject();
    XXXEditor editor = new XXXEditor();

    // Show dialog
    DomainObject editedObject = editor.editDialog(originalObject, parentComponent);
    if (editedObject == null) {
        return;
    }

    // Clone for undo
    DomainObject undoObject = originalObject.clone();

    // COMPLEX VALIDATION LOGIC
    // Extract value(s) from edited object
    String value = editedObject.getValue();

    // Validate against context
    if (!isValid(value, context)) {
        JOptionPane.showMessageDialog(parentComponent,
            "Validation error message",
            "Error Title",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Apply changes
    originalObject.setValue(value);

    // Post undo edit
    UndoableEdit edit = new EditXXXEdit(originalObject, undoObject, model);
    parentComponent.getUndoSupport().postEdit(edit);

    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**Examples:**

-   `ADSelectionController.editControlFlow()` - Validates guard uniqueness from decision nodes
-   `ADSelectionController.editObjectFlow()` - Validates guard uniqueness and weight format

**Key Steps:**

1. Get domain object
2. Show editor dialog
3. Handle cancellation
4. Clone for undo
5. **Extract values and validate against context**
6. **Show error dialog and abort if validation fails**
7. Apply changes
8. Post undo edit
9. Notify model changed

#### Pattern 5: In-Place Property Edit

**Structure:**

```java
private void editProperty(XXXGR graphicalElement) {
    DomainObject domainObject = graphicalElement.getDomainObject();
    XXXEditor editor = new XXXEditor();

    // Show dialog
    DomainObject editedObject = editor.editDialog(domainObject, parentComponent);
    if (editedObject == null) {
        return;
    }

    // Clone for undo
    DomainObject undoObject = domainObject.clone();

    // Apply changes to original object (in-place modification)
    domainObject.setProperty1(editedObject.getProperty1());
    domainObject.setProperty2(editedObject.getProperty2());
    // ... more properties

    // Post undo edit
    UndoableEdit edit = new EditXXXEdit(domainObject, undoObject, model);
    parentComponent.getUndoSupport().postEdit(edit);

    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**Examples:**

-   `DCDSelectionController.editAssociation()` - Copies all association properties
-   `CCDSelectionController.editAssociation()` - Copies all association properties (slightly different)
-   `AbstractSDSelectionController.editCallMessage()` - Copies message properties

**Key Steps:**

1. Get domain object
2. Show editor dialog
3. Handle cancellation
4. Clone for undo
5. **Apply properties from edited object to original (in-place)**
6. Post undo edit
7. Notify model changed

**Difference from Pattern 1:**

-   Pattern 1 uses `copyOf()` or `repository.editXXX()` to apply ALL changes
-   Pattern 5 explicitly copies each property (more control, but more verbose)

#### Pattern 6: Simple Value Edit

**Structure:**

```java
private void editSimpleValue(XXXGR graphicalElement) {
    DomainObject domainObject = graphicalElement.getDomainObject();

    // Capture current value for undo
    String undoValue = domainObject.getValue();

    // Show simple dialog (StringEditorDialog, etc.)
    StringEditorDialog dialog = new StringEditorDialog(parentComponent, "Title", "Label:", undoValue);
    if (!dialog.showDialog()) {
        return;
    }

    String newValue = dialog.getText();

    // Optional: Only create undo if value actually changed
    if ((undoValue == null && newValue != null) ||
            (undoValue != null && !undoValue.equals(newValue))) {

        // Apply change
        domainObject.setValue(newValue);

        // Post undo edit
        UndoableEdit edit = new EditXXXEdit(domainObject, undoValue, newValue, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }
}
```

**Examples:**

-   `DCDSelectionController.editDependency()` - Edits stereotype string
-   `AbstractSDSelectionController.editReturnMessage()` - Edits return message name

**Key Steps:**

1. Get domain object
2. Capture current value for undo
3. Show simple dialog
4. Handle cancellation
5. **Optional: Check if value changed**
6. Apply change
7. Post undo edit (may include old/new values instead of cloned object)
8. Notify model changed

**Note:** This pattern typically doesn't clone the entire object, just captures the specific value being edited.

---

## Common Elements Across All Patterns

Despite the variations, ALL edit methods share these common steps:

1. **Extract domain object** from graphical element
2. **Show editor dialog** and get edited result
3. **Handle cancellation** (return early if null)
4. **Capture undo state** (clone object or capture values)
5. **Apply changes** (various methods)
6. **Create and post UndoableEdit**
7. **Notify observers** (`model.modelChanged()` + `SystemWideObjectNamePool.reload()`)

## Refactoring Opportunities

### Opportunity 1: Polymorphic Edit Method on GraphicalElement (RECOMMENDED)

**Key Insight:** The current architecture already uses polymorphism via `editElementMapper` in `SelectionController.java`:

```java
// Current implementation (line 264)
private void mapeditElement(GraphicalElement element) {
    Consumer<GraphicalElement> editElementConsumer = editElementMapper.get(element.getClass());
    if (editElementConsumer != null) {
        editElementConsumer.accept(element);
    }
}
```

**Better Approach:** Move edit logic INTO the GraphicalElement hierarchy itself:

```java
// Add to GraphicalElement base class
public abstract class GraphicalElement {
    /**
     * Opens an editor dialog for this graphical element.
     * Returns true if edit was applied, false if cancelled.
     *
     * @param context provides access to model, repository, parent component, undo support
     * @return true if edit was successfully applied, false if cancelled
     */
    public boolean edit(EditContext context) {
        // Default implementation: element is not editable
        return false;
    }
}

// EditContext encapsulates controller dependencies
public class EditContext {
    private final DiagramModel model;
    private final CentralRepository repository;
    private final Component parentComponent;
    private final UndoSupport undoSupport;

    public EditContext(DiagramModel model, Component parentComponent, UndoSupport undoSupport) {
        this.model = model;
        this.repository = model.getCentralRepository();
        this.parentComponent = parentComponent;
        this.undoSupport = undoSupport;
    }

    // Notification helper
    public void notifyModelChanged() {
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // Getters...
}
```

**Example Implementation in ClassGR:**

```java
public class ClassGR extends GraphicalElement {
    @Override
    public boolean edit(EditContext context) {
        DesignClass originalClass = this.getDesignClass();
        ClassEditor editor = new ClassEditor(context.getRepository());

        DesignClass newClass = editor.editDialog(originalClass, context.getParentComponent());
        if (newClass == null) {
            return false; // User cancelled
        }

        // Handle name conflicts
        if (!originalClass.getName().equals(newClass.getName())
                && context.getRepository().getDesignClass(newClass.getName()) != null
                && !newClass.getName().equals("")) {
            this.setDesignClass(context.getRepository().getDesignClass(newClass.getName()));
            if (originalClass.getName().equals("")) {
                context.getRepository().removeClass(originalClass);
            }
        } else {
            UndoableEdit edit = new EditDCDClassEdit(originalClass, newClass, context.getModel());
            context.getRepository().editClass(originalClass, newClass);
            context.getUndoSupport().postEdit(edit);
        }

        context.notifyModelChanged();
        return true;
    }
}
```

**Usage in SelectionController:**

```java
// Simplified - just call edit() on the element!
private void mapeditElement(GraphicalElement element) {
    EditContext context = new EditContext(model, parentComponent, parentComponent.getUndoSupport());
    element.edit(context);
}

// Or even simpler at lines 415 and 563:
if (element != null) {
    element.edit(new EditContext(model, parentComponent, parentComponent.getUndoSupport()));
}
```

**Benefits:**

-   **True polymorphism** - each element knows how to edit itself
-   **Eliminates editElementMapper** - no more registration needed
-   **Simplifies SelectionController** - from 28+ edit methods across subclasses to ZERO
-   **Clear separation of concerns** - GraphicalElement owns its editing behavior
-   **Easy to test** - can test edit() on each element independently
-   **Consistent pattern** - all elements follow same interface
-   **Handles all 6 patterns** - each element implements its specific logic

**Migration Path:**

1. Add `EditContext` class
2. Add default `edit()` method to `GraphicalElement` (returns false)
3. Implement `edit()` in specific subclasses one at a time
4. Remove corresponding entries from `editElementMapper`
5. When all migrated, remove `mapeditElement()` and call `element.edit()` directly
6. Delete all `edit*()` methods from controller subclasses

**Drawbacks:**

-   **More complex for simple elements** - but keeps logic with the element
-   **EditContext adds coupling** - but isolates controller dependencies
-   **Large refactoring** - but incremental migration possible

### Opportunity 2: Edit Policy Pattern

Create a policy interface for different edit strategies:

```java
public interface EditPolicy<T extends Copyable<T>> {
    /**
     * Applies the edit operation and returns an UndoableEdit.
     * May perform validation, conflict checking, or other pre-processing.
     */
    UndoableEdit applyEdit(T originalObject, T editedObject, DiagramModel model);
}
```

**Concrete Policies:**

1. **SimpleEditPolicy** - Pattern 1 (most common)

```java
public class SimpleEditPolicy<T extends Copyable<T>> implements EditPolicy<T> {
    @Override
    public UndoableEdit applyEdit(T originalObject, T editedObject, DiagramModel model) {
        T undoObject = originalObject.clone();
        originalObject.copyOf(editedObject);
        return new GenericEditEdit<>(originalObject, undoObject, model);
    }
}
```

2. **NameConflictCheckPolicy** - Pattern 2

```java
public class NameConflictCheckPolicy<T extends NamedDomainObject> implements EditPolicy<T> {
    private final CentralRepository repository;
    private final Function<String, T> repositoryLookup;

    @Override
    public UndoableEdit applyEdit(T originalObject, T editedObject, DiagramModel model) {
        if (!originalObject.getName().equals(editedObject.getName())
                && repositoryLookup.apply(editedObject.getName()) != null
                && !editedObject.getName().equals("")) {
            // Handle conflict...
        } else {
            // Normal edit...
        }
    }
}
```

3. **ValidatingEditPolicy** - Pattern 4

```java
public class ValidatingEditPolicy<T extends Copyable<T>> implements EditPolicy<T> {
    private final Predicate<T> validator;
    private final String errorMessage;

    @Override
    public UndoableEdit applyEdit(T originalObject, T editedObject, DiagramModel model) {
        if (!validator.test(editedObject)) {
            throw new ValidationException(errorMessage);
        }
        // Apply edit...
    }
}
```

4. **CompoundEditPolicy** - Pattern 3

```java
public class CompoundEditPolicy<T extends TypedEntity> implements EditPolicy<T> {
    @Override
    public UndoableEdit applyEdit(T originalObject, T editedObject, DiagramModel model) {
        CompoundEdit compoundEdit = new CompoundEdit();
        // Apply type operations...
        // Apply domain edit...
        compoundEdit.end();
        return compoundEdit;
    }
}
```

**Enhanced Template Method:**

```java
protected <T extends Copyable<T>> void editWithDialog(
        T domainObject,
        Editor<T> editor,
        EditPolicy<T> policy) {

    T editedObject = editor.editDialog(domainObject, parentComponent);
    if (editedObject == null) {
        return;
    }

    try {
        UndoableEdit undoEdit = policy.applyEdit(domainObject, editedObject, model);
        if (undoEdit != null) {
            parentComponent.getUndoSupport().postEdit(undoEdit);
        }
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    } catch (ValidationException e) {
        JOptionPane.showMessageDialog(parentComponent, e.getMessage(),
            "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
```

**Usage Example:**

```java
private void editAssociationClass(AssociationClassGR associationClassGR) {
    DesignAssociationClass originalObject = associationClassGR.getAssociationClass();
    DesignAssociationClassEditor editor = new DesignAssociationClassEditor(model.getCentralRepository());

    editWithDialog(originalObject, editor, new SimpleEditPolicy<>());
}
```

**Benefits:**

-   Highly flexible - each pattern is a separate policy
-   Easy to combine policies (decorator pattern)
-   Testable in isolation
-   Clear separation of concerns

**Challenges:**

-   More complex initial setup
-   May be over-engineering for simple cases
-   Need to design policy interfaces carefully

### Opportunity 3: Extract Common Post-Edit Steps

Even without full template method, extract the common notification code:

```java
protected void notifyModelChanged() {
    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}

protected void postEditAndNotify(UndoableEdit edit) {
    if (edit != null) {
        parentComponent.getUndoSupport().postEdit(edit);
    }
    notifyModelChanged();
}
```

**Usage:**

```java
private void editSomething(...) {
    // ... edit logic ...
    UndoableEdit edit = new EditSomethingEdit(...);
    postEditAndNotify(edit);
}
```

**Benefits:**

-   Simple, low-risk refactoring
-   Reduces duplication immediately
-   No architectural changes needed

### Opportunity 4: Specialized Helper Methods

Create helper methods for each pattern in the base class:

```java
// Pattern 1: Simple edit
protected <T extends Copyable<T>> void simpleEdit(
        T domainObject,
        Editor<T> editor,
        BiFunction<T, T, UndoableEdit> editFactory) {

    T editedObject = editor.editDialog(domainObject, parentComponent);
    if (editedObject == null) return;

    T undoObject = domainObject.clone();
    domainObject.copyOf(editedObject);

    UndoableEdit edit = editFactory.apply(domainObject, undoObject);
    postEditAndNotify(edit);
}

// Pattern 2: Name conflict checking
protected <T extends NamedCopyable<T>> void editWithConflictCheck(
        T domainObject,
        Editor<T> editor,
        Function<String, T> repositoryLookup,
        BiConsumer<T, T> repositoryEdit,
        BiFunction<T, T, UndoableEdit> editFactory) {

    T editedObject = editor.editDialog(domainObject, parentComponent);
    if (editedObject == null) return;

    if (!domainObject.getName().equals(editedObject.getName())
            && repositoryLookup.apply(editedObject.getName()) != null
            && !editedObject.getName().equals("")) {
        // Handle conflict...
    } else {
        repositoryEdit.accept(domainObject, editedObject);
        UndoableEdit edit = editFactory.apply(domainObject, editedObject);
        postEditAndNotify(edit);
    }
}

// Pattern 6: Simple value edit
protected <T> void simpleValueEdit(
        T domainObject,
        Function<T, String> getter,
        BiConsumer<T, String> setter,
        TriFunction<T, String, String, UndoableEdit> editFactory,
        String dialogTitle,
        String dialogLabel) {

    String undoValue = getter.apply(domainObject);
    StringEditorDialog dialog = new StringEditorDialog(parentComponent, dialogTitle, dialogLabel, undoValue);

    if (!dialog.showDialog()) return;

    String newValue = dialog.getText();
    if ((undoValue == null && newValue != null) ||
            (undoValue != null && !undoValue.equals(newValue))) {
        setter.accept(domainObject, newValue);
        UndoableEdit edit = editFactory.apply(domainObject, undoValue, newValue);
        postEditAndNotify(edit);
    }
}
```

**Benefits:**

-   Provides helpers for common patterns
-   Subclasses can still override for special cases
-   Gradual migration - can adopt one method at a time

**Drawbacks:**

-   Many helper methods with complex signatures
-   May not cover all edge cases
-   Lambdas can make code harder to read

---

## Recommendations

### Recommended Approach: Polymorphic Edit (Opportunity 1)

**Implement `edit(EditContext)` method on `GraphicalElement` hierarchy.**

**Rationale:**

-   **Eliminates ALL controller edit methods** - reduces code by hundreds of lines
-   **True OO design** - elements know how to edit themselves
-   **Removes editElementMapper complexity** - no registration needed
-   **Easier to test** - test elements independently
-   **Simpler controllers** - just call `element.edit(context)`
-   **Handles all 6 patterns naturally** - each element implements its specific logic
-   **Better encapsulation** - edit behavior lives with the element

**Implementation Steps:**

1. **Create EditContext class** (new file)

    - Encapsulates model, repository, parent component, undo support
    - Provides `notifyModelChanged()` helper
    - Low risk, no existing code affected

2. **Add default edit() to GraphicalElement** (base class)

    - Returns false (element not editable)
    - Zero risk, backward compatible

3. **Migrate elements incrementally** (one at a time)

    - Start with simple Pattern 1 elements (e.g., ActivityNodeGR, DecisionNodeGR)
    - Move to Pattern 2-6 as confidence grows
    - Remove editElementMapper entry after each migration
    - Test thoroughly after each migration

4. **Simplify SelectionController** (after all migrations)

    - Remove `mapeditElement()` method
    - Replace with direct `element.edit(context)` calls
    - Delete editElementMapper entirely

5. **Delete controller edit methods** (after all migrations)
    - Remove all `editXXX()` methods from controller subclasses
    - Massive code reduction

**Impact:**

-   **~28+ edit methods eliminated** from controllers
-   **~800+ lines of code removed**
-   **editElementMapper removed** - simpler architecture
-   **All tests continue to work** - behavior unchanged

### Alternative Approaches (Not Recommended)

#### Phase 1: Low-Hanging Fruit (Immediate - If Not Doing Polymorphic Edit)

If for some reason polymorphic edit is not pursued, at minimum extract:

**Extract common notification code:**

```java
protected void notifyModelChanged() {
    model.modelChanged();
    SystemWideObjectNamePool.getInstance().reload();
}
```

**Impact:**

-   28+ edit methods × 2 lines = 56+ lines eliminated
-   Zero risk
-   Improves consistency

(But still leaves 28+ edit methods in controllers - polymorphic edit approach is much better)

#### Phase 2-4: Template Methods, Policies, etc.

These approaches are **not recommended** because:

-   Still require controller methods (no real simplification)
-   Add complexity without eliminating code
-   Don't leverage OO polymorphism
-   Harder to test and maintain

See original opportunity sections for details if needed.

---

## Design Decisions

### Decision 1: Where Should Edit Logic Live?

**Question:** Should edit logic be in controllers or in GraphicalElement subclasses?

**Current State:**

-   Edit logic in controller subclass methods
-   Registered via editElementMapper
-   Controllers have 28+ edit methods total

**Recommendation:** **Move to GraphicalElement subclasses**

**Rationale:**

-   **Single Responsibility:** Each element knows how to edit itself
-   **Open/Closed Principle:** Add new element types without modifying controllers
-   **Testability:** Can test element editing in isolation
-   **Code Reduction:** Eliminates hundreds of lines of controller code
-   **Consistency:** One edit() method vs. 28+ different methods

**Trade-offs:**

-   Elements gain dependency on EditContext (acceptable - controller deps are isolated)
-   Slightly more complex for simple elements (but more maintainable overall)

### Decision 2: EditContext Design

**Implement basic template method for Pattern 1 (simple edits):**

```java
protected <T extends Copyable<T>> void editSimple(
        T domainObject,
        Editor<T> editor,
        BiFunction<T, T, UndoableEdit> undoEditFactory) {

    T editedObject = editor.editDialog(domainObject, parentComponent);
    if (editedObject == null) {
        return;
    }

    T undoObject = domainObject.clone();
    domainObject.copyOf(editedObject);
    UndoableEdit edit = undoEditFactory.apply(domainObject, undoObject);

    parentComponent.getUndoSupport().postEdit(edit);
    notifyModelChanged();
}
```

**Apply to:**

-   `editAssociationClass()` (DCD and CCD)
-   `editExtend()` (UCD)
-   `editActivityNode()` (AD)
-   `editDecisionNode()` (AD)
-   `editActionNode()` (AD)

**Impact:**

-   Eliminates ~10 lines per method × 6 methods = 60 lines
-   Establishes pattern for future editors
-   Medium risk (need thorough testing)

### Phase 3: Analyze Complex Patterns (Future)

**Document and standardize:**

-   Pattern 2 (name conflicts) - Two different behaviors (DCD vs UCD)
-   Pattern 3 (typed entities) - Already well-structured with CompoundEdit
-   Pattern 4 (validation) - Domain-specific, hard to generalize

**Consider:**

-   Should name conflict behavior be unified? (DCD/CCD vs UCD)
-   Can validation be extracted into policy objects?
-   Is TypedEntity pattern extendable to other cases?

### Phase 4: Consider Policy Pattern (Long-term)

**If pattern complexity grows:**

-   Implement `EditPolicy<T>` interface
-   Create concrete policies for each pattern
-   Allows composition and reuse across controllers

**Only pursue if:**

-   More patterns emerge
-   Testing reveals issues with current approach
-   Team agrees on architectural direction

---

## Affected Components

### SelectionController (Base Class)

-   Add `notifyModelChanged()` helper method
-   Add optional `editSimple()` template method
-   All 7 subclasses inherit these methods

### Subclass Controllers

Each controller has multiple edit methods that could be refactored:

1. **DCDSelectionController** - 5 methods

    - `editClass()` - Pattern 2 (name conflict)
    - `editInterface()` - Pattern 2 (name conflict)
    - `editAssociation()` - Pattern 5 (in-place properties)
    - `editDependency()` - Pattern 6 (simple value)
    - `editAssociationClass()` - **Pattern 1 (candidate for editSimple)**

2. **CCDSelectionController** - 3 methods

    - `editClass()` - Pattern 2 (name conflict)
    - `editAssociation()` - Pattern 5 (in-place properties)
    - `editAssociationClass()` - **Pattern 1 (candidate for editSimple)**

3. **ADSelectionController** - 6 methods

    - `editControlFlow()` - Pattern 4 (validation-heavy)
    - `editObjectFlow()` - Pattern 4 (validation-heavy)
    - `editActionNode()` - **Pattern 1 (candidate for editSimple)**
    - `editObjectNode()` - Pattern 3 (typed entity)
    - `editActivityNode()` - **Pattern 1 (candidate for editSimple)**
    - `editDecisionNode()` - **Pattern 1 (candidate for editSimple)**

4. **UCDSelectionController** - 4 methods

    - `editActor()` - Pattern 2 (name conflict - error variant)
    - `editUseCase()` - Pattern 2 (name conflict - error variant)
    - `editSystem()` - Pattern 2 (name conflict - error variant)
    - `editExtend()` - **Pattern 1 (candidate for editSimple)**

5. **SDSelectionController** - 2 methods

    - `editSDObject()` - Pattern 3 (typed entity)
    - `editMultiObject()` - Pattern 3 (typed entity)

6. **SSDSelectionController** - 1 method

    - `editSystemInstance()` - Pattern 3 (typed entity)

7. **AbstractSDSelectionController** - 3 methods

    - `editActorInstance()` - Pattern 3 (typed entity)
    - `editCallMessage()` - Pattern 5 (in-place properties)
    - `editReturnMessage()` - Pattern 6 (simple value)

8. **SelectionController** - 1 method
    - `editUMLNote()` - Pattern 6 (simple value)

### Test Coverage

All affected controllers have existing test classes:

-   `DCDSelectionControllerTest.java`
-   `CCDSelectionControllerTest.java`
-   `ADSelectionControllerTest.java`
-   `UCDSelectionControllerTest.java`

**Testing Strategy:**

-   Run all existing tests before refactoring
-   Add specific tests for template methods
-   Verify edit operations produce same results
-   Test undo/redo functionality

---

## Design Decisions

### Decision 1: Granularity of Extraction

**Question:** Should we extract a single generic template method, or multiple specialized helpers?

**Option A:** Single generic template method with callbacks

-   Pros: DRY principle, one place to change
-   Cons: Complex signature, may not fit all cases

**Option B:** Multiple specialized helper methods (Pattern 1, Pattern 2, etc.)

-   Pros: Each helper fits its pattern perfectly
-   Cons: More methods to maintain, still some duplication

**Recommendation:** Start with **Option B** (specialized helpers)

-   More pragmatic
-   Easier to adopt gradually
-   Can consolidate later if patterns converge

### Decision 2: EditContext Design

**Question:** What should EditContext contain?

**Recommendation:** Minimal necessary dependencies

```java
public class EditContext {
    private final DiagramModel model;
    private final CentralRepository repository;  // Derived from model
    private final Component parentComponent;
    private final UndoSupport undoSupport;

    // Helper method
    public void notifyModelChanged() {
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }
}
```

**Rationale:**

-   Provides everything needed for edit operations
-   Keeps elements decoupled from controller internals
-   Easy to mock for testing
-   Single place to add cross-cutting concerns

### Decision 3: Name Conflict Handling

**Question:** Should name conflict checking be standardized?

**Current State:**

-   **DCD/CCD:** Replace graphical reference with existing element (silent merge)
-   **UCD:** Show error message and reject edit

**Recommendation:** Keep both behaviors for now

-   DCD/CCD behavior makes sense for design classes (can have multiple diagrams referencing same class)
-   UCD behavior makes sense for use cases (usually one actor/use case per diagram)
-   Document the difference clearly
-   Consider adding option to users in future

### Decision 3: Name Conflict Handling

**Question:** Should name conflict checking be standardized?

**Current State:**

-   **DCD/CCD:** Replace graphical reference with existing element (silent merge)
-   **UCD:** Show error message and reject edit

**Recommendation:** Keep both behaviors for now

-   DCD/CCD behavior makes sense for design classes (can have multiple diagrams referencing same class)
-   UCD behavior makes sense for use cases (usually one actor/use case per diagram)
-   Document the difference in each element's edit() method
-   Consider adding user preference in future

### Decision 4: Migration Strategy

**Question:** Big bang or incremental migration?

**Recommendation:** **Incremental migration**

**Steps:**

1. Add EditContext + default edit() method (zero risk)
2. Migrate simple elements first (Pattern 1 - ActivityNodeGR, DecisionNodeGR, etc.)
3. Test thoroughly after each element
4. Migrate complex elements (Pattern 2-6)
5. Remove editElementMapper and mapeditElement() last

**Benefits:**

-   Lower risk - can revert individual changes
-   Continuous testing and validation
-   Team can learn pattern gradually
-   Production system remains stable throughout

---

## TODO Tasks

### Phase 1: Create Infrastructure (Low Risk)

-   [x] Task 1.1: Create `EditContext` class in `controller` package ✅

    -   **Simplified to 2-parameter version:** (DiagramModel model, DiagramInternalFrame parentComponent)
    -   Derives repository from model: `model.getCentralRepository()`
    -   Derives undoSupport from parentComponent: `parentComponent.getUndoSupport()`
    -   Added comprehensive JavaDoc
    -   Benefits: Cleaner API, type safety, impossible to pass mismatched dependencies

-   [x] Task 1.2: Add default `edit(EditContext)` method to `GraphicalElement` ✅

    -   Default implementation returns false (element not editable)
    -   Added JavaDoc explaining contract
    -   No existing functionality affected
    -   Backward compatible

-   [x] Task 1.3: Run all tests to verify no regressions ✅
    -   All 339 tests passing

### Phase 2: Pilot Migration - Simple Elements (Medium Risk) ✅ COMPLETE

**Pilot with Pattern 1 elements (simplest)**

-   [x] Task 2.1: Implement `edit()` in `ActivityNodeGR` (AD) ✅

    -   Created template method `editNameWithDialog()` in `NodeComponentGR`
    -   Moved logic from ADSelectionController.editActivityNode()
    -   Tested double-click editing works
    -   Tested undo/redo works
    -   Removed editElementMapper.put for ActivityNodeGR
    -   Cleaned up unused method and imports

-   [x] Task 2.2: Implement `edit()` in `DecisionNodeGR` (AD) ✅

    -   Used template method from NodeComponentGR
    -   Tested and verified
    -   Removed mapper entry and cleaned up

-   [x] Task 2.3: Implement `edit()` in `ActionNodeGR` (AD) ✅

    -   Used template method from NodeComponentGR
    -   Tested and verified
    -   Removed mapper entry and cleaned up

-   [x] Task 2.4: Review pilot results ✅
    -   **SUCCESS**: Template method approach validated
    -   Code reduction: 68% per element (from ~35 lines to 11 lines)
    -   All 339 tests passing
    -   Ready to proceed with remaining elements

### Phase 3: Pattern Classification Correction

**Investigation Results:**

After implementing the template method for Pattern 1 elements, we investigated the originally planned Phase 3 tasks and discovered they are NOT Pattern 1:

-   **UCExtendGR** ❌ Not Pattern 1

    -   Uses `UCExtendEditor` with `ExtensionPointsPanel`
    -   Edits a **list of extension points** (complex multi-field edit)
    -   **Reclassified as Pattern 5** (Complex Multi-Field Edit)

-   **UMLNoteGR** ❌ Not Pattern 1

    -   Uses `UMLNoteEditor` with `JTextArea` (multi-line text)
    -   Different UI component than StringEditorDialog
    -   **Reclassified as Pattern 6** (Simple Value Edit with different dialog)

-   **AssociationClassGR** ❌ Not Pattern 1
    -   Uses `DesignAssociationClassEditor` extending `AssociationEditorBase`
    -   Edits: name, direction, roles, **attributes**, and **methods**
    -   **Reclassified as Pattern 5** (Complex Multi-Field Edit)

**Conclusion:** All Pattern 1 (Simple Domain Edit) elements are COMPLETE. Only ActivityNodeGR, DecisionNodeGR, and ActionNodeGR truly fit this pattern (simple name edit with StringEditorDialog, clone/apply, standard undo).

### Phase 4: Migrate Name Conflict Elements (Higher Risk)

**Pattern 2 elements use StringEditorDialog but check for name conflicts**

-   [ ] Task 4.1: Implement `edit()` in `ClassGR` (DCD) - Pattern 2
-   [ ] Task 4.2: Implement `edit()` in `InterfaceGR` (DCD) - Pattern 2
-   [ ] Task 4.3: Implement `edit()` in `ConceptualClassGR` (CCD) - Pattern 2
-   [x] Task 4.4: Implement `edit()` in `UCActorGR` (UCD) - Pattern 2 variant (shows error instead of merging)
-   [x] Task 4.5: Implement `edit()` in `UseCaseGR` (UCD) - Pattern 2 variant (shows error instead of merging)
-   [x] Task 4.6: Implement `edit()` in `SystemGR` (UCD) - Pattern 2 variant (shows error instead of merging)

**Strategy:** Could create variant template method in base class for name conflict checking

### Phase 5: Migrate Validation Elements (Higher Risk)

**Pattern 4 elements have complex domain-specific validation logic**

-   [x] Task 5.1: Implement `edit()` in `ControlFlowGR` (AD) - Pattern 4 (guard uniqueness validation)
-   [x] Task 5.2: Implement `edit()` in `ObjectFlowGR` (AD) - Pattern 4 (guard uniqueness + weight format validation)
-   [x] Task 5.3: Implement `edit()` in `DependencyGR` (DCD) - Pattern 6 (simple stereotype value)

**Strategy:** May not be suitable for template method due to complex validation logic

### Phase 6: Migrate TypedEntity Elements (Highest Risk)

**Pattern 3 elements use CompoundEdit with type operations - complex but well-structured**

-   [ ] Task 6.1: Implement `edit()` in `SDObjectGR` (SD) - Pattern 3
-   [ ] Task 6.2: Implement `edit()` in `MultiObjectGR` (SD) - Pattern 3
-   [ ] Task 6.3: Implement `edit()` in `ActorInstanceGR` (SD/SSD) - Pattern 3
-   [ ] Task 6.4: Implement `edit()` in `SystemInstanceGR` (SSD) - Pattern 3
-   [ ] Task 6.5: Implement `edit()` in `ObjectNodeGR` (AD) - Pattern 3 variant (simpler, validates name or type)

**Strategy:** Pattern 3 is well-structured with TypedEntityEditResult and CompoundEdit, may not need template method

### Phase 7: Migrate Message Elements (Medium Risk)

-   [ ] Task 7.1: Implement `edit()` in `CallMessageGR` (SD) - Pattern 5 (in-place property edit)
-   [ ] Task 7.2: Implement `edit()` in `CreateMessageGR` (SD) - Pattern 5
-   [x] Task 7.3: Implement `edit()` in `ReturnMessageGR` (SD) - Pattern 6 (simple value with StringEditorDialog)

**Note:** ReturnMessageGR uses StringEditorDialog, similar to Pattern 1 but simpler (no domain clone)

### Phase 8: Reclassified Complex Elements (Pattern 5)

**These were incorrectly classified as Pattern 1 - they use complex multi-field editors**

-   [ ] Task 8.1: Implement `edit()` in `UCExtendGR` (UCD) - Pattern 5 (extension points list)
-   [ ] Task 8.2: Implement `edit()` in `AssociationClassGR` (DCD, CCD) - Pattern 5 (name, direction, roles, attributes, methods)
-   [ ] Task 8.3: Implement `edit()` in `AssociationGR` (DCD, CCD) - Pattern 5 (name, direction, roles)
-   [ ] Task 8.4: Implement `edit()` in `UMLNoteGR` (SelectionController) - Pattern 6 (JTextArea, not StringEditorDialog)

**Strategy:** Each uses custom editor, implement edit() directly without template method

### Phase 9: Cleanup and Finalization (Low Risk)

-   [ ] Task 9.1: Verify all elements have edit() implemented or explicitly don't support editing
-   [ ] Task 9.2: Update `mapeditElement()` to call `element.edit(context)` directly
-   [ ] Task 9.3: Remove `editElementMapper` field from all controllers
-   [ ] Task 9.4: Delete all `editXXX()` methods from controller subclasses
-   [ ] Task 9.5: Run full test suite to verify all functionality works
-   [ ] Task 9.6: Update documentation and CHANGELOG

### Phase 10: Testing and Validation (Critical)

-   [ ] Task 10.1: Manual testing of edit operations in all diagram types
-   [ ] Task 10.2: Test undo/redo for all element types
-   [ ] Task 10.3: Test name conflict scenarios (Pattern 2)
-   [ ] Task 10.4: Test validation scenarios (Pattern 4: ControlFlow, ObjectFlow)
-   [ ] Task 10.5: Test TypedEntity scenarios with type operations (Pattern 3)
-   [ ] Task 10.6: Add unit tests for remaining edit() methods on elements
-   [ ] Task 10.7: Verify test coverage is maintained or improved

---

---

## Implementation Summary

**Progress Update (Phase 2 Complete)**

After implementing the polymorphic edit approach with template method:

-   **EditContext created:** Yes (2-parameter version: model, parentComponent)
-   **Template method created:** `NodeComponentGR.editNameWithDialog()` with `UndoableEditFactory` functional interface
-   **Pattern 1 elements migrated:** 3/3 (100% complete)
    -   ActivityNodeGR ✅
    -   DecisionNodeGR ✅
    -   ActionNodeGR ✅
-   **Edit methods eliminated:** 3 controller methods removed from ADSelectionController
-   **Lines of code reduced:** ~68% per element (from ~35 lines to 11 lines each)
-   **Tests passing:** 339/339 (100%)
-   **Known issues:** None
-   **Pattern reclassification:** Discovered UCExtendGR, UMLNoteGR, and AssociationClassGR are NOT Pattern 1

**Next Steps:**

-   Decide on next pattern to tackle (Pattern 2, 4, 5, or 6)
-   Update remaining phase tasks based on correct pattern classifications
-   Continue incremental migration approach

**Additional Migrations Completed (Post-Phase 2):**

-   ControlFlowGR (AD) edit() implemented with guard validation and centralized helper; redo-aware undo fixed via explicit redo snapshot
-   DependencyGR (DCD) edit() implemented (stereotype simple value edit)
-   ReturnMessageGR (SD) edit() implemented (simple value)
-   UCD elements migrated to polymorphic edit(): UCActorGR, UseCaseGR, SystemGR with conflict-checking variant (error dialog on duplicates); corresponding controller mapper entries removed

**Immediate Next Task:**

-   Implement `edit()` in `ObjectFlowGR` (AD) with validation:
    -   Guard uniqueness among outgoing edges (same as ControlFlow)
    -   Weight format validation (integer > 0 or acceptable format per domain rules)
    -   Use centralized helper with duplicate-checking and provide redo snapshot; add/ensure redo-aware undo edit if required
    -   Add or adjust unit tests to cover undo/redo and validation failures

---

## Success Criteria

1. **Code Reduction:** Eliminate 28+ edit methods from controllers (~800+ lines)
2. **Simplification:** Remove editElementMapper registration system
3. **Polymorphism:** All editable elements implement `edit(EditContext)` method
4. **Tests Pass:** All existing controller tests continue to pass
5. **Functionality:** All edit operations work identically to before
6. **Undo/Redo:** All undo/redo operations work correctly
7. **Maintainability:** New elements can implement edit() without modifying controllers
8. **Documentation:** EditContext and edit() contract well documented

---

## Comparison: Current vs. Proposed Architecture

### Current Architecture

**SelectionController hierarchy:**

```
SelectionController (1 edit method)
├── mapeditElement(element) → looks up in editElementMapper
├── editElementMapper (Map<Class, Consumer>)
└── editUMLNote()

DCDSelectionController (5 edit methods)
├── editClass()
├── editInterface()
├── editAssociation()
├── editDependency()
└── editAssociationClass()

CCDSelectionController (3 edit methods)
├── editClass()
├── editAssociation()
└── editAssociationClass()

... 5 more controller subclasses with 19 more edit methods
```

**Total:** 28+ edit methods, ~800+ lines of code

**To edit an element:**

1. Double-click element
2. Controller calls `mapeditElement(element)`
3. Look up element.getClass() in editElementMapper
4. Call registered lambda which calls specific editXXX() method
5. editXXX() method contains all logic

### Proposed Architecture

**GraphicalElement hierarchy:**

```
GraphicalElement
└── edit(EditContext context) - default returns false

ClassGR
└── edit(EditContext) - implements editing logic

InterfaceGR
└── edit(EditContext) - implements editing logic

... each editable element implements edit()
```

**SelectionController:**

```java
// Just 3 lines!
private void mapeditElement(GraphicalElement element) {
    EditContext context = new EditContext(model, parentComponent, parentComponent.getUndoSupport());
    element.edit(context);
}
```

**Total:** 1 method in controller, ~10 lines. Edit logic in each element class.

**To edit an element:**

1. Double-click element
2. Controller calls `element.edit(context)`
3. Element implements its own editing logic
4. Done!

**Benefits:**

-   **Simpler controllers** - 28+ methods → 1 method
-   **No mapper** - no registration needed
-   **Better OO** - elements own their behavior
-   **Easier to test** - test elements independently
-   **Easier to extend** - add new element, implement edit(), done
