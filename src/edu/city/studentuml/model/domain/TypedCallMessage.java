package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author dimitris
 */
public class TypedCallMessage extends SDMessage implements IXMLCustomStreamable {
    
    Logger logger = Logger.getLogger(TypedCallMessage.class.getName());

    private GenericOperation genericOperation;
    private boolean iterative;
    private NotifierVector<MethodParameter> parameters;
    private MessageReturnValue returnValue;
    private Type returnType;

    public TypedCallMessage(RoleClassifier from, RoleClassifier to, GenericOperation go) {
        super(from, to);
        genericOperation = go;
        iterative = false;
        parameters = new NotifierVector();
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
        returnType = null;
    }

    public MethodParameter getParameterByName(String name) {
        for(MethodParameter param: parameters) {
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

        if (returnType != null) {
            node.setAttribute("returnType", returnType.getName());
        } else {
            node.setAttribute("returnType", "");
        }

        streamer.streamObject(node, "operation", genericOperation);
        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());
    }

    public TypedCallMessage clone() {
        TypedCallMessage copyCallMessage = new TypedCallMessage(getSource(), getTarget(), new GenericOperation(this.getName()));
        copyCallMessage.setIterative(this.isIterative());
        
        for(MethodParameter p: parameters) 
            copyCallMessage.addParameter(p.clone());

        MessageReturnValue returnVal = this.getReturnValue();
        if (returnVal != null) {
            copyCallMessage.setReturnValue(new MessageReturnValue(this.getReturnValue().getName()));
        }

        copyCallMessage.setReturnType(this.getReturnType());

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
            	logger.info("Wrong Parameter");
                e.printStackTrace();
            } 
        }
        return methodParameters;
    }
}
