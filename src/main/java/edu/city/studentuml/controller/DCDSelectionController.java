package edu.city.studentuml.controller;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditDCDAssociationClassEdit;
import edu.city.studentuml.util.undoredo.EditDCDClassEdit;
import edu.city.studentuml.util.undoredo.EditDependencyEdit;
import edu.city.studentuml.util.undoredo.EditInterfaceEdit;
import edu.city.studentuml.view.gui.AssociationEditor;
import edu.city.studentuml.view.gui.ClassEditor;
import edu.city.studentuml.view.gui.StringEditorDialog;
import edu.city.studentuml.view.gui.DesignAssociationClassEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.InterfaceEditor;

public class DCDSelectionController extends SelectionController {

    public DCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);

        editElementMapper.put(AssociationClassGR.class, e -> editAssociationClass((AssociationClassGR) e));
        editElementMapper.put(AssociationGR.class, e -> editAssociation((AssociationGR) e));
        editElementMapper.put(AggregationGR.class, e -> editAssociation((AssociationGR) e));
        editElementMapper.put(ClassGR.class, e -> editClass((ClassGR) e));
        editElementMapper.put(DependencyGR.class, e -> editDependency((DependencyGR) e));
        editElementMapper.put(InterfaceGR.class, e -> editInterface((InterfaceGR) e));
    }

    // TODO very similar to editClass in CCDSelectionController
    // Editing the selected graphical element if it is a class
    private void editClass(ClassGR classGR) {
        CentralRepository repository = model.getCentralRepository();
        DesignClass originalClass = classGR.getDesignClass();
        ClassEditorI classEditor = new ClassEditor(repository);

        // show the class editor dialog and check whether the user has pressed cancel
        DesignClass newClass = classEditor.editDialog(originalClass, parentComponent);
        if (newClass == null) {
            return;
        }

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && repository.getDesignClass(newClass.getName()) != null && !newClass.getName().equals("")) {

            classGR.setDesignClass(repository.getDesignClass(newClass.getName()));

            // remove the existing class if it has no name
            if (originalClass.getName().equals("")) {
                repository.removeClass(originalClass);
            }

        } else {
            // Undo/Redo [edit]
            UndoableEdit edit = new EditDCDClassEdit(originalClass, newClass, model);
            repository.editClass(originalClass, newClass);
            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // Editing the selected graphical element if it is an interface
    private void editInterface(InterfaceGR interfaceGR) {
        CentralRepository repository = model.getCentralRepository();
        Interface originalInterface = interfaceGR.getInterface();
        InterfaceEditor interfaceEditor = new InterfaceEditor(repository);

        // show the interface editor dialog and check whether the user has pressed
        // cancel
        Interface newInterface = interfaceEditor.editDialog(originalInterface, parentComponent);
        if (newInterface == null) {
            return;
        }

        // edit the interface if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalInterface.getName().equals(newInterface.getName())
                && repository.getInterface(newInterface.getName()) != null && !newInterface.getName().equals("")) {

            interfaceGR.setInterface(repository.getInterface(newInterface.getName()));

            // remove the existing interface if it has no name
            if (originalInterface.getName().equals("")) {
                repository.removeInterface(originalInterface);

            }
        } else {
            UndoableEdit edit = new EditInterfaceEdit(originalInterface, newInterface, model);

            repository.editInterface(originalInterface, newInterface);

            parentComponent.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    // Editing the selected graphical element if it is an association
    private void editAssociation(AssociationGR associationGR) {
        AssociationEditor associationEditor = new AssociationEditor();
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
        originalAssociation.setDirection(editedAssociation.getDirection());
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

    /**
     * Edit the stereotype of a dependency. Opens a dialog allowing the user to set
     * or modify the dependency stereotype.
     * 
     * @param dependencyGR The graphical representation of the dependency to edit
     */
    private void editDependency(DependencyGR dependencyGR) {
        StringEditorDialog stringEditorDialog = new StringEditorDialog(parentComponent, "Dependency Editor",
                "Stereotype: ", dependencyGR.getDependency().getStereotype());
        Dependency dependency = dependencyGR.getDependency();

        // show the dependency editor dialog and check whether the user has pressed cancel
        if (!stringEditorDialog.showDialog()) {
            return;
        }

        // Undo/Redo - capture state before editing
        String undoStereotype = dependency.getStereotype();
        String newStereotype = stringEditorDialog.getText();

        // Only create undo edit if the value actually changed
        if ((undoStereotype == null && newStereotype != null) ||
                (undoStereotype != null && !undoStereotype.equals(newStereotype))) {

            dependency.setStereotype(newStereotype);

            // Undo/Redo
            UndoableEdit edit = new EditDependencyEdit(dependency, undoStereotype, newStereotype, model);
            parentComponent.getUndoSupport().postEdit(edit);

            // set observable model to changed in order to notify its views
            model.modelChanged();
            SystemWideObjectNamePool.getInstance().reload();
        }
    }

    private void editAssociationClass(AssociationClassGR associationClassGR) {
        CentralRepository r = model.getCentralRepository();
        DesignAssociationClass originalAssociationClass = (DesignAssociationClass) associationClassGR
                .getAssociationClass();

        // Create editor and use Editor pattern
        DesignAssociationClassEditor editor = new DesignAssociationClassEditor(r);
        DesignAssociationClass editedAssociationClass = editor.editDialog(originalAssociationClass, parentComponent);

        // Check if user cancelled
        if (editedAssociationClass == null) {
            return;
        }

        DesignAssociationClass undoAssociationClass = originalAssociationClass.clone();

        // Apply changes using copyOf
        originalAssociationClass.copyOf(editedAssociationClass);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditDCDAssociationClassEdit(originalAssociationClass, undoAssociationClass, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
