package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditObjectNodeEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.ObjectNodeEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ADSelectionController extends SelectionController {

    public ADSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        // ControlFlowGR now uses polymorphic edit() method
        editElementMapper.put(ObjectNodeGR.class, el -> editObjectNode((ObjectNodeGR) el));
        // ActivityNodeGR, DecisionNodeGR, and ActionNodeGR now use polymorphic edit() method
    }

    // ControlFlowGR now uses polymorphic edit(EditContext); legacy controller editor removed.

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
