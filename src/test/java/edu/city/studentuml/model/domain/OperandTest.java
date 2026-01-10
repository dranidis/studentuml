package edu.city.studentuml.model.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the Operand class.
 * 
 * @author dimitris
 */
public class OperandTest {

    @Test
    public void testCreateOperand() {
        Operand operand = new Operand("[x > 0]");

        assertEquals("[x > 0]", operand.getGuardCondition());
    }

    @Test
    public void testCreateOperandWithNullGuard() {
        Operand operand = new Operand(null);

        assertEquals("", operand.getGuardCondition());
        assertNotNull(operand.getGuardCondition());
    }

    @Test
    public void testSetGuardCondition() {
        Operand operand = new Operand("[x > 0]");

        operand.setGuardCondition("[y < 10]");

        assertEquals("[y < 10]", operand.getGuardCondition());
    }

    @Test
    public void testSetGuardConditionToNull() {
        Operand operand = new Operand("[x > 0]");

        operand.setGuardCondition(null);

        assertEquals("", operand.getGuardCondition());
        assertNotNull(operand.getGuardCondition());
    }

    @Test
    public void testToString() {
        Operand operand = new Operand("[else]");

        String str = operand.toString();

        assertTrue(str.contains("[else]"));
        assertTrue(str.contains("Operand"));
    }
}
