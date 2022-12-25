package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.List;
import java.util.stream.Collectors;

public class DCDModel extends AbstractCDModel {

    public DCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    // override the superclass method addGraphicalElement to handle different
    // classes
    @Override
    public void addGraphicalElement(GraphicalElement element) {
        SystemWideObjectNamePool.getInstance().loading();
        if (element instanceof ClassGR) {
            addClass((ClassGR) element);
        } else if (element instanceof InterfaceGR) {
            addInterface((InterfaceGR) element);
        } else if (element instanceof AssociationGR) {
            addAssociation((AssociationGR) element);
        } else if (element instanceof AssociationClassGR) {
            addAssociationClass((AssociationClassGR) element);
        } else if (element instanceof DependencyGR) {
            addDependency((DependencyGR) element);
        } else if (element instanceof AggregationGR) {
            addAggregation((AggregationGR) element);
        } else if (element instanceof GeneralizationGR) {
            addGeneralization((GeneralizationGR) element);
        } else if (element instanceof RealizationGR) {
            addRealization((RealizationGR) element);
        } else if (element instanceof UMLNoteGR) {
            super.addGraphicalElement(element);
        }

        SystemWideObjectNamePool.getInstance().done();
    }

    // add a new diagram class
    public void addClass(ClassGR c) {

        // add the class to the project repository first and then to the diagram
        repository.addClass(c.getDesignClass());
        super.addGraphicalElement(c);
    }

    // add a new diagram interface
    public void addInterface(InterfaceGR i) {

        // add the interface to the project repository first and then to the diagram
        repository.addInterface(i.getInterface());
        super.addGraphicalElement(i);
    }

    // add a new diagram dependency
    public void addDependency(DependencyGR d) {
        Dependency dependency = d.getDependency();

        // try to add the dependency to the repository first, if it doesn't exist
        repository.addDependency(dependency);

        super.addGraphicalElement(d);
    }

    // add a new diagram realization
    public void addRealization(RealizationGR r) {
        Realization realization = r.getRealization();

        // try to add the realization to the repository first, if it doesn't exist
        repository.addRealization(realization);

        super.addGraphicalElement(r);
    }

    // override superclass method removeGraphicalElement()
    @Override
    public void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof ClassGR) {
            removeClass((ClassGR) e);
        } else if (e instanceof InterfaceGR) {
            removeInterface((InterfaceGR) e);
        } else if (e instanceof AssociationGR) {
            // also aggregations are removed
            removeAssociation((AssociationGR) e);
        } else if (e instanceof AssociationClassGR) {
            removeAssociationClass((AssociationClassGR) e);
        } else if (e instanceof DependencyGR) {
            removeDependency((DependencyGR) e);
        } else if (e instanceof GeneralizationGR) {
            removeGeneralization((GeneralizationGR) e);
        } else if (e instanceof RealizationGR) {
            removeRealization((RealizationGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    // since more than one graphical class or interface can refer
    // to the same domain representation, just remove the graphical representation
    // from the diagram, and not the domain representation from the repository
    public void removeClass(ClassGR c) {
        getClassGRDependencyGRs(c).forEach(e -> removeDependency((DependencyGR) e));
        getClassGRAssociationGRs(c).forEach(e -> removeAssociation((AssociationGR) e));
        getClassGRAssociationClassGRs(c).forEach(e -> removeAssociationClass((AssociationClassGR) e));
        getClassGRRealizationGRs(c).forEach(e -> removeRealization((RealizationGR) e));
        getClassGRGeneralizationGRs(c).forEach(e -> removeGeneralization((GeneralizationGR) e));

        if (!umlProject.isClassReferenced(c, c.getDesignClass())) {
            c.getDesignClass().clear();
            repository.removeClass(c.getDesignClass());
        }
        super.removeGraphicalElement(c);
    }

    public void removeInterface(InterfaceGR i) {
        getInterfaceGRRealizationGRs(i).forEach(e -> removeRealization((RealizationGR) e));
        getInterfaceGRAssociationGRs(i).forEach(e -> removeAssociation((AssociationGR) e));
        getInterfaceGRGeneralizationGRs(i).forEach(e -> removeGeneralization((GeneralizationGR) e));

        i.getInterface().clear();
        repository.removeInterface(i.getInterface());
        super.removeGraphicalElement(i);
    }

    public List<GraphicalElement> getClassGRDependencyGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof DependencyGR
                        && (((DependencyGR) grElement).getDependency().getFrom() == c.getDesignClass()
                                || ((DependencyGR) grElement).getDependency().getTo() == c.getDesignClass())))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRAssociationGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationGR
                        && (((AssociationGR) grElement).getAssociation().getClassB() == c.getDesignClass()
                                || ((AssociationGR) grElement).getAssociation().getClassA() == c.getDesignClass())))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRAssociationClassGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationClassGR
                        && (((AssociationClassGR) grElement).getAssociationClass().getClassB() == c.getDesignClass()
                                || ((AssociationClassGR) grElement).getAssociationClass().getClassA() == c.getDesignClass())))
                .collect(Collectors.toList());
    }    

    public List<GraphicalElement> getClassGRRealizationGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof RealizationGR
                        && ((RealizationGR) grElement).getRealization().getTheClass() == c.getDesignClass()))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRGeneralizationGRs(ClassGR c) {
        return graphicalElements.stream().filter(grElement -> grElement instanceof GeneralizationGR
                && ((((GeneralizationGR) grElement).getClassifierA().getClassifier() == c.getDesignClass())
                        || ((GeneralizationGR) grElement).getClassifierB().getClassifier() == c.getDesignClass()))
                .collect(Collectors.toList());
    }    


    public List<GraphicalElement> getInterfaceGRRealizationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof RealizationGR
                        && ((RealizationGR) grElement).getRealization().getTheInterface() == interf.getInterface()))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRAssociationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationGR
                        && ((AssociationGR) grElement).getAssociation().getClassB() == interf.getInterface()))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRGeneralizationGRs(InterfaceGR interf) {
        return graphicalElements.stream().filter(grElement -> grElement instanceof GeneralizationGR
                && ((((GeneralizationGR) grElement).getClassifierA().getClassifier() == interf.getInterface())
                        || ((GeneralizationGR) grElement).getClassifierB().getClassifier() == interf.getInterface()))
                .collect(Collectors.toList());
    }    

    public void removeDependency(DependencyGR d) {
        repository.removeDependency(d.getDependency());
        super.removeGraphicalElement(d);
    }

    public void removeRealization(RealizationGR r) {
        repository.removeRealization(r.getRealization());
        super.removeGraphicalElement(r);
    }

}
