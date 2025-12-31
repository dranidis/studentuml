package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditSystemInstanceEdit;
import edu.city.studentuml.util.undoredo.SystemEdit;
import edu.city.studentuml.util.undoredo.SystemRepositoryOperations;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SystemInstanceEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class SSDSelectionController extends AbstractSDSelectionController {

    public SSDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(SystemInstanceGR.class, el -> editSystemInstance((SystemInstanceGR) el));
    }

    private void editSystemInstance(SystemInstanceGR systemInstanceGR) {
        CentralRepository repository = model.getCentralRepository();
        SystemInstance originalSystemInstance = systemInstanceGR.getSystemInstance();

        // Create editor and initial result
        SystemInstanceEditor systemEditor = new SystemInstanceEditor(repository);
        TypedEntityEditResult<System, SystemInstance> initialResult = new TypedEntityEditResult<>(
                originalSystemInstance, new java.util.ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<System, SystemInstance> result = systemEditor
                .editDialog(initialResult, parentComponent);

        // Check if user cancelled
        if (result == null) {
            return;
        }

        SystemInstance newSystemInstance = result.getDomainObject();

        // UNDO/REDO setup
        SystemInstance undoSystemInstance = new SystemInstance(originalSystemInstance.getName(),
                originalSystemInstance.getSystem());
        SystemEdit undoEdit = new SystemEdit(undoSystemInstance, originalSystemInstance.getSystem().getName());

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<System> typeOps = new SystemRepositoryOperations();
        for (TypeOperation<System> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // edit the system instance if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalSystemInstance.getName().equals(newSystemInstance.getName())
                && repository.getSystemInstance(newSystemInstance.getName()) != null
                && !newSystemInstance.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing system instance with the given name already.\n"
                            + "Do you want this diagram system instance to refer to the existing one?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                systemInstanceGR.setSystemInstance(repository.getSystemInstance(newSystemInstance.getName()));

                if (originalSystemInstance.getName().equals("")) {
                    repository.removeSystemInstance(originalSystemInstance);
                }
            }
        } else {
            repository.editSystemInstance(originalSystemInstance, newSystemInstance);

            // Add domain object edit to compound
            SystemEdit originalEdit = new SystemEdit(originalSystemInstance,
                    originalSystemInstance.getSystem().getName());
            compoundEdit.addEdit(new EditSystemInstanceEdit(originalEdit, undoEdit, model));
        }

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
