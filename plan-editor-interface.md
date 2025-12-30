# Plan: Standardize Editor Interface

## Goal

Unify all editor dialogs to use a consistent `Editor<T>` interface pattern for better maintainability, testability, and code clarity.

## Investigation

Current state analysis completed in `editor-interface-analysis.md`. Key findings:

-   3 different editor patterns in use across 17+ editors
-   Pattern 2 (ElementEditor) is the cleanest and already implemented by 3 editors
-   ~26 controller call sites need updating
-   All editors can be migrated incrementally without breaking functionality

## Affected Components

### Phase 1: Rename Interface (Low Risk)

-   `ElementEditor<T>` → `Editor<T>` interface
-   Files: `src/main/java/edu/city/studentuml/view/gui/components/ElementEditor.java`
-   Already compatible: `AttributeEditor`, `MethodEditor`, `MethodParameterEditor`
-   Test files: `AttributeEditorTest`, `MethodEditorTest`, `MethodParameterEditorTest`

### Phase 2: Pattern 3 Editors (Medium Risk - 5 editors)

-   `StringEditorDialog`
-   `UCExtendEditor`
-   `CallMessageEditor`
-   `ObjectFlowEditor`
-   `UMLNoteEditor`

Changes needed:

-   Move parent/title from constructor to `editDialog()` parameters
-   Implement `Editor<T>` interface
-   Return domain object instead of boolean
-   Update controllers: UCDSelectionController, ADSelectionController, AbstractSDSelectionController

### Phase 3: Pattern 1 Editors - TypedEntityEditor (High Risk - 5 editors)

-   `ObjectEditor`
-   `MultiObjectEditor`
-   `ActorInstanceEditor`
-   `SystemInstanceEditor`
-   `ObjectNodeEditor`

Changes needed:

-   Implement `Editor<T>` interface
-   Add `editDialog()` method that internally calls `showDialog()`
-   Eventually deprecate `showDialog()` in favor of `editDialog()`
-   Update controllers: SDSelectionController, SSDSelectionController, ADSelectionController, AbstractSDSelectionController

### Phase 4: Pattern 1 Editors - ClassifierEditor (High Risk - 4 editors)

-   `ClassEditor`
-   `InterfaceEditor`
-   `ConceptualClassEditor`
-   `ActorEditor`

Changes needed:

-   Implement `Editor<T>` interface
-   Add `editDialog()` method
-   Update controllers: DCDSelectionController, CCDSelectionController, UCDSelectionController

### Phase 5: Special Cases

-   `AssociationEditor` and `AssociationEditorBase`
-   Evaluate and migrate

## Design Decisions

1. **Interface Choice**: Use Pattern 2 (ElementEditor) as the standard

    - Cleanest API: single method returns result
    - Null convention clearly indicates cancellation
    - Type-safe with generics
    - Already proven in 3 existing editors

2. **Migration Strategy**: Incremental, low-risk first

    - Phase 1: Cosmetic rename (no behavior change)
    - Phase 2-3: Isolated editors (one at a time)
    - Phase 4-5: Complex hierarchies (careful planning)

3. **Backward Compatibility**:

    - Keep old methods initially (deprecate them)
    - Allow gradual migration of controllers
    - Remove deprecated methods in future release

4. **Testing Strategy**:
    - All existing tests must pass after each phase
    - Update test mocks to use `editDialog()` pattern
    - Add tests for new `editDialog()` methods

## TODO Tasks

### Phase 1: Rename Interface ✅

-   [x] Rename `ElementEditor<T>` to `Editor<T>`
-   [x] Update all references (AttributeEditor, MethodEditor, MethodParameterEditor)
-   [x] Update test files
-   [x] Run full test suite to verify no breaks
-   [x] Commit: "Rename ElementEditor to Editor interface"

**Completed**: Phase 1 successful! Renamed interface and updated 10 files:

-   Editor.java (renamed from ElementEditor.java)
-   AttributeEditor.java
-   MethodEditor.java
-   MethodParameterEditor.java
-   ObjectNodeEditor.java
-   ListPanel.java
-   AttributesPanel.java
-   MethodsPanel.java
-   MethodParameterPanel.java
-   ExtensionPointsPanel.java

All 338 tests passing.

### Phase 2: Pattern 3 Editors - Refactor to Use Domain Objects

**IMPORTANT**: Pattern 3 editors currently take graphical elements (UCExtendGR, CallMessageGR, UMLNoteGR) but should be refactored to take domain objects instead. This is necessary before migrating to the `Editor<T>` interface.

**Refactoring Strategy**: Each graphical wrapper contains a domain object:

-   `UCExtendGR` wraps `UCExtend` (domain object with extension points)
-   `CallMessageGR` wraps `CallMessage` (domain object with message details)
-   `UMLNoteGR` contains `String text` directly (no separate domain object)

**Phase 2a: Refactor Editors to Accept Domain Objects**

**Scope Analysis**:

-   **UCExtendEditor**: 1 controller file (UCDSelectionController), 1 test file (6 test call sites)
-   **CallMessageEditor**: 2 controller files (AbstractSDSelectionController, SDSelectionController), 1 test file (8 test call sites)
-   **UMLNoteEditor**: 1 controller file (SelectionController), 1 test file (7 test call sites) - SKIP (no domain object)

-   [x] Task 2a.1: Refactor UCExtendEditor ✅
    -   [x] Change constructor: `UCExtendEditor(Component, String, UCExtend, CentralRepository)`
    -   [x] Update field from `UCExtendGR` to `UCExtend`
    -   [x] Update `initialize()` to work with `ucExtend` directly
    -   [x] Update 1 controller: `UCDSelectionController.java`
    -   [x] Update test: `UCExtendEditorTest.java` (6 test methods passing)
    -   [x] Run tests: `mvn test -Dtest=UCExtendEditorTest`
    -   [x] Commit: 220f2fe "Phase 2a: Refactor Pattern 3 editors to use domain objects"
