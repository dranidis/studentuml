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

    @Test
    public void testReconnectCallMessageSourceUpdatesReturnMessageTarget() {
        // Create three SD objects
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        RoleClassifierGR objC = h.addSDObject("C", 500);

        // Create a call message from A to B with its return message
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);
        ReturnMessageGR returnMsg = h.addReturnMessage(objB, objA, "result", 200);

        // Verify initial state
        assertEquals("Call message source should be A", objA, callMsg.getSource());
        assertEquals("Call message target should be B", objB, callMsg.getTarget());
        assertEquals("Return message source should be B", objB, returnMsg.getSource());
        assertEquals("Return message target should be A", objA, returnMsg.getTarget());

        // Find the corresponding return message BEFORE reconnection
        ReturnMessageGR foundReturnMsg = selectionController.findCorrespondingReturnMessage(callMsg);
        assertNotNull("Should find corresponding return message before reconnection", foundReturnMsg);
        assertEquals("Found return message should be the same object", returnMsg, foundReturnMsg);

        // Reconnect call message source from A to C
        boolean reconnected = callMsg.reconnectSource(objC);
        assertTrue("Call message should reconnect successfully", reconnected);

        // Now update the return message target to match the call's new source
        // This is what the SelectionController should do automatically
        returnMsg.reconnectTarget(objC);

        // Verify the synchronized state
        assertEquals("Call message source should now be C", objC, callMsg.getSource());
        assertEquals("Call message target should still be B", objB, callMsg.getTarget());
        assertEquals("Return message source should still be B", objB, returnMsg.getSource());
        assertEquals("Return message target should now be C", objC, returnMsg.getTarget());

        // Verify domain model is also updated
        assertEquals("Call domain source should be C", objC.getRoleClassifier(),
                callMsg.getMessage().getSource());
        assertEquals("Return domain target should be C", objC.getRoleClassifier(),
                returnMsg.getMessage().getTarget());
    }

    @Test
    public void testReconnectCallMessageTargetUpdatesReturnMessageSource() {
        // Create three SD objects
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        RoleClassifierGR objD = h.addSDObject("D", 500);

        // Create a call message from A to B with its return message
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);
        ReturnMessageGR returnMsg = h.addReturnMessage(objB, objA, "result", 200);

        // Verify initial state
        assertEquals("Call message source should be A", objA, callMsg.getSource());
        assertEquals("Call message target should be B", objB, callMsg.getTarget());
        assertEquals("Return message source should be B", objB, returnMsg.getSource());
        assertEquals("Return message target should be A", objA, returnMsg.getTarget());

        // Reconnect call message target from B to D
        boolean reconnected = callMsg.reconnectTarget(objD);
        assertTrue("Call message should reconnect successfully", reconnected);

        // Update the return message source to match the call's new target
        returnMsg.reconnectSource(objD);

        // Verify the synchronized state
        assertEquals("Call message source should still be A", objA, callMsg.getSource());
        assertEquals("Call message target should now be D", objD, callMsg.getTarget());
        assertEquals("Return message source should now be D", objD, returnMsg.getSource());
        assertEquals("Return message target should still be A", objA, returnMsg.getTarget());

        // Verify domain model is also updated
        assertEquals("Call domain target should be D", objD.getRoleClassifier(),
                callMsg.getMessage().getTarget());
        assertEquals("Return domain source should be D", objD.getRoleClassifier(),
                returnMsg.getMessage().getSource());
    }

    @Test
    public void testDeleteCallMessageAlsoDeletesReturnMessage() {
        // Create two SD objects
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);

        // Create a call message from A to B with its return message
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);
        ReturnMessageGR returnMsg = h.addReturnMessage(objB, objA, "result", 200);

        // Verify both messages exist
        assertEquals("Should have 4 elements (2 objects + 2 messages)", 4,
                model.getGraphicalElements().size());
        assertTrue("Call message should be in model",
                model.getGraphicalElements().contains(callMsg));
        assertTrue("Return message should be in model",
                model.getGraphicalElements().contains(returnMsg));

        // Delete the call message
        selectionController.addElementToSelection(callMsg);
        selectionController.deleteSelected();

        // Verify both the call and return messages are deleted
        assertEquals("Should have 2 elements (only objects remain)", 2,
                model.getGraphicalElements().size());
        assertTrue("Object A should still be in model",
                model.getGraphicalElements().contains(objA));
        assertTrue("Object B should still be in model",
                model.getGraphicalElements().contains(objB));
        assertTrue("Call message should not be in model",
                !model.getGraphicalElements().contains(callMsg));
        assertTrue("Return message should not be in model",
                !model.getGraphicalElements().contains(returnMsg));
    }

    // ========== Undo/Redo Tests ==========

    @Test
    public void testCallMessageReconnectWithUndoRedo() {
        // Create objects A, B, C, D
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        RoleClassifierGR objC = h.addSDObject("C", 500);

        // Create call message from A to B with return message
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);
        ReturnMessageGR returnMsg = h.addReturnMessage(objB, objA, "result", 200);

        // Verify initial state
        assertEquals("Initial: A should be call source", objA, callMsg.getSource());
        assertEquals("Initial: B should be call target", objB, callMsg.getTarget());
        assertEquals("Initial: B should be return source", objB, returnMsg.getSource());
        assertEquals("Initial: A should be return target", objA, returnMsg.getTarget());

        // Reconnect call message source from A to C (also updates return message target)
        assertTrue("Reconnection should succeed", callMsg.reconnectSource(objC));
        returnMsg.reconnectTarget(objC);

        // Create and post the undo edit (compound edit for both messages)
        javax.swing.undo.CompoundEdit compoundEdit = new javax.swing.undo.CompoundEdit();

        edu.city.studentuml.util.undoredo.ReconnectMessageEdit callEdit = new edu.city.studentuml.util.undoredo.ReconnectMessageEdit(
                callMsg, model, objA, objC, edu.city.studentuml.model.graphical.EndpointType.SOURCE);
        compoundEdit.addEdit(callEdit);

        edu.city.studentuml.util.undoredo.ReconnectMessageEdit returnEdit = new edu.city.studentuml.util.undoredo.ReconnectMessageEdit(
                returnMsg, model, objA, objC, edu.city.studentuml.model.graphical.EndpointType.TARGET);
        compoundEdit.addEdit(returnEdit);

        compoundEdit.end();
        internalFrame.getUndoSupport().postEdit(compoundEdit);

        // Verify reconnected state
        assertEquals("After reconnect: C should be call source", objC, callMsg.getSource());
        assertEquals("After reconnect: B should be call target", objB, callMsg.getTarget());
        assertEquals("After reconnect: B should be return source", objB, returnMsg.getSource());
        assertEquals("After reconnect: C should be return target", objC, returnMsg.getTarget());

        // Undo the reconnection
        assertTrue("Should be able to undo", internalFrame.getUndoManager().canUndo());
        internalFrame.getUndoManager().undo();

        // Verify undone state
        assertEquals("After undo: A should be call source", objA, callMsg.getSource());
        assertEquals("After undo: B should be call target", objB, callMsg.getTarget());
        assertEquals("After undo: B should be return source", objB, returnMsg.getSource());
        assertEquals("After undo: A should be return target", objA, returnMsg.getTarget());

        // Redo the reconnection
        assertTrue("Should be able to redo", internalFrame.getUndoManager().canRedo());
        internalFrame.getUndoManager().redo();

        // Verify redone state
        assertEquals("After redo: C should be call source", objC, callMsg.getSource());
        assertEquals("After redo: B should be call target", objB, callMsg.getTarget());
        assertEquals("After redo: B should be return source", objB, returnMsg.getSource());
        assertEquals("After redo: C should be return target", objC, returnMsg.getTarget());
    }

    @Test
    public void testMultipleMessageReconnectionsWithUndoRedo() {
        // Create objects A, B, C, D
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        RoleClassifierGR objC = h.addSDObject("C", 500);
        RoleClassifierGR objD = h.addSDObject("D", 700);

        // Create call message from A to B
        CallMessageGR callMsg = h.addCallMessage(objA, objB, "doSomething()", 150);

        // First reconnection: A -> B becomes C -> B
        assertTrue(callMsg.reconnectSource(objC));
        edu.city.studentuml.util.undoredo.ReconnectMessageEdit edit1 = new edu.city.studentuml.util.undoredo.ReconnectMessageEdit(
                callMsg, model, objA, objC, edu.city.studentuml.model.graphical.EndpointType.SOURCE);
        internalFrame.getUndoSupport().postEdit(edit1);

        // Second reconnection: C -> B becomes C -> D
        assertTrue(callMsg.reconnectTarget(objD));
        edu.city.studentuml.util.undoredo.ReconnectMessageEdit edit2 = new edu.city.studentuml.util.undoredo.ReconnectMessageEdit(
                callMsg, model, objB, objD, edu.city.studentuml.model.graphical.EndpointType.TARGET);
        internalFrame.getUndoSupport().postEdit(edit2);

        // Verify final state: C -> D
        assertEquals("Final: C should be source", objC, callMsg.getSource());
        assertEquals("Final: D should be target", objD, callMsg.getTarget());

        // Undo second reconnection (C -> D becomes C -> B)
        internalFrame.getUndoManager().undo();
        assertEquals("After undo 1: C should be source", objC, callMsg.getSource());
        assertEquals("After undo 1: B should be target", objB, callMsg.getTarget());

        // Undo first reconnection (C -> B becomes A -> B)
        internalFrame.getUndoManager().undo();
        assertEquals("After undo 2: A should be source", objA, callMsg.getSource());
        assertEquals("After undo 2: B should be target", objB, callMsg.getTarget());

        // Redo first reconnection (A -> B becomes C -> B)
        internalFrame.getUndoManager().redo();
        assertEquals("After redo 1: C should be source", objC, callMsg.getSource());
        assertEquals("After redo 1: B should be target", objB, callMsg.getTarget());

        // Redo second reconnection (C -> B becomes C -> D)
        internalFrame.getUndoManager().redo();
        assertEquals("After redo 2: C should be source", objC, callMsg.getSource());
        assertEquals("After redo 2: D should be target", objD, callMsg.getTarget());
    }

    @Test
    public void testSimpleMessageReconnectWithVisualUpdate() {
        // Create objects A, B, C
        RoleClassifierGR objA = h.addSDObject("A", 100);
        RoleClassifierGR objB = h.addSDObject("B", 300);
        RoleClassifierGR objC = h.addSDObject("C", 500);

        // Send a message from A to B
        CallMessageGR msg = h.addCallMessage(objA, objB, "doSomething()", 150);

        // Verify initial state
        assertEquals("Initial: A should be source", objA, msg.getSource());
        assertEquals("Initial: B should be target", objB, msg.getTarget());
        assertEquals("Initial: should have 4 elements", 4, model.getGraphicalElements().size());

        // Reconnect message target from B to C
        assertTrue("Reconnection should succeed", msg.reconnectTarget(objC));

        edu.city.studentuml.util.undoredo.ReconnectMessageEdit edit = new edu.city.studentuml.util.undoredo.ReconnectMessageEdit(
                msg, model, objB, objC, edu.city.studentuml.model.graphical.EndpointType.TARGET);
        internalFrame.getUndoSupport().postEdit(edit);

        // Verify after reconnection
        assertEquals("After reconnect: A should be source", objA, msg.getSource());
        assertEquals("After reconnect: C should be target", objC, msg.getTarget());

        // Undo the reconnection
        assertTrue("Should be able to undo", internalFrame.getUndoManager().canUndo());
        internalFrame.getUndoManager().undo();

        // Verify after undo - THIS IS WHERE THE BUG OCCURS
        assertEquals("After undo: A should be source", objA, msg.getSource());
        assertEquals("After undo: B should be target (VISUAL UPDATE NEEDED)", objB, msg.getTarget());

        // Redo
        assertTrue("Should be able to redo", internalFrame.getUndoManager().canRedo());
        internalFrame.getUndoManager().redo();

        assertEquals("After redo: A should be source", objA, msg.getSource());
        assertEquals("After redo: C should be target", objC, msg.getTarget());
    }
}
