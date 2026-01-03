package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.util.undoredo.EditInterfaceEdit;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.editing.EditContext;

public class DCDSelectionControllerTest {

    UMLProject umlProject;
    DCDModel model;
    DCDInternalFrame internalFrame;
    Helper h;
    SelectionController selectionController;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new DCDModel("ccd", umlProject);
        internalFrame = new DCDInternalFrame(model, true);
        h = new Helper(model);
        selectionController = new DCDSelectionController(internalFrame, model);
    }

    @Test
    public void testCreation() {

        assertNotNull(selectionController);
    }

    @Test
    public void testDeleteInterfaceWithAnAssociation() {

        InterfaceGR i = h.addInterface("I");
        ClassGR a = h.addClass("A");

        h.addAssociation(a, i);

        selectionController.addElementToSelection(i);
        selectionController.deleteSelected();

        assertEquals(1, model.getGraphicalElements().size());

    }

    @Test
    public void testDeleteElementUndo() {

        /**
         * Adds a conceptual class A
         */
        GraphicalElement cGr = h.addClass("A");

        selectionController.addElementToSelection(cGr);
        selectionController.deleteSelected();

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR));

        internalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR));
    }

    @Test
    public void testDeleteElementWithRelationshipsUndo() {

        ClassGR a = h.addClass("A");
        ClassGR b = h.addClass("B");
        ClassGR c = h.addClass("C");
        ClassGR d = h.addClass("D");
        ClassGR f = h.addClass("F");

        h.addAssociation(a, b);
        h.addAssociation(c, a);
        h.addAggregation(a, d);
        h.addAggregation(c, a);
        h.addGeneralization(c, a);
        h.addGeneralization(a, b);
        h.addAssociationClass(a, f);

        // System.out.println("BEFORE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));

        /**
         * DELETE a
         */
        selectionController.addElementToSelection(a);
        selectionController.deleteSelected();

        assertFalse("no matches", model.getGraphicalElements().stream()
                .anyMatch(ge -> ge instanceof ClassGR && ((ClassGR) ge).getAbstractClass().getName().equals("A")));
        assertEquals(0, h.countRelationshipsWithClassNamed("A"));

        // System.out.println("DELETED A");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        /**
         * UNDO
         */
        internalFrame.getUndoManager().undo();
        // System.out.println("UNDONE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));
    }

    @Test
    public void testdeleteSelectedElements() {

        ClassGR a = h.addClass("A");
        ClassGR b = h.addClass("B");

        h.addAssociation(a, b);
        h.addAssociation(a, b);

        // System.out.println("BEFORE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertEquals(4, model.getGraphicalElements().size());

        /**
         * DELETE all
         */
        selectionController.selectAll();
        selectionController.deleteSelected();

        // System.out.println("DELETE ALL");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(0, model.getGraphicalElements().size());

        /**
         * UNDO
         */
        internalFrame.getUndoManager().undo();
        // System.out.println("UNDO");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertEquals(4, model.getGraphicalElements().size());

        /**
         * REDO
         */
        internalFrame.getUndoManager().redo();
        // System.out.println("REDO");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertEquals(0, model.getGraphicalElements().size());
    }

    @Test
    public void testdeleteSelectedElementsWithNotes() {

        ClassGR a = h.addClass("A");
        ClassGR b = h.addClass("B");

        AssociationGR ab1 = h.addAssociation(a, b);
        GeneralizationGR ab2 = h.addGeneralization(a, b);

        h.addNote(a);
        h.addNote(b);
        h.addNote(ab1);
        h.addNote(ab2);

        int countAll = 8;

        // System.out.println("BEFORE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(countAll, model.getGraphicalElements().size());

        /*
         * DELETE all
         */
        selectionController.selectAll();
        selectionController.deleteSelected();

        // System.out.println("DELETE ALL");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(0, model.getGraphicalElements().size());

        /*
         * UNDO
         */
        internalFrame.getUndoManager().undo();
        // System.out.println("UNDO");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(countAll, model.getGraphicalElements().size());

        /*
         * REDO
         */
        internalFrame.getUndoManager().redo();
        // System.out.println("REDO");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(0, model.getGraphicalElements().size());
    }

    @Test
    public void testCopyPasteClassesWithoutAssociation() {
        // Setup: Create two classes with an association between them
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        h.addAssociation(classA, classB);

        // Initial state: 2 classes + 1 association = 3 elements
        assertEquals(3, model.getGraphicalElements().size());

        // Copy both classes ONLY (not the association)
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.copySelected();

        // Paste
        selectionController.pasteClipboard();

        // After paste: original 2 classes + 1 association + pasted 2 classes = 5 elements
        // Association should NOT be copied unless explicitly selected
        assertEquals("Should have 5 elements after paste (2 original classes, 1 association, 2 pasted classes)",
                5, model.getGraphicalElements().size());

        // Count associations - should still have only 1 (the original)
        long associationCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof AssociationGR)
                .count();
        assertEquals("Should have only 1 association (not copied)", 1, associationCount);

        // Find the pasted classes (they should be selected)
        long selectedClassCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof ClassGR && e.isSelected())
                .count();
        assertEquals("Should have 2 selected classes (the pasted ones)", 2, selectedClassCount);
    }

    @Test
    public void testCopyPasteClassesWithExplicitlySelectedAssociation() {
        // Setup: Create two classes with an association between them
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        AssociationGR originalAssociation = h.addAssociation(classA, classB);

        // Initial state: 2 classes + 1 association = 3 elements
        assertEquals(3, model.getGraphicalElements().size());

        // Copy both classes AND the association (explicitly select all)
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.addElementToSelection(originalAssociation);
        selectionController.copySelected();

        // Paste
        selectionController.pasteClipboard();

        // After paste: original 2 classes + 1 association + pasted 2 classes + 1 pasted association = 6 elements
        assertEquals("Should have 6 elements after paste (original 3 + pasted 3)",
                6, model.getGraphicalElements().size());

        // Count associations - should have 2 (one for original, one for pasted)
        long associationCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof AssociationGR)
                .count();
        assertEquals("Should have 2 associations after paste", 2, associationCount);

        // Find the new association
        AssociationGR newAssociation = (AssociationGR) model.getGraphicalElements().stream()
                .filter(e -> e instanceof AssociationGR && e != originalAssociation)
                .findFirst()
                .orElse(null);

        assertNotNull("Should have created a new association for pasted classes", newAssociation);

        // Verify the new association connects the pasted classes, not the originals
        ClassGR newAssocClassA = (ClassGR) newAssociation.getA();
        ClassGR newAssocClassB = (ClassGR) newAssociation.getB();

        assertTrue("New association should connect different class instances",
                newAssocClassA != classA && newAssocClassB != classB);

        // Verify the pasted classes share the same domain objects (as per clone() behavior)
        assertEquals("Pasted class A should share domain object",
                classA.getDesignClass(), newAssocClassA.getDesignClass());
        assertEquals("Pasted class B should share domain object",
                classB.getDesignClass(), newAssocClassB.getDesignClass());
    }

    @Test
    public void testCopyPastePreservesAssociationProperties() {
        // Setup: Create two classes with a configured association
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        AssociationGR originalAssociation = h.addAssociation(classA, classB);

        // Configure the association with properties
        originalAssociation.getAssociation().setName("manages");
        originalAssociation.getAssociation().setDirection(1); // A to B
        originalAssociation.getAssociation().getRoleA().setName("manager");
        originalAssociation.getAssociation().getRoleA().setMultiplicity("1");
        originalAssociation.getAssociation().getRoleB().setName("employee");
        originalAssociation.getAssociation().getRoleB().setMultiplicity("*");

        // Copy and paste the classes AND the association (explicitly)
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.addElementToSelection(originalAssociation);
        selectionController.copySelected();
        selectionController.pasteClipboard();

        // Find the new association
        AssociationGR newAssociation = (AssociationGR) model.getGraphicalElements().stream()
                .filter(e -> e instanceof AssociationGR && e != originalAssociation)
                .findFirst()
                .orElse(null);

        assertNotNull("Should have created a new association", newAssociation);

        // Verify properties were copied
        assertEquals("Association name should be preserved",
                "manages", newAssociation.getAssociation().getName());
        assertEquals("Direction should be preserved",
                1, newAssociation.getAssociation().getDirection());
        assertEquals("Role A name should be preserved",
                "manager", newAssociation.getAssociation().getRoleA().getName());
        assertEquals("Role A multiplicity should be preserved",
                "1", newAssociation.getAssociation().getRoleA().getMultiplicity());
        assertEquals("Role B name should be preserved",
                "employee", newAssociation.getAssociation().getRoleB().getName());
        assertEquals("Role B multiplicity should be preserved",
                "*", newAssociation.getAssociation().getRoleB().getMultiplicity());
    }

    @Test
    public void testCopyPasteWithGeneralization() {
        // Setup: Create inheritance relationship
        ClassGR superClass = h.addClass("Animal");
        ClassGR subClass = h.addClass("Dog");
        GeneralizationGR originalGen = h.addGeneralization(subClass, superClass);

        assertEquals(3, model.getGraphicalElements().size());

        // Copy and paste - including the generalization
        selectionController.addElementToSelection(superClass);
        selectionController.addElementToSelection(subClass);
        selectionController.addElementToSelection(originalGen);
        selectionController.copySelected();
        selectionController.pasteClipboard();

        // Should have 6 elements: 2 original classes + 1 original gen + 2 pasted classes + 1 pasted gen
        assertEquals(6, model.getGraphicalElements().size());

        // Count generalizations
        long genCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR)
                .count();
        assertEquals("Should have 2 generalizations after paste", 2, genCount);

        // Find the new generalization
        GeneralizationGR newGen = (GeneralizationGR) model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR && e != originalGen)
                .findFirst()
                .orElse(null);

        assertNotNull("Should have created a new generalization for pasted classes", newGen);

        // Verify it connects different graphical instances
        assertTrue("New generalization should connect different instances",
                newGen.getA() != subClass && newGen.getB() != superClass);
    }

    @Test
    public void testCopyPasteAssociationUndo() {
        // Setup: Create two classes and an association
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        AssociationGR assoc = h.addAssociation(classA, classB);

        int initialCount = model.getGraphicalElements().size();
        assertEquals("Should have 2 classes + 1 association", 3, initialCount);

        // Copy and paste - including the association
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.addElementToSelection(assoc);
        selectionController.copySelected();
        selectionController.pasteClipboard();

        assertEquals("After paste should have 6 elements (2 original + 2 pasted classes + 2 associations)",
                6, model.getGraphicalElements().size());

        // Undo the paste operation (should be atomic - one compound edit)
        internalFrame.getUndoManager().undo();

        assertEquals("After undo should be back to original 3 elements",
                initialCount, model.getGraphicalElements().size());

        // Verify the original association still exists
        assertTrue("Original association should still be in model",
                model.getGraphicalElements().contains(assoc));
        assertTrue("Original class A should still be in model",
                model.getGraphicalElements().contains(classA));
        assertTrue("Original class B should still be in model",
                model.getGraphicalElements().contains(classB));

        // Redo
        internalFrame.getUndoManager().redo();

        assertEquals("After redo should have 6 elements again",
                6, model.getGraphicalElements().size());
    }

    @Test
    public void testClassEdit_withPolymorphicMethod() {
        // This test verifies that ClassGR.edit() method exists and accepts EditContext
        // We cannot test the actual dialog interaction in headless mode

        // Verify the polymorphic edit() method exists and has correct signature
        try {
            java.lang.reflect.Method editMethod = ClassGR.class.getMethod("edit", EditContext.class);
            assertNotNull("ClassGR should have edit(EditContext) method", editMethod);
            assertEquals("edit() should return boolean", boolean.class, editMethod.getReturnType());

            // Verify EditContext can be created with correct dependencies
            EditContext context = new EditContext(model, internalFrame);
            assertNotNull("EditContext should be created successfully", context);
            assertEquals("EditContext should provide correct model", model, context.getModel());
            assertEquals("EditContext should provide correct repository",
                    model.getCentralRepository(), context.getRepository());
            assertEquals("EditContext should provide correct parent component",
                    internalFrame, context.getParentComponent());
            assertNotNull("EditContext should provide undo support", context.getUndoSupport());

        } catch (NoSuchMethodException e) {
            assertTrue("ClassGR should have edit(EditContext) method", false);
        }
    }

    @Test
    public void testInterfaceEdit_withPolymorphicMethod() {
        // This test verifies that InterfaceGR.edit() method exists and accepts EditContext
        // We cannot test the actual dialog interaction in headless mode

        // Verify the polymorphic edit() method exists and has correct signature
        try {
            java.lang.reflect.Method editMethod = InterfaceGR.class.getMethod("edit", EditContext.class);
            assertNotNull("InterfaceGR should have edit(EditContext) method", editMethod);
            assertEquals("edit() should return boolean", boolean.class, editMethod.getReturnType());

            // Verify EditContext can be created with correct dependencies
            EditContext context = new EditContext(model, internalFrame);
            assertNotNull("EditContext should be created successfully", context);
            assertEquals("EditContext should provide correct model", model, context.getModel());
            assertEquals("EditContext should provide correct repository",
                    model.getCentralRepository(), context.getRepository());
            assertEquals("EditContext should provide correct parent component",
                    internalFrame, context.getParentComponent());
            assertNotNull("EditContext should provide undo support", context.getUndoSupport());

        } catch (NoSuchMethodException e) {
            assertTrue("InterfaceGR should have edit(EditContext) method", false);
        }
    }

    @Test
    public void testEditInterfaceNameWithUndo() {
        // Create an Interface
        InterfaceGR interfaceGR = h.addInterface("OriginalInterface");
        Interface interfaceObj = interfaceGR.getInterface();

        // Verify initial state in both domain and repository
        assertEquals("OriginalInterface", interfaceObj.getName());
        assertEquals(interfaceObj, umlProject.getCentralRepository().getInterface("OriginalInterface"));

        // Create edit
        Interface newInterface = interfaceObj.clone();
        newInterface.setName("EditedInterface");
        EditInterfaceEdit edit = new EditInterfaceEdit(interfaceObj, newInterface, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("EditedInterface", interfaceObj.getName());
        // Repository should be synchronized
        assertEquals(interfaceObj, umlProject.getCentralRepository().getInterface("EditedInterface"));
        assertNull(umlProject.getCentralRepository().getInterface("OriginalInterface"));

        // Undo
        edit.undo();
        assertEquals("OriginalInterface", interfaceObj.getName());
        // Repository should be restored
        assertEquals(interfaceObj, umlProject.getCentralRepository().getInterface("OriginalInterface"));
        assertNull(umlProject.getCentralRepository().getInterface("EditedInterface"));

        // Redo again
        edit.redo();
        assertEquals("EditedInterface", interfaceObj.getName());
        assertEquals(interfaceObj, umlProject.getCentralRepository().getInterface("EditedInterface"));
        assertNull(umlProject.getCentralRepository().getInterface("OriginalInterface"));
    }

}
