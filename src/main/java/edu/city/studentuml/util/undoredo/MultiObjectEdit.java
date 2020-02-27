package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.MultiObject;

/**
 *
 * @author draganbisercic
 */
public class MultiObjectEdit {

    MultiObject multiObject;
    String typeName;

    public MultiObjectEdit(MultiObject multiObject, String typeName) {
        this.multiObject = multiObject;
        this.typeName = typeName;
    }

    public MultiObject getMultiObject() {
        return multiObject;
    }

    public void setMultiObject(MultiObject multiObject) {
        this.multiObject = multiObject;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public MultiObjectEdit clone() {
        MultiObject obj = new MultiObject(multiObject.getName(), multiObject.getDesignClass());
        MultiObjectEdit copy = new MultiObjectEdit(obj, typeName);
        return copy;
    }
}
