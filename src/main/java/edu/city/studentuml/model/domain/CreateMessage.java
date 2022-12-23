package edu.city.studentuml.model.domain;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class CreateMessage extends CallMessage {

    private static final Logger logger = Logger.getLogger(CreateMessage.class.getName());

    public CreateMessage(RoleClassifier from, RoleClassifier to) {
        super(from, to, new GenericOperation("create"));
    }

    @Override
    public String toString() {
        return getRank() + ": " + "create" + getParametersString();
    }

    @Override
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

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        streamer.streamObjects(streamer.addChild(node, "parameters"), parameters.iterator());

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
        MethodParameter parameter;
        while (iterator.hasNext()) {
            parameter = (MethodParameter) iterator.next();
            copyCreateMessage.addParameter(parameter.clone());
        }

        return copyCreateMessage;
    }

}
