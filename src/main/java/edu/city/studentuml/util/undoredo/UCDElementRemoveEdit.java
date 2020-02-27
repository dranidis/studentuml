package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCLinkGR;
import java.util.Iterator;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class UCDElementRemoveEdit extends MyCompoundEdit {

    protected UCDComponentGR ucdComponentGR;
    protected DiagramModel model;

    public UCDElementRemoveEdit(UCDComponentGR ucdComponentGR, DiagramModel model) {
        super();
        this.ucdComponentGR = ucdComponentGR;
        this.model = model;
        this.loadEdit(ucdComponentGR);
        this.end();
    }

    private void loadEdit(UCDComponentGR ucdComponentGR) {
        // load ucd element edits
        int index = ucdComponentGR.getNumberOfElements() - 1;
        while (index >= 0) {
            UCDComponentGR n = ucdComponentGR.getElement(index);
            loadEdit(n);
            // update index
            index--;
        }

        // load all the links to and from the element
        Iterator incomingLinks = ucdComponentGR.getIncomingLinks();
        while (incomingLinks.hasNext()) {
            UCLinkGR link = (UCLinkGR) incomingLinks.next();
            addEdit(new UCLinkRemoveEdit(link, model));
        }

        Iterator outgoingLinks = ucdComponentGR.getOutgoingLinks();
        while (outgoingLinks.hasNext()) {
            UCLinkGR link = (UCLinkGR) outgoingLinks.next();
            addEdit(new UCLinkRemoveEdit(link, model));
        }
        
        // and lastly load the selected element
        addEdit(new ElementEdit(ucdComponentGR, model));
    }
}

class ElementEdit extends AbstractUndoableEdit {
    protected UCDComponentGR element;
    protected DiagramModel model;

    public ElementEdit(UCDComponentGR element, DiagramModel model) {
        this.element = element;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(element);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(element);
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return ": delete " + element.getUCDComponent().getName();
    }
}
