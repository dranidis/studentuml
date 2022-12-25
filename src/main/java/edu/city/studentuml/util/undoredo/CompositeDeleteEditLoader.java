package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;

/**
 *
 * @author draganbisercic
 */
public class CompositeDeleteEditLoader {
    private CompositeDeleteEditLoader() {
        throw new IllegalStateException("Utility class");
    }

    public static void loadCompositeDeleteEdit(GraphicalElement e, CompositeDeleteEdit edit, DiagramModel model) {
        if (e instanceof ConceptualClassGR) {
            loadCCDClassCompositeDeleteEdit((ConceptualClassGR) e, edit, model);
        } else if (e instanceof ClassGR) {
            loadDCDClassCompositeDeleteEdit((ClassGR) e, edit, model);
        } else if (e instanceof InterfaceGR) {
            loadInterfaceCompositeDeleteEdit((InterfaceGR) e, edit, model);
        } else if (e instanceof RoleClassifierGR) {
            loadRoleClassifierCompositeDeleteEdit((RoleClassifierGR) e, edit, model);
        }
    }

    private static void loadCCDClassCompositeDeleteEdit(ConceptualClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(c, model));

        ((CCDModel) model).getClassGRAssociationGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((CCDModel) model).getClassGRAssociationClassGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((CCDModel) model).getClassGRGeneralizationGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
    }

    private static void loadDCDClassCompositeDeleteEdit(ClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(c, model));

        ((DCDModel) model).getClassGRDependencyGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getClassGRAssociationGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getClassGRAssociationClassGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getClassGRRealizationGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getClassGRGeneralizationGRs(c).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
    }

    private static void loadInterfaceCompositeDeleteEdit(InterfaceGR i, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(i, model));

        /**
         * aggregations are associations
         */
        ((DCDModel) model).getInterfaceGRAssociationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getInterfaceGRGeneralizationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
        ((DCDModel) model).getInterfaceGRRealizationGRs(i).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
    }

    private static void loadRoleClassifierCompositeDeleteEdit(RoleClassifierGR rc, CompositeDeleteEdit edit,
            DiagramModel model) {
        LeafDeleteEdit leaf;

        leaf = new LeafDeleteEdit(rc, model);
        edit.add(leaf);

        ((AbstractSDModel) model).getRoleClaffierGRMessages(rc).forEach(e -> edit.add(new LeafDeleteEdit(e, model)));
    }
}
