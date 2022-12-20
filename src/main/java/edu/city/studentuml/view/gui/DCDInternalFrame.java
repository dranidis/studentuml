package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.DCDSelectionController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.view.DCDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DCDInternalFrame extends DiagramInternalFrame {

    private boolean advancedMode;

    public DCDInternalFrame(DCDModel dcdModel, boolean advancedMode) {
        super(dcdModel.getDiagramName());
        model = dcdModel;
        view = new DCDView((DCDModel) model);
        selectionController = new DCDSelectionController(this, (DCDModel) model);
        drawLineController = new DrawLineController(view, model);//TK draw line
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        JPanel drawingPanel = new JPanel();

        drawingPanel.add(view);
        toolbar = new DCDDrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "ClassGR"));

        setSize(550, 450);

        this.advancedMode = advancedMode;
    }

    public boolean isAdvancedMode() {
        return advancedMode;
    }

    public void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }
}
