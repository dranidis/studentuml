package edu.city.studentuml;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.Operand;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Tests for ALT fragment with multiple operands.
 * 
 * @author dimitris
 */
public class CombinedFragmentAltTest {

    private CombinedFragment altFragment;

    @Before
    public void setUp() {
        altFragment = new CombinedFragment(InteractionOperator.ALT, "");
    }

    @Test
    public void testAltFragmentStartsWithNoOperands() {
        List<Operand> operands = altFragment.getOperands();

        assertNotNull(operands);
        assertTrue(operands.isEmpty());
    }

    @Test
    public void testAddOperandToAltFragment() {
        Operand operand1 = new Operand("[x > 0]");
        altFragment.addOperand(operand1);

        List<Operand> operands = altFragment.getOperands();
        assertEquals(1, operands.size());
        assertEquals("[x > 0]", operands.get(0).getGuardCondition());
    }

    @Test
    public void testAddMultipleOperandsToAltFragment() {
        Operand operand1 = new Operand("[x > 0]");
        Operand operand2 = new Operand("[else]");

        altFragment.addOperand(operand1);
        altFragment.addOperand(operand2);

        List<Operand> operands = altFragment.getOperands();
        assertEquals(2, operands.size());
        assertEquals("[x > 0]", operands.get(0).getGuardCondition());
        assertEquals("[else]", operands.get(1).getGuardCondition());
    }

    @Test
    public void testRemoveOperandFromAltFragment() {
        Operand operand1 = new Operand("[x > 0]");
        Operand operand2 = new Operand("[else]");
        altFragment.addOperand(operand1);
        altFragment.addOperand(operand2);

        boolean removed = altFragment.removeOperand(operand1);

        assertTrue(removed);
        List<Operand> operands = altFragment.getOperands();
        assertEquals(1, operands.size());
        assertEquals("[else]", operands.get(0).getGuardCondition());
    }

    @Test
    public void testRemoveNonExistentOperand() {
        Operand operand1 = new Operand("[x > 0]");
        Operand operand2 = new Operand("[else]");
        altFragment.addOperand(operand1);

        boolean removed = altFragment.removeOperand(operand2);

        assertFalse(removed);
        assertEquals(1, altFragment.getOperands().size());
    }

    @Test
    public void testClearOperands() {
        altFragment.addOperand(new Operand("[x > 0]"));
        altFragment.addOperand(new Operand("[else]"));

        altFragment.clearOperands();

        assertTrue(altFragment.getOperands().isEmpty());
    }

    @Test
    public void testBackwardCompatibility_NoOperandsInitializedReturnsEmptyList() {
        // Create a fragment without adding operands (simulates legacy fragments)
        CombinedFragment legacyFragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");

        List<Operand> operands = legacyFragment.getOperands();

        assertNotNull(operands);
        assertTrue(operands.isEmpty());
    }
}
