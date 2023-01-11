package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author draganbisercic
 */
public class UCDModel extends DiagramModel {

    public UCDModel(String title, UMLProject umlProject) {
        super(title, umlProject);
    }

    @Override
    public void addGraphicalElement(GraphicalElement element) {
        SystemWideObjectNamePool.getInstance().loading();
        if (element instanceof UCLinkGR) {
            addLink((UCLinkGR) element);
        } else if (element instanceof UCDComponentGR) {
            addUCDComponent((UCDComponentGR) element);
        } else if (element instanceof UMLNoteGR) {
            super.addGraphicalElement(element);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    private void addLink(UCLinkGR ucLinkGR) {
        // add if it doesnt already exist
        if (!graphicalElements.contains(ucLinkGR)) {
            UCDComponentGR source = ucLinkGR.getSource();
            UCDComponentGR target = ucLinkGR.getTarget();

            source.addOutgoingLink(ucLinkGR);
            target.addIncomingLink(ucLinkGR);

            repository.addUCLink(ucLinkGR.getLink());
            super.addGraphicalElement(ucLinkGR);
        }
    }

    private void addUCDComponent(UCDComponentGR ucdComponentGR) {
        UCDComponentGR context = this.findContext(ucdComponentGR);
        if (context != null) {
            // does not have a default context activity
            context.add(ucdComponentGR);
            ucdComponentGR.setContext(context);
            repository.addUCDComponent(ucdComponentGR.getComponent());
            SystemWideObjectNamePool.getInstance().objectAdded(ucdComponentGR);
            modelChanged();
            return;
        }

        ucdComponentGR.setContext(UCDComponentGR.DEFAULT_CONTEXT);
        repository.addUCDComponent(ucdComponentGR.getComponent());
        super.insertGraphicalElementAt(ucdComponentGR, getFirstLinkIndex());
    }

    // override superclass method removeGraphicalElement()
    @Override
    public void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof UCLinkGR) {
            removeLink((UCLinkGR) e);
        } else if (e instanceof UCDComponentGR) {
            removeUCDComponent((UCDComponentGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    private void removeLink(UCLinkGR ucLinkGR) {
        UCDComponentGR source = ucLinkGR.getSource();
        UCDComponentGR target = ucLinkGR.getTarget();

        source.removeOutgoingLink(ucLinkGR);
        target.removeIncomingLink(ucLinkGR);

        repository.removeLink(ucLinkGR.getLink());
        super.removeGraphicalElement(ucLinkGR);
    }

    private void removeUCDComponent(UCDComponentGR ucdComponentGR) {
        // remove containing elements
        int index = ucdComponentGR.getNumberOfElements() - 1;
        while (index >= 0) {
            UCDComponentGR comp = ucdComponentGR.getElement(index);
            removeUCDComponent(comp);
            // update index
            index = ucdComponentGR.getNumberOfElements() - 1;
        }

        // remove all the links to the element
        Iterator<UCLinkGR> incomingLinks = ucdComponentGR.getIncomingRelations().iterator();
        while (incomingLinks.hasNext()) {
            UCLinkGR link = incomingLinks.next();
            removeLink(link);
            // need to update iterator
            incomingLinks = ucdComponentGR.getIncomingRelations().iterator();
        }

        Iterator<UCLinkGR> outgoingLinks = ucdComponentGR.getOutgoingRelations();
        while (outgoingLinks.hasNext()) {
            UCLinkGR link = outgoingLinks.next();
            removeLink(link);
            // need to update iterator
            outgoingLinks = ucdComponentGR.getOutgoingRelations();
        }

        // and lastly remove the element
        UCDComponentGR context = ucdComponentGR.getContext();
        repository.removeUCDComponent(ucdComponentGR.getComponent());
        if (context == UCDComponentGR.DEFAULT_CONTEXT) {
            super.removeGraphicalElement(ucdComponentGR);
        } else {
            context.remove(ucdComponentGR);

            changeViewSize();

            modelChanged();
        }
    }

    // Override: needed because of the composite structure
    @Override
    public GraphicalElement getContainingGraphicalElement(Point2D point) {

        ListIterator<GraphicalElement> listIterator = graphicalElements.listIterator(graphicalElements.size());
        GraphicalElement element = null;

        while (listIterator.hasPrevious()) {
            element = listIterator.previous();

            if (element.contains(point)) {
                if (element instanceof UCDComponentGR) {
                    UCDComponentGR comp = (UCDComponentGR) element;
                    return comp.getContainingGraphicalElement(point);
                } else {
                    return element;
                }
            }
        }

        // if no element was found, return null
        return null;
    }

    /*
     * Gets the context system for the given element
     */
    public UCDComponentGR findContext(UCDComponentGR component) {

        Iterator<GraphicalElement> iterator = graphicalElements.iterator();
        GraphicalElement element = null;

        while (iterator.hasNext()) {
            element = iterator.next();

            if (element instanceof UCDComponentGR) {
                UCDComponentGR myComp = (UCDComponentGR) element;
                if (myComp.contains(component)) {
                    return myComp.findContext(component);
                }
            }
        }

        // if component has default context return null
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
        Iterator<GraphicalElement> iterator = graphicalElements.iterator();
        GraphicalElement element;

        while (iterator.hasNext()) {
            element = iterator.next();
            if (element instanceof UCDComponentGR) {
                UCDComponentGR comp = (UCDComponentGR) element;
                comp.clearSelected();
            } else {
                element.setSelected(false);
            }
        }
        if (!selected.isEmpty()) {
            selected.clear();
            modelChanged();
        }
    }

    /*
     * Override: need to take care of the context of the ucd component
     * after move is completed.
     */
    @Override
    public void moveGraphicalElement(GraphicalElement e, int x, int y) {
        e.move(x, y);
        if (e instanceof UCDComponentGR) {
            UCDComponentGR comp = (UCDComponentGR) e;
            UCDComponentGR oldContext = comp.getContext();
            UCDComponentGR newContext = findContext(comp);
            if (oldContext != newContext) {
                if (oldContext == UCDComponentGR.DEFAULT_CONTEXT) {
                    graphicalElements.remove(comp);
                    newContext.add(comp);
                    comp.setContext(newContext);
                    SystemWideObjectNamePool.getInstance().objectAdded(comp);
                } else if (newContext == UCDComponentGR.DEFAULT_CONTEXT) {
                    oldContext.remove(comp);
                    graphicalElements.insertElementAt(comp, getFirstLinkIndex());
                    comp.setContext(UCDComponentGR.DEFAULT_CONTEXT);
                    SystemWideObjectNamePool.getInstance().objectAdded(comp);
                } else {
                    oldContext.remove(comp);
                    newContext.add(comp);
                    comp.setContext(newContext);
                    SystemWideObjectNamePool.getInstance().objectAdded(comp);
                }
            }
        }

        changeViewSize();

        modelChanged();
    }

    private int getFirstLinkIndex() {
        int index;
        for (index = 0; index < graphicalElements.size(); index++) {
            GraphicalElement el = graphicalElements.get(index);
            if (el instanceof UCLinkGR) {
                return index;
            }
        }
        return index;
    }
}
