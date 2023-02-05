package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 * 
 * @author Ervin Ramollari
 */
public class MultiObjectGR extends AbstractSDObjectGR {

    private static final double FRONT_BACK_DISTANCE = 6.0;

    public MultiObjectGR(MultiObject multiObject, int x) {
        super(multiObject, x);
    }

    @Override
    public boolean contains(Point2D point) {
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + FRONT_BACK_DISTANCE, getY() - FRONT_BACK_DISTANCE, width, height);
        return super.contains(point) || rectangle2.contains(point);
    }

    @Override
    protected void drawObjectShape(Graphics2D g, int startingX, int startingY) {
        Shape frontBox = new Rectangle2D.Double(startingX, startingY, width, height);
        Shape backBox = new Rectangle2D.Double(startingX + FRONT_BACK_DISTANCE, startingY - FRONT_BACK_DISTANCE, width, height);

        Paint originalPaint = g.getPaint();

        // draw the back box
        g.setPaint(getFillColor());
        g.fill(backBox);
        g.setPaint(originalPaint);
        g.draw(backBox);

        // draw the front box
        g.setPaint(getFillColor());
        g.fill(frontBox);
        g.setPaint(originalPaint);
        g.draw(frontBox);

    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public MultiObject getMultiObject() {
        return (MultiObject) roleClassifier;
    }

    public void setMultiObject(MultiObject mo) {
        roleClassifier = mo;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "multiobject", getMultiObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }
}
