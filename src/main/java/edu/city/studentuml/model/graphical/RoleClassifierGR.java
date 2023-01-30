package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDObject;


/**
 * The inherited startingPoint refers to the x coordinate of the center and to
 * the y coordinate of the top most point
 */
public abstract class RoleClassifierGR extends GraphicalElement {

    private static final Logger logger = Logger.getLogger(RoleClassifierGR.class.getName());

    public static final int MINIMUM_LIFELINE_LENGTH = 60;

    /**
     * The default vertical distance from the top border where drawing of role
     * classifiers starts
     */
    public static final int VERTICAL_OFFSET = 20;

    /**
     * Refers to the point where the lifeline of the object ends in case it is
     * destroyed, or for display purposes
     */
    protected int endingY;
    /**
     * the role classifier concept this graphical element refers to
     */
    protected RoleClassifier roleClassifier;
    
    /**
     * stacks keeping ingoing and outgoing messages for SD validation
     */
    private Deque<RoleClassifierGR> in = new ArrayDeque<>();
    private Deque<RoleClassifierGR> out = new ArrayDeque<>();

    /**
     * store all the Ys of messages
    */
    protected List<Integer> messageYs = new ArrayList<>();
    
    /**
     * Stores the activation depth at Y
     */
    protected Map<Integer, Integer> activationAt =  new HashMap<>();

    /**
     * of the x and y coordinates, x is significant
     * @param roleClassifier
     * @param x
     */
    protected RoleClassifierGR(RoleClassifier roleClassifier, int x) {
        this.roleClassifier = roleClassifier;
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

    /**
     * used only to move the name box down when a create message is added
     * 
     * @param y
     */
    public void setBeginningY(int y) {
        startingPoint.setLocation(startingPoint.getX(), y);
    }

    /**
     * all role classifiers respond to drag and drop events by moving only horizontally
     */
    @Override
    public void move(int x, int y) {
        startingPoint.setLocation(x, startingPoint.getY());
    }

    public String validateOut(RoleClassifierGR target) {
        if( in.size() > out.size()) {
            out.push(target);
            return "";
        } else {
            logger.finer(this::stacksToString);
            return this.getRoleClassifier().getName() + " Cannot call method. It does not have the focus";
        }
    }

    public String validateIn(RoleClassifierGR source) {
        if( in.size() == out.size()) {
            in.push(source);
            return "";
        } else {
            logger.finer(this::stacksToString);
            return this.getRoleClassifier().getName() + " Cannot accept incoming method. It HAS the focus";
        }
    }

    public String validateOutReturn(RoleClassifierGR target) {
        if( in.size() > out.size() ) {
            RoleClassifierGR origFrom = in.peek();
            if (origFrom == target) {
                in.pop();
                return "";
            } else {
                logger.finer(this::stacksToString);
                return this.getRoleClassifier().getName() + " Cannot return to " + target.getRoleClassifier().getName() + ". " +
                        origFrom.getRoleClassifier().getName() + " was the original caller.";
            }
        } else {
            return this.getRoleClassifier().getName() + " Cannot return. It does not have the focus";
        }
    }

    String validateInReturn(RoleClassifierGR source) {
        if(out.isEmpty()) {
            logger.finer(this::stacksToString);
            return this.getRoleClassifier().getName() + " Cannot accept return messages. Did not send any messages";
        }
        if( in.size() == out.size() ) {
            RoleClassifierGR origTo = out.peek();
            if (origTo == source) {
                out.pop();
                return "";
            } else {
                logger.finer(this::stacksToString);
                return this.getRoleClassifier().getName() + " Cannot accept return from " + source.getRoleClassifier().getName() + ". " +
                        " Expecting from " + origTo.getRoleClassifier().getName();
            }
        } else {
            logger.finer(this::stacksToString);
            return this.getRoleClassifier().getName() + " Cannot accept return messages. It HAS the focus";
        }
    }

    private String stacksToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stack: [");
        for(RoleClassifierGR m: in) {
            sb.append(m.getRoleClassifier().getName());
        }
        sb.append("] - [");
        for(RoleClassifierGR m: out) {
            sb.append(m.getRoleClassifier().getName());
        }
        sb.append("]\n");
        return sb.toString();
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