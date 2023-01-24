package edu.city.studentuml.util;

import java.awt.Color;
import java.util.logging.Logger;

public class Theme {

    private static final Logger logger = Logger.getLogger(Theme.class.getName());

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
        return bg.getBlue() < 125;    }
    
}
