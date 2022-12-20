package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.SSDSelectionController;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.view.SSDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SSDInternalFrame extends DiagramInternalFrame {

    public SSDInternalFrame(SSDModel ssdModel) {
        super(ssdModel.getDiagramName());
        model = ssdModel;
        view = new SSDView((SSDModel) model);
        selectionController = new SSDSelectionController(this, (SSDModel) model);
        drawLineController = new DrawLineController(view, model);//TK draw line
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());

        JPanel drawingPanel = new JPanel();

        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new SDDToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "SystemInstanceGR"));
        setSize(550, 450);
    }

}
