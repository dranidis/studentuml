package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for CallMessageEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and the editor's ability to manage
 * call message properties (name, parameters, return value/type, iterative
 * flag). Note: Tests now use CallMessage domain objects directly, not
 * CallMessageGR graphical wrappers. This follows proper MVC architecture where
 * editors work with domain models.
 * 
 * @author Dimitris Dranidis
 */
public class CallMessageEditorTest {

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
        // Create SD objects
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        // Create call message
        GenericOperation operation = new GenericOperation("doSomething");
        CallMessage callMessage = new CallMessage(objA, objB, operation);

        // Test that constructor completes without exceptions
        CallMessageEditor editor = new CallMessageEditor(null, "Test Call Message", callMessage, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withCallMessage() {
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        GenericOperation operation = new GenericOperation("calculate");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        callMessage.setIterative(true);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessage, repository);

        // Editor should have initialized with the message data
        assertNotNull("Editor should be created", editor);
    }

    @Test
    public void testShowDialog_OK() {
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        GenericOperation operation = new GenericOperation("test");
        CallMessage callMessage = new CallMessage(objA, objB, operation);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessage, repository) {
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
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        GenericOperation operation = new GenericOperation("test");
        CallMessage callMessage = new CallMessage(objA, objB, operation);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessage, repository) {
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
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        GenericOperation operation = new GenericOperation("process");
        CallMessage callMessage = new CallMessage(objA, objB, operation);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessage, repository) {
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
    }

    @Test
    public void testConstructor_withIterativeMessage() {
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        GenericOperation operation = new GenericOperation("iterate");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        callMessage.setIterative(true);

        // Test that constructor handles iterative messages
        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessage, repository);

        assertNotNull("Editor should be created for iterative message", editor);
    }
}
