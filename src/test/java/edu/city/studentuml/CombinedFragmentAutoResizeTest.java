package edu.city.studentuml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.CombinedFragmentGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;

/**
 * Tests for auto-resize functionality of CombinedFragmentGR. Verifies that
 * fragments automatically adjust their X position and width to span all
 * messages within their Y range.
 * 
 * @author dimitris
 */
public class CombinedFragmentAutoResizeTest {

    private UMLProject project;
    private SDModel model;

    // Test objects (lifelines)
    private SDObjectGR obj1;
    private SDObjectGR obj2;
    private SDObjectGR obj3;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
        model = new SDModel("Test SD", project);

        // Create three objects at different X positions
        DesignClass class1 = new DesignClass("Class1");
        DesignClass class2 = new DesignClass("Class2");
        DesignClass class3 = new DesignClass("Class3");

        SDObject sdObj1 = new SDObject("obj1", class1);
        SDObject sdObj2 = new SDObject("obj2", class2);
        SDObject sdObj3 = new SDObject("obj3", class3);

        obj1 = new SDObjectGR(sdObj1, 100); // Leftmost
        obj2 = new SDObjectGR(sdObj2, 300); // Middle
        obj3 = new SDObjectGR(sdObj3, 500); // Rightmost

        model.addRoleClassifier(obj1);
        model.addRoleClassifier(obj2);
        model.addRoleClassifier(obj3);
    }

    @Test
    public void testAutoResize_SingleMessage() {
        // Add a message from obj1 to obj2 at Y=200
        GenericOperation op1 = new GenericOperation("message1");
        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 200);
        model.addMessage(msgGR1);

        // Create fragment at Y=180-280 (covers the message)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 180), 300);

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Verify fragment spans from obj1 to obj2 with padding
        int msg1StartX = msgGR1.getStartingX();
        int msg1EndX = msgGR1.getEndingX();
        int expectedLeftX = Math.min(msg1StartX, msg1EndX) - 20; // MESSAGE_PADDING
        int expectedWidth = Math.abs(msg1EndX - msg1StartX) + 40; // 2 * MESSAGE_PADDING

        assertEquals("Fragment X should be adjusted to leftmost message minus padding",
                expectedLeftX, fragmentGR.getX());
        assertEquals("Fragment width should span message endpoints plus padding",
                expectedWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_MultipleMessages() {
        // Add messages at different positions
        GenericOperation op1 = new GenericOperation("message1");
        GenericOperation op2 = new GenericOperation("message2");
        GenericOperation op3 = new GenericOperation("message3");

        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessage msg2 = new CallMessage(obj2.getSDObject(), obj3.getSDObject(), op2);
        CallMessage msg3 = new CallMessage(obj3.getSDObject(), obj1.getSDObject(), op3);

        // Message from obj1 to obj2 at Y=200
        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 200);
        // Message from obj2 to obj3 at Y=220
        CallMessageGR msgGR2 = new CallMessageGR(obj2, obj3, msg2, 220);
        // Message from obj3 to obj1 (backward) at Y=240
        CallMessageGR msgGR3 = new CallMessageGR(obj3, obj1, msg3, 240);

        model.addMessage(msgGR1);
        model.addMessage(msgGR2);
        model.addMessage(msgGR3);

        // Create fragment at Y=180-280 (covers all three messages)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.LOOP, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 180), 300);

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Verify fragment spans from obj1 to obj3 (leftmost to rightmost endpoints)
        int leftmost = Integer.MAX_VALUE;
        int rightmost = Integer.MIN_VALUE;

        for (CallMessageGR msg : new CallMessageGR[] { msgGR1, msgGR2, msgGR3 }) {
            leftmost = Math.min(leftmost, Math.min(msg.getStartingX(), msg.getEndingX()));
            rightmost = Math.max(rightmost, Math.max(msg.getStartingX(), msg.getEndingX()));
        }

        int expectedLeftX = leftmost - 20;
        int expectedWidth = (rightmost - leftmost) + 40;

        assertEquals("Fragment X should span leftmost message endpoint minus padding",
                expectedLeftX, fragmentGR.getX());
        assertEquals("Fragment width should span all message endpoints plus padding",
                expectedWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_NoMessages() {
        // Create fragment with no messages in range
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(150, 180), 300);

        int originalX = fragmentGR.getX();
        int originalWidth = fragmentGR.getWidth();

        // Apply auto-resize (should do nothing)
        fragmentGR.autoResizeToMessages(model, true);

        // Verify no change when no messages
        assertEquals("Fragment X should not change when no messages",
                originalX, fragmentGR.getX());
        assertEquals("Fragment width should not change when no messages",
                originalWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_MessagesOutsideYRange() {
        // Add messages outside the fragment's Y range
        GenericOperation op1 = new GenericOperation("message1");
        GenericOperation op2 = new GenericOperation("message2");

        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessage msg2 = new CallMessage(obj2.getSDObject(), obj3.getSDObject(), op2);

        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 100); // Above
        CallMessageGR msgGR2 = new CallMessageGR(obj2, obj3, msg2, 400); // Below

        model.addMessage(msgGR1);
        model.addMessage(msgGR2);

        // Create fragment at Y=200-300 (no messages in range)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(150, 200), 300);

        int originalX = fragmentGR.getX();
        int originalWidth = fragmentGR.getWidth();

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Verify no change when no messages in Y range
        assertEquals("Fragment X should not change when messages outside Y range",
                originalX, fragmentGR.getX());
        assertEquals("Fragment width should not change when messages outside Y range",
                originalWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_PartialMessageOverlap() {
        // Add messages, some inside and some outside Y range
        GenericOperation op1 = new GenericOperation("message1");
        GenericOperation op2 = new GenericOperation("message2");
        GenericOperation op3 = new GenericOperation("message3");

        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessage msg2 = new CallMessage(obj2.getSDObject(), obj3.getSDObject(), op2);
        CallMessage msg3 = new CallMessage(obj3.getSDObject(), obj1.getSDObject(), op3);

        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 100); // Above
        CallMessageGR msgGR2 = new CallMessageGR(obj2, obj3, msg2, 220); // Inside
        CallMessageGR msgGR3 = new CallMessageGR(obj3, obj1, msg3, 400); // Below

        model.addMessage(msgGR1);
        model.addMessage(msgGR2);
        model.addMessage(msgGR3);

        // Create fragment at Y=200-300 (only msgGR2 in range)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 200), 300);

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Verify fragment only spans msgGR2
        int msg2StartX = msgGR2.getStartingX();
        int msg2EndX = msgGR2.getEndingX();
        int expectedLeftX = Math.min(msg2StartX, msg2EndX) - 20;
        int expectedWidth = Math.abs(msg2EndX - msg2StartX) + 40;

        assertEquals("Fragment should only span messages within Y range",
                expectedLeftX, fragmentGR.getX());
        assertEquals("Fragment width should only consider messages within Y range",
                expectedWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_MessageAtFragmentBoundary() {
        // Add message exactly at fragment top boundary
        GenericOperation op1 = new GenericOperation("message1");
        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 200);
        model.addMessage(msgGR1);

        // Create fragment at Y=200-300 (message at top edge)
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 200), 300);

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Message at boundary should be included
        int msg1StartX = msgGR1.getStartingX();
        int msg1EndX = msgGR1.getEndingX();
        int expectedLeftX = Math.min(msg1StartX, msg1EndX) - 20;

        assertEquals("Message at top boundary should be included",
                expectedLeftX, fragmentGR.getX());
    }

    @Test
    public void testAutoResize_MinimumWidth() {
        // Add a very short message (e.g., reflective message on same object)
        GenericOperation op1 = new GenericOperation("reflectiveMsg");
        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj1.getSDObject(), op1);
        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj1, msg1, 200);
        model.addMessage(msgGR1);

        // Create fragment at Y=180-280
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 180), 300);

        // Apply auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        // Verify minimum width is enforced (100 pixels)
        assertTrue("Fragment should respect minimum width of 100",
                fragmentGR.getWidth() >= 100);
    }

    @Test
    public void testAutoResize_NullModel() {
        // Create fragment
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(150, 200), 300);

        int originalX = fragmentGR.getX();
        int originalWidth = fragmentGR.getWidth();

        // Apply auto-resize with null model (should handle gracefully)
        fragmentGR.autoResizeToMessages(null);

        // Verify no crash and no change
        assertEquals("Fragment X should not change with null model",
                originalX, fragmentGR.getX());
        assertEquals("Fragment width should not change with null model",
                originalWidth, fragmentGR.getWidth());
    }

    @Test
    public void testAutoResize_WhenRoleClassifierMoves() {
        // Add messages between objects
        GenericOperation op1 = new GenericOperation("message1");
        GenericOperation op2 = new GenericOperation("message2");

        CallMessage msg1 = new CallMessage(obj1.getSDObject(), obj2.getSDObject(), op1);
        CallMessage msg2 = new CallMessage(obj2.getSDObject(), obj3.getSDObject(), op2);

        CallMessageGR msgGR1 = new CallMessageGR(obj1, obj2, msg1, 200);
        CallMessageGR msgGR2 = new CallMessageGR(obj2, obj3, msg2, 220);

        model.addMessage(msgGR1);
        model.addMessage(msgGR2);

        // Create fragment covering both messages
        CombinedFragment fragment = new CombinedFragment(InteractionOperator.OPT, "");
        fragment.setHeight(100);
        CombinedFragmentGR fragmentGR = new CombinedFragmentGR(fragment, new Point(0, 180), 600);

        // Add fragment to model so it's tracked
        model.addGraphicalElement(fragmentGR);

        // Manually trigger initial auto-resize
        fragmentGR.autoResizeToMessages(model, true);

        int initialFragmentX = fragmentGR.getX();
        int initialFragmentWidth = fragmentGR.getWidth();

        // Move obj3 (rightmost object) further right by 200 pixels
        // NOTE: Fragments no longer auto-resize when objects move (disabled per requirements)
        // Fragments only auto-resize on manual resize with handles
        int obj3NewX = obj3.getX() + 200;
        model.settleGraphicalElement(obj3, obj3NewX, obj3.getY());

        // Verify fragment has NOT resized (auto-resize on object move is disabled)
        int newFragmentWidth = fragmentGR.getWidth();

        // The fragment should maintain its size when objects move
        assertEquals("Fragment width should NOT change when objects move (auto-resize disabled)",
                initialFragmentWidth, newFragmentWidth);

        // The fragment position should also remain unchanged
        assertEquals("Fragment X position should NOT change when objects move",
                initialFragmentX, fragmentGR.getX());
    }
}
