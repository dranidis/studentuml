package edu.city.studentuml.model.domain;

/**
 * Enumeration of UML 2.x Combined Fragment Interaction Operators. Phase 1
 * implementation includes the most common operators: - OPT: Optional execution
 * (if-then) - ALT: Alternatives (if-then-else) - to be implemented - LOOP:
 * Repeated execution - to be implemented Additional operators can be added in
 * Phase 2: PAR, BREAK, CRITICAL, NEG, ASSERT, STRICT, SEQ, IGNORE, CONSIDER
 * 
 * @author dimitris
 */
public enum InteractionOperator {
    /**
     * Optional - Represents a choice of behavior where either the (sole) operand
     * happens or nothing happens. An option is semantically equivalent to an alt
     * with two operands, one of which is empty.
     */
    OPT("opt"),

    /**
     * Alternative - Designates that the CombinedFragment represents a choice of
     * behavior. At most one of the operands will be chosen. The chosen operand must
     * have an explicit or implicit guard expression that evaluates to true. (To be
     * implemented)
     */
    ALT("alt"),

    /**
     * Loop - Designates that the CombinedFragment represents a loop. The loop
     * operand will be repeated a number of times based on the loop specification.
     * (To be implemented)
     */
    LOOP("loop");

    private final String displayName;

    InteractionOperator(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the lowercase display name of the operator (e.g., "opt", "alt",
     * "loop"). This is used for rendering the operator label in the pentagon.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
