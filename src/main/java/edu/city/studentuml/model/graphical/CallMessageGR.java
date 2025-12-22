package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

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
    protected Stroke makeSelectedMessageStroke() {
        return GraphicsHelper.makeSelectedSolidStroke();
    }

    @Override
    protected void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        double angle = forward ? 0 : -Math.PI;
        GraphicsHelper.drawBlackArrowHead(x, y, angle, g);
    }

    @Override
    public int getEndingX() {
        int endingX = super.getEndingX();
        boolean forward = (endingX > getStartingX());
        int plusBarWidth = forward ? - barWidth / 2 : barWidth / 2;
        return endingX + (target.acticationAtY(getY()) - 1) * barWidth/2 + plusBarWidth;
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
        streamer.streamObject(node, XMLSyntax.MESSAGE, getCallMessage());
    }

    @Override
    public boolean isReflective() {
        return message.isReflective();
    }

    @Override
    public CallMessageGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Messages connect graphical elements, so we reference the same endpoints
        RoleClassifierGR sameFrom = (RoleClassifierGR) getSource();
        RoleClassifierGR sameTo = (RoleClassifierGR) getTarget();
        CallMessage sameMessage = getCallMessage();
        
        // Create new graphical wrapper referencing the SAME domain object and endpoints
        CallMessageGR clonedGR = new CallMessageGR(sameFrom, sameTo, sameMessage, this.getY());
        
        return clonedGR;
    }
    
}
