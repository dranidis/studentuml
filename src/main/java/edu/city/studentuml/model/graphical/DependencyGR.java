package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 * @author Ervin Ramollari
 */
@JsonIncludeProperties({ "internalid", "from", "to", "dependency" })
public class DependencyGR extends LinkGR {

    // the graphical classes that the dependency line connects in the diagram
    private Dependency dependency;

    public DependencyGR(ClassGR a, ClassGR b, Dependency dep) {
        super(a, b);
        dependency = dep;
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    public void draw(Graphics2D g) {
        a.refreshDimensions(g);
        b.refreshDimensions(g);

        int xA = getXA();
        int yA = getYA();
        int xB = getXB();
        int yB = getYB();

        Stroke originalStroke = g.getStroke();

        // the pattern of dashes for drawing the dependency line
        
        if (isSelected()) {
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
            g.setPaint(outlineColor);
        }

        g.drawLine(xA, yA, xB, yB);

        // restore the original stroke
        g.setStroke(originalStroke);

        double rotationAngle = getAngleRoleA();
        drawDependencyArrowHead(xB, yB, rotationAngle, g);
    }

    public void drawDependencyArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);
        g.drawLine(-8, -4, 0, 0);
        g.drawLine(-8, 4, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    // dependency cannot be reflective
    public boolean isReflective() {
        return false;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @JsonProperty("from")
    public ClassGR getClassA() {
        return (ClassGR) a;
    }

    @JsonProperty("to")
    public ClassGR getClassB() {
        return (ClassGR) b;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("classa", SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute("classb", SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "dependency", dependency);
    }
}
