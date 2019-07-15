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
    
    public Method getMethod() {
    	Method sdMethod = new Method(this.toString().substring(this.toString().indexOf(":=")+3,this.toString().lastIndexOf("(")));
    	return sdMethod;
    }
    
    public String getReturnType() {
    	return (this.toString().substring(this.toString().indexOf(": ")+2,this.toString().lastIndexOf(":=")));
    	
    }
    
    public String getAttributes() {
    	return (this.toString().substring(this.toString().indexOf("(")+1,this.toString().lastIndexOf(")")));
    	
    }

    // the sd message subclasses should define a toString() method
    public abstract String toString();
}
