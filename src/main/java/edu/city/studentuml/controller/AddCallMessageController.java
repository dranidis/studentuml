package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AddCallMessageController.java
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.AbstractSDObjectGR;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ConstantsGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import java.awt.geom.Point2D;
import java.util.Optional;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

public class AddCallMessageController extends AddElementController {

    private RoleClassifierGR source = null;

    public AddCallMessageController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        Point2D origin = new Point2D.Double(x, y);
        
        Optional<GraphicalElement> element = diagramModel.getGraphicalElements().stream()
                .filter(el -> el instanceof RoleClassifierGR && el.contains(origin))
                .findFirst();
        
        if (element.isPresent())
            source = (RoleClassifierGR) element.get();
    }

    @Override
    public void dragged(int x, int y) {
        // empty
    }

    @Override
    public void released(int x, int y) {
        if (source == null) {
            return;
        }
        Point2D origin = new Point2D.Double(x, y);
        
        Optional<GraphicalElement> element = diagramModel.getGraphicalElements().stream()
                .filter(el -> el instanceof AbstractSDObjectGR && el.contains(origin))
                .findFirst();
        
        if (element.isPresent()) {
            addCallMessage(source, (RoleClassifierGR) element.get(), y);
        } else {
            // set originating role classifier to null to start over again
            source = null;
        }
    }

    private void addCallMessage(RoleClassifierGR source, RoleClassifierGR target, int y) {
        CallMessage message = new CallMessage(source.getRoleClassifier(), target.getRoleClassifier(),
                new GenericOperation(""));
        CallMessageGR messageGR = new CallMessageGR(source, target, message, y);

        ReturnMessage returnMessage = new ReturnMessage(target.getRoleClassifier(), source.getRoleClassifier(), "");
        
        int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");
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
}
