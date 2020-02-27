package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.UseCaseGR;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddUCExtendController extends AddUCLinkController {

    UseCaseGR useCaseSource;
    UseCaseGR useCaseTarget;

    public AddUCExtendController(UCDModel model, DiagramInternalFrame frame) {
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

        UCExtend useCaseExtend = new UCExtend((UseCase) useCaseSource.getUCDComponent(), (UseCase) useCaseTarget.getUCDComponent());
        UCExtendGR extendGR = new UCExtendGR(useCaseSource, useCaseTarget, useCaseExtend);
        extendGR.addExtensionPoint(new ExtensionPoint("New Extension Point"));

        UndoableEdit edit = new AddEdit(extendGR, diagramModel);

        diagramModel.addGraphicalElement(extendGR);
        setSelectionMode();

        parentFrame.getUndoSupport().postEdit(edit);
    }
}
