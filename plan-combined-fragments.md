# Plan: Support Combined Fragments in Sequence Diagrams

## Implementation Status

**Approach:** Minimal implementation starting with OPT operator only

### Phase 1: Core Foundation (COMPLETED ✅)

**Completed Tasks:**

✅ **Task 1: Create InteractionOperator enum**

-   Defined OPT, ALT, LOOP operators with display names
-   File: `src/main/java/edu/city/studentuml/model/domain/InteractionOperator.java`

✅ **Task 2: Create CombinedFragment domain class**

-   Properties: operator, guardCondition, rank, height
-   Reflection-compatible methods (getName(), getOperator(), getRank()) for consistency checker
-   Implements Serializable for persistence
-   File: `src/main/java/edu/city/studentuml/model/domain/CombinedFragment.java`

✅ **Task 3: Create CombinedFragmentGR graphical class**

-   Extends GraphicalElement with full implementation
-   Rounded rectangle rendering with antialiasing
-   Pentagon operator label in upper left corner
-   Guard condition text display below pentagon
-   XML serialization support (streamToXML/streamFromXML)
-   Implements move(), clone(), contains() methods
-   File: `src/main/java/edu/city/studentuml/model/graphical/CombinedFragmentGR.java`

✅ **Task 4: Create comprehensive tests**

-   CombinedFragmentTest: 5 tests for domain model functionality
-   CombinedFragmentGRTest: 6 tests for graphical rendering and bounds
-   All 337 project tests passing ✅

**Test Results:** All tests passing (337 tests, 1 skipped)

### Phase 1 Integration (COMPLETED ✅)

✅ **Task 8:** Add CombinedFragment to SDModel's graphical elements collection  
✅ **Task 9:** Create AddCombinedFragmentController for UI creation tool  
✅ **Task 10:** Add XML serialization to ObjectFactory for diagram save/load  
✅ **Task 11:** Edit functionality with dialog for operator, guard, and loop iterations  
✅ **Task 12:** 8-handle resize support with cursor feedback (N, S, E, W, NE, NW, SE, SW)  
✅ **Task 13:** Copy/paste support preserving Y-coordinates for SD time axis  
✅ **Task 14:** Comprehensive unit tests (13 tests total)  
✅ **Task 15:** Manual testing - All edit operations verified  
✅ **Task 16:** Documentation updated (CHANGELOG.md and features.md)

**Test Results:** All 345 tests passing ✅

### Phase 1 Implementation Complete - OPT, ALT, LOOP Operators Fully Supported

The following features have been successfully implemented:

**Editing and Interaction:**

-   ✅ Double-click edit dialog to modify operator type, guard condition, and loop iterations
-   ✅ Default creation of OPT fragments with empty guard
-   ✅ Automatic guard bracket addition (adds `[...]` if missing)
-   ✅ Loop iteration support following UML 2.x spec: `loop`, `loop(3)`, `loop(0,2)`, `loop(3,*)`
-   ✅ Undo/redo support for all edit operations
-   ✅ Copy/paste support with Y-coordinate preservation

**UI/Visual:**

-   ✅ Pentagon operator label showing operator name and loop iterations
-   ✅ Transparent background to show contained messages
-   ✅ 8-handle resize support with proper cursor feedback
-   ✅ Toolbar button with generic icon

**Persistence:**

-   ✅ Complete XML serialization/deserialization
-   ✅ Backward compatibility maintained

### Phase 2: Additional Features

**UI/Visual Enhancements:**

-   ✅ **Create custom icon for combined fragment toolbar button** - Completed
    -   Created 24x24 GIF icon showing rounded rectangle with pentagon in upper-left corner
    -   Follows standard UML combined fragment visual notation
    -   Icon file: `src/main/resources/images/fragment.gif`
    -   Updated SDToolbar.java to use `fragment.gif` instead of generic `note.gif`
    -   Icon is clear and recognizable at toolbar size (20px height)
