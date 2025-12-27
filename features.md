# Future Features

This document tracks potential features and improvements for StudentUML.

## Diagram Relationships

### Support Dependencies between Classes and Interfaces

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Currently, Dependency relationships only work between DesignClass objects. We should support dependencies from a DesignClass to an Interface, which is a common UML pattern when a class uses an interface without implementing it.

**Technical Notes:**

-   `ObjectFactory.newdependency()` (line ~946) currently casts both `from` and `to` to DesignClass
-   `ObjectFactory.newdependencygr()` (line ~779) casts `classb` to ClassGR
-   Need to handle Interface/InterfaceGR in addition to DesignClass/ClassGR
-   May need to create a common base type or use conditional logic

**Use Case:** When documenting that a class depends on an interface (e.g., VersionChecker depends on VersionProvider interface), currently this causes a ClassCastException.

### Stereotype Labels on Dependencies

**Status:** Not implemented  
**Priority:** Low  
**Description:** Add support for stereotype labels on Dependency relationships (e.g., «use», «create», «call», «instantiate»).

**Technical Notes:**

-   Dependency class likely needs a `stereotype` attribute
-   DependencyGR needs to render the stereotype label above/below the dashed arrow
-   XML serialization needs to include stereotype attribute
-   UI needs a way to set/edit the stereotype (probably in properties dialog)

**Use Case:** Provide more semantic information about the nature of dependencies between classes, making diagrams more expressive and aligned with UML standards.

### Distinction Between Navigability and End Ownership in Associations

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add proper support for distinguishing between navigability and end ownership in associations, including proper handling of aggregation types at each end. Currently, StudentUML may conflate these distinct UML concepts or not fully support the UML 2.5 specification for association ends.

**Reference:** https://www.omg.org/spec/UML/2.5/PDF#page=239&zoom=100,142,76

**Technical Notes:**

-   UML 2.5 distinguishes between:
    -   **Navigability**: Indicates whether you can navigate from one class to another (shown by arrow/no arrow)
    -   **Ownership**: Indicates which class owns the association end (shown by dot notation in UML 2.5)
    -   **Aggregation type**: None, shared (aggregation), or composite (composition) at each end
-   Current limitations to investigate:
    -   Are navigability and ownership properly separated in the Association model?
    -   Can aggregation/composition be set independently at each end?
    -   Is the rendering correct according to UML 2.5 notation?
-   Implementation requirements:
    -   Association ends should have independent properties:
        -   `isNavigable` (boolean): whether this end is navigable
        -   `ownedByEnd` (boolean): whether the association end is owned by the classifier at that end
        -   `aggregation` (enum): NONE, SHARED (hollow diamond), COMPOSITE (filled diamond)
    -   Both ends of an association should support these properties independently
    -   Rendering:
        -   Navigable: arrow at the end
        -   Non-navigable: no arrow (or X in UML 2.5 strict mode)
        -   Owned by classifier: dot at the end
        -   Aggregation: hollow diamond at the aggregate end
        -   Composition: filled diamond at the composite end
    -   UI: Property dialogs for each association end to set these independently
    -   XML serialization: Store navigability, ownership, and aggregation for each end
-   Note: Many tools conflate these concepts or use simplified notation; StudentUML should align with UML 2.5 spec

**Use Case:** Properly distinguishing navigability from ownership is important for precise modeling. For example, a bidirectional association (navigable both ways) is different from an owned association end. Composition and aggregation can theoretically exist at either end of an association, though composition at the source is less common. Following UML 2.5 notation ensures diagrams are correctly interpreted and can be exchanged with other UML tools that follow the standard.

## Consistency Checking

### Sanitize Clause Strings for Prolog

**Status:** Not implemented  
**Priority:** High  
**Description:** Clause strings sent to the Prolog-based consistency checker need sanitization. Generic types like `List<T>` and other special characters break Prolog parsing.

**Technical Notes:**

