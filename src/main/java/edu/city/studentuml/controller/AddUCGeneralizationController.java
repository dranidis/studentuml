package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUCGeneralizationController extends AddUCLinkController {

    private UCDComponentGR source;
    private UCDComponentGR target;

    public AddUCGeneralizationController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void addLink(UCDComponentGR src, UCDComponentGR trg) {
        if (src instanceof UseCaseGR) {
            source = src;
            if (trg instanceof UseCaseGR) {
                target = trg;

                UCGeneralization generalization = new UCGeneralization((UseCase) source.getComponent(), (UseCase) target.getComponent());
                UCGeneralizationGR generalizationGR = new UCGeneralizationGR((UseCaseGR) source, (UseCaseGR) target, generalization);

                UndoableEdit edit = new AddEdit(generalizationGR, diagramModel);

                diagramModel.addGraphicalElement(generalizationGR);

                parentFrame.setSelectionMode();

                parentFrame.getUndoSupport().postEdit(edit);
            } else {
                // generalization from use case to use case
                showErrorMessage("The source element is a use case but "
                        + "the target element is not a use case!");
                setSelectionMode();
                return;
            }
        } else if (src instanceof UCActorGR) {
            source = src;
            if (trg instanceof UCActorGR) {
                target = trg;

                UCGeneralization generalization = new UCGeneralization((Actor) source.getComponent(), (Actor) target.getComponent());
                UCGeneralizationGR generalizationGR = new UCGeneralizationGR((UCActorGR) source, (UCActorGR) target, generalization);

                UndoableEdit edit = new AddEdit(generalizationGR, diagramModel);

                diagramModel.addGraphicalElement(generalizationGR);

                parentFrame.setSelectionMode();

                parentFrame.getUndoSupport().postEdit(edit);
            } else {
                // generalization from actor to actor
                showErrorMessage("The source element is an actor but "
                        + "the target element is not an actor!");
                setSelectionMode();
                return;
            }
        } else {
            // nothing
        }
    }
}
