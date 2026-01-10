package edu.city.studentuml.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 * Represents a UML 2.x Combined Fragment in a Sequence Diagram. A combined
 * fragment defines an expression of interaction fragments. It is defined by an
 * interaction operator and corresponding interaction operands. Supports OPT,
 * ALT, and LOOP operators with multiple operands for ALT fragments.
 * 
 * @author dimitris
 */
public class CombinedFragment implements Serializable, IXMLCustomStreamable {
    private static final long serialVersionUID = 1L;

    /**
     * Default height for new combined fragments - set to minimum height. Users can
     * expand as needed after creation.
     */
    public static final int DEFAULT_HEIGHT = 40;

    /**
     * The interaction operator type (opt, alt, loop, etc.)
     */
    private InteractionOperator operator;

    /**
     * Guard condition for the fragment (e.g., "[x > 0]") For OPT, this is the
     * single guard condition. For ALT, each operand would have its own guard.
     */
    private String guardCondition;

    /**
     * Height of the fragment in pixels. Determines where the fragment ends
     * vertically.
     */
    private int height;

    /**
     * Minimum number of loop iterations (for LOOP operator only). Default is null
     * (not set). When set, represents the minimum iterations.
     */
    private Integer loopMin;

    /**
     * Maximum number of loop iterations (for LOOP operator only). Default is null
     * (not set). "*" is represented as -1 (unlimited). When set, represents the
     * maximum iterations.
     */
    private Integer loopMax;

    /**
     * List of operands for this fragment. For ALT fragments, this contains multiple
     * operands (one for each alternative path). For OPT and LOOP, this typically
     * contains one operand. Initialized lazily to maintain backward compatibility.
     */
    private List<Operand> operands;

    /**
     * Creates a new CombinedFragment with the specified operator, guard condition.
     * 
     * @param operator       the interaction operator type
     * @param guardCondition the guard condition (can be empty string)
     */
    public CombinedFragment(InteractionOperator operator, String guardCondition) {
        this.operator = operator;
        this.guardCondition = guardCondition;
        this.height = DEFAULT_HEIGHT; // Default height
        this.loopMin = null; // Not set by default
        this.loopMax = null; // Not set by default
        this.operands = null; // Initialized lazily for backward compatibility
    }

    /**
     * Returns the interaction operator type. DO NOT CHANGE THE NAME: MAY BE CALLED
     * BY REFLECTION IN CONSISTENCY CHECK
     * 
     * @return the operator
     */
    public InteractionOperator getOperator() {
        return operator;
    }

    /**
     * Sets the interaction operator type.
     * 
     * @param operator the operator to set
     */
    public void setOperator(InteractionOperator operator) {
        this.operator = operator;
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
        this.guardCondition = guardCondition;
    }

    /**
     * Returns the height of the fragment.
     * 
     * @return the height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the fragment.
     * 
     * @param height the height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the minimum loop iterations (for LOOP operator).
     * 
     * @return the minimum iterations, or null if not set
     */
    public Integer getLoopMin() {
        return loopMin;
    }

    /**
     * Sets the minimum loop iterations (for LOOP operator).
     * 
     * @param loopMin the minimum iterations (null for not set, 0+ for value)
     */
    public void setLoopMin(Integer loopMin) {
        this.loopMin = loopMin;
    }

    /**
     * Returns the maximum loop iterations (for LOOP operator).
     * 
     * @return the maximum iterations, -1 for unlimited (*), or null if not set
     */
    public Integer getLoopMax() {
        return loopMax;
    }

    /**
     * Sets the maximum loop iterations (for LOOP operator).
     * 
     * @param loopMax the maximum iterations (null for not set, -1 for unlimited, 0+
     *                for value)
     */
    public void setLoopMax(Integer loopMax) {
        this.loopMax = loopMax;
    }

    /**
     * Returns the list of operands for this fragment. For backward compatibility,
     * returns an empty list if operands haven't been initialized (legacy
     * fragments).
     * 
     * @return list of operands (never null)
     */
    public List<Operand> getOperands() {
        if (operands == null) {
            return new ArrayList<>();
        }
        return operands;
    }

    /**
     * Sets the list of operands for this fragment.
     * 
     * @param operands the list of operands to set
     */
    public void setOperands(List<Operand> operands) {
        this.operands = operands;
    }

    /**
     * Adds an operand to this fragment.
     * 
     * @param operand the operand to add
     */
    public void addOperand(Operand operand) {
        if (operands == null) {
            operands = new ArrayList<>();
        }
        operands.add(operand);
    }

