package edu.city.studentuml.controller;

import java.awt.Point;
import java.util.stream.Collectors;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InitialNodeGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.model.graphical.UseCaseGR;

class Helper {
    private DiagramModel model;

    Helper(DiagramModel model) {
        this.model = model;
    }

    UCActorGR addActor(String name) {
        UCActorGR a = new UCActorGR(new Actor(name), 0, 0);
        model.addGraphicalElement(a);
        return a;
    }

    UseCaseGR addUseCase(String name) {
        UseCaseGR u = new UseCaseGR(new UseCase(name), 0, 0);
        model.addGraphicalElement(u);
        return u;
    }

    UCAssociationGR addUcAssociation(UCActorGR a, UseCaseGR u) {
        UCAssociationGR as = new UCAssociationGR(a, u,
                new UCAssociation((Actor) a.getComponent(), (UseCase) u.getComponent()));
        model.addGraphicalElement(as);
        return as;
    }

    UCIncludeGR addUcInclude(UseCaseGR source, UseCaseGR target) {
        UCIncludeGR include = new UCIncludeGR(
                source, target, new UCInclude(
                        (UseCase) source.getComponent(), (UseCase) target.getComponent()));
        model.addGraphicalElement(include);
        return include;
    }

    UCExtendGR addUcExtend(UseCaseGR source, UseCaseGR target) {
        UCExtendGR extend = new UCExtendGR(
                source, target, new UCExtend(
                        (UseCase) source.getComponent(), (UseCase) target.getComponent()));
        model.addGraphicalElement(extend);
        return extend;
    }

    UCGeneralizationGR addUcGeneralizationActor(UCActorGR source,
            UCActorGR target) {
        UCGeneralizationGR gen = new UCGeneralizationGR(
                source, target, new UCGeneralization(
                        (Actor) source.getComponent(), (Actor) target.getComponent()));
        model.addGraphicalElement(gen);
        return gen;
    }

    UCGeneralizationGR addUcGeneralizationUseCase(UseCaseGR source,
            UseCaseGR target) {
        UCGeneralizationGR gen = new UCGeneralizationGR(
                source, target, new UCGeneralization(
                        (UseCase) source.getComponent(), (UseCase) target.getComponent()));
        model.addGraphicalElement(gen);
        return gen;
    }

    AssociationGR addAssociation(ClassifierGR cGr, ClassifierGR b) {
        AssociationGR assoc = new AssociationGR(cGr, b, new Association(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(assoc);
        return assoc;
    }

    AggregationGR addAggregation(ClassifierGR cGr, ClassifierGR b) {
        AggregationGR rel = new AggregationGR(cGr, b, new Aggregation(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);
        return rel;
    }

    GeneralizationGR addGeneralization(ClassifierGR cGr, ClassifierGR b) {
        // Constructor expects (parent, child), but our convention is addGeneralization(child, parent)
        GeneralizationGR rel = new GeneralizationGR(b, cGr, new Generalization(b.getClassifier(), cGr.getClassifier()));
        model.addGraphicalElement(rel);
        return rel;
    }

    GeneralizationGR addGeneralizationInterface(InterfaceGR a, InterfaceGR b) {
        // Constructor expects (parent, child), but our convention is addGeneralizationInterface(child, parent)
        GeneralizationGR rel = new GeneralizationGR(b, a, new Generalization(b.getClassifier(), a.getClassifier()));
        model.addGraphicalElement(rel);
        return rel;
    }

    DependencyGR addDependency(ClassGR a, ClassGR b) {
        DependencyGR dep = new DependencyGR(
                a, b, new Dependency(
                        (DesignClass) a.getClassifier(), (DesignClass) b.getClassifier()));
        model.addGraphicalElement(dep);
        return dep;
    }

    edu.city.studentuml.model.graphical.RealizationGR addRealization(ClassGR c, InterfaceGR i) {
        edu.city.studentuml.model.graphical.RealizationGR real = new edu.city.studentuml.model.graphical.RealizationGR(
                c, i, new edu.city.studentuml.model.domain.Realization(
                        (DesignClass) c.getClassifier(), (Interface) i.getClassifier()));
        model.addGraphicalElement(real);
        return real;
    }

    AssociationClassGR addConceptualAssociationClass(ClassifierGR cGr, ClassifierGR b) {
        AssociationClassGR rel = new AssociationClassGR(cGr, b,
                new ConceptualAssociationClass(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);
        return rel;
    }

    ConceptualClassGR addConceptualClass(String name) {
        ConceptualClass c = new ConceptualClass(name);
        ConceptualClassGR cGr = new ConceptualClassGR(c, new Point());
        model.addGraphicalElement(cGr);
        return cGr;
    }

    AssociationClassGR addAssociationClass(ClassifierGR cGr, ClassifierGR b) {
        AssociationClassGR rel = new AssociationClassGR(cGr, b,
                new DesignAssociationClass(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);
        return rel;
    }

    ClassGR addClass(String name) {
        DesignClass c = new DesignClass(name);
        ClassGR cGr = new ClassGR(c, new Point());
        model.addGraphicalElement(cGr);
        ;
        return cGr;
    }

    InterfaceGR addInterface(String name) {
        Interface c = new Interface(name);
        InterfaceGR cGr = new InterfaceGR(c, new Point());
        model.addGraphicalElement(cGr);
        ;
        return cGr;
    }

    UMLNoteGR addNote(GraphicalElement selectedElement) {
        UMLNoteGR graphicalNote = new UMLNoteGR(null, selectedElement, new Point());
        model.addGraphicalElement(graphicalNote);
        return graphicalNote;
    }

    int countRelationshipsWithClassNamed(String name) {
        return model.getGraphicalElements().stream()
                .filter(ge -> (ge instanceof LinkGR
                        && (((LinkGR) ge).getA().getClassifier().getName().equals(name)
                                || ((LinkGR) ge).getB().getClassifier().getName().equals(name))))
                .collect(Collectors.toList()).size();
    }

    SystemGR addSystem(String name) {
        SystemGR s = new SystemGR(new System(name), 0, 0);
        model.addGraphicalElement(s);
        return s;
    }

    public ActivityNodeGR addActivityNode(String name) {
        ActivityNodeGR a = new ActivityNodeGR(new ActivityNode(name), 0, 0);
        model.addGraphicalElement(a);
        return a;
    }

    public InitialNodeGR addInitialNodeInActivityNode(ActivityNodeGR an) {
        InitialNodeGR i = new InitialNodeGR(new InitialNode(), 0, 0);
        an.add(i);
        i.setContext(an);
        return i;
    }

}
