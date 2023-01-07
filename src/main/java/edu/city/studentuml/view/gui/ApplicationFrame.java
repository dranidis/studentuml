package edu.city.studentuml.view.gui;

import java.beans.PropertyVetoException;
import java.io.File;
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
    private JFileChooser xmlFileChooser;
    String path = Settings.getDefaultPath();

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        ImageIcon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        frame.setIconImage(icon.getImage());
        xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
        xmlFileChooser.setCurrentDirectory(new File(path));

        umlProject.setUser(Constants.DESKTOP_USER);
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

        int response = xmlFileChooser.showOpenDialog(this);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }

        if (!closeProject()) {
            return;
        }

        String file = xmlFileChooser.getSelectedFile().getAbsolutePath();

        openProjectFile(file);
    }

    public void openProjectFile(String fileName) {
        boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);

        checkTreeManager.getSelectionModel().clearSelection();
        messageTree.setModel(null);//
        factsTree.setModel(null);//
        repairButton.setEnabled(false);

        logger.fine(fileName);

        addToRecentFiles(fileName);

        closingOrLoading = true;

        umlProject.loadFromXML(fileName);

        repositoryTreeView.expandDiagrams();
        repositoryTreeView.update(null, null);

        umlProject.setFilepath(fileName);

        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }

        try {
            if (selectedFrame != null)
                selectedFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        umlProject.setSaved(true);
        closingOrLoading = false;
        updateFrameTitle();
        logger.fine("Opened project");
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

        logger.log(Level.INFO, "Saving file as: {0}", filePath);

        umlProject.streamToXML();
        updateFrameTitle();

        addToRecentFiles(filePath);

        Settings.setDefaultPath(filePath);
    }

    @Override
    public void exportImage() {
        JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

        if (selectedFrame != null) {
            DiagramView view = ((DiagramInternalFrame) selectedFrame).getView();
            ImageExporter.exportToImage(view, this);
        }
    }

    @Override
    public void help() {
        // to be implemented
    }
}
