# Analysis: Lifting `editNameWithDialog` Higher in the GR Hierarchy

Date: 2026-01-01
Branch: `feature/polymorphic-edit-method`

## Goal

Enable more graphical elements to reuse the simple “name edit” workflow (StringEditorDialog + undo/redo + model change) by moving the helper currently implemented in `NodeComponentGR` higher in the hierarchy.

Target: reduce duplicated controller logic and let any graphical element that edits a single string field (typically `name`) call a shared helper.

## Current State

-   Location: `src/main/java/edu/city/studentuml/model/graphical/NodeComponentGR.java`
-   Signature:
    -   `protected boolean editNameWithDialog(EditContext context, String dialogTitle, String fieldLabel, UndoableEditFactory undoableEditFactory)`
    -   Uses `getComponent()` → `NodeComponent` as the domain object
    -   Dependencies:
        -   `EditContext` (model, parent component, undo support)
        -   `StringEditorDialog`
        -   `SystemWideObjectNamePool.reload()`
        -   `NodeComponent.clone()` (for undo)
        -   `UndoableEditFactory` (NodeComponent-specific) to post an `UndoableEdit`
-   Callers (examples):
    -   `ActivityNodeGR.edit(...)`
    -   `DecisionNodeGR.edit(...)`
    -   `ActionNodeGR.edit(...)`
-   Limitation: The helper is tied to `NodeComponent` and isn’t usable from other element families that also have “simple name edit” needs (e.g., potential use in `UMLNoteGR`, simple UCD elements, or any other GR with a one-field dialog).

## What blocks moving it to `GraphicalElement` today

1. Domain dependency is concrete:
    - The helper grabs the domain via `getComponent()` and assumes a `NodeComponent` with `getName()/setName()/clone()`.
    - Other GR classes wrap different domain types (e.g., `Interface`, `UseCase`, `Actor`, `UMLNote`, etc.).
2. Functional interface is typed to `NodeComponent`:
    - `UndoableEditFactory` is currently `NodeComponent`-specific.
3. Access to domain object is not unified at `GraphicalElement` level:
    - `GraphicalElement` does not expose a `getDomain()` hook.
4. Clone requirement:
    - The workflow clones the domain object before applying changes. Not all domain objects currently advertise a uniform `clone()` contract.

## Design options

### Option A — Generic helper in `GraphicalElement` using functional hooks (RECOMMENDED)

Add a reusable, type-agnostic helper in `GraphicalElement` that receives the domain object and operations via lambdas. Subclasses keep full control over how the domain object is obtained and how the undo edit is created.

Proposed API (example):

-   In `GraphicalElement`:
    -   `protected <D> boolean editStringPropertyWithDialog(EditContext ctx, String title, String label,
  D domain,
  java.util.function.Function<D, String> getter,
  java.util.function.BiConsumer<D, String> setter,
  java.util.function.Function<D, D> cloner,
  TriFunction<D, D, DiagramModel, javax.swing.undo.UndoableEdit> undoFactory)`
-   Add a tiny `@FunctionalInterface` `TriFunction<A,B,C,R>` to `edu.city.studentuml.util` (or as a nested interface in `GraphicalElement`) to avoid introducing a new dependency.

How callers would use it:

-   `NodeComponentGR.edit(...)` stays the same at the call-site level, but delegates to the new generic helper:
    -   Pass `getComponent()` as `domain`
    -   Pass `NodeComponent::getName` and `NodeComponent::setName`
    -   Pass `nc -> (NodeComponent) nc.clone()`
    -   Pass a lambda that creates the specific `UndoableEdit`

Pros:

-   Minimal changes to existing subclasses (only callsite adjustment).
-   No changes to domain model types (no new interfaces required).
-   Keeps UI concerns out of domain; stays inside GR layer.
-   Makes the helper available to any GR class, not just `NodeComponentGR` descendants.

Cons:

-   Slightly more verbose call sites than a hard-typed helper.
-   Introduces a small functional interface (TriFunction) unless placed as nested.

### Option B — Introduce a `Nameable` domain interface and a `NamedGraphicalElement<D extends Nameable>` base class

Create a domain-side interface (e.g., `Nameable { String getName(); void setName(String); Nameable clone(); }`) and let
applicable domain classes implement it. Then introduce a generic base GR class with a protected helper tailored to `Nameable`.

Pros:

-   Strong typing, clean constraints, simpler call sites.
-   Encourages uniformity across domain objects.

Cons:

-   Invasive: many domain classes must be updated to implement `Nameable`.
-   Risky due to reflection-based constraints in rules (several domain methods are “DO NOT RENAME: called by reflection”).
-   Requires touching many files and increases migration scope.

### Option C — Keep helper in `NodeComponentGR` and create a separate `NameEditHelper` utility class (static)

Extract the logic to a static utility and have `NodeComponentGR` delegate to it. Other GR classes could also use it by passing lambdas.

Pros:

-   No base-class changes.
-   Reusable from anywhere.

Cons:

-   Static helper hides lifecycle expectations (model changed, name pool reload) unless explicitly passed.
-   Slightly worse discoverability vs. a protected base-class method.
-   Still need functional parameters (similar verbosity to Option A).

## Recommended approach

Proceed with Option A: add a generic, protected helper in `GraphicalElement` and keep a thin delegating wrapper in `NodeComponentGR` for backward compatibility of call sites.

Rationale:

