package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.view.gui.StringEditorDialog;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;

import static org.junit.Assert.*;

/**
 * Test suite for UseCaseGR edit functionality with undo/redo support.
 */
public class UseCaseGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testUseCaseGR_EditName_UndoRedo() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create use case
        UseCase useCase = new UseCase("Login");
        model.getCentralRepository().addUseCase(useCase);
        UseCaseGR useCaseGR = new UseCaseGR(useCase, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return "Authenticate";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(useCaseGR);

        assertEquals("Login", useCase.getName());

        // Edit: change name
        EditContext context = new EditContext(model, frame);
        boolean editResult = useCaseGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("Authenticate", useCase.getName());

        // Undo: back to original name
        undoManager.undo();
        model.modelChanged();
        assertEquals("Login", useCase.getName());

        // Redo: apply new name again
        undoManager.redo();
        model.modelChanged();
        assertEquals("Authenticate", useCase.getName());
    }

    @Test
    public void testUseCaseGR_DuplicateNameValidation_ShouldPreventEdit() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create two use cases
        UseCase useCase1 = new UseCase("Login");
        UseCase useCase2 = new UseCase("Register");
        model.getCentralRepository().addUseCase(useCase1);
        model.getCentralRepository().addUseCase(useCase2);

        UseCaseGR useCaseGR1 = new UseCaseGR(useCase1, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        // Try to set name to existing use case name
                        return "Register";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(useCaseGR1);

        assertEquals("Login", useCase1.getName());
        assertEquals("Register", useCase2.getName());

        // Edit: attempt to change to duplicate name
        EditContext context = new EditContext(model, frame);
        boolean editResult = false;
        try {
            editResult = useCaseGR1.edit(context);
        } catch (java.awt.HeadlessException e) {
            // Expected in headless test environment when error dialog is shown
            // The important thing is that the edit was blocked before mutation
        }

        // Edit should be prevented due to duplicate
        assertFalse("Edit should return false for duplicate name", editResult);
        assertFalse("UndoManager should not have edits", undoManager.canUndo());
        assertEquals("Login", useCase1.getName()); // Name should remain unchanged
    }

    @Test
    public void testUseCaseGR_EmptyNameAllowed_ShouldSucceed() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);
        UCDInternalFrame frame = new UCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create use case
        UseCase useCase = new UseCase("Login");
        model.getCentralRepository().addUseCase(useCase);
        UseCaseGR useCaseGR = new UseCaseGR(useCase, 100, 100) {
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
        model.addGraphicalElement(useCaseGR);

        assertEquals("Login", useCase.getName());

        // Edit: change to empty name
        EditContext context = new EditContext(model, frame);
        boolean editResult = useCaseGR.edit(context);

        assertTrue("Edit should return true for empty name", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("", useCase.getName());

        // Undo: back to original name
        undoManager.undo();
        model.modelChanged();
        assertEquals("Login", useCase.getName());

        // Redo: apply empty name again
        undoManager.redo();
        model.modelChanged();
        assertEquals("", useCase.getName());
    }
}
