package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class AggregationGR extends AssociationGR {

    public AggregationGR(ClassifierGR w, ClassifierGR p, Aggregation aggreg) {
        super(w, p, aggreg);
    }

    @Override
    protected void drawArrowHeads(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        drawAggregationArrowHead(aX, aY, getAggregation().isStrong(), angleB, g);
        super.drawArrowHeads(aX, aY, bX, bY, angleA, angleB, g);
    }

    @Override
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

    public Aggregation getAggregation() {
        return (Aggregation) getAssociation();
    }

    @Override
    public boolean isReflective() {
        return getAggregation().isReflective();
    }

    public ClassifierGR getWhole() {
        return getClassA();
    }

    public ClassifierGR getPart() {
        return getClassB();
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute(ObjectFactory.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(getWhole()));
        node.setAttribute(ObjectFactory.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(getPart()));

        streamer.streamObject(node, "aggregation", getAggregation());
    }

    @Override
    public String toString() {
        return "" + a + " ---aggregation---> " + b;
    }    
}
