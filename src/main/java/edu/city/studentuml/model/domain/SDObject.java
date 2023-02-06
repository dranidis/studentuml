package edu.city.studentuml.model.domain;

/**
 * 
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class SDObject extends AbstractObject {

    public SDObject(String name, DesignClass dc) {
        super(name, dc);
    }

    // for Undo/Redo
    public SDObject clone() {
        return new SDObject(this.getName(), this.getDesignClass().clone());
    }
}
