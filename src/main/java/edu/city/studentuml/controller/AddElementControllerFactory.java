package edu.city.studentuml.controller;

//Author: Ervin Ramollari
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.UCDModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that uses Factory Method design pattern to dynamically
 * instantiate the appropriate controller for adding a particular element
 */
public class AddElementControllerFactory {
    /**
     * Each frame has its own controllers, one for each command represented by the string
     * Controllers are dynamically created upon clicking on the toolbars and then stored
     * for later usage.
     * 
     * Older implementation used to always create a new controller on every click.
     */
    private Map<DiagramInternalFrame, Map<String, AddElementController>> controllers;

    private static AddElementControllerFactory factory;

    // AddElementControllerFactory constructor
    protected AddElementControllerFactory() {
        controllers = new HashMap<>();
    }

    public static final AddElementControllerFactory getInstance() {
        if (factory == null) {
            factory = new AddElementControllerFactory();
        }

        return factory;
    }

    public AddElementController newAddElementController(DiagramModel model, DiagramInternalFrame frame, String elementClass) {
        if (controllers.get(frame) == null) {
            controllers.put(frame, new HashMap<>());
        }
        Map<String, AddElementController> frameControllers = controllers.get(frame);
        if (frameControllers.get(elementClass) == null) {
            frameControllers.put(elementClass, makeController(model, frame, elementClass));
        }
        return frameControllers.get(elementClass);
    }

    private AddElementController makeController(DiagramModel model, DiagramInternalFrame frame, String elementClass) {
        if (model instanceof UCDModel) {
            if (elementClass.equals("ActorGR")) {
                return new AddUCActorController((UCDModel) model, frame);
            } else if (elementClass.equals("UseCaseGR")) {
                return new AddUseCaseController((UCDModel) model, frame);
            } else if (elementClass.equals("SystemBoundaryGR")) {
                return new AddSystemController((UCDModel) model, frame);
            } else if (elementClass.equals("AssociationGR")) {
                return new AddUCAssociationController((UCDModel) model, frame);
            } else if (elementClass.equals("IncludeGR")) {
                return new AddUCIncludeController((UCDModel) model, frame);
            } else if (elementClass.equals("ExtendGR")) {
                return new AddUCExtendController((UCDModel) model, frame);
            } else if (elementClass.equals("GeneralizationGR")) {
                return new AddUCGeneralizationController((UCDModel) model, frame);
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((UCDModel) model, frame);
            }
        } else if (model instanceof SSDModel) {
            if (elementClass.equals("SystemInstanceGR")) {
                return new AddSystemInstanceController((SSDModel) model, frame);
            } else if (elementClass.equals("ActorInstanceGR")) {
                return new AddActorInstanceController((SSDModel) model, frame);
            } else if (elementClass.equals("SystemOperationGR")) {
                return new AddCallMessageController(model, frame);
            } else if (elementClass.equals("ReturnMessageGR")) {
                return new AddReturnMessageController((SSDModel) model, frame);
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((SSDModel) model, frame);
            }
        } else if (model instanceof CCDModel) {
            if (elementClass.equals("ConceptualClassGR")) {
                return new AddConceptualClassController((CCDModel) model, frame);
            } else if (elementClass.equals("AssociationGR")) {
                return new AddAssociationController((CCDModel) model, frame);
            } else if (elementClass.equals("AssociationClassGR")) {
                return new AddAssociationClassController((CCDModel) model, frame);
            } else if (elementClass.equals("AggregationGR")) {
                return new AddAggregationController((CCDModel) model, frame);
            } else if (elementClass.equals("CompositionGR")) {
                return new AddCompositionController((CCDModel) model, frame);
            } else if (elementClass.equals("GeneralizationGR")) {
                return new AddGeneralizationController((CCDModel) model, frame);
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((CCDModel) model, frame);
            }
        } else if (model instanceof SDModel) {
            if (elementClass.equals("SDObjectGR")) {
                return new AddSDObjectController((SDModel) model, frame);
            } else if (elementClass.equals("ActorInstanceGR")) {
                return new AddActorInstanceController((SDModel) model, frame);
            } else if (elementClass.equals("MultiObjectGR")) {
                return new AddMultiObjectController((SDModel) model, frame);
            } else if (elementClass.equals("CallMessageGR")) {
                return new AddCallMessageController(model, frame);
            } else if (elementClass.equals("ReturnMessageGR")) {
                return new AddReturnMessageController((SDModel) model, frame);
            } else if (elementClass.equals("CreateMessageGR")) {
                return new AddCreateMessageController((SDModel) model, frame);
            } else if (elementClass.equals("DestroyMessageGR")) {
                return new AddDestroyMessageController((SDModel) model, frame);
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((SDModel) model, frame);
            }
        } else if (model instanceof DCDModel) {
            if (elementClass.equals("ClassGR")) {
                return new AddClassController((DCDModel) model, frame);
            } else if (elementClass.equals("InterfaceGR")) {
                return new AddInterfaceController((DCDModel) model, frame);
            } else if (elementClass.equals("AssociationGR")) {
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
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((DCDModel) model, frame);
            }
        } else if (model instanceof ADModel) {
            if (elementClass.equals("InitialNodeGR")) {
                return new AddInitialNodeController((ADModel) model, frame);
            } else if (elementClass.equals("ActivityFinalNodeGR")) {
                return new AddActivityFinalNodeController((ADModel) model, frame);
            } else if (elementClass.equals("FlowFinalNodeGR")) {
                return new AddFlowFinalNodeController((ADModel) model, frame);
            } else if (elementClass.equals("ActionNodeGR")) {
                return new AddActionNodeController((ADModel) model, frame);
            } else if (elementClass.equals("DecisionNodeGR")) {
                return new AddDecisionNodeController((ADModel) model, frame);
            } else if (elementClass.equals("MergeNodeGR")) {
                return new AddMergeNodeController((ADModel) model, frame);
            } else if (elementClass.equals("ForkNodeGR")) {
                return new AddForkNodeController((ADModel) model, frame);
            } else if (elementClass.equals("JoinNodeGR")) {
                return new AddJoinNodeController((ADModel) model, frame);
            } else if (elementClass.equals("ControlFlowGR")) {
                return new AddControlFlowController((ADModel) model, frame);
            } else if (elementClass.equals("ObjectNodeGR")) {
                return new AddObjectNodeController((ADModel) model, frame);
            } else if (elementClass.equals("ObjectFlowGR")) {
                return new AddObjectFlowController((ADModel) model, frame);
            } else if (elementClass.equals("ActivityNodeGR")) {
                return new AddActivityNodeController((ADModel) model, frame);
            } else if (elementClass.equals("UMLNoteGR")) {
                return new AddUMLNoteController((ADModel) model, frame);
            }
        }
        throw new RuntimeException("AddElementController not found");
    }
}
