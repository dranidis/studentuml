package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//RoleClassifierGR.java
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDObject;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    
    /**
     * stacks keeping ingoing and outgoing messages for SD validation
     */
    private Stack<RoleClassifierGR> in = new Stack<>();
    private Stack<RoleClassifierGR> out = new Stack<>();
    /**
     * store all the Ys of messages
     * and the activation depth at that Y
    */
    protected List<Integer> messageYs = new ArrayList<>();
    protected Map<Integer, Integer> activationAt =  new HashMap<>();

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

    boolean validateOut(RoleClassifierGR target) {
        if( in.size() > out.size()) {
            out.push(target);
            return true;
        } else {
            System.out.println(this.getRoleClassifier().getName() + " Cannot call method. It does not have the focus");
            showStacks();
            return false;
        }
    }

    boolean validateIn(RoleClassifierGR source) {
        if( in.size() == out.size()) {
            in.push(source);
            return true;
        } else {
            System.out.println(this.getRoleClassifier().getName() + " Cannot accept incoming method. It HAS the focus");
            showStacks();
            return false;
        }
    }

    boolean validateOutReturn(RoleClassifierGR target) {
        if( in.size() > out.size() ) {
            RoleClassifierGR origFrom = in.peek();
            if (origFrom == target) {
                in.pop();
                return true;
            } else {
                System.out.println(this.getRoleClassifier().getName() + " Cannot return to " + target.getRoleClassifier().getName() + ". " +
                        origFrom.getRoleClassifier().getName() + " was the original caller.");
                showStacks();
                return false;
            }
        } else {
            System.out.println(this.getRoleClassifier().getName() + " Cannot return. It does not have the focus");
            showStacks();
            return false;
        }
    }

    boolean validateInReturn(RoleClassifierGR source) {
        if(out.isEmpty()) {
            System.out.println(this.getRoleClassifier().getName() + " Cannot accept return messages. Did not send any messages");
            showStacks();
            return false;
        }
        if( in.size() == out.size() ) {
            RoleClassifierGR origTo = out.peek();
            if (origTo == source) {
                out.pop();
                return true;
            } else {
                System.out.println(this.getRoleClassifier().getName() + " Cannot accept return from " + source.getRoleClassifier().getName() + ". " +
                        " Expecting from " + origTo.getRoleClassifier().getName());
                showStacks();
                return false;
            }
        } else {
            System.out.println(this.getRoleClassifier().getName() + " Cannot accept return messages. It HAS the focus");
            showStacks();
            return false;
        }
    }

    private void showStacks() {
        System.out.print("[");
        for(RoleClassifierGR m: in) {
            System.out.print(m.getRoleClassifier().getName());
        }
        System.out.print("] - [");
        for(RoleClassifierGR m: out) {
            System.out.print(m.getRoleClassifier().getName());
        }
        System.out.println("]");
    }

    void clearInOutStacks() {
        in.clear();
        out.clear();
        messageYs.clear();
        activationAt.clear();
    }

    /**
     * Adds a dummy object in the in stack to make the object have the focus
     * and avoid error in the validation
     */
    void setActiveIn() {
        in.push(new SDObjectGR(new SDObject("void", new DesignClass("Void")), 0));
    }
    


    void addActivationHeight(int y) {
        messageYs.add(y);
        /*
        store size of in stack at the specific time
        */
        activationAt.put(y, in.size());
    }

    int acticationAtY(int y) {
        if (activationAt.get(y) != null)
            return activationAt.get(y);
        for(int i=0; i< messageYs.size() - 1; i++) {
            if (y >= messageYs.get(i) && y < messageYs.get(i+1))
                return activationAt.get(messageYs.get(i));
        }
        return 0;
    }
}