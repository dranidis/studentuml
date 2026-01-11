# Manual Test Cases for StudentUML

## Copy/Paste Functionality

### Test Case 1: Copy/Paste Classes WITHOUT Selecting Association

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste (Ctrl+C / Ctrl+V)  
**Diagram Type:** Design Class Diagram (DCD)

**Test Setup:**

1. Create a new Design Class Diagram
2. Add Class A
3. Add Class B
4. Create an Association from A to B (A -> B)

**Test Steps:**

1. Select both Class A and Class B (but **NOT** the association)
2. Press Ctrl+C to copy
3. Press Ctrl+V to paste

**Expected Result:**

-   Classes A and B should be pasted at an offset (20 pixels right and down)
-   The pasted classes should be independent copies
-   **NO association** should be created between the pasted classes
-   The original association should remain connecting the original classes only

**Status:** ✅ **PASS** - Working as designed

**Notes:**

-   Relationships are only copied if explicitly selected
-   This prevents unwanted relationship duplication
-   Users must explicitly select relationships they want to copy

---

### Test Case 2: Copy/Paste Classes WITH Explicitly Selected Association

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste (Ctrl+C / Ctrl+V)  
**Diagram Type:** Design Class Diagram (DCD)

**Test Setup:**

1. Create a new Design Class Diagram
2. Add Class A
3. Add Class B
4. Create an Association from A to B (A -> B)

**Test Steps:**

1. Select both Class A and Class B **AND the association** (hold Shift/Ctrl to multi-select)
2. Press Ctrl+C to copy
3. Press Ctrl+V to paste

**Expected Result:**

-   Classes A and B should be pasted at an offset (20 pixels right and down)
-   The pasted classes should be independent copies
-   **A new association** should be created between the pasted classes (pasted A -> pasted B)
-   The original association should remain connecting the original classes
-   Association properties (name, direction, roles, multiplicity) should be preserved

**Status:** ✅ **PASS** - Fixed and working correctly

**Notes:**

-   The pasted association connects the **pasted classes**, not the original ones
-   All association properties are copied (name, direction, roles, multiplicities)
-   Works with all relationship types: Association, Aggregation, Generalization, Dependency, Realization
-   Undo/redo is fully supported - single undo removes the entire paste operation

---

### Test Case 3: Copy/Paste Association with Undo

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste Undo for Associations  
**Diagram Type:** Design Class Diagram (DCD)

**Test Setup:**

1. Create a new Design Class Diagram
2. Add Class A
3. Add Class B
4. Create an Association from A to B (A -> B)

**Test Steps:**

1. Select both Class A, Class B, and the association
2. Press Ctrl+C to copy
3. Press Ctrl+V to paste
4. Press Ctrl+Z to undo

**Expected Result:**

-   After paste: Two pasted classes with a new association between them should appear
-   After undo: The pasted classes and pasted association should be removed
-   **The original association between A and B should remain intact**

**Status:** ✅ **PASS** - Fixed

**Implementation Details:**

-   **Issue**: When undoing a paste operation with associations, the original association was incorrectly deleted
-   **Root Cause**:
    1. Pasted links reuse the same domain object (Association, Aggregation, etc.) as the original
    2. When removing a pasted ClassGR, `removeClass()` cascades to remove ALL associations connected to that domain DesignClass
    3. Since both original and pasted AssociationGR reference the same domain Association (which references the same DesignClasses), both were removed
-   **Solution**:
    1. Modified `createLinkForPastedElements()` to reuse domain objects instead of creating new ones
    2. Added duplicate checks in `addAssociation()`, `addAggregation()`, etc. to prevent adding the same domain object twice
    3. Added reference counting in `removeAssociation()`, `removeGeneralization()`, etc. to only remove from repository when last graphical reference is deleted
    4. **Critical fix**: Changed `getClassGRAssociationGRs()` and similar methods to filter by graphical endpoints instead of domain classifiers, ensuring cascading deletes only affect graphically connected elements
-   **Test Coverage**: `DCDSelectionControllerTest.testCopyPasteAssociationUndo()`

---

### Test Case 4: Copy/Paste Between Different Diagram Types

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste Cross-Diagram Prevention  
**Diagram Types:** Any combination (e.g., DCD to CCD, UCD to SD, etc.)

**Test Setup:**

1. Create a Design Class Diagram (DCD)
2. Add Class A and Class B
3. Create a second diagram of a different type (e.g., Conceptual Class Diagram - CCD)

**Test Steps:**

1. In the DCD, select Class A and Class B
2. Press Ctrl+C to copy
3. Switch to the CCD diagram
4. Press Ctrl+V to paste

**Expected Result:**

-   Nothing should be pasted
-   A warning should be logged: "Cannot paste from DCDModel to CCDModel - diagram types must match"
-   The target diagram should remain unchanged

**Status:** ✅ **PASS** - Implemented and working

**Notes:**

