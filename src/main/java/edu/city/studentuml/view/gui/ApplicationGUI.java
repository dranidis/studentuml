package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.TreePath;

import edu.city.studentuml.applet.StudentUMLApplet;
import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.DiagramType;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.FrameProperties;
import edu.city.studentuml.util.NewversionChecker;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.gui.components.ProjectToolBar;
import edu.city.studentuml.view.gui.menu.MenuBar;

public abstract class ApplicationGUI extends JPanel implements KeyListener, Observer {

    private static final Logger logger = Logger.getLogger(ApplicationGUI.class.getName());

    public static boolean isApplet = false;
    protected StudentUMLFrame frame = null;
    protected StudentUMLApplet applet = null;
    protected boolean repairMode = false;
    protected UMLProject umlProject = UMLProject.getInstance();
    protected CentralRepository centralRepository;
    protected String simpleRulesFile;
    protected String advancedRulesFile;
    protected String currentRuleFile;
    protected JDesktopPane desktopPane; // holds internal frames
    protected RepositoryTreeView repositoryTreeView;
    protected JScrollPane treePane;
    protected JTree factsTree;
    protected JTree messageTree;
    protected CheckTreeManager checkTreeManager;
    protected JTabbedPane consistencyCheckTabbedPane;
    protected JSplitPane mainSplitPane;
    protected JSplitPane viewSplitPane;
    protected JScrollPane treeScrollPane;
    protected JScrollPane factsScrollPane;
    protected JPanel panel;
    protected int ruleEditorTabPlacement = -1;
    protected int factsTreeTabPlacement = -1;
    protected JPopupMenu popupMenu;
    protected JMenuItem popupRepair;
    protected JMenuItem popupHelp;
    protected JPanel repairPanel;
    protected JButton repairButton;
    protected int openFrameCounter = 0;
    private static ApplicationGUI instance; // need in ObjectFactory [backward compatiblity]

    protected boolean closingOrLoading = false;

    protected MenuBar menuBar;

    protected ApplicationGUI(StudentUMLFrame frame) {

        loadLookAndFeel();

        isApplet = false;
        this.frame = frame;
        instance = this;
        initialize();
        addWindowClosing(frame);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

        frame.setVisible(true);

        NewversionChecker.checkForNewVersion(frame);

        ObjectFactory.getInstance().addObserver(this);
        umlProject.addObserver(this);

        setRunTimeConsistencyCheckAndShowTabbedPane(Settings.isConsistencyCheckEnabled());
        showFactsTab(Settings.showFacts());
        showRuleEditorTab(Settings.showRules());

        consistencyCheckTabbedPane.setSelectedIndex(0);

    }

    private void loadLookAndFeel() {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            logger.fine("Available look and feel: " + info.getName() + " className: " + info.getClassName());
        }

        String preferredLF = Settings.getLookAndFeel();
        logger.fine(() -> "Preferred look and feel: " + preferredLF);

        try {
            UIManager.setLookAndFeel(preferredLF);
        } catch (Exception e) {
            logger.severe("Look and feel:  " + preferredLF + " not available. Using default.");
        }

        /*
         * uncomment if you want to examine the color resources
         */
        // Colors.prinUIManagerColorResources();

