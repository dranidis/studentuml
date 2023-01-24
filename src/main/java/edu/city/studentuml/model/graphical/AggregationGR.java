package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

public class AggregationGR extends AssociationGR {

    public AggregationGR(ClassifierGR w, ClassifierGR p, Aggregation aggreg) {
        super(w, p, aggreg);
    }

    @Override
    protected void drawArrowHeads(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        GraphicsHelper.drawAggregationArrowHead(aX, aY, getAggregation().isStrong(), angleB, g);
        super.drawArrowHeads(aX, aY, bX, bY, angleA, angleB, g);
    }

    @Override
    public void drawArrowHeadsReflective(Graphics2D g) {
        GraphicsHelper.drawAggregationArrowHead(getXA(), getYA(), getAggregation().isStrong(), Math.PI / 2, g);
        super.drawArrowHeadsReflective(g);
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
        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(getWhole()));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(getPart()));

        streamer.streamObject(node, "aggregation", getAggregation());
    }

    @Override
    public String toString() {
        return "" + a + " ---aggregation---> " + b;
    }    
}
