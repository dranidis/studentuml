package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ClassEditor using mocked showDialog() to avoid UI blocking.
 * Validates the ClassifierEditor pattern for DesignClass editing with
 * attributes, methods, and stereotype support.
 * 
 * @author Dimitris Dranidis
 */
public class ClassEditorTest {

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
        DesignClass dc = new DesignClass("Account");

        // Test that constructor completes without exceptions
        ClassEditor editor = new ClassEditor(dc, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testConstructor_withAttributesAndMethods() {
        DesignClass dc = new DesignClass("Customer");
        dc.addAttribute(new Attribute("name"));
        dc.addAttribute(new Attribute("email"));
        dc.addMethod(new Method("getName"));
        dc.addMethod(new Method("setName"));

        ClassEditor editor = new ClassEditor(dc, repository);

        assertNotNull("Editor should be created with attributes and methods", editor);
    }

    @Test
    public void testConstructor_withStereotype() {
        DesignClass dc = new DesignClass("Controller");
        dc.setStereotype("<<controller>>");

        ClassEditor editor = new ClassEditor(dc, repository);

        assertNotNull("Editor should be created with stereotype", editor);
    }

    @Test
    public void testGetDesignClass_returnsNewClassWithName() {
        DesignClass dc = new DesignClass("Product");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertNotNull("Should return a design class", result);
        assertEquals("Class name should match", "Product", result.getName());
    }

    @Test
    public void testGetDesignClass_preservesAttributes() {
        DesignClass dc = new DesignClass("Order");
        dc.addAttribute(new Attribute("id"));
        dc.addAttribute(new Attribute("total"));

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should preserve attribute count", 2, result.getAttributes().size());
    }

    @Test
    public void testGetDesignClass_preservesMethods() {
        DesignClass dc = new DesignClass("Calculator");
        dc.addMethod(new Method("add"));
        dc.addMethod(new Method("subtract"));
        dc.addMethod(new Method("multiply"));

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should preserve method count", 3, result.getMethods().size());
    }

    @Test
    public void testGetDesignClass_preservesStereotype() {
        DesignClass dc = new DesignClass("Repository");
        dc.setStereotype("<<entity>>");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should preserve stereotype", "<<entity>>", result.getStereotype());
    }

    @Test
    public void testGetDesignClass_withNullStereotype() {
        DesignClass dc = new DesignClass("SimpleClass");
        dc.setStereotype(null);

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertNull("Stereotype should be null", result.getStereotype());
    }

    @Test
    public void testGetDesignClass_withEmptyStereotype() {
        DesignClass dc = new DesignClass("SimpleClass");
        dc.setStereotype("");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertNull("Empty stereotype should be converted to null", result.getStereotype());
    }

    @Test
    public void testShowDialog_OK() {
        DesignClass dc = new DesignClass("Account");

        ClassEditor editor = new ClassEditor(dc, repository) {
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
        DesignClass dc = new DesignClass("Account");

        ClassEditor editor = new ClassEditor(dc, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetDesignClass_withEmptyName() {
        DesignClass dc = new DesignClass("");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Class should have empty name", "", result.getName());
    }

    @Test
    public void testGetDesignClass_withNoAttributesOrMethods() {
        DesignClass dc = new DesignClass("EmptyClass");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should have no attributes", 0, result.getAttributes().size());
        assertEquals("Should have no methods", 0, result.getMethods().size());
    }

    @Test
    public void testGetDesignClass_withMatchingConceptualClass() {
        // Create a conceptual class with attributes
        ConceptualClass cc = new ConceptualClass("Person");
        cc.addAttribute(new Attribute("name"));
        cc.addAttribute(new Attribute("age"));
        repository.addConceptualClass(cc);

        // Create a design class with the same name
        DesignClass dc = new DesignClass("Person");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertNotNull("Should return design class", result);
        assertEquals("Class name should match", "Person", result.getName());
    }

    @Test
    public void testGetDesignClass_multipleClassesInRepository() {
        DesignClass dc1 = new DesignClass("ClassA");
        DesignClass dc2 = new DesignClass("ClassB");
        repository.addClass(dc1);
        repository.addClass(dc2);

        ClassEditor editor = new ClassEditor(dc1, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should return class with correct name", "ClassA", result.getName());
        assertEquals("Repository should still have 2 classes", 2, repository.getClasses().size());
    }

    @Test
    public void testGetDesignClass_withComplexStereotype() {
        DesignClass dc = new DesignClass("Service");
        dc.setStereotype("<<service layer>>");

        ClassEditor editor = new ClassEditor(dc, repository);

        DesignClass result = editor.getDesignClass();

        assertEquals("Should preserve complex stereotype", "<<service layer>>", result.getStereotype());
    }
}
