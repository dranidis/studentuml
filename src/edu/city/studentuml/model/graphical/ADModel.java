package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author Biser
 */
public class ADModel extends DiagramModel {

    public ADModel(String title, UMLProject project) {
        super(title, project);
    }

    @Override
    public void addGraphicalElement(GraphicalElement element) {
        SystemWideObjectNamePool.getInstance().loading();
        if (element instanceof EdgeGR) {
            addEdge((EdgeGR) element);
        } else if (element instanceof NodeComponentGR) {
            addNodeComponent((NodeComponentGR) element);
        } else if (element instanceof UMLNoteGR) {
            super.addGraphicalElement(element);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    private void addEdge(EdgeGR edgeGR) {
        // add if it doesnt already exist
        if (!graphicalElements.contains(edgeGR)) {
            NodeComponentGR source = edgeGR.getSource();
            NodeComponentGR target = edgeGR.getTarget();

            source.addOutgoingEdge(edgeGR);
            target.addIncomingEdge(edgeGR);

            // add the control flow to the project repository and to the diagram
            repository.addEdge(edgeGR.getEdge());
            super.addGraphicalElement(edgeGR);
        }
    }

    private void addNodeComponent(NodeComponentGR nodeComponentGR) {
        NodeComponentGR context = this.findContext(nodeComponentGR);
        if (context != null) {
            // does not have a default context activity
            context.add(nodeComponentGR);
            nodeComponentGR.setContext(context);
            repository.addNodeComponent(nodeComponentGR.getNodeComponent());
            SystemWideObjectNamePool.getInstance().objectAdded(nodeComponentGR);
            modelChanged();
            return;
        }

        nodeComponentGR.setContext(NodeComponentGR.DEFAULT_CONTEXT);
        repository.addNodeComponent(nodeComponentGR.getNodeComponent());
        super.insertGraphicalElementAt(nodeComponentGR, getFirstEdgeIndex());
    }

    // override superclass method removeGraphicalElement()
    @Override
    public void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof EdgeGR) {
            removeEdge((EdgeGR) e);
        } else if (e instanceof NodeComponentGR) {
            removeNodeComponent((NodeComponentGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    private void removeEdge(EdgeGR edgeGR) {
        NodeComponentGR source = edgeGR.getSource();
        NodeComponentGR target = edgeGR.getTarget();

        source.removeOutgoingEdge(edgeGR);
        target.removeIncomingEdge(edgeGR);

        repository.removeEdge(edgeGR.getEdge());
        super.removeGraphicalElement(edgeGR);
    }

    private void removeNodeComponent(NodeComponentGR nodeComponentGR) {
        // remove containing elements
        int index = nodeComponentGR.getNumberOfNodeComponents() - 1;
        while (index >= 0) {
            NodeComponentGR node = nodeComponentGR.getNodeComponent(index);
            removeNodeComponent(node);
            // update index
            index = nodeComponentGR.getNumberOfNodeComponents() - 1;
        }

        // remove all the edges to the node
        Iterator incomingEdges = nodeComponentGR.getIncomingEdges();
        while (incomingEdges.hasNext()) {
            EdgeGR edge = (EdgeGR) incomingEdges.next();
            removeEdge(edge);
            // need to update iterator
            incomingEdges = nodeComponentGR.getIncomingEdges();
        }

        Iterator outgoingEdges = nodeComponentGR.getOutgoingEdges();
        while (outgoingEdges.hasNext()) {
            EdgeGR edge = (EdgeGR) outgoingEdges.next();
            removeEdge(edge);
            // need to update iterator
            outgoingEdges = nodeComponentGR.getOutgoingEdges();
        }

        // and lastly remove the node
        NodeComponentGR context = nodeComponentGR.getContext();
        repository.removeNodeComponent(nodeComponentGR.getNodeComponent());
        if (context == NodeComponentGR.DEFAULT_CONTEXT) {
            super.removeGraphicalElement(nodeComponentGR);
        } else {
            context.remove(nodeComponentGR);
            modelChanged();
        }
    }

    // Override: needed because of the composite structure
    @Override
    public GraphicalElement getContainingGraphicalElement(Point2D point) {

        ListIterator listIterator = graphicalElements.listIterator(graphicalElements.size());
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = (GraphicalElement) listIterator.previous();

            if (element.contains(point)) {
                if (element instanceof NodeComponentGR) {
                    NodeComponentGR node = (NodeComponentGR) element;
                    return node.getContainingGraphicalElement(point);
                } else {
                    return element;
                }
            }
        }

        // if no element was found, return null
        return null;
    }

    /*
     * Gets the context activity for the given node
     */
    public NodeComponentGR findContext(NodeComponentGR node) {

        Iterator iterator = graphicalElements.iterator();
        GraphicalElement element = null;

        while (iterator.hasNext()) {
            element = (GraphicalElement) iterator.next();

            if (element instanceof NodeComponentGR) {
                NodeComponentGR myNode = (NodeComponentGR) element;
                if (myNode.contains(node)) {
                    return myNode.findContext(node);
                }
            }
        }

        // if node has default context return null
        return null;
    }

    @Override
    public void selectGraphicalElement(GraphicalElement el) {
        selected.add(el);
        el.setSelected(true);
        modelChanged();
    }

    // Override: needed because of the composite structure
    @Override
    public void clearSelected() {
        Iterator iterator = graphicalElements.iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = (GraphicalElement) iterator.next();
            if (element instanceof NodeComponentGR) {
                NodeComponentGR node = (NodeComponentGR) element;
                node.clearSelected();
            } else {
                element.setSelected(false);
            }
        }

        if (!selected.isEmpty()) {
            selected.clear();
            modelChanged();
        }
    }

    @Override
    public void clear() {

        while (graphicalElements.size() > 0) {
            removeGraphicalElement((GraphicalElement) graphicalElements.get(0));
        }

        super.clear();
    }

    /*
     * Override: need to take care of the context of the node component
     * after move is completed.
     */
    @Override
    public void moveGraphicalElement(GraphicalElement e, int x, int y) {
        e.move(x, y);
        if (e instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) e;
            NodeComponentGR oldContext = node.getContext();
            NodeComponentGR newContext = findContext(node);
            if (oldContext != newContext) {
                if (oldContext == NodeComponentGR.DEFAULT_CONTEXT) {
                    graphicalElements.remove(node);
                    newContext.add(node);
                    node.setContext(newContext);
                    SystemWideObjectNamePool.getInstance().objectAdded(node);
                } else if (newContext == NodeComponentGR.DEFAULT_CONTEXT) {
                    oldContext.remove(node);
                    graphicalElements.insertElementAt(node, getFirstEdgeIndex());
                    node.setContext(NodeComponentGR.DEFAULT_CONTEXT);
                    SystemWideObjectNamePool.getInstance().objectAdded(node);
                } else {
                    oldContext.remove(node);
                    newContext.add(node);
                    node.setContext(newContext);
                    SystemWideObjectNamePool.getInstance().objectAdded(node);
                }
            }
        }
        modelChanged();
    }

    private int getFirstEdgeIndex() {
        int index;
        for (index = 0; index < graphicalElements.size(); index++) {
            GraphicalElement el = (GraphicalElement) graphicalElements.get(index);
            if (el instanceof EdgeGR) {
                return index;
            }
        }
        return index;
    }
}
