package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.view.gui.CCDInternalFrame;

public class CCDSelectionControllerTest {

    UMLProject umlProject;
    CCDModel model;
    CCDInternalFrame ccdInternalFrame;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new CCDModel("ccd", umlProject);
        ccdInternalFrame = new CCDInternalFrame(model);
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
        GraphicalElement cGr = addClass("A");

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

        ConceptualClassGR a = addClass("A");
        ConceptualClassGR b = addClass("B");
        ConceptualClassGR c = addClass("C");
        ConceptualClassGR d = addClass("D");
        ConceptualClassGR f = addClass("F");

        addAssociation(a, b);
        addAssociation(c, a);
        addAggregation(a, d);
        addAggregation(c, a);
        addGeneralization(c, a);
        addGeneralization(a, b);
        addAssociationClass(a, f);

        System.out.println("BEFORE");
        model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(7, countRelationshipsWithA());

        /**
         * DELETE a
         */
        ccdSelectionController.deleteElement(a);

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR
                && ((ConceptualClassGR) ge).getAbstractClass().getName().equals("A")));
        assertEquals(0, countRelationshipsWithA());

        System.out.println("DELETED A");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        /**
         * UNDO
         */
        ccdInternalFrame.getUndoManager().undo();
        System.out.println("UNDONE");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR));
        assertEquals(7, countRelationshipsWithA());
    }  
    
    private AssociationGR addAssociation(ConceptualClassGR cGr, ConceptualClassGR b) {
        AssociationGR assoc = new AssociationGR(cGr, b, new Association(cGr.getClassifier(), b.getClassifier()));
        model.addAssociation(assoc);        
        return assoc;
    }

    private AggregationGR addAggregation(ConceptualClassGR cGr, ConceptualClassGR b) {
        AggregationGR rel = new AggregationGR(cGr, b, new Aggregation(cGr.getClassifier(), b.getClassifier()));
        model.addAssociation(rel);        
        return rel;
    }


    private GeneralizationGR addGeneralization(ConceptualClassGR cGr, ConceptualClassGR b) {
        GeneralizationGR rel = new GeneralizationGR(cGr, b, new Generalization(cGr.getClassifier(), b.getClassifier()));
        model.addGeneralization(rel);        
        return rel;
    }

    private AssociationClassGR addAssociationClass(ConceptualClassGR cGr, ConceptualClassGR b) {
        AssociationClassGR rel = new AssociationClassGR(cGr, b, new ConceptualAssociationClass(cGr.getClassifier(), b.getClassifier()));
        model.addAssociationClass(rel);        
        return rel;
    }    

    private ConceptualClassGR addClass(String name) {
        ConceptualClass c = new ConceptualClass(name);
        ConceptualClassGR cGr = new ConceptualClassGR(c, new Point());
        model.addClass(cGr); 
        return cGr;       
    }

    private int countRelationshipsWithA() {
        return model.getGraphicalElements().stream()
                .filter(ge -> (ge instanceof LinkGR 
                && (((LinkGR) ge).getA().getClassifier().getName().equals("A")
                || ((LinkGR) ge).getB().getClassifier().getName().equals("A")))
                )
                .collect(Collectors.toList()).size();
    }

}
