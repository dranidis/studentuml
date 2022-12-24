package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "internalid", "from", "to", "generalization" })
public class GeneralizationGR extends LinkGR {
    private Generalization generalization;
    // the graphical classes that the generalization line connects in the diagram
    @JsonProperty("to")
    private ClassifierGR superClass;
    @JsonProperty("from")
    private ClassifierGR baseClass;

    public GeneralizationGR(ClassifierGR parent, ClassifierGR child, Generalization gener) {
        super(child, parent);
        outlineColor = Color.black;
        highlightColor = Color.blue;
        superClass = parent;
        baseClass = child;
        generalization = gener;
    }


    protected ClassifierGR getClassifierA() {
        return this.baseClass;
    }

    protected ClassifierGR getClassifierB() {
        return this.superClass;
    }

    public Generalization getGeneralization() {
        return generalization;
    }

    public ClassifierGR getSuperClass() {
        return superClass;
    }

    public ClassifierGR getBaseClass() {
        return baseClass;
    }

    public int getTopLeftXA() {
        return (int) baseClass.getStartingPoint().getX();
    }

    public int getTopLeftXB() {
        return (int) superClass.getStartingPoint().getX();
    }

    public int getTopLeftYA() {
        return (int) baseClass.getStartingPoint().getY();
    }

    public int getTopLeftYB() {
        return (int) superClass.getStartingPoint().getY();
    }

    public int getWidthA() {
        return baseClass.getWidth();
    }

    public int getWidthB() {
        return superClass.getWidth();
    }

    public int getHeightA() {
        return baseClass.getHeight();
    }

    public int getHeightB() {
        return superClass.getHeight();
    }

    @Override
    public void draw(Graphics2D g) {
        baseClass.refreshDimensions(g);
        superClass.refreshDimensions(g);
        super.draw(g);

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

        GeneralPath triangle = new GeneralPath();

        triangle.moveTo(0, 0);
        triangle.lineTo(-10, -5);
        triangle.lineTo(-10, 5);
        triangle.closePath();

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
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("base", SystemWideObjectNamePool.getInstance().getNameForObject(baseClass));
        node.setAttribute("super", SystemWideObjectNamePool.getInstance().getNameForObject(superClass));

        streamer.streamObject(node, "generalization", generalization);
    }
}
