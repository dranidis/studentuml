package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.ADSelectionController;
import edu.city.studentuml.controller.ActivityResizeWithCoveredElementsController;
import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.EdgeController;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.view.ADView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Biser
 */
public class ADInternalFrame extends DiagramInternalFrame {

    public ADInternalFrame(ADModel adModel) {
        super(adModel.getDiagramName());
        model = adModel;
        view = new ADView((ADModel) model);
        // add view to drawing panel in the center and toolbar to the west
        JPanel drawingPanel = new JPanel();
        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new ADDrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);

        // create selection, draw line, and add element controllers
        selectionController = new ADSelectionController(this, (ADModel) model);
        resizeController = new ActivityResizeWithCoveredElementsController(this, model, selectionController);
        drawLineController = new DrawLineController(view, model);
        edgeController = new EdgeController(this, (ADModel) model, selectionController);

        view.addMouseListener(resizeController.getMouseListener());
        view.addMouseMotionListener(resizeController.getMouseMotionListener());

        // pass selection controller and add element controller to view
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        view.addMouseListener(edgeController.getMouseListener());
        view.addMouseMotionListener(edgeController.getMouseMotionListener());
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "InitialNodeGR"));

        setSize(550, 450);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
        edgeController.setSelectionMode(getSelectionMode());
    }

}
