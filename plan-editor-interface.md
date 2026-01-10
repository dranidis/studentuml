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

### Phase 3: Pattern 1 Editors - ClassifierEditor (High Risk - 4 editors) ✅ COMPLETE

-   `ActorEditor` ✅
-   `ConceptualClassEditor` ✅
-   `ClassEditor` ✅
-   `InterfaceEditor` ✅

Changes completed:

-   All 4 domain objects now implement `Copyable<T>` (Actor, ConceptualClass, DesignClass, Interface)
-   All 4 editors now implement `Editor<T>` interface with `editDialog()` method
-   Added `setClassifierName()` protected helper method to ClassifierEditor base class
-   All editors have new constructors taking only `CentralRepository`
-   Old constructors deprecated for backward compatibility
-   All 41 tests passing
-   Controllers: DCDSelectionController, CCDSelectionController, UCDSelectionController (still use deprecated constructors)

### Phase 4: Pattern 1 Editors - TypedEntityEditor (High Risk - 5 editors)

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

### Phase 5: Update Controllers to Use New Editor<T> Interface

Update all controller call sites to use the new `editDialog()` method instead of deprecated constructors:

**Phase 2 & 3 Controllers**:

-   `UCDSelectionController` (uses UCExtendEditor, ActorEditor)
-   `AbstractSDSelectionController` (uses CallMessageEditor)
-   `SDSelectionController` (uses CallMessageEditor)
-   `DCDSelectionController` (uses ClassEditor, InterfaceEditor)
-   `CCDSelectionController` (uses ConceptualClassEditor)

Changes needed:

-   Create editor instance with new constructor (taking only CentralRepository)
-   Call `editDialog(domainObject, parent)` instead of passing domain object to constructor
-   Handle null return (indicates cancellation)
-   Update all call sites in each controller

### Phase 6: TypedEntityEditor Hierarchy - PENDING

TypedEntityEditor hierarchy needs to be analyzed for Editor interface migration.

-   [ ] Task 6.1: Analyze TypedEntityEditor pattern and determine if migration is appropriate
-   [ ] Task 6.2: If appropriate, proceed with migration (ObjectEditor, MultiObjectEditor, ActorInstanceEditor, SystemInstanceEditor, ObjectNodeEditor)

### Phase 7: Special Cases ✅ COMPLETE

-   ✅ **AssociationEditor** and **AssociationEditorBase** - Migrated to Editor<Association>
-   ✅ **ConceptualAssociationClassEditor** - Enhanced with editDialog() method following Editor pattern
-   ✅ **DesignAssociationClassEditor** - Enhanced with editDialog() method following Editor pattern

**Phase 7 Details:**

-   [x] Task 7.1: Refactor AssociationEditor to remove AssociationGR dependency ✅
    -   [x] Removed `private AssociationGR association` field
    -   [x] Changed constructor from `AssociationEditor(AssociationGR)` to `AssociationEditor()`
    -   [x] Deprecated old `initialize()` method
    -   [x] Controllers extract domain object before calling editor
-   [x] Task 7.2: Refactor CCDAssociationEditor to remove AssociationGR dependency ✅
    -   [x] Removed `private AssociationGR association` field
    -   [x] Changed constructor from `CCDAssociationEditor(AssociationGR)` to `CCDAssociationEditor()`
    -   [x] Deprecated old `initialize()` method
    -   [x] Controllers extract domain object before calling editor
-   [x] Task 7.3: Refactor ConceptualAssociationClassEditor to remove AssociationClassGR dependency ✅
    -   [x] Removed `private AssociationClassGR associationClassGR` field
    -   [x] Changed constructor from `ConceptualAssociationClassEditor(AssociationClassGR, CentralRepository)` to `ConceptualAssociationClassEditor(CentralRepository)`
    -   [x] Added `initializeFromAssociationClass(ConceptualAssociationClass)` method
    -   [x] Added `editDialog(ConceptualAssociationClass, Component)` method following Editor pattern
    -   [x] Deprecated old `initialize()` method
    -   [x] ConceptualAssociationClass implements `Copyable<ConceptualAssociationClass>` with `copyOf()` method
    -   [x] Controllers now use `editDialog()` and `copyOf()` pattern
