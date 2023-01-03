package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUCAssociationController extends AddUCLinkController {

    UCActorGR actor;
    UseCaseGR useCase;

    public AddUCAssociationController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void addLink(UCDComponentGR src, UCDComponentGR trg) {
        if (src instanceof UCActorGR) {
            actor = (UCActorGR) src;
        } else {
            // association has to be from actor to use case
            showErrorMessage("The source element is not an actor!");
            setSelectionMode();
            return;
        }

        if (trg instanceof UseCaseGR) {
            useCase = (UseCaseGR) trg;
        } else {
            // association has to be from actor to use case
            showErrorMessage("The target element is not a use case!");
            setSelectionMode();
            return;
        }

        UCAssociation association = new UCAssociation((Actor) actor.getComponent(), (UseCase) useCase.getComponent());
        UCAssociationGR associationGR = new UCAssociationGR(actor, useCase, association);

        UndoableEdit edit = new AddEdit(associationGR, diagramModel);

        diagramModel.addGraphicalElement(associationGR);
        setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}
