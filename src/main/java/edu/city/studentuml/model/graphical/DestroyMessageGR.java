package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * 
 * @author Ervin Ramollari
 */
public class DestroyMessageGR extends SDMessageGR {

    public DestroyMessageGR(RoleClassifierGR from, RoleClassifierGR to, DestroyMessage message, int y) {
        super(from, to, message, y);
        refreshTargetPosition();
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
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);

        g.drawLine(x - 15, y - 20, x + 15, y + 20);
        g.drawLine(x - 15, y + 20, x + 15, y - 20);
    }

    // override superclass move(), so that the target role classifier also moves
    @Override
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

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // emtpy
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, XMLSyntax.MESSAGE, getDestroyMessage());
    }

    @Override
    public boolean isReflective() {
        return false;
    }
    
}
