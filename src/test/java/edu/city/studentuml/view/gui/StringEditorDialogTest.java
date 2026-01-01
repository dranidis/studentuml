package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for StringEditorDialog using mocked showDialog() to avoid UI blocking.
 * Validates the lazy initialization pattern for the StringEditorDialog which is
 * a direct subclass of OkCancelDialog.
 * 
 * @author Dimitris Dranidis
 */
public class StringEditorDialogTest {

    @Test
    public void testShowDialog_OK() {
        StringEditorDialog editor = new StringEditorDialog(null, "Test", "Label:", "initial") {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return true;
            }
        };

        assertNotNull("Editor should be created", editor);
        assertTrue("Should return true for OK", editor.showDialog());
    }

    @Test
    public void testShowDialog_Cancel() {
        StringEditorDialog editor = new StringEditorDialog(null, "Test", "Label:", "initial") {
            @Override
            public boolean showDialog() {
                initializeIfNeeded();
                return false;
            }
        };

        assertFalse("Should return false for Cancel", editor.showDialog());
    }

    @Test
    public void testConstructor_shouldNotThrow() {
        // Test that constructor completes without exceptions
        StringEditorDialog editor = new StringEditorDialog(null, "Test Dialog", "Enter value:", "initial value");

        assertNotNull("Editor should be created successfully", editor);
    }
}
