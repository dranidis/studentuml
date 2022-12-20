package edu.city.studentuml.view.gui;

public class SDToolbar extends AbsractToolbar {
    public SDToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("object.gif", "SDObjectGR", "Object", this);
        addToolBarButton("actor.gif", "ActorInstanceGR", "Actor", this);
        addToolBarButton("multiobject.gif", "MultiObjectGR", "Multiobject", this);
        addToolBarButton("call_message.gif", "CallMessageGR", "Call Message", this);
        addToolBarButton("return_message.gif", "ReturnMessageGR", "Return Message", this);
        addToolBarButton("create_message.gif", "CreateMessageGR", "Create Message", this);
        addToolBarButton("destroy_message.gif", "DestroyMessageGR", "Destroy Message", this);
    }
}