        logger.fine(() -> "Using look and feel: " + UIManager.getLookAndFeel().getClass().getName());
    }

    protected ApplicationGUI(StudentUMLApplet applet) {
        isApplet = true;
        this.applet = applet;
        instance = this;
        initialize();
        applet.getContentPane().add(this);
        applet.setVisible(true);
        setRunTimeConsistencyCheckAndShowTabbedPane(Settings.isConsistencyCheckEnabled());
    }

    // NEED FOR BACKWARD COMPATIBILITY
    public static ApplicationGUI getInstance() {
        return instance;
    }

    private void initialize() {
        initializeRules();
        SystemWideObjectNamePool.getInstance().addObserver(this);
        setUserId();
        createLookAndFeel();
        addKeyListener(this);
        createInterface();
    }

    private void initializeRules() {
        simpleRulesFile = getResource(Constants.RULES_SIMPLE);
        advancedRulesFile = getResource(Constants.RULES_ADVANCED);
        currentRuleFile = advancedRulesFile;

        // set the rule file and construct the consistency checker
        SystemWideObjectNamePool.getInstance().setRuleFile(currentRuleFile);
        
    }


    /*
     * sets the user id for coloring purposes (when drawing graphical elements)
     */
    private void setUserId() {
        if (isApplet) {
            SystemWideObjectNamePool.getInstance().setUid(applet.getUsername());
        } else {
            SystemWideObjectNamePool.getInstance().setUid(Constants.DESKTOP_USER);
        }
    }

    private void createLookAndFeel() {

    }

    private void createInterface() {
        setLayout(new BorderLayout());
        createMenuBar();
        createToolBar();
        createDesktopPane();
        update(umlProject, this);
        createFactsAndMessageTree();
        createDiagramAndConsistencyArea();
        createRepairPopupMenu();
        createRepairPanel();
        createCentralRepositoryTreeView();
    }

    private void createMenuBar() {
        if (!isApplet) {
            menuBar = new MenuBar(this);
            frame.setJMenuBar(menuBar.getjMenuBar());
        } else {
            applet.setJMenuBar(new MenuBar(this).getjMenuBar());
        }
    }

    private void createToolBar() {
        ProjectToolBar toolbar = new ProjectToolBar(this);
        BorderLayout bl = (BorderLayout) this.getLayout();
        ProjectToolBar c = (ProjectToolBar) bl.getLayoutComponent(BorderLayout.NORTH);
        if (c != null) {
            remove(c);
        } 
        add(toolbar, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void createDesktopPane() {
        desktopPane = new JDesktopPane();
        desktopPane.setBorder(new LineBorder(UIManager.getColor("Tree.hash"), 1, false));
        desktopPane.setBackground(UIManager.getColor("Tree.background"));
        desktopPane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    }

    private void createCentralRepositoryTreeView() {
        centralRepository = umlProject.getCentralRepository();

        repositoryTreeView = new RepositoryTreeView();
        treePane = new JScrollPane(repositoryTreeView);

        mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setResizeWeight(0);
        mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setRightComponent(viewSplitPane);
        mainSplitPane.setLeftComponent(treePane);
        add(mainSplitPane, BorderLayout.CENTER);
    }

    private void createDiagramAndConsistencyArea() {
        consistencyCheckTabbedPane = new JTabbedPane();
        consistencyCheckTabbedPane.setVisible(false);

        viewSplitPane = new JSplitPane();
        viewSplitPane.setDividerSize(5);
        viewSplitPane.setDividerLocation(450);
        viewSplitPane.setResizeWeight(1);
        viewSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        viewSplitPane.setLeftComponent(desktopPane);
        viewSplitPane.setRightComponent(consistencyCheckTabbedPane);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(treeScrollPane);

        consistencyCheckTabbedPane.addTab("Problems", null, panel, null);
    }

    private void createFactsAndMessageTree() {
        factsTree = new JTree();
        factsTree.setModel(null);
        messageTree = new JTree();
        messageTree.setModel(null);

        checkTreeManager = new CheckTreeManager(messageTree, false, path -> path.getPathCount() == 3);

        factsScrollPane = new JScrollPane();
        factsScrollPane.setViewportView(factsTree);

        treeScrollPane = new JScrollPane();
        treeScrollPane.setViewportView(messageTree);
    }

    private void createRepairPopupMenu() {
        popupMenu = new JPopupMenu();

        popupRepair = new JMenuItem();
        popupRepair.setText("Repair");
        popupRepair.addActionListener(e -> {
            String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();
            SystemWideObjectNamePool.getInstance().setSelectedRule(rulename);
            SystemWideObjectNamePool.getInstance().reload();
            SystemWideObjectNamePool.getInstance().setSelectedRule(null);

        });

        popupHelp = new JMenuItem();
        popupHelp.setText("Get Help");
        popupHelp.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(null, "No help URL defined or wrong URL", "Wrong URL",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        addPopup(messageTree, popupMenu);
    }

    private void addPopup(final Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if ((e.isPopupTrigger())) {
                    showMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if ((e.isPopupTrigger())) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                TreePath path = ((JTree) component).getSelectionPath();
                if (path != null && path.getPathCount() == 3) {
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
        });
    }

    public void createRepairPanel() {
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        repairPanel = new JPanel();
        repairPanel.setLayout(flowLayout);
        panel.add(repairPanel, BorderLayout.PAGE_END);

        repairButton = new JButton();
        repairButton.setBorder(new EmptyBorder(2, 5, 2, 5));
        repairButton.setName("Repair selected");
        repairButton.setText(" Repair selected");
        addBorderListener(repairButton);
        repairButton.addActionListener(e -> {
            TreePath[] checkedPaths = checkTreeManager.getSelectionModel().getSelectionPaths();

            if (checkedPaths != null) {
                for (int i = 0; i < checkedPaths.length; i++) {
                    SystemWideObjectNamePool.getInstance()
                            .setSelectedRule(checkedPaths[i].getLastPathComponent().toString());
                    SystemWideObjectNamePool.getInstance().reload();
                }
            }

            SystemWideObjectNamePool.getInstance().setSelectedRule(null);
            checkTreeManager.getSelectionModel().clearSelection();
            repairButton.setEnabled(false);

        });

        repairPanel.add(repairButton);
        repairButton.setEnabled(false);
        repairPanel.setVisible(false);
        showRepairButton(messageTree, repairButton);

        setRepairMode(true); // Sets on/off the REPAIR feature
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

    protected void showRepairButton(final Component component, final JButton button) {
        component.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                button.setEnabled(checkTreeManager.getSelectionModel().getSelectionPaths() != null);
            }
        });
    }

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        logger.finest(() -> "" + e);
        if (c != KeyEvent.CHAR_UNDEFINED) {
            logger.finest(() -> "" + c);
            repaint();
            e.consume();
        }
    }

    public void keyPressed(KeyEvent e) {
        logger.finest(() -> "" + e);
    }

    public void keyReleased(KeyEvent e) {
        logger.finest(() -> "" + e);
    }

    public void update(Observable observable, Object object) {
        String objString = "null";
        if (object != null) {
            objString = object.getClass().getSimpleName();
        }
        final String objStringFinal = objString;
        logger.finest(() -> ("UPDATE: from: " + observable.getClass().getSimpleName() + " arg: " + objStringFinal));

        if (object instanceof SystemWideObjectNamePool) {
            CollectionTreeModel messages = SystemWideObjectNamePool.getInstance().getMessages();
            CollectionTreeModel facts = SystemWideObjectNamePool.getInstance().getFacts();

            String messTreeState = null;

            if (messageTree.getModel() instanceof CollectionTreeModel) {
                messTreeState = getExpansionState(messageTree, 0);
                checkTreeManager.getSelectionModel().clearSelection();
                repairButton.setEnabled(false);
            }

            messageTree.setModel(messages);
            if (messTreeState != null) {
                restoreExpanstionState(messageTree, 0, messTreeState);
            }

            if (repairPanel != null && messages != null && messages.size() > 0 && isRepairMode()) {
                repairPanel.setVisible(true);
            } else if (repairPanel != null && messages != null && messages.size() == 0) {
                repairPanel.setVisible(false);
            }

            if (factsTree != null) {
                factsTree.setModel(facts);
            }

            umlProject.projectChanged();
        }
        if (object instanceof FrameProperties) {
            FrameProperties frameProperties = (FrameProperties) object;
            addInternalFrame(frameProperties.model, frameProperties);
        }
    }

    public abstract void newProject();

    public abstract void openProject();

    public abstract void openProjectFile(String fileName);

    public abstract void saveProject();

    public abstract void saveProjectAs();

    public abstract void exportImage();

    /*
     * creates a new empty diagram within the appropriate internal frame, depending
     * on the type integer
     */
    public void createNewInternalFrame(int type) {
        DiagramModel model;
        String modelName = inputModelName(type);

        if ((modelName != null) && (modelName.length() > 0)) {
            if (type == DiagramType.SSD) {
                model = new SSDModel("SSD: " + modelName, umlProject);
            } else if (type == DiagramType.SD) {
                model = new SDModel("SD: " + modelName, umlProject);
            } else if (type == DiagramType.CCD) {
                model = new CCDModel("CCD: " + modelName, umlProject);
            } else if (type == DiagramType.DCD) {
                model = new DCDModel("DCD: " + modelName, umlProject);
            } else if (type == DiagramType.AD) {
                model = new ADModel("AD: " + modelName, umlProject);
            } else if (type == DiagramType.UCD) {
                model = new UCDModel("UCD: " + modelName, umlProject);
            } else {
                return;
            }

            addInternalFrame(model);
        }
    }

    private String inputModelName(int type) {
        String dialogText;
        String initialName;
        switch (type) {
            case DiagramType.UCD:
                dialogText = "Use Case Diagram Name: ";
                initialName = "ucd";
                break;
            case DiagramType.SSD:
                dialogText = "System Sequence Diagram Name:";
                initialName = "ssd";
                break;
            case DiagramType.SD:
                dialogText = "Sequence Diagram Name: ";
                initialName = "sd";
                break;
            case DiagramType.CCD:
                dialogText = "Conceptual Class Diagram Name: ";
                initialName = "ccd";
                break;
            case DiagramType.DCD:
                dialogText = "Design Class Diagram Name: ";
                initialName = "dcd";
                break;
            case DiagramType.AD:
                dialogText = "Activity Diagram Name: ";
                initialName = "ad";
                break;
            default:
                throw new IllegalArgumentException("Unknown diagram (int) type: " + type);
        }
        // modelName
        return JOptionPane.showInputDialog(dialogText, initialName);
    }

    public abstract void help();

    protected void setRepairMode(boolean repairMode) {
        this.repairMode = repairMode;
    }

    protected boolean isRepairMode() {
        return repairMode;
    }

    public void reloadRules() {
        SystemWideObjectNamePool.getInstance().createNewConsistencyCheckerAndReloadRules();
    }

    protected boolean isRuntimeChecking() {
        return SystemWideObjectNamePool.getInstance().isRuntimeChecking();
    }

    protected void setRuntimeChecking(boolean b) {
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(b);
    }

    protected void setRuleFile(String ruleFile) {
        SystemWideObjectNamePool.getInstance().setRuleFile(ruleFile);
    }

    private void addWindowClosing(StudentUMLFrame f) {
        // override the default action when user presses the close button
        f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                exitApplication();
            }
        });
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
     * utilized by other methods trying to embed a diagram model in the appropriate
     * internal frame
     */
    public void addInternalFrame(DiagramModel model, FrameProperties frameProperties) {
        DiagramInternalFrame diagramInternalFrame = null;

        if (model instanceof UCDModel) {
            diagramInternalFrame = new UCDInternalFrame(model);
        } else if (model instanceof SSDModel) {
            diagramInternalFrame = new SSDInternalFrame((SSDModel) model);
        } else if (model instanceof CCDModel) {
            diagramInternalFrame = new CCDInternalFrame((CCDModel) model);
        } else if (model instanceof SDModel) {
            diagramInternalFrame = new SDInternalFrame((SDModel) model);
        } else if (model instanceof DCDModel) {
            diagramInternalFrame = new DCDInternalFrame((DCDModel) model,
                    /* advancedModeRadioButtonMenuItem.isSelected() */ true);
        } else if (model instanceof ADModel) {
            diagramInternalFrame = new ADInternalFrame(model);
        } else {
            logger.severe("Diagram Internal frame is null. Unknown model!");
            return;
        }

        diagramInternalFrame.addInternalFrameListener(new DiagramInternalFrameListener());

        final DiagramInternalFrame internal = diagramInternalFrame;

        diagramInternalFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                final int SLACK = 100;
                Dimension desktopSize = desktopPane.getSize();
                int minX = (int) (0 - internal.getSize().getWidth() + SLACK);
                int minY = 0;
                int maxX = desktopSize.width - SLACK;
                int maxY = desktopSize.height - SLACK;
                Rectangle bounds = internal.getBounds();
                if (bounds.x < minX) {
                    bounds.x = minX;
                    internal.setBounds(bounds);
                    desktopPane.repaint();
                }
                if (bounds.y < minY) {
                    bounds.y = minY;
                    internal.setBounds(bounds);
                    desktopPane.repaint();
                }
                if (bounds.x > maxX) {
                    bounds.x = maxX;
                    internal.setBounds(bounds);
                    desktopPane.repaint();
                }
                if (bounds.y > maxY) {
                    bounds.y = maxY;
                    internal.setBounds(bounds);
                    desktopPane.repaint();
                }

            }
        });

        desktopPane.add(diagramInternalFrame);
        openFrameCounter++;

        diagramInternalFrame.initialize(frameProperties);

        repositoryTreeView.expandDiagrams();
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
    }

    /*
     * returns a list of internal frames having a particular diagram type
     */
    public List<JInternalFrame> getInternalFramesOfType(int type) {
        List<JInternalFrame> frames = Arrays.asList(desktopPane.getAllFrames());

        switch (type) {
        case DiagramType.UCD:
            return frames.stream().filter(UCDInternalFrame.class::isInstance).collect(Collectors.toList());
        case DiagramType.SSD:
            return frames.stream().filter(SSDInternalFrame.class::isInstance).collect(Collectors.toList());
        case DiagramType.CCD:
            return frames.stream().filter(CCDInternalFrame.class::isInstance).collect(Collectors.toList());
        case DiagramType.SD:
            return frames.stream().filter(SDInternalFrame.class::isInstance).collect(Collectors.toList());
        case DiagramType.DCD:
            return frames.stream().filter(DCDInternalFrame.class::isInstance).collect(Collectors.toList());
        case DiagramType.AD:
            return frames.stream().filter(ADInternalFrame.class::isInstance).collect(Collectors.toList());
        default:
        }
        return new ArrayList<>();
    }

    public void setRunTimeConsistencyCheckAndShowTabbedPane(boolean b) {
        setRuntimeChecking(b);
        consistencyCheckTabbedPane.setVisible(b);
        if (b) {
            viewSplitPane.setDividerSize(5);
            viewSplitPane.setDividerLocation(getHeight() * 360 / 600);
            reloadRules();
        } else {
            viewSplitPane.setDividerSize(0);
        }
    }

    public void showRuleEditorTab(boolean b) {
        if (b) {
            ruleEditorTabPlacement = consistencyCheckTabbedPane.getTabCount();
            consistencyCheckTabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null,
                    consistencyCheckTabbedPane.getTabCount());
            consistencyCheckTabbedPane.setSelectedIndex(ruleEditorTabPlacement);
        } else if (ruleEditorTabPlacement != -1) {
            consistencyCheckTabbedPane.remove(ruleEditorTabPlacement);
            ruleEditorTabPlacement = -1;
        }
    }

    public int getRuleEditorTabPlacement() {
        return ruleEditorTabPlacement;
    }

    public void showFactsTab(boolean selected) {
        if (selected && factsTreeTabPlacement == -1) {
            factsTreeTabPlacement = consistencyCheckTabbedPane.getTabCount();
            consistencyCheckTabbedPane.insertTab("Facts", null, factsScrollPane, null,
                    consistencyCheckTabbedPane.getTabCount());
            consistencyCheckTabbedPane.setSelectedIndex(factsTreeTabPlacement);
        } else if (factsTreeTabPlacement != -1) {
            consistencyCheckTabbedPane.remove(factsTreeTabPlacement);
            factsTreeTabPlacement = -1;
        }
    }

    public void simpleMode() {
        currentRuleFile = simpleRulesFile;
        setRuleFile(simpleRulesFile);
        reloadRules();
        if (ruleEditorTabPlacement != -1) {
            int selected = consistencyCheckTabbedPane.getSelectedIndex();
            consistencyCheckTabbedPane.remove(ruleEditorTabPlacement);
            consistencyCheckTabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
            consistencyCheckTabbedPane.setSelectedIndex(selected);
        }

        getInternalFramesOfType(DiagramType.DCD).forEach(iFrame -> ((DCDInternalFrame) iFrame).setAdvancedMode(false));
    }

    public void advancedMode() {
        currentRuleFile = advancedRulesFile;
        setRuleFile(advancedRulesFile);
        reloadRules();
        if (ruleEditorTabPlacement != -1) {
            int selected = consistencyCheckTabbedPane.getSelectedIndex();
            consistencyCheckTabbedPane.remove(ruleEditorTabPlacement);
            consistencyCheckTabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
            consistencyCheckTabbedPane.setSelectedIndex(selected);
        }
        getInternalFramesOfType(DiagramType.DCD).forEach(iFrame -> ((DCDInternalFrame) iFrame).setAdvancedMode(true));
    }

    /*
     * inner class listens for events from internal frames; overrides default
     * behavior when the user closes the frame
     */
    private class DiagramInternalFrameListener extends InternalFrameAdapter {

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {
            logger.finer("Frame activated");
            umlProject.setSaved(false);
            ((DiagramInternalFrame) e.getInternalFrame()).setActive(true);
        }

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {
            logger.finer("Frame deactivated");
            umlProject.setSaved(false);
            ((DiagramInternalFrame) e.getInternalFrame()).setActive(false);
        }

        @Override
        public void internalFrameIconified(InternalFrameEvent e) {
            logger.finer("Frame iconified");
            umlProject.setSaved(false);
            ((DiagramInternalFrame) e.getInternalFrame()).setIconified(true);
        }

        @Override
        public void internalFrameDeiconified(InternalFrameEvent e) {
            logger.finer("Frame deiconified");
            umlProject.setSaved(false);
            ((DiagramInternalFrame) e.getInternalFrame()).setIconified(false);
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            logger.finer("Frame closing");

            // closing a frame removes the diagram from the model
            // closing is only possible from the popup menu "Delete" on the diagram
            // top bar.
            umlProject.setSaved(false);
            removeInternalFrame((DiagramInternalFrame) event.getSource());
        }
    }

    /*
     * Prompts the user to save changes, closes the project, and finally exits
     * application
     */
    private void exitApplication() {
        // exit app if the user decided to close project and didn't cancel
        if (closeProject()) {
            System.exit(0);
        }
    }

    private String getResource(String path) {
        return this.getClass().getResource(path).toString();
    }

    /**
     * Closes the current project while prompting the user to save changes if necessary
     * 
     * @return true if project was closed AND false if closing was cancelled or if saving was cancellled in an unsaved project. 
     */
     public boolean closeProject() {
        boolean runtimeChecking = isRuntimeChecking();
        setRuntimeChecking(false);

        logger.fine("Closing project");
        if (!isSaved()) {
            int response = JOptionPane.showConfirmDialog(this, "Do you want to save changes to this project?",
                    "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                saveProject();

                if (!isSaved()) // saving was cancelled
                    return false;

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
            } else { // user pressed the cancel option
                setRuntimeChecking(runtimeChecking);
                return false;
            }

        } else {
            closingOrLoading = true;

            closeFrames();
            setRuntimeChecking(runtimeChecking);

            closingOrLoading = false;
            return true;
        }
    }

    protected boolean isSaved() {
        if (umlProject == null) {
            return true;
        } else {
            return umlProject.isSaved();
        }
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
     * Below methods are used for remembering the tree expansion state for
     * messageTree is path1 descendant of path2
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
        StringBuilder buf = new StringBuilder();
        int rowCount = tree.getRowCount();
        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (i == row || isDescendant(path, rowPath)) {
                if (tree.isExpanded(path)) {
                    buf.append("," + (i - row));
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


    public void changeLookAndFeel(String className) {

        for (JInternalFrame f: desktopPane.getAllFrames()) {
            DiagramInternalFrame iFrame = (DiagramInternalFrame) f;
            iFrame.setzOrder(desktopPane.getComponentZOrder(iFrame));
        }

        try {
            UIManager.setLookAndFeel(className);
            Settings.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.updateComponentTreeUI(frame);

        createToolBar();

        for (JInternalFrame f : desktopPane.getAllFrames()) {
            DiagramInternalFrame iFrame = (DiagramInternalFrame) f;
            iFrame.recreateInternalFrame();
            desktopPane.setComponentZOrder(iFrame, iFrame.getzOrder());
            f.revalidate();
            f.repaint();
        }   

        /*
         * create again the repositoryTreeView
         */
        umlProject.deleteObserver(repositoryTreeView);
        RepositoryTreeView newRepositoryTreeView = new RepositoryTreeView();
        JScrollPane newTreePane = new JScrollPane(newRepositoryTreeView);
        
        int splitLocation = mainSplitPane.getDividerLocation();
        mainSplitPane.setLeftComponent(newTreePane);
        mainSplitPane.setDividerLocation(splitLocation);

        String expansionState = repositoryTreeView.getExpansionState(0);
        logger.finer(() -> "EXP state: " + expansionState);

        repositoryTreeView = newRepositoryTreeView;
        treePane = newTreePane;

        mainSplitPane.revalidate();
        mainSplitPane.repaint();
        
        repositoryTreeView.updateTree();
        repositoryTreeView.restoreExpansionState(0, expansionState);

        repositoryTreeView.revalidate();
        repositoryTreeView.repaint();
        treePane.revalidate();
        treePane.repaint();

        this.repaint();
        this.revalidate();
    }

    private void repaintInternalFrames() {
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            f.repaint();
        }
    }

    public void changeFillColor() {
        Colors.chooseFillColor();
        repaintInternalFrames();    
    }

}
