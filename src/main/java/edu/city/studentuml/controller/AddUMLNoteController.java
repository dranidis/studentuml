package edu.city.studentuml.controller;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.undoredo.AddEdit;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

public class AddUMLNoteController extends AddElementController {
    private static final Logger logger = Logger.getLogger(AddUMLNoteController.class.getName());

    private static final String MESSAGE = "To create a note you must select an element first, then click the note button and anywhere on the diagram.";
    GraphicalElement selectedElement;

    public AddUMLNoteController(DiagramModel model, DiagramInternalFrame frame) {
        super(model, frame);
    }

    @Override
    public void pressed(int x, int y) {
        if (!diagramModel.getSelectedGraphicalElements().isEmpty()) {
            selectedElement = diagramModel.getSelectedGraphicalElements().get(0);
        } else {
            JOptionPane.showMessageDialog(null, MESSAGE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }        
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
        logger.info(() -> "dragged XY: " + x + y);
    }

    public void released(int x, int y) {
        logger.info(() -> "released XY: " + x + y);
    }
}
