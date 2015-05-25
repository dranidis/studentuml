package edu.city.studentuml.view.gui;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;

/**
 *
 * @author draganbisercic
 */
public class ApplicationFrame extends ApplicationGUI {

    public static String applicationName = "StudentUML";
    private JFileChooser xmlFileChooser;

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        ImageIcon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        frame.setIconImage(icon.getImage());
        xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
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
        setFileName("New Project");
        SystemWideObjectNamePool.getInstance().clear();
        SystemWideObjectNamePool.getInstance().reload();
        SystemWideObjectNamePool.umlProject = umlProject;
        umlProject.setSaved(true);
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

        umlProject.setSaved(true);
        umlProject.becomeObserver();
        umlProject.addObserver(this);
        repositoryTreeView.setUMLProject(umlProject);
        umlProject.projectChanged();

        setFilePath(file);
        setFileName(file.substring(file.lastIndexOf('\\') + 1));

        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }
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
            umlProject.setSaved(true);
        }
    }

    @Override
    @SuppressWarnings("static-access")
    public void saveProjectAs() {
        //System.out.println(SystemWideObjectNamePool.getInstance().isRuntimeChecking());
        int response = xmlFileChooser.showSaveDialog(this);
        if (response != xmlFileChooser.APPROVE_OPTION) {
            return;
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
        umlProject.setSaved(true);
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
