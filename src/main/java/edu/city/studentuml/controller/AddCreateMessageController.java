package edu.city.studentuml.controller;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.ConstantsGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SDInternalFrame;

/**
 * @author Ervin Ramollari
 * @author Dimitris Dranidis
 */
public class AddCreateMessageController extends AddSDLinkController {

    public AddCreateMessageController(SDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void addCompoundRelationship(RoleClassifierGR source, RoleClassifierGR target, int y) {
        SDMessageGR messageGR = createRelationship(source, target, y);

        int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");
        ReturnMessage returnMessage = new ReturnMessage(target.getRoleClassifier(), source.getRoleClassifier(), "");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(target, source, returnMessage,
                y + barHeight + target.getHeight());

        CompoundEdit compoundEdit = new CompoundEdit();
        UndoableEdit edit = new AddEdit(messageGR, returnMessageGR, diagramModel);
        compoundEdit.addEdit(edit);

        ((AbstractSDModel) diagramModel).setCompoundEdit(compoundEdit);
        ((AbstractSDModel) diagramModel).setAutomove(true);
        diagramModel.addGraphicalElement(messageGR);
        diagramModel.addGraphicalElement(returnMessageGR);
        ((AbstractSDModel) diagramModel).setAutomove(false);

        if (parentFrame instanceof SDInternalFrame) {
            ((SDInternalFrame) parentFrame).setSelectionMode();
        }

        compoundEdit.end();
        ((AbstractSDModel) diagramModel).setCompoundEdit(null);
        parentFrame.getUndoSupport().postEdit(compoundEdit);
    }

    @Override
    protected SDMessageGR createRelationship(RoleClassifierGR roleA, RoleClassifierGR roleB, int y) {
        CreateMessage message = new CreateMessage(roleA.getRoleClassifier(), roleB.getRoleClassifier());
        return new CreateMessageGR(roleA, roleB, message, y);
    }
}
