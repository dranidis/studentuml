package edu.city.studentuml.integration;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.graphical.*;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Integration test for Sequence Diagram (SD) save/load functionality. Phase
 * 0.4: Tests SD with one actor interacting with multiple objects including
 * multiobjects, with create and destroy messages. SD Pattern: ActorInstance →
 * SDObject(s) + MultiObject(s) with various message types
 * 
 * @author Copilot
 */
public class SDSaveLoadTest extends SaveLoadTestBase {

    @Test
    public void testSDSaveLoad() throws Exception {
        // ============================================================
        // 1. CREATE - Build SD with actor, objects, and multiobjects
        // ============================================================
        SDModel model = new SDModel("Shopping Cart SD", project);

        // Create Actor (external user)
        Actor userActor = new Actor("User");

        // Create Actor Instance
        ActorInstance user = new ActorInstance("user", userActor);
        ActorInstanceGR userGR = new ActorInstanceGR(user, 50);
        model.addRoleClassifier(userGR);

        // Create Design Classes for objects
        DesignClass cartClass = new DesignClass("ShoppingCart");
        DesignClass itemClass = new DesignClass("Item");
        DesignClass orderClass = new DesignClass("Order");

        // Create SD Objects
        SDObject cart = new SDObject("cart", cartClass);
        SDObjectGR cartGR = new SDObjectGR(cart, 200);
        model.addRoleClassifier(cartGR);

        SDObject item1 = new SDObject("item1", itemClass);
        SDObjectGR item1GR = new SDObjectGR(item1, 350);
        model.addRoleClassifier(item1GR);

        // Create MultiObject for item collection
        MultiObject items = new MultiObject("items", itemClass);
        MultiObjectGR itemsGR = new MultiObjectGR(items, 500);
        model.addRoleClassifier(itemsGR);

        // Create SDObject that will be created dynamically
        SDObject order = new SDObject("order", orderClass);
        SDObjectGR orderGR = new SDObjectGR(order, 650);
        model.addRoleClassifier(orderGR);

        // Message 1: User creates shopping cart
        CreateMessage createCart = new CreateMessage(user, cart);
        createCart.addParameter(new MethodParameter("userId"));
        CreateMessageGR createCartGR = new CreateMessageGR(userGR, cartGR, createCart, 100);
        model.addMessage(createCartGR);

        // Message 2: Return from create cart
        ReturnMessage createCartReturn = new ReturnMessage(cart, user, "cartRef");
        ReturnMessageGR createCartReturnGR = new ReturnMessageGR(cartGR, userGR, createCartReturn, 125);
        model.addMessage(createCartReturnGR);

        // Message 3: User adds item to cart
        GenericOperation addItemOp = new GenericOperation("addItem");
        CallMessage addItem = new CallMessage(user, cart, addItemOp);
        addItem.addParameter(new MethodParameter("item"));
        CallMessageGR addItemGR = new CallMessageGR(userGR, cartGR, addItem, 150);
        model.addMessage(addItemGR);

        // Message 4: Cart creates new item
        CreateMessage createItem = new CreateMessage(cart, item1);
        createItem.addParameter(new MethodParameter("productId"));
        createItem.addParameter(new MethodParameter("quantity"));
        CreateMessageGR createItemGR = new CreateMessageGR(cartGR, item1GR, createItem, 190);
        model.addMessage(createItemGR);

        // Message 5: Return from create item
        ReturnMessage createItemReturn = new ReturnMessage(item1, cart, "itemRef");
        ReturnMessageGR createItemReturnGR = new ReturnMessageGR(item1GR, cartGR, createItemReturn, 215);
        model.addMessage(createItemReturnGR);

        // Message 6: Cart calls items.add()
        GenericOperation addToCollectionOp = new GenericOperation("add");
        CallMessage addToCollection = new CallMessage(cart, items, addToCollectionOp);
        addToCollection.addParameter(new MethodParameter("item"));
        CallMessageGR addToCollectionGR = new CallMessageGR(cartGR, itemsGR, addToCollection, 240);
        model.addMessage(addToCollectionGR);

        // Message 7: Return from add
        ReturnMessage addReturn = new ReturnMessage(items, cart, "true");
        ReturnMessageGR addReturnGR = new ReturnMessageGR(itemsGR, cartGR, addReturn, 265);
        model.addMessage(addReturnGR);

        // Message 8: Return from addItem to user
        ReturnMessage addItemReturn = new ReturnMessage(cart, user, "success");
        ReturnMessageGR addItemReturnGR = new ReturnMessageGR(cartGR, userGR, addItemReturn, 290);
        model.addMessage(addItemReturnGR);

        // Message 9: User checks out (now user has focus again)
        GenericOperation checkoutOp = new GenericOperation("checkout");
        CallMessage checkout = new CallMessage(user, cart, checkoutOp);
        CallMessageGR checkoutGR = new CallMessageGR(userGR, cartGR, checkout, 330);
        model.addMessage(checkoutGR);

        // Message 10: Cart creates order
        CreateMessage createOrder = new CreateMessage(cart, order);
        createOrder.addParameter(new MethodParameter("cartId"));
        CreateMessageGR createOrderGR = new CreateMessageGR(cartGR, orderGR, createOrder, 370);
        model.addMessage(createOrderGR);

        // Message 11: Return from create order
        ReturnMessage createOrderReturn = new ReturnMessage(order, cart, "orderRef");
        ReturnMessageGR createOrderReturnGR = new ReturnMessageGR(orderGR, cartGR, createOrderReturn, 395);
        model.addMessage(createOrderReturnGR);

        // Message 12: Cart gets all items
        GenericOperation getAllOp = new GenericOperation("getAll");
        CallMessage getAll = new CallMessage(cart, items, getAllOp);
        CallMessageGR getAllGR = new CallMessageGR(cartGR, itemsGR, getAll, 420);
        model.addMessage(getAllGR);

        // Message 13: Return items list
        ReturnMessage itemsReturn = new ReturnMessage(items, cart, "itemsList");
        ReturnMessageGR itemsReturnGR = new ReturnMessageGR(itemsGR, cartGR, itemsReturn, 445);
        model.addMessage(itemsReturnGR);

        // Message 14: Cart calculates total on item
        GenericOperation getPriceOp = new GenericOperation("getPrice");
        CallMessage getPrice = new CallMessage(cart, item1, getPriceOp);
        CallMessageGR getPriceGR = new CallMessageGR(cartGR, item1GR, getPrice, 470);
        model.addMessage(getPriceGR);

        // Message 15: Return price
        ReturnMessage priceReturn = new ReturnMessage(item1, cart, "99.99");
        ReturnMessageGR priceReturnGR = new ReturnMessageGR(item1GR, cartGR, priceReturn, 495);
        model.addMessage(priceReturnGR);

        // Message 16: Return order to user
        ReturnMessage orderReturn = new ReturnMessage(cart, user, "orderId");
        ReturnMessageGR orderReturnGR = new ReturnMessageGR(cartGR, userGR, orderReturn, 520);
        model.addMessage(orderReturnGR);

        // Message 17: Cart destroys itself (after all returns)
        DestroyMessage destroyCart = new DestroyMessage(cart, cart);
        DestroyMessageGR destroyCartGR = new DestroyMessageGR(cartGR, cartGR, destroyCart, 560);
        model.addMessage(destroyCartGR);

        // Validate focus of control - all messages should be valid
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Check that no messages have validation errors
        StringBuilder validationErrors = new StringBuilder();
        for (GraphicalElement ge : model.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                SDMessageGR msgGR = (SDMessageGR) ge;
                String errorMsg = msgGR.getErrorMsg();
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    validationErrors.append(msgGR.getMessage().getName())
                            .append(": ").append(errorMsg).append("; ");
                }
            }
        }
        assertEquals("All messages should have correct focus of control (no validation errors)",
                "", validationErrors.toString());

        // Create Note
        UMLNoteGR noteGR = new UMLNoteGR("Shopping cart with collection of items", cartGR,
                new java.awt.Point(100, 500));
        model.addGraphicalElement(noteGR);

        // Total: 1 actor + 4 objects + 17 messages + 1 note = 23 elements
        // Messages: 3 create + 5 call + 8 return + 1 destroy = 17

        // ============================================================
        // 2. SAVE - Persist diagram to XML
        // ============================================================
        saveProject();

        // ============================================================
        // 3. LOAD - Restore from XML
        // ============================================================
        loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements were restored correctly
        // ============================================================

        // Verify diagram exists
        Vector<DiagramModel> diagrams = project.getDiagramModels();
        assertEquals("Should have 1 diagram", 1, diagrams.size());

        SDModel loadedModel = (SDModel) diagrams.get(0);
        assertNotNull("SD model should be loaded", loadedModel);
        assertEquals("Diagram title should match", "Shopping Cart SD", loadedModel.getName());

        // Verify graphical elements count (now 5 role classifiers + 17 messages + 1 note = 23)
        assertEquals("Should have 23 graphical elements", 23, loadedModel.getGraphicalElements().size());

        // Verify Role Classifiers (1 actor + 4 SD objects = 5)
        Vector<RoleClassifierGR> roleClassifiers = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof RoleClassifierGR) {
                roleClassifiers.add((RoleClassifierGR) ge);
            }
        }
        assertEquals("Should have 5 role classifiers", 5, roleClassifiers.size());

        // Check actor
        ActorInstanceGR loadedUserGR = null;
        SDObjectGR loadedCartGR = null;
        SDObjectGR loadedItem1GR = null;
        MultiObjectGR loadedItemsGR = null;
        SDObjectGR loadedOrderGR = null;

        for (RoleClassifierGR rcGR : roleClassifiers) {
            if (rcGR instanceof ActorInstanceGR) {
                ActorInstance ai = (ActorInstance) rcGR.getRoleClassifier();
                if (ai.getName().equals("user")) {
                    loadedUserGR = (ActorInstanceGR) rcGR;
                    assertEquals("User actor name", "User", ai.getActor().getName());
                }
            } else if (rcGR instanceof MultiObjectGR) {
                MultiObject mobj = (MultiObject) rcGR.getRoleClassifier();
                if (mobj.getName().equals("items")) {
                    loadedItemsGR = (MultiObjectGR) rcGR;
                    assertEquals("Items class name", "Item", mobj.getDesignClass().getName());
                }
            } else if (rcGR instanceof SDObjectGR) {
                SDObject obj = (SDObject) rcGR.getRoleClassifier();
                if (obj.getName().equals("cart")) {
                    loadedCartGR = (SDObjectGR) rcGR;
                    assertEquals("Cart class name", "ShoppingCart", obj.getDesignClass().getName());
                } else if (obj.getName().equals("item1")) {
                    loadedItem1GR = (SDObjectGR) rcGR;
                    assertEquals("Item class name", "Item", obj.getDesignClass().getName());
                } else if (obj.getName().equals("order")) {
                    loadedOrderGR = (SDObjectGR) rcGR;
                    assertEquals("Order class name", "Order", obj.getDesignClass().getName());
                }
            }
        }

        assertNotNull("User actor should be loaded", loadedUserGR);
        assertNotNull("Cart object should be loaded", loadedCartGR);
        assertNotNull("Item1 object should be loaded", loadedItem1GR);
        assertNotNull("Items multiobject should be loaded", loadedItemsGR);
        assertNotNull("Order object should be loaded", loadedOrderGR);

        // Verify Messages (17 messages total: 3 create + 5 call + 8 return + 1 destroy)
        Vector<SDMessageGR> messages = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                messages.add((SDMessageGR) ge);
            }
        }
        assertEquals("Should have 17 messages", 17, messages.size());

        // Count message types
        int createMessageCount = 0;
        int callMessageCount = 0;
        int returnMessageCount = 0;
        int destroyMessageCount = 0;

        for (SDMessageGR msgGR : messages) {
            SDMessage msg = msgGR.getMessage();
            // Check CreateMessage and DestroyMessage first since they extend CallMessage
            if (msg instanceof CreateMessage) {
                createMessageCount++;
                CreateMessage cm = (CreateMessage) msg;
                assertTrue("Create message should have name 'create'", cm.getName().equals("create"));
            } else if (msg instanceof DestroyMessage) {
                destroyMessageCount++;
            } else if (msg instanceof ReturnMessage) {
                returnMessageCount++;
            } else if (msg instanceof CallMessage) {
                // Regular call messages (not create/destroy)
                CallMessageGR cmGR = (CallMessageGR) msgGR;
                CallMessage cm = cmGR.getCallMessage();
                callMessageCount++;

                // Verify operations exist
                String opName = cm.getName();
                assertTrue("Operation name should be valid",
                        opName.equals("addItem") || opName.equals("add") ||
                                opName.equals("checkout") || opName.equals("getAll") ||
                                opName.equals("getPrice"));
            }
        }

        assertEquals("Should have 3 create messages", 3, createMessageCount);
        assertEquals("Should have 5 call messages", 5, callMessageCount);
        assertEquals("Should have 8 return messages", 8, returnMessageCount);
        assertEquals("Should have 1 destroy message", 1, destroyMessageCount);

        // Verify Note
        UMLNoteGR loadedNote = null;
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof UMLNoteGR) {
                loadedNote = (UMLNoteGR) ge;
                break;
            }
        }
        assertNotNull("Note should be loaded", loadedNote);
        assertEquals("Note text should match", "Shopping cart with collection of items",
                loadedNote.getText());

        // Verify CentralRepository contains domain objects

        // Check objects (cart, item1, order = 3 objects)
        Vector<SDObject> sdObjects = project.getCentralRepository().getSdObjects();
        assertEquals("Repository should have 3 SD objects", 3, sdObjects.size());

        // Check multiobjects (items = 1 multiobject)
        Vector<MultiObject> multiObjects = project.getCentralRepository().getMultiObjects();
        assertEquals("Repository should have 1 multiobject", 1, multiObjects.size());
        assertEquals("MultiObject should be named 'items'", "items", multiObjects.get(0).getName());

        // Check messages (17 total)
        Vector<SDMessage> sdMessages = project.getCentralRepository().getSDMessages();
        assertEquals("Repository should have 17 messages", 17, sdMessages.size());

        // Verify design classes are in repository
        Vector<DesignClass> classes = project.getCentralRepository().getClasses();
        assertTrue("Should have at least 3 design classes", classes.size() >= 3);
        boolean hasCart = false;
        boolean hasItem = false;
        boolean hasOrder = false;
        for (DesignClass dc : classes) {
            if (dc.getName().equals("ShoppingCart"))
                hasCart = true;
            if (dc.getName().equals("Item"))
                hasItem = true;
            if (dc.getName().equals("Order"))
                hasOrder = true;
        }
        assertTrue("Repository should have ShoppingCart class", hasCart);
        assertTrue("Repository should have Item class", hasItem);
        assertTrue("Repository should have Order class", hasOrder);
    }

    @Test
    public void testSDSaveLoadWithCombinedFragment() throws Exception {
        // ============================================================
        // 1. CREATE - Build SD with combined fragment
        // ============================================================
        SDModel model = new SDModel("Payment SD", project);

        // Create Actor
        Actor customerActor = new Actor("Customer");
        ActorInstance customer = new ActorInstance("customer", customerActor);
        ActorInstanceGR customerGR = new ActorInstanceGR(customer, 50);
        model.addRoleClassifier(customerGR);

        // Create Design Classes
        DesignClass paymentClass = new DesignClass("PaymentService");
        DesignClass bankClass = new DesignClass("BankAPI");

        // Create SD Objects
        SDObject payment = new SDObject("payment", paymentClass);
        SDObjectGR paymentGR = new SDObjectGR(payment, 250);
        model.addRoleClassifier(paymentGR);

        SDObject bank = new SDObject("bank", bankClass);
        SDObjectGR bankGR = new SDObjectGR(bank, 450);
        model.addRoleClassifier(bankGR);

        // Message 1: Customer initiates payment
        GenericOperation processOp = new GenericOperation("processPayment");
        CallMessage processPayment = new CallMessage(customer, payment, processOp);
        processPayment.addParameter(new MethodParameter("amount"));
        CallMessageGR processPaymentGR = new CallMessageGR(customerGR, paymentGR, processPayment, 100);
        model.addMessage(processPaymentGR);

        // Message 2: Payment validates amount
        GenericOperation validateOp = new GenericOperation("validateAmount");
        CallMessage validateAmount = new CallMessage(payment, bank, validateOp);
        validateAmount.addParameter(new MethodParameter("amount"));
        CallMessageGR validateAmountGR = new CallMessageGR(paymentGR, bankGR, validateAmount, 150);
        model.addMessage(validateAmountGR);

        // Message 3: Bank returns validation result
        ReturnMessage validationReturn = new ReturnMessage(bank, payment, "true");
        ReturnMessageGR validationReturnGR = new ReturnMessageGR(bankGR, paymentGR, validationReturn, 175);
        model.addMessage(validationReturnGR);

        // Create Combined Fragment covering the conditional charge
        CombinedFragment optFragment = new CombinedFragment(InteractionOperator.OPT, "[amount > 0]");
        optFragment.setHeight(150);
        project.getCentralRepository().addCombinedFragment(optFragment);
        CombinedFragmentGR optFragmentGR = new CombinedFragmentGR(optFragment, new java.awt.Point(200, 200), 400);
        model.addGraphicalElement(optFragmentGR);

        // Message 4: Charge account (inside OPT fragment)
        GenericOperation chargeOp = new GenericOperation("chargeAccount");
        CallMessage charge = new CallMessage(payment, bank, chargeOp);
        charge.addParameter(new MethodParameter("accountId"));
        charge.addParameter(new MethodParameter("amount"));
        CallMessageGR chargeGR = new CallMessageGR(paymentGR, bankGR, charge, 250);
        model.addMessage(chargeGR);

        // Message 5: Bank confirms charge (inside OPT fragment)
        ReturnMessage chargeReturn = new ReturnMessage(bank, payment, "transactionId");
        ReturnMessageGR chargeReturnGR = new ReturnMessageGR(bankGR, paymentGR, chargeReturn, 275);
        model.addMessage(chargeReturnGR);

        // Message 6: Return payment result to customer
        ReturnMessage paymentReturn = new ReturnMessage(payment, customer, "success");
        ReturnMessageGR paymentReturnGR = new ReturnMessageGR(paymentGR, customerGR, paymentReturn, 380);
        model.addMessage(paymentReturnGR);

        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // Total: 1 actor + 2 objects + 6 messages + 1 combined fragment = 10 elements

        // ============================================================
        // 2. SAVE - Persist diagram to XML
        // ============================================================
        saveProject();

        // ============================================================
        // 3. LOAD - Restore from XML
        // ============================================================
        loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements were restored correctly
        // ============================================================

        // Verify diagram exists
        Vector<DiagramModel> diagrams = project.getDiagramModels();
        assertEquals("Should have 1 diagram", 1, diagrams.size());

        SDModel loadedModel = (SDModel) diagrams.get(0);
        assertNotNull("SD model should be loaded", loadedModel);
        assertEquals("Diagram title should match", "Payment SD", loadedModel.getName());

        // Verify graphical elements count (3 role classifiers + 6 messages + 1 fragment = 10)
        assertEquals("Should have 10 graphical elements", 10, loadedModel.getGraphicalElements().size());

        // Verify Role Classifiers (1 actor + 2 objects = 3)
        Vector<RoleClassifierGR> roleClassifiers = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof RoleClassifierGR) {
                roleClassifiers.add((RoleClassifierGR) ge);
            }
        }
        assertEquals("Should have 3 role classifiers", 3, roleClassifiers.size());

        // Find loaded objects
        ActorInstanceGR loadedCustomerGR = null;
        SDObjectGR loadedPaymentGR = null;
        SDObjectGR loadedBankGR = null;

        for (RoleClassifierGR rcGR : roleClassifiers) {
            if (rcGR instanceof ActorInstanceGR) {
                ActorInstance ai = (ActorInstance) rcGR.getRoleClassifier();
                if (ai.getName().equals("customer")) {
                    loadedCustomerGR = (ActorInstanceGR) rcGR;
                }
            } else if (rcGR instanceof SDObjectGR) {
                SDObject obj = (SDObject) rcGR.getRoleClassifier();
                if (obj.getName().equals("payment")) {
                    loadedPaymentGR = (SDObjectGR) rcGR;
                } else if (obj.getName().equals("bank")) {
                    loadedBankGR = (SDObjectGR) rcGR;
                }
            }
        }

        assertNotNull("Customer actor should be loaded", loadedCustomerGR);
        assertNotNull("Payment object should be loaded", loadedPaymentGR);
        assertNotNull("Bank object should be loaded", loadedBankGR);

        // Verify Messages (6 messages: 3 call + 3 return)
        Vector<SDMessageGR> messages = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                messages.add((SDMessageGR) ge);
            }
        }
        assertEquals("Should have 6 messages", 6, messages.size());

        // Verify Combined Fragment
        CombinedFragmentGR loadedFragmentGR = null;
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof CombinedFragmentGR) {
                loadedFragmentGR = (CombinedFragmentGR) ge;
                break;
            }
        }
        assertNotNull("Combined fragment should be loaded", loadedFragmentGR);

        CombinedFragment loadedFragment = loadedFragmentGR.getCombinedFragment();
        assertNotNull("Combined fragment domain object should be loaded", loadedFragment);
        assertEquals("Fragment operator should be OPT", InteractionOperator.OPT,
                loadedFragment.getOperator());
        assertEquals("Fragment guard condition should match", "[amount > 0]",
                loadedFragment.getGuardCondition());
        assertEquals("Fragment height should be 150", 150, loadedFragment.getHeight());

        // Verify graphical properties
        // Note: The fragment position and width are auto-adjusted during creation to span
        // the messages within its Y range. We just verify it was loaded successfully.
        assertNotNull("Fragment should have a starting point", loadedFragmentGR.getStartingPoint());
        assertEquals("Fragment GR Y position should be 200", 200, loadedFragmentGR.getY());
        assertEquals("Fragment GR height should be 150", 150, loadedFragmentGR.getHeight());
        assertTrue("Fragment should have reasonable width", loadedFragmentGR.getWidth() >= 100); // Minimum width

        // Verify CentralRepository contains combined fragment
        Vector<CombinedFragment> fragments = project.getCentralRepository().getCombinedFragments();
        assertEquals("Repository should have 1 combined fragment", 1, fragments.size());
        assertEquals("Repository fragment should match loaded fragment", loadedFragment, fragments.get(0));

        // Verify messages are in repository
        Vector<SDMessage> sdMessages = project.getCentralRepository().getSDMessages();
        assertEquals("Repository should have 6 messages", 6, sdMessages.size());
    }

    @Test
    public void testSDWithAltFragmentSaveLoad() throws Exception {
        // ============================================================
        // 1. CREATE - Build SD with ALT fragment with multiple operands
        // ============================================================
        SDModel model = new SDModel("Payment Processing SD", project);

        // Create Design Classes for objects
        DesignClass customerClass = new DesignClass("Customer");
        DesignClass paymentClass = new DesignClass("PaymentService");

        // Create SD Objects
        SDObject customer = new SDObject("customer", customerClass);
        SDObjectGR customerGR = new SDObjectGR(customer, 100);
        model.addRoleClassifier(customerGR);

        SDObject payment = new SDObject("payment", paymentClass);
        SDObjectGR paymentGR = new SDObjectGR(payment, 300);
        model.addRoleClassifier(paymentGR);

        // Message 1: Customer requests payment
        GenericOperation payOp = new GenericOperation("processPayment");
        CallMessage payRequest = new CallMessage(customer, payment, payOp);
        payRequest.addParameter(new MethodParameter("amount"));
        CallMessageGR payRequestGR = new CallMessageGR(customerGR, paymentGR, payRequest, 150);
        model.addMessage(payRequestGR);

        // Message 2: Payment approved return (inside first operand)
        ReturnMessage approvedReturn = new ReturnMessage(payment, customer, "approved");
        ReturnMessageGR approvedReturnGR = new ReturnMessageGR(paymentGR, customerGR, approvedReturn, 200);
        model.addMessage(approvedReturnGR);

        // Message 3: Payment denied return (inside second operand)
        ReturnMessage deniedReturn = new ReturnMessage(payment, customer, "denied");
        ReturnMessageGR deniedReturnGR = new ReturnMessageGR(paymentGR, customerGR, deniedReturn, 250);
        model.addMessage(deniedReturnGR);

        // Create ALT Combined Fragment with two operands
        CombinedFragment altFragment = new CombinedFragment(InteractionOperator.ALT, "");
        altFragment.setHeight(140);

        // Add operands
        Operand validPayment = new Operand("[payment valid]");
        Operand invalidPayment = new Operand("[payment invalid]");
        altFragment.addOperand(validPayment);
        altFragment.addOperand(invalidPayment);

        // Create graphical representation
        CombinedFragmentGR altFragmentGR = new CombinedFragmentGR(
                altFragment,
                new java.awt.Point(50, 140),
                400);
        altFragmentGR.setHeight(140);
        model.addGraphicalElement(altFragmentGR);

        // Add to repository
        model.getCentralRepository().addCombinedFragment(altFragment);

        // Validate focus of control
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // ============================================================
        // 2. SAVE - Persist diagram to XML
        // ============================================================
        saveProject();

        // ============================================================
        // 3. LOAD - Restore from XML
        // ============================================================
        loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements were restored correctly
        // ============================================================

        // Verify diagram exists
        Vector<DiagramModel> diagrams = project.getDiagramModels();
        assertEquals("Should have 1 diagram", 1, diagrams.size());

        SDModel loadedModel = (SDModel) diagrams.get(0);
        assertEquals("Diagram name should match", "Payment Processing SD", loadedModel.getName());

        // Verify Role Classifiers (2 objects)
        Vector<RoleClassifierGR> roleClassifiers = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof RoleClassifierGR) {
                roleClassifiers.add((RoleClassifierGR) ge);
            }
        }
        assertEquals("Should have 2 objects", 2, roleClassifiers.size());

        // Verify Messages (3 messages)
        Vector<SDMessageGR> messages = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                messages.add((SDMessageGR) ge);
            }
        }
        assertEquals("Should have 3 messages", 3, messages.size());

        // Verify ALT Combined Fragment
        CombinedFragmentGR loadedFragmentGR = null;
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof CombinedFragmentGR) {
                loadedFragmentGR = (CombinedFragmentGR) ge;
                break;
            }
        }
        assertNotNull("ALT fragment should be loaded", loadedFragmentGR);

        CombinedFragment loadedFragment = loadedFragmentGR.getCombinedFragment();
        assertNotNull("ALT fragment domain object should be loaded", loadedFragment);
        assertEquals("Fragment operator should be ALT", InteractionOperator.ALT,
                loadedFragment.getOperator());
        assertEquals("Fragment height should be 140", 140, loadedFragment.getHeight());

        // Verify operands were loaded
        java.util.List<Operand> operands = loadedFragment.getOperands();
        assertNotNull("Operands list should not be null", operands);
        assertEquals("Should have 2 operands", 2, operands.size());

        // Verify first operand
        Operand firstOperand = operands.get(0);
        assertEquals("First operand guard should match", "[payment valid]",
                firstOperand.getGuardCondition());

        // Verify second operand
        Operand secondOperand = operands.get(1);
        assertEquals("Second operand guard should match", "[payment invalid]",
                secondOperand.getGuardCondition());

        // Verify graphical properties
        assertNotNull("Fragment should have a starting point", loadedFragmentGR.getStartingPoint());
        assertEquals("Fragment GR Y position should be 140", 140, loadedFragmentGR.getY());
        assertEquals("Fragment GR height should be 140", 140, loadedFragmentGR.getHeight());
        assertTrue("Fragment should have reasonable width", loadedFragmentGR.getWidth() >= 100);

        // Verify CentralRepository contains combined fragment
        Vector<CombinedFragment> fragments = project.getCentralRepository().getCombinedFragments();
        assertEquals("Repository should have 1 combined fragment", 1, fragments.size());
        assertEquals("Repository fragment should match loaded fragment", loadedFragment, fragments.get(0));

        // Verify messages are in repository
        Vector<SDMessage> sdMessages = project.getCentralRepository().getSDMessages();
        assertEquals("Repository should have 3 messages", 3, sdMessages.size());
    }

    @Test
    public void testSDWithAllFragmentTypesSaveLoad() throws Exception {
        // ============================================================
        // 1. CREATE - Build SD with all fragment types: OPT, ALT, LOOP
        // ============================================================
        SDModel model = new SDModel("Complete Fragment Test SD", project);

        // Create Design Classes for objects
        DesignClass userClass = new DesignClass("User");
        DesignClass serviceClass = new DesignClass("Service");
        DesignClass dbClass = new DesignClass("Database");

        // Create SD Objects
        SDObject user = new SDObject("user", userClass);
        SDObjectGR userGR = new SDObjectGR(user, 50);
        model.addRoleClassifier(userGR);

        SDObject service = new SDObject("service", serviceClass);
        SDObjectGR serviceGR = new SDObjectGR(service, 250);
        model.addRoleClassifier(serviceGR);

        SDObject database = new SDObject("db", dbClass);
        SDObjectGR databaseGR = new SDObjectGR(database, 450);
        model.addRoleClassifier(databaseGR);

        // === Message 1: User calls service.login() ===
        GenericOperation loginOp = new GenericOperation("login");
        CallMessage loginCall = new CallMessage(user, service, loginOp);
        loginCall.addParameter(new MethodParameter("username"));
        loginCall.addParameter(new MethodParameter("password"));
        CallMessageGR loginCallGR = new CallMessageGR(userGR, serviceGR, loginCall, 120);
        model.addMessage(loginCallGR);

        // === OPT Fragment: Authentication check [user authenticated] ===
        CombinedFragment optFragment = new CombinedFragment(InteractionOperator.OPT, "[user authenticated]");
        optFragment.setHeight(180);
        CombinedFragmentGR optFragmentGR = new CombinedFragmentGR(
                optFragment,
                new java.awt.Point(30, 150),
                550);
        optFragmentGR.setHeight(180);
        model.addGraphicalElement(optFragmentGR);
        model.getCentralRepository().addCombinedFragment(optFragment);

        // Message 2: Service queries database (inside OPT)
        GenericOperation queryOp = new GenericOperation("query");
        CallMessage queryCall = new CallMessage(service, database, queryOp);
        queryCall.addParameter(new MethodParameter("userId"));
        CallMessageGR queryCallGR = new CallMessageGR(serviceGR, databaseGR, queryCall, 200);
        model.addMessage(queryCallGR);

        // Message 3: Database returns result (inside OPT)
        ReturnMessage queryReturn = new ReturnMessage(database, service, "userData");
        ReturnMessageGR queryReturnGR = new ReturnMessageGR(databaseGR, serviceGR, queryReturn, 250);
        model.addMessage(queryReturnGR);

        // Message 4: Return from login
        ReturnMessage loginReturn = new ReturnMessage(service, user, "sessionToken");
        ReturnMessageGR loginReturnGR = new ReturnMessageGR(serviceGR, userGR, loginReturn, 350);
        model.addMessage(loginReturnGR);

        // === Message 5: User calls service.fetchData() ===
        GenericOperation fetchOp = new GenericOperation("fetchData");
        CallMessage fetchCall = new CallMessage(user, service, fetchOp);
        CallMessageGR fetchCallGR = new CallMessageGR(userGR, serviceGR, fetchCall, 400);
        model.addMessage(fetchCallGR);

        // === ALT Fragment: Two alternatives for data source ===
        CombinedFragment altFragment = new CombinedFragment(InteractionOperator.ALT, "");
        altFragment.setHeight(200);

        // Add operands with custom height ratios
        Operand cacheOperand = new Operand("[data in cache]");
        cacheOperand.setHeightRatio(1.0);
        Operand dbOperand = new Operand("[cache miss]");
        dbOperand.setHeightRatio(2.0); // Twice as tall
        altFragment.addOperand(cacheOperand);
        altFragment.addOperand(dbOperand);

        CombinedFragmentGR altFragmentGR = new CombinedFragmentGR(
                altFragment,
                new java.awt.Point(30, 430),
                550);
        altFragmentGR.setHeight(200);
        model.addGraphicalElement(altFragmentGR);
        model.getCentralRepository().addCombinedFragment(altFragment);

        // Message 6: Return cached data (first operand)
        ReturnMessage cachedReturn = new ReturnMessage(service, user, "cachedData");
        ReturnMessageGR cachedReturnGR = new ReturnMessageGR(serviceGR, userGR, cachedReturn, 480);
        model.addMessage(cachedReturnGR);

        // Message 7: Query database (second operand)
        GenericOperation fetchFromDBOp = new GenericOperation("fetchFromDB");
        CallMessage fetchFromDBCall = new CallMessage(service, database, fetchFromDBOp);
        CallMessageGR fetchFromDBCallGR = new CallMessageGR(serviceGR, databaseGR, fetchFromDBCall, 530);
        model.addMessage(fetchFromDBCallGR);

        // Message 8: Return from database (second operand)
        ReturnMessage dbReturn = new ReturnMessage(database, service, "freshData");
        ReturnMessageGR dbReturnGR = new ReturnMessageGR(databaseGR, serviceGR, dbReturn, 580);
        model.addMessage(dbReturnGR);

        // Message 9: Return from fetchData
        ReturnMessage fetchReturn = new ReturnMessage(service, user, "data");
        ReturnMessageGR fetchReturnGR = new ReturnMessageGR(serviceGR, userGR, fetchReturn, 650);
        model.addMessage(fetchReturnGR);

        // === Message 10: User calls service.processItems() ===
        GenericOperation processOp = new GenericOperation("processItems");
        CallMessage processCall = new CallMessage(user, service, processOp);
        processCall.addParameter(new MethodParameter("items"));
        CallMessageGR processCallGR = new CallMessageGR(userGR, serviceGR, processCall, 700);
        model.addMessage(processCallGR);

        // === LOOP Fragment: Process each item loop(3, 5) ===
        CombinedFragment loopFragment = new CombinedFragment(InteractionOperator.LOOP, "[for each item]");
        loopFragment.setHeight(120);
        loopFragment.setLoopMin(3);
        loopFragment.setLoopMax(5);

        CombinedFragmentGR loopFragmentGR = new CombinedFragmentGR(
                loopFragment,
                new java.awt.Point(30, 730),
                550);
        loopFragmentGR.setHeight(120);
        model.addGraphicalElement(loopFragmentGR);
        model.getCentralRepository().addCombinedFragment(loopFragment);

        // Message 11: Process item (inside LOOP)
        GenericOperation processItemOp = new GenericOperation("processItem");
        CallMessage processItemCall = new CallMessage(service, database, processItemOp);
        processItemCall.addParameter(new MethodParameter("item"));
        CallMessageGR processItemCallGR = new CallMessageGR(serviceGR, databaseGR, processItemCall, 780);
        model.addMessage(processItemCallGR);

        // Message 12: Return from processItem (inside LOOP)
        ReturnMessage processItemReturn = new ReturnMessage(database, service, "success");
        ReturnMessageGR processItemReturnGR = new ReturnMessageGR(databaseGR, serviceGR, processItemReturn, 810);
        model.addMessage(processItemReturnGR);

        // Message 13: Return from processItems
        ReturnMessage processReturn = new ReturnMessage(service, user, "allProcessed");
        ReturnMessageGR processReturnGR = new ReturnMessageGR(serviceGR, userGR, processReturn, 870);
        model.addMessage(processReturnGR);

        // Validate focus of control
        model.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        // ============================================================
        // 2. SAVE - Persist diagram to XML
        // ============================================================
        saveProject();

        // ============================================================
        // 3. LOAD - Restore from XML
        // ============================================================
        loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements were restored correctly
        // ============================================================

        // Verify diagram exists
        Vector<DiagramModel> diagrams = project.getDiagramModels();
        assertEquals("Should have 1 diagram", 1, diagrams.size());

        SDModel loadedModel = (SDModel) diagrams.get(0);
        assertEquals("Diagram name should match", "Complete Fragment Test SD", loadedModel.getName());

        // Verify Role Classifiers (3 objects)
        Vector<RoleClassifierGR> roleClassifiers = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof RoleClassifierGR) {
                roleClassifiers.add((RoleClassifierGR) ge);
            }
        }
        assertEquals("Should have 3 objects", 3, roleClassifiers.size());

        // Verify Messages (13 messages total)
        Vector<SDMessageGR> messages = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof SDMessageGR) {
                messages.add((SDMessageGR) ge);
            }
        }
        assertEquals("Should have 13 messages", 13, messages.size());

        // Verify Combined Fragments (3 fragments: OPT, ALT, LOOP)
        Vector<CombinedFragmentGR> fragmentGRs = new Vector<>();
        for (GraphicalElement ge : loadedModel.getGraphicalElements()) {
            if (ge instanceof CombinedFragmentGR) {
                fragmentGRs.add((CombinedFragmentGR) ge);
            }
        }
        assertEquals("Should have 3 combined fragments", 3, fragmentGRs.size());

        // Find each fragment type
        CombinedFragmentGR loadedOptGR = null;
        CombinedFragmentGR loadedAltGR = null;
        CombinedFragmentGR loadedLoopGR = null;

        for (CombinedFragmentGR fragmentGR : fragmentGRs) {
            CombinedFragment fragment = fragmentGR.getCombinedFragment();
            switch (fragment.getOperator()) {
            case OPT:
                loadedOptGR = fragmentGR;
                break;
            case ALT:
                loadedAltGR = fragmentGR;
                break;
            case LOOP:
                loadedLoopGR = fragmentGR;
                break;
            }
        }

        // ====== Verify OPT Fragment ======
        assertNotNull("OPT fragment should be loaded", loadedOptGR);
        CombinedFragment loadedOpt = loadedOptGR.getCombinedFragment();
        assertEquals("OPT operator should match", InteractionOperator.OPT, loadedOpt.getOperator());
        assertEquals("OPT guard should match", "[user authenticated]", loadedOpt.getGuardCondition());
        assertEquals("OPT height should be 180", 180, loadedOpt.getHeight());
        assertNull("OPT should have no loop min", loadedOpt.getLoopMin());
        assertNull("OPT should have no loop max", loadedOpt.getLoopMax());
        assertTrue("OPT should have no operands", loadedOpt.getOperands().isEmpty());

        // ====== Verify ALT Fragment ======
        assertNotNull("ALT fragment should be loaded", loadedAltGR);
        CombinedFragment loadedAlt = loadedAltGR.getCombinedFragment();
        assertEquals("ALT operator should match", InteractionOperator.ALT, loadedAlt.getOperator());
        assertEquals("ALT height should be 200", 200, loadedAlt.getHeight());

        // Verify ALT operands
        java.util.List<Operand> altOperands = loadedAlt.getOperands();
        assertNotNull("ALT operands should not be null", altOperands);
        assertEquals("ALT should have 2 operands", 2, altOperands.size());

        Operand loadedCacheOperand = altOperands.get(0);
        assertEquals("First ALT operand guard should match", "[data in cache]",
                loadedCacheOperand.getGuardCondition());
        assertEquals("First ALT operand height ratio should be 1.0", 1.0,
                loadedCacheOperand.getHeightRatio(), 0.001);

        Operand loadedDBOperand = altOperands.get(1);
        assertEquals("Second ALT operand guard should match", "[cache miss]",
                loadedDBOperand.getGuardCondition());
        assertEquals("Second ALT operand height ratio should be 2.0", 2.0,
                loadedDBOperand.getHeightRatio(), 0.001);

        // ====== Verify LOOP Fragment ======
        assertNotNull("LOOP fragment should be loaded", loadedLoopGR);
        CombinedFragment loadedLoop = loadedLoopGR.getCombinedFragment();
        assertEquals("LOOP operator should match", InteractionOperator.LOOP, loadedLoop.getOperator());
        assertEquals("LOOP guard should match", "[for each item]", loadedLoop.getGuardCondition());
        assertEquals("LOOP height should be 120", 120, loadedLoop.getHeight());

        // Verify loop iterations
        assertNotNull("LOOP should have loop min", loadedLoop.getLoopMin());
        assertEquals("LOOP min should be 3", 3, loadedLoop.getLoopMin().intValue());
        assertNotNull("LOOP should have loop max", loadedLoop.getLoopMax());
        assertEquals("LOOP max should be 5", 5, loadedLoop.getLoopMax().intValue());

        // Verify CentralRepository contains all 3 fragments
        Vector<CombinedFragment> fragments = project.getCentralRepository().getCombinedFragments();
        assertEquals("Repository should have 3 combined fragments", 3, fragments.size());

        // Verify messages are in repository
        Vector<SDMessage> sdMessages = project.getCentralRepository().getSDMessages();
        assertEquals("Repository should have 13 messages", 13, sdMessages.size());
    }
}
