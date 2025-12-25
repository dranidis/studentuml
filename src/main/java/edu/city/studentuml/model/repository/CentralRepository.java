package edu.city.studentuml.model.repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Logger;

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
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCLink;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.NotifierVector;

/**
 * CentralRepository is the core of the application data. It represents the
 * domain model layer of the model component in MVC. All domain concepts of UML
 * diagrams, such as classes, interfaces, messages, etc. are stored here in
 * their own lists. These concepts do not keep graphical rendering information
 * as is the case with GraphicalElements in UML diagrams. Whatever UML element
 * is added in a diagram, its corresponding domain concept is added to this
 * repository. There exists only one Central Repository for each project.
 * 
 * @author Ervin Ramollari
 */
public class CentralRepository implements Serializable {
    private static final Logger logger = Logger.getLogger(CentralRepository.class.getName());

    private Vector<DataType> datatypes;
    private NotifierVector<UseCase> useCases;
    private NotifierVector<UCLink> ucLinks;
    private NotifierVector<ActorInstance> actorInstances;
    private NotifierVector<SystemInstance> systemInstances;
    private NotifierVector<System> systems;
    private NotifierVector<Actor> actors;
    private NotifierVector<Aggregation> aggregations;
    private NotifierVector<Association> associations;
    private NotifierVector<DesignClass> classes;
    private NotifierVector<ConceptualClass> concepts;
    private NotifierVector<ConceptualAssociationClass> conceptualAssociationClasses;
    private NotifierVector<DesignAssociationClass> designAssociationClasses;
    private NotifierVector<Dependency> dependencies;
    private NotifierVector<Generalization> generalizations;
    private NotifierVector<GenericAttribute> genericAttributes;
    private NotifierVector<GenericClass> genericClasses;
    private NotifierVector<GenericOperation> genericOperations;
    private NotifierVector<Interface> interfaces;
    private NotifierVector<MultiObject> multiObjects;
    private NotifierVector<SDObject> sdObjects;
    private NotifierVector<Realization> realizations;
    private NotifierVector<SDMessage> sdMessages;
    private NotifierVector<ControlFlow> controlFlows;
    private NotifierVector<ObjectFlow> objectFlows;
    private NotifierVector<ActionNode> actionNodes;
    private NotifierVector<InitialNode> initialNodes;
    private NotifierVector<ActivityFinalNode> activityFinalNodes;
    private NotifierVector<FlowFinalNode> flowFinalNodes;
    private NotifierVector<DecisionNode> decisionNodes;
    private NotifierVector<MergeNode> mergeNodes;
    private NotifierVector<ForkNode> forkNodes;
    private NotifierVector<JoinNode> joinNodes;
    private NotifierVector<ObjectNode> objectNodes;
    private NotifierVector<ActivityNode> activityNodes;

    public CentralRepository() {

        datatypes = new Vector<>();
        initialiseDataTypes(); // all the lists of domain concepts created in a project

        ucLinks = new NotifierVector<>();
        useCases = new NotifierVector<>();

        // basic shared concepts
        genericClasses = new NotifierVector<>();
        genericAttributes = new NotifierVector<>();
        genericOperations = new NotifierVector<>();

        classes = new NotifierVector<>();
        interfaces = new NotifierVector<>();
        associations = new NotifierVector<>();
        generalizations = new NotifierVector<>();
        aggregations = new NotifierVector<>();
        realizations = new NotifierVector<>();
        dependencies = new NotifierVector<>();
        designAssociationClasses = new NotifierVector<>();

        concepts = new NotifierVector<>();
        conceptualAssociationClasses = new NotifierVector<>();

        systems = new NotifierVector<>();
        systemInstances = new NotifierVector<>();
        actors = new NotifierVector<>();
        actorInstances = new NotifierVector<>();
        sdObjects = new NotifierVector<>();
        multiObjects = new NotifierVector<>();
        sdMessages = new NotifierVector<>();

        controlFlows = new NotifierVector<>();
        objectFlows = new NotifierVector<>();
        actionNodes = new NotifierVector<>();
        initialNodes = new NotifierVector<>();
        activityFinalNodes = new NotifierVector<>();
        flowFinalNodes = new NotifierVector<>();
        decisionNodes = new NotifierVector<>();
        mergeNodes = new NotifierVector<>();
        forkNodes = new NotifierVector<>();
        joinNodes = new NotifierVector<>();
        objectNodes = new NotifierVector<>();
        activityNodes = new NotifierVector<>();
    }

