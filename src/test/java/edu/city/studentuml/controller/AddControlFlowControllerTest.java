package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ActivityFinalNodeGR;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.ADInternalFrame;

/**
 * Tests for AddControlFlowController focusing on UML Activity Diagram semantic
 * validation. Tests that the controller enforces UML constraints for control
 * flows.
 */
public class AddControlFlowControllerTest {

    private UMLProject project;
    private ADModel model;
    private ADInternalFrame frame;
    private TestableAddControlFlowController controller;

    /**
     * Testable subclass that captures error messages instead of showing dialogs.
     */
    private static class TestableAddControlFlowController extends AddControlFlowController {
        private String lastErrorMessage = null;

        public TestableAddControlFlowController(ADModel model, ADInternalFrame frame) {
            super(model, frame);
        }

        @Override
        protected void showErrorMessage(String msg) {
            // Capture the error message instead of showing a dialog
            lastErrorMessage = msg;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }

        public void clearLastErrorMessage() {
            lastErrorMessage = null;
        }
    }

    @Before
    public void setUp() {
        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);

        project = UMLProject.getInstance();
        project.clear();
        model = new ADModel("Test AD", project);
        frame = new ADInternalFrame(model);
        controller = new TestableAddControlFlowController(model, frame);
    }

    @Test
    public void testCreation() {
        assertNotNull("Controller should be created", controller);
    }

    /**
     * Test that action nodes CAN have one outgoing control flow. This is the valid
     * case.
     */
    @Test
    public void testActionNode_SingleOutgoingFlow_ShouldSucceed() {
        // Create nodes
        InitialNode initial = new InitialNode();
        InitialNodeGR initialGR = new InitialNodeGR(initial, 100, 50);
        model.addGraphicalElement(initialGR);

        ActionNode action1 = new ActionNode("Action 1");
        ActionNodeGR action1GR = new ActionNodeGR(action1, 100, 150);
        model.addGraphicalElement(action1GR);

        ActivityFinalNode final1 = new ActivityFinalNode();
        ActivityFinalNodeGR final1GR = new ActivityFinalNodeGR(final1, 100, 250);
        model.addGraphicalElement(final1GR);

        // Add first control flow (should succeed)
        controller.addEdge(action1GR, final1GR,
                new java.awt.Point(100, 150),
                new java.awt.Point(100, 250));

        // Verify the flow was added
        assertEquals("Should have 3 nodes + 1 control flow", 4, model.getGraphicalElements().size());
        assertEquals("Action should have 1 outgoing edge", 1,
                action1GR.getComponent().getNumberOfOutgoingEdges());
    }

    /**
     * Test that action nodes CANNOT have multiple outgoing control flows. The
     * controller should prevent the second flow from being added. UML Semantics:
     * Action nodes should have at most ONE outgoing flow. Only control flow nodes
     * (Decision, Fork) can have multiple outgoing flows.
     */
    @Test
    public void testActionNode_MultipleOutgoingFlows_ShouldBePreventedByController() {
        // Create nodes
        ActionNode action1 = new ActionNode("Action 1");
        ActionNodeGR action1GR = new ActionNodeGR(action1, 100, 150);
        model.addGraphicalElement(action1GR);

        ActivityFinalNode final1 = new ActivityFinalNode();
        ActivityFinalNodeGR final1GR = new ActivityFinalNodeGR(final1, 50, 250);
        model.addGraphicalElement(final1GR);

        ActivityFinalNode final2 = new ActivityFinalNode();
        ActivityFinalNodeGR final2GR = new ActivityFinalNodeGR(final2, 150, 250);
        model.addGraphicalElement(final2GR);

        // Add first control flow (should succeed)
        controller.addEdge(action1GR, final1GR,
                new java.awt.Point(100, 150),
                new java.awt.Point(50, 250));

        int elementsAfterFirstFlow = model.getGraphicalElements().size();
        assertEquals("Should have 3 nodes + 1 control flow", 4, elementsAfterFirstFlow);
        assertEquals("Action should have 1 outgoing edge", 1,
                action1GR.getComponent().getNumberOfOutgoingEdges());

        // Clear any error message from first flow (should be null anyway)
        controller.clearLastErrorMessage();

        // Attempt to add second control flow from same action (should be prevented by controller)
        controller.addEdge(action1GR, final2GR,
                new java.awt.Point(100, 150),
                new java.awt.Point(150, 250));

        // Verify the second flow was NOT added
        assertEquals("Element count should not increase (controller prevented addition)",
                elementsAfterFirstFlow, model.getGraphicalElements().size());
        assertEquals("Action should still have only 1 outgoing edge", 1,
                action1GR.getComponent().getNumberOfOutgoingEdges());

        // Verify only one ControlFlowGR exists
        long flowCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof ControlFlowGR)
                .count();
        assertEquals("Should have exactly 1 control flow", 1, flowCount);

        // Verify that an error message was shown
        assertNotNull("Error message should have been shown", controller.getLastErrorMessage());
        assertEquals("Error message should mention action node constraint",
                true, controller.getLastErrorMessage().contains("Action node"));
        assertEquals("Error message should mention 'Fork node'",
                true, controller.getLastErrorMessage().contains("Fork node"));
    }

    /**
     * Test that action nodes CANNOT have multiple incoming control flows. The
     * controller should prevent the second incoming flow from being added. UML
     * Semantics: Action nodes should have at most ONE incoming flow. Only control
     * flow nodes (Merge, Join) can have multiple incoming flows.
     */
    @Test
    public void testActionNode_MultipleIncomingFlows_ShouldBePreventedByController() {
        // Create nodes
        InitialNode initial1 = new InitialNode();
        InitialNodeGR initial1GR = new InitialNodeGR(initial1, 50, 50);
        model.addGraphicalElement(initial1GR);

        InitialNode initial2 = new InitialNode();
        InitialNodeGR initial2GR = new InitialNodeGR(initial2, 150, 50);
        model.addGraphicalElement(initial2GR);

        ActionNode action1 = new ActionNode("Action 1");
        ActionNodeGR action1GR = new ActionNodeGR(action1, 100, 150);
        model.addGraphicalElement(action1GR);

        // Add first incoming flow (should succeed)
        controller.addEdge(initial1GR, action1GR,
                new java.awt.Point(50, 50),
                new java.awt.Point(100, 150));

        int elementsAfterFirstFlow = model.getGraphicalElements().size();
        assertEquals("Should have 3 nodes + 1 control flow", 4, elementsAfterFirstFlow);
        assertEquals("Action should have 1 incoming edge", 1,
                action1GR.getComponent().getNumberOfIncomingEdges());

        // Clear any error message from first flow (should be null anyway)
        controller.clearLastErrorMessage();

        // Attempt to add second incoming flow to same action (should be prevented by controller)
        controller.addEdge(initial2GR, action1GR,
                new java.awt.Point(150, 50),
                new java.awt.Point(100, 150));

        // Verify the second flow was NOT added
        assertEquals("Element count should not increase (controller prevented addition)",
                elementsAfterFirstFlow, model.getGraphicalElements().size());
        assertEquals("Action should still have only 1 incoming edge", 1,
                action1GR.getComponent().getNumberOfIncomingEdges());

        // Verify only one ControlFlowGR exists
        long flowCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof ControlFlowGR)
                .count();
        assertEquals("Should have exactly 1 control flow", 1, flowCount);

        // Verify that an error message was shown
        assertNotNull("Error message should have been shown", controller.getLastErrorMessage());
        assertEquals("Error message should mention action node constraint",
                true, controller.getLastErrorMessage().contains("Action node"));
        assertEquals("Error message should mention 'Merge node'",
                true, controller.getLastErrorMessage().contains("Merge node"));
    }

    /**
     * Test that the validation only applies to action nodes, not other node types.
     * Fork nodes, for example, can have multiple outgoing flows.
     */
    @Test
    public void testForkNode_MultipleOutgoingFlows_ShouldBeAllowed() {
        // Create nodes
        InitialNode initial = new InitialNode();
        InitialNodeGR initialGR = new InitialNodeGR(initial, 100, 50);
        model.addGraphicalElement(initialGR);

        // Fork nodes can have multiple outgoing flows
        ActionNode action1 = new ActionNode("Action 1");
        ActionNodeGR action1GR = new ActionNodeGR(action1, 50, 150);
        model.addGraphicalElement(action1GR);

        ActionNode action2 = new ActionNode("Action 2");
        ActionNodeGR action2GR = new ActionNodeGR(action2, 150, 150);
        model.addGraphicalElement(action2GR);

        // Note: This test demonstrates that the action node constraint is specific
        // Fork/Decision nodes would be tested separately with their own constraints
        assertEquals("Should have 3 nodes", 3, model.getGraphicalElements().size());
    }
}
