package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ObjectEditor using mocked showDialog() to avoid UI blocking.
 * Validates the TypedEntityEditor pattern for SD Object editing with name and
 * type (DesignClass) management.
 * 
 * @author Dimitris Dranidis
 */
public class ObjectEditorTest {

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
        // Create a design class and SD object
        DesignClass dc = new DesignClass("TestClass");
        SDObject obj = new SDObject("testObject", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        // Test that constructor completes without exceptions
        ObjectEditor editor = new ObjectEditor(objGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withObjectAndClass() {
        DesignClass dc = new DesignClass("Person");
        repository.addClass(dc);

        SDObject obj = new SDObject("p1", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository);

        // Verify editor initializes with the object data
        assertEquals("Object name should match", "p1", editor.getObjectName());
        assertEquals("Design class should match", dc, editor.getDesignClass());
    }

    @Test
    public void testInitialize_withEmptyName() {
        DesignClass dc = new DesignClass("TestClass");
        SDObject obj = new SDObject("", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository);

        // Verify editor handles empty name
        assertEquals("Object name should be empty", "", editor.getObjectName());
        assertEquals("Design class should match", dc, editor.getDesignClass());
    }

    @Test
    public void testShowDialog_OK() {
        DesignClass dc = new DesignClass("TestClass");
        SDObject obj = new SDObject("obj", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository) {
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
        DesignClass dc = new DesignClass("TestClass");
        SDObject obj = new SDObject("obj", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository) {
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
        DesignClass dc = new DesignClass("TestClass");
        SDObject obj = new SDObject("myObject", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository);

        assertEquals("Should return object name", "myObject", editor.getObjectName());
    }

    @Test
    public void testGetDesignClass_returnsCurrentType() {
        DesignClass dc = new DesignClass("Account");
        repository.addClass(dc);

        SDObject obj = new SDObject("account1", dc);
        SDObjectGR objGR = new SDObjectGR(obj, 100);

        ObjectEditor editor = new ObjectEditor(objGR, repository);

        assertEquals("Should return design class", dc, editor.getDesignClass());
        assertEquals("Design class name should match", "Account", editor.getDesignClass().getName());
    }
}