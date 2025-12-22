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

### Test-Driven Bug Fixes

**When an issue is reported, follow this workflow:**

1. **Document the issue** in the appropriate file (e.g., `manual-tests.md` or inline comments)
2. **Create a failing test** that reproduces the issue
3. **Fix the code** to address the root cause
4. **Verify the test passes** after the fix
5. **Update documentation** to reflect the resolution

This ensures issues are properly tested and prevents regressions.

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

-   Target: Java 8 (`maven.compiler.source/target = 1.8`)
-   UI: Swing with FlatLaf themes (v3.0) for modern look-and-feel
-   JSON handling: Jackson 2.14.1
-   JUnit 4.13 for testing

## Settings and Configuration

User preferences stored at: `~/.java/.userPrefs/edu/city/studentuml/util/prefs.xml`
Includes default paths, fill colors for themes, recent files, etc.
