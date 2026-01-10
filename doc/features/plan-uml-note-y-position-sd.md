# Plan: Fix UML Note Y-Position in Sequence Diagrams

## Investigation

The issue is in `SelectionController.pasteClipboard()` method where UML notes in Sequence Diagrams use the mouse Y-coordinate to calculate their pasted position. Since all elements in Sequence Diagrams are positioned at the top, this causes notes to jump to unexpected positions.

## Affected Components

- Class: `SelectionController` - `pasteClipboard()` method needs modification
- File: `src/main/java/edu/city/studentuml/controller/SelectionController.java`

## Design Decisions

- Detect if the target diagram is a Sequence Diagram (SDModel or SSDModel)
- For SD/SSD: Use original Y-coordinate plus fixed offset for UMLNoteGR elements
- For other diagrams: Keep existing mouse-based positioning behavior
- Use a small fixed offset (20 pixels) to avoid exact overlap with original

## TODO Tasks

- [x] Investigate pasteClipboard() method
- [x] Write test to reproduce the issue
- [x] Implement fix for SD/SSD diagrams
- [x] Verify fix works correctly
- [x] Run full test suite
- [x] Update CHANGELOG

## Implementation Summary

Modified `SelectionController.pasteClipboard()` to detect Sequence Diagrams (SDModel/SSDModel) and use original Y-coordinate plus fixed offset (20px) for UMLNoteGR elements instead of calculating from mouse position. Other diagram types retain existing behavior.

## Testing Coverage

- Added test case in SelectionControllerTest to verify UML note paste behavior in SD
- Verified all existing tests still pass