-   ✅ **ALT operator with multiple operands and draggable separators** - Completed
    -   Created `Operand` domain class with guard condition and height ratio
    -   Added operands list to `CombinedFragment` with methods: `addOperand()`, `removeOperand()`, `getOperands()`, `clearOperands()`
    -   `CombinedFragmentGR` renders dashed horizontal separator lines between operands
    -   Each operand displays its guard condition in its section
    -   Fragment height divided proportionally based on operand height ratios (default 1.0 for equal distribution)
    -   XML serialization/deserialization support for operands and height ratios (backward compatible)
    -   **Fully functional draggable separators:**
        -   Height ratios stored per operand for proportional sizing
        -   API methods: `getSeparatorIndexAt()`, `startDraggingSeparator()`, `dragSeparator()`, `finishDraggingSeparator()`
        -   Separator hit detection with 5-pixel tolerance
        -   **Minimum height enforcement:** Each operand section must be at least `MINIMUM_HEIGHT` (40 pixels) - prevents collapsing operands below fragment minimum
        -   Smooth dragging updates adjacent operand heights dynamically
        -   **UI Integration complete:** `SDSelectionController` intercepts separator clicks and handles drag/release
        -   Mouse event handling: press→drag→release cycle fully wired
        -   Real-time visual feedback during dragging
        -   **Undo/Redo support:** Separator dragging creates `DragSeparatorEdit` undoable edit that captures old/new height ratios
    -   **Interactive UI for operand management:**
        -   ALT operands panel with scrollable list in `CombinedFragmentEditor`
        -   Add button to create new operands with guard condition input dialog
        -   Edit button to modify selected operand's guard condition
        -   Remove button to delete selected operand with confirmation
        -   Automatic bracket addition `[...]` for guard conditions
        -   Panel visibility controlled by operator selection (only visible for ALT)
        -   Guard condition field disabled for ALT (operands used instead)
        -   Empty operand list warning (with option to continue)
        -   Operands cleared automatically when switching away from ALT operator
    -   Test coverage: 5 tests for Operand, 7 tests for ALT with multiple operands, 1 integration test
-   [ ] Message containment within fragments (visual feedback)
-   [ ] Support for nested fragments

**Advanced Layout and Interaction:**

-   ✅ **Auto-resize fragment to fully contain messages within its Y range** - Completed
    -   ✅ Automatically adjust width to span from leftmost to rightmost message endpoint
    -   ✅ Automatically adjust starting X position (left edge) to include all message starting points
    -   ✅ Include return messages when a call message is within the fragment
    -   ✅ Include created objects in bounds calculation for CreateMessage
    -   ✅ Apply extra horizontal padding (30px) when fragment contains nested fragments (vs 10px for messages)
    -   ✅ Auto-resize only when Control key is pressed during manual resize
    -   ✅ Auto-resize only the specific fragment being resized (not all fragments)
    -   ✅ Auto-resize on fragment creation with allowShrink=true for initial sizing
    -   ✅ Prevent fragments from having negative coordinates (clamped to 0,0)
    -   ❌ Disabled auto-resize when messages are added/moved
    -   ❌ Disabled auto-resize when lifelines (objects) are moved
    -   ❌ Disabled auto-resize when fragments are moved
    -   Implementation: `CombinedFragmentGR.autoResizeToMessages()` method
    -   Called on fragment creation (`AddCombinedFragmentController`)
    -   Called when Control+resize handle is released (`SelectionController.myMouseReleased()`)
    -   Test coverage: 9 tests in `CombinedFragmentAutoResizeTest` covering all scenarios
    -   Test updated to reflect new behavior (no auto-resize on object movement)

### Phase 3: Advanced Operators (Future)

Additional operators to implement:

-   **par** (parallel execution)
-   **break** (exception handling)
-   **critical** (critical region)
-   **neg**, **assert**, **strict**, **seq**, **ignore**, **consider**

---

## Original Investigation and Planning (Below this line)

## Investigation

### Problem Statement

StudentUML sequence diagrams currently only support linear message flows. Combined fragments are essential UML 2.x constructs that allow modeling of:

-   **Conditionals** (alt, opt)
-   **Iteration** (loop)
-   **Parallel execution** (par)
-   **Exception handling** (break)
-   **Critical sections** (critical)

Without these, sequence diagrams cannot represent realistic control flow in complex interactions.

### Scope for Initial Implementation (Phase 1)

Focus on the three most common operators:

1. **alt** (alternatives) - if-then-else conditional paths
2. **opt** (option) - optional execution (if-then)
3. **loop** (loop) - repeated execution with guard condition

Additional operators will be added in Phase 2.

### Architecture Investigation

#### Current Sequence Diagram Structure

Need to investigate:

1. How are sequence diagram elements currently structured?
2. What is the parent-child relationship model?
3. How are messages positioned and rendered?
4. How does the Y-axis (time) coordinate system work?
5. What is the interaction between domain and graphical layers?

#### Key Files to Investigate

