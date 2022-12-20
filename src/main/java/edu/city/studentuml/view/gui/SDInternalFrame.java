package edu.city.studentuml.view.gui;

import edu.city.studentuml.controller.DrawLineController;
import edu.city.studentuml.controller.SDSelectionController;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.SDView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SDInternalFrame extends DiagramInternalFrame {

    public SDInternalFrame(SDModel sdModel) {
        super(sdModel.getDiagramName());
        model = sdModel;
        view = new SDView((SDModel) model);
        selectionController = new SDSelectionController(this, (SDModel) model);
        drawLineController = new DrawLineController(view, model);//TK draw line
        view.addMouseListener(selectionController.getMouseListener());
        view.addMouseMotionListener(selectionController.getMouseMotionListener());

        JPanel drawingPanel = new JPanel();
        
        createHelpMenubar();
        
        drawingPanel.add(view);
        getContentPane().add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        toolbar = new SDDrawingToolbar(this);
        toolbar.setFloatable(false);
        toolbar.setLayout(new GridLayout(0, 1));
        JScrollPane sp = new JScrollPane(toolbar);
        sp.setPreferredSize(new Dimension(55, 400));
        getContentPane().add(sp, BorderLayout.WEST);
        setAddElementController(addElementControllerFactory.newAddElementController(model, this, "SDObjectGR"));
        setSize(550, 450);
    }

    private void createHelpMenubar() {
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
    
    
}
