package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditSystemInstanceEdit;
import edu.city.studentuml.util.undoredo.SystemEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SystemInstanceEditor;

/**
 *
 * @author draganbisercic
 */
public class SSDSelectionController extends AbstractSDSelectionController {

    public SSDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
    }

    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof SystemInstanceGR) {
            editSystemInstance((SystemInstanceGR) selectedElement);
        } 
    }

    public void editSystemInstance(SystemInstanceGR systemInstanceGR) {
        CentralRepository repository = model.getCentralRepository();
        SystemInstanceEditor systemEditor = new SystemInstanceEditor(systemInstanceGR, repository);
        SystemInstance originalSystemInstance = systemInstanceGR.getSystemInstance();

        // UNDO/REDO
        SystemInstance undoSystemInstance = new SystemInstance(originalSystemInstance.getName(),
                originalSystemInstance.getSystem());
        SystemEdit undoEdit = new SystemEdit(undoSystemInstance, originalSystemInstance.getSystem().getName());

        // show the system instance editor dialog and check whether the user has pressed
        // cancel
        if (!systemEditor.showDialog(parentComponent, "System Instance Editor")) {
            return;
        }

        SystemInstance newSystemInstance = new SystemInstance(systemEditor.getSystemName(), systemEditor.getSystem());
        SystemEdit originalEdit;

        // edit the system instance if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalSystemInstance.getName().equals(newSystemInstance.getName())
                && (repository.getSystemInstance(newSystemInstance.getName()) != null)
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

            // UNDO/REDO
            originalEdit = new SystemEdit(originalSystemInstance, originalSystemInstance.getSystem().getName());
            UndoableEdit edit = new EditSystemInstanceEdit(originalEdit, undoEdit, model);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
