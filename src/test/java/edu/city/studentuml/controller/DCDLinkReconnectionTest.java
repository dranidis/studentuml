package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.EndpointType;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.view.gui.DCDInternalFrame;

/**
 * Tests for link endpoint reconnection in Design Class Diagrams. Tests the
 * reconnection of associations, aggregations, dependencies, realizations, and
 * generalizations.
 */
public class DCDLinkReconnectionTest {

        UMLProject umlProject;
        DCDModel model;
        DCDInternalFrame internalFrame;
        Helper h;

        @Before
        public void setup() {
                umlProject = UMLProject.getInstance();
                umlProject.clear();
                model = new DCDModel("dcd", umlProject);
                internalFrame = new DCDInternalFrame(model, true);
                h = new Helper(model);
        }

        // ========== Association Reconnection Tests ==========

        @Test
        public void testAssociationReconnectSource() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AssociationGR assoc = h.addAssociation(a, b);

                // Verify initial state
                assertEquals(a, assoc.getClassA());
                assertEquals(b, assoc.getClassB());
                assertEquals("A", assoc.getAssociation().getRoleA().getReferredClass().getName());

                // Reconnect source from A to C
                assertTrue("Reconnection should succeed", assoc.reconnectSource(c));

                // Create new link with updated endpoints
                AssociationGR newAssoc = assoc.createWithNewEndpoints(c, b);
                assertNotNull(newAssoc);
                assertEquals(c, newAssoc.getClassA());
                assertEquals(b, newAssoc.getClassB());
                assertEquals("C", newAssoc.getAssociation().getRoleA().getReferredClass().getName());
                assertEquals("B", newAssoc.getAssociation().getRoleB().getReferredClass().getName());

