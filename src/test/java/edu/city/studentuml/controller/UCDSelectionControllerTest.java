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

    @Test
    public void testCopyPasteSystemWithUseCases() {
        SelectionController selectionController = new UCDSelectionController(internalFrame, model);

        // Create a system with use cases inside
        SystemGR system = h.addSystem("System");
        UseCaseGR uc1 = h.addUseCase("UseCase1");
        UseCaseGR uc2 = h.addUseCase("UseCase2");
        
        // Move use cases inside system
        model.removeGraphicalElement(uc1);
        model.removeGraphicalElement(uc2);
        system.add(uc1);
        uc1.setContext(system);
        system.add(uc2);
        uc2.setContext(system);
        
        assertEquals(1, model.getGraphicalElements().size()); // Just the system
        assertEquals(2, system.getNumberOfElements()); // 2 use cases inside
        
        // Select only the system (not individual use cases)
        selectionController.addElementToSelection(system);
        
        // Copy and paste
        selectionController.copySelected();
        selectionController.pasteClipboard();
        
        // Should now have 2 systems, each with 2 use cases
        assertEquals(2, model.getGraphicalElements().size());
        
        // Find the pasted system (should be the second one)
        SystemGR pastedSystem = null;
        for (int i = 0; i < model.getGraphicalElements().size(); i++) {
            if (model.getGraphicalElements().get(i) != system) {
                pastedSystem = (SystemGR) model.getGraphicalElements().get(i);
            }
        }
        
        assertNotNull("Pasted system should exist", pastedSystem);
        assertEquals("Pasted system should have 2 use cases", 2, pastedSystem.getNumberOfElements());
        
        // Verify the use cases are properly connected to the pasted system
        final SystemGR finalPastedSystem = pastedSystem;
        pastedSystem.createIterator().forEachRemaining(child -> {
            assertEquals("Child should have pasted system as context", finalPastedSystem, child.getContext());
        });
    }

    @Test
    public void testCopyPasteUndoSingleOperation() {
        SelectionController selectionController = new UCDSelectionController(internalFrame, model);

        // Create a system with use cases inside
        SystemGR system = h.addSystem("System");
        UseCaseGR uc1 = h.addUseCase("UseCase1");
        UseCaseGR uc2 = h.addUseCase("UseCase2");
        
        // Move use cases inside system
        model.removeGraphicalElement(uc1);
        model.removeGraphicalElement(uc2);
        system.add(uc1);
        uc1.setContext(system);
        system.add(uc2);
        uc2.setContext(system);
        
        int initialCount = model.getGraphicalElements().size();
        assertEquals(1, initialCount); // Just the system
        
        // Select and paste the system
        selectionController.addElementToSelection(system);
        selectionController.copySelected();
        selectionController.pasteClipboard();
        
        // Should now have 2 systems
        assertEquals(2, model.getGraphicalElements().size());
        
        // Single undo should remove all pasted elements (1 system + 2 use cases)
        internalFrame.getUndoManager().undo();
        
        // Should be back to original state with single undo
        assertEquals("Single undo should remove entire paste operation", 
                     initialCount, model.getGraphicalElements().size());
    }

}

