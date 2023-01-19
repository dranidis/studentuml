package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.util.NotifierVector;


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
        } else if (e instanceof UCDComponentGR) {
            loadUCDComponentCompositeDeleteEdit((UCDComponentGR) e, edit, model);
        }  else if (e instanceof NodeComponentGR) {
            loadNodeComponentCompositeDeleteEdit((NodeComponentGR) e, edit, model);
        } else {
            edit.add(new LeafDeleteEdit(e, model));
        }

        NotifierVector<GraphicalElement> elements = model.getGraphicalElements();
        int i = 0;
        while (i < elements.size()) {
            GraphicalElement o = elements.get(i);
            if (o instanceof UMLNoteGR && ((UMLNoteGR) o).getTo().equals(e)) {
                edit.add(new LeafDeleteEdit(o, model));
                model.removeGraphicalElement(o);
            } else {
                i++;
            }
        }        
    }

    private static void loadUCDComponentCompositeDeleteEdit(UCDComponentGR c, CompositeDeleteEdit edit,
            DiagramModel model) {
                int index = c.getNumberOfElements() - 1;
                while (index >= 0) {
                    UCDComponentGR n = c.getElement(index);
                    loadUCDComponentCompositeDeleteEdit(n, edit, model);
                    // update index
                    index--;
                }
                edit.add(new LeafDeleteEdit(c, model));

                c.getIncomingRelations().forEach(e -> loadCompositeDeleteEdit(e, edit, model));
                c.getOutgoingRelations().forEachRemaining(e -> loadCompositeDeleteEdit(e, edit, model));
    }

    private static void loadNodeComponentCompositeDeleteEdit(NodeComponentGR c, CompositeDeleteEdit edit,
            DiagramModel model) {
                int index = c.getNumberOfElements() - 1;
                while (index >= 0) {
                    NodeComponentGR n = c.getElement(index);
                    loadNodeComponentCompositeDeleteEdit(n, edit, model);
                    // update index
                    index--;
                }
                edit.add(new LeafDeleteEdit(c, model));

                c.getIncomingRelations().forEachRemaining(e -> loadCompositeDeleteEdit(e, edit, model));
                c.getOutgoingRelations().forEachRemaining(e -> loadCompositeDeleteEdit(e, edit, model));
    }

    private static void loadCCDClassCompositeDeleteEdit(ConceptualClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(c, model));

        ((CCDModel) model).getClassGRAssociationGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((CCDModel) model).getClassGRAssociationClassGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((CCDModel) model).getClassGRGeneralizationGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
    }

    private static void loadDCDClassCompositeDeleteEdit(ClassGR c, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(c, model));

        ((DCDModel) model).getClassGRDependencyGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getClassGRAssociationGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getClassGRAssociationClassGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getClassGRRealizationGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getClassGRGeneralizationGRs(c).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
    }

    private static void loadInterfaceCompositeDeleteEdit(InterfaceGR i, CompositeDeleteEdit edit, DiagramModel model) {
        edit.add(new LeafDeleteEdit(i, model));

        /**
         * aggregations are associations
         */
        ((DCDModel) model).getInterfaceGRAssociationGRs(i).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getInterfaceGRGeneralizationGRs(i).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
        ((DCDModel) model).getInterfaceGRRealizationGRs(i).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
    }

    private static void loadRoleClassifierCompositeDeleteEdit(RoleClassifierGR rc, CompositeDeleteEdit edit,
            DiagramModel model) {

        edit.add(new LeafDeleteEdit(rc, model));
        ((AbstractSDModel) model).getRoleClaffierGRMessages(rc).forEach(e -> loadCompositeDeleteEdit(e, edit, model));
    }
}
