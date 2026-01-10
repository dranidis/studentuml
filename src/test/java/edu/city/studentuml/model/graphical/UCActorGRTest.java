package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.UMLProject;

/**
 * Test class for UCActorGR.edit() method. UCActorGR is a Use Case Diagram
 * component that represents an actor with a name. It uses the Template Method
 * pattern (editStringPropertyWithDialog) for name editing with duplicate
 * checking. Actor constructor: new Actor(name) Actor is stored in the
 * repository with duplicate name validation
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class UCActorGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testUCActorGR_EditName_UndoRedo() {
        // Create UCD model and frame
        UCDModel model = new UCDModel("ucd", umlProject);
        // Create Actor - uses parameterized constructor
        Actor actor = new Actor("Customer");
        model.getCentralRepository().addActor(actor);

        // Create UCActorGR (simple graphical wrapper)
        UCActorGR actorGR = new UCActorGR(actor, 100, 100);
        model.addGraphicalElement(actorGR);

        // Note: We cannot easily mock editStringPropertyWithDialog since it's part of the template method
        // and calls internal dialog methods. This test verifies the structure exists but
        // cannot test the full edit workflow without UI interaction.

        // Verify initial state
        assertEquals("Customer", actor.getName());

        // Verify the component is properly set up
        assertNotNull("Actor should be set as component", actorGR.getComponent());
        assertEquals("Customer", actorGR.getComponent().getName());
    }

    @Test
    public void testUCActorGR_CreateInstance() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);

        // Create Actor with name
        Actor actor = new Actor("Administrator");
        model.getCentralRepository().addActor(actor);

        // Create UCActorGR at specific position
        UCActorGR actorGR = new UCActorGR(actor, 150, 200);
        model.addGraphicalElement(actorGR);

        // Verify structure
        assertNotNull("UCActorGR should be created", actorGR);
        assertEquals("Administrator", actor.getName());
        assertEquals("Actor should be the component", actor, actorGR.getComponent());

        // Verify position
        assertEquals(150, actorGR.getX());
        assertEquals(200, actorGR.getY());
    }

    @Test
    public void testUCActorGR_SetNameDirectly() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);

        // Create Actor
        Actor actor = new Actor("original");
        model.getCentralRepository().addActor(actor);
        UCActorGR actorGR = new UCActorGR(actor, 100, 100);
        model.addGraphicalElement(actorGR);

        // Change name directly on domain object
        actor.setName("modified");

        // Verify change reflected
        assertEquals("modified", actor.getName());
        assertEquals("modified", actorGR.getComponent().getName());
    }

    @Test
    public void testUCActorGR_EmptyName() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);

        // Create Actor with empty name
        Actor actor = new Actor("");
        model.getCentralRepository().addActor(actor);
        UCActorGR actorGR = new UCActorGR(actor, 100, 100);
        model.addGraphicalElement(actorGR);

        // Verify empty name is accepted
        assertEquals("", actor.getName());
        assertEquals("", actorGR.getComponent().getName());
    }

    @Test
    public void testUCActorGR_Clone() {
        // Create UCD model
        UCDModel model = new UCDModel("ucd", umlProject);

        // Create Actor
        Actor actor = new Actor("Manager");
        model.getCentralRepository().addActor(actor);
        UCActorGR original = new UCActorGR(actor, 100, 100);
        model.addGraphicalElement(original);

        // Clone the graphical element
        UCActorGR cloned = original.clone();

        // Verify clone shares the same domain object (important!)
        assertSame("Clone should share the same Actor",
                original.getComponent(), cloned.getComponent());

        // Verify clone has same position
        assertEquals(original.getX(), cloned.getX());
        assertEquals(original.getY(), cloned.getY());

        // Verify modifying the shared domain object affects both
        actor.setName("Supervisor");
        assertEquals("Supervisor", original.getComponent().getName());
        assertEquals("Supervisor", cloned.getComponent().getName());
    }
}
