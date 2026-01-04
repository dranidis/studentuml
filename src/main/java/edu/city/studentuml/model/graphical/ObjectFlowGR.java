package edu.city.studentuml.model.graphical;

import java.awt.Point;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditObjectFlowEdit;
import edu.city.studentuml.view.gui.ObjectFlowEditor;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ObjectFlowGR extends EdgeGR {

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow) {
        super(source, target, flow);
    }

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow, Point srcPoint,
            Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    protected String getStreamName() {
        return "objectflow";
    }

    @Override
    public ObjectFlowGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Edges connect graphical elements, so we reference the same endpoints
        NodeComponentGR sameSource = (NodeComponentGR) getSource();
        NodeComponentGR sameTarget = (NodeComponentGR) getTarget();
        ObjectFlow sameFlow = (ObjectFlow) getEdge();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        ObjectFlowGR clonedGR = new ObjectFlowGR(sameSource, sameTarget, sameFlow);

        return clonedGR;
    }

    /**
     * Polymorphic edit method for ObjectFlow, enabling users to edit the guard and
     * weight with validation and undo/redo support.
     */
    @Override
    public boolean edit(EditContext context) {
        ObjectFlow originalObjectFlow = (ObjectFlow) getEdge();

        // Launch editor dialog
        ObjectFlowEditor objectFlowEditor = createEditor(context);
        ObjectFlow editedObjectFlow = objectFlowEditor.editDialog(originalObjectFlow, context.getParentComponent());
        if (editedObjectFlow == null) {
            return false; // User cancelled
        }

        // Prepare new state (redo) clone
        ObjectFlow newObjectFlow = originalObjectFlow.clone();

        String newWeight = editedObjectFlow.getWeight();
        String newGuard = editedObjectFlow.getGuard();

        // Validation: Decision node outgoing edges must have a non-empty guard
        NodeComponent sourceNode = originalObjectFlow.getSource();
        if (sourceNode instanceof DecisionNode && (newGuard == null || newGuard.isEmpty())) {
            JOptionPane.showMessageDialog(context.getParentComponent(),
                    "The guard must be specified for the flow going out from the decision node!",
                    "Object Flow Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validation: Guard uniqueness among outgoing edges (non-empty guards)
        for (Edge edge : sourceNode.getOutgoingEdges()) {
            if (edge != originalObjectFlow) {
                String existingGuard = edge.getGuard();
                if (existingGuard.equals(newGuard) && !existingGuard.isEmpty()) {
                    JOptionPane.showMessageDialog(context.getParentComponent(),
                            "Multiple outgoing edges with the same guard are not allowed!",
                            "Object Flow Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        // Validation: Weight format (positive integer if specified)
        // Apply to clone first for validation
        try {
            newObjectFlow.setWeight(newWeight);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(context.getParentComponent(),
                    e.getMessage(),
                    "Object Flow Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Apply guard to clone
        newObjectFlow.setGuard(newGuard);

        // Create and post undoable edit (original unmutated, newObjectFlow has target state)
        UndoableEdit edit = new EditObjectFlowEdit(originalObjectFlow, newObjectFlow, context.getModel());
        context.getUndoSupport().postEdit(edit);

        // Now apply changes to original for immediate effect
        originalObjectFlow.setWeight(newWeight);
        originalObjectFlow.setGuard(newGuard);

        // Notify model and refresh name pool
        context.notifyModelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Creates the editor for this Object Flow. Extracted into a protected method to
     * enable testing without UI dialogs (can be overridden to return mock editor).
     * 
     * @param context the edit context
     * @return the editor instance
     */
    protected ObjectFlowEditor createEditor(EditContext context) {
        return new ObjectFlowEditor();
    }

}
