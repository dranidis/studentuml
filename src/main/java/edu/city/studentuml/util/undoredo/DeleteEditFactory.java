package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;

/**
 *
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
        // if (element instanceof RoleClassifierGR || element instanceof ConceptualClassGR || element instanceof ClassGR
        //         || element instanceof InterfaceGR) {
        //     return new CompositeDeleteEdit(element, model);
        // } else {
        //     return new LeafDeleteEdit(element, model);
        // }
        return new CompositeDeleteEdit(element, model);

    }

}
