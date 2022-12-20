package edu.city.studentuml.view.gui;

public class CCDDrawingToolbar extends AbsractDiagramDrawingToolbar {
    public CCDDrawingToolbar(DiagramInternalFrame parentFr) {
        super(parentFr);
    }

    protected void addDiagramButtons() {
        addToolBarButton("class.gif", "ConceptualClassGR", "Concept", this);
        addToolBarButton("association.gif", "AssociationGR", "Association", this);
        addToolBarButton("associationClass.gif", "AssociationClassGR", "Association Class", this);
        addToolBarButton("aggregation.gif", "AggregationGR", "Aggregation", this);
        addToolBarButton("composition.gif", "CompositionGR", "Composition", this);
        addToolBarButton("generalization.gif", "GeneralizationGR", "Generalization", this);
    }
}