-   [x] Task 2a.2: Refactor CallMessageEditor ✅
    -   [x] Change constructor: `CallMessageEditor(Component, String, CallMessage, CentralRepository)`
    -   [x] Update field from `CallMessageGR` to `CallMessage`
    -   [x] Update `initialize()` to work with `callMessage` directly
    -   [x] Changed instanceof check from `CreateMessageGR` to `CreateMessage` (domain level)
    -   [x] Update 2 controllers: `AbstractSDSelectionController.java`, `SDSelectionController.java`
    -   [x] Update test: `CallMessageEditorTest.java` (6 test methods passing)
    -   [x] Run tests: `mvn test -Dtest=CallMessageEditorTest`
    -   [x] Commit: 220f2fe "Phase 2a: Refactor Pattern 3 editors to use domain objects"
-   [x] Task 2a.3: Skip UMLNoteEditor ✅
    -   [x] Reason: No separate domain object (UMLNoteGR stores `String text` directly)
    -   [x] Notes are special: they annotate other elements rather than being standalone entities
    -   [x] Can revisit later if domain separation becomes necessary
-   [x] Task 2a.4: Skip StringEditorDialog ✅
    -   [x] Reason: Utility dialog for generic string input, not a domain editor

**Phase 2a Summary**: All tasks complete. Both editors (UCExtendEditor, CallMessageEditor) now accept domain objects instead of graphical wrappers. All 338 tests passing. Proper MVC architecture restored: editors work with domain models, not views.

**Phase 2b: Migrate to Editor<T> Interface** (IN PROGRESS)

**Dependencies**: Phase 2a complete ✅

After Phase 2a, these editors work with domain objects and can be migrated to Editor<T>:

-   [ ] Task 2b.1: Make UCExtend implement Copyable<UCExtend>
    -   [ ] Add clone() method to UCExtend class
    -   [ ] Handle deep copy of `List<ExtensionPoint> extensionPoints`
    -   [ ] Determine if ExtensionPoint needs Copyable
    -   [ ] Write test for cloning
-   [ ] Task 2b.2: Make CallMessage implement Copyable<CallMessage>
    -   [ ] Add clone() method to CallMessage class
    -   [ ] Ensure CreateMessage subclass clones correctly
    -   [ ] Write test for cloning both CallMessage and CreateMessage
-   [ ] Task 2b.3: Migrate UCExtendEditor to implement Editor<UCExtend>
    -   [ ] Add `implements Editor<UCExtend>`
    -   [ ] Implement `editDialog(UCExtend ucExtend, Component parent)` method
    -   [ ] Update UCDSelectionController to use new interface method
-   [ ] Task 2b.4: Migrate CallMessageEditor to implement Editor<CallMessage>
    -   [ ] Add `implements Editor<CallMessage>`
    -   [ ] Implement `editDialog(CallMessage message, Component parent)` method
    -   [ ] Update AbstractSDSelectionController and SDSelectionController
-   [ ] Task 2b.5: Decide on UMLNoteEditor approach
    -   [ ] Option 1: Create NoteContent domain object
    -   [ ] Option 2: Keep as special case (String-based)
-   [ ] Task 2b.6: Update all controller call sites
-   [ ] Task 2b.7: Run full test suite

### Phase 3: Migrate TypedEntityEditor (Pattern 1) - IN PROGRESS

TypedEntityEditor hierarchy may also not fit cleanly into `Editor<T>` pattern due to similar issues (graphical wrappers, complex return types). Need to investigate.

-   [ ] Task 3.1: Analyze TypedEntityEditor pattern and determine if migration is appropriate
-   [ ] Task 3.2: If appropriate, proceed with migration

### Phase 4: Migrate ClassifierEditor (Pattern 1) - PENDING

ClassifierEditor hierarchy should be investigated for Editor interface migration.

-   [ ] Task 4.1: Analyze ClassifierEditor pattern
-   [ ] Task 4.2: Determine migration strategy

### Phase 5: Special Cases - PENDING

-   [ ] Task 5.1: Analyze `AssociationEditor` family
-   [ ] Task 5.2: Migrate or document exceptions
-   [ ] Task 5.3: Commit Phase 5

### Final Tasks

-   [ ] Update CHANGELOG.md
-   [ ] Remove deprecated methods (or mark for future removal)
-   [ ] Update documentation
-   [ ] Final full test suite run
-   [ ] Merge to develop

## Implementation Summary

**Phase 1**: ✅ Successfully renamed `ElementEditor<T>` to `Editor<T>` - 10 files updated, all tests passing.

**Phase 2**: 🔄 IN PROGRESS - Refactoring Pattern 3 editors to use domain objects instead of graphical wrappers

**Key Insight**: Pattern 3 editors take graphical elements (UCExtendGR, CallMessageGR) instead of domain objects. The graphical wrappers contain the actual domain objects:

-   `UCExtendGR` → contains `UCExtend` (with extension points)
-   `CallMessageGR` → contains `CallMessage` (with message details)
-   `UMLNoteGR` → contains `String text` directly

**Refactoring Approach**:

1. **Phase 2a**: Change editors to accept domain objects instead of graphical wrappers
2. **Phase 2b**: Make domain objects implement `Copyable<T>` and migrate editors to `Editor<T>` interface

This follows proper architecture: editors should work with domain objects, not graphical representations.

**Phase 3-5**: PENDING - Will address after Phase 2 completes

## Design Documentation

[Will create StudentUML diagram showing Editor interface hierarchy]
