# Future Features

This document tracks potential features and improvements for StudentUML.

## Known Bugs

### Association Class Display Bug in DCD

**Status:** Bug - Not fixed  
**Priority:** High  
**Description:** When creating an association class between two classes in a Design Class Diagram (DCD), all three classes (the two original classes and the association class) are incorrectly displayed as association classes with dashed lines connecting them. The two original classes should be displayed as normal classes with solid borders, only the association class itself should have the dashed line to the association.

**Steps to Reproduce:**

1. Create a new DCD diagram
2. Add two regular classes (e.g., Class A and Class B)
3. Create an association class connecting them
4. Observe that all three classes now appear as association classes with dashed lines

**Expected Behavior:**

-   Class A: Normal class with solid border
-   Class B: Normal class with solid border
-   Association Class: Connected to the association line with a dashed line
-   Only the association class should have dashed line representation

**Technical Notes:**

-   Issue likely in `AssociationClassGR` or the rendering logic for associations
-   May be related to how `ClassGR` instances are marked or styled when part of an association class
-   Check `AssociationClassGR.paint()` and related rendering methods
-   Verify that regular classes maintain their style independently of participation in association classes

**Use Case:** Association classes are a fundamental UML construct. The current bug makes diagrams confusing and non-standard, as it's unclear which classes are actual association classes versus regular classes participating in associations.

## Diagram Relationships

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

### Activity Diagram Node Degree Constraints

**Status:** Not implemented  
**Priority:** High  
**Description:** Enforce control-flow degree constraints in Activity Diagrams:

-   Initial nodes must have exactly one outgoing Control Flow.
-   Final nodes (Activity Final and Flow Final) must have exactly one incoming Control Flow.

Violations should be prevented at creation time (with a user-facing error/warning) and detected during consistency checks.

**Technical Notes:**

-   Domain types: `InitialNode`, `ActivityFinalNode`, `FlowFinalNode` under `edu.city.studentuml.model.domain`.
-   Graphical types: `InitialNodeGR`, `ActivityFinalNodeGR`, `FlowFinalNodeGR` under `edu.city.studentuml.model.graphical`.
-   Edge types: `ControlFlow` (domain) and `ControlFlowGR` (graphical).
-   Creation path to validate (to locate exact enforcement points):
    -   Add-element controllers for AD (e.g., `AddControlFlowController` or equivalent) or a generic add-edge controller.
    -   Reconnection logic in `EdgeGR` and specific GRs (to block making an invalid degree via reconnect).
    -   Consider a model-level validation in `NodeComponent` or a dedicated validator that counts `getOutgoingEdges()` / `getIncomingEdges()`.
-   Suggested enforcement:
    -   Before creating a `ControlFlow` where source is `InitialNode`, check `source.getOutgoingEdges().isEmpty()`; if not empty, block and show: "Initial node can have a single outgoing control flow".
    -   Before creating a `ControlFlow` where target is `ActivityFinalNode` or `FlowFinalNode`, check `target.getIncomingEdges().isEmpty()`; if not empty, block and show: "Final nodes can have a single incoming control flow".
    -   Mirror these checks in reconnection handlers (`ControlFlowGR` / `EdgeGR`) to prevent violating the constraints via reconnect.
    -   Add consistency rules to the Prolog ruleset: count incoming/outgoing degrees for final/initial nodes and report violations.
-   XML Save/Load:
    -   On load, optionally run a validation pass to flag diagrams that violate the constraints.
    -   Consider auto-fix option (disabled by default) that removes surplus flows or highlights them.
-   Tests:
    -   Unit tests for creation blocking in controllers.
    -   Integration tests: attempt to draw invalid flows and assert error dialogs; ensure valid flows are allowed.

**Use Case:** In UML Activity Diagrams, initial nodes represent the single entry point of the activity, and final nodes represent termination points. Allowing multiple outgoing/incoming flows makes the diagram ambiguous and violates UML semantics. Enforcing these constraints keeps models clean and semantically correct.

### Weight Validation for Control and Object Flows

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Validate that the weight property of Control Flows and Object Flows in Activity Diagrams is a positive integer. The weight determines the number of tokens that traverse the edge when fired, and must be at least 1.

**Technical Notes:**

