/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.controller;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditCCDClassEdit;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.view.gui.CCDAssociationEditor;
import edu.city.studentuml.view.gui.ConceptualAssociationClassEditor;
import edu.city.studentuml.view.gui.ConceptualClassEditor;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.undoredo.CompositeDeleteEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEditLoader;
import edu.city.studentuml.util.undoredo.DeleteEditFactory;
import edu.city.studentuml.util.undoredo.EditCCDAssociationClassEdit;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class CCDSelectionController extends SelectionController {

    public CCDSelectionController(DiagramInternalFrame parent, CCDModel m) {
        super(parent, m);
    }

    // this method overrides the abstract method of the superclass
    // to handle editing of the mouse-selected graphical element
    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof ConceptualClassGR) {
            editClass((ConceptualClassGR) selectedElement);
        } else if (selectedElement instanceof AssociationGR) {
            editAssociation((AssociationGR) selectedElement);
        } else if (selectedElement instanceof AssociationClassGR) {
            editAssociationClass((AssociationClassGR) selectedElement);
        } else if (selectedElement instanceof UMLNoteGR) {
            editUMLNote((UMLNoteGR) selectedElement);
        }
    }

    private void editUMLNote(UMLNoteGR noteGR) {
        UMLNoteEditor noteEditor = new UMLNoteEditor(noteGR);

        // Undo/Redo [edit note]
        String undoText = noteGR.getText();

        if (!noteEditor.showDialog(parentComponent, "UML Note Editor")) {
            return;
        }

        noteGR.setText(noteEditor.getText());

        // Undo/Redo
        UndoableEdit edit = new EditNoteGREdit(noteGR, model, undoText);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // Editing the selected graphical element if it is a class
    public void editClass(ConceptualClassGR classGR) {
        CentralRepository repository = model.getCentralRepository();
        ConceptualClassEditor classEditor = new ConceptualClassEditor(classGR, repository);
        ConceptualClass originalClass = classGR.getConceptualClass();

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classEditor.showDialog(parentComponent, "Class Editor")) {
            return;
        }

        ConceptualClass newClass = new ConceptualClass(classEditor.getName());

        // add the attributes to the new class
        Iterator attributeIterator = classEditor.getAttributes().iterator();
        while (attributeIterator.hasNext()) {
            newClass.addAttribute((Attribute) attributeIterator.next());
        }

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && (repository.getConceptualClass(newClass.getName()) != null)
                && !newClass.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing class with the given name already.\n"
                    + "Do you want this diagram class to refer to the existing one?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                classGR.setConceptualClass(repository.getConceptualClass(newClass.getName()));

                // remove the existing class if it has no name
                if (originalClass.getName().equals("")) {
                    repository.removeConceptualClass(originalClass);
                }
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
    public void editAssociation(AssociationGR associationGR) {
        CCDAssociationEditor associationEditor = new CCDAssociationEditor(associationGR);
        Association association = associationGR.getAssociation();

        // show the association editor dialog and check whether the user has pressed cancel
        if (!associationEditor.showDialog(parentComponent, "Association Editor")) {
            return;
        }

        // Undo/Redo
        Association undoAssociation = association.clone();

        association.setName(associationEditor.getName());
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

    public void editAssociationClass(AssociationClassGR associationClassGR) {
        CentralRepository r = model.getCentralRepository();
        ConceptualAssociationClassEditor associationClassEditor = new ConceptualAssociationClassEditor(associationClassGR, r);
        ConceptualAssociationClass associationClass = (ConceptualAssociationClass) associationClassGR.getAssociationClass();

        // show the association class editor dialog and check whether the user has pressed cancel
        if (!associationClassEditor.showDialog(parentComponent, "Association Class Editor")) {
            return;
        }

        ConceptualAssociationClass undoAssociationClass = (ConceptualAssociationClass) associationClass.clone();

        associationClass.setName(associationClassEditor.getName());

        Role roleA = associationClass.getRoleA();
        roleA.setName(associationClassEditor.getRoleAName());
        roleA.setMultiplicity(associationClassEditor.getRoleAMultiplicity());

        Role roleB = associationClass.getRoleB();
        roleB.setName(associationClassEditor.getRoleBName());
        roleB.setMultiplicity(associationClassEditor.getRoleBMultiplicity());

        // add the attributes to the new association class
        NotifierVector attributes = new NotifierVector();
        Iterator attributeIterator = associationClassEditor.getAttributes().iterator();
        while (attributeIterator.hasNext()) {
            attributes.add((Attribute) attributeIterator.next());
        }
        associationClass.setAttributes(attributes);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditCCDAssociationClassEdit(associationClass, undoAssociationClass, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // this method overrides the abstract method of the superclass
    // to handle deletion of the mouse-selected graphical element
    public void deleteElement(GraphicalElement selectedElement) {
        UndoableEdit edit = DeleteEditFactory.getInstance().createDeleteEdit(selectedElement, model);
        if (edit instanceof CompositeDeleteEdit) {
            CompositeDeleteEditLoader.loadCompositeDeleteEdit(selectedElement, (CompositeDeleteEdit) edit, model);
        }
        parentComponent.getUndoSupport().postEdit(edit);
        model.removeGraphicalElement(selectedElement);
    }
}
