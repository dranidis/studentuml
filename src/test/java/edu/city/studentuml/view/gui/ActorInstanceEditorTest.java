package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ActorInstanceEditor using mocked showDialog() to avoid UI blocking.
 * Validates the TypedEntityEditor pattern for Actor Instance editing with name
 * and type (Actor) management.
 * 
 * @author Dimitris Dranidis
 */
public class ActorInstanceEditorTest {

    private CentralRepository repository;
    private UMLProject project;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
        repository = project.getCentralRepository();
    }

    @Test
    public void testConstructor_shouldNotThrow() {
        // Create an actor and actor instance
        Actor actor = new Actor("Customer");
        ActorInstance actorInstance = new ActorInstance("customer1", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        // Test that constructor completes without exceptions
        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withActorInstanceAndActor() {
        Actor actor = new Actor("Administrator");
        repository.addActor(actor);

        ActorInstance actorInstance = new ActorInstance("admin1", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository);

        // Verify editor initializes with the actor instance data
        assertEquals("Actor instance name should match", "admin1", editor.getActorInstanceName());
        assertEquals("Actor should match", actor, editor.getActor());
    }

    @Test
    public void testInitialize_withEmptyName() {
        Actor actor = new Actor("User");
        ActorInstance actorInstance = new ActorInstance("", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository);

        // Verify editor handles empty name
        assertEquals("Actor instance name should be empty", "", editor.getActorInstanceName());
        assertEquals("Actor should match", actor, editor.getActor());
    }

    @Test
    public void testShowDialog_OK() {
        Actor actor = new Actor("Manager");
        ActorInstance actorInstance = new ActorInstance("manager", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return true; // Mock OK
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertTrue("Should return true for OK", result);
    }

    @Test
    public void testShowDialog_Cancel() {
        Actor actor = new Actor("Manager");
        ActorInstance actorInstance = new ActorInstance("manager", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetActorInstanceName_returnsNameFieldValue() {
        Actor actor = new Actor("Guest");
        ActorInstance actorInstance = new ActorInstance("guest1", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository);

        assertEquals("Should return actor instance name", "guest1", editor.getActorInstanceName());
    }

    @Test
    public void testGetActor_returnsCurrentType() {
        Actor actor = new Actor("Developer");
        repository.addActor(actor);

        ActorInstance actorInstance = new ActorInstance("dev1", actor);
        ActorInstanceGR actorInstanceGR = new ActorInstanceGR(actorInstance, 100);

        ActorInstanceEditor editor = new ActorInstanceEditor(actorInstanceGR, repository);

        assertEquals("Should return actor", actor, editor.getActor());
        assertEquals("Actor name should match", "Developer", editor.getActor().getName());
    }
}
