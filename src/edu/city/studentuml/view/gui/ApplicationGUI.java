package edu.city.studentuml.view.gui;

import edu.city.studentuml.applet.StudentUMLApplet;
import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.DiagramView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author draganbisercic
 */
public abstract class ApplicationGUI extends JPanel implements KeyListener, Observer {

    protected static boolean isApplet = false;
    protected StudentUMLFrame frame = null;
    protected StudentUMLApplet applet = null;
    protected boolean repairMode = false;
    protected UMLProject umlProject = null;
    protected CentralRepository centralRepository;
    protected String simpleRulesFile;
    protected String advancedRulesFile;
    protected String currentRuleFile;
    protected static final String rulesLocation = "/edu/city/studentuml/util/validation/rules/";
    protected JMenuBar menuBar;
    protected JMenu fileMenu;
    protected JMenuItem newProjectMenuItem;
    protected JMenuItem openProjectMenuItem;
    protected JMenuItem saveProjectMenuItem;
    protected JMenuItem saveProjectAsMenuItem;
    protected JMenuItem exportToImageMenuItem;
    protected JMenuItem exitMenuItem;
    protected JMenu editMenu;
    protected JMenuItem resizeDrawingAreaMenuItem;
    protected JMenuItem renameProject;
    protected JMenuItem reloadRulesMenuItem;
    protected JMenu preferencesMenu;
    protected JCheckBoxMenuItem enableRuntimeConsistencyCheckBoxMenuItem;
    protected JCheckBoxMenuItem showRuleEditorCheckBoxMenuItem;
    protected JCheckBoxMenuItem showFactsTabCheckBoxMenuItem;
    protected ButtonGroup bgroup;
    protected JRadioButtonMenuItem simpleModeRadioButtonMenuItem;
    protected JRadioButtonMenuItem advancedModeRadioButtonMenuItem;
    protected JMenu createMenu;
    protected JMenuItem newUseCaseMenuItem;
    protected JMenuItem newSystemSequenceMenuItem;
    protected JMenuItem newConceptualClassMenuItem;
    protected JMenuItem newSequenceDiagramMenuItem;
    protected JMenuItem newDesignClassMenuItem;
    protected JMenuItem newActivityDiagramMenuItem;
    protected JMenu helpMenu;
    protected JMenuItem getHelpMenuItem;
    private ProjectToolBar toolbar;
    protected JDesktopPane desktopPane;   //holds internal frames
    protected RepositoryTreeView repositoryTreeView;
    protected JScrollPane treePane;
    protected JTree factsTree;
    protected JTree messageTree;
    protected CheckTreeManager checkTreeManager;
    protected JTabbedPane tabbedPane;
    protected JSplitPane splitPane_1;
    protected JScrollPane scrollPane_p;
    protected JScrollPane scrollPane_f;
    protected JPanel panel;
    protected int ruleEditorTabPlacement = -1;
    protected int factsTreeTabPlacement = -1;
    protected JPopupMenu popupMenu;
    protected JMenuItem popupRepair;
    protected JMenuItem popupHelp;
    protected JPanel repairPanel;
    protected JButton repairButton;
    protected int openFrameCounter = 0;
    public static String DESKTOP_USER = "Desktop Application User";
    private static ApplicationGUI instance; // need in ObjectFactory [backward compatiblity]

