package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

public class CreateMessageGR extends CallMessageGR {

    public CreateMessageGR(RoleClassifierGR from, RoleClassifierGR to, CreateMessage message, int y) {
        super(from, to, message, y);
        refreshTargetPosition();
    }

    // override superclass getEndingX, so that the arrow line ends in the
    // created object's name box
    @Override
    public int getEndingX() {
        if (target.getX() > source.getX()) {
            return target.getX();
        } else {
            return target.getX() + target.getWidth();
        }
    }

    @Override
    protected Stroke makeMessageStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected Stroke makeSelectedMessageStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    @Override
    protected void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        double angle = forward ? 0 : -Math.PI;
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    // override superclass move(), so that the target role classifier also moves
    @Override
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

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, XMLSyntax.MESSAGE, getCreateMessage());
    }

    @Override
    public boolean isReflective() {
        return false;
    }

}
