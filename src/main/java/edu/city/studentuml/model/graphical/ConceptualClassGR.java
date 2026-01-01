package edu.city.studentuml.model.graphical;

import java.awt.Point;

import javax.swing.undo.UndoableEdit;

import org.w3c.dom.Element;

import edu.city.studentuml.controller.EditContext;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.EditCCDClassEdit;
import edu.city.studentuml.view.gui.ConceptualClassEditor;

/**
 * @author draganbisercic
 */
public class ConceptualClassGR extends AbstractClassGR {

    public ConceptualClassGR(ConceptualClass c, Point start) {
        super(c, start);
    }

    public void setConceptualClass(ConceptualClass cl) {
        abstractClass = cl;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public ConceptualClass getConceptualClass() {
        return (ConceptualClass) abstractClass;
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "conceptualclass", getConceptualClass());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    /**
     * Polymorphic edit method for ConceptualClassGR. Implements Pattern 2 (Name
     * Conflict) - DCD/CCD variant (silent merge on conflict).
     * 
     * @param context The edit context containing model, repository, parent
     *                component, and undo support
     * @return true if the edit was successful, false if user cancelled
     */
    @Override
    public boolean edit(EditContext context) {
        CentralRepository repository = context.getRepository();
        ConceptualClass originalClass = this.getConceptualClass();
        ConceptualClassEditor classEditor = new ConceptualClassEditor(repository);

        // show the class editor dialog and check whether the user has pressed cancel
        ConceptualClass newClass = classEditor.editDialog(originalClass, context.getParentComponent());
        if (newClass == null) {
            return false; // User cancelled
        }

        // Pattern 2: Name conflict handling - DCD/CCD variant (silent merge)
        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!originalClass.getName().equals(newClass.getName())
                && repository.getConceptualClass(newClass.getName()) != null
                && !newClass.getName().equals("")) {

            // Name conflict: replace this graphical element's reference with the existing class
            this.setConceptualClass(repository.getConceptualClass(newClass.getName()));

            // remove the existing class if it has no name
            if (originalClass.getName().equals("")) {
                repository.removeConceptualClass(originalClass);
            }
        } else {
            // No conflict: normal edit with undo/redo
            UndoableEdit edit = new EditCCDClassEdit(originalClass, newClass, context.getModel());
            repository.editConceptualClass(originalClass, newClass);
            context.getUndoSupport().postEdit(edit);
        }

        // set observable model to changed in order to notify its views
        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    @Override
    public ConceptualClassGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Multiple graphical elements can reference the same domain object
        ConceptualClass sameClass = getConceptualClass();

        // Create new graphical wrapper referencing the SAME domain object
        ConceptualClassGR clonedGR = new ConceptualClassGR(sameClass,
                new Point(this.startingPoint.x, this.startingPoint.y));

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;

        return clonedGR;
    }
}
