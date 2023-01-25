package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UCDInternalFrame;

public class UCDSelectionControllerTest {

    UMLProject umlProject;
    DiagramModel model;
    DiagramInternalFrame internalFrame;
    Helper h;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new UCDModel("ucd", umlProject);
        internalFrame = new UCDInternalFrame(model);
        h = new Helper(model);
    }

    @Test
    public void testCreation() {
        SelectionController selectionController  = new UCDSelectionController(internalFrame, model);
        assertNotNull(selectionController);
    }

    @Test 
    public void testDeleteActorWithRelationshipToUC() {
        SelectionController selectionController  = new UCDSelectionController(internalFrame, model);

        UCActorGR a = h.addActor("A");
        UseCaseGR u = h.addUseCase("U");
        h.addUcAssociation(a, u);
        assertEquals(3, model.getGraphicalElements().size());
        
        selectionController.addElementToSelection(a);

        // System.out.println("DELETE");
        selectionController.deleteSelected();

        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(1, model.getGraphicalElements().size());

        // System.out.println("UNDO");
        internalFrame.getUndoManager().undo();
        
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(3, model.getGraphicalElements().size());
    }

    @Test
    public void testDeleteSystemWithUseCase() {
        SelectionController selectionController  = new UCDSelectionController(internalFrame, model);

        SystemGR s = h.addSystem("System");  
        UseCaseGR uc = new UseCaseGR(new UseCase("U"), 0, 0);
        s.add(uc);
        // if this is not acced, it does not terminate
        uc.setContext(s);

        model.getGraphicalElements().forEach(e -> System.out.println(e));
        // System.out.println(s.getNumberOfElements());
        
        s.createIterator().forEachRemaining(e -> System.out.println("IN s: " + e));

        selectionController.addElementToSelection(s);

        // System.out.println("DELETE and UNDO");
        selectionController.deleteSelected();
        assertEquals(0, model.getGraphicalElements().size());

        internalFrame.getUndoManager().undo();

        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        System.out.println(s.getNumberOfElements());

        // s.createIterator().forEachRemaining(e -> System.out.println("IN s: " + e));

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((SystemGR) model.getGraphicalElements().get(0)).getNumberOfElements());
             
    }

    @Test
    public void testDeleteSystemWithinSystem() {
        SelectionController selectionController  = new UCDSelectionController(internalFrame, model);

        SystemGR s1 = h.addSystem("System1");  
        SystemGR s2 = h.addSystem("System2");  

        model.removeGraphicalElement(s2);
        s1.add(s2);
        s2.setContext(s1);

        // s1.createIterator().forEachRemaining(e -> System.out.println("IN s1: " + e));

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());

        selectionController.addElementToSelection(s2);

        // System.out.println("DELETE");
        selectionController.deleteSelected();

        // s1.createIterator().forEachRemaining(e -> System.out.println("IN s1: " + e));

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(0, s1.getNumberOfElements());
             
    }

    @Test
    public void testDeleteSystemWithinSystemWithinSystem() {
        SelectionController selectionController  = new UCDSelectionController(internalFrame, model);

        SystemGR s1 = h.addSystem("System1");  
        SystemGR s2 = h.addSystem("System2");  
        SystemGR s3 = h.addSystem("System3");  

        model.removeGraphicalElement(s2);
        s1.add(s2);
        s2.setContext(s1);

        model.removeGraphicalElement(s3);
        s2.add(s3);
        s3.setContext(s2);

        // s1.createIterator().forEachRemaining(e -> System.out.println("IN s1: " + e));

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());
        assertEquals(1, s2.getNumberOfElements());

        selectionController.addElementToSelection(s3);

        // System.out.println("DELETE");
        selectionController.deleteSelected();

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());
        assertEquals(0, s2.getNumberOfElements());
             
    }

}
