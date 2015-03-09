package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.UCLink;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.UCDInternalFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Biser
 */
public abstract class AddUCLinkController extends AddElementController {

    private UCDComponentGR source;

    public AddUCLinkController(UCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
        source = null;
    }

    @Override
    public void pressed(int x, int y) {
        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if ((element != null) && (element instanceof UCDComponentGR)) {
            source = (UCDComponentGR) element;
        }
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
        if (source == null) {
            return;
        }

        GraphicalElement element = diagramModel.getContainingGraphicalElement(x, y);
        if ((element != null) && (element != source) && (element instanceof UCDComponentGR)) {
            UCDComponentGR target = (UCDComponentGR) element;

            // check that there is no other links between these two classifiers
            UCLink testOne = diagramModel.getCentralRepository().getUCLink(source.getUCDComponent(), target.getUCDComponent());
            UCLink testTwo = diagramModel.getCentralRepository().getUCLink(target.getUCDComponent(), source.getUCDComponent());
            if (testOne == null && testTwo == null) {
                addLink(source, target);
            } else {
                showErrorMessage("The link between these two classifiers already exists!");
                setSelectionMode();
            }

            // set source to null to start over again
            source = null;
        }
    }

    public abstract void addLink(UCDComponentGR src, UCDComponentGR trg);

    protected void setSelectionMode() {
        ((UCDInternalFrame) parentFrame).setSelectionMode();
    }

    protected void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(parentFrame,
                msg,
                "Use Case Link Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
