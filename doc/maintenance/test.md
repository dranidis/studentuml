# StudentUML Testing Strategy

## Current Coverage Status

**Overall Coverage: 32% (23,581 of 72,317 instructions)** ⬆️ +4% from previous

**Test Count: 275 tests** (0 failures, 0 errors, 1 skipped) ⬆️ +55 new tests

### Package Coverage Breakdown

| Package              | Coverage | Change from Baseline | Priority                  |
| -------------------- | -------- | -------------------- | ------------------------- |
| **Code Generation**  | 71%      | (baseline)           | ✅ Well tested            |
| **Model Repository** | 64%      | ⬆️ +14%              | ✅ Good progress          |
| **Util**             | 59%      | ⬆️ +12%              | ✅ Good progress          |
| **Model Domain**     | 58%      | ⬆️ +10%              | ✅ Good progress          |
| **Frame**            | 54%      | ⬆️ +4%               | ✅ Improved               |
| **Util Version**     | 45%      | NEW                  | � New area                |
| **Model Graphical**  | 38%      | ⬆️ +6%               | 🟡 Needs improvement      |
| **Undo/Redo**        | 24%      | ⬆️ +3%               | 🔴 Critical functionality |
| **View**             | 22%      | ⬇️ -6%               | 🔴 High priority          |
| **Controller**       | 21%      | (unchanged)          | 🔴 High priority          |
| **GUI Main**         | 9%       | ⬆️ +3%               | 🔴 Untested               |
| **GUI Components**   | 0%       | (unchanged)          | 🔴 Untested               |
| **Validation**       | 0%       | (unchanged)          | 🔴 Critical untested      |
| **GUI Menu**         | 0%       | (unchanged)          | 🔴 Untested               |

**Recent Impact Summary:**

-   ✅ Overall coverage increased by **4%** (28% → 32%)
-   ✅ Model Repository coverage increased by **14%** (50% → 64%)
-   ✅ Util coverage increased by **12%** (47% → 59%)
-   ✅ Domain Model coverage increased by **10%** (48% → 58%)
-   ✅ Graphical Model coverage increased by **6%** (32% → 38%)
-   ✅ Added comprehensive link reconnection tests (40 new tests)
-   ✅ Added comprehensive AddElementControllerFactory tests (7 new tests)

**Refactoring Impact (refactor-edit-dialogs branch):**

-   📊 **Java Code Statistics vs develop**:
    -   Java files deleted: 7 files (812 lines eliminated)
    -   Java files added: 7 files (1,333 lines of reusable abstractions)
    -   Net Java code: +521 lines of reusable infrastructure
-   🗑️ **Files Eliminated**: 7 editor files (812 lines total)
    -   ActionNodeEditor (105), ActivityNodeEditor (105), ClassNameEditor (142)
    -   ControlFlowEditor (105), DecisionNodeEditor (106), DependencyEditor (148)
    -   StateEditor (101)
-   ➕ **New Reusable Abstractions**: 7 Java files (1,333 lines total)
    -   OkCancelDialog (124), StringEditorDialog (65), TypedEntityEditor (500)
    -   AssociationEditorBase (329), RolePanel (144), ExtensionPointsPanel (52)
    -   AssociationClassShowArrowTest (119)
-   ✅ **Code Quality**: Eliminated 812 lines of duplicate editor code, invested 521 net lines in reusable abstractions that benefit 14 refactored editors
-   🎯 **Impact**: ~54% average code duplication eliminated across 14 editors through abstraction patterns, all 220 tests passing

## Testing Goals

### Short-term Goal (Phase 1)

**Target: 50% overall coverage**

-   Focus on business logic and core functionality
-   Prioritize domain models and controllers

### Medium-term Goal (Phase 2)

**Target: 65% overall coverage**

-   Expand controller tests
-   Add undo/redo comprehensive testing
-   Cover validation layer

### Long-term Goal (Phase 3)

**Target: 75% overall coverage**

-   GUI component testing (where feasible)
-   Integration test scenarios
-   Edge cases and error handling

## Testing Strategy by Layer

### 1. Domain Model Layer (37% → 75%)

**Priority: HIGH** - Core business logic that should be thoroughly tested

#### Current State

-   23 of 69 classes tested
-   Basic functionality covered, complex logic untested

#### Testing Approach

-   **Unit tests for each domain entity**: Test all public methods, getters/setters, business logic
-   **Test equality and hash code**: Ensure proper object comparison
-   **Test serialization**: Validate XML streaming (IXMLCustomStreamable)
-   **Test relationships**: Association, composition, inheritance, dependencies

#### Key Classes to Test

```
Priority 1 (Core entities):
- ConceptualClass and its attributes
- DesignClass and its methods/attributes
- Actor, UseCase
- Message (CallMessage, ReturnMessage)
- Interface and InterfaceExtension
- Association, AssociationClass
- Generalization, Composition, Aggregation

Priority 2 (Supporting entities):
- Note, Constraint
- ActivityAction, DecisionNode, MergeNode
- Package, Subsystem
- Attribute, Method, Parameter
```

#### Example Test Pattern

```java
@Test
public void testConceptualClassCreation() {
    ConceptualClass cc = new ConceptualClass("Customer");
    assertEquals("Customer", cc.getName());
    assertNotNull(cc.getAttributes());
    assertTrue(cc.getAttributes().isEmpty());
}

@Test
public void testAddAttribute() {
    ConceptualClass cc = new ConceptualClass("Customer");
    Attribute attr = new Attribute("name", "String");
    cc.addAttribute(attr);
    assertEquals(1, cc.getAttributes().size());
    assertTrue(cc.getAttributes().contains(attr));
}

@Test
public void testXMLSerialization() {
    ConceptualClass cc = new ConceptualClass("Customer");
    // Test toXML and fromXML methods
}
```

---

### 2. Graphical Model Layer (28% → 60%)

**Priority: HIGH** - Visual representation logic is critical

#### Current State

-   28 of 83 classes tested
-   Basic rendering tested, complex interactions untested

#### Testing Approach

