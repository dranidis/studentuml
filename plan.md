# Copy and Paste Implementation Plan for StudentUML

**Target Branch**: `develop` (version 1.4.0-SNAPSHOT)

## Overview

Add copy and paste functionality for diagram elements in StudentUML, allowing users to duplicate graphical elements (classes, use cases, messages, etc.) within and across diagrams.

## Current State Analysis (Develop Branch)

### What We Have

-   ✅ Selection system (`SelectionController`) with multi-select support
-   ✅ `clone()` methods in many domain model classes
-   ✅ Keyboard shortcut infrastructure (Ctrl+A for select all, Del for delete)
-   ✅ Undo/Redo system with `UndoableEdit` implementations
-   ✅ Factory patterns for creating elements (`AddElementControllerFactory`)
-   ✅ Model/View/Controller architecture with proper separation
-   ✅ **NEW**: Refactored abstract classes (`AbstractDecisionNodeGR`, `AbstractForkNodeGR`) - reduces duplication
-   ✅ **NEW**: Cleaner codebase (applet code removed in commit b284af7)

### What We Don't Have

-   ❌ Clipboard storage for graphical elements
-   ❌ Copy/Paste keyboard shortcuts (Ctrl+C/V)
-   ❌ Cloning logic for graphical elements (`*GR` classes)
-   ❌ Paste positioning logic (offset to avoid overlap)
-   ❌ Undo/Redo edits for paste operations
-   ❌ Handling of relationships during copy/paste

## Technical Challenges

### 1. **Two-Layer Architecture**

-   **Domain Layer**: Classes, interfaces, messages stored in `CentralRepository`
-   **Graphical Layer**: `*GR` wrapper classes with visual positioning
-   **Challenge**: Must understand the relationship between layers

### ⚠️ CRITICAL DESIGN DECISION: Domain Object Handling

**When copying/pasting graphical elements:**

❌ **DO NOT** clone domain objects
✅ **DO** create new graphical wrappers that reference the SAME domain object

**Example**:

```
Original:  ClassGR1 ──references──> DesignClass("Customer")
                                            ↑
Pasted:    ClassGR2 ──references───────────┘
                      (SAME object)
```

**Why this approach?**

1. **Consistency**: Editing class name/attributes updates ALL graphical views
2. **Single Source of Truth**: Domain object exists once in `CentralRepository`
3. **Multi-view support**: Same class can appear in CCD, DCD, multiple diagrams
4. **Simpler implementation**: No need to manage duplicate domain objects
5. **Less error-prone**: Avoids synchronization issues

**What gets copied?**

-   ✅ Position (x, y coordinates)
-   ✅ Size (width, height)
-   ✅ Visual state (but not "selected")
-   ❌ Domain object content (use reference instead)

**Use case**: User copies a class to show it in a different part of the diagram or in multiple diagrams. All representations show the same class data.

### 2. **Relationship Handling**

-   **Simple Copy**: Copy only selected elements (break relationships)
-   **Deep Copy**: Copy selected elements + their relationships
-   **Decision**: Start with simple copy, add deep copy later

### 3. **Cross-Diagram Paste**

-   Elements may reference objects in `CentralRepository`
-   Pasting to different diagram requires creating new domain objects
-   May need diagram-type validation

### 4. **Composite Elements**

-   Activity diagrams have `CompositeNodeGR` with nested components
-   Use cases have `CompositeUCDElementGR` with child elements
-   Must handle parent-child relationships during copy

## Implementation Phases

---

## Phase 1: Clipboard Infrastructure

### 1.1 Create Clipboard Manager

**File**: `src/main/java/edu/city/studentuml/util/ClipboardManager.java`

```java
public class ClipboardManager {
    private static ClipboardManager instance;
    private List<GraphicalElement> clipboardElements;
    private DiagramModel sourceDiagram;

    public static ClipboardManager getInstance();
    public void copy(List<GraphicalElement> elements, DiagramModel source);
    public List<GraphicalElement> paste();
    public boolean hasContent();
    public void clear();
}
```

**Responsibilities**:

-   Singleton pattern for global clipboard access
-   Store copied elements temporarily
-   Track source diagram for context

### 1.2 Add Cloneable Interface to GraphicalElement

**File**: `src/main/java/edu/city/studentuml/model/graphical/GraphicalElement.java`

Add method signature:

```java
public GraphicalElement clone();
```

Implement in all `*GR` classes (80+ files):

-   `ClassGR`, `InterfaceGR`, `ConceptualClassGR`
-   `UseCaseGR`, `UCActorGR`, `SystemGR`
-   `SDObjectGR`, `CallMessageGR`, `ReturnMessageGR`
-   `ActionNodeGR`, `DecisionNodeGR`, `ControlFlowGR`
-   etc.

