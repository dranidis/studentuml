package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import java.awt.Component;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.SDInternalFrame;

import javax.swing.undo.UndoManager;

/**
 * Test class for CreateMessageGR.edit() method. CreateMessageGR is a Sequence
 * Diagram component that represents a create message between role classifiers.
 * CreateMessage extends CallMessage and has parameters. CreateMessage
 * constructor: new CreateMessage(from, to) CreateMessage has setParameters()
 * method
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class CreateMessageGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testCreateMessageGR_EditParameters_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create two design classes for source and target
        DesignClass orderClass = new DesignClass("Order");
        DesignClass paymentClass = new DesignClass("Payment");
        model.getCentralRepository().addClass(orderClass);
        model.getCentralRepository().addClass(paymentClass);

        // Create SDObjects (role classifiers)
        SDObject fromObject = new SDObject("order1", orderClass);
        SDObject toObject = new SDObject("payment1", paymentClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create CreateMessage with initial parameters
        CreateMessage createMessage = new CreateMessage(fromObject, toObject);
        MethodParameter param1 = new MethodParameter("amount");
        createMessage.getParameters().add(param1);

        // Create CreateMessageGR with mock editor
        CreateMessageGR createMessageGR = new CreateMessageGR(fromGR, toGR, createMessage, 150) {
            @Override
            protected CallMessageEditor createEditor(EditContext context) {
                return new CallMessageEditor(context.getRepository()) {
                    @Override
                    public CallMessage editDialog(CallMessage original, Component parent) {
                        // Editor modifies message in place - add another parameter
                        MethodParameter param2 = new MethodParameter("currency");
                        original.getParameters().add(param2);
                        return original; // Return the modified message
                    }
                };
            }
        };

        model.addGraphicalElement(createMessageGR);

        // Verify initial state
        assertEquals(1, createMessage.getParameters().size());
        assertEquals("amount", createMessage.getParameters().get(0).getName());

        // Edit the create message
        EditContext context = new EditContext(model, frame);
        boolean editResult = createMessageGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(2, createMessage.getParameters().size());
        assertEquals("currency", createMessage.getParameters().get(1).getName());

        // Verify undo manager has the edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert changes
        undoManager.undo();
        model.modelChanged();
        assertEquals(1, createMessage.getParameters().size());
        assertEquals("amount", createMessage.getParameters().get(0).getName());

        // Redo should reapply changes
        assertTrue("Undo manager should have redo", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals(2, createMessage.getParameters().size());
    }

    @Test
    public void testCreateMessageGR_EditCancel() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create classes and objects
        DesignClass orderClass = new DesignClass("Order");
        DesignClass itemClass = new DesignClass("Item");
        model.getCentralRepository().addClass(orderClass);
        model.getCentralRepository().addClass(itemClass);

        SDObject fromObject = new SDObject("order1", orderClass);
        SDObject toObject = new SDObject("item1", itemClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create CreateMessage
        CreateMessage createMessage = new CreateMessage(fromObject, toObject);
        MethodParameter param = new MethodParameter("id");
        createMessage.getParameters().add(param);

        // Create CreateMessageGR with mock editor that returns null (cancel)
        CreateMessageGR createMessageGR = new CreateMessageGR(fromGR, toGR, createMessage, 150) {
            @Override
            protected CallMessageEditor createEditor(EditContext context) {
                return new CallMessageEditor(context.getRepository()) {
                    @Override
                    public CallMessage editDialog(CallMessage original, Component parent) {
                        return null; // User cancelled
                    }
                };
            }
        };

        model.addGraphicalElement(createMessageGR);

        // Edit should still return true even on cancel (legacy behavior)
        EditContext context = new EditContext(model, frame);
        boolean editResult = createMessageGR.edit(context);

        assertTrue("Edit returns true even on cancel", editResult);
        assertEquals(1, createMessage.getParameters().size());

        // Verify no undo edit was created
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testCreateMessageGR_EditEmptyParameters() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create classes and objects
        DesignClass customerClass = new DesignClass("Customer");
        DesignClass accountClass = new DesignClass("Account");
        model.getCentralRepository().addClass(customerClass);
        model.getCentralRepository().addClass(accountClass);

        SDObject fromObject = new SDObject("customer1", customerClass);
        SDObject toObject = new SDObject("account1", accountClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create CreateMessage with parameters
        CreateMessage createMessage = new CreateMessage(fromObject, toObject);
        MethodParameter param = new MethodParameter("balance");
        createMessage.getParameters().add(param);

        // Create CreateMessageGR with mock editor that clears parameters
        CreateMessageGR createMessageGR = new CreateMessageGR(fromGR, toGR, createMessage, 150) {
            @Override
            protected CallMessageEditor createEditor(EditContext context) {
                return new CallMessageEditor(context.getRepository()) {
                    @Override
                    public CallMessage editDialog(CallMessage original, Component parent) {
                        // Clear all parameters
                        original.getParameters().clear();
                        return original;
                    }
                };
            }
        };

        model.addGraphicalElement(createMessageGR);

        // Edit to remove parameters
        EditContext context = new EditContext(model, frame);
        boolean editResult = createMessageGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(0, createMessage.getParameters().size());
    }

    @Test
    public void testCreateMessageGR_EditAddMultipleParameters() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create classes and objects
        DesignClass productClass = new DesignClass("Product");
        DesignClass reviewClass = new DesignClass("Review");
        model.getCentralRepository().addClass(productClass);
        model.getCentralRepository().addClass(reviewClass);

        SDObject fromObject = new SDObject("product1", productClass);
        SDObject toObject = new SDObject("review1", reviewClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create CreateMessage with no parameters
        CreateMessage createMessage = new CreateMessage(fromObject, toObject);

        // Create CreateMessageGR with mock editor that adds multiple parameters
        CreateMessageGR createMessageGR = new CreateMessageGR(fromGR, toGR, createMessage, 150) {
            @Override
            protected CallMessageEditor createEditor(EditContext context) {
                return new CallMessageEditor(context.getRepository()) {
                    @Override
                    public CallMessage editDialog(CallMessage original, Component parent) {
                        // Add multiple parameters
                        original.getParameters().add(new MethodParameter("rating"));
                        original.getParameters().add(new MethodParameter("comment"));
                        original.getParameters().add(new MethodParameter("date"));
                        return original;
                    }
                };
            }
        };

        model.addGraphicalElement(createMessageGR);

        // Edit to add parameters
        EditContext context = new EditContext(model, frame);
        boolean editResult = createMessageGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(3, createMessage.getParameters().size());
        assertEquals("rating", createMessage.getParameters().get(0).getName());
        assertEquals("comment", createMessage.getParameters().get(1).getName());
        assertEquals("date", createMessage.getParameters().get(2).getName());
    }

    @Test
    public void testCreateMessageGR_CreateEditorMethodExists() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create classes and objects
        DesignClass fromClass = new DesignClass("From");
        DesignClass toClass = new DesignClass("To");
        model.getCentralRepository().addClass(fromClass);
        model.getCentralRepository().addClass(toClass);

        SDObject fromObject = new SDObject("from1", fromClass);
        SDObject toObject = new SDObject("to1", toClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);

        CreateMessage createMessage = new CreateMessage(fromObject, toObject);
        CreateMessageGR createMessageGR = new CreateMessageGR(fromGR, toGR, createMessage, 150);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        CallMessageEditor editor = createMessageGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}