-   Issue occurs in rule-based consistency checking system (`RuleBasedSystemGenerator.java`)
-   Need to identify all places where class names, types, and attributes are converted to Prolog clauses
-   Sanitization should handle:
    -   Generic type parameters: `List<T>` → `List` or `'List<T>'` (quoted)
    -   Special characters: `<`, `>`, brackets, etc.
    -   Package names with dots: `java.util.List` → escape or quote
-   Consider adding a utility method `sanitizeForProlog(String)` in appropriate utility class

**Use Case:** Projects using generic types (List<T>, Map<K,V>, etc.) cause consistency checking to fail with Prolog syntax errors.

## Command-Line Interface

### Open Diagram from Command Line

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for opening a diagram file directly from the command line when launching StudentUML (e.g., `java -jar studentuml.jar path/to/diagram.xml`).

**Technical Notes:**

-   Main class needs to check for command-line arguments in `main(String[] args)`
-   If a file path argument is provided, validate it exists and is readable
-   After ApplicationGUI initialization, automatically open the specified file
-   Should handle both absolute and relative paths
-   May need to handle errors gracefully (file not found, invalid format, etc.)

**Use Case:** Quickly open a specific diagram for review or editing without navigating through the File menu. Useful for automation, scripts, and integration with other tools.

## Class Diagram Features

### Support Package Visibility for Methods and Attributes

**Status:** Not implemented  
**Priority:** Low  
**Description:** Add support for package-private (default) visibility in addition to the existing private, public, and protected visibilities for methods and attributes.

**Technical Notes:**

-   Currently Method and Attribute classes only define: PRIVATE=1, PUBLIC=2, PROTECTED=3
-   Need to add a fourth visibility constant (e.g., PACKAGE=0 or PACKAGE=4)
-   Update `Method.setVisibility()` and `Attribute.setVisibility()` to handle the new value
-   Update UI dialogs/editors to include package-private option (typically shown with no visibility symbol or `~` in UML)
-   Update code generation to correctly generate package-private methods/fields (no access modifier in Java)
-   Update XML serialization/deserialization to handle the new visibility value
-   Ensure backwards compatibility with existing diagrams that don't use package visibility

**Use Case:** In Java, package-private is a common visibility level for internal helper methods and fields. Currently, there's no way to model this in StudentUML DCDs, forcing users to choose private or public instead.

### Support Abstract Methods

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for marking methods as abstract in class diagrams. Abstract methods should be rendered in italics (per UML standard) and code generation should generate abstract methods correctly.

**Technical Notes:**

-   Method class needs a new `boolean isAbstract` attribute (default false)
-   Add `setAbstract(boolean)` and `isAbstract()` methods to Method class
-   Update MethodEditor UI to include an "Abstract" checkbox
-   Update Method.toString() or rendering logic to italicize abstract method names
-   Update XML serialization: add `abstract="true/false"` attribute to Method elements
-   Code generation: abstract methods should not have method bodies
-   Validation: only methods in abstract classes or interfaces should be marked abstract
-   Interface methods are implicitly abstract (may not need explicit flag for interfaces)

**Use Case:** When modeling abstract classes with template method patterns or interface-like abstract base classes, abstract methods are essential. Currently, there's no way to distinguish abstract methods from concrete ones in DCDs.

### Simple Box Notation for Classes Without Methods

**Status:** Not implemented  
**Priority:** Low  
**Description:** Add support for displaying classes without attributes and methods as simple boxes containing only the class name, without showing the attributes and methods compartments. This provides a cleaner, more compact representation when method details are not relevant.

**Technical Notes:**

-   Add a display option/flag for classes (e.g., `showSimplified` or `simpleBox` boolean attribute)
-   When enabled for a class with no attributes and methods:
    -   Render only the class name compartment (top section)
    -   Hide the attributes and methods compartments
    -   Reduce vertical size to just fit the class name