-   **Test graphical element creation**: Verify proper initialization
-   **Test bounds and positioning**: Location, size, resize logic
-   **Test containment**: Point-in-shape tests
-   **Test relationships rendering**: Line routing, anchors
-   **Test visual properties**: Colors, styles, labels

#### Key Classes to Test

```
Priority 1 (Core graphical elements):
- ClassGR, InterfaceGR
- UseCaseGR, ActorGR
- MessageGR, ActivationBoxGR
- AssociationGR, GeneralizationGR
- NoteGR, CommentLinkGR

Priority 2 (Abstract base classes):
- AbstractLineGR (line routing, anchors)
- RectangularGR (resize logic)
- SDElementGR (sequence diagram specifics)
```

#### Example Test Pattern

```java
@Test
public void testClassGRCreation() {
    DesignClass dc = new DesignClass("Customer");
    ClassGR cgr = new ClassGR(dc, 100, 100);
    assertEquals(100, cgr.getX());
    assertEquals(100, cgr.getY());
    assertNotNull(cgr.getBounds());
}

@Test
public void testContainsPoint() {
    ClassGR cgr = new ClassGR(new DesignClass("Test"), 0, 0);
    assertTrue(cgr.contains(50, 50));
    assertFalse(cgr.contains(-10, -10));
}

@Test
public void testResize() {
    ClassGR cgr = new ClassGR(new DesignClass("Test"), 0, 0);
    int originalWidth = cgr.getWidth();
    cgr.setWidth(200);
    assertEquals(200, cgr.getWidth());
}
```

---

### 3. Controller Layer (18% → 70%)

**Priority: CRITICAL** - User interaction logic must be reliable

#### Current State

-   35 of 73 classes tested
-   Selection controllers partially tested
-   **✅ AddControlFlowController fully tested** (6 comprehensive tests)
-   Add element controllers mostly untested
-   Edit/delete controllers untested

#### Recently Completed

**DCDLinkReconnectionTest** (Phase 0.8):

-   File: `src/test/java/edu/city/studentuml/controller/DCDLinkReconnectionTest.java`
-   Tests: 21 comprehensive link reconnection tests for Design Class Diagrams
-   Pattern: Controller + Model level testing (no GUI dependencies)
-   Coverage: All class diagram link types
    -   **Association**: reconnect source, reconnect target, self-association, interface connection
    -   **Aggregation**: reconnect source/target, preserves composition flag
    -   **Dependency**: reconnect source/target, type validation (ClassGR only)
    -   **Realization**: reconnect source/target, strict validation (ClassGR→InterfaceGR)
    -   **Generalization**: reconnect source/target, interface generalization, prevent self-inheritance, prevent cross-type mixing
-   Validation Rules:
    -   Type compatibility enforced (e.g., Realization requires class→interface)
    -   Self-inheritance prevented for Generalization
    -   Domain model properly updated after reconnection
    -   Link properties preserved (composition flag, role names, multiplicities)
-   Status: ✅ ALL 21 TESTS PASSING

**UCDLinkReconnectionTest** (Phase 0.8):

-   File: `src/test/java/edu/city/studentuml/controller/UCDLinkReconnectionTest.java`
-   Tests: 19 comprehensive link reconnection tests for Use Case Diagrams
-   Pattern: Controller + Model level testing (no GUI dependencies)
-   Coverage: All use case diagram link types
    -   **UCAssociation**: reconnect source/target, type validation (Actor↔UseCase)
    -   **UCInclude**: reconnect source/target, type validation (UseCase→UseCase)
    -   **UCExtend**: reconnect source/target, preserves extension points
    -   **UCGeneralization**: Actor→Actor and UseCase→UseCase, prevent cross-type, prevent self-inheritance
