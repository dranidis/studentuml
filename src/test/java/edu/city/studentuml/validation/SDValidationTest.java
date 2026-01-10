package edu.city.studentuml.validation;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.graphical.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Sequence Diagram focus of control validation. Validates that the
 * system correctly detects invalid message sequences where objects send
 * messages without having focus.
 * 
 * @author Copilot
 */
public class SDValidationTest {

    private UMLProject project;
    private SDModel model;
    private ActorInstance actor;
    private ActorInstanceGR actorGR;
    private SDObject obj1;
    private SDObjectGR obj1GR;
    private SDObject obj2;
    private SDObjectGR obj2GR;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        model = new SDModel("Test SD", project);

        // Create actor
        Actor actorType = new Actor("User");
        actor = new ActorInstance("user", actorType);
        actorGR = new ActorInstanceGR(actor, 50);
        model.addRoleClassifier(actorGR);

        // Create two objects
        DesignClass class1 = new DesignClass("Object1");
        obj1 = new SDObject("obj1", class1);
        obj1GR = new SDObjectGR(obj1, 200);
        model.addRoleClassifier(obj1GR);

        DesignClass class2 = new DesignClass("Object2");
        obj2 = new SDObject("obj2", class2);
        obj2GR = new SDObjectGR(obj2, 350);
        model.addRoleClassifier(obj2GR);
    }

    /**
     * Test Case 1: Object sends message without having focus Scenario: - Actor
     * sends message to obj1 (obj1 has focus) - obj2 tries to send message to obj1
     * (ERROR: obj2 doesn't have focus) Expected: obj2's message should have
     * validation error
     */
    @Test
    public void testObjectSendsWithoutFocus() {
        // Message 1: Actor calls obj1 (valid - actor starts with focus)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: obj2 tries to call obj1 (INVALID - obj2 doesn't have focus, obj1 does)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(obj2, obj1, op2);
        CallMessageGR call2GR = new CallMessageGR(obj2GR, obj1GR, call2, 130);
        model.addMessage(call2GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that call2 has validation error
        String error2 = call2GR.getErrorMsg();
        assertNotNull("Message 2 should have validation error", error2);
        assertTrue("Error should mention 'does not have the focus'",
                error2.contains("does not have the focus"));

        // Check that call1 is valid
        String error1 = call1GR.getErrorMsg();
        assertTrue("Message 1 should be valid", error1 == null || error1.isEmpty());
    }

    /**
     * Test Case 2: Object sends message after call but before return Scenario: -
     * Actor calls obj1 (obj1 has focus) - Actor tries to send another message
     * (ERROR: actor doesn't have focus anymore) Expected: Second actor message
     * should have validation error
     */
    @Test
    public void testCallerSendsBeforeReturn() {
        // Message 1: Actor calls obj1 (valid)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: Actor tries to call obj2 (INVALID - actor doesn't have focus)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(actor, obj2, op2);
        CallMessageGR call2GR = new CallMessageGR(actorGR, obj2GR, call2, 130);
        model.addMessage(call2GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that call2 has validation error
        String error2 = call2GR.getErrorMsg();
        assertNotNull("Message 2 should have validation error", error2);
        assertTrue("Error should mention 'does not have the focus'",
                error2.contains("does not have the focus"));
    }

    /**
     * Test Case 3: Return from object that doesn't have focus Scenario: - Actor
     * calls obj1 (obj1 has focus) - obj2 tries to return to actor (ERROR: obj2
     * never received a call) Expected: obj2's return should have validation error
     */
    @Test
    public void testReturnWithoutCall() {
        // Message 1: Actor calls obj1 (valid)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: obj2 tries to return to actor (INVALID - obj2 was never called)
        ReturnMessage return1 = new ReturnMessage(obj2, actor, "result");
        ReturnMessageGR return1GR = new ReturnMessageGR(obj2GR, actorGR, return1, 130);
        model.addMessage(return1GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that return1 has validation error
        String error2 = return1GR.getErrorMsg();
        assertNotNull("Return message should have validation error", error2);
        assertTrue("Error should mention cannot return",
                error2.contains("Cannot return") || error2.contains("does not have the focus"));
    }

    /**
     * Test Case 4: Missing return - valid until another call Scenario: - Actor
     * calls obj1 (obj1 has focus) - obj1 calls obj2 (obj2 has focus) - obj1 tries
     * to call something (ERROR: obj1 doesn't have focus) Expected: obj1's second
     * call should have validation error
     */
    @Test
    public void testMissingReturnCausesError() {
        // Message 1: Actor calls obj1 (valid)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: obj1 calls obj2 (valid - obj1 has focus)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(obj1, obj2, op2);
        CallMessageGR call2GR = new CallMessageGR(obj1GR, obj2GR, call2, 130);
        model.addMessage(call2GR);

        // Message 3: obj1 tries to send another call (INVALID - obj1 doesn't have focus, obj2 does)
        GenericOperation op3 = new GenericOperation("operation3");
        CallMessage call3 = new CallMessage(obj1, obj2, op3);
        CallMessageGR call3GR = new CallMessageGR(obj1GR, obj2GR, call3, 160);
        model.addMessage(call3GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that call3 has validation error
        String error3 = call3GR.getErrorMsg();
        assertNotNull("Message 3 should have validation error", error3);
        assertTrue("Error should mention 'does not have the focus'",
                error3.contains("does not have the focus"));

        // Check that call1 and call2 are valid
        String error1 = call1GR.getErrorMsg();
        String error2 = call2GR.getErrorMsg();
        assertTrue("Message 1 should be valid", error1 == null || error1.isEmpty());
        assertTrue("Message 2 should be valid", error2 == null || error2.isEmpty());
    }

    /**
     * Test Case 5: Create message without return causes focus error Scenario: -
     * Actor creates obj1 (obj1 has focus) - Actor tries to send another message
     * (ERROR: actor doesn't have focus) Expected: Second actor message should have
     * validation error
     */
    @Test
    public void testCreateWithoutReturnCausesError() {
        // Remove obj1 from model (we'll create it dynamically)
        model.getGraphicalElements().remove(obj1GR);

        // Message 1: Actor creates obj1 (valid)
        CreateMessage create1 = new CreateMessage(actor, obj1);
        CreateMessageGR create1GR = new CreateMessageGR(actorGR, obj1GR, create1, 100);
        model.addMessage(create1GR);

        // Message 2: Actor tries to call obj2 (INVALID - actor doesn't have focus, obj1 does)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(actor, obj2, op2);
        CallMessageGR call2GR = new CallMessageGR(actorGR, obj2GR, call2, 130);
        model.addMessage(call2GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that call2 has validation error
        String error2 = call2GR.getErrorMsg();
        assertNotNull("Message 2 should have validation error", error2);
        assertTrue("Error should mention 'does not have the focus'",
                error2.contains("does not have the focus"));
    }

    /**
     * Test Case 6: Proper returns clear validation errors Scenario: - Actor calls
     * obj1 (obj1 has focus) - obj1 returns to actor (actor has focus) - Actor calls
     * obj2 (valid now) Expected: All messages should be valid
     */
    @Test
    public void testProperReturnsAllowSubsequentCalls() {
        // Message 1: Actor calls obj1 (valid)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: obj1 returns to actor (valid)
        ReturnMessage return1 = new ReturnMessage(obj1, actor, "result1");
        ReturnMessageGR return1GR = new ReturnMessageGR(obj1GR, actorGR, return1, 130);
        model.addMessage(return1GR);

        // Message 3: Actor calls obj2 (valid - actor has focus again)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(actor, obj2, op2);
        CallMessageGR call2GR = new CallMessageGR(actorGR, obj2GR, call2, 160);
        model.addMessage(call2GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that all messages are valid
        String error1 = call1GR.getErrorMsg();
        String error2 = return1GR.getErrorMsg();
        String error3 = call2GR.getErrorMsg();

        assertTrue("Message 1 should be valid", error1 == null || error1.isEmpty());
        assertTrue("Message 2 should be valid", error2 == null || error2.isEmpty());
        assertTrue("Message 3 should be valid", error3 == null || error3.isEmpty());
    }

    /**
     * Test Case 7: Nested calls require proper return order Scenario: - Actor calls
     * obj1 (obj1 has focus) - obj1 calls obj2 (obj2 has focus) - obj1 tries to
     * return to actor (ERROR: should return to obj2 first) Expected: Invalid return
     * should have validation error
     */
    @Test
    public void testNestedCallsRequireProperReturnOrder() {
        // Message 1: Actor calls obj1 (valid)
        GenericOperation op1 = new GenericOperation("operation1");
        CallMessage call1 = new CallMessage(actor, obj1, op1);
        CallMessageGR call1GR = new CallMessageGR(actorGR, obj1GR, call1, 100);
        model.addMessage(call1GR);

        // Message 2: obj1 calls obj2 (valid)
        GenericOperation op2 = new GenericOperation("operation2");
        CallMessage call2 = new CallMessage(obj1, obj2, op2);
        CallMessageGR call2GR = new CallMessageGR(obj1GR, obj2GR, call2, 130);
        model.addMessage(call2GR);

        // Message 3: obj1 tries to return to actor (INVALID - obj1 doesn't have focus)
        // This is wrong because obj2 still has focus and should return first
        ReturnMessage return1 = new ReturnMessage(obj1, actor, "result1");
        ReturnMessageGR return1GR = new ReturnMessageGR(obj1GR, actorGR, return1, 160);
        model.addMessage(return1GR);

        // Validate
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that return1 has validation error
        String error3 = return1GR.getErrorMsg();
        assertNotNull("Return message should have validation error", error3);
        assertTrue("Error should mention cannot return or doesn't have focus",
                error3.contains("Cannot return") || error3.contains("does not have the focus"));
    }
}
