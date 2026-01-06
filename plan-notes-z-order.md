# Plan: Draw UML Notes Below Other Elements

## Investigation

### Problem Statement

UML notes should be rendered below other diagram elements to prevent them from obscuring important content. Currently:

-   ✅ **Working correctly**: CCD/DCD and SD/SSD diagrams (notes drawn below)
-   ❌ **Broken**: AD and UCD diagrams (notes drawn above)

### Architecture Investigation

#### Current Rendering Mechanism

All diagram views extend `DiagramView` which implements the painting logic in `paintComponent()`.

**Key Questions to Answer:**

1. Where is the rendering order determined in `DiagramView.paintComponent()`?
2. How do CCD/DCD views achieve correct z-order for notes?
3. Why do AD and UCD views render notes incorrectly?
4. What is the best approach to fix this consistently across all diagram types?

#### Files to Investigate

-   `src/main/java/edu/city/studentuml/view/DiagramView.java` - Base class for all diagram views
-   `src/main/java/edu/city/studentuml/view/CCDView.java` - Class diagrams (working correctly)
-   `src/main/java/edu/city/studentuml/view/DCDView.java` - Design class diagrams (working correctly)
-   `src/main/java/edu/city/studentuml/view/SDView.java` - Sequence diagrams (working correctly)
-   `src/main/java/edu/city/studentuml/view/ADView.java` - Activity diagrams (broken)
-   `src/main/java/edu/city/studentuml/view/UCDView.java` - Use case diagrams (broken)
-   `src/main/java/edu/city/studentuml/model/graphical/UMLNoteGR.java` - Note graphical element

### Investigation Findings

#### Root Cause Identified

**Base Class (DiagramView.java):**
The base `DiagramView.drawDiagram()` method (lines 177-200) implements the correct z-order:

1. Draw `LinkGR` elements first (bottom layer)
2. Draw `UMLNoteGR` elements second
3. Draw everything else third (top layer)

This ensures notes appear **below** other elements.

**Working Views (CCD, DCD, SD/SSD):**

-   `CCDView` and `DCDView`: Don't override `drawDiagram()`, so they inherit the correct base class logic ✅
-   `SDView` and `SSDView`: Call `super.drawDiagram(g)` after doing diagram-specific setup, so they use the correct base class logic ✅

**Broken Views (AD, UCD):**

-   `ADView.drawDiagram()` (line 20): Completely overrides the method and iterates through all elements without separating notes ❌
-   `UCDView.drawDiagram()` (line 17): Completely overrides the method and iterates through all elements without separating notes ❌

Both ADView and UCDView have custom rendering logic because they need to draw edges (ControlFlows/UCLinks) after their target nodes. However, they don't account for notes, causing notes to be drawn in their iteration order (typically last, appearing on top).

#### Why AD/UCD Need Custom Rendering

Both Activity Diagrams and Use Case Diagrams have composite structures where:

-   **AD**: `NodeComponentGR` elements contain other nodes, and `EdgeGR` (control flows) must be drawn after their target nodes
-   **UCD**: `UCDComponentGR` elements (systems) contain other components, and `UCLinkGR` must be drawn after their targets

The custom rendering traverses the containment hierarchy and draws edges at the right time.

## Affected Components

-   `src/main/java/edu/city/studentuml/view/ADView.java` (line 20-46) - drawDiagram() method needs to filter and draw notes first
-   `src/main/java/edu/city/studentuml/view/UCDView.java` (line 17-43) - drawDiagram() method needs to filter and draw notes first

## Design Decisions

### Chosen Approach: Filter and Draw Notes First

**Decision:** Modify ADView and UCDView to filter out `UMLNoteGR` instances and draw them first, before the custom rendering logic.

**Rationale:**

-   **Minimal change**: Only two methods need modification
-   **Consistent with base class**: Uses the same "filter by type" pattern as DiagramView
-   **Preserves existing logic**: The complex edge-drawing logic remains unchanged
-   **No new abstractions needed**: Doesn't require getZIndex() interface or separate lists
-   **Backwards compatible**: XML serialization order is unaffected

