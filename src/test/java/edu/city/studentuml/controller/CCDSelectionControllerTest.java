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

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new CCDModel("ccd", umlProject);
        ccdInternalFrame = new CCDInternalFrame(model);
        h = new Helper(model);
    }

    @Test
    public void testCreation() {
        CCDSelectionController ccdSelectionController  = new CCDSelectionController(ccdInternalFrame, model);
        assertNotNull(ccdSelectionController);
    }

    @Test
    public void testDeleteElementUndo() {
        CCDSelectionController ccdSelectionController  = new CCDSelectionController(ccdInternalFrame, model);

        /**
         * Adds a conceptual class A
         */
        GraphicalElement cGr = h.addConceptualClass("A");

        ccdSelectionController.deleteElement(cGr);

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ConceptualClassGR));

        ccdInternalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ConceptualClassGR));        
    }

    @Test
    public void testDeleteElementWithRelationshipsUndo() {
        CCDSelectionController ccdSelectionController = new CCDSelectionController(ccdInternalFrame, model);

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
        ccdSelectionController.deleteElement(a);

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
