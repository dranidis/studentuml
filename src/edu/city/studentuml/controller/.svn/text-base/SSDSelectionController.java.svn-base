package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.ActorInstanceEditor;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SystemInstanceEditor;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.MessageParameter;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.ActorInstanceEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEditLoader;
import edu.city.studentuml.util.undoredo.DeleteEditFactory;
import edu.city.studentuml.util.undoredo.EditActorInstanceEdit;
import edu.city.studentuml.util.undoredo.EditCallMessageEdit;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.util.undoredo.EditReturnMessageEdit;
import edu.city.studentuml.util.undoredo.EditSystemInstanceEdit;
import edu.city.studentuml.util.undoredo.SystemEdit;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class SSDSelectionController extends SelectionController {

    public SSDSelectionController(DiagramInternalFrame parent, SSDModel m) {
        super(parent, m);
    }

    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof SystemInstanceGR) {
            editSystemInstance((SystemInstanceGR) selectedElement);
        } else if (selectedElement instanceof ActorInstanceGR) {
            editActorInstance((ActorInstanceGR) selectedElement);
        } else if (selectedElement instanceof CallMessageGR) {
            editCallMessage((CallMessageGR) selectedElement);
        } else if (selectedElement instanceof ReturnMessageGR) {
            editReturnMessage((ReturnMessageGR) selectedElement);
        } else if (selectedElement instanceof UMLNoteGR) {
            editUMLNote((UMLNoteGR) selectedElement);
        }
    }

    private void editUMLNote(UMLNoteGR noteGR) {
        UMLNoteEditor noteEditor = new UMLNoteEditor(noteGR);

        // Undo/Redo
        String undoText = noteGR.getText();

        if (!noteEditor.showDialog(parentComponent, "UML Note Editor")) {
            return;
        }

        noteGR.setText(noteEditor.getText());

        // Undo/Redo
        UndoableEdit edit = new EditNoteGREdit(noteGR, model, undoText);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void editSystemInstance(SystemInstanceGR systemInstanceGR) {
        CentralRepository repository = model.getCentralRepository();
        SystemInstanceEditor systemEditor = new SystemInstanceEditor(systemInstanceGR, repository);
        SystemInstance originalSystemInstance = systemInstanceGR.getSystemInstance();

        // UNDO/REDO
        SystemInstance undoSystemInstance = new SystemInstance(originalSystemInstance.getName(), originalSystemInstance.getSystem());
        SystemEdit undoEdit = new SystemEdit(undoSystemInstance, originalSystemInstance.getSystem().getName());

        // show the system instance editor dialog and check whether the user has pressed cancel
        if (!systemEditor.showDialog(parentComponent, "System Instance Editor")) {
            return;
        }

        SystemInstance newSystemInstance = new SystemInstance(systemEditor.getName(), systemEditor.getSystem());
        SystemEdit originalEdit;

        // edit the system instance if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalSystemInstance.getName().equals(newSystemInstance.getName())
                && (repository.getSystemInstance(newSystemInstance.getName()) != null)
                && !newSystemInstance.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing system instance with the given name already.\n"
                    + "Do you want this diagram system instance to refer to the existing one?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                systemInstanceGR.setSystemInstance(repository.getSystemInstance(newSystemInstance.getName()));

                if (originalSystemInstance.getName().equals("")) {
                    repository.removeSystemInstance(originalSystemInstance);
                }
            }
        } else {
            repository.editSystemInstance(originalSystemInstance, newSystemInstance);

            // UNDO/REDO
            originalEdit = new SystemEdit(originalSystemInstance, originalSystemInstance.getSystem().getName());
            UndoableEdit edit = new EditSystemInstanceEdit(originalEdit, undoEdit, model);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void editActorInstance(ActorInstanceGR actorInstance) {
        CentralRepository repository = model.getCentralRepository();
        ActorInstanceEditor actorInstanceEditor = new ActorInstanceEditor(actorInstance, repository);
        ActorInstance originalActorInstance = actorInstance.getActorInstance();

        // UNDO/REDO
        ActorInstance undoActorInstance = originalActorInstance.clone();
        ActorInstanceEdit undoEdit = new ActorInstanceEdit(undoActorInstance, originalActorInstance.getActor().getName());

        // show the actor instance editor dialog and check whether the user has pressed cancel
        if (!actorInstanceEditor.showDialog(parentComponent, "Actor Instance Editor")) {
            return;
        }

        ActorInstance newActorInstance = new ActorInstance(actorInstanceEditor.getName(),
                actorInstanceEditor.getActor());
        ActorInstanceEdit originalEdit;

        // edit the actor if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalActorInstance.getName().equals(newActorInstance.getName())
                && (repository.getActorInstance(newActorInstance.getName()) != null)
                && !newActorInstance.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing actor instance with the given name already.\n"
                    + "Do you want this diagram actor instance to refer to the existing one?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

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

    public void editCallMessage(CallMessageGR messageGR) {
        CallMessageEditor callMessageEditor = new CallMessageEditor(messageGR);
        CallMessage message = messageGR.getCallMessage();

        CallMessage undoCallMessage = message.clone();

        // if user presses cancel don't do anything
        if (!callMessageEditor.showDialog(parentComponent, "Call Message Editor")) {
            return;
        }

        message.setName(callMessageEditor.getName());
        message.setIterative(callMessageEditor.isIterative());
        message.setReturnValue(callMessageEditor.getReturnValue());

        Vector parameters = callMessageEditor.getParameters();
        Iterator iterator = parameters.iterator();
        message.setParameters(new Vector());
        while (iterator.hasNext()) {
            message.addParameter((MessageParameter) iterator.next());
        }

        // UNDO/REDO
        UndoableEdit edit = new EditCallMessageEdit(message, undoCallMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void editReturnMessage(ReturnMessageGR messageGR) {
        String newName = JOptionPane.showInputDialog("Enter the return message string");

        if (newName == null) {    // user pressed cancel
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

    public void deleteElement(GraphicalElement selectedElement) {
        UndoableEdit edit = DeleteEditFactory.getInstance().createDeleteEdit(selectedElement, model);
        if (edit instanceof CompositeDeleteEdit) {
            CompositeDeleteEditLoader.loadCompositeDeleteEdit(selectedElement, (CompositeDeleteEdit) edit, model);
        }
        parentComponent.getUndoSupport().postEdit(edit);
        model.removeGraphicalElement(selectedElement);
    }
}
