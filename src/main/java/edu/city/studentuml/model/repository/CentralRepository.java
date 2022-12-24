package edu.city.studentuml.model.repository;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CentralRepository.java
//Class CentralRepository is the core of the application data.
//It represents the domain model layer of the model component in MVC.
//All domain concepts of UML diagrams, such as classes, interfaces, messages, etc.
//are stored here in their own lists. These concepts do not keep
//graphical rendering information as is the case with GraphicalElements in UML diagrams.
//Whatever UML element is added in a diagram, its corresponding domain concept
//is added to this repository. There exists only one Central Repository for each project.
import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.domain.AbstractClass;
import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.domain.ControlNode;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Edge;
import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.GenericAttribute;
import edu.city.studentuml.model.domain.GenericClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.SDMessage;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCLink;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.NotifierVector;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

public class CentralRepository extends Observable implements Serializable {
    private static final Logger logger = Logger.getLogger(CentralRepository.class.getName());

    private Vector datatypes;
    private NotifierVector useCases;
    private NotifierVector ucLinks;
    private NotifierVector actorInstances;
    private NotifierVector systemInstances;
    private NotifierVector systems;
    private NotifierVector actors;
    private NotifierVector aggregations;
    private NotifierVector associations;
    private NotifierVector classes;
    private NotifierVector concepts;
    private NotifierVector conceptualAssociationClasses;
    private NotifierVector designAssociationClasses;
    private NotifierVector dependencies;
    private NotifierVector generalizations;
    private NotifierVector genericAttributes;
    private NotifierVector genericClasses;
    private NotifierVector genericOperations;
    private NotifierVector interfaces;
    private NotifierVector multiObjects;
    private NotifierVector objects;
    private NotifierVector realizations;
    private NotifierVector sdMessages;
    private NotifierVector ssdMessages;
    private NotifierVector controlFlows;
    private NotifierVector objectFlows;
    private NotifierVector actionNodes;
    private NotifierVector initialNodes;
    private NotifierVector activityFinalNodes;
    private NotifierVector flowFinalNodes;
    private NotifierVector decisionNodes;
    private NotifierVector mergeNodes;
    private NotifierVector forkNodes;
    private NotifierVector joinNodes;
    private NotifierVector objectNodes;
    private NotifierVector activityNodes;

    public CentralRepository() {

        datatypes = new Vector();
        initialiseDataTypes(); // all the lists of domain concepts created in a project

        ucLinks = new NotifierVector();
        useCases = new NotifierVector();

        // basic shared concepts
        genericClasses = new NotifierVector();
        genericAttributes = new NotifierVector();
        genericOperations = new NotifierVector();

        classes = new NotifierVector();
        interfaces = new NotifierVector();
        associations = new NotifierVector();
        generalizations = new NotifierVector();
        aggregations = new NotifierVector();
        realizations = new NotifierVector();
        dependencies = new NotifierVector();
        designAssociationClasses = new NotifierVector();

        concepts = new NotifierVector();
        conceptualAssociationClasses = new NotifierVector();

        systems = new NotifierVector();
        systemInstances = new NotifierVector();
        actors = new NotifierVector();
        actorInstances = new NotifierVector();
        objects = new NotifierVector();
        multiObjects = new NotifierVector();
        sdMessages = new NotifierVector();
        ssdMessages = new NotifierVector();

        controlFlows = new NotifierVector();
        objectFlows = new NotifierVector();
        actionNodes = new NotifierVector();
        initialNodes = new NotifierVector();
        activityFinalNodes = new NotifierVector();
        flowFinalNodes = new NotifierVector();
        decisionNodes = new NotifierVector();
        mergeNodes = new NotifierVector();
        forkNodes = new NotifierVector();
        joinNodes = new NotifierVector();
        objectNodes = new NotifierVector();
        activityNodes = new NotifierVector();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        logger.fine("OBSERVER added: " + o.toString());
        super.addObserver(o);
    }

    public Vector getAllObjects() {
        Vector v = new Vector();

        v.addAll(actorInstances);
        v.addAll(actors);
        v.addAll(aggregations);
        v.addAll(associations);
        v.addAll(classes);
        v.addAll(concepts);
        v.addAll(getDatatypes());
        v.addAll(dependencies);
        v.addAll(generalizations);
        v.addAll(genericAttributes);
        v.addAll(genericClasses);
        v.addAll(genericOperations);
        v.addAll(interfaces);
        v.addAll(multiObjects);
        v.addAll(objects);
        v.addAll(realizations);
        v.addAll(sdMessages);
        v.addAll(ssdMessages);

        v.addAll(ucLinks);
        v.addAll(useCases);
        v.addAll(designAssociationClasses);
        v.addAll(conceptualAssociationClasses);
        v.addAll(systems);
        v.addAll(systemInstances);

        v.addAll(controlFlows);
        v.addAll(objectFlows);
        v.addAll(actionNodes);
        v.addAll(initialNodes);
        v.addAll(activityFinalNodes);
        v.addAll(flowFinalNodes);
        v.addAll(decisionNodes);
        v.addAll(mergeNodes);
        v.addAll(forkNodes);
        v.addAll(joinNodes);
        v.addAll(objectNodes);
        v.addAll(activityNodes);

        return v;
    }