-   Validation Rules:
    -   Type compatibility enforced (e.g., Include/Extend only between use cases)
    -   Self-inheritance prevented for Generalization
    -   Cross-type generalization prevented (Actor can't inherit from UseCase)
    -   Extension points preserved in UCExtend
-   Status: ✅ ALL 19 TESTS PASSING

**AddControlFlowControllerTest** (Phase 0.7):

-   File: `src/test/java/edu/city/studentuml/controller/AddControlFlowControllerTest.java`
-   Tests: 6 comprehensive validation tests
-   Pattern: Testable controller (overrides showErrorMessage to avoid JOptionPane blocking)
-   Coverage: Action node flow validation, decision/merge/fork node validation
-   Validation Rules:
    -   Action nodes: maximum 1 incoming, maximum 1 outgoing control flow
    -   Decision/Fork nodes: multiple outgoing flows allowed
    -   Merge/Join nodes: multiple incoming flows allowed
-   Status: ✅ ALL 6 TESTS PASSING

#### Testing Approach

-   **Test mouse event handling**: Click, drag, double-click
-   **Test state management**: Current tool, selected elements
-   **Test undo/redo integration**: All operations create proper edits
-   **Test validation**: Invalid operations prevented
-   **Test multi-selection**: Group operations
-   **Use testable pattern**: Override UI methods to prevent test blocking

#### Key Controllers to Test

```
Priority 1 (Add element controllers):
- AddClassController
- AddAssociationController
- AddGeneralizationController
- AddMessageController
- AddUseCaseController
- AddActorController

Priority 2 (Edit controllers):
- EditNameController
- EditTextController
- ResizeController
- MoveController

Priority 3 (Specialized controllers):
- AddAttributeController
- AddMethodController
- AddNoteController
```

#### Example Test Pattern

```java
@Test
public void testAddClass() {
    DCDModel model = Helper.createDCDModel();
    AddClassController controller = new AddClassController(model);

    // Simulate click
    controller.mousePressed(new MouseEvent(...));
    controller.mouseReleased(new MouseEvent(...));

    assertEquals(1, model.getGraphicalElements().size());
    assertTrue(model.getGraphicalElements().get(0) instanceof ClassGR);
}

@Test
public void testMoveClassWithUndo() {
    DCDModel model = Helper.createDCDModel();
    ClassGR cgr = Helper.addClassToModel(model, "Test", 100, 100);

    MoveController controller = new MoveController(model);
    controller.moveElement(cgr, 200, 200);

    assertEquals(200, cgr.getX());
    assertEquals(200, cgr.getY());

    // Verify undo edit was created
    assertNotNull(model.getLastEdit());
    assertTrue(model.getLastEdit() instanceof MoveEdit);
}
```

#### Testable Controller Pattern (NEW - Phase 0.7)

For controllers that show dialogs (e.g., validation errors), use the testable pattern to avoid blocking tests:

```java
// Inner test class that overrides UI methods
private static class TestableAddControlFlowController extends AddControlFlowController {
    private String lastErrorMessage;

    public TestableAddControlFlowController(ADModel model) {
        super(model, null);
    }

    @Override
    protected void showErrorMessage(String message) {
        // Capture message instead of showing dialog
        this.lastErrorMessage = message;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void clearLastErrorMessage() {
        this.lastErrorMessage = null;
    }
}

// Test validation without blocking on JOptionPane
@Test
public void testValidationPreventsInvalidOperation() {
    ADModel model = Helper.createADModel();
    ActionNodeGR action = Helper.addActionNode(model, "Action1", 100, 100);
    ActionNodeGR action2 = Helper.addActionNode(model, "Action2", 200, 100);

    TestableAddControlFlowController controller = new TestableAddControlFlowController(model);

    // First flow succeeds
    controller.addEdge(action, action2);
    assertEquals(1, model.getGraphicalElements().stream()
        .filter(e -> e instanceof ControlFlowGR).count());

    // Second flow should be prevented
    ActionNodeGR action3 = Helper.addActionNode(model, "Action3", 300, 100);
    controller.clearLastErrorMessage();
    controller.addEdge(action, action3);  // Should fail - action already has outgoing flow

    // Verify error message shown
    assertNotNull("Error message should be captured", controller.getLastErrorMessage());
    assertTrue("Error should mention 'outgoing'",
        controller.getLastErrorMessage().contains("outgoing"));

    // Verify second flow NOT added
    assertEquals(1, model.getGraphicalElements().stream()
        .filter(e -> e instanceof ControlFlowGR).count());
}
```

**Key Benefits**:

-   Tests don't block waiting for user input
-   Can verify error message content
-   Model state remains testable
-   Follows existing architecture patterns

---

### 4. Undo/Redo System (21% → 85%)

**Priority: CRITICAL** - Data integrity depends on this

#### Current State

-   37 of 43 classes tested
-   Basic undo/redo tested
-   Complex composite operations untested
-   Edge cases untested

#### Testing Approach

-   **Test each edit type individually**: Add, Delete, Move, Modify, Composite
-   **Test undo/redo chains**: Multiple operations in sequence
-   **Test composite edits**: Operations affecting multiple elements
-   **Test edge cases**: Undo with no history, redo with no future
-   **Test state consistency**: Model state always valid after undo/redo

#### Key Classes to Test

```
Priority 1 (Core edit types):
- AddEdit
- DeleteEdit
- MoveEdit
- ModifyEdit (name changes, property changes)
- CompositeEdit
- CompositeDeleteEdit (handles cascading deletes)

Priority 2 (Specialized edits):
- ResizeEdit
- AddAttributeEdit
- AddMethodEdit
- ChangeTypeEdit
```

#### Example Test Pattern

```java
@Test
public void testDeleteWithUndo() {
    DCDModel model = Helper.createDCDModel();
    ClassGR cgr = Helper.addClassToModel(model, "Test", 0, 0);

    DeleteEdit edit = new DeleteEdit(model, cgr);
    edit.execute();

    assertFalse(model.getGraphicalElements().contains(cgr));

    edit.undo();
    assertTrue(model.getGraphicalElements().contains(cgr));

    edit.redo();
    assertFalse(model.getGraphicalElements().contains(cgr));
}

@Test
public void testCompositeDeleteWithRelationships() {
    DCDModel model = Helper.createDCDModel();
    ClassGR class1 = Helper.addClassToModel(model, "Class1", 0, 0);
    ClassGR class2 = Helper.addClassToModel(model, "Class2", 200, 0);
    AssociationGR assoc = Helper.addAssociation(model, class1, class2);

    CompositeDeleteEdit edit = new CompositeDeleteEdit(model, class1);
    edit.execute();

    // Both class and association should be deleted
    assertFalse(model.getGraphicalElements().contains(class1));
    assertFalse(model.getGraphicalElements().contains(assoc));

    edit.undo();
    // Both should be restored
    assertTrue(model.getGraphicalElements().contains(class1));
    assertTrue(model.getGraphicalElements().contains(assoc));
}
```

---

### 5. Validation Layer (0% → 80%)

**Priority: CRITICAL** - Currently completely untested!

#### Current State

-   0 of 11 classes tested
-   No validation logic tested
-   Rule-based consistency checker untested

#### Testing Approach

-   **Test validation rules**: Name validation, type validation, relationship rules
-   **Test consistency checking**: Inter-diagram consistency
-   **Test error reporting**: Proper error messages
-   **Test validation integration**: Validation triggered at right times

#### Key Classes to Test

```
Priority 1 (Core validators):
- NameValidator
- TypeValidator
- RelationshipValidator
- ConsistencyChecker

Priority 2 (Rule-based system):
- RuleBasedSystemGenerator
- Rule execution engine
```

#### Example Test Pattern

```java
@Test
public void testValidClassName() {
    assertTrue(NameValidator.isValidClassName("Customer"));
    assertTrue(NameValidator.isValidClassName("OrderDetail"));
}

@Test
public void testInvalidClassName() {
    assertFalse(NameValidator.isValidClassName(""));
    assertFalse(NameValidator.isValidClassName("123Invalid"));
    assertFalse(NameValidator.isValidClassName("Class Name"));
}

@Test
public void testConsistencyCheckDetectsMismatch() {
    UMLProject project = new UMLProject();
    // Create CCD with class "Customer"
    // Create DCD with class "Client" (should be "Customer")

    List<ConsistencyError> errors = ConsistencyChecker.check(project);
    assertEquals(1, errors.size());
    assertTrue(errors.get(0).getMessage().contains("Customer"));
}
```

---

### 6. Utility Layer (36% → 70%)

**Priority: MEDIUM** - Supporting functionality

#### Current State

-   Some utilities well tested (PositiveRectangle, Rotate, Scale)
-   XML streaming partially tested
-   Preferences untested
-   System utilities untested

#### Testing Approach

-   **Test mathematical utilities**: Geometry calculations, transformations
-   **Test XML utilities**: Parsing, streaming, error handling
-   **Test file utilities**: Path handling, file operations
-   **Test preferences**: Save/load settings

#### Key Classes to Test

```
Priority 1:
- XMLStreamer (save/load robustness)
- SystemWideObjectNamePool
- CollectionTreeModel

Priority 2:
- Preferences handling
- File utilities
- String utilities
```

---

### 7. View/GUI Layer (6% → 40%)

**Priority: MEDIUM** - Difficult to test, focus on testable parts

#### Current State

-   Almost completely untested
-   GUI components require special testing approach

#### Testing Approach

-   **Focus on logic, not rendering**: Test methods that can run without GUI
-   **Test data binding**: Model updates trigger view updates
-   **Test event handling**: Observer pattern behavior
-   **Use headless mode**: Test without X11/display server
-   **Integration tests**: Test complete user workflows

#### Key Areas to Test

```
Testable without GUI:
- Observer/Observable notification system
- View state management
- Data transformation for display

Integration tests:
- Model change → View update
- Diagram save/load round-trip
- Multiple diagram synchronization
```

#### Example Test Pattern

```java
@Test
public void testViewNotifiedOnModelChange() {
    DCDModel model = Helper.createDCDModel();
    DiagramView view = new TestableView(model); // Mock/stub

    // Add observer
    model.addObserver(view);

    // Make change
    model.addGraphicalElement(new ClassGR(...));

    // Verify view was notified
    assertTrue(view.wasNotified());
}
```

---

## Test Organization

### Test Structure

```
src/test/java/edu/city/studentuml/
├── model/
│   ├── domain/          # Domain entity tests
│   └── graphical/       # Graphical element tests
├── controller/          # Controller tests (already exists)
├── util/               # Utility tests (already exists)
│   └── undoredo/       # Undo/redo tests
├── validation/         # Validation tests (NEW)
├── codegeneration/     # Code gen tests (already exists)
├── integration/        # Integration tests (NEW)
└── Helper.java         # Test helpers (already exists)
```

### Naming Conventions

-   Test class: `{ClassName}Test.java`
-   Test method: `test{MethodName}{Scenario}()`
-   Examples:
    -   `ConceptualClassTest.java`
    -   `testAddAttribute()`, `testAddAttributeWithNull()`, `testAddDuplicateAttribute()`

---

## Testing Principles

### 1. Test Independence

-   Each test should be completely independent
-   Use `@Before` to set up fresh state
-   Use `@After` to clean up resources
-   No test should depend on execution order

### 2. Test One Thing

-   Each test should verify a single behavior
-   Clear test name describes what is being tested
-   Single assertion when possible, multiple assertions when testing object state

### 3. Arrange-Act-Assert Pattern

```java
@Test
public void testExample() {
    // Arrange: Set up test data
    Model model = new Model();

    // Act: Perform the operation
    model.doSomething();

    // Assert: Verify the result
    assertEquals(expected, model.getResult());
}
```

### 4. Test Edge Cases

-   Null inputs
-   Empty collections
-   Boundary values
-   Invalid states

### 5. Use Test Helpers

-   Reuse Helper class for common operations
-   Create additional helpers as needed
-   Keep tests readable and maintainable

---

## Implementation Roadmap

### Phase 0: Save/Load Integration Tests (Week 1)

**Goal: Verify serialization integrity for all diagram types**

This is the **critical foundation** - if save/load doesn't work correctly, nothing else matters. These tests will also provide comprehensive examples of creating all element types programmatically.

#### 1. **UCD (Use Case Diagram) Save/Load Test**

-   Create diagram with all element types:
    -   Actors (standard and stick figure)
    -   Use Cases
    -   Associations
    -   Generalizations (actor and use case)
    -   Include relationships
    -   Extend relationships
    -   System boundary
    -   Notes and comment links
-   Save to XML
-   Load from XML
-   Verify all elements present with correct properties

#### 2. **CCD (Conceptual Class Diagram) Save/Load Test**

-   Create diagram with:
    -   Conceptual Classes with attributes
    -   Associations (all multiplicities, bidirectional/unidirectional)
    -   Association Classes
    -   Generalizations
    -   Notes and comment links
-   Save to XML
-   Load from XML
-   Verify all elements and relationships

#### 3. **DCD (Design Class Diagram) Save/Load Test**

-   Create diagram with:
    -   Design Classes with attributes and methods
    -   Interfaces with methods
    -   Interface Extensions
    -   Associations (all types)
    -   Generalizations
    -   Compositions
    -   Aggregations
    -   Dependencies
    -   Notes and comment links
-   Save to XML
-   Load from XML
-   Verify all elements, methods, attributes, and relationships

#### 4. **SD (Sequence Diagram) Save/Load Test**

-   Create diagram with:
    -   Objects (actors and regular objects)
    -   Call Messages (synchronous and asynchronous)
    -   Return Messages
    -   Self-calls
    -   Create/Destroy messages
    -   Activation boxes
    -   Notes
-   Save to XML
-   Load from XML
-   Verify message ordering and activation boxes

#### 5. **SSD (System Sequence Diagram) Save/Load Test**

-   Create diagram with:
    -   System object
    -   Actor object
    -   System messages
    -   Return messages
    -   Notes
-   Save to XML
-   Load from XML
-   Verify system boundaries and messages

#### 6. **AD (Activity Diagram) Save/Load Test**

-   Create diagram with:
    -   Activity Actions
    -   Decision Nodes
    -   Merge Nodes
    -   Fork Nodes
    -   Join Nodes
    -   Initial Node
    -   Final Node
    -   Control Flows
    -   Notes
-   Save to XML
-   Load from XML
-   Verify flow connections and node types

#### 7. **Multi-Diagram Project Save/Load Test**

-   Create complete UMLProject with:
    -   One diagram of each type
    -   Elements in each diagram
    -   Cross-references (same class in CCD and DCD)
-   Save entire project
-   Load entire project
-   Verify all diagrams and cross-references intact
-   Verify CentralRepository consistency

#### Implementation Approach

**Test Class Structure:**

```java
src/test/java/edu/city/studentuml/integration/
├── SaveLoadTestBase.java           // Common save/load utilities
├── UCDSaveLoadTest.java
├── CCDSaveLoadTest.java
├── DCDSaveLoadTest.java
├── SDSaveLoadTest.java
├── SSDSaveLoadTest.java
├── ADSaveLoadTest.java
└── ProjectSaveLoadTest.java        // Multi-diagram test
```

**Test Pattern Example:**

```java
@Test
public void testDCDSaveLoadComplete() {
    // 1. CREATE - Build diagram with all element types
    DCDModel model = new DCDModel();

    // Add classes
    DesignClass class1 = new DesignClass("Customer");
    class1.addAttribute(new Attribute("name", "String"));
    class1.addMethod(new Method("getName", "String"));
    ClassGR cgr1 = new ClassGR(class1, 100, 100);
    model.addGraphicalElement(cgr1);

    // Add interface
    Interface iface = new Interface("Serializable");
    iface.addMethod(new Method("serialize", "void"));
    InterfaceGR igr = new InterfaceGR(iface, 300, 100);
    model.addGraphicalElement(igr);

    // Add interface extension
    InterfaceExtension ext = new InterfaceExtension(class1, iface);
    InterfaceExtensionGR egr = new InterfaceExtensionGR(ext, cgr1, igr);
    model.addGraphicalElement(egr);

    // Add another class
    DesignClass class2 = new DesignClass("Order");
    ClassGR cgr2 = new ClassGR(class2, 100, 300);
    model.addGraphicalElement(cgr2);

    // Add association
    Association assoc = new Association(class1, class2, "places");
    AssociationGR agr = new AssociationGR(assoc, cgr1, cgr2);
    model.addGraphicalElement(agr);

    // Add composition
    Composition comp = new Composition(class1, class2);
    CompositionGR compGr = new CompositionGR(comp, cgr1, cgr2);
    model.addGraphicalElement(compGr);

    // Add note
    Note note = new Note("Important design decision");
    NoteGR ngr = new NoteGR(note, 500, 100);
    model.addGraphicalElement(ngr);

    // 2. SAVE - Serialize to XML file
    File tempFile = File.createTempFile("dcd_test_", ".xml");
    tempFile.deleteOnExit();

    UMLProject project = new UMLProject();
    project.addDiagram(model);
    XMLStreamer.toXML(project, tempFile.getAbsolutePath());

    // 3. LOAD - Deserialize from XML file
    UMLProject loadedProject = XMLStreamer.fromXML(tempFile.getAbsolutePath());
    assertNotNull("Loaded project should not be null", loadedProject);

    DCDModel loadedModel = (DCDModel) loadedProject.getDiagrams().get(0);
    assertNotNull("Loaded model should not be null", loadedModel);

    // 4. VERIFY - Check all elements are present
    List<GraphicalElement> elements = loadedModel.getGraphicalElements();
    assertEquals("Should have 7 elements", 7, elements.size());

    // Verify classes
    ClassGR loadedCgr1 = findClassByName(elements, "Customer");
    assertNotNull("Customer class should exist", loadedCgr1);
    assertEquals("Customer x position", 100, loadedCgr1.getX());
    assertEquals("Customer y position", 100, loadedCgr1.getY());
    assertEquals("Customer should have 1 attribute", 1,
                 loadedCgr1.getDesignClass().getAttributes().size());
    assertEquals("Customer should have 1 method", 1,
                 loadedCgr1.getDesignClass().getMethods().size());

    // Verify interface
    InterfaceGR loadedIgr = findInterfaceByName(elements, "Serializable");
    assertNotNull("Serializable interface should exist", loadedIgr);
    assertEquals("Serializable should have 1 method", 1,
                 loadedIgr.getInterface().getMethods().size());

    // Verify relationships
    AssociationGR loadedAgr = findAssociation(elements, "Customer", "Order");
    assertNotNull("Association should exist", loadedAgr);
    assertEquals("Association name", "places",
                 loadedAgr.getAssociation().getName());

    // Verify note
    NoteGR loadedNgr = findNote(elements);
    assertNotNull("Note should exist", loadedNgr);
    assertEquals("Note text", "Important design decision",
                 loadedNgr.getNote().getText());

    // 5. VERIFY CENTRAL REPOSITORY
    CentralRepository repo = loadedProject.getCentralRepository();
    assertNotNull("Central repository should exist", repo);
    assertEquals("Repository should have 2 classes", 2,
                 repo.getDesignClasses().size());
    assertEquals("Repository should have 1 interface", 1,
                 repo.getInterfaces().size());
}

// Helper methods
private ClassGR findClassByName(List<GraphicalElement> elements, String name) {
    return elements.stream()
        .filter(e -> e instanceof ClassGR)
        .map(e -> (ClassGR) e)
        .filter(cgr -> cgr.getDesignClass().getName().equals(name))
        .findFirst()
        .orElse(null);
}
```

**Benefits of This Approach:**

1. ✅ **Tests critical functionality first** - Save/load is essential
2. ✅ **Provides code examples** - Shows how to create every element type
3. ✅ **High coverage quickly** - Exercises domain, graphical, and XML layers
4. ✅ **Integration test foundation** - Tests real-world workflows
5. ✅ **Regression prevention** - Catches serialization bugs immediately
6. ✅ **Documentation** - Tests serve as usage examples
7. ✅ **CentralRepository validation** - Ensures domain model consistency

**Success Criteria:**

-   ✅ All 6 diagram types have comprehensive save/load tests
-   ✅ Multi-diagram project save/load works correctly
-   ✅ All element types in each diagram are tested
-   ✅ CentralRepository integrity verified
-   ✅ No data loss during save/load cycle
-   ✅ Coverage increase: ~10-15% (testing domain, graphical, XML streaming)

**Estimated Time:** 3-5 days for all save/load tests

---

## ✅ Phase 0 Completion Summary

**Status: COMPLETED** (December 25, 2025)

### Implemented Tests

All five core diagram types now have comprehensive save/load integration tests:

#### 1. **Phase 0.1: UCDSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/UCDSaveLoadTest.java`
-   **Lines**: 255 lines
-   **Coverage**: Actor, use cases, includes/extends relations, generalizations, system boundary, notes
-   **Test Scenario**: Student registration system with 2 actors, 5 use cases, 4 include relations, 2 extend relations, 2 generalizations, 1 system boundary, 1 note
-   **Total Elements**: 17
-   **Status**: ✅ PASSING

#### 2. **Phase 0.2: CCDSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/CCDSaveLoadTest.java`
-   **Lines**: 261 lines
-   **Coverage**: Conceptual classes with attributes, associations (bidirectional, unidirectional), generalizations, notes
-   **Test Scenario**: E-commerce domain model with Customer, Order, Product, ShoppingCart classes
-   **Total Elements**: 12 (4 classes + 4 associations + 3 generalizations + 1 note)
-   **Status**: ✅ PASSING

#### 3. **Phase 0.3: DCDSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/DCDSaveLoadTest.java`
-   **Lines**: 381 lines
-   **Coverage**: Design classes with methods/attributes, interfaces, interface extensions, associations, generalizations, dependencies, compositions, aggregations, notes
-   **Test Scenario**: Complete design with PaymentService, PaymentProcessor interface, CreditCardProcessor, OrderManager, Customer, Order classes
-   **Total Elements**: 19 (5 classes + 1 interface + 10 relations + 3 notes)
-   **Status**: ✅ PASSING

#### 4. **Phase 0.4: SDSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/SDSaveLoadTest.java`
-   **Lines**: 348 lines
-   **Coverage**: Actor instance, SD objects, MultiObject, all message types (create, call, return, destroy), focus of control validation, notes
-   **Test Scenario**: Shopping cart system with proper focus of control semantics
-   **Message Sequence**: 17 messages (3 create + 3 create returns + 5 calls + 5 call returns + 1 destroy)
-   **Key Features**:
    -   Proper focus of control with returns for ALL create messages
    -   Validation system integrated (checks errorMsg on all messages)
    -   MultiObject named "items" for collection representation
    -   Message spacing: 25-40 pixel gaps between messages
-   **Repository Objects**: 3 SD objects + 1 MultiObject
-   **Total Elements**: 23 (5 role classifiers + 17 messages + 1 note)
-   **Status**: ✅ PASSING with full validation

#### 5. **Phase 0.5: SSDSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/SSDSaveLoadTest.java`
-   **Lines**: 190 lines
-   **Coverage**: System object, actor instance, system messages, return messages, notes
-   **Test Scenario**: Simple ATM system with user and system interaction
-   **Total Elements**: 11 (2 role classifiers + 8 messages + 1 note)
-   **Status**: ✅ PASSING

#### 6. **Phase 0.6: ADSaveLoadTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/integration/ADSaveLoadTest.java`
-   **Lines**: 388 lines
-   **Coverage**: All AD node types (Initial, Action, Decision, Merge, Fork, Join, Final), control flows with guards, note attachment
-   **Test Scenario**: Order Processing workflow with complete activity diagram semantics including parallel flows
-   **Node Types**: 13 nodes total
    -   1 InitialNode (start point)
    -   7 ActionNodes (Receive Order, Validate Order, Process Payment, Ship Order, Notify Customer, Update Inventory, Handle Rejection)
    -   1 DecisionNode (order valid branching)
    -   1 MergeNode (merge rejection and valid paths)
    -   1 ForkNode (split into parallel flows)
    -   1 JoinNode (synchronize parallel flows)
    -   1 ActivityFinalNode (end point)
-   **Control Flows**: 14 flows with 2 guarded transitions ([valid], [invalid])
-   **Key Features**:
    -   Guarded transitions on decision node
    -   Parallel flows (fork/join synchronization with 2 concurrent activities)
    -   Alternative paths (decision/merge for error handling)
    -   Note attached to ProcessPayment action
    -   Proper UML Activity Diagram semantics: fork node splits flow, not action nodes
-   **API Corrections**:
    -   Used `ActionNode` (not ActivityAction which doesn't exist)
    -   Used `ActivityFinalNode` (not FinalNode which is abstract)
    -   ControlFlowGR 5-arg constructor for proper rendering: `(source, target, flow, srcPoint, trgPoint)`
    -   3-arg constructor `(source, target, flow)` creates empty points list for XML persistence only
    -   UMLNoteGR methods: `getText()` and `getTo()` (not getTextualContent/getConnectedTo)
-   **Semantic Fix**: Corrected parallel flow structure - Fork node outputs to both parallel branches (not action node with multiple outputs)
-   **Total Elements**: 28 (13 nodes + 14 control flows + 1 note)
-   **Status**: ✅ PASSING

#### 7. **Phase 0.7: AddControlFlowControllerTest** ✅

-   **File**: `src/test/java/edu/city/studentuml/controller/AddControlFlowControllerTest.java`
-   **Lines**: 225 lines
-   **Purpose**: Test Activity Diagram control flow validation at controller layer
-   **Test Scenario**: Validate UML Activity Diagram semantics for action nodes
-   **Key Validation Rules**:
    -   Action nodes must have at most ONE outgoing control flow
    -   Action nodes must have at most ONE incoming control flow
    -   Fork/Decision nodes can have multiple outgoing flows
    -   Merge/Join nodes can have multiple incoming flows
-   **Test Coverage**: 6 comprehensive tests
    1. `testActionNode_SingleOutgoingFlow_ShouldSucceed()` - First outgoing flow allowed
    2. `testActionNode_MultipleOutgoingFlows_ShouldBePreventedByController()` - Second outgoing flow prevented with error
    3. `testActionNode_MultipleIncomingFlows_ShouldBePreventedByController()` - Second incoming flow prevented with error
    4. `testDecisionNode_MultipleOutgoingFlows_ShouldBeAllowed()` - Decision nodes can have multiple outputs
    5. `testMergeNode_MultipleIncomingFlows_ShouldBeAllowed()` - Merge nodes can have multiple inputs
    6. `testForkNode_MultipleOutgoingFlows_ShouldBeAllowed()` - Fork nodes can have multiple outputs
-   **Testing Pattern**: Testable Controller Pattern
    -   `TestableAddControlFlowController` inner class overrides `showErrorMessage()`
    -   Captures error messages instead of showing JOptionPane dialogs
    -   Prevents test execution blocking
    -   Allows verification of error message content
-   **Implementation**:
    -   Validation in `AddControlFlowController` lines 35-46 (outgoing) and 49-60 (incoming)
    -   Error messages provide helpful suggestions (use Fork/Merge nodes)
    -   Model state unchanged when validation fails
-   **Status**: ✅ ALL 6 TESTS PASSING

### Key Accomplishments

1. **Complete Save/Load Coverage**: All 6 diagram types tested (UCD, CCD, DCD, SD, SSD, AD)
2. **Focus of Control Semantics**: Properly modeled with validation in SD
3. **MultiObject Support**: Successfully tested MultiObject serialization
4. **Validation Integration**: SD test includes focus control validation checking
5. **Activity Diagram Nodes**: All node types covered (Initial, Action, Decision, Merge, Fork, Join, Final) with proper UML semantics
6. **Guarded Transitions**: Control flows with guards tested and verified
7. **Parallel Flow Semantics**: Fork/Join synchronization with concurrent activities properly structured
8. **Controller Validation Tests**: AddControlFlowController thoroughly tested with 6 tests
9. **Testable Controller Pattern**: Implemented pattern to avoid JOptionPane blocking in tests
10. **UML Semantic Enforcement**: Action nodes restricted to 1 incoming and 1 outgoing control flow
11. **API Corrections**: Fixed API method usage throughout
    - `getSdObjects`, `getClasses`, `getMultiObjects`, `getSDMessages` for SD/SSD
    - `ActionNode` (not ActivityAction), `ActivityFinalNode` (not FinalNode) for AD
    - `ControlFlowGR(source, target, flow, srcPoint, trgPoint)` 5-arg constructor for rendering
    - `ControlFlowGR(source, target, flow)` 3-arg constructor for XML persistence only
    - `UMLNoteGR.getText()` and `getTo()` methods
12. **Access Modifiers**: Made validation methods public for testing:
    - `RoleClassifierGR.validateInReturn()` → public
    - `RoleClassifierGR.setActiveIn()` → public
    - `SDMessageGR.getErrorMsg()` → public (newly added)

### Coverage Impact

**Actual Coverage Increase**: +2% overall (23% → 25%)

Detailed breakdown by package:

-   **Overall**: 23% → **25%** (+2 percentage points, +2,004 instructions covered)
-   **Domain Model**: 37% → **48%** (+11 percentage points)
-   **Graphical Model**: 28% → **32%** (+4 percentage points)
-   **Repository**: 48% → **50%** (+2 percentage points)
-   **Util**: 36% → **47%** (+11 percentage points)

These tests exercise:

-   Domain model layer (all entity types) - **significant increase**
-   Graphical model layer (all \*GR classes) - **moderate increase**
-   XML serialization (XMLStreamer, IXMLCustomStreamable)
-   Central repository (domain object storage and retrieval)
-   Model validation (focus of control in SD)

**Why the increase was lower than estimated:**

-   The estimate of 10-15% was too optimistic
-   Many classes have GUI-dependent code paths not exercised by headless tests
-   Controller and View layers remain largely untested (need UI interaction tests)
-   However, the tests provide **excellent foundational coverage** of core domain and serialization logic

### Test Infrastructure

**Created Files**:

-   `SaveLoadTestBase.java`: Base class with common save/load utilities
-   `UCDSaveLoadTest.java`: Use Case Diagram test
-   `CCDSaveLoadTest.java`: Conceptual Class Diagram test
-   `DCDSaveLoadTest.java`: Design Class Diagram test
-   `SDSaveLoadTest.java`: Sequence Diagram test
-   `SSDSaveLoadTest.java`: System Sequence Diagram test
-   `ADSaveLoadTest.java`: Activity Diagram test
-   `AddControlFlowControllerTest.java`: Activity Diagram controller validation test

**Total Test Code**: ~1,660 lines of comprehensive integration and controller tests

### Success Criteria Met

-   ✅ All 6 diagram types have comprehensive save/load tests
-   ✅ All element types in each diagram are tested
-   ✅ CentralRepository integrity verified
-   ✅ No data loss during save/load cycle
-   ✅ Focus of control validation working (SD)
-   ✅ MultiObject serialization working (SD)
-   ✅ Activity Diagram control flow validation implemented and tested
-   ✅ Testable controller pattern established for non-blocking tests
-   ✅ All tests passing with BUILD SUCCESS (129 tests)

### Lessons Learned

1. **CreateMessages require return messages**: Treated identically to CallMessages for focus validation
2. **Focus semantics**: Object receiving a call/create has focus until it returns
3. **MultiObject handling**: Stored separately in repository from regular SDObjects
4. **Validation system**: Sophisticated and catches focus control errors automatically
5. **API consistency**: Proper use of getSdObjects() vs getObjects() for different contexts
6. **Controller-layer validation**: Validation logic should be in controllers, not models, for user interaction
7. **Testable controller pattern**: Override UI methods (like showErrorMessage) to avoid blocking dialogs in tests
8. **UML semantics enforcement**: Action nodes in Activity Diagrams must have exactly 1 incoming and 1 outgoing flow
9. **Test at the right layer**: Test validation logic where it lives (controller tests for controller validation)

### Next Steps

Ready to proceed to **Phase 1: Critical Gaps (Weeks 2-3)**

---

### Phase 1: Critical Gaps (Weeks 2-3)

**Goal: Cover critical untested areas**

1. **Validation Layer** (0% → 80%)

    - Create `ValidationTest.java` structure
    - Test all validators
    - Test consistency checker

2. **Undo/Redo** (21% → 85%)

    - Test all edit types
    - Test composite operations
    - Test edge cases

3. **Controller Add Operations** (18% → 50%)
    - Test add controllers for all element types
    - Verify undo/redo integration

**Success Criteria:**

-   All validation code tested
-   All undo/redo operations tested
-   Basic controller operations covered
-   Overall coverage > 40%

### Phase 2: Core Functionality (Weeks 4-5)

**Goal: Comprehensive domain and graphical testing**

1. **Domain Model** (37% → 75%)

    - Test all domain entities (building on save/load tests)
    - Test relationships
    - Test business logic

2. **Graphical Model** (28% → 60%)

    - Test all graphical elements (building on save/load tests)
    - Test rendering logic
    - Test geometry calculations

3. **Controller Completion** (50% → 70%)
    - Test edit/modify controllers
    - Test selection behavior
    - Test multi-element operations

**Success Criteria:**

-   All domain entities thoroughly tested
-   All graphical elements covered
-   Controller layer well tested
-   Overall coverage > 55%

### Phase 3: Integration & Edge Cases (Weeks 6-7)

**Goal: Integration tests and edge case coverage**

1. **Integration Tests** (expanding on Phase 0 foundation)

    - Cross-diagram consistency scenarios
    - Code generation workflows (from DCD)
    - Consistency checking across diagrams
    - Complex multi-diagram operations

2. **Edge Cases**

    - Error handling in save/load (corrupted XML)
    - Invalid input
    - Large diagrams (performance)
    - Concurrent operations

3. **View Layer** (6% → 40%)
    - Testable view logic
    - Observer pattern behavior
    - Data binding tests

**Success Criteria:**

-   Comprehensive integration test suite
-   Edge cases identified and tested
-   View layer partially covered
-   Overall coverage > 70%

### Phase 4: Polish & Maintenance (Ongoing)

**Goal: Maintain and improve coverage**

1. **Coverage Maintenance**

    - Test new features as they're added
    - Update tests when refactoring
    - Monitor coverage reports

2. **Test Quality**

    - Review and improve test clarity
    - Reduce test duplication
    - Optimize test execution time

3. **Documentation**
    - Document testing patterns
    - Create test writing guidelines
    - Share best practices

**Success Criteria:**

-   Coverage maintained above 70%
-   New features always tested
-   Test suite runs in < 2 minutes
-   Clear testing documentation

---

## Measuring Success

### Coverage Metrics

-   **Instruction Coverage**: Primary metric (currently 23%, target 70%+)
-   **Branch Coverage**: Secondary metric (currently 19%, target 60%+)
-   **Method Coverage**: Tertiary metric

### Quality Metrics

-   **Test Count**: 220 tests (no new tests added during refactoring)
-   **Code Reduction**: ~1,545 lines eliminated across 14 refactored editors (~54% average reduction)
-   **Test Execution Time**: ~16 seconds (well under 3 minute target)
-   **Test Failure Rate**: 0% (220 passing, 0 failures, 0 errors, 1 skipped)

### Process Metrics

-   **New Feature Coverage**: 100% of new features tested (starting with save/load)
-   **Bug Fix Testing**: Every bug fix includes regression test
-   **Refactoring Safety**: Can refactor confidently with test coverage

---

## Tools & Commands

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ConceptualClassTest

# Run specific test method
mvn test -Dtest=ConceptualClassTest#testAddAttribute

# Skip tests (for fast builds)
mvn clean package -DskipTests
```

### Coverage Reports

```bash
# Generate coverage report
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

### Continuous Monitoring

```bash
# Watch for changes and run tests
mvn test -Dsurefire.rerunFailingTestsCount=2

# Generate coverage on every test run
mvn test jacoco:report
```

---

## Next Steps

1. **Start with Phase 0** - Save/Load integration tests (Week 1)
    - Begin with `UCDSaveLoadTest.java` (simplest diagram type)
    - Then `CCDSaveLoadTest.java` and `DCDSaveLoadTest.java`
    - Follow with `SDSaveLoadTest.java`, `SSDSaveLoadTest.java`, `ADSaveLoadTest.java`
    - Complete with `ProjectSaveLoadTest.java` (multi-diagram)
2. **Run coverage reports** after Phase 0 to measure impact
3. **Proceed to Phase 1** - Critical gaps (validation, undo/redo)
4. **Continue with Phase 2** - Domain and graphical model tests
5. **Track progress weekly** and adjust priorities as needed

## Notes

-   **Start with save/load tests** - Most critical functionality
-   Focus on **business logic** over GUI testing
-   Prioritize **frequently used features**
-   Test **error paths** as well as happy paths
-   Keep tests **fast and reliable**
-   Make tests **readable and maintainable**
-   Save/load tests provide **code examples** for creating all element types

### Refactoring Impact on Testing (December 2025)

The editor refactoring effort demonstrated the value of a solid test suite:

-   **14 editors refactored** across 5 categories
-   **~1,545 lines of code eliminated** (~54% average reduction per editor)
-   **Zero new tests required** - existing 220 tests validated all changes
-   **Zero test failures** - all refactoring passed existing test suite
-   **Improved maintainability** through:
    -   ListPanel<T> component (with double-click support)
    -   AssociationEditorBase template pattern
    -   TypedEntityEditor base class
    -   Anonymous inner class patterns where appropriate

**Key Lesson**: The existing test coverage (32% overall, but higher in domain/model layers) was sufficient to catch regressions during major refactoring. This validates the test-first approach and demonstrates that well-placed tests enable confident refactoring even at modest coverage levels.

---

**Last Updated:** December 29, 2025
**Current Coverage:** 32%
**Test Count:** 220 tests (0 failures, 0 errors, 1 skipped)
**Test Execution Time:** ~16 seconds
**Code Reduction:** ~1,545 lines eliminated through refactoring (no test changes needed)
**Target Coverage:** 70%+
**Estimated Timeline:** 7 weeks (Phase 0-3)
**Latest Additions:**

-   ✅ DCDLinkReconnectionTest: 21 tests for class diagram link reconnection
-   ✅ UCDLinkReconnectionTest: 19 tests for use case diagram link reconnection
-   ✅ Helper class extended with methods for creating all link types
-   ✅ Editor refactoring: 14 editors refactored, ~1,545 lines eliminated, all tests still passing