**Cloning Strategy** (CRITICAL ARCHITECTURAL DECISION):

1. **DO NOT clone domain objects** - keep reference to the same domain object
2. Create new `*GR` wrapper referencing the SAME domain object
3. Copy visual properties (position, size, selected state)
4. **Don't copy**: relationships to other elements (initially)

**Rationale**:

-   Multiple graphical representations can reference the same domain object
-   Example: Same class can appear in multiple diagrams (CCD and DCD)
-   Domain objects live in `CentralRepository` (shared across diagrams)
-   Graphical elements (`*GR`) are diagram-specific positioning/rendering
-   This maintains consistency: editing domain properties affects all views

**Exception**:

-   For elements without domain objects (e.g., `UMLNoteGR`), clone the graphical state only

---

## Phase 2: Copy Functionality

### 2.1 Add Copy Action to SelectionController

**File**: `src/main/java/edu/city/studentuml/controller/SelectionController.java`

**Changes**:

```java
// Add to registerKeyboardActions()
KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    .put(copy, "ctrl-c");
parentComponent.getActionMap().put("ctrl-c", copyActionListener);

// Add action listener
private final Action copyActionListener = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        copySelected();
    }
};

// Add copy method
public void copySelected() {
    if (selectedElements.isEmpty()) {
        return;
    }
    ClipboardManager.getInstance().copy(selectedElements, model);
}
```

### 2.2 Add Menu Items

**File**: `src/main/java/edu/city/studentuml/view/gui/DiagramInternalFrame.java`

Add to popup menu:

-   Copy (Ctrl+C)
-   Cut (Ctrl+X) - copy then delete
-   Paste (Ctrl+V)

---

## Phase 3: Paste Functionality

### 3.1 Add Paste Action to SelectionController (REVISED)

**File**: `src/main/java/edu/city/studentuml/controller/SelectionController.java`

```java
// Register keyboard action
KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
parentComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    .put(paste, "ctrl-v");
parentComponent.getActionMap().put("ctrl-v", pasteActionListener);

// Action listener
private final Action pasteActionListener = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        pasteClipboard();
    }
};

// Paste method
public void pasteClipboard() {
    if (!ClipboardManager.getInstance().hasContent()) {
        return;
    }

    List<GraphicalElement> clonedElements = new ArrayList<>();
    List<GraphicalElement> originalElements = ClipboardManager.getInstance().getClipboardElements();

    for (GraphicalElement original : originalElements) {
        GraphicalElement cloned = original.clone();

        // Offset position to avoid exact overlap
        offsetElement(cloned, PASTE_OFFSET_X, PASTE_OFFSET_Y);

        // NO NEED to add to repository - domain object is already there!
        // The cloned GR references the same domain object

        // Add to diagram (graphical layer only)
        model.addGraphicalElement(cloned);

        clonedElements.add(cloned);
    }

    // Create undo/redo edit
    UndoableEdit edit = new PasteEdit(model, clonedElements);
    parentComponent.getUndoSupport().postEdit(edit);

    // Select pasted elements
    selectPastedElements(clonedElements);
}

private void offsetElement(GraphicalElement element, int dx, int dy) {
    // Move element by offset
    element.move(dx, dy);
}
```

**Constants**:

```java
private static final int PASTE_OFFSET_X = 20;
private static final int PASTE_OFFSET_Y = 20;
```

### 3.2 Repository Integration (REVISED - SIMPLIFIED!)

**NO repository manipulation needed!**

The pasted graphical element references the SAME domain object that's already in `CentralRepository`.

**Benefits**:

-   ✅ Simpler implementation
-   ✅ No duplicate domain objects
-   ✅ Consistency across all views
-   ✅ Less error-prone

**Exception Handling**:

-   For elements that are diagram-specific positioning only (notes attached to elements), handle appropriately

### 3.3 Create PasteEdit for Undo/Redo

**File**: `src/main/java/edu/city/studentuml/util/undoredo/PasteEdit.java`

```java
public class PasteEdit extends AbstractUndoableEdit {
    private DiagramModel model;
    private List<GraphicalElement> pastedElements;

    public PasteEdit(DiagramModel model, List<GraphicalElement> elements) {
        this.model = model;
        this.pastedElements = elements;
    }

    @Override
    public void undo() throws CannotUndoException {
        // Remove pasted elements from diagram
        for (GraphicalElement element : pastedElements) {
            model.removeGraphicalElement(element);
            // Remove from repository
        }
        model.modelChanged();
    }

    @Override
    public void redo() throws CannotRedoException {
        // Re-add pasted elements
        for (GraphicalElement element : pastedElements) {
            model.addGraphicalElement(element);
            // Re-add to repository
        }
        model.modelChanged();
    }
}
```

