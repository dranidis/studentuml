package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 * @author Ervin Ramollari
 */
public class CallMessageGR extends SDMessageGR {

    public CallMessageGR(RoleClassifierGR from, RoleClassifierGR to, CallMessage message, int y) {
        super(from, to, message, y);
    }

    @Override
    protected Stroke makeMessageStroke() {
        return GraphicsHelper.makeSolidStroke();
    }

    @Override
    protected void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        if (forward) {
            GraphicsHelper.drawBlackArrowHead(x, y, 0, g);
        } else {
            GraphicsHelper.drawBlackArrowHead(x, y, -Math.PI, g);
        }
    }

    public CallMessage getCallMessage() {
        return (CallMessage) getMessage();
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));

        node.setAttribute("y", Integer.toString(getY()));

        streamer.streamObject(node, "message", getCallMessage());
    }

    @Override
    public boolean isReflective() {
        return message.isReflective();
    }
    
}
