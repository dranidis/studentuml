package edu.city.studentuml.frame;

import edu.city.studentuml.util.Constants;
import edu.city.studentuml.view.gui.ApplicationFrame;
import edu.city.studentuml.view.gui.ApplicationGUI;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 *
 * @author draganbisercic
 */
public class StudentUMLFrame extends JFrame {

    private static StudentUMLFrame instance = null;

    private StudentUMLFrame() {
        super("StudentUML");
    }

    public static StudentUMLFrame getInstance() {
        if (instance == null) {
            instance = new StudentUMLFrame();
        }

        return instance;
    }

    public static void main(String args[]) {
        StudentUMLFrame f = StudentUMLFrame.getInstance();
        ApplicationGUI applicationGUI = new ApplicationFrame(f);
    }
}