                // Verify role names and multiplicities are preserved
                assertEquals(assoc.getAssociation().getRoleA().getName(),
                                newAssoc.getAssociation().getRoleA().getName());
                assertEquals(assoc.getAssociation().getRoleB().getName(),
                                newAssoc.getAssociation().getRoleB().getName());
        }

        @Test
        public void testAssociationReconnectTarget() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AssociationGR assoc = h.addAssociation(a, b);

                // Reconnect target from B to C
                assertTrue("Reconnection should succeed", assoc.reconnectTarget(c));

                AssociationGR newAssoc = assoc.createWithNewEndpoints(a, c);
                assertEquals(a, newAssoc.getClassA());
                assertEquals(c, newAssoc.getClassB());
                assertEquals("C", newAssoc.getAssociation().getRoleB().getReferredClass().getName());
        }

        @Test
        public void testAssociationReconnectToSelf() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");

                AssociationGR assoc = h.addAssociation(a, b);

                // Self-associations are allowed in UML (reflexive relationships)
                assertTrue("Self-association should be allowed", assoc.reconnectTarget(a));

                AssociationGR newAssoc = assoc.createWithNewEndpoints(a, a);
                assertEquals(a, newAssoc.getClassA());
                assertEquals(a, newAssoc.getClassB());
        }

        @Test
        public void testAssociationReconnectToInterface() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                InterfaceGR i = h.addInterface("I");

                AssociationGR assoc = h.addAssociation(a, b);

                // Associations can connect to interfaces
                boolean reconnected = assoc.reconnectTarget(i);
                assertTrue("Association to interface should be allowed", reconnected);

                AssociationGR newAssoc = assoc.createWithNewEndpoints(a, i);
                assertEquals(i, newAssoc.getClassB());
        }

        // ========== Aggregation Reconnection Tests ==========

        @Test
        public void testAggregationReconnectSource() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AggregationGR aggr = h.addAggregation(a, b);

                boolean reconnected = aggr.reconnectSource(c);
                assertTrue("Reconnection should succeed", reconnected);

                AggregationGR newAggr = aggr.createWithNewEndpoints(c, b);
                assertEquals(c, newAggr.getClassA());
                assertEquals(b, newAggr.getClassB());

                // Verify it's still an Aggregation domain object (not just Association)
                assertTrue("Should preserve Aggregation type",
                                newAggr.getAssociation() instanceof Aggregation);
        }

        @Test
        public void testAggregationReconnectTarget() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AggregationGR aggr = h.addAggregation(a, b);

                boolean reconnected = aggr.reconnectTarget(c);
                assertTrue("Reconnection should succeed", reconnected);

                AggregationGR newAggr = aggr.createWithNewEndpoints(a, c);
                assertEquals(c, newAggr.getClassB());
                assertTrue("Should preserve Aggregation type",
                                newAggr.getAssociation() instanceof Aggregation);
        }

        @Test
        public void testAggregationPreservesCompositionFlag() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // Create composition (strong aggregation)
                AggregationGR aggr = h.addAggregation(a, b);
                ((Aggregation) aggr.getAssociation()).setStrong(true);

                boolean reconnected = aggr.reconnectSource(c);
                assertTrue(reconnected);

                AggregationGR newAggr = aggr.createWithNewEndpoints(c, b);

                // Verify composition flag is preserved
                assertTrue("Should preserve composition (strong) flag",
                                ((Aggregation) newAggr.getAssociation()).isStrong());
        }

        // ========== Dependency Reconnection Tests ==========

        @Test
        public void testDependencyReconnectSource() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                DependencyGR dep = h.addDependency(a, b);

                boolean reconnected = dep.reconnectSource(c);
                assertTrue("Reconnection should succeed", reconnected);

                DependencyGR newDep = dep.createWithNewEndpoints(c, b);
                assertEquals(c, newDep.getClassA());
                assertEquals(b, newDep.getClassB());
        }

        @Test
        public void testDependencyReconnectTarget() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                DependencyGR dep = h.addDependency(a, b);

                boolean reconnected = dep.reconnectTarget(c);
                assertTrue("Reconnection should succeed", reconnected);

                DependencyGR newDep = dep.createWithNewEndpoints(a, c);
                assertEquals(c, newDep.getClassB());
        }

        @Test
        public void testDependencyCanReconnectToInterface() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                InterfaceGR i = h.addInterface("I");

                DependencyGR dep = h.addDependency(a, b);

                // Dependencies can now connect to any classifier (including interfaces)
                boolean canReconnect = dep.canReconnect(
                                EndpointType.TARGET, i);
                assertTrue("Dependency should allow interface target", canReconnect);

                boolean reconnected = dep.reconnectTarget(i);
                assertTrue("Reconnection to interface should succeed", reconnected);
        }

        // ========== Realization Reconnection Tests ==========

        @Test
        public void testRealizationReconnectSource() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                InterfaceGR i = h.addInterface("I");

                RealizationGR real = h.addRealization(a, i);

                // Reconnect source from A to B (both classes)
                boolean reconnected = real.reconnectSource(b);
                assertTrue("Reconnection should succeed", reconnected);

                RealizationGR newReal = real.createWithNewEndpoints(b, i);
                assertEquals(b, newReal.getTheClass());
                assertEquals(i, newReal.getTheInterface());
        }

        @Test
        public void testRealizationReconnectTarget() {
                ClassGR a = h.addClass("A");
                InterfaceGR i1 = h.addInterface("I1");
                InterfaceGR i2 = h.addInterface("I2");

                RealizationGR real = h.addRealization(a, i1);

                // Reconnect target from I1 to I2 (both interfaces)
                boolean reconnected = real.reconnectTarget(i2);
                assertTrue("Reconnection should succeed", reconnected);

                RealizationGR newReal = real.createWithNewEndpoints(a, i2);
                assertEquals(i2, newReal.getTheInterface());
        }

        @Test
        public void testRealizationCannotReconnectSourceToInterface() {
                ClassGR a = h.addClass("A");
                InterfaceGR i1 = h.addInterface("I1");
                InterfaceGR i2 = h.addInterface("I2");

                RealizationGR real = h.addRealization(a, i1);

                // Source must be a class, not an interface
                boolean canReconnect = real.canReconnect(
                                EndpointType.SOURCE, i2);
                assertFalse("Realization source must be a class", canReconnect);

                boolean reconnected = real.reconnectSource(i2);
                assertFalse("Reconnection should fail", reconnected);
        }

        @Test
        public void testRealizationCannotReconnectTargetToClass() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                InterfaceGR i = h.addInterface("I");

                RealizationGR real = h.addRealization(a, i);

                // Target must be an interface, not a class
                boolean canReconnect = real.canReconnect(
                                EndpointType.TARGET, b);
                assertFalse("Realization target must be an interface", canReconnect);

                boolean reconnected = real.reconnectTarget(b);
                assertFalse("Reconnection should fail", reconnected);
        }

        // ========== Generalization Reconnection Tests ==========

        @Test
        public void testGeneralizationReconnectSourceClassToClass() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                GeneralizationGR gen = h.addGeneralization(a, b);

                boolean reconnected = gen.reconnectSource(c);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(c, b);
                assertEquals(c, newGen.getBaseClass());
                assertEquals(b, newGen.getSuperClass());
        }

        @Test
        public void testGeneralizationReconnectTargetClassToClass() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                GeneralizationGR gen = h.addGeneralization(a, b);

                boolean reconnected = gen.reconnectTarget(c);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(a, c);
                assertEquals(c, newGen.getSuperClass());
        }

        @Test
        public void testGeneralizationReconnectSourceInterfaceToInterface() {
                InterfaceGR i1 = h.addInterface("I1");
                InterfaceGR i2 = h.addInterface("I2");
                InterfaceGR i3 = h.addInterface("I3");

                GeneralizationGR gen = h.addGeneralizationInterface(i1, i2);

                boolean reconnected = gen.reconnectSource(i3);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(i3, i2);
                assertEquals(i3, newGen.getBaseClass());
                assertEquals(i2, newGen.getSuperClass());
        }

        @Test
        public void testGeneralizationReconnectTargetInterfaceToInterface() {
                InterfaceGR i1 = h.addInterface("I1");
                InterfaceGR i2 = h.addInterface("I2");
                InterfaceGR i3 = h.addInterface("I3");

                GeneralizationGR gen = h.addGeneralizationInterface(i1, i2);

                boolean reconnected = gen.reconnectTarget(i3);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(i1, i3);
                assertEquals(i3, newGen.getSuperClass());
        }

        @Test
        public void testGeneralizationCannotMixClassAndInterface() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                InterfaceGR i = h.addInterface("I");

                GeneralizationGR gen = h.addGeneralization(a, b);

                // Cannot reconnect class generalization to interface
                boolean canReconnect = gen.canReconnect(
                                EndpointType.TARGET, i);
                assertFalse("Cannot mix class and interface in generalization", canReconnect);

                boolean reconnected = gen.reconnectTarget(i);
                assertFalse("Reconnection should fail", reconnected);
        }

        @Test
        public void testGeneralizationCannotCreateDirectSelfInheritance() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");

                GeneralizationGR gen = h.addGeneralization(a, b);

                // Direct self-inheritance should be prevented
                boolean canReconnect = gen.canReconnect(
                                EndpointType.TARGET, a);
                assertFalse("Direct self-inheritance should be prevented", canReconnect);

                boolean reconnected = gen.reconnectTarget(a);
                assertFalse("Reconnection should fail", reconnected);
        }

        @Test
        public void testGeneralizationReconnectSourceMaintainsInheritanceDirection() {
                // Create A extends B, then reconnect source to make C extends B
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // A extends B (A is child/base, B is parent/super)
                GeneralizationGR gen = h.addGeneralization(a, b);

                // Verify initial state
                assertEquals("Initial: A should be the child", a, gen.getBaseClass());
                assertEquals("Initial: B should be the parent", b, gen.getSuperClass());
                assertEquals("Initial domain: B should be superclass of A",
                                b.getClassifier(), gen.getGeneralization().getSuperClass());
                assertEquals("Initial domain: A should be baseclass (child) of B",
                                a.getClassifier(), gen.getGeneralization().getBaseClass());

                // Reconnect source (child) from A to C
                // Result should be: C extends B (C is child, B is parent)
                boolean reconnected = gen.reconnectSource(c);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(c, b);

                // Verify graphical model maintains correct direction
                assertEquals("After reconnect: C should be the child", c, newGen.getBaseClass());
                assertEquals("After reconnect: B should still be the parent", b, newGen.getSuperClass());

                // Verify domain model maintains correct inheritance direction
                assertEquals("Domain model: B should be superclass of C",
                                b.getClassifier(), newGen.getGeneralization().getSuperClass());
                assertEquals("Domain model: C should be baseclass (child) of B",
                                c.getClassifier(), newGen.getGeneralization().getBaseClass());
        }

        @Test
        public void testGeneralizationReconnectTargetMaintainsInheritanceDirection() {
                // Create A extends B, then reconnect target to make A extends C
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // A extends B (A is child/base, B is parent/super)
                GeneralizationGR gen = h.addGeneralization(a, b);

                // Verify initial state
                assertEquals("Initial: A should be the child", a, gen.getBaseClass());
                assertEquals("Initial: B should be the parent", b, gen.getSuperClass());

                // Reconnect target (parent) from B to C
                // Result should be: A extends C (A is child, C is parent)
                boolean reconnected = gen.reconnectTarget(c);
                assertTrue("Reconnection should succeed", reconnected);

                GeneralizationGR newGen = gen.createWithNewEndpoints(a, c);

                // Verify graphical model maintains correct direction
                assertEquals("After reconnect: A should still be the child", a, newGen.getBaseClass());
                assertEquals("After reconnect: C should be the new parent", c, newGen.getSuperClass());

                // Verify domain model maintains correct inheritance direction
                assertEquals("Domain model: C should be superclass of A",
                                c.getClassifier(), newGen.getGeneralization().getSuperClass());
                assertEquals("Domain model: A should be baseclass (child) of C",
                                a.getClassifier(), newGen.getGeneralization().getBaseClass());
        }

        // ========== Model Integration Tests ==========

        @Test
        public void testReconnectionUpdatesModel() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AssociationGR oldAssoc = h.addAssociation(a, b);

                int initialCount = model.getGraphicalElements().size();

                // Reconnect and replace in model
                oldAssoc.reconnectSource(c);
                AssociationGR newAssoc = oldAssoc.createWithNewEndpoints(c, b);

                model.removeGraphicalElement(oldAssoc);
                model.addGraphicalElement(newAssoc);

                // Model should still have same number of elements
                assertEquals(initialCount, model.getGraphicalElements().size());

                // New association should be in model
                assertTrue(model.getGraphicalElements().contains(newAssoc));
                assertFalse(model.getGraphicalElements().contains(oldAssoc));
        }

        // ========== Integration Tests Through SelectionController ==========

        @Test
        public void testGeneralizationReconnectSourceThroughController() {
                // Integration test: A extends B, drag source from A to C to make C extends B
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // A extends B (A is child/base, B is parent/super)
                GeneralizationGR gen = h.addGeneralization(a, b);

                // Verify initial state
                assertEquals("Initial: A should be the child", a, gen.getBaseClass());
                assertEquals("Initial: B should be the parent", b, gen.getSuperClass());

                // Get the selection controller
                SelectionController controller = internalFrame.getSelectionController();

                // Simulate dragging the source endpoint from A to C
                controller.draggingLink = gen;
                controller.draggingEndpoint = EndpointType.SOURCE;
                controller.potentialTarget = c;

                // Calculate center coordinates of target class C
                int targetX = c.getX() + c.getWidth() / 2;
                int targetY = c.getY() + c.getHeight() / 2;

                // Complete the drag operation through the controller
                int initialElementCount = model.getGraphicalElements().size();
                controller.completeEndpointDrag(targetX, targetY);

                // Verify the model still has the same number of elements
                assertEquals("Element count should remain the same after reconnection",
                                initialElementCount, model.getGraphicalElements().size());

                // Find the new generalization in the model
                GeneralizationGR newGen = null;
                for (Object element : model.getGraphicalElements()) {
                        if (element instanceof GeneralizationGR) {
                                GeneralizationGR g = (GeneralizationGR) element;
                                if (g.getBaseClass() == c && g.getSuperClass() == b) {
                                        newGen = g;
                                        break;
                                }
                        }
                }

                assertNotNull("New generalization C extends B should exist", newGen);

                // Verify graphical model correctness: C extends B
                assertEquals("C should be the child", c, newGen.getBaseClass());
                assertEquals("B should be the parent", b, newGen.getSuperClass());

                // Verify domain model correctness
                assertEquals("Domain model: B should be superclass of C",
                                b.getClassifier(), newGen.getGeneralization().getSuperClass());
                assertEquals("Domain model: C should be baseclass (child) of B",
                                c.getClassifier(), newGen.getGeneralization().getBaseClass());

                // Verify old link was removed
                assertFalse("Old generalization should be removed from model",
                                model.getGraphicalElements().contains(gen));
        }

        @Test
        public void testGeneralizationReconnectTargetThroughController() {
                // Integration test: A extends B, drag target from B to C to make A extends C
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // A extends B (A is child/base, B is parent/super)
                GeneralizationGR gen = h.addGeneralization(a, b);

                // Verify initial state
                assertEquals("Initial: A should be the child", a, gen.getBaseClass());
                assertEquals("Initial: B should be the parent", b, gen.getSuperClass());

                // Get the selection controller
                SelectionController controller = internalFrame.getSelectionController();

                // Simulate dragging the target endpoint from B to C
                controller.draggingLink = gen;
                controller.draggingEndpoint = EndpointType.TARGET;
                controller.potentialTarget = c;

                // Calculate center coordinates of target class C
                int targetX = c.getX() + c.getWidth() / 2;
                int targetY = c.getY() + c.getHeight() / 2;

                // Complete the drag operation through the controller
                int initialElementCount = model.getGraphicalElements().size();
                controller.completeEndpointDrag(targetX, targetY);

                // Verify the model still has the same number of elements
                assertEquals("Element count should remain the same after reconnection",
                                initialElementCount, model.getGraphicalElements().size());

                // Find the new generalization in the model
                GeneralizationGR newGen = null;
                for (Object element : model.getGraphicalElements()) {
                        if (element instanceof GeneralizationGR) {
                                GeneralizationGR g = (GeneralizationGR) element;
                                if (g.getBaseClass() == a && g.getSuperClass() == c) {
                                        newGen = g;
                                        break;
                                }
                        }
                }

                assertNotNull("New generalization A extends C should exist", newGen);

                // Verify graphical model correctness: A extends C
                assertEquals("A should be the child", a, newGen.getBaseClass());
                assertEquals("C should be the parent", c, newGen.getSuperClass());

                // Verify domain model correctness
                assertEquals("Domain model: C should be superclass of A",
                                c.getClassifier(), newGen.getGeneralization().getSuperClass());
                assertEquals("Domain model: A should be baseclass (child) of C",
                                a.getClassifier(), newGen.getGeneralization().getBaseClass());

                // Verify old link was removed
                assertFalse("Old generalization should be removed from model",
                                model.getGraphicalElements().contains(gen));
        }

        // ========== Undo/Redo Tests ==========

        @Test
        public void testAssociationReconnectWithUndoRedo() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                AssociationGR oldAssoc = h.addAssociation(a, b);

                // Verify initial state
                assertEquals("Initial: A should be source", a, oldAssoc.getClassA());
                assertEquals("Initial: B should be target", b, oldAssoc.getClassB());
                assertTrue("Initial: Old association should be in model",
                                model.getGraphicalElements().contains(oldAssoc));

                // Perform reconnection (simulating what SelectionController does)
                assertTrue("Reconnection should succeed", oldAssoc.reconnectSource(c));
                AssociationGR newAssoc = oldAssoc.createWithNewEndpoints(c, b);
                model.removeGraphicalElement(oldAssoc);
                model.addGraphicalElement(newAssoc);

                // Create and post the undo edit
                edu.city.studentuml.util.undoredo.ReconnectLinkEdit edit = new edu.city.studentuml.util.undoredo.ReconnectLinkEdit(
                                oldAssoc, newAssoc, model, EndpointType.SOURCE);
                internalFrame.getUndoSupport().postEdit(edit);

                // Verify reconnected state
                assertEquals("After reconnect: C should be source", c, newAssoc.getClassA());
                assertEquals("After reconnect: B should be target", b, newAssoc.getClassB());
                assertTrue("After reconnect: New association should be in model",
                                model.getGraphicalElements().contains(newAssoc));
                assertFalse("After reconnect: Old association should not be in model",
                                model.getGraphicalElements().contains(oldAssoc));

                // Undo the reconnection
                assertTrue("Should be able to undo", internalFrame.getUndoManager().canUndo());
                internalFrame.getUndoManager().undo();

                // Verify undone state
                assertTrue("After undo: Old association should be back in model",
                                model.getGraphicalElements().contains(oldAssoc));
                assertFalse("After undo: New association should not be in model",
                                model.getGraphicalElements().contains(newAssoc));
                assertEquals("After undo: A should be source", a, oldAssoc.getClassA());
                assertEquals("After undo: B should be target", b, oldAssoc.getClassB());

                // Redo the reconnection
                assertTrue("Should be able to redo", internalFrame.getUndoManager().canRedo());
                internalFrame.getUndoManager().redo();

                // Verify redone state
                assertFalse("After redo: Old association should not be in model",
                                model.getGraphicalElements().contains(oldAssoc));
                assertTrue("After redo: New association should be back in model",
                                model.getGraphicalElements().contains(newAssoc));
                assertEquals("After redo: C should be source", c, newAssoc.getClassA());
                assertEquals("After redo: B should be target", b, newAssoc.getClassB());
        }

        @Test
        public void testMultipleReconnectionsWithUndoRedo() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");
                ClassGR d = h.addClass("D");

                // Initial association A -> B
                AssociationGR assoc1 = h.addAssociation(a, b);

                // First reconnection: A -> B becomes C -> B
                assertTrue(assoc1.reconnectSource(c));
                AssociationGR assoc2 = assoc1.createWithNewEndpoints(c, b);
                model.removeGraphicalElement(assoc1);
                model.addGraphicalElement(assoc2);

                edu.city.studentuml.util.undoredo.ReconnectLinkEdit edit1 = new edu.city.studentuml.util.undoredo.ReconnectLinkEdit(
                                assoc1, assoc2, model, EndpointType.SOURCE);
                internalFrame.getUndoSupport().postEdit(edit1);

                // Second reconnection: C -> B becomes C -> D
                assertTrue(assoc2.reconnectTarget(d));
                AssociationGR assoc3 = assoc2.createWithNewEndpoints(c, d);
                model.removeGraphicalElement(assoc2);
                model.addGraphicalElement(assoc3);

                edu.city.studentuml.util.undoredo.ReconnectLinkEdit edit2 = new edu.city.studentuml.util.undoredo.ReconnectLinkEdit(
                                assoc2, assoc3, model, EndpointType.TARGET);
                internalFrame.getUndoSupport().postEdit(edit2);

                // Verify final state: C -> D
                assertEquals("Final: C should be source", c, assoc3.getClassA());
                assertEquals("Final: D should be target", d, assoc3.getClassB());
                assertTrue("Final: assoc3 should be in model",
                                model.getGraphicalElements().contains(assoc3));

                // Undo second reconnection (C -> D becomes C -> B)
                internalFrame.getUndoManager().undo();
                assertTrue("After undo 1: assoc2 should be in model",
                                model.getGraphicalElements().contains(assoc2));
                assertFalse("After undo 1: assoc3 should not be in model",
                                model.getGraphicalElements().contains(assoc3));
                assertEquals("After undo 1: C should be source", c, assoc2.getClassA());
                assertEquals("After undo 1: B should be target", b, assoc2.getClassB());

                // Undo first reconnection (C -> B becomes A -> B)
                internalFrame.getUndoManager().undo();
                assertTrue("After undo 2: assoc1 should be in model",
                                model.getGraphicalElements().contains(assoc1));
                assertFalse("After undo 2: assoc2 should not be in model",
                                model.getGraphicalElements().contains(assoc2));
                assertEquals("After undo 2: A should be source", a, assoc1.getClassA());
                assertEquals("After undo 2: B should be target", b, assoc1.getClassB());

                // Redo first reconnection (A -> B becomes C -> B)
                internalFrame.getUndoManager().redo();
                assertTrue("After redo 1: assoc2 should be in model",
                                model.getGraphicalElements().contains(assoc2));
                assertFalse("After redo 1: assoc1 should not be in model",
                                model.getGraphicalElements().contains(assoc1));

                // Redo second reconnection (C -> B becomes C -> D)
                internalFrame.getUndoManager().redo();
                assertTrue("After redo 2: assoc3 should be in model",
                                model.getGraphicalElements().contains(assoc3));
                assertFalse("After redo 2: assoc2 should not be in model",
                                model.getGraphicalElements().contains(assoc2));
                assertEquals("After redo 2: C should be source", c, assoc3.getClassA());
                assertEquals("After redo 2: D should be target", d, assoc3.getClassB());
        }

        @Test
        public void testReconnectPreservesPropertiesAfterUndoRedo() {
                ClassGR a = h.addClass("A");
                ClassGR b = h.addClass("B");
                ClassGR c = h.addClass("C");

                // Create association with properties
                AssociationGR oldAssoc = h.addAssociation(a, b);
                oldAssoc.getAssociation().getRoleA().setName("roleA");
                oldAssoc.getAssociation().getRoleB().setName("roleB");
                oldAssoc.getAssociation().getRoleA().setMultiplicity("1");
                oldAssoc.getAssociation().getRoleB().setMultiplicity("*");

                // Reconnect source
                assertTrue(oldAssoc.reconnectSource(c));
                AssociationGR newAssoc = oldAssoc.createWithNewEndpoints(c, b);
                model.removeGraphicalElement(oldAssoc);
                model.addGraphicalElement(newAssoc);

                // Verify properties are preserved after reconnection
                assertEquals("roleA", newAssoc.getAssociation().getRoleA().getName());
                assertEquals("roleB", newAssoc.getAssociation().getRoleB().getName());
                assertEquals("1", newAssoc.getAssociation().getRoleA().getMultiplicity());
                assertEquals("*", newAssoc.getAssociation().getRoleB().getMultiplicity());

                // Create undo edit
                edu.city.studentuml.util.undoredo.ReconnectLinkEdit edit = new edu.city.studentuml.util.undoredo.ReconnectLinkEdit(
                                oldAssoc, newAssoc, model, EndpointType.SOURCE);
                internalFrame.getUndoSupport().postEdit(edit);

                // Undo
                internalFrame.getUndoManager().undo();

                // Verify properties are preserved after undo
                assertEquals("roleA", oldAssoc.getAssociation().getRoleA().getName());
                assertEquals("roleB", oldAssoc.getAssociation().getRoleB().getName());
                assertEquals("1", oldAssoc.getAssociation().getRoleA().getMultiplicity());
                assertEquals("*", oldAssoc.getAssociation().getRoleB().getMultiplicity());

                // Redo
                internalFrame.getUndoManager().redo();

                // Verify properties are preserved after redo
                assertEquals("roleA", newAssoc.getAssociation().getRoleA().getName());
                assertEquals("roleB", newAssoc.getAssociation().getRoleB().getName());
                assertEquals("1", newAssoc.getAssociation().getRoleA().getMultiplicity());
                assertEquals("*", newAssoc.getAssociation().getRoleB().getMultiplicity());
        }
}
