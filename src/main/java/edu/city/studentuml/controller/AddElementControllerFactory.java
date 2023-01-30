package edu.city.studentuml.controller;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ActivityFinalNodeGR;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.DestroyMessageGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.FlowFinalNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Singleton class that uses Factory Method design pattern to dynamically
 * instantiate the appropriate controller for adding a particular element
 * 
 * @author Dimitris Dranidis
 */
public class AddElementControllerFactory {

    private static final Logger logger = Logger.getLogger(AddElementControllerFactory.class.getName());

    private static final String LINK_BETWEEN_CLASSIFIERS_EXISTS = "The link between these two classifiers already exists!";

    /**
     * Each frame has its own controllers, one for each command represented by the
     * string Controllers are dynamically created upon clicking on the toolbars and
     * then stored for later usage. Older implementation used to always create a new
     * controller on every click.
     */
    private Map<DiagramInternalFrame, Map<String, AddElementController>> controllers;

    private static AddElementControllerFactory factory;

    protected AddElementControllerFactory() {
        controllers = new HashMap<>();
    }

    public static final AddElementControllerFactory getInstance() {
        if (factory == null) {
            factory = new AddElementControllerFactory();
        }
        return factory;
    }

    public AddElementController newAddElementController(DiagramModel model, DiagramInternalFrame frame,
            String elementClass) {
        if (controllers.get(frame) == null) {
            controllers.put(frame, new HashMap<>());
        }
        Map<String, AddElementController> frameControllers = controllers.get(frame);
        if (frameControllers.get(elementClass) == null) {
            AddElementController controller = makeController(model, frame, elementClass);
            if (controller != null) {
                frameControllers.put(elementClass, controller);
            }
        }
        return frameControllers.get(elementClass);
    }

    private AddElementController makeController(DiagramModel model, DiagramInternalFrame frame, String elementClass) {

        switch (elementClass) {
        case "UMLNoteGR":
            return new AddUMLNoteController(model, frame);
        case "ActorGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new UCActorGR(new Actor(""), x, y);
                }

            };

