package edu.city.studentuml.view.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.RecentFiles;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;

public class ApplicationFrame extends ApplicationGUI {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ApplicationFrame.class.getName());

    public static final String APPLICATION_NAME = "StudentUML";

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        logger.fine(() -> "Path: " + Settings.getDefaultPath());

        ImageIcon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        frame.setIconImage(icon.getImage());
        createXMLFileChooser();

        umlProject.setUser(Constants.DESKTOP_USER);
    }

    private JFileChooser createXMLFileChooser() {
        String pathToOpen = Settings.getDefaultPath();

        JFileChooser xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
        xmlFileChooser.setCurrentDirectory(new File(pathToOpen));
        return xmlFileChooser;
    }

    @Override
    public void update(Observable observable, Object object) {
        super.update(observable, object);
        updateFrameTitle();
    }

    private void updateFrameTitle() {
        if (closingOrLoading)
            return;

        logger.finest("Updating title: saved: " + umlProject.isSaved());

        String title = APPLICATION_NAME + " - " + umlProject.getName();
        if (!umlProject.isSaved()) {
            title += " (not saved)";
        }
        frame.setTitle(title);
    }

    @Override
    public void newProject() {
        if (!closeProject()) {
            return;
        }

        umlProject.createNewProject();
        updateFrameTitle();
    }

    @Override
    public void openProject() {

        JFileChooser xmlFileChooser = createXMLFileChooser();
        int response = xmlFileChooser.showOpenDialog(this);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String file = xmlFileChooser.getSelectedFile().getAbsolutePath();
        openProjectFile(file);
    }

    public void openProjectFile(String fileName) {
        if (!closeProject()) {
            return;
        }
        
        boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);

        checkTreeManager.getSelectionModel().clearSelection();
        messageTree.setModel(null);//
        factsTree.setModel(null);//
        repairButton.setEnabled(false);

        logger.fine(fileName);

        addToRecentFiles(fileName);

        closingOrLoading = true;

        try {
            umlProject.loadFromXML(fileName);
        } catch (IOException e) {
            logger.finer(e::getMessage);
            JOptionPane.showMessageDialog(null, "The file " + fileName + " cannot be found.", "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            RecentFiles.getInstance().removeRecentFile(fileName);
            menuBar.loadRecentFilesInMenu();
            return;
        }

        repositoryTreeView.expandDiagrams();
        repositoryTreeView.update(null, null);

        umlProject.setFilepath(fileName);

        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }

        umlProject.setSaved(true);
        closingOrLoading = false;
        updateFrameTitle();
        logger.fine("Opened project");

        Settings.setDefaultPath(fileName);

    }

    private void addToRecentFiles(String fileName) {
        RecentFiles.getInstance().addRecentFile(fileName);
        menuBar.loadRecentFilesInMenu();
    }

    @Override
    public void saveProject() {
        String umlProjectFilePath = umlProject.getFilepath();

        if ((umlProjectFilePath == null) || umlProjectFilePath.equals("")) {
            // if no file has yet been chosen, prompt via method saveProjectAs
            saveProjectAs();
        } else {
            umlProject.streamToXML();
            updateFrameTitle();

            Settings.setDefaultPath(umlProjectFilePath);
        }
    }

    @Override
    @SuppressWarnings("static-access")
    public void saveProjectAs() {
        JFileChooser xmlFileChooser = createXMLFileChooser();

        xmlFileChooser.setSelectedFile(new File(umlProject.getFilename()));
        xmlFileChooser.setDialogTitle("Save as");
        int response = xmlFileChooser.showSaveDialog(this);
        if (response != xmlFileChooser.APPROVE_OPTION) {
            return;
        }
        if (xmlFileChooser.getSelectedFile().exists()) {
            int existsResponse = JOptionPane.showConfirmDialog(null, "Are you sure you want to override existing file?",
                    "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (existsResponse == JOptionPane.NO_OPTION || existsResponse == JOptionPane.CLOSED_OPTION) {
                return;
            }
        }
        String filePath = xmlFileChooser.getSelectedFile().getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(XMLFileFilter.EXTENSION)) {
            filePath += XMLFileFilter.EXTENSION;
        }

        umlProject.setFilepath(filePath);

        logger.log(Level.FINE, "Saving file as: {0}", filePath);

        umlProject.streamToXML();
        updateFrameTitle();

        addToRecentFiles(filePath);

        Settings.setDefaultPath(filePath);
    }

    @Override
    public void exportImage() {
        JInternalFrame paneSelectedFrame = desktopPane.getSelectedFrame();

        if (paneSelectedFrame != null) {
            DiagramView view = ((DiagramInternalFrame) paneSelectedFrame).getView();
            ImageExporter.exportToImage(view, this);
        }
    }

    @Override
    public void help() {
        // to be implemented
    }
}
