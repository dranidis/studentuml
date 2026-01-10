# Plan: Open Diagram from Command Line

## Investigation

### Current Architecture

1. **Entry Point**: `StudentUMLFrame.java`

    - Location: `src/main/java/edu/city/studentuml/frame/StudentUMLFrame.java`
    - Contains `main(String[] args)` method
    - Currently ignores command-line arguments
    - Creates `ApplicationFrame` instance

2. **File Opening Mechanism**: `ApplicationFrame.java`

    - Location: `src/main/java/edu/city/studentuml/view/gui/ApplicationFrame.java`
    - Contains `openProjectFile(String fileName)` method (line 109)
    - Handles file loading, validation, and error handling
    - Manages runtime checking state during load
    - Updates recent files list

3. **ApplicationGUI Initialization**:
    - ApplicationFrame constructor calls parent `ApplicationGUI` constructor
    - GUI components are initialized before we can open a file
    - Frame must be visible and initialized before opening file

### Key Methods to Use

-   `ApplicationFrame.openProjectFile(String fileName)` - Existing method that:
    -   Validates file exists
    -   Closes current project if open
    -   Loads XML project file
    -   Handles errors gracefully
    -   Updates recent files

### Design Decisions

**Approach**: Modify `StudentUMLFrame.main()` to:

1. Check if command-line arguments are provided
2. Create ApplicationFrame as usual (GUI must be initialized first)
3. After GUI initialization, call `openProjectFile()` with the provided path
4. Use `SwingUtilities.invokeLater()` to ensure GUI is ready

**Why this approach?**

-   Reuses existing `openProjectFile()` method (DRY principle)
-   Maintains separation of concerns
-   GUI must be initialized before opening files
-   Handles both absolute and relative paths (existing code does this)
-   Error handling already implemented in `openProjectFile()`

## Affected Components

-   **StudentUMLFrame.java** - Modify `main()` method to:
    -   Parse command-line arguments
    -   Pass filename to ApplicationFrame after initialization
-   **ApplicationFrame.java** - Add method to:
    -   Allow opening file after initialization
    -   Make it easy to call from outside

## TODO Tasks

-   [x] Task 1: Write failing test for command-line argument parsing
-   [x] Task 2: Implement command-line argument handling in StudentUMLFrame.main()
-   [x] Task 3: Add method in ApplicationFrame to open file after initialization
-   [x] Task 4: Test with valid diagram file (absolute path)
-   [x] Task 5: Test with valid diagram file (relative path)
-   [x] Task 6: Test with invalid/non-existent file
-   [x] Task 7: Test with no arguments (normal startup)
-   [x] Task 8: Update documentation and create StudentUML diagram

## Implementation Summary

The command-line file opening feature has been successfully implemented using a clean, test-driven approach. The solution reuses existing infrastructure and maintains thread safety throughout.

### Key Implementation Details

1. **Command-Line Argument Parsing**: Added `parseCommandLineArguments(String[] args)` to extract the file path from command-line arguments. Returns `null` if no arguments provided, allowing normal startup.

2. **File Validation**: Implemented `validateFile(String filePath)` to check:

    - File exists
    - Is a regular file (not directory)
    - Is readable
    - Handles both absolute and relative paths

3. **Main Method Integration**: Modified `StudentUMLFrame.main()` to:
    - Parse command-line arguments
    - Create ApplicationFrame (GUI initialization)
    - Use `SwingUtilities.invokeLater()` to defer file opening until GUI is ready
    - Log warnings for invalid files
    - Reuse existing `ApplicationFrame.openProjectFile()` method

### Thread Safety

The implementation uses `SwingUtilities.invokeLater()` to ensure the file opening operation happens on the Event Dispatch Thread (EDT) after the GUI is fully initialized. This prevents race conditions and ensures the application is ready to handle file loading.

### Testing Coverage

Created comprehensive unit tests (`StudentUMLFrameTest.java`) covering:

-   Valid file arguments
-   No arguments (normal startup)
-   Multiple arguments (uses first)
-   Non-existent files
-   Null paths
-   Relative paths

Manual integration tests verified:

-   ✅ Relative path opening works (`java -jar studentuml.jar diagrams/ad.xml`)
-   ✅ Absolute path opening works
-   ✅ Invalid files show warnings and start normally
-   ✅ No arguments results in normal startup

### Code Quality

-   No duplication: Reuses existing `openProjectFile()` method
-   All 200+ existing tests still pass (no regressions)
-   Methods are package-private (`static`) for testability
-   Follows existing logging patterns

## Design Documentation

[Reference to StudentUML diagram file: `diagrams/feature-cmd-line-open.xml`]
