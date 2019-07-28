package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Class.java
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.XMLStreamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.lang.*;
import java.util.*;

import org.w3c.dom.Element;

public class DesignClass extends AbstractClass {

    private String stereotype;
    private NotifierVector methods;
    private AbstractClass extendClass;
    private List<Interface> implementInterfaces = new ArrayList();
    private Vector sdMethods = new Vector();
    private HashMap <String,Integer> calledMethods = new HashMap<String,Integer>();
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

    public DesignClass(GenericClass gc) {
        super(gc);
        stereotype = null;
        methods = new NotifierVector();
    }

    public DesignClass(String name) {
        this(new GenericClass(name));
    }

    public DesignClass(GenericClass gc, String st) {
        this(gc);
        stereotype = st;
        methods = new NotifierVector();
    }

    public void setStereotype(String st) {
        stereotype = st;
    }

    public String getStereotype() {
        return stereotype;
    }

    public void addMethod(Method m) {
        methods.add(m);
    }

    public void removeMethod(Method m) {
        methods.remove(m);
    }

    public void setMethods(NotifierVector meths) {
        methods.clear();
        methods = meths;
    }

    public NotifierVector getMethods() {
        return methods;
    }

    public Method getMethodByName(String n) {
        Method meth;
        Iterator iterator = methods.iterator();

        while (iterator.hasNext()) {
            meth = (Method) iterator.next();

            if (meth.getName().equals(n)) {
                return meth;
            }
        }

        return null;
    }

    public Method getMethodByIndex(int index) {
        Method meth = null;

        try {
            meth = (Method) methods.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return meth;
    }

    public void clear() {
        super.clear();
        methods.clear();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        setStereotype(node.getAttribute("stereotype"));
        clear();
        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), attributes, this);
        streamer.streamObjectsFrom(streamer.getNodeById(node, "methods"), methods, this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());
        node.setAttribute("stereotype", getStereotype());
        streamer.streamObject(node, "generic", genericClass);

        streamer.streamObjects(streamer.addChild(node, "attributes"), attributes.iterator());
        streamer.streamObjects(streamer.addChild(node, "methods"), methods.iterator());
    }

    public DesignClass clone() {
        DesignClass copyClass = new DesignClass(this.getName());

        if (this.getStereotype() != null) {
            copyClass.setStereotype(this.getStereotype());
        }

        Attribute attribute;
        Iterator attributeIterator = attributes.iterator();
        while (attributeIterator.hasNext()) {
            attribute = (Attribute) attributeIterator.next();
            copyClass.addAttribute(attribute.clone());
        }

        Method method;
        Iterator methodIterator = methods.iterator();
        while (methodIterator.hasNext()) {
            method = (Method) methodIterator.next();
            copyClass.addMethod(method.clone());
        }

        return copyClass;
    }
    
    public void setExtendClass(AbstractClass newExtendClass) {
    	this.extendClass = newExtendClass;
    }
    
    public AbstractClass getExtendClass() {
    	return this.extendClass;
    }
    
    public void setImplementInterface(Interface newInterface) {
    	this.implementInterfaces.add(newInterface);
    }
    
    public List<Interface> getImplementInterfaces() {
    	return this.implementInterfaces;
    }
    
    public void resetImplementInterfaces() {
    	this.implementInterfaces.clear() ;
    }
    
    public void resetSDMethods() {
    	this.sdMethods.clear();
    }
    
    public void addSDMethod(Method m) {
        this.sdMethods.add(m);
    }
    
    public Vector getSDMethods() {
    	return this.sdMethods;
    }
    
    public void addCalledMethod (Method m, DesignClass calledClass, boolean isIterative) {
    	StringBuffer sb = new StringBuffer();
    	if (m.getName().equals("create")) {
    		sb.append(calledClass.getName()+" "+calledClass.getName().toLowerCase()).append(" = ");
    		sb.append("new ").append(calledClass.getName()+"("+")"+";");
    	}else {
	    	if(isIterative) {
	    		sb.append("for(int i=0;i<x;i++){").append(LINE_SEPARATOR);
	    		sb.append("   ");
	    	}
	    	if (!m.getReturnType().getName().equals("void")) {
	    		sb.append(m.getReturnType().getName()+ " " + m.getReturnParameter() + " = ");
	    	}
	    	if (calledClass.getName().equals(this.getName())) {
	    		sb.append("this").append(".");
	    	}else {
	    		sb.append(calledClass.getName()).append(".");
	    	}
	    	sb.append(m.getName()).append("(");
	    	sb.append(m.getParametersAsString());
	    	sb.append(");");
	    	if(isIterative) {
	    		sb.append(LINE_SEPARATOR).append(" ");
	    		sb.append(" }");
	    	}
    	}	
    	this.calledMethods.put(sb.toString(),m.getPriority());
    }
    
    public HashMap<String,Integer> getCalledMethods(){
    	
    	return sortByValue(this.calledMethods);
    }
    
    public void clearCalledMethods() {
    	this.calledMethods.clear();
    }
    
    public static HashMap<String,Integer> sortByValue(HashMap<String,Integer> hm){
    	List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String,Integer>>(hm.entrySet());
    	Collections.sort(list, new Comparator<Map.Entry<String,Integer>>(){
    		public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
    			return (o1.getValue()).compareTo(o2.getValue());
    		}
    	});
    	HashMap<String,Integer> temp = new LinkedHashMap<String,Integer>();
    	for( Map.Entry<String,Integer> aa : list) {
    		temp.put(aa.getKey(), aa.getValue());
    	}
    	return temp;
    }
    
}
