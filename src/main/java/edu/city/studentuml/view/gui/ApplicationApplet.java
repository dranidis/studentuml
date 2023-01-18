package edu.city.studentuml.view.gui;

import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.city.studentuml.applet.APICallException;
import edu.city.studentuml.applet.AuthenticationFailedException;
import edu.city.studentuml.applet.Browser;
import edu.city.studentuml.applet.SolutionInputPanel;
import edu.city.studentuml.applet.StudentUMLApplet;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.DiagramView;

/**
 *
 * @author draganbisercic
 */
public class ApplicationApplet extends ApplicationGUI {

    public ApplicationApplet(StudentUMLApplet applet) {
        super(applet);

        // NEW STEP, load the node of specified id
        if (applet.getNodeid() != null && !applet.getNodeid().equals("")) {
            // loadSolution();
        } // NEW SOLUTION, start a new project as solution to given exercise
        else if (applet.getExid() != null && !applet.getExid().equals("")) {
            // newSolution();
        } // SANDBOX MODE, start a new project without an exercise
        else {
            // newProject();
        }
    }

    // NE VALJA
    // LOAD STEP TO CREATE NEW STEP TO SOLUTION
    public boolean loadSolution() {
        try {
            Document doc = applet.getServerInterface().getNode(Integer.valueOf(applet.getNodeid()));

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

            Document solutionDocument = applet.getServerInterface().getSolutionFileFromFileID(solutionFileID);
            Node solutionNode = solutionDocument.getElementsByTagName("message").item(0);
            String solution = solutionNode.getTextContent().trim();

            umlProject.clear();

            umlProject.loadFromXMLString(solution);

            umlProject.setUser(applet.getUsername());
            umlProject.setExid(exerciseid);

            // PARENT ID OVERRIDING BEHAVIOUR ONLY IF THE PARENTID PARAMETER
            // IS SPECIFIED
            if (applet.getParentid() != null && !applet.getParentid().trim().equals("")) {
                umlProject.setParentid(Integer.valueOf(applet.getParentid()));
            } else {
                // as before
                umlProject.setParentid(Integer.valueOf(applet.getNodeid()));
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

            umlProject.addObserver(this);
            umlProject.projectChanged();
            // setFilePath(file);
            // setFileName(file.substring( file.lastIndexOf('\\') + 1));

            // Set the top internal frame (selected) to the one specified by user
            try {
                DiagramInternalFrame frameToBeSelected = umlProject
                        .getDiagramModel(Integer.valueOf(applet.getDiagramno()) - 1).getFrame();

                // desktopPane.setSelectedFrame(selectedFrame);
                frameToBeSelected.setSelected(true);
            } catch (NumberFormatException nfe) {
                // non-integer string, let the default behaviour continue
                if (applet.getDiagramno() != null && !applet.getDiagramno().equals("")) {
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
                SystemWideObjectNamePool.getInstance().createNewConsistencyCheckerAndReloadRules();
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

    // NEW SOLUTION TO EXERCISE
    public void newSolution() {
        umlProject.clear();
        umlProject.setUser(applet.getUsername());

        // SET EXERCISE ID TO THE PASSED PARAMETER
        umlProject.setExid(Integer.valueOf(applet.getExid()));

        // SET PARENT ID TO 0
        umlProject.setParentid(0);

        umlProject.setMode(Mode.NEW_SOLUTION);
    }

    // STARTS A UML PROJECT IN SANDBOX MODE
    @Override
    public void newProject() {
        umlProject.clear();
        umlProject.setUser(applet.getUsername());

        // SET EXID to 0 and the PARENT ID also to 0
        umlProject.setExid(0);
        umlProject.setParentid(0);
        umlProject.setMode(Mode.SANDBOX);
    }

    @Override
    public void openProject() {
        String browserURL = "http://" + applet.getApiURL().replaceAll("api.php", "browser.php?exid=-1");
        Browser b = new Browser(browserURL, applet.getServerInterface().currentAuthToken);

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
                Document doc = applet.getServerInterface().getNode(Integer.valueOf(fNodeid));

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

                Document solutionDocument = applet.getServerInterface().getSolutionFileFromFileID(solutionFileID);
                Node solutionNode = solutionDocument.getElementsByTagName("message").item(0);
                String solution = solutionNode.getTextContent().trim();

                umlProject.clear();

                umlProject.loadFromXMLString(solution);

                umlProject.addObserver(this);
                umlProject.projectChanged();
                // setFilePath(file);
                // setFileName(file.substring( file.lastIndexOf('\\') + 1));

                SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);

                if (runtimeChecking) {
                    SystemWideObjectNamePool.getInstance().createNewConsistencyCheckerAndReloadRules();
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

    @Override
    public void saveProject() {
        saveSolution();
    }

    private void saveSolution() {
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
            Document saveNodeResponse = applet.getServerInterface().saveNode(username, exid, parentid, title, comment,
                    nodeType, savePrivate, solution, "");

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

                saveNodeImageResponse = applet.getServerInterface().saveNodeImage(nodeid, number, scr, thumb);
            }

            // DISPLAY SAVING RESULT; AT THIS POINT EVERYTHING MUST HAVE BEEN SUCCESSFUL
            JOptionPane.showMessageDialog(this, "Solution successfully " + (savePrivate ? "saved!" : "shared!"),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

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

    private Vector getStatusStrings() throws APICallException, AuthenticationFailedException {
        Vector statusStrings = new Vector();

        // POPULATE THE STATUSES VECTOR
        Document statusDocument = applet.getServerInterface().getStatuses();
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

    private Vector getNodeTypeStrings() throws APICallException, AuthenticationFailedException {
        Vector nodeTypeStrings = new Vector();

        // POPULATE THE NODE TYPES VECTOR
        Document nodeTypeDocument = applet.getServerInterface().getNodeTypes();
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

    @Override
    public void saveProjectAs() {
    }

    @Override
    public void exportImage() {
    }

    @Override
    public void help() {
        String rulename = messageTree.getSelectionPath().getLastPathComponent().toString();

        Rule rule = SystemWideObjectNamePool.getInstance().getRule(rulename);
        String helpString = (rule == null) ? null : rule.gethelpurl();

        try {
            URL url = new URL(helpString);
            applet.getAppletContext().showDocument(url, "_blank");
        } catch (MalformedURLException mue) {
            JOptionPane.showMessageDialog(null, "No help URL defined or wrong URL", "Wrong URL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void openProjectFile(String fileName) {
        // TODO Auto-generated method stub
        
    }
}
