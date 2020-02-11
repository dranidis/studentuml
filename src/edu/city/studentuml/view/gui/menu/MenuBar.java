package edu.city.studentuml.view.gui.menu;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.view.gui.SSDInternalFrame;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author dimitris
 */
public class MenuBar {
    ApplicationGUI app;
    JMenuBar menuBar;
    
    public MenuBar(ApplicationGUI app) {
        menuBar = new JMenuBar();

        this.app = app;
        createFileMenu();
        createEditMenu();
        createCreateMenu();
        createHelpMenu();
    }
    
    public JMenuBar getMenuBar() {
        return menuBar;
    }
    
    public void createFileMenu() {
        JMenu fileMenu = new JMenu();
        fileMenu.setText(" File ");
        menuBar.add(fileMenu);

        JMenuItem newProjectMenuItem = new JMenuItem();
        newProjectMenuItem.setText("New Project");
        KeyStroke keyStrokeToNew = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        newProjectMenuItem.setAccelerator(keyStrokeToNew);  

        newProjectMenuItem.addActionListener(e -> app.newProject());
        fileMenu.add(newProjectMenuItem);

        JMenuItem openProjectMenuItem = new JMenuItem();
        openProjectMenuItem.setText("Open Project");
        KeyStroke keyStrokeToOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        openProjectMenuItem.setAccelerator(keyStrokeToOpen);  
        openProjectMenuItem.addActionListener(e -> app.openProject());
        fileMenu.add(openProjectMenuItem);

        JMenuItem saveProjectMenuItem = new JMenuItem();
        saveProjectMenuItem.setText("Save");
        KeyStroke keyStrokeToSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        saveProjectMenuItem.setAccelerator(keyStrokeToSave);  
        saveProjectMenuItem.addActionListener(e -> app.saveProject());
        fileMenu.add(saveProjectMenuItem);

        JMenuItem saveProjectAsMenuItem = new JMenuItem();
        saveProjectAsMenuItem.setText("Save As...");
        saveProjectAsMenuItem.addActionListener(e -> app.saveProjectAs());
        fileMenu.add(saveProjectAsMenuItem);

        fileMenu.addSeparator();

        JMenuItem exportToImageMenuItem = new JMenuItem();
        exportToImageMenuItem.setText("Export To Image");
        exportToImageMenuItem.addActionListener(e -> app.exportImage());
        fileMenu.add(exportToImageMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(e -> {
                if (app.closeProject()) {
                    System.exit(0);
                }
            });
        fileMenu.add(exitMenuItem);

        if (app.isApplet) {
            newProjectMenuItem.setEnabled(false);
            saveProjectAsMenuItem.setEnabled(false);
            exportToImageMenuItem.setEnabled(false);
            exitMenuItem.setEnabled(false);
        }
    }    
    
    public void createEditMenu() {
        JMenu editMenu = new JMenu();
        editMenu.setText(" Edit ");
        menuBar.add(editMenu);

        JMenuItem resizeDrawingAreaMenuItem = new JMenuItem();
        resizeDrawingAreaMenuItem.setText("Resize Drawing Area");
        resizeDrawingAreaMenuItem.addActionListener(e -> app.resizeView());
        editMenu.add(resizeDrawingAreaMenuItem);

        JMenuItem reloadRulesMenuItem = new JMenuItem();
        reloadRulesMenuItem.setText("Reload Rules");
        reloadRulesMenuItem.addActionListener(e -> app.reloadRules());
        editMenu.add(reloadRulesMenuItem);

        editMenu.addSeparator();

        createPreferencesSubmenu(editMenu);
    }    
    
    private void createPreferencesSubmenu(JMenu editMenu) {
        JMenu preferencesMenu = new JMenu();
        preferencesMenu.setText("Preferences");
        editMenu.add(preferencesMenu);

        JCheckBoxMenuItem selectLastCheckBoxMenuItem = new JCheckBoxMenuItem();
        selectLastCheckBoxMenuItem.setText("Keep last selection in diagram toolbars");
        selectLastCheckBoxMenuItem.setToolTipText("<html>An element can be selected and then drawn on the canvas several times without"
                + " the need to select it again. <br>"
                + "If this is disabled the selection is always reset to the selection arrow.</html>");
        selectLastCheckBoxMenuItem.addActionListener(e -> {
                if (selectLastCheckBoxMenuItem.isSelected()) {
                    Preferences.userRoot().put("SELECT_LAST", "TRUE");
                } else {
                    Preferences.userRoot().put("SELECT_LAST", "FALSE");
                }
            }
        );
        
        boolean selectLastPref = Preferences.userRoot().get("SELECT_LAST", "").equals("TRUE") ? true : false;
        selectLastCheckBoxMenuItem.setSelected(selectLastPref);
        preferencesMenu.add(selectLastCheckBoxMenuItem);

        preferencesMenu.addSeparator();
        

        JCheckBoxMenuItem showTypesInSDCheckBoxMenuItem = new JCheckBoxMenuItem();
        showTypesInSDCheckBoxMenuItem.setText("Show types in messages in Sequence Diagrams");
        showTypesInSDCheckBoxMenuItem.setToolTipText("");
        showTypesInSDCheckBoxMenuItem.addActionListener(e -> {
                if (showTypesInSDCheckBoxMenuItem.isSelected()) {
                    Preferences.userRoot().put("SHOW_TYPES_SD", "TRUE");
                } else {
                    Preferences.userRoot().put("SHOW_TYPES_SD", "FALSE");
                }
                Vector sdFrames = app.getInternalFramesOfType(DiagramModel.SD);
                for (int i = 0; i < sdFrames.size(); i++) {
                    System.out.println("REPAINT : ");
                    ((SDInternalFrame) sdFrames.get(i)).repaint();
                }               
                sdFrames = app.getInternalFramesOfType(DiagramModel.SSD);
                for (int i = 0; i < sdFrames.size(); i++) {
                    System.out.println("REPAINT : ");
                    ((SSDInternalFrame) sdFrames.get(i)).repaint();
                }               
            }
        );
        
        boolean showTypesSDPref = Preferences.userRoot().get("SHOW_TYPES_SD", "").equals("TRUE") ? true : false;
        showTypesInSDCheckBoxMenuItem.setSelected(showTypesSDPref);
        preferencesMenu.add(showTypesInSDCheckBoxMenuItem);

        preferencesMenu.addSeparator();
                
        JCheckBoxMenuItem showReturnsSDCheckBoxMenuItem = new JCheckBoxMenuItem();
        showReturnsSDCheckBoxMenuItem.setText("Show return arrows in Sequence Diagrams");
        showReturnsSDCheckBoxMenuItem.setToolTipText("");
        showReturnsSDCheckBoxMenuItem.addActionListener(e -> {
                if (showReturnsSDCheckBoxMenuItem.isSelected()) {
                    Preferences.userRoot().put("SHOW_RETURN_SD", "TRUE");
                } else {
                    Preferences.userRoot().put("SHOW_RETURN_SD", "FALSE");
                }
                Vector sdFrames = app.getInternalFramesOfType(DiagramModel.SD);
                for (int i = 0; i < sdFrames.size(); i++) {
                    System.out.println("REPAINT : ");
                    ((SDInternalFrame) sdFrames.get(i)).repaint();
                }               
                sdFrames = app.getInternalFramesOfType(DiagramModel.SSD);
                for (int i = 0; i < sdFrames.size(); i++) {
                    System.out.println("REPAINT : ");
                    ((SSDInternalFrame) sdFrames.get(i)).repaint();
                }               
            }
        );
        
        boolean showReturnPref = Preferences.userRoot().get("SHOW_RETURN_SD", "").equals("TRUE") ? true : false;
        showReturnsSDCheckBoxMenuItem.setSelected(showReturnPref);
        preferencesMenu.add(showReturnsSDCheckBoxMenuItem);
        
        
        preferencesMenu.addSeparator();
                
        JCheckBoxMenuItem enableRuntimeConsistencyCheckBoxMenuItem = new JCheckBoxMenuItem();
        enableRuntimeConsistencyCheckBoxMenuItem.setText("Enable Runtime Consistency Checking");
        enableRuntimeConsistencyCheckBoxMenuItem.setToolTipText("<html>Displays the message tab containing feedback and advisory information<br/> gained from the performed consistency checks. Also enables the user<br/> to perform automated repair operations</html>");
        enableRuntimeConsistencyCheckBoxMenuItem.addActionListener(
                e -> app.setRunTimeConsistencyCheck(enableRuntimeConsistencyCheckBoxMenuItem.isSelected())
              );
        enableRuntimeConsistencyCheckBoxMenuItem.setSelected(false);
        preferencesMenu.add(enableRuntimeConsistencyCheckBoxMenuItem);

        JCheckBoxMenuItem showRuleEditorCheckBoxMenuItem = new JCheckBoxMenuItem();
        showRuleEditorCheckBoxMenuItem.setText("Show Rule Editor Tab");
        showRuleEditorCheckBoxMenuItem.setToolTipText("<html><b>Advanced:</b> Displays the rule editor tab that enables the user<br/> to edit the rules on which the consistency checking is being based</html>");
        showRuleEditorCheckBoxMenuItem.addActionListener(e ->
                app.showRuleEditorTab(showRuleEditorCheckBoxMenuItem.isSelected() && app.getRuleEditorTabPlacement() == -1)
                );
        preferencesMenu.add(showRuleEditorCheckBoxMenuItem);

        JCheckBoxMenuItem showFactsTabCheckBoxMenuItem = new JCheckBoxMenuItem();
        showFactsTabCheckBoxMenuItem.setText("Show Facts Tab");
        showFactsTabCheckBoxMenuItem.setToolTipText("<html><b>Advanced:</b> Displays the fact's tab</html>");
        showFactsTabCheckBoxMenuItem.addActionListener(
                e -> app.showFactsTab(showFactsTabCheckBoxMenuItem.isSelected()));
        preferencesMenu.add(showFactsTabCheckBoxMenuItem);

        preferencesMenu.addSeparator();

        JRadioButtonMenuItem simpleModeRadioButtonMenuItem = new JRadioButtonMenuItem("Simple Mode", false);
        simpleModeRadioButtonMenuItem.setToolTipText("<html>Disables <b>dependency relationship</b> in DCD's and does not<br/> take in consideration <b>object visibility</b> in consistency checks.</html>");
        simpleModeRadioButtonMenuItem.addActionListener(e -> app.simpleMode());

        preferencesMenu.add(simpleModeRadioButtonMenuItem);

        JRadioButtonMenuItem advancedModeRadioButtonMenuItem = new JRadioButtonMenuItem("Advanced Mode", true);
        advancedModeRadioButtonMenuItem.setToolTipText("<html>Enables <b>dependency relationship</b> in DCD's and takes<br/> in consideration <b>object visibility</b> in consistency checks.</html>");
        advancedModeRadioButtonMenuItem.addActionListener(e -> app.advancedMode());
        preferencesMenu.add(advancedModeRadioButtonMenuItem);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(simpleModeRadioButtonMenuItem);
        bgroup.add(advancedModeRadioButtonMenuItem);
    }    

    private void createCreateMenu() {
        JMenu createMenu = new JMenu();
        createMenu.setText(" Create ");
        menuBar.add(createMenu);

        JMenuItem newUseCaseMenuItem = new JMenuItem();
        newUseCaseMenuItem.setText("New Use Case Diagram");
        newUseCaseMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.UCD));

