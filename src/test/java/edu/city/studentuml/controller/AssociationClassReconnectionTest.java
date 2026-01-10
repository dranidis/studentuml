package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.EndpointType;

/**
 * Test reconnection of AssociationClassGR endpoints.
 * 
 * @author AI Assistant
 */
public class AssociationClassReconnectionTest {

    private Helper helper;
    private DCDModel model;
    private ClassGR classA;
    private ClassGR classB;
    private ClassGR classC;
    private AssociationClassGR assocClass;

    @Before
    public void setUp() {
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new DCDModel("test", umlProject);
        helper = new Helper(model);

        // Create three classes with explicit positions
        classA = helper.addClass("ClassA");
        model.moveGraphicalElement(classA, 100, 100);

        classB = helper.addClass("ClassB");
        model.moveGraphicalElement(classB, 300, 100);

        classC = helper.addClass("ClassC");
        model.moveGraphicalElement(classC, 200, 300);

        // Create association class between A and B
        assocClass = helper.addAssociationClass(classA, classB);
    }

    @Test
    public void testReconnectSourceEndpoint() {
        // Initial state: A ---association-class---> B
        assertEquals("Initial source should be ClassA", classA, assocClass.getA());
        assertEquals("Initial target should be ClassB", classB, assocClass.getB());

        // Simulate reconnecting source endpoint from A to C
        boolean canReconnect = assocClass.canReconnect(EndpointType.SOURCE, classC);
        assertEquals("Should be able to reconnect source to ClassC", true, canReconnect);

        // Perform reconnection through domain model
        boolean reconnected = assocClass.reconnectSource(classC);
        assertEquals("Reconnection should succeed", true, reconnected);

        // Create new graphical element with updated endpoints
        AssociationClassGR newAssocClass = new AssociationClassGR(
                classC, assocClass.getB(), assocClass.getAssociationClass());

        assertNotNull("New association class should not be null", newAssocClass);
        assertEquals("New source should be ClassC", classC, newAssocClass.getA());
        assertEquals("Target should remain ClassB", classB, newAssocClass.getB());

        // Verify endpoints are different
        assertNotEquals("Endpoint positions should be different",
                assocClass.getEndPointRoleA(), newAssocClass.getEndPointRoleA());
    }

    @Test
    public void testReconnectTargetEndpoint() {
        // Initial state: A ---association-class---> B
        assertEquals("Initial source should be ClassA", classA, assocClass.getA());
        assertEquals("Initial target should be ClassB", classB, assocClass.getB());

        // Simulate reconnecting target endpoint from B to C
        boolean canReconnect = assocClass.canReconnect(EndpointType.TARGET, classC);
        assertEquals("Should be able to reconnect target to ClassC", true, canReconnect);

        // Perform reconnection through domain model
        boolean reconnected = assocClass.reconnectTarget(classC);
        assertEquals("Reconnection should succeed", true, reconnected);

        // Create new graphical element with updated endpoints
        AssociationClassGR newAssocClass = new AssociationClassGR(
                assocClass.getA(), classC, assocClass.getAssociationClass());

        assertNotNull("New association class should not be null", newAssocClass);
        assertEquals("Source should remain ClassA", classA, newAssocClass.getA());
        assertEquals("New target should be ClassC", classC, newAssocClass.getB());

        // Verify endpoints are different
        assertNotEquals("Endpoint positions should be different",
                assocClass.getEndPointRoleB(), newAssocClass.getEndPointRoleB());
    }

    @Test
    public void testReconnectToSelf() {
        // Test creating a reflexive (self) association class
        boolean canReconnect = assocClass.canReconnect(EndpointType.TARGET, classA);
        assertEquals("Should be able to create reflexive association class", true, canReconnect);

        boolean reconnected = assocClass.reconnectTarget(classA);
        assertEquals("Reconnection to same class should succeed", true, reconnected);

        AssociationClassGR reflexiveAssocClass = new AssociationClassGR(
                classA, classA, assocClass.getAssociationClass());

        assertNotNull("Reflexive association class should not be null", reflexiveAssocClass);
        assertEquals("Both endpoints should be ClassA", classA, reflexiveAssocClass.getA());
        assertEquals("Both endpoints should be ClassA", classA, reflexiveAssocClass.getB());
    }

    @Test
    public void testAssociationClassDomainModelSharingAfterReconnection() {
        // Verify that the domain model (AssociationClass) is shared
        // even after reconnection, not duplicated

        AssociationClassGR newAssocClass = new AssociationClassGR(
                classC, classB, assocClass.getAssociationClass());

        // The domain model should be the SAME object (reference equality)
        assertEquals("Domain model should be shared after reconnection",
                assocClass.getAssociationClass(), newAssocClass.getAssociationClass());
    }
}
