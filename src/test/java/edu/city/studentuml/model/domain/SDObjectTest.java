package edu.city.studentuml.model.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for SDObject stereotype functionality.
 * 
 * @author Dimitris Dranidis
 */
public class SDObjectTest {

    private DesignClass testClass;
    private SDObject sdObject;

    @Before
    public void setUp() {
        testClass = new DesignClass("TestClass");
        sdObject = new SDObject("obj1", testClass);
    }

    @Test
    public void testStereotypeDefaultIsNull() {
        assertNull("Default stereotype should be null", sdObject.getStereotype());
    }

    @Test
    public void testSetAndGetStereotype() {
        sdObject.setStereotype("interface");
        assertEquals("Stereotype should be 'interface'", "interface", sdObject.getStereotype());
    }

    @Test
    public void testSetMultipleStereotypes() {
        sdObject.setStereotype("metaclass");
        assertEquals("Stereotype should be 'metaclass'", "metaclass", sdObject.getStereotype());

        sdObject.setStereotype("abstract");
        assertEquals("Stereotype should be 'abstract'", "abstract", sdObject.getStereotype());

        sdObject.setStereotype("controller");
        assertEquals("Stereotype should be 'controller'", "controller", sdObject.getStereotype());
    }

    @Test
    public void testSetStereotypeToNull() {
        sdObject.setStereotype("interface");
        assertEquals("Stereotype should be 'interface'", "interface", sdObject.getStereotype());

        sdObject.setStereotype(null);
        assertNull("Stereotype should be null", sdObject.getStereotype());
    }

    @Test
    public void testSetEmptyStereotypeNormalizesToNull() {
        sdObject.setStereotype("");
        assertNull("Empty string should be normalized to null", sdObject.getStereotype());

        sdObject.setStereotype("   ");
        assertNull("Whitespace-only string should be normalized to null", sdObject.getStereotype());
    }

    @Test
    public void testClonePreservesStereotype() {
        sdObject.setStereotype("interface");
        SDObject cloned = sdObject.clone();

        assertEquals("Cloned object should have same stereotype", "interface", cloned.getStereotype());
        assertNotSame("Cloned object should be different instance", sdObject, cloned);
    }

    @Test
    public void testCloneWithNoStereotype() {
        SDObject cloned = sdObject.clone();
        assertNull("Cloned object should have null stereotype", cloned.getStereotype());
    }

    @Test
    public void testStereotypeIndependentOfName() {
        sdObject.setName("newName");
        sdObject.setStereotype("metaclass");

        assertEquals("Name should be 'newName'", "newName", sdObject.getName());
        assertEquals("Stereotype should be 'metaclass'", "metaclass", sdObject.getStereotype());
    }

    @Test
    public void testStereotypeWithSpecialCharacters() {
        sdObject.setStereotype("custom-stereotype");
        assertEquals("Stereotype with hyphen should be preserved", "custom-stereotype", sdObject.getStereotype());

        sdObject.setStereotype("Stereotype123");
        assertEquals("Stereotype with numbers should be preserved", "Stereotype123", sdObject.getStereotype());
    }

    @Test
    public void testScopeDefaultIsInstance() {
        assertEquals("Default scope should be INSTANCE", AbstractObject.Scope.INSTANCE, sdObject.getScope());
    }

    @Test
    public void testSetAndGetScope() {
        sdObject.setScope(AbstractObject.Scope.CLASS);
        assertEquals("Scope should be CLASS", AbstractObject.Scope.CLASS, sdObject.getScope());
    }

    @Test
    public void testSetScopeToNull() {
        sdObject.setScope(AbstractObject.Scope.CLASS);
        sdObject.setScope(null);
        assertEquals("Null scope should default to INSTANCE", AbstractObject.Scope.INSTANCE, sdObject.getScope());
    }

    @Test
    public void testClonePreservesScope() {
        sdObject.setScope(AbstractObject.Scope.CLASS);
        SDObject cloned = sdObject.clone();

        assertEquals("Cloned object should have same scope", AbstractObject.Scope.CLASS, cloned.getScope());
        assertNotSame("Cloned object should be different instance", sdObject, cloned);
    }

    @Test
    public void testToStringWithInstanceScope() {
        // Instance scope: "instanceName : ClassName"
        sdObject.setScope(AbstractObject.Scope.INSTANCE);
        String toString = sdObject.toString();
        assertTrue("Instance scope should show 'instanceName : ClassName'",
                toString.contains("obj1") && toString.contains(":") && toString.contains("TestClass"));
    }

    @Test
    public void testToStringWithClassScope() {
        // Class scope: just "ClassName" (no instance name or colon)
        sdObject.setScope(AbstractObject.Scope.CLASS);
        String toString = sdObject.toString();
        assertEquals("Class scope should show only class name", "TestClass", toString);
        assertFalse("Class scope should not contain instance name", toString.contains("obj1"));
        assertFalse("Class scope should not contain colon", toString.contains(":"));
    }

    @Test
    public void testClonePreservesScopeAndStereotype() {
        sdObject.setStereotype("interface");
        sdObject.setScope(AbstractObject.Scope.CLASS);
        SDObject cloned = sdObject.clone();

        assertEquals("Cloned object should have same stereotype", "interface", cloned.getStereotype());
        assertEquals("Cloned object should have same scope", AbstractObject.Scope.CLASS, cloned.getScope());
    }
}
