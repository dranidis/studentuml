package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ReturnMessageGR.java
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

public class ReturnMessageGR extends SDMessageGR implements IXMLCustomStreamable {

    public ReturnMessageGR(RoleClassifierGR from, RoleClassifierGR to, ReturnMessage message, int y) {
        super(from, to, message, y);
    }

    public Stroke getStroke() {
        float dashes[] = {8};    // the pattern of dashes for drawing the return line

        return new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0);
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

    public ReturnMessage getReturnMessage() {
        return (ReturnMessage) getMessage();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, "message", getReturnMessage());
    }
}
