package edu.city.studentuml.view.gui;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;
import java.io.File;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


/**
 *
 * @author draganbisercic
 */
public class ApplicationFrame extends ApplicationGUI {
    
    private Logger logger = Logger.getLogger(ApplicationFrame.class.getName());

    public static String applicationName = "StudentUML";
    private JFileChooser xmlFileChooser;
    Preferences pref= Preferences.userRoot();
    String path = pref.get("DEFAULT_PATH", "");
    

    public ApplicationFrame(StudentUMLFrame frame) {
        super(frame);

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        ImageIcon icon = new ImageIcon(this.getClass().getResource(Constants.IMAGES_DIR + "icon.gif"));
        frame.setIconImage(icon.getImage());
        xmlFileChooser = new JFileChooser();
        xmlFileChooser.setFileFilter(new XMLFileFilter());
        xmlFileChooser.setCurrentDirectory(new File(path));
    }

    @Override
    public void update(Observable observable, Object object) {
        super.update(observable, object);
        updateTitle();
    }
    
    private void updateTitle() {
        String title = applicationName + " - " + umlProject.getName();
        if (!title.endsWith(" *") && !umlProject.isSaved()) {
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
        /**
         * already performed
         */
//        umlProject.becomeObserver();
        /**
         * does not need to observe umlProject
         */
//        umlProject.addObserver(this);

        umlProject.setUser(DESKTOP_USER);
//        String projectName = JOptionPane.showInputDialog("Enter project name: ");
//        if ((projectName != null) && (projectName.length() > 0)) {
//            setFileName(projectName + ".xml");
//        }
        SystemWideObjectNamePool.getInstance().clear();
        SystemWideObjectNamePool.getInstance().reload();
        
        umlProject.setSaved(true);
        setSaveActionState();
//        updateTitle();
        frame.setTitle(applicationName + " - New Project");

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

        /**
         * already performed
         */
//        umlProject.becomeObserver();
        /**
         * ApplicationGUI does not need to observe the umlProject.
         */
//        umlProject.addObserver(this);

        repositoryTreeView.setUMLProject(umlProject);
        umlProject.projectChanged();
        
        setFilePath(file);
//        setFileName(file.substring(file.lastIndexOf('\\') + 1));
        setFileName(file.substring(file.lastIndexOf(File.separatorChar) + 1));

//        /* throws error
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }

//        */

        umlProject.setSaved(true);
        setSaveActionState();
        updateTitle();
        logger.info("Opened project");

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
            umlProject.streamToXML(path);
            setFilePath(path);
//            setFileName(path.substring(path.lastIndexOf('\\') + 1));
            setFileName(path.substring(path.lastIndexOf(File.separatorChar) + 1));
            
            umlProject.setSaved(true);

            setSaveActionState();
            pref.put("DEFAULT_PATH", path);
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
        if (xmlFileChooser.getSelectedFile().exists()) {
            int existsResponse = JOptionPane.showConfirmDialog(null, "Are you sure you want to override existing file?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (existsResponse == JOptionPane.NO_OPTION || existsResponse == JOptionPane.CLOSED_OPTION) {
                return;
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
        
        logger.log(Level.INFO, "Saving file as: {0}", filePath);

        umlProject.getInstance().streamToXML(getFilePath());
        
        umlProject.setSaved(true);
        
        setSaveActionState();
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
