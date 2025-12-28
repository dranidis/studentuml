package edu.city.studentuml.model.domain;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Tests for Dependency class stereotype functionality.
 * 
 * @author StudentUML Team
 */
public class DependencyTest {

    private DesignClass classA;
    private DesignClass classB;
    private Dependency dependency;
    private XMLStreamer streamer;
    private Element testNode;

    @Before
    public void setUp() throws Exception {
        classA = new DesignClass("ClassA");
        classB = new DesignClass("ClassB");
        dependency = new Dependency(classA, classB);

        // Register classes in SystemWideObjectNamePool for XML serialization
        SystemWideObjectNamePool.getInstance().getNameForObject(classA);
        SystemWideObjectNamePool.getInstance().getNameForObject(classB);

        // Set up XML infrastructure
        streamer = new XMLStreamer();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        testNode = doc.createElement("dependency");
    }

    @Test
    public void testDefaultStereotype_shouldBeNull() {
        assertNull("Default stereotype should be null", dependency.getStereotype());
    }

    @Test
    public void testSetStereotype_withValidValue() {
        dependency.setStereotype("use");
        assertEquals("Stereotype should be 'use'", "use", dependency.getStereotype());
    }

    @Test
    public void testSetStereotype_withNullValue() {
        dependency.setStereotype("create");
        dependency.setStereotype(null);
        assertNull("Stereotype should be null after setting to null", dependency.getStereotype());
    }

    @Test
    public void testSetStereotype_withEmptyString() {
        dependency.setStereotype("");
        assertEquals("Stereotype should be empty string", "", dependency.getStereotype());
    }

    @Test
    public void testSetStereotype_withCommonStereotypes() {
        String[] commonStereotypes = { "use", "create", "call", "instantiate", "import", "access" };

        for (String stereotype : commonStereotypes) {
            dependency.setStereotype(stereotype);
            assertEquals("Stereotype should be '" + stereotype + "'", stereotype, dependency.getStereotype());
        }
    }

    @Test
    public void testConstructor_fromAndToAreSet() {
        assertEquals("From class should be ClassA", classA, dependency.getFrom());
        assertEquals("To class should be ClassB", classB, dependency.getTo());
    }

    @Test
    public void testStreamToXML_withoutStereotype() {
        dependency.streamToXML(testNode, streamer);

        assertNotNull("from attribute should be set", testNode.getAttribute("from"));
        assertNotNull("to attribute should be set", testNode.getAttribute("to"));
        assertEquals("stereotype attribute should be empty", "", testNode.getAttribute("stereotype"));
    }

    @Test
    public void testStreamToXML_withStereotype() {
        dependency.setStereotype("use");
        dependency.streamToXML(testNode, streamer);

        assertNotNull("from attribute should be set", testNode.getAttribute("from"));
        assertNotNull("to attribute should be set", testNode.getAttribute("to"));
        assertEquals("stereotype attribute should be 'use'", "use", testNode.getAttribute("stereotype"));
    }

    @Test
    public void testStreamFromXML_withStereotype() {
        testNode.setAttribute("stereotype", "create");
        dependency.streamFromXML(testNode, streamer, dependency);

        assertEquals("Stereotype should be 'create'", "create", dependency.getStereotype());
    }

    @Test
    public void testStreamFromXML_withoutStereotype() {
        // Don't set stereotype attribute (simulates old XML files)
        dependency.streamFromXML(testNode, streamer, dependency);

        assertNull("Stereotype should remain null", dependency.getStereotype());
    }

    @Test
    public void testStreamFromXML_withEmptyStereotype() {
        testNode.setAttribute("stereotype", "");
        dependency.streamFromXML(testNode, streamer, dependency);

        assertNull("Stereotype should be null for empty attribute", dependency.getStereotype());
    }

    @Test
    public void testStereotypePersistence_fullCycle() {
        // Set stereotype
        dependency.setStereotype("instantiate");

        // Serialize to XML
        dependency.streamToXML(testNode, streamer);
        assertEquals("XML should have stereotype attribute", "instantiate", testNode.getAttribute("stereotype"));

        // Create new dependency and deserialize
        Dependency loadedDependency = new Dependency(classA, classB);
        loadedDependency.streamFromXML(testNode, streamer, loadedDependency);

        // Verify stereotype persisted
        assertEquals("Loaded dependency should have same stereotype", "instantiate",
                loadedDependency.getStereotype());
    }
}
