package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.util.undoredo.EditDCDClassEdit;
import edu.city.studentuml.util.undoredo.EditInterfaceEdit;
import edu.city.studentuml.util.undoredo.EditNoteGREdit;
import edu.city.studentuml.view.gui.AssociationEditor;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.view.gui.ClassEditor;
import edu.city.studentuml.view.gui.DesignAssociationClassEditor;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.view.gui.InterfaceEditor;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.view.gui.UMLNoteEditor;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.undoredo.CompositeDeleteEdit;
import edu.city.studentuml.util.undoredo.CompositeDeleteEditLoader;
import edu.city.studentuml.util.undoredo.DeleteEditFactory;
import edu.city.studentuml.util.undoredo.EditDCDAssociationClassEdit;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

public class DCDSelectionController extends SelectionController {

    public DCDSelectionController(DiagramInternalFrame parent, DCDModel m) {
        super(parent, m);
    }

    // this method overrides the abstract method of the superclass
    // to handle editing of the mouse-selected graphical element
    public void editElement(GraphicalElement selectedElement) {
        if (selectedElement instanceof ClassGR) {
            editClass((ClassGR) selectedElement);
        } else if (selectedElement instanceof InterfaceGR) {
            editInterface((InterfaceGR) selectedElement);
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
    public void editClass(ClassGR classGR) {
        CentralRepository repository = model.getCentralRepository();
        ClassEditor classEditor = new ClassEditor(classGR, repository);
        DesignClass originalClass = classGR.getDesignClass();

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classEditor.showDialog(parentComponent, "Class Editor")) {
            return;
        }

        DesignClass newClass = new DesignClass(classEditor.getClassName());
        newClass.setStereotype(classEditor.getStereotype());

        // add the attributes to the new class
        Iterator attributeIterator = classEditor.getAttributes().iterator();
        while (attributeIterator.hasNext()) {
            newClass.addAttribute((Attribute) attributeIterator.next());
        }

        // add the methods to the new class
        Iterator methodIterator = classEditor.getMethods().iterator();
        while (methodIterator.hasNext()) {
            newClass.addMethod((Method) methodIterator.next());
        }

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && (repository.getDesignClass(newClass.getName()) != null) && !newClass.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing class with the given name already.\n"
                            + "Do you want this diagram class to refer to the existing one?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                classGR.setDesignClass(repository.getDesignClass(newClass.getName()));

                // remove the existing class if it has no name
                if (originalClass.getName().equals("")) {
                    repository.removeClass(originalClass);
                }
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
    public void editInterface(InterfaceGR interfaceGR) {
        CentralRepository repository = model.getCentralRepository();
        InterfaceEditor interfaceEditor = new InterfaceEditor(interfaceGR, repository);
        Interface originalInterface = interfaceGR.getInterface();

        // show the interface editor dialog and check whether the user has pressed
        // cancel
        if (!interfaceEditor.showDialog(parentComponent, "Interface Editor")) {
            return;
        }

        Interface newInterface = new Interface(interfaceEditor.getInterfaceName());

        // add the methods to the new interface
        Iterator methodIterator = interfaceEditor.getMethods().iterator();
        while (methodIterator.hasNext()) {
            newInterface.addMethod((Method) methodIterator.next());
        }

        // edit the interface if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalInterface.getName().equals(newInterface.getName())
                && (repository.getInterface(newInterface.getName()) != null) && !newInterface.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing interface with the given name already.\n"
                            + "Do you want this diagram interface to refer to the existing one?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                interfaceGR.setInterface(repository.getInterface(newInterface.getName()));

                // remove the existing interface if it has no name
                if (originalInterface.getName().equals("")) {
                    repository.removeInterface(originalInterface);
                }
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
    public void editAssociation(AssociationGR associationGR) {
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

    public void editAssociationClass(AssociationClassGR associationClassGR) {
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
        NotifierVector attributes = new NotifierVector();
        Iterator attributeIterator = associationClassEditor.getAttributes().iterator();
        while (attributeIterator.hasNext()) {
            attributes.add((Attribute) attributeIterator.next());
        }
        associationClass.setAttributes(attributes);

        // add the methods to the new association class
        NotifierVector methods = new NotifierVector();
        Iterator methodIterator = associationClassEditor.getMethods().iterator();
        while (methodIterator.hasNext()) {
            methods.add((Method) methodIterator.next());
        }
        associationClass.setMethods(methods);

        // Undo/Redo [edit]
        UndoableEdit edit = new EditDCDAssociationClassEdit(associationClass, undoAssociationClass, model);
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
        synchronized (this) {
            for (Object o : model.getGraphicalElements()) {
                if (o instanceof UMLNoteGR && ((UMLNoteGR) o).getTo().equals(selectedElement)) {
                    model.removeGraphicalElement((UMLNoteGR) o);
                }
            }
        }
        parentComponent.getUndoSupport().postEdit(edit);
        model.removeGraphicalElement(selectedElement);
    }
}
