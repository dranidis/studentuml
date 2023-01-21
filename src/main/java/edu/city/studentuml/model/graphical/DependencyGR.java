package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

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
    }

    @Override
    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
        drawDependencyArrowHead(bX, bY, rotationAngle, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    public void drawDependencyArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
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
        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "dependency", dependency);
    }
}
