package edu.city.studentuml.frame;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import javax.swing.JFrame;

import edu.city.studentuml.view.gui.ApplicationFrame;

public class StudentUMLFrame extends JFrame {
    
    static {
        InputStream stream = StudentUMLFrame.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  

    public static void main(String[] args) {
        new ApplicationFrame(new StudentUMLFrame());
    }
}
