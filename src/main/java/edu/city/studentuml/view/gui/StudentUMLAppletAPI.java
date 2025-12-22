package edu.city.studentuml.view.gui;

import java.applet.AppletContext;
import java.awt.Container;

import javax.swing.JMenuBar;

public interface StudentUMLAppletAPI {

    Container getContentPane();

    void setVisible(boolean b);

    String getUsername();

    void setJMenuBar(JMenuBar getjMenuBar);

    AppletContext getAppletContext();
    
}
