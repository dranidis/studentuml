package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
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
        ConceptualClassEditor classEditor = new ConceptualClassEditor(repository);

        // show the class editor dialog and check whether the user has pressed cancel
        ConceptualClass newClass = classEditor.editDialog(originalClass, parentComponent);
        if (newClass == null) {
            return;
        }

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && repository.getConceptualClass(newClass.getName()) != null && !newClass.getName().equals("")) {

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
        CCDAssociationEditor associationEditor = new CCDAssociationEditor();
        Association originalAssociation = associationGR.getAssociation();

        // Use new editDialog() method
        Association editedAssociation = associationEditor.editDialog(originalAssociation, parentComponent);
        if (editedAssociation == null) {
            return; // User cancelled
        }

        // Undo/Redo - capture original state
        Association undoAssociation = originalAssociation.clone();

        // Apply all changes atomically
        originalAssociation.setName(editedAssociation.getName());
        originalAssociation.setShowArrow(editedAssociation.getShowArrow());
        originalAssociation.setLabelDirection(editedAssociation.getLabelDirection());

        // Update roles
        Role roleA = originalAssociation.getRoleA();
        roleA.setName(editedAssociation.getRoleA().getName());
        roleA.setMultiplicity(editedAssociation.getRoleA().getMultiplicity());

        Role roleB = originalAssociation.getRoleB();
        roleB.setName(editedAssociation.getRoleB().getName());
        roleB.setMultiplicity(editedAssociation.getRoleB().getMultiplicity());

        // Undo/Redo
        UndoableEdit edit = new EditAssociationEdit(originalAssociation, undoAssociation, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void editAssociationClass(AssociationClassGR associationClassGR) {
        CentralRepository r = model.getCentralRepository();
        ConceptualAssociationClass originalAssociationClass = (ConceptualAssociationClass) associationClassGR
                .getAssociationClass();

        // Create editor and use Editor pattern
        ConceptualAssociationClassEditor editor = new ConceptualAssociationClassEditor(r);
        ConceptualAssociationClass editedAssociationClass = editor.editDialog(originalAssociationClass,
                parentComponent);

        // Check if user cancelled
        if (editedAssociationClass == null) {
            return;
        }

        ConceptualAssociationClass undoAssociationClass = originalAssociationClass.clone();

        // Apply changes using copyOf
        originalAssociationClass.copyOf(editedAssociationClass);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditCCDAssociationClassEdit(originalAssociationClass, undoAssociationClass, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
