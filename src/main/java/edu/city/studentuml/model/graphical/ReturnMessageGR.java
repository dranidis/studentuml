package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class ReturnMessageGR extends SDMessageGR {

    public ReturnMessageGR(RoleClassifierGR from, RoleClassifierGR to, ReturnMessage message, int y) {
        super(from, to, message, y);
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
        if (forward) {
            g.drawLine(x, y, x - 8, y - 4);
            g.drawLine(x, y, x - 8, y + 4);
        } else {
            g.drawLine(x, y, x + 8, y - 4);
            g.drawLine(x, y, x + 8, y + 4);
        }
    }

    @Override
    public void draw(Graphics2D g) {

        if (Settings.showReturnArrows()) {
            super.draw(g);
            return;
        }

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(5));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        int startingX = getStartingX();

        g.drawLine(startingX - barWidth / 2, getY(), startingX + barWidth / 2, getY());
        // restore the original stroke
        g.setStroke(originalStroke);
    }

    @Override
    public boolean contains(Point2D point) {

        if (Settings.showReturnArrows()) {
            return super.contains(point);
        }

        if (!getMessage().isReflective()) {
            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX() - 10.0, getY() - 10.0, 20, 20);

            return bounds.contains(point);
        } else {

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX(), getY(), 40, 15);

            return bounds.contains(point);
        }
    }

    public ReturnMessage getReturnMessage() {
        return (ReturnMessage) getMessage();
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
        streamer.streamObject(node, ObjectFactory.MESSAGE, getReturnMessage());
    }

    @Override
    public boolean isReflective() {
        return message.isReflective();
    }
}
