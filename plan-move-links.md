# Plan: Move Link Endpoints Feature

## Overview

Implement the ability to grab and move link endpoints to reconnect them to different elements in diagrams. This feature allows users to change the source or target of relationships (associations, dependencies, generalizations, etc.) by dragging the link endpoints.

## Feature Description

Users should be able to:

-   Click and drag link endpoints (source or target) to reconnect them to different elements
-   Visual feedback showing which endpoint is being dragged
-   Cancel the operation (invalid drop)
-   Support undo/redo for link reconnection operations

## Applicable Link Types

### Use Case Diagrams (UCD)

-   UC Links (associations, include, extend)

### Class Diagrams (CCD/DCD)

-   Associations
-   Aggregations
-   Compositions
-   Generalizations
-   Realizations
-   Dependencies

### Sequence Diagrams (SD/SSD)

-   Messages (may have special handling due to ordering constraints)

### Activity Diagrams (AD)

-   Control Flow
-   Object Flow

## Architecture Analysis

### Current Link Structure

Links inherit from `LinkGR` which extends `AbstractLinkGR`:

-   Links have source and target elements (`GraphicalElement`)
-   Links are drawn between element connection points
-   Links have control points for routing

### Key Classes to Modify

1. **LinkGR and subclasses** (`AbstractLinkGR`, `AssociationGR`, `GeneralizationGR`, etc.)

    - Need to expose endpoint hit testing
    - Need methods to update source/target references

2. **SelectionController** (diagram-specific)

    - Mouse event handling for endpoint dragging
    - State management for drag operation

