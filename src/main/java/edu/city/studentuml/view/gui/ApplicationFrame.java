package edu.city.studentuml.view.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import edu.city.studentuml.codegeneration.CodePreparation;
import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.MyImageIcon;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.RecentFiles;
import edu.city.studentuml.util.Settings;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;

public class ApplicationFrame extends ApplicationGUI {

    private static final Logger logger = Logger.getLogger(ApplicationFrame.class.getName());

    public static final String APPLICATION_NAME = "StudentUML";

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        logger.fine(() -> "Path: " + Settings.getDefaultPath());

        Colors.setFillColor(Settings.getFillColor());
        Colors.setDarkFillColor(Settings.getDarkFillColor());

        ImageIcon icon = new MyImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        logger.finer(() -> "ICON: " + icon);
        frame.setIconImage(icon.getImage());
        createXMLFileChooser();

        umlProject.setUser(Constants.DESKTOP_USER);

        /*
         * when the window opens a New Project appears but it should be considered saved
         * if the user does nothing.
         */
        umlProject.setSaved(true);
        updateFrameTitle();
    }

    private JFileChooser createXMLFileChooser() {
        String pathToOpen = Settings.getDefaultPath();

        JFileChooser xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
        xmlFileChooser.setCurrentDirectory(new File(pathToOpen));
        return xmlFileChooser;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
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

        List<String> errors = new ArrayList<>();
        try {
            errors = umlProject.loadFromXML(fileName);
        } catch (IOException e) {
            logger.finer(e::getMessage);
            JOptionPane.showMessageDialog(null, "The file " + fileName + " cannot be found.", "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            RecentFiles.getInstance().removeRecentFile(fileName);
            menuBar.loadRecentFilesInMenu();
            return;
        } catch (NotStreamable e) {
            logger.warning("File cannot be read (NotStreamable): " + e.getMessage());
            String message = e.getMessage() != null && !e.getMessage().isEmpty()
                    ? "Encountered problems while reading the file " + fileName + ".\n\n" + e.getMessage()
                    : "Encountered some problems while reading the file " + fileName
                            + ". \nContents might not be fully loaded.";
            JOptionPane.showMessageDialog(null, message, "XML file error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NullPointerException e) {
            logger.warning("NullPointerException while loading file: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "The file " + fileName + " has an invalid or corrupted XML structure.\n" +
                            "This may be due to missing required elements or incorrect XML format.\n\n" +
                            "Please check the file format matches StudentUML's expected structure.",
                    "Invalid XML Structure",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (Exception e) {
            logger.warning("Unexpected error while loading file: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An unexpected error occurred while loading the file " + fileName + ".\n" +
                            "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "Loading Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errors.forEach(e -> sb.append(e + "\n"));
            logger.finer(() -> "File cannot be read (NotStreamable): " + sb.toString());
            JOptionPane.showMessageDialog(null,
                    "Encountered some problems while reading the file " + fileName
                            + ". \nContents might not fully loaded.\nElements with errors:\n" + sb.toString(),
                    "XML file error", JOptionPane.ERROR_MESSAGE);
        }

        repositoryTreeView.expandDiagrams();
        // repositoryTreeView.update(null, null); // No longer needed; repositoryTreeView should listen via PropertyChangeListener

        umlProject.setFilepath(fileName);

        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().createNewConsistencyCheckerAndReloadRules();
        }

        setZOrderOfInternalFrames();

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

        if (umlProjectFilePath == null || umlProjectFilePath.equals("")) {
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

    @Override
    public void forwardEngineer() {
        JCheckBox checkBox = new JCheckBox("Update Current Files", false);
        String message = "Do you Want to Generate Code? \n"
                + "Make Sure You Have Created and Saved the Approrpiate\n"
                + "Design (first) and Sequence Diagrams!";
        Object[] params = { message, checkBox };
        // 0 for yes and 1 for no
        int codeGenerationConfirm = JOptionPane.showConfirmDialog(frame, params, "Code Generation",
                JOptionPane.YES_NO_OPTION);
        if (codeGenerationConfirm == 0) {
            CodePreparation codePreparation = new CodePreparation();
            int genFilesCount = codePreparation.generateCode(checkBox.isSelected());
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

}
