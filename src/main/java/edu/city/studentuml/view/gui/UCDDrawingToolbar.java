package edu.city.studentuml.view.gui;

public class UCDDrawingToolbar extends AbsractDiagramDrawingToolbar {
    public UCDDrawingToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("actor.gif", "ActorGR", "Actor", this);
        addToolBarButton("useCase.gif", "UseCaseGR", "Use Case", this);
        addToolBarButton("system.gif", "SystemBoundaryGR", "System Boundary", this);
        addToolBarButton("association.gif", "AssociationGR", "Association", this);
        addToolBarButton("include.gif", "IncludeGR", "Include", this);
        addToolBarButton("extend.gif", "ExtendGR", "Extend", this);
        addToolBarButton("generalization.gif", "GeneralizationGR", "Generalization", this);
    }
}
