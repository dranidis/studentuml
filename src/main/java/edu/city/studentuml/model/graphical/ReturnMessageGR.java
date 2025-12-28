package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

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
        double angle = forward ? 0 : -Math.PI;
        // GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
        GraphicsHelper.drawBlackArrowHead(x, y, angle, g);
    }

    @Override
    public int getEndingX() {
        int endingX = super.getEndingX();
        boolean forward = endingX > getStartingX();
        int plusBarWidth = forward ? -barWidth / 2 : barWidth / 2;
        return endingX + (target.acticationAtY(getY()) - 1) * barWidth / 2 + plusBarWidth;
    }

    @Override
    public void draw(Graphics2D g) {

        if (Settings.showReturnArrows()) {
            super.draw(g);
            return;
        }

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeSolidStroke());
            g.setPaint(getOutlineColor());
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
        streamer.streamObject(node, XMLSyntax.MESSAGE, getReturnMessage());
    }

    @Override
    public boolean isReflective() {
        return message.isReflective();
    }

    @Override
    public ReturnMessageGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Messages connect graphical elements, so we reference the same endpoints
        RoleClassifierGR sameFrom = (RoleClassifierGR) getSource();
        RoleClassifierGR sameTo = (RoleClassifierGR) getTarget();
        ReturnMessage sameMessage = (ReturnMessage) getMessage();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        ReturnMessageGR clonedGR = new ReturnMessageGR(sameFrom, sameTo, sameMessage, this.getY());

        return clonedGR;
    }
}
