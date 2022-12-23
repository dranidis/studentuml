package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditSDObjectEdit;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.MultiObjectEditor;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.view.gui.ObjectEditor;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.util.undoredo.EditCreateMessageEdit;
import edu.city.studentuml.util.undoredo.EditMultiObjectEdit;
import edu.city.studentuml.util.undoredo.MultiObjectEdit;
import edu.city.studentuml.util.undoredo.ObjectEdit;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

//handles all events when the "selection" button in the SD toolbar is pressed
public class SDSelectionController extends AbstractSDSelectionController {

    private static final String WARNING = "Warning";

    public SDSelectionController(DiagramInternalFrame parent, SDModel m) {
        super(parent, m);
    }

    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof SDObjectGR) {
            editSDObject((SDObjectGR) selectedElement);
        } else if (selectedElement instanceof MultiObjectGR) {
            editMultiObject((MultiObjectGR) selectedElement);
        } else if (selectedElement instanceof CreateMessageGR) {
        	editCreateMessage((CreateMessageGR) selectedElement);
        // callMessage should be after create since create is a subclass
        } 
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
                && (repository.getObject(newObject.getName()) != null)
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

        MultiObject newMultiObject = new MultiObject(multiObjectEditor.getMultiObjectName(), multiObjectEditor.getDesignClass());
        MultiObjectEdit originalEdit;

        // edit the multiobject if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalMultiObject.getName().equals(newMultiObject.getName())
                && (repository.getMultiObject(newMultiObject.getName()) != null)
                && !newMultiObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing multiobject with the given name already.\n"
                    + "Do you want this diagram object to refer to the existing one?", WARNING,
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
        CallMessageEditor createMessageEditor = new CallMessageEditor(messageGR, model.getCentralRepository());
        CreateMessage message = messageGR.getCreateMessage();

        CreateMessage undoCreateMessage = message.clone();

        // if user presses cancel don't do anything
        if (!createMessageEditor.showDialog(parentComponent, "Create Message Editor")) {
            return;
        }


        List<MethodParameter> parameters = createMessageEditor.getParameters();
        Iterator<MethodParameter> iterator = parameters.iterator();
        message.setParameters(new Vector<>());
        while (iterator.hasNext()) {
            message.addParameter(iterator.next());
        }

        // UNDO/REDO
        UndoableEdit edit = new EditCreateMessageEdit(message, undoCreateMessage, model);
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
