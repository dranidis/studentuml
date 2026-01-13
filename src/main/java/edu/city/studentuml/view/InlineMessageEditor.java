package edu.city.studentuml.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.graphical.ConstantsGR;
import edu.city.studentuml.model.graphical.FontRegistry;
import edu.city.studentuml.model.graphical.GraphicsHelper;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.util.MessageSyntaxParser;
import edu.city.studentuml.util.MessageSyntaxParser.ParseResult;

/**
 * Message-specific inline editor that handles message syntax parsing and error
 * handling. Delegates UI responsibilities to InlineStringEditor and focuses on
 * message domain logic.
 * 
 * @author Dimitris Dranidis
 */
public class InlineMessageEditor {

    private final DiagramView view;
    private final InlineStringEditor stringEditor;
    private final MessageSyntaxParser parser;
    private SDMessageGR currentMessage;
    private Consumer<ParseResult> onSuccess;
    private boolean originalVisibility;

    public InlineMessageEditor(DiagramView view, Component parentComponent) {
        this.view = view;
        this.stringEditor = new InlineStringEditor(view);
        this.parser = new MessageSyntaxParser();
    }

    /**
     * Start inline editing for a message.
     *
     * @param message   The message graphical element to edit
     * @param onSuccess Callback when editing is successfully committed with valid
     *                  syntax
     * @param onCancel  Callback when editing is cancelled (Escape key)
     */
    public void startEditing(SDMessageGR message, Consumer<ParseResult> onSuccess, Runnable onCancel) {
        this.currentMessage = message;
        this.onSuccess = onSuccess;

        // Reconstruct current message syntax
        String currentSyntax = reconstructMessageSyntax(message);

        // Start editing with the reconstructed syntax
        startEditingWithText(currentSyntax, onCancel);
    }

    /**
     * Start editing with custom text (used for error recovery).
     * 
     * @param text     The text to edit
     * @param onCancel Callback when editing is cancelled
     */
    private void startEditingWithText(String text, Runnable onCancel) {
        // Hide the message text while editing
        originalVisibility = !currentMessage.isTextHidden();
        currentMessage.setHideText(true);

        // Calculate position and font
        Point position = calculateTextFieldPosition(currentMessage, text);
        Font font = FontRegistry.getScaledFont(FontRegistry.MESSAGE_FONT, view.getScale());

        // Start the string editor with validation
        stringEditor.startEditing(
                text,
                position,
                font,
                this::validateMessageSyntax, // Validator: returns error message or null
                this::handleCommit, // On commit: apply changes (text is already validated)
                () -> handleCancel(onCancel), // On cancel: restore and notify
                this::restoreMessageVisibility // On editor removed: restore visibility
        );
    }

    /**
     * Validate message syntax and return error message or null if valid.
     */
    private String validateMessageSyntax(String text) {
        ParseResult result = parser.parse(text);
        if (result.isValid()) {
            return null; // Valid
        } else {
            return result.getErrorMessage();
        }
    }

    /**
     * Handle commit by applying the parsed changes (text is already validated).
     */
    private void handleCommit(String text) {
        // Parse the text (we know it's valid because validator passed)
        ParseResult result = parser.parse(text);

        // Restore message text visibility
        restoreMessageVisibility();

        // Call success callback
        if (onSuccess != null) {
            onSuccess.accept(result);
        }
    }

    /**
     * Handle cancel by restoring visibility and calling cancel callback.
     */
    private void handleCancel(Runnable onCancel) {
        restoreMessageVisibility();
        if (onCancel != null) {
            onCancel.run();
        }
    }

    /**
     * Restore the message text visibility.
     */
    private void restoreMessageVisibility() {
        if (currentMessage != null && originalVisibility) {
            currentMessage.setHideText(false);
        }
    }

    /**
     * Reconstruct the message syntax from a CallMessage.
     */
    private String reconstructMessageSyntax(SDMessageGR message) {
        if (message.getMessage() instanceof CallMessage) {
            CallMessage callMsg = (CallMessage) message.getMessage();
            return parser.reconstruct(callMsg);
        }
        return "";
    }

    /**
     * Calculate the position for the text field based on message location.
     */
    private Point calculateTextFieldPosition(SDMessageGR message, String currentSyntax) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D textBounds = GraphicsHelper
                .getTextBounds(currentSyntax.isBlank() ? "(" : currentSyntax, FontRegistry.MESSAGE_FONT, frc);

        int messageDY = ConstantsGR.getInstance().get("SDMessageGR", "messageDY");

        int atY = message.getY() - messageDY - (int) textBounds.getHeight() - 3; // add some padding below: 3

        int atX;

        if (!message.getMessage().isReflective()) {
            int startX = message.getStartingX();
            int endX = message.getEndingX();

            int lineWidth = Math.abs(startX - endX);
            int messageStartX = Math.min(startX, endX);
            int textX = GraphicsHelper.calculateCenteredTextX(lineWidth, textBounds);

            atX = messageStartX + textX;
        } else {
            atX = message.getStartingX() + 5;
        }

        // Since paintChildren resets the transform, text field doesn't get scaled
        // But the drawn text IS scaled, so we need to position at SCALED coordinates
        // Scale coordinates to match drawn text position
        double scale = view.getScale();
        int scaledX = (int) Math.round(atX * scale);
        int scaledY = (int) Math.round(atY * scale);

        return new Point(scaledX, scaledY);
    }

    /**
     * Check if currently editing.
     */
    public boolean isEditing() {
        return stringEditor.isEditing();
    }

    /**
     * Get the message currently being edited.
     */
    public SDMessageGR getCurrentMessage() {
        return currentMessage;
    }
}