---

## Phase 4: Element-Specific Cloning

### 4.1 Priority Order for Implementation

Implement `clone()` methods in this order:

**High Priority** (most commonly used):

1. `ClassGR` / `ConceptualClassGR` (class diagrams)
2. `InterfaceGR` (design class diagrams)
3. `UseCaseGR` / `UCActorGR` (use case diagrams)
4. `UMLNoteGR` (notes - used everywhere)
5. `SDObjectGR` / `CallMessageGR` (sequence diagrams)

**Medium Priority**: 6. `ActionNodeGR` / `ActivityNodeGR` (activity diagrams) 7. `ControlFlowGR` / `ObjectFlowGR` (activity diagrams) 8. Association/Aggregation/Generalization GRs (relationships)

**Low Priority**: 9. Composite elements 10. Edge elements with multiple points

### 4.2 Example Implementation: ClassGR (REVISED)

**File**: `src/main/java/edu/city/studentuml/model/graphical/ClassGR.java`

```java
@Override
public ClassGR clone() {
    // DO NOT clone domain object - reuse the same one!
    DesignClass sameDesignClass = this.getDesignClass();

    // Create new graphical wrapper with SAME domain object
    ClassGR clonedGR = new ClassGR(sameDesignClass, this.getX(), this.getY());

    // Copy visual properties only
    clonedGR.setWidth(this.getWidth());
    clonedGR.setHeight(this.getHeight());

    // Don't copy selected state or relationships

    return clonedGR;
}
```

**Key Point**: The new `ClassGR` references the SAME `DesignClass` object. This means:

-   Both graphical elements will show the same class name, attributes, methods
-   Editing the class in one view updates it everywhere
-   The class appears only once in `CentralRepository`

### 4.3 Example Implementation: UMLNoteGR

**File**: `src/main/java/edu/city/studentuml/model/graphical/UMLNoteGR.java`

```java
@Override
public UMLNoteGR clone() {
    // Notes don't have domain objects, just text
    UMLNoteGR clonedNote = new UMLNoteGR(this.getText(), null, this.getX(), this.getY());

    // Copy dimensions
    clonedNote.setWidth(this.getWidth());
    clonedNote.setHeight(this.getHeight());

    // Don't copy the "to" reference (note attachment)

    return clonedNote;
}
```

### 4.4 Example Implementation: AbstractDecisionNodeGR (NEW in Develop - REVISED)

**File**: `src/main/java/edu/city/studentuml/model/graphical/AbstractDecisionNodeGR.java`

```java
@Override
public AbstractDecisionNodeGR clone() {
    // DO NOT clone domain object - reuse the same one!
    ControlNode sameControlNode = this.getControlNode();

    // Create new GR using factory/constructor pattern
    // Subclass constructor will be called polymorphically
    AbstractDecisionNodeGR clonedGR = createInstance(sameControlNode, this.getX(), this.getY());

    // Copy visual properties only
    clonedGR.setWidth(this.getWidth());
    clonedGR.setHeight(this.getHeight());

    return clonedGR;
}

// Abstract factory method for subclasses
protected abstract AbstractDecisionNodeGR createInstance(ControlNode node, int x, int y);
```

**Benefit**: Both `DecisionNodeGR` and `MergeNodeGR` inherit this logic, only need to implement `createInstance()`.

**Key Point**: The same `ControlNode` is shared between graphical representations.

---

## Phase 5: Relationship Handling (Future Enhancement)

### 5.1 Relationship Detection

When copying multiple elements, detect relationships:

-   Associations between classes
-   Generalizations (inheritance)
-   Messages between objects
-   Control/Object flows between nodes

### 5.2 Smart Copy Strategies

**Strategy 1: Include Internal Relationships**
If copying classes A and B with an association between them:

-   Copy A, B, and the association
-   Maintain relationship in pasted elements

**Strategy 2: Exclude External Relationships**
If copying class A that has association to class C (not selected):

-   Copy only A
-   Don't copy the association (broken reference)

**Strategy 3: User Choice**
Add dialog: "Copy with relationships?" (checkbox)

### 5.3 Implementation Notes

This is complex and can be deferred. Initial version will:

-   Copy elements only
-   Not copy relationships
-   User can manually recreate relationships if needed

---

## Phase 6: Testing Strategy

### 6.1 Unit Tests

**File**: `src/test/java/edu/city/studentuml/controller/CopyPasteTest.java`

Test cases:

