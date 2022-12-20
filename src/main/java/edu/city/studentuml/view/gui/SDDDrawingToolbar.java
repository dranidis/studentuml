package edu.city.studentuml.view.gui;

public class SDDDrawingToolbar extends AbsractDiagramDrawingToolbar {
    public SDDDrawingToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("object.gif", "SystemInstanceGR", "System", this);
        addToolBarButton("actor.gif", "ActorInstanceGR", "Actor", this);
        addToolBarButton("call_message.gif", "SystemOperationGR", "System Operation", this);
        addToolBarButton("return_message.gif", "ReturnMessageGR", "Return Message", this);
    }
}
