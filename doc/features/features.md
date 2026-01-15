# Future Features

This document tracks potential features and improvements for StudentUML.

## Known Bugs

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

### Edge Reconnection in Activity Diagrams

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Enable users to reconnect control flows and object flows in Activity Diagrams by dragging edge endpoints to different nodes. This allows for easier diagram modification without having to delete and recreate edges.

**Technical Notes:**

-   Currently, edges in Activity Diagrams cannot be reconnected - must be deleted and recreated
-   Similar functionality exists in other diagram types (e.g., associations in class diagrams)
-   Need to implement reconnection for both edge types:
    -   `ControlFlowGR` - control flow edges
    -   `ObjectFlowGR` - object flow edges (if applicable)
-   Implementation approach:
    -   Add mouse handlers to detect dragging of edge endpoints
    -   Visual feedback: highlight valid target nodes during drag
    -   Validation: check if reconnection violates semantic constraints
    -   Update domain model: change source/target in `ControlFlow` or `ObjectFlow`
    -   Undo/redo support: create reconnection edit
-   Validation during reconnection must enforce:
    -   Initial node degree constraint (exactly 1 outgoing)
    -   Final node degree constraint (exactly 1 incoming)
    -   Action node constraints (at most 1 incoming, 1 outgoing)
    -   Decision/Merge/Fork/Join node constraints
    -   Edge type compatibility (control flow vs object flow)
    -   Same activity context
-   UI considerations:
    -   Cursor feedback when hovering over edge endpoints
    -   Visual indication of drag operation
    -   Error message if reconnection violates constraints
    -   Snap to valid target nodes
-   Edge cases to handle:
    -   Reconnecting to the same node (reflexive edge)
    -   Reconnecting both source and target
    -   Reconnecting while maintaining guard conditions (for decision nodes)
-   Undo/redo:
    -   Create `ReconnectEdgeEdit` undoable edit
    -   Store old and new source/target nodes
    -   Handle edge repositioning on undo

**Use Case:** When refactoring an activity diagram, users often need to change the flow connections. Currently, this requires deleting the edge and creating a new one, which is tedious and loses properties like guards. With reconnection support, users can simply drag an edge endpoint to a different node, making diagram modification much faster and preserving edge properties. For example, when reorganizing a workflow, a user could drag a control flow from one action to another without losing the flow's configuration.

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

### Stereotype Support for Objects in Sequence Diagrams

**Status:** Not implemented  
**Priority:** Medium  
**Description:** Add support for stereotypes on objects in Sequence Diagrams (SD/SSD) to enable modeling of static method calls, abstract classes, and interfaces. This allows objects to be annotated with stereotypes like `<<metaclass>>`, `<<interface>>`, `<<abstract>>`, or custom stereotypes, providing more semantic information about the object's role in the interaction.

**Technical Notes:**

-   Add stereotype support to `SDObject` domain model:
    -   Add `stereotype` field (String, optional)
    -   Common values: `"metaclass"`, `"interface"`, `"abstract"`, or custom values
    -   Default: null or empty (no stereotype)
-   Update `SDObjectGR` rendering:
    -   Display stereotype above object name in guillemets: `<<stereotype>>`
    -   Position between top border and object name
    -   Use appropriate font size and styling
-   UI enhancements:
    -   Add stereotype field to object creation/edit dialog
    -   Dropdown with common stereotypes: `<<metaclass>>`, `<<interface>>`, `<<abstract>>`, or allow custom entry
    -   Optional: "None" to clear stereotype
-   XML serialization:
    -   Add `stereotype="value"` attribute to SDObject XML elements
    -   Ensure backward compatibility (missing stereotype = no stereotype)
-   Use cases enabled by stereotypes:

    **Static Method Calls (<<metaclass>>):**

    -   Represent class-level operations (e.g., factory methods, utility functions)
    -   Example: `Math <<metaclass>>` receiving `sqrt(x)` message
    -   Semantically correct representation of static method invocations

    **Polymorphic Messages (<<interface>> or <<abstract>>):**

    -   Show dependency on abstractions rather than concrete implementations
    -   Example: `provider : VersionProvider <<interface>>`
    -   Makes it clear that any implementation can be used
    -   Can be combined with found messages to show concrete implementation separately

    **Custom Stereotypes:**

    -   Support domain-specific stereotypes (e.g., `<<controller>>`, `<<service>>`, `<<repository>>`)
    -   Enables architectural pattern documentation

**Implementation Considerations:**

-   Type selection: When stereotype is `<<interface>>` or `<<abstract>>`, allow selecting from available interfaces/abstract classes in the repository
-   Validation: Optionally validate that stereotype matches the object's type (e.g., `<<interface>>` should refer to an interface)
-   Code generation: Stereotyped objects may affect generated code (static calls, interface types)
-   Consistency checking: Validate stereotype usage (e.g., `<<metaclass>>` should only receive static method calls)

**Use Cases:**

1. **Static Utility Methods**: Model calls like `VersionLoader.getCurrentVersion()` or `Math.sqrt()` by creating a `VersionLoader <<metaclass>>` object, making it semantically correct rather than showing a false instance.

2. **Dependency Inversion**: Show a client depending on `provider : VersionProvider <<interface>>` rather than a concrete implementation, documenting the use of abstraction and polymorphism (Strategy pattern, dependency injection).

3. **Architectural Patterns**: Use stereotypes like `<<controller>>` or `<<service>>` to document architectural layers and responsibilities in the sequence diagram.

4. **Testing Scenarios**: Use `<<mock>>` or `<<stub>>` stereotypes to document test doubles in test scenario diagrams.

## Additional Potential Features

_Add new feature requests below this line_