-   `src/main/java/edu/city/studentuml/model/domain/` - Domain model for SD elements
-   `src/main/java/edu/city/studentuml/model/graphical/` - Graphical SD elements
-   `src/main/java/edu/city/studentuml/view/SDView.java` - Sequence diagram rendering
-   `src/main/java/edu/city/studentuml/controller/` - SD controllers
-   Existing message classes: `CallMessageGR`, `ReturnMessageGR`, `CreateMessageGR`
-   Existing object classes: `SDObjectGR`, `RoleClassifierGR`

### Investigation Findings

[To be filled during investigation]

## Affected Components

[To be filled after investigation]

## Design Decisions

[To be filled after investigation]

## TODO Tasks (Phase 1: alt, opt, loop)

### Investigation Tasks

-   [ ] Task 1: Investigate current SD domain model structure
-   [ ] Task 2: Investigate current SD graphical model structure
-   [ ] Task 3: Investigate SD rendering and layout system
-   [ ] Task 4: Research UML 2.x combined fragment specification
-   [ ] Task 5: Design domain model for CombinedFragment
-   [ ] Task 6: Design graphical model for CombinedFragmentGR
-   [ ] Task 7: Design fragment editing UI approach

### Implementation Tasks (Core)

-   [ ] Task 8: Create CombinedFragment domain class
-   [ ] Task 9: Create CombinedFragmentGR graphical class
-   [ ] Task 10: Create Operand domain class (represents one region in fragment)
-   [ ] Task 11: Create OperandGR graphical class

### Implementation Tasks (Rendering)

-   [ ] Task 12: Implement fragment rectangle rendering with rounded corners
-   [ ] Task 13: Implement operator label pentagon rendering
-   [ ] Task 14: Implement operand separator lines (dashed horizontal)
-   [ ] Task 15: Implement guard condition rendering
-   [ ] Task 16: Implement message containment within fragments

### Implementation Tasks (UI/Controllers)

-   [ ] Task 17: Create AddCombinedFragmentController
-   [ ] Task 18: Create CombinedFragmentEditor dialog
-   [ ] Task 19: Add combined fragment toolbar button/menu item
-   [ ] Task 20: Implement fragment resize handling
-   [ ] Task 21: Implement fragment move handling

### Implementation Tasks (XML/Persistence)

-   [ ] Task 22: Implement XML serialization for CombinedFragment
-   [ ] Task 23: Implement XML deserialization for CombinedFragment
-   [ ] Task 24: Add ObjectFactory support for fragments

### Testing Tasks

-   [ ] Task 25: Create tests for CombinedFragment domain class
-   [ ] Task 26: Create tests for CombinedFragmentGR graphical class
-   [ ] Task 27: Create integration test for SD with fragments
-   [ ] Task 28: Test XML save/load with fragments
-   [ ] Task 29: Manual testing of all three operators (alt, opt, loop)

### Documentation Tasks

-   [ ] Task 30: Update plan file with implementation summary
-   [ ] Task 31: Update CHANGELOG.md
-   [ ] Task 32: Update features.md
-   [ ] Task 33: Create StudentUML diagram documenting the design

## Implementation Summary

### Overview

Combined fragments for sequence diagrams have been successfully implemented with support for OPT, ALT, and LOOP operators. The implementation follows UML 2.x specification and provides a complete user experience including creation, editing, resizing, and persistence.

### Key Design Decisions

1. **Minimal Default Creation:** Fragments are created as OPT with empty guard by default, removing the need for a creation dialog and simplifying the user workflow.

2. **Double-Click Editing:** Edit functionality is accessed via double-click on the pentagon operator label, providing intuitive access to all fragment properties.

3. **Dynamic UI:** The edit dialog shows/hides loop iteration fields dynamically based on operator selection, reducing UI clutter.

4. **Auto-Bracketing:** Guard conditions automatically receive square brackets if not provided by the user, following UML convention.

5. **UML 2.x Loop Specification:** Full support for loop iteration syntax including exact iterations `loop(3)`, ranges `loop(0,2)`, and unlimited `loop(3,*)`.

6. **Transparent Background:** Fragments use transparent fill to allow messages inside to remain visible and selectable.

7. **Pentagon Label Selection:** Only the pentagon operator label is clickable for selection, allowing messages inside the fragment to remain selectable.

8. **Y-Coordinate Preservation:** Copy/paste operations preserve Y-coordinates since sequence diagrams use Y-axis for time sequencing.

### Architecture

**Domain Layer:**

-   `CombinedFragment` - Domain model with operator, guard condition, rank, height, loopMin, loopMax
-   `InteractionOperator` - Enum for OPT, ALT, LOOP with display names

**Graphical Layer:**

