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

    public DeleteEditComponent createDeleteEdit(GraphicalElement element, DiagramModel model) {
        if (model instanceof SSDModel) {
            if (element instanceof RoleClassifierGR) {
                return new CompositeDeleteEdit(element, (SSDModel) model);
            } else {
                return new LeafDeleteEdit(element, (SSDModel) model);
            }
        } else if (model instanceof CCDModel) {
            if (element instanceof ConceptualClassGR) {
                return new CompositeDeleteEdit(element, (CCDModel) model);
            } else {
                return new LeafDeleteEdit(element, (CCDModel) model);
            }
        } else if (model instanceof SDModel) {
            if (element instanceof  RoleClassifierGR) {
                return new CompositeDeleteEdit(element, (SDModel) model);
            } else {
                return new LeafDeleteEdit(element, (SDModel) model);
            }
        } else if (model instanceof DCDModel) {
            if ((element instanceof ClassGR) ||
                    (element instanceof InterfaceGR)) {
                return new CompositeDeleteEdit(element, (DCDModel) model);
            } else {
                return new LeafDeleteEdit(element, (DCDModel) model);
            }
        }

        return null;
    }
}
