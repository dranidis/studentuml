package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.controller.ResizeWithCoveredElementsController;
import edu.city.studentuml.controller.SDSelectionController;
import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.view.SDView;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class SDInternalFrame extends DiagramInternalFrame {

    public SDInternalFrame(SDModel model) {
        super(model.getDiagramName(), model);
    }

    @Override
    protected void createHelpMenubar() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText(" Help ");
        menuBar.add(helpMenu);

        JMenuItem selectMenuItem = new JMenuItem();
        selectMenuItem.setText("Selection keystrokes");
        selectMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ctrl-click adds the clicked messages to the selected messages.\n\n" +
                "Shift-Ctrl click selects a message and all the messages below it", 
                "Selection keystrokes", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(selectMenuItem);
    }

    @Override
    protected DiagramView makeView(DiagramModel model) {
        return new SDView(model);
    }

    @Override
    protected AbsractToolbar makeToolbar(DiagramInternalFrame diagramInternalFrame) {
        return new SDToolbar(this);
    }

    @Override
    protected SelectionController makeSelectionController(DiagramInternalFrame diagramInternalFrame,
            DiagramModel model) {
        return new SDSelectionController(this, model);
    }

    @Override
    protected ResizeWithCoveredElementsController makeResizeWithCoveredElementsController(
            DiagramInternalFrame diagramInternalFrame, DiagramModel model, SelectionController selectionController) {
        return null;
    }

    @Override
    protected EdgeController makeEdgeController(DiagramInternalFrame diagramInternalFrame, DiagramModel model,
            SelectionController selectionController) {
        return null;
    }

    @Override
    protected String makeElementClassString() {
        return "SDObjectGR";
    }    
    
}