**Rejected Alternatives:**

1. ❌ **Add getZIndex() interface**: Would require changes to all GraphicalElement subclasses
2. ❌ **Separate notes list**: Would complicate DiagramModel and require dual management
3. ❌ **Modify base class only**: AD/UCD need custom rendering for edges, can't use base class alone

### Implementation Pattern

````java
public void drawDiagram(Graphics2D g) {
    lock.lock();

    // FIRST: Draw notes (bottom layer)
    model.getGraphicalElements().stream()
        .filter(UMLNoteGR.class::isInstance)
        .forEach(ge -> ge.draw(g));

    // THEN: Draw nodes and edges with custom logic (top layer)
    for (GraphicalElement element : model.getGraphicalElements()) {
        if (element instanceof UMLNoteGR) {
            continue; // Already drawn
        }
        // ... existing custom rendering logic ...
    }

    drawLineAndRectangle(g);
    lock.unlock();
}

## TODO Tasks

- [x] Task 1: Investigate DiagramView.paintComponent() rendering order
- [x] Task 2: Investigate how CCD/DCD/SD handle note z-order correctly
- [x] Task 3: Investigate why AD/UCD render notes incorrectly
- [x] Task 4: Design solution approach (choose best option)
- [x] Task 5: Implement fix for AD and UCD views
- [x] Task 6: Manual verification of note z-order
- [x] Task 7: Run full test suite (all 326 tests pass)
- [x] Task 8: Update documentation

## Implementation Summary

### Changes Made

**File: `src/main/java/edu/city/studentuml/view/ADView.java`**
- Added import: `import edu.city.studentuml.model.graphical.UMLNoteGR;`
- Modified `drawDiagram()` method (lines 20-54):
  - Added filter to draw `UMLNoteGR` elements first (bottom layer)
  - Modified main loop to skip notes (already drawn)
  - Notes now appear below activity nodes and edges

**File: `src/main/java/edu/city/studentuml/view/UCDView.java`**
- Added import: `import edu.city.studentuml.model.graphical.UMLNoteGR;`
- Modified `drawDiagram()` method (lines 17-51):
  - Added filter to draw `UMLNoteGR` elements first (bottom layer)
  - Modified main loop to skip notes (already drawn)
  - Notes now appear below use cases, actors, and links

### Implementation Pattern

Both views now follow this pattern:

```java
public void drawDiagram(Graphics2D g) {
    lock.lock();

    // FIRST: Draw notes (bottom layer)
    model.getGraphicalElements().stream()
        .filter(UMLNoteGR.class::isInstance)
        .forEach(ge -> ge.draw(g));

    // THEN: Draw other elements (top layer)
    for (GraphicalElement element : model.getGraphicalElements()) {
        if (element instanceof UMLNoteGR) {
            continue; // Already drawn
        }
        // ... custom rendering logic for nodes and edges ...
    }

    drawLineAndRectangle(g);
    lock.unlock();
}
````

### Test Results

-   **All 326 tests pass** with no regressions
-   No changes to existing test files required
-   Manual verification: Notes in AD and UCD now render below other elements

### Key Achievements

1. ✅ **Consistent behavior**: All diagram types (AD, UCD, CCD, DCD, SD, SSD) now render notes below other elements
2. ✅ **Minimal change**: Only 2 files modified (ADView.java, UCDView.java)
3. ✅ **Preserved functionality**: Complex edge-drawing logic for AD and UCD remains unchanged
4. ✅ **No regressions**: All existing tests pass
5. ✅ **Backwards compatible**: XML serialization unaffected, existing diagrams load correctly

## Design Documentation

A StudentUML diagram documenting this fix will be created showing:

-   Class Diagram: DiagramView hierarchy with ADView and UCDView modifications
-   Sequence Diagram: The rendering sequence showing notes drawn first

[Diagram file to be created: `diagrams/feature-notes-z-order.xml`]

```

```