-   Domain types: `ControlFlow` and `ObjectFlow` under `edu.city.studentuml.model.domain`
-   Both classes have a `weight` property (likely int or String)
-   Need to add validation:
    -   In setters: `setWeight(int weight)` should throw exception or ignore if weight < 1
    -   In editors: `ControlFlowEditor` and `ObjectFlowEditor` should validate input before accepting
    -   Show error message: "Weight must be a positive integer (≥ 1)"
-   Consider default value: Weight defaults to 1 if not specified (standard UML behavior)
-   UI validation:
    -   Add input validation in property dialogs/editors
    -   Use `JSpinner` with minimum value of 1 instead of free-text field
    -   Or use regex validation if text field: `^[1-9][0-9]*$`
-   XML serialization: Ensure invalid weights are not saved or are corrected on load
-   Consistency checking: Add Prolog rule to detect flows with weight < 1
-   Tests:
    -   Unit tests for weight setter validation
    -   UI tests for editor validation
    -   Integration tests for XML load with invalid weights

**Use Case:** In UML Activity Diagrams, the weight property specifies how many tokens must be available on the edge for it to be traversed. A weight of 0 or negative value is semantically invalid and would prevent the flow from ever being traversed or cause undefined behavior. Enforcing positive integer validation ensures diagrams remain semantically valid and prevents modeling errors.

## Class Diagram Features

### Class Renaming Undo/Redo Issues

**Status:** Bug / Not fixed  
**Priority:** High  
**Description:** There is an issue with Classes and renaming them. The logic in `editClassifierWithDialog` is not correct. Renaming to an existing class name and multiple undo/redo leave the repository in inconsistent states.

**Reproduction Steps:**

1. **Scenario 1:** Create class, rename to "A", create another class, rename to "A", then undo → **Expected:** Second class name reverts to "" (empty) **Actual:** The class gets deleted instead
2. **Scenario 2:** Create two classes, rename first to "A", rename second to "B", then rename second to "A", then undo → **Expected:** Undo should work correctly **Actual:** Undo does not work
3. **Scenario 3:** Multiple undos leave the repository with extra classes at the end instead of proper state restoration

**Technical Notes:**

-   Problem location: `GraphicalElement.editClassifierWithDialog()` method (line 407)
-   This method implements "Silent Merge on Conflict" pattern (Pattern 2)
-   Affects: `ClassGR`, `ConceptualClassGR`, `InterfaceGR`
-   Current algorithm:
    -   When name conflict detected: performs silent merge (replaces reference, optionally removes original)
    -   When no conflict: creates undo/redo edit
-   Issues with empty names ("") and multiple renames not handled correctly
-   Undo/redo stack becomes inconsistent with repository state
-   Probably other issues as well

**Investigation Needed:** This requires comprehensive investigation of the name conflict resolution logic, especially:

-   How empty names are handled during undo/redo
-   Whether silent merges should create undo edits or not
-   Repository cleanup when classes are removed/merged
-   Interaction between multiple rename operations and undo stack

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

## Copy/Paste Operations

### UML Note Y-Position in Sequence Diagrams

**Status:** Not implemented  
**Priority:** Low  
**Description:** When pasting UML notes in Sequence Diagrams, the Y-coordinate should be preserved from the original position (with offset), not calculated relative to the mouse cursor. In Sequence Diagrams, all elements are positioned at the top of the diagram, and the mouse Y-coordinate is irrelevant for vertical positioning.

**Technical Notes:**

-   Current behavior: `SelectionController.pasteClipboard()` uses mouse position (currentMouseY) to calculate offsetY for all elements, including UML notes in SD diagrams
-   Expected behavior: In Sequence Diagrams, UML notes should use their original Y-coordinate plus a fixed offset, ignoring mouse Y
-   Implementation:
    -   In `SelectionController.pasteClipboard()`, detect if target diagram is a Sequence Diagram (SDModel or SSDModel)
    -   For SD/SSD diagrams: when calculating offsetY for UMLNoteGR elements, use a fixed offset (e.g., 20 pixels) instead of currentMouseY
    -   For other diagram types: keep current mouse-based positioning behavior
-   Related code: `SelectionController.pasteClipboard()` lines ~770-795 (offset calculation)

**Use Case:** When copying and pasting elements in Sequence Diagrams, UML notes attached to messages or objects should maintain their relative position in the timeline/Y-axis. Using the mouse Y-coordinate causes notes to jump to unexpected positions since SD elements are always positioned at the top regardless of where the user clicks.

## Additional Potential Features

_Add new feature requests below this line_
