package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Tests for AddElementControllerFactory generalization validation logic.
 * Specifically tests the constraint: "Interfaces and Classes cannot participate
 * in Generalizations with each other!" These tests use the ACTUAL
 * factory-created controller to verify the production code.
 */
public class AddElementControllerFactoryTest {

    UMLProject umlProject;
    DCDModel model;
    DCDInternalFrame internalFrame;
    Helper h;

    // Testable factory that captures error messages instead of showing dialogs
    private static class TestableAddElementControllerFactory extends AddElementControllerFactory {
        private String lastErrorMessage;

        @Override
        protected void showErrorMessage(DiagramInternalFrame parentFrame, String msg) {
            // Capture message instead of showing dialog
            this.lastErrorMessage = msg;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }

        public void clearLastErrorMessage() {
            this.lastErrorMessage = null;
        }
    }

    private TestableAddElementControllerFactory testableFactory;
    private AddLinkController generalizationController;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new DCDModel("dcd", umlProject);
        internalFrame = new DCDInternalFrame(model, true);
        h = new Helper(model);

        testableFactory = new TestableAddElementControllerFactory();
        // Get the actual controller from the factory - this is what we're testing!
        AddElementController controller = testableFactory.newAddElementController(model, internalFrame,
                "GeneralizationGR");
        generalizationController = (AddLinkController) controller;
    }

    @Test
    public void testCreation() {
        assertNotNull("Generalization controller should be created from factory", generalizationController);
    }

    @Test
    public void testClassToClassGeneralization_ShouldSucceed() {
        ClassGR classA = h.addClass("ClassA");
        ClassGR classB = h.addClass("ClassB");

        testableFactory.clearLastErrorMessage();

        // Use the actual factory-created controller to create the relationship
        LinkGR result = generalizationController.createRelationship(classB, classA);

        assertNotNull("Generalization between two classes should be created", result);
        assertNull("No error message should be shown for valid generalization", testableFactory.getLastErrorMessage());
        assertEquals("Result should be a GeneralizationGR", GeneralizationGR.class, result.getClass());
    }

    @Test
    public void testInterfaceToInterfaceGeneralization_ShouldSucceed() {
        InterfaceGR interfaceA = h.addInterface("InterfaceA");
        InterfaceGR interfaceB = h.addInterface("InterfaceB");

        testableFactory.clearLastErrorMessage();

        // Use the actual factory-created controller to create the relationship
        LinkGR result = generalizationController.createRelationship(interfaceB, interfaceA);

        assertNotNull("Generalization between two interfaces should be created", result);
        assertNull("No error message should be shown for valid generalization", testableFactory.getLastErrorMessage());
        assertEquals("Result should be a GeneralizationGR", GeneralizationGR.class, result.getClass());
    }

    @Test
    public void testClassToInterfaceGeneralization_ShouldBePreventedByFactory() {
        ClassGR classA = h.addClass("ClassA");
        InterfaceGR interfaceB = h.addInterface("InterfaceB");

        int initialElementCount = model.getGraphicalElements().size();

        testableFactory.clearLastErrorMessage();

        // Use the actual factory-created controller to attempt creating the invalid relationship
        LinkGR result = generalizationController.createRelationship(classA, interfaceB);

        // Verify generalization was NOT created
        assertNull("Generalization from class to interface should not be created", result);

        // Verify error message was captured from the production code
        assertNotNull("Error message should be captured", testableFactory.getLastErrorMessage());
        assertEquals("Error message should mention interfaces and classes",
                "Interfaces and Classes cannot participate in Generalizations with each other!",
                testableFactory.getLastErrorMessage());

        // Verify model state unchanged
        assertEquals("Model should have same number of elements",
                initialElementCount, model.getGraphicalElements().size());

        long genCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR)
                .count();
        assertEquals("Should have 0 generalizations", 0, genCount);
    }

    @Test
    public void testInterfaceToClassGeneralization_ShouldBePreventedByFactory() {
        InterfaceGR interfaceA = h.addInterface("InterfaceA");
        ClassGR classB = h.addClass("ClassB");

        int initialElementCount = model.getGraphicalElements().size();

        testableFactory.clearLastErrorMessage();

        // Use the actual factory-created controller to attempt creating the invalid relationship
        LinkGR result = generalizationController.createRelationship(interfaceA, classB);

        // Verify generalization was NOT created
        assertNull("Generalization from interface to class should not be created", result);

        // Verify error message was captured from the production code
        assertNotNull("Error message should be captured", testableFactory.getLastErrorMessage());
        assertEquals("Error message should mention interfaces and classes",
                "Interfaces and Classes cannot participate in Generalizations with each other!",
                testableFactory.getLastErrorMessage());

        // Verify model state unchanged
        assertEquals("Model should have same number of elements",
                initialElementCount, model.getGraphicalElements().size());

        long genCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR)
                .count();
        assertEquals("Should have 0 generalizations", 0, genCount);
    }

    @Test
    public void testSelfGeneralization_ShouldBePreventedByFactory() {
        ClassGR classA = h.addClass("ClassA");

        int initialElementCount = model.getGraphicalElements().size();

        testableFactory.clearLastErrorMessage();

        // Use the actual factory-created controller to test self-generalization prevention
        LinkGR result = generalizationController.createRelationship(classA, classA);

        // Verify generalization was NOT created
        assertNull("Self-generalization should not be created", result);

        // Verify model state unchanged
        assertEquals("Model should have same number of elements",
                initialElementCount, model.getGraphicalElements().size());

        long genCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR)
                .count();
        assertEquals("Should have 0 generalizations", 0, genCount);
    }

    @Test
    public void testDuplicateGeneralization_ShouldBePreventedByFactory() {
        ClassGR classA = h.addClass("ClassA");
        ClassGR classB = h.addClass("ClassB");

        // Add first generalization using Helper which adds to both model and repository
        h.addGeneralization(classB, classA);

        testableFactory.clearLastErrorMessage();

        // Attempt to create duplicate generalization using the factory-created controller
        LinkGR result = generalizationController.createRelationship(classB, classA);

        // Verify duplicate was NOT created
        assertNull("Duplicate generalization should not be created", result);

        // Verify error message about existing link was captured from production code
        assertNotNull("Error message should be captured", testableFactory.getLastErrorMessage());
        assertEquals("Error message should mention existing link",
                "The link between these two classifiers already exists!",
                testableFactory.getLastErrorMessage());

        // Verify only one generalization exists
        long genCount = model.getGraphicalElements().stream()
                .filter(e -> e instanceof GeneralizationGR)
                .count();
        assertEquals("Should still have only 1 generalization", 1, genCount);
    }
}
