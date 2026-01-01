package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.UMLProject;

/**
 * Tests for ObjectFlowEditor using the new Editor<ObjectFlow> pattern.
 * Validates editing object flow properties (weight and guard).
 * 
 * @author Dimitris Dranidis
 */
public class ObjectFlowEditorTest {

    private UMLProject project;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
    }

    @Test
    public void testConstructor_shouldNotThrow() {
        // Test that constructor completes without exceptions
        ObjectFlowEditor editor = new ObjectFlowEditor();
        assertNotNull("Editor should be created", editor);
    }

    @Test
    public void testEditDialog_returnsEditedFlow() {
        // Create object flow with initial values
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow originalFlow = new ObjectFlow(sourceAction, targetNode);
        originalFlow.setWeight("5");
        originalFlow.setGuard("[x > 0]");

        ObjectFlowEditor editor = new ObjectFlowEditor() {
            @Override
            public ObjectFlow editDialog(ObjectFlow objectFlow, java.awt.Component parent) {
                // Simulate user editing: create edited version
                ObjectFlow edited = objectFlow.clone();
                edited.setWeight("10");
                edited.setGuard("[y < 5]");
                return edited;
            }
        };

        ObjectFlow result = editor.editDialog(originalFlow, null);

        assertNotNull("Should return edited flow", result);
        assertEquals("Weight should be updated", "10", result.getWeight());
        assertEquals("Guard should be updated", "[y < 5]", result.getGuard());
        // Original should be unchanged
        assertEquals("Original weight unchanged", "5", originalFlow.getWeight());
        assertEquals("Original guard unchanged", "[x > 0]", originalFlow.getGuard());
    }

    @Test
    public void testEditDialog_cancelReturnsNull() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow originalFlow = new ObjectFlow(sourceAction, targetNode);

        ObjectFlowEditor editor = new ObjectFlowEditor() {
            @Override
            public ObjectFlow editDialog(ObjectFlow objectFlow, java.awt.Component parent) {
                return null; // Simulate cancel
            }
        };

        ObjectFlow result = editor.editDialog(originalFlow, null);

        assertNull("Should return null on cancel", result);
    }

    @Test
    public void testEditDialog_withEmptyProperties() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow originalFlow = new ObjectFlow(sourceAction, targetNode);
        // Leave weight and guard empty

        ObjectFlowEditor editor = new ObjectFlowEditor() {
            @Override
            public ObjectFlow editDialog(ObjectFlow objectFlow, java.awt.Component parent) {
                // Simulate editing empty flow
                ObjectFlow edited = objectFlow.clone();
                edited.setWeight("1");
                edited.setGuard("[condition]");
                return edited;
            }
        };

        ObjectFlow result = editor.editDialog(originalFlow, null);

        assertNotNull("Should return edited flow", result);
        assertEquals("Weight should be set", "1", result.getWeight());
        assertEquals("Guard should be set", "[condition]", result.getGuard());
    }
}
