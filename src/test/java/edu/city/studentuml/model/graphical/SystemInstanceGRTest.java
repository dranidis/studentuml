package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.view.gui.SystemInstanceEditor;
import edu.city.studentuml.view.gui.TypedEntityEditResult;
import edu.city.studentuml.view.gui.TypeOperation;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test suite for SystemInstanceGR edit functionality with undo/redo support.
 * Note: Uses fully qualified name for edu.city.studentuml.model.domain.System
 * to avoid conflict with java.lang.System.
 */
public class SystemInstanceGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testSystemInstanceGR_EditName_UndoRedo() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain System and SystemInstance
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System("PaymentSystem");
        model.getCentralRepository().addSystem(system);
        SystemInstance systemInstance = new SystemInstance("ps1", system);
        model.getCentralRepository().addSystemInstance(systemInstance);

        // Create SystemInstanceGR with mock editor
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100) {
            @Override
            protected SystemInstanceEditor createEditor(EditContext context) {
                return new SystemInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> editDialog(
                            TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> initialResult,
                            Component parent) {
                        // Mock the editor: return a modified SystemInstance
                        SystemInstance original = initialResult.getDomainObject();
                        SystemInstance edited = new SystemInstance("ps2", original.getSystem());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(systemInstanceGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the system instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("ps2", systemInstance.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("ps1", systemInstance.getName());

        // Redo the edit
        undoManager.redo();
        model.modelChanged();
        assertEquals("ps2", systemInstance.getName());
    }

    @Test
    public void testSystemInstanceGR_EditCancel() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain System and SystemInstance
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System("PaymentSystem");
        model.getCentralRepository().addSystem(system);
        SystemInstance systemInstance = new SystemInstance("ps1", system);
        model.getCentralRepository().addSystemInstance(systemInstance);

        // Create SystemInstanceGR with mock editor that returns null (cancel)
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100) {
            @Override
            protected SystemInstanceEditor createEditor(EditContext context) {
                return new SystemInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> editDialog(
                            TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> initialResult,
                            Component parent) {
                        // Return null to simulate cancel
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(systemInstanceGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the system instance (should cancel)
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemInstanceGR.edit(context);

        assertTrue("Edit should return true even on cancel", editResult);
        assertEquals("ps1", systemInstance.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testSystemInstanceGR_EditWithTypeOperation() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain System and SystemInstance
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System("PaymentSystem");
        model.getCentralRepository().addSystem(system);
        SystemInstance systemInstance = new SystemInstance("ps1", system);
        model.getCentralRepository().addSystemInstance(systemInstance);

        // Create SystemInstanceGR with mock editor that adds a new System type
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100) {
            @Override
            protected SystemInstanceEditor createEditor(EditContext context) {
                return new SystemInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> editDialog(
                            TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> initialResult,
                            Component parent) {
                        // Create a new System type and a type operation to add it
                        edu.city.studentuml.model.domain.System newSystem = new edu.city.studentuml.model.domain.System(
                                "OrderSystem");
                        TypeOperation<edu.city.studentuml.model.domain.System> addSystemOp = TypeOperation
                                .add(newSystem);

                        ArrayList<TypeOperation<edu.city.studentuml.model.domain.System>> typeOps = new ArrayList<>();
                        typeOps.add(addSystemOp);

                        // Change SystemInstance to use the new System
                        SystemInstance edited = new SystemInstance("os1", newSystem);
                        return new TypedEntityEditResult<>(edited, typeOps);
                    }
                };
            }
        };

        model.addGraphicalElement(systemInstanceGR);

        // Verify new System doesn't exist yet
        assertNull("OrderSystem should not exist yet", model.getCentralRepository().getSystem("OrderSystem"));

        // Edit the system instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("os1", systemInstance.getName());
        assertEquals("OrderSystem", systemInstance.getSystem().getName());
        assertNotNull("OrderSystem should now exist", model.getCentralRepository().getSystem("OrderSystem"));

        // Verify undo manager has the compound edit
        UndoManager undoManager = frame.getUndoManager();
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo should revert both the type operation and the system instance edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("ps1", systemInstance.getName());
        assertEquals("PaymentSystem", systemInstance.getSystem().getName());
    }

    @Test
    public void testSystemInstanceGR_EditEmptyName() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain System and SystemInstance
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System("PaymentSystem");
        model.getCentralRepository().addSystem(system);
        SystemInstance systemInstance = new SystemInstance("ps1", system);
        model.getCentralRepository().addSystemInstance(systemInstance);

        // Create SystemInstanceGR with mock editor that returns empty name
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100) {
            @Override
            protected SystemInstanceEditor createEditor(EditContext context) {
                return new SystemInstanceEditor(context.getRepository()) {
                    @Override
                    public TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> editDialog(
                            TypedEntityEditResult<edu.city.studentuml.model.domain.System, SystemInstance> initialResult,
                            Component parent) {
                        // Return a SystemInstance with empty name
                        SystemInstance original = initialResult.getDomainObject();
                        SystemInstance edited = new SystemInstance("", original.getSystem());
                        return new TypedEntityEditResult<>(edited, new ArrayList<>());
                    }
                };
            }
        };

        model.addGraphicalElement(systemInstanceGR);

        // Edit the system instance
        EditContext context = new EditContext(model, frame);
        boolean editResult = systemInstanceGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", systemInstance.getName());
    }

    @Test
    public void testSystemInstanceGR_CreateEditorMethodExists() {
        // Create SD model and frame
        SDModel model = new SDModel("sd", umlProject);
        SDInternalFrame frame = new SDInternalFrame(model);

        // Create domain System and SystemInstance
        edu.city.studentuml.model.domain.System system = new edu.city.studentuml.model.domain.System("PaymentSystem");
        model.getCentralRepository().addSystem(system);
        SystemInstance systemInstance = new SystemInstance("ps1", system);
        model.getCentralRepository().addSystemInstance(systemInstance);

        // Create SystemInstanceGR (not mocked)
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        // Verify createEditor method exists and returns non-null
        EditContext context = new EditContext(model, frame);
        SystemInstanceEditor editor = systemInstanceGR.createEditor(context);
        assertNotNull("createEditor should return a non-null editor", editor);
    }
}
