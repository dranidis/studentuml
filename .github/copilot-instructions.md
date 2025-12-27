# StudentUML AI Coding Agent Instructions

## Project Overview

StudentUML is a Java Swing-based desktop UML diagram editor supporting multiple diagram types: Use Case (UCD), Sequence (SD/SSD), Class (CCD/DCD), and Activity Diagrams (AD). The application uses MVC architecture with Observable/Observer pattern for UI updates and supports code generation, consistency checking via rule-based systems, and undo/redo functionality.

## Architecture

### Core MVC Pattern

-   **Models**: `DiagramModel` subclasses (DCDModel, CCDModel, SDModel, etc.) store graphical elements and extend `Observable`
-   **Views**: `DiagramView` subclasses implement `Observer`, automatically repaint on model changes
-   **Controllers**: `SelectionController` and `AddElementController` subclasses handle user interactions

### Key Components

-   **UMLProject** (Singleton): Top-level container managing all diagrams and the central repository
-   **CentralRepository**: Domain model layer storing UML concepts (classes, interfaces, messages) without graphical information
-   **GraphicalElement Hierarchy**: Each domain concept has a corresponding `*GR` class (e.g., `ClassGR`, `UseCaseGR`) for rendering

### Data Layer Separation

The domain model (`src/main/java/edu/city/studentuml/model/domain/`) is completely separate from graphical representation (`src/main/java/edu/city/studentuml/model/graphical/`). All domain elements are stored in `CentralRepository`, while diagram-specific graphical wrappers are in `DiagramModel.graphicalElements`.

## Build and Test Workflow

### Essential Commands

```bash
# Build (skip tests for faster builds)
mvn clean package -DskipTests

# Run application
java -jar target/studentuml-1.3.1-jar-with-dependencies.jar

# Run tests with coverage
mvn clean test
mvn jacoco:report  # Coverage report in target/site/jacoco/
```

### Local Maven Repository

This project uses a local Maven repo (`local-maven-repo/`) for custom JARs:

-   `ubc.cs:jlogic` and `ubc.cs:builtinsLib` (logic/consistency checking)
-   Referenced in `pom.xml` via `file:///${project.basedir}/local-maven-repo`
-   See README.md for commands to recreate local repo if needed

### Testing Patterns

-   Tests use JUnit 4 (`@Test`, `@Before`, `@After` annotations)
-   Test helpers create models without GUI: see `src/test/java/edu/city/studentuml/controller/Helper.java` for examples
-   Models can exist independently of frames/views for testing

#### CRITICAL: Never Duplicate Production Code in Tests (Non-Negotiable)

**Tests MUST call actual production code, not copy it.** This is a fundamental principle that cannot be compromised.

-   ❌ **WRONG**: Copying production logic into test methods to execute it
-   ✅ **CORRECT**: Calling production methods directly from tests
-   ❌ **WRONG**: Reimplementing algorithms in test code to verify behavior
-   ✅ **CORRECT**: Invoking the actual production methods and asserting on their results
-   ❌ **WRONG**: Creating "test helper" methods that duplicate production logic
-   ✅ **CORRECT**: Using production methods directly, making them `protected` or package-private if needed for testing

**Why this matters:**

1. Duplicated code masks bugs: if the test copies buggy logic, it will pass even when production code is wrong
2. Double maintenance burden: changes require updating both production and test code
3. Tests become unreliable: they test the copy, not the actual production behavior
4. Violates DRY principle: reduces code quality and maintainability

**If production code is not accessible for testing (e.g., `private` methods with hardcoded dependencies):**

1. Refactor production code to be testable (extract methods, use dependency injection)
2. Make methods package-private (`static` without modifier) for testing
3. Extract logic into separate testable classes if needed

**Examples:**

```java
// ❌ WRONG: Copying version parsing logic into test
@Test
public void testVersionParsing() {
    String tagName = "v1.3.1";
    String version = tagName.substring(1); // DUPLICATING PRODUCTION LOGIC
    assertEquals("1.3.1", version);
}

// ✅ CORRECT: Testing actual production method with mock
@Test
public void testParseVersionFromJSON_withVPrefix() {
    GitHubVersionProvider provider = new GitHubVersionProvider(mockUrl);
    String json = "{\"tag_name\":\"v1.3.1\"}";
    String version = provider.parseVersionFromJSON(json);
    assertEquals("1.3.1", version);
}
```

This principle applies to ALL tests without exception. If you find yourself copying production code into a test, STOP and refactor the production code to be testable instead.

#### Design for Testability

**When creating new classes or refactoring existing ones, follow these principles:**

1. **Separate Concerns**: Utility classes should not handle UI concerns (dialogs, HTML rendering). Extract UI logic to caller or separate view classes.
2. **Dependency Injection**: Avoid static methods with hardcoded dependencies. Use constructor injection or method parameters.
3. **Interface Extraction**: Create interfaces for external dependencies (HTTP clients, file systems) to enable mocking.
4. **Avoid Static State**: Prefer instance methods over static methods for better testability.
5. **Package-Private Methods**: Make methods package-private (no modifier) instead of `private` when they need to be tested directly.

**Example: Refactoring for Testability**

