package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditCCDAssociationClassEdit;
import edu.city.studentuml.view.gui.CCDAssociationEditor;
import edu.city.studentuml.view.gui.ConceptualAssociationClassEditor;
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
        // ConceptualClassGR now uses polymorphic edit() method
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
