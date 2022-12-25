package edu.city.studentuml.applet;

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
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.city.studentuml.model.domain.UMLProject;
//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//Application.java
//Class Application contains the init() method where applet execution starts.
//It stands at the top of the system and implements the GUI of the application.
//This class is the root of all the UML PROJECT data, including the central repository
//of UML elements, as well as the UML diagrams, each of which exists
//within a diagram internal frame. Only ONE project can be open at one time.
//Finally, class Application manages various utilities, such as opening/saving a project,
//exporting a diagram to a graphics file, forward engineering code, checking diagram's
//consistencies, etc, mainly by making use of other classes.
//Alexander taking over
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.DiagramType;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.DiagramView;
import edu.city.studentuml.view.gui.CCDInternalFrame;
import edu.city.studentuml.view.gui.CollectionTreeModel;
import edu.city.studentuml.view.gui.DCDInternalFrame;
import edu.city.studentuml.view.gui.DiagramInternalFrame;
import edu.city.studentuml.view.gui.RepositoryTreeView;
import edu.city.studentuml.view.gui.SDInternalFrame;
import edu.city.studentuml.view.gui.SSDInternalFrame;

public class Application extends JApplet implements Observer, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Application instance;

    private JButton repairButton;
    private JPanel repairPanel;
    private JPanel panel;
    private JMenuItem popupRepair;
    private JMenuItem popupHelp;
    private JPopupMenu popupMenu;
    private JTree factsTree;
    private JScrollPane scrollPane_f;
    private JTabbedPane tabbedPane;
    private JTree messageTree;
    private JScrollPane scrollPane_p;
    private JSplitPane splitPane_1;
    // the central repository of conceptual UML elements
    private CentralRepository centralRepository;
    // all diagrams are stored in corresponding internal frames,
    // which in turn are stored in the desktop pane
    private JDesktopPane desktopPane;
    private RepositoryTreeView repositoryTreeView;
    private ProjectToolBar toolbar;
    private JScrollPane treePane;
    private ServerInterface serverInterface = null;
    private UMLProject umlProject = null;
    private CheckTreeManager checkTreeManager;
    private String advancedRulesFile;
    private String currentRuleFile = null;
    private String username = null;
    private String exid = null;
    private String nodeid = null;
    private String parentid = null;
    private String diagramno = null;
    private String apiURL = null;
    private boolean repairMode = false;

    public static Application getInstance() {
        return instance;
    }

    // init method just creates an instance of the StudentUMLApplet class
    // which handles the rest of the program
    // Firefox: when a fresh browser is opened
    public void init() {
        addKeyListener(this);
        super.init();
        // because applet is a SINGLETON
        instance = this;
        initialiseApplet();
    }

    // Firefox: when the URL containing the applet is opened
    public void start() {
    }

    // Firefox: when the URL containing the applet is closed or navigated away
    public void stop() {
    }

    public void destroy() {
        // Firefox: when the last browser instance is closed
    }

    public void initialiseApplet() {
        username = getParameter("username");
        exid = getParameter("exid");
        nodeid = getParameter("nodeid");
        parentid = getParameter("parentid");
        diagramno = getParameter("diagramno");
        apiURL = getParameter("api_base");

        String passedAuthenticationToken = getParameter("auth_token");
        serverInterface = new ServerInterface(apiURL, passedAuthenticationToken);

        // initialize the rules files
        // simpleRulesFile = getCodeBase() + "rules/simplerules.txt";
        // advancedRulesFile = getCodeBase() + "rules/advancedrules.txt";
        String rulesLocation = "/edu/city/studentuml/util/validation/rules/";
        this.getClass().getResource(rulesLocation + "simplerules.txt").toString();
        advancedRulesFile = this.getClass().getResource(rulesLocation + "advancedrules.txt").toString();
        currentRuleFile = advancedRulesFile;

        // initialize the SystemWideObjectNamePool singleton for the first time
        // with the current rules file
        SystemWideObjectNamePool.uid = username;
        SystemWideObjectNamePool.getInstance().init(currentRuleFile);
        SystemWideObjectNamePool.getInstance().addObserver(this);

        factsTree = new JTree();
        factsTree.setModel(null);
        messageTree = new JTree();
        messageTree.setModel(null);

        checkTreeManager = new CheckTreeManager(messageTree, false, new TreePathSelectable() {

            public boolean isSelectable(TreePath path) {
                return path.getPathCount() == 3;
            }
        });

        desktopPane = new JDesktopPane();
        desktopPane.setBorder(new LineBorder(UIManager.getColor("Tree.hash"), 1, false));
        desktopPane.setBackground(UIManager.getColor("Tree.background"));
        desktopPane.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
        toolbar = new ProjectToolBar();
        getContentPane().add(toolbar, BorderLayout.NORTH);

        // initialize a new project
        umlProject = UMLProject.getInstance();
        umlProject.addObserver(this);

        centralRepository = umlProject.getCentralRepository();
        repositoryTreeView = new RepositoryTreeView();
        treePane = new JScrollPane(repositoryTreeView);
        getContentPane().add(treePane, BorderLayout.WEST);

        update(umlProject, this);

        new JSplitPane();

        splitPane_1 = new JSplitPane();
        splitPane_1.setDividerSize(5);
        splitPane_1.setDividerLocation(450);
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);

        /*
         * splitPane_1.setOneTouchExpandable(true);
         * 
         * BasicSplitPaneUI ui = (BasicSplitPaneUI)splitPane_1.getUI();
         * BasicSplitPaneDivider divider = ui.getDivider(); JButton button =
         * (JButton)divider.getComponent(1); button.doClick();
         */
        getContentPane().add(splitPane_1, BorderLayout.CENTER);
        splitPane_1.setLeftComponent(desktopPane);

        tabbedPane = new JTabbedPane();
        splitPane_1.setRightComponent(tabbedPane);
        splitPane_1.setResizeWeight(0.7);

        scrollPane_f = new JScrollPane();
        scrollPane_f.setViewportView(factsTree);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        tabbedPane.addTab("Problems", null, panel, null);
        // tabbedPane.addTab("Rule Editor", null, new RuleEditor(), null);
        // tabbedPane.addTab("Facts", null, scrollPane_f, null);

        scrollPane_p = new JScrollPane();
        panel.add(scrollPane_p);

        scrollPane_p.setViewportView(messageTree);

        popupMenu = new JPopupMenu();
        new JPopupMenu();

        popupRepair = new JMenuItem();

        // Repair Action
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
        popupRepair.setText("Repair");

        popupHelp = new JMenuItem();

        // Repair Action
        popupHelp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();

                Rule rule = SystemWideObjectNamePool.getInstance().getRule(rulename);
                String helpString = (rule == null) ? null : rule.gethelpurl();

                try {
                    URL url = new URL(helpString);
                    Application.this.getAppletContext().showDocument(url, "_blank");
                } catch (MalformedURLException mue) {
                    JOptionPane.showMessageDialog(null, "No help URL defined or wrong URL", "Wrong URL",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        popupHelp.setText("Get Help");

        addPopup(messageTree, popupMenu);

        repairPanel = new JPanel();
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        repairPanel.setLayout(flowLayout);
        panel.add(repairPanel, BorderLayout.PAGE_END);

        repairButton = new JButton();
        repairButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                repairButton.setBorder(
                        new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1), new EmptyBorder(1, 4, 1, 4)));
            }

            public void mouseExited(MouseEvent e) {
                repairButton.setBorder(new EmptyBorder(2, 5, 2, 5));
            }
        });
        repairButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TreePath checkedPaths[] = checkTreeManager.getSelectionModel().getSelectionPaths();

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

        repairButton.setBorder(new EmptyBorder(2, 5, 2, 5));
        repairButton.setName("Repair selected");
        repairButton.setText(" Repair selected");
        repairPanel.add(repairButton);
        repairButton.setEnabled(false);
        repairPanel.setVisible(false);
        showRepairButton(messageTree, repairButton);

        setRepairMode(true); // Sets on/off the REPAIR feature

        setVisible(true);

        // NEW STEP, load the node of specified id
        if (nodeid != null && !nodeid.equals("")) {
            loadSolution();
        } // NEW SOLUTION, start a new project as solution to given exercise
        else if (exid != null && !exid.equals("")) {
            newSolution();
        } // SANDBOX MODE, start a new project without an exercise
        else {
            newProject();
        }
    }

    // NEW SOLUTION TO EXERCISE
    public void newSolution() {
        /*
         * umlProject = new UMLProject(); umlProject.addObserver(this);
         */

        umlProject.clear();

        umlProject.setUser(username);

        // SET EXERCISE ID TO THE PASSED PARAMETER
        umlProject.setExid(Integer.valueOf(exid));

        // SET PARENT ID TO 0
        umlProject.setParentid(0);

        umlProject.setMode(Mode.NEW_SOLUTION);
    }

    // STARTS A UML PROJECT IN SANDBOX MODE
    public void newProject() {
        /*
         * if (!closeProject()) { return; }
         * 
         * if(umlProject != null) umlProject.clear(); umlProject = new UMLProject();
         * umlProject.addObserver(this);
         */

        umlProject.clear();
        umlProject.setUser(username);

        // SET EXID to 0 and the PARENT ID also to 0
        umlProject.setExid(0);
        umlProject.setParentid(0);
        umlProject.setMode(Mode.SANDBOX);

        // setFileName("New Project");
        // SystemWideObjectNamePool.getInstance().clear();
        // SystemWideObjectNamePool.getInstance().reload();
    }

    // LOAD STEP TO CREATE NEW STEP TO SOLUTION
    public boolean loadSolution() {
        try {
            Document doc = serverInterface.getNode(Integer.valueOf(nodeid));

            Node resultNode = doc.getElementsByTagName("result").item(0);
            String resultString = resultNode.getTextContent().trim();

            /*
             * if (!resultString.equals("success")) { JOptionPane.showMessageDialog(this,
             * "Requested node does not exist", "Error", JOptionPane.ERROR_MESSAGE);
             * 
             * umlProject.setMode(Mode.SANDBOX); newProject();
             * 
             * return false; }
             */
            boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
            SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);
            checkTreeManager.getSelectionModel().clearSelection();
            messageTree.setModel(null); //
            factsTree.setModel(null); //
            repairButton.setEnabled(false);

            Node nodeTypeNode = doc.getElementsByTagName("nodetype").item(0);
            String nodeType = nodeTypeNode.getTextContent().trim();

            Node exerciseidNode = doc.getElementsByTagName("exerciseid").item(0);
            int exerciseid = Integer.valueOf(exerciseidNode.getTextContent().trim());

            Node titleNode = doc.getElementsByTagName("nodetitle").item(0);
            String title = titleNode.getTextContent().trim();

            Node commentNode = doc.getElementsByTagName("nodedescription").item(0);
            String comment = commentNode.getTextContent().trim();

            /*
             * Node authoridNode = doc.getElementsByTagName("authorid").item(0); int
             * authorid = Integer.valueOf(authoridNode.getTextContent().trim());
             */
            Node authorNameNode = doc.getElementsByTagName("authornickname").item(0);
            String authorName = authorNameNode.getTextContent().trim();

            Node isprivateNode = doc.getElementsByTagName("isprivate").item(0);
            String isprivateString = isprivateNode.getTextContent().trim();
            boolean isprivate;

            if (isprivateString.equals("true")) {
                isprivate = true;
            } else {
                isprivate = false;
            }

            // Get the actual solution node
            Node solutionFileIDNode = doc.getElementsByTagName("solutionfileid").item(0);
            int solutionFileID = Integer.valueOf(solutionFileIDNode.getTextContent().trim());

            Document solutionDocument = serverInterface.getSolutionFileFromFileID(solutionFileID);
            Node solutionNode = solutionDocument.getElementsByTagName("message").item(0);
            String solution = solutionNode.getTextContent().trim();

            umlProject.clear();

            umlProject.loadFromXMLString(solution);

            umlProject.setUser(username);
            umlProject.setExid(exerciseid);

            // PARENT ID OVERRIDING BEHAVIOUR ONLY IF THE PARENTID PARAMETER
            // IS SPECIFIED
            if (parentid != null && !parentid.trim().equals("")) {
                umlProject.setParentid(Integer.valueOf(parentid));
            } else {
                // as before
                umlProject.setParentid(Integer.valueOf(nodeid));
            }

            umlProject.setMode(Mode.NEW_STEP);
            umlProject.setNodeType(nodeType);
            umlProject.setTitle(title);
            umlProject.setComment(comment);

            /*
             * String infoString = "Node Id: " + umlProject.getParentid() +
             * "\nExercise Id: " + umlProject.getExid() + "\nNode Type: " +
             * umlProject.getNodeType() + "\nTitle: " + umlProject.getTitle() +
             * "\nComment: " + umlProject.getComment() + "\nAuthor: " + authorName +
             * "\nIs Private: " + (isprivate?"Yes":"No");
             */

            /*
             * JOptionPane.showMessageDialog(null, infoString, "Solution Information",
             * JOptionPane.INFORMATION_MESSAGE);
             */
            setSaved(true);
            umlProject.addObserver(this);
            umlProject.projectChanged();
            // setFilePath(file);
            // setFileName(file.substring( file.lastIndexOf('\\') + 1));

            // Set the top internal frame (selected) to the one specified by user
            try {
                DiagramInternalFrame frameToBeSelected = umlProject.getDiagramModel(Integer.valueOf(diagramno) - 1)
                        .getFrame();

                // desktopPane.setSelectedFrame(selectedFrame);
                frameToBeSelected.setSelected(true);
            } catch (NumberFormatException nfe) {
                // non-integer string, let the default behaviour continue
                if (diagramno != null && !diagramno.equals("")) {
                    JOptionPane.showMessageDialog(this, "Badly-formed thumbnail number", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IndexOutOfBoundsException ioobe) {
                // non-existing pic number, let the default behaviour continue
                JOptionPane.showMessageDialog(this, "Non-existing thumbnail number", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (PropertyVetoException pve) {
                JOptionPane.showMessageDialog(this, "PropertyVetoException", "Error", JOptionPane.ERROR_MESSAGE);
            }

            SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);

            if (runtimeChecking) {
                SystemWideObjectNamePool.getInstance().reloadRules();
            }

            return true;
        } catch (APICallException ace) {
            JOptionPane.showMessageDialog(this, "There was an API error while loading node", ace.getMessage(),
                    JOptionPane.ERROR_MESSAGE);

            umlProject.setMode(Mode.SANDBOX);
            newProject();

            return false;
        } catch (AuthenticationFailedException afe) {
            JOptionPane.showMessageDialog(this, "User Authentication failed while trying to load node",
                    "Authentication failed", JOptionPane.ERROR_MESSAGE);

            umlProject.setMode(Mode.SANDBOX);
            newProject();

            return false;
        }
    }

    // closes the current project while prompting the user to save changes if
    // necessary
    public boolean closeProject() {
        boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);

        if (!isSaved()) {
            int response = JOptionPane.showConfirmDialog(this, "Do you want to save changes to this project?",
                    "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                saveSolution();
                closeFrames();
                umlProject.clear();
                centralRepository.clear();
                SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
                return true;
            } else if (response == JOptionPane.NO_OPTION) {
                closeFrames();
                umlProject.clear();
                centralRepository.clear();
                SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
                return true;
            } else { // user pressed the cancel option
                SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
                return false;
            }

        } else {
            closeFrames();

            SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
            return true;

        }
    }

    public void saveSolution() {

        // COLLECT ALL DATA TO SAVE
        // Streaming to XML could be delayed after the display of "Save" dialog box
        String solution = umlProject.streamToXMLString();
        String username = umlProject.getUser();
        int parentid = umlProject.getParentid();
        int exid = umlProject.getExid();
        Mode mode = umlProject.getMode();

        // JUST TEMPORARILY INITIALIZED
        String nodeType = "";
        String title = "";
        String comment = "";
        boolean savePrivate = false;

        try {
            // INITIALIZE SOLUTION DATA INPUT PANEL
            Vector statusStrings = getStatusStrings();
            Vector nodeTypeStrings = getNodeTypeStrings();
            SolutionInputPanel solutionInput = new SolutionInputPanel(statusStrings, nodeTypeStrings);

            // solutionInput.setStatus(0);
            if (JOptionPane.showOptionDialog(this, solutionInput, "Your solution info", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null, null) != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this, "Solution saving was canceled!", "Solution was not saved",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            nodeType = solutionInput.getNodeType();
            title = solutionInput.getTitle();
            comment = solutionInput.getComment();

            // THIS IS NOT ONLY BECAUSE THE API EXPECTS A NON-EMPTY TITLE
            // BUT ALSO BECAUSE TITLES HAVE TO HAVE ACTUAL TEXT (NOT JUST WHITESPACES)
            if (title == null || title.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "You need to provide a Title!", "Solution was not saved",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            // THIS IS BECAUSE THE API EXPECTS A NON-EMPTY COMMENT
            if (comment == null || comment.equals("")) {
                comment = " ";
            }

            savePrivate = solutionInput.isPrivate();

            if (mode == Mode.SANDBOX) {
                savePrivate = true;
            }
        } catch (APICallException ace) {
            JOptionPane.showMessageDialog(this, "There was an API error while loading node types", ace.getMessage(),
                    JOptionPane.ERROR_MESSAGE);

            return;
        } catch (AuthenticationFailedException afe) {
            JOptionPane.showMessageDialog(this, "User Authentication failed while trying to load node types",
                    "Authentication failed", JOptionPane.ERROR_MESSAGE);

            return;
        }

        // TRY-CATCH BLOCK WITH THE ACTUAL API CALLS
        // DONE IN TWO PARTS: (1) SAVING NODE and (2) SAVING PIC(S)
        try {
            // (1) SAVING NODE
            Document saveNodeResponse = serverInterface.saveNode(username, exid, parentid, title, comment, nodeType,
                    savePrivate, solution, "");

            Node node = saveNodeResponse.getElementsByTagName("nodeid").item(0);
            int nodeid = Integer.valueOf(node.getTextContent());

            if (!savePrivate) {
                // UPDATE UML project node id
                // IMPORTANT since it becomes the parent of next node
                umlProject.setParentid(nodeid);
                umlProject.setMode(Mode.NEW_STEP);
            }

            /*
             * // DISPLAY SAVING RESULT Node resultNode =
             * saveNodeResponse.getElementsByTagName("result").item(0); String result =
             * resultNode.getTextContent().trim();
             * 
             * if (result.equals("success")) {
             * 
             * }
             */
            // (2) SAVING PICS
            Vector diagramModels = umlProject.getDiagramModels();
            DiagramModel diagramModel;
            DiagramView diagramView;
            Iterator modelsIterator = diagramModels.iterator();
            int number = 0;
            String scr = "";
            String thumb = "";
            Document saveNodeImageResponse;

            while (modelsIterator.hasNext()) {
                diagramModel = (DiagramModel) modelsIterator.next();
                // deselect any selected element, to remove blue outlines from output
                diagramModel.clearSelected();

                diagramView = diagramModel.getFrame().getView();

                scr = ImageExporter.exportToPNGImageString(diagramView);
                thumb = ImageExporter.exportToPNGImageStringByDimensions(diagramView, 80, 80);
                number++;

                saveNodeImageResponse = serverInterface.saveNodeImage(nodeid, number, scr, thumb);
            }

            // DISPLAY SAVING RESULT; AT THIS POINT EVERYTHING MUST HAVE BEEN SUCCESSFUL
            JOptionPane.showMessageDialog(this, "Solution successfully " + (savePrivate ? "saved!" : "shared!"),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            setSaved(true);

        } catch (APICallException ace) {
            JOptionPane.showMessageDialog(this,
                    "There was an API error while " + (savePrivate ? "saving" : "sharing") + " the solution!",
                    ace.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (AuthenticationFailedException afe) {
            JOptionPane.showMessageDialog(this,
                    "User Authentication failed while " + (savePrivate ? "saving" : "sharing") + " the solution!",
                    "Authentication failed", JOptionPane.ERROR_MESSAGE);
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

    public void setSaved(boolean projectSaved) {
        setSaveMenuActionEnabled(!umlProject.isSaved());
        toolbar.setSaveActionEnabled(!umlProject.isSaved());

    }

    public boolean isSaved() {
        if (umlProject == null) {
            return true;
        } else {
            return umlProject.isSaved();
        }
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

    // this method creates a new empty diagram within
    // the appropriate internal frame, depending on the type integer
    public void createNewInternalFrame(int type) {
        String dialogText;
        DiagramModel model;
        switch (type) {
        case DiagramType.SSD:
            dialogText = "System Sequence Diagram Name :";
            break;
        case DiagramType.SD:
            dialogText = "Sequence Diagram Name: ";
            break;
        case DiagramType.CCD:
            dialogText = "Conceptual Class Diagram Name: ";
            break;
        case DiagramType.DCD:
            dialogText = "Design Class Diagram Name: ";
            break;
        default:
            dialogText = "";
        }
        String modelName = JOptionPane.showInputDialog(dialogText);
        if ((modelName != null) && (modelName.length() > 0)) {
            if (type == DiagramType.SSD) {
                model = new SSDModel(modelName, umlProject);

            } else if (type == DiagramType.SD) {
                model = new SDModel(modelName, umlProject);

            } else if (type == DiagramType.CCD) {
                model = new CCDModel(modelName, umlProject);

            } else if (type == DiagramType.DCD) {
                model = new DCDModel(modelName, umlProject);
            } else {
                return;
            }
            model.addObserver(this);
            addInternalFrame(model);
            setSaved(false);
        }
    }

    public void addInternalFrame(DiagramModel model) {
        addInternalFrame(model, null);
    }

    // this method is utilized by other methods trying to embed a diagram model
    // in the appropriate internal frame
    public void addInternalFrame(DiagramModel model, Rectangle R) {
        DiagramInternalFrame f = null;

        /*
         * if ( model instanceof UCDModel ) { } else
         */
        if (model instanceof SSDModel) {
            f = new SSDInternalFrame((SSDModel) model);
        } else if (model instanceof SDModel) {
            f = new SDInternalFrame((SDModel) model);
        } else if (model instanceof CCDModel) {
            f = new CCDInternalFrame((CCDModel) model);
        } else if (model instanceof DCDModel) {
            f = new DCDInternalFrame((DCDModel) model, /* advancedModeRadioButtonMenuItem.isSelected() */ true);
        }

        if (R != null) {
            f.setBounds(R);
            f.getView().setSize((int) R.getWidth(), (int) R.getHeight());
        }

        model.setFrame(f);

        f.addInternalFrameListener(new DiagramInternalFrameListener());
        desktopPane.add(f);
        f.setOpaque(true);
        f.setVisible(true);

        try {
            f.setSelected(true);
        } catch (PropertyVetoException vetoException) {
            vetoException.printStackTrace();
        }

        setSaved(false);
    }

    // closes and removes the given internal frame, together with the diagram
    // model
    public void removeInternalFrame(DiagramInternalFrame frame) {
        frame.getModel().clear();
        umlProject.removeDiagram(frame.getModel());

        frame.dispose();
        desktopPane.remove(frame);
        setSaved(false);
    }

    // returns a list of internal frames having a particular diagram type
    public Vector getInternalFramesOfType(int type) {
        Vector ucdFrames = new Vector();
        Vector ssdFrames = new Vector();
        Vector sdFrames = new Vector();
        Vector ccdFrames = new Vector();
        Vector dcdFrames = new Vector();
        JInternalFrame[] frames = desktopPane.getAllFrames();
        JInternalFrame f;

        for (int i = 0; i < frames.length; i++) {
            f = frames[i];

            /*
             * if ( frame instanceof UCDInternalFrame ) { ucdFrames.add(f); } else
             */
            if (f instanceof SSDInternalFrame) {
                ssdFrames.add(f);
            } else if (f instanceof SDInternalFrame) {
                sdFrames.add(f);
            } else if (f instanceof CCDInternalFrame) {
                ccdFrames.add(f);
            } else if (f instanceof DCDInternalFrame) {
                dcdFrames.add(f);
            }
        }

        if (type == DiagramType.SSD) {
            return ssdFrames;
        } else if (type == DiagramType.SD) {
            return sdFrames;
        } else if (type == DiagramType.CCD) {
            return ccdFrames;
        } else if (type == DiagramType.DCD) {
            return dcdFrames;
        }

        return new Vector();
    }

    // closes all existing internal frames (diagrams) in the application
    public void closeFrames() {
        JInternalFrame[] frames = desktopPane.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            removeInternalFrame((DiagramInternalFrame) frames[i]);
        }
    }

    // this method opens appropriate frames based on a vector of diagram model
    // objects
    public void openFrames(Vector diagramModels) {
        DiagramModel model;
        Iterator iterator = diagramModels.iterator();

        while (iterator.hasNext()) {
            model = (DiagramModel) iterator.next();
            model.addObserver(this);
            addInternalFrame(model);
        }
    }

    // exports the diagram in the selected internal frame to an image file
    /*
     * public void exportImage() { JInternalFrame selectedFrame =
     * desktopPane.getSelectedFrame();
     * 
     * if (selectedFrame != null) { DiagramView view = ((DiagramInternalFrame)
     * selectedFrame).getView();
     * 
     * ImageExporter.exportToImage(view, this); } }
     */
    // sets the size in pixels of the drawing area of the selected internal
    // frame (diagram)
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

    // exits the application while closing the project and prompting the user
    // for saving changes
    public void exitApplication() {
        // if the user has finally decided to close the project and hasn't
        // pressed cancel
        // exit the application
        if (closeProject()) {
            System.exit(0);
        }
    }

    private void jbInit() throws Exception {
    }

    // This inner class listens for events from internal frames.
    // It overrides default behavior when the user closes the frame
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
            // closing a frame removes the diagram from the model
            // closing is only possible from the popup menu "Delete" on the diagram
            // top bar.
            removeInternalFrame((DiagramInternalFrame) event.getSource());
        }
    }

    public void setSaveMenuActionEnabled(boolean enabled) {
        // saveProjectMenuItem.setEnabled(enabled);
    }

    // Inner class ProjectToolBar implements the main toolbar of the application
    private class ProjectToolBar extends JToolBar implements ActionListener {

        private JButton ccdButton;
        private JButton dcdButton;
        private JButton sdButton;
        private JButton ssdButton;
        private JButton exportButton;
        private JButton newButton;
        private JButton openButton;
        private JButton resizeButton;
        private JButton saveAsButton;
        private JButton saveButton;
        private JButton helpButton;
        private JButton forwardEngineerButton;
        private JButton validateSD_DCDButton;
        JButton reloadRulesButton;

        public ProjectToolBar() {

            /*
             * ImageIcon newIcon = new
             * ImageIcon(Application.class.getResource("images/new.gif"));
             * 
             * newButton = new JButton(newIcon); newButton.addMouseListener(new
             * MouseAdapter() { public void mouseEntered(MouseEvent e) {
             * newButton.setBorder(new CompoundBorder(new
             * LineBorder(UIManager.getColor("blue"), 1),new EmptyBorder(4, 4, 4, 4))); }
             * public void mouseExited(MouseEvent e) { newButton.setBorder(new
             * EmptyBorder(5, 5, 5, 5)); } }); newButton.setBorder(new EmptyBorder(5, 5, 5,
             * 5)); newButton.setToolTipText("New Project");
             * newButton.addActionListener(this); add(newButton);
             */
            ImageIcon openIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "open.gif"));
            openButton = new JButton(openIcon);
            openButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    openButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    openButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            openButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            openButton.setToolTipText("Open a Private Solution");
            openButton.addActionListener(this);
            add(openButton);

            ImageIcon saveIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "save.gif"));
            saveButton = new JButton(saveIcon);
            saveButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    saveButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    saveButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            saveButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            saveButton.setToolTipText("Share this Solution in PENCIL");
            saveButton.addActionListener(this);
            add(saveButton);

            // SSD
            ImageIcon ssdIcon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "ssd.gif"));
            ssdButton = new JButton(ssdIcon);
            ssdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    ssdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    ssdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            ssdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            ssdButton.setToolTipText("New Conceptual Class Diagram");
            ssdButton.addActionListener(this);
            addSeparator();
            add(ssdButton);

            // CCD
            ImageIcon ccdIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "ccd.gif"));
            ccdButton = new JButton(ccdIcon);
            ccdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    ccdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    ccdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            ccdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            ccdButton.setToolTipText("New Conceptual Class Diagram");
            ccdButton.addActionListener(this);
            addSeparator();
            add(ccdButton);

            ImageIcon sdIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "sd.gif"));
            sdButton = new JButton(sdIcon);
            sdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    sdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    sdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            sdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            sdButton.setToolTipText("New Sequence Diagram");
            sdButton.addActionListener(this);
            add(sdButton);

            ImageIcon dcdIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "dcd.gif"));
            dcdButton = new JButton(dcdIcon);
            dcdButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    dcdButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    dcdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            dcdButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            dcdButton.setToolTipText("New Design Class Diagram");
            dcdButton.addActionListener(this);
            add(dcdButton);

            /*
             * ImageIcon exportIcon = new
             * ImageIcon(Application.class.getResource("images/export.gif"));
             * 
             * exportButton = new JButton(exportIcon); exportButton.addMouseListener(new
             * MouseAdapter() { public void mouseEntered(MouseEvent e) {
             * exportButton.setBorder(new CompoundBorder(new
             * LineBorder(UIManager.getColor("blue"), 1),new EmptyBorder(4, 4, 4, 4))); }
             * public void mouseExited(MouseEvent e) { exportButton.setBorder(new
             * EmptyBorder(5, 5, 5, 5)); } }); exportButton.setBorder(new EmptyBorder(5, 5,
             * 5, 5)); exportButton.setToolTipText("Export to image");
             * exportButton.addActionListener(this);
             * 
             * add(exportButton);
             */
            ImageIcon resizeIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "resize.gif"));

            resizeButton = new JButton(resizeIcon);
            resizeButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    resizeButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    resizeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            resizeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            resizeButton.setToolTipText("Resize the drawing area");
            resizeButton.addActionListener(this);
            addSeparator();
            add(resizeButton);

            Icon validateSD_DCDIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "sd_dcd.gif"));
            validateSD_DCDButton = new JButton(validateSD_DCDIcon);
            validateSD_DCDButton.setToolTipText("Validate SD against DCD");
            validateSD_DCDButton.addActionListener(this);
            addSeparator();

            ImageIcon reloadIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "reload.gif"));

            reloadRulesButton = new JButton(reloadIcon);
            reloadRulesButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    reloadRulesButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    reloadRulesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });
            reloadRulesButton.setBorder(new EmptyBorder(5, 5, 5, 5));
            reloadRulesButton.addActionListener(this);
            reloadRulesButton.setToolTipText("Reload Rules");
            add(reloadRulesButton);

            addSeparator();

            ImageIcon forwardEngineerIcon = new ImageIcon(
                    Application.class.getResource(Constants.IMAGES_DIR + "code.gif"));
            Image img2 = forwardEngineerIcon.getImage();
            Image imgScaled2 = img2.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            forwardEngineerIcon.setImage(imgScaled2);
            forwardEngineerButton = new JButton(forwardEngineerIcon);
            forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));

            forwardEngineerButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    forwardEngineerButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    forwardEngineerButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });

            forwardEngineerButton.setToolTipText("Generate Code");
            forwardEngineerButton.addActionListener(this);

            add(forwardEngineerButton);

            setBorder(new EtchedBorder(EtchedBorder.LOWERED));

            addSeparator();

            ImageIcon helpIcon = new ImageIcon(Application.class.getResource(Constants.IMAGES_DIR + "help.gif"));
            Image img = helpIcon.getImage();
            Image imgScaled = img.getScaledInstance(-1, 19, Image.SCALE_SMOOTH);
            helpIcon.setImage(imgScaled);
            helpButton = new JButton(helpIcon);
            helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));

            helpButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    helpButton.setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("blue"), 1),
                            new EmptyBorder(4, 4, 4, 4)));
                }

                public void mouseExited(MouseEvent e) {
                    helpButton.setBorder(new EmptyBorder(5, 5, 5, 5));
                }
            });

            helpButton.setToolTipText("Get help on using StudentUML");
            helpButton.addActionListener(this);

            add(helpButton);

            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        }

        public void setSaveActionEnabled(boolean enabled) {
            saveButton.setEnabled(enabled);
        }

        // ProjectToolbar listens for its own events coming from the buttons
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == ssdButton) {
                createNewInternalFrame(DiagramType.SSD);
            } else if (event.getSource() == sdButton) {
                createNewInternalFrame(DiagramType.SD);
            } else if (event.getSource() == ccdButton) {
                createNewInternalFrame(DiagramType.CCD);
            } else if (event.getSource() == dcdButton) {
                createNewInternalFrame(DiagramType.DCD);
            } else if (event.getSource() == newButton) {
                newProject();
            } else if (event.getSource() == openButton) {
                openProject();
            } else if (event.getSource() == saveButton) {
                saveSolution();
                /*
                 * } else if (event.getSource() == saveAsButton) { saveProjectAs();
                 */
                /*
                 * } else if (event.getSource() == exportButton) { exportImage();
                 */
            } else if (event.getSource() == resizeButton) {
                resizeView();
            } /*
               * else if (event.getSource() == validateSD_DCDButton) { validateSD_DCD(); }
               */ else if (event.getSource() == reloadRulesButton) {
                SystemWideObjectNamePool.getInstance().reloadRules();
            } else if (event.getSource() == helpButton) {
                try {
                    URL helpURL = new URL(getCodeBase() + "help/StudentUMLHelp.htm");
                    getAppletContext().showDocument(helpURL, "_blank");
                } catch (MalformedURLException e) {
                    JOptionPane.showMessageDialog(null, "Incorrect URL!", "Incorrect Help URL!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openProject() {
        String browserURL = "http://" + apiURL.replaceAll("api.php", "browser.php?exid=-1");
        Browser b = new Browser(browserURL, serverInterface.currentAuthToken);

        if (b.selected != null) {
            String fExid = null;
            String fNodeid = null;
            boolean isPrivate = false;

            String[] l = b.selected.split("\\?")[1].split("\\&");
            for (int i = 0; i < l.length; i++) {
                System.out.println(l[i]);

                if (l[i].indexOf("exid=") == 0) {
                    fExid = l[i].split("\\=")[1].trim();
                }
                if (l[i].indexOf("nodeid=") == 0) {
                    fNodeid = l[i].split("\\=")[1].trim();
                }
                if (l[i].indexOf("private=") == 0) {
                    isPrivate = l[i].split("\\=")[1].trim().equals("true");
                }
            }

            System.out.println(fExid + "->" + fNodeid);

            /*
             * if (isPrivate) { nodeid = ""; exid = "";
             * 
             * newProject(); umlProject.setMode(Mode.SANDBOX); } else {
             * 
             * nodeid = fNodeid; exid = fExid; if (nodeid != null && !nodeid.equals("")) {
             * loadSolution(); umlProject.setMode(Mode.NEW_STEP); } // NEW SOLUTION, start a
             * new project as solution to given exercise else if (exid != null &&
             * !exid.equals("")) { newSolution(); umlProject.setMode(Mode.NEW_SOLUTION); }
             * // SANDBOX MODE, start a new project without an exercise }
             */
            try {
                Document doc = serverInterface.getNode(Integer.valueOf(fNodeid));

                Node resultNode = doc.getElementsByTagName("result").item(0);
                String resultString = resultNode.getTextContent().trim();

                /*
                 * if (!resultString.equals("success")) { JOptionPane.showMessageDialog(this,
                 * "Requested node does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                 * 
                 * umlProject.setMode(Mode.SANDBOX); newProject();
                 * 
                 * return false; }
                 */
                boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
                SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);
                checkTreeManager.getSelectionModel().clearSelection();
                messageTree.setModel(null); //
                factsTree.setModel(null); //
                repairButton.setEnabled(false);

                // Get the actual solution node
                Node solutionFileIDNode = doc.getElementsByTagName("solutionfileid").item(0);
                int solutionFileID = Integer.valueOf(solutionFileIDNode.getTextContent().trim());

                Document solutionDocument = serverInterface.getSolutionFileFromFileID(solutionFileID);
                Node solutionNode = solutionDocument.getElementsByTagName("message").item(0);
                String solution = solutionNode.getTextContent().trim();

                umlProject.clear();

                umlProject.loadFromXMLString(solution);

                setSaved(true);
                umlProject.addObserver(this);
                umlProject.projectChanged();
                // setFilePath(file);
                // setFileName(file.substring( file.lastIndexOf('\\') + 1));

                SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);

                if (runtimeChecking) {
                    SystemWideObjectNamePool.getInstance().reloadRules();
                }
            } catch (APICallException ace) {
                JOptionPane.showMessageDialog(this, "There was an API error while loading node", ace.getMessage(),
                        JOptionPane.ERROR_MESSAGE);

                umlProject.setMode(Mode.SANDBOX);
                newProject();
            } catch (AuthenticationFailedException afe) {
                JOptionPane.showMessageDialog(this, "User Authentication failed while trying to load node",
                        "Authentication failed", JOptionPane.ERROR_MESSAGE);

                umlProject.setMode(Mode.SANDBOX);
                newProject();
            }

            // loadSolution();
            // umlProject.setMode(Mode.NEW_STEP);
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
            int width = 0, height = 0;

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

            /*
             * public void mouseClicked(MouseEvent e) { JTree tree = (JTree) component;
             * TreePath path = tree.getPathForLocation(e.getX(), e.getY()); if (path !=
             * null) { if ( path.getPathCount() == 3) { Object o =
             * path.getLastPathComponent(); TreeModel model = tree.getModel(); if
             * (model.isLeaf(o)) { // a leaf was clicked; handle it } } } }
             */
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

        /*
         * component.addMouseMotionListener(new MouseMotionAdapter() { public void
         * mouseMoved(MouseEvent e) { int cursor = Cursor.DEFAULT_CURSOR;
         * 
         * JTree tree = (JTree)component; TreePath path =
         * tree.getPathForLocation(e.getX(), e.getY()); if (path != null) { if (
         * path.getPathCount() == 3) { // hovering over a leaf; set the cursor cursor =
         * Cursor.HAND_CURSOR; } }
         * 
         * tree.setCursor(new Cursor(cursor)); } });
         */
    }

    private void showRepairButton(final Component component, final JButton button) {
        component.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // System.out.println(checkTreeManager.getSelectionModel().getSelectionPaths()
                // != null);
                // //TreePath path = ((JTree)component).getSelectionPath();
                // if(checkTreeManager.getSelectionModel().getSelectionPaths() != null) {
                // if ((checkTreeManager.getSelectionModel().getSelectionPaths().length > 0))
                // button.setEnabled(true);
                // else button.setEnabled(false);
                // }
            }

            public void mouseClicked(MouseEvent e) {
                // System.out.println(e.getSource().toString());
                // System.out.println(checkTreeManager.getSelectionModel().getSelectionPaths()
                // != null);
                // TreePath path = ((JTree)component).getSelectionPath();
                if (checkTreeManager.getSelectionModel().getSelectionPaths() != null) // {
                // if ((checkTreeManager.getSelectionModel().getSelectionPaths().length > 0))
                {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
                // }
            }
        });
    }

    // Below methods are used for remembering the tree expansion state for
    // messageTree
    //
    // is path1 descendant of path2
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

    public void setRepairMode(boolean repairMode) {
        this.repairMode = repairMode;
    }

    public boolean isRepairMode() {
        return repairMode;
    }

    public Vector getStatusStrings() throws APICallException, AuthenticationFailedException {
        Vector statusStrings = new Vector();

        // POPULATE THE STATUSES VECTOR
        Document statusDocument = serverInterface.getStatuses();
        NodeList statusNodes = statusDocument.getElementsByTagName("status");

        Node node = null;
        String status = null;

        for (int i = 0; i < statusNodes.getLength(); i++) {
            node = statusNodes.item(i);
            status = node.getTextContent();
            statusStrings.add(status);
        }

        return statusStrings;
    }

    public Vector getNodeTypeStrings() throws APICallException, AuthenticationFailedException {
        Vector nodeTypeStrings = new Vector();

        // POPULATE THE NODE TYPES VECTOR
        Document nodeTypeDocument = serverInterface.getNodeTypes();
        NodeList nodeTypeNodes = nodeTypeDocument.getElementsByTagName("type");

        Node node = null;
        String type = null;

        for (int i = 0; i < nodeTypeNodes.getLength(); i++) {
            node = nodeTypeNodes.item(i);
            type = node.getTextContent();
            nodeTypeStrings.add(type);
        }

        return nodeTypeStrings;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED) {
            System.out.println(c);
            repaint();
            e.consume();
        }
    }
}
