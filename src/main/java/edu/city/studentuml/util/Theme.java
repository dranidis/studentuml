package edu.city.studentuml.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Theme {

    private static final Logger logger = Logger.getLogger(Theme.class.getName());

    private static final List<FlatLafTheme> flatLafThemes = new ArrayList<>();

    private Theme() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Naive theme detection
     * 
     * @return
     */
    public static boolean isDark() {
        Color bg = Colors.getColorForKey("TextArea.background");
        logger.finest(() -> "Detecting theme " + (bg.getBlue() < 125 ? "dark" : "light"));
        return bg.getBlue() < 125;
    }
    
    static {
        flatLafThemes.add(new FlatLafTheme("FlatLaf Light ", "com.formdev.flatlaf.FlatLightLaf"));
        flatLafThemes.add(new FlatLafTheme("FlatLaf Dark ", "com.formdev.flatlaf.FlatDarkLaf"));
        flatLafThemes.add(new FlatLafTheme("FlatLaf IntelliJ (light)", "com.formdev.flatlaf.FlatIntelliJLaf"));
        flatLafThemes.add(new FlatLafTheme("FlatLaf Darcula (dark)", "com.formdev.flatlaf.FlatDarculaLaf"));
    }

    public static List<FlatLafTheme> getFlatLafThemes() {
        return flatLafThemes;
    }

}

