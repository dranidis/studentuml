package edu.city.studentuml.util;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

/**
 * @author Dimitris Dranidis
 */
public class Settings {

    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    private static final String DEFAULT_PATH = "DEFAULT_PATH";
    private static final String SELECT_LAST = "SELECT_LAST";
    private static final String SHOW_TYPES_SD = "SHOW_TYPES_SD";
    private static final String SHOW_RETURN_SD = "SHOW_RETURN_SD";
    private static final String LOOK_AND_FEEL = "LOOK_AND_FEEL";

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    private static Preferences pref = Preferences.userNodeForPackage(Settings.class);

    private Settings() {
    }

    public static boolean showTypes() {
        return pref.get(SHOW_TYPES_SD, "").equals(TRUE);
    }

    public static void setShowTypes(boolean show) {
        pref.put(SHOW_TYPES_SD, show ? TRUE : FALSE);
    }

    public static boolean showReturnArrows() {
        return pref.get(SHOW_RETURN_SD, "").equals(TRUE);
    }

    public static void setShowReturnArrows(boolean show) {
        pref.put(SHOW_RETURN_SD, show ? TRUE : FALSE);
    }

    public static String getDefaultPath() {
        return pref.get(DEFAULT_PATH, "");
    }

    public static void setDefaultPath(String path) {
        logger.fine(() -> "Set path to: " + path);
        pref.put(DEFAULT_PATH, path);
    }

    public static boolean keepLastSelection() {
        return pref.get(SELECT_LAST, "").equals(TRUE);
    }

    public static void setKeepLastSelection(boolean keep) {
        pref.put(SELECT_LAST, keep ? TRUE : FALSE);
    }

    public static String getLookAndFeel() {
        return pref.get(LOOK_AND_FEEL, UIManager.getSystemLookAndFeelClassName());
    }

    public static void setLookAndFeel(String className) {
        pref.put(LOOK_AND_FEEL, className);
    }
}
