package edu.city.studentuml.view;

import java.awt.Graphics2D;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;

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

        for (GraphicalElement element : model.getGraphicalElements()) {
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
