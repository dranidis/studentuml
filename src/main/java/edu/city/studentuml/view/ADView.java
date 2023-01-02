package edu.city.studentuml.view;

import edu.city.studentuml.model.graphical.DiagramModel;
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

    public ADView(DiagramModel model) {
        super(model);
    }

    @Override
    public void drawDiagram(Graphics2D g) {

        // draw edges after the target node is drawn
        SystemWideObjectNamePool.drawLock.lock();

        Iterator<GraphicalElement> iterator = model.getGraphicalElements().iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = iterator.next();
            if (element instanceof EdgeGR) {
                // do nothing
            } else if (element instanceof NodeComponentGR) {
                NodeComponentGR node = (NodeComponentGR) element;
                node.draw(g);
                Iterator<EdgeGR> incomingEdges = node.getIncomingEdges();
                while (incomingEdges.hasNext()) {
                    EdgeGR edge = incomingEdges.next();
                    edge.draw(g);
                }

                Iterator<NodeComponentGR> subnodes = node.createIterator();
                while (subnodes.hasNext()) {
                    NodeComponentGR subnode = subnodes.next();
                    subnode.draw(g);

                    incomingEdges = subnode.getIncomingEdges();
                    while (incomingEdges.hasNext()) {
                        EdgeGR edge = incomingEdges.next();
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
