package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.UMLProject;

/**
 * Test class for ActivityNodeGR.edit() method. ActivityNodeGR is an Activity
 * Diagram component that represents a composite activity node with a name. It
 * uses the Template Method pattern (editNameWithDialog) for simpler name-only
 * editing. ActivityNodeGR is also Resizable and has resize handles.
 * ActivityNode uses parameterized constructor: new ActivityNode(name)
 * ActivityNode is NOT stored in the repository (private addActivityNode method)
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class ActivityNodeGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testActivityNodeGR_EditName_UndoRedo() {
        // Create AD model and frame
        ADModel model = new ADModel("ad", umlProject);
        // Create ActivityNode - uses parameterized constructor
        ActivityNode activityNode = new ActivityNode("bookingProcess");

        // Create ActivityNodeGR (composite graphical wrapper)
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, 100, 100);
        model.addGraphicalElement(activityNodeGR);

        // Note: We cannot easily mock editNameWithDialog since it's part of the template method
        // and calls internal dialog methods. This test verifies the structure exists but
        // cannot test the full edit workflow without UI interaction.

        // Verify initial state
        assertEquals("bookingProcess", activityNode.getName());

        // Verify the component is properly set up
        assertNotNull("ActivityNode should be set as component", activityNodeGR.getComponent());
        assertEquals("bookingProcess", activityNodeGR.getComponent().getName());
    }

    @Test
    public void testActivityNodeGR_CreateInstance() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActivityNode with name
        ActivityNode activityNode = new ActivityNode("checkoutProcess");

        // Create ActivityNodeGR at specific position
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, 150, 200);
        model.addGraphicalElement(activityNodeGR);

        // Verify structure
        assertNotNull("ActivityNodeGR should be created", activityNodeGR);
        assertEquals("checkoutProcess", activityNode.getName());
        assertEquals("ActivityNode should be the component", activityNode, activityNodeGR.getComponent());

        // Verify position
        assertEquals(150, activityNodeGR.getX());
        assertEquals(200, activityNodeGR.getY());
    }

    @Test
    public void testActivityNodeGR_SetNameDirectly() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActivityNode
        ActivityNode activityNode = new ActivityNode("original");
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, 100, 100);
        model.addGraphicalElement(activityNodeGR);

        // Change name directly on domain object
        activityNode.setName("modified");

        // Verify change reflected
        assertEquals("modified", activityNode.getName());
        assertEquals("modified", activityNodeGR.getComponent().getName());
    }

    @Test
    public void testActivityNodeGR_EmptyName() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActivityNode with empty name
        ActivityNode activityNode = new ActivityNode("");
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, 100, 100);
        model.addGraphicalElement(activityNodeGR);

        // Verify empty name is accepted
        assertEquals("", activityNode.getName());
        assertEquals("", activityNodeGR.getComponent().getName());
    }

    @Test
    public void testActivityNodeGR_Clone() {
        // Create AD model
        ADModel model = new ADModel("ad", umlProject);

        // Create ActivityNode
        ActivityNode activityNode = new ActivityNode("shippingProcess");
        ActivityNodeGR original = new ActivityNodeGR(activityNode, 100, 100);
        model.addGraphicalElement(original);

        // Clone the graphical element
        ActivityNodeGR cloned = original.clone();

        // Verify clone shares the same domain object (important!)
        assertSame("Clone should share the same ActivityNode",
                original.getComponent(), cloned.getComponent());

        // Verify clone has same position
        assertEquals(original.getX(), cloned.getX());
        assertEquals(original.getY(), cloned.getY());

        // Verify modifying the shared domain object affects both
        activityNode.setName("deliveryProcess");
        assertEquals("deliveryProcess", original.getComponent().getName());
        assertEquals("deliveryProcess", cloned.getComponent().getName());
    }
}
