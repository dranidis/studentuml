package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.view.gui.ConceptualClassEditor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for ConceptualClassGR edit functionality with undo/redo support.
 */
public class ConceptualClassGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testConceptualClassGR_EditName_UndoRedo() {
        // Create CCD model and frame
        CCDModel model = new CCDModel("ccd", umlProject);
        CCDInternalFrame frame = new CCDInternalFrame(model);

        // Create domain ConceptualClass
        ConceptualClass conceptualClass = new ConceptualClass("Customer");
        model.getCentralRepository().addConceptualClass(conceptualClass);

        // Create ConceptualClassGR with mock editor
        ConceptualClassGR conceptualClassGR = new ConceptualClassGR(conceptualClass, new Point(100, 100)) {
            @Override
            protected ConceptualClassEditor createConceptualClassEditor(EditContext context) {
                return new ConceptualClassEditor(context.getRepository()) {
                    @Override
                    public ConceptualClass editDialog(ConceptualClass original, Component parent) {
                        // Mock the editor: return a modified copy
                        ConceptualClass edited = new ConceptualClass("CustomerAccount");
                        // Copy attributes if needed
                        for (Attribute attr : original.getAttributes()) {
                            edited.addAttribute(attr);
                        }
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(conceptualClassGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the conceptual class
        EditContext context = new EditContext(model, frame);
        boolean editResult = conceptualClassGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("CustomerAccount", conceptualClass.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("Customer", conceptualClass.getName());

        // Redo the edit
        assertTrue("Undo manager should have a redo available", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals("CustomerAccount", conceptualClass.getName());
    }
}
