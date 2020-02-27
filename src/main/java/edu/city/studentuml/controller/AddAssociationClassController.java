/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.controller;

import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.domain.AbstractAssociationClass;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.ClassifierGR;
import edu.city.studentuml.model.graphical.LinkGR;

/**
 *
 * @author draganbisercic
 * @author dranidis
 */
public class AddAssociationClassController extends AddAssociationController {

    public AddAssociationClassController(DCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public AddAssociationClassController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    protected LinkGR createLinkGR(ClassifierGR whole, ClassifierGR part) {
        AbstractAssociationClass associationClass;
        AssociationClassGR associationClassGR;

        // can be either CCD or DCD
        if (diagramModel instanceof CCDModel) {
            associationClass = new ConceptualAssociationClass(whole.getClassifier(), part.getClassifier());
            associationClass.setBidirectional();
            associationClassGR = new AssociationClassGR(whole, part, associationClass);
        } else {
            associationClass = new DesignAssociationClass(whole.getClassifier(), part.getClassifier());
            associationClass.setDirection(AbstractAssociationClass.AB);
            associationClassGR = new AssociationClassGR(whole, part, associationClass);
        }
        
        return associationClassGR;
    }    
}
