package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.CCDSelectionController;
import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.view.CCDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CCDInternalFrame extends DiagramInternalFrame {

    public CCDInternalFrame(CCDModel ccdModel) {
        super(ccdModel.getDiagramName());
        model = ccdModel;
        view = new CCDView((CCDModel) model);
        selectionController = new CCDSelectionController(this, (CCDModel) model);
        drawLineController = new DrawLineController(view, (CCDModel) model);
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());
        JPanel drawingPanel = new JPanel();

        drawingPanel.add(view);
        toolbar = new CCDToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0,1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "ConceptualClassGR"));
        //Let user specify this, or load
        setSize(550, 450);
    }

}
