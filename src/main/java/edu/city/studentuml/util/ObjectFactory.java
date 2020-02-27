package edu.city.studentuml.util;

//import edu.city.studentuml.applet.Application;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.DestroyMessageGR;
import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.AbstractClass;
import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.GenericClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.domain.MessageParameter;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.AbstractClassGR;
import edu.city.studentuml.model.graphical.ActionNodeGR;
import edu.city.studentuml.model.graphical.ActivityFinalNodeGR;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.ControlFlowGR;
import edu.city.studentuml.model.graphical.DecisionNodeGR;
import edu.city.studentuml.model.graphical.EdgeGR;
import edu.city.studentuml.model.graphical.EndPointGR;
import edu.city.studentuml.model.graphical.FlowFinalNodeGR;
import edu.city.studentuml.model.graphical.ForkNodeGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.JoinNodeGR;
import edu.city.studentuml.model.graphical.MergeNodeGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.graphical.PointGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.w3c.dom.Element;

public final class ObjectFactory extends Observable {
    private static ObjectFactory instance = new ObjectFactory();
    protected ObjectFactory() {}
    public static ObjectFactory getInstance() {
        return instance;
    }
    private Logger logger = Logger.getLogger(ObjectFactory.class.getName());

    @Override
    public synchronized void addObserver(Observer o) {
        logger.fine("OBSERVER added: " + o.toString());
        super.addObserver(o);
    }  
    
    public IXMLCustomStreamable newInstance(String className, Object parent, Element stream, XMLStreamer streamer) {
        // THIS IS QUICK HACK; SHOULD BE CHANGED IF TIME ALLOWS
        String modelGraphicalPackageName = "edu.city.studentuml.model.graphical.";
        String modelDomainPackageName = "edu.city.studentuml.model.domain.";
        String viewPackageName = "edu.city.studentuml.view.";
        String viewGUIPackageName = "edu.city.studentuml.view.gui.";
        try {
            Class m = Class.forName(modelGraphicalPackageName + className);
            return newInstance(m, parent, stream, streamer);
        } catch (ClassNotFoundException e) {
            try {
                Class v = Class.forName(modelDomainPackageName + className);
                return newInstance(v, parent, stream, streamer);
            } catch (ClassNotFoundException ex) {
                try {
                    Class v = Class.forName(viewPackageName + className);
                    return newInstance(v, parent, stream, streamer);
                } catch (ClassNotFoundException exc) {
                    try {
                        Class v = Class.forName(viewGUIPackageName + className);
                        return newInstance(v, parent, stream, streamer);
                    } catch (ClassNotFoundException except) {
                        java.lang.System.err.println("ERROR in ObjectFactory in newInstance(className, parent, stream, streamer)");
                        return null;
                    }
                }
            }
        }
    }

