package edu.city.studentuml.view;

import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class ADView extends DiagramView {

    public ADView(ADModel model) {
        super(model);
    }

    @Override
    public void drawDiagram(Graphics2D g) {

        // draw edges after the target node is drawn
        SystemWideObjectNamePool.drawLock.lock();

        Iterator iterator = model.getGraphicalElements().iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = (GraphicalElement) iterator.next();
            if (element instanceof EdgeGR) {
                // do nothing
            } else if (element instanceof NodeComponentGR) {
                NodeComponentGR node = (NodeComponentGR) element;
                node.draw(g);
                Iterator incomingEdges = node.getIncomingEdges();
                while (incomingEdges.hasNext()) {
                    EdgeGR edge = (EdgeGR) incomingEdges.next();
                    edge.draw(g);
                }

                Iterator subnodes = node.createIterator();
                while (subnodes.hasNext()) {
                    NodeComponentGR subnode = (NodeComponentGR) subnodes.next();
                    subnode.draw(g);

                    incomingEdges = subnode.getIncomingEdges();
                    while (incomingEdges.hasNext()) {
                        EdgeGR edge = (EdgeGR) incomingEdges.next();
                        edge.draw(g);
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
