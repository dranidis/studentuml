# Plan: Fix Association Class Display Bug in DCD

## Investigation

### Root Cause
The bug is in `AssociationClassGR.drawClassAndDashedLine()` method. The method sets the graphics context to use a dashed stroke for drawing the connecting line between the association and the association class (lines 140-143), but **never restores the original stroke** before calling `classElement.draw(g)` at line 151.

When `classElement.draw(g)` is executed with a dashed stroke still active in the graphics context, the class border is drawn with dashes instead of solid lines, making it appear as an association class element rather than a normal class.

### Current Code Flow
```
AssociationClassGR.draw(g)
├── associationElement.draw(g)  // Draws the association line between classes A and B
└── drawClassAndDashedLine(g)
    ├── Set dashed stroke (line 140-143)
    ├── Draw dashed line from association to class (line 145)
    └── classElement.draw(g)  // BUG: Still has dashed stroke!
```

### Affected Components

- **File**: `src/main/java/edu/city/studentuml/model/graphical/AssociationClassGR.java`
- **Method**: `drawClassAndDashedLine(Graphics2D g)` (lines 118-152)
- **Issue**: Missing stroke restoration before drawing class element

## Design Decisions

### Solution
Save the original stroke before setting the dashed stroke, then restore it before drawing the class element. This is the standard pattern used in other drawing methods in the codebase (e.g., `LinkGR.draw()`, `AbstractClassGR.draw()`).

### Pattern to Follow
```java
Stroke originalStroke = g.getStroke();  // Save original
g.setStroke(GraphicsHelper.makeDashedStroke());  // Set dashed
g.drawLine(...);  // Draw with dashed stroke
g.setStroke(originalStroke);  // Restore original
classElement.draw(g);  // Draw with correct stroke
```

## TODO Tasks

- [x] Create test to reproduce the bug
- [x] Fix stroke restoration in `drawClassAndDashedLine()`
- [x] Verify fix with test
- [x] Run full test suite
- [x] Update CHANGELOG.md
- [x] Remove bug from features.md

## Implementation Summary

The fix is straightforward: save the original stroke before setting the dashed stroke, and restore it after drawing the dashed line but before drawing the class element. This ensures the association class itself is drawn with solid borders while maintaining the dashed line connecting it to the association.

The fix follows the existing pattern used throughout the codebase for managing graphics context state during drawing operations.

## Testing

Created `AssociationClassGRTest.testAssociationClassDisplayBug()` to verify that:
1. The association line between Class A and Class B uses solid stroke
2. The connecting line from association to association class uses dashed stroke  
3. The association class itself uses solid stroke (not dashed)

The test uses a `StrokeCapturingGraphics2D` mock to capture which strokes are used during drawing.
