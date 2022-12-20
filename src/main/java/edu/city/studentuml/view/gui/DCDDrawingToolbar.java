package edu.city.studentuml.view.gui;

public class DCDDrawingToolbar extends AbsractDiagramDrawingToolbar {
    public DCDDrawingToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("class.gif", "ClassGR", "Class", this);
        addToolBarButton("interface.gif", "InterfaceGR", "Interface", this);
        addToolBarButton("association.gif", "AssociationGR", "Association", this);
        addToolBarButton("associationClass.gif", "AssociationClassGR", "Association Class", this);
        addToolBarButton("aggregation.gif", "AggregationGR", "Aggregation", this);
        addToolBarButton("composition.gif", "CompositionGR", "Composition", this);
        addToolBarButton("dependency.gif", "DependencyGR", "Dependency", this);
        addToolBarButton("generalization.gif", "GeneralizationGR", "Generalization", this);
        addToolBarButton("realization.gif", "RealizationGR", "Realization", this);
    }
}
