package edu.city.studentuml.frame;

import javax.swing.JFrame;

import edu.city.studentuml.view.gui.ApplicationFrame;

public class StudentUMLFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public static void main(String args[]) {
        new ApplicationFrame(new StudentUMLFrame());
    }
}
