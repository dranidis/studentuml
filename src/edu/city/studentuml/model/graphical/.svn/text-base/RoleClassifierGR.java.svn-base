package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//RoleClassifierGR.java
import edu.city.studentuml.model.domain.RoleClassifier;
import java.awt.Point;

//the inherited startingPoint refers ot the x coordinate of the center
//and to the y coordinate of the top most point
public abstract class RoleClassifierGR extends GraphicalElement {

    public static final int MINIMUM_LIFELINE_LENGTH = 100;
    // the default vertical distance from the top
    // border where drawing of role classifiers starts
    public static final int VERTICAL_OFFSET = 20;
    // this new variable refers to the point where the lifeline of the object ends
    // in case it is destroyed, or for display purposes
    protected int endingY;
    // the role classifier concept this graphical element refers to
    protected RoleClassifier roleClassifier;

    // of the x and y coordinates, x is significant
    public RoleClassifierGR(RoleClassifier rc, int x) {
        roleClassifier = rc;
        startingPoint = new Point(x, VERTICAL_OFFSET);
        endingY = VERTICAL_OFFSET + MINIMUM_LIFELINE_LENGTH;
    }

    public void setRoleClassifier(RoleClassifier rc) {
        roleClassifier = rc;
    }

    public RoleClassifier getRoleClassifier() {
        return roleClassifier;
    }

    public int getEndingY() {
        return endingY;
    }

    public void setEndingY(int y) {
        endingY = y;
    }

    // used only to move the name box down when a create message is added
    public void setBeginningY(int y) {
        startingPoint.setLocation(startingPoint.getX(), y);
    }

    // override abstract method move of GraphicalElement
    // all role classifiers respond to drag and drop events by moving only horizontally
    public void move(int x, int y) {
        startingPoint.setLocation(x, startingPoint.getY());
    }
}
