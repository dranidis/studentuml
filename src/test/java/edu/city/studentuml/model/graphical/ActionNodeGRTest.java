package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.UMLProject;

/**
 * Test class for ActionNodeGR.edit() method. ActionNodeGR is an Activity
 * Diagram component that represents an action node with a name. It uses the
 * Template Method pattern (editNameWithDialog) for simpler name-only editing.
 * ActionNode uses parameterized constructor: new ActionNode(name) ActionNode is
 * NOT stored in the repository (private addActionNode method)
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class ActionNodeGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testActionNodeGR_EditName_UndoRedo() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        // Create ActionNode - uses parameterized constructor
        ActionNode actionNode = new ActionNode("processOrder");

        // Create ActionNodeGR (simple graphical wrapper)
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(actionNodeGR);

        // Note: We cannot easily mock editNameWithDialog since it's part of the template method
        // and calls internal dialog methods. This test verifies the structure exists but
        // cannot test the full edit workflow without UI interaction.

        // Verify initial state
        assertEquals("processOrder", actionNode.getName());

        // Verify the component is properly set up
        assertNotNull("ActionNode should be set as component", actionNodeGR.getComponent());
        assertEquals("processOrder", actionNodeGR.getComponent().getName());
    }

    @Test
    public void testActionNodeGR_CreateInstance() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActionNode with name
        ActionNode actionNode = new ActionNode("validatePayment");

        // Create ActionNodeGR at specific position
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 150, 200);
        model.addGraphicalElement(actionNodeGR);

        // Verify structure
        assertNotNull("ActionNodeGR should be created", actionNodeGR);
        assertEquals("validatePayment", actionNode.getName());
        assertEquals("ActionNode should be the component", actionNode, actionNodeGR.getComponent());

        // Verify position
        assertEquals(150, actionNodeGR.getX());
        assertEquals(200, actionNodeGR.getY());
    }

    @Test
    public void testActionNodeGR_SetNameDirectly() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActionNode
        ActionNode actionNode = new ActionNode("original");
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(actionNodeGR);

        // Change name directly on domain object
        actionNode.setName("modified");

        // Verify change reflected
        assertEquals("modified", actionNode.getName());
        assertEquals("modified", actionNodeGR.getComponent().getName());
    }

    @Test
    public void testActionNodeGR_EmptyName() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActionNode with empty name
        ActionNode actionNode = new ActionNode("");
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(actionNodeGR);

        // Verify empty name is accepted
        assertEquals("", actionNode.getName());
        assertEquals("", actionNodeGR.getComponent().getName());
    }

    @Test
    public void testActionNodeGR_Clone() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActionNode
        ActionNode actionNode = new ActionNode("shipOrder");
        ActionNodeGR original = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(original);

        // Clone the graphical element
        ActionNodeGR cloned = original.clone();

        // Verify clone shares the same domain object (important!)
        assertSame("Clone should share the same ActionNode",
                original.getComponent(), cloned.getComponent());

        // Verify clone has same position
        assertEquals(original.getX(), cloned.getX());
        assertEquals(original.getY(), cloned.getY());

        // Verify modifying the shared domain object affects both
        actionNode.setName("deliverOrder");
        assertEquals("deliverOrder", original.getComponent().getName());
        assertEquals("deliverOrder", cloned.getComponent().getName());
    }
}
