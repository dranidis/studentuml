package edu.city.studentuml.applet;

import edu.city.studentuml.model.graphical.DCDModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {

    private DCDModel dcd;

    MyKeyListener(DCDModel dcd) {
        this.dcd = dcd;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("Key Typed");
    }

    public void keyReleased(KeyEvent e) {
    }
}
