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
        selectionController  = new DCDSelectionController(internalFrame, model);
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

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ClassGR));

        internalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ClassGR));        
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
        
        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR
                && ((ClassGR) ge).getAbstractClass().getName().equals("A")));
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
        assertEquals(countAll, model.getGraphicalElements().size());

                /**
         * REDO
         */
        internalFrame.getUndoManager().redo();
        // System.out.println("REDO");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(0, model.getGraphicalElements().size());
    }  


}
