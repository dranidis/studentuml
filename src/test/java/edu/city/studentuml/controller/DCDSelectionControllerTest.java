package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;

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
    public void testCopyPasteClassesWithAssociation() {
        // Setup: Create two classes with an association between them
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        AssociationGR originalAssociation = h.addAssociation(classA, classB);

        // Initial state: 2 classes + 1 association = 3 elements
        assertEquals(3, model.getGraphicalElements().size());

        // Copy both classes (not the association)
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.copySelected();

        // Paste
        selectionController.pasteClipboard();

        // After paste: original 2 classes + 1 association + pasted 2 classes + 1 new association = 6 elements
        assertEquals("Should have 6 elements after paste (2 original classes, 2 pasted classes, 2 associations)", 
                     6, model.getGraphicalElements().size());

        // Count associations - should have 2 (one for original, one for pasted)
        long associationCount = model.getGraphicalElements().stream()
            .filter(e -> e instanceof AssociationGR)
            .count();
        assertEquals("Should have 2 associations after paste", 2, associationCount);

        // Find the pasted classes (they should be selected)
        long selectedClassCount = model.getGraphicalElements().stream()
            .filter(e -> e instanceof ClassGR && e.isSelected())
            .count();
        assertEquals("Should have 2 selected classes (the pasted ones)", 2, selectedClassCount);

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

        // Copy and paste the classes
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
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

        // Copy and paste
        selectionController.addElementToSelection(superClass);
        selectionController.addElementToSelection(subClass);
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
    public void testCopyPasteUndo() {
        // Setup
        ClassGR classA = h.addClass("A");
        ClassGR classB = h.addClass("B");
        h.addAssociation(classA, classB);

        assertEquals(3, model.getGraphicalElements().size());

        // Copy and paste
        selectionController.addElementToSelection(classA);
        selectionController.addElementToSelection(classB);
        selectionController.copySelected();
        selectionController.pasteClipboard();

        assertEquals(6, model.getGraphicalElements().size());

        // Undo should remove the 3 pasted elements (2 classes + 1 association)
        // Note: Each pasted element creates separate undo edits, so we need to undo 3 times
        internalFrame.getUndoManager().undo();
        assertEquals(5, model.getGraphicalElements().size());
        
        internalFrame.getUndoManager().undo();
        assertEquals(4, model.getGraphicalElements().size());
        
        internalFrame.getUndoManager().undo();
        assertEquals(3, model.getGraphicalElements().size());

        // Redo should restore all 3
        internalFrame.getUndoManager().redo();
        assertEquals(4, model.getGraphicalElements().size());
        
        internalFrame.getUndoManager().redo();
        assertEquals(5, model.getGraphicalElements().size());
        
        internalFrame.getUndoManager().redo();
        assertEquals(6, model.getGraphicalElements().size());
    }

}
