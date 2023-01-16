package edu.city.studentuml.controller;

import java.util.Iterator;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditCCDAssociationClassEdit;
import edu.city.studentuml.util.undoredo.EditCCDClassEdit;
import edu.city.studentuml.view.gui.CCDAssociationEditor;
import edu.city.studentuml.view.gui.ConceptualAssociationClassEditor;
import edu.city.studentuml.view.gui.ConceptualClassEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class CCDSelectionController extends SelectionController {

    public CCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);

        editElementMapper.put(AssociationClassGR.class, e -> editAssociationClass((AssociationClassGR) e));
        editElementMapper.put(AggregationGR.class, e -> editAssociation((AssociationGR) e));
        editElementMapper.put(AssociationGR.class, e -> editAssociation((AssociationGR) e));
        editElementMapper.put(ConceptualClassGR.class, e -> editClass((ConceptualClassGR) e));
    }

    // TODO very similar to editClass in DCDSelectionController
    // Editing the selected graphical element if it is a class
    private void editClass(ConceptualClassGR classGR) {
        CentralRepository repository = model.getCentralRepository();
        ConceptualClass originalClass = classGR.getConceptualClass();
        ConceptualClassEditor classEditor = new ConceptualClassEditor(originalClass, repository);

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classEditor.showDialog(parentComponent, "Conceptual Class Editor")) {
            return;
        }

        ConceptualClass newClass = classEditor.getConceptualClass();

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && (repository.getConceptualClass(newClass.getName()) != null) && !newClass.getName().equals("")) {

            classGR.setConceptualClass(repository.getConceptualClass(newClass.getName()));

            // remove the existing class if it has no name
            if (originalClass.getName().equals("")) {
                repository.removeConceptualClass(originalClass);
            }

        } else {
            // Undo/Redo [edit]
            UndoableEdit edit = new EditCCDClassEdit(originalClass, newClass, model);
            repository.editConceptualClass(originalClass, newClass);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // Editing the selected graphical element if it is an association
    private void editAssociation(AssociationGR associationGR) {
        CCDAssociationEditor associationEditor = new CCDAssociationEditor(associationGR);
        Association association = associationGR.getAssociation();

        // show the association editor dialog and check whether the user has pressed cancel
        if (!associationEditor.showDialog(parentComponent, "Association Editor")) {
            return;
        }

        // Undo/Redo
        Association undoAssociation = association.clone();

        association.setName(associationEditor.getAssociationName());
        association.setShowArrow(associationEditor.getShowArrow());
        association.setLabelDirection(associationEditor.getLabelDirection());

        Role roleA = association.getRoleA();
        roleA.setName(associationEditor.getRoleAName());
        roleA.setMultiplicity(associationEditor.getRoleAMultiplicity());

        Role roleB = association.getRoleB();
        roleB.setName(associationEditor.getRoleBName());
        roleB.setMultiplicity(associationEditor.getRoleBMultiplicity());

        // Undo/Redo
        UndoableEdit edit = new EditAssociationEdit(association, undoAssociation, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editAssociationClass(AssociationClassGR associationClassGR) {
        CentralRepository r = model.getCentralRepository();
        ConceptualAssociationClassEditor associationClassEditor = new ConceptualAssociationClassEditor(
                associationClassGR, r);
        ConceptualAssociationClass associationClass = (ConceptualAssociationClass) associationClassGR
                .getAssociationClass();

        // show the association class editor dialog and check whether the user has pressed cancel
        if (!associationClassEditor.showDialog(parentComponent, "Association Class Editor")) {
            return;
        }

        ConceptualAssociationClass undoAssociationClass = (ConceptualAssociationClass) associationClass.clone();

        associationClass.setName(associationClassEditor.getAssociationClassName());

        Role roleA = associationClass.getRoleA();
        roleA.setName(associationClassEditor.getRoleAName());
        roleA.setMultiplicity(associationClassEditor.getRoleAMultiplicity());

        Role roleB = associationClass.getRoleB();
        roleB.setName(associationClassEditor.getRoleBName());
        roleB.setMultiplicity(associationClassEditor.getRoleBMultiplicity());

        // add the attributes to the new association class
        NotifierVector<Attribute> attributes = new NotifierVector<>();
        Iterator<Attribute> attributeIterator = associationClassEditor.getAttributes().iterator();
        while (attributeIterator.hasNext()) {
            attributes.add(attributeIterator.next());
        }
        associationClass.setAttributes(attributes);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditCCDAssociationClassEdit(associationClass, undoAssociationClass, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
