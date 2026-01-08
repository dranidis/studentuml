package edu.city.studentuml.model.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for CombinedFragment domain class. Tests the minimal implementation for
 * OPT operator.
 * 
 * @author dimitris
 */
public class CombinedFragmentTest {

    @Before
    public void setup() {
        // Setup if needed
    }

    @Test
    public void testCreateOptFragment_withGuardCondition() {
        // Create a simple OPT fragment with a guard condition
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");

        assertNotNull("Fragment should not be null", fragment);
        assertEquals("Operator should be OPT", InteractionOperator.OPT, fragment.getOperator());
        assertEquals("Guard condition should be '[x > 0]'", "[x > 0]", fragment.getGuardCondition());
    }

    @Test
    public void testCreateOptFragment_withoutGuardCondition() {
        // Create OPT fragment without guard (should default to empty or null)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");

        assertNotNull("Fragment should not be null", fragment);
        assertEquals("Operator should be OPT", InteractionOperator.OPT, fragment.getOperator());
        assertEquals("Guard condition should be empty", "", fragment.getGuardCondition());
    }

    @Test
    public void testSetGuardCondition() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");

        fragment.setGuardCondition("[y < 10]");

        assertEquals("Guard condition should be updated", "[y < 10]", fragment.getGuardCondition());
    }

    @Test
    public void testGetName_returnsOperatorName() {
        // getName() is required for consistency checker (reflection)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");

        String name = fragment.getName();

        assertNotNull("Name should not be null", name);
        assertEquals("Name should be 'opt'", "opt", name);
    }
}
