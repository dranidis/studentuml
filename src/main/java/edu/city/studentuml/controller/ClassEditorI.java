package edu.city.studentuml.controller;

import java.awt.Component;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.view.gui.components.Editor;

public interface ClassEditorI extends Editor<DesignClass> {

    /**
     * Returns true if OK button is pressed.
     * 
     * @param parentComponent
     * @param string
     * @return
     * @deprecated Use {@link #editDialog(DesignClass, Component)} instead.
     */
    @Deprecated
    boolean showDialog(Component parentComponent, String string);

    /**
     * @deprecated Use {@link #editDialog(DesignClass, Component)} instead.
     */
    @Deprecated
    DesignClass getDesignClass();

}
