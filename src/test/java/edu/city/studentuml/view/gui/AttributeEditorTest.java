package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for AttributeEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and editDialog() workflow for
 * creating and editing attributes.
 * 
 * @author Dimitris Dranidis
 */
public class AttributeEditorTest {

    private CentralRepository repository;
    private UMLProject project;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
        repository = project.getCentralRepository();
    }

    @Test
    public void testCreateNew_OK() {
        AttributeEditor editor = new AttributeEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return true;
            }
        };

        Attribute result = editor.editDialog(null, null);

        assertNotNull("Should create new attribute when OK is clicked", result);
        assertNotNull("Attribute should have a name", result.getName());
    }

    @Test
    public void testCreateNew_Cancel() {
        AttributeEditor editor = new AttributeEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return false;
            }
        };

        Attribute result = editor.editDialog(null, null);

        assertNull("Should return null when Cancel is clicked", result);
    }

    @Test
    public void testEditExisting_OK() {
        AttributeEditor editor = new AttributeEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                assertEquals("testAttr", getAttributeName());
                assertEquals(DataType.INTEGER, getType());
                return true;
            }
        };

        Attribute existing = new Attribute("testAttr", DataType.INTEGER);
        existing.setVisibility(Attribute.PUBLIC);

        Attribute result = editor.editDialog(existing, null);

        assertNotNull("Should return attribute when OK is clicked", result);
        assertSame("Should return same attribute instance", existing, result);
    }

    @Test
    public void testMultipleInitializeCalls_shouldBeIdempotent() {
        AttributeEditor editor = new AttributeEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                initializeIfNeeded();
                initializeIfNeeded();
                return true;
            }
        };

        Attribute attr = new Attribute("test", DataType.INTEGER);
        Attribute result = editor.editDialog(attr, null);

        assertNotNull("Should still work after multiple init calls", result);
    }

    @Test
    public void testEditNull_shouldInitializeEmptyFields() {
        AttributeEditor editor = new AttributeEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                String name = getAttributeName();
                assertTrue("Name field should be empty for new attribute",
                        name == null || name.isEmpty());
                return true;
            }
        };

        Attribute result = editor.editDialog(null, null);

        assertNotNull("Should create new attribute", result);
    }
}
