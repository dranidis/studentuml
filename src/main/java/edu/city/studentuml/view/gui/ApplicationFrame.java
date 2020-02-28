package edu.city.studentuml.view.gui;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.ImageExporter;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.DiagramView;
import java.beans.PropertyVetoException;
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
        
        umlProject.setUser(DESKTOP_USER);
    }

    @Override
    public void update(Observable observable, Object object) {
        super.update(observable, object);
        updateFrameTitle();
    }
    
    private void updateFrameTitle() {
        String title = applicationName + " - " + umlProject.getName();
        if (!umlProject.isSaved()) {
            title = title + " *";
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
        boolean runtimeChecking = SystemWideObjectNamePool.getInstance().isRuntimeChecking();
        SystemWideObjectNamePool.getInstance().setRuntimeChecking(false);

        int response = xmlFileChooser.showOpenDialog(this);
        if (response != xmlFileChooser.APPROVE_OPTION) {
            return;
        }

        if (!closeProject()) {
            return;
        }

        checkTreeManager.getSelectionModel().clearSelection();
        messageTree.setModel(null);//
        factsTree.setModel(null);//
        repairButton.setEnabled(false);
        
        String file = xmlFileChooser.getSelectedFile().getAbsolutePath();
        umlProject.loadFromXML(file);

        repositoryTreeView.expandDiagrams();
        repositoryTreeView.update(null, null);
//        umlProject.projectChanged();
        
        umlProject.setFilepath(file);

        SystemWideObjectNamePool.getInstance().setRuntimeChecking(runtimeChecking);
        if (runtimeChecking) {
            SystemWideObjectNamePool.getInstance().reloadRules();
        }
        
        try {
            selectedFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        umlProject.setSaved(true);
        updateFrameTitle();
        logger.fine("Opened project");
    }

    private String getFilePath() {
        return umlProject.getFilepath();
    }

    @Override
    public void saveProject() {
        String path = umlProject.getFilepath();

        if ((path == null) || path.equals("")) {
            // if no file has yet been chosen, prompt via method saveProjectAs
            saveProjectAs();
        } else {
            umlProject.streamToXML();

            pref.put("DEFAULT_PATH", path);
        }
    }

    @Override
    @SuppressWarnings("static-access")
    public void saveProjectAs() {
        xmlFileChooser.setSelectedFile(new File(umlProject.getFilename()));
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
        String filePath = xmlFileChooser.getSelectedFile().getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(XMLFileFilter.EXTENSION)) {
            filePath += XMLFileFilter.EXTENSION;
        }

        umlProject.setFilepath(filePath);
        
        logger.log(Level.INFO, "Saving file as: {0}", filePath);

        umlProject.streamToXML();
        
        
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
    }
}
