package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//DCDModel.java
//This class represents the model of a UML design class diagram
//by containing a list of graphical elements that belong to DCDs
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import java.awt.Point;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

public class DCDModel extends DiagramModel {

    public DCDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    // override the superclass method addGraphicalElement to handle different classes
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
    
/**
 * TODO: not used?
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

    /*
    TODO: Not used?
    */
    public void addAssoc(ClassifierGR classA, ClassifierGR classB) {
        SystemWideObjectNamePool.getInstance().loading();

        Association association = new Association(classA.getClassifier(), classB.getClassifier());
        association.setDirection(Association.AB);
        AssociationGR associationGR = new AssociationGR(classA, classB, association);

        addAssociation(associationGR);

        SystemWideObjectNamePool.getInstance().done();
    }

    public void addAssociationClass(AssociationClassGR a) {
        repository.addAssociationClass((DesignAssociationClass) a.getAssociationClass());
        super.addGraphicalElement(a);
    }

    // add a new diagram dependency
    public void addDependency(DependencyGR d) {
        Dependency dependency = d.getDependency();
        DesignClass from = dependency.getFrom();
        DesignClass to = dependency.getTo();

        // try to add the dependency to the repository first, if it doesn't exist
        if (!repository.addDependency(dependency)) {
            JOptionPane.showMessageDialog(null,
                    "Dependency from \"" + from.getName() + "\" to \"" + to.getName()
                    + "\" already exists!", "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        super.addGraphicalElement(d);
    }

    public void addDep(ClassGR classA, ClassGR classB) {
        SystemWideObjectNamePool.getInstance().loading();

        Dependency dependency = new Dependency(classA.getDesignClass(), classB.getDesignClass());
        DependencyGR dependencyGR = new DependencyGR(classA, classB, dependency);

        addDependency(dependencyGR);

        SystemWideObjectNamePool.getInstance().done();
    }

    // add a new diagram aggregation
    public void addAggregation(AggregationGR a) {

        // add the aggregation to the project repository first and then to the diagram
        if (!repository.addAggregation(a.getAggregation())) {
            return;
        }

        super.addGraphicalElement(a);
    }

    /*
    TODO: Not used?
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

    // add a new diagram generalization
    public void addGeneralization(GeneralizationGR g) {
        Generalization generalization = g.getGeneralization();
        DesignClass superClass = (DesignClass) generalization.getSuperClass();
        DesignClass baseClass = (DesignClass) generalization.getBaseClass();

        // try to add the generalization to the repository first, if it doesn't exist
        if (!repository.addGeneralization(generalization)) {
            JOptionPane.showMessageDialog(null,
                    "Class \"" + baseClass.getName() + "\" already inherits from class \""
                    + superClass.getName() + "\".", "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        super.addGraphicalElement(g);
    }

    // add a new diagram realization
    public void addRealization(RealizationGR r) {
        Realization realization = r.getRealization();
        DesignClass theClass = realization.getTheClass();
        Interface theInterface = realization.getTheInterface();

        if (!repository.addRealization(realization)) {
            JOptionPane.showMessageDialog(null,
                    "Class \"" + theClass.getName() + "\" already realizes interface \""
                    + theInterface.getName(), "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        super.addGraphicalElement(r);
    }

    // override superclass method removeGraphicalElement()
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
        }else if (e instanceof DependencyGR) {
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

            if ((associationClass.getClassA() == c.getDesignClass()) || (associationClass.getClassB() == c.getDesignClass())) {
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
        Vector realizations, associations, aggregations;
        Realization rea;
        Association association;
        Aggregation aggregation;
        GraphicalElement grElement;
        Iterator iterator, iterGE;

        realizations = repository.getRealizations();
        iterator = realizations.iterator();
        while (iterator.hasNext()) {
            rea = (Realization) iterator.next();

            if (rea.getTheInterface() == i.getInterface()) {
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

        associations = repository.getAssociations();
        iterator = associations.iterator();
        while (iterator.hasNext()) {
            association = (Association) iterator.next();

            if (association.getClassB() == i.getInterface()) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationGR) {
                        if (((AssociationGR) grElement).getAssociation() == association) {
                            removeAssociation((AssociationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = associations.iterator();
                        }
                    }
                }
            }
        }

        aggregations = repository.getAggregations();
        iterator = aggregations.iterator();
        while (iterator.hasNext()) {
            aggregation = (Aggregation) iterator.next();

            if (aggregation.getPart() == i.getInterface()) {
                iterGE = graphicalElements.iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AggregationGR) {
                        if (((AggregationGR) grElement).getAggregation() == aggregation) {
                            removeAggregation((AggregationGR) grElement);
                            iterGE = graphicalElements.iterator();
                            iterator = aggregations.iterator();
                        }
                    }
                }
            }
        }

        i.getInterface().clear();
        repository.removeInterface(i.getInterface());
        super.removeGraphicalElement(i);
    }

    // since graphical associations, dependencies, and other links
    // have a one-to one association with their domain representations,
    // remove them both from the central repository and from the diagram
    public void removeAssociation(AssociationGR a) {
        repository.removeAssociation(a.getAssociation());
        super.removeGraphicalElement(a);
    }

    public void removeAssociationClass(AssociationClassGR a) {
//        a.clear();  //removes association object from links instances in AssociationClassGR
        repository.removeAssociationClass((DesignAssociationClass) a.getAssociationClass());
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

    public void clear() {

        while (graphicalElements.size() > 0) {
            removeGraphicalElement((GraphicalElement) graphicalElements.get(0));
        }

        super.clear();
    }
}
