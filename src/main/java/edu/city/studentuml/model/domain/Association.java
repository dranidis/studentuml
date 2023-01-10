package edu.city.studentuml.model.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
/**
 * @author Ramollari Ervin
 */
@JsonIncludeProperties({ "name", "direction", "showArrow", "labelDirection", "reflective", "bidirectional", "roleA",
        "roleB" })
public class Association implements Serializable, IXMLCustomStreamable {

    // integer constants defining direction
    public static final int BIDIRECTIONAL = 0;
    public static final int AB = 1;
    public static final int BA = 2;
    public static final int BIDIRECTIONAL_FIX = 3;
    // constants defining reading direction label
    public static final int FROM_A_TO_B = 3;
    public static final int FROM_B_TO_A = 4;
    private String name;
    private int direction; // 0 = bidirectional, 1 = A -> B, 2 = B -> A
    private boolean showArrow; // true if a direction arrow is to be shown along with the name
    private int labelDirection;
    private Role roleA;
    private Role roleB;

    // Its direction is always from role A to role B
    public Association(Classifier classA, Classifier classB) {
        this(new Role(classA), new Role(classB));
    }

    public Association(Role rA, Role rB) {
        roleA = rA;
        roleB = rB;
        name = null;
        direction = 0;
        showArrow = false;
        labelDirection = FROM_A_TO_B;
    }

    public void setName(String n) {
        name = n;
    }

    public void setDirection(int dir) {
        if (dir == AB) {
            direction = dir;
        } else if (dir == BA) {
            direction = dir;
        } else if (dir == BIDIRECTIONAL_FIX) {
            direction = dir;
        } else {
            direction = BIDIRECTIONAL;
        }
    }

    public void setBidirectional() {
        direction = BIDIRECTIONAL;
    }

    public void setShowArrow(boolean show) {
        showArrow = show;
    }

    public String getName() {
        if (name == null)
            return "";
        return name.trim();
    }

    public int getDirection() {
        return direction;
    }

    public boolean isBidirectional() {
        return (direction == BIDIRECTIONAL);
    }

    public boolean getShowArrow() {
        return showArrow;
    }

    public Role getRoleA() {
        return roleA;
    }

    public Role getRoleB() {
        return roleB;
    }

    public Classifier getClassA() {
        return roleA.getReferredClass();
    }

    public Classifier getClassB() {
        return roleB.getReferredClass();
    }

    public boolean isReflective() {
        return (getClassA() == getClassB());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
        setDirection(Integer.parseInt(node.getAttribute("direction")));
        setShowArrow(Boolean.parseBoolean(node.getAttribute("showArrow")));
        setLabelDirection(Integer.parseInt(node.getAttribute("labelDirection")));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        node.setAttribute("direction", Integer.toString(direction));
        node.setAttribute("showArrow", String.valueOf(getShowArrow()));
        node.setAttribute("labelDirection", Integer.toString(getLabelDirection()));

        streamer.streamObject(node, "rolea", roleA);
        streamer.streamObject(node, "roleb", roleB);
    }

    public Association clone() {
        Association copyAssociation = new Association(this.getRoleA().clone(), this.getRoleB().clone());

        if (this.getName() != null) {
            copyAssociation.setName(this.getName());
        }

        copyAssociation.setDirection(this.getDirection());
        copyAssociation.setShowArrow(this.getShowArrow());
        copyAssociation.setLabelDirection(this.getLabelDirection());

        return copyAssociation;
    }

    // need for undo/redo
    public void setRoleA(Role roleA) {
        this.roleA = roleA;
    }

    public void setRoleB(Role roleB) {
        this.roleB = roleB;
    }

    // fix for reading direction label
    public void changeLabelDirection() {
        if (labelDirection == FROM_A_TO_B) {
            setLabelDirection(FROM_B_TO_A);
        } else {
            setLabelDirection(FROM_A_TO_B);
        }
    }

    public int getLabelDirection() {
        return labelDirection;
    }

    public void setLabelDirection(int direction) {
        if (direction == FROM_A_TO_B) {
            labelDirection = FROM_A_TO_B;
        } else if (direction == FROM_B_TO_A) {
            labelDirection = FROM_B_TO_A;
        }
    }
}
