package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;

public class DCDModel extends AbstractCDModel {

    public DCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

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

    private void addClass(ClassGR c) {

        repository.addClass(c.getDesignClass());
        super.addGraphicalElement(c);
    }

    private void addInterface(InterfaceGR i) {

        repository.addInterface(i.getInterface());
        super.addGraphicalElement(i);
    }

    private void addDependency(DependencyGR d) {
        // Only add to repository if not already present (avoid duplicates when pasting)
        if (!repository.getDependencies().contains(d.getDependency())) {
            repository.addDependency(d.getDependency());
        }
        super.addGraphicalElement(d);
    }

    /**
     * Adds the realization at the diagram model only if there is no realization
     * graphical element from and to the same class and interface. If the underlying
     * domain realization already exists in the repository (from another diagram) it
     * links the graphical element to the existing realization.
     * 
     * @param g
     */
    private void addRealization(RealizationGR r) {

        Optional<GraphicalElement> alreadyExistingGen = getGraphicalElements().stream().filter(e -> {
            if (e instanceof RealizationGR) {
                RealizationGR realizationGR = (RealizationGR) e;
                if (realizationGR.getTheClass() == r.getTheClass()
                        && realizationGR.getTheInterface() == r.getTheInterface()) {
                    return true;
                }
            }
            return false;
        }).findFirst();

        if (alreadyExistingGen.isPresent()) {
            return;
        }

        Realization realization = r.getRealization();

        // if get fails there is an existing generalization
        if (repository.addRealization(realization) == false) {
            // link to the already existing generalization
            r.setRealization(repository.getRealization(realization.getTheClass(), realization.getTheInterface()));
        }

        super.addGraphicalElement(r);
    }

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
    private void removeClass(ClassGR c) {
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

    private void removeInterface(InterfaceGR i) {
        getInterfaceGRRealizationGRs(i).forEach(e -> removeRealization((RealizationGR) e));
        getInterfaceGRAssociationGRs(i).forEach(e -> removeAssociation((AssociationGR) e));
        getInterfaceGRGeneralizationGRs(i).forEach(e -> removeGeneralization((GeneralizationGR) e));

        i.getInterface().clear();
        repository.removeInterface(i.getInterface());
        super.removeGraphicalElement(i);
    }

    public List<GraphicalElement> getClassGRDependencyGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof DependencyGR
                        // Check graphical endpoints, not domain classifiers
                        && (((DependencyGR) grElement).getClassA() == c
                                || ((DependencyGR) grElement).getClassB() == c))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRAssociationGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof AssociationGR
                        // Check graphical endpoints, not domain classifiers
                        // This ensures we only remove associations graphically connected to this specific ClassGR
                        && (((AssociationGR) grElement).getA() == c
                                || ((AssociationGR) grElement).getB() == c))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRAssociationClassGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof AssociationClassGR
                        // Check graphical endpoints, not domain classifiers
                        && (((AssociationClassGR) grElement).getClassB() == c
                                || ((AssociationClassGR) grElement).getClassA() == c))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRRealizationGRs(ClassGR c) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof RealizationGR
                        // Check graphical endpoints, not domain classifiers
                        && ((RealizationGR) grElement).getTheClass() == c)
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getClassGRGeneralizationGRs(ClassGR c) {
        return graphicalElements.stream().filter(grElement -> grElement instanceof GeneralizationGR
                // Check graphical endpoints, not domain classifiers
                && (((GeneralizationGR) grElement).getClassifierA() == c
                        || ((GeneralizationGR) grElement).getClassifierB() == c))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRRealizationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof RealizationGR
                        // Check graphical endpoints, not domain classifiers
                        && ((RealizationGR) grElement).getTheInterface() == interf)
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRAssociationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof AssociationGR
                        // Check graphical endpoints, not domain classifiers
                        && ((AssociationGR) grElement).getB() == interf)
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRGeneralizationGRs(InterfaceGR interf) {
        return graphicalElements.stream().filter(grElement -> grElement instanceof GeneralizationGR
                // Check graphical endpoints, not domain classifiers
                && (((GeneralizationGR) grElement).getClassifierA() == interf
                        || ((GeneralizationGR) grElement).getClassifierB() == interf))
                .collect(Collectors.toList());
    }

    private void removeDependency(DependencyGR d) {
        long count = graphicalElements.stream()
                .filter(e -> e instanceof DependencyGR && e != d)
                .filter(e -> ((DependencyGR) e).getDependency() == d.getDependency())
                .count();

        if (count == 0 && repository.getDependencies().contains(d.getDependency())) {
            repository.removeDependency(d.getDependency());
        }
        super.removeGraphicalElement(d);
    }

    private void removeRealization(RealizationGR r) {
        long count = graphicalElements.stream()
                .filter(e -> e instanceof RealizationGR && e != r)
                .filter(e -> ((RealizationGR) e).getRealization() == r.getRealization())
                .count();

        if (count == 0 && repository.getRealizations().contains(r.getRealization())) {
            repository.removeRealization(r.getRealization());
        }
        super.removeGraphicalElement(r);
    }

    /**
     * Called by REFLECTION Repair actions from consistency check
     * 
     * @param dc
     */
    public void addC(DesignClass dc) {
        SystemWideObjectNamePool.getInstance().loading();
        Random rn = new Random();

        ClassGR c = new ClassGR(dc, new Point(rn.nextInt(100), rn.nextInt(100)));
        // add the class to the project repository first and then to the diagram
        repository.addClass(c.getDesignClass());
        super.addGraphicalElement(c);
        SystemWideObjectNamePool.getInstance().done();
    }

    /**
     * Called by REFLECTION Repair actions from consistency check
     */
    public void addAssoc(ClassifierGR classA, ClassifierGR classB) {
        SystemWideObjectNamePool.getInstance().loading();

        Association association = new Association(classA.getClassifier(), classB.getClassifier());
        association.setDirection(Association.AB);
        AssociationGR associationGR = new AssociationGR(classA, classB, association);

        addAssociation(associationGR);

        SystemWideObjectNamePool.getInstance().done();
    }

    /**
     * Called by REFLECTION Repair actions from consistency check
     */
    public void addDep(ClassifierGR classA, ClassifierGR classB) {
        SystemWideObjectNamePool.getInstance().loading();

        Dependency dependency = new Dependency(classA.getClassifier(), classB.getClassifier());
        DependencyGR dependencyGR = new DependencyGR(classA, classB, dependency);

        addDependency(dependencyGR);

        SystemWideObjectNamePool.getInstance().done();
    }

    /**
     * Called by REFLECTION Repair actions from consistency check
     */
    public void addAggreg(ClassGR whole, ClassGR part) {
        SystemWideObjectNamePool.getInstance().loading();

        // the false flag indicates that the aggregation is not strong (composition)
        Aggregation aggregation = new Aggregation(whole.getDesignClass(), part.getDesignClass(), false);
        aggregation.setDirection(Association.AB);
        AggregationGR aggregationGR = new AggregationGR(whole, part, aggregation);

        addAggregation(aggregationGR);

        SystemWideObjectNamePool.getInstance().done();
    }

}