    public void clear() {

        classes.clear();
        interfaces.clear();
        associations.clear();
        generalizations.clear();
        aggregations.clear();
        realizations.clear();
        dependencies.clear();
        // getDatatypes().clear();
        concepts.clear();
        objects.clear();
        multiObjects.clear();
        actors.clear();
        actorInstances.clear();
        sdMessages.clear();
        ssdMessages.clear();
        genericClasses.clear();
        genericAttributes.clear();
        genericOperations.clear();

        ucLinks.clear();
        useCases.clear();
        designAssociationClasses.clear();
        conceptualAssociationClasses.clear();
        systems.clear();
        systemInstances.clear();

        controlFlows.clear();
        objectFlows.clear();
        actionNodes.clear();
        initialNodes.clear();
        activityFinalNodes.clear();
        flowFinalNodes.clear();
        decisionNodes.clear();
        mergeNodes.clear();
        forkNodes.clear();
        joinNodes.clear();
        objectNodes.clear();
        activityNodes.clear();
    }

    // this method occurs whenever a change in the repository occurs to notify
    // observers
    public void repositoryChanged() {
        logger.fine("Notifying observers: " + this.countObservers());
        setChanged();
        notifyObservers();

    }

    private void initialiseDataTypes() {
        datatypes.add(DataType.VOID);
        datatypes.add(DataType.INTEGER);
        datatypes.add(DataType.FLOAT);
        datatypes.add(DataType.DOUBLE);
        datatypes.add(DataType.BOOLEAN);
        datatypes.add(DataType.LONG);
        datatypes.add(DataType.BYTE);
        datatypes.add(DataType.STRING);
    }

    public void setDatatypes(Vector datatypes) {
        this.datatypes = datatypes;
    }

    public Vector getDatatypes() {
        return datatypes;
    }

    public Vector getTypes() {
        Vector types = new Vector();
        types.addAll(datatypes);
        types.addAll(classes);
        types.addAll(interfaces);

        return types;
    }

