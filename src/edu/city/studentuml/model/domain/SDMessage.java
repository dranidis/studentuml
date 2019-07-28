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
    private String returnParameter = "x";

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
    	try {
	    	if (!this.toString().substring(this.toString().indexOf(":")+1).chars().allMatch(Character::isWhitespace)) {
	    		String methodName = this.toString().substring(this.toString().indexOf(":=")+3,this.toString().lastIndexOf("("));
	    		methodName= methodName.replaceAll("\\s+","");
		    	Method sdMethod = new Method(methodName);
		    	String mtdNumber = this.toString().substring(0,1);
		    	sdMethod.setPriority(Integer.parseInt(mtdNumber));
		    	if (this.getReturnType() != null ) {
		    		sdMethod.setReturnType(this.getReturnType());
		    	}else {
		    		sdMethod.setReturnType(new DataType("void"));	
		    	}
		    	this.getMethodParameters();
		    	if (!this.methodParameters.isEmpty()) {
		         for (int x=0; x<this.methodParameters.size();x++) {
		    		sdMethod.addParameter((MethodParameter) this.methodParameters.get(x)); 
		    		}
		    	}
		    	return sdMethod;
	    	}else {
	    		return null;
	    	}
    	}catch(StringIndexOutOfBoundsException e) {
    		String[] split = this.toString().split("\\s+");
    		for(int i=0;i<split.length;i++) {
    			if (i==2) {
    				setReturnParameter(split[i]);
    			}
    		}
    		out.println("returnParameter: " + returnParameter);
    		return null;
    	}
    }
    
    public Type getReturnType() {
    	DataType dataType = new DataType("void"); 
    	try {
    	String returnType = this.toString().substring(this.toString().indexOf(":")+2,this.toString().lastIndexOf(":=")-1);
    	String[] split2 = returnType.toString().split("\\s+");
    	int i=0;
    	do {
			if (i==0) {
				returnType = split2[i];
			}
			if (i==1) {
				setReturnParameter(split2[i]);
			}
			i++;
		}while(i<split2.length);
	    dataType = new DataType(returnType);
    	}catch(StringIndexOutOfBoundsException e) {
    		out.println("No datatype, reseting to void!");
    	}
        return dataType;
    }
    
    public void getMethodParameters() {
    	
    	this.methodParameters.clear();
    	String parameters = this.toString().substring(this.toString().indexOf("(")+1,this.toString().lastIndexOf(")"));
    	if (parameters !=null && !parameters.isEmpty() && parameters != "") {
	    	String [] splitParameters = parameters.split("[,]");
	    	for(int i=0;i<splitParameters.length;i++) {
	    		String [] words = splitParameters[i].split("\\s+");
	    		if(words[0].chars().allMatch(Character::isWhitespace)) {
	    			words[0] = words[1];
	    			words[1] = words[2];
	    		}
	    		Type parameterType = new DataType (words[0]);
	    		String parameter = words[1];
	    		out.println("Parameter:" + parameter);
	    		MethodParameter methodParameter = new MethodParameter (parameter,parameterType);
	    		this.methodParameters.add(methodParameter);
	    	 }
    	}
    }
    
    public boolean isIterative() {
    	if (this.toString().contains("*")) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void setReturnParameter (String newParameter) {
    	this.returnParameter = newParameter;
    }
    
    public String getReturnParameter () {
    	return this.returnParameter;
    }

    // the sd message subclasses should define a toString() method
    public abstract String toString();
}