    public void clear() {

        classes.clear();
        interfaces.clear();
        associations.clear();
        generalizations.clear();
        aggregations.clear();
        realizations.clear();
        dependencies.clear();
        concepts.clear();
        sdObjects.clear();
        multiObjects.clear();
        actors.clear();
        actorInstances.clear();
        sdMessages.clear();
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

    public Vector<DataType> getDatatypes() {
        return datatypes;
    }

    public Vector<Type> getTypes() {
        Vector<Type> types = new Vector<>();
        types.addAll(datatypes);

        classes.forEach(c -> {
            if (!c.getName().equals("")) {
                types.add(c);
            }
        });

        interfaces.forEach(c -> {
            if (!c.getName().equals("")) {
                types.add(c);
            }
        });

        return types;
    }

    // methods for manipulating the list of conceptual classes
    public boolean addConceptualClass(ConceptualClass c) {
        ConceptualClass existingClass = getConceptualClass(c.getName());

        if (existingClass == null || c.getName().equals("")) {
            concepts.add(c);

            GenericClass gc = getGenericClass(c.getGenericClass().getName());

            if (gc == null || gc.getName().equals("")) {
                genericClasses.add(c.getGenericClass());
            } else {
                c.setGenericClass(gc);
            }

            return true;
        } else {
            return false;
        }
    }

    public ConceptualClass getConceptualClass(String name) {

        Optional<ConceptualClass> found = concepts.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public boolean editConceptualClass(ConceptualClass originalClass, ConceptualClass newClass) {
        ConceptualClass existingClass = getConceptualClass(newClass.getName());
        return editAbstractClass(originalClass, newClass, existingClass);
    }

    public boolean removeConceptualClass(ConceptualClass c) {
        if (concepts.remove(c)) {
            removeGenericClass(c.getGenericClass());
            return true;
        } else {
            return false;
        }
    }

    // methods for manipulating the list of project classes
    public boolean addClass(DesignClass c) {
        logger.fine("Adding class:" + c.getName());
        DesignClass existingClass = getDesignClass(c.getName());

        // if a class with the same name doesn't exist, or the class hasn't been named
        // yet,
        // add it and return true; else return false
        if (existingClass == null || c.getName().equals("")) {
            classes.add(c);

            // if a generic class with the same name exists, set it as the classe's
            // generic class, otherwise add it to the repository
            GenericClass gc = getGenericClass(c.getGenericClass().getName());

            if (gc == null || gc.getName().equals("")) {
                genericClasses.add(c.getGenericClass());
            } else {
                c.setGenericClass(gc);
            }

            logger.fine(() -> "Classes:" + classes);

            return true;
        } else {
            logger.fine(() -> "Already in repository:" + c.getName());
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editClass(DesignClass originalClass, DesignClass newClass) {
        DesignClass existingClass = getDesignClass(newClass.getName());

        if (editAbstractClass(originalClass, newClass, existingClass)) {
            originalClass.setStereotype(newClass.getStereotype());
            originalClass.setMethods(newClass.getMethods());
            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    private boolean editAbstractClass(AbstractClass originalClass, AbstractClass newClass,
            AbstractClass existingClass) {

        // if the name of the class is changed and the new name causes conflict
        // with an existing class, and the new name is non-blank, then don't edit
        // and return false
        if (!originalClass.getName().equals(newClass.getName()) && existingClass != null
                && !newClass.getName().equals("")) {
            return false;
        }

        GenericClass existingGenericClass = getGenericClass(newClass.getName());

        if (existingGenericClass == null || existingGenericClass.getName().equals("")) {
            originalClass.setName(newClass.getName());
        } else {
            originalClass.setGenericClass(newClass.getGenericClass());
        }

        originalClass.setAttributes(newClass.getAttributes());

        return true;
    }

    public boolean removeClass(DesignClass c) {
        if (classes.remove(c)) {
            removeGenericClass(c.getGenericClass());
            return true;
        } else {
            return false;
        }
    }

    public Vector<DesignClass> getClasses() {
        return classes;
    }

    /**
     * Retrieves a design class by its name. If no match is found, the method
     * returns null. This method is useful when adding a graphical class in a design
     * class diagram in order to determine if there is a class with the same name.
     *
     * @param name
     * @return
     */
    public DesignClass getDesignClass(String name) {
        Optional<DesignClass> found = classes.stream().filter(dc -> dc.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project interfaces
    public boolean addInterface(Interface i) {
        Interface existingInterface = getInterface(i.getName());

        if (existingInterface == null || i.getName().equals("")) {
            interfaces.add(i);

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful, false if there is an existing
    // interface with the same name
    public boolean editInterface(Interface originalInterface, Interface newInterface) {
        Interface existingInterface = getInterface(newInterface.getName());

        if (!originalInterface.getName().equals(newInterface.getName()) && existingInterface != null
                && !newInterface.getName().equals("")) {
            return false;
        }

        originalInterface.setName(newInterface.getName());
        originalInterface.setMethods(newInterface.getMethods());

        return true;
    }

    public boolean removeInterface(Interface i) {
        if (interfaces.remove(i)) {

            return true;
        } else {
            return false;
        }
    }

    public Vector<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     * Retrieves an interface by its name If no match is found, the method returns
     * null This method is useful when adding a graphical interface in a design
     * class diagram in order to determine if there is an inteface with the same
     * name.
     */
    public Interface getInterface(String name) {

        Optional<Interface> found = interfaces.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project associations
    public boolean addAssociation(Association a) {
        associations.add(a);

        return true;
    }

    public boolean removeAssociation(Association a) {
        return associations.remove(a);
    }

    public Vector<Association> getAssociations() {
        return associations;
    }

    public boolean addAssociationClass(AbstractAssociationClass associationClass) {
        // associationClass can either be conceptual or design
        if (associationClass instanceof ConceptualAssociationClass) {
            conceptualAssociationClasses.add((ConceptualAssociationClass) associationClass);
        } else if (associationClass instanceof DesignAssociationClass) {
            designAssociationClasses.add((DesignAssociationClass) associationClass);
        }

        return true;
    }

    public boolean removeAssociationClass(AbstractAssociationClass a) {
        return a instanceof ConceptualAssociationClass && conceptualAssociationClasses.remove(a)
                || a instanceof DesignAssociationClass && designAssociationClasses.remove(a);
    }

    public Vector<ConceptualAssociationClass> getConceptualAssociationClasses() {
        return conceptualAssociationClasses;
    }

    public Vector<DesignAssociationClass> getDesignAssociationClasses() {
        return designAssociationClasses;
    }

    /**
     * Adds the generalization between two classes only if there doesn't exists one
     * between them as there is no need for more than one
     */
    public boolean addGeneralization(Generalization g) {
        if (getGeneralization(g.getSuperClass(), g.getBaseClass()) == null) {
            generalizations.add(g);

            return true;
        } else {
            return false;
        }
    }

    public boolean removeGeneralization(Generalization g) {
        return generalizations.remove(g);
    }

    public Vector<Generalization> getGeneralizations() {
        return generalizations;
    }

    // retrieves the generalization that exists between two classes, if any
    public Generalization getGeneralization(Classifier parent, Classifier child) {
        Optional<Generalization> found = generalizations.stream().filter(
                generalization -> generalization.getSuperClass() == parent && generalization.getBaseClass() == child)
                .findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project aggregations
    public boolean addAggregation(Aggregation a) {
        aggregations.add(a);

        return true;
    }

    public boolean removeAggregation(Aggregation a) {
        return aggregations.remove(a);
    }

    public Vector<Aggregation> getAggregations() {
        return aggregations;
    }

    /**
     * Adds the realization between one class and one interface only if there
     * doesn't exists one between them as there is no need for more than one.
     * 
     * @param r
     * @return
     */
    public boolean addRealization(Realization r) {
        if (getRealization(r.getTheClass(), r.getTheInterface()) == null) {
            realizations.add(r);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeRealization(Realization r) {
        return realizations.remove(r);
    }

    public Vector<Realization> getRealizations() {
        return realizations;
    }

    // retrieves the realization that exists between
    // one class and one interface, if any
    public Realization getRealization(DesignClass c, Interface i) {

        Optional<Realization> found = realizations.stream()
                .filter(realization -> realization.getTheClass() == c && realization.getTheInterface() == i).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project dependencies
    // adds the dependency between two classes only if there
    // doesn't exists one between them as there is no need for more than one
    public boolean addDependency(Dependency d) {
        if (getDependency(d.getFrom(), d.getTo()) == null) {
            dependencies.add(d);

            return true;
        } else {
            return false;
        }
    }

    public boolean removeDependency(Dependency d) {
        return dependencies.remove(d);
    }

    public Vector<Dependency> getDependencies() {
        return dependencies;
    }

    // returns the dependency that exists between two classes, if any
    public Dependency getDependency(DesignClass from, DesignClass to) {

        Optional<Dependency> found = dependencies.stream()
                .filter(dependency -> dependency.getFrom() == from && dependency.getTo() == to).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project objects
    // appearing in sequence diagrams
    public boolean addSystemInstance(SystemInstance s) {
        SystemInstance existingSystem = getSystemInstance(s.getName());
        if (existingSystem == null || s.getName().equals("")) {
            systemInstances.add(s);
            return true;
        } else {
            return false;
        }
    }

    public boolean editSystemInstance(SystemInstance originalSystemInstance, SystemInstance newSystemInstance) {
        SystemInstance existingSystemInstance = getSystemInstance(newSystemInstance.getName());

        if (!originalSystemInstance.getName().equals(newSystemInstance.getName()) && existingSystemInstance != null
                && !newSystemInstance.getName().equals("")) {
            return false;
        }

        originalSystemInstance.setName(newSystemInstance.getName());
        originalSystemInstance.setSystem(newSystemInstance.getSystem());

        return true;
    }

    public SystemInstance getSystemInstance(String name) {

        Optional<SystemInstance> found = systemInstances.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public Vector<SystemInstance> getSystemInstances() {
        return systemInstances;
    }

    public boolean addSystem(System s) {
        System existingSystem = getSystem(s.getName());

        if (existingSystem == null || s.getName().equals("")) {
            systems.add(s);

            return true;
        } else {
            return false;
        }
    }

    public boolean removeSystem(System s) {
        return systems.remove(s);
    }

    public boolean editSystem(System originalSystem, System newSystem) {
        System existingSystem = getSystem(newSystem.getName());
        if (!originalSystem.getName().equals(newSystem.getName()) && existingSystem != null
                && !newSystem.getName().equals("")) {
            return false;
        }

        originalSystem.setName(newSystem.getName());

        return true;
    }

    public System getSystem(String name) {

        Optional<System> found = systems.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public Vector<System> getSystems() {
        return systems;
    }

    public boolean removeSystemInstance(SystemInstance s) {
        return systemInstances.remove(s);
    }

    public boolean addObject(SDObject o) {
        logger.fine(() -> "Adding object " + o.toString());
        SDObject existingObject = getObject(o.getName());

        if (existingObject == null || o.getName().equals("")) {
            sdObjects.add(o);

            logger.finer(() -> "objects: " + sdObjects);
            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editObject(SDObject originalObject, SDObject newObject) {
        SDObject existingObject = getObject(newObject.getName());

        if (!originalObject.getName().equals(newObject.getName()) && existingObject != null
                && !newObject.getName().equals("")) {
            return false;
        }

        originalObject.setName(newObject.getName());
        originalObject.setDesignClass(newObject.getDesignClass());

        return true;
    }

    public boolean removeObject(SDObject o) {
        return sdObjects.remove(o);
    }

    public Vector<SDObject> getSdObjects() {
        return sdObjects;
    }

    // this method retrieves an object by its name
    // If no match is found, the method returns null
    // This method is useful when adding a graphical object
    // in a sequence diagram in order to determine if there is an
    // object with the same name.
    public SDObject getObject(String name) {

        Optional<SDObject> found = sdObjects.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project multi-objects
    // appearing in sequence diagrams
    public boolean addMultiObject(MultiObject mo) {
        MultiObject existingMultiObject = getMultiObject(mo.getName());

        if (existingMultiObject == null || mo.getName().equals("")) {
            multiObjects.add(mo);

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editMultiObject(MultiObject originalObject, MultiObject newObject) {
        MultiObject existingObject = getMultiObject(newObject.getName());

        if (!originalObject.getName().equals(newObject.getName()) && existingObject != null
                && !newObject.getName().equals("")) {
            return false;
        }

        originalObject.setName(newObject.getName());
        originalObject.setDesignClass(newObject.getDesignClass());

        return true;
    }

    public boolean removeMultiObject(MultiObject mo) {
        return multiObjects.remove(mo);
    }

    public Vector<MultiObject> getMultiObjects() {
        return multiObjects;
    }

    // this method retrieves a multi-object by its name
    // If no match is found, the method returns null
    public MultiObject getMultiObject(String name) {

        Optional<MultiObject> found = multiObjects.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project actors
    public boolean addActor(Actor a) {
        Actor existingActor = getActor(a.getName());

        if (existingActor == null || a.getName().equals("")) {
            actors.add(a);

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editActor(Actor originalActor, Actor newActor) {
        Actor existingActor = getActor(newActor.getName());

        if (!originalActor.getName().equals(newActor.getName()) && existingActor != null
                && !newActor.getName().equals("")) {
            return false;
        }

        originalActor.setName(newActor.getName());

        return true;
    }

    public boolean removeActor(Actor a) {
        return actors.remove(a);
    }

    public Vector<Actor> getActors() {
        return actors;
    }

    // this method retrieves an actor by its name
    // If no match is found, the method returns null
    public Actor getActor(String name) {

        Optional<Actor> found = actors.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of actor instances
    // in sequence diagrams
    public boolean addActorInstance(ActorInstance a) {
        ActorInstance existingActor = getActorInstance(a.getName());

        if (existingActor == null || a.getName().equals("")) {
            actorInstances.add(a);

            return true;
        } else {
            return false;
        }
    }

    // returns true if the editing was successful
    public boolean editActorInstance(ActorInstance originalActorInstance, ActorInstance newActorInstance) {
        ActorInstance existingActorInstance = getActorInstance(newActorInstance.getName());

        if (!originalActorInstance.getName().equals(newActorInstance.getName()) && existingActorInstance != null
                && !newActorInstance.getName().equals("")) {
            return false;
        }

        originalActorInstance.setName(newActorInstance.getName());
        originalActorInstance.setActor(newActorInstance.getActor());

        return true;
    }

    public boolean removeActorInstance(ActorInstance a) {
        return actorInstances.remove(a);
    }

    public Vector<ActorInstance> getActorInstances() {
        return actorInstances;
    }

    // this method retrieves an actor instance by its name
    // If no match is found, the method returns null
    public ActorInstance getActorInstance(String name) {

        Optional<ActorInstance> found = actorInstances.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of project sequence diagram messages
    public boolean addSDMessage(SDMessage m) {
        logger.fine(() -> "Adding message " + m.toString());
        sdMessages.add(m);

        return true;
    }

    public boolean removeSDMessage(SDMessage m) {
        return sdMessages.remove(m);
    }

    public Vector<SDMessage> getSDMessages() {
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

        return true;
    }

    public boolean removeGenericClass(GenericClass gc) {
        return genericClasses.remove(gc);
    }

    public Vector<GenericClass> getGenericClasses() {
        return genericClasses;
    }

    // this method retrieves a generic class by its name
    // If no match is found, the method returns null
    // This method is useful when adding a design or conceptual class
    // in the repository in order to determine if there
    // already exists a generic class with the same name
    public GenericClass getGenericClass(String name) {

        Optional<GenericClass> found = genericClasses.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    // methods for manipulating the list of generic operations
    public boolean addGenericOperation(GenericOperation go) {
        genericOperations.add(go);

        return true;
    }

    public boolean removeGenericOperation(GenericOperation go) {
        return genericOperations.remove(go);
    }

    public Vector<GenericOperation> getGenericOperations() {
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
        for (DesignClass designClass : classes) {
            if (designClass.getGenericClass() == parentClass) {
                for (GenericOperation go : genericOperations) {
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

        return true;
    }

    public boolean removeGenericAttribute(GenericAttribute ga) {
        return genericAttributes.remove(ga);
    }

    public Vector<GenericAttribute> getGenericAttributes() {
        return genericAttributes;
    }

    // this method retrieves a generic attribute by its name
    // If no match is found, the method returns null
    // This method is useful when adding a design or conceptual class
    // attribute in the repository in order to determine if there
    // already exists a generic attribute with the same name and belonging to same
    // class
    public GenericAttribute getGenericAttribute(String name, GenericClass parentClass) {
        for (DesignClass designClass : classes) {
            if (designClass.getGenericClass() == parentClass) {
                for (GenericAttribute ga : genericAttributes) {
                    if (ga.getName().equals(name)) {
                        return ga;
                    }
                }
            }
        }

        for (ConceptualClass concept : concepts) {
            if (concept.getGenericClass() == parentClass) {
                for (GenericAttribute ga : genericAttributes) {
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

        if (uc == null || useCase.getName().equals("")) {
            useCases.add(useCase);

            return true;
        } else {
            return false;
        }
    }

    public UseCase getUseCase(String name) {

        Optional<UseCase> found = useCases.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public boolean editUseCase(UseCase original, UseCase other) {
        UseCase existingUseCase = getUseCase(other.getName());

        if (!original.getName().equals(other.getName()) && existingUseCase != null && !other.getName().equals("")) {
            return false;
        }

        original.setName(other.getName());

        return true;
    }

    public boolean removeUseCase(UseCase useCase) {
        return useCases.remove(useCase);
    }

    public boolean addUCLink(UCLink link) {
        if (getUCLink(link.getSource(), link.getTarget()) == null
                && getUCLink(link.getTarget(), link.getSource()) == null) {
            ucLinks.add(link);

            return true;
        } else {
            return false;
        }
    }

    public UCLink getUCLink(Classifier classifierFrom, Classifier classifierTo) {

        Optional<UCLink> found = ucLinks.stream()
                .filter(link -> link.getSource() == classifierFrom && link.getTarget() == classifierTo).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public Vector<UCLink> getUCLinks() {
        return ucLinks;
    }

    public boolean removeLink(UCLink link) {
        return ucLinks.remove(link);
    }

    public void editUCExtend(UCExtend originalUCExtend, UCExtend newUCExtend) {
        originalUCExtend.clearPoints();
        for (ExtensionPoint ep : newUCExtend.getExtensionPoints()) {
            originalUCExtend.addExtensionPoint(ep.clone());
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
            logger.severe("Error in addUCDComponent()");
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
            logger.severe("Error in removeUCDComponent()");
            return false;
        }
    }

    public boolean addEdge(Edge edge) {
        if (edge instanceof ControlFlow) {
            return addControlFlow((ControlFlow) edge);
        } else if (edge instanceof ObjectFlow) {
            return addObjectFlow((ObjectFlow) edge);
        } else {
            logger.severe("Error in addEdge()");
            return false;
        }
    }

    private boolean addControlFlow(ControlFlow controlFlow) {
        controlFlows.add(controlFlow);

        return true;
    }

    private boolean addObjectFlow(ObjectFlow objectFlow) {
        objectFlows.add(objectFlow);

        return true;
    }

    public boolean removeEdge(Edge edge) {
        if (edge instanceof ControlFlow) {
            return removeControlFlow((ControlFlow) edge);
        } else if (edge instanceof ObjectFlow) {
            return removeObjectFlow((ObjectFlow) edge);
        } else {
            logger.severe("Error in removeEdge()");
            return false;
        }
    }

    private boolean removeControlFlow(ControlFlow controlFlow) {
        return controlFlows.remove(controlFlow);
    }

    private boolean removeObjectFlow(ObjectFlow objectFlow) {
        return objectFlows.remove(objectFlow);
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
            logger.severe("Error in addNodeComponent()");
            return false;
        }
    }

    private boolean addActionNode(ActionNode actionNode) {
        // can add only one in an activity
        ActionNode existingActionNode = getActionNode(actionNode.getName());

        if (existingActionNode == null || actionNode.getName().equals("")) {
            actionNodes.add(actionNode);

            return true;
        } else {
            return false;
        }
    }

    public ActionNode getActionNode(String name) {

        Optional<ActionNode> found = actionNodes.stream().filter(x -> x.getName().equals(name)).findAny();
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
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
            logger.severe("Error in addControlNode()");
            return false;
        }
    }

    private boolean addInitialNode(InitialNode initialNode) {
        initialNodes.add(initialNode);

        return true;
    }

    private boolean addActivityFinalNode(ActivityFinalNode activityFinalNode) {
        activityFinalNodes.add(activityFinalNode);

        return true;
    }

    private boolean addFlowFinalNode(FlowFinalNode flowFinalNode) {
        flowFinalNodes.add(flowFinalNode);

        return true;
    }

    private boolean addDecisionNode(DecisionNode decisionNode) {
        decisionNodes.add(decisionNode);

        return true;
    }

    private boolean addMergeNode(MergeNode mergeNode) {
        mergeNodes.add(mergeNode);

        return true;
    }

    private boolean addForkNode(ForkNode forkNode) {
        forkNodes.add(forkNode);

        return true;
    }

    private boolean addJoinNode(JoinNode joinNode) {
        joinNodes.add(joinNode);

        return true;
    }

    private boolean addObjectNode(ObjectNode objectNode) {
        objectNodes.add(objectNode);

        return true;
    }

    private boolean addActivityNode(ActivityNode activityNode) {
        activityNodes.add(activityNode);

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
            logger.severe("Error in removeNodeComponent");
            return false;
        }
    }

    private boolean removeActionNode(ActionNode actionNode) {
        return actionNodes.remove(actionNode);
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
            logger.severe("Error in removeControlNode");
            return false;
        }
    }

    private boolean removeInitialNode(InitialNode initialNode) {
        return initialNodes.remove(initialNode);
    }

    private boolean removeActivityFinalNode(ActivityFinalNode activityFinalNode) {
        return activityFinalNodes.remove(activityFinalNode);
    }

    private boolean removeFlowFinalNode(FlowFinalNode flowFinalNode) {
        return flowFinalNodes.remove(flowFinalNode);
    }

    private boolean removeDecisionNode(DecisionNode decisionNode) {
        return decisionNodes.remove(decisionNode);
    }

    private boolean removeMergeNode(MergeNode mergeNode) {
        return mergeNodes.remove(mergeNode);
    }

    private boolean removeForkNode(ForkNode forkNode) {
        return forkNodes.remove(forkNode);

    }

    private boolean removeJoinNode(JoinNode joinNode) {
        return joinNodes.remove(joinNode);
    }

    private boolean removeObjectNode(ObjectNode objectNode) {
        return objectNodes.remove(objectNode);
    }

    private boolean removeActivityNode(ActivityNode activityNode) {
        return activityNodes.remove(activityNode);
    }

    public boolean editObjectNode(ObjectNode originalObjectNode, ObjectNode newObjectNode) {
        originalObjectNode.setName(newObjectNode.getName());
        originalObjectNode.setType(newObjectNode.getType());

        originalObjectNode.clearStates();
        for (State state : newObjectNode.getStates()) {
            originalObjectNode.addState(state);
        }

        return true;
    }
}