-   [x] Task 7.4: Refactor DesignAssociationClassEditor to remove AssociationClassGR dependency ✅
    -   [x] Removed `private AssociationClassGR associationClassGR` field
    -   [x] Changed constructor from `DesignAssociationClassEditor(AssociationClassGR, CentralRepository)` to `DesignAssociationClassEditor(CentralRepository)`
    -   [x] Added `initializeFromAssociationClass(DesignAssociationClass)` method
    -   [x] Added `editDialog(DesignAssociationClass, Component)` method following Editor pattern
    -   [x] Deprecated old `initialize()` method
    -   [x] DesignAssociationClass implements `Copyable<DesignAssociationClass>` with `copyOf()` method
    -   [x] Controllers now use `editDialog()` and `copyOf()` pattern
-   [x] Task 7.5: Update CCDSelectionController to use new pattern ✅
    -   [x] Changed from `showDialog()` + manual field extraction to `editDialog()` + `copyOf()`
    -   [x] Code reduced from 26 lines to 14 lines (46% reduction)
-   [x] Task 7.6: Update DCDSelectionController to use new pattern ✅
    -   [x] Changed from `showDialog()` + manual field extraction to `editDialog()` + `copyOf()`
    -   [x] Code reduced from 48 lines to 14 lines (71% reduction)
-   [x] Task 7.7: All tests passing ✅
    -   [x] All 336 tests passing, no regressions

**Phase 7 Summary**: All association editors now work exclusively with domain objects (no graphical dependencies). AssociationClass editors provide `editDialog()` methods following the Editor pattern, though they can't formally implement `Editor<AssociationClass>` due to inheriting from `AssociationEditorBase` which implements `Editor<Association>`. Controllers use clean, concise `editDialog()` + `copyOf()` pattern. All tests passing.

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

**Phase 2b: Migrate to Editor<T> Interface** ✅ COMPLETE

**Dependencies**: Phase 2a complete ✅

After Phase 2a, these editors work with domain objects and were migrated to Editor<T>:

-   [x] Task 2b.1: Make UCExtend implement Copyable<UCExtend> ✅
    -   [x] Added clone() method to UCExtend class
    -   [x] Handled deep copy of `List<ExtensionPoint> extensionPoints`
    -   [x] ExtensionPoint also implements Copyable
    -   [x] Tests passing
-   [x] Task 2b.2: Make CallMessage implement Copyable<CallMessage> ✅
    -   [x] Added clone() method to CallMessage class
    -   [x] CreateMessage subclass clones correctly
    -   [x] Tests passing for both CallMessage and CreateMessage
-   [x] Task 2b.3: Migrate UCExtendEditor to implement Editor<UCExtend> ✅
    -   [x] Added `implements Editor<UCExtend>`
    -   [x] Implemented `editDialog(UCExtend ucExtend, Component parent)` method
    -   [x] UCDSelectionController still uses deprecated constructor
-   [x] Task 2b.4: Migrate CallMessageEditor to implement Editor<CallMessage> ✅
    -   [x] Added `implements Editor<CallMessage>`
    -   [x] Implemented `editDialog(CallMessage message, Component parent)` method
    -   [x] AbstractSDSelectionController and SDSelectionController still use deprecated constructors
-   [x] Task 2b.5: Decided on UMLNoteEditor approach ✅
    -   [x] Kept as special case (String-based, no separate domain object)
-   [x] Task 2b.6: All tests passing ✅
    -   [x] All 338 tests passing, no regressions

**Phase 2b Summary**: Both UCExtendEditor and CallMessageEditor now implement `Editor<T>` interface. Domain objects implement `Copyable<T>`. All tests passing.

### Phase 3: Migrate ClassifierEditor Hierarchy ✅ COMPLETE

**Dependencies**: None

ClassifierEditor hierarchy (Actor, ConceptualClass, DesignClass, Interface) editors migrated to Editor<T> pattern.

-   [x] Task 3.1: Make domain objects implement Copyable<T> ✅
    -   [x] Actor implements Copyable<Actor>
    -   [x] ConceptualClass implements Copyable<ConceptualClass>
    -   [x] DesignClass implements Copyable<DesignClass>
    -   [x] Interface implements Copyable<Interface>
    -   [x] All already had clone() methods
-   [x] Task 3.2: Migrate ActorEditor to implement Editor<Actor> ✅
    -   [x] Added `implements Editor<Actor>`
    -   [x] Implemented `editDialog(Actor actor, Component parent)` method
    -   [x] Added protected `setClassifierName(String)` to ClassifierEditor base class
    -   [x] All 7 tests passing
-   [x] Task 3.3: Migrate ConceptualClassEditor to implement Editor<ConceptualClass> ✅
    -   [x] Added `implements Editor<ConceptualClass>`
    -   [x] Implemented `editDialog(ConceptualClass conceptualClass, Component parent)` method
    -   [x] All 8 tests passing
