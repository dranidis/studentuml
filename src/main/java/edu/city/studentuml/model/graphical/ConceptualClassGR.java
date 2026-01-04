package edu.city.studentuml.model.graphical;

import java.awt.Point;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.ConceptualClass;
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

    // ========== EDIT OPERATION ==========

    /**
     * Factory method to create a ConceptualClassEditor for testing purposes. Can be
     * overridden in tests to provide mock editors.
     */
    protected ConceptualClassEditor createConceptualClassEditor(EditContext context) {
        return new ConceptualClassEditor(context.getRepository());
    }

    @Override
    public boolean edit(EditContext context) {
        return editClassifierWithDialog(
                context,
                this::getConceptualClass,
                this::setConceptualClass,
                (original, parent) -> createConceptualClassEditor(context).editDialog(original, parent),
                context.getRepository()::getConceptualClass,
                context.getRepository()::removeConceptualClass,
                (repo, orig, edited) -> repo.editConceptualClass(orig, edited),
                (orig, edited, model) -> new EditCCDClassEdit(orig, edited, model));
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
