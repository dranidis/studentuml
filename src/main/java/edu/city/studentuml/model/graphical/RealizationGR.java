package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import org.w3c.dom.Element;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//RealizationGR.java
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class RealizationGR extends LinkGR {

    // the graphical class and interface that the dependency line connects in the
    // diagram
    private ClassGR classGR;
    private InterfaceGR interfaceGR;
    private Realization realization;

    protected ClassifierGR getClassifierA() {
        return this.classGR;
    }

    protected ClassifierGR getClassifierB() {
        return this.interfaceGR;
    }

    public RealizationGR(ClassGR c, InterfaceGR i, Realization real) {
        super(c, i);
        classGR = c;
        interfaceGR = i;
        realization = real;
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    public int getTopLeftXA() {
        return (int) classGR.getStartingPoint().getX();
    }

    public int getTopLeftXB() {
        return (int) interfaceGR.getStartingPoint().getX();
    }

    public int getTopLeftYA() {
        return (int) classGR.getStartingPoint().getY();
    }

    public int getTopLeftYB() {
        return (int) interfaceGR.getStartingPoint().getY();
    }

    public int getWidthA() {
        return classGR.getWidth();
    }

    public int getWidthB() {
        return interfaceGR.getWidth();
    }

    public int getHeightA() {
        return classGR.getHeight();
    }

    public int getHeightB() {
        return interfaceGR.getHeight();
    }

    public void draw(Graphics2D g) {
        classGR.refreshDimensions(g);
        interfaceGR.refreshDimensions(g);

        super.draw(g);

        int classX = getXA();
        int classY = getYA();
        int interfaceX = getXB();
        int interfaceY = getYB();

        Stroke originalStroke = g.getStroke();

        // the pattern of dashes for drawing the realization line
        float dashes[] = { 8 };
        if (isSelected()) {
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0));
            g.setPaint(outlineColor);
        }

        g.drawLine(classX, classY, interfaceX, interfaceY);

        // restore the original stroke
        g.setStroke(originalStroke);

        double rotationAngle = getAngleRoleA();
        drawRealizationArrowHead(interfaceX, interfaceY, rotationAngle, g);
    }

    public void drawRealizationArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath triangle = new GeneralPath();

        triangle.moveTo(0, 0);
        triangle.lineTo(-8, -4);
        triangle.lineTo(-8, 4);
        triangle.closePath();

        Paint originalPaint = g.getPaint();

        g.setPaint(Color.white);
        g.fill(triangle);
        g.setPaint(originalPaint);
        g.draw(triangle);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    // realizations cannot be reflective
    public boolean isReflective() {
        return false;
    }

    public Realization getRealization() {
        return realization;
    }

    public ClassGR getTheClass() {
        return classGR;
    }

    public InterfaceGR getTheInterface() {
        return interfaceGR;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub

        node.setAttribute("classa", SystemWideObjectNamePool.getInstance().getNameForObject(classGR));
        node.setAttribute("interfaceb", SystemWideObjectNamePool.getInstance().getNameForObject(interfaceGR));

        streamer.streamObject(node, "realization", realization);
    }
}
