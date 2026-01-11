# Plan: Quick Message Entry with Parsing in Sequence Diagrams

## Investigation

This feature allows users to quickly enter and edit call messages in SD/SSD diagrams by typing directly on the canvas with a syntax that includes message name, parameters with optional types, and return value with optional type.

### Current Message Editing Flow

1. User creates a message by clicking between two objects
2. Message editor dialog opens (`CallMessageEditor`)
3. User manually enters message name
4. User adds parameters one by one using the parameters panel
5. User sets return value/type separately

### Required Changes

**Areas to explore:**
- How messages are currently edited (double-click behavior)
- Message text rendering in `SDMessageGR` and `CallMessageGR`
- Current structure of `CallMessage` domain model
- How parameters are stored and managed
- How return value/type are stored

**Key Classes to Investigate:**
- `CallMessage` (domain model)
- `SDMessageGR` / `CallMessageGR` (graphical representation)
- `CallMessageEditor` (current editor dialog)
- `SDSelectionController` (handles mouse events)
- `MethodParameter` (parameter model)

## Affected Components

### New Components to Create

- **`MessageSyntaxParser`** - Utility class to parse and reconstruct message syntax
  - Location: `src/main/java/edu/city/studentuml/util/`
  - Methods:
    - `ParseResult parse(String text)` - Parse syntax string
    - `String reconstruct(CallMessage message)` - Reconstruct syntax from message
    - `boolean validate(String text)` - Validate syntax

- **`ParseResult`** - Data class holding parsed components
  - returnValue (String, optional)
  - returnType (String, optional)
  - messageName (String, required)
  - parameters (List<ParameterInfo>, optional)
  
- **`ParameterInfo`** - Data class for parsed parameter
  - name (String, required)
  - type (String, optional)

### Components to Modify

- **`SDMessageGR`** / **`CallMessageGR`** - Add inline text editing on canvas
  - Distinguish between click on arrow vs text
  - Show text field for inline editing
  - Apply parsing when editing completes

- **`SDSelectionController`** - Handle double-click events
  - Double-click on message text → inline editing
  - Double-click on message arrow → open dialog editor

- **`CallMessage`** - May need bulk parameter operations
  - Method to set parameters from list
  - Method to clear and rebuild parameters

- **`CallMessageEditor`** - Keep as full-featured dialog
  - No changes needed, remains as detailed editor

## Design Decisions

### 1. Parser Design

Use a simple regex-based parser with clear patterns for each component:
- Return value: `(\w+)` (optional)
- Return type: `: (\w+)` (optional, follows return value)
- Message name: `(\w+)` (required)
- Parameters: `\(([^)]*)\)` (required parentheses, optional content)
- Parameter pattern: `(\w+)(: (\w+))?` per parameter

### 2. Inline Editing Approach

Two options:
- **Option A**: Create a lightweight `JTextField` overlay on the diagram view
- **Option B**: Use Swing's built-in editing capabilities with custom cell editor

**Decision**: Option A - Use `JTextField` overlay for more control and better UX

### 3. Click Detection

Distinguish between:
- Click on arrow line → select message
- Double-click on arrow → open full dialog
- Double-click on text label → inline editing

Need to detect click position relative to message text bounds vs arrow line.

### 4. Error Handling

Show validation errors as a tooltip near the text field without closing it.
Allow user to correct the syntax and try again.

### 5. Undo/Redo Support

Create appropriate `UndoableEdit` for inline message editing that captures:
- Old message state (name, parameters, return value/type)
- New message state
- Can undo/redo the inline edit

## TODO Tasks

### Phase 1: Parser Implementation ✅ COMPLETE
- [x] Create `MessageSyntaxParser` class with parse() method
- [x] Create `ParseResult` and `ParameterInfo` data classes
- [x] Implement parsing logic with regex patterns
- [x] Add validation logic
- [x] Write comprehensive unit tests for parser (various syntax combinations)
- [x] Implement reconstruct() method to generate syntax from CallMessage

### Phase 2: Inline Editing UI
- [ ] Investigate how to add inline text editing to diagram view
- [ ] Create text field overlay mechanism in `SDMessageGR`
- [ ] Implement click detection to distinguish arrow vs text
- [ ] Add double-click handler for text (inline edit)
- [ ] Add double-click handler for arrow (dialog edit)
- [ ] Handle text field focus, commit, and cancel

### Phase 3: Integration with Domain Model
- [ ] Add bulk parameter setting method to `CallMessage`
- [ ] Apply parsed results to message when inline editing completes
- [ ] Handle parameter creation/deletion based on parsed text
- [ ] Set return value and return type from parsed results
- [ ] Update message display after parsing

### Phase 4: Undo/Redo Support
- [ ] Create `EditMessageInlineEdit` undoable edit class
- [ ] Capture old and new message state
- [ ] Integrate with undo manager

### Phase 5: Testing and Polish
- [ ] Test with various syntax combinations
- [ ] Test error handling and validation feedback
- [ ] Test undo/redo functionality
- [ ] Test with existing messages (reconstruction)
- [ ] Manual testing of user workflow
- [ ] Update integration tests if needed

### Phase 6: Documentation
- [ ] Update CHANGELOG.md with new feature
- [ ] Remove feature from features.md
- [ ] Create StudentUML diagram documenting the implementation
- [ ] Complete implementation summary

## Implementation Summary

[To be filled at completion]

## Design Documentation

[Reference to StudentUML diagram file: diagrams/feature-quick-message-entry.xml]
