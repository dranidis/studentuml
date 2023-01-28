package edu.city.studentuml.model.domain;

import java.io.Serializable;

/**
 * 
 * @author Ervin Ramollari
 */
public abstract class SDMessage implements Serializable {

    protected int rank;    // the rank is not initialized in the constructor, but
    // instead it is set by the diagram model
    protected RoleClassifier source;    // message originating from this object
    protected RoleClassifier target;    // message directed to this object
    private String returnParameter = "x";

    protected SDMessage(RoleClassifier from, RoleClassifier to) {
        source = from;
        target = to;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public RoleClassifier getSource() {
        return source;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public RoleClassifier getTarget() {
        return target;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */        
    public int getRank() {
        return rank;
    }

    public void setRank(int r) {
        rank = r;
    }

    public boolean isReflective() {
        return (source == target);
    }
    
    
    public void setReturnParameter (String newParameter) {
    	this.returnParameter = newParameter;
    }
    
    public String getReturnParameter () {
    	return this.returnParameter;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */        
    public abstract String getName();

    // the sd message subclasses should define a toString() method
    public abstract String toString();
}
