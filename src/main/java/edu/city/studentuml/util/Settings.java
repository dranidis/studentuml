package edu.city.studentuml.util;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author Dimitris Dranidis
 */
public class Settings {

    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    private static final String DEFAULT_PATH = "DEFAULT_PATH";
    private static final String SELECT_LAST = "SELECT_LAST";
    private static final String SHOW_TYPES_SD = "SHOW_TYPES_SD";
    private static final String SHOW_RETURN_SD = "SHOW_RETURN_SD";
    private static final String GTKLOOKANDFEEL = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    private static final String LOOK_AND_FEEL = "LOOK_AND_FEEL";

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    private Settings() {
    }

    public static boolean showTypes() {
        return Preferences.userRoot().get(SHOW_TYPES_SD, "").equals(TRUE);
    }

    public static void setShowTypes(boolean show) {
        Preferences.userRoot().put(SHOW_TYPES_SD, show ? TRUE : FALSE);
    }

    public static boolean showReturnArrows() {
        return Preferences.userRoot().get(SHOW_RETURN_SD, "").equals(TRUE);
    }

    public static void setShowReturnArrows(boolean show) {
        Preferences.userRoot().put(SHOW_RETURN_SD, show ? TRUE : FALSE);
    }

    public static String getDefaultPath() {
        return Preferences.userRoot().get(DEFAULT_PATH, "");
    }

    public static void setDefaultPath(String path) {
        logger.fine(() -> "Set path to: " + path);
        Preferences.userRoot().put(DEFAULT_PATH, path);
    }

    public static boolean keepLastSelection() {
        return Preferences.userRoot().get(SELECT_LAST, "").equals(TRUE);
    }

    public static void setKeepLastSelection(boolean keep) {
        Preferences.userRoot().put(SELECT_LAST, keep ? TRUE : FALSE);
    }

    public static String getLookAndFeel() {
        return Preferences.userRoot().get(LOOK_AND_FEEL, GTKLOOKANDFEEL);
    }

    public static void setLookAndFeel(String className) {
        Preferences.userRoot().put(LOOK_AND_FEEL, className);
    }
}
