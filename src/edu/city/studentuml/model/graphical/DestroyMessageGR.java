package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DestroyMessageGR.java
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

public class DestroyMessageGR extends SDMessageGR implements IXMLCustomStreamable {

    public DestroyMessageGR(RoleClassifierGR from, RoleClassifierGR to, DestroyMessage message, int y) {
        super(from, to, message, y);
        refreshTargetPosition();
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

    // override superclass move(), so that the target role classifier also moves
    public void move(int x, int y) {

        // move the message to the given vertical position
        startingPoint.setLocation(startingPoint.getX(), y);

        // refresh the target's lifeline ending point
        refreshTargetPosition();
    }

    public void refreshTargetPosition() {
        getTarget().setEndingY(getY());
    }

    public DestroyMessage getDestroyMessage() {
        return (DestroyMessage) getMessage();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, "message", getDestroyMessage());
    }
}
