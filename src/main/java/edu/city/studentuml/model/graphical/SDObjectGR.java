package edu.city.studentuml.model.graphical;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditSDObjectEdit;
import edu.city.studentuml.util.undoredo.ObjectEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.ObjectEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

public class SDObjectGR extends AbstractSDObjectGR {

    public SDObjectGR(SDObject obj, int x) {
        super(obj, x);
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public SDObject getSDObject() {
        return (SDObject) roleClassifier;
    }

    public void setSDObject(SDObject obj) {
        roleClassifier = obj;
    }

    @Override
    public boolean edit(EditContext context) {
        CentralRepository repository = context.getModel().getCentralRepository();
        SDObject originalObject = getSDObject();

        // Create editor and initial result
        ObjectEditor objectEditor = createEditor(context);
        TypedEntityEditResult<DesignClass, SDObject> initialResult = new TypedEntityEditResult<>(originalObject,
                new ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<DesignClass, SDObject> result = objectEditor.editDialog(initialResult,
                context.getParentComponent());

        // Check if user cancelled
        if (result == null) {
            return true;
        }

        SDObject newObject = result.getDomainObject();

        // UNDO/REDO setup - clone the original object to preserve all properties
        SDObject undoObject = originalObject.clone();
        ObjectEdit undoEdit = new ObjectEdit(undoObject, originalObject.getDesignClass().getName());

        // Create compound edit for all operations (type operations + domain object change)
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // Handle domain object editing with conflict checking
        if (!originalObject.getName().equals(newObject.getName())
                && repository.getObject(newObject.getName()) != null
                && !newObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing object with the given name already.\n"
                            + "Do you want this diagram object to refer to the existing one?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                setSDObject(repository.getObject(newObject.getName()));

                if (originalObject.getName().equals("")) {
                    repository.removeObject(originalObject);
                }
            }
        } else {
            repository.editObject(originalObject, newObject);
            // Add domain object edit to compound
            ObjectEdit originalEdit = new ObjectEdit(originalObject, originalObject.getDesignClass().getName());
            compoundEdit.addEdit(new EditSDObjectEdit(originalEdit, undoEdit, context.getModel()));
        }

        // Post the compound edit if there were any edits
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
     * Creates the editor for this SD Object. Extracted into a protected method to
     * enable testing without UI dialogs (can be overridden to return mock editor).
     * 
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected ObjectEditor createEditor(EditContext context) {
        return new ObjectEditor(context.getRepository());
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "sdobject", getSDObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public SDObjectGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        SDObject sameSDObject = getSDObject();

        // Create new graphical wrapper referencing the SAME domain object
        SDObjectGR clonedGR = new SDObjectGR(sameSDObject, this.startingPoint.x);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;

        return clonedGR;
    }
}
