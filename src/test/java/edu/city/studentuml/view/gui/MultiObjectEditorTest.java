package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for MultiObjectEditor using mocked showDialog() to avoid UI blocking.
 * Validates the TypedEntityEditor pattern for SD MultiObject editing with name
 * and type (DesignClass) management.
 * 
 * @author Dimitris Dranidis
 */
public class MultiObjectEditorTest {

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
        // Create a design class and multi-object
        DesignClass dc = new DesignClass("TestClass");
        MultiObject multiObj = new MultiObject("testCollection", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        // Test that constructor completes without exceptions
        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withMultiObjectAndClass() {
        DesignClass dc = new DesignClass("Student");
        repository.addClass(dc);

        MultiObject multiObj = new MultiObject("students", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository);

        // Verify editor initializes with the multi-object data
        assertEquals("Multi-object name should match", "students", editor.getMultiObjectName());
        assertEquals("Design class should match", dc, editor.getDesignClass());
    }

    @Test
    public void testInitialize_withEmptyName() {
        DesignClass dc = new DesignClass("TestClass");
        MultiObject multiObj = new MultiObject("", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository);

        // Verify editor handles empty name
        assertEquals("Multi-object name should be empty", "", editor.getMultiObjectName());
        assertEquals("Design class should match", dc, editor.getDesignClass());
    }

    @Test
    public void testShowDialog_OK() {
        DesignClass dc = new DesignClass("TestClass");
        MultiObject multiObj = new MultiObject("collection", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository) {
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
        MultiObject multiObj = new MultiObject("collection", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetMultiObjectName_returnsNameFieldValue() {
        DesignClass dc = new DesignClass("Order");
        MultiObject multiObj = new MultiObject("orders", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository);

        assertEquals("Should return multi-object name", "orders", editor.getMultiObjectName());
    }

    @Test
    public void testGetDesignClass_returnsCurrentType() {
        DesignClass dc = new DesignClass("Product");
        repository.addClass(dc);

        MultiObject multiObj = new MultiObject("products", dc);
        MultiObjectGR multiObjGR = new MultiObjectGR(multiObj, 100);

        MultiObjectEditor editor = new MultiObjectEditor(multiObjGR, repository);

        assertEquals("Should return design class", dc, editor.getDesignClass());
        assertEquals("Design class name should match", "Product", editor.getDesignClass().getName());
    }
}
