package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CreateMessageGR.java
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

public class CreateMessageGR extends SDMessageGR implements IXMLCustomStreamable {

    public CreateMessageGR(RoleClassifierGR from, RoleClassifierGR to, CreateMessage message, int y) {
        super(from, to, message, y);
        refreshTargetPosition();
    }

    // override superclass getEndingX, so that the arrow line ends in the
    // created object's name box
    public int getEndingX() {
        if (target.getX() > source.getX()) {
            return target.getX();
        } else {
            return target.getX() + target.getWidth();
        }
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
        startingPoint.setLocation(startingPoint.getX(), y);
        refreshTargetPosition();
    }

    public void refreshTargetPosition() {
        getTarget().setBeginningY(getY() - (getTarget().getHeight() / 2));
    }

    public CreateMessage getCreateMessage() {
        return (CreateMessage) getMessage();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, "message", getCreateMessage());
    }
}
