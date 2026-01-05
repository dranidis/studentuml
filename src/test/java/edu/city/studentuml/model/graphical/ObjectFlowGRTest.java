package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.ObjectFlowEditor;

import javax.swing.undo.UndoManager;

/**
 * Test class for ObjectFlowGR.edit() method. ObjectFlowGR is an Activity
 * Diagram component that represents an object flow (edge) between nodes. It has
 * guard and weight properties that can be edited. ObjectFlow constructor: new
 * ObjectFlow(source, target) ObjectFlow has setGuard() and setWeight() methods
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class ObjectFlowGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testObjectFlowGR_EditGuardAndWeight_UndoRedo() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create two ActionNodes for source and target
        ActionNode sourceNode = new ActionNode("processPayment");
        ActionNode targetNode = new ActionNode("shipOrder");

        // Create graphical wrappers
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 300, 100);
        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(targetGR);

        // Create ObjectFlow
        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        objectFlow.setGuard("[paymentApproved]");
        objectFlow.setWeight("1");

        // Create ObjectFlowGR with mock editor
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow) {
            @Override
            protected ObjectFlowEditor createEditor(EditContext context) {
                return new ObjectFlowEditor() {
                    @Override
                    public ObjectFlow editDialog(ObjectFlow original, Component parent) {
                        // Return edited ObjectFlow with new guard and weight
                        ObjectFlow edited = new ObjectFlow(original.getSource(), original.getTarget());
                        edited.setGuard("[orderValid]");
                        edited.setWeight("2");
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(objectFlowGR);

        // Verify initial state
        assertEquals("[paymentApproved]", objectFlow.getGuard());
        assertEquals("1", objectFlow.getWeight());

        // Edit the object flow
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectFlowGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("[orderValid]", objectFlow.getGuard());
        assertEquals("2", objectFlow.getWeight());

        // Verify undo manager has the edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert changes
        undoManager.undo();
        model.modelChanged();
        assertEquals("[paymentApproved]", objectFlow.getGuard());
        assertEquals("1", objectFlow.getWeight());

        // Redo should reapply changes
        assertTrue("Undo manager should have redo", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals("[orderValid]", objectFlow.getGuard());
        assertEquals("2", objectFlow.getWeight());
    }

    @Test
    public void testObjectFlowGR_EditCancel() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create nodes and flow
        ActionNode sourceNode = new ActionNode("validate");
        ActionNode targetNode = new ActionNode("process");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 300, 100);
        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(targetGR);

        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        objectFlow.setGuard("[valid]");
        objectFlow.setWeight("1");

        // Create ObjectFlowGR with mock editor that returns null (cancel)
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow) {
            @Override
            protected ObjectFlowEditor createEditor(EditContext context) {
                return new ObjectFlowEditor() {
                    @Override
                    public ObjectFlow editDialog(ObjectFlow original, Component parent) {
                        return null; // User cancelled
                    }
                };
            }
        };

        model.addGraphicalElement(objectFlowGR);

        // Edit should be cancelled
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectFlowGR.edit(context);

        assertFalse("Edit should be cancelled", editResult);
        assertEquals("[valid]", objectFlow.getGuard());
        assertEquals("1", objectFlow.getWeight());

        // Verify no undo edit was created
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testObjectFlowGR_EditEmptyGuardAndWeight() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create nodes and flow
        ActionNode sourceNode = new ActionNode("start");
        ActionNode targetNode = new ActionNode("end");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 300, 100);
        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(targetGR);

        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        objectFlow.setGuard("[condition]");
        objectFlow.setWeight("5");

        // Create ObjectFlowGR with mock editor that returns empty values
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow) {
            @Override
            protected ObjectFlowEditor createEditor(EditContext context) {
                return new ObjectFlowEditor() {
                    @Override
                    public ObjectFlow editDialog(ObjectFlow original, Component parent) {
                        // Return ObjectFlow with empty guard and weight
                        ObjectFlow edited = new ObjectFlow(original.getSource(), original.getTarget());
                        edited.setGuard("");
                        edited.setWeight("");
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(objectFlowGR);

        // Edit with empty values
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectFlowGR.edit(context);

        assertTrue("Edit should succeed with empty values", editResult);
        assertEquals("", objectFlow.getGuard());
        assertEquals("", objectFlow.getWeight());
    }

    @Test
    public void testObjectFlowGR_EditGuardOnly() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create nodes and flow
        ActionNode sourceNode = new ActionNode("check");
        ActionNode targetNode = new ActionNode("proceed");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 300, 100);
        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(targetGR);

        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        objectFlow.setGuard("[old]");
        objectFlow.setWeight("");

        // Create ObjectFlowGR with mock editor that changes only guard
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow) {
            @Override
            protected ObjectFlowEditor createEditor(EditContext context) {
                return new ObjectFlowEditor() {
                    @Override
                    public ObjectFlow editDialog(ObjectFlow original, Component parent) {
                        ObjectFlow edited = new ObjectFlow(original.getSource(), original.getTarget());
                        edited.setGuard("[new]");
                        edited.setWeight(original.getWeight());
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(objectFlowGR);

        // Edit guard only
        EditContext context = new EditContext(model, frame);
        boolean editResult = objectFlowGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("[new]", objectFlow.getGuard());
        assertEquals("", objectFlow.getWeight());
    }

    @Test
    public void testObjectFlowGR_CreateEditorMethodExists() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        ADInternalFrame frame = new ADInternalFrame(model);

        // Create nodes and flow
        ActionNode sourceNode = new ActionNode("source");
        ActionNode targetNode = new ActionNode("target");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 300, 100);
        model.addGraphicalElement(sourceGR);
        model.addGraphicalElement(targetGR);

        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        ObjectFlowEditor editor = objectFlowGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}