-   `CombinedFragmentGR` - Graphical representation extending GraphicalElement
-   Pentagon operator label rendering with loop iterations
-   Rounded rectangle with transparent background
-   8-handle resize support (ResizeHandle integration)

**UI Layer:**

-   `CombinedFragmentEditor` - Modal dialog for editing fragment properties
-   Dynamic field visibility based on operator type
-   Validation logic for loop iterations
-   Help text with examples

**Controller Layer:**

-   `AddCombinedFragmentController` - Single-click creation of default OPT fragments
-   Integrated with AddElementControllerFactory

**Undo/Redo:**

-   `EditCombinedFragmentEdit` - Undoable edit for property changes
-   Tracks old/new values for operator, guard, loopMin, loopMax

**Persistence:**

-   XML serialization in CombinedFragment (streamToXML/streamFromXML)
-   Backward compatibility maintained
-   Loop iteration values saved/loaded correctly

### Files Modified/Created

**Created (6 files):**

1. `src/main/java/edu/city/studentuml/model/domain/InteractionOperator.java` (enum, 36 lines)
2. `src/main/java/edu/city/studentuml/model/domain/CombinedFragment.java` (domain, 350+ lines)
3. `src/main/java/edu/city/studentuml/model/domain/Operand.java` (domain, 54 lines)
4. `src/main/java/edu/city/studentuml/model/graphical/CombinedFragmentGR.java` (graphical, 700+ lines)
5. `src/main/java/edu/city/studentuml/view/gui/CombinedFragmentEditor.java` (UI with ALT operand management, 500+ lines)
6. `src/main/java/edu/city/studentuml/util/undoredo/EditCombinedFragmentEdit.java` (undo/redo, 109 lines)

**Modified (12 files):**

1. `src/main/java/edu/city/studentuml/controller/AddCombinedFragmentController.java` - Simplified creation + initial auto-resize
2. `src/main/java/edu/city/studentuml/controller/AddElementControllerFactory.java` - Added factory case
3. `src/main/java/edu/city/studentuml/model/domain/CentralRepository.java` - Added fragment collection
4. `src/main/java/edu/city/studentuml/model/graphical/SDModel.java` - Added fragment support
5. `src/main/java/edu/city/studentuml/view/gui/DiagramInternalFrame.java` - Added toolbar button
6. `src/main/java/edu/city/studentuml/util/ObjectFactory.java` - Added XML deserialization
7. `src/main/java/edu/city/studentuml/controller/SelectionController.java` - Y-coordinate preservation + Control-key auto-resize
8. `src/main/java/edu/city/studentuml/model/graphical/AbstractSDModel.java` - Removed auto-resize on lifeline/fragment movement
9. `src/main/java/edu/city/studentuml/model/graphical/CombinedFragmentGR.java` - Auto-resize logic + coordinate clamping
10. `src/main/java/edu/city/studentuml/model/graphical/ResizeHandle.java` - Added getResizableElement() getter
11. `src/test/java/edu/city/studentuml/model/domain/CombinedFragmentTest.java` - 5 tests
12. `src/test/java/edu/city/studentuml/model/graphical/CombinedFragmentGRTest.java` - 13 tests
13. `src/test/java/edu/city/studentuml/CombinedFragmentAutoResizeTest.java` - Updated for new behavior

### Test Coverage

**Unit Tests:** 40 tests across 6 test classes

-   CombinedFragmentTest: 5 tests (domain model)
-   CombinedFragmentGRTest: 13 tests (graphical + loop formatting + cloning)
-   CombinedFragmentAutoResizeTest: 9 tests (auto-resize with Control key)
-   OperandTest: 5 tests (operand domain model)
-   CombinedFragmentAltTest: 7 tests (ALT with multiple operands)
-   SDSaveLoadTest: 1 additional test (ALT fragment save/load integration test)

**Integration Tests:** 4 comprehensive save/load tests

-   testSDSaveLoad: Complex SD with actor, objects, multiobjects, create/destroy messages
-   testSDWithOptFragmentSaveLoad: OPT fragment with guard condition
-   testSDWithAltFragmentSaveLoad: ALT fragment with 2 operands and custom guards
-   **testSDWithAllFragmentTypesSaveLoad: All fragment types (OPT, ALT, LOOP) in one diagram**
    -   3 objects, 13 messages
    -   OPT fragment with guard condition
    -   ALT fragment with 2 operands and custom height ratios (1.0 and 2.0)
    -   LOOP fragment with min/max iterations (3,5) and guard condition
    -   Validates complete serialization/deserialization of all operators
    -   Tests operand height ratio persistence
    -   Tests loop iteration persistence

**Test Results:** All 368 project tests passing ✅

