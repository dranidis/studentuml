package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.view.gui.ConceptualAssociationClassEditor;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DesignAssociationClassEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for AssociationClassGR edit functionality with undo/redo support.
 */
public class AssociationClassGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testConceptualAssociationClassGR_EditName_UndoRedo() {
        CCDModel model = new CCDModel("ccd", umlProject);
        DiagramInternalFrame frame = new CCDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        // Create two conceptual classes
        ConceptualClass classA = new ConceptualClass("Student");
        ConceptualClass classB = new ConceptualClass("Course");
        model.getCentralRepository().addConceptualClass(classA);
        model.getCentralRepository().addConceptualClass(classB);

        ConceptualClassGR classGRA = new ConceptualClassGR(classA, new Point(50, 50));
        ConceptualClassGR classGRB = new ConceptualClassGR(classB, new Point(250, 50));
        model.addGraphicalElement(classGRA);
        model.addGraphicalElement(classGRB);

        // Create association class
        ConceptualAssociationClass associationClass = new ConceptualAssociationClass(classA, classB);
        // setName sets both association name and association class name
        associationClass.setName("Enrollment");

        // Override factory method to inject mock editor behavior
        AssociationClassGR assocClassGR = new AssociationClassGR(classGRA, classGRB, associationClass) {
            @Override
            protected ConceptualAssociationClassEditor createConceptualEditor(EditContext context) {
                return new ConceptualAssociationClassEditor(context.getRepository()) {
                    @Override
                    public ConceptualAssociationClass editDialog(ConceptualAssociationClass original,
                            Component parentComponent) {
                        ConceptualAssociationClass edited = original.clone();
                        // setName sets both class name and association name
                        edited.setName("Registration");
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(assocClassGR);
        assertEquals("Enrollment", associationClass.getAssociation().getName());
        assertEquals("Enrollment", associationClass.getAssociationClass().getName());

        // Edit: association name and class name
        EditContext context = new EditContext(model, frame);
        boolean editResult = assocClassGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("Registration", associationClass.getAssociation().getName());
        assertEquals("Registration", associationClass.getAssociationClass().getName());

        // Undo: back to original names
        undoManager.undo();
        model.modelChanged();
        assertEquals("Enrollment", associationClass.getAssociation().getName());
        assertEquals("Enrollment", associationClass.getAssociationClass().getName());

        // Redo: apply edited names again
        undoManager.redo();
        model.modelChanged();
        assertEquals("Registration", associationClass.getAssociation().getName());
        assertEquals("Registration", associationClass.getAssociationClass().getName());
    }

    @Test
    public void testDesignAssociationClassGR_EditName_UndoRedo() {
        DCDModel model = new DCDModel("dcd", umlProject);
        DiagramInternalFrame frame = new DCDInternalFrame(model, false);
        UndoManager undoManager = frame.getUndoManager();

        // Create two design classes
        DesignClass classA = new DesignClass("Order");
        DesignClass classB = new DesignClass("Product");
        model.getCentralRepository().addClass(classA);
        model.getCentralRepository().addClass(classB);

        ClassGR classGRA = new ClassGR(classA, new Point(50, 50));
        ClassGR classGRB = new ClassGR(classB, new Point(250, 50));
        model.addGraphicalElement(classGRA);
        model.addGraphicalElement(classGRB);

        // Create design association class
        DesignAssociationClass associationClass = new DesignAssociationClass(classA, classB);
        // setName sets both association name and association class name
        associationClass.setName("LineItem");

        // Override factory method to inject mock editor behavior
        AssociationClassGR assocClassGR = new AssociationClassGR(classGRA, classGRB, associationClass) {
            @Override
            protected DesignAssociationClassEditor createDesignEditor(EditContext context) {
                return new DesignAssociationClassEditor(context.getRepository()) {
                    @Override
                    public DesignAssociationClass editDialog(DesignAssociationClass original,
                            Component parentComponent) {
                        DesignAssociationClass edited = original.clone();
                        // setName sets both class name and association name
                        edited.setName("OrderLine");
                        // Add an attribute to the association class
                        edited.getAssociationClass().addAttribute(new Attribute("quantity", new DataType("int")));
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(assocClassGR);
        assertEquals("LineItem", associationClass.getAssociation().getName());
        assertEquals("LineItem", associationClass.getAssociationClass().getName());
        assertEquals(0, associationClass.getAssociationClass().getAttributes().size());

        // Edit: association name, class name, and add attribute
        EditContext context = new EditContext(model, frame);
        boolean editResult = assocClassGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("OrderLine", associationClass.getAssociation().getName());
        assertEquals("OrderLine", associationClass.getAssociationClass().getName());
        assertEquals(1, associationClass.getAssociationClass().getAttributes().size());
        assertEquals("quantity", associationClass.getAssociationClass().getAttributes().get(0).getName());

        // Undo: back to original state
        undoManager.undo();
        model.modelChanged();
        assertEquals("LineItem", associationClass.getAssociation().getName());
        assertEquals("LineItem", associationClass.getAssociationClass().getName());
        assertEquals(0, associationClass.getAssociationClass().getAttributes().size());

        // Redo: apply edited state again
        undoManager.redo();
        model.modelChanged();
        assertEquals("OrderLine", associationClass.getAssociation().getName());
        assertEquals("OrderLine", associationClass.getAssociationClass().getName());
        assertEquals(1, associationClass.getAssociationClass().getAttributes().size());
    }
}