-   [x] Task 3.4: Migrate ClassEditor to implement Editor<DesignClass> ✅
    -   [x] Added `implements Editor<DesignClass>`
    -   [x] Implemented `editDialog(DesignClass designClass, Component parent)` method
    -   [x] Extracted `initializeUI(CentralRepository)` helper method
    -   [x] All 16 tests passing
-   [x] Task 3.5: Migrate InterfaceEditor to implement Editor<Interface> ✅
    -   [x] Added `implements Editor<Interface>`
    -   [x] Implemented `editDialog(Interface interfaceObj, Component parent)` method
    -   [x] All 10 tests passing

**Phase 3 Summary**: All 4 ClassifierEditor subclasses now implement `Editor<T>` interface. All 41 tests passing. Controllers (DCDSelectionController, CCDSelectionController, UCDSelectionController) still use deprecated constructors.

### Phase 4: Migrate TypedEntityEditor Hierarchy - PENDING

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

**Phase 2**: ✅ COMPLETE - Pattern 3 editors refactored to use domain objects and migrated to `Editor<T>` interface

**Phase 3**: ✅ COMPLETE - ClassifierEditor hierarchy migrated to `Editor<T>` interface (Actor, ConceptualClass, DesignClass, Interface editors)

**Phase 4**: ⏸️ NOT STARTED - Would analyze TypedEntityEditor hierarchy, but skipping for now

**Phase 5**: ⏸️ NEXT - Update controllers to use new `editDialog()` methods (removing deprecated constructor calls)

**Phase 6**: ✅ COMPLETE - TypedEntityEditor hierarchy fully migrated (Phase 9 refactoring)

**Phase 7**: ✅ COMPLETE - AssociationEditor family migrated to Editor<Association>

-   All association editors now work with domain objects only (no dependency on graphical elements)
-   AssociationEditor and CCDAssociationEditor refactored to remove AssociationGR dependency
-   ConceptualAssociationClassEditor and DesignAssociationClassEditor refactored to remove AssociationClassGR dependency
-   AssociationClass editors provide `editDialog()` methods following Editor pattern
-   ConceptualAssociationClass and DesignAssociationClass implement `Copyable<T>` with `copyOf()` method
-   Controllers updated to use `editDialog()` + `copyOf()` pattern (CCDSelectionController, DCDSelectionController)

## Editors by Controller - Implementation Status

### UCDSelectionController (Use Case Diagrams)

-   ✅ **ActorEditor** - `implements Editor<Actor>` - uses `editDialog()` ✅ **UPDATED**
-   ❌ **UseCaseDialog** - uses `showDialog()` (not an Editor)
-   🔧 **StringEditorDialog** (for include/extend stereotype) - uses `showDialog()` (utility dialog)
-   ✅ **UCExtendEditor** - `implements Editor<UCExtend>` - uses `editDialog()` ✅ **UPDATED**

### CCDSelectionController (Conceptual Class Diagrams)

-   ✅ **ConceptualClassEditor** - `implements Editor<ConceptualClass>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **CCDAssociationEditor** (extends AssociationEditorBase) - `implements Editor<Association>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **ConceptualAssociationClassEditor** (extends AssociationEditorBase) - has `editDialog(ConceptualAssociationClass, Component)` method - uses `editDialog()` + `copyOf()` pattern ✅ **UPDATED**

### DCDSelectionController (Design Class Diagrams)

-   ✅ **ClassEditor** - `implements Editor<DesignClass>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **InterfaceEditor** - `implements Editor<Interface>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **AssociationEditor** (extends AssociationEditorBase) - `implements Editor<Association>` - uses `editDialog()` ✅ **UPDATED**
-   🔧 **StringEditorDialog** (for dependency stereotype) - uses `showDialog()` (utility dialog)
-   ✅ **DesignAssociationClassEditor** (extends AssociationEditorBase) - has `editDialog(DesignAssociationClass, Component)` method - uses `editDialog()` + `copyOf()` pattern ✅ **UPDATED**

### SDSelectionController (Sequence Diagrams)

-   ✅ **ObjectEditor** (extends TypedEntityEditor) - `implements Editor<TypedEntityEditResult<DesignClass, SDObject>>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **MultiObjectEditor** (extends TypedEntityEditor) - `implements Editor<TypedEntityEditResult<DesignClass, MultiObject>>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **CallMessageEditor** - `implements Editor<CallMessage>` - uses `editDialog()` ✅ **UPDATED**

### AbstractSDSelectionController (SD/SSD Base)

