package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.CompositeUCDElement;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 *
 * @author draganbisercic
 */
public abstract class CompositeUCDElementGR extends UCDComponentGR {

    private static final Logger logger = Logger.getLogger(CompositeUCDElementGR.class.getName());

    protected List<UCDComponentGR> components;

    protected CompositeUCDElementGR(CompositeUCDElement compositeElement, int x, int y) {
        super(compositeElement, x, y);
        components = new ArrayList<>();
    }

    public List<UCDComponentGR> getComponents() {
        return components;
    }

    @Override
    public void add(UCDComponentGR componentGR) {
        components.add(componentGR);
        component.add(componentGR.getComponent());
    }

    @Override
    public void remove(UCDComponentGR componentGR) {
        components.remove(componentGR);
        component.remove(componentGR.getComponent());
    }

    /*
     * Returns the number of ucd components contained
     */
    public int getNumberOfElements() {
        return components.size();
    }

    public UCDComponentGR getElement(int index) {
        return components.get(index);
    }

    @Override
    public Iterator<UCDComponentGR> createIterator() {
        return new CompositeUCDGRIterator(components.iterator());
    }

    @Override
    public void move(int x, int y) {
        logger.finer(() -> "move");
        int deltaX = x - startingPoint.x;
        int deltaY = y - startingPoint.y;
        startingPoint.setLocation(x, y);

        components
                .forEach(comp -> comp.move(comp.getStartingPoint().x + deltaX, comp.getStartingPoint().y + deltaY));
    }

    public boolean contains(UCDComponentGR otherUCDComponentGR) {
        // starting point
        Point s = new Point(otherUCDComponentGR.getStartingPoint().x,
                otherUCDComponentGR.getStartingPoint().y);
        if (!this.contains(s)) {
            return false;
        }

        int x = (int) s.getX();
        int y = (int) s.getY();
        int wdth = otherUCDComponentGR.getWidth();
        int hght = otherUCDComponentGR.getHeight();

        // top right point
        s.setLocation(x + wdth, y);
        if (!this.contains(s)) {
            return false;
        }

        // bottom left point
        s.setLocation(x, y + hght);
        if (!this.contains(s)) {
            return false;
        }

        // bottom right point
        s.setLocation(x + wdth, y + hght);
        if (!this.contains(s)) {
            return false;
        }

        return true;
    }

    public GraphicalElement getContainingGraphicalElement(Point2D point) {
        ListIterator<UCDComponentGR> iterator = components.listIterator(components.size());
        while (iterator.hasPrevious()) {
            UCDComponentGR comp = iterator.previous();
            if (comp.contains(point)) {
                return comp.getContainingGraphicalElement(point);
            }
        }

        return this;
    }

    public UCDComponentGR findContext(UCDComponentGR comp) {
        Iterator<UCDComponentGR> iterator = components.iterator();
        while (iterator.hasNext()) {
            UCDComponentGR myComp = iterator.next();
            if (myComp.contains(comp)) {
                return myComp.findContext(comp);
            }
        }

        return this;
    }

    public void clearSelected() {
        Iterator<UCDComponentGR> iterator = components.iterator();
        while (iterator.hasNext()) {
            UCDComponentGR comp = iterator.next();
            comp.clearSelected();
        }

        this.setSelected(false);
    }
}
