package edu.city.studentuml.view;

import java.awt.Font;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A lightweight inline text editor that appears as an overlay on a diagram
 * view. Provides basic text editing functionality with commit/cancel callbacks.
 * This is a general-purpose UI component with no knowledge of domain-specific
 * logic (like message parsing).
 * 
 * @author Dimitris Dranidis
 */
public class InlineStringEditor {

    private final DiagramView view;
    private JTextField textField;
    private Font font;
    private Consumer<String> onCommit;
    private Function<String, String> validator; // Returns error message or null if valid
    private Runnable onCancel;
    private MouseAdapter viewMouseListener;
    private Runnable onEditorRemoved; // Called when editor is removed (to restore UI state)
    private boolean isShowingError; // Flag to prevent re-validation during error display

    /**
     * Create an inline string editor for the given view.
     *
     * @param view The diagram view where the editor will appear
     */
    public InlineStringEditor(DiagramView view) {
        this.view = view;
    }

    /**
     * Start inline editing at the specified position with the given text.
     *
     * @param initialText     The initial text to display
     * @param position        The position where the editor should appear (in scaled
     *                        view coordinates)
     * @param font            The font to use for the text field
     * @param validator       Optional validator function that returns error message
     *                        or null if valid
     * @param onCommit        Callback when editing is successfully committed with
     *                        valid text
     * @param onCancel        Callback when editing is cancelled (Escape key)
     * @param onEditorRemoved Callback when editor is removed (to restore UI state)
     */
    public void startEditing(String initialText, Point position, Font font,
            Function<String, String> validator,
            Consumer<String> onCommit, Runnable onCancel, Runnable onEditorRemoved) {
        if (textField != null && textField.getParent() != null) {
            // Already editing, cancel previous edit
            cancelEditing();
        }

        this.font = font;
        this.validator = validator;
        this.onCommit = onCommit;
        this.onCancel = onCancel;
        this.onEditorRemoved = onEditorRemoved;

        textField = new JTextField();
        textField.setText(initialText);
        textField.setFont(font);
        textField.setBorder(null);

        // Calculate initial size
        Rectangle2D textBounds = getBoundsForText(initialText.isEmpty() ? " " : initialText);

        // Set bounds BEFORE adding to view to prevent flash at wrong position
        textField.setBounds(position.x, position.y,
                (int) textBounds.getWidth() + 5,
                (int) textBounds.getHeight() + 5);

        // Make invisible initially to prevent flash
        textField.setVisible(false);

        view.setLayout(null);
        view.add(textField);

        // Add key listeners
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    commitEditing();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelEditing();
                }
            }
        });

        // Add focus listener to commit on focus lost
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Commit when focus is lost (unless explicitly cancelled)
                if (textField != null && textField.getParent() != null) {
                    commitEditing();
                }
            }
        });

        // Add document listener to auto-resize text field as user types
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                adjustTextFieldWidth();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                adjustTextFieldWidth();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                adjustTextFieldWidth();
            }

            private void adjustTextFieldWidth() {
                if (textField == null)
                    return;

                String text = textField.getText();
                if (text.isEmpty()) {
                    text = " "; // Minimum width
                }

                Rectangle2D bounds = getBoundsForText(text);
                textField.setSize((int) bounds.getWidth() + 5, (int) bounds.getHeight() + 5);
                view.revalidate();
                view.repaint();
            }
        });

        // Add mouse listener to view to detect clicks outside the text field
        viewMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // If editing and click is outside the text field, commit
                if (textField != null && textField.getParent() != null) {
                    // Check if click is outside text field bounds
                    if (!textField.getBounds().contains(e.getPoint())) {
                        commitEditing();
                    }
                }
            }
        };
        view.addMouseListener(viewMouseListener);

        // Use invokeLater to make visible only after layout is complete
        // This prevents flash at wrong position
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (textField != null) {
                textField.setVisible(true);
                textField.selectAll();
                textField.requestFocusInWindow();
            }
        });
    }

    /**
     * Calculate text bounds for the given text using the current font.
     */
    private Rectangle2D getBoundsForText(String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        return font.getStringBounds(text.isEmpty() ? " " : text, frc);
    }

    /**
     * Commit the current edit by validating and calling the commit callback with
     * the text. If validation fails, shows error and keeps the editor open.
     */
    private void commitEditing() {
        if (textField == null || textField.getParent() == null) {
            return;
        }

        // Prevent re-validation if we're already showing an error dialog
        if (isShowingError) {
            return;
        }

        String text = textField.getText().trim();

        // Validate if validator is provided
        if (validator != null) {
            String errorMessage = validator.apply(text);
            if (errorMessage != null) {
                // Set flag to prevent focus listener from triggering another validation
                isShowingError = true;

                // Validation failed - show error and keep editor open
                javax.swing.JOptionPane.showMessageDialog(
                        view,
                        errorMessage,
                        "Validation Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);

                // Clear flag after dialog closes
                isShowingError = false;

                // Keep editor open - user can continue editing
                // Use invokeLater to ensure focus is restored after dialog closes
                // and any pending Enter key events are consumed
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (textField != null) {
                        textField.selectAll();
                        textField.requestFocusInWindow();
                    }
                });
                return;
            }
        }

        // Validation passed (or no validator) - remove text field and commit
        removeTextField();

        // Call commit callback with the text
        if (onCommit != null) {
            onCommit.accept(text);
        }
    }

    /**
     * Cancel the current edit without applying changes.
     */
    private void cancelEditing() {
        removeTextField();

        // Call cancel callback
        if (onCancel != null) {
            onCancel.run();
        }
    }

    /**
     * Remove the text field from the view.
     */
    private void removeTextField() {
        if (textField != null && textField.getParent() != null) {
            view.remove(textField);
            view.revalidate();
            view.repaint();

            // Return focus to the view to prevent focus going to other UI components
            view.requestFocusInWindow();
        }

        // Remove the mouse listener from view
        if (viewMouseListener != null) {
            view.removeMouseListener(viewMouseListener);
            viewMouseListener = null;
        }

        textField = null;

        // Call editor removed callback (to restore UI state)
        if (onEditorRemoved != null) {
            onEditorRemoved.run();
        }
    }

    /**
     * Check if currently editing.
     */
    public boolean isEditing() {
        return textField != null && textField.getParent() != null;
    }
}