        JMenuItem newSystemSequenceMenuItem = new JMenuItem();
        newSystemSequenceMenuItem.setText("New System Sequence Diagram");
        newSystemSequenceMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.SSD));

        JMenuItem newConceptualClassMenuItem = new JMenuItem();
        newConceptualClassMenuItem.setText("New Conceptual Class Diagram");
        newConceptualClassMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.CCD));

        JMenuItem newSequenceDiagramMenuItem = new JMenuItem();
        newSequenceDiagramMenuItem.setText("New Sequence Diagram");
        newSequenceDiagramMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.SD));

        JMenuItem newDesignClassMenuItem = new JMenuItem();
        newDesignClassMenuItem.setText("New Design Class Diagram");
        newDesignClassMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.DCD));

        JMenuItem newActivityDiagramMenuItem = new JMenuItem();
        newActivityDiagramMenuItem.setText("New Activity Diagram");
        newActivityDiagramMenuItem.addActionListener(e -> app.createNewInternalFrame(DiagramModel.AD));
        createMenu.add(newActivityDiagramMenuItem);
        createMenu.add(newUseCaseMenuItem);
        createMenu.add(newConceptualClassMenuItem);
        createMenu.add(newSystemSequenceMenuItem);
        createMenu.add(newSequenceDiagramMenuItem);
        createMenu.add(newDesignClassMenuItem);
    }

    private void createHelpMenu() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText(" Help ");
        menuBar.add(helpMenu);

        JMenuItem getHelpMenuItem = new JMenuItem();
        getHelpMenuItem.setText("Get Help");
        getHelpMenuItem.addActionListener(e -> app.help());
        helpMenu.add(getHelpMenuItem);
    }    
}
