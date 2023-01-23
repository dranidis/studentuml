package edu.city.studentuml.view.gui.components;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import edu.city.studentuml.model.graphical.DiagramType;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.MyImageIcon;
import edu.city.studentuml.view.gui.ApplicationGUI;

public class ProjectToolBar extends JToolBar {

    // private JButton validateSD_DCDButton;

    private JButton createToolBarButton(String iconFileName, String toolTipText, ActionListener listener) {
        ImageIcon newIcon = new MyImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + iconFileName));
        JButton button = new JButton(newIcon);
        button.setBorder(new EmptyBorder(5, 5, 5, 5));
        button.setToolTipText(toolTipText);
        addBorderListener(button);
        button.addActionListener(listener);
        return button;
    }

    public ProjectToolBar(ApplicationGUI applicationGUI) {
        setFloatable(false);

        JButton newButton = createToolBarButton("new.gif", "New Project", e -> applicationGUI.newProject());
        JButton openButton = createToolBarButton("open.gif", "Open Project", e -> applicationGUI.openProject());
        JButton saveButton = createToolBarButton("save.gif", "Save Project", e -> applicationGUI.saveProject());
        JButton saveAsButton = createToolBarButton("save_as2.gif", "Save As", e -> applicationGUI.saveProjectAs());
        JButton exportButton = createToolBarButton("export.gif", "Export to image", e -> applicationGUI.exportImage());

        if (!ApplicationGUI.isApplet()) { // applet version does not allow creation of new project
            add(newButton);
        }
        add(openButton);
        add(saveButton);
        if (!ApplicationGUI.isApplet()) {
            add(saveAsButton);
            add(exportButton);
            addSeparator();
        }

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

        // Icon validateSD_DCDIcon = new
        // ImageIcon(Application.class.getResource(imageLocation + "sd_dcd.gif"));
        // validateSD_DCDButton = new JButton(validateSD_DCDIcon);
        // validateSD_DCDButton.setToolTipText("Validate SD against DCD");
        // validateSD_DCDButton.addActionListener(this);
        // addSeparator();

        // JButton reloadRulesButton = createToolBarButton("reload.gif", "Reload Rules", e -> reloadRules());

        /**
         * TODO: REMOVE TILL it is clear what it does! // add(reloadRulesButton);
         */

        // addSeparator();

        // ImageIcon forwardEngineerIcon = new MyImageIcon(
        //         this.getClass().getResource(Constants.IMAGES_DIR + "code.gif"));
        // Image img2 = forwardEngineerIcon.getImage();
        // Image imgScaled2 = img2.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
        // forwardEngineerIcon.setImage(imgScaled2);
        // JButton forwardEngineerButton = new JButton(forwardEngineerIcon);
        // forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        // forwardEngineerButton.setToolTipText("Generate Code");
        // addBorderListener(forwardEngineerButton);

        // forwardEngineerButton.addActionListener(e -> {
        //     JCheckBox checkBox = new JCheckBox("Update Current Files", false);
        //     String message = "Do you Want to Generate Code? \n"
        //             + "Make Sure You Have Created and Saved the Approrpiate\n"
        //             + "Design (first) and Sequence Diagrams!";
        //     Object[] params = { message, checkBox };
        //     // 0 for yes and 1 for no
        //     int codeGenerationConfirm = JOptionPane.showConfirmDialog(frame, params, "Code Generation",
        //             JOptionPane.YES_NO_OPTION);
        //     if (codeGenerationConfirm == 0) {
        //         CodePreparation codePreparation = new CodePreparation();
        //         int genFilesCount = codePreparation.generateCode(checkBox.isSelected());
        //         if (genFilesCount > 0) {
        //             JOptionPane.showMessageDialog(frame,
        //                     "Success!! \n" + "You have generated " + genFilesCount + " files in\n"
        //                             + umlProject.getFilepath().replace(".xml", File.separator),
        //                     "Code Generator", JOptionPane.INFORMATION_MESSAGE);
        //         } else {
        //             JOptionPane.showMessageDialog(frame, "No Input - New Files Not Generated", "Code Generator",
        //                     JOptionPane.INFORMATION_MESSAGE);
        //         }
        //     }
        // });

        /**
         * TODO: REMOVE THE BUTTON TILL code generation is completed! //
         * add(forwardEngineerButton);
         */

        // ImageIcon helpIcon = new MyImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "help.gif"));
        // Image img = helpIcon.getImage();
        // Image imgScaled = img.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
        // helpIcon.setImage(imgScaled);

        // JButton helpButton = new JButton(helpIcon);
        // helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        // helpButton.setToolTipText("Get help on using StudentUML");
        // addBorderListener(helpButton);

        // helpButton.addActionListener(e -> help());

        /**
         * TODO: REMOVE THE HELP BUTTON TILL HELP IS IMPLEMENTED // add(helpButton);
         */

        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    }

    private void addBorderListener(JButton button) {
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(
                        new CompoundBorder(new LineBorder(Colors.getHighlightColor(), 1), new EmptyBorder(4, 4, 4, 4)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(new EmptyBorder(5, 5, 5, 5));
            }
        });
    }

}
