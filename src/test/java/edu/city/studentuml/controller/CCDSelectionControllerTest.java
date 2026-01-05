package edu.city.studentuml.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.util.undoredo.EditCCDClassEdit;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.editing.EditContext;

public class CCDSelectionControllerTest {

    UMLProject umlProject;
    CCDModel model;
    CCDInternalFrame ccdInternalFrame;
    Helper h;
    SelectionController selectionController;

    @Before
    public void setup() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        model = new CCDModel("ccd", umlProject);
        ccdInternalFrame = new CCDInternalFrame(model);
        h = new Helper(model);
        selectionController = new SelectionController(ccdInternalFrame, model);
    }

    @Test
    public void testCreation() {

        assertNotNull(selectionController);
    }

    @Test
    public void testDeleteElementUndo() {

        /**
         * Adds a conceptual class A
         */
        GraphicalElement cGr = h.addConceptualClass("A");

        selectionController.addElementToSelection(cGr);
        selectionController.deleteSelected();

        assertFalse("no matches",
                model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR));

        ccdInternalFrame.getUndoManager().undo();

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR));
    }

    @Test
    public void testDeleteElementWithRelationshipsUndo() {

        ConceptualClassGR a = h.addConceptualClass("A");
        ConceptualClassGR b = h.addConceptualClass("B");
        ConceptualClassGR c = h.addConceptualClass("C");
        ConceptualClassGR d = h.addConceptualClass("D");
        ConceptualClassGR f = h.addConceptualClass("F");

        h.addAssociation(a, b);
        h.addAssociation(c, a);
        h.addAggregation(a, d);
        h.addAggregation(c, a);
        h.addGeneralization(c, a);
        h.addGeneralization(a, b);
        h.addConceptualAssociationClass(a, f);

        // System.out.println("BEFORE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));

        /*
         * DELETE a
         */
        selectionController.addElementToSelection(a);
        selectionController.deleteSelected();

        assertFalse("no matches", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR
                && ((ConceptualClassGR) ge).getAbstractClass().getName().equals("A")));
        assertEquals(0, h.countRelationshipsWithClassNamed("A"));

        // System.out.println("DELETED A");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        /*
         * UNDO
         */
        ccdInternalFrame.getUndoManager().undo();
        // System.out.println("UNDONE");
        // model.getGraphicalElements().forEach(e -> System.out.println(e));

        assertTrue("found", model.getGraphicalElements().stream().anyMatch(ge -> ge instanceof ConceptualClassGR));
        assertEquals(7, h.countRelationshipsWithClassNamed("A"));
    }

    @Test
    public void testConceptualClassEdit_withPolymorphicMethod() {
        // This test verifies that ConceptualClassGR.edit() method exists and accepts EditContext
        // We cannot test the actual dialog interaction in headless mode

        // Verify the polymorphic edit() method exists and has correct signature
        try {
            java.lang.reflect.Method editMethod = ConceptualClassGR.class.getMethod("edit", EditContext.class);
            assertNotNull("ConceptualClassGR should have edit(EditContext) method", editMethod);
            assertEquals("edit() should return boolean", boolean.class, editMethod.getReturnType());

            // Verify EditContext can be created with correct dependencies
            EditContext context = new EditContext(model, ccdInternalFrame);
            assertNotNull("EditContext should be created successfully", context);
            assertEquals("EditContext should provide correct model", model, context.getModel());
            assertEquals("EditContext should provide correct repository",
                    model.getCentralRepository(), context.getRepository());
            assertEquals("EditContext should provide correct parent component",
                    ccdInternalFrame, context.getParentComponent());
            assertNotNull("EditContext should provide undo support", context.getUndoSupport());

        } catch (NoSuchMethodException e) {
            assertTrue("ConceptualClassGR should have edit(EditContext) method", false);
        }
    }

    @Test
    public void testEditConceptualClassNameWithUndo() {
        // Create a ConceptualClass
        ConceptualClassGR classGR = h.addConceptualClass("OriginalClass");
        ConceptualClass conceptualClass = classGR.getConceptualClass();

        // Verify initial state in both domain and repository
        assertEquals("OriginalClass", conceptualClass.getName());
        assertEquals(conceptualClass, umlProject.getCentralRepository().getConceptualClass("OriginalClass"));

        // Create edit
        ConceptualClass newClass = conceptualClass.clone();
        newClass.setName("EditedClass");
        EditCCDClassEdit edit = new EditCCDClassEdit(conceptualClass, newClass, model);

        // Apply edit (redo)
        edit.redo();
        assertEquals("EditedClass", conceptualClass.getName());
        // Repository should be synchronized
        assertEquals(conceptualClass, umlProject.getCentralRepository().getConceptualClass("EditedClass"));
        assertNull(umlProject.getCentralRepository().getConceptualClass("OriginalClass"));

        // Undo
        edit.undo();
        assertEquals("OriginalClass", conceptualClass.getName());
        // Repository should be restored
        assertEquals(conceptualClass, umlProject.getCentralRepository().getConceptualClass("OriginalClass"));
        assertNull(umlProject.getCentralRepository().getConceptualClass("EditedClass"));

        // Redo again
        edit.redo();
        assertEquals("EditedClass", conceptualClass.getName());
        assertEquals(conceptualClass, umlProject.getCentralRepository().getConceptualClass("EditedClass"));
        assertNull(umlProject.getCentralRepository().getConceptualClass("OriginalClass"));
    }

}
