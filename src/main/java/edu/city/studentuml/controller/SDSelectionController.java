package edu.city.studentuml.controller;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditCreateMessageEdit;
import edu.city.studentuml.util.undoredo.EditMultiObjectEdit;
import edu.city.studentuml.util.undoredo.EditSDObjectEdit;
import edu.city.studentuml.util.undoredo.MultiObjectEdit;
import edu.city.studentuml.util.undoredo.ObjectEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.MultiObjectEditor;
import edu.city.studentuml.view.gui.ObjectEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

// handles all events when the "selection" button in the SD toolbar is pressed
public class SDSelectionController extends AbstractSDSelectionController {

    private static final String WARNING = "Warning";

    public SDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(SDObjectGR.class, el -> editSDObject((SDObjectGR) el));
        editElementMapper.put(MultiObjectGR.class, el -> editMultiObject((MultiObjectGR) el));
        editElementMapper.put(CreateMessageGR.class, el -> editCreateMessage((CreateMessageGR) el));

    }

    public void editSDObject(SDObjectGR object) {
        CentralRepository repository = model.getCentralRepository();
        SDObject originalObject = object.getSDObject();

        // Create editor and initial result
        ObjectEditor objectEditor = new ObjectEditor(repository);
        TypedEntityEditResult<DesignClass, SDObject> initialResult = new TypedEntityEditResult<>(originalObject,
                new java.util.ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<DesignClass, SDObject> result = objectEditor.editDialog(initialResult, parentComponent);

        // Check if user cancelled
        if (result == null) {
            return;
        }

        SDObject newObject = result.getDomainObject();

        // UNDO/REDO setup
        SDObject undoObject = new SDObject(originalObject.getName(), originalObject.getDesignClass());
        ObjectEdit undoEdit = new ObjectEdit(undoObject, originalObject.getDesignClass().getName());

        // Create compound edit for all operations (type operations + domain object change)
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // Handle domain object editing with conflict checking
        if (!originalObject.getName().equals(newObject.getName())
                && repository.getObject(newObject.getName()) != null
                && !newObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing object with the given name already.\n"
                            + "Do you want this diagram object to refer to the existing one?",
                    WARNING,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                object.setSDObject(repository.getObject(newObject.getName()));

                if (originalObject.getName().equals("")) {
                    repository.removeObject(originalObject);
                }
            }
        } else {
            repository.editObject(originalObject, newObject);
            // Add domain object edit to compound
            ObjectEdit originalEdit = new ObjectEdit(originalObject, originalObject.getDesignClass().getName());
            compoundEdit.addEdit(new EditSDObjectEdit(originalEdit, undoEdit, model));
        }

        // Post the compound edit if there were any edits
        compoundEdit.end();
        if (!compoundEdit.isInProgress() && compoundEdit.canUndo()) {
            parentComponent.getUndoSupport().postEdit(compoundEdit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void editMultiObject(MultiObjectGR multiObject) {
        CentralRepository repository = model.getCentralRepository();
        MultiObject originalMultiObject = multiObject.getMultiObject();

        // Create editor and initial result
        MultiObjectEditor multiObjectEditor = new MultiObjectEditor(repository);
        TypedEntityEditResult<DesignClass, MultiObject> initialResult = new TypedEntityEditResult<>(originalMultiObject,
                new java.util.ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<DesignClass, MultiObject> result = multiObjectEditor.editDialog(initialResult,
                parentComponent);

        // Check if user cancelled
        if (result == null) {
            return;
        }

        MultiObject newMultiObject = result.getDomainObject();

        // UNDO/REDO setup
        MultiObject undoObject = new MultiObject(originalMultiObject.getName(), originalMultiObject.getDesignClass());
        MultiObjectEdit undoEdit = new MultiObjectEdit(undoObject, originalMultiObject.getDesignClass().getName());

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // edit the multiobject if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalMultiObject.getName().equals(newMultiObject.getName())
                && repository.getMultiObject(newMultiObject.getName()) != null
                && !newMultiObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing multiobject with the given name already.\n"
                            + "Do you want this diagram object to refer to the existing one?",
                    WARNING,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                multiObject.setMultiObject(repository.getMultiObject(newMultiObject.getName()));

                if (originalMultiObject.getName().equals("")) {
                    repository.removeMultiObject(originalMultiObject);
                }
            }
        } else {
            repository.editMultiObject(originalMultiObject, newMultiObject);

            // Add domain object edit to compound
            MultiObjectEdit originalEdit = new MultiObjectEdit(originalMultiObject,
                    originalMultiObject.getDesignClass().getName());
            compoundEdit.addEdit(new EditMultiObjectEdit(originalEdit, undoEdit, model));
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

    //new edit create method 
    public void editCreateMessage(CreateMessageGR messageGR) {
        CreateMessage message = messageGR.getCreateMessage();
        CallMessageEditor createMessageEditor = new CallMessageEditor(model.getCentralRepository());

        CreateMessage undoCreateMessage = message.clone();

        // if user presses cancel don't do anything
        CallMessage editedMessage = createMessageEditor.editDialog(message, parentComponent);
        if (editedMessage == null) {
            return;
        }

        // Copy parameters from edited message to original message
        message.setParameters(new Vector<>());
        message.getParameters().addAll(editedMessage.getParameters());

        // UNDO/REDO
        UndoableEdit edit = new EditCreateMessageEdit(message, undoCreateMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