    public IXMLCustomStreamable newInstance(Class c, Object parent, Element stream, XMLStreamer streamer) {
        if (c == null) {
            return null;
        }
        String methodName = "new" + c.getSimpleName().toLowerCase();
        try {
            Method m = ObjectFactory.class.getMethod(methodName, new Class[]{Object.class, Element.class, XMLStreamer.class});
            Object result = m.invoke(this, new Object[]{parent, stream, streamer});
            if (result instanceof IXMLCustomStreamable) {

                String thisID = stream.getAttribute("internalid");
                if ((thisID != null) && (!thisID.equals(""))) {
                    SystemWideObjectNamePool.getInstance().renameObject(result, thisID);
                }
                return (IXMLCustomStreamable) result;
            }

        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            logger.severe("---> ObjectFactory: No Such Method Defined : " + methodName);
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public Rectangle readRect(String val) {
        if (val == null) {
            return null;
        }
        String[] vals = val.split(",");
        if (vals.length == 4) {
            return new Rectangle(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Integer.parseInt(vals[2]), Integer.parseInt(vals[3]));
        }
        return null;
    }

    public IXMLCustomStreamable newucdmodel(Object parent, Element stream, XMLStreamer streamer) {
        UMLProject u = (UMLProject) parent;
        DiagramModel model = new UCDModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;
    }

    public IXMLCustomStreamable newccdmodel(Object parent, Element stream, XMLStreamer streamer) {

        UMLProject u = (UMLProject) parent;
        DiagramModel model = new CCDModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;
    }

    public IXMLCustomStreamable newssdmodel(Object parent, Element stream, XMLStreamer streamer) {

        UMLProject u = (UMLProject) parent;
        DiagramModel model = new SSDModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;
    }

    public IXMLCustomStreamable newdcdmodel(Object parent, Element stream, XMLStreamer streamer) {

        UMLProject u = (UMLProject) parent;
        DiagramModel model = new DCDModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;
    }

    public IXMLCustomStreamable newsdmodel(Object parent, Element stream, XMLStreamer streamer) {

        UMLProject u = (UMLProject) parent;
        DiagramModel model = new SDModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;
    }

    public IXMLCustomStreamable newadmodel(Object parent, Element stream, XMLStreamer streamer) {
        UMLProject u = (UMLProject) parent;
        DiagramModel model = new ADModel(stream.getAttribute("name"), u);
        
        notifyApplicationGUI(model, stream);
        return model;

    }

    // UCD
    public IXMLCustomStreamable newucactorgr(Object parent, Element stream, XMLStreamer streamer) {
        Actor actor = (Actor) streamer.readObjectByID(stream, "ucActor", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        UCActorGR actorGR = new UCActorGR(actor, x, y);

        if (parent instanceof UCDModel) {
            ((UCDModel) parent).addGraphicalElement(actorGR);
        } else if (parent instanceof UCDComponentGR) {
            UCDComponentGR element = (UCDComponentGR) parent;
            element.add(actorGR);
            actorGR.setContext(element);
            SystemWideObjectNamePool.getInstance().objectAdded(actorGR);
        }

        return actorGR;
    }
    
    public IXMLCustomStreamable newsystemgr(Object parent, Element stream, XMLStreamer streamer) {
        System s = (System) streamer.readObjectByID(stream, "system", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        SystemGR systemGR = new SystemGR(s, x, y);

        if (parent instanceof UCDModel) {
            ((UCDModel) parent).addGraphicalElement(systemGR);
        } else if (parent instanceof UCDComponentGR) {
            UCDComponentGR element = (UCDComponentGR) parent;
            element.add(systemGR);
            systemGR.setContext(element);
            SystemWideObjectNamePool.getInstance().objectAdded(systemGR);
        }

        return systemGR;
    }
    
    public IXMLCustomStreamable newusecasegr(Object parent, Element stream, XMLStreamer streamer) {
        UseCase uc = (UseCase) streamer.readObjectByID(stream, "useCase", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        UseCaseGR useCaseGR = new UseCaseGR(uc, x, y);

        if (parent instanceof UCDModel) {
            ((UCDModel) parent).addGraphicalElement(useCaseGR);
        } else if (parent instanceof UCDComponentGR) {
            UCDComponentGR element = (UCDComponentGR) parent;
            element.add(useCaseGR);
            useCaseGR.setContext(element);
            SystemWideObjectNamePool.getInstance().objectAdded(useCaseGR);
        }

        return useCaseGR;
    }

    public IXMLCustomStreamable newusecase(Object parent, Element stream, XMLStreamer streamer) {
        UseCase uc = new UseCase(stream.getAttribute("name"));
        return uc;
    }

    public IXMLCustomStreamable newucassociationgr(Object parent, Element stream, XMLStreamer streamer) {
        UCAssociation association = (UCAssociation) streamer.readObjectByID(stream, "link", null);
        UCActorGR actor = (UCActorGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCaseGR useCase = (UseCaseGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCAssociationGR g = new UCAssociationGR(actor, useCase, association);

        ((UCDModel) parent).addGraphicalElement(g);

        return g;
    }

    public IXMLCustomStreamable newucassociation(Object parent, Element stream, XMLStreamer streamer) {
        Actor actor = (Actor) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCase useCase = (UseCase) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCAssociation a = new UCAssociation(actor, useCase);
        return a;
    }

    public IXMLCustomStreamable newucincludegr(Object parent, Element stream, XMLStreamer streamer) {
        UCInclude include = (UCInclude) streamer.readObjectByID(stream, "link", null);
        UseCaseGR from = (UseCaseGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCaseGR to = (UseCaseGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCIncludeGR g = new UCIncludeGR(from, to, include);

        ((UCDModel) parent).addGraphicalElement(g);

        return g;
    }

    public IXMLCustomStreamable newucinclude(Object parent, Element stream, XMLStreamer streamer) {
        UseCase from = (UseCase) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCase to = (UseCase) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to")); 

        UCInclude include = new UCInclude(from, to);
        return include;
    }

    public IXMLCustomStreamable newucgeneralizationgr(Object parent, Element stream, XMLStreamer streamer) {
        UCGeneralization generalization = (UCGeneralization) streamer.readObjectByID(stream, "link", null);
        UCDComponentGR from = (UCDComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UCDComponentGR to = (UCDComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCGeneralizationGR generalizationGR;
        if (from instanceof UseCaseGR && to instanceof UseCaseGR) {
            generalizationGR = new UCGeneralizationGR((UseCaseGR) from, (UseCaseGR) to, generalization);
        } else {
            generalizationGR = new UCGeneralizationGR((UCActorGR) from, (UCActorGR) to, generalization);
        }

        ((UCDModel) parent).addGraphicalElement(generalizationGR);

        return generalizationGR;
    }

    public IXMLCustomStreamable newucgeneralization(Object parent, Element stream, XMLStreamer streamer) {
        UCDComponent from = (UCDComponent) streamer.readObjectByID(stream, "from", null);
        UCDComponent to = (UCDComponent) streamer.readObjectByID(stream, "to", null);

        UCGeneralization generalization;
        if (from instanceof UseCase && to instanceof UseCase) {
            generalization = new UCGeneralization((UseCase) from, (UseCase) to);
        } else {
            generalization = new UCGeneralization((Actor) from, (Actor) to);
        }

        return generalization;
    }

    public IXMLCustomStreamable newucextendgr(Object parent, Element stream, XMLStreamer streamer) {
        UCExtend extend = (UCExtend) streamer.readObjectByID(stream, "link", null);
        UseCaseGR from = (UseCaseGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCaseGR to = (UseCaseGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCExtendGR g = new UCExtendGR(from, to, extend);

        ((UCDModel) parent).addGraphicalElement(g);

        return g;
    }

    public IXMLCustomStreamable newucextend(Object parent, Element stream, XMLStreamer streamer) {
        UseCase from = (UseCase) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        UseCase to = (UseCase) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        UCExtend extend = new UCExtend(from, to);
        return extend;
    }

    public IXMLCustomStreamable newextensionpoint(Object parent, Element stream, XMLStreamer streamer) {
        ExtensionPoint ext = new ExtensionPoint(stream.getAttribute("name"));
        if (parent instanceof UCExtend) {
            UCExtend extend = (UCExtend) parent;
            extend.addExtensionPoint(ext);
        }

        return ext;
    }

    // SSD and SD
    public IXMLCustomStreamable newsdobjectgr(Object parent, Element stream, XMLStreamer streamer) {
        SDObject sd = (SDObject) streamer.readObjectByID(stream, "sdobject", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        SDObjectGR sdObjectGR = new SDObjectGR(sd, x);
        ((SDModel) parent).addGraphicalElement(sdObjectGR);
        return sdObjectGR;
    }

    public IXMLCustomStreamable newactorinstancegr(Object parent, Element stream, XMLStreamer streamer) {
        ActorInstance sd = (ActorInstance) streamer.readObjectByID(stream, "actor", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        ActorInstanceGR actorGR = new ActorInstanceGR(sd, x);

        if (parent instanceof SDModel) {
            ((SDModel) parent).addGraphicalElement(actorGR);
        } else if (parent instanceof SSDModel) {
            ((SSDModel) parent).addGraphicalElement(actorGR);
        }
        return actorGR;
    }

    public IXMLCustomStreamable newsysteminstancegr(Object parent, Element stream, XMLStreamer streamer) {
        SystemInstance sd = (SystemInstance) streamer.readObjectByID(stream, "systeminstance", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        SystemInstanceGR systemGR = new SystemInstanceGR(sd, x);

        ((SSDModel) parent).addGraphicalElement(systemGR);
        return systemGR;
    }

    public IXMLCustomStreamable newmultiobjectgr(Object parent, Element stream, XMLStreamer streamer) {
        MultiObject sd = (MultiObject) streamer.readObjectByID(stream, "multiobject", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        MultiObjectGR multiObjectGR = new MultiObjectGR(sd, x);
        ((SDModel) parent).addGraphicalElement(multiObjectGR);
        return multiObjectGR;
    }

    public IXMLCustomStreamable newcallmessagegr(Object parent, Element stream, XMLStreamer streamer) {
        CallMessage sd = (CallMessage) streamer.readObjectByID(stream, "message", null);

        RoleClassifierGR from = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifierGR to = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        int y = Integer.parseInt(stream.getAttribute("y"));

        CallMessageGR cg = new CallMessageGR(from, to, sd, y);

        if (parent instanceof SDModel) {
            ((SDModel) parent).addGraphicalElement(cg);
        } else if (parent instanceof SSDModel) {
            ((SSDModel) parent).addGraphicalElement(cg);
        }

        return cg;
    }

    public IXMLCustomStreamable newreturnmessagegr(Object parent, Element stream, XMLStreamer streamer) {
        ReturnMessage sd = (ReturnMessage) streamer.readObjectByID(stream, "message", null);

        RoleClassifierGR from = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifierGR to = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        int y = Integer.parseInt(stream.getAttribute("y"));

        ReturnMessageGR cg = new ReturnMessageGR(from, to, sd, y);

        if (parent instanceof SDModel) {
            ((SDModel) parent).addGraphicalElement(cg);
        } else if (parent instanceof SSDModel) {
            ((SSDModel) parent).addGraphicalElement(cg);
        }

        return cg;
    }

    public IXMLCustomStreamable newcreatemessagegr(Object parent, Element stream, XMLStreamer streamer) {
        CreateMessage sd = (CreateMessage) streamer.readObjectByID(stream, "message", null);

        RoleClassifierGR from = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifierGR to = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        int y = Integer.parseInt(stream.getAttribute("y"));

        CreateMessageGR cg = new CreateMessageGR(from, to, sd, y);
        ((SDModel) parent).addGraphicalElement(cg);

        return cg;
    }

    public IXMLCustomStreamable newdestroymessagegr(Object parent, Element stream, XMLStreamer streamer) {
        DestroyMessage sd = (DestroyMessage) streamer.readObjectByID(stream, "message", null);

        RoleClassifierGR from = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifierGR to = (RoleClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        int y = Integer.parseInt(stream.getAttribute("y"));

        DestroyMessageGR cg = new DestroyMessageGR(from, to, sd, y);
        ((SDModel) parent).addGraphicalElement(cg);

        return cg;
    }

    public IXMLCustomStreamable newdestroymessage(Object parent, Element stream, XMLStreamer streamer) {
        RoleClassifier from = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifier to = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));
        DestroyMessage a = new DestroyMessage(from, to);

        return a;
    }

    public IXMLCustomStreamable newcreatemessage(Object parent, Element stream, XMLStreamer streamer) {
        RoleClassifier from = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifier to = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));
        CreateMessage a = new CreateMessage(from, to);

        return a;
    }

    public IXMLCustomStreamable newreturnmessage(Object parent, Element stream, XMLStreamer streamer) {
        RoleClassifier from = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifier to = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        ReturnMessage a = new ReturnMessage(from, to, stream.getAttribute("name"));

        return a;
    }

    public IXMLCustomStreamable newcallmessage(Object parent, Element stream, XMLStreamer streamer) {
        GenericOperation go = (GenericOperation) streamer.readObjectByID(stream, "operation", null);
        RoleClassifier from = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        RoleClassifier to = (RoleClassifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        CallMessage a = new CallMessage(from, to, go);

        return a;
    }

//    public IXMLCustomStreamable newmessageparameter(Object parent, Element stream, XMLStreamer streamer) {
//        MethodParameter m = new MethodParameter(stream.getAttribute("name"));
//        if(parent instanceof CallMessage) {
//         ((CallMessage) parent).addParameter(m);//FIXME: PACKAGE
//        }
//        if(parent instanceof CreateMessage) {
//         ((CreateMessage) parent).addParameter(m);	
//        }
//        return m;
//    }

    public IXMLCustomStreamable newgenericoperation(Object parent, Element stream, XMLStreamer streamer) {
        return new GenericOperation(stream.getAttribute("name"));
    }

    public IXMLCustomStreamable newactorinstance(Object parent, Element stream, XMLStreamer streamer) {
        //Actor base = (Actor)SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("actor"));
        Actor base = (Actor) streamer.readObjectByID(stream, "actor", null);
        ActorInstance a = new ActorInstance(stream.getAttribute("name"), base);
        return a;
    }

    public IXMLCustomStreamable newsysteminstance(Object parent, Element stream, XMLStreamer streamer) {
        System base = (System) streamer.readObjectByID(stream, "system", null);
        SystemInstance s = new SystemInstance(stream.getAttribute("name"), base);
        return s;
    }

    public IXMLCustomStreamable newactor(Object parent, Element stream, XMLStreamer streamer) {
        Actor base = new Actor(stream.getAttribute("name"));
        return base;
    }

    public IXMLCustomStreamable newsystem(Object parent, Element stream, XMLStreamer streamer) {
        System base = new System(stream.getAttribute("name"));
        return base;
    }

    public IXMLCustomStreamable newsdobject(Object parent, Element stream, XMLStreamer streamer) {
        DesignClass base = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("designclass"));
        if (base == null) {
            base = (DesignClass) streamer.readObjectByID(stream, "designclass", null);
            UMLProject.getInstance().getCentralRepository().addClass(base);
        }
        SDObject a = new SDObject(stream.getAttribute("name"), base);
        return a;
    }

    public IXMLCustomStreamable newmultiobject(Object parent, Element stream, XMLStreamer streamer) {
        DesignClass base = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("designclass"));
        if (base == null) {
            base = (DesignClass) streamer.readObjectByID(stream, "designclass", null);
            UMLProject.getInstance().getCentralRepository().addClass(base);
        }
        MultiObject a = new MultiObject(stream.getAttribute("name"), base);
        return a;
    }

    // CCD and DCD
    public IXMLCustomStreamable newinterfacegr(Object parent, Element stream, XMLStreamer streamer) {
        Interface interfaceObject = (Interface) streamer.readObjectByID(stream, "interface", null);
        Point p = new Point(10, 10);
        InterfaceGR interfaceq = new InterfaceGR(interfaceObject, p);
        ((DCDModel) parent).addGraphicalElement(interfaceq);
        return interfaceq;
    }

    public IXMLCustomStreamable newclassgr(Object parent, Element stream, XMLStreamer streamer) {
        DesignClass dc = (DesignClass) streamer.readObjectByID(stream, "designclass", null);
        Point p = new Point(10, 10);
        ClassGR classg = new ClassGR(dc, p);
        ((DCDModel) parent).addGraphicalElement(classg);
        return classg;
    }

    public IXMLCustomStreamable newconceptualclassgr(Object parent, Element stream, XMLStreamer streamer) {
        ConceptualClass cc = (ConceptualClass) streamer.readObjectByID(stream, "conceptualclass", null);
        Point p = new Point(10, 10);
        ConceptualClassGR classg = new ConceptualClassGR(cc, p);
        ((CCDModel) parent).addGraphicalElement(classg);
        return classg;
    }

    public IXMLCustomStreamable newumlnotegr(Object parent, Element stream, XMLStreamer streamer) {
        GraphicalElement to = (GraphicalElement) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));
        Point p = new Point(10, 10);

        UMLNoteGR note = new UMLNoteGR("", to, p);

        ((DiagramModel) parent).addGraphicalElement(note);
        return note;
    }

    public IXMLCustomStreamable newdependencygr(Object parent, Element stream, XMLStreamer streamer) {
        Dependency dependency = (Dependency) streamer.readObjectByID(stream, "dependency", null);
        ClassGR classA = (ClassGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classa"));
        ClassGR classB = (ClassGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classb"));

        DependencyGR g = new DependencyGR(classA, classB, dependency);

        ((DCDModel) parent).addGraphicalElement(g);
        return g;
    }

    public IXMLCustomStreamable newassociationgr(Object parent, Element stream, XMLStreamer streamer) {
        Association association = (Association) streamer.readObjectByID(stream, "association", null);
        ClassifierGR classA = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classa"));
        ClassifierGR classB = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classb"));

        AssociationGR g = new AssociationGR(classA, classB, association);

        if (parent instanceof CCDModel) {
            ((CCDModel) parent).addGraphicalElement(g);
        } else if (parent instanceof DCDModel) {
            ((DCDModel) parent).addGraphicalElement(g);
        }

        return g;
    }

    public IXMLCustomStreamable newassociationclassgr(Object parent, Element stream, XMLStreamer streamer) {
        AbstractAssociationClass associationClass = (AbstractAssociationClass) streamer.readObjectByID(stream, "associationclass", null);

        ClassifierGR classA = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classa"));
        ClassifierGR classB = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classb"));

        AssociationClassGR g = new AssociationClassGR(classA, classB, associationClass);

        if (parent instanceof CCDModel) {
            ((CCDModel) parent).addGraphicalElement(g);
        } else if (parent instanceof DCDModel) {
            ((DCDModel) parent).addGraphicalElement(g);
        }

        return g;
    }

    public IXMLCustomStreamable newaggregationgr(Object parent, Element stream, XMLStreamer streamer) {
        Aggregation aggregation = (Aggregation) streamer.readObjectByID(stream, "aggregation", null);
        ClassifierGR whole = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classa"));
        ClassifierGR part = (ClassifierGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classb"));

        AggregationGR g = new AggregationGR(whole, part, aggregation);

        if (parent instanceof CCDModel) {
            ((CCDModel) parent).addGraphicalElement(g);
        } else if (parent instanceof DCDModel) {
            ((DCDModel) parent).addGraphicalElement(g);
        }

        return g;
    }

    public IXMLCustomStreamable newrealizationgr(Object parent, Element stream, XMLStreamer streamer) {
        Realization realization = (Realization) streamer.readObjectByID(stream, "realization", null);

        ClassGR classA = (ClassGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classa"));
        InterfaceGR classB = (InterfaceGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("interfaceb"));

        RealizationGR g = new RealizationGR(classA, classB, realization);

        ((DCDModel) parent).addGraphicalElement(g);
        return g;
    }

    public IXMLCustomStreamable newgeneralizationgr(Object parent, Element stream, XMLStreamer streamer) {
        Generalization generalization = (Generalization) streamer.readObjectByID(stream, "generalization", null);

        AbstractClassGR base = (AbstractClassGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("base"));
        AbstractClassGR superclass = (AbstractClassGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("super"));

        GeneralizationGR g = null;
        if (base instanceof ConceptualClassGR && superclass instanceof ConceptualClassGR) {
            g = new GeneralizationGR((ConceptualClassGR) superclass, (ConceptualClassGR) base, generalization);
        } else if (base instanceof ClassGR && superclass instanceof ClassGR) {
            g = new GeneralizationGR((ClassGR) superclass, (ClassGR) base, generalization);
        }

        if (parent instanceof CCDModel) {
            ((CCDModel) parent).addGraphicalElement(g);
        } else if (parent instanceof DCDModel) {
            ((DCDModel) parent).addGraphicalElement(g);
        }

        return g;
    }

    public IXMLCustomStreamable newgeneralization(Object parent, Element stream, XMLStreamer streamer) {
        AbstractClass base = (AbstractClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("base"));
        AbstractClass superclass = (AbstractClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("super"));

        Generalization g = null;
        if (base instanceof ConceptualClass && superclass instanceof ConceptualClass) {
            g = new Generalization((ConceptualClass) superclass, (ConceptualClass) base);
        } else if (base instanceof DesignClass && superclass instanceof DesignClass) {
            g = new Generalization((DesignClass) superclass, (DesignClass) base);
        }

        return g;
    }

    public IXMLCustomStreamable newaggregation(Object parent, Element stream, XMLStreamer streamer) {
        Role whole = (Role) streamer.readObjectByID(stream, "rolea", null);
        Role part = (Role) streamer.readObjectByID(stream, "roleb", null);

        Aggregation a = new Aggregation(whole, part, Boolean.parseBoolean(stream.getAttribute("strong")));
        return a;
    }

    public IXMLCustomStreamable newassociation(Object parent, Element stream, XMLStreamer streamer) {
        Role roleA = (Role) streamer.readObjectByID(stream, "rolea", null);
        Role roleB = (Role) streamer.readObjectByID(stream, "roleb", null);

        Association a = new Association(roleA, roleB);
        return a;
    }

    public IXMLCustomStreamable newdesignassociationclass(Object parent, Element stream, XMLStreamer streamer) {
        Role roleA = (Role) streamer.readObjectByID(stream, "rolea", null);
        Role roleB = (Role) streamer.readObjectByID(stream, "roleb", null);

        DesignAssociationClass a = new DesignAssociationClass(roleA, roleB);
        return a;
    }

    public IXMLCustomStreamable newconceptualassociationclass(Object parent, Element stream, XMLStreamer streamer) {
        Role roleA = (Role) streamer.readObjectByID(stream, "rolea", null);
        Role roleB = (Role) streamer.readObjectByID(stream, "roleb", null);

        ConceptualAssociationClass a = new ConceptualAssociationClass(roleA, roleB);
        return a;
    }

    public IXMLCustomStreamable newdependency(Object parent, Element stream, XMLStreamer streamer) {
        DesignClass from = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("from"));
        DesignClass to = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("to"));

        Dependency a = new Dependency(from, to);
        return a;
    }

    public IXMLCustomStreamable newrealization(Object parent, Element stream, XMLStreamer streamer) {
        DesignClass classA = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("a"));
        Interface classB = (Interface) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("b"));

        Realization a = new Realization(classA, classB);
        return a;
    }

    public IXMLCustomStreamable newrole(Object parent, Element stream, XMLStreamer streamer) {
        Role a = new Role((Classifier) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("classifier")));
        return a;

    }

    public IXMLCustomStreamable newdesignclass(Object parent, Element stream, XMLStreamer streamer) {
        GenericClass gc = (GenericClass) streamer.readObjectByID(stream, "generic", null);
        DesignClass dc = new DesignClass(gc);
        return dc;
    }

    public IXMLCustomStreamable newconceptualclass(Object parent, Element stream, XMLStreamer streamer) {
        GenericClass gc = (GenericClass) streamer.readObjectByID(stream, "generic", null);
        ConceptualClass cc = new ConceptualClass(gc);
        return cc;
    }

    public IXMLCustomStreamable newinterface(Object parent, Element stream, XMLStreamer streamer) {
        Interface gc = new Interface(stream.getAttribute("name"));
        return gc;
    }

    public IXMLCustomStreamable newgenericclass(Object parent, Element stream, XMLStreamer streamer) {
        GenericClass gc = new GenericClass(stream.getAttribute("name"));
        return gc;
    }

    public IXMLCustomStreamable newattribute(Object parent, Element stream, XMLStreamer streamer) {
        Attribute a = new Attribute(stream.getAttribute("name"));

        if (parent instanceof ConceptualClass) {
            ((ConceptualClass) parent).addAttribute(a);
        } else if (parent instanceof DesignClass) {
            ((DesignClass) parent).addAttribute(a);
        } else if (parent instanceof AbstractAssociationClass) {
            ((AbstractAssociationClass) parent).addAttribute(a);
        } else {
            java.lang.System.err.println("::::::trying to stream attributes but dont know where?");
        }

        return a;
    }

    public IXMLCustomStreamable newmethod(Object parent, Element stream, XMLStreamer streamer) {
        edu.city.studentuml.model.domain.Method m = new edu.city.studentuml.model.domain.Method(stream.getAttribute("name"));//FIXME: PACKAGE
        if (parent instanceof Interface) {
            ((Interface) parent).addMethod(m);
        } else if (parent instanceof DesignClass) {
            ((DesignClass) parent).addMethod(m);
        } else if (parent instanceof DesignAssociationClass) {
            ((DesignAssociationClass) parent).addMethod(m);
        } else {
            java.lang.System.err.println("::::::trying to stream methods but dont know where?");
        }
        return m;
    }

    public IXMLCustomStreamable newmethodparameter(Object parent, Element stream, XMLStreamer streamer) {
        MethodParameter m = new MethodParameter(stream.getAttribute("name"));
        if (parent instanceof edu.city.studentuml.model.domain.Method) {
            ((edu.city.studentuml.model.domain.Method) parent).addParameter(m);//FIXME: PACKAGE
        } else if (parent instanceof CallMessage) {
            ((CallMessage) parent).addParameter(m);
        } else if (parent instanceof CreateMessage) {
            ((CreateMessage) parent).addParameter(m);
        } else {
            java.lang.System.err.println("::::::trying to stream method parameter but dont know where?");
            java.lang.System.err.println("::::::parent: " + parent + " instanceof " + parent.getClass().getName());
            java.lang.System.err.println("::::::stream element: " + stream);
            java.lang.System.err.println("::::::XMLStreamer: " + streamer);
            throw new RuntimeException();
        }
        return m;
    }

    // AD
    public IXMLCustomStreamable newinitialnodegr(Object parent, Element stream, XMLStreamer streamer) {
        InitialNode initialNode = (InitialNode) streamer.readObjectByID(stream, "initialnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        InitialNodeGR initialNodeGR = new InitialNodeGR(initialNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(initialNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(initialNodeGR);
            initialNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(initialNodeGR);
        }
        return initialNodeGR;
    }

    public IXMLCustomStreamable newinitialnode(Object parent, Element stream, XMLStreamer streamer) {
//        InitialNode n = new InitialNode(stream.getAttribute("name"));
        InitialNode n = new InitialNode();
        return n;
    }

    public IXMLCustomStreamable newactivityfinalnodegr(Object parent, Element stream, XMLStreamer streamer) {
        ActivityFinalNode activityFinalNode = (ActivityFinalNode) streamer.readObjectByID(stream, "activityfinalnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        ActivityFinalNodeGR activityFinalNodeGR = new ActivityFinalNodeGR(activityFinalNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(activityFinalNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(activityFinalNodeGR);
            activityFinalNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(activityFinalNodeGR);
        }
        return activityFinalNodeGR;
    }

    public IXMLCustomStreamable newactivityfinalnode(Object parent, Element stream, XMLStreamer streamer) {
//        ActivityFinalNode n = new ActivityFinalNode(stream.getAttribute("name"));
        ActivityFinalNode n = new ActivityFinalNode();
        return n;
    }

    public IXMLCustomStreamable newflowfinalnodegr(Object parent, Element stream, XMLStreamer streamer) {
        FlowFinalNode flowFinalNode = (FlowFinalNode) streamer.readObjectByID(stream, "flowfinalnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        FlowFinalNodeGR flowFinalNodeGR = new FlowFinalNodeGR(flowFinalNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(flowFinalNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(flowFinalNodeGR);
            flowFinalNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(flowFinalNodeGR);
        }
        return flowFinalNodeGR;
    }

    public IXMLCustomStreamable newflowfinalnode(Object parent, Element stream, XMLStreamer streamer) {
//        FlowFinalNode n = new FlowFinalNode(stream.getAttribute("name"));
        FlowFinalNode n = new FlowFinalNode();
        return n;
    }

    public IXMLCustomStreamable newactionnodegr(Object parent, Element stream, XMLStreamer streamer) {
        ActionNode actionNode = (ActionNode) streamer.readObjectByID(stream, "actionnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        ActionNodeGR actionNodeGR = new ActionNodeGR(actionNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(actionNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(actionNodeGR);
            actionNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(actionNodeGR);
        }
        return actionNodeGR;
    }

    public IXMLCustomStreamable newactionnode(Object parent, Element stream, XMLStreamer streamer) {
        ActionNode n = new ActionNode(stream.getAttribute("name"));
        return n;
    }

    public IXMLCustomStreamable newmergenodegr(Object parent, Element stream, XMLStreamer streamer) {
        MergeNode mergeNode = (MergeNode) streamer.readObjectByID(stream, "mergenode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        MergeNodeGR mergeNodeGR = new MergeNodeGR(mergeNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(mergeNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(mergeNodeGR);
            mergeNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(mergeNodeGR);
        }
        return mergeNodeGR;
    }

    public IXMLCustomStreamable newmergenode(Object parent, Element stream, XMLStreamer streamer) {
//        MergeNode n = new MergeNode(stream.getAttribute("name"));
        MergeNode n = new MergeNode();
        return n;
    }

    public IXMLCustomStreamable newdecisionnodegr(Object parent, Element stream, XMLStreamer streamer) {
        DecisionNode decisionNode = (DecisionNode) streamer.readObjectByID(stream, "decisionnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        DecisionNodeGR decisionNodeGR = new DecisionNodeGR(decisionNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(decisionNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(decisionNodeGR);
            decisionNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(decisionNodeGR);
        }
        return decisionNodeGR;
    }

    public IXMLCustomStreamable newdecisionnode(Object parent, Element stream, XMLStreamer streamer) {
//        DecisionNode n = new DecisionNode(stream.getAttribute("name"));
        DecisionNode n = new DecisionNode();
        return n;
    }

    public IXMLCustomStreamable newforknodegr(Object parent, Element stream, XMLStreamer streamer) {
        ForkNode forkNode = (ForkNode) streamer.readObjectByID(stream, "forknode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        ForkNodeGR forkNodeGR = new ForkNodeGR(forkNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(forkNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(forkNodeGR);
            forkNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(forkNodeGR);
        }
        return forkNodeGR;
    }

    public IXMLCustomStreamable newforknode(Object parent, Element stream, XMLStreamer streamer) {
//        ForkNode n = new ForkNode(stream.getAttribute("name"));
        ForkNode n = new ForkNode();
        return n;
    }

    public IXMLCustomStreamable newjoinnodegr(Object parent, Element stream, XMLStreamer streamer) {
        JoinNode joinNode = (JoinNode) streamer.readObjectByID(stream, "joinnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        JoinNodeGR joinNodeGR = new JoinNodeGR(joinNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(joinNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(joinNodeGR);
            joinNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(joinNodeGR);
        }
        return joinNodeGR;
    }

    public IXMLCustomStreamable newjoinnode(Object parent, Element stream, XMLStreamer streamer) {
//        JoinNode n = new JoinNode(stream.getAttribute("name"));
        JoinNode n = new JoinNode();
        return n;
    }

    public IXMLCustomStreamable newobjectnodegr(Object parent, Element stream, XMLStreamer streamer) {
        ObjectNode objectNode = (ObjectNode) streamer.readObjectByID(stream, "objectnode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        ObjectNodeGR objectNodeGR = new ObjectNodeGR(objectNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(objectNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(objectNodeGR);
            objectNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(objectNodeGR);
        }
        return objectNodeGR;
    }

    public IXMLCustomStreamable newobjectnode(Object parent, Element stream, XMLStreamer streamer) {
//        ObjectNode n = new ObjectNode(stream.getAttribute("name"));
        ObjectNode n = new ObjectNode();
        n.setName(stream.getAttribute("name"));
        return n;
    }

    public IXMLCustomStreamable newstate(Object parent, Element stream, XMLStreamer streamer) {
        State s = new State(stream.getAttribute("name"));
        if (parent instanceof ObjectNode) {
            ObjectNode node = (ObjectNode) parent;
            node.addState(s);
        }
        return s;
    }

    public IXMLCustomStreamable newactivitynodegr(Object parent, Element stream, XMLStreamer streamer) {
        ActivityNode activityNode = (ActivityNode) streamer.readObjectByID(stream, "activitynode", null);
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        ActivityNodeGR activityNodeGR = new ActivityNodeGR(activityNode, x, y);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(activityNodeGR);
        } else if (parent instanceof NodeComponentGR) {
            NodeComponentGR node = (NodeComponentGR) parent;
            node.add(activityNodeGR);
            activityNodeGR.setContext(node);
            SystemWideObjectNamePool.getInstance().objectAdded(activityNodeGR);
        }
        return activityNodeGR;
    }

    public IXMLCustomStreamable newactivitynode(Object parent, Element stream, XMLStreamer streamer) {
//        ActivityNode n = new ActivityNode(stream.getAttribute("name"));
        ActivityNode n = new ActivityNode();
        n.setName(stream.getAttribute("name"));
        return n;
    }

    public IXMLCustomStreamable newcontrolflowgr(Object parent, Element stream, XMLStreamer streamer) {
        ControlFlow controlFlow = (ControlFlow) streamer.readObjectByID(stream, "controlflow", null);
        NodeComponentGR source = (NodeComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("source"));
        NodeComponentGR target = (NodeComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("target"));

        ControlFlowGR controlFlowGR = new ControlFlowGR(source, target, controlFlow);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(controlFlowGR);
        }

        return controlFlowGR;
    }

    public IXMLCustomStreamable newcontrolflow(Object parent, Element stream, XMLStreamer streamer) {
        NodeComponent source = (NodeComponent) streamer.readObjectByID(stream, "source", null);
        NodeComponent target = (NodeComponent) streamer.readObjectByID(stream, "target", null);

        ControlFlow flow = new ControlFlow(source, target);
        return flow;
    }

    public IXMLCustomStreamable newobjectflowgr(Object parent, Element stream, XMLStreamer streamer) {
        ObjectFlow objectFlow = (ObjectFlow) streamer.readObjectByID(stream, "objectflow", null);
        NodeComponentGR source = (NodeComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("source"));
        NodeComponentGR target = (NodeComponentGR) SystemWideObjectNamePool.getInstance().getObjectByName(stream.getAttribute("target"));

        ObjectFlowGR objectFlowGR = new ObjectFlowGR(source, target, objectFlow);

        if (parent instanceof ADModel) {
            ((ADModel) parent).addGraphicalElement(objectFlowGR);
        }

        return objectFlowGR;
    }

    public IXMLCustomStreamable newobjectflow(Object parent, Element stream, XMLStreamer streamer) {
        NodeComponent source = (NodeComponent) streamer.readObjectByID(stream, "source", null);
        NodeComponent target = (NodeComponent) streamer.readObjectByID(stream, "target", null);

        ObjectFlow flow = new ObjectFlow(source, target);
        return flow;
    }

    public IXMLCustomStreamable newendpointgr(Object parent, Element stream, XMLStreamer streamer) {
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        EndPointGR p = new EndPointGR(x, y);
        if (parent instanceof EdgeGR) {
            EdgeGR edge = (EdgeGR) parent;
            edge.addPoint(p);
        }
        return p;
    }

    public IXMLCustomStreamable newpointgr(Object parent, Element stream, XMLStreamer streamer) {
        int x = Integer.parseInt(stream.getAttribute("x"));
        int y = Integer.parseInt(stream.getAttribute("y"));
        PointGR p = new PointGR(x, y);
        if (parent instanceof EdgeGR) {
            EdgeGR edge = (EdgeGR) parent;
            edge.addPoint(p);
        }
        return p;
    }

    private void notifyApplicationGUI(DiagramModel model, Element stream) {
        Rectangle R = readRect(stream.getAttribute("framex"));
        boolean selected = Boolean.parseBoolean(stream.getAttribute("selected"));
        boolean iconified = Boolean.parseBoolean(stream.getAttribute("iconified"));
        
        FrameProperties frameProperties = new FrameProperties(model, R, selected, iconified);
        logger.fine("Notifying observers: " + this.countObservers());
        setChanged();
        notifyObservers(frameProperties);
    }
}