-   ✅ **ActorInstanceEditor** (extends TypedEntityEditor) - `implements Editor<TypedEntityEditResult<Actor, ActorInstance>>` - uses `editDialog()` ✅ **UPDATED**
-   ✅ **CallMessageEditor** - `implements Editor<CallMessage>` - uses `editDialog()` ✅ **UPDATED**
-   ❌ **ReturnMessageDialog** - uses `showDialog()` (simple dialog, not an Editor)

### SSDSelectionController (System Sequence Diagrams)

-   ✅ **SystemInstanceEditor** (extends TypedEntityEditor) - `implements Editor<TypedEntityEditResult<System, SystemInstance>>` - uses `editDialog()` ✅ **UPDATED**

### ADSelectionController (Activity Diagrams)

-   ❌ **ControlFlowEditor** - uses `showDialog()` (not an Editor)
-   ✅ **ObjectFlowEditor** - `implements Editor<ObjectFlow>` - uses `editDialog()` ✅ **UPDATED**
-   ❌ **ActionNodeEditor** - uses `showDialog()` (not an Editor)
-   ✅ **ObjectNodeEditor** (extends TypedEntityEditor) - `implements Editor<TypedEntityEditResult<DesignClass, ObjectNode>>` - uses `editDialog()` ✅ **UPDATED**
-   ❌ **DecisionNodeEditor** - uses `showDialog()` (not an Editor)
-   🔧 **StringEditorDialog** (for action/decision/merge stereotypes) - uses `showDialog()` (utility dialog)

### SelectionController (Base - Notes)

-   ❌ **UMLNoteEditor** - uses `showDialog()` (special case, no domain object)

## Summary Statistics

**Editors Using Editor<T> Interface**: 23/27 editors

### ✅ Implementing Editor<T> or Editor Pattern (23 editors):

1. AttributeEditor - `Editor<Attribute>`
2. MethodEditor - `Editor<Method>`
3. MethodParameterEditor - `Editor<MethodParameter>`
4. ActorEditor - `Editor<Actor>`
5. ConceptualClassEditor - `Editor<ConceptualClass>`
6. ClassEditor - `Editor<DesignClass>`
7. InterfaceEditor - `Editor<Interface>`
8. UCExtendEditor - `Editor<UCExtend>`
9. CallMessageEditor - `Editor<CallMessage>`
10. AssociationEditorBase (abstract) - `Editor<Association>`
11. AssociationEditor - extends AssociationEditorBase
12. CCDAssociationEditor - extends AssociationEditorBase
13. ConceptualAssociationClassEditor - extends AssociationEditorBase, has `editDialog(ConceptualAssociationClass, Component)` method ✅ **UPDATED**
14. DesignAssociationClassEditor - extends AssociationEditorBase, has `editDialog(DesignAssociationClass, Component)` method ✅ **UPDATED**
15. TypedEntityEditor<T,D> (abstract) - `Editor<TypedEntityEditResult<T, D>>`
16. ObjectEditor - extends TypedEntityEditor
17. MultiObjectEditor - extends TypedEntityEditor
18. ActorInstanceEditor - extends TypedEntityEditor
19. SystemInstanceEditor - extends TypedEntityEditor
20. ObjectNodeEditor - extends TypedEntityEditor
21. ObjectFlowEditor - `Editor<ObjectFlow>`

### ❌ Not Using Editor<T> (6 editors/dialogs):

1. 🔧 **StringEditorDialog** - utility dialog for generic string input (used in UCDSelectionController, DCDSelectionController, ADSelectionController)
2. **UseCaseDialog** - legacy use case editor
3. **UMLNoteEditor** - special case (String-based, no domain object)
4. **ControlFlowEditor** - activity diagram control flow
5. **ActionNodeEditor** - activity diagram action node
6. **DecisionNodeEditor** - activity diagram decision node
7. **ReturnMessageDialog** - simple return message dialog
8. **ClassNameEditor** - utility dialog for class names

**Legend**:

-   ✅ = Implements `Editor<T>` interface or provides `editDialog()` method following Editor pattern
-   ❌ = Does not implement `Editor<T>` interface
-   🔧 = StringEditorDialog (utility dialog used across multiple controllers)

**Key Achievement**: All major domain object editors now use the `Editor<T>` interface pattern or provide `editDialog()` methods following the same contract, ensuring consistency, testability, and maintainability across the codebase. AssociationClass editors, while unable to formally implement `Editor<AssociationClass>` due to inheritance constraints, provide equivalent functionality through their `editDialog()` methods.

## Design Documentation

[Will create StudentUML diagram showing Editor interface hierarchy]
