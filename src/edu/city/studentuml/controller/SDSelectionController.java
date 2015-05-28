package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.MessageParameter;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.util.undoredo.EditSDObjectEdit;
import edu.city.studentuml.view.gui.ActorInstanceEditor;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.MultiObjectEditor;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.view.gui.ObjectEditor;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.util.undoredo.ActorInstanceEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEditLoader;
import edu.city.studentuml.util.undoredo.DeleteEditFactory;
import edu.city.studentuml.util.undoredo.EditActorInstanceEdit;
import edu.city.studentuml.util.undoredo.EditCallMessageEdit;
import edu.city.studentuml.util.undoredo.EditMultiObjectEdit;
import edu.city.studentuml.util.undoredo.EditReturnMessageEdit;
import edu.city.studentuml.util.undoredo.MultiObjectEdit;
import edu.city.studentuml.util.undoredo.ObjectEdit;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

//handles all events when the "selection" button in the SD toolbar is pressed
public class SDSelectionController extends SelectionController {

    public SDSelectionController(DiagramInternalFrame parent, SDModel m) {
        super(parent, m);
    }

    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof SDObjectGR) {
            editSDObject((SDObjectGR) selectedElement);
        } else if (selectedElement instanceof MultiObjectGR) {
            editMultiObject((MultiObjectGR) selectedElement);
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

    public void editSDObject(SDObjectGR object) {
        CentralRepository repository = model.getCentralRepository();
        ObjectEditor objectEditor = new ObjectEditor(object, repository);
        SDObject originalObject = object.getSDObject();

        // UNDO/REDO
        SDObject undoObject = new SDObject(originalObject.getName(), originalObject.getDesignClass());
        ObjectEdit undoEdit = new ObjectEdit(undoObject, originalObject.getDesignClass().getName());

        // show the object editor dialog and check whether the user has pressed cancel
        if (!objectEditor.showDialog(parentComponent, "Object Editor")) {
            return;
        }

        SDObject newObject = new SDObject(objectEditor.getName(), objectEditor.getDesignClass());
        ObjectEdit originalEdit;

        // edit the object if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalObject.getName().equals(newObject.getName())
                && (repository.getObject(newObject.getName()) != null)
                && !newObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing object with the given name already.\n"
                    + "Do you want this diagram object to refer to the existing one?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                object.setSDObject(repository.getObject(newObject.getName()));

                if (originalObject.getName().equals("")) {
                    repository.removeObject(originalObject);
                }

                // UNDO/REDO
                //originalObject = object.getSDObject();
                //originalEdit = new ObjectEdit(originalObject, originalObject.getDesignClass().getName());
                //UndoableEdit e = new EditSDObjectEdit(originalEdit, undoEdit, model);
                //parentComponent.getUndoSupport().postEdit(e);
            }
        } else {
            repository.editObject(originalObject, newObject);
            // Undo/Redo
            originalEdit = new ObjectEdit(originalObject, originalObject.getDesignClass().getName());
            UndoableEdit edit = new EditSDObjectEdit(originalEdit, undoEdit, model);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void editMultiObject(MultiObjectGR multiObject) {
        CentralRepository repository = model.getCentralRepository();
        MultiObjectEditor multiObjectEditor = new MultiObjectEditor(multiObject, repository);
        MultiObject originalMultiObject = multiObject.getMultiObject();

        // UNDO/REDO
        MultiObject undoObject = new MultiObject(originalMultiObject.getName(), originalMultiObject.getDesignClass());
        MultiObjectEdit undoEdit = new MultiObjectEdit(undoObject, originalMultiObject.getDesignClass().getName());

        // show the multi object editor dialog and check whether the user has pressed cancel
        if (!multiObjectEditor.showDialog(parentComponent, "Multiobject Editor")) {
            return;
        }

        MultiObject newMultiObject = new MultiObject(multiObjectEditor.getName(), multiObjectEditor.getDesignClass());
        MultiObjectEdit originalEdit;

        // edit the multiobject if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalMultiObject.getName().equals(newMultiObject.getName())
                && (repository.getMultiObject(newMultiObject.getName()) != null)
                && !newMultiObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing multiobject with the given name already.\n"
                    + "Do you want this diagram object to refer to the existing one?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                multiObject.setMultiObject(repository.getMultiObject(newMultiObject.getName()));

                if (originalMultiObject.getName().equals("")) {
                    repository.removeMultiObject(originalMultiObject);
                }
            }
        } else {
            repository.editMultiObject(originalMultiObject, newMultiObject);

            // UNDO/REDO
            originalEdit = new MultiObjectEdit(originalMultiObject, originalMultiObject.getDesignClass().getName());
            UndoableEdit edit = new EditMultiObjectEdit(originalEdit, undoEdit, model);
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
        synchronized (this) {
            for (Object o : model.getGraphicalElements()) {
                if (o instanceof UMLNoteGR && ((UMLNoteGR) o).getTo().equals(selectedElement)) {
                    model.removeGraphicalElement((UMLNoteGR) o);
                }
            }
        }
        parentComponent.getUndoSupport().postEdit(edit);
        model.removeGraphicalElement(selectedElement);
    }
}
