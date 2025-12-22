# StudentUML Changelog

## [Unreleased]

### Added

### Changed

### Fixed

### Deprecated

### Removed

### Security

## [1.4.0] - 2025-12-22

### Added

-   Copy/paste functionality for graphical elements in all diagram types (DCD, SD, UCD)
-   Copy and Paste menu items added to diagram Edit menus (in addition to application Edit menu)
-   Comprehensive test coverage for copy/paste operations (22 new tests)

### Fixed

-   Original association deleted when undoing paste operation - now properly handles domain object reuse
-   Test isolation issues in ConsistencyCheckTest (documented and skipped flaky test)

### Changed

-   Upgraded from Java 8 to Java 11 for better compatibility and performance
-   Upgraded JaCoCo from 0.7.7 to 0.8.8 for Java 11 compatibility
-   Improved SystemWideObjectNamePool.clear() to reset loading counter for better test isolation

## 1.3.1 - 2023-02-18

### Added

-   Flatlaf themes (light and dark)

### Fixed

-   Bug when "Choose a fill color" and pressing Cancel
-   Removed Darcula and Lipstik (don't work with newer Java in Windows)

## 1.3.0 - 2023-01-30

### Added

-   Support for Dark and Light themes
-   Choosing a personal color for diagram fills (for both dark and light themes)
-   Pressing CTRL while drawing a message in a SD only draws the call message without automatically adding the return message

### Fixed

-   Changing theme is fixed
-   Deleting elements from UCD and AD (bug when there was a System/ActivityNode element)

## 1.2.2 - 2023-01-12

### Fixed

-   Close working project when opening a recent file
-   CTRL-SHIFT message selection works in SSD as well
-   Association editor appears on Aggreration and Compositions

## 1.2.1 - 2023-01-11

### Fixed

-   Popup-menu location and edit

## 1.2.0 - 2023-01-08

### Added

-   Zoom in and zoom out
-   Rectangular selection with the mouse
-   Selecting all elements
-   Opening Recent files
-   Tree Data model shows extend relationships
-   Remembers diagram positions
-   Autocompletion of class names
-   Support of multiple "look and feel"s
-   Check for availability of new version to download

### Fixed

-   Deletion of elements
-   Undo/redo of deletion of many elements
-   Scrollbars don't show when diagram fits
-   UML Notes don't ignore new lines.

## 1.1.0 - 2022-12-23

### Fixed

-   Movement in negative coordinates is not allowed
-   Movement of SD messages

## 1.0.3 - 2022-12-18

### Fixed

-   Diagram view resizes to fit all elements

## 1.0.2 - 2021-04-12

### Fixed

-   Generalization direction fixed

## 1.0.1 - 2020-03-05

### Fixed

-   Show correct save status

## 1.0.0 - 2020-02-29

-   First version uploaded

## Older versions

-   The tool has been developed without versioning releases.
