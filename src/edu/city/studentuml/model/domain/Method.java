package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Method.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import static java.lang.System.out;

import org.w3c.dom.Element;

public class Method implements Serializable, IXMLCustomStreamable {

    // static integer constants defining scope
    public static final int INSTANCE = 1;
    public static final int CLASSIFIER = 2;
    // static integer constants defining visibility
    public static final int PRIVATE = 1;
    public static final int PUBLIC = 2;
    public static final int PROTECTED = 3;
    public GenericOperation genericOperation;
    private int scope;         // 1 = instance, 2 = classifier
    private int visibility;    // 1 = private, 2 = public, 3 = protected
    private Type returnType;
    private NotifierVector parameters;
    private int priority = 0 ;
    private String returnParameter = "x";
    private List<String> calledMethods = new ArrayList<String>();
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
    private boolean iterative = false;

    public Method(GenericOperation go) {
        genericOperation = go;
        scope = INSTANCE;
        visibility = PUBLIC;
        returnType = DataType.VOID;
        parameters = new NotifierVector();
    }

    public Method(String name) {
        this(new GenericOperation(name));
    }

    // 'set' methods
    public void setGenericOperation(GenericOperation go) {
        genericOperation = go;
    }

    public void setName(String name) {
        genericOperation.setName(name);
    }

    public void setScope(int sc) {
        if (sc == INSTANCE) {
            scope = sc;
        } else {
            scope = CLASSIFIER;
        }
    }

    public void setVisibility(int vis) {
        if (vis == PRIVATE) {
            visibility = vis;
        } else if (vis == PUBLIC) {
            visibility = vis;
        } else {
            visibility = PROTECTED;
        }
    }

    public void setReturnType(Type dt) {
        returnType = dt;
    }

    public void addParameter(MethodParameter p) {
        parameters.add(p);
    }

    public void removeParameter(MethodParameter p) {
        parameters.remove(p);
    }

