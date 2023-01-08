package edu.city.studentuml.controller;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ActivityFinalNodeGR;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.FlowFinalNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * Singleton class that uses Factory Method design pattern to dynamically
 * instantiate the appropriate controller for adding a particular element
 */
public class AddElementControllerFactory {

    private static final Logger logger = Logger.getLogger(AddElementControllerFactory.class.getName());

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

        default:
            // Return some default value or throw an exception
        }

        if (model instanceof UCDModel) {
            if (elementClass.equals("AssociationGR")) {
                return new AddUCAssociationController((UCDModel) model, frame);
            } else if (elementClass.equals("IncludeGR")) {
                return new AddUCIncludeController((UCDModel) model, frame);
            } else if (elementClass.equals("ExtendGR")) {
                return new AddUCExtendController((UCDModel) model, frame);
            } else if (elementClass.equals("GeneralizationGR")) {
                return new AddUCGeneralizationController((UCDModel) model, frame);
            }
        } else if (model instanceof SSDModel) {
            if (elementClass.equals("SystemOperationGR")) {
                return new AddCallMessageController(model, frame);
            } else if (elementClass.equals("ReturnMessageGR")) {
                return new AddReturnMessageController((SSDModel) model, frame);
            }
        } else if (model instanceof CCDModel) {
            if (elementClass.equals("AssociationGR")) {
                return new AddAssociationController((CCDModel) model, frame);
            } else if (elementClass.equals("AssociationClassGR")) {
                return new AddAssociationClassController((CCDModel) model, frame);
            } else if (elementClass.equals("AggregationGR")) {
                return new AddAggregationController((CCDModel) model, frame);
            } else if (elementClass.equals("CompositionGR")) {
                return new AddCompositionController((CCDModel) model, frame);
            } else if (elementClass.equals("GeneralizationGR")) {
                return new AddGeneralizationController((CCDModel) model, frame);
            }
        } else if (model instanceof SDModel) {
            if (elementClass.equals("CallMessageGR")) {
                return new AddCallMessageController(model, frame);
            } else if (elementClass.equals("ReturnMessageGR")) {
                return new AddReturnMessageController((SDModel) model, frame);
            } else if (elementClass.equals("CreateMessageGR")) {
                return new AddCreateMessageController((SDModel) model, frame);
            } else if (elementClass.equals("DestroyMessageGR")) {
                return new AddDestroyMessageController((SDModel) model, frame);
            }
        } else if (model instanceof DCDModel) {
            if (elementClass.equals("AssociationGR")) {
                return new AddAssociationController((DCDModel) model, frame);
            } else if (elementClass.equals("AssociationClassGR")) {
                return new AddAssociationClassController((DCDModel) model, frame);
            } else if (elementClass.equals("DependencyGR")) {
                return new AddDependencyController((DCDModel) model, frame);
            } else if (elementClass.equals("AggregationGR")) {
                return new AddAggregationController((DCDModel) model, frame);
            } else if (elementClass.equals("CompositionGR")) {
                return new AddCompositionController((DCDModel) model, frame);
            } else if (elementClass.equals("GeneralizationGR")) {
                return new AddGeneralizationController((DCDModel) model, frame);
            } else if (elementClass.equals("RealizationGR")) {
                return new AddRealizationController((DCDModel) model, frame);
            }
        } else if (model instanceof ADModel) {
            if (elementClass.equals("ControlFlowGR")) {
                return new AddControlFlowController((ADModel) model, frame);
            } else if (elementClass.equals("ObjectFlowGR")) {
                return new AddObjectFlowController((ADModel) model, frame);
            }
        }
        logger.severe(() -> "AddElementController not found for string " + elementClass);
        return null;
    }
}
