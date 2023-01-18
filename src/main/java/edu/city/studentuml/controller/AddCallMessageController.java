package edu.city.studentuml.controller;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ConstantsGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author Ervin Ramollari
 */
public class AddCallMessageController extends AddSDLinkController {

    public AddCallMessageController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    /**
     * Responds to CTRL-released.
     * Creates a call message and its return message
     */
    @Override
    protected void addCompoundRelationship(RoleClassifierGR source, RoleClassifierGR target, int y) {
        SDMessageGR messageGR = createRelationship(source, target, y);
        int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");

        ReturnMessage returnMessage = new ReturnMessage(target.getRoleClassifier(), source.getRoleClassifier(), "");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(target, source, returnMessage, y + barHeight);
        
        /**
         * fix undo
         */
        CompoundEdit compoundEdit = new CompoundEdit();
        UndoableEdit edit = new AddEdit(messageGR, returnMessageGR, diagramModel);
        compoundEdit.addEdit(edit);

        ((AbstractSDModel) diagramModel).setAutomove(true);
        ((AbstractSDModel) diagramModel).setCompoundEdit(compoundEdit);
        // handle the rest of addition details to the diagram model
        diagramModel.addGraphicalElement(messageGR);
        diagramModel.addGraphicalElement(returnMessageGR);
        ((AbstractSDModel) diagramModel).setAutomove(false);

        parentFrame.setSelectionMode();

        compoundEdit.end();
        parentFrame.getUndoSupport().postEdit(compoundEdit);
        ((AbstractSDModel) diagramModel).setCompoundEdit(null);

    }

    @Override
    protected SDMessageGR createRelationship(RoleClassifierGR roleA, RoleClassifierGR roleB, int y) {
        CallMessage message = new CallMessage(roleA.getRoleClassifier(), roleB.getRoleClassifier(),
                new GenericOperation(""));
        return new CallMessageGR(roleA, roleB, message, y);
    }
}
