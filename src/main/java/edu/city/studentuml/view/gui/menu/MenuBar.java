package edu.city.studentuml.view.gui.menu;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.bulenkov.darcula.DarculaLaf;
import com.lipstikLF.LipstikLookAndFeel;

import edu.city.studentuml.model.graphical.DiagramType;
import edu.city.studentuml.util.RecentFiles;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.view.gui.ApplicationGUI;

/**
 * @author dimitris
 */
public class MenuBar {

    private static final Logger logger = Logger.getLogger(MenuBar.class.getName());

    private static final boolean SHOW_CONSISTENCY_CHECKING_MENU = false;

    ApplicationGUI app;
    JMenuBar jMenuBar;

    private JMenu recentFilesMenu;

    public MenuBar(ApplicationGUI app) {
        jMenuBar = new JMenuBar();

        this.app = app;
        createFileMenu();
        createCreateMenu();
        jMenuBar.add(createHelpMenu());
    }

    public JMenuBar getjMenuBar() {
        return jMenuBar;
    }

    public void createFileMenu() {
        JMenu fileMenu = new JMenu();
        fileMenu.setText(" File ");
        jMenuBar.add(fileMenu);

        JMenuItem newProjectMenuItem = new JMenuItem();
        newProjectMenuItem.setText("New Project");
        newProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

        newProjectMenuItem.addActionListener(e -> app.newProject());
        fileMenu.add(newProjectMenuItem);

        JMenuItem openProjectMenuItem = new JMenuItem();
        openProjectMenuItem.setText("Open Project");
        openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openProjectMenuItem.addActionListener(e -> app.openProject());
        fileMenu.add(openProjectMenuItem);

        recentFilesMenu = new JMenu();
        recentFilesMenu.setText("Open Recent");
        fileMenu.add(recentFilesMenu);

        loadRecentFilesInMenu();

        JMenuItem saveProjectMenuItem = new JMenuItem();
        saveProjectMenuItem.setText("Save");
        saveProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveProjectMenuItem.addActionListener(e -> app.saveProject());
        fileMenu.add(saveProjectMenuItem);

        JMenuItem saveProjectAsMenuItem = new JMenuItem();
        saveProjectAsMenuItem.setText("Save As...");
        saveProjectAsMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveProjectAsMenuItem.addActionListener(e -> app.saveProjectAs());
        fileMenu.add(saveProjectAsMenuItem);

        fileMenu.addSeparator();

        JMenuItem exportToImageMenuItem = new JMenuItem();
        exportToImageMenuItem.setText("Export To Image");
        exportToImageMenuItem.addActionListener(e -> app.exportImage());
        fileMenu.add(exportToImageMenuItem);

        createPreferencesSubmenu(fileMenu);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(e -> {
            if (app.closeProject()) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        if (ApplicationGUI.isApplet) {
            newProjectMenuItem.setEnabled(false);
            saveProjectAsMenuItem.setEnabled(false);
            exportToImageMenuItem.setEnabled(false);
            exitMenuItem.setEnabled(false);
        }
    }

    public void loadRecentFilesInMenu() {
        recentFilesMenu.removeAll();

        List<String> recentFiles = RecentFiles.getInstance().getRecentFiles();
        for (String fileName : recentFiles) {

            JMenuItem recentFile = new JMenuItem();
            recentFile.setText(fileName);
            recentFile.addActionListener(e -> app.openProjectFile(fileName));
            recentFilesMenu.add(recentFile);
        }
    }

    private void createPreferencesSubmenu(JMenu fileMenu) {
        JMenu preferencesMenu = new JMenu();
        preferencesMenu.setText("Preferences");
        fileMenu.add(preferencesMenu);

        JCheckBoxMenuItem selectLastCheckBoxMenuItem = new JCheckBoxMenuItem();
        selectLastCheckBoxMenuItem.setText("Keep last selection in diagram toolbars");
        selectLastCheckBoxMenuItem
                .setToolTipText("<html>An element can be selected and then drawn on the canvas several times without"
                        + " the need to select it again. <br>"
                        + "If this is disabled the selection is always reset to the selection arrow.</html>");
        selectLastCheckBoxMenuItem
                .addActionListener(e -> Settings.setKeepLastSelection(selectLastCheckBoxMenuItem.isSelected()));

        selectLastCheckBoxMenuItem.setSelected(Settings.keepLastSelection());
        preferencesMenu.add(selectLastCheckBoxMenuItem);

        preferencesMenu.addSeparator();

        JCheckBoxMenuItem showTypesInSDCheckBoxMenuItem = new JCheckBoxMenuItem();
        showTypesInSDCheckBoxMenuItem.setText("Show types in methods");
        showTypesInSDCheckBoxMenuItem
                .setToolTipText("Show types in methods in class diagrams and in sequence diagrams");
        showTypesInSDCheckBoxMenuItem.addActionListener(e -> {
            Settings.setShowTypes(showTypesInSDCheckBoxMenuItem.isSelected());
            repaintSDandSSDDiagrams();

        });

        showTypesInSDCheckBoxMenuItem.setSelected(Settings.showTypes());
        preferencesMenu.add(showTypesInSDCheckBoxMenuItem);

        preferencesMenu.addSeparator();

        JCheckBoxMenuItem showReturnsSDCheckBoxMenuItem = new JCheckBoxMenuItem();
        showReturnsSDCheckBoxMenuItem.setText("Show return arrows in Sequence Diagrams");
        showReturnsSDCheckBoxMenuItem.setToolTipText("");
        showReturnsSDCheckBoxMenuItem.addActionListener(e -> {
            Settings.setShowReturnArrows(showReturnsSDCheckBoxMenuItem.isSelected());
            repaintSDandSSDDiagrams();
        });

        showReturnsSDCheckBoxMenuItem.setSelected(Settings.showReturnArrows());
        preferencesMenu.add(showReturnsSDCheckBoxMenuItem);

        preferencesMenu.addSeparator();

        JCheckBoxMenuItem enableRuntimeConsistencyCheckBoxMenuItem = new JCheckBoxMenuItem();
        enableRuntimeConsistencyCheckBoxMenuItem.setText("Enable Runtime Consistency Checking");
        enableRuntimeConsistencyCheckBoxMenuItem.setToolTipText(
                "<html>Displays the message tab containing feedback and advisory information<br/> gained from the performed consistency checks. Also enables the user<br/> to perform automated repair operations</html>");
        enableRuntimeConsistencyCheckBoxMenuItem.addActionListener(e -> {
            Settings.setEnableConsistencyCheck(enableRuntimeConsistencyCheckBoxMenuItem.isSelected());
            app.setRunTimeConsistencyCheckAndShowTabbedPane(enableRuntimeConsistencyCheckBoxMenuItem.isSelected());
        });
        enableRuntimeConsistencyCheckBoxMenuItem.setSelected(Settings.isConsistencyCheckEnabled());

        JCheckBoxMenuItem showRuleEditorCheckBoxMenuItem = new JCheckBoxMenuItem();
        showRuleEditorCheckBoxMenuItem.setText("Show Rule Editor Tab");
        showRuleEditorCheckBoxMenuItem.setToolTipText(
                "<html><b>Advanced:</b> Displays the rule editor tab that enables the user<br/> to edit the rules on which the consistency checking is being based</html>");
        showRuleEditorCheckBoxMenuItem.addActionListener(e -> {
            Settings.setShowRules(showRuleEditorCheckBoxMenuItem.isSelected());
            app.showRuleEditorTab(showRuleEditorCheckBoxMenuItem.isSelected() && app.getRuleEditorTabPlacement() == -1);
        });
        showRuleEditorCheckBoxMenuItem.setSelected(Settings.showRules());



        JCheckBoxMenuItem showFactsTabCheckBoxMenuItem = new JCheckBoxMenuItem();
        showFactsTabCheckBoxMenuItem.setText("Show Facts Tab");
        showFactsTabCheckBoxMenuItem.setToolTipText("<html><b>Advanced:</b> Displays the fact's tab</html>");
        showFactsTabCheckBoxMenuItem.addActionListener(e -> {
            Settings.setShowFacts(showFactsTabCheckBoxMenuItem.isSelected());
            app.showFactsTab(showFactsTabCheckBoxMenuItem.isSelected());
        });
        showFactsTabCheckBoxMenuItem.setSelected(Settings.showFacts());



        JRadioButtonMenuItem simpleModeRadioButtonMenuItem = new JRadioButtonMenuItem("Simple Mode", false);
        simpleModeRadioButtonMenuItem.setToolTipText(
                "<html>Disables <b>dependency relationship</b> in DCD's and does not<br/> take in consideration <b>object visibility</b> in consistency checks.</html>");
        simpleModeRadioButtonMenuItem.addActionListener(e -> app.simpleMode());

        JRadioButtonMenuItem advancedModeRadioButtonMenuItem = new JRadioButtonMenuItem("Advanced Mode", true);
        advancedModeRadioButtonMenuItem.setToolTipText(
                "<html>Enables <b>dependency relationship</b> in DCD's and takes<br/> in consideration <b>object visibility</b> in consistency checks.</html>");
        advancedModeRadioButtonMenuItem.addActionListener(e -> app.advancedMode());

        if (SHOW_CONSISTENCY_CHECKING_MENU) {

            preferencesMenu.add(enableRuntimeConsistencyCheckBoxMenuItem);

            preferencesMenu.add(showRuleEditorCheckBoxMenuItem);
            preferencesMenu.add(showFactsTabCheckBoxMenuItem);
            preferencesMenu.add(simpleModeRadioButtonMenuItem);

            preferencesMenu.addSeparator();
            preferencesMenu.add(advancedModeRadioButtonMenuItem);

            ButtonGroup bgroup = new ButtonGroup();
            bgroup.add(simpleModeRadioButtonMenuItem);
            bgroup.add(advancedModeRadioButtonMenuItem);
            preferencesMenu.addSeparator();
        }


        ButtonGroup lookAndFeelGroup = new ButtonGroup();

        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            JRadioButtonMenuItem gtkLFRadioButtonMenuItem = new JRadioButtonMenuItem(info.getName(),
                    UIManager.getLookAndFeel().getClass().getName().equals(info.getClassName()));
            gtkLFRadioButtonMenuItem.setToolTipText("Use theme: " + info.getName());
            gtkLFRadioButtonMenuItem.addActionListener(e -> app.changeLookAndFeel(info.getClassName()));
            preferencesMenu.add(gtkLFRadioButtonMenuItem);
            lookAndFeelGroup.add(gtkLFRadioButtonMenuItem);
        }

        /*
         * Add option for Darcula theme
         */
        JRadioButtonMenuItem gtkLFRadioButtonMenuItem = new JRadioButtonMenuItem("Darcula (Dark)",
                UIManager.getLookAndFeel() instanceof DarculaLaf);
        gtkLFRadioButtonMenuItem.setToolTipText("Use theme: Darcula (Dark)");
        gtkLFRadioButtonMenuItem.addActionListener(e -> app.changeLookAndFeel("com.bulenkov.darcula.DarculaLaf"));
        preferencesMenu.add(gtkLFRadioButtonMenuItem);
        lookAndFeelGroup.add(gtkLFRadioButtonMenuItem);

        JRadioButtonMenuItem gtkLFRadioButtonMenuItem1 = new JRadioButtonMenuItem("Lipstik (Light)",
                UIManager.getLookAndFeel() instanceof LipstikLookAndFeel);
        gtkLFRadioButtonMenuItem1.setToolTipText("Use theme: Lipstik (Light)");
        gtkLFRadioButtonMenuItem1.addActionListener(e -> app.changeLookAndFeel("com.lipstikLF.LipstikLookAndFeel"));
        preferencesMenu.add(gtkLFRadioButtonMenuItem1);
        lookAndFeelGroup.add(gtkLFRadioButtonMenuItem1);

        preferencesMenu.addSeparator();

        JMenuItem chooseFillColorMenuItem = new JMenuItem();
        chooseFillColorMenuItem.setText("Choose fill color");
        chooseFillColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));

