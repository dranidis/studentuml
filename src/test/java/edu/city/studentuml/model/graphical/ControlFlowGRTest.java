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
 * Test suite for ControlFlowGR edit functionality with undo/redo support.
 */
public class ControlFlowGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testControlFlowGR_EditGuard_UndoRedo() {
        ADModel model = new ADModel("ad", umlProject);
        DiagramInternalFrame frame = new ADInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create source and target action nodes
        ActionNode sourceNode = new ActionNode("Source Action");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 50, 50);
        model.addGraphicalElement(sourceGR);

        ActionNode target1Node = new ActionNode("Target 1");
        ActionNodeGR target1GR = new ActionNodeGR(target1Node, 150, 150);
        model.addGraphicalElement(target1GR);

        ActionNode target2Node = new ActionNode("Target 2");
        ActionNodeGR target2GR = new ActionNodeGR(target2Node, 150, 250);
        model.addGraphicalElement(target2GR);

        // Create first control flow with a different guard "[y < 10]"
        ControlFlow otherFlow = new ControlFlow(sourceNode, target1Node);
        otherFlow.setGuard("[y < 10]");
        ControlFlowGR otherFlowGR = new ControlFlowGR(sourceGR, target1GR, otherFlow);
        model.addGraphicalElement(otherFlowGR);

        // Create second control flow that we'll edit
        ControlFlow controlFlow = new ControlFlow(sourceNode, target2Node);

        // Override factory method to inject mock dialog behavior
        ControlFlowGR flowGR = new ControlFlowGR(sourceGR, target2GR, controlFlow) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return "[x > 0]";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };

        model.addGraphicalElement(flowGR);

        // Verify initial state: one flow has guard "[y < 10]", other is empty
        assertEquals("[y < 10]", otherFlow.getGuard());
        assertEquals("Initial guard should be empty string", "", controlFlow.getGuard());

        // Edit: "" → "[x > 0]" (should succeed because it's different from "[y < 10]")
        EditContext context = new EditContext(model, frame);
        boolean editResult = flowGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("[x > 0]", controlFlow.getGuard());
        assertEquals("[y < 10]", otherFlow.getGuard()); // Other flow unchanged

        // Undo: "[x > 0]" → ""
        undoManager.undo();
        model.modelChanged();
        assertEquals("Guard should be empty string after undo", "", controlFlow.getGuard());
        assertEquals("[y < 10]", otherFlow.getGuard()); // Other flow unchanged

        // Redo: "" → "[x > 0]"
        undoManager.redo();
        model.modelChanged();
        assertEquals("[x > 0]", controlFlow.getGuard());
        assertEquals("[y < 10]", otherFlow.getGuard()); // Other flow unchanged
    }

    @Test
    public void testControlFlowGR_DuplicateGuardValidation_ShouldPreventEdit() {
        ADModel model = new ADModel("ad", umlProject);
        DiagramInternalFrame frame = new ADInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create source action node and two target nodes
        ActionNode sourceNode = new ActionNode("Source Action");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 50, 50);
        model.addGraphicalElement(sourceGR);

        ActionNode target1Node = new ActionNode("Target 1");
        ActionNodeGR target1GR = new ActionNodeGR(target1Node, 150, 150);
        model.addGraphicalElement(target1GR);

        ActionNode target2Node = new ActionNode("Target 2");
        ActionNodeGR target2GR = new ActionNodeGR(target2Node, 150, 250);
        model.addGraphicalElement(target2GR);

        // Create first control flow with guard "[x > 0]"
        ControlFlow flow1 = new ControlFlow(sourceNode, target1Node);
        flow1.setGuard("[x > 0]");
        ControlFlowGR flow1GR = new ControlFlowGR(sourceGR, target1GR, flow1);
        model.addGraphicalElement(flow1GR);

        // Create second control flow (initially no guard)
        ControlFlow flow2 = new ControlFlow(sourceNode, target2Node);
        ControlFlowGR flow2GR = new ControlFlowGR(sourceGR, target2GR, flow2) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        // Try to set the same guard as flow1
                        return "[x > 0]";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };
        model.addGraphicalElement(flow2GR);

        assertEquals("[x > 0]", flow1.getGuard());
        assertEquals("", flow2.getGuard());

        // Try to edit flow2 with duplicate guard - should be blocked
        EditContext context = new EditContext(model, frame);
        boolean editResult = false;
        try {
            editResult = flow2GR.edit(context);
        } catch (java.awt.HeadlessException e) {
            // Expected in headless test environment when error dialog is shown
            // The important thing is that the edit was blocked before mutation
        }

        // Edit should fail because of duplicate guard
        assertFalse("Edit should return false due to duplicate guard", editResult);
        assertFalse("UndoManager should NOT have edits", undoManager.canUndo());
        assertEquals("Guard should remain empty after failed edit", "", flow2.getGuard());
    }

    @Test
    public void testControlFlowGR_EmptyGuardAllowed_ShouldSucceed() throws Exception {
        // Setup: Create activity diagram with source node and two target nodes
        ADModel model = new ADModel("ad", umlProject);
        DiagramInternalFrame frame = new ADInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        ActionNode sourceNode = new ActionNode("Source");
        ActionNode target1 = new ActionNode("Target1");
        ActionNode target2 = new ActionNode("Target2");

        ControlFlow flow1 = new ControlFlow(sourceNode, target1);
        ControlFlow flow2 = new ControlFlow(sourceNode, target2);
        flow2.setGuard("nonEmpty"); // Start with non-empty guard

        // flow1 has empty guard (default), flow2 also has empty guard
        assertEquals("flow1 guard should be empty", "", flow1.getGuard());
        assertEquals("flow2 guard should not be be empty", "nonEmpty", flow2.getGuard());

        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR target1GR = new ActionNodeGR(target1, 200, 100);
        ActionNodeGR target2GR = new ActionNodeGR(target2, 200, 200);

        ControlFlowGR flow1GR = new ControlFlowGR(sourceGR, target1GR, flow1);
        ControlFlowGR flow2GR = new ControlFlowGR(sourceGR, target2GR, flow2) {
            @Override
            protected StringEditorDialog createStringDialog(
                    EditContext context, String dialogTitle,
                    String fieldLabel, String currentValue) {
                return new StringEditorDialog(context.getParentComponent(),
                        dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return ""; // Return empty guard
                    }

                    @Override
                    public boolean showDialog() {
                        return true; // User confirmed
                    }
                };
            }
        };

        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(target1GR);
        model.addGraphicalElement(target2GR);
        model.addGraphicalElement(flow1GR);
        model.addGraphicalElement(flow2GR);

        EditContext context = new EditContext(model, frame);

        // Edit flow2 to have empty guard (same as flow1)
        // This should SUCCEED because empty guards are allowed to be duplicated
        boolean editResult = flow2GR.edit(context);

        // Edit should succeed - empty guards can be duplicated
        assertTrue("Edit should return true for empty guard", editResult);
        // No undo edit is created because the value didn't actually change (empty -> empty)
        assertTrue("UndoManager should have edits since value did change", undoManager.canUndo());
        assertEquals("Guard should be empty after edit", "", flow2.getGuard());
    }
}
