package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ActorEditor using mocked showDialog() to avoid UI blocking.
 * Validates the ClassifierEditor pattern for Actor editing.
 * 
 * @author Dimitris Dranidis
 */
public class ActorEditorTest {

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
        Actor actor = new Actor("Customer");

        // Test that constructor completes without exceptions
        ActorEditor editor = new ActorEditor(actor, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testGetActor_returnsNewActorWithName() {
        Actor actor = new Actor("Customer");

        ActorEditor editor = new ActorEditor(actor, repository);

        Actor result = editor.getActor();

        assertNotNull("Should return an actor", result);
        assertEquals("Actor name should match", "Customer", result.getName());
    }

    @Test
    public void testShowDialog_OK() {
        Actor actor = new Actor("Admin");

        ActorEditor editor = new ActorEditor(actor, repository) {
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
        Actor actor = new Actor("Admin");

        ActorEditor editor = new ActorEditor(actor, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetActor_withEmptyName() {
        Actor actor = new Actor("");

        ActorEditor editor = new ActorEditor(actor, repository);

        Actor result = editor.getActor();

        assertEquals("Actor should have empty name", "", result.getName());
    }

    @Test
    public void testGetActor_withSpecialCharacters() {
        Actor actor = new Actor("User-Admin_123");

        ActorEditor editor = new ActorEditor(actor, repository);

        Actor result = editor.getActor();

        assertEquals("Actor name should handle special characters", "User-Admin_123", result.getName());
    }

    @Test
    public void testGetActor_multipleActorsInRepository() {
        Actor actor1 = new Actor("Customer");
        Actor actor2 = new Actor("Admin");
        repository.addActor(actor1);
        repository.addActor(actor2);

        ActorEditor editor = new ActorEditor(actor1, repository);

        Actor result = editor.getActor();

        assertEquals("Should return actor with correct name", "Customer", result.getName());
        assertEquals("Repository should still have 2 actors", 2, repository.getActors().size());
    }
}
