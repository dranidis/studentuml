package edu.city.studentuml.util;

import java.awt.Color;

import javax.swing.UIManager;

public class Theme {

    private Theme() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isDark() {
        Color bg = UIManager.getColor("TextArea.background");
        return bg.getBlue() < 125;    }
    
}