**Test Categories:**

-   Domain model creation and properties
-   XML serialization/deserialization (including operands)
-   Graphical rendering and bounds
-   Loop iteration string formatting (5 tests covering all UML syntax variants)
-   Cloning with loop iteration preservation
-   Contains point detection for selection
-   Auto-resize with Control key behavior
-   Return message inclusion
-   Nested fragment padding
-   Coordinate validation (no negative values)
-   Operand management (add, remove, clear)
-   ALT operand rendering with dashed separators
-   Complete save/load integration test for ALT fragments with multiple operands

### Known Limitations (Phase 1)

1. ~~**ALT Operand Editing:**~~ ✅ RESOLVED - Full interactive UI now available for managing ALT operands
2. **Message Containment:** No automatic detection or validation of which messages are inside fragments (Phase 2)
3. **Nested Fragments:** Support for nesting exists with extra padding, but no validation or advanced features (Phase 2)
4. **Auto-Resize Control:** Auto-resize only triggers when Control key is pressed during manual resize, giving users full control

### Auto-Resize Design Decisions (Phase 2 Enhancement)

**User Control Philosophy:**
The auto-resize feature was refined to give users maximum control over fragment sizing:

1. **Manual Control by Default:** Fragments maintain their size during diagram construction

    - No resize when adding messages
    - No resize when moving messages
    - No resize when moving lifelines/objects
    - No resize when moving fragments themselves

2. **Opt-In Auto-Resize:** Users explicitly trigger auto-resize by holding Control during manual resize

    - Control + drag resize handle → auto-adjusts on release
    - Without Control → fragment keeps exact user-specified size
    - Only the specific fragment being resized is affected

3. **Intelligent Initial Sizing:** New fragments auto-size to span messages with allowShrink=true

4. **Return Message Inclusion:** When a fragment contains a call message, it automatically includes the corresponding return message in bounds calculation

5. **Nested Fragment Padding:** Fragments containing other fragments get 30px horizontal padding (vs 10px for normal messages) to show visual hierarchy

6. **Coordinate Validation:** Fragments are constrained to non-negative coordinates (≥0, ≥0) during:
    - Movement (drag)
    - Resize (all handles)
    - Paste operations
    - Mass selection movements

**Implementation Details:**

-   `ResizeHandle.getResizableElement()` - Added getter to retrieve the element being resized
-   `SelectionController.myMouseReleased()` - Checks `event.isControlDown()` before auto-resize
-   `CombinedFragmentGR.move()` and `setStartingPoint()` - Clamp coordinates to (0,0) minimum
-   `CombinedFragmentGR.autoResizeToMessages()` - Detects nested fragments and applies appropriate padding

**Test Updates:**

-   `testAutoResize_WhenRoleClassifierMoves` updated to verify fragments maintain size when objects move
-   All 354 tests passing with new behavior

### Manual Testing Performed

✅ Fragment creation (default OPT with initial auto-sizing)  
✅ Double-click editing  
✅ Operator changes (OPT → ALT → LOOP)  
✅ Guard condition editing and auto-bracketing  
✅ Loop iteration formatting (all UML syntax variants)  
✅ Undo/redo operations  
✅ Cancel behavior  
✅ Resize handles with cursor feedback  
✅ Copy/paste with Y-coordinate preservation  
✅ Save/load persistence  
✅ Integration with existing SD elements  
✅ Control+resize for auto-adjust behavior  
✅ Resize without Control maintains exact size  
✅ Return message inclusion in auto-resize  
✅ Nested fragment padding (30px vs 10px)  
✅ Coordinate clamping (no negative positions)  
✅ Fragment movement without auto-resize  
✅ Lifeline movement without affecting fragments

### Future Enhancements (Phase 2)

See Phase 2 section above for planned enhancements including:

-   ALT with multiple operands and separators
-   Message containment detection
-   Nested fragments support
-   Custom toolbar icon
-   Auto-resize functionality
-   Additional operators (par, break, critical, etc.)

## Design Documentation

[To be created as StudentUML diagram file: `diagrams/feature-combined-fragments.xml`]

## Phase 2 Planning (Future)

Additional operators to implement:

-   **par** (parallel execution)
-   **break** (exception handling)
-   **critical** (critical region)
-   **neg**, **assert**, **strict**, **seq**, **ignore**, **consider**

## Notes

-   Combined fragments are one of the most complex features in UML sequence diagrams
-   This is a large feature that may require multiple sessions
-   Start with investigation and design before implementing
-   Focus on getting alt, opt, loop working perfectly before adding more operators
