# Phase 2a Analysis: Refactor Pattern 3 Editors to Use Domain Objects

## Problem Statement

Pattern 3 editors currently accept **graphical wrappers** instead of **domain objects**:

-   `UCExtendEditor(Component, String, UCExtendGR, CentralRepository)`
-   `CallMessageEditor(Component, String, CallMessageGR, CentralRepository)`
-   `UMLNoteEditor(Component, String, UMLNoteGR)`

However, graphical wrappers are just rendering containers. The actual data lives in domain objects:

-   `UCExtendGR` contains `UCExtend` (domain)
-   `CallMessageGR` contains `CallMessage` (domain)
-   `UMLNoteGR` contains `String text` directly

## Architecture Analysis

### UCExtendEditor

**Current Flow**:

```
Controller → UCExtendGR → UCExtendEditor.initialize() → extensionPointsPanel.setElements(UCExtend.getExtensionPoints())
```

**Domain Object**: `UCExtend extends UCLink`

-   Located: `src/main/java/edu/city/studentuml/model/domain/UCExtend.java`
-   Contains: `List<ExtensionPoint> extensionPoints`
-   Methods: `getExtensionPoints()`, `addExtensionPoint()`, `removeExtensionPoint()`, etc.

**Graphical Wrapper**: `UCExtendGR extends UCLinkGR`

-   Located: `src/main/java/edu/city/studentuml/model/graphical/UCExtendGR.java`
-   Delegates to: `link.getExtensionPoints()` where `link` is `UCExtend`

**Refactoring**:

-   Change constructor: `UCExtendEditor(Component parent, String title, UCExtend ucExtend, CentralRepository repository)`
-   Initialize from domain object directly: `extensionPointsPanel.setElements(ucExtend.getExtensionPoints())`
-   Controllers pass: `ucExtendGR.getLink()` instead of `ucExtendGR`

### CallMessageEditor

**Current Flow**:

```
Controller → CallMessageGR → CallMessageEditor.initialize() → callMessageGR.getCallMessage() → set fields
```

**Domain Object**: `CallMessage extends SDMessage`

-   Located: `src/main/java/edu/city/studentuml/model/domain/CallMessage.java`
-   Contains: name, parameters, return value, return type, iterative flag
-   Methods: `getName()`, `getParameters()`, `getReturnValue()`, `getReturnType()`, `isIterative()`

**Graphical Wrapper**: `CallMessageGR extends SDMessageGR`

-   Located: `src/main/java/edu/city/studentuml/model/graphical/CallMessageGR.java`
-   Contains: `CallMessage message` (inherited from SDMessageGR)
-   Provides: `getCallMessage()` which casts `getMessage()` to `CallMessage`

**Refactoring**:

-   Change constructor: `CallMessageEditor(Component parent, String title, CallMessage callMessage, CentralRepository repository)`
-   Initialize from domain object: `nameField.setText(callMessage.getName())`
-   Controllers pass: `callMessageGR.getCallMessage()` instead of `callMessageGR`

### UMLNoteEditor

**Current Flow**:

```
Controller → UMLNoteGR → UMLNoteEditor.initialize() → note.getText() → textArea.setText()
```

**Domain Object**: No separate domain class

-   `UMLNoteGR` contains `String text` directly
-   No domain layer separation for notes

**Options**:

1. **Create UMLNote domain class** - Consistent with architecture but may be overkill for a simple string
2. **Accept String as domain object** - Simpler but less consistent
3. **Keep UMLNoteEditor as-is** - Notes are different from other elements

**Recommendation**: Option 1 (create UMLNote) OR skip UMLNoteEditor for now and focus on UCExtendEditor and CallMessageEditor first.

## Controller Update Analysis

### UCExtendEditor Usage

**Files to update**: `UCDSelectionController.java`

**Current pattern**:

