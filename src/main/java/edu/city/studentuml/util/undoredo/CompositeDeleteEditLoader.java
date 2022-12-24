package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author draganbisercic
 */
public class CompositeDeleteEditLoader {
    private CompositeDeleteEditLoader() {
        throw new IllegalStateException("Utility class");
    }

    public static void loadCompositeDeleteEdit(GraphicalElement e, CompositeDeleteEdit edit, DiagramModel model) {
        if (e instanceof ConceptualClassGR) {
            loadCCDClassCompositeDeleteEdit((ConceptualClassGR) e, edit, model);
        } else if (e instanceof ClassGR) {
            loadDCDClassCompositeDeleteEdit((ClassGR) e, edit, model);
        } else if (e instanceof InterfaceGR) {
            loadInterfaceCompositeDeleteEdit((InterfaceGR) e, edit, model);
        } else if (e instanceof RoleClassifierGR) {
            loadRoleClassifierCompositeDeleteEdit((RoleClassifierGR) e, edit, model);
        }
    }

    private static void loadCCDClassCompositeDeleteEdit(ConceptualClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        LeafDeleteEdit leaf;
        Vector associations, aggregations, generalizations, associationClasses;
        Association association;
        Aggregation agregation;
        ConceptualAssociationClass associationClass;
        Generalization generalization;
        GraphicalElement grElement;
        Iterator iterator, iterGE;
        CentralRepository repository = model.getCentralRepository();

        // when this element gets removed, the class is cleared;
        // need to remember changes
        leaf = new LeafDeleteEdit(c, model);
        edit.add(leaf);

        associations = repository.getAssociations();
        iterator = associations.iterator();
        while (iterator.hasNext()) {
            association = (Association) iterator.next();

            if ((association.getClassA() == c.getConceptualClass())
                    || (association.getClassB() == c.getConceptualClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationGR) {
                        if (((AssociationGR) grElement).getAssociation() == association) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        aggregations = repository.getAggregations();
        iterator = aggregations.iterator();
        while (iterator.hasNext()) {
            agregation = (Aggregation) iterator.next();

            if ((agregation.getPart() == c.getConceptualClass()) || (agregation.getWhole() == c.getConceptualClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AggregationGR) {
                        if (((AggregationGR) grElement).getAggregation() == agregation) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        associationClasses = repository.getConceptualAssociationClasses();
        iterator = associationClasses.iterator();
        while (iterator.hasNext()) {
            associationClass = (ConceptualAssociationClass) iterator.next();

            if ((associationClass.getClassA() == c.getConceptualClass()) || (associationClass.getClassB() == c.getConceptualClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationClassGR) {
                        if (((AssociationClassGR) grElement).getAssociationClass() == associationClass) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        generalizations = repository.getGeneralizations();
        iterator = generalizations.iterator();
        while (iterator.hasNext()) {
            generalization = (Generalization) iterator.next();

            if ((generalization.getSuperClass() == c.getConceptualClass()) || (generalization.getBaseClass() == c.getConceptualClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof GeneralizationGR) {
                        if (((GeneralizationGR) grElement).getGeneralization() == generalization) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }
    }

    private static void 
    loadDCDClassCompositeDeleteEdit(ClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        LeafDeleteEdit leaf;
        Vector associations, aggregations, generalizations, associationClasses, dependencies, realizations;
        Association association;
        Aggregation agregation;
        DesignAssociationClass associationClass;
        Generalization generalization;
        Dependency dependancy;
        Realization realization;
        GraphicalElement grElement;
        Iterator iterator, iterGE;
        CentralRepository repository = model.getCentralRepository();

        leaf = new LeafDeleteEdit(c, model);
        edit.add(leaf);

        associations = repository.getAssociations();
        iterator = associations.iterator();
        while (iterator.hasNext()) {
            association = (Association) iterator.next();

            if ((association.getClassA() == c.getDesignClass())
                    || (association.getClassB() == c.getDesignClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationGR) {
                        if (((AssociationGR) grElement).getAssociation() == association) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        aggregations = repository.getAggregations();
        iterator = aggregations.iterator();
        while (iterator.hasNext()) {
            agregation = (Aggregation) iterator.next();

            if ((agregation.getPart() == c.getDesignClass())
                    || (agregation.getWhole() == c.getDesignClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AggregationGR) {
                        if (((AggregationGR) grElement).getAggregation() == agregation) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
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
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof AssociationClassGR) {
                        if (((AssociationClassGR) grElement).getAssociationClass() == associationClass) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        dependencies = repository.getDependencies();
        iterator = dependencies.iterator();

        while (iterator.hasNext()) {
            dependancy = (Dependency) iterator.next();

            if ((dependancy.getFrom() == c.getDesignClass())
                    || (dependancy.getTo() == c.getDesignClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof DependencyGR) {
                        if (((DependencyGR) grElement).getDependency() == dependancy) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        generalizations = repository.getGeneralizations();
        iterator = generalizations.iterator();
        while (iterator.hasNext()) {
            generalization = (Generalization) iterator.next();

            if ((generalization.getSuperClass() == c.getDesignClass())
                    || (generalization.getBaseClass() == c.getDesignClass())) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof GeneralizationGR) {
                        if (((GeneralizationGR) grElement).getGeneralization() == generalization) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }

        realizations = repository.getRealizations();
        iterator = realizations.iterator();

        while (iterator.hasNext()) {
            realization = (Realization) iterator.next();

            if (realization.getTheClass() == c.getDesignClass()) {
                iterGE = model.getGraphicalElements().iterator();

                while (iterGE.hasNext()) {
                    grElement = (GraphicalElement) iterGE.next();

                    if (grElement instanceof RealizationGR) {
                        if (((RealizationGR) grElement).getRealization() == realization) {
                            leaf = new LeafDeleteEdit(grElement, model);
                            edit.add(leaf);
                        }
                    }
                }
            }
        }
    }

    private static void loadInterfaceCompositeDeleteEdit(InterfaceGR i, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(i, model));

        /**
         * aggregations are associations
         */
        ((DCDModel) model).getInterfaceGRAssociationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getInterfaceGRGeneralizationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getInterfaceGRRealizationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
    }

    private static void loadRoleClassifierCompositeDeleteEdit(RoleClassifierGR rc, CompositeDeleteEdit edit, DiagramModel model) {
        CompositeDeleteEdit composite;
        LeafDeleteEdit leaf;
        AbstractSDModel m = (AbstractSDModel) model;

        leaf = new LeafDeleteEdit(rc, model);
        edit.add(leaf);

        SDMessageGR message;
        Iterator iterator = m.getMessages().iterator();
        while (iterator.hasNext()) {
            message = (SDMessageGR) iterator.next();

            if ((message.getSource() == rc) || (message.getTarget() == rc)) {
                leaf = new LeafDeleteEdit(message, model);
                edit.add(leaf);
            }
        }
    }
}
