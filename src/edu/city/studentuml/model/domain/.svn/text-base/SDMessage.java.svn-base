package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDMessage.java
import java.io.Serializable;

public abstract class SDMessage implements Serializable {

    protected int rank;    // the rank is not initialized in the constructor, but
    // instead it is set by the diagram model
    protected RoleClassifier source;    // message originating from this object
    protected RoleClassifier target;    // message directed to this object

    public SDMessage(RoleClassifier from, RoleClassifier to) {
        source = from;
        target = to;
    }

    public RoleClassifier getSource() {
        return source;
    }

    public RoleClassifier getTarget() {
        return target;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int r) {
        rank = r;
    }

    public boolean isReflective() {
        return (source == target);
    }

    // the sd message subclasses should define a toString() method
    public abstract String toString();
}
