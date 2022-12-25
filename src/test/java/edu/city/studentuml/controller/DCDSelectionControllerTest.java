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
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;

public class DCDSelectionControllerTest {

    UMLProject umlProject;
    DCDModel model;
    DCDInternalFrame internalFrame;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new DCDModel("ccd", umlProject);
        internalFrame = new DCDInternalFrame(model, true);
    }

    @Test
    public void testCreation() {
        DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);
        assertNotNull(ccdSelectionController);
    }

    @Test
    public void testDeleteElementUndo() {
        DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

        /**
         * Adds a conceptual class A
         */
        GraphicalElement cGr = addClass("A");

        ccdSelectionController.deleteElement(cGr);

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ClassGR));

        internalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> 
        ge instanceof ClassGR));        
    }

    @Test
    public void testDeleteElementWithRelationshipsUndo() {
        DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

        ClassGR a = addClass("A");
        ClassGR b = addClass("B");
        ClassGR c = addClass("C");
        ClassGR d = addClass("D");
        ClassGR f = addClass("F");

        addAssociation(a, b);
        addAssociation(c, a);
        addAggregation(a, d);
        addAggregation(c, a);
        addGeneralization(c, a);
        addGeneralization(a, b);
        addAssociationClass(a, f);

        System.out.println("BEFORE");
        model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(7, countRelationshipsWithClassNamed("A"));

        /**
         * DELETE a
         */
        ccdSelectionController.deleteElement(a);

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR
                && ((ClassGR) ge).getAbstractClass().getName().equals("A")));
        assertEquals(0, countRelationshipsWithClassNamed("A"));

        System.out.println("DELETED A");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        /**
         * UNDO
         */
        internalFrame.getUndoManager().undo();
        System.out.println("UNDONE");

        model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR));
        assertEquals(7, countRelationshipsWithClassNamed("A"));
    }  
    
    private AssociationGR addAssociation(AbstractClassGR cGr, AbstractClassGR b) {
        AssociationGR assoc = new AssociationGR(cGr, b, new Association(cGr.getClassifier(), b.getClassifier()));
        model.addAssociation(assoc);        
        return assoc;
    }

    private AggregationGR addAggregation(AbstractClassGR cGr, AbstractClassGR b) {
        AggregationGR rel = new AggregationGR(cGr, b, new Aggregation(cGr.getClassifier(), b.getClassifier()));
        model.addAssociation(rel);        
        return rel;
    }


    private GeneralizationGR addGeneralization(AbstractClassGR cGr, AbstractClassGR b) {
        GeneralizationGR rel = new GeneralizationGR(cGr, b, new Generalization(cGr.getClassifier(), b.getClassifier()));
        model.addGeneralization(rel);        
        return rel;
    }

    private AssociationClassGR addAssociationClass(AbstractClassGR cGr, AbstractClassGR b) {
        AssociationClassGR rel = new AssociationClassGR(cGr, b, new ConceptualAssociationClass(cGr.getClassifier(), b.getClassifier()));
        model.addAssociationClass(rel);        
        return rel;
    }    

    private ClassGR addClass(String name) {
        DesignClass c = new DesignClass(name);
        ClassGR cGr = new ClassGR(c, new Point());
        model.addClass(cGr); 
        return cGr;       
    }

    private int countRelationshipsWithClassNamed(String name) {
        return model.getGraphicalElements().stream()
                .filter(ge -> (ge instanceof LinkGR 
                && (((LinkGR) ge).getA().getClassifier().getName().equals(name)
                || ((LinkGR) ge).getB().getClassifier().getName().equals(name)))
                )
                .collect(Collectors.toList()).size();
    }

}
