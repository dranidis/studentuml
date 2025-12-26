package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(RealizationGR.class.getName());

    // the graphical class and interface that the dependency line connects in the
    // diagram
    private Realization realization;

    public RealizationGR(ClassGR c, InterfaceGR i, Realization real) {
        super(c, i);
        realization = real;
    }

    public RealizationGR(ClassGR c, InterfaceGR i) {
        this(c, i, new Realization(c.getDesignClass(), i.getInterface()));
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

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // Realization: source must be a class, target must be an interface
        if (endpoint == EndpointType.SOURCE) {
            // Source must be a class
            if (!(newElement instanceof ClassGR)) {
                logger.fine(() -> "Cannot reconnect realization source: target is not a ClassGR");
                return false;
            }
        } else if (endpoint == EndpointType.TARGET && !(newElement instanceof InterfaceGR)) {
            // Target must be an interface
            logger.fine(() -> "Cannot reconnect realization target: target is not an InterfaceGR");
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        if (!(newSource instanceof ClassGR)) {
            return false;
        }

        ClassGR newClass = (ClassGR) newSource;

        // Create a new Realization with the new source
        this.realization = new Realization(newClass.getDesignClass(), realization.getTheInterface());

        logger.fine(() -> "Prepared realization source reconnection to: " + newClass.getDesignClass().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        if (!(newTarget instanceof InterfaceGR)) {
            return false;
        }

        InterfaceGR newInterface = (InterfaceGR) newTarget;

        // Create a new Realization with the new target
        this.realization = new Realization(realization.getTheClass(), newInterface.getInterface());

        logger.fine(() -> "Prepared realization target reconnection to: " + newInterface.getInterface().getName());
        return true;
    }

    /**
     * Creates a new RealizationGR with updated endpoints. Used for reconnection
     * since LinkGR endpoints are final.
     * 
     * @param newClass     the new source class
     * @param newInterface the new target interface
     * @return new RealizationGR with same domain model but new endpoints
     */
    public RealizationGR createWithNewEndpoints(ClassGR newClass, InterfaceGR newInterface) {
        return new RealizationGR(newClass, newInterface, this.realization);
    }
}
