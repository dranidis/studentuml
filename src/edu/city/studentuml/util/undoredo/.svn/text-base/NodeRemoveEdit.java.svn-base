package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import java.util.Iterator;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class NodeRemoveEdit extends MyCompoundEdit {

    protected NodeComponentGR node;
    protected DiagramModel model;

    public NodeRemoveEdit(NodeComponentGR node, DiagramModel model) {
        super();
        this.node = node;
        this.model = model;
        this.loadEdit(node);
        this.end();
    }

    private void loadEdit(NodeComponentGR nodeComponentGR) {
        // load node edits
        int index = nodeComponentGR.getNumberOfNodeComponents() - 1;
        while (index >= 0) {
            NodeComponentGR n = nodeComponentGR.getNodeComponent(index);
            loadEdit(n);
            // update index
            index--;
        }

        // load all the edges to and from the node
        Iterator incomingEdges = nodeComponentGR.getIncomingEdges();
        while (incomingEdges.hasNext()) {
            EdgeGR edge = (EdgeGR) incomingEdges.next();
            addEdit(new EdgeRemoveEdit(edge, model));
        }

        Iterator outgoingEdges = nodeComponentGR.getOutgoingEdges();
        while (outgoingEdges.hasNext()) {
            EdgeGR edge = (EdgeGR) outgoingEdges.next();
            addEdit(new EdgeRemoveEdit(edge, model));
        }

        // and lastly load the node
        addEdit(new NodeEdit(nodeComponentGR, model));
    }
}

class NodeEdit extends AbstractUndoableEdit {
    protected NodeComponentGR node;
    protected DiagramModel model;

    public NodeEdit(NodeComponentGR node, DiagramModel model) {
        this.node = node;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addGraphicalElement(node);
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeGraphicalElement(node);
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
        return ": delete " + node.getNodeComponent().getName();
    }
}