```java
@Test
public void testCopySingleClass()
@Test
public void testCopyMultipleClasses()
@Test
public void testPasteWithOffset()
@Test
public void testUndoPaste()
@Test
public void testRedoPaste()
@Test
public void testCopyPasteAcrossDiagrams()
@Test
public void testClipboardClear()
@Test
public void testPasteEmptyClipboard()
```

### 6.2 Manual Testing Checklist

-   [ ] Copy single element (each diagram type)
-   [ ] Copy multiple elements
-   [ ] Paste in same diagram
-   [ ] Paste in different diagram (same type)
-   [ ] Paste in different diagram (different type - should fail gracefully)
-   [ ] Undo paste
-   [ ] Redo paste
-   [ ] Copy, close diagram, paste in another diagram
-   [ ] Multiple paste operations (should offset each time)
-   [ ] Cut operation (copy + delete)

### 6.3 Edge Cases

-   Empty selection → copy does nothing
-   No clipboard content → paste does nothing
-   Paste near diagram boundary → handle positioning
-   Paste very large number of elements → performance
-   Copy from one project, paste in another project → handle repository differences

---

## Phase 7: User Interface Updates

### 7.1 Context Menu

**File**: `src/main/java/edu/city/studentuml/view/gui/DiagramInternalFrame.java`

Add to right-click popup menu:

```
─────────────────
Copy     Ctrl+C
Cut      Ctrl+X
Paste    Ctrl+V
─────────────────
```

### 7.2 Edit Menu

**File**: `src/main/java/edu/city/studentuml/view/gui/menu/MenuBar.java`

Add to main Edit menu (if it exists, or create it):

```
Edit
├─ Undo           Ctrl+Z
├─ Redo           Ctrl+Y
├─ ─────────────
├─ Cut            Ctrl+X
├─ Copy           Ctrl+C
├─ Paste          Ctrl+V
├─ ─────────────
├─ Select All     Ctrl+A
└─ Delete         Del
```

### 7.3 Visual Feedback

-   Show "Copied N elements" in status bar (optional)
-   Change cursor during paste operation (optional)
-   Highlight pasted elements after paste

---

## Phase 8: Documentation

### 8.1 Update Copilot Instructions

**File**: `.github/copilot-instructions.md`

Add section:

```markdown
### Copy and Paste System

-   `ClipboardManager` (Singleton): Global clipboard for diagram elements
-   Copy: Clones selected elements and stores in clipboard
-   Paste: Creates new instances with offset positioning
-   Keyboard shortcuts: Ctrl+C (copy), Ctrl+V (paste), Ctrl+X (cut)
-   Undo/Redo: `PasteEdit` for paste operations
-   Limitations: Does not copy relationships between elements (initial version)
```

### 8.2 Update User Documentation

Add to help/tutorial:

-   How to copy and paste elements
-   Keyboard shortcuts
-   Limitations (no relationship copying)

---

## Implementation Timeline

### Week 1: Foundation

-   [ ] Create `ClipboardManager`
-   [ ] Add `clone()` method to `GraphicalElement` interface
-   [ ] Implement keyboard shortcuts in `SelectionController`
-   [ ] Create `PasteEdit` undo/redo class

### Week 2: Basic Elements

-   [ ] Implement `clone()` for `UMLNoteGR`
-   [ ] Implement `clone()` for `ClassGR`
-   [ ] Implement `clone()` for `ConceptualClassGR`
-   [ ] Implement `clone()` for `InterfaceGR`
-   [ ] Test basic copy/paste for class diagrams

### Week 3: More Elements

-   [ ] Implement `clone()` for UCD elements (`UseCaseGR`, `UCActorGR`, `SystemGR`)
-   [ ] Implement `clone()` for SD elements (`SDObjectGR`, `CallMessageGR`)
-   [ ] Implement repository integration for each diagram type
-   [ ] Test across all diagram types

### Week 4: Polish & Testing

-   [ ] Add context menu items
-   [ ] Add main menu items
-   [ ] Write unit tests
-   [ ] Manual testing and bug fixes
-   [ ] Update documentation

---

## Known Limitations (Initial Version)

1. **No Relationship Copying**: Associations, generalizations, messages between elements will not be copied
2. **No Cross-Type Paste**: Cannot paste UCD elements into DCD
3. **No Composite Handling**: Complex nested structures may not copy correctly
4. **No Name Uniqueness**: Pasted elements may have duplicate names (user must rename)
5. **Fixed Offset**: All pastes use same offset (20, 20) - no smart positioning

---

## Future Enhancements

### Version 2.0