```java
UCExtendEditor editor = new UCExtendEditor(parent, "Use Case Extend Editor", link, repository);
if (!editor.showDialog()) return;
for (ExtensionPoint ep : editor.getExtensionPoints()) {
    link.addExtensionPoint(ep);
}
```

**New pattern**:

```java
UCExtend ucExtend = (UCExtend) link.getLink();
UCExtendEditor editor = new UCExtendEditor(parent, "Use Case Extend Editor", ucExtend, repository);
if (!editor.showDialog()) return;
for (ExtensionPoint ep : editor.getExtensionPoints()) {
    ucExtend.addExtensionPoint(ep);
}
```

### CallMessageEditor Usage

**Files to update**: `AbstractSDSelectionController.java` and subclasses

**Current pattern**:

```java
CallMessageEditor editor = new CallMessageEditor(parent, "Edit Call Message", callMessageGR, repository);
if (!editor.showDialog()) return;
CallMessage message = callMessageGR.getCallMessage();
message.setName(editor.getMessageName());
// ... update other fields
```

**New pattern**:

```java
CallMessage message = callMessageGR.getCallMessage();
CallMessageEditor editor = new CallMessageEditor(parent, "Edit Call Message", message, repository);
if (!editor.showDialog()) return;
// Editor updates message directly (or returns updated message)
```

## Implementation Strategy

### Phase 2a.1: UCExtendEditor (Safest Start)

1. **Preparation**:

    - Find all call sites: `grep -r "new UCExtendEditor" src/`
    - Document current behavior in test

2. **Refactor editor**:

    - Change constructor signature
    - Update field: `private UCExtend ucExtend;` (replace `UCExtendGR ucExtendGR`)
    - Update `initialize()` method to use `ucExtend` directly
    - Keep `showDialog()` return type as `boolean`
    - Keep `getExtensionPoints()` method

3. **Update controllers**:

    - Extract domain object: `UCExtend ucExtend = (UCExtend) link.getLink();`
    - Pass to editor constructor
    - Update usage to work with domain object

4. **Test**:
    - Run existing tests
    - Manual test: Edit use case extend in StudentUML UI

### Phase 2a.2: CallMessageEditor

1. **Preparation**:

    - Find all call sites: `grep -r "new CallMessageEditor" src/`
    - Document current behavior

2. **Refactor editor**:

    - Change constructor signature
    - Update field: `private CallMessage callMessage;`
    - Update `initialize()` method
    - Consider: Should editor update domain object in-place or return values?

3. **Update controllers**:

    - Extract: `CallMessage message = callMessageGR.getCallMessage();`
    - Pass to constructor
    - Handle result

4. **Test**:
    - Run tests
    - Manual test in UI

### Phase 2a.3: UMLNoteEditor (Optional)

Decision needed: Create domain object or skip for now?

## Benefits of This Refactoring

1. **Architectural Purity**: Editors work with domain objects, not graphical representations
2. **Testability**: Can test editors without creating graphical wrappers
3. **Reusability**: Editors can be reused in contexts without graphics (e.g., batch operations)
4. **Clarity**: Separation of concerns - editing logic separate from rendering logic
5. **Enables Phase 2b**: Domain objects can implement `Copyable<T>` for `Editor<T>` interface

## Risks and Mitigation

**Risk**: Breaking existing controller logic

-   **Mitigation**: Thorough grep search for all usages, update incrementally

**Risk**: Domain object vs graphical wrapper confusion

-   **Mitigation**: Clear naming in controllers, extract domain object to well-named variable

**Risk**: Undo/redo system may rely on graphical wrapper references

-   **Mitigation**: Review undo/redo edits for these editors before changing

## Next Steps

1. Start with UCExtendEditor (simpler, fewer call sites)
2. Create test case for domain object editing
3. Refactor editor class
4. Update controllers
5. Run full test suite
6. Commit with clear message
7. Repeat for CallMessageEditor
8. Decide on UMLNoteEditor approach
