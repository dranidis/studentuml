/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.controller;

//~--- JDK imports ------------------------------------------------------------
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.awt.Point;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

public class AddUMLNoteController extends AddElementController {

    GraphicalElement selectedElement;

    public AddUMLNoteController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
        selectedElement = null;

        // if no selection has been made quit
        if (diagramModel.getSelectedGraphicalElements().size() > 0) {
            selectedElement = diagramModel.getSelectedGraphicalElements().get(0);
        } else {
            JOptionPane.showMessageDialog(null, "You must select an element",
                    "Error", JOptionPane.ERROR_MESSAGE);

            parentFrame.setSelectionMode();
        }
    }

    public void pressed(int x, int y) {
        UndoableEdit edit;

        if (selectedElement != null && !(selectedElement instanceof UMLNoteGR)) {
            UMLNoteGR graphicalNote = new UMLNoteGR(null, selectedElement, new Point(x, y));

            edit = new AddEdit(graphicalNote, diagramModel);

            diagramModel.addGraphicalElement(graphicalNote);

            parentFrame.getUndoSupport().postEdit(edit);
        }

        parentFrame.setSelectionMode();
    }

    public void dragged(int x, int y) {
    }

    public void released(int x, int y) {
    }
}
