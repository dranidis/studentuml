package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.view.gui.UCExtendEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;

import static org.junit.Assert.*;

/**
 * Test demonstrating how to test GraphicalElement.edit() methods using the
 * Template Method pattern. The protected createEditor() method is overridden to
 * return a mock editor that doesn't show UI, enabling headless testing.
 */
public class UCExtendGREditTest {

    private UMLProject umlProject;
    private UCDModel model;
    private DiagramInternalFrame frame;
    private UndoManager undoManager;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new UCDModel("ucd", umlProject);
        frame = new UCDInternalFrame(model);
        undoManager = frame.getUndoManager();
    }

    @Test
    public void testEditUCExtend_AddExtensionPoint_UndoRedo() {
        // Setup: Create a use case extend relationship
        UseCaseGR extendingUseCase = new UseCaseGR(new UseCase("Extending Use Case"), 200, 50);
        UseCaseGR baseUseCase = new UseCaseGR(new UseCase("Base Use Case"), 50, 50);

        model.addGraphicalElement(baseUseCase);
        model.addGraphicalElement(extendingUseCase);

        UCExtend ucExtend = new UCExtend(
                (UseCase) extendingUseCase.getComponent(),
                (UseCase) baseUseCase.getComponent());

        // Create testable UCExtendGR with mock editor
        UCExtendGR ucExtendGR = new UCExtendGR(extendingUseCase, baseUseCase, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                // Return a mock editor that modifies the object without showing UI
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parentComponent) {
                        // Simulate user adding an extension point
                        UCExtend modified = original.clone();
                        modified.addExtensionPoint(new ExtensionPoint("Test Extension Point"));
                        return modified;
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Record original state
        assertEquals("Should start with no extension points", 0, ucExtend.getExtensionPoints().size());

        // Execute: Call edit() - uses mock editor, doesn't show UI
        EditContext context = new EditContext(model, frame);
        boolean result = ucExtendGR.edit(context);

        // Verify: Edit succeeded and extension point was added
        assertTrue("Edit should succeed", result);
        assertEquals("Should have 1 extension point after edit", 1, ucExtend.getExtensionPoints().size());
        assertEquals("Extension point name should match",
                "Test Extension Point",
                ucExtend.getExtensionPoints().get(0).getName());

        // Test Undo
        assertTrue("Should be able to undo", undoManager.canUndo());
        undoManager.undo();
        model.modelChanged(); // Trigger view update

        assertEquals("Should have no extension points after undo", 0, ucExtend.getExtensionPoints().size());

        // Test Redo
        assertTrue("Should be able to redo", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged(); // Trigger view update

        assertEquals("Should have 1 extension point after redo", 1, ucExtend.getExtensionPoints().size());
        assertEquals("Extension point name should match after redo",
                "Test Extension Point",
                ucExtend.getExtensionPoints().get(0).getName());
    }

    @Test
    public void testEditUCExtend_UserCancels() {
        // Setup: Create a use case extend relationship
        UseCaseGR extendingUseCase = new UseCaseGR(new UseCase("Extending Use Case"), 200, 50);
        UseCaseGR baseUseCase = new UseCaseGR(new UseCase("Base Use Case"), 50, 50);

        model.addGraphicalElement(baseUseCase);
        model.addGraphicalElement(extendingUseCase);

        UCExtend ucExtend = new UCExtend(
                (UseCase) extendingUseCase.getComponent(),
                (UseCase) baseUseCase.getComponent());

        // Create testable UCExtendGR with mock editor that simulates cancel
        UCExtendGR ucExtendGR = new UCExtendGR(extendingUseCase, baseUseCase, ucExtend) {
            @Override
            protected UCExtendEditor createEditor(EditContext context) {
                return new UCExtendEditor(context.getRepository()) {
                    @Override
                    public UCExtend editDialog(UCExtend original, Component parentComponent) {
                        // Return null to simulate user cancelling
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(ucExtendGR);

        // Execute: Call edit() with mock editor that cancels
        EditContext context = new EditContext(model, frame);
        boolean result = ucExtendGR.edit(context);

        // Verify: Edit returns true (handled cancel), no changes made
        assertTrue("Edit should return true even when cancelled", result);
        assertEquals("Should still have no extension points", 0, ucExtend.getExtensionPoints().size());
        assertFalse("Should not be able to undo cancelled operation", undoManager.canUndo());
    }
}
