package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.AddElementController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.UCDSelectionController;
import edu.city.studentuml.controller.UseCaseResizeWithCoveredElementsController;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.view.UCDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author draganbisercic
 */
public class UCDInternalFrame extends DiagramInternalFrame {

    public UCDInternalFrame(UCDModel ucdModel) {
        super(ucdModel.getDiagramName());
        model = ucdModel;
        view = new UCDView((UCDModel) model);

        // add view to drawing panel in the center and toolbar to the west
        JPanel drawingPanel = new JPanel();
        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new UCDToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);

        // create selection, draw line, and add element controllers
        selectionController = new UCDSelectionController(this, (UCDModel) model);
        resizeController = new UseCaseResizeWithCoveredElementsController(this, model, selectionController);
        drawLineController = new DrawLineController(view, model);

        view.addMouseListener(resizeController.getMouseListener());
        view.addMouseMotionListener(resizeController.getMouseMotionListener());

        // pass selection controller and add element controller to view
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "ActorGR"));

        setSize(550, 450);
    }

    @Override
    public void setAddElementController(AddElementController controller) {
        super.setAddElementController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

    @Override
    public void setDrawLineController(DrawLineController controller) {//TK draw line
        super.setDrawLineController(controller);
        resizeController.setSelectionMode(getSelectionMode());
    }

}
