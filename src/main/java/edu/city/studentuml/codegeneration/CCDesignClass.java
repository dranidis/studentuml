package edu.city.studentuml.codegeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;

public class CCDesignClass {

    private Classifier extendClass; 
    private List<Interface> implementInterfaces = new ArrayList<>(); 
    private Vector<Method> sdMethods = new Vector<>();
    private List<Attribute> attributes = new ArrayList<>();
    

    public void setExtendClass(Classifier newExtendClass) {
        this.extendClass = newExtendClass;
    }

    public Classifier getExtendClass() {
        return this.extendClass;
    }

    public void addImplementInterfaces(Interface newInterface) {
        this.implementInterfaces.add(newInterface);
    }

    public List<Interface> getImplementInterfaces() {
        return this.implementInterfaces;
    }

    public void resetImplementInterfaces() {
        this.implementInterfaces.clear();
    }

    public void addSDMethod(Method m) {
        this.sdMethods.add(m);
    }

    public Vector<Method> getSDMethods() {
        return this.sdMethods;
    }

    public void replaceSDMethod(int index, Method newSDMethod) {
        this.sdMethods.set(index, newSDMethod);
    }

    /*
     * 
     */

     public void addAttribute(Attribute a) {
        Attribute existingAttribute = getAttributeByName(a.getName());
        if (existingAttribute == null) {
            attributes.add(a);
        }
    }

    private Attribute getAttributeByName(String n) {
        for (Attribute attrib : attributes) {
            if (attrib.getName().equals(n)) {
                return attrib;
            }  
        }
        return null;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
