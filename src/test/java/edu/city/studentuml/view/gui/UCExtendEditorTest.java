package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for UCExtendEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and the editor's ability to manage
 * extension points for UC Extend relationships.
 * 
 * Note: Tests now use UCExtend domain objects directly, not UCExtendGR graphical wrappers.
 * This follows proper MVC architecture where editors work with domain models.
 * 
 * @author Dimitris Dranidis
 */
public class UCExtendEditorTest {

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
        // Create use cases and extend relationship
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);

        // Test that constructor completes without exceptions
        UCExtendEditor editor = new UCExtendEditor(null, "Test UC Extend", extend, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withNoExtensionPoints() {
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);

        UCExtendEditor editor = new UCExtendEditor(null, "Test", extend, repository);

        // Verify editor initializes with empty extension points
        assertNotNull("Extension points list should not be null", editor.getExtensionPoints());
        assertEquals("Should have no extension points initially", 0, editor.getExtensionPoints().size());
    }

    @Test
    public void testInitialize_withExtensionPoints() {
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);

        // Add extension points
        ExtensionPoint ep1 = new ExtensionPoint("point1");
        ExtensionPoint ep2 = new ExtensionPoint("point2");
        extend.addExtensionPoint(ep1);
        extend.addExtensionPoint(ep2);

        UCExtendEditor editor = new UCExtendEditor(null, "Test", extend, repository);

        // Verify editor initializes with the extension points
        assertEquals("Should have 2 extension points", 2, editor.getExtensionPoints().size());
        assertEquals("First extension point should match", "point1", editor.getExtensionPoints().get(0).getName());
        assertEquals("Second extension point should match", "point2", editor.getExtensionPoints().get(1).getName());
    }

    @Test
    public void testShowDialog_OK() {
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);

        UCExtendEditor editor = new UCExtendEditor(null, "Test", extend, repository) {
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
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);

        UCExtendEditor editor = new UCExtendEditor(null, "Test", extend, repository) {
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
        UseCase baseUC = new UseCase("BaseUseCase");
        UseCase extendingUC = new UseCase("ExtendingUseCase");
        UCExtend extend = new UCExtend(extendingUC, baseUC);
        extend.addExtensionPoint(new ExtensionPoint("test"));

        UCExtendEditor editor = new UCExtendEditor(null, "Test", extend, repository) {
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
        assertEquals("Extension points should still be correct", 1, editor.getExtensionPoints().size());
    }
}
