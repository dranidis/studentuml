package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * @author Ervin Ramollari
 */
@JsonIncludeProperties({ "internalid", "from", "to", "realization" })
public class RealizationGR extends LinkGR {

    // the graphical class and interface that the dependency line connects in the
    // diagram
    private Realization realization;

    public RealizationGR(ClassGR c, InterfaceGR i, Realization real) {
        super(c, i);
        realization = real;
    }

    public RealizationGR(ClassGR c, InterfaceGR i) {
        this(c, i, new Realization (c.getDesignClass(), i.getInterface()));
    }

    @Override
    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
        drawRealizationArrowHead(bX, bY, rotationAngle, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    public void drawRealizationArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawWhiteArrowHead(x, y, angle, g);
    }

    // realizations cannot be reflective
    public boolean isReflective() {
        return false;
    }

    public Realization getRealization() {
        return realization;
    }

    @JsonProperty("from")
    public ClassGR getTheClass() {
        return (ClassGR) a;
    }

    @JsonProperty("to")
    public InterfaceGR getTheInterface() {
        return (InterfaceGR) b;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {

        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute("interfaceb", SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "realization", realization);
    }

    public void setRealization(Realization realization) {
        this.realization = realization;
    }

    @Override
    public RealizationGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        ClassGR sameClass = getTheClass();
        InterfaceGR sameInterface = getTheInterface();
        Realization sameRealization = getRealization();
        
        // Create new graphical wrapper referencing the SAME domain object and endpoints
        RealizationGR clonedGR = new RealizationGR(sameClass, sameInterface, sameRealization);
        
        return clonedGR;
    }

}
