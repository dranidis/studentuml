package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CallMessageGR.java
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class CallMessageGR extends SDMessageGR {

    public CallMessageGR(RoleClassifierGR from, RoleClassifierGR to, CallMessage message, int y) {
        super(from, to, message, y);
    }

    public Stroke getStroke() {
        return new BasicStroke();
    }

    public void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        if (forward) {
            g.drawLine(x, y, x - 8, y - 4);
            g.drawLine(x, y, x - 8, y + 4);
        } else {
            g.drawLine(x, y, x + 8, y - 4);
            g.drawLine(x, y, x + 8, y + 4);
        }
    }

    public CallMessage getCallMessage() {
        return (CallMessage) getMessage();
    }


    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }


    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        node.setAttribute("y", Integer.toString(getY()));

        streamer.streamObject(node, "message", getCallMessage());
    }
}
