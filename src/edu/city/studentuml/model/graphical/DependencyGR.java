package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DependencyGR.java
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.w3c.dom.Element;

public class DependencyGR extends LinkGR implements IXMLCustomStreamable {

    // the graphical classes that the dependency line connects in the diagram
    private ClassGR classA;
    private ClassGR classB;
    private Dependency dependency;

    protected ClassifierGR getClassifierA() {
        return this.classA;
    }

    protected ClassifierGR getClassifierB() {
        return this.classB;
    }

    public DependencyGR(ClassGR a, ClassGR b, Dependency dep) {
        classA = a;
        classB = b;
        dependency = dep;
        outlineColor = Color.black;
        highlightColor = Color.blue;
    }

    public int getTopLeftXA() {
        return (int) classA.getStartingPoint().getX();
    }

    public int getTopLeftXB() {
        return (int) classB.getStartingPoint().getX();
    }

    public int getTopLeftYA() {
        return (int) classA.getStartingPoint().getY();
    }

    public int getTopLeftYB() {
        return (int) classB.getStartingPoint().getY();
    }

    public int getWidthA() {
        return classA.getWidth();
    }

    public int getWidthB() {
        return classB.getWidth();
    }

    public int getHeightA() {
        return classA.getHeight();
    }

    public int getHeightB() {
        return classB.getHeight();
    }

    public void draw(Graphics2D g) {
        classA.refreshDimensions(g);
        classB.refreshDimensions(g);

        super.draw(g);

        int xA = getXA();
        int yA = getYA();
        int xB = getXB();
        int yB = getYB();

        Stroke originalStroke = g.getStroke();

        // the pattern of dashes for drawing the dependency line
        float dashes[] = {8};
        if (isSelected()) {
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0));
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

    public ClassGR getClassA() {
        return classA;
    }

    public ClassGR getClassB() {
        return classB;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("classa", SystemWideObjectNamePool.getInstance().getNameForObject(classA));
        node.setAttribute("classb", SystemWideObjectNamePool.getInstance().getNameForObject(classB));

        streamer.streamObject(node, "dependency", dependency);
    }
}
