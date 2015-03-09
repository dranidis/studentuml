package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUCIncludeController extends AddUCLinkController {

    UseCaseGR useCaseSource;
    UseCaseGR useCaseTarget;

    public AddUCIncludeController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void addLink(UCDComponentGR src, UCDComponentGR trg) {
        if (src instanceof UseCaseGR) {
            useCaseSource = (UseCaseGR) src;
        } else {
            // association has to be from actor to use case
            showErrorMessage("The source element is not a use case!");
            setSelectionMode();
            return;
        }

        if (trg instanceof UseCaseGR) {
            useCaseTarget = (UseCaseGR) trg;
        } else {
            // association has to be from actor to use case
            showErrorMessage("The target element is not a use case!");
            setSelectionMode();
            return;
        }

        UCInclude useCaseInclude = new UCInclude((UseCase) useCaseSource.getUCDComponent(), (UseCase) useCaseTarget.getUCDComponent());
        UCIncludeGR includeGR = new UCIncludeGR(useCaseSource, useCaseTarget, useCaseInclude);

        UndoableEdit edit = new AddEdit(includeGR, diagramModel);

        diagramModel.addGraphicalElement(includeGR);
        setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}
