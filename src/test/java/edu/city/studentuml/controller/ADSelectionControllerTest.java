package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

public class ADSelectionControllerTest {

    UMLProject umlProject;
    DiagramModel model;
    DiagramInternalFrame internalFrame;
    SelectionController selectionController;
    Helper h;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new ADModel("ad", umlProject);
        internalFrame = new ADInternalFrame(model);
        h = new Helper(model);
        selectionController  = new ADSelectionController(internalFrame, model);
    }

    @Test
    public void testCreation() {
        assertNotNull(selectionController);
    }

    @Test 
    public void testDeleteInitialNodeWithinActivityNode() {
        ActivityNodeGR an = h.addActivityNode("an");
        InitialNodeGR i = h.addInitialNodeInActivityNode(an);

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));

        // SELECT the initial node
        selectionController.addElementToSelection(i);

        // DELETE it
        System.out.println("DELETE");
        selectionController.deleteSelected();

        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(0, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // UNDO
        System.out.println("UNDO");
        internalFrame.getUndoManager().undo();

        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // REDO
        System.out.println("REDO");
        internalFrame.getUndoManager().redo();

        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(0, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());
   }

    @Test
    public void testDeleteActivityNodeWithAnInitialNodeInside() {
        ActivityNodeGR an = h.addActivityNode("an");
        InitialNodeGR i = h.addInitialNodeInActivityNode(an);

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));

        // SELECT the activity node
        selectionController.addElementToSelection(an);

        // DELETE it
        System.out.println("DELETE");
        selectionController.deleteSelected();

        model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(0, model.getGraphicalElements().size());

        // UNDO
        System.out.println("UNDO");
        internalFrame.getUndoManager().undo();

        model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // REDO
        System.out.println("REDO");
        internalFrame.getUndoManager().redo();

        model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(0, model.getGraphicalElements().size());
             
    }

}
