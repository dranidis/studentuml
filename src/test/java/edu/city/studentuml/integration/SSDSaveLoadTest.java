package edu.city.studentuml.integration;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.graphical.*;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Integration test for System Sequence Diagram (SSD) save/load functionality.
 * Phase 0.5: Tests that SSDs with actors interacting with a single System
 * object are correctly persisted to XML and restored. SSD Pattern:
 * ActorInstance(s) → SystemInstance (single system object)
 * 
 * @author Copilot
 */
public class SSDSaveLoadTest extends SaveLoadTestBase {

    @Test
    public void testSSDSaveLoad() throws Exception {
        // ============================================================
        //  CREATE - Build SSD with actors and system
        // ============================================================
        SSDModel model = new SSDModel("Library System SSD", project);

        // Create Actor (domain object) - using only one actor as requested
        Actor customerActor = new Actor("Customer");

        // Create System (domain object) - fully qualified to avoid conflict with java.lang.System
        edu.city.studentuml.model.domain.System librarySystem = new edu.city.studentuml.model.domain.System(
                "LibrarySystem");

        // Create Actor Instance (external entity interacting with the system)
        ActorInstance customer = new ActorInstance("customer", customerActor);
        ActorInstanceGR customerGR = new ActorInstanceGR(customer, 100);
        model.addRoleClassifier(customerGR);

        // Create System Instance (the single system instance that receives messages)
        SystemInstance system = new SystemInstance("system", librarySystem);
        SystemInstanceGR systemGR = new SystemInstanceGR(system, 500);
        model.addRoleClassifier(systemGR);

        // Create Messages from actor to system
        // Message 1: Customer searches for a book
        GenericOperation searchBookOp = new GenericOperation("searchBook");
        CallMessage searchBook = new CallMessage(customer, system, searchBookOp);
        searchBook.addParameter(new MethodParameter("title"));
        CallMessageGR searchBookGR = new CallMessageGR(customerGR, systemGR, searchBook, 100);
        model.addMessage(searchBookGR);

        ReturnMessage searchBookReturn = new ReturnMessage(system, customer, "bookList");
        ReturnMessageGR searchBookReturnGR = new ReturnMessageGR(systemGR, customerGR, searchBookReturn, 130);
        model.addMessage(searchBookReturnGR);

        // Message 2: Customer borrows a book
        GenericOperation borrowBookOp = new GenericOperation("borrowBook");
        CallMessage borrowBook = new CallMessage(customer, system, borrowBookOp);
        borrowBook.addParameter(new MethodParameter("bookId"));
        CallMessageGR borrowBookGR = new CallMessageGR(customerGR, systemGR, borrowBook, 160);
        model.addMessage(borrowBookGR);

        ReturnMessage borrowBookReturn = new ReturnMessage(system, customer, "confirmationCode");
        ReturnMessageGR borrowBookReturnGR = new ReturnMessageGR(systemGR, customerGR, borrowBookReturn, 190);
        model.addMessage(borrowBookReturnGR);

        // Create Note
        UMLNoteGR noteGR = new UMLNoteGR("System operations for library management", systemGR,
                new java.awt.Point(650, 250));
        model.addGraphicalElement(noteGR);

        // ============================================================
        //  SAVE - Persist diagram to XML
        // ============================================================
        saveProject();

        // ============================================================
        //  LOAD - Restore from XML
        // ============================================================
        loadProject();

        // ============================================================
        //  VERIFY - Check all elements were restored correctly
        // ============================================================

        // Verify diagram exists
        SSDModel loadedModel = (SSDModel) project.getDiagramModels().get(0);
        assertNotNull("SSD model should be loaded", loadedModel);
        assertEquals("Diagram title should match", "Library System SSD", loadedModel.getName());

        // Verify graphical elements count (2 role classifiers + 4 messages + 1 note = 7)
        assertEquals("Should have 7 graphical elements", 7, loadedModel.getGraphicalElements().size());

        // Verify Role Classifiers (1 actor + 1 system)
        Vector<RoleClassifierGR> roleClassifiers = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof RoleClassifierGR) {
                roleClassifiers.add((RoleClassifierGR) ge);
            }
        }
        assertEquals("Should have 2 role classifiers", 2, roleClassifiers.size());

        // Check actor and system
        ActorInstanceGR loadedCustomerGR = null;
        SystemInstanceGR loadedSystemGR = null;

        for (RoleClassifierGR rcGR : roleClassifiers) {
            if (rcGR instanceof ActorInstanceGR) {
                ActorInstance ai = (ActorInstance) rcGR.getRoleClassifier();
                if (ai.getName().equals("customer")) {
                    loadedCustomerGR = (ActorInstanceGR) rcGR;
                    assertEquals("Customer actor name", "Customer", ai.getActor().getName());
                }
            } else if (rcGR instanceof SystemInstanceGR) {
                SystemInstance si = (SystemInstance) rcGR.getRoleClassifier();
                loadedSystemGR = (SystemInstanceGR) rcGR;
                assertEquals("System instance name", "system", si.getName());
                assertEquals("System name", "LibrarySystem", si.getSystem().getName());
            }
        }

        assertNotNull("Customer actor should be loaded", loadedCustomerGR);
        assertNotNull("System instance should be loaded", loadedSystemGR);

        // Verify Messages (2 call + 2 return = 4)
        Vector<SDMessageGR> messages = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                messages.add((SDMessageGR) ge);
            }
        }
        assertEquals("Should have 4 messages", 4, messages.size());

        // Verify specific messages
        int callMessageCount = 0;
        int returnMessageCount = 0;

        for (SDMessageGR msgGR : messages) {
            SDMessage msg = msgGR.getMessage();
            if (msg instanceof CallMessage) {
                callMessageCount++;
                CallMessage cm = (CallMessage) msg;

                if (cm.getName().equals("searchBook")) {
                    assertEquals("searchBook should have 1 parameter", 1, cm.getParameters().size());
                    assertEquals("Parameter name", "title", cm.getParameters().get(0).getName());
                } else if (cm.getName().equals("borrowBook")) {
                    assertEquals("borrowBook should have 1 parameter", 1, cm.getParameters().size());
                    assertEquals("Parameter name", "bookId", cm.getParameters().get(0).getName());
                }
            } else if (msg instanceof ReturnMessage) {
                returnMessageCount++;
            }
        }

        assertEquals("Should have 2 call messages", 2, callMessageCount);
        assertEquals("Should have 2 return messages", 2, returnMessageCount);

        // Verify Note
        UMLNoteGR loadedNote = null;
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof UMLNoteGR) {
                loadedNote = (UMLNoteGR) ge;
                break;
            }
        }
        assertNotNull("Note should be loaded", loadedNote);
        assertEquals("Note text should match", "System operations for library management",
                loadedNote.getText());

        // Note: Actors and Systems in SSD are embedded within ActorInstance/SystemInstance
        // and are not stored separately in CentralRepository (unlike UCD elements).
        // The key verification is that the diagram elements are correctly saved/loaded.

        // Verify messages are in repository
        Vector<SDMessage> sdMessages = project.getCentralRepository().getSDMessages();
        assertEquals("Repository should have 4 messages", 4, sdMessages.size());

        // Verify all call messages have correct operations
        for (SDMessage msg : sdMessages) {
            if (msg instanceof CallMessage) {
                CallMessage cm = (CallMessage) msg;
                String opName = cm.getName();
                assertTrue("Operation name should be valid",
                        opName.equals("searchBook") || opName.equals("borrowBook"));
            }
        }
    }
}