-   Zero changes to domain model.
-   Lowest risk and effort; aligns with the ongoing “move edit logic into GR polymorphic methods” effort.
-   Extensible: any GR can adopt it when a simple string edit suffices.

## Proposed API sketch

In `GraphicalElement`:

```java
@FunctionalInterface
protected interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

protected <D> boolean editStringPropertyWithDialog(
        EditContext context,
        String dialogTitle,
        String fieldLabel,
        D domainObject,
        java.util.function.Function<D, String> getter,
        java.util.function.BiConsumer<D, String> setter,
        java.util.function.Function<D, D> cloner,
        TriFunction<D, D, DiagramModel, javax.swing.undo.UndoableEdit> undoFactory) {

    // Show dialog
    StringEditorDialog dialog = new StringEditorDialog(
            context.getParentComponent(), dialogTitle, fieldLabel, getter.apply(domainObject));
    if (!dialog.showDialog()) {
        return false;
    }

    // Clone before applying
    D undoDomain = cloner.apply(domainObject);

    // Apply change
    String newValue = dialog.getText();
    setter.accept(domainObject, newValue);

    // Post undo
    javax.swing.undo.UndoableEdit edit = undoFactory.apply(domainObject, undoDomain, context.getModel());
    context.getUndoSupport().postEdit(edit);

    // Notify & refresh name pool
    context.notifyModelChanged();
    SystemWideObjectNamePool.getInstance().reload();
    return true;
}
```

Then, in `NodeComponentGR`, keep the existing signature and delegate for smooth migration:

```java
protected boolean editNameWithDialog(
        EditContext context,
        String dialogTitle,
        String fieldLabel,
        UndoableEditFactory undoableEditFactory) {
    NodeComponent domain = getComponent();
    return editStringPropertyWithDialog(
            context,
            dialogTitle,
            fieldLabel,
            domain,
            NodeComponent::getName,
            NodeComponent::setName,
            d -> (NodeComponent) d.clone(),
            (orig, undo, model) -> undoableEditFactory.create(orig, undo, model));
}
```

Note: `UndoableEditFactory` may remain in `NodeComponentGR` for the delegating wrapper, or we can create a generic `UndoableEditFactory<D>` in `GraphicalElement` and update callers — both are small changes.

## Elements likely to benefit

-   Already using it: `ActivityNodeGR`, `DecisionNodeGR`, `ActionNodeGR`.
-   Potential future adopters if they only need a single string field edit:
    -   `UMLNoteGR` (editing note text)
    -   Simple UCD components that don’t require specialized editors
    -   Certain edge labels if they support a direct rename

For complex editors (e.g., `ClassEditor`, `InterfaceEditor`, `UseCase`/`Actor` editors), this helper is not applicable — they involve multi-field dialog logic and conflict handling.

## Step-by-step plan

1. Add the generic helper to `GraphicalElement` (as shown above), including a minimal `TriFunction` functional interface (nested or placed in `util`).
2. Keep `NodeComponentGR.editNameWithDialog(...)` and refactor it to delegate to the new helper; no behavior change.
3. (Optional) Generalize `UndoableEditFactory` to a generic version in `GraphicalElement` and update the three AD nodes accordingly.
4. Identify candidate GRs beyond NodeComponentGR that can leverage the helper; migrate them opportunistically.
5. Run the full test suite; ensure no regressions.

## Risks and considerations

-   Domain clone contract: the caller supplies a `cloner` lambda, so each GR owns the correct cloning semantics. This avoids enforcing clone across all domain types.
-   UI in tests: Some Swing tests run in headless environments and may emit AWT warnings in logs; current suite accepts that (0 failures). No change expected.
-   Access control: Keep the helper `protected` to be used by subclasses, not external callers.
-   Backward compatibility: Keeping the `NodeComponentGR` signature preserves existing subclass code.

## Acceptance criteria

-   A generic helper exists in `GraphicalElement` and can be invoked by any subclass with simple string edits.
-   Existing AD node edits continue to pass unchanged (delegation through `NodeComponentGR`).
-   No changes required in domain code.
-   All tests pass (build, lint/typecheck N/A, tests PASS).

## Follow-ups (later)

-   If more complex patterns repeat (e.g., name conflict policies), consider adding specialized helpers or strategy interfaces to keep controllers and GR classes lean.

## Implementation Summary (2026-01-02)

-   Implemented the generic helper `editStringPropertyWithDialog(...)` in `GraphicalElement` and adopted it in:
    -   `UseCaseGR.edit(EditContext)` with UCD duplicate-name blocking and repository sync
    -   `UCActorGR.edit(EditContext)` with UCD duplicate-name blocking and repository sync
    -   `SystemGR.edit(EditContext)` with UCD duplicate-name blocking and repository sync
-   Refactored `UCDSelectionController` to rely on polymorphic `edit(EditContext)` for UCD elements and removed legacy mapper entries for `UCActorGR`, `UseCaseGR`, and `SystemGR`. Kept mapper handling for `UCExtendGR` (link editor remains controller-driven).
-   Guarded headless popup behavior in `AutocompleteJComboBox` to avoid exceptions in test runs.
-   Verified via full test suite: all tests PASS. Manual UCD editing verified with duplicate-name error dialogs firing correctly.

Next steps:

-   Identify other GRs that perform single-string edits and migrate them to use the helper when appropriate (e.g., simple label/stereotype cases).
-   Continue controller cleanup where elements have polymorphic `edit(EditContext)` methods.
