package edu.city.studentuml.model.graphical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.UMLProject;

/**
 * Test for CombinedFragmentGR graphical class. Tests the minimal rendering
 * implementation for OPT operator.
 * 
 * @author dimitris
 */
public class CombinedFragmentGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testCreateCombinedFragmentGR_withOptOperator() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");

        // Create graphical element at position (50, 100) with default width
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        assertNotNull("FragmentGR should not be null", fragmentGR);
        assertEquals("Fragment should match", fragment, fragmentGR.getCombinedFragment());
        assertEquals("X position should be 50", 50, fragmentGR.getX());
        assertEquals("Y position should be 100", 100, fragmentGR.getY());
        assertEquals("Width should be 300", 300, fragmentGR.getWidth());
    }

    @Test
    public void testGetBounds_returnsCorrectRectangle() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        fragment.setHeight(150);

        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        assertNotNull("Bounds should not be null", fragmentGR.getBounds());
        assertEquals("Bounds X should be 50", 50.0, fragmentGR.getBounds().getX(), 0.01);
        assertEquals("Bounds Y should be 100", 100.0, fragmentGR.getBounds().getY(), 0.01);
        assertEquals("Bounds width should be 300", 300.0, fragmentGR.getBounds().getWidth(), 0.01);
        assertEquals("Bounds height should be 150", 150.0, fragmentGR.getBounds().getHeight(), 0.01);
    }

    @Test
    public void testSetWidth_updatesWidth() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        fragmentGR.setWidth(400);

        assertEquals("Width should be updated to 400", 400, fragmentGR.getWidth());
    }

    @Test
    public void testSetHeight_updatesFragmentHeight() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        fragmentGR.setHeight(200);

        assertEquals("Height should be updated to 200", 200, fragmentGR.getHeight());
        assertEquals("Domain fragment height should also be 200", 200, fragment.getHeight());
    }

    @Test
    public void testContains_pointInsideFragment() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        fragment.setHeight(150);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        // Point inside the pentagon label area (top-left) should be contained
        boolean contains = fragmentGR.contains(new Point(60, 110));

        assertEquals("Fragment should contain point in pentagon label area (60, 110)", true, contains);
    }

    @Test
    public void testContains_pointOutsideFragment() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        fragment.setHeight(150);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);

        // Point outside the pentagon label (but inside fragment body) should NOT be contained
        // This allows messages inside the fragment to be selectable
        boolean contains = fragmentGR.contains(new Point(200, 150));

        assertEquals("Fragment should not contain point outside pentagon label (200, 150)", false, contains);
    }

    /**
     * Tests loop iteration string formatting for exact iterations. UML syntax:
     * loop(3) means exactly 3 iterations (min=max=3)
     */
    @Test
    public void testLoopIterationsString_exactIterations() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "");
        fragment.setLoopMin(3);
        fragment.setLoopMax(3);

        String iterationsString = fragment.getLoopIterationsString();

        assertEquals("Loop iterations should be (3)", "(3)", iterationsString);
    }

    /**
     * Tests loop iteration string formatting for range with min and max. UML
     * syntax: loop(0,2) means 0 to 2 iterations
     */
    @Test
    public void testLoopIterationsString_rangeWithMinMax() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "");
        fragment.setLoopMin(0);
        fragment.setLoopMax(2);

        String iterationsString = fragment.getLoopIterationsString();

        assertEquals("Loop iterations should be (0,2)", "(0,2)", iterationsString);
    }

    /**
     * Tests loop iteration string formatting for unlimited iterations. UML syntax:
     * loop(3,*) means 3 to unlimited iterations
     */
    @Test
    public void testLoopIterationsString_unlimitedIterations() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "");
        fragment.setLoopMin(3);
        fragment.setLoopMax(-1); // -1 represents unlimited (*)

        String iterationsString = fragment.getLoopIterationsString();

        assertEquals("Loop iterations should be (3,*)", "(3,*)", iterationsString);
    }

    /**
     * Tests loop iteration string when no iterations are set. Should return empty
     * string.
     */
    @Test
    public void testLoopIterationsString_noIterations() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "");
        // Don't set loopMin or loopMax (both null)

        String iterationsString = fragment.getLoopIterationsString();

        assertEquals("Loop iterations should be empty string", "", iterationsString);
    }

    /**
     * Tests loop iteration string for non-LOOP operator. Should return empty string
     * for OPT operator.
     */
    @Test
    public void testLoopIterationsString_optOperator() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "[x > 0]");
        fragment.setLoopMin(3);
        fragment.setLoopMax(5);

        String iterationsString = fragment.getLoopIterationsString();

        // OPT operator should not show loop iterations
        assertEquals("OPT operator should return empty iteration string", "", iterationsString);
    }

    /**
     * Tests cloning preserves loop iteration values.
     */
    @Test
    public void testClone_preservesLoopIterations() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "[i < n]");
        fragment.setHeight(150);
        fragment.setLoopMin(0);
        fragment.setLoopMax(5);

        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);
        CombinedFragmentGR clonedGR = (CombinedFragmentGR) fragmentGR.clone();

        assertNotNull("Cloned fragment should not be null", clonedGR);
        assertEquals("Cloned fragment operator should be LOOP",
                InteractionOperator.LOOP, clonedGR.getCombinedFragment().getOperator());
        assertEquals("Cloned fragment guard should match",
                "[i < n]", clonedGR.getCombinedFragment().getGuardCondition());
        assertEquals("Cloned fragment loopMin should be 0",
                Integer.valueOf(0), clonedGR.getCombinedFragment().getLoopMin());
        assertEquals("Cloned fragment loopMax should be 5",
                Integer.valueOf(5), clonedGR.getCombinedFragment().getLoopMax());
    }

    /**
     * Tests cloning when loop iterations are null.
     */
    @Test
    public void testClone_withNullLoopIterations() {
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.ALT, "[condition]");
        fragment.setHeight(150);
        // Don't set loop iterations (remain null)

        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(50, 100), 300);
        CombinedFragmentGR clonedGR = (CombinedFragmentGR) fragmentGR.clone();

        assertNotNull("Cloned fragment should not be null", clonedGR);
        assertNull("Cloned fragment loopMin should be null",
                clonedGR.getCombinedFragment().getLoopMin());
        assertNull("Cloned fragment loopMax should be null",
                clonedGR.getCombinedFragment().getLoopMax());
    }
}
