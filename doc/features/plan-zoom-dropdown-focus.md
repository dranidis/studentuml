# Plan: Fix Zoom Dropdown Focus on New Diagram

## Investigation

When a new diagram is created, the zoom dropdown in the menu bar gains focus instead of the diagram view. This is undesirable because:

1. Users expect to immediately start drawing after creating a diagram
2. The dropdown focus intercepts keyboard shortcuts
3. It's inconsistent with standard UX patterns

The zoom dropdown is created in `DiagramInternalFrame.createZoomComboBox()` and added to the menu bar in the constructor.

## Affected Components

-   Class: `DiagramInternalFrame` - Need to prevent zoom dropdown from gaining focus on initialization
-   Method: `initialize()` - Should transfer focus to the view after setup

## Design Decisions

The fix should ensure that after a diagram is created or loaded:

1. The diagram view gets focus automatically
2. The zoom dropdown doesn't steal focus during initialization
3. This works for both new diagrams and loaded diagrams

## TODO Tasks

-   [x] Investigate where focus is being set
-   [x] Add code to transfer focus to view after initialization
-   [x] Test with new diagram creation
-   [x] Update CHANGELOG.md
-   [x] Commit changes

## Implementation Summary

Added `view.requestFocusInWindow()` call at the end of `DiagramInternalFrame.initialize()` method to ensure the diagram view gets focus after the frame is fully initialized, whether it's a new diagram or loaded from file.

## Testing

Manual testing:

1. Create new diagram → View should have focus, can start drawing immediately
2. Open existing diagram → View should have focus
3. Use zoom dropdown → View regains focus after zoom change
