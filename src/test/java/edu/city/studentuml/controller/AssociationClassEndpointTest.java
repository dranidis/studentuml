package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.EndpointType;

/**
 * Test that AssociationClassGR properly supports endpoint detection for
 * reconnection.
 * 
 * @author AI Assistant
 */
public class AssociationClassEndpointTest {

    private Helper helper;
    private ClassGR classA;
    private ClassGR classB;
    private AssociationClassGR assocClass;
    private DCDModel model;

    @Before
    public void setUp() {
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new DCDModel("test", umlProject);
        helper = new Helper(model);

        // Create two classes with explicit positions
        classA = helper.addClass("ClassA");
        model.moveGraphicalElement(classA, 100, 100);

        classB = helper.addClass("ClassB");
        model.moveGraphicalElement(classB, 300, 100);

        // Create association class between them
        assocClass = helper.addAssociationClass(classA, classB);
    }

    @Test
    public void testAssociationClassHasEndpoints() {
        // The association class should have valid endpoint positions
        Point2D sourceEndpoint = assocClass.getEndPointRoleA();
        Point2D targetEndpoint = assocClass.getEndPointRoleB();

        assertNotNull("Source endpoint should not be null", sourceEndpoint);
        assertNotNull("Target endpoint should not be null", targetEndpoint);
    }

    @Test
    public void testGetEndpointAtPoint_SourceEndpoint() {
        // Get the actual source endpoint position
        Point2D sourceEndpoint = assocClass.getEndPointRoleA();

        // Check that clicking near the source endpoint is detected
        EndpointType detected = assocClass.getEndpointAtPoint(sourceEndpoint);

        assertEquals("Should detect source endpoint when clicking on it",
                EndpointType.SOURCE, detected);
    }

    @Test
    public void testGetEndpointAtPoint_TargetEndpoint() {
        // Get the actual target endpoint position
        Point2D targetEndpoint = assocClass.getEndPointRoleB();

        // Check that clicking near the target endpoint is detected
        EndpointType detected = assocClass.getEndpointAtPoint(targetEndpoint);

        assertEquals("Should detect target endpoint when clicking on it",
                EndpointType.TARGET, detected);
    }

    @Test
    public void testGetEndpointAtPoint_NoEndpoint() {
        // Click somewhere far from any endpoint (middle of the diagram)
        Point2D farPoint = new Point2D.Double(500, 500);

        EndpointType detected = assocClass.getEndpointAtPoint(farPoint);

        assertEquals("Should not detect any endpoint when clicking far away",
                EndpointType.NONE, detected);
    }

    @Test
    public void testGetEndpointAtPoint_MidpointNotEndpoint() {
        // Get the midpoint of the association line
        Point2D source = assocClass.getEndPointRoleA();
        Point2D target = assocClass.getEndPointRoleB();
        Point2D midpoint = new Point2D.Double(
                (source.getX() + target.getX()) / 2,
                (source.getY() + target.getY()) / 2);

        // The midpoint should NOT be detected as an endpoint
        EndpointType detected = assocClass.getEndpointAtPoint(midpoint);

        assertEquals("Midpoint should not be detected as an endpoint",
                EndpointType.NONE, detected);
    }
}
