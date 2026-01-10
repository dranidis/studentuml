package edu.city.studentuml.view;

import java.awt.Graphics2D;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;

/**
 * @author Biser
 */
public class ADView extends DiagramView {

    public ADView(DiagramModel model) {
        super(model);
    }

    @Override
    public void drawDiagram(Graphics2D g) {

        // draw edges after the target node is drawn
        lock.lock();

        // FIRST: Draw notes (bottom layer, won't obscure other elements)
        model.getGraphicalElements().stream()
                .filter(UMLNoteGR.class::isInstance)
                .forEach(ge -> ge.draw(g));

        // THEN: Draw nodes and edges (top layer)
        for (GraphicalElement element : model.getGraphicalElements()) {
            if (element instanceof UMLNoteGR) {
                continue; // Already drawn
            }
            if (element instanceof NodeComponentGR) {
                NodeComponentGR comp = (NodeComponentGR) element;
                comp.draw(g);

                comp.getIncomingRelations().forEach(link -> link.draw(g));

                comp.createIterator().forEachRemaining(el -> {
                    el.draw(g);
                    el.getIncomingRelations().forEach(link -> link.draw(g));
                });

            } else if (!(element instanceof EdgeGR)) { // already drawn
                element.draw(g);
            }
        }

        // ... finally draw the dragline and rectangle
        drawLineAndRectangle(g);

        lock.unlock();
    }
}
