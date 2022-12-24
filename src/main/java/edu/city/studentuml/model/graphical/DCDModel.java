package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class DCDModel extends DiagramModel {

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

    // add a new diagram association
    public void addAssociation(AssociationGR a) {

        // add the association to the project repository first and then to the diagram
        repository.addAssociation(a.getAssociation());
        super.addGraphicalElement(a);
    }

    public void addAssociationClass(AssociationClassGR a) {
        repository.addAssociationClass(a.getAssociationClass());
        super.addGraphicalElement(a);
    }

    // add a new diagram dependency
    public void addDependency(DependencyGR d) {
        Dependency dependency = d.getDependency();

        // try to add the dependency to the repository first, if it doesn't exist
        repository.addDependency(dependency);

        super.addGraphicalElement(d);
    }

    // add a new diagram aggregation
    public void addAggregation(AggregationGR a) {

        // add the aggregation to the project repository first and then to the diagram
        repository.addAggregation(a.getAggregation());

        super.addGraphicalElement(a);
    }

    // add a new diagram generalization
    public void addGeneralization(GeneralizationGR g) {
        Generalization generalization = g.getGeneralization();

        // try to add the generalization to the repository first, if it doesn't exist
        repository.addGeneralization(generalization);
        super.addGraphicalElement(g);
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
            removeAssociation((AssociationGR) e);
        } else if (e instanceof AssociationClassGR) {
            removeAssociationClass((AssociationClassGR) e);
        } else if (e instanceof DependencyGR) {
            removeDependency((DependencyGR) e);
        } else if (e instanceof AggregationGR) {
            removeAggregation((AggregationGR) e);
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
        Vector aggregations, associations, associationClasses, dependencies, generalizations, realizations;
        Aggregation agr;
        Association ass;
        DesignAssociationClass associationClass;
        Dependency dep;
        Generalization gen;
        Realization rea;
        GraphicalElement grElement;
        Iterator iterator, iterGE;

        aggregations = repository.getAggregations();
        iterator = aggregations.iterator();

        while (iterator.hasNext()) {
            agr = (Aggregation) iterator.next();

            if ((agr.getPart() == c.getDesignClass()) || (agr.getWhole() == c.getDesignClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AggregationGR) {
                        if (((AggregationGR) grElement).getAggregation() == agr) {
                            removeAggregation((AggregationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = aggregations.iterator();
                        }
                    }
                }
            }
        }

        associations = repository.getAssociations();
        iterator = associations.iterator();

        while (iterator.hasNext()) {
            ass = (Association) iterator.next();

            if ((ass.getClassA() == c.getDesignClass()) || (ass.getClassB() == c.getDesignClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationGR) {
                        if (((AssociationGR) grElement).getAssociation() == ass) {
                            removeAssociation((AssociationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = associations.iterator();
                        }
                    }
                }
            }
        }

        associationClasses = repository.getDesignAssociationClasses();
        iterator = associationClasses.iterator();
        while (iterator.hasNext()) {
            associationClass = (DesignAssociationClass) iterator.next();

            if ((associationClass.getClassA() == c.getDesignClass())
                    || (associationClass.getClassB() == c.getDesignClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationClassGR) {
                        if (((AssociationClassGR) grElement).getAssociationClass() == associationClass) {
                            removeAssociationClass((AssociationClassGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = associationClasses.iterator();
                        }
                    }
                }
            }
        }

        dependencies = repository.getDependencies();
        iterator = dependencies.iterator();

        while (iterator.hasNext()) {
            dep = (Dependency) iterator.next();

            if ((dep.getFrom() == c.getDesignClass()) || (dep.getTo() == c.getDesignClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof DependencyGR) {
                        if (((DependencyGR) grElement).getDependency() == dep) {
                            removeDependency((DependencyGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = dependencies.iterator();
                        }
                    }
                }
            }
        }

        generalizations = repository.getGeneralizations();
        iterator = generalizations.iterator();

        while (iterator.hasNext()) {
            gen = (Generalization) iterator.next();

            if ((gen.getSuperClass() == c.getDesignClass()) || (gen.getBaseClass() == c.getDesignClass())) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof GeneralizationGR) {
                        if (((GeneralizationGR) grElement).getGeneralization() == gen) {
                            removeGeneralization((GeneralizationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = generalizations.iterator();
                        }
                    }
                }
            }
        }

        realizations = repository.getRealizations();
        iterator = realizations.iterator();

        while (iterator.hasNext()) {
            rea = (Realization) iterator.next();

            if (rea.getTheClass() == c.getDesignClass()) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof RealizationGR) {
                        if (((RealizationGR) grElement).getRealization() == rea) {
                            removeRealization((RealizationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = realizations.iterator();
                        }
                    }
                }
            }
        }

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

    public List<GraphicalElement> getInterfaceGRRealizationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof RealizationGR
                && ((RealizationGR) grElement).getRealization().getTheInterface() == interf.getInterface()))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRAssociationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> (grElement instanceof AssociationGR
                        && ((AssociationGR) grElement).getAssociation().getClassB()  == interf.getInterface()))
                .collect(Collectors.toList());
    }

    public List<GraphicalElement> getInterfaceGRGeneralizationGRs(InterfaceGR interf) {
        return graphicalElements.stream()
                .filter(grElement -> grElement instanceof GeneralizationGR
                && ((((GeneralizationGR) grElement).getClassifierA().getClassifier() == interf.getInterface())
                        || ((GeneralizationGR) grElement).getClassifierB().getClassifier() == interf
                                .getInterface()))
                .collect(Collectors.toList());
    }    

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    public void removeAssociation(AssociationGR a) {
        repository.removeAssociation(a.getAssociation());
        super.removeGraphicalElement(a);
    }

    public void removeAssociationClass(AssociationClassGR a) {
        // a.clear(); //removes association object from links instances in
        // AssociationClassGR
        repository.removeAssociationClass(a.getAssociationClass());
        super.removeGraphicalElement(a);
    }

    public void removeDependency(DependencyGR d) {
        repository.removeDependency(d.getDependency());
        super.removeGraphicalElement(d);
    }

    public void removeAggregation(AggregationGR a) {
        repository.removeAggregation(a.getAggregation());
        super.removeGraphicalElement(a);
    }

    public void removeGeneralization(GeneralizationGR g) {
        repository.removeGeneralization(g.getGeneralization());
        super.removeGraphicalElement(g);
    }

    public void removeRealization(RealizationGR r) {
        repository.removeRealization(r.getRealization());
        super.removeGraphicalElement(r);
    }

    @Override
    public void clear() {

        while (!graphicalElements.isEmpty()) {
            removeGraphicalElement(graphicalElements.get(0));
        }

        super.clear();
    }
}
