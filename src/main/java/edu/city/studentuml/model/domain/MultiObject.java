package edu.city.studentuml.model.domain;

/**
 * 
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class MultiObject extends AbstractObject {

    public MultiObject(String name, DesignClass dc) {
        super(name, dc);
    }

    // for Undo/Redo
    public MultiObject clone() {
        return new MultiObject(this.getName(), this.getDesignClass().clone());
    }
}
