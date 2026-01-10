package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import edu.city.studentuml.view.gui.UCExtendEditor;

import javax.swing.undo.UndoManager;

/**
 * Test class for UCExtendGR.edit() method. UCExtendGR is a Use Case Diagram
 * component that represents an extend relationship between use cases. It has
 * extension points that can be edited. UCExtend constructor: new
 * UCExtend(extendingUseCase, extendedUseCase) UCExtend has addExtensionPoint()
 * and getExtensionPoints() methods
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class UCExtendGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testUCExtendGR_EditExtensionPoints_UndoRedo() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);

        // Create two use cases
        UseCase extendingUseCase = new UseCase("HandleException");
        UseCase extendedUseCase = new UseCase("ProcessOrder");
        model.getCentralRepository().addUseCase(extendingUseCase);
        model.getCentralRepository().addUseCase(extendedUseCase);

        // Create graphical wrappers
        UseCaseGR extendingGR = new UseCaseGR(extendingUseCase, 100, 100);
        UseCaseGR extendedGR = new UseCaseGR(extendedUseCase, 300, 100);
        model.addGraphicalElement(extendingGR);
        model.addGraphicalElement(extendedGR);

        // Create UCExtend with initial extension point
        UCExtend ucExtend = new UCExtend(extendingUseCase, extendedUseCase);
        ExtensionPoint ep1 = new ExtensionPoint("Payment Failed");
        ucExtend.addExtensionPoint(ep1);

        // Create UCExtendGR with mock editor
        UCExtendGR ucExtendGR = new UCExtendGR(extendingGR, extendedGR, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parent) {
                        // Return edited UCExtend with additional extension point
                        UCExtend edited = new UCExtend((UseCase) original.getSource(), (UseCase) original.getTarget());
                        edited.addExtensionPoint(new ExtensionPoint("Payment Failed"));
                        edited.addExtensionPoint(new ExtensionPoint("Inventory Unavailable"));
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Verify initial state
        assertEquals(1, ucExtend.getExtensionPoints().size());
        assertEquals("Payment Failed", ucExtend.getExtensionPoints().get(0).getName());

        // Edit the UCExtend
        EditContext context = new EditContext(model, frame);
        boolean editResult = ucExtendGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(2, ucExtend.getExtensionPoints().size());
        assertEquals("Inventory Unavailable", ucExtend.getExtensionPoints().get(1).getName());

        // Verify undo manager has the edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert changes
        undoManager.undo();
        model.modelChanged();
        assertEquals(1, ucExtend.getExtensionPoints().size());
        assertEquals("Payment Failed", ucExtend.getExtensionPoints().get(0).getName());

        // Redo should reapply changes
        assertTrue("Undo manager should have redo", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals(2, ucExtend.getExtensionPoints().size());
    }

    @Test
    public void testUCExtendGR_EditCancel() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);

        // Create use cases
        UseCase extendingUseCase = new UseCase("ValidateInput");
        UseCase extendedUseCase = new UseCase("SubmitForm");
        model.getCentralRepository().addUseCase(extendingUseCase);
        model.getCentralRepository().addUseCase(extendedUseCase);

        // Create graphical wrappers
        UseCaseGR extendingGR = new UseCaseGR(extendingUseCase, 100, 100);
        UseCaseGR extendedGR = new UseCaseGR(extendedUseCase, 300, 100);
        model.addGraphicalElement(extendingGR);
        model.addGraphicalElement(extendedGR);

        // Create UCExtend
        UCExtend ucExtend = new UCExtend(extendingUseCase, extendedUseCase);
        ExtensionPoint ep = new ExtensionPoint("Invalid Data");
        ucExtend.addExtensionPoint(ep);

        // Create UCExtendGR with mock editor that returns null (cancel)
        UCExtendGR ucExtendGR = new UCExtendGR(extendingGR, extendedGR, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parent) {
                        return null; // User cancelled
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Edit should still return true even on cancel (legacy behavior)
        EditContext context = new EditContext(model, frame);
        boolean editResult = ucExtendGR.edit(context);

        assertTrue("Edit returns true even on cancel", editResult);
        assertEquals(1, ucExtend.getExtensionPoints().size());

        // Verify no undo edit was created
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testUCExtendGR_EditEmptyExtensionPoints() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);

        // Create use cases
        UseCase extendingUseCase = new UseCase("HandleTimeout");
        UseCase extendedUseCase = new UseCase("PerformOperation");
        model.getCentralRepository().addUseCase(extendingUseCase);
        model.getCentralRepository().addUseCase(extendedUseCase);

        // Create graphical wrappers
        UseCaseGR extendingGR = new UseCaseGR(extendingUseCase, 100, 100);
        UseCaseGR extendedGR = new UseCaseGR(extendedUseCase, 300, 100);
        model.addGraphicalElement(extendingGR);
        model.addGraphicalElement(extendedGR);

        // Create UCExtend with extension points
        UCExtend ucExtend = new UCExtend(extendingUseCase, extendedUseCase);
        ucExtend.addExtensionPoint(new ExtensionPoint("Timeout"));

        // Create UCExtendGR with mock editor that clears extension points
        UCExtendGR ucExtendGR = new UCExtendGR(extendingGR, extendedGR, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parent) {
                        // Return UCExtend with no extension points
                        return new UCExtend((UseCase) original.getSource(), (UseCase) original.getTarget());
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Edit to remove extension points
        EditContext context = new EditContext(model, frame);
        boolean editResult = ucExtendGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(0, ucExtend.getExtensionPoints().size());
    }

    @Test
    public void testUCExtendGR_EditAddMultipleExtensionPoints() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);

        // Create use cases
        UseCase extendingUseCase = new UseCase("SendNotification");
        UseCase extendedUseCase = new UseCase("PlaceOrder");
        model.getCentralRepository().addUseCase(extendingUseCase);
        model.getCentralRepository().addUseCase(extendedUseCase);

        // Create graphical wrappers
        UseCaseGR extendingGR = new UseCaseGR(extendingUseCase, 100, 100);
        UseCaseGR extendedGR = new UseCaseGR(extendedUseCase, 300, 100);
        model.addGraphicalElement(extendingGR);
        model.addGraphicalElement(extendedGR);

        // Create UCExtend with no extension points
        UCExtend ucExtend = new UCExtend(extendingUseCase, extendedUseCase);

        // Create UCExtendGR with mock editor that adds multiple extension points
        UCExtendGR ucExtendGR = new UCExtendGR(extendingGR, extendedGR, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parent) {
                        // Add multiple extension points
                        UCExtend edited = new UCExtend((UseCase) original.getSource(), (UseCase) original.getTarget());
                        edited.addExtensionPoint(new ExtensionPoint("Order Confirmed"));
                        edited.addExtensionPoint(new ExtensionPoint("Payment Successful"));
                        edited.addExtensionPoint(new ExtensionPoint("Items Shipped"));
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Edit to add extension points
        EditContext context = new EditContext(model, frame);
        boolean editResult = ucExtendGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals(3, ucExtend.getExtensionPoints().size());
        assertEquals("Order Confirmed", ucExtend.getExtensionPoints().get(0).getName());
        assertEquals("Payment Successful", ucExtend.getExtensionPoints().get(1).getName());
        assertEquals("Items Shipped", ucExtend.getExtensionPoints().get(2).getName());
    }

    @Test
    public void testUCExtendGR_CreateEditorMethodExists() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);

        // Create use cases
        UseCase extendingUseCase = new UseCase("Extending");
        UseCase extendedUseCase = new UseCase("Extended");
        model.getCentralRepository().addUseCase(extendingUseCase);
        model.getCentralRepository().addUseCase(extendedUseCase);

        // Create graphical wrappers
        UseCaseGR extendingGR = new UseCaseGR(extendingUseCase, 100, 100);
        UseCaseGR extendedGR = new UseCaseGR(extendedUseCase, 300, 100);

        UCExtend ucExtend = new UCExtend(extendingUseCase, extendedUseCase);
        UCExtendGR ucExtendGR = new UCExtendGR(extendingGR, extendedGR, ucExtend);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        UCExtendEditor editor = ucExtendGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}
