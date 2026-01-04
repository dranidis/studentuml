package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.CallMessageEditor;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.SDInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Test suite for CallMessageGR edit functionality with undo/redo support.
 */
public class CallMessageGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testCallMessageGR_EditParameters_UndoRedo() {
        SDModel model = new SDModel("sd", umlProject);
        DiagramInternalFrame frame = new SDInternalFrame(model);
        UndoManager undoManager = frame.getUndoManager();

        DesignClass classA = new DesignClass("ClassA");
        model.getCentralRepository().addClass(classA);

        SDObject objA = new SDObject("a", classA);
        SDObject objB = new SDObject("b", classA);
        model.getCentralRepository().addObject(objA);
        model.getCentralRepository().addObject(objB);

        SDObjectGR objGRA = new SDObjectGR(objA, 50);
        SDObjectGR objGRB = new SDObjectGR(objB, 150);
        model.addGraphicalElement(objGRA);
        model.addGraphicalElement(objGRB);

        GenericOperation operation = new GenericOperation("method");
        CallMessage message = new CallMessage(objA, objB, operation);

        CallMessageGR messageGR = new CallMessageGR(objGRA, objGRB, message, 100) {
            @Override
            protected CallMessageEditor createEditor(EditContext context) {
                return new CallMessageEditor(context.getRepository()) {
                    @Override
                    public CallMessage editDialog(CallMessage original, Component parentComponent) {
                        Vector<MethodParameter> params = new Vector<>();
                        params.add(new MethodParameter("param1", new DataType("String")));
                        original.setParameters(params);
                        return original;
                    }
                };
            }
        };

        model.addGraphicalElement(messageGR);
        assertEquals(0, message.getParameters().size());

        EditContext context = new EditContext(model, frame);
        messageGR.edit(context);
        assertEquals(1, message.getParameters().size());
        assertEquals("param1", message.getParameters().get(0).getName());

        undoManager.undo();
        model.modelChanged();
        assertEquals(0, message.getParameters().size());

        undoManager.redo();
        model.modelChanged();
        assertEquals(1, message.getParameters().size());
    }
}
