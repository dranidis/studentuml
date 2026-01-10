# Testing Strategy for Edit/Undo/Redo Functionality

## Problem

We want to test that all GraphicalElement `edit()` methods correctly support undo/redo. However, the `edit()` methods invoke editor dialogs which block and require UI interaction.

## Solution: Test Repository Operations Directly

Instead of mocking editors (complex, fragile), we test the **core undo/redo mechanism** by:

1. **Bypassing the editor dialog entirely**
2. **Manually creating the modified domain objects** (same as what the editor would return)
3. **Calling repository.edit\*() methods** to modify the state
4. **Creating and posting undo edits** in the correct order
5. **Verifying undo/redo works correctly**

This tests the EXACT same code path that `edit()` uses, without the UI complication.

## Pattern Template

```java
@Test
public void testElementEdit_UndoRedo() {
    // 1. Setup: Create model, frame, element
    XYZModel model = new XYZModel("test", project);
    XYZInternalFrame frame = new XYZInternalFrame(model, false); // false = not root diagram

    DomainObject original = new DomainObject("OriginalValue");
    GraphicalElement gr = new GraphicalElementGR(original, new Point(100, 100));
    model.addGraphicalElement(gr);

    // 2. Verify original state
    assertEquals("OriginalValue", original.getSomeProperty());

    // 3. Simulate edit: Create modified copy (what editor would return)
    DomainObject modified = original.clone(); // or copyOf() or new instance
    modified.setSomeProperty("EditedValue");

    // 4. CRITICAL: Create undo edit BEFORE modifying repository
    EditSomethingEdit undoEdit = new EditSomethingEdit(original, modified, model);

    // 5. Modify repository (this changes original in-place)
    repository.editSomething(original, modified);

    // 6. Post undo edit
    frame.getUndoSupport().postEdit(undoEdit);

    // 7. Verify edited state
    assertEquals("EditedValue", original.getSomeProperty());

    // 8. Undo
    assertTrue("Should be able to undo", frame.getUndoManager().canUndo());
    frame.getUndoManager().undo();
    assertEquals("After undo: restored", "OriginalValue", original.getSomeProperty());

    // 9. Redo
    assertTrue("Should be able to redo", frame.getUndoManager().canRedo());
    frame.getUndoManager().redo();
    assertEquals("After redo: re-applied", "EditedValue", original.getSomeProperty());
}
```

## Why This Works

1. **No UI blocking**: We never call `editor.showDialog()`
2. **Tests actual code**: Uses the same repository methods that `edit()` calls
3. **Tests critical ordering**: Ensures undo edit created BEFORE modification
4. **Simple to understand**: Direct, linear test flow
5. **Easy to maintain**: No mocking framework needed

## Elements to Test

For each GraphicalElement with an `edit()` method:

-   [ ] `ConceptualClassGR` → tests `editConceptualClass()`
-   [ ] `ClassGR` (DesignClass) → tests `editDesignClass()`
-   [ ] `InterfaceGR` → tests `editInterface()`
-   [ ] `UCActorGR` → tests `editActor()`
-   [ ] `UseCaseGR` → tests `editUseCase()`
-   [ ] `SystemGR` → tests `editSystem()`
-   [ ] `UCExtendGR` → tests `editUCExtend()` (DONE - has test)
-   [ ] `CallMessageGR` → tests `editCallMessage()`
-   [ ] `CreateMessageGR` → tests `editCreateMessage()`
-   [ ] `ReturnMessageGR` → tests `editReturnMessage()`
-   [ ] `AssociationGR` → tests `editAssociation()`
-   [ ] `AssociationClassGR` → tests `editDesignAssociationClass()` / `editConceptualAssociationClass()`
-   [ ] `UMLNoteGR` → tests note text editing
-   [ ] `SDObjectGR` → tests `editSDObject()`
-   [ ] `MultiObjectGR` → tests `editMultiObject()`
-   [ ] `ActorInstanceGR` → tests `editActorInstance()`
-   [ ] `SystemInstanceGR` → tests `editSystemInstance()`
-   [ ] `ObjectNodeGR` → tests `editObjectNode()`
-   [ ] `ActivityNodeGR` → tests `editActivityNode()`
-   [ ] `ActionNodeGR` → tests `editActionNode()`
-   [ ] `ControlFlowGR` → tests `editControlFlow()`
-   [ ] `ObjectFlowGR` → tests `editObjectFlow()`
-   [ ] `DecisionNodeGR` → tests `editDecisionNode()`
-   [ ] `DependencyGR` → tests `editDependency()`

## Existing Test Example

See `UCExtendEditorTest.java` - but note it tests the **editor**, not the **edit() method**.

We need tests that focus on the **GraphicalElement.edit()** → **repository modification** → **undo/redo** flow.

## Next Steps

1. Create a simple test for ONE element (e.g., `UMLNoteGR`) as a prototype
2. Verify the pattern works correctly
3. Systematically add tests for all other elements
4. Run full test suite to ensure no regressions
