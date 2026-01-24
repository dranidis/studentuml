package edu.city.studentuml.model.domain;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class SDObject extends AbstractObject {

    public SDObject(String name, DesignClass dc) {
        super(name, dc);
    }

    // for Undo/Redo
    public SDObject clone() {
        SDObject cloned = new SDObject(this.getName(), this.getDesignClass().clone());
        cloned.setStereotype(this.getStereotype());
        cloned.setScope(this.getScope());
        return cloned;
    }
}