-   [ ] Copy relationships along with elements
-   [ ] Smart paste positioning (avoid overlaps)
-   [ ] Duplicate detection and auto-rename
-   [ ] Copy format only (without content)
-   [ ] Paste special options dialog

### Version 3.0

-   [ ] Cross-diagram type paste with conversion
-   [ ] Clipboard history (multiple clipboard slots)
-   [ ] System clipboard integration (export to image/text)
-   [ ] Drag-and-drop between diagrams

---

## Risk Assessment

| Risk                              | Impact | Mitigation                                                          |
| --------------------------------- | ------ | ------------------------------------------------------------------- |
| Breaking existing undo/redo       | High   | Comprehensive testing, ensure `PasteEdit` follows existing patterns |
| Memory leaks from clipboard       | Medium | Clear clipboard on project close, implement size limits             |
| CentralRepository inconsistency   | High   | Careful handling of domain object addition/removal                  |
| Clone implementation errors       | Medium | Start with simple elements, extensive testing per element type      |
| Performance with large selections | Low    | Initially limit to reasonable selection sizes (e.g., 100 elements)  |

---

## Success Criteria

-   ✅ User can copy and paste elements within same diagram
-   ✅ User can copy from one diagram and paste in another (same type)
-   ✅ Pasted elements are positioned with visible offset
-   ✅ Undo/redo works correctly for paste operations
-   ✅ All diagram types support copy/paste
-   ✅ No crashes or repository corruption
-   ✅ Keyboard shortcuts work as expected
-   ✅ Menu items are accessible and functional

---

## Develop Branch Advantages

### Refactoring Improvements Since Master

1. **Abstract Classes Introduced** (reduces code duplication):

    - `AbstractDecisionNodeGR` - base for DecisionNodeGR and MergeNodeGR
    - `AbstractForkNodeGR` - base for ForkNodeGR and JoinNodeGR
    - Impact: Implement `clone()` in abstract class, benefits multiple subclasses

2. **Cleaner Codebase**:

    - Applet code removed (commit b284af7) - ~3000+ lines of legacy code gone
    - No backwards compatibility concerns with old applet architecture
    - Easier to understand and modify

3. **Duplicate Code Eliminated**:

    - Multiple refactoring commits (e2054e5, 5d350ff, eb052c0, etc.)
    - Actor rendering unified (commit bc4731c)
    - Consistency across similar components

4. **Improved Test Infrastructure**:
    - Tests updated and expanded
    - Better patterns for testing without GUI
    - Can follow same patterns for copy/paste tests

### Implementation Benefits

✅ **Fewer classes to modify** (~75 vs ~80 GR classes)
✅ **Abstractions reduce repetitive code** in clone implementations
✅ **Cleaner structure** makes debugging easier
✅ **Better foundation** for future enhancements

### Version Information

-   **Master branch**: 1.3.1 (stable release)
-   **Develop branch**: 1.4.0-SNAPSHOT (active development)
-   **Recommendation**: Implement copy/paste in develop for inclusion in v1.4.0

---

## References

### Existing Code to Study

-   `DeleteEdit` and `CompositeDeleteEdit` for undo/redo patterns
-   `AddElementController` for element creation patterns
-   Domain object `clone()` methods (already implemented)
-   `SelectionController.selectAll()` for multi-element handling

### Files to Modify

-   Core: `SelectionController.java` (~150 lines)
-   New: `ClipboardManager.java` (~100 lines)
-   New: `PasteEdit.java` (~80 lines)
-   Per element: `*GR.java` files (~30 lines each × ~75 files) **[Reduced due to abstractions]**
-   Abstract classes: `AbstractDecisionNodeGR`, `AbstractForkNodeGR` (~40 lines each)
-   UI: `DiagramInternalFrame.java` (~50 lines)
-   UI: `MenuBar.java` (~30 lines)

### Estimated Total Changes

-   New files: ~280 lines
-   Modified files: ~2300 lines (~75 GR classes × 30 lines + UI + abstractions)
-   Abstract class modifications: ~80 lines (implement shared `clone()` logic)
-   Test files: ~300 lines
-   **Total**: ~2960 lines of code

**Note**: Estimate reduced from original ~3000 lines due to abstract class optimizations in develop branch.

-   New files: ~280 lines
-   Modified files: ~2300 lines (~75 GR classes × 30 lines + UI) **[Reduced from master estimate]**
-   Test files: ~300 lines
-   **Total**: ~2880 lines of code

### Develop Branch Advantages

1. **Fewer total files** to modify due to abstractions
2. **Cleaner code structure** makes changes easier to implement
3. **Better test infrastructure** to validate copy/paste functionality
4. **No legacy applet code** to worry about compatibility
