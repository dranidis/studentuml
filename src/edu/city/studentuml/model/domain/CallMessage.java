package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CallMessage.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

import static java.lang.System.out;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;

public class CallMessage extends SDMessage implements IXMLCustomStreamable {

    private GenericOperation genericOperation;
    private boolean iterative;
    private NotifierVector parameters;
    private MessageReturnValue returnValue;

    public CallMessage(RoleClassifier from, RoleClassifier to, GenericOperation go) {
        super(from, to);
        genericOperation = go;
        iterative = false;
        parameters = new NotifierVector();
        returnValue = null;
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

    public void addParameter(MessageParameter p) {
        parameters.add(p);
    }

    public void removeParameter(MessageParameter p) {
        parameters.remove(p);
    }

    public Vector getParameters() {
        return parameters;
    }

    public void setParameters(Vector param) {
        parameters.clear();
        parameters = NotifierVector.from(param);
    }

    public void clear() {
        parameters.clear();
        returnValue = null;
    }

    public MessageParameter getParameterByName(String name) {
        Iterator iterator = parameters.iterator();
        MessageParameter param;

        while (iterator.hasNext()) {
            param = (MessageParameter) iterator.next();

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

    public String toString() {
        String text = "";

        text += getRank() + (isIterative() ? "*" : "") + ": ";

        if ((returnValue != null) && !returnValue.equals("")) {
            text += returnValue.toString() + " := ";
        }

        if ((getName() != null) && !getName().equals("")) {
            text += getName();
            text += "(";
            text += getParametersString();
            text += ")";
        }

        return text;
    }

    public String getReturnValueAsString() {
        if (returnValue != null && !returnValue.equals("")) {
            return returnValue.getName();
        } else {
            return "VOID";
        }
    }

    public String getParametersString() {
        String parametersString = "";
        Iterator iterator = parameters.iterator();
        MessageParameter parameter;
        int i = 0;    // keeps track if it is the first iteration

        while (iterator.hasNext()) {
            parameter = (MessageParameter) iterator.next();

            if (i == 0) {
                parametersString += parameter.toString();
            } else {
                parametersString = parametersString + ", " + parameter.toString();
            }

            i++;
        }

        return parametersString;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
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

    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("name", getName());
        node.setAttribute("iterative", Boolean.toString(isIterative()));

        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        if (returnValue != null) {
            node.setAttribute("returns", returnValue.getName());
        } else {
            node.setAttribute("returns", "");
        }

        streamer.streamObject(node, "operation", genericOperation);
        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());
    }

    public CallMessage clone() {
        CallMessage copyCallMessage = new CallMessage(getSource(), getTarget(), new GenericOperation(this.getName()));
        copyCallMessage.setIterative(this.isIterative());

        Iterator iterator = parameters.iterator();
        MessageParameter parameter;
        while (iterator.hasNext()) {
            parameter = (MessageParameter) iterator.next();
            copyCallMessage.addParameter(parameter.clone());
        }

        MessageReturnValue returnVal = this.getReturnValue();
        if (returnVal != null) {
            copyCallMessage.setReturnValue(new MessageReturnValue(this.getReturnValue().getName()));
        }

        return copyCallMessage;
    }
    //new method to return MethodParameter and not MessageParameter
    public Vector getSDMethodParameters() {
        Iterator iterator = parameters.iterator();
        MessageParameter param;
        Vector methodParameters = new Vector<MethodParameter>();

        while (iterator.hasNext()) {
            param = (MessageParameter) iterator.next();
            String [] parameterStr = param.getName().split("\\s+");
            try {
            	Type parameterType = new DataType (parameterStr[0]);
	    		String parameter = parameterStr[1];
	    		methodParameters.add(new MethodParameter(parameter,parameterType));
            }catch(ArrayIndexOutOfBoundsException e) {
            	out.println("Wrong Parameter");
            } 
        }
        return methodParameters;
    }
}
