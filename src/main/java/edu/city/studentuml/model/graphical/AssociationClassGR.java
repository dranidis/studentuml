package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.util.Ray;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.Vector2D;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * @author draganbisercic
 */
public class AssociationClassGR extends LinkGR {

    private static final Logger logger = Logger.getLogger(AssociationClassGR.class.getName());

    private AbstractAssociationClass associationClass;
    private AssociationGR associationElement;
    private AbstractClassGR classElement;
    // the graphical classes that the association line connects in the diagram

    private Point associationCenterPoint;
    public static final int MINIMUM_DISTANCE = 30; // minimum distance from association to association class

    public AssociationClassGR(ClassifierGR a, ClassifierGR b, AbstractAssociationClass associationClass) {
        super(a, b);
        this.associationClass = associationClass;
        associationElement = new AssociationGR(a, b, associationClass.getAssociation());
        if (associationClass instanceof ConceptualAssociationClass) {
            classElement = new ConceptualClassGR((ConceptualClass) associationClass.getAssociationClass(),
                    new Point(0, 0));
        } else if (associationClass instanceof DesignAssociationClass) {
            classElement = new ClassGR((DesignClass) associationClass.getAssociationClass(), new Point(0, 0));
        } else {
            logger.severe("Some error occured in AssociationClassGR constructor!");
        }

        associationCenterPoint = getAssociationCenterPoint();

    }

    @Override
    public void objectAdded(GraphicalElement obj) {
        if (!linkInstances.contains(obj) && obj instanceof AssociationClassGR) {
            linkInstances.add(((AssociationClassGR) obj).getAssociationElement());
        }
        associationElement.objectAdded(associationElement);
    }

    @Override
    public void objectRemoved(GraphicalElement obj) {
        if (linkInstances.contains(obj)) {
            linkInstances.remove(obj);
        }
        associationElement.objectRemoved(associationElement);
    }

    public AssociationGR getAssociationElement() {
        return associationElement;
    }

    public void setAssociationElement(AssociationGR associationElement) {
        this.associationElement = associationElement;
    }

    public AbstractClassGR getClassElement() {
        return classElement;
    }

    public void setClassElement(AbstractClassGR classElement) {
        this.classElement = classElement;
    }

    @Override
    public void draw(Graphics2D g) {
        associationElement.setSelected(isSelected());
        classElement.setSelected(isSelected());

        associationElement.draw(g);
        if (!isReflective()) {
            associationCenterPoint = getAssociationCenterPoint();
        } else {
            associationCenterPoint = new Point(
                    (int) (associationElement.getXA() + REFLECTIVE_RIGHT * associationElement.getReflectiveStep()),
                    getTopLeftYA() - 15);
        }
        drawClassAndDashedLine(g);
    }

    private Point getAssociationCenterPoint() {
        int x = (associationElement.getXA() + associationElement.getXB()) / 2;
        int y = (associationElement.getYA() + associationElement.getYB()) / 2;
        return new Point(x, y);
    }

    private void drawClassAndDashedLine(Graphics2D g) {
        Vector2D a;
        if (!isReflective()) {
            int ax = associationElement.getXB() - associationElement.getXA();
            int ay = associationElement.getYB() - associationElement.getYA();
            a = new Vector2D(ax, ay);
        } else {
            a = new Vector2D(0, -1);
        }
        a.normalize();
        Vector2D n = a.getNormal();
        int u = (int) (classElement.getBounds().getCenterX() - classElement.getBounds().getX());
        int v = (int) (classElement.getBounds().getCenterY() - classElement.getBounds().getY());
        Vector2D h = new Vector2D(u, v);
        int length = (int) h.getLength();
        Ray d = new Ray(associationCenterPoint, n.multiply((double) length + MINIMUM_DISTANCE));
        Point p = d.getDirection().add(d.getOrigin());

        int x1 = (int) associationCenterPoint.getX();
        int y1 = (int) associationCenterPoint.getY();
        int x2 = (int) p.getX();
        int y2 = (int) p.getY();

        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedDashedStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeDashedStroke());
            g.setPaint(getOutlineColor());
        }

        g.drawLine(x1, y1, x2, y2);

        int x = x2 - (classElement.getWidth() / 2);
        int y = y2 - (classElement.getHeight() / 2);
        classElement.move(x, y);
        classElement.draw(g);
    }

    public boolean isReflective() {
        return associationClass.isReflective();
    }

    public AbstractAssociationClass getAssociationClass() {
        return associationClass;
    }

    public AbstractClassGR getClassA() {
        if (a instanceof ConceptualClassGR) {
            return (ConceptualClassGR) a;
        } else {
            return (ClassGR) a;
        }
    }

    public AbstractClassGR getClassB() {
        if (b instanceof ConceptualClassGR) {
            return (ConceptualClassGR) b;
        } else {
            return (ClassGR) b;
        }
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "associationclass", getAssociationClass());
    }

    @Override
    public boolean contains(Point2D p) {
        boolean classElementContains = classElement.contains(p);
        boolean associationElementContains = associationElement.contains(p);

        return classElementContains || associationElementContains;
    }

    @Override
    @JsonIgnore
    public Rectangle2D getBounds() {
        return classElement.getBounds();
    }

    @Override
    public String toString() {
        return "" + a + " ---associationClass---> " + b;
    }

    @Override
    public Point2D getEndPointRoleA() {
        return associationElement.getEndPointRoleA();
    }

    @Override
    public Point2D getEndPointRoleB() {
        return associationElement.getEndPointRoleB();
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        // Delegate to the internal association element
        return associationElement.reconnectSource(newSource);
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        // Delegate to the internal association element
        return associationElement.reconnectTarget(newTarget);
    }

    @Override
    public AssociationClassGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        ClassifierGR sameA = (ClassifierGR) a;
        ClassifierGR sameB = (ClassifierGR) b;
        AbstractAssociationClass sameAssociationClass = getAssociationClass();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        AssociationClassGR clonedGR = new AssociationClassGR(sameA, sameB, sameAssociationClass);

        return clonedGR;
    }
}
