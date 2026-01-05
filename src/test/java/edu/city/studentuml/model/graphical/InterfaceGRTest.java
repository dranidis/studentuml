package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.InterfaceEditor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for InterfaceGR edit functionality with undo/redo support.
 */
public class InterfaceGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testInterfaceGR_EditMethods_UndoRedo() {
        DCDModel model = new DCDModel("dcd", umlProject);
        DiagramInternalFrame frame = new DCDInternalFrame(model, false);
        UndoManager undoManager = frame.getUndoManager();

        Interface originalInterface = new Interface("OriginalInterface");
        model.getCentralRepository().addInterface(originalInterface);

        // Override factory method to inject mock editor behavior
        InterfaceGR interfaceGR = new InterfaceGR(originalInterface, new Point(50, 50)) {
            @Override
            protected InterfaceEditor createEditor(EditContext context) {
                return new InterfaceEditor(context.getRepository()) {
                    @Override
                    public Interface editDialog(Interface original, Component parentComponent) {
                        Interface edited = original.clone();
                        edited.setName("EditedInterface");
                        edited.addMethod(new Method("newMethod"));
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(interfaceGR);
        assertEquals("OriginalInterface", originalInterface.getName());
        assertEquals(0, originalInterface.getMethods().size());

        EditContext context = new EditContext(model, frame);
        boolean editResult = interfaceGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertTrue("UndoManager should have edits", undoManager.canUndo());
        assertEquals("EditedInterface", originalInterface.getName());
        assertEquals(1, originalInterface.getMethods().size());
        assertEquals("newMethod", originalInterface.getMethods().get(0).getName());

        undoManager.undo();
        model.modelChanged();
        assertEquals("OriginalInterface", originalInterface.getName());
        assertEquals(0, originalInterface.getMethods().size());

        undoManager.redo();
        model.modelChanged();
        assertEquals("EditedInterface", originalInterface.getName());
        assertEquals(1, originalInterface.getMethods().size());
    }
}
