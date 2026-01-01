package edu.city.studentuml.controller;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditActorEdit;
import edu.city.studentuml.util.undoredo.EditSystemEdit;
import edu.city.studentuml.util.undoredo.EditUCExtendEdit;
import edu.city.studentuml.util.undoredo.EditUseCaseEdit;
import edu.city.studentuml.view.gui.ActorEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.StringEditorDialog;
import edu.city.studentuml.view.gui.UCExtendEditor;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCDSelectionController extends SelectionController {

    private static final String CANNOT_EDIT = "Cannot Edit";

    public UCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);
        editElementMapper.put(UCActorGR.class, e -> editActor((UCActorGR) e));
        editElementMapper.put(UseCaseGR.class, e -> editUseCase((UseCaseGR) e));
        editElementMapper.put(SystemGR.class, e -> editSystem((SystemGR) e));
        editElementMapper.put(UCExtendGR.class, e -> editExtend((UCExtendGR) e));
    }

    private void editActor(UCActorGR uCActorGR) {
        CentralRepository repository = model.getCentralRepository();
        Actor originalActor = (Actor) uCActorGR.getComponent();
        ActorEditor actorEditor = new ActorEditor(repository);

        Actor newActor = actorEditor.editDialog(originalActor, parentComponent);
        if (newActor == null) {
            return;
        }

        if (!originalActor.getName().equals(newActor.getName()) && repository.getActor(newActor.getName()) != null
                && !newActor.getName().equals("")) {
            JOptionPane.showMessageDialog(null, "There is an existing actor with the given name already!\n",
                    CANNOT_EDIT, JOptionPane.ERROR_MESSAGE);
        } else {
            // Undo/Redo [edit]
            UndoableEdit edit = new EditActorEdit(originalActor, newActor, model);
            repository.editActor(originalActor, newActor);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editUseCase(UseCaseGR useCaseGR) {
        CentralRepository repository = model.getCentralRepository();
        UseCase originalUseCase = (UseCase) useCaseGR.getComponent();

        StringEditorDialog stringEditorDialog = new StringEditorDialog(parentComponent,
                "Use Case Editor", "Use Case Name:", originalUseCase.getName());

        if (!stringEditorDialog.showDialog()) {
            // cancel clicked
            return;
        }

        UseCase newUseCase = new UseCase(stringEditorDialog.getText());

        if (!originalUseCase.getName().equals(newUseCase.getName())
                && repository.getUseCase(newUseCase.getName()) != null && !newUseCase.getName().equals("")) {
            JOptionPane.showMessageDialog(null, "There is an existing use case with the same name already!\n",
                    CANNOT_EDIT, JOptionPane.ERROR_MESSAGE);
        } else {
            // Undo/Redo [edit]
            UndoableEdit edit = new EditUseCaseEdit(originalUseCase, newUseCase, model);
            repository.editUseCase(originalUseCase, newUseCase);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editSystem(SystemGR systemGR) {
        CentralRepository repository = model.getCentralRepository();
        System originalSystem = (System) systemGR.getComponent();

        StringEditorDialog stringEditorDialog = new StringEditorDialog(parentComponent,
                "System Editor", "System Name:", originalSystem.getName());

        if (!stringEditorDialog.showDialog()) {
            // cancel clicked
            return;
        }

        System newSystem = new System(stringEditorDialog.getText());

        if (!originalSystem.getName().equals(newSystem.getName()) && repository.getSystem(newSystem.getName()) != null
                && !newSystem.getName().equals("")) {
            JOptionPane.showMessageDialog(null, "There is an existing system with the given name already!\n",
                    CANNOT_EDIT, JOptionPane.ERROR_MESSAGE);
        } else {
            // Undo/Redo [edit]
            UndoableEdit edit = new EditSystemEdit(originalSystem, newSystem, model);
            repository.editSystem(originalSystem, newSystem);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
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
