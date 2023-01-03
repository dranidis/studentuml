package edu.city.studentuml.model.graphical;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.UMLProject;

import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Class DiagramModel is a Model component of the MVC architecture It stores the
 * graphical representation of UML concepts that are part of a UML diagram. This
 * class is abstract with known subclasses including SSD/SD/CCD/DCDModel. It
 * extends Observable to notify the views of any changes that occur.
 */
@JsonIncludeProperties({ "name", "internalid", "graphicalElements" })
public abstract class DiagramModel extends Observable implements Serializable, IXMLCustomStreamable {

    private static final long serialVersionUID = 1L;

    private static final  Logger logger = Logger.getLogger(DiagramModel.class.getName());

    @JsonProperty("name")
    protected String diagramName;
    protected DiagramInternalFrame frame;
    protected NotifierVector<GraphicalElement> graphicalElements;
    protected Vector<GraphicalElement> selected;

    // every diagram has to have a reference to the central repository of UML
    // elements
    protected CentralRepository repository;
    protected UMLProject umlProject;

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    protected DiagramModel(String name, UMLProject umlp) {
        diagramName = name;
        graphicalElements = new NotifierVector<>();
        selected = null;
        umlProject = umlp;
        repository = umlp.getCentralRepository();
        umlProject.addDiagram(this);
        selected = new Vector<>();
    }

    public void setFrame(DiagramInternalFrame frm) {
        frame = frm;
    }

    public DiagramInternalFrame getFrame() {
        return frame;
    }

    /**
     * This method adds a graphical element to the diagram, triggered by the
     * controller. The default behavior is to simply add the graphical element to
     * the list and notify the view observers, but subclasses may override this
     * behavior, for example in case the addition of one element affects other
     * elements
     * 
     * @param e
     */
    public void addGraphicalElement(GraphicalElement e) {
        logger.fine(() -> "Adding Element e: " + e.toString() + " " + e.getClass().getSimpleName());
        e.objectAdded(e);
        graphicalElements.add(e);
        modelChanged();
    }

    public void insertGraphicalElementAt(GraphicalElement e, int index) {
        logger.fine(() -> "Inserting Element e: " + e.toString() + " " + e.getClass().getSimpleName() + " at index: " + index);
        e.objectAdded(e);
        graphicalElements.insertElementAt(e, index);
        modelChanged();
    }

    /**
     * This method removes a graphical element from the diagram, triggered by the
     * controller. The default behavior is to simply remove the graphical element
     * from the list, if it exists, and notify the observers, but subclasses may
     * override this behavior, for example in case the deletion of one element (e.g.
     * design class) triggers the deletion of other elements (associations,
     * dependencies, etc.)
     * 
     * @param e
     */
    public void removeGraphicalElement(GraphicalElement e) {
        e.objectRemoved(e);
        graphicalElements.remove(e);
        modelChanged();
    }


    /**
     * This method moves a graphical element in the drawing area, by changing its
     * coordinates. The method is usually triggered by a drag event caused by the
     * drag-and-drop controller. Each element responds polymorphically with its
     * move() method. The simplest behavior is implemented by calling the element's
     * move method, but subclasses may override this behavior in case the movement
     * of one element affects other elements
     * 
     * @param e
     * @param x
     * @param y
     */
    public void moveGraphicalElement(GraphicalElement e, int x, int y) {
        e.move(x, y);
        modelChanged();
    }


    /**
     * This method moves a graphical element in the drawing area, but in difference
     * from moveGraphicalElement(), it is triggered by a releasing of the mouse
     * button after dragging, in case the moved element may have to be validated in
     * its new position. The simplest behavior is exactly the same as move(), but
     * subclasses may override it.
     * 
     * @param e
     * @param x
     * @param y
     */
    public void settleGraphicalElement(GraphicalElement e, int x, int y) {
        moveGraphicalElement(e, x, y);
    }

    // This method causes the selection of a single element at a time in the diagram
    public void selectGraphicalElement(GraphicalElement el) {

        int i = graphicalElements.indexOf(el);

        if (i != -1) {
            GraphicalElement e = (GraphicalElement) graphicalElements.get(i);
            selected.add(e);
            e.setSelected(true);
            modelChanged();
        }
    }

    public void clearSelected() {
        for (GraphicalElement element : graphicalElements) {
            element.setSelected(false);
        }

        if (!selected.isEmpty()) {
            selected.clear();
            modelChanged();
        }
    }

    public Vector<GraphicalElement> getSelectedGraphicalElements() {
        return selected;
    }

    public NotifierVector<GraphicalElement> getGraphicalElements() {
        return graphicalElements;
    }

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public String getDiagramName() {
        return diagramName;
    }

    public void setDiagramName(String name) {
        diagramName = name;
    }

    // retrieves the graphical element in the diagram that contains a given 2D point
    // Usually triggered by a select, drag-and-drop, and addition event.
    public GraphicalElement getContainingGraphicalElement(Point2D point) {

        // get the first element that contains the point, starting from the end of the
        // list,
        // i.e. from the most recently drawn grapical element, so that the uppermost is
        // returned in case elements are overlayed one on top of the other
        ListIterator<GraphicalElement> listIterator = graphicalElements.listIterator(graphicalElements.size());

        while (listIterator.hasPrevious()) {
            GraphicalElement element = listIterator.previous();

            if (element.contains(point)) {
                return element;
            }
        }

        // if no element was found, return null
        return null;
    }

    public GraphicalElement getContainingGraphicalElement(int x, int y) {
        return this.getContainingGraphicalElement(new Point2D.Double(x, y));
    }

    // clears the drawing area of a diagram by setting all graphical elements to
    // empty
    public void clear() {
        while (!graphicalElements.isEmpty()) {
            removeGraphicalElement(graphicalElements.get(0));
        }
        graphicalElements.clear();
        graphicalElements = new NotifierVector<>();
        modelChanged();
    }

    public void setName(String name) {
        diagramName = name;
        modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // method that prints the diagram's name
    public String toString() {
        return diagramName;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        logger.fine(() -> "OBSERVER added: " + o.toString());
        super.addObserver(o);
    }

    // this custom method is called whenever a change in the diagram occurs to
    // notify observers
    public void modelChanged() {
        logger.finest(() -> "Notifying observers: " + this.countObservers());
        setChanged();
        notifyObservers();
    }

    public void setRect(String rect) {
        if (rect != null) {
            logger.finest(() -> "RECT : " + rect);
        }
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getDiagramName());
        if (frame != null) {
            String values = frame.getBounds().x + "," + frame.getBounds().y + "," + frame.getBounds().width + ","
                    + frame.getBounds().height;
            node.setAttribute("framex", values);
            node.setAttribute("selected", Boolean.toString(frame.isSelected()));
            node.setAttribute("iconified", Boolean.toString(frame.isIcon()));
        }
        streamer.streamObjects(node, graphicalElements.iterator());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setDiagramName(node.getAttribute("name"));

        graphicalElements.clear();
        streamer.streamObjectsFrom(node, graphicalElements, instance);
    }

    public UMLProject getUmlProject() {
        return umlProject;
    }
}
