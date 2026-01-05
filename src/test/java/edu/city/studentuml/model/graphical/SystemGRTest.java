package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.view.gui.StringEditorDialog;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;

import static org.junit.Assert.*;

/**
 * Test suite for SystemGR edit functionality with undo/redo support.
 */
public class SystemGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testSystemGR_EditName_UndoRedo() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create system
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System(
                "BookstoreSystem");
        model.getCentralRepository().addSystem(system);
        SystemGR systemGR = new SystemGR(system, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return "OnlineBookstoreSystem";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(systemGR);

        assertEquals("BookstoreSystem", system.getName());

        // Edit: change name
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("OnlineBookstoreSystem", system.getName());

        // Undo: back to original name
        undoManager.undo();
        model.modelChanged();
        assertEquals("BookstoreSystem", system.getName());

        // Redo: apply new name again
        undoManager.redo();
        model.modelChanged();
        assertEquals("OnlineBookstoreSystem", system.getName());
    }

    @Test
    public void testSystemGR_DuplicateNameValidation_ShouldPreventEdit() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create two systems
        edu.city.studentuml.model.domain.System system1 = new edu.city.studentuml.model.domain.System(
                "BookstoreSystem");
        edu.city.studentuml.model.domain.System system2 = new edu.city.studentuml.model.domain.System(
                "LibrarySystem");
        model.getCentralRepository().addSystem(system1);
        model.getCentralRepository().addSystem(system2);

        SystemGR systemGR1 = new SystemGR(system1, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        // Try to set name to existing system name
                        return "LibrarySystem";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(systemGR1);

        assertEquals("BookstoreSystem", system1.getName());
        assertEquals("LibrarySystem", system2.getName());

        // Edit: attempt to change to duplicate name
        EditContext context = new EditContext(model, frame);
        boolean editResult = false;
        try {
            editResult = systemGR1.edit(context);
        } catch (java.awt.HeadlessException e) {
            // Expected in headless test environment when error dialog is shown
            // The important thing is that the edit was blocked before mutation
        }

        // Edit should be prevented due to duplicate
        assertFalse("Edit should return false for duplicate name", editResult);
        assertFalse("UndoManager should not have edits", undoManager.canUndo());
        assertEquals("BookstoreSystem", system1.getName()); // Name should remain unchanged
    }

    @Test
    public void testSystemGR_EmptyNameAllowed_ShouldSucceed() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create system
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System(
                "BookstoreSystem");
        model.getCentralRepository().addSystem(system);
        SystemGR systemGR = new SystemGR(system, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        // Return empty string
                        return "";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(systemGR);

        assertEquals("BookstoreSystem", system.getName());

        // Edit: change to empty name (should be allowed - empty check is only in duplicate predicate)
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemGR.edit(context);

        assertTrue("Edit should return true for empty name", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("", system.getName());

        // Undo: back to original name
        undoManager.undo();
        model.modelChanged();
        assertEquals("BookstoreSystem", system.getName());

        // Redo: apply empty name again
        undoManager.redo();
        model.modelChanged();
        assertEquals("", system.getName());
    }
}
