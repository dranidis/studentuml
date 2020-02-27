package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.AbstractPointGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Biser
 */
public class MoveEdgeEdit extends AbstractUndoableEdit {

    private EdgeGR edge;
    private List<AbstractPointGR> undoPoints;
    private List<AbstractPointGR> redoPoints;
    private DiagramModel model;

    public MoveEdgeEdit(EdgeGR edge, List<AbstractPointGR> undoPoints, List<AbstractPointGR> redoPoints, DiagramModel model) {
        this.edge = edge;
        this.undoPoints = undoPoints;
        this.redoPoints = redoPoints;
        this.model = model;
    }

    @Override
    public void undo() throws CannotUndoException {
        move(undoPoints);
    }

    @Override
    public void redo() throws CannotRedoException {
        move(redoPoints);
    }

    private void move(List<AbstractPointGR> points) {
        edge.clearPoints();
        Iterator it = points.iterator();
        while (it.hasNext()) {
            edge.addPoint((AbstractPointGR) it.next());
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
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
        return ": move edge point";
    }
}
