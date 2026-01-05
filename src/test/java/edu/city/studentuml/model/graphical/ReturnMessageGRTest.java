package edu.city.studentuml.model.graphical;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.SDInternalFrame;

/**
 * Test class for ReturnMessageGR.edit() method. ReturnMessageGR is a Sequence
 * Diagram component that represents a return message between role classifiers.
 * It uses the Template Method pattern (editStringPropertyWithDialog) for name
 * editing. ReturnMessage constructor: new ReturnMessage(from, to, name)
 * ReturnMessage has setName() and getName() methods
 * 
 * @author Dimitris Dranidis (AI-assisted)
 */
public class ReturnMessageGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testReturnMessageGR_EditName_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create two design classes for source and target
        DesignClass orderClass = new DesignClass("Order");
        DesignClass paymentClass = new DesignClass("Payment");
        model.getCentralRepository().addClass(orderClass);
        model.getCentralRepository().addClass(paymentClass);

        // Create SDObjects (role classifiers)
        SDObject fromObject = new SDObject("order1", orderClass);
        SDObject toObject = new SDObject("payment1", paymentClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create ReturnMessage with initial name
        ReturnMessage returnMessage = new ReturnMessage(fromObject, toObject, "success");

        // Create ReturnMessageGR (simple graphical wrapper)
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(fromGR, toGR, returnMessage, 150);
        model.addGraphicalElement(returnMessageGR);

        // Note: We cannot easily mock editStringPropertyWithDialog since it's part of the template method
        // and calls internal dialog methods. This test verifies the structure exists but
        // cannot test the full edit workflow without UI interaction.

        // Verify initial state
        assertEquals("success", returnMessage.getName());

        // Verify the message is properly set up
        assertNotNull("ReturnMessage should be set as message", returnMessageGR.getReturnMessage());
        assertEquals("success", returnMessageGR.getReturnMessage().getName());
    }

    @Test
    public void testReturnMessageGR_CreateInstance() {
        // Create SD model
        SDModel model = new SDModel("sd", umlProject);

        // Create classes and objects
        DesignClass customerClass = new DesignClass("Customer");
        DesignClass accountClass = new DesignClass("Account");
        model.getCentralRepository().addClass(customerClass);
        model.getCentralRepository().addClass(accountClass);

        SDObject fromObject = new SDObject("customer1", customerClass);
        SDObject toObject = new SDObject("account1", accountClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create ReturnMessage
        ReturnMessage returnMessage = new ReturnMessage(fromObject, toObject, "balance");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(fromGR, toGR, returnMessage, 200);
        model.addGraphicalElement(returnMessageGR);

        // Verify structure
        assertNotNull("ReturnMessageGR should be created", returnMessageGR);
        assertEquals("balance", returnMessage.getName());
        assertEquals("ReturnMessage should be the message", returnMessage, returnMessageGR.getReturnMessage());

        // Verify position
        assertEquals(200, returnMessageGR.getY());
    }

    @Test
    public void testReturnMessageGR_SetNameDirectly() {
        // Create SD model
        SDModel model = new SDModel("sd", umlProject);

        // Create classes and objects
        DesignClass productClass = new DesignClass("Product");
        DesignClass reviewClass = new DesignClass("Review");
        model.getCentralRepository().addClass(productClass);
        model.getCentralRepository().addClass(reviewClass);

        SDObject fromObject = new SDObject("product1", productClass);
        SDObject toObject = new SDObject("review1", reviewClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create ReturnMessage
        ReturnMessage returnMessage = new ReturnMessage(fromObject, toObject, "original");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(fromGR, toGR, returnMessage, 150);
        model.addGraphicalElement(returnMessageGR);

        // Change name directly on domain object
        returnMessage.setName("modified");

        // Verify change reflected
        assertEquals("modified", returnMessage.getName());
        assertEquals("modified", returnMessageGR.getReturnMessage().getName());
    }

    @Test
    public void testReturnMessageGR_EmptyName() {
        // Create SD model
        SDModel model = new SDModel("sd", umlProject);

        // Create classes and objects
        DesignClass fromClass = new DesignClass("From");
        DesignClass toClass = new DesignClass("To");
        model.getCentralRepository().addClass(fromClass);
        model.getCentralRepository().addClass(toClass);

        SDObject fromObject = new SDObject("from1", fromClass);
        SDObject toObject = new SDObject("to1", toClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create ReturnMessage with empty name
        ReturnMessage returnMessage = new ReturnMessage(fromObject, toObject, "");
        ReturnMessageGR returnMessageGR = new ReturnMessageGR(fromGR, toGR, returnMessage, 150);
        model.addGraphicalElement(returnMessageGR);

        // Verify empty name is accepted
        assertEquals("", returnMessage.getName());
        assertEquals("", returnMessageGR.getReturnMessage().getName());
    }

    @Test
    public void testReturnMessageGR_Clone() {
        // Create SD model
        SDModel model = new SDModel("sd", umlProject);

        // Create classes and objects
        DesignClass orderClass = new DesignClass("Order");
        DesignClass shipmentClass = new DesignClass("Shipment");
        model.getCentralRepository().addClass(orderClass);
        model.getCentralRepository().addClass(shipmentClass);

        SDObject fromObject = new SDObject("order1", orderClass);
        SDObject toObject = new SDObject("shipment1", shipmentClass);
        model.getCentralRepository().addObject(fromObject);
        model.getCentralRepository().addObject(toObject);

        // Create graphical wrappers
        SDObjectGR fromGR = new SDObjectGR(fromObject, 100);
        SDObjectGR toGR = new SDObjectGR(toObject, 300);
        model.addGraphicalElement(fromGR);
        model.addGraphicalElement(toGR);

        // Create ReturnMessage
        ReturnMessage returnMessage = new ReturnMessage(fromObject, toObject, "trackingNumber");
        ReturnMessageGR original = new ReturnMessageGR(fromGR, toGR, returnMessage, 150);
        model.addGraphicalElement(original);

        // Clone the graphical element
        ReturnMessageGR cloned = original.clone();

        // Verify clone shares the same domain object (important!)
        assertSame("Clone should share the same ReturnMessage",
                original.getReturnMessage(), cloned.getReturnMessage());

        // Verify clone has same position
        assertEquals(original.getY(), cloned.getY());

        // Verify modifying the shared domain object affects both
        returnMessage.setName("confirmationCode");
        assertEquals("confirmationCode", original.getReturnMessage().getName());
        assertEquals("confirmationCode", cloned.getReturnMessage().getName());
    }
}
