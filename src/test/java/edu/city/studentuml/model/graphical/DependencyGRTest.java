package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.StringEditorDialog;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for DependencyGR edit functionality with undo/redo support.
 */
public class DependencyGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testDependencyGR_EditStereotype_UndoRedo() {
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

        Dependency dependency = new Dependency(classA, classB);

        // Override the dialog hook to inject test value instead of showing UI
        DependencyGR dependencyGR = new DependencyGR(classGRA, classGRB, dependency) {
            @Override
            protected StringEditorDialog createStringDialog(EditContext context, String dialogTitle, String fieldLabel,
                    String currentValue) {
                // Return mock value instead of showing dialog
                return new StringEditorDialog(context.getParentComponent(), dialogTitle, fieldLabel, currentValue) {
                    @Override
                    public String getText() {
                        return "use";
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }
                };
            }
        };

        model.addGraphicalElement(dependencyGR);
        assertNull(dependency.getStereotype());

        // Call the actual production edit() method
        EditContext context = new EditContext(model, frame);
        boolean editResult = dependencyGR.edit(context);

        assertTrue("Edit should return true", editResult);
        assertEquals("use", dependency.getStereotype());
        assertTrue("UndoManager should have edits", undoManager.canUndo());

        undoManager.undo();
        model.modelChanged();
        assertNull("After undo, stereotype should be null", dependency.getStereotype());

        undoManager.redo();
        model.modelChanged();
        assertEquals("use", dependency.getStereotype());
    }
}
