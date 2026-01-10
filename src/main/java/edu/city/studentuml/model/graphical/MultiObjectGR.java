package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditMultiObjectEdit;
import edu.city.studentuml.util.undoredo.MultiObjectEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.MultiObjectEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * @author Ervin Ramollari
 */
public class MultiObjectGR extends AbstractSDObjectGR {

    private static final double FRONT_BACK_DISTANCE = 6.0;

    public MultiObjectGR(MultiObject multiObject, int x) {
        super(multiObject, x);
    }

    @Override
    public boolean contains(Point2D point) {
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + FRONT_BACK_DISTANCE, getY() - FRONT_BACK_DISTANCE,
                width, height);
        return super.contains(point) || rectangle2.contains(point);
    }

    @Override
    protected void drawObjectShape(Graphics2D g, int startingX, int startingY) {
        Shape frontBox = new Rectangle2D.Double(startingX, startingY, width, height);
        Shape backBox = new Rectangle2D.Double(startingX + FRONT_BACK_DISTANCE, startingY - FRONT_BACK_DISTANCE, width,
                height);

        Paint originalPaint = g.getPaint();

        // draw the back box
        g.setPaint(getFillColor());
        g.fill(backBox);
        g.setPaint(originalPaint);
        g.draw(backBox);

        // draw the front box
        g.setPaint(getFillColor());
        g.fill(frontBox);
        g.setPaint(originalPaint);
        g.draw(frontBox);

    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public MultiObject getMultiObject() {
        return (MultiObject) roleClassifier;
    }

    public void setMultiObject(MultiObject mo) {
        roleClassifier = mo;
    }

    @Override
    public boolean edit(EditContext context) {
        CentralRepository repository = context.getModel().getCentralRepository();
        MultiObject originalMultiObject = getMultiObject();

        // Create editor and initial result
        MultiObjectEditor multiObjectEditor = createEditor(context);
        TypedEntityEditResult<DesignClass, MultiObject> initialResult = new TypedEntityEditResult<>(originalMultiObject,
                new ArrayList<>());

        // Use new editDialog() method
        TypedEntityEditResult<DesignClass, MultiObject> result = multiObjectEditor.editDialog(initialResult,
                context.getParentComponent());

        // Check if user cancelled
        if (result == null) {
            return true;
        }

        MultiObject newMultiObject = result.getDomainObject();

        // UNDO/REDO setup
        MultiObject undoObject = new MultiObject(originalMultiObject.getName(), originalMultiObject.getDesignClass());
        MultiObjectEdit undoEdit = new MultiObjectEdit(undoObject, originalMultiObject.getDesignClass().getName());

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // edit the multiobject if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalMultiObject.getName().equals(newMultiObject.getName())
                && repository.getMultiObject(newMultiObject.getName()) != null
                && !newMultiObject.getName().equals("")) {
            int response = JOptionPane.showConfirmDialog(null,
                    "There is an existing multiobject with the given name already.\n"
                            + "Do you want this diagram object to refer to the existing one?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                setMultiObject(repository.getMultiObject(newMultiObject.getName()));

                if (originalMultiObject.getName().equals("")) {
                    repository.removeMultiObject(originalMultiObject);
                }
            }
        } else {
            repository.editMultiObject(originalMultiObject, newMultiObject);

            // Add domain object edit to compound
            MultiObjectEdit originalEdit = new MultiObjectEdit(originalMultiObject,
                    originalMultiObject.getDesignClass().getName());
            compoundEdit.addEdit(new EditMultiObjectEdit(originalEdit, undoEdit, context.getModel()));
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
     * Creates the editor for this Multi Object. Extracted into a protected method
     * to enable testing without UI dialogs (can be overridden to return mock
     * editor).
     * 
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected MultiObjectEditor createEditor(EditContext context) {
        return new MultiObjectEditor(context.getRepository());
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "multiobject", getMultiObject());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public MultiObjectGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        MultiObject sameMultiObject = getMultiObject();

        // Create new graphical wrapper referencing the SAME domain object
        MultiObjectGR clonedGR = new MultiObjectGR(sameMultiObject, this.startingPoint.x);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;

        return clonedGR;
    }
}
