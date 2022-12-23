package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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

import edu.city.studentuml.applet.StudentUMLApplet;
import edu.city.studentuml.codegeneration.CodePreparation;
import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.FrameProperties;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.gui.menu.MenuBar;

public abstract class ApplicationGUI extends JPanel implements KeyListener, Observer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    private ProjectToolBar toolbar;
    protected JDesktopPane desktopPane; // holds internal frames
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
    private static ApplicationGUI instance; // need in ObjectFactory [backward compatiblity]
    protected DiagramInternalFrame selectedFrame;

    protected boolean closingOrLoading = false;

    private static final String SELECT_LAST = "SELECT_LAST";

    protected ApplicationGUI(StudentUMLFrame frame) {
        if (Preferences.userRoot().get(SELECT_LAST, "").equals("")) {
            Preferences.userRoot().put(SELECT_LAST, "TRUE");
        }
        String selectLast = Preferences.userRoot().get(SELECT_LAST, "");
        logger.fine(() -> ("SELECT_LAST:" + selectLast));
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

        ObjectFactory.getInstance().addObserver(this);
        umlProject.addObserver(this);
    }

    protected ApplicationGUI(StudentUMLApplet applet) {
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
        SystemWideObjectNamePool.getInstance().addObserver(this);
        setUserId();
        createLookAndFeel();
        addKeyListener(this);
        createInterface();
        setRunTimeConsistencyCheck(false);
    }

    private void initializeRules() {
        simpleRulesFile = getResource(Constants.RULES_SIMPLE);
        advancedRulesFile = getResource(Constants.RULES_ADVANCED);
        currentRuleFile = advancedRulesFile;

        // set the rule file and construct the consistency checker
        SystemWideObjectNamePool.getInstance().init(currentRuleFile);
    }

    /*
     * sets the user id for coloring purposes (when drawing graphical elements)
     */
    private void setUserId() {
        if (isApplet) {
            SystemWideObjectNamePool.uid = applet.getUsername();
        } else {
            SystemWideObjectNamePool.uid = Constants.DESKTOP_USER;
        }
    }

    private void createLookAndFeel() {
        // System.setProperty("lipstikLF.theme", "LightGrayTheme");
        //
        // try {
        // UIManager.setLookAndFeel(new com.lipstikLF.LipstikLookAndFeel());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
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
            frame.setJMenuBar(new MenuBar(this).getMenuBar());
        } else {
            applet.setJMenuBar(new MenuBar(this).getMenuBar());
        }
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
        treePane = new JScrollPane(repositoryTreeView);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setRightComponent(splitPane_1);
        splitPane.setLeftComponent(treePane);
        add(splitPane, BorderLayout.CENTER);
    }

    private void createDiagramAndConsistencyArea() {
        tabbedPane = new JTabbedPane();
        splitPane_1 = new JSplitPane();
        splitPane_1.setDividerSize(5);
        splitPane_1.setDividerLocation(450);
        splitPane_1.setResizeWeight(1);
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane_1.setLeftComponent(desktopPane);
        splitPane_1.setRightComponent(tabbedPane);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane_p);

        // scrollPane_f = new JScrollPane();
        // scrollPane_f.setViewportView(factsTree);
        tabbedPane.addTab("Problems", null, panel, null);
        // tabbedPane.addTab("Rule Editor", null, new RuleEditor(currentRuleFile),
        // null);
        // tabbedPane.addTab("Facts", null, scrollPane_f, null);
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
                // SystemWideObjectNamePool.getInstance().loading();
                SystemWideObjectNamePool.getInstance().setSelectedRule(rulename);
                // SystemWideObjectNamePool.getInstance().done();
                //// SystemWideObjectNamePool.getInstance().reloadRules();
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
                    JOptionPane.showMessageDialog(null, "No help URL defined or wrong URL", "Wrong URL",
                            JOptionPane.ERROR_MESSAGE);
                }
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
        repairButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TreePath[] checkedPaths = checkTreeManager.getSelectionModel().getSelectionPaths();

                // String rulename =
                // messageTree.getSelectionPath().getLastPathComponent().toString();
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
            }
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
                        new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(4, 4, 4, 4)));
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
        System.out.println(e);
        if (c != KeyEvent.CHAR_UNDEFINED) {
            System.out.println(c);
            repaint();
            e.consume();
        }
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e);
    }

    public void keyReleased(KeyEvent e) {
        System.out.println(e);
    }

    public void update(Observable observable, Object object) {
        String objString = "null";
        if (object != null) {
            objString = object.getClass().getSimpleName();
        }
        final String objStringFinal = objString;
        logger.fine(() -> ("UPDATE: from: " + observable.getClass().getSimpleName() + " arg: " + objStringFinal));

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
            FrameProperties fp = (FrameProperties) object;
            addInternalFrame(fp.model, fp.R);

            if (fp.selected) {
                selectedFrame = fp.model.getFrame();
            }
            try {
                // TODO: refactor
                fp.model.getFrame().setSelected(fp.selected);
                fp.model.getFrame().setIcon(fp.iconified);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void newProject();

    public abstract void openProject();

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

            addInternalFrame(model);
        }
    }

    private String inputModelName(int type) {
        String dialogText;
        String initialName;
        switch (type) {
        case DiagramModel.UCD:
            dialogText = "Use Case Diagram Name: ";
            initialName = "ucd";
            break;
        case DiagramModel.SSD:
            dialogText = "System Sequence Diagram Name:";
            initialName = "ssd";
            break;
        case DiagramModel.SD:
            dialogText = "Sequence Diagram Name: ";
            initialName = "sd";
            break;
        case DiagramModel.CCD:
            dialogText = "Conceptual Class Diagram Name: ";
            initialName = "ccd";
            break;
        case DiagramModel.DCD:
            dialogText = "Design Class Diagram Name: ";
            initialName = "dcd";
            break;
        case DiagramModel.AD:
            dialogText = "Activity Diagram Name: ";
            initialName = "ad";
            break;
        default:
            throw new RuntimeException("Unknown diagram (int) type: " + type);
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
        SystemWideObjectNamePool.getInstance().reloadRules();
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
     * opens appropriate frames based on a vector of diagram model objects
     */
    // private void openFrames(Vector diagramModels) {
    // DiagramModel model;
    // Iterator iterator = diagramModels.iterator();
    //
    // while (iterator.hasNext()) {
    // model = (DiagramModel) iterator.next();
    // model.addObserver(this);
    // addInternalFrame(model);
    // }
    // }

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
    public void addInternalFrame(DiagramModel model, Rectangle rectangle) {
        DiagramInternalFrame diagramInternalFrame = null;

        if (model instanceof UCDModel) {
            diagramInternalFrame = new UCDInternalFrame((UCDModel) model);
        } else if (model instanceof SSDModel) {
            diagramInternalFrame = new SSDInternalFrame((SSDModel) model);
        } else if (model instanceof CCDModel) {
            diagramInternalFrame = new CCDInternalFrame((CCDModel) model);
        } else if (model instanceof SDModel) {
            diagramInternalFrame = new SDInternalFrame((SDModel) model);
        } else if (model instanceof DCDModel) {
            diagramInternalFrame = new DCDInternalFrame((DCDModel) model, /* advancedModeRadioButtonMenuItem.isSelected() */ true);
        } else if (model instanceof ADModel) {
            diagramInternalFrame = new ADInternalFrame((ADModel) model);
        } // else if (model instanceof StateModel) {
          // f = new StateInternalFrame((StateModel) model);
          // }

        if (diagramInternalFrame == null) {
            logger.severe("Diagram Internal frame is null. Unknown model!");
            return;
        }

        if (rectangle != null) {
            diagramInternalFrame.setBounds(rectangle);
            diagramInternalFrame.getView().setSize((int) rectangle.getWidth(), (int) rectangle.getHeight());
        }

        model.setFrame(diagramInternalFrame);
        diagramInternalFrame.addInternalFrameListener(new DiagramInternalFrameListener());
        desktopPane.add(diagramInternalFrame);
        openFrameCounter++;
        diagramInternalFrame.setOpaque(true);
        diagramInternalFrame.setVisible(true);

        try {
            diagramInternalFrame.setSelected(true);
        } catch (PropertyVetoException vetoException) {
            vetoException.printStackTrace();
        }

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
    public Vector<JInternalFrame> getInternalFramesOfType(int type) {
        Vector<JInternalFrame> ucdFrames = new Vector<>();
        Vector<JInternalFrame> ssdFrames = new Vector<>();
        Vector<JInternalFrame> sdFrames = new Vector<>();
        Vector<JInternalFrame> ccdFrames = new Vector<>();
        Vector<JInternalFrame> dcdFrames = new Vector<>();
        Vector<JInternalFrame> adFrames = new Vector<>();
        // Vector stateFrames = new Vector();
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
            } // else if (f instanceof StateInternalFrame) {
              // stateFrames.add(f);
              // }
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
        } // else if () {
          // return stateFrames;
          // }

        return new Vector<JInternalFrame>();
    }

    public void setRunTimeConsistencyCheck(boolean b) {
        setRuntimeChecking(b);
        tabbedPane.setVisible(b);
        if (b) {
            splitPane_1.setDividerSize(5);
            splitPane_1.setDividerLocation(getHeight() * 360 / 600);
            reloadRules();
        } else {
            splitPane_1.setDividerSize(0);
        }
    }

    public void showRuleEditorTab(boolean b) {
        if (b) {
            ruleEditorTabPlacement = tabbedPane.getTabCount();
            tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, tabbedPane.getTabCount());
            tabbedPane.setSelectedIndex(ruleEditorTabPlacement);
        } else {
            tabbedPane.remove(ruleEditorTabPlacement);
            ruleEditorTabPlacement = -1;
        }
    }

    public int getRuleEditorTabPlacement() {
        return ruleEditorTabPlacement;
    }

    public void showFactsTab(boolean selected) {
        if (selected && factsTreeTabPlacement == -1) {
            factsTreeTabPlacement = tabbedPane.getTabCount();
            tabbedPane.insertTab("Facts", null, scrollPane_f, null, tabbedPane.getTabCount());
            tabbedPane.setSelectedIndex(factsTreeTabPlacement);
        } else {
            tabbedPane.remove(factsTreeTabPlacement);
            factsTreeTabPlacement = -1;
        }
    }

    public void simpleMode() {
        currentRuleFile = simpleRulesFile;
        setRuleFile(simpleRulesFile);
        reloadRules();
        if (ruleEditorTabPlacement != -1) {
            int selected = tabbedPane.getSelectedIndex();
            tabbedPane.remove(ruleEditorTabPlacement);
            tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
            tabbedPane.setSelectedIndex(selected);
        }
        Vector<JInternalFrame> dcdFrames = getInternalFramesOfType(DiagramModel.DCD);
        for (int i = 0; i < dcdFrames.size(); i++) {
            ((DCDInternalFrame) dcdFrames.get(i)).setAdvancedMode(false);
        }
    }

    public void advancedMode() {
        currentRuleFile = advancedRulesFile;
        setRuleFile(advancedRulesFile);
        reloadRules();
        if (ruleEditorTabPlacement != -1) {
            int selected = tabbedPane.getSelectedIndex();
            tabbedPane.remove(ruleEditorTabPlacement);
            tabbedPane.insertTab("Rule Editor", null, new RuleEditor(currentRuleFile), null, ruleEditorTabPlacement);
            tabbedPane.setSelectedIndex(selected);
        }
        Vector<JInternalFrame> dcdFrames = getInternalFramesOfType(DiagramModel.DCD);
        for (int i = 0; i < dcdFrames.size(); i++) {
            ((DCDInternalFrame) dcdFrames.get(i)).setAdvancedMode(true);
        }
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

        public void internalFrameDeActivated(InternalFrameEvent e) {
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

        public void internalFrameDeIconified(InternalFrameEvent e) {
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

    /*
     * closes the current project while prompting the user to save changes if
     * necessary
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
    private class ProjectToolBar extends JToolBar {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
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
        private JButton helpButton;
        JButton reloadRulesButton;
        // private JButton validateSD_DCDButton;

        private JButton createToolBarButton(String iconFileName, String toolTipText, ActionListener listener) {
            ImageIcon newIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + iconFileName));
            JButton button = new JButton(newIcon);
            button.setBorder(new EmptyBorder(5, 5, 5, 5));
            button.setToolTipText(toolTipText);
            addBorderListener(button);
            button.addActionListener(listener);
            return button;
        }

        public ProjectToolBar() {
            setFloatable(false);

            newButton = createToolBarButton("new.gif", "New Project", e -> newProject());
            openButton = createToolBarButton("open.gif", "Open Project", e -> openProject());
            saveButton = createToolBarButton("save.gif", "Save Project", e -> saveProject());
            saveAsButton = createToolBarButton("save_as2.gif", "Save As", e -> saveProjectAs());
            exportButton = createToolBarButton("export.gif", "Export to image", e -> exportImage());

            if (!isApplet) { // applet version does not allow creation of new project
                add(newButton);
            }
            add(openButton);
            add(saveButton);
            if (!isApplet) {
                add(saveAsButton);
                add(exportButton);
                addSeparator();
            }

            useCaseButton = createToolBarButton("useCaseDiagram.gif", "New Use Case Diagram", e -> createNewInternalFrame(DiagramModel.UCD));
            ssdButton = createToolBarButton("ssd.gif", "New System Sequence Diagram", e -> createNewInternalFrame(DiagramModel.SSD));
            ccdButton = createToolBarButton("ccd.gif", "New Conceptual Class Diagram", e -> createNewInternalFrame(DiagramModel.CCD));
            sdButton = createToolBarButton("sd.gif", "New Sequence Diagram", e -> createNewInternalFrame(DiagramModel.SD));
            dcdButton = createToolBarButton("dcd.gif", "New Design Class Diagram", e -> createNewInternalFrame(DiagramModel.DCD));
            adButton = createToolBarButton("activityDiagram.gif", "New Activity Diagram", e -> createNewInternalFrame(DiagramModel.AD));

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
            addSeparator();

            reloadRulesButton = createToolBarButton("reload.gif", "Reload Rules", e -> reloadRules());

            /**
             * TODO: REMOVE TILL it is clear what it does!
            // add(reloadRulesButton);
             */

            addSeparator();

            ImageIcon forwardEngineerIcon = new ImageIcon(
                    this.getClass().getResource(Constants.IMAGES_DIR + "code.gif"));
            Image img2 = forwardEngineerIcon.getImage();
            Image imgScaled2 = img2.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            forwardEngineerIcon.setImage(imgScaled2);
            forwardEngineerButton = new JButton(forwardEngineerIcon);
            forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            forwardEngineerButton.setToolTipText("Generate Code");
            addBorderListener(forwardEngineerButton);

            forwardEngineerButton.addActionListener(e -> {
                    JCheckBox checkBox = new JCheckBox("Update Current Files", false);
                    String message = "Do you Want to Generate Code? \n"
                            + "Make Sure You Have Created and Saved the Approrpiate\n"
                            + "Design (first) and Sequence Diagrams!";
                    Object[] params = { message, checkBox };
                    // 0 for yes and 1 for no
                    int codeGenerationConfirm = JOptionPane.showConfirmDialog(frame, params, "Code Generation",
                            JOptionPane.YES_NO_OPTION);
                    boolean update = checkBox.isSelected();
                    if (codeGenerationConfirm == 0) {
                        CodePreparation codePreparation = new CodePreparation();
                        int genFilesCount = codePreparation.generateCode(update);
                        if (genFilesCount > 0) {
                            JOptionPane.showMessageDialog(frame,
                                    "Success!! \n" + "You have generated " + genFilesCount + " files in\n"
                                            + umlProject.getFilepath().replace(".xml", File.separator),
                                    "Code Generator", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "No Input - New Files Not Generated", "Code Generator",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            );

            /**
             * TODO: REMOVE THE BUTTON TILL code generation is completed!
            // add(forwardEngineerButton);
             */

            addSeparator();

            ImageIcon helpIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "help.gif"));
            Image img = helpIcon.getImage();
            Image imgScaled = img.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            helpIcon.setImage(imgScaled);
            helpButton = new JButton(helpIcon);
            helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            helpButton.setToolTipText("Get help on using StudentUML");
            addBorderListener(helpButton);

            helpButton.addActionListener(e -> help());

            /**
             * TODO: REMOVE THE HELP BUTTON TILL HELP IS IMPLEMENTED
            // add(helpButton);
             */

            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        }



        public void setSaveActionEnabled(boolean enabled) {
            saveButton.setEnabled(enabled);
        }
    }

}
