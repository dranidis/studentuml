package edu.city.studentuml.view.gui.components;

import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import edu.city.studentuml.model.graphical.DiagramType;
import edu.city.studentuml.model.graphical.GraphicsHelper;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.MyImageIcon;
import edu.city.studentuml.view.gui.ApplicationGUI;

public class ProjectToolBar extends JToolBar {

    private JButton createToolBarButton(String iconFileName, String toolTipText, ActionListener listener) {
        final int MAX_HEIGHT = 20;
        ImageIcon newIcon = new MyImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + iconFileName));

        if (newIcon.getIconHeight() > MAX_HEIGHT) {
            Image img2 = newIcon.getImage();
            Image imgScaled2 = img2.getScaledInstance(-1, MAX_HEIGHT, Image.SCALE_SMOOTH);
            newIcon.setImage(imgScaled2);
        }

        JButton button = new JButton(newIcon);
        GraphicsHelper.addHightLightMouseAdapter(button);
        GraphicsHelper.clearBorder(button);
        button.setToolTipText(toolTipText);
        button.addActionListener(listener);
        return button;
    }

    public ProjectToolBar(ApplicationGUI applicationGUI) {
        setFloatable(false);

        JButton openButton = createToolBarButton("open.gif", "Open Project", e -> applicationGUI.openProject());
        JButton saveButton = createToolBarButton("save.gif", "Save Project", e -> applicationGUI.saveProject());
        JButton saveAsButton = createToolBarButton("save_as2.gif", "Save As", e -> applicationGUI.saveProjectAs());
        JButton exportButton = createToolBarButton("export.gif", "Export to image", e -> applicationGUI.exportImage());
        // JButton codeGenerateButton = createToolBarButton("code.gif", "Generate Code", e -> applicationGUI.forwardEngineer());

        if (!ApplicationGUI.isApplet()) { // applet version does not allow creation of new project
            add(createToolBarButton("new.gif", "New Project", e -> applicationGUI.newProject()));
        }
        add(openButton);
        add(saveButton);
        if (!ApplicationGUI.isApplet()) {
            add(saveAsButton);
            add(exportButton);
        }

        /*
         * uncomment to show button
         */
        // add(codeGenerateButton);

        addSeparator();

        JButton useCaseButton = createToolBarButton("useCaseDiagram.gif", "New Use Case Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.UCD));
        JButton ssdButton = createToolBarButton("ssd.gif", "New System Sequence Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.SSD));
        JButton ccdButton = createToolBarButton("ccd.gif", "New Conceptual Class Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.CCD));
        JButton sdButton = createToolBarButton("sd.gif", "New Sequence Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.SD));
        JButton dcdButton = createToolBarButton("dcd.gif", "New Design Class Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.DCD));
        JButton adButton = createToolBarButton("activityDiagram.gif", "New Activity Diagram",
                e -> applicationGUI.createNewInternalFrame(DiagramType.AD));

        add(adButton);
        add(useCaseButton);
        add(ccdButton);
        add(ssdButton);
        add(sdButton);
        add(dcdButton);

        /**
         * Unused buttons
         */
        // JButton validateSD_DCDButton = createToolBarButton("sd_dcd.gif", "Validate SD against DCD", e -> {});
        // JButton reloadRulesButton = createToolBarButton("reload.gif", "Reload Rules", e -> applicationGUI.reloadRules());
        // JButton helpButton = createToolBarButton("help.gif", "Get help on using StudentUML", e -> applicationGUI.help());

        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    }

}
