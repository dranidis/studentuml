package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ObjectNodeEditor using mocked showDialog() to avoid UI blocking.
 * Validates the TypedEntityEditor pattern for Activity Diagram Object Node
 * editing with name, type (DesignClass), and states management.
 * 
 * @author Dimitris Dranidis
 */
public class ObjectNodeEditorTest {

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
        // Create an object node
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("testNode");
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        // Test that constructor completes without exceptions
        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withObjectNodeAndType() {
        DesignClass dc = new DesignClass("Order");
        repository.addClass(dc);

        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("order1");
        objectNode.setType(dc);
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        // Verify editor initializes with the object node data
        assertEquals("Object name should match", "order1", editor.getObjectName());
        assertEquals("Design class should match", dc, editor.getDesignClass());
    }

    @Test
    public void testInitialize_withStates() {
        DesignClass dc = new DesignClass("Document");
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("doc");
        objectNode.setType(dc);

        // Add states to the object node
        State state1 = new State("draft");
        State state2 = new State("published");
        objectNode.addState(state1);
        objectNode.addState(state2);

        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        // Verify editor initializes with states
        Vector<State> states = editor.getStates();
        assertNotNull("States should not be null", states);
        assertEquals("Should have 2 states", 2, states.size());
        assertEquals("First state should match", "draft", states.get(0).getName());
        assertEquals("Second state should match", "published", states.get(1).getName());
    }

    @Test
    public void testInitialize_withEmptyStates() {
        DesignClass dc = new DesignClass("Product");
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("product");
        objectNode.setType(dc);
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        // Verify editor handles empty states list
        Vector<State> states = editor.getStates();
        assertNotNull("States should not be null", states);
        assertEquals("Should have no states", 0, states.size());
    }

    @Test
    public void testInitialize_withNullType() {
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("node");
        objectNode.setType(null);
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        // Verify editor handles null type
        assertEquals("Object name should match", "node", editor.getObjectName());
        assertNull("Design class should be null", editor.getDesignClass());
    }

    @Test
    public void testShowDialog_OK() {
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("test");
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository) {
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
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("test");
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetObjectName_returnsNameFieldValue() {
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("myNode");
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        assertEquals("Should return object name", "myNode", editor.getObjectName());
    }

    @Test
    public void testGetType_returnsDesignClass() {
        DesignClass dc = new DesignClass("Invoice");
        repository.addClass(dc);

        ObjectNode objectNode = new ObjectNode();
        objectNode.setName("invoice");
        objectNode.setType(dc);
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, 100, 100);

        ObjectNodeEditor editor = new ObjectNodeEditor(objectNodeGR, repository);

        assertEquals("Should return design class via getType()", dc, editor.getType());
        assertEquals("Should return design class via getDesignClass()", dc, editor.getDesignClass());
    }
}
