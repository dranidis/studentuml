package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.StringEditorDialog;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;

import static org.junit.Assert.*;

/**
 * Test suite for DecisionNodeGR edit functionality with undo/redo support.
 */
public class DecisionNodeGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testDecisionNodeGR_EditName_UndoRedo() {
        ADModel model = new ADModel("ad", umlProject);
        DiagramInternalFrame frame = new ADInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create a decision node with default name
        DecisionNode decisionNode = new DecisionNode();
        assertEquals("Decision", decisionNode.getName()); // Default name

        // Override factory method to inject mock dialog behavior
        DecisionNodeGR decisionGR = new DecisionNodeGR(decisionNode, 100, 100) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return "Check Login Status";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };

        model.addGraphicalElement(decisionGR);
        assertEquals("Initial name should be 'Decision'", "Decision", decisionNode.getName());

        // Edit: "Decision" → "Check Login Status"
        EditContext context = new EditContext(model, frame);
        boolean editResult = decisionGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("Check Login Status", decisionNode.getName());

        // Undo: "Check Login Status" → "Decision"
        undoManager.undo();
        model.modelChanged();
        assertEquals("Name should be 'Decision' after undo", "Decision", decisionNode.getName());

        // Redo: "Decision" → "Check Login Status"
        undoManager.redo();
        model.modelChanged();
        assertEquals("Check Login Status", decisionNode.getName());
    }
}
