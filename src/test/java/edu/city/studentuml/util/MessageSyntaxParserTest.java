package edu.city.studentuml.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.MessageSyntaxParser.ParameterInfo;
import edu.city.studentuml.util.MessageSyntaxParser.ParseResult;

/**
 * Test class for MessageSyntaxParser
 */
public class MessageSyntaxParserTest {

    private MessageSyntaxParser parser;

    @Before
    public void setUp() {
        parser = new MessageSyntaxParser();
    }

    // Test simple message without parameters
    @Test
    public void testParse_simpleMessageNoParameters() {
        ParseResult result = parser.parse("message()");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertNull(result.getReturnValue());
        assertNull(result.getReturnType());
        assertTrue(result.getParameters().isEmpty());
    }

    // Test message with single parameter
    @Test
    public void testParse_messageWithSingleParameter() {
        ParseResult result = parser.parse("message(param1)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals(1, result.getParameters().size());
        assertEquals("param1", result.getParameters().get(0).getName());
        assertNull(result.getParameters().get(0).getType());
    }

    // Test message with multiple parameters
    @Test
    public void testParse_messageWithMultipleParameters() {
        ParseResult result = parser.parse("message(param1, param2, param3)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals(3, result.getParameters().size());
        assertEquals("param1", result.getParameters().get(0).getName());
        assertEquals("param2", result.getParameters().get(1).getName());
        assertEquals("param3", result.getParameters().get(2).getName());
    }

    // Test message with typed parameters
    @Test
    public void testParse_messageWithTypedParameters() {
        ParseResult result = parser.parse("message(param1: String, param2: int)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals(2, result.getParameters().size());
        assertEquals("param1", result.getParameters().get(0).getName());
        assertEquals("String", result.getParameters().get(0).getType());
        assertEquals("param2", result.getParameters().get(1).getName());
        assertEquals("int", result.getParameters().get(1).getType());
    }

    // Test message with return value
    @Test
    public void testParse_messageWithReturnValue() {
        ParseResult result = parser.parse("result := message()");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertNull(result.getReturnType());
        assertTrue(result.getParameters().isEmpty());
    }

    // Test message with typed return value
    @Test
    public void testParse_messageWithTypedReturnValue() {
        ParseResult result = parser.parse("result: String := message()");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertEquals("String", result.getReturnType());
        assertTrue(result.getParameters().isEmpty());
    }

    // Test complete message with everything
    @Test
    public void testParse_completeMessage() {
        ParseResult result = parser.parse("result: double := calculateTotal(items: List, taxRate: double)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("calculateTotal", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertEquals("double", result.getReturnType());
        assertEquals(2, result.getParameters().size());
        assertEquals("items", result.getParameters().get(0).getName());
        assertEquals("List", result.getParameters().get(0).getType());
        assertEquals("taxRate", result.getParameters().get(1).getName());
        assertEquals("double", result.getParameters().get(1).getType());
    }

    // Test message with whitespace variations
    @Test
    public void testParse_withWhitespace() {
        ParseResult result = parser.parse("  result  :  String  :=  message  (  param1  ,  param2  )  ");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertEquals("String", result.getReturnType());
        assertEquals(2, result.getParameters().size());
    }

    // Test invalid syntax - missing parentheses
    // Note: Missing parentheses is now VALID - defaults to no parameters
    // This test was removed as parentheses are now optional

    // Test invalid syntax - unmatched parentheses
    @Test
    public void testParse_invalidSyntax_unmatchedParentheses() {
        ParseResult result = parser.parse("message(param1");

        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // Test invalid syntax - empty message name
    @Test
    public void testParse_invalidSyntax_emptyMessageName() {
        ParseResult result = parser.parse("()");

        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // Test invalid syntax - missing assignment operator
    @Test
    public void testParse_invalidSyntax_missingAssignment() {
        ParseResult result = parser.parse("result message()");

        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    // Test empty input
    @Test
    public void testParse_emptyInput() {
        ParseResult result = parser.parse("");

        assertNotNull(result);
        assertTrue(result.isValid());
    }

    // Test null input
    @Test
    public void testParse_nullInput() {
        ParseResult result = parser.parse(null);

        assertNotNull(result);
        assertTrue(result.isValid());
    }

    // Test ParameterInfo
    @Test
    public void testParameterInfo_withoutType() {
        ParameterInfo param = new ParameterInfo("param1", null);

        assertEquals("param1", param.getName());
        assertNull(param.getType());
    }

    @Test
    public void testParameterInfo_withType() {
        ParameterInfo param = new ParameterInfo("param1", "String");

        assertEquals("param1", param.getName());
        assertEquals("String", param.getType());
    }

    // Test create message syntax
    @Test
    public void testParse_createMessage() {
        ParseResult result = parser.parse("create(name: String, age: int)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("create", result.getMessageName());
        assertNull(result.getReturnValue());
        assertNull(result.getReturnType());
        assertEquals(2, result.getParameters().size());
        assertEquals("name", result.getParameters().get(0).getName());
        assertEquals("String", result.getParameters().get(0).getType());
    }

    // Test create message with return value
    @Test
    public void testParse_createMessageWithReturnValue() {
        ParseResult result = parser.parse("obj: Customer := create(name: String)");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("create", result.getMessageName());
        assertEquals("obj", result.getReturnValue());
        assertEquals("Customer", result.getReturnType());
        assertEquals(1, result.getParameters().size());
        assertEquals("name", result.getParameters().get(0).getName());
        assertEquals("String", result.getParameters().get(0).getType());
    }

    // Test message without parentheses (should default to no parameters)
    @Test
    public void testParse_messageWithoutParentheses() {
        ParseResult result = parser.parse("message");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertNull(result.getReturnValue());
        assertNull(result.getReturnType());
        assertEquals(0, result.getParameters().size());
    }

    // Test message with return value but without parentheses
    @Test
    public void testParse_messageWithReturnValueNoParentheses() {
        ParseResult result = parser.parse("result := message");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertNull(result.getReturnType());
        assertEquals(0, result.getParameters().size());
    }

    // Test message with typed return value but without parentheses
    @Test
    public void testParse_messageWithTypedReturnValueNoParentheses() {
        ParseResult result = parser.parse("result: String := message");

        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals("message", result.getMessageName());
        assertEquals("result", result.getReturnValue());
        assertEquals("String", result.getReturnType());
        assertEquals(0, result.getParameters().size());
    }

    // ========== RECONSTRUCT METHOD TESTS ==========

    @Test
    public void testReconstruct_simpleMessageNoParameters() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("doSomething");
        CallMessage message = new CallMessage(from, to, op);

        String result = parser.reconstruct(message);

        assertEquals("doSomething()", result);
    }

    @Test
    public void testReconstruct_messageWithSingleParameter() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("doSomething");
        CallMessage message = new CallMessage(from, to, op);

        MethodParameter param = new MethodParameter("value");
        message.addParameter(param);

        String result = parser.reconstruct(message);

        assertEquals("doSomething(value)", result);
    }

    @Test
    public void testReconstruct_messageWithTypedParameter() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("doSomething");
        CallMessage message = new CallMessage(from, to, op);

        MethodParameter param = new MethodParameter("value");
        param.setType(new DataType("String"));
        message.addParameter(param);

        String result = parser.reconstruct(message);

        assertEquals("doSomething(value: String)", result);
    }

    @Test
    public void testReconstruct_messageWithMultipleParameters() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("calculate");
        CallMessage message = new CallMessage(from, to, op);

        MethodParameter param1 = new MethodParameter("x");
        param1.setType(new DataType("int"));
        message.addParameter(param1);

        MethodParameter param2 = new MethodParameter("y");
        param2.setType(new DataType("int"));
        message.addParameter(param2);

        String result = parser.reconstruct(message);

        assertEquals("calculate(x: int, y: int)", result);
    }

    @Test
    public void testReconstruct_messageWithReturnValue() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("getValue");
        CallMessage message = new CallMessage(from, to, op);

        MessageReturnValue returnVal = new MessageReturnValue("result");
        message.setReturnValue(returnVal);

        String result = parser.reconstruct(message);

        assertEquals("result := getValue()", result);
    }

    @Test
    public void testReconstruct_messageWithTypedReturnValue() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("getValue");
        CallMessage message = new CallMessage(from, to, op);

        MessageReturnValue returnVal = new MessageReturnValue("result");
        message.setReturnValue(returnVal);
        message.setReturnType(new DataType("String"));

        String result = parser.reconstruct(message);

        assertEquals("result: String := getValue()", result);
    }

    @Test
    public void testReconstruct_completeMessage() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("calculateTotal");
        CallMessage message = new CallMessage(from, to, op);

        // Add return value
        MessageReturnValue returnVal = new MessageReturnValue("total");
        message.setReturnValue(returnVal);
        message.setReturnType(new DataType("double"));

        // Add parameters
        MethodParameter param1 = new MethodParameter("items");
        param1.setType(new DataType("List"));
        message.addParameter(param1);

        MethodParameter param2 = new MethodParameter("taxRate");
        param2.setType(new DataType("double"));
        message.addParameter(param2);

        String result = parser.reconstruct(message);

        assertEquals("total: double := calculateTotal(items: List, taxRate: double)", result);
    }

    @Test
    public void testReconstruct_nullMessage() {
        String result = parser.reconstruct(null);
        assertEquals("", result);
    }

    @Test
    public void testReconstruct_messageWithEmptyReturnValue() {
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation("doSomething");
        CallMessage message = new CallMessage(from, to, op);

        // Set empty return value - should not be included in reconstruction
        MessageReturnValue returnVal = new MessageReturnValue("");
        message.setReturnValue(returnVal);

        String result = parser.reconstruct(message);

        assertEquals("doSomething()", result);
    }

    @Test
    public void testReconstruct_roundTrip_simpleMessage() {
        // Test that parsing and reconstructing yields the same result
        String original = "doSomething()";
        ParseResult parsed = parser.parse(original);

        // Create message from parsed result
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation(parsed.getMessageName());
        CallMessage message = new CallMessage(from, to, op);

        String reconstructed = parser.reconstruct(message);

        assertEquals(original, reconstructed);
    }

    @Test
    public void testReconstruct_roundTrip_completeMessage() {
        // Test that parsing and reconstructing yields equivalent result
        String original = "result: String := calculate(x: int, y: int)";
        ParseResult parsed = parser.parse(original);

        assertTrue(parsed.isValid());

        // Create message from parsed result
        SDObject from = new SDObject("Obj1", new DesignClass("Obj1Class"));
        SDObject to = new SDObject("Obj2", new DesignClass("Obj2Class"));
        GenericOperation op = new GenericOperation(parsed.getMessageName());
        CallMessage message = new CallMessage(from, to, op);

        // Set return value
        if (parsed.getReturnValue() != null) {
            message.setReturnValue(new MessageReturnValue(parsed.getReturnValue()));
            if (parsed.getReturnType() != null) {
                message.setReturnType(new DataType(parsed.getReturnType()));
            }
        }

        // Set parameters
        for (ParameterInfo paramInfo : parsed.getParameters()) {
            MethodParameter param = new MethodParameter(paramInfo.getName());
            if (paramInfo.getType() != null) {
                param.setType(new DataType(paramInfo.getType()));
            }
            message.addParameter(param);
        }

        String reconstructed = parser.reconstruct(message);

        assertEquals(original, reconstructed);

        // Parse again to verify round-trip
        ParseResult reparsed = parser.parse(reconstructed);
        assertTrue(reparsed.isValid());
        assertEquals(parsed.getMessageName(), reparsed.getMessageName());
        assertEquals(parsed.getReturnValue(), reparsed.getReturnValue());
        assertEquals(parsed.getReturnType(), reparsed.getReturnType());
        assertEquals(parsed.getParameters().size(), reparsed.getParameters().size());
    }
}
