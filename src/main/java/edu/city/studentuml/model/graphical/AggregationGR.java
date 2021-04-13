package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;

import org.w3c.dom.Element;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AggregationGR.java
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class AggregationGR extends AssociationGR {

    public AggregationGR(ClassifierGR w, ClassifierGR p, Aggregation aggreg) {
        super(w, p, aggreg);

        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    // OVERRIDE drawArrowHeads of AssociationGR
    public void drawArrowHeads(Graphics2D g) {
        drawAggregationArrowHead(getXA(), getYA(), getAggregation().isStrong(), getAngleRoleB(), g);
        // drawAssociationArrowHead(getXB(), getYB(), getAngleRoleA(), g);
        super.drawArrowHeads(g);
    }

    public void drawArrowHeadsReflective(Graphics2D g) {
        drawAggregationArrowHead(getXA(), getYA(), getAggregation().isStrong(), Math.PI / 2, g);
        super.drawArrowHeadsReflective(g);
    }

    public void drawAggregationArrowHead(int x, int y, boolean isStrong, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath diamond = new GeneralPath();

        diamond.moveTo(0, 0);
        diamond.lineTo(-8, -4);
        diamond.lineTo(-16, 0);
        diamond.lineTo(-8, 4);
        diamond.closePath();

        Paint originalPaint = g.getPaint();

        if (!isStrong) {
            g.setPaint(Color.white);
        } else {
            g.setPaint(originalPaint);
        }

        g.fill(diamond);
        g.setPaint(originalPaint);
        g.draw(diamond);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    // public void drawAssociationArrowHead(int x, int y, double angle, Graphics2D
    // g) {
    // g.translate(x, y);
    // g.rotate(angle);
    // g.drawLine(-8, 4, 0, 0);
    // g.drawLine(-8, -4, 0, 0);
    // g.rotate(-angle);
    // g.translate(-x, -y);
    // }

    public Aggregation getAggregation() {
        return (Aggregation) getAssociation();
    }

    public boolean isReflective() {
        return getAggregation().isReflective();
    }

    public AbstractClassGR getWhole() {
        return getClassA();
    }

    public AbstractClassGR getPart() {
        return getClassB();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        super.streamFromXML(node, streamer, instance);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("classa", SystemWideObjectNamePool.getInstance().getNameForObject(getWhole()));
        node.setAttribute("classb", SystemWideObjectNamePool.getInstance().getNameForObject(getPart()));

        streamer.streamObject(node, "aggregation", getAggregation());
    }
}
