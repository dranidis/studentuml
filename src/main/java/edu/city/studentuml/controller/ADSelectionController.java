package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditActionNodeEdit;
import edu.city.studentuml.util.undoredo.EditActivityNodeEdit;
import edu.city.studentuml.util.undoredo.EditControlFlowEdit;
import edu.city.studentuml.util.undoredo.EditDecisionNodeEdit;
import edu.city.studentuml.util.undoredo.EditObjectFlowEdit;
import edu.city.studentuml.util.undoredo.EditObjectNodeEdit;
import edu.city.studentuml.view.gui.ActionNodeEditor;
import edu.city.studentuml.view.gui.ActivityNodeEditor;
import edu.city.studentuml.view.gui.ControlFlowEditor;
import edu.city.studentuml.view.gui.DecisionNodeEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.ObjectFlowEditor;
import edu.city.studentuml.view.gui.ObjectNodeEditor;

/**
 * @author Biser
 */
public class ADSelectionController extends SelectionController {

    private static final String OBJECT_FLOW_ERROR_STRING = "Object Flow Error";

    public ADSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(ControlFlowGR.class, el -> editControlFlow((ControlFlowGR) el));
        editElementMapper.put(ObjectFlowGR.class, el -> editObjectFlow((ObjectFlowGR) el));
        editElementMapper.put(ActionNodeGR.class, el -> editActionNode((ActionNodeGR) el));
        editElementMapper.put(ObjectNodeGR.class, el -> editObjectNode((ObjectNodeGR) el));
        editElementMapper.put(ActivityNodeGR.class, el -> editActivityNode((ActivityNodeGR) el));
        editElementMapper.put(DecisionNodeGR.class, el -> editDecisionNode((DecisionNodeGR) el));
    }

    private void editControlFlow(ControlFlowGR controlFlowGR) {
        ControlFlowEditor controlFlowEditor = new ControlFlowEditor(controlFlowGR);
        ControlFlow controlFlow = (ControlFlow) controlFlowGR.getEdge();

        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!controlFlowEditor.showDialog(parentComponent, "Control Flow Editor")) {
            return;
        }

        // Undo/Redo
        ControlFlow undoControlFlow = (ControlFlow) controlFlow.clone();

        String guard = controlFlowEditor.getGuard();
        // check that the guard is ok before moving on
        // the outgoing edge from the Decision Node must have a guard (different than other guards)
        NodeComponent sourceNode = controlFlow.getSource();
        if (sourceNode instanceof DecisionNode && guard.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent,
                    "The guard must be specified for the flow going out from the decision node!",
                    "Control Flow Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Edge edge : sourceNode.getOutgoingEdges()) {
            if (edge != controlFlow) {
                String s = edge.getGuard();
                if (s.equals(guard) && !s.isEmpty()) {
                    JOptionPane.showMessageDialog(parentComponent,
                            "Multiple outgoing edges with the same guard are not allowed!",
                            "Control Flow Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        controlFlow.setGuard(guard);

        // Undo/Redo
        UndoableEdit edit = new EditControlFlowEdit(controlFlow, undoControlFlow, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editObjectFlow(ObjectFlowGR objectFlowGR) {
        ObjectFlowEditor objectFlowEditor = new ObjectFlowEditor(objectFlowGR);
        ObjectFlow objectFlow = (ObjectFlow) objectFlowGR.getEdge();

        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!objectFlowEditor.showDialog(parentComponent, "Object Flow Editor")) {
            return;
        }

        // Undo/Redo
        ObjectFlow undoObjectFlow = (ObjectFlow) objectFlow.clone();

        String weight = objectFlowEditor.getWeight();
        String guard = objectFlowEditor.getGuard();
        // check that the guard is ok before moving on
        // the outgoing edge from the Decision Node must have a guard (different than other guards)
        NodeComponent sourceNode = objectFlow.getSource();
        if (sourceNode instanceof DecisionNode && guard.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent,
                    "The guard must be specified for the flow going out from the decision node!",
                    OBJECT_FLOW_ERROR_STRING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Edge edge : sourceNode.getOutgoingEdges()) {
            if (edge != objectFlow) {
                String s = edge.getGuard();
                if (s.equals(guard) && !s.isEmpty()) {
                    JOptionPane.showMessageDialog(parentComponent,
                            "Multiple outgoing edges with the same guard are not allowed!",
                            OBJECT_FLOW_ERROR_STRING,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        try {
            objectFlow.setWeight(weight);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(parentComponent,
                    e.getMessage(),
                    OBJECT_FLOW_ERROR_STRING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        objectFlow.setGuard(guard);

        // Undo/Redo
        UndoableEdit edit = new EditObjectFlowEdit(objectFlow, undoObjectFlow, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editActionNode(ActionNodeGR actionNodeGR) {
        ActionNodeEditor actionNodeEditor = new ActionNodeEditor(actionNodeGR);
        ActionNode actionNode = (ActionNode) actionNodeGR.getComponent();

        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!actionNodeEditor.showDialog(parentComponent, "Action Node Editor")) {
            return;
        }

        // Undo/Redo
        ActionNode undoActionNode = (ActionNode) actionNode.clone();

        String actionName = actionNodeEditor.getActionName();
        actionNode.setName(actionName);

        // Undo/Redo
        UndoableEdit edit = new EditActionNodeEdit(actionNode, undoActionNode, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editObjectNode(ObjectNodeGR objectNodeGR) {
        CentralRepository repository = model.getCentralRepository();
        ObjectNodeEditor objectNodeEditor = new ObjectNodeEditor(objectNodeGR, repository);
        ObjectNode objectNode = (ObjectNode) objectNodeGR.getComponent();

        // show the object node editor dialog and check whether the user has pressed cancel
        if (!objectNodeEditor.showDialog(parentComponent, "Object Node Editor")) {
            return;
        }

        // Undo/Redo

        // do not edit if name and type are both empty
        if (objectNodeEditor.getObjectName().isEmpty() && objectNodeEditor.getType() == null) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Object name and/or type is missing!",
                    "Object Node Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            ObjectNode newObjectNode = new ObjectNode();
            newObjectNode.setName(objectNodeEditor.getObjectName());
            newObjectNode.setType(objectNodeEditor.getType());

            // add the states to the new object node
            for (State state : objectNodeEditor.getStates()) {
                newObjectNode.addState(state);
            }

            // Undo/Redo [edit]
            UndoableEdit edit = new EditObjectNodeEdit(objectNode, newObjectNode, model);
            repository.editObjectNode(objectNode, newObjectNode);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editActivityNode(ActivityNodeGR activityNodeGR) {
        ActivityNodeEditor activityNodeEditor = new ActivityNodeEditor(activityNodeGR);
        ActivityNode activityNode = (ActivityNode) activityNodeGR.getComponent();

        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!activityNodeEditor.showDialog(parentComponent, "Activity Node Editor")) {
            return;
        }

        // Undo/Redo
        ActivityNode undoActivityNode = (ActivityNode) activityNode.clone();

        String activityName = activityNodeEditor.getActivityName();
        activityNode.setName(activityName);

        // Undo/Redo
        UndoableEdit edit = new EditActivityNodeEdit(activityNode, undoActivityNode, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editDecisionNode(DecisionNodeGR decisionNodeGR) {
        DecisionNodeEditor decisionNodeEditor = new DecisionNodeEditor(decisionNodeGR);
        DecisionNode decisionNode = (DecisionNode) decisionNodeGR.getComponent();

        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!decisionNodeEditor.showDialog(parentComponent, "Decision Node Editor")) {
            return;
        }

        // Undo/Redo
        DecisionNode undoDecisionNode = (DecisionNode) decisionNode.clone();

        String decisionName = decisionNodeEditor.getActionName();
        decisionNode.setName(decisionName);

        // Undo/Redo
        UndoableEdit edit = new EditDecisionNodeEdit(decisionNode, undoDecisionNode, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
