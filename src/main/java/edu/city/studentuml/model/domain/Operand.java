package edu.city.studentuml.model.domain;

import java.io.Serializable;

/**
 * Represents a single operand within a Combined Fragment. For ALT fragments,
 * each alternative path is represented by an operand with its own guard
 * condition. For OPT and LOOP fragments, there is typically only one operand.
 * 
 * @author dimitris
 */
public class Operand implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Guard condition for this operand (e.g., "[x > 0]", "[else]")
     */
    private String guardCondition;

    /**
     * Height ratio for this operand (relative to other operands). Default is 1.0
     * (equal distribution). A value of 2.0 means twice the height. Used for ALT
     * fragments with draggable separators.
     */
    private double heightRatio;

    /**
     * Creates a new Operand with the specified guard condition.
     * 
     * @param guardCondition the guard condition (can be empty string)
     */
    public Operand(String guardCondition) {
        this.guardCondition = guardCondition != null ? guardCondition : "";
        this.heightRatio = 1.0; // Default to equal distribution
    }

    /**
     * Creates a copy of another operand (including height ratio).
     * 
     * @param other the operand to copy
     * @return a new Operand with the same properties
     */
    public static Operand copy(Operand other) {
        Operand operand = new Operand(other.guardCondition);
        operand.heightRatio = other.heightRatio;
        return operand;
    }

    /**
     * Returns the guard condition.
     * 
     * @return the guard condition
     */
    public String getGuardCondition() {
        return guardCondition;
    }

    /**
     * Sets the guard condition.
     * 
     * @param guardCondition the guard condition to set
     */
    public void setGuardCondition(String guardCondition) {
        this.guardCondition = guardCondition != null ? guardCondition : "";
    }

    /**
     * Returns the height ratio for this operand.
     * 
     * @return the height ratio (default 1.0)
     */
    public double getHeightRatio() {
        return heightRatio;
    }

    /**
     * Sets the height ratio for this operand.
     * 
     * @param heightRatio the height ratio (must be positive)
     */
    public void setHeightRatio(double heightRatio) {
        if (heightRatio <= 0) {
            throw new IllegalArgumentException("Height ratio must be positive");
        }
        this.heightRatio = heightRatio;
    }

    @Override
    public String toString() {
        return "Operand[guard=" + guardCondition + "]";
    }
}
