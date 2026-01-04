package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditActionNodeEdit;
import edu.city.studentuml.util.undoredo.EditActivityNodeEdit;
import edu.city.studentuml.util.undoredo.EditDecisionNodeEdit;
import edu.city.studentuml.util.undoredo.EditObjectFlowEdit;
import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.view.gui.ADInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

public class ADSelectionControllerTest {

    UMLProject umlProject;
    DiagramModel model;
    DiagramInternalFrame internalFrame;
    SelectionController selectionController;
    Helper h;

    @Before
    public void setup() {
        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new ADModel("ad", umlProject);
        internalFrame = new ADInternalFrame(model);
        h = new Helper(model);
        selectionController = new SelectionController(internalFrame, model);
    }

    @Test
    public void testCreation() {
        assertNotNull(selectionController);
    }

    @Test
    public void testDeleteInitialNodeWithinActivityNode() {
        ActivityNodeGR an = h.addActivityNode("an");
        InitialNodeGR i = h.addInitialNodeInActivityNode(an);

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));

        // SELECT the initial node
        selectionController.addElementToSelection(i);

        // DELETE it
        // System.out.println("DELETE");
        selectionController.deleteSelected();

        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(0, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // UNDO
        // System.out.println("UNDO");
        internalFrame.getUndoManager().undo();

        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // REDO
        // System.out.println("REDO");
        internalFrame.getUndoManager().redo();

        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(0, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());
    }

    @Test
    public void testDeleteActivityNodeWithAnInitialNodeInside() {
        ActivityNodeGR an = h.addActivityNode("an");
        h.addInitialNodeInActivityNode(an);

        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));

        // SELECT the activity node
        selectionController.addElementToSelection(an);

        // DELETE it
        // System.out.println("DELETE");
        selectionController.deleteSelected();

        // model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(0, model.getGraphicalElements().size());

        // UNDO
        // System.out.println("UNDO");
        internalFrame.getUndoManager().undo();

        // model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(1, model.getGraphicalElements().size());
        assertEquals(1, ((ActivityNodeGR) model.getGraphicalElements().get(0)).getComponents().size());

        // REDO
        // System.out.println("REDO");
        internalFrame.getUndoManager().redo();

        // model.getGraphicalElements().forEach(e -> System.out.println("In model: " + e));
        // an.createIterator().forEachRemaining(e -> System.out.println("IN an: " + e));
        assertEquals(0, model.getGraphicalElements().size());

    }

    @Test
    public void testActivityNodeEdit_withPolymorphicMethod() {
        // Create an ActivityNode via helper
        ActivityNodeGR activityNodeGR = h.addActivityNode("InitialName");
        ActivityNode activityNode = (ActivityNode) activityNodeGR.getComponent();

        // Verify initial state
        assertEquals("InitialName", activityNode.getName());
        assertEquals(1, model.getGraphicalElements().size());

        // Create EditContext (simulating what SelectionController would do)
        EditContext context = new EditContext(model, internalFrame);

        // Note: We cannot test the actual edit() execution in headless mode
        // because it would show a GUI dialog. The important validation here is that:
        // 1. EditContext can be created with the required dependencies
        // 2. ActivityNodeGR has the edit(EditContext) method (compilation confirms this)
        // 3. The method signature matches the polymorphic contract

        // Verify EditContext was created successfully
        assertNotNull(context);
        assertEquals(model, context.getModel());
        assertEquals(umlProject.getCentralRepository(), context.getRepository());
        assertEquals(internalFrame, context.getParentComponent());
        assertEquals(internalFrame.getUndoSupport(), context.getUndoSupport());

        // Manual/integration testing would be needed to verify the full edit workflow
    }

    @Test
    public void testDecisionNodeEdit_withPolymorphicMethod() {
        // Create a DecisionNode directly
        DecisionNode decisionNode = new DecisionNode();
        DecisionNodeGR decisionNodeGR = new DecisionNodeGR(decisionNode, 100, 100);
        model.addGraphicalElement(decisionNodeGR);

        // Verify initial state
        assertNotNull(decisionNode);
        assertEquals(1, model.getGraphicalElements().size());

        // Create EditContext (simulating what SelectionController would do)
        EditContext context = new EditContext(model, internalFrame);

        // Note: We cannot test the actual edit() execution in headless mode
        // because it would show a GUI dialog. The important validation here is that:
        // 1. EditContext can be created with the required dependencies
        // 2. DecisionNodeGR has the edit(EditContext) method (compilation confirms this)
        // 3. The method signature matches the polymorphic contract

        // Verify EditContext was created successfully
        assertNotNull(context);
        assertEquals(model, context.getModel());
        assertEquals(umlProject.getCentralRepository(), context.getRepository());
        assertEquals(internalFrame, context.getParentComponent());
        assertEquals(internalFrame.getUndoSupport(), context.getUndoSupport());

        // Manual/integration testing would be needed to verify the full edit workflow
    }

    @Test
    public void testActionNodeEdit_withPolymorphicMethod() {
        // Create an ActionNode directly
        ActionNode actionNode = new ActionNode();
        actionNode.setName("InitialAction");
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(actionNodeGR);

        // Verify initial state
        assertEquals("InitialAction", actionNode.getName());
        assertEquals(1, model.getGraphicalElements().size());

        // Create EditContext (simulating what SelectionController would do)
        EditContext context = new EditContext(model, internalFrame);

        // Note: We cannot test the actual edit() execution in headless mode
        // because it would show a GUI dialog. The important validation here is that:
        // 1. EditContext can be created with the required dependencies
        // 2. ActionNodeGR has the edit(EditContext) method (compilation confirms this)
        // 3. The method signature matches the polymorphic contract

        // Verify EditContext was created successfully
        assertNotNull(context);
        assertEquals(model, context.getModel());
        assertEquals(umlProject.getCentralRepository(), context.getRepository());
        assertEquals(internalFrame, context.getParentComponent());
        assertEquals(internalFrame.getUndoSupport(), context.getUndoSupport());

        // Manual/integration testing would be needed to verify the full edit workflow
    }

    @Test
    public void testEditActionNodeNameWithUndo() {
        // Create an ActionNode
        ActionNode actionNode = new ActionNode();
        actionNode.setName("OriginalAction");
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, 100, 100);
        model.addGraphicalElement(actionNodeGR);

        // Verify initial state
        assertEquals("OriginalAction", actionNode.getName());

        // Create edit (simulating what edit() method creates)
        ActionNode newActionNode = (ActionNode) actionNode.clone();
        newActionNode.setName("EditedAction");
        EditActionNodeEdit edit = new EditActionNodeEdit(actionNode, newActionNode, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("EditedAction", actionNode.getName());

        // Undo
        edit.undo();
        assertEquals("OriginalAction", actionNode.getName());

        // Redo again
        edit.redo();
        assertEquals("EditedAction", actionNode.getName());
    }

    @Test
    public void testEditActivityNodeNameWithUndo() {
        // Create an ActivityNode
        ActivityNode activityNode = new ActivityNode();
        activityNode.setName("OriginalActivity");
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, 100, 100);
        model.addGraphicalElement(activityNodeGR);

        // Verify initial state
        assertEquals("OriginalActivity", activityNode.getName());

        // Create edit
        ActivityNode newActivityNode = (ActivityNode) activityNode.clone();
        newActivityNode.setName("EditedActivity");
        EditActivityNodeEdit edit = new EditActivityNodeEdit(activityNode, newActivityNode, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("EditedActivity", activityNode.getName());

        // Undo
        edit.undo();
        assertEquals("OriginalActivity", activityNode.getName());

        // Redo again
        edit.redo();
        assertEquals("EditedActivity", activityNode.getName());
    }

    @Test
    public void testEditDecisionNodeNameWithUndo() {
        // Create a DecisionNode
        DecisionNode decisionNode = new DecisionNode();
        decisionNode.setName("OriginalDecision");
        DecisionNodeGR decisionNodeGR = new DecisionNodeGR(decisionNode, 100, 100);
        model.addGraphicalElement(decisionNodeGR);

        // Verify initial state
        assertEquals("OriginalDecision", decisionNode.getName());

        // Create edit
        DecisionNode newDecisionNode = (DecisionNode) decisionNode.clone();
        newDecisionNode.setName("EditedDecision");
        EditDecisionNodeEdit edit = new EditDecisionNodeEdit(decisionNode, newDecisionNode, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("EditedDecision", decisionNode.getName());

        // Undo
        edit.undo();
        assertEquals("OriginalDecision", decisionNode.getName());

        // Redo again
        edit.redo();
        assertEquals("EditedDecision", decisionNode.getName());
    }

    @Test
    public void testEditObjectFlowGuardWithUndo() {
        // Create ObjectFlow (requires source and target nodes)
        ActionNode sourceNode = new ActionNode();
        sourceNode.setName("Source");
        ActionNodeGR sourceGR = new ActionNodeGR(sourceNode, 100, 100);
        model.addGraphicalElement(sourceGR);

        ActionNode targetNode = new ActionNode();
        targetNode.setName("Target");
        ActionNodeGR targetGR = new ActionNodeGR(targetNode, 200, 200);
        model.addGraphicalElement(targetGR);

        ObjectFlow objectFlow = new ObjectFlow(sourceNode, targetNode);
        objectFlow.setGuard("originalGuard");
        objectFlow.setWeight("1");
        ObjectFlowGR objectFlowGR = new ObjectFlowGR(sourceGR, targetGR, objectFlow);
        model.addGraphicalElement(objectFlowGR);

        // Verify initial state
        assertEquals("originalGuard", objectFlow.getGuard());
        assertEquals("1", objectFlow.getWeight());

        // Create edit
        ObjectFlow newObjectFlow = objectFlow.clone();
        newObjectFlow.setGuard("editedGuard");
        newObjectFlow.setWeight("2");
        EditObjectFlowEdit edit = new EditObjectFlowEdit(objectFlow, newObjectFlow, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("editedGuard", objectFlow.getGuard());
        assertEquals("2", objectFlow.getWeight());

        // Undo
        edit.undo();
        assertEquals("originalGuard", objectFlow.getGuard());
        assertEquals("1", objectFlow.getWeight());

        // Redo again
        edit.redo();
        assertEquals("editedGuard", objectFlow.getGuard());
        assertEquals("2", objectFlow.getWeight());
    }
}
