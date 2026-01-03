package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.util.Coverable;

/**
 * @author Biser
 */
public abstract class NodeComponentGR extends GraphicalElement implements Coverable {

    protected NodeComponent component;
    public static final NodeComponentGR DEFAULT_CONTEXT = null;
    protected NodeComponentGR context;
    protected List<EdgeGR> incomingRelations;
    protected List<EdgeGR> outgoingRelations;

    protected NodeComponentGR(NodeComponent nodeComponent, int x, int y) {
        this.component = nodeComponent;
        startingPoint = new Point(x, y);
        context = DEFAULT_CONTEXT;
        incomingRelations = new ArrayList<>();
        outgoingRelations = new ArrayList<>();
    }

    public void add(NodeComponentGR nodeGR) {
        throw new UnsupportedOperationException("add(nodeGR) not supported");
    }

    public void remove(NodeComponentGR nodeGR) {
        throw new UnsupportedOperationException("remove(nodeGR) not supported");
    }

    public NodeComponentGR getContext() {
        return context;
    }

    public void setContext(NodeComponentGR context) {
        this.context = context;
        if (context != DEFAULT_CONTEXT) {
            component.setContext(context.getComponent());
        } else {
            component.setContext(NodeComponent.DEFAULT_CONTEXT);
        }
    }

    public void addIncomingEdge(EdgeGR edge) {
        incomingRelations.add(edge);
        component.addIncomingEdge(edge.getEdge());
    }

    public void removeIncomingEdge(EdgeGR edge) {
        incomingRelations.remove(edge);
        component.removeIncomingEdge(edge.getEdge());
    }

    public int getNumberOfIncomingEdges() {
        return incomingRelations.size();
    }

    public List<EdgeGR> getIncomingRelations() {
        return incomingRelations;
    }

    public void addOutgoingEdge(EdgeGR edge) {
        outgoingRelations.add(edge);
        component.addOutgoingEdge(edge.getEdge());
    }

    public void removeOutgoingEdge(EdgeGR edge) {
        outgoingRelations.remove(edge);
        component.removeOutgoingEdge(edge.getEdge());
    }

    public int getNumberOfOutgoingEdges() {
        return outgoingRelations.size();
    }

    public List<EdgeGR> getOutgoingRelations() {
        return outgoingRelations;
    }

    /*
     * Returns the number of node components contained
     */
    public abstract int getNumberOfElements();

    public abstract NodeComponentGR getElement(int index);

    public abstract Iterator<NodeComponentGR> createIterator();

    public NodeComponent getComponent() {
        return component;
    }

    public abstract boolean contains(NodeComponentGR otherNodeComponent);

    public abstract GraphicalElement getContainingGraphicalElement(Point2D point);

    public abstract NodeComponentGR findContext(NodeComponentGR node);

    public abstract void clearSelected();

    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
        calculateHeight(g);
    }

    protected abstract int calculateWidth(Graphics2D g);

    protected abstract int calculateHeight(Graphics2D g);

    /**
     * Template method for editing a node component's name using a
     * StringEditorDialog. This implements Pattern 1 (Simple Domain Edit) for domain
     * objects that have a name property.
     * <p>
     * The workflow is:
     * </p>
     * <ol>
     * <li>Show StringEditorDialog with the provided title and label</li>
     * <li>If cancelled, return false</li>
     * <li>Clone the domain object for undo/redo</li>
     * <li>Apply the new name via setName()</li>
     * <li>Create and post the UndoableEdit (provided by subclass)</li>
     * <li>Notify model changed and reload name pool</li>
     * <li>Return true</li>
     * </ol>
     * 
     * @param context             the edit context providing access to model,
     *                            repository, parent component, and undo support
     * @param dialogTitle         the title to display in the StringEditorDialog
     *                            (e.g., "Activity Node Editor")
     * @param fieldLabel          the label for the text field (e.g., "Activity
     *                            name: ")
     * @param undoableEditFactory a function that creates the appropriate
     *                            UndoableEdit given the original and cloned domain
     *                            objects
     * @return true if the edit was successful and applied, false if cancelled
     */
    protected boolean editNameWithDialog(
            EditContext context,
            String dialogTitle,
            String fieldLabel,
            UndoableEditFactory undoableEditFactory) {
        NodeComponent domain = getComponent();

        return editStringPropertyWithDialog(
                context,
                dialogTitle,
                fieldLabel,
                domain,
                NodeComponent::getName,
                NodeComponent::setName,
                d -> (NodeComponent) d.clone(),
                (orig, undo, model) -> undoableEditFactory.create(orig, undo, model),
                null, // no duplicate check
                null); // no duplicate error message
    }

    /**
     * Functional interface for creating UndoableEdit instances. This allows
     * subclasses to provide their specific UndoableEdit implementation while
     * reusing the common editing workflow.
     */
    @FunctionalInterface
    protected interface UndoableEditFactory {
        /**
         * Creates an UndoableEdit for the given domain objects.
         * 
         * @param original the original domain object being edited (unmutated at call
         *                 time)
         * @param newValue the domain object with the new/target state for redo
         * @param model    the diagram model
         * @return the UndoableEdit instance
         */
        UndoableEdit create(NodeComponent original, NodeComponent newValue,
                DiagramModel model);
    }

    @Override
    public String toString() {
        return component.getName() + " : " + component.getClass().getSimpleName();
    }
}
