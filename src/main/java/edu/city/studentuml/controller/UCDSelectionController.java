package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditActorEdit;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.util.undoredo.EditSystemEdit;
import edu.city.studentuml.util.undoredo.EditUseCaseEdit;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.util.undoredo.EditUCExtendEdit;
import edu.city.studentuml.util.undoredo.RemoveEditFactory;
import edu.city.studentuml.view.gui.UCExtendEditor;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class UCDSelectionController extends SelectionController {

    public UCDSelectionController(DiagramInternalFrame parent, UCDModel model) {
        super(parent, model);
    }

    @Override
    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof UCActorGR) {
            editActor((UCActorGR) selectedElement);
        } else if (selectedElement instanceof UseCaseGR) {
            editUseCase((UseCaseGR) selectedElement);
        } else if (selectedElement instanceof SystemGR) {
            editSystem((SystemGR) selectedElement);
        } else if (selectedElement instanceof UCExtendGR) {
            editExtend((UCExtendGR) selectedElement);
        } else if (selectedElement instanceof UMLNoteGR) {
            editUMLNote((UMLNoteGR) selectedElement);
        }
    }

    private void editActor(UCActorGR uCActorGR) {
        CentralRepository repository = model.getCentralRepository();
        Actor originalActor = (Actor) uCActorGR.getUCDComponent();

        String actorName = JOptionPane.showInputDialog("Enter the Actor's Name",
                uCActorGR.getUCDComponent().getName());

        if (actorName == null) {    // user canceled
            return;
        }

        Actor newActor = new Actor(actorName);

        if (!originalActor.getName().equals(newActor.getName())
                && (repository.getActor(newActor.getName()) != null)
                && !newActor.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing actor with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
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
        UseCase originalUseCase = (UseCase) useCaseGR.getUCDComponent();

        String useCaseName = JOptionPane.showInputDialog("Enter the Use Case's Name",
                originalUseCase.getName());

        if (useCaseName == null) {
            // cancel clicked
            return;
        }

        UseCase newUseCase = new UseCase(useCaseName);

        if (!originalUseCase.getName().equals(newUseCase.getName())
                && (repository.getUseCase(newUseCase.getName()) != null)
                && !newUseCase.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing use case with the same name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
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
        System originalSystem = (System) systemGR.getUCDComponent();

        String systemName = JOptionPane.showInputDialog("Enter the System's Name",
                systemGR.getUCDComponent().getName());

        if (systemName == null) {    // user canceled
            return;
        }

        System newSystem = new System(systemName);

        if (!originalSystem.getName().equals(newSystem.getName())
                && (repository.getSystem(newSystem.getName()) != null)
                && !newSystem.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing system with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
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
        UCExtendEditor ucExtendEditor = new UCExtendEditor(link);
        if (!ucExtendEditor.showDialog(parentComponent, "Use Case Extend Editor")) {
            return;
        }

        UCExtend newUCExtend = new UCExtend((UseCase) originalUCExtend.getSource(),
                (UseCase) originalUCExtend.getTarget());
        Iterator i = ucExtendEditor.getExtensionPoints();
        while (i.hasNext()) {
            newUCExtend.addExtensionPoint(((ExtensionPoint) i.next()).clone());
        }

        // Undo/Redo [edit]
        UndoableEdit edit = new EditUCExtendEdit(originalUCExtend, newUCExtend, model);
        model.getCentralRepository().editUCExtend(originalUCExtend, newUCExtend);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editUMLNote(UMLNoteGR noteGR) {
        UMLNoteEditor noteEditor = new UMLNoteEditor(noteGR);

        // Undo/Redo [edit note]
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

    @Override
    public void deleteElement(GraphicalElement selectedElement) {
        UndoableEdit edit = RemoveEditFactory.getInstance().createRemoveEdit(selectedElement, model);
        synchronized (this) {
            for (Object o : model.getGraphicalElements()) {
                if (o instanceof UMLNoteGR && ((UMLNoteGR) o).getTo().equals(selectedElement)) {
                    model.removeGraphicalElement((UMLNoteGR) o);
                }
            }
        }
        model.removeGraphicalElement(selectedElement);
        parentComponent.setSelectionMode();

        parentComponent.getUndoSupport().postEdit(edit);
    }
}
