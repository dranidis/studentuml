package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for MethodEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and editDialog() workflow for
 * creating and editing methods.
 * 
 * @author Dimitris Dranidis
 */
public class MethodEditorTest {

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
        MethodEditor editor = new MethodEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return true;
            }
        };

        Method result = editor.editDialog(null, null);

        assertNotNull("Should create new method when OK is clicked", result);
    }

    @Test
    public void testCreateNew_Cancel() {
        MethodEditor editor = new MethodEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return false;
            }
        };

        Method result = editor.editDialog(null, null);

        assertNull("Should return null when Cancel is clicked", result);
    }

    @Test
    public void testEditExisting_OK() {
        MethodEditor editor = new MethodEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                assertEquals("testMethod", getMethodName());
                return true;
            }
        };

        Method existing = new Method("testMethod");

        Method result = editor.editDialog(existing, null);

        assertNotNull("Should return method when OK is clicked", result);
        assertSame("Should return same method instance", existing, result);
    }

    @Test
    public void testEditNull_shouldInitializeEmptyFields() {
        MethodEditor editor = new MethodEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                String name = getMethodName();
                assertTrue("Name field should be empty for new method",
                        name == null || name.isEmpty());
                return true;
            }
        };

        Method result = editor.editDialog(null, null);

        assertNotNull("Should create new method", result);
    }
}
