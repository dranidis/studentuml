package edu.city.studentuml.editing;

import java.util.Objects;
import java.util.function.Predicate;

import javax.swing.JOptionPane;

import edu.city.studentuml.view.gui.StringEditorDialog;

/**
 * Shared editing helpers used by graphical elements. Extracted to a neutral
 * package to decouple UI dialog flow and validation from the graphical layer.
 */
public final class ElementEditHelpers {

    private ElementEditHelpers() {
    }

    /**
     * Opens a string editor dialog and performs optional duplicate validation.
     * Returns the new value, or null if cancelled or blocked by duplicate policy.
     */
    public static String requestStringValue(
            EditContext context,
            String dialogTitle,
            String fieldLabel,
            String currentValue,
            Predicate<String> duplicateExists,
            String duplicateErrorMessage) {

        // Open dialog with current value
        StringEditorDialog dialog = new StringEditorDialog(
                context.getParentComponent(),
                dialogTitle,
                fieldLabel,
                currentValue);

        if (!dialog.showDialog()) {
            return null; // user cancelled
        }

        String newValue = dialog.getText();

        // If unchanged, return the same value; callers will short-circuit
        if (Objects.equals(currentValue, newValue)) {
            return newValue;
        }

        // Check for duplicates when provided
        if (duplicateExists != null && duplicateExists.test(newValue)) {
            if (duplicateErrorMessage != null && !duplicateErrorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        duplicateErrorMessage,
                        "Cannot Edit", JOptionPane.ERROR_MESSAGE);
            }
            return null; // blocked due to duplicate
        }

        return newValue;
    }
}