    public void removeParameter(int index) {
        try {
            parameters.remove(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
    }

    // 'get' methods
    public GenericOperation getGenericOperation() {
        return genericOperation;
    }

    public String getName() {
        return genericOperation.getName();
    }

    public int getScope() {
        return scope;
    }

    public int getVisibility() {
        return visibility;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Vector getParameters() {
        return parameters;
    }

    public void setParameters(Vector param) {
        parameters.clear();
        parameters = NotifierVector.from(param);
    }

    public MethodParameter getParameter(int index) {
        MethodParameter p;

        try {
            p = (MethodParameter) parameters.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return p;
    }

    public String toString() {
        return getVisibilityString() + getNameString() + "(" + getParametersString() + ")" + getReturnTypeString();
    }

    public String getVisibilityString() {
        if (visibility == PRIVATE) {
            return "-";
        } else if (visibility == PUBLIC) {
            return "+";
        } else {
            return "#";
        }
    }
    
    public String getVisibilityAsString() {
        if (visibility == PRIVATE) {
            return "private";
        } else if (visibility == PUBLIC) {
            return "public";
        } else {
            return "protected";
        }
    }

    public String getNameString() {
        return getName();
    }

    public String getParametersString() {
        String parametersString = "";
        Iterator iterator = parameters.iterator();
        MethodParameter parameter;
        int i = 0;    // keeps track if it is the first iteration

        while (iterator.hasNext()) {
            parameter = (MethodParameter) iterator.next();

            if (i == 0) {
                parametersString += parameter.toString();
            } else {
                parametersString = parametersString + ", " + parameter.toString();
            }

            i++;
        }

        return parametersString;
    }

    public String getReturnTypeString() {
        return " : " + returnType.getName();
    }

    public String getReturnTypeAsString() {
        return returnType.getName();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        setName(node.getAttribute("name"));
        setVisibility(Integer.parseInt(node.getAttribute("visibility")));
        setScope(Integer.parseInt(node.getAttribute("scope")));

        String thistype = node.getAttribute("returntype");
        returnType = new DataType(thistype);
        parameters.clear();
        streamer.streamObjectsFrom(streamer.getNodeById(node, "parameters"), parameters, this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());

        node.setAttribute("returntype", returnType.getName());
        node.setAttribute("scope", Integer.toString(getScope()));
        node.setAttribute("visibility", Integer.toString(getVisibility()));
        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());
    }

    private void setReturnTypeByName(String dt) {
        returnType = new DataType(dt);
    }

    public Method clone() {
        Method copyMethod = new Method(this.getName());
        copyMethod.setScope(this.getScope());
        copyMethod.setVisibility(this.getVisibility());

        if (this.getReturnType() != null) {
            copyMethod.setReturnTypeByName(this.returnType.getName());
        }

        MethodParameter parameter;
        Iterator parameterIterator = parameters.iterator();
        while (parameterIterator.hasNext()) {
            parameter = (MethodParameter) parameterIterator.next();
            copyMethod.addParameter(parameter.clone());
        }

        return copyMethod;
    }
    
    public String getParametersAsString() {
    	String allParameters = "";
    	for (int i=0;i<parameters.size();i++) {
    		MethodParameter parameter = (MethodParameter) parameters.get(i);
    		allParameters += parameter.getName();
    		if (i+2 <= parameters.size()) {
    			allParameters += ",";
    		}
    	}
    	return allParameters;
    }
    
    public void setPriority(int mtdPriority)
    {
    	this.priority = mtdPriority;
    }
    
    public int getPriority()
    {
    	return this.priority;
    }
    
    public void setReturnParameter (String newParameter) {
    	this.returnParameter = newParameter;
    }
    
    public String getReturnParameter () {
    	return this.returnParameter;
    }
    
    public void addCalledMethod (DesignClass homeClass, Method m, DesignClass calledClass, RoleClassifier object, boolean isReflective) {
    	//create a string with the call message for the method
    	StringBuffer sb = new StringBuffer();
    	boolean parameterExists = false;
    	Attribute attribute;
    	Vector attributes = homeClass.getAttributes();
    	
    	if (m.getName().equals("create")) {
    		for(int i=0;i<attributes.size();i++) {
    			attribute= (Attribute) attributes.get(i);
    			out.println(attribute.getName().toLowerCase());
    			out.println(m.getReturnParameter().toString().toLowerCase());
    			if(attribute.getName().toLowerCase().equals(object.getName().toLowerCase())){
    				parameterExists = true;
    			}
    		}
    		if(!parameterExists) {
    			sb.append(calledClass.getName()+" ");
    		}
    		if( object instanceof SDObject) {
	    		sb.append(object.getName()).append(" = ");
	    		sb.append("new ").append(calledClass.getName()+"("+")"+";");
    		}else if (object instanceof MultiObject) {
    		  	sb.append(object.getName()+" = new ArrayList<"+calledClass.getName()+">();");
    		}
    	}else if(m.getName().equals("destroy") && object instanceof SDObject) {
    		sb.append(object.getName() + ".destroy()").append(";");
    	}else if(m.getName().equals("destroy") && object instanceof MultiObject) {
    		sb.append(object.getName() + " = null").append(";");
    	}else {
	    	if(m.isIterative() && object instanceof SDObject) {
	    		sb.append("for(int i=0;i<10;i++){").append(LINE_SEPARATOR);
	    		sb.append("    ");
	    	}else if (m.isIterative() && object instanceof MultiObject) {
	    		sb.append("for(" + calledClass.getName() + " obj : "+object.getName()+") {").append(LINE_SEPARATOR);
	    		sb.append("    ");
	    	}
	    	if (!m.getReturnType().getName().equals("void") && !m.getReturnType().getName().equals("VOID")) {
	    		parameterExists=false;
	    		for(int i=0;i<attributes.size();i++) {
	    			attribute= (Attribute) attributes.get(i);
	    			out.println(attribute.getName().toLowerCase());
	    			out.println(m.getReturnParameter().toString().toLowerCase());
	    			if(attribute.getName().toLowerCase().equals(m.getReturnParameter().toString().toLowerCase())){
	    				parameterExists = true;
	    			}
	    		}
	    		if(!parameterExists) {
	    			sb.append(m.getReturnTypeAsString() + " ");
	    		}
	    		sb.append(m.getReturnParameter() + " = ");
	    	}
	    	if (isReflective && object instanceof SDObject) {
	    		sb.append("this").append(".");
	    	}else if (object instanceof SDObject){
	    		sb.append(object.getName()).append(".");
	    	}else if (object instanceof MultiObject && m.isIterative()) {
	    		sb.append("obj.");
	    	}else if (object instanceof MultiObject && !m.isIterative()) {
	    		sb.append(object.getName() + ".");
	    	}
	    	sb.append(m.getName()).append("(");
	    	sb.append(m.getParametersAsString());
	    	sb.append(");");
	    	if(m.isIterative()) {
	    		sb.append(LINE_SEPARATOR).append(" ");
	    		sb.append("   }");
	    	}
    	}	
    	this.calledMethods.add(sb.toString());
    }
    
    public List<String> getCalledMethods(){
    	//sort by rank and return list of call messages
    	return this.calledMethods;
    }
    
    public void clearCalledMethods() {
    	this.calledMethods.clear();
    }
    
    public void replaceCalledMethod(int index,String newCallMethod) {
    	this.calledMethods.set(index,newCallMethod);
    }
    
    public boolean isIterative() {
        return iterative;
    }

    public void setIterative(boolean i) {
        iterative = i;
    }
    
}
