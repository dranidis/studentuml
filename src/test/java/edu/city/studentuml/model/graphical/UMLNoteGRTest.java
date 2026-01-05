package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for UMLNoteGR edit functionality with undo/redo support.
 */
public class UMLNoteGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testUMLNoteGR_EditText_UndoRedo() {
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);
        UndoManager undoManager = frame.getUndoManager();

        UMLNoteGR noteGR = new UMLNoteGR("Original text", null, new Point(50, 50)) {
            @Override
            protected UMLNoteEditor createEditor(EditContext context) {
                return new UMLNoteEditor(context.getParentComponent(), "Editor", this) {
                    @Override
                    public boolean showDialog() {
                        return true;
                    }

                    @Override
                    public String getText() {
                        return "Modified text";
                    }
                };
            }
        };

        model.addGraphicalElement(noteGR);
        assertEquals("Original text", noteGR.getText());

        EditContext context = new EditContext(model, frame);
        noteGR.edit(context);
        assertEquals("Modified text", noteGR.getText());

        undoManager.undo();
        model.modelChanged();
        assertEquals("Original text", noteGR.getText());

        undoManager.redo();
        model.modelChanged();
        assertEquals("Modified text", noteGR.getText());
    }
}
