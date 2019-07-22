package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//SDMessage.java
import java.io.Serializable;
import java.util.Vector;
import static java.lang.System.out;

public abstract class SDMessage implements Serializable {

    protected int rank;    // the rank is not initialized in the constructor, but
    // instead it is set by the diagram model
    protected RoleClassifier source;    // message originating from this object
    protected RoleClassifier target;    // message directed to this object
    private Vector methodParameters = new Vector();

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
    	sdMethod.setReturnType(this.getReturnType());
    	this.getMethodParameters();
    	if (!this.methodParameters.isEmpty()) {
         for (int x=0; x<this.methodParameters.size();x++) {
    		sdMethod.addParameter((MethodParameter) this.methodParameters.get(x)); 
    		}
    	}
    	return sdMethod;
    }
    
    public Type getReturnType() {
    	String returnType = this.toString().substring(this.toString().indexOf(": ")+2,this.toString().lastIndexOf(":="));
    	DataType dataType = new DataType(returnType);
    	return dataType;
    }
    
    public void getMethodParameters() {
    	
    	this.methodParameters.clear();
    	String parameters = this.toString().substring(this.toString().indexOf("(")+1,this.toString().lastIndexOf(")"));
    	if (parameters !=null && !parameters.isEmpty() && parameters != "") {
	    	String [] splitParameters = parameters.split("[\\s|,]");
	    	for(int i=0;i<splitParameters.length;i++) {
	    		Type parameterType = new DataType (splitParameters[i]);
	    		i++;
	    		String parameter = splitParameters[i];
	    		out.println("Parameter:" + parameter);
	    		MethodParameter methodParameter = new MethodParameter (parameter,parameterType);
	    		this.methodParameters.add(methodParameter);
	    	 }
    	}
    }

    // the sd message subclasses should define a toString() method
    public abstract String toString();
}
