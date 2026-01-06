package edu.city.studentuml.view;

import java.awt.Graphics2D;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCLinkGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;

public class UCDView extends DiagramView {

    public UCDView(DiagramModel m) {
        super(m);
    }

    @Override
    public void drawDiagram(Graphics2D g) {

        // draw links after
        lock.lock();

        // FIRST: Draw notes (bottom layer, won't obscure other elements)
        model.getGraphicalElements().stream()
                .filter(UMLNoteGR.class::isInstance)
                .forEach(ge -> ge.draw(g));

        // THEN: Draw components and links (top layer)
        for (GraphicalElement element : model.getGraphicalElements()) {
            if (element instanceof UMLNoteGR) {
                continue; // Already drawn
            }
            if (element instanceof UCDComponentGR) {
                UCDComponentGR comp = (UCDComponentGR) element;
                comp.draw(g);

                comp.getIncomingRelations().forEach(link -> link.draw(g));

                comp.createIterator().forEachRemaining(el -> {
                    el.draw(g);
                    el.getIncomingRelations().forEach(link -> link.draw(g));
                });

            } else if (!(element instanceof UCLinkGR)) { // links are already drawn
                element.draw(g);
            }
        }

        // ... finally draw the dragline and rectangle
        drawLineAndRectangle(g);

        lock.unlock();
    }
}