-   Consider also supporting this for classes where you want to hide details temporarily (even if attributes and methods exist)
-   UI options:
    -   Right-click context menu: "Show as Simple Box" / "Show Full Details"
    -   Or automatically apply when class has no methods and no attributes
    -   Preference setting for default behavior
-   XML serialization: Add attribute like `displayMode="simple"` or `simplified="true"`
-   Ensure relationships (associations, generalizations, etc.) still connect properly
-   This is similar to UML's notation for showing classes at different levels of detail

**Use Case:** In high-level conceptual class diagrams (CCDs) or when showing architectural overviews, you often want to show classes and their relationships without cluttering the diagram with attributes and method details. For example, showing domain entities or DTOs that are primarily data containers, or showing external/third-party classes where internal details aren't relevant. A simple box with just the class name makes the diagram cleaner and easier to read while still conveying the essential structure.

## Sequence Diagram Features

### Support Found Messages

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for "found messages" in sequence diagrams. A found message is a message whose sender is not specified - it appears to come from outside the system. This can be used as an alternative to using an Actor to start a sequence diagram.

**Technical Notes:**

-   UML 2.x defines found messages as messages with an unspecified sender
-   Graphically represented as an arrow coming from the left edge of the diagram (no lifeline source)
-   Need to create a `FoundMessageGR` class or extend `CallMessageGR` to support null/undefined `from` attribute
-   XML serialization needs to handle missing or special `from` value
-   UI: Add "Found Message" option to message creation tools
-   Rendering: Arrow should start from diagram edge, not from an object lifeline
-   Consider similar support for "lost messages" (no receiver specified)

**Use Case:** When modeling interactions triggered by external events (timers, external systems, callbacks) where the specific sender is not part of the system being modeled. Provides a cleaner alternative to creating an Actor for every external stimulus.

### Support Static Method Calls to Classes

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for calling static methods on classes (not object instances) in sequence diagrams. Messages should be sent to the class itself rather than to an object instance.

**Technical Notes:**

-   UML represents static method calls as messages sent to a class rather than an instance
-   Two possible notations:
    1. **Metaclass stereotype:** Show `<<metaclass>>` stereotype above the object name, with no class specified (e.g., just `VersionLoader` instead of `loader : VersionLoader`)
    2. **Class instance notation:** Show object as an instance of `Class` (e.g., `Customer : Class`), with or without the `<<metaclass>>` stereotype
-   `SDObject` or `SDObjectGR` needs to support:
    -   Optional flag `isMetaclass` or `isStatic`
    -   Rendering of `<<metaclass>>` stereotype when flag is set
    -   Alternative rendering: `ClassName : Class` format
-   UI: Add checkbox or option in object creation dialog to mark as metaclass/static
-   XML serialization: Add attribute like `metaclass="true"` or `static="true"`
-   Messages to metaclass objects should typically call static methods from the class diagram

**Use Case:** When modeling static utility methods (e.g., `VersionLoader.getCurrentVersion()`, `Math.sqrt()`, factory methods) in sequence diagrams. Currently, these must be shown as regular object instances, which is semantically incorrect since no instance is created. Showing these as class-level calls makes the diagram more accurate and aligned with UML standards.

### Support Polymorphic Messages to Abstract Classes and Interfaces

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for sending messages to abstract classes or interfaces in sequence diagrams, representing polymorphic method calls. The abstract type should be shown with `<<abstract>>` or `<<interface>>` stereotype, and the diagram should not show implementation details. Alternative implementations should only be shown when using concrete class instances (potentially using found message notation).

**Technical Notes:**

-   `SDObject` needs to support abstract classes and interfaces as types
-   Rendering should show stereotypes:
    -   `<<abstract>>` above object name for abstract classes
    -   `<<interface>>` above object name for interfaces
-   When a message is sent to an abstract type/interface object:
    -   The method call is shown but no implementation details should be revealed
    -   The lifeline continues normally (no activation bar expansion showing internal calls)