    // methods for manipulating the list of conceptual classes
    public boolean addConceptualClass(ConceptualClass c) {
        ConceptualClass existingClass = getConceptualClass(c.getName());

        if ((existingClass == null) || c.getName().equals("")) {
            concepts.add(c);

            GenericClass gc = getGenericClass(c.getGenericClass().getName());

            if ((gc == null) || gc.getName().equals("")) {
                genericClasses.add(c.getGenericClass());
            } else {
                c.setGenericClass(gc);
            }

            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public ConceptualClass getConceptualClass(String name) {
        ConceptualClass cc;
        Iterator iterator = concepts.iterator();

        while (iterator.hasNext()) {
            cc = (ConceptualClass) iterator.next();

            if (cc.getName().equals(name)) {
                return cc;
            }
        }

        return null;
    }

    public boolean editConceptualClass(ConceptualClass originalClass, ConceptualClass newClass) {
        ConceptualClass existingClass = getConceptualClass(newClass.getName());

        if (!originalClass.getName().equals(newClass.getName()) && (existingClass != null)
                && !newClass.getName().equals("")) {
            return false;
        }

        GenericClass existingGenericClass = getGenericClass(newClass.getName());

        if ((existingGenericClass == null) || existingGenericClass.getName().equals("")) {
            originalClass.setName(newClass.getName());
        } else {
            originalClass.setGenericClass(newClass.getGenericClass());
        }

        originalClass.setAttributes(newClass.getAttributes());
        repositoryChanged();

        return true;
    }

    public boolean removeConceptualClass(ConceptualClass c) {
        if (concepts.remove(c)) {
            removeGenericClass(c.getGenericClass());
            repositoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public Vector getConceptualClasses() {
        return concepts;
    }

    // methods for manipulating the list of project classes
    public boolean addClass(DesignClass c) {
        logger.fine("Adding class:" + c.getName());
        DesignClass existingClass = getDesignClass(c.getName());

        // if a class with the same name doesn't exist, or the class hasn't been named
        // yet,
        // add it and return true; else return false
        if ((existingClass == null) || c.getName().equals("")) {
            classes.add(c);

            // if a generic class with the same name exists, set it as the classe's
            // generic class, otherwise add it to the repository
            GenericClass gc = getGenericClass(c.getGenericClass().getName());

            if ((gc == null) || gc.getName().equals("")) {
                genericClasses.add(c.getGenericClass());
            } else {
                c.setGenericClass(gc);
            }

            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editClass(DesignClass originalClass, DesignClass newClass) {
        DesignClass existingClass = getDesignClass(newClass.getName());

        // if the name of the class is changed and the new name causes conflict
        // with an existing class, and the new name is non-blank, then don't edit
        // and return false
        if (!originalClass.getName().equals(newClass.getName()) && (existingClass != null)
                && !newClass.getName().equals("")) {
            return false;
        }

        GenericClass existingGenericClass = getGenericClass(newClass.getName());

        if ((existingGenericClass == null) || existingGenericClass.getName().equals("")) {
            originalClass.setName(newClass.getName());
        } else {
            originalClass.setGenericClass(newClass.getGenericClass());
        }

        originalClass.setStereotype(newClass.getStereotype());
        originalClass.setAttributes(newClass.getAttributes());
        originalClass.setMethods(newClass.getMethods());
        repositoryChanged();

        return true;
    }

    public boolean removeClass(DesignClass c) {
        if (classes.remove(c)) {
            removeGenericClass(c.getGenericClass());
            repositoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public Vector getClasses() {
        return classes;
    }

    // this method retrieves a design class by its name
    // If no match is found, the method returns null
    // This method is useful when adding a graphical class
    // in a design class diagram in order to determine if there is a
    // class with the same name.
    public DesignClass getDesignClass(String name) {
        DesignClass dc;
        Iterator iterator = classes.iterator();

        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();

            if (dc.getName().equals(name)) {
                return dc;
            }
        }

        return null;
    }

    // methods for manipulating the list of project interfaces
    public boolean addInterface(Interface i) {
        Interface existingInterface = getInterface(i.getName());

        if ((existingInterface == null) || i.getName().equals("")) {
            interfaces.add(i);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful, false if there is an existing
    // interface with the same name
    public boolean editInterface(Interface originalInterface, Interface newInterface) {
        Interface existingInterface = getInterface(newInterface.getName());

        if (!originalInterface.getName().equals(newInterface.getName()) && (existingInterface != null)
                && !newInterface.getName().equals("")) {
            return false;
        }

        originalInterface.setName(newInterface.getName());
        originalInterface.setMethods(newInterface.getMethods());
        repositoryChanged();

        return true;
    }

    public boolean removeInterface(Interface i) {
        if (interfaces.remove(i)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getInterfaces() {
        return interfaces;
    }

    // this method retrieves an interface by its name
    // If no match is found, the method returns null
    // This method is useful when adding a graphical interface
    // in a design class diagram in order to determine if there is an
    // inteface with the same name.
    public Interface getInterface(String name) {
        Interface interf;
        Iterator iterator = interfaces.iterator();

        while (iterator.hasNext()) {
            interf = (Interface) iterator.next();

            if (interf.getName().equals(name)) {
                return interf;
            }
        }

        return null;
    }

    // methods for manipulating the list of project associations
    public boolean addAssociation(Association a) {
        associations.add(a);
        repositoryChanged();

        return true;
    }

    public boolean removeAssociation(Association a) {
        if (associations.remove(a)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getAssociations() {
        return associations;
    }

    public boolean addAssociationClass(AbstractAssociationClass associationClass) {
        // associationClass can either be conceptual or design
        if (associationClass instanceof ConceptualAssociationClass) {
            conceptualAssociationClasses.add((ConceptualAssociationClass) associationClass);
        } else if (associationClass instanceof DesignAssociationClass) {
            designAssociationClasses.add((DesignAssociationClass) associationClass);
        }
        repositoryChanged();

        return true;
    }

    public boolean removeAssociationClass(AbstractAssociationClass a) {
        if (a instanceof ConceptualAssociationClass) {
            if (conceptualAssociationClasses.remove((ConceptualAssociationClass) a)) {
                repositoryChanged();
                return true;
            }
        } else if (a instanceof DesignAssociationClass) {
            if (designAssociationClasses.remove((DesignAssociationClass) a)) {
                repositoryChanged();
                return true;
            }
        }

        return false;
    }

    public Vector getConceptualAssociationClasses() {
        return conceptualAssociationClasses;
    }

    public Vector getDesignAssociationClasses() {
        return designAssociationClasses;
    }

    // methods for manipulating the list of project generalizations
    // adds the generalization between two classes only if there
    // doesn't exists one between them as there is no need for more than one
    public boolean addGeneralization(Generalization g) {
        if (getGeneralization(g.getSuperClass(), g.getBaseClass()) == null) {
            generalizations.add(g);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeGeneralization(Generalization g) {
        if (generalizations.remove(g)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getGeneralizations() {
        return generalizations;
    }

    // retrieves the generalization that exists between two classes, if any
    public Generalization getGeneralization(Classifier parent, Classifier child) {
        Generalization generalization;
        Iterator iterator = generalizations.iterator();
        while (iterator.hasNext()) {
            generalization = (Generalization) iterator.next();

            if ((generalization.getSuperClass() == parent) && (generalization.getBaseClass() == child)) {
                return generalization;
            }
        }

        return null;
    }

    // methods for manipulating the list of project aggregations
    public boolean addAggregation(Aggregation a) {
        aggregations.add(a);
        repositoryChanged();

        return true;
    }

    public boolean removeAggregation(Aggregation a) {
        if (aggregations.remove(a)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getAggregations() {
        return aggregations;
    }

    // methods for manipulating the list of project realizations
    // adds the realization between one class and one interface only if there
    // doesn't exists one between them as there is no need for more than one
    public boolean addRealization(Realization r) {
        if (getRealization(r.getTheClass(), r.getTheInterface()) == null) {
            realizations.add(r);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeRealization(Realization r) {
        if (realizations.remove(r)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getRealizations() {
        return realizations;
    }

    // retrieves the realization that exists between
    // one class and one interface, if any
    public Realization getRealization(DesignClass c, Interface i) {
        Iterator iterator = realizations.iterator();
        Realization realization;

        while (iterator.hasNext()) {
            realization = (Realization) iterator.next();

            if ((realization.getTheClass() == c) && (realization.getTheInterface() == i)) {
                return realization;
            }
        }

        return null;
    }

    // methods for manipulating the list of project dependencies
    // adds the dependency between two classes only if there
    // doesn't exists one between them as there is no need for more than one
    public boolean addDependency(Dependency d) {
        if (getDependency(d.getFrom(), d.getTo()) == null) {
            dependencies.add(d);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeDependency(Dependency d) {
        if (dependencies.remove(d)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getDependencies() {
        return dependencies;
    }

    // returns the dependency that exists between two classes, if any
    public Dependency getDependency(DesignClass from, DesignClass to) {
        Iterator iterator = dependencies.iterator();
        Dependency dependency;

        while (iterator.hasNext()) {
            dependency = (Dependency) iterator.next();

            if ((dependency.getFrom() == from) && (dependency.getTo() == to)) {
                return dependency;
            }
        }

        return null;
    }

    // methods for manipulating the list of project objects
    // appearing in sequence diagrams
    public boolean addSystemInstance(SystemInstance s) {
        SystemInstance existingSystem = getSystemInstance(s.getName());
        if (existingSystem == null || s.getName().equals("")) {
            systemInstances.add(s);
            repositoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean editSystemInstance(SystemInstance originalSystemInstance, SystemInstance newSystemInstance) {
        SystemInstance existingSystemInstance = getSystemInstance(newSystemInstance.getName());

        if (!originalSystemInstance.getName().equals(newSystemInstance.getName()) && (existingSystemInstance != null)
                && !newSystemInstance.getName().equals("")) {
            return false;
        }

        originalSystemInstance.setName(newSystemInstance.getName());
        originalSystemInstance.setSystem(newSystemInstance.getSystem());
        repositoryChanged();

        return true;
    }

    public SystemInstance getSystemInstance(String name) {
        SystemInstance s;
        Iterator iterator = systemInstances.iterator();

        while (iterator.hasNext()) {
            s = (SystemInstance) iterator.next();

            if (s.getName().equals(name)) {
                return s;
            }
        }

        return null;
    }

    public Vector getSystemInstances() {
        return systemInstances;
    }

    public boolean addSystem(System s) {
        System existingSystem = getSystem(s.getName());

        if ((existingSystem == null) || s.getName().equals("")) {
            systems.add(s);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeSystem(System s) {
        if (systems.remove(s)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean editSystem(System originalSystem, System newSystem) {
        System existingSystem = getSystem(newSystem.getName());
        if (!originalSystem.getName().equals(newSystem.getName()) && (existingSystem != null)
                && !newSystem.getName().equals("")) {
            return false;
        }

        originalSystem.setName(newSystem.getName());
        repositoryChanged();

        return true;
    }

    public System getSystem(String name) {
        System s;
        Iterator iterator = systems.iterator();
        while (iterator.hasNext()) {
            s = (System) iterator.next();

            if (s.getName().equals(name)) {
                return s;
            }
        }

        return null;
    }

    public Vector getSystems() {
        return systems;
    }

    public boolean removeSystemInstance(SystemInstance s) {
        if (systemInstances.remove(s)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean addObject(SDObject o) {
        logger.fine("Adding object " + o.toString());
        SDObject existingObject = getObject(o.getName());

        if ((existingObject == null) || o.getName().equals("")) {
            objects.add(o);
            repositoryChanged();

            logger.finer("objects: " + objects);
            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editObject(SDObject originalObject, SDObject newObject) {
        SDObject existingObject = getObject(newObject.getName());

        if (!originalObject.getName().equals(newObject.getName()) && (existingObject != null)
                && !newObject.getName().equals("")) {
            return false;
        }

        originalObject.setName(newObject.getName());
        originalObject.setDesignClass(newObject.getDesignClass());

        repositoryChanged();
        return true;
    }

    public boolean removeObject(SDObject o) {
        if (objects.remove(o)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getObjects() {
        return objects;
    }

    // this method retrieves an object by its name
    // If no match is found, the method returns null
    // This method is useful when adding a graphical object
    // in a sequence diagram in order to determine if there is an
    // object with the same name.
    public SDObject getObject(String name) {
        SDObject o;
        Iterator iterator = objects.iterator();

        while (iterator.hasNext()) {
            o = (SDObject) iterator.next();

            if (o.getName().equals(name)) {
                return o;
            }
        }

        return null;
    }

    // methods for manipulating the list of project multi-objects
    // appearing in sequence diagrams
    public boolean addMultiObject(MultiObject mo) {
        MultiObject existingMultiObject = getMultiObject(mo.getName());

        if ((existingMultiObject == null) || mo.getName().equals("")) {
            multiObjects.add(mo);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editMultiObject(MultiObject originalObject, MultiObject newObject) {
        MultiObject existingObject = getMultiObject(newObject.getName());

        if (!originalObject.getName().equals(newObject.getName()) && (existingObject != null)
                && !newObject.getName().equals("")) {
            return false;
        }

        originalObject.setName(newObject.getName());
        originalObject.setDesignClass(newObject.getDesignClass());
        repositoryChanged();

        return true;
    }

    public boolean removeMultiObject(MultiObject mo) {
        if (multiObjects.remove(mo)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getMultiObjects() {
        return multiObjects;
    }

    // this method retrieves a multi-object by its name
    // If no match is found, the method returns null
    public MultiObject getMultiObject(String name) {
        MultiObject mo;
        Iterator iterator = multiObjects.iterator();

        while (iterator.hasNext()) {
            mo = (MultiObject) iterator.next();

            if (mo.getName().equals(name)) {
                return mo;
            }
        }

        return null;
    }

    // methods for manipulating the list of project actors
    public boolean addActor(Actor a) {
        Actor existingActor = getActor(a.getName());

        if ((existingActor == null) || a.getName().equals("")) {
            actors.add(a);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editActor(Actor originalActor, Actor newActor) {
        Actor existingActor = getActor(newActor.getName());

        if (!originalActor.getName().equals(newActor.getName()) && (existingActor != null)
                && !newActor.getName().equals("")) {
            return false;
        }

        originalActor.setName(newActor.getName());
        repositoryChanged();

        return true;
    }

    public boolean removeActor(Actor a) {
        if (actors.remove(a)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getActors() {
        return actors;
    }

    // this method retrieves an actor by its name
    // If no match is found, the method returns null
    public Actor getActor(String name) {
        Actor a;
        Iterator iterator = actors.iterator();

        while (iterator.hasNext()) {
            a = (Actor) iterator.next();

            if (a.getName().equals(name)) {
                return a;
            }
        }

        return null;
    }

    // methods for manipulating the list of actor instances
    // in sequence diagrams
    public boolean addActorInstance(ActorInstance a) {
        ActorInstance existingActor = getActorInstance(a.getName());

        if ((existingActor == null) || a.getName().equals("")) {
            actorInstances.add(a);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editActorInstance(ActorInstance originalActorInstance, ActorInstance newActorInstance) {
        ActorInstance existingActorInstance = getActorInstance(newActorInstance.getName());

        if (!originalActorInstance.getName().equals(newActorInstance.getName()) && (existingActorInstance != null)
                && !newActorInstance.getName().equals("")) {
            return false;
        }

        originalActorInstance.setName(newActorInstance.getName());
        originalActorInstance.setActor(newActorInstance.getActor());
        repositoryChanged();

        return true;
    }

    public boolean removeActorInstance(ActorInstance a) {
        if (actorInstances.remove(a)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getActorInstances() {
        return actorInstances;
    }

    // this method retrieves an actor instance by its name
    // If no match is found, the method returns null
    public ActorInstance getActorInstance(String name) {
        ActorInstance a;
        Iterator iterator = actorInstances.iterator();

        while (iterator.hasNext()) {
            a = (ActorInstance) iterator.next();

            if (a.getName().equals(name)) {
                return a;
            }
        }

        return null;
    }

    // methods for manipulating the list of project sequence diagram messages
    public boolean addSDMessage(SDMessage m) {
        logger.fine("Adding message " + m.toString());
        sdMessages.add(m);
        repositoryChanged();

        return true;
    }

    public boolean removeSDMessage(SDMessage m) {
        if (sdMessages.remove(m)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getSDMessages() {
        return sdMessages;
    }

    // The following methods manage the lists of generic concepts which can be
    // shared by more than one type of UML concept. For example, a generic operation
    // can be referred to by a System Sequence Diagram message, a Sequence Diagram
    // message,
    // and by a Class Method
    // methods for manipulating the list of generic classes
    public boolean addGenericClass(GenericClass gc) {
        genericClasses.add(gc);
        repositoryChanged();

        return true;
    }

    public boolean removeGenericClass(GenericClass gc) {
        if (genericClasses.remove(gc)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getGenericClasses() {
        return genericClasses;
    }

    // this method retrieves a generic class by its name
    // If no match is found, the method returns null
    // This method is useful when adding a design or conceptual class
    // in the repository in order to determine if there
    // already exists a generic class with the same name
    public GenericClass getGenericClass(String name) {
        GenericClass gc;
        Iterator iterator = genericClasses.iterator();

        while (iterator.hasNext()) {
            gc = (GenericClass) iterator.next();

            if (gc.getName().equals(name)) {
                return gc;
            }
        }

        return null;
    }

    // methods for manipulating the list of generic operations
    public boolean addGenericOperation(GenericOperation go) {
        genericOperations.add(go);
        repositoryChanged();

        return true;
    }

    public boolean removeGenericOperation(GenericOperation go) {
        if (genericOperations.remove(go)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getGenericOperations() {
        return genericOperations;
    }

    // this method retrieves a generic operation by its name
    // If no match is found, the method returns null
    // This method is useful when adding a message or class method
    // in the repository in order to determine if there
    // already exists a generic operation with the same name AND belonging to the
    // same class
    // to which the element can refer
    public GenericOperation getGenericOperation(String name, GenericClass parentClass) {
        GenericOperation go;
        DesignClass designClass;
        Iterator iterator = classes.iterator();

        while (iterator.hasNext()) {
            designClass = (DesignClass) iterator.next();

            if (designClass.getGenericClass() == parentClass) {
                Iterator goIterator = genericOperations.iterator();

                while (goIterator.hasNext()) {
                    go = (GenericOperation) goIterator.next();

                    if (go.getName().equals(name)) {
                        return go;
                    }
                }
            }
        }

        return null;
    }

    // methods for manipulating the list of generic attributes
    public boolean addGenericAttribute(GenericAttribute ga) {
        genericAttributes.add(ga);
        repositoryChanged();

        return true;
    }

    public boolean removeGenericAttribute(GenericAttribute ga) {
        if (genericAttributes.remove(ga)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public Vector getGenericAttributes() {
        return genericAttributes;
    }

    // this method retrieves a generic attribute by its name
    // If no match is found, the method returns null
    // This method is useful when adding a design or conceptual class
    // attribute in the repository in order to determine if there
    // already exists a generic attribute with the same name and belonging to same
    // class
    public GenericAttribute getGenericAttribute(String name, GenericClass parentClass) {
        GenericAttribute ga;

        DesignClass designClass;
        Iterator iterator = classes.iterator();
        while (iterator.hasNext()) {
            designClass = (DesignClass) iterator.next();

            if (designClass.getGenericClass() == parentClass) {
                Iterator gaIterator = genericAttributes.iterator();

                while (gaIterator.hasNext()) {
                    ga = (GenericAttribute) gaIterator.next();

                    if (ga.getName().equals(name)) {
                        return ga;
                    }
                }
            }
        }

        ConceptualClass concept;
        iterator = concepts.iterator();
        while (iterator.hasNext()) {
            concept = (ConceptualClass) iterator.next();

            if (concept.getGenericClass() == parentClass) {
                Iterator gaIterator = genericAttributes.iterator();

                while (gaIterator.hasNext()) {
                    ga = (GenericAttribute) gaIterator.next();

                    if (ga.getName().equals(name)) {
                        return ga;
                    }
                }
            }
        }

        return null;
    }

    public boolean addUseCase(UseCase useCase) {
        UseCase uc = getUseCase(useCase.getName());

        if ((uc == null) || useCase.getName().equals("")) {
            useCases.add(useCase);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public UseCase getUseCase(String s) {
        UseCase useCase;
        Iterator iterator = useCases.iterator();
        while (iterator.hasNext()) {
            useCase = (UseCase) iterator.next();

            if (useCase.getName().equals(s)) {
                return useCase;
            }
        }

        return null;
    }

    public boolean editUseCase(UseCase original, UseCase other) {
        UseCase existingUseCase = getUseCase(other.getName());

        if (!original.getName().equals(other.getName()) && (existingUseCase != null) && !other.getName().equals("")) {
            return false;
        }

        original.setName(other.getName());

        repositoryChanged();

        return true;
    }

    public boolean removeUseCase(UseCase useCase) {
        if (useCases.remove(useCase)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean addUCLink(UCLink link) {
        if ((getUCLink(link.getClassifierFrom(), link.getClassifierTo()) == null)
                && getUCLink(link.getClassifierTo(), link.getClassifierFrom()) == null) {
            ucLinks.add(link);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public UCLink getUCLink(Classifier classifierFrom, Classifier classifierTo) {
        UCLink link;
        Iterator iterator = ucLinks.iterator();
        while (iterator.hasNext()) {
            link = (UCLink) iterator.next();

            if ((link.getClassifierFrom() == classifierFrom) && (link.getClassifierTo() == classifierTo)) {
                return link;
            }
        }

        return null;
    }

    public Vector getUCLinks() {
        return ucLinks;
    }

    public boolean removeLink(UCLink link) {
        if (ucLinks.remove(link)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public void editUCExtend(UCExtend originalUCExtend, UCExtend newUCExtend) {
        originalUCExtend.clearPoints();
        Iterator i = newUCExtend.getExtensionPoints();
        while (i.hasNext()) {
            originalUCExtend.addExtensionPoint(((ExtensionPoint) i.next()).clone());
        }
    }

    public boolean addUCDComponent(UCDComponent ucdComponent) {
        if (ucdComponent instanceof System) {
            return addSystem((System) ucdComponent);
        } else if (ucdComponent instanceof Actor) {
            return addActor((Actor) ucdComponent);
        } else if (ucdComponent instanceof UseCase) {
            return addUseCase((UseCase) ucdComponent);
        } else {
            java.lang.System.err.println("Error in addUCDComponent()");
            return false;
        }
    }

    public boolean removeUCDComponent(UCDComponent ucdComponent) {
        if (ucdComponent instanceof System) {
            return removeSystem((System) ucdComponent);
        } else if (ucdComponent instanceof Actor) {
            return removeActor((Actor) ucdComponent);
        } else if (ucdComponent instanceof UseCase) {
            return removeUseCase((UseCase) ucdComponent);
        } else {
            java.lang.System.err.println("Error in removeUCDComponent()");
            return false;
        }
    }

    public boolean addEdge(Edge edge) {
        if (edge instanceof ControlFlow) {
            return addControlFlow((ControlFlow) edge);
        } else if (edge instanceof ObjectFlow) {
            return addObjectFlow((ObjectFlow) edge);
        } else {
            java.lang.System.err.println("Error in addEdge()");
            return false;
        }
    }

    private boolean addControlFlow(ControlFlow controlFlow) {
        controlFlows.add(controlFlow);
        repositoryChanged();

        return true;
    }

    private boolean addObjectFlow(ObjectFlow objectFlow) {
        objectFlows.add(objectFlow);
        repositoryChanged();

        return true;
    }

    public boolean removeEdge(Edge edge) {
        if (edge instanceof ControlFlow) {
            return removeControlFlow((ControlFlow) edge);
        } else if (edge instanceof ObjectFlow) {
            return removeObjectFlow((ObjectFlow) edge);
        } else {
            java.lang.System.err.println("Error in removeEdge()");
            return false;
        }
    }

    private boolean removeControlFlow(ControlFlow controlFlow) {
        if (controlFlows.remove(controlFlow)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeObjectFlow(ObjectFlow objectFlow) {
        if (objectFlows.remove(objectFlow)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean addNodeComponent(NodeComponent nodeComponent) {
        if (nodeComponent instanceof ActionNode) {
            return addActionNode((ActionNode) nodeComponent);
        } else if (nodeComponent instanceof ControlNode) {
            return addControlNode((ControlNode) nodeComponent);
        } else if (nodeComponent instanceof ObjectNode) {
            return addObjectNode((ObjectNode) nodeComponent);
        } else if (nodeComponent instanceof ActivityNode) {
            return addActivityNode((ActivityNode) nodeComponent);
        } else {
            java.lang.System.err.println("Error in addNodeComponent()");
            return false;
        }
    }

    private boolean addActionNode(ActionNode actionNode) {
        // can add only one in an activity
        ActionNode existingActionNode = getActionNode(actionNode.getName());

        if ((existingActionNode == null) || actionNode.getName().equals("")) {
            actionNodes.add(actionNode);
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public ActionNode getActionNode(String name) {
        ActionNode actionNode;
        Iterator iterator = actionNodes.iterator();
        while (iterator.hasNext()) {
            actionNode = (ActionNode) iterator.next();

            if (actionNode.getName().equals(name)) {
                return actionNode;
            }
        }

        return null;
    }

    private boolean addControlNode(ControlNode controlNode) {
        if (controlNode instanceof InitialNode) {
            return addInitialNode((InitialNode) controlNode);
        } else if (controlNode instanceof ActivityFinalNode) {
            return addActivityFinalNode((ActivityFinalNode) controlNode);
        } else if (controlNode instanceof FlowFinalNode) {
            return addFlowFinalNode((FlowFinalNode) controlNode);
        } else if (controlNode instanceof DecisionNode) {
            return addDecisionNode((DecisionNode) controlNode);
        } else if (controlNode instanceof MergeNode) {
            return addMergeNode((MergeNode) controlNode);
        } else if (controlNode instanceof ForkNode) {
            return addForkNode((ForkNode) controlNode);
        } else if (controlNode instanceof JoinNode) {
            return addJoinNode((JoinNode) controlNode);
        } else {
            java.lang.System.err.println("Error in addControlNode()");
            return false;
        }
    }

    private boolean addInitialNode(InitialNode initialNode) {
        initialNodes.add(initialNode);
        repositoryChanged();

        return true;
    }

    private boolean addActivityFinalNode(ActivityFinalNode activityFinalNode) {
        activityFinalNodes.add(activityFinalNode);
        repositoryChanged();

        return true;
    }

    private boolean addFlowFinalNode(FlowFinalNode flowFinalNode) {
        flowFinalNodes.add(flowFinalNode);
        repositoryChanged();

        return true;
    }

    private boolean addDecisionNode(DecisionNode decisionNode) {
        decisionNodes.add(decisionNode);
        repositoryChanged();

        return true;
    }

    private boolean addMergeNode(MergeNode mergeNode) {
        mergeNodes.add(mergeNode);
        repositoryChanged();

        return true;
    }

    private boolean addForkNode(ForkNode forkNode) {
        forkNodes.add(forkNode);
        repositoryChanged();

        return true;
    }

    private boolean addJoinNode(JoinNode joinNode) {
        joinNodes.add(joinNode);
        repositoryChanged();

        return true;
    }

    private boolean addObjectNode(ObjectNode objectNode) {
        objectNodes.add(objectNode);
        repositoryChanged();

        return true;
    }

    private boolean addActivityNode(ActivityNode activityNode) {
        activityNodes.add(activityNode);
        repositoryChanged();

        return true;
    }

    public boolean removeNodeComponent(NodeComponent nodeComponent) {
        if (nodeComponent instanceof ActionNode) {
            return removeActionNode((ActionNode) nodeComponent);
        } else if (nodeComponent instanceof ControlNode) {
            return removeControlNode((ControlNode) nodeComponent);
        } else if (nodeComponent instanceof ObjectNode) {
            return removeObjectNode((ObjectNode) nodeComponent);
        } else if (nodeComponent instanceof ActivityNode) {
            return removeActivityNode((ActivityNode) nodeComponent);
        } else {
            java.lang.System.err.println("Error in removeNodeComponent()");
            return false;
        }
    }

    private boolean removeActionNode(ActionNode actionNode) {
        if (actionNodes.remove(actionNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeControlNode(ControlNode controlNode) {
        if (controlNode instanceof InitialNode) {
            return removeInitialNode((InitialNode) controlNode);
        } else if (controlNode instanceof ActivityFinalNode) {
            return removeActivityFinalNode((ActivityFinalNode) controlNode);
        } else if (controlNode instanceof FlowFinalNode) {
            return removeFlowFinalNode((FlowFinalNode) controlNode);
        } else if (controlNode instanceof DecisionNode) {
            return removeDecisionNode((DecisionNode) controlNode);
        } else if (controlNode instanceof MergeNode) {
            return removeMergeNode((MergeNode) controlNode);
        } else if (controlNode instanceof ForkNode) {
            return removeForkNode((ForkNode) controlNode);
        } else if (controlNode instanceof JoinNode) {
            return removeJoinNode((JoinNode) controlNode);
        } else {
            java.lang.System.err.println("Error in addControlNode()");
            return false;
        }
    }

    private boolean removeInitialNode(InitialNode initialNode) {
        if (initialNodes.remove(initialNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeActivityFinalNode(ActivityFinalNode activityFinalNode) {
        if (activityFinalNodes.remove(activityFinalNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeFlowFinalNode(FlowFinalNode flowFinalNode) {
        if (flowFinalNodes.remove(flowFinalNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeDecisionNode(DecisionNode decisionNode) {
        if (decisionNodes.remove(decisionNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeMergeNode(MergeNode mergeNode) {
        if (mergeNodes.remove(mergeNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeForkNode(ForkNode forkNode) {
        if (forkNodes.remove(forkNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeJoinNode(JoinNode joinNode) {
        if (joinNodes.remove(joinNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeObjectNode(ObjectNode objectNode) {
        if (objectNodes.remove(objectNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    private boolean removeActivityNode(ActivityNode activityNode) {
        if (activityNodes.remove(activityNode)) {
            repositoryChanged();

            return true;
        } else {
            return false;
        }
    }

    public boolean editObjectNode(ObjectNode originalObjectNode, ObjectNode newObjectNode) {
        originalObjectNode.setName(newObjectNode.getName());
        originalObjectNode.setType(newObjectNode.getType());

        originalObjectNode.clearStates();
        Iterator it = newObjectNode.getStates();
        while (it.hasNext()) {
            State state = (State) it.next();
            originalObjectNode.addState(state);
        }

        repositoryChanged();

        return true;
    }
}
