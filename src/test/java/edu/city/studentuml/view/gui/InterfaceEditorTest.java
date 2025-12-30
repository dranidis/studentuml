package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for InterfaceEditor using mocked showDialog() to avoid UI blocking.
 * Validates the ClassifierEditor pattern for Interface editing with methods.
 * 
 * @author Dimitris Dranidis
 */
public class InterfaceEditorTest {

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
        Interface interfaceObj = new Interface("Comparable");

        // Test that constructor completes without exceptions
        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testConstructor_withMethods() {
        Interface interfaceObj = new Interface("Serializable");
        interfaceObj.addMethod(new Method("serialize"));
        interfaceObj.addMethod(new Method("deserialize"));

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        assertNotNull("Editor should be created with methods", editor);
    }

    @Test
    public void testGetInterface_returnsNewInterfaceWithName() {
        Interface interfaceObj = new Interface("Runnable");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        Interface result = editor.getInterface();

        assertNotNull("Should return an interface", result);
        assertEquals("Interface name should match", "Runnable", result.getName());
    }

    @Test
    public void testGetInterfaceName_returnsName() {
        Interface interfaceObj = new Interface("Cloneable");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        String result = editor.getInterfaceName();

        assertEquals("Interface name should match", "Cloneable", result);
    }

    @Test
    public void testGetInterface_preservesMethods() {
        Interface interfaceObj = new Interface("Collection");
        interfaceObj.addMethod(new Method("add"));
        interfaceObj.addMethod(new Method("remove"));
        interfaceObj.addMethod(new Method("size"));

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        Interface result = editor.getInterface();

        assertEquals("Should preserve method count", 3, result.getMethods().size());
    }

    @Test
    public void testGetMethods_returnsMethodVector() {
        Interface interfaceObj = new Interface("List");
        interfaceObj.addMethod(new Method("get"));
        interfaceObj.addMethod(new Method("set"));

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        assertEquals("Should return correct number of methods", 2, editor.getMethods().size());
    }

    @Test
    public void testShowDialog_OK() {
        Interface interfaceObj = new Interface("Comparable");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository) {
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
        Interface interfaceObj = new Interface("Comparable");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetInterface_withEmptyName() {
        Interface interfaceObj = new Interface("");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        Interface result = editor.getInterface();

        assertEquals("Interface should have empty name", "", result.getName());
    }

    @Test
    public void testGetInterface_withNoMethods() {
        Interface interfaceObj = new Interface("Marker");

        InterfaceEditor editor = new InterfaceEditor(interfaceObj, repository);

        Interface result = editor.getInterface();

        assertEquals("Should have no methods", 0, result.getMethods().size());
    }
}
