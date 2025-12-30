package edu.city.studentuml.controller;

import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditCreateMessageEdit;
import edu.city.studentuml.util.undoredo.EditMultiObjectEdit;
import edu.city.studentuml.util.undoredo.EditSDObjectEdit;
import edu.city.studentuml.util.undoredo.MultiObjectEdit;
import edu.city.studentuml.util.undoredo.ObjectEdit;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.MultiObjectEditor;
import edu.city.studentuml.view.gui.ObjectEditor;

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
        ObjectEditor objectEditor = new ObjectEditor(object, repository);
        SDObject originalObject = object.getSDObject();

        // UNDO/REDO
        SDObject undoObject = new SDObject(originalObject.getName(), originalObject.getDesignClass());
        ObjectEdit undoEdit = new ObjectEdit(undoObject, originalObject.getDesignClass().getName());

        // show the object editor dialog and check whether the user has pressed cancel
        if (!objectEditor.showDialog(parentComponent, "Object Editor")) {
            return;
        }

        SDObject newObject = new SDObject(objectEditor.getObjectName(), objectEditor.getDesignClass());
        ObjectEdit originalEdit;

        // edit the object if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
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

        MultiObject newMultiObject = new MultiObject(multiObjectEditor.getMultiObjectName(),
                multiObjectEditor.getDesignClass());
        MultiObjectEdit originalEdit;

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

            // UNDO/REDO
            originalEdit = new MultiObjectEdit(originalMultiObject, originalMultiObject.getDesignClass().getName());
            UndoableEdit edit = new EditMultiObjectEdit(originalEdit, undoEdit, model);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    //new edit create method 
    public void editCreateMessage(CreateMessageGR messageGR) {
        CreateMessage message = messageGR.getCreateMessage();
        CallMessageEditor createMessageEditor = new CallMessageEditor(parentComponent, "Create Message Editor",
                message, model.getCentralRepository());

        CreateMessage undoCreateMessage = message.clone();

        // if user presses cancel don't do anything
        if (!createMessageEditor.showDialog()) {
            return;
        }

        List<MethodParameter> parameters = createMessageEditor.getParameters();
        message.setParameters(new Vector<>());
        message.getParameters().addAll(parameters);

        // UNDO/REDO
        UndoableEdit edit = new EditCreateMessageEdit(message, undoCreateMessage, model);
        parentComponent.getUndoSupport().postEdit(edit);

        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
