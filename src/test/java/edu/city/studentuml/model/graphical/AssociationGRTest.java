package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for AssociationGR edit functionality with undo/redo support.
 */
public class AssociationGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testAssociationGR_EditName_UndoRedo() {
        DCDModel model = new DCDModel("dcd", umlProject);
        DiagramInternalFrame frame = new DCDInternalFrame(model, false);
        UndoManager undoManager = frame.getUndoManager();

        DesignClass classA = new DesignClass("ClassA");
        DesignClass classB = new DesignClass("ClassB");
        model.getCentralRepository().addClass(classA);
        model.getCentralRepository().addClass(classB);

        ClassGR classGRA = new ClassGR(classA, new Point(50, 50));
        ClassGR classGRB = new ClassGR(classB, new Point(200, 50));
        model.addGraphicalElement(classGRA);
        model.addGraphicalElement(classGRB);

        Association association = new Association(classA, classB);

        AssociationGR associationGR = new AssociationGR(classGRA, classGRB, association) {
            @Override
            protected Association createAndRunEditor(EditContext context, Association original) {
                Association edited = original.clone();
                edited.setName("newAssocName");
                return edited;
            }
        };

        model.addGraphicalElement(associationGR);
        assertEquals("", association.getName());

        EditContext context = new EditContext(model, frame);
        associationGR.edit(context);
        assertEquals("newAssocName", association.getName());

        undoManager.undo();
        model.modelChanged();
        assertEquals("", association.getName());

        undoManager.redo();
        model.modelChanged();
        assertEquals("newAssocName", association.getName());
    }
}
