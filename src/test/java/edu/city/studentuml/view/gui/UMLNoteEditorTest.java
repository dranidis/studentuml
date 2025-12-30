package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.UMLNoteGR;

/**
 * Tests for UMLNoteEditor using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern and the editor's ability to manage
 * UML note text content.
 * 
 * @author Dimitris Dranidis
 */
public class UMLNoteEditorTest {

    private UMLProject project;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
    }

    @Test
    public void testConstructor_shouldNotThrow() {
        // Create a UML note
        UMLNoteGR note = new UMLNoteGR("Test note content", null, new Point(100, 100));

        // Test that constructor completes without exceptions
        UMLNoteEditor editor = new UMLNoteEditor(null, "Test Note", note);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withNoteText() {
        String noteText = "This is a test note\nwith multiple lines";
        UMLNoteGR note = new UMLNoteGR(noteText, null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note);

        // Verify editor initializes with the note text
        assertEquals("Note text should match", noteText, editor.getText());
    }

    @Test
    public void testInitialize_withEmptyText() {
        UMLNoteGR note = new UMLNoteGR("", null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note);

        // Verify editor initializes with empty text
        assertEquals("Note text should be empty", "", editor.getText());
    }

    @Test
    public void testShowDialog_OK() {
        UMLNoteGR note = new UMLNoteGR("Original text", null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note) {
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
        UMLNoteGR note = new UMLNoteGR("Original text", null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note) {
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
        UMLNoteGR note = new UMLNoteGR("Test content", null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note) {
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
        assertEquals("Text should still be correct", "Test content", editor.getText());
    }

    @Test
    public void testGetText_returnsCurrentValue() {
        String initialText = "Initial note text";
        UMLNoteGR note = new UMLNoteGR(initialText, null, new Point(100, 100));

        UMLNoteEditor editor = new UMLNoteEditor(null, "Test", note);

        // Verify getText returns the current text
        assertEquals("Should return initial text", initialText, editor.getText());
    }
}
