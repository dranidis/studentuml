package edu.city.studentuml.util;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.JColorChooser;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public class Colors {

    private static final Logger logger = Logger.getLogger(Colors.class.getName());

    public static final String DEFAULT_FILL_COLOR_STRING = "" + new Color(222, 183, 255).getRGB();
    public static final String DEFAULT_DARK_FILL_COLOR_STRING = "" + new Color(99, 64, 135).getRGB();

    public static final Color BLACK = new Color(0, 0, 0);

    public static final Color WHITE = new Color(255, 255, 255);

    private static Color fillColor;
    private static Color darkFillColor;

    private Colors() {
        throw new IllegalStateException("Utility class");
    }

    public static void prinUIManagerColorResources() {
        for (Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getValue() instanceof ColorUIResource)
                logger.fine(() -> entry.getKey() + "              " + entry.getValue().toString());
        }
    }

    public static Color getFillColor() {
        if (Theme.isDark()) {
            return darkFillColor;
        } else {
            return fillColor;
        }
    }

    public static Color getBackgroundColor() {

        return getColorForKey("TextArea.background");
    }

    public static Color getOutlineColor() {
        return getColorForKey("TextArea.foreground");
    }

    public static Color getHighlightColor() {
        Color c = getColorForKey("TextArea.selectionBackground", "Tree.selectionBackground");
        if (c == null) {
            c = new Color(36, 121, 255);
        }
        return c;
    }

    public static Color getErrorColor() {
        return Color.RED;
    }

    public static void chooseFillColor() {
        if (Theme.isDark()) {
            Color fc = JColorChooser.showDialog(null, "Choose a color", darkFillColor);

            if (fc == null) {
                return;
            }

            darkFillColor = fc;
            Settings.setDarkFillColor(darkFillColor);
        } else {
            Color fc = JColorChooser.showDialog(null, "Choose a color", fillColor);

            if (fc == null) {
                return;
            }

            fillColor = fc;
            Settings.setFillColor(fillColor);
        }
    }

    public static void setFillColor(Color color) {
        fillColor = color;
    }

    public static void setDarkFillColor(Color color) {
        darkFillColor = color;
    }

    static Color getColorForKey(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Color c = UIManager.getColor(key);
            if (c == null) {
                sb.append(key + " ");
                logger.finest(() -> "Key not found in UIManager: " + key);
            } else {
                return c;
            }
        }
        logger.severe(() -> "None of the Keys were found in UIManager: )" + sb.toString());
        return null;
    }
}