-   For showing concrete implementations:
    -   Create separate `SDObject` instances for concrete classes (e.g., `concreteImpl : ConcreteClass`)
    -   Use found messages to show the actual implementation execution on the concrete instance
    -   This separates the polymorphic call from the concrete implementation
-   XML serialization:
    -   Support Interface and AbstractClass types in addition to DesignClass
    -   May need stereotype attribute: `stereotype="interface"` or `stereotype="abstract"`
-   UI: Allow selecting interfaces/abstract classes when creating SD objects
-   Example pattern:
    ```
    caller -> provider : <<interface>> : getLatestVersion()
    [found message] -> concreteProvider : GitHubVersionProvider : getLatestVersion()
    ```

**Use Case:** When modeling designs that rely on abstraction and polymorphism (Strategy pattern, dependency injection, plugin architectures), it's important to show that clients depend on abstract types, not concrete implementations. For example, showing `VersionChecker` calling `getLatestVersion()` on a `provider : <<interface>> VersionProvider` makes it clear that any implementation can be used. Concrete implementations (like `GitHubVersionProvider`) can be shown separately using found messages to demonstrate actual execution flow without coupling the abstract interaction to a specific implementation.

### Support Combined Fragments

**Status:** Not implemented  
**Status:** Not implemented  
**Priority:** High  
**Description:** Add support for UML 2.x combined fragments in sequence diagrams, including alternatives (alt), options (opt), loops (loop), parallel execution (par), and other interaction operators. Combined fragments allow modeling of control flow, conditionals, and iteration in sequence diagrams.

**Reference:** https://www.uml-diagrams.org/sequence-diagrams-combined-fragment.html

**Technical Notes:**

-   UML 2.x defines 12 interaction operators for combined fragments:
    -   **alt** (alternatives): mutually exclusive conditional paths (if-then-else)
    -   **opt** (option): optional execution (if-then)
    -   **loop** (loop): repeated execution with guard condition
    -   **par** (parallel): concurrent/parallel execution
    -   **break**: breaking/exception handling
    -   **critical**: critical region (atomic execution)
    -   **neg** (negative): invalid/prohibited interaction
    -   **assert**: mandatory/required interaction
    -   **strict**: strict sequential ordering
    -   **seq**: weak sequential ordering
    -   **ignore**: ignore certain messages
    -   **consider**: consider only certain messages
-   Implementation requirements:
    -   Create `CombinedFragmentGR` class for graphical representation
    -   Create `CombinedFragment` domain class with:
        -   Operator type (alt, opt, loop, etc.)
        -   Guard conditions for each operand
        -   Nested message sequences
        -   Coverage (which lifelines are included)
    -   Rendering:
        -   Rectangle with rounded corners covering relevant lifelines
        -   Operator label in pentagon in upper left corner (e.g., "alt", "loop")
        -   Dashed horizontal lines separating operands (for alt)
        -   Guard conditions in square brackets (e.g., `[x > 0]`, `[else]`)
        -   Messages within the fragment region
    -   Support nesting of fragments (e.g., loop inside alt)
    -   XML serialization for fragment structure, operators, and guards
    -   UI: Tools to create/edit fragments, set operators and guards
-   Start with most common operators: **alt**, **opt**, **loop** (Priority 1)
-   Add additional operators in subsequent phases (Priority 2)

**Use Case:** Combined fragments are essential for modeling realistic control flow in sequence diagrams. For example:

-   **alt**: Show conditional behavior (e.g., "if shouldNotify() then showDialog() else do nothing")
-   **opt**: Show optional operations (e.g., "[new version available] show update dialog")
-   **loop**: Show repeated operations (e.g., "[for each item] process item")
    Without combined fragments, sequence diagrams can only show simple linear flows, limiting their usefulness for documenting complex interactions with conditionals, loops, and error handling.

## Additional Potential Features

_Add new feature requests below this line_
