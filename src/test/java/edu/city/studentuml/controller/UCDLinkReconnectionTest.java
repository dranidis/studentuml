package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.view.gui.UCDInternalFrame;

/**
 * Tests for link endpoint reconnection in Use Case Diagrams. Tests the
 * reconnection of UC associations, includes, extends, and generalizations (both
 * Actor and UseCase).
 */
public class UCDLinkReconnectionTest {

    UMLProject umlProject;
    UCDModel model;
    UCDInternalFrame internalFrame;
    Helper h;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new UCDModel("ucd", umlProject);
        internalFrame = new UCDInternalFrame(model);
        h = new Helper(model);
    }

    // ========== UC Association Reconnection Tests ==========

    @Test
    public void testUCAssociationReconnectSource() {
        UCActorGR actor1 = h.addActor("Customer");
        UCActorGR actor2 = h.addActor("Manager");
        UseCaseGR uc = h.addUseCase("Search");

        UCAssociationGR assoc = h.addUcAssociation(actor1, uc);

        // Verify initial state
        assertEquals(actor1, assoc.getSource());
        assertEquals(uc, assoc.getTarget());

        // Reconnect source from actor1 to actor2
        boolean reconnected = assoc.reconnectSource(actor2);
        assertTrue("Reconnection should succeed", reconnected);

        UCAssociationGR newAssoc = assoc.createWithNewEndpoints(actor2, uc);
        assertNotNull(newAssoc);
        assertEquals(actor2, newAssoc.getSource());
        assertEquals(uc, newAssoc.getTarget());
    }

    @Test
    public void testUCAssociationReconnectTarget() {
        UCActorGR actor = h.addActor("Customer");
        UseCaseGR uc1 = h.addUseCase("Search");
        UseCaseGR uc2 = h.addUseCase("Borrow");

        UCAssociationGR assoc = h.addUcAssociation(actor, uc1);

        // Reconnect target from uc1 to uc2
        boolean reconnected = assoc.reconnectTarget(uc2);
        assertTrue("Reconnection should succeed", reconnected);

        UCAssociationGR newAssoc = assoc.createWithNewEndpoints(actor, uc2);
        assertEquals(actor, newAssoc.getSource());
        assertEquals(uc2, newAssoc.getTarget());
    }

    @Test
    public void testUCAssociationCannotReconnectSourceToUseCase() {
        UCActorGR actor = h.addActor("Customer");
        UseCaseGR uc1 = h.addUseCase("Search");
        UseCaseGR uc2 = h.addUseCase("Borrow");

        UCAssociationGR assoc = h.addUcAssociation(actor, uc1);

        // Source must be an Actor, not a UseCase
        boolean canReconnect = assoc.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.SOURCE, uc2);
        assertFalse("UC Association source must be an Actor", canReconnect);

        boolean reconnected = assoc.reconnectSource(uc2);
        assertFalse("Reconnection should fail", reconnected);
    }

    @Test
    public void testUCAssociationCannotReconnectTargetToActor() {
        UCActorGR actor1 = h.addActor("Customer");
        UCActorGR actor2 = h.addActor("Manager");
        UseCaseGR uc = h.addUseCase("Search");

        UCAssociationGR assoc = h.addUcAssociation(actor1, uc);

        // Target must be a UseCase, not an Actor
        boolean canReconnect = assoc.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.TARGET, actor2);
        assertFalse("UC Association target must be a UseCase", canReconnect);

        boolean reconnected = assoc.reconnectTarget(actor2);
        assertFalse("Reconnection should fail", reconnected);
    }

    // ========== UC Include Reconnection Tests ==========

    @Test
    public void testUCIncludeReconnectSource() {
        UseCaseGR uc1 = h.addUseCase("Borrow Book");
        UseCaseGR uc2 = h.addUseCase("Return Book");
        UseCaseGR uc3 = h.addUseCase("Check Availability");

        UCIncludeGR include = h.addUcInclude(uc1, uc3);

        // Verify initial state
        assertEquals(uc1, include.getSource());
        assertEquals(uc3, include.getTarget());

        // Reconnect source from uc1 to uc2
        boolean reconnected = include.reconnectSource(uc2);
        assertTrue("Reconnection should succeed", reconnected);

        UCIncludeGR newInclude = include.createWithNewEndpoints(uc2, uc3);
        assertEquals(uc2, newInclude.getSource());
        assertEquals(uc3, newInclude.getTarget());
    }

    @Test
    public void testUCIncludeReconnectTarget() {
        UseCaseGR uc1 = h.addUseCase("Borrow Book");
        UseCaseGR uc2 = h.addUseCase("Check Availability");
        UseCaseGR uc3 = h.addUseCase("Search Catalog");

        UCIncludeGR include = h.addUcInclude(uc1, uc2);

        // Reconnect target from uc2 to uc3
        boolean reconnected = include.reconnectTarget(uc3);
        assertTrue("Reconnection should succeed", reconnected);

        UCIncludeGR newInclude = include.createWithNewEndpoints(uc1, uc3);
        assertEquals(uc3, newInclude.getTarget());
    }

    @Test
    public void testUCIncludeCannotReconnectToActor() {
        UseCaseGR uc1 = h.addUseCase("Borrow Book");
        UseCaseGR uc2 = h.addUseCase("Check Availability");
        UCActorGR actor = h.addActor("Customer");

        UCIncludeGR include = h.addUcInclude(uc1, uc2);

        // Include must connect UseCase to UseCase only
        boolean canReconnect = include.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.TARGET, actor);
        assertFalse("UC Include cannot connect to Actor", canReconnect);

        boolean reconnected = include.reconnectTarget(actor);
        assertFalse("Reconnection should fail", reconnected);
    }

    // ========== UC Extend Reconnection Tests ==========

    @Test
    public void testUCExtendReconnectSource() {
        UseCaseGR uc1 = h.addUseCase("Search Catalog");
        UseCaseGR uc2 = h.addUseCase("Browse Catalog");
        UseCaseGR uc3 = h.addUseCase("Advanced Search");

        UCExtendGR extend = h.addUcExtend(uc1, uc3);

        // Reconnect source from uc1 to uc2
        boolean reconnected = extend.reconnectSource(uc2);
        assertTrue("Reconnection should succeed", reconnected);

        UCExtendGR newExtend = extend.createWithNewEndpoints(uc2, uc3);
        assertEquals(uc2, newExtend.getSource());
        assertEquals(uc3, newExtend.getTarget());
    }

    @Test
    public void testUCExtendReconnectTarget() {
        UseCaseGR uc1 = h.addUseCase("Advanced Search");
        UseCaseGR uc2 = h.addUseCase("Search Catalog");
        UseCaseGR uc3 = h.addUseCase("Browse Catalog");

        UCExtendGR extend = h.addUcExtend(uc1, uc2);

        // Reconnect target from uc2 to uc3
        boolean reconnected = extend.reconnectTarget(uc3);
        assertTrue("Reconnection should succeed", reconnected);

        UCExtendGR newExtend = extend.createWithNewEndpoints(uc1, uc3);
        assertEquals(uc3, newExtend.getTarget());
    }

    @Test
    public void testUCExtendPreservesExtensionPoints() {
        UseCaseGR uc1 = h.addUseCase("Advanced Search");
        UseCaseGR uc2 = h.addUseCase("Search Catalog");
        UseCaseGR uc3 = h.addUseCase("Browse Catalog");

        UCExtendGR extend = h.addUcExtend(uc1, uc2);

        // Add extension points
        edu.city.studentuml.model.domain.ExtensionPoint ep1 = new edu.city.studentuml.model.domain.ExtensionPoint(
                "filter results");
        extend.getLink().addExtensionPoint(ep1);

        assertEquals(1, extend.getNumberOfExtensionPoints());

        // Reconnect and verify extension points are preserved
        extend.reconnectTarget(uc3);
        UCExtendGR newExtend = extend.createWithNewEndpoints(uc1, uc3);

        assertEquals("Extension points should be preserved",
                1, newExtend.getNumberOfExtensionPoints());
        assertEquals("Extension point should match",
                "filter results",
                newExtend.getLink().getExtensionPoints().get(0).getName());
    }

    @Test
    public void testUCExtendCannotReconnectToActor() {
        UseCaseGR uc1 = h.addUseCase("Advanced Search");
        UseCaseGR uc2 = h.addUseCase("Search Catalog");
        UCActorGR actor = h.addActor("Customer");

        UCExtendGR extend = h.addUcExtend(uc1, uc2);

        // Extend must connect UseCase to UseCase only
        boolean canReconnect = extend.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.SOURCE, actor);
        assertFalse("UC Extend cannot connect to Actor", canReconnect);

        boolean reconnected = extend.reconnectSource(actor);
        assertFalse("Reconnection should fail", reconnected);
    }

    // ========== UC Generalization Actor Tests ==========

    @Test
    public void testUCGeneralizationActorReconnectSource() {
        UCActorGR customer = h.addActor("Customer");
        UCActorGR manager = h.addActor("Manager");
        UCActorGR admin = h.addActor("Admin");

        UCGeneralizationGR gen = h.addUcGeneralizationActor(customer, manager);

        // Verify initial state
        assertEquals(customer, gen.getSource());
        assertEquals(manager, gen.getTarget());

        // Reconnect source from customer to admin
        boolean reconnected = gen.reconnectSource(admin);
        assertTrue("Reconnection should succeed", reconnected);

        UCGeneralizationGR newGen = gen.createWithNewEndpoints(admin, manager);
        assertEquals(admin, newGen.getSource());
        assertEquals(manager, newGen.getTarget());
    }

    @Test
    public void testUCGeneralizationActorReconnectTarget() {
        UCActorGR customer = h.addActor("Customer");
        UCActorGR manager = h.addActor("Manager");
        UCActorGR admin = h.addActor("Admin");

        UCGeneralizationGR gen = h.addUcGeneralizationActor(customer, manager);

        // Reconnect target from manager to admin
        boolean reconnected = gen.reconnectTarget(admin);
        assertTrue("Reconnection should succeed", reconnected);

        UCGeneralizationGR newGen = gen.createWithNewEndpoints(customer, admin);
        assertEquals(customer, newGen.getSource());
        assertEquals(admin, newGen.getTarget());
    }

    @Test
    public void testUCGeneralizationActorCannotMixWithUseCase() {
        UCActorGR actor1 = h.addActor("Customer");
        UCActorGR actor2 = h.addActor("Manager");
        UseCaseGR uc = h.addUseCase("Search");

        UCGeneralizationGR gen = h.addUcGeneralizationActor(actor1, actor2);

        // Cannot mix Actor and UseCase in generalization
        boolean canReconnect = gen.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.TARGET, uc);
        assertFalse("Cannot mix Actor and UseCase", canReconnect);

        boolean reconnected = gen.reconnectTarget(uc);
        assertFalse("Reconnection should fail", reconnected);
    }

    // ========== UC Generalization UseCase Tests ==========

    @Test
    public void testUCGeneralizationUseCaseReconnectSource() {
        UseCaseGR uc1 = h.addUseCase("Search Books");
        UseCaseGR uc2 = h.addUseCase("Search Documents");
        UseCaseGR uc3 = h.addUseCase("Search");

        UCGeneralizationGR gen = h.addUcGeneralizationUseCase(uc1, uc3);

        // Reconnect source from uc1 to uc2
        boolean reconnected = gen.reconnectSource(uc2);
        assertTrue("Reconnection should succeed", reconnected);

        UCGeneralizationGR newGen = gen.createWithNewEndpoints(uc2, uc3);
        assertEquals(uc2, newGen.getSource());
        assertEquals(uc3, newGen.getTarget());
    }

    @Test
    public void testUCGeneralizationUseCaseReconnectTarget() {
        UseCaseGR uc1 = h.addUseCase("Search Books");
        UseCaseGR uc2 = h.addUseCase("Search");
        UseCaseGR uc3 = h.addUseCase("Find");

        UCGeneralizationGR gen = h.addUcGeneralizationUseCase(uc1, uc2);

        // Reconnect target from uc2 to uc3
        boolean reconnected = gen.reconnectTarget(uc3);
        assertTrue("Reconnection should succeed", reconnected);

        UCGeneralizationGR newGen = gen.createWithNewEndpoints(uc1, uc3);
        assertEquals(uc3, newGen.getTarget());
    }

    @Test
    public void testUCGeneralizationUseCaseCannotMixWithActor() {
        UseCaseGR uc1 = h.addUseCase("Search Books");
        UseCaseGR uc2 = h.addUseCase("Search");
        UCActorGR actor = h.addActor("Customer");

        UCGeneralizationGR gen = h.addUcGeneralizationUseCase(uc1, uc2);

        // Cannot mix UseCase and Actor in generalization
        boolean canReconnect = gen.canReconnect(
                edu.city.studentuml.model.graphical.EndpointType.SOURCE, actor);
        assertFalse("Cannot mix UseCase and Actor", canReconnect);

        boolean reconnected = gen.reconnectSource(actor);
        assertFalse("Reconnection should fail", reconnected);
    }

    // ========== Model Integration Tests ==========

    @Test
    public void testReconnectionUpdatesModel() {
        UCActorGR actor1 = h.addActor("Customer");
        UCActorGR actor2 = h.addActor("Manager");
        UseCaseGR uc = h.addUseCase("Search");

        UCAssociationGR oldAssoc = h.addUcAssociation(actor1, uc);

        int initialCount = model.getGraphicalElements().size();

        // Reconnect and replace in model
        oldAssoc.reconnectSource(actor2);
        UCAssociationGR newAssoc = oldAssoc.createWithNewEndpoints(actor2, uc);

        model.removeGraphicalElement(oldAssoc);
        model.addGraphicalElement(newAssoc);

        // Model should still have same number of elements
        assertEquals(initialCount, model.getGraphicalElements().size());

        // New association should be in model
        assertTrue(model.getGraphicalElements().contains(newAssoc));
        assertFalse(model.getGraphicalElements().contains(oldAssoc));
    }

    @Test
    public void testMultipleReconnections() {
        UCActorGR actor1 = h.addActor("Customer");
        UCActorGR actor2 = h.addActor("Manager");
        UCActorGR actor3 = h.addActor("Admin");
        UseCaseGR uc = h.addUseCase("Search");

        UCAssociationGR assoc1 = h.addUcAssociation(actor1, uc);

        // First reconnection
        assoc1.reconnectSource(actor2);
        UCAssociationGR assoc2 = assoc1.createWithNewEndpoints(actor2, uc);
        assertEquals(actor2, assoc2.getSource());

        // Second reconnection
        assoc2.reconnectSource(actor3);
        UCAssociationGR assoc3 = assoc2.createWithNewEndpoints(actor3, uc);
        assertEquals(actor3, assoc3.getSource());
        assertEquals(uc, assoc3.getTarget());
    }
}