    /**
     * Removes an operand from this fragment.
     * 
     * @param operand the operand to remove
     * @return true if the operand was removed, false otherwise
     */
    public boolean removeOperand(Operand operand) {
        if (operands == null) {
            return false;
        }
        return operands.remove(operand);
    }

    /**
     * Clears all operands from this fragment.
     */
    public void clearOperands() {
        if (operands != null) {
            operands.clear();
        }
    }

    /**
     * Returns the formatted loop iterations string for display in pentagon label.
     * Follows UML 2.x syntax: loop, loop(3), loop(0,2), loop(3,*) etc.
     * 
     * @return formatted loop string, or empty string if not LOOP operator
     */
    public String getLoopIterationsString() {
        if (operator != InteractionOperator.LOOP) {
            return "";
        }

        // No iterations specified: just "loop"
        if (loopMin == null && loopMax == null) {
            return "";
        }

        // Only min specified: loop(n) means exactly n iterations
        if (loopMin != null && loopMax == null) {
            return "(" + loopMin + ")";
        }

        // Only max specified: default min to 0
        if (loopMin == null && loopMax != null) {
            String maxStr = loopMax == -1 ? "*" : String.valueOf(loopMax);
            return "(0," + maxStr + ")";
        }

        // Both specified
        if (loopMin.equals(loopMax)) {
            // Same value: loop(n)
            return "(" + loopMin + ")";
        } else {
            // Different: loop(min,max)
            String maxStr = loopMax == -1 ? "*" : String.valueOf(loopMax);
            return "(" + loopMin + "," + maxStr + ")";
        }
    }

    /**
     * Returns the name of the fragment (the operator display name). DO NOT CHANGE
     * THE NAME: MAY BE CALLED BY REFLECTION IN CONSISTENCY CHECK
     * 
     * @return the operator display name (e.g., "opt", "alt", "loop")
     */
    public String getName() {
        return operator.getDisplayName();
    }

    @Override
    public String toString() {
        return operator.getDisplayName() + " " + guardCondition;
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("operator", operator.name());
        node.setAttribute("guardCondition", guardCondition != null ? guardCondition : "");
        node.setAttribute("height", String.valueOf(height));

        // Save loop iterations if set
        if (loopMin != null) {
            node.setAttribute("loopMin", String.valueOf(loopMin));
        }
        if (loopMax != null) {
            node.setAttribute("loopMax", String.valueOf(loopMax));
        }

        // Save operands if any
        if (operands != null && !operands.isEmpty()) {
            Element operandsElement = node.getOwnerDocument().createElement("Operands");
            for (Operand operand : operands) {
                Element operandElement = node.getOwnerDocument().createElement("Operand");
                operandElement.setAttribute("guardCondition", operand.getGuardCondition());
                operandElement.setAttribute("heightRatio", String.valueOf(operand.getHeightRatio()));
                operandsElement.appendChild(operandElement);
            }
            node.appendChild(operandsElement);
        }
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        String operatorName = node.getAttribute("operator");
        operator = InteractionOperator.valueOf(operatorName);
        guardCondition = node.getAttribute("guardCondition");
        height = Integer.parseInt(node.getAttribute("height"));

        // Load loop iterations if present
        String loopMinStr = node.getAttribute("loopMin");
        if (loopMinStr != null && !loopMinStr.isEmpty()) {
            loopMin = Integer.parseInt(loopMinStr);
        }

        String loopMaxStr = node.getAttribute("loopMax");
        if (loopMaxStr != null && !loopMaxStr.isEmpty()) {
            loopMax = Integer.parseInt(loopMaxStr);
        }

        // Load operands if present
        NodeList operandsNodes = node.getElementsByTagName("Operands");
        if (operandsNodes.getLength() > 0) {
            Element operandsElement = (Element) operandsNodes.item(0);
            NodeList operandNodes = operandsElement.getElementsByTagName("Operand");

            operands = new ArrayList<>();
            for (int i = 0; i < operandNodes.getLength(); i++) {
                Element operandElement = (Element) operandNodes.item(i);
                String operandGuard = operandElement.getAttribute("guardCondition");
                Operand operand = new Operand(operandGuard);

                // Load height ratio if present (defaults to 1.0 if not specified for backward compatibility)
                String heightRatioStr = operandElement.getAttribute("heightRatio");
                if (heightRatioStr != null && !heightRatioStr.isEmpty()) {
                    try {
                        double heightRatio = Double.parseDouble(heightRatioStr);
                        operand.setHeightRatio(heightRatio);
                    } catch (NumberFormatException e) {
                        // Keep default 1.0
                        operand.setHeightRatio(1.0);
                    }
                }

                operands.add(operand);
            }
        }
    }
}
