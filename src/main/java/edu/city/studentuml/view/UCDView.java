package edu.city.studentuml.view;

import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UCLinkGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

public class UCDView extends DiagramView {

    public UCDView(UCDModel m) {
        super(m);
    }

    @Override
    public void drawDiagram(Graphics2D g) {

        // draw links after
        SystemWideObjectNamePool.drawLock.lock();

        Iterator<GraphicalElement> iterator = model.getGraphicalElements().iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = iterator.next();
            if (element instanceof UCLinkGR) {
                // do nothing
            } else if (element instanceof UCDComponentGR) {
                UCDComponentGR comp = (UCDComponentGR) element;
                comp.draw(g);
                Iterator<UCLinkGR> incomingLinks = comp.getIncomingLinks();
                while (incomingLinks.hasNext()) {
                    UCLinkGR link = incomingLinks.next();
                    link.draw(g);
                }

                Iterator<UCDComponentGR> elements = comp.createIterator();
                while (elements.hasNext()) {
                    UCDComponentGR el = elements.next();
                    el.draw(g);

                    incomingLinks = el.getIncomingLinks();
                    while (incomingLinks.hasNext()) {
                        UCLinkGR link = incomingLinks.next();
                        link.draw(g);
                    }
                }
            } else {
                element.draw(g);
            }
        }

        g.setPaint(Color.GRAY);
        g.draw(dragLine);

        SystemWideObjectNamePool.drawLock.unlock();
    }
}
