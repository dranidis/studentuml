package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for MethodParameterEditor using mocked showDialog() to avoid UI
 * blocking. Validates the lazy initialization pattern and editDialog() workflow
 * for creating and editing method parameters.
 * 
 * @author Dimitris Dranidis
 */
public class MethodParameterEditorTest {

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
        MethodParameterEditor editor = new MethodParameterEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return true;
            }
        };

        MethodParameter result = editor.editDialog(null, null);

        assertNotNull("Should create new parameter when OK is clicked", result);
    }

    @Test
    public void testCreateNew_Cancel() {
        MethodParameterEditor editor = new MethodParameterEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return false;
            }
        };

        MethodParameter result = editor.editDialog(null, null);

        assertNull("Should return null when Cancel is clicked", result);
    }

    @Test
    public void testEditExisting_OK() {
        MethodParameterEditor editor = new MethodParameterEditor(repository) {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                // Just verify initialization doesn't throw
                return true;
            }
        };

        MethodParameter existing = new MethodParameter("param1", DataType.STRING);

        MethodParameter result = editor.editDialog(existing, null);

        assertNotNull("Should return parameter when OK is clicked", result);
        assertSame("Should return same parameter instance", existing, result);
    }
}
