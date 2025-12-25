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
}
