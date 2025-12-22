package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.SDInternalFrame;

public class SDSelectionControllerTest {

    UMLProject umlProject;
    SDModel model;
    SDInternalFrame internalFrame;
    SDHelper h;
    SelectionController selectionController;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new SDModel("sd", umlProject);
        internalFrame = new SDInternalFrame(model);
        h = new SDHelper(model);
        selectionController = new SDSelectionController(internalFrame, model);
    }

    @Test
    public void testCreation() {
        assertNotNull(selectionController);
    }

    @Test
    public void testCopyPasteMessagesWithObjects() {
        // Create two SD objects
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);

        // Create messages between them
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);
        ReturnMessageGR returnMsg = h.addReturnMessage(objB, objA, "result", 200);

        // Select all elements
        selectionController.addElementToSelection(objA);
        selectionController.addElementToSelection(objB);
        selectionController.addElementToSelection(callMsg);
        selectionController.addElementToSelection(returnMsg);

        // Copy
        selectionController.copySelected();

        // Paste
        selectionController.pasteClipboard();

        // Count objects and messages after paste
        long objectCountAfter = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof RoleClassifierGR)
                .count();
        long callMessageCountAfter = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof CallMessageGR)
                .count();
        long returnMessageCountAfter = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof ReturnMessageGR)
                .count();

        // Verify: Should have double the objects and messages
        assertEquals(4, objectCountAfter);
        assertEquals(2, callMessageCountAfter);
        assertEquals(2, returnMessageCountAfter);

        // Verify that pasted messages connect pasted objects (not originals)
        CallMessageGR[] callMessages = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof CallMessageGR)
                .map(ge -> (CallMessageGR) ge)
                .toArray(CallMessageGR[]::new);

        // Second call message should be the pasted one
        CallMessageGR pastedCall = callMessages[1];
        assertTrue("Pasted call message should connect different objects than original",
                pastedCall.getSource() != objA && pastedCall.getTarget() != objB);
        assertNotNull("Pasted call message should have a source", pastedCall.getSource());
        assertNotNull("Pasted call message should have a target", pastedCall.getTarget());

        // Verify the message connects RoleClassifier objects
        assertTrue("Source should be RoleClassifierGR", pastedCall.getSource() instanceof RoleClassifierGR);
        assertTrue("Target should be RoleClassifierGR", pastedCall.getTarget() instanceof RoleClassifierGR);
    }

    @Test
    public void testCopyPasteUndoMessages() {
        // Create two SD objects and a message
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);

        assertEquals(3, model.getGraphicalElements().size());

        // Select all
        selectionController.addElementToSelection(objA);
        selectionController.addElementToSelection(objB);
        selectionController.addElementToSelection(callMsg);

        // Copy and paste
        selectionController.copySelected();
        selectionController.pasteClipboard();

        // Count elements after paste
        long objectCountAfter = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof RoleClassifierGR)
                .count();

        // Verify doubled (2 original -> 4 after paste)
        assertEquals(4, objectCountAfter);

        // Undo - should remove all pasted elements in one operation
        internalFrame.getUndoManager().undo();

        long objectCountAfterUndo = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof RoleClassifierGR)
                .count();

        // Verify back to original 2
        assertEquals(2, objectCountAfterUndo);

        // Verify original elements still exist
        assertTrue("Original objA should exist", model.getGraphicalElements().contains(objA));
        assertTrue("Original objB should exist", model.getGraphicalElements().contains(objB));
        assertTrue("Original message should exist", model.getGraphicalElements().contains(callMsg));
    }

    @Test
    public void testCopyPasteOnlyMessagesWithoutObjects() {
        // Create two SD objects and a message
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);

        assertEquals(3, model.getGraphicalElements().size());

        // Select only the message (not the objects)
        selectionController.addElementToSelection(callMsg);

        // Copy
        selectionController.copySelected();

        // Paste - should not paste the message since endpoints aren't copied
        selectionController.pasteClipboard();

        // Count objects - should still be 2 (message can't be pasted without objects)
        long objectCountAfter = model.getGraphicalElements().stream()
                .filter(ge -> ge instanceof RoleClassifierGR)
                .count();
        
        assertEquals(2, objectCountAfter);
    }
}
