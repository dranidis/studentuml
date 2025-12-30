package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;

/**
 * Tests for ObjectFlowEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and the editor's ability to manage
 * object flow properties (weight and guard).
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
        // Create source and target nodes
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");

        // Create object flow
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);
        flow.setWeight("1");
        flow.setGuard("[valid]");

        // Create graphical elements
        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        // Test that constructor completes without exceptions
        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withWeightAndGuard() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);
        flow.setWeight("5");
        flow.setGuard("[x > 0]");

        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR);

        // Verify editor initializes with the flow properties
        assertEquals("Weight should match", "5", editor.getWeight());
        assertEquals("Guard should match", "[x > 0]", editor.getGuard());
    }

    @Test
    public void testInitialize_withEmptyProperties() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);

        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR);

        // Verify editor initializes with empty properties
        assertEquals("Weight should be empty", "", editor.getWeight());
        assertEquals("Guard should be empty", "", editor.getGuard());
    }

    @Test
    public void testShowDialog_OK() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);

        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return true;
            }
        };

        boolean result = editor.showDialog();

        assertTrue("Should return true for OK", result);
    }

    @Test
    public void testShowDialog_Cancel() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);

        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return false;
            }
        };

        boolean result = editor.showDialog();

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testMultipleInitializeCalls_shouldBeIdempotent() {
        ActionNode sourceAction = new ActionNode("SourceAction");
        ObjectNode targetNode = new ObjectNode();
        targetNode.setName("TargetNode");
        ObjectFlow flow = new ObjectFlow(sourceAction, targetNode);
        flow.setWeight("3");
        flow.setGuard("[test]");

        ActionNodeGR sourceGR = new ActionNodeGR(sourceAction, 100, 100);
        ObjectNodeGR targetGR = new ObjectNodeGR(targetNode, 200, 200);
        ObjectFlowGR flowGR = new ObjectFlowGR(sourceGR, targetGR, flow);

        ObjectFlowEditor editor = new ObjectFlowEditor(null, flowGR) {
            @Override
            public boolean showDialog() {
                // Call initializeIfNeeded multiple times
                initializeIfNeeded();
                initializeIfNeeded();
                initializeIfNeeded();
                return true;
            }
        };

        // Should handle multiple initialization calls gracefully
        boolean result = editor.showDialog();

        assertTrue("Should still work after multiple init calls", result);
        assertEquals("Weight should still be correct", "3", editor.getWeight());
        assertEquals("Guard should still be correct", "[test]", editor.getGuard());
    }
}
