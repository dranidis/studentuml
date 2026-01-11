package edu.city.studentuml.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
    @Test
    public void testParse_invalidSyntax_missingParentheses() {
        ParseResult result = parser.parse("message");

        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

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
        assertFalse(result.isValid());
    }

    // Test null input
    @Test
    public void testParse_nullInput() {
        ParseResult result = parser.parse(null);

        assertNotNull(result);
        assertFalse(result.isValid());
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
}
