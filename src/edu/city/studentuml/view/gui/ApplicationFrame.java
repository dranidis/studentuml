package edu.city.studentuml.view.gui;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;
import java.io.File;
import java.util.Observable;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author draganbisercic
 */
public class ApplicationFrame extends ApplicationGUI {

    public static String applicationName = "StudentUML";
    private JFileChooser xmlFileChooser;
    Preferences pref= Preferences.userRoot();
    String path = pref.get("DEFAULT_PATH", "");
    

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        ImageIcon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        frame.setIconImage(icon.getImage());
        xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
        xmlFileChooser.setCurrentDirectory(new File(path));
    }

    @Override
    public void update(Observable observable, Object object) {
        super.update(observable, object);

        String title = applicationName + " - " + umlProject.getName();
        if (!title.endsWith(" *") & !umlProject.isSaved()) {
            title = title + " *";
        }
        frame.setTitle(title);
    }

    @Override
    public void renameProject() {
        if (umlProject == null) {
            return;
        }
        String projectName = JOptionPane.showInputDialog("Enter project name: ");
        if ((projectName != null) && (projectName.length() > 0)) {
            projectName += ".xml";
            if (getFilePath() != null && !getFilePath().equalsIgnoreCase("")) {
                String newFilePath = getFilePath().replace(getFileName(), projectName);
                saveProject();
            }
            setFileName(projectName);
        }
    }

    @Override
    public void newProject() {
        if (!closeProject()) {
            return;
        }

        if (umlProject != null) {
            umlProject.clear();
        }
        //umlProject = new UMLProject();
        umlProject.becomeObserver();
        umlProject.addObserver(this);
        umlProject.setUser(DESKTOP_USER);
        String projectName = JOptionPane.showInputDialog("Enter project name: ");
        if ((projectName != null) && (projectName.length() > 0)) {
            setFileName(projectName + ".xml");
        }
        SystemWideObjectNamePool.getInstance().clear();
        SystemWideObjectNamePool.getInstance().reload();
        SystemWideObjectNamePool.umlProject = umlProject;
    }

    private void setFileName(String name) {
        umlProject.setFilename(name);
        umlProject.setName(name);
        frame.setTitle(applicationName + " - " + name);
    }

    public String getFileName() {
        return umlProject.getFilename();
    }

    @Override
    public void openProject() {
        openProjectFromFile();
    }

    private boolean openProjectFromFile() {
        boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);

        int response = xmlFileChooser.showOpenDialog(this);
        if (response != xmlFileChooser.APPROVE_OPTION) {
            return false;
        }

        if (!closeProject()) {
            return false;
        }

        checkTreeManager.getSelectionModel().clearSelection();
        messageTree.setModel(null);//
        factsTree.setModel(null);//
        repairButton.setEnabled(false);
        
        String file = xmlFileChooser.getSelectedFile().getAbsolutePath();
        
        umlProject.loadFromXML(file);
        
        setSaved(true);
        umlProject.becomeObserver();
        umlProject.addObserver(this);
        repositoryTreeView.setUMLProject(umlProject);
        umlProject.projectChanged();
        
        setFilePath(file);
        setFileName(file.substring(file.lastIndexOf('\\') + 1));
        /* throws error
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }
        */
        return true;
    }

    private void setFilePath(String path) {
        umlProject.setFilepath(path);
    }

    private String getFilePath() {
        return umlProject.getFilepath();
    }

    @Override
    public void saveProject() {
        String path = getFilePath();

        if ((path == null) || path.equals("")) {
            // if no file has yet been chosen, prompt via method saveProjectAs
            saveProjectAs();
        } else {
            umlProject.getInstance().streamToXML(path);
            setFilePath(path);
            setFileName(path.substring(path.lastIndexOf('\\') + 1));
            setSaved(true);
        }
    }

    @Override
    @SuppressWarnings("static-access")
    public void saveProjectAs() {
        //System.out.println(SystemWideObjectNamePool.getInstance().isRuntimeChecking());
        xmlFileChooser.setSelectedFile(new File(getFileName()));
        int response = xmlFileChooser.showSaveDialog(this);
        if (response != xmlFileChooser.APPROVE_OPTION) {
            return;
        }
        boolean exists = (xmlFileChooser.getSelectedFile().exists());
        if (exists) {
            int existsResponse = JOptionPane.showConfirmDialog(null, "Are you sure you want to override existing file?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (existsResponse == JOptionPane.NO_OPTION || existsResponse == JOptionPane.CLOSED_OPTION) {
                xmlFileChooser.setSelectedFile(new File(getFileName()));
            }
        }
        String fileName = xmlFileChooser.getSelectedFile().getName();
        String filePath = xmlFileChooser.getSelectedFile().getAbsolutePath();

        if (!fileName.toLowerCase().endsWith(XMLFileFilter.EXTENSION)) {
            fileName += XMLFileFilter.EXTENSION;
            filePath += XMLFileFilter.EXTENSION;
        }

        setFilePath(filePath);
        setFileName(fileName);

        umlProject.getInstance().streamToXML(getFilePath());
        setSaved(true);
        pref.put("DEFAULT_PATH", filePath);
    }

    //ZASTO STRING
    @Override
    public void exportImage() {
        JInternalFrame selectedFrame = desktopPane.getSelectedFrame();

        if (selectedFrame != null) {
            DiagramView view = ((DiagramInternalFrame) selectedFrame).getView();

            //ImageExporter.exportToPNGImageString(view);
            ImageExporter.exportToImage(view, this);
        }
    }

    @Override
    public void help() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
