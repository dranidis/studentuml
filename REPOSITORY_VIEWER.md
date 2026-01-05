# Repository Viewer Feature

## Overview

Added a non-blocking Repository Viewer window that displays the current state of the CentralRepository and logs all changes in real-time as they occur. This feature helps developers understand how the repository works during diagram editing operations.

## Features Implemented

### 1. RepositoryViewerDialog

**File**: `src/main/java/edu/city/studentuml/view/gui/RepositoryViewerDialog.java`

A non-modal JDialog that provides:

-   **Current Repository State Panel**: Shows all domain objects currently in the repository

    -   Design Classes, Interfaces, Conceptual Classes
    -   Actors, Systems, Use Cases
    -   SD Objects, Multi Objects, Actor Instances, System Instances
    -   Associations, Aggregations, Generalizations, Dependencies, Realizations
    -   SD Messages
    -   Total element count

-   **Change Log Panel**: Real-time log of repository operations

    -   Edit operations with old → new name transitions
    -   Add operations (not yet fully implemented)
    -   Remove operations (not yet fully implemented)
    -   Type operations (not yet fully implemented)
    -   Timestamps for each operation
    -   Auto-scrolls to show latest changes

-   **Controls**:
    -   Refresh State button: Manually update the repository state display
    -   Clear Log button: Clear the change log history

### 2. Repository Change Listener System

**File**: `src/main/java/edu/city/studentuml/model/repository/RepositoryChangeListener.java`

An observer interface for repository changes:

```java
void onAdd(String entityType, String entityName);
void onEdit(String entityType, String oldName, String newName);
void onRemove(String entityType, String entityName);
void onTypeOperation(String operation, String typeName);
```

### 3. CentralRepository Integration

**File**: `src/main/java/edu/city/studentuml/model/repository/CentralRepository.java`

Enhanced with:

-   Listener registration: `addRepositoryChangeListener()`, `removeRepositoryChangeListener()`
-   Notification methods: `notifyAdd()`, `notifyEdit()`, `notifyRemove()`, `notifyTypeOperation()`
-   Edit methods now notify listeners after successful changes:
    -   `editObject()` - SDObject editing
    -   `editMultiObject()` - MultiObject editing
    -   `editActorInstance()` - ActorInstance editing
    -   `editSystemInstance()` - SystemInstance editing
    -   `editObjectNode()` - ObjectNode editing

### 4. Menu Integration

**Files**:

-   `src/main/java/edu/city/studentuml/view/gui/menu/MenuBar.java`
-   `src/main/java/edu/city/studentuml/view/gui/ApplicationGUI.java`

Added new "Tools" menu with:

-   **Repository Viewer** menu item (Ctrl+Shift+R)
-   `openRepositoryViewer()` method in ApplicationGUI
-   Singleton pattern: reuses existing window if already open, brings it to front

## Usage

1. Start StudentUML application
2. Open any project or create diagrams
3. Access via: **Tools → Repository Viewer** or press **Ctrl+Shift+R**
4. The viewer window opens and shows current repository state
5. Edit any diagram elements (objects, actors, etc.)
6. Watch the Change Log panel for real-time updates
7. Use "Refresh State" to manually update the left panel
8. Window stays open and updates automatically during editing
9. Close window or keep it open while working

## Technical Details

### Architecture

-   **Observer Pattern**: RepositoryChangeListener interface with CentralRepository as subject
-   **Non-blocking**: JDialog with `modal=false` doesn't block main application
-   **Automatic Cleanup**: Window listener removes listener on close
-   **Null-safe**: Checks for null changeListeners (transient field, not serialized)
-   **Thread-safe notifications**: Iterates copy-on-write for listener notifications

### Change Tracking

Currently tracking these edit operations:

-   SDObject name and type changes
-   MultiObject name and type changes
-   ActorInstance name and actor changes
-   SystemInstance name and system changes
-   ObjectNode name, type, and state changes

Future enhancements can add tracking for:

-   Add operations (addClass, addActor, etc.)
-   Remove operations (removeClass, removeActor, etc.)
-   Type operations from TypeOperation.apply()

### Display Format

Change log format: `[HH:mm:ss.SSS] EDIT EntityType: 'oldName' -> 'newName'`

Example:

```
[13:05:42.123] Repository Viewer opened
[13:05:45.456] EDIT SDObject: 'controller' -> 'mainController'
[13:05:47.789] EDIT ActorInstance: 'user' -> 'customer'
```

## Testing

**File**: `src/test/java/edu/city/studentuml/view/gui/RepositoryViewerDialogTest.java`

Three unit tests verify:

1. `testEditObjectNotifiesListener()` - Edit operations trigger notifications
2. `testMultipleEditsTracked()` - Multiple edits are tracked correctly
3. `testListenerRemoval()` - Removed listeners don't receive notifications

All tests pass successfully.

## Build Status

✅ Compilation: SUCCESS
✅ Unit Tests: 3/3 passing
✅ Package: studentuml-1.5.0-SNAPSHOT-jar-with-dependencies.jar built successfully

## Future Enhancements

1. Add tracking for all add/remove operations throughout CentralRepository
2. Add tracking for type operations in TypeOperation.apply()
3. Add filtering options to show only specific entity types
4. Add search/filter in change log
5. Add export change log to file
6. Add statistics panel (adds per minute, most edited entities, etc.)
7. Add visual diff for object states before/after edit
8. Add ability to "undo" directly from viewer
9. Color-code operations (green=add, blue=edit, red=remove)
10. Add graph visualization of repository relationships
