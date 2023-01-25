package edu.city.studentuml.model.domain;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.Vector;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.codegeneration.CCMethod;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class Method implements Serializable, IXMLCustomStreamable, Copyable<Method> {

    // static integer constants defining scope
    public static final int INSTANCE = 1;
    public static final int CLASSIFIER = 2;
    // static integer constants defining visibility
    public static final int PRIVATE = 1;
    public static final int PUBLIC = 2;
    public static final int PROTECTED = 3;
    @JsonIgnore
    public GenericOperation genericOperation;
    private int scope; // 1 = instance, 2 = classifier
    private int visibility; // 1 = private, 2 = public, 3 = protected
    private Type returnType;
    private NotifierVector<MethodParameter> parameters;
    @JsonIgnore
    private int priority = 0;
    @JsonIgnore
    private String returnParameter = "x";

    /*
     * CODE GEN
     */
    @JsonIgnore
    private final CCMethod ccMethod= new CCMethod();

    @JsonIgnore
    public CCMethod getCCMethod() {
        return ccMethod;
    }

    public Method(GenericOperation go) {
        genericOperation = go;
        scope = INSTANCE;
        visibility = PUBLIC;
        returnType = DataType.VOID;
        parameters = new NotifierVector<>();
    }

    public Method(String name) {
        this(new GenericOperation(name));
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
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

    public Vector<MethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Vector<MethodParameter> param) {
        parameters.clear();

        parameters = new NotifierVector<>();
        // with REFERENCES (not clones) to the
        // elements of param. 
        // TO CHECK Correct?
        for (int i = 0; i < param.size(); i++) {
            parameters.add(param.get(i));
        }
    }

    // used by codegeneration; refactor
    public MethodParameter getParameter(int index) {
        MethodParameter p;

        try {
            p = parameters.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        return p;
    }

    public String toString() {
        return getVisibilityString() + getNameString() + getParametersString()
                + (Settings.showTypes() ? getReturnTypeString() : "");
    }

    @JsonIgnore
    public String getVisibilityString() {
        if (visibility == PRIVATE) {
            return "-";
        } else if (visibility == PUBLIC) {
            return "+";
        } else {
            return "#";
        }
    }

    // used by codegeneration
    @JsonIgnore
    public String getVisibilityAsString() {
        if (visibility == PRIVATE) {
            return "private";
        } else if (visibility == PUBLIC) {
            return "public";
        } else {
            return "protected";
        }
    }

    @JsonIgnore
    public String getNameString() {
        return getName();
    }

    @JsonIgnore
    private String getParametersString() {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        parameters.forEach(par -> sj.add(par.toStringShowTypes()));

        return sj.toString();
    }

    @JsonIgnore
    public String getReturnTypeString() {
        return " : " + returnType.getName();
    }

    @JsonIgnore
    public String getReturnTypeAsString() {
        return returnType.getName();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        setName(node.getAttribute("name"));
        setVisibility(Integer.parseInt(node.getAttribute("visibility")));
        setScope(Integer.parseInt(node.getAttribute("scope")));

        String thistype = node.getAttribute("returntype");
        returnType = new DataType(thistype);
        parameters.clear();
        streamer.streamChildrenFrom(streamer.getNodeById(node, "parameters"), this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
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

        parameters.forEach(parameter -> copyMethod.addParameter(parameter.clone()));

        return copyMethod;
    }

    // used by code generation: TODO: Refactor
    @JsonIgnore
    public String getParametersAsString() {
        StringJoiner sj = new StringJoiner(", ");
        parameters.forEach(par -> sj.add(par.getName()));

        return sj.toString();
    }

    public void setPriority(int mtdPriority) {
        this.priority = mtdPriority;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setReturnParameter(String newParameter) {
        this.returnParameter = newParameter;
    }

    public String getReturnParameter() {
        return this.returnParameter;
    }

    @Override
    public Method copyOf(Method a) {
        return a.clone();
    }

}
