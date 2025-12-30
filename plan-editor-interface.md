# Plan: Standardize Editor Interface

## Goal
Unify all editor dialogs to use a consistent `Editor<T>` interface pattern for better maintainability, testability, and code clarity.

## Investigation

Current state analysis completed in `editor-interface-analysis.md`. Key findings:
- 3 different editor patterns in use across 17+ editors
- Pattern 2 (ElementEditor) is the cleanest and already implemented by 3 editors
- ~26 controller call sites need updating
- All editors can be migrated incrementally without breaking functionality

## Affected Components

### Phase 1: Rename Interface (Low Risk)
- `ElementEditor<T>` → `Editor<T>` interface
- Files: `src/main/java/edu/city/studentuml/view/gui/components/ElementEditor.java`
- Already compatible: `AttributeEditor`, `MethodEditor`, `MethodParameterEditor`
- Test files: `AttributeEditorTest`, `MethodEditorTest`, `MethodParameterEditorTest`

### Phase 2: Pattern 3 Editors (Medium Risk - 5 editors)
- `StringEditorDialog`
- `UCExtendEditor`
- `CallMessageEditor`
- `ObjectFlowEditor`
- `UMLNoteEditor`

Changes needed:
- Move parent/title from constructor to `editDialog()` parameters
- Implement `Editor<T>` interface
- Return domain object instead of boolean
- Update controllers: UCDSelectionController, ADSelectionController, AbstractSDSelectionController

### Phase 3: Pattern 1 Editors - TypedEntityEditor (High Risk - 5 editors)
- `ObjectEditor`
- `MultiObjectEditor`
- `ActorInstanceEditor`
- `SystemInstanceEditor`
- `ObjectNodeEditor`

Changes needed:
- Implement `Editor<T>` interface
- Add `editDialog()` method that internally calls `showDialog()`
- Eventually deprecate `showDialog()` in favor of `editDialog()`
- Update controllers: SDSelectionController, SSDSelectionController, ADSelectionController, AbstractSDSelectionController

### Phase 4: Pattern 1 Editors - ClassifierEditor (High Risk - 4 editors)
- `ClassEditor`
- `InterfaceEditor`
- `ConceptualClassEditor`
- `ActorEditor`

Changes needed:
- Implement `Editor<T>` interface
- Add `editDialog()` method
- Update controllers: DCDSelectionController, CCDSelectionController, UCDSelectionController

### Phase 5: Special Cases
- `AssociationEditor` and `AssociationEditorBase`
- Evaluate and migrate

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
- [x] Rename `ElementEditor<T>` to `Editor<T>`
- [x] Update all references (AttributeEditor, MethodEditor, MethodParameterEditor)
- [x] Update test files
- [x] Run full test suite to verify no breaks
- [x] Commit: "Rename ElementEditor to Editor interface"

**Completed**: Phase 1 successful! Renamed interface and updated 10 files:
- Editor.java (renamed from ElementEditor.java)
- AttributeEditor.java
- MethodEditor.java  
- MethodParameterEditor.java
- ObjectNodeEditor.java
- ListPanel.java
- AttributesPanel.java
- MethodsPanel.java
- MethodParameterPanel.java
- ExtensionPointsPanel.java

All 338 tests passing.

### Phase 2: Migrate Pattern 3 Editors (OkCancelDialog No-Param)
- [ ] Task 2.1: Migrate `StringEditorDialog`
  - [ ] Write failing test for `editDialog()` method
  - [ ] Implement `Editor<String>` interface
  - [ ] Add `editDialog(String text, Component parent)` method
  - [ ] Keep `showDialog()` as deprecated (calls editDialog internally)
  - [ ] Update test to verify both methods work
  - [ ] Run tests
  
- [ ] Task 2.2: Update controllers using `StringEditorDialog`
  - [ ] Update `UCDSelectionController`
  - [ ] Update `ADSelectionController`
  - [ ] Update `DCDSelectionController` (if used)
  - [ ] Run tests
  
- [ ] Task 2.3: Migrate `UCExtendEditor`
  - [ ] Write failing test for `editDialog()` method
  - [ ] Implement `Editor<UCExtend>` interface
  - [ ] Add `editDialog()` method
  - [ ] Keep `showDialog()` as deprecated
  - [ ] Run tests
  
- [ ] Task 2.4: Update controllers using `UCExtendEditor`
  - [ ] Update `UCDSelectionController`
  - [ ] Run tests
  
- [ ] Task 2.5: Migrate `CallMessageEditor`
  - [ ] Write failing test for `editDialog()` method
  - [ ] Implement `Editor<CallMessage>` interface
  - [ ] Add `editDialog()` method
  - [ ] Run tests
  
- [ ] Task 2.6: Update controllers using `CallMessageEditor`
  - [ ] Update `AbstractSDSelectionController`
  - [ ] Run tests
  
- [ ] Task 2.7: Migrate `ObjectFlowEditor`
  - [ ] Write failing test for `editDialog()` method
  - [ ] Implement `Editor<ObjectFlow>` interface
  - [ ] Add `editDialog()` method
  - [ ] Run tests
  
- [ ] Task 2.8: Update controllers using `ObjectFlowEditor`
  - [ ] Update `ADSelectionController`
  - [ ] Run tests
  
- [ ] Task 2.9: Migrate `UMLNoteEditor`
  - [ ] Write failing test for `editDialog()` method
  - [ ] Implement `Editor<UMLNote>` interface
  - [ ] Add `editDialog()` method
  - [ ] Run tests
  
- [ ] Task 2.10: Commit Phase 2
  - [ ] Run full test suite
  - [ ] Commit: "Migrate Pattern 3 editors to Editor interface"

### Phase 3: Migrate TypedEntityEditor (Pattern 1)
- [ ] Task 3.1: Design `TypedEntityEditor` interface adaptation
  - [ ] Analyze TypedEntityEditor<T, D> generic structure
  - [ ] Decide on return type for `editDialog()` (entity vs. pair?)
  
- [ ] Task 3.2-3.6: Migrate each TypedEntityEditor subclass
  - [ ] `ObjectEditor` + update SDSelectionController
  - [ ] `MultiObjectEditor` + update SDSelectionController
  - [ ] `ActorInstanceEditor` + update AbstractSDSelectionController
  - [ ] `SystemInstanceEditor` + update SSDSelectionController
  - [ ] `ObjectNodeEditor` + update ADSelectionController
  
- [ ] Task 3.7: Commit Phase 3

### Phase 4: Migrate ClassifierEditor (Pattern 1)
- [ ] Task 4.1: Design `ClassifierEditor` interface adaptation
  
- [ ] Task 4.2-4.5: Migrate each ClassifierEditor subclass
  - [ ] `ClassEditor` + update DCDSelectionController
  - [ ] `InterfaceEditor` + update DCDSelectionController
  - [ ] `ConceptualClassEditor` + update CCDSelectionController
  - [ ] `ActorEditor` + update UCDSelectionController
  
- [ ] Task 4.6: Commit Phase 4

### Phase 5: Special Cases
- [ ] Task 5.1: Analyze `AssociationEditor` family
- [ ] Task 5.2: Migrate or document exceptions
- [ ] Task 5.3: Commit Phase 5

### Final Tasks
- [ ] Update CHANGELOG.md
- [ ] Remove deprecated methods (or mark for future removal)
- [ ] Update documentation
- [ ] Final full test suite run
- [ ] Merge to develop

## Implementation Summary

[To be filled at completion]

## Design Documentation

[Will create StudentUML diagram showing Editor interface hierarchy]