-   Prevents incompatible elements from being pasted across different diagram types
-   Each diagram type has specific element types (e.g., Use Cases in UCD, Classes in DCD)
-   Pasting only works within the same diagram type
-   This prevents data corruption and invalid diagram states

---

### Test Case 4: Copy/Paste Messages in Sequence Diagrams

**Test Date:** December 22, 2025  
**Feature:** Copy/Paste Messages (Call/Return) in Sequence Diagrams  
**Diagram Type:** Sequence Diagram (SD)

**Test Setup:**

1. Create a new Sequence Diagram
2. Add two SD Objects (e.g., Object A and Object B)
3. Create a Call Message from A to B
4. Create a Return Message from B to A

**Test Steps:**

1. Select both SD Objects (A and B) **AND** both messages (call and return)
2. Press Ctrl+C to copy
3. Move mouse to a different location in the diagram
4. Press Ctrl+V to paste

**Expected Result:**

-   Two new SD Objects should be pasted at the mouse cursor location
-   A new Call Message should connect the pasted objects (pasted A -> pasted B)
-   A new Return Message should connect the pasted objects (pasted B -> pasted A)
-   **Messages should NOT connect to the original objects**
-   The pasted messages should maintain their Y positions relative to the objects

**Status:** ✅ **PASS** - Messages paste correctly with remapped endpoints

**Implementation Details:**

-   SD messages (CallMessageGR, ReturnMessageGR) are now skipped in first pass along with LinkGR
-   Second pass detects SDMessageGR instances and remaps their endpoints
-   New method `createMessageForPastedElements()` creates messages with cloned source/target
-   Message properties preserved: name, parameters, return values, Y coordinate
-   Automated test coverage: SDSelectionControllerTest (4 tests, all passing)

---

## Activity Diagram Semantic Validation

### Issue: Multiple Control Flows to/from Action Nodes

**Date**: December 25, 2025
**Status**: ✅ **FIXED**

**Description**:
The system was allowing action nodes to have multiple outgoing and incoming control flows, which violates UML Activity Diagram semantics. According to UML specification:

-   Action nodes should have **at most one** outgoing control flow
-   Action nodes should have **at most one** incoming control flow
-   Only control flow nodes (Decision, Fork) can have multiple outgoing flows
-   Only control flow nodes (Merge, Join) can have multiple incoming flows

**RESOLUTION** (December 25, 2025):
✅ **FIXED** - Full validation implemented in the controller layer for both incoming and outgoing flows.

**Implementation Details**:

-   **File**: `src/main/java/edu/city/studentuml/controller/AddControlFlowController.java`
-   **Location**: `addFlow()` method (lines 32-60)

**Validation Logic**:

1. **Outgoing Flow Validation** (lines 35-46):

    - Checks if source is ActionNodeGR and already has outgoing edges
    - Shows error dialog if trying to add second outgoing flow
    - Suggests using Fork node for parallel flows

2. **Incoming Flow Validation** (lines 49-60):
    - Checks if target is ActionNodeGR and already has incoming edges
    - Shows error dialog if trying to add second incoming flow
    - Suggests using Merge node for merging multiple flows

**Architecture Decision**:

-   Validation placed in **controller layer** (not model layer) for consistency
-   Follows same pattern as other AD validations in `AddEdgeController` and subclasses
-   Provides immediate user feedback through error dialogs
-   Model layer remains focused on data management
-   Prevents invalid diagrams from being created through the UI

**Test Coverage**:

-   `AddControlFlowControllerTest.testActionNode_SingleOutgoingFlow_ShouldSucceed()` - Verifies first outgoing flow is allowed
-   `AddControlFlowControllerTest.testActionNode_MultipleOutgoingFlows_ShouldBePreventedByController()` - Confirms second outgoing flow is prevented
-   `AddControlFlowControllerTest.testActionNode_MultipleIncomingFlows_ShouldBePreventedByController()` - Confirms second incoming flow is prevented
-   `AddControlFlowControllerTest.testCreation()` - Basic controller instantiation
-   All tests use `TestableAddControlFlowController` that overrides `showErrorMessage()` to avoid blocking JOptionPane dialogs during tests

**Error Messages**:

For **outgoing** flows:

```
Action node '[name]' already has 1 outgoing control flow(s).

UML Activity Diagram semantics allow at most ONE outgoing flow from action nodes.
Use a Fork node if you need parallel flows.
```

For **incoming** flows:

```
Action node '[name]' already has 1 incoming control flow(s).

UML Activity Diagram semantics allow at most ONE incoming flow to action nodes.
Use a Merge node if you need to merge multiple flows.
```

**Impact**:

-   ✅ Users prevented from creating invalid Activity Diagrams through the UI
-   ✅ Clear error dialogs guide users to use proper control flow nodes (Fork/Merge)
-   ✅ Maintains UML Activity Diagram semantic correctness
-   ✅ Consistent user experience with other AD validation errors
-   ✅ No JOptionPane blocking during automated tests (testable controller pattern)
-   ✅ Complete enforcement of UML semantics for action nodes
