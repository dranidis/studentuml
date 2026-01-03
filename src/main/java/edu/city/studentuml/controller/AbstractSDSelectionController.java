package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.ActorInstanceEdit;
import edu.city.studentuml.util.undoredo.ActorRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditActorInstanceEdit;
import edu.city.studentuml.util.undoredo.EditCallMessageEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.ActorInstanceEditor;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * Overrides the methods for setting the undo and redo coordinates for the
 * undo/redo of movement of SD and SSD diagrams.\
 * 
 * @author Dimitris Dranidis
 */
public abstract class AbstractSDSelectionController extends SelectionController {

    private List<Integer> oldXList = new ArrayList<>();
    private List<Integer> oldYList = new ArrayList<>();

    protected AbstractSDSelectionController(DiagramInternalFrame parent, DiagramModel m) {
        super(parent, m);
        editElementMapper.put(ActorInstanceGR.class, el -> editActorInstance((ActorInstanceGR) el));
        editElementMapper.put(CallMessageGR.class, el -> editCallMessage((CallMessageGR) el));
    }

    /**
     * Objects are always at the top. Their Y is not changing. Messages x is always
     * 0. Only their height is changings. Store all X, Y coord of selected elements.
     */
    @Override
    protected void setUndoCoordinates() {
        oldXList.clear();
        oldYList.clear();

        for (GraphicalElement el : selectedElements) {
            oldXList.add(el.getX());
            oldYList.add(el.getY());
        }
    }

    /**
     * Compare old X with new X to see what is the X distance Same for old Y and new
     * Y for the Y distance
     */
    @Override
    protected void setRedoCoordinates() {

        if (selectedElements.isEmpty())
            return;

        int newIndexX = 0;
        int newIndexY = 0;
        for (int i = 0; i < oldXList.size(); i++) {
            int oldX = oldXList.get(i);
            if (oldX != selectedElements.get(i).getX()) {
                newIndexX = i;
            }
            int oldY = oldYList.get(i);
            if (oldY != selectedElements.get(i).getY()) {
                newIndexY = i;
            }
        }

        undoCoordinates.setLocation(oldXList.get(newIndexX), oldYList.get(newIndexY));
        redoCoordinates.setLocation(selectedElements.get(newIndexX).getX(), selectedElements.get(newIndexY).getY());
    }

    private void editActorInstance(ActorInstanceGR actorInstance) {
        CentralRepository repository = model.getCentralRepository();
        ActorInstance originalActorInstance = actorInstance.getActorInstance();

        // Create editor and initial result
        ActorInstanceEditor actorInstanceEditor = new ActorInstanceEditor(repository);
        TypedEntityEditResult<Actor, ActorInstance> initialResult = new TypedEntityEditResult<>(originalActorInstance,
                new java.util.ArrayList<>());

        TypedEntityEditResult<Actor, ActorInstance> result = actorInstanceEditor.editDialog(initialResult,
                parentComponent);

        // Check if user cancelled
        if (result == null) {
            return;
        }

        ActorInstance newActorInstance = result.getDomainObject();

        // UNDO/REDO setup
        ActorInstance undoActorInstance = originalActorInstance.clone();
        ActorInstanceEdit undoEdit = new ActorInstanceEdit(undoActorInstance,
                originalActorInstance.getActor().getName());

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<Actor> typeOps = new ActorRepositoryOperations();
        for (TypeOperation<Actor> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // edit the actor if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalActorInstance.getName().equals(newActorInstance.getName())
                && repository.getActorInstance(newActorInstance.getName()) != null
                && !newActorInstance.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing actor instance with the given name already.\n"
                            + "Do you want this diagram actor instance to refer to the existing one?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                actorInstance.setActorInstance(repository.getActorInstance(newActorInstance.getName()));

                if (originalActorInstance.getName().equals("")) {
                    repository.removeActorInstance(originalActorInstance);
                }
            }
        } else {
            repository.editActorInstance(originalActorInstance, newActorInstance);

            // Add domain object edit to compound
            ActorInstanceEdit originalEdit = new ActorInstanceEdit(originalActorInstance,
                    originalActorInstance.getActor().getName());
            compoundEdit.addEdit(new EditActorInstanceEdit(originalEdit, undoEdit, model));
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

    private void editCallMessage(CallMessageGR messageGR) {
        CallMessage message = messageGR.getCallMessage();
        CallMessageEditor callMessageEditor = new CallMessageEditor(model.getCentralRepository());

        CallMessage undoCallMessage = message.clone();

        // if user presses cancel don't do anything
        CallMessage editedMessage = callMessageEditor.editDialog(message, parentComponent);
        if (editedMessage == null) {
            return;
        }

        message.setName(editedMessage.getName());
        message.setIterative(editedMessage.isIterative());
        message.setReturnValue(editedMessage.getReturnValue());
        message.setReturnType(editedMessage.getReturnType());

        message.setParameters(editedMessage.getParameters());

        // UNDO/REDO
        UndoableEdit edit = new EditCallMessageEdit(message, undoCallMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // ReturnMessageGR now uses polymorphic edit(EditContext); legacy controller editor removed.

    /**
     * Override to handle deletion of call messages and their corresponding return
     * messages. When a call message is deleted, its return message should also be
     * deleted automatically.
     */
    @Override
    protected void deleteSelectedElements() {
        // Before deleting, check if any selected element is a call message
        // and if so, add its corresponding return message to the selection
        List<GraphicalElement> elementsToDelete = new ArrayList<>(selectedElements);

        for (GraphicalElement element : elementsToDelete) {
            if (element instanceof CallMessageGR) {
                CallMessageGR callMsg = (CallMessageGR) element;
                ReturnMessageGR returnMsg = findCorrespondingReturnMessage(callMsg);

                // Add return message to selection if found and not already selected
                if (returnMsg != null && !selectedElements.contains(returnMsg)) {
                    selectedElements.add(returnMsg);
                }
            }
        }

        // Call parent's delete logic which will now delete both call and return messages
        super.deleteSelectedElements();
    }

    @Override
    public void handleCtrlShiftSelect(GraphicalElement element) {
        if (element instanceof SDMessageGR) {
            if (!selectedElements.contains(element)) {
                selectedElements.add(element);
            }
            SDMessageGR message = (SDMessageGR) element;
            AbstractSDModel sdmodel = (AbstractSDModel) model;
            List<SDMessageGR> belowMessages = sdmodel.getMessagesBelow(message);
            for (SDMessageGR m : belowMessages) {
                if (!selectedElements.contains(m)) {
                    selectedElements.add(m);
                }
            }
        }
    }

}
