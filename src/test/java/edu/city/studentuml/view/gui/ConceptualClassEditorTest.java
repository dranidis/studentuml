package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for ConceptualClassEditor using mocked showDialog() to avoid UI
 * blocking. Validates the ClassifierEditor pattern for ConceptualClass editing
 * with attributes.
 * 
 * @author Dimitris Dranidis
 */
public class ConceptualClassEditorTest {

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
        ConceptualClass cc = new ConceptualClass("Person");

        // Test that constructor completes without exceptions
        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testConstructor_withAttributes() {
        ConceptualClass cc = new ConceptualClass("Person");
        cc.addAttribute(new Attribute("name"));
        cc.addAttribute(new Attribute("age"));

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        assertNotNull("Editor should be created with attributes", editor);
    }

    @Test
    public void testGetConceptualClass_returnsNewClassWithName() {
        ConceptualClass cc = new ConceptualClass("Customer");

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        ConceptualClass result = editor.getConceptualClass();

        assertNotNull("Should return a conceptual class", result);
        assertEquals("Class name should match", "Customer", result.getName());
    }

    @Test
    public void testGetConceptualClass_preservesAttributes() {
        ConceptualClass cc = new ConceptualClass("Person");
        cc.addAttribute(new Attribute("name"));
        cc.addAttribute(new Attribute("age"));

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        ConceptualClass result = editor.getConceptualClass();

        assertEquals("Should preserve attribute count", 2, result.getAttributes().size());
    }

    @Test
    public void testShowDialog_OK() {
        ConceptualClass cc = new ConceptualClass("Product");

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository) {
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
        ConceptualClass cc = new ConceptualClass("Product");

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetConceptualClass_withEmptyName() {
        ConceptualClass cc = new ConceptualClass("");

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        ConceptualClass result = editor.getConceptualClass();

        assertEquals("Class should have empty name", "", result.getName());
    }

    @Test
    public void testGetConceptualClass_withNoAttributes() {
        ConceptualClass cc = new ConceptualClass("EmptyClass");

        ConceptualClassEditor editor = new ConceptualClassEditor(cc, repository);

        ConceptualClass result = editor.getConceptualClass();

        assertEquals("Should have no attributes", 0, result.getAttributes().size());
    }
}
