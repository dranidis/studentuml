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

-   How messages are currently edited (double-click behavior)
-   Message text rendering in `SDMessageGR` and `CallMessageGR`
-   Current structure of `CallMessage` domain model
-   How parameters are stored and managed
-   How return value/type are stored

**Key Classes to Investigate:**

-   `CallMessage` (domain model)
-   `SDMessageGR` / `CallMessageGR` (graphical representation)
-   `CallMessageEditor` (current editor dialog)
-   `SDSelectionController` (handles mouse events)
-   `MethodParameter` (parameter model)

## Affected Components

### New Components to Create

-   **`MessageSyntaxParser`** - Utility class to parse and reconstruct message syntax

    -   Location: `src/main/java/edu/city/studentuml/util/`
    -   Methods:
        -   `ParseResult parse(String text)` - Parse syntax string
        -   `String reconstruct(CallMessage message)` - Reconstruct syntax from message
        -   `boolean validate(String text)` - Validate syntax

-   **`ParseResult`** - Data class holding parsed components
    -   returnValue (String, optional)
    -   returnType (String, optional)
    -   messageName (String, required)
    -   parameters (List<ParameterInfo>, optional)
-   **`ParameterInfo`** - Data class for parsed parameter
    -   name (String, required)
    -   type (String, optional)

### Components to Modify

-   **`SDMessageGR`** / **`CallMessageGR`** - Add inline text editing on canvas

    -   Distinguish between click on arrow vs text
    -   Show text field for inline editing
    -   Apply parsing when editing completes

-   **`SDSelectionController`** - Handle double-click events

    -   Double-click on message text → inline editing
    -   Double-click on message arrow → open dialog editor

-   **`CallMessage`** - May need bulk parameter operations

    -   Method to set parameters from list
    -   Method to clear and rebuild parameters

-   **`CallMessageEditor`** - Keep as full-featured dialog
    -   No changes needed, remains as detailed editor

## Design Decisions

### 1. Parser Design

Use a simple regex-based parser with clear patterns for each component:

-   Return value: `(\w+)` (optional)
-   Return type: `: (\w+)` (optional, follows return value)
-   Message name: `(\w+)` (required)
-   Parameters: `\(([^)]*)\)` (required parentheses, optional content)
-   Parameter pattern: `(\w+)(: (\w+))?` per parameter

### 2. Inline Editing Approach

Two options:

-   **Option A**: Create a lightweight `JTextField` overlay on the diagram view
-   **Option B**: Use Swing's built-in editing capabilities with custom cell editor

**Decision**: Option A - Use `JTextField` overlay for more control and better UX

### 3. Click Detection

Distinguish between:

-   Click on arrow line → select message
-   Double-click on arrow → open full dialog
-   Double-click on text label → inline editing

Need to detect click position relative to message text bounds vs arrow line.

### 4. Auto-invoke Inline Editing on Message Creation

**Decision**: Automatically open inline editor when a new call message is created (without requiring double-click).

**Benefits:**

-   Faster workflow - user can immediately start typing
-   More intuitive - matches the mental model of "draw and label"
-   Reduces friction - no need for extra double-click step
-   Consistent with modern UI expectations

**Implementation:**

-   After `AddCallMessageController` creates the message, trigger inline editing
-   Pass reference to the newly created message graphical element
-   Open inline editor at the message text position

### 5. Error Handling

Show validation errors as a tooltip near the text field without closing it.
Allow user to correct the syntax and try again.

### 6. Undo/Redo Support

Create appropriate `UndoableEdit` for inline message editing that captures:

-   Old message state (name, parameters, return value/type)
-   New message state
-   Can undo/redo the inline edit

## TODO Tasks

### Phase 1: Parser Implementation ✅ COMPLETE

