package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditUCExtendEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UCExtendEditor;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCDSelectionController extends SelectionController {

    public UCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(UCExtendGR.class, e -> editExtend((UCExtendGR) e));
    }

    private void editExtend(UCExtendGR link) {
        UCExtend originalUCExtend = (UCExtend) link.getLink();
        UCExtendEditor ucExtendEditor = new UCExtendEditor(model.getCentralRepository());

        UCExtend newUCExtend = ucExtendEditor.editDialog(originalUCExtend, parentComponent);
        if (newUCExtend == null) {
            return;
        }

        // Undo/Redo [edit]
        UndoableEdit edit = new EditUCExtendEdit(originalUCExtend, newUCExtend, model);
        model.getCentralRepository().editUCExtend(originalUCExtend, newUCExtend);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
