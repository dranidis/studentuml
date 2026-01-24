package edu.city.studentuml.model.graphical;

import java.awt.Font;

/**
 * Central registry for all fonts used in the StudentUML application. This class
 * provides a single location to define and manage fonts used for drawing
 * diagram elements, ensuring consistency across the application.
 * 
 * @author Dimitris Dranidis
 */
public class FontRegistry {

    // Base font family names
    private static final String SANS_SERIF = "SansSerif";
    private static final String MONOSPACED = "Monospaced";

    // Message fonts
    public static final Font MESSAGE_FONT = new Font(SANS_SERIF, Font.PLAIN, 12);

    // Class diagram fonts
    public static final Font CLASS_NAME_FONT = new Font(SANS_SERIF, Font.BOLD, 14);
    public static final Font CLASS_ATTRIBUTE_FONT = new Font(SANS_SERIF, Font.PLAIN, 12);
    public static final Font CLASS_METHOD_FONT = new Font(SANS_SERIF, Font.ITALIC, 12);
    public static final Font CLASS_STEREOTYPE_FONT = new Font(SANS_SERIF, Font.PLAIN, 12);

    // Interface fonts
    public static final Font INTERFACE_NAME_FONT = new Font(SANS_SERIF, Font.BOLD, 14);
    public static final Font INTERFACE_METHOD_FONT = new Font(SANS_SERIF, Font.ITALIC, 12);

    // Association fonts
    public static final Font ASSOCIATION_NAME_FONT = new Font(SANS_SERIF, Font.PLAIN, 12);
    public static final Font ASSOCIATION_ROLE_FONT = new Font(SANS_SERIF, Font.PLAIN, 10);

    // Activity diagram fonts
    public static final Font ACTIVITY_NAME_FONT = new Font(SANS_SERIF, Font.BOLD, 12);
    public static final Font ACTION_NODE_FONT = new Font(SANS_SERIF, Font.PLAIN, 14);
    public static final Font DECISION_NODE_FONT = new Font(SANS_SERIF, Font.ITALIC, 10);
    public static final Font FORK_NODE_FONT = new Font(SANS_SERIF, Font.ITALIC, 10);
    public static final Font OBJECT_NODE_NAME_FONT = new Font(SANS_SERIF, Font.PLAIN, 14);
    public static final Font OBJECT_NODE_STATES_FONT = new Font(SANS_SERIF, Font.PLAIN, 11);

    // Sequence diagram fonts
    public static final Font SD_OBJECT_NAME_FONT = new Font(SANS_SERIF, Font.BOLD, 12);
    public static final Font COMBINED_FRAGMENT_OPERATOR_FONT = new Font(SANS_SERIF, Font.BOLD, 10);
    public static final Font COMBINED_FRAGMENT_GUARD_FONT = new Font(SANS_SERIF, Font.PLAIN, 10);

    // Use case diagram fonts
    public static final Font USE_CASE_FONT = new Font(SANS_SERIF, Font.PLAIN, 12);
    public static final Font USE_CASE_EXTENSION_POINT_LABEL_FONT = new Font(SANS_SERIF, Font.BOLD, 10);
    public static final Font USE_CASE_EXTENSION_POINT_FONT = new Font(SANS_SERIF, Font.PLAIN, 8);
    public static final Font ACTOR_NAME_FONT = new Font(SANS_SERIF, Font.BOLD, 12);

    // Edge/relationship fonts
    public static final Font EDGE_FONT = new Font(SANS_SERIF, Font.PLAIN, 10);

    // Note fonts
    public static final Font NOTE_FONT = new Font(SANS_SERIF, Font.PLAIN, 10);
    public static final Font NOTE_SMALL_FONT = new Font(SANS_SERIF, Font.PLAIN, 8);

    // Helper text fonts
    public static final Font HELPER_TEXT_FONT = new Font(SANS_SERIF, Font.PLAIN, 9);

    // Repository viewer fonts
    public static final Font REPOSITORY_STATE_FONT = new Font(MONOSPACED, Font.PLAIN, 12);
    public static final Font REPOSITORY_LOG_FONT = new Font(MONOSPACED, Font.PLAIN, 11);

    /**
     * Create a scaled version of a font.
     * 
     * @param baseFont The base font to scale
     * @param scale    The scale factor
     * @return A new font with size scaled by the given factor
     */
    public static Font getScaledFont(Font baseFont, double scale) {
        int scaledSize = (int) Math.round(baseFont.getSize() * scale);
        return baseFont.deriveFont((float) scaledSize);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private FontRegistry() {
        // Utility class - no instances needed
    }
}
