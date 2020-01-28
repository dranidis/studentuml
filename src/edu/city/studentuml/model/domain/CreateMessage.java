package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CreateMessage.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;


import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

public class CreateMessage extends SDMessage implements IXMLCustomStreamable {

    Logger logger = Logger.getLogger(CreateMessage.class.getName());

    private NotifierVector parameters;

    public CreateMessage(RoleClassifier from, RoleClassifier to) {
        super(from, to);
        parameters = new NotifierVector();
    }

    public String toString() {
        String text = "";
//        text += getRank() + ": ";
        text += "create(" + getParametersString() + ")";
        return text;
    }

    public void streamFromXML(Element node, XMLStreamer streamer,
            Object instance) {
        parameters.clear();
        try {
            streamer.streamObjectsFrom(streamer.getNodeById(node, "parameters"), parameters, this);
        } catch (Exception e) {
            logger.severe("No parameters");
            e.printStackTrace();
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());

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

    public Vector getSDMethodParameters() {
        Iterator iterator = parameters.iterator();
        MessageParameter param;
        Vector methodParameters = new Vector<MethodParameter>();

        while (iterator.hasNext()) {
            param = (MessageParameter) iterator.next();
            String[] parameterStr = param.getName().split("\\s+");
            try {
                Type parameterType = new DataType(parameterStr[0]);
                String parameter = parameterStr[1];
                methodParameters.add(new MethodParameter(parameter, parameterType));
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.severe("Wrong Parameter");
                e.printStackTrace();
            }
        }
        return methodParameters;
    }

    public CreateMessage clone() {
        CreateMessage copyCreateMessage = new CreateMessage(getSource(), getTarget());

        Iterator iterator = parameters.iterator();
        MessageParameter parameter;
        while (iterator.hasNext()) {
            parameter = (MessageParameter) iterator.next();
            copyCreateMessage.addParameter(parameter.clone());
        }

        return copyCreateMessage;
    }

}
