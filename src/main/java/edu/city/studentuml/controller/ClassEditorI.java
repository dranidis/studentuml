package edu.city.studentuml.controller;

import java.awt.Component;

import edu.city.studentuml.model.domain.DesignClass;


public interface ClassEditorI {

    /**
     * Returns true if OK button is pressed.
     * 
     * @param parentComponent
     * @param string
     * @return
     */
    boolean showDialog(Component parentComponent, String string);

    DesignClass getDesignClass();

}
