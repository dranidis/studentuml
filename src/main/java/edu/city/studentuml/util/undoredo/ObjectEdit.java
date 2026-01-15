package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.SDObject;

/**
 * @author draganbisercic
 */
public class ObjectEdit {

    private SDObject object;
    private String typeName;

    public ObjectEdit(SDObject object, String typeName) {
        this.object = object;
        this.typeName = typeName;
    }

    public SDObject getObject() {
        return object;
    }

    public void setObject(SDObject object) {
        this.object = object;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ObjectEdit clone() {
        SDObject obj = new SDObject(object.getName(), object.getDesignClass());
        obj.setStereotype(object.getStereotype());
        obj.setScope(object.getScope());
        ObjectEdit copy = new ObjectEdit(obj, typeName);
        return copy;
    }
}
