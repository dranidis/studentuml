package edu.city.studentuml.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.ReturnMessage;
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
import edu.city.studentuml.util.undoredo.EditActorInstanceEdit;
import edu.city.studentuml.util.undoredo.EditCallMessageEdit;
import edu.city.studentuml.util.undoredo.EditReturnMessageEdit;
import edu.city.studentuml.view.gui.ActorInstanceEditor;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Overrides the methods for setting the undo and redo coordinates for the undo/redo of movement of SD and SSD diagrams.
 */
public abstract class AbstractSDSelectionController extends SelectionController {
    
    private List<Integer> oldXList = new ArrayList<>();
    private List<Integer> oldYList = new ArrayList<>();
    
    protected AbstractSDSelectionController(DiagramInternalFrame parent, DiagramModel m) {
        super(parent, m);
        editElementMapper.put(ActorInstanceGR.class, el -> editActorInstance((ActorInstanceGR) el));
        editElementMapper.put(CallMessageGR.class, el -> editCallMessage((CallMessageGR) el));
        editElementMapper.put(ReturnMessageGR.class, el -> editReturnMessage((ReturnMessageGR) el));
    }

    /**
     * Objects are always at the top. Their Y is not changing.
     * Messages x is always 0. Only their height is changings.  
     * Store all X, Y coord of selected elements.
     */
    @Override
    protected void setUndoCoordinates() {
        oldXList.clear();
        oldYList.clear();

        for (GraphicalElement el: selectedElements) {
            oldXList.add(el.getX());
            oldYList.add(el.getY());
        }
    }

    /**
     * Compare old X with new X to see what is the X distance
     * Same for old Y and new Y for the Y distance
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
        ActorInstanceEditor actorInstanceEditor = new ActorInstanceEditor(actorInstance, repository);
        ActorInstance originalActorInstance = actorInstance.getActorInstance();

        // UNDO/REDO
        ActorInstance undoActorInstance = originalActorInstance.clone();
        ActorInstanceEdit undoEdit = new ActorInstanceEdit(undoActorInstance,
                originalActorInstance.getActor().getName());

        // show the actor instance editor dialog and check whether the user has pressed
        // cancel
        if (!actorInstanceEditor.showDialog(parentComponent, "Actor Instance Editor")) {
            return;
        }

        ActorInstance newActorInstance = new ActorInstance(actorInstanceEditor.getActorInstanceName(),
                actorInstanceEditor.getActor());
        ActorInstanceEdit originalEdit;

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

            // UNDO/REDO
            originalEdit = new ActorInstanceEdit(originalActorInstance, originalActorInstance.getActor().getName());
            UndoableEdit edit = new EditActorInstanceEdit(originalEdit, undoEdit, model);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editCallMessage(CallMessageGR messageGR) {
        CallMessageEditor callMessageEditor = new CallMessageEditor(messageGR, model.getCentralRepository());
        CallMessage message = messageGR.getCallMessage();

        CallMessage undoCallMessage = message.clone();

        // if user presses cancel don't do anything
        if (!callMessageEditor.showDialog(parentComponent, "Call Message Editor")) {
            return;
        }

        message.setName(callMessageEditor.getCallMessageName());
        message.setIterative(callMessageEditor.isIterative());
        message.setReturnValue(callMessageEditor.getReturnValue());
        message.setReturnType(callMessageEditor.getReturnType());

        message.setParameters(callMessageEditor.getParameters());

        // UNDO/REDO
        UndoableEdit edit = new EditCallMessageEdit(message, undoCallMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editReturnMessage(ReturnMessageGR messageGR) {
        String newName = JOptionPane.showInputDialog("Enter the return message string",
                messageGR.getReturnMessage().getName());

        if (newName == null) { // user pressed cancel
            return;
        }

        ReturnMessage undoReturnMessage = messageGR.getReturnMessage().clone();
        ReturnMessage originalReturnMessage = messageGR.getReturnMessage();
        originalReturnMessage.setName(newName);

        // UNDO/REDO
        UndoableEdit edit = new EditReturnMessageEdit(originalReturnMessage, undoReturnMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    @Override
    public void handleCtrlShiftSelect(GraphicalElement element) {
        if(element instanceof SDMessageGR) {
            if (!selectedElements.contains(element)) {
                selectedElements.add(element);
            }
            SDMessageGR message = (SDMessageGR) element;
            AbstractSDModel sdmodel = (AbstractSDModel) model;
            List<SDMessageGR> belowMessages = sdmodel.getMessagesBelow(message);
            for (SDMessageGR m: belowMessages) {
                if (!selectedElements.contains(m)) {
                    selectedElements.add(m);
                }
            }
        }
    }

}
