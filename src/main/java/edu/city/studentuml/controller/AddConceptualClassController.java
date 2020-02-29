/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import java.awt.Point;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author draganbisercic
 */
public class AddConceptualClassController extends AddElementController {

    public AddConceptualClassController(CCDModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    public void pressed(int x, int y) {
        ConceptualClassGR graphicalClass = new ConceptualClassGR(new ConceptualClass(""), new Point(x, y));

        UndoableEdit edit = new AddEdit(graphicalClass, diagramModel);

        diagramModel.addGraphicalElement(graphicalClass);
        if (parentFrame instanceof CCDInternalFrame) {
            ((CCDInternalFrame) parentFrame).setSelectionMode();
        }

        parentFrame.getUndoSupport().postEdit(edit);
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}
