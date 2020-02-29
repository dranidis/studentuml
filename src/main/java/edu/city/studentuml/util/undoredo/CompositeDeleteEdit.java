package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author draganbisercic
 */
public class CompositeDeleteEdit extends DeleteEditComponent {

    List deleteEditComponents = new ArrayList();
    Object clone;   //need to store model elements before they are cleared at deletion

    CompositeDeleteEdit(GraphicalElement element, DiagramModel model) {
        super(element, model);
        setClone();
    }

    private void setClone() {
        if (element instanceof ConceptualClassGR) {
            clone = ((ConceptualClassGR) element).getConceptualClass().clone();
        } else if (element instanceof ClassGR) {
            clone = ((ClassGR) element).getDesignClass().clone();
        } else if (element instanceof InterfaceGR) {
            clone = ((InterfaceGR) element).getInterface().clone();
        } else if (element instanceof RoleClassifierGR) {
            if (element instanceof ActorInstanceGR) {
                clone = ((ActorInstanceGR) element).getActorInstance().clone();
            } else if (element instanceof SystemInstanceGR) {
                clone = ((SystemInstanceGR) element).getSystemInstance().clone();
            } else if (element instanceof SDObjectGR) {
                clone = ((SDObjectGR) element).getSDObject().clone();
            } else if (element instanceof MultiObjectGR) {
                clone = ((MultiObjectGR) element).getMultiObject().clone();
            }
        }
    }

    public void add(DeleteEditComponent edit) {
        deleteEditComponents.add(edit);
    }

    public void remove(DeleteEditComponent edit) {
        deleteEditComponents.remove(edit);
    }

    public DeleteEditComponent getChild(int i) {
        return (DeleteEditComponent) deleteEditComponents.get(i);
    }

    @Override
    public void undo() throws CannotUndoException {
        DeleteEditComponent comp;
        Iterator i = deleteEditComponents.iterator();
        while (i.hasNext()) {
            comp = (DeleteEditComponent) i.next();
            comp.undo();
        }

        // need to edit element after it's been cleared at deletion time
        rebuildElement();

        // set observable model to changed in order to notify its views
        model.modelChanged();
        SystemWideObjectNamePool.getInstance().reload();
    }

    private void rebuildElement() {
        if (clone instanceof ConceptualClass) {
            ConceptualClass c = ((ConceptualClassGR) element).getConceptualClass();
            ConceptualClass d = (ConceptualClass) clone;
            model.getCentralRepository().editConceptualClass(c, d.clone());
        } else if (clone instanceof DesignClass) {
            DesignClass c = ((ClassGR) element).getDesignClass();
            DesignClass d = (DesignClass) clone;
            model.getCentralRepository().editClass(c, d.clone());
        } else if (clone instanceof Interface) {
            Interface i = ((InterfaceGR) element).getInterface();
            Interface j = (Interface) clone;
            model.getCentralRepository().editInterface(i, j.clone());
        } else if (clone instanceof ActorInstanceGR) {
            ActorInstance a = ((ActorInstanceGR) element).getActorInstance();
            ActorInstance b = (ActorInstance) clone;
            model.getCentralRepository().editActorInstance(a, b.clone());
        } else if (clone instanceof SystemInstanceGR) {
            SystemInstance s = ((SystemInstanceGR) element).getSystemInstance();
            SystemInstance t = (SystemInstance) clone;
            model.getCentralRepository().editSystemInstance(s, t.clone());
        } else if (clone instanceof SDObjectGR) {
            SDObject o = ((SDObjectGR) element).getSDObject();
            SDObject p = (SDObject) clone;
            model.getCentralRepository().editObject(o, p.clone());
        } else if (clone instanceof MultiObjectGR) {
            MultiObject m = ((MultiObjectGR) element).getMultiObject();
            MultiObject n = (MultiObject) clone;
            model.getCentralRepository().editMultiObject(m, n.clone());
        } 
    }

    @Override
    public void redo() throws CannotRedoException {
        DeleteEditComponent comp;
        Iterator i = deleteEditComponents.iterator();
        while (i.hasNext()) {
            comp = (DeleteEditComponent) i.next();
            comp.redo();
        }
    }
}