-   [x] Create `MessageSyntaxParser` class with parse() method
-   [x] Create `ParseResult` and `ParameterInfo` data classes
-   [x] Implement parsing logic with regex patterns
-   [x] Add validation logic
-   [x] Write comprehensive unit tests for parser (various syntax combinations)
-   [x] Implement reconstruct() method to generate syntax from CallMessage
-   [x] Verify parser works with CreateMessage (extends CallMessage, inherits all functionality)
-   [x] Add tests for create message syntax

**Note**: The parser works seamlessly with both `CallMessage` and `CreateMessage` because `CreateMessage` extends `CallMessage` and inherits return value, return type, and parameter methods. Users can use the same syntax for create messages: `create(params)` or `obj: Type := create(params)`.

### Phase 2: Inline Editing UI ✅ COMPLETE

-   [x] Add `containsText()` method to `SDMessageGR` to detect clicks on text vs arrow
-   [x] Create `InlineStringEditor` component - pure UI for inline text editing
-   [x] Create `InlineMessageEditor` component - message-specific logic with syntax validation
-   [x] Modify `SelectionController.myMouseClicked()` to check if double-click is on message text
-   [x] When double-click on text: show inline editor
-   [x] When double-click on arrow: show dialog editor (existing behavior)
-   [x] Handle Enter key to commit, Escape to cancel
-   [x] Implement validation in `InlineStringEditor` to prevent text disappearing on errors
-   [x] Show error messages for parse failures via dialog
-   [x] Position text field correctly on non-reflective and reflective messages
-   [x] Handle text field focus, commit, and cancel
-   [x] Fix text field position flash by setting invisible initially
-   [x] Fix infinite dialog loop with `isShowingError` flag
-   [x] Fixed `contains()` method for reflective messages to include text area

**Key Design Achievement**: Separated UI concerns (`InlineStringEditor`) from domain logic (`InlineMessageEditor`) following Single Responsibility Principle

### Phase 3: Integration with Domain Model ✅ COMPLETE

-   [x] Add bulk parameter setting method to `CallMessage`
-   [x] Apply parsed results to message when inline editing completes
-   [x] Handle parameter creation/deletion based on parsed text
-   [x] Set return value and return type from parsed results
-   [x] Update message display after parsing
-   [x] Implement `applyParsedMessageChanges()` in `SelectionController`

### Phase 4: Undo/Redo Support ✅ COMPLETE

-   [x] Create `EditCallMessageEdit` undoable edit class
-   [x] Capture old and new message state using `clone()`
-   [x] Integrate with undo manager in `SelectionController`

### Phase 5: UI Polish ✅ COMPLETE

-   [x] Add ESC key handler to all edit dialogs (`OkCancelDialog`, `AssociationEditorBase`, `TypedEntityEditor`)
-   [x] Remove debug visual overlay (green rectangle) for clickable areas
-   [x] Remove excessive debug logging from `SelectionController` and `SDMessageGR`

### Phase 6: Testing and Remaining Tasks

-   [x] **Auto-invoke inline editing when new call message is created**
-   [x] Modified `AddCallMessageController` to trigger inline editing after message creation
-   [x] Made `startInlineMessageEdit()` public in `SelectionController`
-   [x] Used `SwingUtilities.invokeLater()` to ensure proper timing
-   [ ] Test with various syntax combinations
-   [ ] Test error handling and validation feedback
-   [ ] Test undo/redo functionality
-   [ ] Test with existing messages (reconstruction)
-   [ ] Manual testing of user workflow
-   [ ] Update integration tests if needed
-   [ ] Fix text field position flash (appears at top-right briefly before correct position)

### Phase 7: Documentation

-   [ ] Update CHANGELOG.md with new feature
-   [ ] Remove feature from features.md
-   [ ] Create StudentUML diagram documenting the implementation
-   [ ] Complete implementation summary

## Implementation Summary

[To be filled at completion]

## Design Documentation

[Reference to StudentUML diagram file: diagrams/feature-quick-message-entry.xml]
