package edu.city.studentuml.integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.graphical.*;
import edu.city.studentuml.util.NotStreamable;

/**
 * Phase 0.6: Activity Diagram Save/Load Integration Test Tests
 * serialization/deserialization of Activity Diagrams with: - All node types:
 * Initial, Action, Object Node, Decision, Merge, Fork, Join, Final - Control
 * flows with guards (guarded transitions) - Object flows with weights and
 * guards - Note attached to action node Scenario: Order Processing workflow
 * with 15 nodes, 16 flows (10 control + 4 object), 1 note Expected: All AD
 * elements correctly saved to and restored from XML with: - All nodes present
 * with correct types and positions (including object nodes) - All control flows
 * and object flows with correct source/target, guards, and weights - Note
 * properly attached to action node - Domain model objects properly shared in
 * central repository
 * 
 * @author Copilot
 */
public class ADSaveLoadTest extends SaveLoadTestBase {

    @Test
    public void testActivityDiagramSaveLoad() throws IOException, NotStreamable {
        // ===== Phase 1: Create model with comprehensive Activity Diagram =====
        // Note: ADModel constructor automatically adds itself to the project
        ADModel model = new ADModel("Order Processing AD", project);

        // Create Initial Node (starting point)
        InitialNode initialNode = new InitialNode();
        InitialNodeGR initialNodeGR = new InitialNodeGR(initialNode, 250, 50);
        model.addGraphicalElement(initialNodeGR);

        // Create Action Nodes (rectangular rounded boxes with action names)
        ActionNode receiveOrder = new ActionNode("Receive Order");
        ActionNodeGR receiveOrderGR = new ActionNodeGR(receiveOrder, 200, 150);
        model.addGraphicalElement(receiveOrderGR);

        // Object Node 1: Order data passed between actions
        ObjectNode orderData = new ObjectNode();
        orderData.setName("Order");
        ObjectNodeGR orderDataGR = new ObjectNodeGR(orderData, 250, 230);
        model.addGraphicalElement(orderDataGR);

        ActionNode validateOrder = new ActionNode("Validate Order");
        ActionNodeGR validateOrderGR = new ActionNodeGR(validateOrder, 250, 310);
        model.addGraphicalElement(validateOrderGR);

        ActionNode processPayment = new ActionNode("Process Payment");
        ActionNodeGR processPaymentGR = new ActionNodeGR(processPayment, 100, 480);
        model.addGraphicalElement(processPaymentGR);

        // Object Node 2: Payment receipt passed to next action
        ObjectNode paymentReceipt = new ObjectNode();
        paymentReceipt.setName("Receipt");
        ObjectNodeGR paymentReceiptGR = new ObjectNodeGR(paymentReceipt, 150, 570);
        model.addGraphicalElement(paymentReceiptGR);

        ActionNode shipOrder = new ActionNode("Ship Order");
        ActionNodeGR shipOrderGR = new ActionNodeGR(shipOrder, 250, 730);
        model.addGraphicalElement(shipOrderGR);

        ActionNode notifyCustomer = new ActionNode("Notify Customer");
        ActionNodeGR notifyCustomerGR = new ActionNodeGR(notifyCustomer, 120, 910);
        model.addGraphicalElement(notifyCustomerGR);

        ActionNode updateInventory = new ActionNode("Update Inventory");
        ActionNodeGR updateInventoryGR = new ActionNodeGR(updateInventory, 380, 910);
        model.addGraphicalElement(updateInventoryGR);

        ActionNode handleRejection = new ActionNode("Handle Rejection");
        ActionNodeGR handleRejectionGR = new ActionNodeGR(handleRejection, 400, 480);
        model.addGraphicalElement(handleRejectionGR);

        // Create Decision Node (diamond for branching)
        DecisionNode orderValidDecision = new DecisionNode();
        DecisionNodeGR orderValidDecisionGR = new DecisionNodeGR(orderValidDecision, 250, 390);
        model.addGraphicalElement(orderValidDecisionGR);

        // Create Merge Node (diamond for merging branches)
        MergeNode mergeNode = new MergeNode();
        MergeNodeGR mergeNodeGR = new MergeNodeGR(mergeNode, 250, 650);
        model.addGraphicalElement(mergeNodeGR);

        // Create Fork Node (thick bar for splitting into parallel flows)
        ForkNode forkNode = new ForkNode();
        ForkNodeGR forkNodeGR = new ForkNodeGR(forkNode, 250, 820);
        model.addGraphicalElement(forkNodeGR);

        // Create Join Node (thick bar for synchronizing parallel flows)
        JoinNode joinNode = new JoinNode();
        JoinNodeGR joinNodeGR = new JoinNodeGR(joinNode, 250, 1000);
        model.addGraphicalElement(joinNodeGR);

        // Create Activity Final Node (circle with filled inner circle)
        ActivityFinalNode finalNode = new ActivityFinalNode();
        ActivityFinalNodeGR finalNodeGR = new ActivityFinalNodeGR(finalNode, 250, 1080);
        model.addGraphicalElement(finalNodeGR);

        // Create Control Flows (directed edges connecting nodes)
        // NOTE: ControlFlowGR constructor is (source, target, flow, srcPoint, trgPoint)
        // We need to provide Points for proper edge rendering

        // Flow 1: Initial -> Receive Order
        ControlFlow cf1 = new ControlFlow(initialNode, receiveOrder);
        ControlFlowGR cf1GR = new ControlFlowGR(initialNodeGR, receiveOrderGR, cf1,
                new Point(250, 50), new Point(200, 150));
        model.addGraphicalElement(cf1GR);

        // Flow 2: Receive Order -> Order (Object Flow)
        ObjectFlow of1 = new ObjectFlow(receiveOrder, orderData);
        of1.setWeight("1");
        ObjectFlowGR of1GR = new ObjectFlowGR(receiveOrderGR, orderDataGR, of1,
                new Point(200, 150), new Point(250, 230));
        model.addGraphicalElement(of1GR);

        // Flow 3: Order -> Validate Order (Object Flow)
        ObjectFlow of2 = new ObjectFlow(orderData, validateOrder);
        ObjectFlowGR of2GR = new ObjectFlowGR(orderDataGR, validateOrderGR, of2,
                new Point(250, 230), new Point(250, 310));
        model.addGraphicalElement(of2GR);

        // Flow 4: Validate Order -> Decision
        ControlFlow cf3 = new ControlFlow(validateOrder, orderValidDecision);
        ControlFlowGR cf3GR = new ControlFlowGR(validateOrderGR, orderValidDecisionGR, cf3,
                new Point(250, 310), new Point(250, 390));
        model.addGraphicalElement(cf3GR);

        // Flow 5: Decision -> Process Payment [valid]
        ControlFlow cf4 = new ControlFlow(orderValidDecision, processPayment);
        cf4.setGuard("valid");
        ControlFlowGR cf4GR = new ControlFlowGR(orderValidDecisionGR, processPaymentGR, cf4,
                new Point(250, 390), new Point(100, 480));
        model.addGraphicalElement(cf4GR);

        // Flow 6: Decision -> Handle Rejection [invalid]
        ControlFlow cf5 = new ControlFlow(orderValidDecision, handleRejection);
        cf5.setGuard("invalid");
        ControlFlowGR cf5GR = new ControlFlowGR(orderValidDecisionGR, handleRejectionGR, cf5,
                new Point(250, 390), new Point(400, 480));
        model.addGraphicalElement(cf5GR);

        // Flow 7: Process Payment -> Receipt (Object Flow)
        ObjectFlow of3 = new ObjectFlow(processPayment, paymentReceipt);
        of3.setWeight("1");
        of3.setGuard("payment successful");
        ObjectFlowGR of3GR = new ObjectFlowGR(processPaymentGR, paymentReceiptGR, of3,
                new Point(100, 480), new Point(150, 570));
        model.addGraphicalElement(of3GR);

        // Flow 8: Receipt -> Merge (Object Flow)
        ObjectFlow of4 = new ObjectFlow(paymentReceipt, mergeNode);
        ObjectFlowGR of4GR = new ObjectFlowGR(paymentReceiptGR, mergeNodeGR, of4,
                new Point(150, 570), new Point(250, 650));
        model.addGraphicalElement(of4GR);

        // Flow 9: Merge -> Ship Order
        ControlFlow cf7 = new ControlFlow(mergeNode, shipOrder);
        ControlFlowGR cf7GR = new ControlFlowGR(mergeNodeGR, shipOrderGR, cf7,
                new Point(250, 650), new Point(250, 730));
        model.addGraphicalElement(cf7GR);

        // Flow 10: Ship Order -> Fork
        ControlFlow cf8 = new ControlFlow(shipOrder, forkNode);
        ControlFlowGR cf8GR = new ControlFlowGR(shipOrderGR, forkNodeGR, cf8,
                new Point(250, 730), new Point(250, 820));
        model.addGraphicalElement(cf8GR);

        // Flow 11: Fork -> Notify Customer (parallel branch 1)
        ControlFlow cf9 = new ControlFlow(forkNode, notifyCustomer);
        ControlFlowGR cf9GR = new ControlFlowGR(forkNodeGR, notifyCustomerGR, cf9,
                new Point(250, 820), new Point(120, 910));
        model.addGraphicalElement(cf9GR);

        // Flow 12: Fork -> Update Inventory (parallel branch 2)
        ControlFlow cf10 = new ControlFlow(forkNode, updateInventory);
        ControlFlowGR cf10GR = new ControlFlowGR(forkNodeGR, updateInventoryGR, cf10,
                new Point(250, 820), new Point(380, 910));
        model.addGraphicalElement(cf10GR);

        // Flow 13: Notify Customer -> Join
        ControlFlow cf11 = new ControlFlow(notifyCustomer, joinNode);
        ControlFlowGR cf11GR = new ControlFlowGR(notifyCustomerGR, joinNodeGR, cf11,
                new Point(120, 910), new Point(250, 1000));
        model.addGraphicalElement(cf11GR);

        // Flow 14: Update Inventory -> Join
        ControlFlow cf12 = new ControlFlow(updateInventory, joinNode);
        ControlFlowGR cf12GR = new ControlFlowGR(updateInventoryGR, joinNodeGR, cf12,
                new Point(380, 910), new Point(250, 1000));
        model.addGraphicalElement(cf12GR);

        // Flow 15: Join -> Final
        ControlFlow cf13 = new ControlFlow(joinNode, finalNode);
        ControlFlowGR cf13GR = new ControlFlowGR(joinNodeGR, finalNodeGR, cf13,
                new Point(250, 1000), new Point(250, 1080));
        model.addGraphicalElement(cf13GR);

        // Flow 16: Handle Rejection -> Merge (alternative path back to main flow)
        ControlFlow cf14 = new ControlFlow(handleRejection, mergeNode);
        ControlFlowGR cf14GR = new ControlFlowGR(handleRejectionGR, mergeNodeGR, cf14,
                new Point(400, 480), new Point(250, 650));
        model.addGraphicalElement(cf14GR);

        // Add a Note attached to Process Payment action
        // Note: UMLNoteGR constructor is (text, connectedElement, position)
        Point notePosition = new Point(10, 450);
        UMLNoteGR noteGR = new UMLNoteGR(
                "Payment processing includes\nfraud detection and\nauthorization checks",
                processPaymentGR,
                notePosition);
        model.addGraphicalElement(noteGR);

        // Total: 15 nodes (1 initial + 7 actions + 2 object nodes + 1 decision + 1 merge + 1 fork + 1 join + 1 final)
        //        16 flows (12 control flows + 4 object flows, with guards on some)
        //        1 note
        // Expected graphical elements: 32

        // ===== Verify Phase 1: Confirm model built correctly =====
        Vector<GraphicalElement> originalElements = model.getGraphicalElements();
        assertEquals("Should have 32 graphical elements (15 nodes + 16 flows + 1 note)",
                32, originalElements.size());

        // Count node types
        int actionCount = 0, controlFlowCount = 0, objectFlowCount = 0, noteCount = 0;
        int initialCount = 0, decisionCount = 0, mergeCount = 0;
        int forkCount = 0, joinCount = 0, finalCount = 0, objectNodeCount = 0;

        for (GraphicalElement ge : originalElements) {
            if (ge instanceof ActionNodeGR)
                actionCount++;
            else if (ge instanceof ObjectNodeGR)
                objectNodeCount++;
            else if (ge instanceof ControlFlowGR)
                controlFlowCount++;
            else if (ge instanceof ObjectFlowGR)
                objectFlowCount++;
            else if (ge instanceof UMLNoteGR)
                noteCount++;
            else if (ge instanceof InitialNodeGR)
                initialCount++;
            else if (ge instanceof DecisionNodeGR)
                decisionCount++;
            else if (ge instanceof MergeNodeGR)
                mergeCount++;
            else if (ge instanceof ForkNodeGR)
                forkCount++;
            else if (ge instanceof JoinNodeGR)
                joinCount++;
            else if (ge instanceof ActivityFinalNodeGR)
                finalCount++;
        }

        assertEquals("Should have 7 action nodes", 7, actionCount);
        assertEquals("Should have 2 object nodes", 2, objectNodeCount);
        assertEquals("Should have 12 control flows", 12, controlFlowCount);
        assertEquals("Should have 4 object flows", 4, objectFlowCount);
        assertEquals("Should have 1 note", 1, noteCount);
        assertEquals("Should have 1 initial node", 1, initialCount);
        assertEquals("Should have 1 decision node", 1, decisionCount);
        assertEquals("Should have 1 merge node", 1, mergeCount);
        assertEquals("Should have 1 fork node", 1, forkCount);
        assertEquals("Should have 1 join node", 1, joinCount);
        assertEquals("Should have 1 final node", 1, finalCount);

        // ===== Phase 2: Save to XML =====
        saveProject();

        // ===== Phase 3: Load from XML =====
        UMLProject loadedProject = loadProject();

        // ===== Phase 4: Verify loaded model structure =====
        assertNotNull("Loaded project should not be null", loadedProject);
        assertEquals("Should have 1 diagram", 1, loadedProject.getDiagramModels().size());
        ADModel loadedModel = (ADModel) loadedProject.getDiagramModels().get(0);
        assertNotNull("Loaded model should not be null", loadedModel);
        assertEquals("Diagram name should match", "Order Processing AD", loadedModel.getName());

        List<GraphicalElement> loadedElements = loadedModel.getGraphicalElements();
        assertEquals("Loaded model should have same 32 elements",
                32, loadedElements.size());

        // Count node types in loaded model
        actionCount = 0;
        controlFlowCount = 0;
        objectFlowCount = 0;
        noteCount = 0;
        initialCount = 0;
        decisionCount = 0;
        mergeCount = 0;
        forkCount = 0;
        joinCount = 0;
        finalCount = 0;
        objectNodeCount = 0;

        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof ActionNodeGR)
                actionCount++;
            else if (ge instanceof ObjectNodeGR)
                objectNodeCount++;
            else if (ge instanceof ControlFlowGR)
                controlFlowCount++;
            else if (ge instanceof ObjectFlowGR)
                objectFlowCount++;
            else if (ge instanceof UMLNoteGR)
                noteCount++;
            else if (ge instanceof InitialNodeGR)
                initialCount++;
            else if (ge instanceof DecisionNodeGR)
                decisionCount++;
            else if (ge instanceof MergeNodeGR)
                mergeCount++;
            else if (ge instanceof ForkNodeGR)
                forkCount++;
            else if (ge instanceof JoinNodeGR)
                joinCount++;
            else if (ge instanceof ActivityFinalNodeGR)
                finalCount++;
        }

        assertEquals("Loaded: Should have 7 action nodes", 7, actionCount);
        assertEquals("Loaded: Should have 2 object nodes", 2, objectNodeCount);
        assertEquals("Loaded: Should have 12 control flows", 12, controlFlowCount);
        assertEquals("Loaded: Should have 4 object flows", 4, objectFlowCount);
        assertEquals("Loaded: Should have 1 note", 1, noteCount);
        assertEquals("Loaded: Should have 1 initial node", 1, initialCount);
        assertEquals("Loaded: Should have 1 decision node", 1, decisionCount);
        assertEquals("Loaded: Should have 1 merge node", 1, mergeCount);
        assertEquals("Loaded: Should have 1 fork node", 1, forkCount);
        assertEquals("Loaded: Should have 1 join node", 1, joinCount);
        assertEquals("Loaded: Should have 1 final node", 1, finalCount);

        // ===== Phase 5: Verify specific action nodes and their names =====
        ActionNodeGR loadedReceiveOrderGR = null;
        ActionNodeGR loadedValidateOrderGR = null;
        ActionNodeGR loadedProcessPaymentGR = null;
        ActionNodeGR loadedHandleRejectionGR = null;

        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof ActionNodeGR) {
                ActionNodeGR agr = (ActionNodeGR) ge;
                ActionNode action = (ActionNode) agr.getComponent();
                String name = action.getName();

                if ("Receive Order".equals(name))
                    loadedReceiveOrderGR = agr;
                else if ("Validate Order".equals(name))
                    loadedValidateOrderGR = agr;
                else if ("Process Payment".equals(name))
                    loadedProcessPaymentGR = agr;
                else if ("Handle Rejection".equals(name))
                    loadedHandleRejectionGR = agr;
            }
        }

        assertNotNull("Should find Receive Order action", loadedReceiveOrderGR);
        assertNotNull("Should find Validate Order action", loadedValidateOrderGR);
        assertNotNull("Should find Process Payment action", loadedProcessPaymentGR);
        assertNotNull("Should find Handle Rejection action", loadedHandleRejectionGR);

        // Verify action node positions are preserved
        assertEquals("Receive Order X position", 200, loadedReceiveOrderGR.getX());
        assertEquals("Receive Order Y position", 150, loadedReceiveOrderGR.getY());
        assertEquals("Validate Order X position", 250, loadedValidateOrderGR.getX());
        assertEquals("Validate Order Y position", 310, loadedValidateOrderGR.getY());
        assertEquals("Process Payment X position", 100, loadedProcessPaymentGR.getX());
        assertEquals("Process Payment Y position", 480, loadedProcessPaymentGR.getY());
        assertEquals("Handle Rejection X position", 400, loadedHandleRejectionGR.getX());
        assertEquals("Handle Rejection Y position", 480, loadedHandleRejectionGR.getY());

        // ===== Phase 6: Verify control flows with guards =====
        ControlFlowGR validGuardFlow = null;
        ControlFlowGR invalidGuardFlow = null;

        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof ControlFlowGR) {
                ControlFlowGR cfGR = (ControlFlowGR) ge;
                ControlFlow cf = (ControlFlow) cfGR.getEdge();
                String guard = cf.getGuard();

                if ("valid".equals(guard)) {
                    validGuardFlow = cfGR;
                } else if ("invalid".equals(guard)) {
                    invalidGuardFlow = cfGR;
                }
            }
        }

        assertNotNull("Should find control flow with [valid] guard", validGuardFlow);
        assertNotNull("Should find control flow with [invalid] guard", invalidGuardFlow);

        // Verify the guard flows connect to correct targets
        ControlFlow validCF = (ControlFlow) validGuardFlow.getEdge();
        ControlFlow invalidCF = (ControlFlow) invalidGuardFlow.getEdge();

        assertEquals("Valid guard should lead to Process Payment",
                "Process Payment",
                ((ActionNode) validCF.getTarget()).getName());
        assertEquals("Invalid guard should lead to Handle Rejection",
                "Handle Rejection",
                ((ActionNode) invalidCF.getTarget()).getName());

        // ===== Phase 6b: Verify object nodes and object flows =====
        ObjectNodeGR loadedOrderDataGR = null;
        ObjectNodeGR loadedPaymentReceiptGR = null;

        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof ObjectNodeGR) {
                ObjectNodeGR onGR = (ObjectNodeGR) ge;
                ObjectNode on = (ObjectNode) onGR.getComponent();
                String name = on.getName();

                if ("Order".equals(name))
                    loadedOrderDataGR = onGR;
                else if ("Receipt".equals(name))
                    loadedPaymentReceiptGR = onGR;
            }
        }

        assertNotNull("Should find Order object node", loadedOrderDataGR);
        assertNotNull("Should find Receipt object node", loadedPaymentReceiptGR);

        // Verify object node positions
        assertEquals("Order object node X position", 250, loadedOrderDataGR.getX());
        assertEquals("Order object node Y position", 230, loadedOrderDataGR.getY());
        assertEquals("Receipt object node X position", 150, loadedPaymentReceiptGR.getX());
        assertEquals("Receipt object node Y position", 570, loadedPaymentReceiptGR.getY());

        // Verify object flows with weight and guard
        ObjectFlowGR paymentReceiptFlow = null;
        int objectFlowsWithWeight = 0;

        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof ObjectFlowGR) {
                ObjectFlowGR ofGR = (ObjectFlowGR) ge;
                ObjectFlow of = (ObjectFlow) ofGR.getEdge();

                if (of.getWeight() != null && !of.getWeight().isEmpty()) {
                    objectFlowsWithWeight++;
                }

                if ("payment successful".equals(of.getGuard())) {
                    paymentReceiptFlow = ofGR;
                }
            }
        }

        assertEquals("Should have 2 object flows with weight", 2, objectFlowsWithWeight);
        assertNotNull("Should find object flow with 'payment successful' guard", paymentReceiptFlow);

        ObjectFlow paymentReceiptOF = (ObjectFlow) paymentReceiptFlow.getEdge();
        assertEquals("Payment receipt flow should have weight '1'", "1", paymentReceiptOF.getWeight());
        assertEquals("Payment receipt flow should have guard 'payment successful'",
                "payment successful", paymentReceiptOF.getGuard());

        // ===== Phase 7: Verify note attachment =====
        UMLNoteGR loadedNoteGR = null;
        for (GraphicalElement ge : loadedElements) {
            if (ge instanceof UMLNoteGR) {
                loadedNoteGR = (UMLNoteGR) ge;
                break;
            }
        }

        assertNotNull("Should find the note", loadedNoteGR);
        assertTrue("Note should contain fraud detection text",
                loadedNoteGR.getText().contains("fraud detection"));
        assertEquals("Note should be connected to Process Payment",
                loadedProcessPaymentGR, loadedNoteGR.getTo());

        // ===== SUCCESS =====
        // java.lang.System.out.println("✅ Phase 0.6: AD Save/Load Test - All verifications passed!");
        // java.lang.System.out.println("   ✓ All 15 node types correctly saved and loaded (including 2 object nodes)");
        // java.lang.System.out.println("   ✓ All 16 flows (12 control + 4 object) with guards/weights correctly saved and loaded");
        // java.lang.System.out.println("   ✓ Note attachment correctly saved and loaded");
    }
}
