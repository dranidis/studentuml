package edu.city.studentuml.controller;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditDCDAssociationClassEdit;
import edu.city.studentuml.util.undoredo.EditDCDClassEdit;
import edu.city.studentuml.util.undoredo.EditInterfaceEdit;
import edu.city.studentuml.view.gui.AssociationEditor;
import edu.city.studentuml.view.gui.ClassEditor;
import edu.city.studentuml.view.gui.DesignAssociationClassEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.InterfaceEditor;

public class DCDSelectionController extends SelectionController {
    private static final Logger logger = Logger.getLogger(DCDSelectionController.class.getName());

    public DCDSelectionController(DiagramInternalFrame parent, DiagramModel model) {
        super(parent, model);

        editElementMapper.put(AssociationClassGR.class, e -> editAssociationClass((AssociationClassGR) e));
        editElementMapper.put(AssociationGR.class, e -> editAssociation((AssociationGR) e));
        editElementMapper.put(ClassGR.class, e -> editClass((ClassGR) e));
        editElementMapper.put(InterfaceGR.class, e -> editInterface((InterfaceGR) e));
    }

    // TODO very similar to editClass in CCDSelectionController
    // Editing the selected graphical element if it is a class
    private void editClass(ClassGR classGR) {
        CentralRepository repository = model.getCentralRepository();
        DesignClass originalClass = classGR.getDesignClass();
        ClassEditorI classEditor = new ClassEditor(originalClass, repository);

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classEditor.showDialog(parentComponent, "Class Editor")) {
            return;
        }

        DesignClass newClass = classEditor.getDesignClass();

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && (repository.getDesignClass(newClass.getName()) != null) && !newClass.getName().equals("")) {

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
        InterfaceEditor interfaceEditor = new InterfaceEditor(originalInterface, repository);

        // show the interface editor dialog and check whether the user has pressed
        // cancel
        if (!interfaceEditor.showDialog(parentComponent, "Interface Editor")) {
            return;
        }

        Interface newInterface = interfaceEditor.getInterface();

        // edit the interface if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalInterface.getName().equals(newInterface.getName())
                && (repository.getInterface(newInterface.getName()) != null) && !newInterface.getName().equals("")) {

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
        AssociationEditor associationEditor = new AssociationEditor(associationGR);
        Association association = associationGR.getAssociation();

        // show the association editor dialog and check whether the user has pressed
        // cancel
        if (!associationEditor.showDialog(parentComponent, "Association Editor")) {
            return;
        }

        // Undo/Redo
        Association undoAssociation = association.clone();

        association.setName(associationEditor.getAssociationName());
        association.setDirection(associationEditor.getDirection());
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
        DesignAssociationClassEditor associationClassEditor = new DesignAssociationClassEditor(associationClassGR, r);
        DesignAssociationClass associationClass = (DesignAssociationClass) associationClassGR.getAssociationClass();

        // show the association class editor dialog and check whether the user has
        // pressed cancel
        if (!associationClassEditor.showDialog(parentComponent, "Association Class Editor")) {
            return;
        }

        DesignAssociationClass undoAssociationClass = (DesignAssociationClass) associationClass.clone();

        associationClass.setName(associationClassEditor.getAssociationClassName());
        associationClass.setDirection(associationClassEditor.getDirection());

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

        // add the methods to the new association class
        NotifierVector<Method> methods = new NotifierVector<>();
        Iterator<Method> methodIterator = associationClassEditor.getMethods().iterator();
        while (methodIterator.hasNext()) {
            methods.add(methodIterator.next());
        }
        associationClass.setMethods(methods);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditDCDAssociationClassEdit(associationClass, undoAssociationClass, model);
        parentComponent.getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

}
