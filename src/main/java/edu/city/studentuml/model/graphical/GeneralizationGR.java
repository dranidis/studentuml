package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "internalid", "from", "to", "generalization" })
public class GeneralizationGR extends LinkGR {
    private Generalization generalization;
    // the graphical classes that the generalization line connects in the diagram

    public GeneralizationGR(ClassifierGR parent, ClassifierGR child, Generalization gener) {
        super(child, parent);
        outlineColor = Color.black;
        highlightColor = Color.blue;
        generalization = gener;
    }

    public Generalization getGeneralization() {
        return generalization;
    }

    @JsonProperty("to")
    public ClassifierGR getSuperClass() {
        return b;
    }

    @JsonProperty("from")
    public ClassifierGR getBaseClass() {
        return a;
    }

    @Override
    public void draw(Graphics2D g) {
        a.refreshDimensions(g);
        b.refreshDimensions(g);

        int baseX = getXA();
        int baseY = getYA();
        int superX = getXB();
        int superY = getYB();

        Stroke originalStroke = g.getStroke();

        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        g.drawLine(baseX, baseY, superX, superY);

        g.setStroke(originalStroke);

        double rotationAngle = getAngleRoleA();
        drawGeneralizationArrowHead(superX, superY, rotationAngle, g);
    }

    public void drawGeneralizationArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath triangle = new Triangle().get();

        Paint originalPaint = g.getPaint();

        g.setPaint(Color.white);
        g.fill(triangle);
        g.setPaint(originalPaint);
        g.draw(triangle);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    // generalizations cannot be reflective
    public boolean isReflective() {
        return false;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("base", SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute("super", SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "generalization", generalization);
    }

    @Override
    public String toString() {
        return "" + a + " ---generalization---> " + b;
    }

    public void setGeneralization(Generalization generalization) {
        this.generalization = generalization;
    }      
    
}