```java
// ❌ BEFORE: Not testable - static methods, hardcoded HTTP, UI responsibility
public class NewversionChecker {
    private static final String URL = "https://api.github.com/...";

    public static void checkForNewVersion(JFrame frame) {
        String version = getFromURL(); // Hardcoded HTTP call
        if (isNewer(version)) {
            JOptionPane.showMessageDialog(frame, "New version!"); // UI responsibility
        }
    }
}

// ✅ AFTER: Testable - DI, separated concerns, mockable dependencies
public class VersionChecker {
    private final VersionProvider versionProvider;
    private final String currentVersion;

    public VersionChecker(VersionProvider versionProvider, String currentVersion) {
        this.versionProvider = versionProvider;
        this.currentVersion = currentVersion;
    }

    public VersionCheckResult checkForNewVersion() {
        String latest = versionProvider.getLatestVersion();
        return new VersionCheckResult(currentVersion, latest, isNewer(latest));
    }

    // Caller handles UI:
    // VersionCheckResult result = checker.checkForNewVersion();
    // if (result.isNewerAvailable()) { showDialog(result); }
}
```

### Test-Driven Bug Fixes

**When an issue is reported, follow this workflow:**

1. **Document the issue** in the appropriate file (e.g., `manual-tests.md` or inline comments)
2. **Create a failing test** that reproduces the issue
3. **Fix the code** to address the root cause
4. **Verify the test passes** after the fix
5. **Update documentation** to reflect the resolution

This ensures issues are properly tested and prevents regressions.

## Version Control and Release Management

### Semantic Versioning

This project follows [Semantic Versioning](https://semver.org/) (MAJOR.MINOR.PATCH):

-   **MAJOR** version: Incompatible API changes or major architectural changes
-   **MINOR** version: New functionality added in a backward-compatible manner
-   **PATCH** version: Backward-compatible bug fixes

**Version Bumping Guidelines:**

-   **Bug fixes only** → Increment PATCH (e.g., 1.3.0 → 1.3.1)
-   **New features** (backward-compatible) → Increment MINOR, reset PATCH (e.g., 1.3.1 → 1.4.0)
-   **Breaking changes** → Increment MAJOR, reset MINOR and PATCH (e.g., 1.4.0 → 2.0.0)

**SNAPSHOT versions** (e.g., 1.4.0-SNAPSHOT) indicate unreleased development versions. When a release is ready:

1. Remove -SNAPSHOT suffix
2. Update CHANGELOG.md with release date
3. Tag the release in git
4. Bump to next SNAPSHOT version for continued development

### CHANGELOG.md Updates

**IMPORTANT: Update `CHANGELOG.md` every time a branch is merged into develop.**

When merging feature branches, bug fixes, or any changes to develop:

1. Add entries under the `[Unreleased]` section
2. Follow the format: `### Added`, `### Changed`, `### Fixed`, `### Deprecated`, `### Removed`, `### Security`
3. Include brief description of changes with issue/PR references if available
4. Example:

    ```markdown
    ## [Unreleased]

    ### Added

    -   Copy/paste functionality for graphical elements in all diagram types

    ### Fixed

    -   Original association deleted when undoing paste operation (#123)

    ### Changed

    -   Upgraded from Java 8 to Java 11 for better compatibility
    -   Upgraded JaCoCo from 0.7.7 to 0.8.8
    ```

This maintains a clear history of changes for each release and helps with version management.

## Critical Conventions

### Reflection and Naming

**DO NOT rename methods marked with "DO NOT CHANGE THE NAME: CALLED BY REFLECTION"** - these are invoked by the consistency checker's rule-based system (`RuleBasedSystemGenerator.java`). Examples:

-   `DiagramModel.getName()`
-   `DiagramModel.getGraphicalElements()`

### Undo/Redo System

-   All model changes must create `UndoableEdit` instances in `util/undoredo/`
-   Common edit types: `AddEdit`, `DeleteEdit`, `MoveEdit`, `CompositeDeleteEdit`
-   `DiagramInternalFrame` manages per-diagram `UndoManager`

### XML Serialization

-   Diagrams are saved/loaded as XML via `XMLStreamer` and `IXMLCustomStreamable`
-   Jackson annotations (`@JsonIncludeProperties`) control JSON export for certain features
-   Example diagram files in `diagrams/` directory

### Controller Factory Pattern

`AddElementControllerFactory` uses switch statements to create diagram-specific controllers. When adding new element types:

1. Add case to factory's `makeController()` method
2. Create controller extending `AddElementController`
3. Override `makeGraphicalElement()` to instantiate proper `*GR` class

## Diagram Type Constants

Use `DiagramType` constants (not magic numbers):

-   `DiagramType.UCD` = 0 (Use Case)
-   `DiagramType.SSD` = 1 (System Sequence)
-   `DiagramType.SD` = 2 (Sequence)
-   `DiagramType.CCD` = 3 (Conceptual Class)
-   `DiagramType.DCD` = 4 (Design Class)
-   `DiagramType.AD` = 5 (Activity)

## Code Generation

-   Entry point: `CodeGenerator.java` generates Java source from DCDs
-   `CodePreparation.java` prepares design classes from diagrams
-   Output path and update mode controlled by user preferences
-   Example projects in `examples/codeGeneration/`

## Java Version and Dependencies

-   Target: Java 11 (`maven.compiler.source/target = 11`)
-   UI: Swing with FlatLaf themes (v3.0) for modern look-and-feel
-   JSON handling: Jackson 2.14.1
-   JUnit 4.13 for testing
-   JaCoCo 0.8.8 for code coverage

## Settings and Configuration

User preferences stored at: `~/.java/.userPrefs/edu/city/studentuml/util/prefs.xml`
Includes default paths, fill colors for themes, recent files, etc.
