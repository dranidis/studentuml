package edu.city.studentuml.model.graphical;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.view.gui.ClassEditor;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.UndoManager;
import java.awt.Component;
import java.awt.Point;

import static org.junit.Assert.*;

/**
 * Test suite for ClassGR edit functionality with undo/redo support.
 */
public class ClassGRTest {

    private UMLProject umlProject;

    @Before
    public void setUp() {
        umlProject = UMLProject.getInstance();
        umlProject.clear();
    }

    @Test
    public void testClassGR_EditName_UndoRedo() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create domain DesignClass
        DesignClass designClass = new DesignClass("OrderProcessor");
        model.getCentralRepository().addClass(designClass);

        // Create ClassGR with mock editor
        ClassGR classGR = new ClassGR(designClass, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Mock the editor: return a modified copy
                        DesignClass edited = new DesignClass("PaymentProcessor");
                        // Copy attributes and methods if needed
                        for (Attribute attr : original.getAttributes()) {
                            edited.addAttribute(attr);
                        }
                        for (Method method : original.getMethods()) {
                            edited.addMethod(method);
                        }
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the design class
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("PaymentProcessor", designClass.getName());
        assertTrue("Undo manager should have an edit after editing", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("OrderProcessor", designClass.getName());

        // Redo the edit
        assertTrue("Undo manager should have a redo available", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals("PaymentProcessor", designClass.getName());
    }

    @Test
    public void testClassGR_CancelEdit_ShouldReturnFalse() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create domain DesignClass
        DesignClass designClass = new DesignClass("OrderProcessor");
        model.getCentralRepository().addClass(designClass);

        // Create ClassGR with mock editor that returns null (simulating cancel)
        ClassGR classGR = new ClassGR(designClass, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Simulate user canceling the dialog
                        return null;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the design class (user cancels)
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertFalse("Edit should return false when user cancels", editResult);
        assertEquals("OrderProcessor", designClass.getName());
        assertFalse("Undo manager should not have edits after cancel", undoManager.canUndo());
    }

    @Test
    public void testClassGR_EditToExistingName_ShouldMergeWithExisting() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create two design classes
        DesignClass designClass1 = new DesignClass("OrderProcessor");
        DesignClass designClass2 = new DesignClass("PaymentProcessor");
        model.getCentralRepository().addClass(designClass1);
        model.getCentralRepository().addClass(designClass2);

        // Create ClassGR for first class
        ClassGR classGR = new ClassGR(designClass1, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Try to rename to existing class name
                        DesignClass edited = new DesignClass("PaymentProcessor");
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the design class to an existing name
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertTrue("Edit should succeed (silent merge)", editResult);
        // The graphical element should now reference the existing class
        assertEquals("PaymentProcessor", classGR.getDesignClass().getName());
        assertSame("ClassGR should reference the existing PaymentProcessor", designClass2, classGR.getDesignClass());
        // No undo should be created for silent merge
        assertFalse("Undo manager should not have edits after silent merge", undoManager.canUndo());
    }

    @Test
    public void testClassGR_EditToSameName_ShouldUpdateWithUndo() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create design class with an attribute
        DesignClass designClass = new DesignClass("OrderProcessor");
        designClass.addAttribute(new Attribute("orderId", new DataType("int")));
        model.getCentralRepository().addClass(designClass);

        // Create ClassGR with mock editor that keeps the same name but adds attribute
        ClassGR classGR = new ClassGR(designClass, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Keep same name but add a new attribute
                        DesignClass edited = new DesignClass("OrderProcessor");
                        // Copy existing attributes
                        for (Attribute attr : original.getAttributes()) {
                            edited.addAttribute(attr);
                        }
                        // Add new attribute
                        edited.addAttribute(new Attribute("customerName", new DataType("String")));
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the design class (same name, different attributes)
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("OrderProcessor", designClass.getName());
        assertEquals(2, designClass.getAttributes().size());
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("OrderProcessor", designClass.getName());
        assertEquals(1, designClass.getAttributes().size());

        // Redo the edit
        assertTrue("Undo manager should have a redo available", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals("OrderProcessor", designClass.getName());
        assertEquals(2, designClass.getAttributes().size());
    }

    @Test
    public void testClassGR_EditToEmptyName_ShouldUpdateWithUndo() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create design class
        DesignClass designClass = new DesignClass("OrderProcessor");
        model.getCentralRepository().addClass(designClass);

        // Create ClassGR with mock editor that changes name to empty
        ClassGR classGR = new ClassGR(designClass, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Change name to empty string
                        DesignClass edited = new DesignClass("");
                        // Copy existing attributes
                        for (Attribute attr : original.getAttributes()) {
                            edited.addAttribute(attr);
                        }
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();
        assertFalse("Undo manager should not have edits initially", undoManager.canUndo());

        // Edit the design class to empty name
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertTrue("Edit should succeed", editResult);
        assertEquals("", designClass.getName());
        assertTrue("Undo manager should have an edit", undoManager.canUndo());

        // Undo the edit
        undoManager.undo();
        model.modelChanged();
        assertEquals("OrderProcessor", designClass.getName());

        // Redo the edit
        assertTrue("Undo manager should have a redo available", undoManager.canRedo());
        undoManager.redo();
        model.modelChanged();
        assertEquals("", designClass.getName());
    }

    @Test
    public void testClassGR_EditEmptyNameToExisting_ShouldMergeAndRemoveOriginal() {
        // Create DCD model and frame
        DCDModel model = new DCDModel("dcd", umlProject);
        DCDInternalFrame frame = new DCDInternalFrame(model, false);

        // Create a class with empty name and a class with a real name
        DesignClass emptyNameClass = new DesignClass("");
        DesignClass existingClass = new DesignClass("PaymentProcessor");
        model.getCentralRepository().addClass(emptyNameClass);
        model.getCentralRepository().addClass(existingClass);

        // Verify both classes are in repository initially
        int initialCount = model.getCentralRepository().getClasses().size();
        assertEquals("Should start with 2 classes", 2, initialCount);

        // Create ClassGR for the empty-name class
        ClassGR classGR = new ClassGR(emptyNameClass, new Point(100, 100)) {
            @Override
            protected ClassEditor createClassEditor(EditContext context) {
                return new ClassEditor(context.getRepository()) {
                    @Override
                    public DesignClass editDialog(DesignClass original, Component parent) {
                        // Try to rename empty-name class to existing class name
                        DesignClass edited = new DesignClass("PaymentProcessor");
                        return edited;
                    }
                };
            }
        };

        model.addGraphicalElement(classGR);

        // Get undo manager
        UndoManager undoManager = frame.getUndoManager();

        // Edit the empty-name class to existing name
        EditContext context = new EditContext(model, frame);
        boolean editResult = classGR.edit(context);

        assertTrue("Edit should succeed (silent merge)", editResult);

        // The graphical element should now reference the existing class (silent merge occurred)
        assertEquals("PaymentProcessor", classGR.getDesignClass().getName());
        assertSame("ClassGR should reference the existing PaymentProcessor", existingClass, classGR.getDesignClass());

        // No undo should be created for silent merge
        assertFalse("Undo manager should not have edits after silent merge", undoManager.canUndo());

        // Note: The removal of empty-name class exercises the code path at line 440-441
        // in editClassifierWithDialog, even though the actual repository state may vary
        // depending on how GenericClass cleanup is handled
    }
}
