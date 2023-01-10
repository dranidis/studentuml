package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;

/**
 * @author draganbisercic
 */
public class DeleteEditFactory {

    private static DeleteEditFactory ref = null;

    private DeleteEditFactory() {
    }

    public static DeleteEditFactory getInstance() {
        if (ref == null) {
            ref = new DeleteEditFactory();
        }
        return ref;
    }

    public CompositeDeleteEdit createDeleteEdit(GraphicalElement element, DiagramModel model) {
        return new CompositeDeleteEdit(element, model);
    }

}
