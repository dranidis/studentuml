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

    protected List<UCDComponentGR> ucdComponents;

    public List<UCDComponentGR> getUcdComponents() {
        return ucdComponents;
    }

    protected CompositeUCDElementGR(CompositeUCDElement compositeElement, int x, int y) {
        super(compositeElement, x, y);
        ucdComponents = new ArrayList<>();
    }

    @Override
    public void add(UCDComponentGR componentGR) {
        ucdComponents.add(componentGR);
        ucdComponent.add(componentGR.getUCDComponent());
    }

    @Override
    public void remove(UCDComponentGR componentGR) {
        ucdComponents.remove(componentGR);
        ucdComponent.remove(componentGR.getUCDComponent());
    }

    /*
     * Returns the number of ucd components contained
     */
    public int getNumberOfElements() {
        return ucdComponents.size();
    }

    public UCDComponentGR getElement(int index) {
        return ucdComponents.get(index);
    }

    @Override
    public Iterator createIterator() {
        return new CompositeUCDGRIterator(ucdComponents.iterator());
    }

    @Override
    public void move(int x, int y) {
        logger.finer(() -> "move");
        int deltaX = x - startingPoint.x;
        int deltaY = y - startingPoint.y;
        startingPoint.setLocation(x, y);

        ucdComponents
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
        ListIterator iterator = ucdComponents.listIterator(ucdComponents.size());
        while (iterator.hasPrevious()) {
            UCDComponentGR comp = (UCDComponentGR) iterator.previous();
            if (comp.contains(point)) {
                return comp.getContainingGraphicalElement(point);
            }
        }

        return this;
    }

    public UCDComponentGR findContext(UCDComponentGR comp) {
        Iterator iterator = ucdComponents.iterator();
        while (iterator.hasNext()) {
            UCDComponentGR myComp = (UCDComponentGR) iterator.next();
            if (myComp.contains(comp)) {
                return myComp.findContext(comp);
            }
        }

        return this;
    }

    public void clearSelected() {
        Iterator iterator = ucdComponents.iterator();
        while (iterator.hasNext()) {
            UCDComponentGR comp = (UCDComponentGR) iterator.next();
            comp.clearSelected();
        }

        this.setSelected(false);
    }
}
