package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditControlFlowEdit;
import edu.city.studentuml.util.undoredo.EditObjectFlowEdit;
import edu.city.studentuml.util.undoredo.EditObjectNodeEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.ObjectFlowEditor;
import edu.city.studentuml.view.gui.ObjectNodeEditor;
import edu.city.studentuml.view.gui.StringEditorDialog;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ADSelectionController extends SelectionController {

    private static final String OBJECT_FLOW_ERROR_STRING = "Object Flow Error";

    public ADSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(ControlFlowGR.class, el -> editControlFlow((ControlFlowGR) el));
        editElementMapper.put(ObjectFlowGR.class, el -> editObjectFlow((ObjectFlowGR) el));
        editElementMapper.put(ObjectNodeGR.class, el -> editObjectNode((ObjectNodeGR) el));
        // ActivityNodeGR, DecisionNodeGR, and ActionNodeGR now use polymorphic edit() method
    }

    private void editControlFlow(ControlFlowGR controlFlowGR) {
        ControlFlow controlFlow = (ControlFlow) controlFlowGR.getEdge();
        StringEditorDialog stringEditorDialog = new StringEditorDialog(parentComponent, "Control Flow Editor", "Guard",
                controlFlow.getGuard());
        // show the control flow editor dialog and check whether the user has pressed cancel
        if (!stringEditorDialog.showDialog()) {
            return;
        }

        // Undo/Redo
        ControlFlow undoControlFlow = (ControlFlow) controlFlow.clone();

        String guard = stringEditorDialog.getText();
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
        ObjectFlowEditor objectFlowEditor = new ObjectFlowEditor();
        ObjectFlow originalObjectFlow = (ObjectFlow) objectFlowGR.getEdge();

        ObjectFlow editedObjectFlow = objectFlowEditor.editDialog(originalObjectFlow, parentComponent);
        if (editedObjectFlow == null) {
            return; // User cancelled
        }

        // Undo/Redo - capture original state
        ObjectFlow undoObjectFlow = originalObjectFlow.clone();

        String weight = editedObjectFlow.getWeight();
        String guard = editedObjectFlow.getGuard();

        // check that the guard is ok before moving on
        // the outgoing edge from the Decision Node must have a guard (different than other guards)
        NodeComponent sourceNode = originalObjectFlow.getSource();
        if (sourceNode instanceof DecisionNode && guard.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent,
                    "The guard must be specified for the flow going out from the decision node!",
                    OBJECT_FLOW_ERROR_STRING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Edge edge : sourceNode.getOutgoingEdges()) {
            if (edge != originalObjectFlow) {
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
            originalObjectFlow.setWeight(weight);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(parentComponent,
                    e.getMessage(),
                    OBJECT_FLOW_ERROR_STRING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        originalObjectFlow.setGuard(guard);

        // Undo/Redo
        UndoableEdit edit = new EditObjectFlowEdit(originalObjectFlow, undoObjectFlow, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editObjectNode(ObjectNodeGR objectNodeGR) {
        CentralRepository repository = model.getCentralRepository();
        ObjectNode objectNode = (ObjectNode) objectNodeGR.getComponent();

        // Create editor and initial result
        ObjectNodeEditor objectNodeEditor = new ObjectNodeEditor(repository);
        TypedEntityEditResult<DesignClass, ObjectNode> initialResult = new TypedEntityEditResult<>(objectNode,
                new java.util.ArrayList<>());

        TypedEntityEditResult<DesignClass, ObjectNode> result = objectNodeEditor.editDialog(initialResult,
                parentComponent);

        // Check if user cancelled
        if (result == null) {
            return;
        }

        ObjectNode newObjectNode = result.getDomainObject();

        // do not edit if name and type are both empty
        if (newObjectNode.getName().isEmpty() && newObjectNode.getType() == null) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Object name and/or type is missing!",
                    "Object Node Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // Add domain object edit and apply
        UndoableEdit edit = new EditObjectNodeEdit(objectNode, newObjectNode, model);
        compoundEdit.addEdit(edit);
        repository.editObjectNode(objectNode, newObjectNode);

        // Post the compound edit
        compoundEdit.end();
        if (!compoundEdit.isInProgress() && compoundEdit.canUndo()) {
            parentComponent.getUndoSupport().postEdit(compoundEdit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }
}
