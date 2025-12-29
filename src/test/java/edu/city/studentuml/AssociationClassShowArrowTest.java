package edu.city.studentuml;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;

/**
 * Test to verify that showArrow and labelDirection properties work correctly
 * for association classes.
 */
public class AssociationClassShowArrowTest {

    private ConceptualClass classA;
    private ConceptualClass classB;
    private ConceptualClassGR classAGR;
    private ConceptualClassGR classBGR;
    private ConceptualAssociationClass assocClass;
    private AssociationClassGR assocClassGR;

    @Before
    public void setUp() {
        // Create two conceptual classes
        classA = new ConceptualClass("ClassA");
        classB = new ConceptualClass("ClassB");

        // Create graphical representations
        classAGR = new ConceptualClassGR(classA, new Point(0, 0));
        classBGR = new ConceptualClassGR(classB, new Point(100, 100));

        // Create association class
        assocClass = new ConceptualAssociationClass(classA, classB);
        assocClass.setName("Enrollment");

        // Create graphical representation
        assocClassGR = new AssociationClassGR(classAGR, classBGR, assocClass);
    }

    @Test
    public void testShowArrowDefaultValue() {
        // By default, showArrow should be false
        assertFalse("showArrow should be false by default", assocClass.getShowArrow());
    }

    @Test
    public void testSetShowArrowTrue() {
        // Set showArrow to true
        assocClass.setShowArrow(true);
        assertTrue("showArrow should be true after setting", assocClass.getShowArrow());

        // Verify it's propagated to the inner Association
        assertTrue("Inner association should also have showArrow=true",
                assocClass.getAssociation().getShowArrow());
    }

    @Test
    public void testSetShowArrowFalse() {
        // Set showArrow to true, then back to false
        assocClass.setShowArrow(true);
        assocClass.setShowArrow(false);
        assertFalse("showArrow should be false after setting", assocClass.getShowArrow());
    }

    @Test
    public void testLabelDirectionDefaultValue() {
        // By default, labelDirection should be FROM_A_TO_B (3)
        assertEquals("labelDirection should be FROM_A_TO_B by default", 3, assocClass.getLabelDirection());
    }

    @Test
    public void testSetLabelDirection() {
        // Set labelDirection to FROM_B_TO_A (4)
        assocClass.setLabelDirection(4);
        assertEquals("labelDirection should be FROM_B_TO_A after setting", 4, assocClass.getLabelDirection());

        // Verify it's propagated to the inner Association
        assertEquals("Inner association should also have labelDirection=4",
                4, assocClass.getAssociation().getLabelDirection());
    }

    @Test
    public void testChangeLabelDirection() {
        // Default is FROM_A_TO_B (3)
        assertEquals(3, assocClass.getLabelDirection());

        // Change to FROM_B_TO_A (4)
        assocClass.changeLabelDirection();
        assertEquals(4, assocClass.getLabelDirection());

        // Change back to FROM_A_TO_B (3)
        assocClass.changeLabelDirection();
        assertEquals(3, assocClass.getLabelDirection());
    }

    @Test
    public void testAssociationElementHasSameAssociation() {
        // The AssociationGR inside AssociationClassGR should reference the same Association object
        assertSame("AssociationGR should reference the same inner Association",
                assocClass.getAssociation(),
                assocClassGR.getAssociationElement().getAssociation());
    }

    @Test
    public void testShowArrowPropagationToAssociationGR() {
        // Set showArrow to true on association class
        assocClass.setShowArrow(true);

        // The AssociationGR inside should see the same value
        assertTrue("AssociationGR should see showArrow=true",
                assocClassGR.getAssociationElement().getAssociation().getShowArrow());
    }
}