    public ApplicationGUI(StudentUMLFrame frame) {
        isApplet = false;
        this.frame = frame;
        instance = this;
        create();
        addWindowClosing(frame);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public ApplicationGUI(StudentUMLApplet applet) {
        isApplet = true;
        this.applet = applet;
        instance = this;
        create();
        applet.getContentPane().add(this);
        applet.setVisible(true);
    }

    // NEED FOR BACKWARD COMPATIBILITY
    public static ApplicationGUI getInstance() {
        return instance;
    }

    private void create() {
        initializeRules();
        addObserver();
        createUMLProject();
        setUserId();
        createLookAndFeel();
        addKeyListener(this);
        createInterface();
    }

    private void initializeRules() {
        simpleRulesFile = getResource(Constants.RULES_DIR + "simplerules.txt");
        advancedRulesFile = getResource(Constants.RULES_DIR + "advancedrules.txt");
        currentRuleFile = advancedRulesFile;

        // set the rule file and construct the consistency checker
        SystemWideObjectNamePool.getInstance().init(currentRuleFile);
    }

    private void addObserver() {
        // observe when changes occur
        SystemWideObjectNamePool.getInstance().addObserver(this);
    }

    /*
     * initializes a new project
     */
    private void createUMLProject() {
        umlProject = UMLProject.getInstance();
        umlProject.becomeObserver();
        umlProject.addObserver(this);
        SystemWideObjectNamePool.umlProject = umlProject;
    }

    /*
     * sets the user id for coloring purposes (when drawing graphical elements)
     */
    private void setUserId() {
        if (isApplet) {
            SystemWideObjectNamePool.uid = applet.getUsername();
        } else {
            SystemWideObjectNamePool.uid = DESKTOP_USER;
        }
    }

    private void createLookAndFeel() {
        System.setProperty("lipstikLF.theme", "LightGrayTheme");

        try {
            UIManager.setLookAndFeel(new com.lipstikLF.LipstikLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createInterface() {
        setLayout(new BorderLayout());
        createMenuBar();
        createToolBar();
        createDesktopPane();
        //createUMLProject();
        createCentralRepositoryTreeView();
        update(umlProject, this);
        createFactsAndMessageTree();
        createDiagramAndConsistencyArea();
        createRepairPopupMenu();
        createRepairPanel();
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        if (!isApplet) {
            frame.setJMenuBar(menuBar);
        } else {
            applet.setJMenuBar(menuBar);
        }

        createFileMenu();
        createEditMenu();
        createCreateMenu();
        createHelpMenu();
    }

    private void createFileMenu() {
        fileMenu = new JMenu();
        fileMenu.setText(" File ");
        menuBar.add(fileMenu);

        newProjectMenuItem = new JMenuItem();
        newProjectMenuItem.setText("New Project");
        newProjectMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                newProject();
            }
        });
        fileMenu.add(newProjectMenuItem);

        openProjectMenuItem = new JMenuItem();
        openProjectMenuItem.setText("Open Project");
        openProjectMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openProject();
            }
        });
        fileMenu.add(openProjectMenuItem);

        saveProjectMenuItem = new JMenuItem();
        saveProjectMenuItem.setText("Save");
        saveProjectMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveProject();
            }
        });
        fileMenu.add(saveProjectMenuItem);

        saveProjectAsMenuItem = new JMenuItem();
        saveProjectAsMenuItem.setText("Save As...");
        saveProjectAsMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveProjectAs();
            }
        });
        fileMenu.add(saveProjectAsMenuItem);

        fileMenu.addSeparator();

        exportToImageMenuItem = new JMenuItem();
        exportToImageMenuItem.setText("Export To Image");
        exportToImageMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        });
        fileMenu.add(exportToImageMenuItem);

        fileMenu.addSeparator();

        exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (closeProject()) {
                    System.exit(0);
                }
            }
        });
        fileMenu.add(exitMenuItem);

        if (isApplet) {
            newProjectMenuItem.setEnabled(false);
            saveProjectAsMenuItem.setEnabled(false);
            exportToImageMenuItem.setEnabled(false);
            exitMenuItem.setEnabled(false);
        }
    }

    private void createEditMenu() {
        editMenu = new JMenu();
        editMenu.setText(" Edit ");
        menuBar.add(editMenu);

        renameProject = new JMenuItem();
        renameProject.setText("Rename Project");
        renameProject.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                renameProject();
            }
        });
        editMenu.add(renameProject);

        resizeDrawingAreaMenuItem = new JMenuItem();
        resizeDrawingAreaMenuItem.setText("Resize Drawing Area");
        resizeDrawingAreaMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                resizeView();
            }
        });
        editMenu.add(resizeDrawingAreaMenuItem);

        reloadRulesMenuItem = new JMenuItem();
        reloadRulesMenuItem.setText("Reload Rules");
        reloadRulesMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadRules();
            }
        });
        editMenu.add(reloadRulesMenuItem);

        editMenu.addSeparator();

        createPreferencesSubmenu();
    }

    private void createPreferencesSubmenu() {
        preferencesMenu = new JMenu();
        preferencesMenu.setText("Preferences ->");
        editMenu.add(preferencesMenu);

        enableRuntimeConsistencyCheckBoxMenuItem = new JCheckBoxMenuItem();
        enableRuntimeConsistencyCheckBoxMenuItem.setText("Enable Runtime Consistency Checking");
        enableRuntimeConsistencyCheckBoxMenuItem.setToolTipText("<html>Displays the message tab containing feedback and advisory information<br/> gained from the performed consistency checks. Also enables the user<br/> to perform automated repair operations</html>");
        enableRuntimeConsistencyCheckBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (enableRuntimeConsistencyCheckBoxMenuItem.isSelected()) {
                    setRuntimeChecking(true);
                    tabbedPane.setVisible(true);
                    splitPane_1.setDividerSize(5);
                    splitPane_1.setDividerLocation(getHeight() * 360 / 600);
                    reloadRules();
                } else {
                    setRuntimeChecking(false);
                    tabbedPane.setVisible(false);
                    splitPane_1.setDividerSize(0);
                }
            }
        });
        enableRuntimeConsistencyCheckBoxMenuItem.setSelected(true);
        preferencesMenu.add(enableRuntimeConsistencyCheckBoxMenuItem);

        showRuleEditorCheckBoxMenuItem = new JCheckBoxMenuItem();
        showRuleEditorCheckBoxMenuItem.setText("Show Rule Editor Tab");
        showRuleEditorCheckBoxMenuItem.setToolTipText("<html><b>Advanced:</b> Displays the rule editor tab that enables the user<br/> to edit the rules on which the consistency checking is being based</html>");
        showRuleEditorCheckBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (showRuleEditorCheckBoxMenuItem.isSelected() && ruleEditorTabPlacement == -1) {
                    ruleEditorTabPlacement = tabbedPane.getTabCount();
                    tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, tabbedPane.getTabCount());

                } else {
                    tabbedPane.remove(ruleEditorTabPlacement);
                    ruleEditorTabPlacement = -1;
                }

            }
        });
        preferencesMenu.add(showRuleEditorCheckBoxMenuItem);

        showFactsTabCheckBoxMenuItem = new JCheckBoxMenuItem();
        showFactsTabCheckBoxMenuItem.setText("Show Facts Tab");
        showFactsTabCheckBoxMenuItem.setToolTipText("<html><b>Advanced:</b> Displays the fact's tab</html>");
        showFactsTabCheckBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (showFactsTabCheckBoxMenuItem.isSelected() && factsTreeTabPlacement == -1) {
                    factsTreeTabPlacement = tabbedPane.getTabCount();
                    tabbedPane.insertTab("Facts", null, scrollPane_f, null, tabbedPane.getTabCount());

                } else {
                    tabbedPane.remove(factsTreeTabPlacement);
                    factsTreeTabPlacement = -1;
                }

            }
        });
        preferencesMenu.add(showFactsTabCheckBoxMenuItem);

        preferencesMenu.addSeparator();

        simpleModeRadioButtonMenuItem = new JRadioButtonMenuItem("Simple Mode", false);
        simpleModeRadioButtonMenuItem.setToolTipText("<html>Disables <b>dependency relationship</b> in DCD's and does not<br/> take in consideration <b>object visibility</b> in consistency checks.</html>");
        simpleModeRadioButtonMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentRuleFile = simpleRulesFile;
                setRuleFile(simpleRulesFile);
                reloadRules();
                if (ruleEditorTabPlacement != -1) {
                    int selected = tabbedPane.getSelectedIndex();
                    tabbedPane.remove(ruleEditorTabPlacement);
                    tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
                    tabbedPane.setSelectedIndex(selected);
                }
                Vector dcdFrames = getInternalFramesOfType(DiagramModel.DCD);
                for (int i = 0; i < dcdFrames.size(); i++) {
                    ((DCDInternalFrame) dcdFrames.get(i)).setAdvancedMode(false);
                }
            }
        });
        preferencesMenu.add(simpleModeRadioButtonMenuItem);

        advancedModeRadioButtonMenuItem = new JRadioButtonMenuItem("Advanced Mode", true);
        advancedModeRadioButtonMenuItem.setToolTipText("<html>Enables <b>dependency relationship</b> in DCD's and takes<br/> in consideration <b>object visibility</b> in consistency checks.</html>");
        advancedModeRadioButtonMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentRuleFile = advancedRulesFile;
                setRuleFile(advancedRulesFile);
                reloadRules();
                if (ruleEditorTabPlacement != -1) {
                    int selected = tabbedPane.getSelectedIndex();
                    tabbedPane.remove(ruleEditorTabPlacement);
                    tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
                    tabbedPane.setSelectedIndex(selected);
                }
                Vector dcdFrames = getInternalFramesOfType(DiagramModel.DCD);
                for (int i = 0; i < dcdFrames.size(); i++) {
                    ((DCDInternalFrame) dcdFrames.get(i)).setAdvancedMode(true);
                }
            }
        });
        preferencesMenu.add(advancedModeRadioButtonMenuItem);

        bgroup = new ButtonGroup();
        bgroup.add(simpleModeRadioButtonMenuItem);
        bgroup.add(advancedModeRadioButtonMenuItem);
    }

    private void createCreateMenu() {
        createMenu = new JMenu();
        createMenu.setText(" Create ");
        menuBar.add(createMenu);

        newUseCaseMenuItem = new JMenuItem();
        newUseCaseMenuItem.setText("New Use Case Diagram");
        newUseCaseMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.UCD);
            }
        });

        newSystemSequenceMenuItem = new JMenuItem();
        newSystemSequenceMenuItem.setText("New System Sequence Diagram");
        newSystemSequenceMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.SSD);
            }
        });

        newConceptualClassMenuItem = new JMenuItem();
        newConceptualClassMenuItem.setText("New Conceptual Class Diagram");
        newConceptualClassMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.CCD);
            }
        });

        newSequenceDiagramMenuItem = new JMenuItem();
        newSequenceDiagramMenuItem.setText("New Sequence Diagram");
        newSequenceDiagramMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.SD);
            }
        });

        newDesignClassMenuItem = new JMenuItem();
        newDesignClassMenuItem.setText("New Design Class Diagram");
        newDesignClassMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.DCD);
            }
        });

        newActivityDiagramMenuItem = new JMenuItem();
        newActivityDiagramMenuItem.setText("New Activity Diagram");
        newActivityDiagramMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInternalFrame(DiagramModel.AD);
            }
        });
        createMenu.add(newActivityDiagramMenuItem);
        createMenu.add(newUseCaseMenuItem);
        createMenu.add(newConceptualClassMenuItem);
        createMenu.add(newSystemSequenceMenuItem);
        createMenu.add(newSequenceDiagramMenuItem);
        createMenu.add(newDesignClassMenuItem);
    }

    private void createHelpMenu() {
        helpMenu = new JMenu();
        helpMenu.setText(" Help ");
        menuBar.add(helpMenu);

        getHelpMenuItem = new JMenuItem();
        getHelpMenuItem.setText("Get Help");
        getHelpMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                help();
            }
        });
        helpMenu.add(getHelpMenuItem);
    }

    private void createToolBar() {
        toolbar = new ProjectToolBar();
        add(toolbar, BorderLayout.NORTH);
    }

    private void createDesktopPane() {
        desktopPane = new JDesktopPane();
        desktopPane.setBorder(new LineBorder(UIManager.getColor("Tree.hash"), 1, false));
        desktopPane.setBackground(UIManager.getColor("Tree.background"));
        desktopPane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
        desktopPane.setBackground(UIManager.getColor("blue"));
    }

    private void createCentralRepositoryTreeView() {
        centralRepository = umlProject.getCentralRepository();
        repositoryTreeView = new RepositoryTreeView();
        repositoryTreeView.setUMLProject(umlProject);
        treePane = new JScrollPane(repositoryTreeView);
        add(treePane, BorderLayout.WEST);
    }

    private void createDiagramAndConsistencyArea() {
        tabbedPane = new JTabbedPane();
        splitPane_1 = new JSplitPane();
        splitPane_1.setDividerSize(5);
        splitPane_1.setDividerLocation(450);
        splitPane_1.setResizeWeight(0.7);
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane_1.setLeftComponent(desktopPane);
        splitPane_1.setRightComponent(tabbedPane);
        add(splitPane_1, BorderLayout.CENTER);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane_p);

        //scrollPane_f = new JScrollPane();
        //scrollPane_f.setViewportView(factsTree);
        tabbedPane.addTab("Problems", null, panel, null);
        //tabbedPane.addTab("Rule Editor", null, new RuleEditor(currentRuleFile), null);
        //tabbedPane.addTab("Facts", null, scrollPane_f, null);
    }

    private void createFactsAndMessageTree() {
        factsTree = new JTree();
        factsTree.setModel(null);
        messageTree = new JTree();
        messageTree.setModel(null);

        checkTreeManager = new CheckTreeManager(messageTree, false, new TreePathSelectable() {

            public boolean isSelectable(TreePath path) {
                return path.getPathCount() == 3;
            }
        });

        scrollPane_f = new JScrollPane();
        scrollPane_f.setViewportView(factsTree);

        scrollPane_p = new JScrollPane();
        scrollPane_p.setViewportView(messageTree);
    }

    private void createRepairPopupMenu() {
        popupMenu = new JPopupMenu();

        popupRepair = new JMenuItem();
        popupRepair.setText("Repair");
        popupRepair.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();
                //SystemWideObjectNamePool.getInstance().loading();
                SystemWideObjectNamePool.getInstance().setSelectedRule(rulename);
                //SystemWideObjectNamePool.getInstance().done();
                ////SystemWideObjectNamePool.getInstance().reloadRules();
                SystemWideObjectNamePool.getInstance().reload();
                SystemWideObjectNamePool.getInstance().setSelectedRule(null);
            }
        });

        popupHelp = new JMenuItem();
        popupHelp.setText("Get Help");
        popupHelp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();

                Rule rule = SystemWideObjectNamePool.getInstance().getRule(rulename);
                String helpString = (rule == null) ? null : rule.gethelpurl();

                try {
                    URL url = new URL(helpString);
                    if (isApplet) {
                        applet.getAppletContext().showDocument(url, "_blank");
                    } else {
                        // do something about this
                    }
                } catch (MalformedURLException mue) {
                    JOptionPane.showMessageDialog(null, "No help URL defined or wrong URL",
                            "Wrong URL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addPopup(messageTree, popupMenu);
    }

    private void addPopup(final Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if ((e.isPopupTrigger())) {
                    showMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if ((e.isPopupTrigger())) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                TreePath path = ((JTree) component).getSelectionPath();
                if (path != null) {
                    if (path.getPathCount() == 3) {
                        if (isRepairMode()) {
                            popup.removeAll();
                            popup.add(popupHelp);
                            popup.add(popupRepair);
                        } else {
                            popup.removeAll();
                            popup.add(popupHelp);
                        }

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    public void createRepairPanel() {
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        repairPanel = new JPanel();
        repairPanel.setLayout(flowLayout);
        panel.add(repairPanel, BorderLayout.PAGE_END);

        repairButton = new JButton();
        //repairButton.setMargin(new Insets(2, 2, 2, 2));
        repairButton.setBorder(new EmptyBorder(2, 5, 2, 5));
        repairButton.setName("Repair selected");
        repairButton.setText(" Repair selected");
        repairButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                repairButton.setBorder(new CompoundBorder(
                        new LineBorder(UIManager.getColor("blue"), 1),
                        new EmptyBorder(1, 4, 1, 4)));
            }

            public void mouseExited(MouseEvent e) {
                repairButton.setBorder(new EmptyBorder(2, 5, 2, 5));
            }
        });
        repairButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();

                //String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();
                if (checkedPaths != null) {
                    for (int i = 0; i < checkedPaths.length; i++) {
                        SystemWideObjectNamePool.getInstance().setSelectedRule(checkedPaths[i].getLastPathComponent().toString());
                        SystemWideObjectNamePool.getInstance().reload();
                    }
                }

                SystemWideObjectNamePool.getInstance().setSelectedRule(null);
                checkTreeManager.getSelectionModel().clearSelection();
                repairButton.setEnabled(false);
            }
        });

        repairPanel.add(repairButton);
        repairButton.setEnabled(false);
        repairPanel.setVisible(false);
        showRepairButton(messageTree, repairButton);

        setRepairMode(true);	// Sets on/off the REPAIR feature
    }

    protected void showRepairButton(final Component component, final JButton button) {
        component.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (checkTreeManager.getSelectionModel().getSelectionPaths() != null) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });
    }

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED) {
            System.out.println(c);
            repaint();
            e.consume();
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void update(Observable observable, Object object) {
        setSaveMenuActionEnabled(!umlProject.isSaved());
        toolbar.setSaveActionEnabled(!umlProject.isSaved());

        if (object != null && object instanceof SystemWideObjectNamePool) {
            CollectionTreeModel messages = SystemWideObjectNamePool.getInstance().getMessages();
            CollectionTreeModel facts = SystemWideObjectNamePool.getInstance().getFacts();

            String messTreeState = null;

            if ((messageTree.getModel() != null) && (messageTree.getModel() instanceof CollectionTreeModel)) {
                messTreeState = getExpansionState(messageTree, 0);
                checkTreeManager.getSelectionModel().clearSelection();
                repairButton.setEnabled(false);
            }

            if (messageTree != null) {
                messageTree.setModel(messages);
                if (messTreeState != null) {
                    restoreExpanstionState(messageTree, 0, messTreeState);
                }

                if (repairPanel != null && messages != null && messages.size() > 0 && isRepairMode()) {
                    repairPanel.setVisible(true);
                } else if (repairPanel != null && messages != null && messages.size() == 0) {
                    repairPanel.setVisible(false);
                }
            }

            if (factsTree != null) {
                factsTree.setModel(facts);
            }
        }
    }

    public abstract void newProject();

    public abstract void openProject();

    public abstract void saveProject();

    public abstract void saveProjectAs();

    public abstract void exportImage();

    /*
     * creates a new empty diagram within the appropriate internal frame,
     * depending on the type integer
     */
    public void createNewInternalFrame(int type) {
        String dialogText;
        DiagramModel model;
        switch (type) {
            case DiagramModel.UCD:
                dialogText = "Use Case Diagram Name: ";
                break;
            case DiagramModel.SSD:
                dialogText = "System Sequence Diagram Name:";
                break;
            case DiagramModel.SD:
                dialogText = "Sequence Diagram Name: ";
                break;
            case DiagramModel.CCD:
                dialogText = "Conceptual Class Diagram Name: ";
                break;
            case DiagramModel.DCD:
                dialogText = "Design Class Diagram Name: ";
                break;
            case DiagramModel.AD:
                dialogText = "Activity Diagram Name: ";
                break;
            default:
                dialogText = "";
        }
        String modelName = JOptionPane.showInputDialog(dialogText);
        if ((modelName != null) && (modelName.length() > 0)) {
            if (type == DiagramModel.SSD) {
                model = new SSDModel("SSD: " + modelName, umlProject);

            } else if (type == DiagramModel.SD) {
                model = new SDModel("SD: " + modelName, umlProject);

            } else if (type == DiagramModel.CCD) {
                model = new CCDModel("CCD: " + modelName, umlProject);

            } else if (type == DiagramModel.DCD) {
                model = new DCDModel("DCD: " + modelName, umlProject);
            } else if (type == DiagramModel.AD) {
                model = new ADModel("AD: " + modelName, umlProject);
            } else if (type == DiagramModel.UCD) {
                model = new UCDModel("UCD: " + modelName, umlProject);
            } else {
                return;
            }
            model.addObserver(this);
            addInternalFrame(model);
            setSaved(false);
        }
    }

    public abstract void renameProject();

    public void resizeView() {
        JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

        if (selectedFrame != null) {
            DiagramView view = ((DiagramInternalFrame) selectedFrame).getView();
            SizeInputPanel sizeInput = new SizeInputPanel();
            sizeInput.setWidth((int) view.getSize().getWidth());
            sizeInput.setHeight((int) view.getSize().getHeight());

            if (JOptionPane.showOptionDialog(this, sizeInput, "Size Input", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null, null) != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                Dimension dimension = sizeInput.getSize();

                view.setSize(dimension);
                selectedFrame.validate();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }
        }
    }

    public abstract void help();

    protected void setRepairMode(boolean repairMode) {
        this.repairMode = repairMode;
    }

    protected boolean isRepairMode() {
        return repairMode;
    }

    protected void reloadRules() {
        SystemWideObjectNamePool.getInstance().reloadRules();
    }

    protected boolean isRuntimeChecking() {
        return SystemWideObjectNamePool.getInstance().isRuntimeChecking();
    }

    protected void setRuntimeChecking(boolean b) {
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(b);
    }

    protected void setRuleFile(String ruleFile) {
        SystemWideObjectNamePool.getInstance().setRuleFile(simpleRulesFile);
    }

    private void addWindowClosing(StudentUMLFrame f) {
        // override the default action when user presses the close button
        f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                exitApplication();
            }
        });
    }

    /*
     * opens appropriate frames based on a vector of diagram model objects
     */
    private void openFrames(Vector diagramModels) {
        DiagramModel model;
        Iterator iterator = diagramModels.iterator();

        while (iterator.hasNext()) {
            model = (DiagramModel) iterator.next();
            model.addObserver(this);
            addInternalFrame(model);
        }
    }

    /*
     * closes all existing internal frames (diagrams) in the application
     */
    private void closeFrames() {
        JInternalFrame[] frames = desktopPane.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            removeInternalFrame((DiagramInternalFrame) frames[i]);
        }
    }

    private void addInternalFrame(DiagramModel model) {
        addInternalFrame(model, null);
    }

    /*
     * utilized by other methods trying to embed a diagram model
     * in the appropriate internal frame
     */
    public void addInternalFrame(DiagramModel model, Rectangle R) {
        DiagramInternalFrame f = null;

        if (model instanceof UCDModel) {
            f = new UCDInternalFrame((UCDModel) model);
        } else if (model instanceof SSDModel) {
            f = new SSDInternalFrame((SSDModel) model);
        } else if (model instanceof CCDModel) {
            f = new CCDInternalFrame((CCDModel) model);
        } else if (model instanceof SDModel) {
            f = new SDInternalFrame((SDModel) model);
        } else if (model instanceof DCDModel) {
            f = new DCDInternalFrame((DCDModel) model, /*advancedModeRadioButtonMenuItem.isSelected()*/ true);
        } else if (model instanceof ADModel) {
            f = new ADInternalFrame((ADModel) model);
        } //else if (model instanceof StateModel) {
        //f = new StateInternalFrame((StateModel) model);
        //}

        if (R != null) {
            f.setBounds(R);
            f.getView().setSize((int) R.getWidth(), (int) R.getHeight());
        }

        model.setFrame(f);

        f.addInternalFrameListener(new DiagramInternalFrameListener());
        desktopPane.add(f);
        //f.setLocation(xOffset * openFrameCounter, yOffset * openFrameCounter);
        openFrameCounter++;
        f.setOpaque(true);
        f.setVisible(true);

        try {
            f.setSelected(true);
        } catch (PropertyVetoException vetoException) {
            vetoException.printStackTrace();
        }

        setSaved(false);
    }

    /*
     * closes and removes the given internal frame, together with the diagram model
     */
    private void removeInternalFrame(DiagramInternalFrame frame) {
        frame.getModel().clear();
        umlProject.removeDiagram(frame.getModel());

        frame.dispose();
        desktopPane.remove(frame);
        openFrameCounter--;
        setSaved(false);
    }

    /*
     * returns a list of internal frames having a particular diagram type
     */
    public Vector getInternalFramesOfType(int type) {
        Vector ucdFrames = new Vector();
        Vector ssdFrames = new Vector();
        Vector sdFrames = new Vector();
        Vector ccdFrames = new Vector();
        Vector dcdFrames = new Vector();
        Vector adFrames = new Vector();
        //Vector stateFrames = new Vector();
        JInternalFrame[] frames = desktopPane.getAllFrames();
        JInternalFrame f;

        for (int i = 0; i < frames.length; i++) {
            f = frames[i];

            if (f instanceof UCDInternalFrame) {
                ucdFrames.add(f);
            } else if (f instanceof SSDInternalFrame) {
                ssdFrames.add(f);
            } else if (f instanceof CCDInternalFrame) {
                ccdFrames.add(f);
            } else if (f instanceof SDInternalFrame) {
                sdFrames.add(f);
            } else if (f instanceof DCDInternalFrame) {
                dcdFrames.add(f);
            } else if (f instanceof ADInternalFrame) {
                adFrames.add(f);
            } //else if (f instanceof StateInternalFrame) {
            //stateFrames.add(f);
            //}
        }

        if (type == DiagramModel.UCD) {
            return ucdFrames;
        } else if (type == DiagramModel.SSD) {
            return ssdFrames;
        } else if (type == DiagramModel.CCD) {
            return ccdFrames;
        } else if (type == DiagramModel.SD) {
            return sdFrames;
        } else if (type == DiagramModel.DCD) {
            return dcdFrames;
        } else if (type == DiagramModel.AD) {
            return adFrames;
        } //else if () {
        //return stateFrames;
        //}

        return new Vector();
    }

    /*
     * inner class listens for events from internal frames;
     * overrides default behavior when the user closes the frame
     */
    private class DiagramInternalFrameListener extends InternalFrameAdapter {

        public void internalFrameActivated(InternalFrameEvent e) {
            ((DiagramInternalFrame) e.getInternalFrame()).setActive(true);
        }

        public void internalFrameDeActivated(InternalFrameEvent e) {
            ((DiagramInternalFrame) e.getInternalFrame()).setActive(false);
        }

        public void internalFrameIconified(InternalFrameEvent e) {
            ((DiagramInternalFrame) e.getInternalFrame()).setIconified(true);
        }

        public void internalFrameDeIconified(InternalFrameEvent e) {
            ((DiagramInternalFrame) e.getInternalFrame()).setIconified(false);
        }

        public void internalFrameClosing(InternalFrameEvent event) {
            int response = JOptionPane.showConfirmDialog(ApplicationGUI.this,
                    "Closing this window will discard the diagram data.\nAre you sure to proceed?",
                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                removeInternalFrame((DiagramInternalFrame) event.getSource());
            }
        }
    }

    /*
     * Prompts the user to save changes, closes the project, and finally exits application
     */
    private void exitApplication() {
        //exit app if the user decided to close project and didn't cancel
        if (closeProject()) {
            System.exit(0);
        }
    }

    private String getResource(String path) {
        return this.getClass().getResource(path).toString();
    }

    /*
     * closes the current project while prompting the user to save changes if necessary
     */
    protected boolean closeProject() {
        boolean runtimeChecking = isRuntimeChecking();
        setRuntimeChecking(false);

        if (!isSaved()) {
            int response = JOptionPane.showConfirmDialog(this, "Do you want to save changes to this project?",
                    "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                saveProject();
                closeFrames();
                umlProject.clear();
                centralRepository.clear();
                setRuntimeChecking(runtimeChecking);
                return true;
            } else if (response == JOptionPane.NO_OPTION) {
                closeFrames();
                umlProject.clear();
                centralRepository.clear();
                setRuntimeChecking(runtimeChecking);
                return true;
            } else {		// user pressed the cancel option
                setRuntimeChecking(runtimeChecking);
                return false;
            }

        } else {
            closeFrames();
            setRuntimeChecking(runtimeChecking);
            return true;
        }
    }

    protected void setSaved(boolean projectSaved) {
        setSaveMenuActionEnabled(!umlProject.isSaved());
        toolbar.setSaveActionEnabled(!umlProject.isSaved());
    }

    protected boolean isSaved() {
        if (umlProject == null) {
            return true;
        } else {
            return umlProject.isSaved();
        }
    }

    protected void setSaveMenuActionEnabled(boolean enabled) {
        saveProjectMenuItem.setEnabled(enabled);
    }

    public JTree getMessageTree() {
        return messageTree;
    }

    public JTree getFactsTree() {
        return factsTree;
    }

    public JPanel getRepairPanel() {
        return repairPanel;
    }

    public static boolean isApplet() {
        return isApplet;
    }
    /*
     * Below methods are used for remembering the tree expansion state for messageTree
     * is path1 descendant of path2
     */

    public static boolean isDescendant(TreePath path1, TreePath path2) {
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if (count1 <= count2) {
            return false;
        }
        while (count1 != count2) {
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    public static String getExpansionState(JTree tree, int row) {
        TreePath rowPath = tree.getPathForRow(row);
        StringBuffer buf = new StringBuffer();
        int rowCount = tree.getRowCount();
        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (i == row || isDescendant(path, rowPath)) {
                if (tree.isExpanded(path)) {
                    buf.append("," + String.valueOf(i - row));
                }
            } else {
                break;
            }
        }
        return buf.toString();
    }

    public static void restoreExpanstionState(JTree tree, int row, String expansionState) {
        StringTokenizer stok = new StringTokenizer(expansionState, ",");
        while (stok.hasMoreTokens()) {
            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }

    // Inner class ProjectToolBar implements the main toolbar of the application
    private class ProjectToolBar extends JToolBar {

        private JButton newButton;
        private JButton openButton;
        private JButton saveButton;
        private JButton saveAsButton;
        private JButton exportButton;
        private JButton useCaseButton;
        private JButton ssdButton;
        private JButton ccdButton;
        private JButton sdButton;
        private JButton dcdButton;
        private JButton adButton;
        private JButton forwardEngineerButton;
        private JButton resizeButton;
        private JButton helpButton;
        JButton reloadRulesButton;
        // private JButton validateSD_DCDButton;

        public ProjectToolBar() {
            setFloatable(false);
            ImageIcon newIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "new.gif"));
            newButton = new JButton(newIcon);
            newButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            newButton.setToolTipText("New Project");
            newButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    newButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    newButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            newButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    newProject();
                }
            });
            if (!isApplet) {  //applet version does not allow creation of new project
                add(newButton);
            }
            ImageIcon openIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "open.gif"));
            openButton = new JButton(openIcon);
            openButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            openButton.setToolTipText("Open Project");
            openButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    openButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    openButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            openButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    openProject();
                }
            });
            add(openButton);

            ImageIcon saveIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "save.gif"));
            saveButton = new JButton(saveIcon);
            saveButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            saveButton.setToolTipText("Save");
            saveButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    saveButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    saveButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            saveButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    saveProject();
                }
            });
            add(saveButton);

            ImageIcon saveAsIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "save_as.gif"));
            saveAsButton = new JButton(saveAsIcon);
            saveAsButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            saveAsButton.setToolTipText("Save As...");
            saveAsButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    saveAsButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    saveAsButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            saveAsButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    saveProjectAs();
                }
            });
            if (!isApplet) {
                add(saveAsButton);
            }

            addSeparator();

            ImageIcon exportIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "export.gif"));
            exportButton = new JButton(exportIcon);
            exportButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            exportButton.setToolTipText("Export to image");
            exportButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    exportButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    exportButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            exportButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    exportImage();
                }
            });
            if (!isApplet) {
                add(exportButton);
                addSeparator();
            }

            ImageIcon useCaseIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "useCaseDiagram.gif"));
            useCaseButton = new JButton(useCaseIcon);
            useCaseButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            useCaseButton.setToolTipText("New Use Case Diagram");
            useCaseButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    useCaseButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    useCaseButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            useCaseButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.UCD);
                }
            });

            ImageIcon ssdIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "ssd.gif"));
            ssdButton = new JButton(ssdIcon);
            ssdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            ssdButton.setToolTipText("New System Sequence Diagram");
            ssdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    ssdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    ssdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            ssdButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.SSD);
                }
            });

            ImageIcon ccdIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "ccd.gif"));
            ccdButton = new JButton(ccdIcon);
            ccdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            ccdButton.setToolTipText("New Conceptual Class Diagram");
            ccdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    ccdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    ccdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            ccdButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.CCD);
                }
            });

            ImageIcon sdIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "sd.gif"));
            sdButton = new JButton(sdIcon);
            sdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            sdButton.setToolTipText("New Sequence Diagram");
            sdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    sdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    sdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            sdButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.SD);
                }
            });

            ImageIcon dcdIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "dcd.gif"));
            dcdButton = new JButton(dcdIcon);
            dcdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            dcdButton.setToolTipText("New Design Class Diagram");
            dcdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    dcdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    dcdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            dcdButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.DCD);
                }
            });

            ImageIcon adIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "activityDiagram.gif"));
            adButton = new JButton(adIcon);
            adButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            adButton.setToolTipText("New Activity Diagram");
            adButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    adButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    adButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            adButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    createNewInternalFrame(DiagramModel.AD);
                }
            });
            add(adButton);
            add(useCaseButton);
            add(ccdButton);
            add(ssdButton);
            add(sdButton);
            add(dcdButton);
            addSeparator();

            ImageIcon resizeIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "resize.gif"));
            resizeButton = new JButton(resizeIcon);
            resizeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            resizeButton.setToolTipText("Resize Drawing Area");
            resizeButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    resizeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    resizeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            resizeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    resizeView();
                }
            });
            add(resizeButton);

            //Icon validateSD_DCDIcon = new ImageIcon(Application.class.getResource(imageLocation + "sd_dcd.gif"));
            //validateSD_DCDButton = new JButton(validateSD_DCDIcon);
            //validateSD_DCDButton.setToolTipText("Validate SD against DCD");
            //validateSD_DCDButton.addActionListener(this);
            addSeparator();

            ImageIcon reloadIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "reload.gif"));
            reloadRulesButton = new JButton(reloadIcon);
            reloadRulesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            reloadRulesButton.setToolTipText("Reload Rules");
            reloadRulesButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    reloadRulesButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    reloadRulesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            reloadRulesButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    reloadRules();
                }
            });
            add(reloadRulesButton);

            addSeparator();
            
            ImageIcon forwardEngineerIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "code.gif"));
            Image img2 = forwardEngineerIcon.getImage();
            Image imgScaled2 = img2.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            forwardEngineerIcon.setImage(imgScaled2);
            forwardEngineerButton = new JButton(forwardEngineerIcon);
            forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            forwardEngineerButton.setToolTipText("Generate Code");
            forwardEngineerButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                	forwardEngineerButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                	forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            forwardEngineerButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                	JCheckBox checkBox = new JCheckBox("Update Current Files",false);
                	String message = "Do you Want to Generate Code? \n"
                		    + "Make Sure You Have Created and Saved the Approrpiate\n"
                            + "Design (first) and Sequence Diagrams!";
                	Object[] params= {message, checkBox};
                	//0 for yes and 1 for no
                	int codeGenerationConfirm = JOptionPane.showConfirmDialog(
                		    frame,
                		    params,
                            "Code Generation",
                		    JOptionPane.YES_NO_OPTION);
                	boolean update = checkBox.isSelected();
                	if (codeGenerationConfirm == 0) {
                		int genFilesCount = umlProject.generateCode(update);
                		if (genFilesCount>0) {
                			JOptionPane.showMessageDialog(frame,
                			"Success!! \n" +
                			"You have generated " + genFilesCount + " files in\n" +
                			umlProject.getFilepath().replace(".xml", File.separator),
                			"Code Generator",JOptionPane.INFORMATION_MESSAGE);		
                		}else {
                			JOptionPane.showMessageDialog(frame,
                        			"No Input - New Files Not Generated",
                					"Code Generator",JOptionPane.INFORMATION_MESSAGE);
                		}
                	}
                }
            });
            add(forwardEngineerButton);
            
            addSeparator();


            ImageIcon helpIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "help.gif"));
            Image img = helpIcon.getImage();
            Image imgScaled = img.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            helpIcon.setImage(imgScaled);
            helpButton = new JButton(helpIcon);
            helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            helpButton.setToolTipText("Get help on using StudentUML");
            helpButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    helpButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            helpButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    help();
                }
            });
            add(helpButton);

            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        }

        public void setSaveActionEnabled(boolean enabled) {
            saveButton.setEnabled(enabled);
        }
    }

    // JPanel with components for inputting drawing view size
    private class SizeInputPanel extends JPanel {

        private JTextField heightField;
        private JLabel heightLabel;
        private JPanel heightPanel;
        private JTextField widthField;
        private JLabel widthLabel;
        private JPanel widthPanel;

        public SizeInputPanel() {
            widthLabel = new JLabel("Width (pixels): ");
            widthField = new JTextField(8);
            heightLabel = new JLabel("Height (pixels): ");
            heightField = new JTextField(8);
            widthPanel = new JPanel();
            widthPanel.setLayout(new FlowLayout());
            widthPanel.add(widthLabel);
            widthPanel.add(widthField);
            heightPanel = new JPanel();
            heightPanel.setLayout(new FlowLayout());
            heightPanel.add(heightLabel);
            heightPanel.add(heightField);
            setLayout(new GridLayout(2, 1, 5, 5));
            add(widthPanel);
            add(heightPanel);
        }

        public void setWidth(int width) {
            widthField.setText(String.valueOf(width));
        }

        public void setHeight(int height) {
            heightField.setText(String.valueOf(height));
        }

        public Dimension getSize() throws NumberFormatException {
            int width = 0,
                    height = 0;

            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());

                if (width < 0) {
                    width = 0;
                }

                if (height < 0) {
                    height = 0;
                }
            } catch (NumberFormatException e) {
                throw e;
            }

            return new Dimension(width, height);
        }
    }
}
