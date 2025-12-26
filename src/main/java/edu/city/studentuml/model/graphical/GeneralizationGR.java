package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "internalid", "from", "to", "generalization" })
public class GeneralizationGR extends LinkGR {

    private static final Logger logger = Logger.getLogger(GeneralizationGR.class.getName());

    private Generalization generalization;
    // the graphical classes that the generalization line connects in the diagram

    public GeneralizationGR(ClassifierGR parent, ClassifierGR child, Generalization gener) {
        super(child, parent);
        generalization = gener;
    }

    public GeneralizationGR(ClassifierGR parent, ClassifierGR child) {
        this(parent, child, new Generalization(parent.getClassifier(), child.getClassifier()));
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
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
    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
        GraphicsHelper.drawWhiteArrowHead(bX, bY, rotationAngle, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeSolidStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedSolidStroke();
    }

    // generalizations cannot be reflective
    @Override
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

    @Override
    public GeneralizationGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        ClassifierGR sameParent = getSuperClass();
        ClassifierGR sameChild = getBaseClass();
        Generalization sameGeneralization = getGeneralization();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        GeneralizationGR clonedGR = new GeneralizationGR(sameParent, sameChild, sameGeneralization);

        return clonedGR;
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // Generalizations must be between same types
        // Classes can only inherit from classes, interfaces from interfaces
        ClassifierGR currentSource = getBaseClass(); // child
        ClassifierGR currentTarget = getSuperClass(); // parent

        if (endpoint == EndpointType.SOURCE) {
            // Changing child - must match type of parent
            if (currentTarget instanceof AbstractClassGR && !(newElement instanceof AbstractClassGR)) {
                logger.fine(() -> "Cannot reconnect: class can only inherit from class");
                return false;
            }
            if (currentTarget instanceof InterfaceGR && !(newElement instanceof InterfaceGR)) {
                logger.fine(() -> "Cannot reconnect: interface can only extend interface");
                return false;
            }
        } else if (endpoint == EndpointType.TARGET) {
            // Changing parent - must match type of child
            if (currentSource instanceof AbstractClassGR && !(newElement instanceof AbstractClassGR)) {
                logger.fine(() -> "Cannot reconnect: class can only inherit from class");
                return false;
            }
            if (currentSource instanceof InterfaceGR && !(newElement instanceof InterfaceGR)) {
                logger.fine(() -> "Cannot reconnect: interface can only extend interface");
                return false;
            }
        }

        // Check for cycles - this is critical for inheritance!
        // We can't fully check here without the model, so we'll do a basic check
        // The full cycle check will happen in reconnectSource/reconnectTarget
        if (!(newElement instanceof ClassifierGR)) {
            return false;
        }

        ClassifierGR newClassifier = (ClassifierGR) newElement;

        // Prevent direct self-inheritance (A extends A)
        if (endpoint == EndpointType.SOURCE && newClassifier == currentTarget) {
            logger.fine(() -> "Cannot reconnect: would create self-inheritance");
            return false;
        }
        if (endpoint == EndpointType.TARGET && newClassifier == currentSource) {
            logger.fine(() -> "Cannot reconnect: would create self-inheritance");
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        // newSource is the new child, b (getSuperClass()) is the parent

        // First check if this reconnection is allowed
        if (!canReconnect(EndpointType.SOURCE, (GraphicalElement) newSource)) {
            logger.fine(() -> "Cannot reconnect source: validation failed");
            return false;
        }

        // Verify type compatibility
        ClassifierGR parent = getSuperClass();
        if (parent instanceof ClassGR && !(newSource instanceof ClassGR)) {
            return false;
        }
        if (parent instanceof InterfaceGR && !(newSource instanceof InterfaceGR)) {
            return false;
        }

        // Create a new Generalization with the new child
        this.generalization = new Generalization(parent.getClassifier(), newSource.getClassifier());

        logger.fine(() -> "Prepared generalization source reconnection to: " + newSource.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        // newTarget is the new parent, a (getBaseClass()) is the child

        // First check if this reconnection is allowed
        if (!canReconnect(EndpointType.TARGET, (GraphicalElement) newTarget)) {
            logger.fine(() -> "Cannot reconnect target: validation failed");
            return false;
        }

        // Verify type compatibility
        ClassifierGR child = getBaseClass();
        if (child instanceof ClassGR && !(newTarget instanceof ClassGR)) {
            return false;
        }
        if (child instanceof InterfaceGR && !(newTarget instanceof InterfaceGR)) {
            return false;
        }

        // Create a new Generalization with the new parent
        this.generalization = new Generalization(newTarget.getClassifier(), child.getClassifier());

        logger.fine(() -> "Prepared generalization target reconnection to: " + newTarget.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new GeneralizationGR with updated endpoints. Used for reconnection
     * since LinkGR endpoints are final.
     * 
     * @param newSource the new source (child/baseclass)
     * @param newTarget the new target (parent/superclass)
     * @return new GeneralizationGR with same domain model but new endpoints
     */
    public GeneralizationGR createWithNewEndpoints(ClassifierGR newSource, ClassifierGR newTarget) {
        // Constructor expects (parent, child), but we receive (child, parent)
        return new GeneralizationGR(newTarget, newSource, this.generalization);
    }

}