3. **DiagramModel**

    - Validate reconnection (e.g., can't create cycles in generalizations)
    - Update domain model references

4. **Undo/Redo System**
    - New edit type: `ReconnectLinkEdit`

## Implementation Phases

### Phase 1: Endpoint Detection and Visual Feedback

**Goal**: Detect when mouse is over a link endpoint and show visual feedback

#### 1.1 Endpoint Hit Testing

**Files to modify:**

-   `src/main/java/edu/city/studentuml/model/graphical/AbstractLinkGR.java`

**Add methods:**

```java
// Hit test radius for endpoints (in pixels)
private static final int ENDPOINT_HIT_RADIUS = 8;

/**
 * Check if point is near the source endpoint
 */
public boolean isPointNearSourceEndpoint(Point2D point);

/**
 * Check if point is near the target endpoint
 */
public boolean isPointNearTargetEndpoint(Point2D point);

/**
 * Get the endpoint type at the given point (SOURCE, TARGET, or NONE)
 */
public EndpointType getEndpointAtPoint(Point2D point);
```

**Create enum:**

```java
public enum EndpointType {
    NONE,
    SOURCE,
    TARGET
}
```

#### 1.2 Visual Endpoint Indicators

**Files to modify:**

-   `src/main/java/edu/city/studentuml/model/graphical/AbstractLinkGR.java`

**Add drawing method:**

```java
/**
 * Draw endpoint handles when link is selected
 */
public void drawEndpointHandles(Graphics2D g);
```

**Implementation:**

-   Draw small circles or squares at source/target endpoints when link is selected
-   Use distinct color (e.g., blue for normal, orange for hover, red for dragging)
-   Only show handles when link is selected

#### 1.3 Cursor Feedback

**Files to modify:**

-   Diagram-specific `SelectionController` classes

**Add cursor changes:**

-   Change cursor to hand/crosshair when hovering over endpoint
-   Change to grabbing cursor when dragging endpoint

---

### Phase 2: Drag State Management

**Goal**: Track dragging state and provide visual feedback during drag

#### 2.1 Drag State Tracking

**Files to modify:**

-   Diagram-specific `SelectionController` classes (e.g., `DCDSelectionController`)

**Add state fields:**

```java
private LinkGR draggingLink = null;
private EndpointType draggingEndpoint = EndpointType.NONE;
private Point2D dragPoint = null;
private GraphicalElement potentialTarget = null;
```

#### 2.2 Mouse Event Handling

**Update methods in SelectionController:**

**mousePressed:**

-   Check if click is on a link endpoint
-   If yes, enter endpoint-dragging mode
-   Store original link, endpoint type, and starting point

**mouseDragged:**

-   Update drag point position
-   Find potential target element under cursor
-   Validate if reconnection is allowed
-   Repaint to show visual feedback

**mouseReleased:**

-   If over valid target, perform reconnection
-   Create undo/redo edit
-   Update domain model
-   Reset drag state
-   Repaint

#### 2.3 Visual Feedback During Drag

**Files to modify:**

-   `src/main/java/edu/city/studentuml/view/DiagramView.java`

**Add drawing:**

-   Draw temporary line from fixed endpoint to cursor position
-   Highlight potential target element (green border for valid, red for invalid)
-   Draw ghost/preview of new link configuration

---

### Phase 3: Link Reconnection Logic ✅

**Goal**: Actually reconnect links while maintaining model integrity

#### 3.1 Link Reconnection Methods ✅

**Files modified:**

-   ✅ `src/main/java/edu/city/studentuml/model/graphical/AbstractLinkGR.java` - added abstract methods
-   ✅ `src/main/java/edu/city/studentuml/model/graphical/LinkGR.java` - base validation
-   ✅ `src/main/java/edu/city/studentuml/model/graphical/AssociationGR.java` - full implementation
-   ✅ `src/main/java/edu/city/studentuml/model/graphical/DependencyGR.java` - full implementation
-   ✅ `src/main/java/edu/city/studentuml/model/graphical/RealizationGR.java` - full implementation
-   ✅ `src/main/java/edu/city/studentuml/model/graphical/GeneralizationGR.java` - full implementation with cycle detection

**Methods added:**

```java
/**
 * Reconnect the source end of this link to a new element
 */
public boolean reconnectSource(ClassifierGR newSource);

/**
 * Reconnect the target end of this link to a new element
 */
public boolean reconnectTarget(ClassifierGR newTarget);

/**
 * Validate if reconnection is allowed
 */
public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement);

/**
 * Create a new link with updated endpoints (since endpoints are final)
 */
public [LinkType]GR createWithNewEndpoints(ClassifierGR newA, ClassifierGR newB);
```

#### 3.2 Domain Model Update ✅

**Files modified:**

-   ✅ All link types create new domain objects (Association, Dependency, Realization, Generalization)
-   ✅ Preserve all properties (names, multiplicities, directions, labels)
-   ✅ Update graphical model by replacing link in diagram

**For each link type, domain model is updated:**

-   ✅ Create new domain objects with updated references (endpoints are immutable)
-   ✅ Preserve all properties from old domain object
-   ✅ Replace graphical link in model (remove old, add new)

#### 3.3 Validation Rules ✅

**Implemented validation for different link types:**

**Implemented validation for different link types:**

**Associations/Aggregations:** ✅

-   ✅ Source and target must be classes or interfaces
-   ✅ Self-associations allowed (reflexive associations are valid UML)

**Generalizations:** ✅

-   ✅ Target must be compatible type (class→class, interface→interface)
-   ✅ Prevent direct self-inheritance (A extends A)
-   ✅ Cycle detection prepared (DFS algorithm implemented)

**Realizations:** ✅

-   ✅ Source must be class, target must be interface

**Dependencies:** ✅

-   ✅ Source and target must be classes (not interfaces)

**UC Links:** ✅

-   ✅ **UCAssociationGR**: Actor (source) ↔ UseCase (target)
-   ✅ **UCIncludeGR**: UseCase → UseCase
-   ✅ **UCExtendGR**: UseCase → UseCase (preserves extension points)
-   ✅ **UCGeneralizationGR**: Actor→Actor OR UseCase→UseCase (type-safe)

**Messages (SD/SSD):**

-   Must maintain temporal ordering
-   Source/target must be lifelines/actors

**Control/Object Flow (AD):**

-   Must connect to activity nodes
-   Maintain flow graph validity

**Note**: AggregationGR extends AssociationGR and inherits reconnection behavior automatically ✅

---

### Phase 4: Undo/Redo Support

**Goal**: Make link reconnection undoable and redoable

#### 4.1 Create Reconnect Edit ⏳

**Tasks:**

-   [ ] 4.1.1 Create `ReconnectLinkEdit.java` in `util/undoredo/` package
-   [ ] 4.1.2 Store old and new link references (since links are immutable)
-   [ ] 4.1.3 Implement `undo()` - remove new link, add back old link
-   [ ] 4.1.4 Implement `redo()` - remove old link, add new link
-   [ ] 4.1.5 Implement `getPresentationName()` - return "Reconnect [LinkType]"

**Files to create:**

-   `src/main/java/edu/city/studentuml/util/undoredo/ReconnectLinkEdit.java`

**Key Design Decisions:**

-   Since link endpoints are immutable (final fields), we must store BOTH the old and new link instances
-   The edit replaces the entire link in the model (remove old, add new)
-   Must preserve link selection state across undo/redo
-   Domain model updates are handled by the link's reconnect methods

**Implementation:**

```java
public class ReconnectLinkEdit extends AbstractUndoableEdit {
    private final AbstractLinkGR oldLink;  // Original link before reconnection
    private final AbstractLinkGR newLink;  // New link after reconnection
    private final DiagramModel model;
    private final EndpointType endpoint;   // Which endpoint was changed

    @Override
    public void undo() {
        // Remove new link from model
        model.removeGraphicalElement(newLink);
        // Add old link back to model
        model.addGraphicalElement(oldLink);
        // Restore selection state
        oldLink.setSelected(true);
        // Trigger repaint
        model.modelChanged();
    }

    @Override
    public void redo() {
        // Remove old link from model
        model.removeGraphicalElement(oldLink);
        // Add new link to model
        model.addGraphicalElement(newLink);
        // Restore selection state
        newLink.setSelected(true);
        // Trigger repaint
        model.modelChanged();
    }

    @Override
    public String getPresentationName() {
        String linkType = newLink.getClass().getSimpleName().replace("GR", "");
        return "Reconnect " + linkType + " " + endpoint.toString().toLowerCase() + " endpoint";
    }
}
```

#### 4.2 Integration with SelectionController ⏳

**Tasks:**

-   [ ] 4.2.1 Update `completeEndpointDrag()` to create `ReconnectLinkEdit`
-   [ ] 4.2.2 Add edit to UndoManager after successful reconnection
-   [ ] 4.2.3 Update selected elements list to reference new link
-   [ ] 4.2.4 Ensure proper cleanup of drag state

**Files to modify:**

-   `src/main/java/edu/city/studentuml/controller/SelectionController.java`

**Integration points:**

```java
// In completeEndpointDrag(), after successful reconnection:
if (reconnected && newLink != null) {
    // Create undo/redo edit
    ReconnectLinkEdit edit = new ReconnectLinkEdit(
        link,      // old link
        newLink,   // new link after reconnection
        model,
        draggingEndpoint
    );

    // Add to undo manager
    parentComponent.getUndoManager().addEdit(edit);

    // Update selection to reference new link
    selectedElements.remove(link);
    selectedElements.add(newLink);
    newLink.setSelected(true);

    // Replace in model
    model.removeGraphicalElement(link);
    model.addGraphicalElement(newLink);

    logger.info(() -> "Successfully reconnected " + draggingEndpoint +
                " endpoint to: " + newClassifier.getName());
}
```

#### 4.3 Testing Undo/Redo ⏳

**Tasks:**

-   [ ] 4.3.1 Create test for basic undo/redo of reconnection
-   [ ] 4.3.2 Test undo/redo with multiple reconnections in sequence
-   [ ] 4.3.3 Test undo/redo preserves link properties (roles, multiplicities, etc.)
-   [ ] 4.3.4 Test undo/redo with selection state
-   [ ] 4.3.5 Verify domain model consistency after undo/redo

**Files to create/modify:**

-   `src/test/java/edu/city/studentuml/controller/DCDLinkReconnectionTest.java` (add undo/redo tests)
-   `src/test/java/edu/city/studentuml/controller/UCDLinkReconnectionTest.java` (add undo/redo tests)

**Test scenarios:**

```java
@Test
public void testUndoRedoAssociationReconnection() {
    // Setup: A -> B
    // Reconnect: A -> C
    // Undo: verify A -> B restored
    // Redo: verify A -> C restored
}

@Test
public void testMultipleReconnectionsUndoRedo() {
    // A -> B, then A -> C, then A -> D
    // Undo: verify A -> C
    // Undo: verify A -> B
    // Redo: verify A -> C
    // Redo: verify A -> D
}

@Test
public void testUndoRedoPreservesLinkProperties() {
    // Create association with roles and multiplicities
    // Reconnect endpoint
    // Undo
    // Verify all properties preserved
}
```

---

### Phase 4: Undo/Redo Support (Estimated: 4-6 hours)

**Goal**: Handle cancellation, edge cases, and polish

#### 5.1 Operation Cancellation

**Add support for:**

-   **Esc key**: Cancel drag operation, restore original state
-   **Mouse exit**: Cancel if cursor leaves diagram area
-   **Right-click during drag**: Cancel operation

#### 5.2 Edge Case Handling

**Handle special cases:**

-   **Dragging over source/target**: Don't allow reconnecting to same element at same endpoint
-   **Multiple selected links**: Disable endpoint dragging when multiple elements selected
-   **Locked elements**: Don't allow reconnecting to locked/immutable elements
-   **Invalid drops**: Show error message or silent failure with visual feedback

#### 5.3 Reflexive Links

**Special handling:**

-   Self-associations (class→same class)
-   Allow reconnecting one end while keeping other on same element
-   Visual feedback for reflexive configuration

---

### Phase 6: Testing

**Goal**: Ensure feature works correctly across all diagram types

#### 6.1 Manual Tests

Create manual test cases for:

1. **Basic Reconnection**

    - Drag source endpoint to different element
    - Drag target endpoint to different element
    - Verify domain model updated correctly

2. **Validation Rules**

    - Try invalid reconnections (should be prevented)
    - Verify validation messages/feedback
    - Test cycle detection in generalizations

3. **Visual Feedback**

    - Verify endpoint handles visible when selected
    - Verify cursor changes appropriately
    - Verify target highlighting during drag

4. **Undo/Redo**

    - Reconnect link, then undo → verify original connection restored
    - Redo → verify new connection restored
    - Multiple reconnections → undo/redo sequence

5. **Edge Cases**

    - Cancel with Esc key
    - Drag outside diagram bounds
    - Reconnect reflexive associations

6. **All Diagram Types**
    - Test on UCD, CCD, DCD, SD, SSD, AD
    - Verify each link type works correctly

#### 6.2 Automated Tests

**Files to create:**

-   `src/test/java/edu/city/studentuml/controller/LinkReconnectionTest.java`

**Test scenarios:**

```java
@Test
public void testReconnectAssociationSource()

@Test
public void testReconnectGeneralizationWithCycleDetection()

@Test
public void testUndoRedoReconnection()

@Test
public void testInvalidReconnectionBlocked()

@Test
public void testReflexiveAssociation()
```

---

## Technical Considerations

### Performance

-   Endpoint hit testing should be efficient (only test visible links)
-   Avoid excessive repaints during drag operations
-   Cache endpoint positions when possible

### Thread Safety

-   All UI updates must be on EDT
-   Drag state should be confined to controller (no shared mutable state)

### Compatibility

-   Must work with existing XML serialization
-   Should not break existing diagram loading/saving
-   Maintain backward compatibility with existing diagrams

### Accessibility

-   Keyboard alternative for reconnection (future enhancement)
-   Clear visual feedback for users with limited color perception
-   Consider screen reader compatibility

---

## User Experience Enhancements

### Visual Polish

1. **Endpoint Handle Styling**

    - Rounded squares or circles
    - Drop shadow for depth
    - Animate on hover (subtle scale or glow)

2. **Drag Preview**

    - Semi-transparent preview line during drag
    - Animate smoothly to new position on release

3. **Target Highlighting**

    - Pulse animation on valid targets
    - Clear invalid state (red border + X icon)

4. **Cursor Feedback**
    - Hand cursor on hover
    - Grabbing cursor during drag
    - Not-allowed cursor for invalid drops

### Contextual Help

-   Tooltip: "Drag to reconnect link endpoint"
-   Status bar message during drag: "Drag to element to reconnect..."
-   Error messages for invalid reconnections

---

## Future Enhancements

### Beyond Initial Implementation

1. **Batch Reconnection**

    - Reconnect multiple links at once
    - Useful when moving/deleting elements

2. **Smart Routing**

    - Automatically adjust link routing after reconnection
    - Avoid overlaps with other elements

3. **Link Splitting**

    - Click on link midpoint to create intermediate element
    - Split association into composition + association

4. **Constraint Preservation**

    - Preserve association roles/multiplicities during reconnection
    - Preserve message ordering in sequence diagrams

5. **Keyboard Shortcuts**
    - Tab through endpoints
    - Arrow keys to change target
    - Enter to confirm, Esc to cancel

---

## Implementation Checklist

### Phase 1: Endpoint Detection (Estimated: 4-6 hours)

-   ✅ Add endpoint hit testing methods to AbstractLinkGR
-   ✅ Create EndpointType enum
-   ✅ Add drawEndpointHandles method
-   ✅ Update selection controllers to show handles
-   ✅ Add cursor feedback

### Phase 2: Drag State Management (Estimated: 6-8 hours)

-   ✅ Add drag state fields to SelectionController
-   ✅ Update mousePressed to detect endpoint clicks
-   ✅ Update mouseDragged to track drag position
-   ✅ Update mouseReleased to complete reconnection
-   ✅ Add visual feedback during drag

### Phase 3: Link Reconnection Logic (Estimated: 8-12 hours)

-   ✅ Add reconnectSource/reconnectTarget methods to LinkGR
-   ✅ Update domain model for each link type
-   ✅ Implement validation rules
-   ☐ Test reconnection for all link types

### Phase 4: Undo/Redo Support (Estimated: 4-6 hours)

-   ✅ 4.1.1 Create ReconnectLinkEdit class with old/new link storage (for class diagrams)
-   ✅ 4.1.2 Implement undo() - swap new link for old link
-   ✅ 4.1.3 Implement redo() - swap old link for new link
-   ✅ 4.1.4 Implement getPresentationName()
-   ✅ 4.2.1 Integrate ReconnectLinkEdit with SelectionController
-   ✅ 4.2.2 Update selection tracking after reconnection
-   ✅ 4.3.1 Write basic undo/redo test (class diagrams)
-   ✅ 4.3.2 Write multiple reconnections undo/redo test (class diagrams)
-   ✅ 4.3.3 Verify property preservation test (class diagrams)
-   ✅ 4.4.1 Create ReconnectMessageEdit class (for sequence diagrams)
-   ✅ 4.4.2 Implement undo/redo for SD messages (modifies in place)
-   ✅ 4.4.3 Handle compound edit for call+return message pairs
-   ✅ 4.4.4 Write undo/redo tests for SD messages

**Note:** Undo/redo support differs between diagram types:

-   **Class diagrams (CCD/DCD)**: Links have immutable endpoints, so we store old/new link instances
-   **Sequence diagrams (SD/SSD)**: Messages modify endpoints in place, so we store old/new endpoint references
-   **Call messages**: Require compound edits since reconnection also updates the corresponding return message

### Phase 5: Polish and Edge Cases (Estimated: 4-6 hours)

-   ☐ Add Esc key cancellation
-   ☐ Handle edge cases
-   ☐ Add reflexive link support
-   ☐ Improve visual feedback

### Phase 6: Testing (Estimated: 6-8 hours)

-   ☐ Write manual test cases
-   ☐ Test all diagram types
-   ☐ Write automated tests
-   ☐ Fix bugs and edge cases

**Total Estimated Effort: 32-46 hours**

---

## Success Criteria

1. ☐ Users can drag link endpoints to reconnect them
2. ☐ Visual feedback clearly shows which endpoint is being dragged
3. ☐ Invalid reconnections are prevented with clear feedback
4. ☐ Reconnection operations are undoable and redoable
5. ☐ Feature works on all diagram types (UCD, CCD, DCD, SD, SSD, AD)
6. ☐ No performance degradation during dragging
7. ☐ Domain model remains consistent after reconnection
8. ☐ Existing diagrams load and save correctly
9. ☐ All validation rules are enforced
10. ☐ Manual and automated tests pass

---

## Risks and Mitigations

| Risk                         | Impact | Mitigation                                                            |
| ---------------------------- | ------ | --------------------------------------------------------------------- |
| Complex domain model updates | High   | Start with simple link types (associations), then handle complex ones |
| Validation rule complexity   | Medium | Document rules clearly, implement incrementally, test thoroughly      |
| Undo/redo complexity         | Medium | Reuse existing edit patterns, test edge cases                         |
| Performance during drag      | Low    | Optimize hit testing, throttle repaints if needed                     |
| Backward compatibility       | Medium | Test with existing diagrams, maintain XML format                      |

---

## References

-   Existing undo/redo classes: `src/main/java/edu/city/studentuml/util/undoredo/`
-   Link hierarchy: `src/main/java/edu/city/studentuml/model/graphical/AbstractLinkGR.java`
-   Selection controllers: `src/main/java/edu/city/studentuml/controller/*SelectionController.java`
-   Domain model: `src/main/java/edu/city/studentuml/model/domain/`
