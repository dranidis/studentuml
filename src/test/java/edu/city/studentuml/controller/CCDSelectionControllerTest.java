package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.CCDInternalFrame;

public class CCDSelectionControllerTest {

    UMLProject umlProject;
    CCDModel model;
    CCDInternalFrame ccdInternalFrame;
    Helper h;
    SelectionController selectionController;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new CCDModel("ccd", umlProject);
        ccdInternalFrame = new CCDInternalFrame(model);
        h = new Helper(model);
        selectionController  = new CCDSelectionController(ccdInternalFrame, model);
    }

    @Test
    public void testCreation() {
        
        assertNotNull(selectionController);
    }

    @Test
    public void testDeleteElementUndo() {
        

        /**
         * Adds a conceptual class A
         */
        GraphicalElement cGr = h.addConceptualClass("A");

        selectionController.addElementToSelection(cGr);
        selectionController.deleteSelected();

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ConceptualClassGR));

        ccdInternalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ConceptualClassGR));        
    }

    @Test
    public void testDeleteElementWithRelationshipsUndo() {

        ConceptualClassGR a = h.addConceptualClass("A");
        ConceptualClassGR b = h.addConceptualClass("B");
        ConceptualClassGR c = h.addConceptualClass("C");
        ConceptualClassGR d = h.addConceptualClass("D");
        ConceptualClassGR f = h.addConceptualClass("F");

        h.addAssociation(a, b);
        h.addAssociation(c, a);
        h.addAggregation(a, d);
        h.addAggregation(c, a);
        h.addGeneralization(c, a);
        h.addGeneralization(a, b);
        h.addConceptualAssociationClass(a, f);

        System.out.println("BEFORE");
        model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));

        /**
         * DELETE a
         */
        selectionController.addElementToSelection(a);
        selectionController.deleteSelected();

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR
                && ((ConceptualClassGR) ge).getAbstractClass().getName().equals("A")));
        assertEquals(0, h.countRelationshipsWithClassNamed("A"));

        System.out.println("DELETED A");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        /**
         * UNDO
         */
        ccdInternalFrame.getUndoManager().undo();
        System.out.println("UNDONE");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));
    }  
    

}
