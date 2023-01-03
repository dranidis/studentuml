package edu.city.studentuml.controller;

import java.awt.Point;
import java.util.stream.Collectors;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.AbstractCDModel;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
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
        UCAssociationGR as = new UCAssociationGR(a, u, new UCAssociation((Actor) a.getComponent(), (UseCase) u.getComponent()));
        model.addGraphicalElement(as);
        return as;
    }

    AssociationGR addAssociation(ClassifierGR  cGr, ClassifierGR  b) {
        AssociationGR assoc = new AssociationGR(cGr, b, new Association(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(assoc);        
        return assoc;
    }

    AggregationGR addAggregation(ClassifierGR  cGr, ClassifierGR  b) {
        AggregationGR rel = new AggregationGR(cGr, b, new Aggregation(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);        
        return rel;
    }


    GeneralizationGR addGeneralization(ClassifierGR  cGr, ClassifierGR  b) {
        GeneralizationGR rel = new GeneralizationGR(cGr, b, new Generalization(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);        
        return rel;
    }

    AssociationClassGR addConceptualAssociationClass(ClassifierGR  cGr, ClassifierGR  b) {
        AssociationClassGR rel = new AssociationClassGR(cGr, b, new ConceptualAssociationClass(cGr.getClassifier(), b.getClassifier()));
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
        AssociationClassGR rel = new AssociationClassGR(cGr, b, new DesignAssociationClass(cGr.getClassifier(), b.getClassifier()));
        model.addGraphicalElement(rel);        
        return rel;
    }    

    ClassGR addClass(String name) {
        DesignClass c = new DesignClass(name);
        ClassGR cGr = new ClassGR(c, new Point());
        model.addGraphicalElement(cGr);; 
        return cGr;       
    }

    
    InterfaceGR addInterface(String name) {
        Interface c = new Interface(name);
        InterfaceGR cGr = new InterfaceGR(c, new Point());
        model.addGraphicalElement(cGr);; 
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
                || ((LinkGR) ge).getB().getClassifier().getName().equals(name)))
                )
                .collect(Collectors.toList()).size();
    }

    SystemGR addSystem(String name) {
        SystemGR s = new SystemGR(new System(name), 0, 0);
        model.addGraphicalElement(s);
        return s;
    }

   
}