        chooseFillColorMenuItem.addActionListener(e -> app.changeFillColor());
        preferencesMenu.add(chooseFillColorMenuItem);

    }

    private void repaintSDandSSDDiagrams() {
        final String REPAINT = "REPAINT : ";

        for (JInternalFrame sdFrame : app.getInternalFramesOfType(DiagramType.SD)) {
            logger.finer(REPAINT);
            sdFrame.repaint();
        }
        
        for (JInternalFrame sdFrame : app.getInternalFramesOfType(DiagramType.SSD)) {
            logger.finer(REPAINT);
            sdFrame.repaint();
        }
        
        for (JInternalFrame sdFrame : app.getInternalFramesOfType(DiagramType.DCD)) {
            logger.finer(REPAINT);
            sdFrame.repaint();
        }

        for (JInternalFrame sdFrame : app.getInternalFramesOfType(DiagramType.CCD)) {
            logger.finer(REPAINT);
            sdFrame.repaint();
        }
    }

    private void createCreateMenu() {
        JMenu createMenu = new JMenu();
        createMenu.setText(" Create ");
        jMenuBar.add(createMenu);

        JMenuItem newUseCaseMenuItem = new JMenuItem();
        newUseCaseMenuItem.setText("New Use Case Diagram");
        newUseCaseMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.UCD));

        JMenuItem newSystemSequenceMenuItem = new JMenuItem();
        newSystemSequenceMenuItem.setText("New System Sequence Diagram");
        newSystemSequenceMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.SSD));

        JMenuItem newConceptualClassMenuItem = new JMenuItem();
        newConceptualClassMenuItem.setText("New Conceptual Class Diagram");
        newConceptualClassMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.CCD));

        JMenuItem newSequenceDiagramMenuItem = new JMenuItem();
        newSequenceDiagramMenuItem.setText("New Sequence Diagram");
        newSequenceDiagramMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.SD));

        JMenuItem newDesignClassMenuItem = new JMenuItem();
        newDesignClassMenuItem.setText("New Design Class Diagram");
        newDesignClassMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.DCD));

        JMenuItem newActivityDiagramMenuItem = new JMenuItem();
        newActivityDiagramMenuItem.setText("New Activity Diagram");
        newActivityDiagramMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramType.AD));
        createMenu.add(newActivityDiagramMenuItem);
        createMenu.add(newUseCaseMenuItem);
        createMenu.add(newConceptualClassMenuItem);
        createMenu.add(newSystemSequenceMenuItem);
        createMenu.add(newSequenceDiagramMenuItem);
        createMenu.add(newDesignClassMenuItem);
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText(" Help ");

        JMenuItem checkUpdatesMenuItem = new JMenuItem();
        checkUpdatesMenuItem.setText("Check for updates...");
        checkUpdatesMenuItem.addActionListener(e -> app.checkForUpdates());
        helpMenu.add(checkUpdatesMenuItem);

        helpMenu.addSeparator();

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(e -> app.aboutStudentUML());
        helpMenu.add(aboutMenuItem);        

        return helpMenu;
    }    
}
