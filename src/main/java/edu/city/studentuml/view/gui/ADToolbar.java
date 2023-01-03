package edu.city.studentuml.view.gui;

public class ADToolbar extends AbsractToolbar {
    public ADToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("initial.gif", "InitialNodeGR", "Initial Node", this);
        addToolBarButton("final.gif", "ActivityFinalNodeGR", "Activity Final Node", this);
        addToolBarButton("flowFinal.gif", "FlowFinalNodeGR", "Flow Final Node", this);
        addToolBarButton("action.gif", "ActionNodeGR", "Action Node", this);
        addToolBarButton("objectNode.gif", "ObjectNodeGR", "Object Node", this);
        addToolBarButton("controlFlow.gif", "ControlFlowGR", "Control Flow", this);
        addToolBarButton("objectFlow.gif", "ObjectFlowGR", "Object Flow", this);
        addToolBarButton("decision.gif", "DecisionNodeGR", "Decision Node", this);
        addToolBarButton("merge.gif", "MergeNodeGR", "Merge Node", this);
        addToolBarButton("fork.gif", "ForkNodeGR", "Fork Node", this);
        addToolBarButton("join.gif", "JoinNodeGR", "Join Node", this);
        // addToolBarButton("swimlanes.gif", "SwimlanesGR", "Swimlanes", this)
        addToolBarButton("activityDiagram.gif", "ActivityNodeGR", "Activity Node", this);
    }
}
