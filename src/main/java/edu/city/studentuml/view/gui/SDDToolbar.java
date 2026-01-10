package edu.city.studentuml.view.gui;

public class SDDToolbar extends AbsractToolbar {
    public SDDToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("object.gif", "SystemInstanceGR", "System", this);
        addToolBarButton("actor.gif", "ActorInstanceGR", "Actor", this);
        addToolBarButton("call_message.gif", "SystemOperationGR", "System Operation", this);
        addToolBarButton("return_message.gif", "ReturnMessageGR", "Return Message", this);
        addToolBarButton("fragment.gif", "CombinedFragmentGR", "Combined Fragment", this);
    }
}
