package edu.city.studentuml.frame;

import edu.city.studentuml.view.gui.ApplicationFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        new ApplicationFrame(StudentUMLFrame.getInstance());
    }
}
