package edu.city.studentuml.model.graphical;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.EditSystemInstanceEdit;
import edu.city.studentuml.util.undoredo.SystemEdit;
import edu.city.studentuml.util.undoredo.SystemRepositoryOperations;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.SystemInstanceEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

public class SystemInstanceGR extends AbstractSDObjectGR {

    public SystemInstanceGR(SystemInstance obj, int x) {
        super(obj, x);
    }

    public SystemInstance getSystemInstance() {
        return (SystemInstance) roleClassifier;
    }

    public void setSystemInstance(SystemInstance obj) {
        roleClassifier = obj;
    }

    /**
     * Edit this system instance using a dialog. This method uses the polymorphic
     * edit pattern.
     * 
     * @param context the edit context providing access to model and parent
     *                component
     * @return true if editing was successful or cancelled, false if there was an
     *         error
     */
    @Override
    public boolean edit(EditContext context) {
        CentralRepository repository = context.getModel().getCentralRepository();
        SystemInstance originalSystemInstance = getSystemInstance();

        // Create editor and initial result
        SystemInstanceEditor systemEditor = createEditor(context);
        TypedEntityEditResult<System, SystemInstance> initialResult = new TypedEntityEditResult<>(
                originalSystemInstance, new java.util.ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<System, SystemInstance> result = systemEditor
                .editDialog(initialResult, context.getParentComponent());

        // Check if user cancelled
        if (result == null) {
            return true;
        }

        SystemInstance newSystemInstance = result.getDomainObject();

        // UNDO/REDO setup
        SystemInstance undoSystemInstance = new SystemInstance(originalSystemInstance.getName(),
                originalSystemInstance.getSystem());
        SystemEdit undoEdit = new SystemEdit(undoSystemInstance, originalSystemInstance.getSystem().getName());

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<System> typeOps = new SystemRepositoryOperations();
        for (TypeOperation<System> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // edit the system instance if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        if (!originalSystemInstance.getName().equals(newSystemInstance.getName())
                && repository.getSystemInstance(newSystemInstance.getName()) != null
                && !newSystemInstance.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing system instance with the given name already.\n"
                            + "Do you want this diagram system instance to refer to the existing one?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                setSystemInstance(repository.getSystemInstance(newSystemInstance.getName()));

                if (originalSystemInstance.getName().equals("")) {
                    repository.removeSystemInstance(originalSystemInstance);
                }
            }
        } else {
            repository.editSystemInstance(originalSystemInstance, newSystemInstance);

            // Add domain object edit to compound
            SystemEdit originalEdit = new SystemEdit(originalSystemInstance,
                    originalSystemInstance.getSystem().getName());
            compoundEdit.addEdit(new EditSystemInstanceEdit(originalEdit, undoEdit, context.getModel()));
        }

        // Post the compound edit
        compoundEdit.end();
        if (!compoundEdit.isInProgress() && compoundEdit.canUndo()) {
            context.getParentComponent().getUndoSupport().postEdit(compoundEdit);
        }

        // set observable model to changed in order to notify its views
        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Creates the editor for this System Instance. Extracted into a protected
     * method to enable testing without UI dialogs (can be overridden to return mock
     * editor).
     * 
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected SystemInstanceEditor createEditor(EditContext context) {
        return new SystemInstanceEditor(context.getRepository());
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "systeminstance", getSystemInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public SystemInstanceGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        SystemInstance sameSystemInstance = getSystemInstance();

        // Create new graphical wrapper referencing the SAME domain object
        SystemInstanceGR clonedGR = new SystemInstanceGR(sameSystemInstance, this.startingPoint.x);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;

        return clonedGR;
    }
}
