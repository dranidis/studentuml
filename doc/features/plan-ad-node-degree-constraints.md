# Plan: Activity Diagram Node Degree Constraints

## Investigation

This feature enforces control-flow degree constraints in Activity Diagrams:

-   Initial nodes must have exactly one outgoing Control Flow
-   Final nodes (Activity Final and Flow Final) must have exactly one incoming Control Flow

Violations should be prevented at creation time with user-facing error messages and detected during consistency checks.

### Current Implementation Analysis

**Key Classes to Investigate:**

Domain Model:

-   `InitialNode` - Starting point of activity
-   `ActivityFinalNode` - Termination of entire activity
-   `FlowFinalNode` - Termination of a single flow
-   `ControlFlow` - Edge representing flow between nodes
-   `NodeComponent` - Base class for activity nodes

Graphical Representation:

-   `InitialNodeGR` - Graphical representation of initial node
-   `ActivityFinalNodeGR` - Graphical representation of activity final node
-   `FlowFinalNodeGR` - Graphical representation of flow final node
-   `ControlFlowGR` - Graphical representation of control flow

Controllers:

-   `AddControlFlowController` - Handles creation of control flows
-   Edge reconnection logic in `EdgeGR` and subclasses

**Areas to Explore:**

1. How control flows are currently created between nodes
2. How reconnection of edges is handled
3. Whether nodes track incoming/outgoing edges
4. Current validation mechanisms (if any)
5. Consistency checking rules for activity diagrams

## Affected Components

### Components to Modify

-   **`AddControlFlowController`** - Add validation before creating control flow

    -   Check source node degree before creating outgoing edge
    -   Check target node degree before creating incoming edge
    -   Show appropriate error message if constraint violated

-   **`ControlFlowGR` / `EdgeGR`** - Add reconnection validation

    -   Prevent reconnecting edges if it violates degree constraints
    -   Check both source and target nodes during reconnection

-   **Domain Model Classes** - May need helper methods

    -   `InitialNode.canAddOutgoingEdge()` - Check if can add outgoing
    -   `ActivityFinalNode.canAddIncomingEdge()` - Check if can add incoming
    -   `FlowFinalNode.canAddIncomingEdge()` - Check if can add incoming
    -   Or use `NodeComponent` base class methods

-   **Consistency Checking** - Add Prolog rules
    -   Rule to detect initial nodes with != 1 outgoing flows
    -   Rule to detect final nodes with != 1 incoming flows

### New Components to Create

-   **Validation utility methods** (possibly in `NodeComponent` or separate validator)
    -   Methods to check degree constraints
    -   Methods to count incoming/outgoing edges for specific node types

## Design Decisions

### 1. Validation Strategy

**Option A**: Validate in controller before creating edge
**Option B**: Validate in domain model when edge is added
**Option C**: Both - controller for user feedback, model for integrity

**Decision**: Option C - Defense in depth

-   Controller validation provides immediate user feedback
-   Model validation ensures integrity even if called from other paths

### 2. Error Message Strategy

Show clear, actionable error messages:

-   "Initial nodes can have only one outgoing control flow"
-   "Final nodes can have only one incoming control flow"
-   Use `JOptionPane.showMessageDialog()` with ERROR_MESSAGE type

### 3. Reconnection Handling

When reconnecting an edge:

-   Check if new source/target would violate constraints
-   If violation: prevent reconnection and show error
-   If valid: allow reconnection

### 4. Consistency Checking

Add Prolog rules to detect violations in existing diagrams:

-   Report nodes with incorrect degree
-   Include node name/ID in error message for easy identification

## TODO Tasks

### Phase 1: Investigation ✅ COMPLETE

-   [x] Examined `InitialNode`, `ActivityFinalNode`, `FlowFinalNode` classes - extend `ControlNode` and `FinalNode`
-   [x] Examined `ControlFlow` and how edges are tracked - tracked in `NodeComponent` base class
-   [x] Found `NodeComponent` tracks incoming/outgoing edges with:
    -   `getNumberOfIncomingEdges()`, `getNumberOfOutgoingEdges()`
    -   `getIncomingEdges()`, `getOutgoingEdges()`
-   [x] Examined `AddControlFlowController` - where edges are created in `addFlow()` method
-   [x] Found existing validation in `AddEdgeController.addEdge()`:
    -   Already prevents incoming edges to InitialNode
    -   Already prevents outgoing edges from FinalNode
    -   Uses `showErrorMessage()` method for user feedback
-   [x] Need to add degree constraints (exactly 1) in addition to existing "no incoming/outgoing" rules

**Key Findings:**

-   Validation already exists but only prevents ANY incoming/outgoing (not degree=1)
-   Must add check: if InitialNode already has 1 outgoing, block adding another
-   Must add check: if FinalNode already has 1 incoming, block adding another
-   Pattern: Check count, show error, call `setSelectionMode()`, return

### Phase 2: Domain Model Enhancement (SKIPPED)

-   [x] No changes needed - `NodeComponent` already has all necessary methods:
    -   `getNumberOfIncomingEdges()` / `getNumberOfOutgoingEdges()`
    -   Edge tracking is already implemented in base class

### Phase 3: Controller Validation ✅ COMPLETE

-   [x] Modified `AddEdgeController.addEdge()` to validate before creating edge
-   [x] Added validation check for source node (if InitialNode, check outgoing count)
-   [x] Added validation check for target node (if FinalNode, check incoming count)
-   [x] Show appropriate error dialog when validation fails:
    -   "Initial node can have only one outgoing control flow!"
    -   "Final nodes can have only one incoming control flow!"
-   [x] Prevent edge creation when validation fails (already handled by existing pattern)

### Phase 4: Documentation ✅ COMPLETE

-   [x] Updated CHANGELOG.md with feature description
-   [x] Removed feature from features.md
-   [x] Completed implementation summary

## Implementation Summary

This feature enforces UML semantic constraints for Initial and Final nodes in Activity Diagrams by preventing violations at creation time.

**Implementation Approach:**

Added validation logic to `AddEdgeController.addEdge()` method that checks degree constraints before creating any edge:

1. **Initial Node Constraint**: When the source node is an `InitialNode`, check if it already has 1 outgoing edge. If so, block creation and show error: "Initial node can have only one outgoing control flow!"

2. **Final Node Constraint**: When the target node is a `FinalNodeGR` (covers both `ActivityFinalNode` and `FlowFinalNode`), check if it already has 1 incoming edge. If so, block creation and show error: "Final nodes can have only one incoming control flow!"

**Key Design Decisions:**

-   **Validation location**: Added to `AddEdgeController` (base class) rather than `AddControlFlowController` to ensure validation applies to all edge types
-   **Domain model unchanged**: No changes needed - `NodeComponent` already provides `getNumberOfIncomingEdges()` and `getNumberOfOutgoingEdges()`
-   **User feedback**: Uses existing `showErrorMessage()` pattern with clear, actionable error messages
-   **Failure mode**: Prevents edge creation and returns to selection mode when constraint violated

**Files Modified:**

-   `src/main/java/edu/city/studentuml/controller/AddEdgeController.java` - Added degree constraint validation

**Testing:**

Manual testing confirmed:

-   Initial nodes are limited to one outgoing control flow
-   Final nodes are limited to one incoming control flow
-   Clear error messages displayed when constraints violated
-   Diagram remains in consistent state after validation failure

**Future Enhancements:**

-   Edge reconnection validation (when reconnection feature is implemented)
-   Prolog consistency checking rules for existing diagrams
-   XML load validation for diagrams created before this constraint
