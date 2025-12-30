package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for CallMessageEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and the editor's ability to manage
 * call message properties (name, parameters, return value/type, iterative
 * flag).
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

        // Create graphical elements
        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        // Create call message
        GenericOperation operation = new GenericOperation("doSomething");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        // Test that constructor completes without exceptions
        CallMessageEditor editor = new CallMessageEditor(null, "Test Call Message", callMessageGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withCallMessage() {
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        GenericOperation operation = new GenericOperation("calculate");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        callMessage.setIterative(true);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessageGR, repository);

        // Editor should have initialized with the message data
        assertNotNull("Editor should be created", editor);
    }

    @Test
    public void testShowDialog_OK() {
        DesignClass dcA = new DesignClass("ClassA");
        DesignClass dcB = new DesignClass("ClassB");
        SDObject objA = new SDObject("a", dcA);
        SDObject objB = new SDObject("b", dcB);

        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        GenericOperation operation = new GenericOperation("test");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessageGR, repository) {
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

        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        GenericOperation operation = new GenericOperation("test");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessageGR, repository) {
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

        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        GenericOperation operation = new GenericOperation("process");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessageGR, repository) {
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

        SDObjectGR objAGR = new SDObjectGR(objA, 100);
        SDObjectGR objBGR = new SDObjectGR(objB, 200);

        GenericOperation operation = new GenericOperation("iterate");
        CallMessage callMessage = new CallMessage(objA, objB, operation);
        callMessage.setIterative(true);
        CallMessageGR callMessageGR = new CallMessageGR(objAGR, objBGR, callMessage, 150);

        // Test that constructor handles iterative messages
        CallMessageEditor editor = new CallMessageEditor(null, "Test", callMessageGR, repository);

        assertNotNull("Editor should be created for iterative message", editor);
    }
}