        case "UseCaseGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new UseCaseGR(new UseCase(""), x, y);
                }

            };

        case "SystemBoundaryGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new SystemGR(new System(), x, y);
                }

            };

        case "SystemInstanceGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new SystemInstanceGR(new SystemInstance("", new System("")), x);
                }

            };

        case "ActorInstanceGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ActorInstanceGR(new ActorInstance("", new Actor("")), x);
                }

            };

        case "InterfaceGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new InterfaceGR(new Interface(""), new Point(x, y));
                }

            };

        case "ClassGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ClassGR(new DesignClass(""), new Point(x, y));
                }

            };

        case "ConceptualClassGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ConceptualClassGR(new ConceptualClass(""), new Point(x, y));
                }

            };

        case "SDObjectGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new SDObjectGR(new SDObject("", new DesignClass("")), x);
                }

            };

        case "MultiObjectGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new MultiObjectGR(new MultiObject("", new DesignClass("")), x);
                }

            };

        case "InitialNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new InitialNodeGR(new InitialNode(), x, y);
                }

            };

        case "ActivityFinalNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ActivityFinalNodeGR(new ActivityFinalNode(), x, y);
                }

            };

        case "FlowFinalNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new FlowFinalNodeGR(new FlowFinalNode(), x, y);
                }

            };

        case "ActionNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ActionNodeGR(new ActionNode(), x, y);
                }

            };

        case "DecisionNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new DecisionNodeGR(new DecisionNode(), x, y);
                }

            };

        case "MergeNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new MergeNodeGR(new MergeNode(), x, y);
                }

            };

        case "ForkNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ForkNodeGR(new ForkNode(), x, y);
                }

            };

        case "JoinNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new JoinNodeGR(new JoinNode(), x, y);
                }

            };

        case "ObjectNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ObjectNodeGR(new ObjectNode(), x, y);
                }

            };

        case "ActivityNodeGR":
            return new AddClickElementController(model, frame) {

                @Override
                protected GraphicalElement makeGraphicalElement(int x, int y) {
                    return new ActivityNodeGR(new ActivityNode(), x, y);
                }

            };

        /*
         * LINKS
         */

        case "DependencyGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR classA, ClassifierGR classB) {
                    if (classA != classB && classA instanceof ClassGR && classB instanceof ClassGR) {
                        ClassGR classAGR = (ClassGR) classA;
                        ClassGR classBGR = (ClassGR) classB;
                        Dependency dependency = new Dependency(classAGR.getDesignClass(), classBGR.getDesignClass());

                        return new DependencyGR(classAGR, classBGR, dependency);
                    } else {
                        return null;
                    }
                }

            };

        case "AssociationGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR classA, ClassifierGR classB) {
                    if (classA instanceof UCActorGR && classB instanceof UseCaseGR) {
                        if (classA == classB) {
                            return null;
                        }
                        if (relationshipExists(model, classA, classB)) {
                            showErrorMessage(frame,
                                    "The asscociation between these the actor and the use case already exists!");
                            return null;
                        }

                        UCActorGR actorGR = (UCActorGR) classA;
                        UseCaseGR ucGR = (UseCaseGR) classB;
                        UCAssociation association = new UCAssociation((Actor) actorGR.getComponent(),
                                (UseCase) ucGR.getComponent());
                        return new UCAssociationGR(actorGR, ucGR, association);

                    } else {

                        Association association = new Association(classA.getClassifier(), classB.getClassifier());
                        if (diagramModel instanceof CCDModel) {
                            association.setBidirectional();
                        } else {
                            association.setDirection(Association.AB);
                        }

                        return new AssociationGR(classA, classB, association);
                    }
                }
            };

        case "AssociationClassGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR classA, ClassifierGR classB) {

                    AbstractAssociationClass associationClass;
                    AssociationClassGR associationClassGR;

                    // can be either CCD or DCD
                    if (diagramModel instanceof CCDModel) {
                        associationClass = new ConceptualAssociationClass(classA.getClassifier(),
                                classB.getClassifier());
                        associationClass.setBidirectional();
                        associationClassGR = new AssociationClassGR(classA, classB, associationClass);
                    } else {
                        associationClass = new DesignAssociationClass(classA.getClassifier(), classB.getClassifier());
                        associationClass.setDirection(AbstractAssociationClass.AB);
                        associationClassGR = new AssociationClassGR(classA, classB, associationClass);
                    }

                    return associationClassGR;

                }
            };

        case "AggregationGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR whole, ClassifierGR part) {
                    // the false flag indicates that the aggregation is not strong (composition)
                    Aggregation aggregation = new Aggregation(whole.getClassifier(), part.getClassifier(), false);

                    if (diagramModel instanceof CCDModel) {
                        aggregation.setBidirectional();
                    } else {
                        aggregation.setDirection(Association.AB);
                    }

                    return new AggregationGR(whole, part, aggregation);
                }
            };

        case "CompositionGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR whole, ClassifierGR part) {
                    // the true flag indicates that the aggregation is strong (composition)
                    Aggregation aggregation = new Aggregation(whole.getClassifier(), part.getClassifier(), true);

                    if (diagramModel instanceof CCDModel) {
                        aggregation.setBidirectional();
                    } else {
                        aggregation.setDirection(Association.AB);
                    }

                    return new AggregationGR(whole, part, aggregation);
                }
            };

        case "GeneralizationGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR baseClass, ClassifierGR superClass) {
                    if (baseClass == superClass) {
                        return null;
                    }
                    if (relationshipExists(model, baseClass, superClass)) {
                        showErrorMessage(frame, LINK_BETWEEN_CLASSIFIERS_EXISTS);
                        return null;
                    }

                    if (baseClass instanceof AbstractClassGR && superClass instanceof InterfaceGR
                            || superClass instanceof AbstractClassGR && baseClass instanceof InterfaceGR) {
                        return null;
                    }
                    if (baseClass instanceof UCDComponentGR && superClass instanceof UCDComponentGR) {

                        if (baseClass instanceof UseCaseGR && superClass instanceof UseCaseGR) {
                            UCGeneralization generalization = new UCGeneralization(
                                    (UseCase) ((UseCaseGR) baseClass).getComponent(),
                                    (UseCase) ((UseCaseGR) superClass).getComponent());
                            return new UCGeneralizationGR((UseCaseGR) baseClass, (UseCaseGR) superClass,
                                    generalization);
                        }
                        if (baseClass instanceof UCActorGR && superClass instanceof UCActorGR) {
                            UCGeneralization generalization = new UCGeneralization(
                                    (Actor) ((UCActorGR) baseClass).getComponent(),
                                    (Actor) ((UCActorGR) superClass).getComponent());
                            return new UCGeneralizationGR((UCActorGR) baseClass, (UCActorGR) superClass,
                                    generalization);
                        }

                    }

                    Generalization generalization = new Generalization(superClass.getClassifier(),
                            baseClass.getClassifier());
                    return new GeneralizationGR(superClass, baseClass, generalization);
                }
            };

        case "RealizationGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR classA, ClassifierGR classB) {
                    if (classA == classB) {
                        return null;
                    }

                    if (classA instanceof ClassGR && classB instanceof InterfaceGR) {

                        if (relationshipExists(model, classA, classB)) {
                            showErrorMessage(frame, LINK_BETWEEN_CLASSIFIERS_EXISTS);
                            return null;
                        }
                        ClassGR classGR = (ClassGR) classA;
                        InterfaceGR interfaceGR = (InterfaceGR) classB;

                        Realization realization = new Realization(classGR.getDesignClass(), interfaceGR.getInterface());
                        return new RealizationGR(classGR, interfaceGR, realization);
                    } else {
                        return null;
                    }
                }
            };

        case "IncludeGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR uc1, ClassifierGR uc2) {
                    if (uc1 == uc2) {
                        return null;
                    }
                    if (relationshipExists(model, uc1, uc2)) {
                        showErrorMessage(frame, LINK_BETWEEN_CLASSIFIERS_EXISTS);
                        return null;
                    }

                    if (uc1 instanceof UseCaseGR && uc2 instanceof UseCaseGR) {
                        UseCaseGR uc1GR = (UseCaseGR) uc1;
                        UseCaseGR uc2GR = (UseCaseGR) uc2;
                        UCInclude useCaseInclude = new UCInclude((UseCase) uc1GR.getComponent(),
                                (UseCase) uc2GR.getComponent());
                        return new UCIncludeGR(uc1GR, uc2GR, useCaseInclude);
                    } else {
                        return null;
                    }
                }

            };

        case "ExtendGR":
            return new AddLinkController(model, frame) {

                @Override
                protected LinkGR createRelationship(ClassifierGR uc1, ClassifierGR uc2) {
                    if (uc1 == uc2) {
                        return null;
                    }
                    if (relationshipExists(model, uc1, uc2)) {
                        showErrorMessage(frame, LINK_BETWEEN_CLASSIFIERS_EXISTS);
                        return null;
                    }

                    if (uc1 instanceof UseCaseGR && uc2 instanceof UseCaseGR) {
                        UseCaseGR uc1GR = (UseCaseGR) uc1;
                        UseCaseGR uc2GR = (UseCaseGR) uc2;
                        UCExtend useCaseExtend = new UCExtend((UseCase) uc1GR.getComponent(),
                                (UseCase) uc2GR.getComponent());
                        UCExtendGR extendGR = new UCExtendGR(uc1GR, uc2GR, useCaseExtend);
                        extendGR.addExtensionPoint(new ExtensionPoint("New Extension Point"));

                        return extendGR;
                    } else {
                        return null;
                    }
                }

            };

        case "SystemOperationGR":
            return new AddCallMessageController(model, frame);
        case "ReturnMessageGR":
            return new AddSDLinkController(model, frame) {

                @Override
                protected SDMessageGR createRelationship(RoleClassifierGR roleA, RoleClassifierGR roleB, int y) {
                    ReturnMessage message = new ReturnMessage(roleA.getRoleClassifier(), roleB.getRoleClassifier(), "");
                    return new ReturnMessageGR(roleA, roleB, message, y);
                }

            };
        case "CallMessageGR":
            return new AddCallMessageController(model, frame);
        case "CreateMessageGR":
            return new AddCreateMessageController((SDModel) model, frame);
        case "DestroyMessageGR":
            return new AddSDLinkController(model, frame) {

                @Override
                protected SDMessageGR createRelationship(RoleClassifierGR roleA, RoleClassifierGR roleB, int y) {
                    if (roleA == roleB) {
                        return null;
                    }
                    DestroyMessage message = new DestroyMessage(roleA.getRoleClassifier(), roleB.getRoleClassifier());
                    return new DestroyMessageGR(roleA, roleB, message, y);                    
                }

            };
        case "ControlFlowGR":
            return new AddControlFlowController((ADModel) model, frame);
        case "ObjectFlowGR":
            return new AddObjectFlowController((ADModel) model, frame);

        default:
            logger.severe(() -> "AddElementController not found for string " + elementClass);
            return null;
        }
    }

    protected boolean relationshipExists(DiagramModel model, ClassifierGR baseClass, ClassifierGR superClass) {

         Optional<Generalization> aGeneralization = model.getCentralRepository().getGeneralizations().stream().filter(
                r -> (r.getBaseClass() == baseClass.getClassifier() && r.getSuperClass() == superClass.getClassifier()
                        || r.getBaseClass() == superClass.getClassifier()
                                && r.getSuperClass() == baseClass.getClassifier())).findFirst();

        Optional<Realization> aRealization = model.getCentralRepository().getRealizations().stream().filter(
                r -> (r.getTheClass() == baseClass.getClassifier() && r.getTheInterface() == superClass.getClassifier())
                        || (r.getTheClass() == superClass.getClassifier()
                                && r.getTheInterface() == baseClass.getClassifier()))
                .findFirst();

        if (aRealization.isPresent() || aGeneralization.isPresent()) {
            if (aRealization.isPresent()) {
                Realization realization = aRealization.get();
                // it is the same relationship; allow
                if (realization.getTheClass() == baseClass.getClassifier()) {
                    return false;
                }
            }
            if (aGeneralization.isPresent()) {
                 Generalization generalization = aGeneralization.get();
                // it is the same relationship; allow
                if (generalization.getBaseClass() == baseClass.getClassifier()) {
                    return false;
                }
            }
            return true;
        }

        if (baseClass instanceof UCDComponentGR && superClass instanceof UCDComponentGR) {
            UCDComponentGR baseGR = (UCDComponentGR) baseClass;
            UCDComponentGR superGR = (UCDComponentGR) superClass;

            return model.getCentralRepository().getUCLinks().stream()
                    .anyMatch(r -> (r.getSource() == baseGR.getComponent() && r.getTarget() == superGR.getComponent()
                            || r.getTarget() == baseGR.getComponent() && r.getSource() == superGR.getComponent()));
        }
        return false;
    }

    protected void showErrorMessage(DiagramInternalFrame parentFrame, String msg) {
        JOptionPane.showMessageDialog(parentFrame, msg, "Classifier Link Error", JOptionPane.ERROR_MESSAGE);
    }

}
