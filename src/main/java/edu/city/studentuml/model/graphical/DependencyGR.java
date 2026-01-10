package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;
import edu.city.studentuml.util.undoredo.EditDependencyEdit;

/**
 * @author Ervin Ramollari
 */
@JsonIncludeProperties({ "internalid", "from", "to", "dependency" })
public class DependencyGR extends LinkGR {

    private static final Logger logger = Logger.getLogger(DependencyGR.class.getName());

    // the graphical classes that the dependency line connects in the diagram
    private Dependency dependency;

    public DependencyGR(ClassifierGR a, ClassifierGR b, Dependency dep) {
        super(a, b);
        dependency = dep;
    }

    public DependencyGR(ClassifierGR a, ClassifierGR b) {
        this(a, b, new Dependency(a.getClassifier(), b.getClassifier()));
    }

    @Override
    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(bX, bY, rotationAngle, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeDashedStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedDashedStroke();
    }

    // dependency cannot be reflective
    @Override
    public boolean isReflective() {
        return false;
    }

    @Override
    protected void drawStereoType(int aX, int aY, int bX, int bY, double rotationAngle, Graphics2D g) {
        String stereotype = dependency.getStereotype();
        if (stereotype != null && !stereotype.isEmpty()) {
            String label = "«" + stereotype + "»";
            int midX = (aX + bX) / 2;
            int midY = (aY + bY) / 2;
            GraphicsHelper.drawString(label, midX, midY, rotationAngle, false, g);
        }
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public Dependency getDependency() {
        return dependency;
    }

    @JsonProperty("from")
    public ClassifierGR getClassA() {
        return a;
    }

    @JsonProperty("to")
    public ClassifierGR getClassB() {
        return b;
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

    @Override
    public DependencyGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        ClassifierGR sameA = getClassA();
        ClassifierGR sameB = getClassB();
        Dependency sameDependency = getDependency();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        DependencyGR clonedGR = new DependencyGR(sameA, sameB, sameDependency);

        return clonedGR;
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // Dependencies can connect any classifier (classes or interfaces)
        if (!(newElement instanceof ClassifierGR)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        if (!(newSource instanceof ClassifierGR)) {
            return false;
        }

        // Create a new Dependency with the new source
        this.dependency = new Dependency(newSource.getClassifier(), dependency.getTo());

        logger.fine(() -> "Prepared dependency source reconnection to: " + newSource.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        if (!(newTarget instanceof ClassifierGR)) {
            return false;
        }

        // Create a new Dependency with the new target
        this.dependency = new Dependency(dependency.getFrom(), newTarget.getClassifier());

        logger.fine(() -> "Prepared dependency target reconnection to: " + newTarget.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new DependencyGR with updated endpoints. Used for reconnection
     * since LinkGR endpoints are final.
     * 
     * @param newA the new source classifier
     * @param newB the new target classifier
     * @return new DependencyGR with same domain model but new endpoints
     */
    public DependencyGR createWithNewEndpoints(ClassifierGR newA, ClassifierGR newB) {
        return new DependencyGR(newA, newB, this.dependency);
    }

    /**
     * Polymorphic edit method using the centralized helper to edit the dependency
     * stereotype with undo/redo support.
     */
    @Override
    public boolean edit(EditContext context) {
        Dependency dep = getDependency();

        return editStringPropertyWithDialog(
                context,
                "Dependency Editor",
                "Stereotype: ",
                dep,
                Dependency::getStereotype,
                Dependency::setStereotype,
                Dependency::clone,
                (original, newDomainObject, model) -> new EditDependencyEdit(original, original.getStereotype(),
                        newDomainObject.getStereotype(), model),
                null, // no duplicate check
                null); // no duplicate error message
    }
}
