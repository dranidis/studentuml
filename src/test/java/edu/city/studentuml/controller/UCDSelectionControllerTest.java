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
import edu.city.studentuml.model.graphical.UCAssociationGR;
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
        model = new UCDModel("ccd", umlProject);
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
        UCAssociationGR as = h.addUcAssociation(a, u);
        assertEquals(3, model.getGraphicalElements().size());
        
        selectionController.addElementToSelection(a);

        System.out.println("DELETE");
        selectionController.deleteSelected();

        model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(1, model.getGraphicalElements().size());

        System.out.println("UNDO");
        internalFrame.getUndoManager().undo();
        
        model.getGraphicalElements().forEach(e -> System.out.println(e));
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
        System.out.println(s.getNumberOfElements());
        s.createIterator().forEachRemaining(e -> System.out.println(e));

        selectionController.addElementToSelection(s);

        System.out.println("DELETE and UNDO");
        selectionController.deleteSelected();
        assertEquals(0, model.getGraphicalElements().size());

        internalFrame.getUndoManager().undo();

        model.getGraphicalElements().forEach(e -> System.out.println(e));
        System.out.println(s.getNumberOfElements());

        s.createIterator().forEachRemaining(e -> System.out.println(e));

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

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());

        selectionController.addElementToSelection(s2);

        System.out.println("DELETE");
        selectionController.deleteSelected();

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

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());
        assertEquals(1, s2.getNumberOfElements());

        selectionController.addElementToSelection(s3);

        System.out.println("DELETE");
        selectionController.deleteSelected();

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, s1.getNumberOfElements());
        assertEquals(0, s2.getNumberOfElements());
             
    }


    // @Test
    // public void testDeleteElementUndo() {
    //     DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

    //     /**
    //      * Adds a conceptual class A
    //      */
    //     GraphicalElement cGr = h.addClass("A");

    //     ccdSelectionController.deleteElement(cGr);

    //     assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> 
    //     ge instanceof ClassGR));

    //     internalFrame.getUndoManager().undo();

    //     assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> 
    //     ge instanceof ClassGR));        
    // }

    // @Test
    // public void testDeleteElementWithRelationshipsUndo() {
    //     DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

    //     ClassGR a = h.addClass("A");
    //     ClassGR b = h.addClass("B");
    //     ClassGR c = h.addClass("C");
    //     ClassGR d = h.addClass("D");
    //     ClassGR f = h.addClass("F");

    //     h.addAssociation(a, b);
    //     h.addAssociation(c, a);
    //     h.addAggregation(a, d);
    //     h.addAggregation(c, a);
    //     h.addGeneralization(c, a);
    //     h.addGeneralization(a, b);
    //     h.addAssociationClass(a, f);

    //     System.out.println("BEFORE");
    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(7, h.countRelationshipsWithClassNamed("A"));

    //     /**
    //      * DELETE a
    //      */
    //     ccdSelectionController.deleteElement(a);

    //     assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR
    //             && ((ClassGR) ge).getAbstractClass().getName().equals("A")));
    //     assertEquals(0, h.countRelationshipsWithClassNamed("A"));

    //     System.out.println("DELETED A");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));

    //     /**
    //      * UNDO
    //      */
    //     internalFrame.getUndoManager().undo();
    //     System.out.println("UNDONE");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));

    //     assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ClassGR));
    //     assertEquals(7, h.countRelationshipsWithClassNamed("A"));
    // }  
    
    // @Test
    // public void testdeleteSelectedElements() {
    //     DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

    //     ClassGR a = h.addClass("A");
    //     ClassGR b = h.addClass("B");

    //     h.addAssociation(a, b);
    //     h.addAssociation(a, b);

    //     System.out.println("BEFORE");
    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(4, model.getGraphicalElements().size());

    //     /**
    //      * DELETE all
    //      */
    //     ccdSelectionController.selectAll();
    //     ccdSelectionController.deleteSelected();


    //     System.out.println("DELETE ALL");
    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(0, model.getGraphicalElements().size());

    //     /**
    //      * UNDO
    //      */
    //     internalFrame.getUndoManager().undo();
    //     System.out.println("UNDO");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(4, model.getGraphicalElements().size());

    //             /**
    //      * REDO
    //      */
    //     internalFrame.getUndoManager().redo();
    //     System.out.println("REDO");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(0, model.getGraphicalElements().size());
    // }  

    // @Test
    // public void testdeleteSelectedElementsWithNotes() {
    //     DCDSelectionController ccdSelectionController  = new DCDSelectionController(internalFrame, model);

    //     ClassGR a = h.addClass("A");
    //     ClassGR b = h.addClass("B");

    //     AssociationGR ab1 = h.addAssociation(a, b);
    //     GeneralizationGR ab2 = h.addGeneralization(a, b);

    //     h.addNote(a);
    //     h.addNote(b);
    //     h.addNote(ab1);
    //     h.addNote(ab2);

    //     int countAll = 8;

    //     System.out.println("BEFORE");
    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(countAll, model.getGraphicalElements().size());

    //     /**
    //      * DELETE all
    //      */
    //     ccdSelectionController.selectAll();
    //     ccdSelectionController.deleteSelected();


    //     System.out.println("DELETE ALL");
    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(0, model.getGraphicalElements().size());

    //     /**
    //      * UNDO
    //      */
    //     internalFrame.getUndoManager().undo();
    //     System.out.println("UNDO");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(countAll, model.getGraphicalElements().size());

    //             /**
    //      * REDO
    //      */
    //     internalFrame.getUndoManager().redo();
    //     System.out.println("REDO");

    //     model.getGraphicalElements().forEach(e -> System.out.println(e));
    //     assertEquals(0, model.getGraphicalElements().size());
    // }  


}
