package edu.city.studentuml.model.domain;

import java.util.StringJoiner;
import java.util.Vector;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author dimitris
 */
public class CallMessage extends SDMessage implements IXMLCustomStreamable {

    private GenericOperation genericOperation;
    private boolean iterative;
    protected NotifierVector<MethodParameter> parameters;
    private MessageReturnValue returnValue;
    private Type returnType;

    public CallMessage(RoleClassifier from, RoleClassifier to, GenericOperation go) {
        super(from, to);
        genericOperation = go;
        iterative = false;
        parameters = new NotifierVector<>();
    }

    public String getName() {
        return genericOperation.getName();
    }

    public void setName(String n) {
        genericOperation.setName(n);
    }

    public boolean isIterative() {
        return iterative;
    }

    public void setIterative(boolean i) {
        iterative = i;
    }

    public void addParameter(MethodParameter p) {
        parameters.add(p);
    }

    public void removeParameter(MethodParameter p) {
        parameters.remove(p);
    }

    public Vector<MethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Vector<MethodParameter> param) {
        parameters.clear();
        for (MethodParameter par : param) {
            parameters.add(par);
        }
    }

    public void clear() {
        parameters.clear();
        returnValue = null;
        returnType = null;
    }

    public MethodParameter getParameterByName(String name) {
        for (MethodParameter param : parameters) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    public MessageReturnValue getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(MessageReturnValue v) {
        returnValue = v;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type v) {
        returnType = v;
    }

    public String toString() {
        String text = "";

        text += getRank() + (isIterative() ? "*" : "") + ": ";

        if ((returnValue != null) && !returnValue.toString().equals("")) {
            text += returnValue.toString() + " := ";
        }

        if ((getName() != null) && !getName().equals("")) {
            text += getName() + getParametersString();
        }

        return text;
    }

    public String getReturnValueAsString() {
        if (returnValue != null && !returnValue.getName().equals("")) {
            return returnValue.getName();
        } else {
            return "VOID";
        }
    }

    protected String getParametersString() {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (MethodParameter par : parameters) {
            sj.add(par.toStringShowTypes());
        }
        return sj.toString();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        setName(node.getAttribute("name"));
        setIterative(Boolean.parseBoolean(node.getAttribute("iterative")));
        parameters.clear();
            streamer.streamObjectsFrom(streamer.getNodeById(node, "parameters"), parameters, this);

        String rv = node.getAttribute("returns");
        if (rv != null) {
            if (rv.equals("")) {
                returnValue = null;
            } else {
                returnValue = new MessageReturnValue(rv);
            }
        }

        rv = node.getAttribute("returnType");
        if (rv != null) {
            if (rv.equals("")) {
                returnType = null;
            } else {
                returnType = new DataType(rv);
                ;
            }
        }

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        node.setAttribute("iterative", Boolean.toString(isIterative()));

        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        if (returnValue != null) {
            node.setAttribute("returns", returnValue.getName());
        } else {
            node.setAttribute("returns", "");
        }

        if (returnType != null) {
            node.setAttribute("returnType", returnType.getName());
        } else {
            node.setAttribute("returnType", "");
        }

        streamer.streamObject(node, "operation", genericOperation);
        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());
    }

    public CallMessage clone() {
        CallMessage copyCallMessage = new CallMessage(getSource(), getTarget(), new GenericOperation(this.getName()));
        copyCallMessage.setIterative(this.isIterative());

        for (MethodParameter p : parameters)
            copyCallMessage.addParameter(p.clone());

        MessageReturnValue returnVal = this.getReturnValue();
        if (returnVal != null) {
            copyCallMessage.setReturnValue(new MessageReturnValue(this.getReturnValue().getName()));
        }

        copyCallMessage.setReturnType(this.getReturnType());

        return copyCallMessage;
    }

}
