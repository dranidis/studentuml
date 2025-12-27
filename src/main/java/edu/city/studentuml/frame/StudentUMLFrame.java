package edu.city.studentuml.frame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;

import edu.city.studentuml.view.gui.ApplicationFrame;

public class StudentUMLFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(StudentUMLFrame.class.getName());

    static {
        InputStream stream = StudentUMLFrame.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Parse command-line arguments
        String fileToOpen = parseCommandLineArguments(args);

        // Create the application frame and GUI
        StudentUMLFrame frame = new StudentUMLFrame();
        ApplicationFrame app = new ApplicationFrame(frame);

        // If a file was specified, open it after GUI initialization
        if (fileToOpen != null) {
            if (validateFile(fileToOpen)) {
                // Use SwingUtilities.invokeLater to ensure GUI is fully initialized
                final String finalPath = fileToOpen;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    app.openProjectFile(finalPath);
                });
            } else {
                logger.warning("Cannot open file: " + fileToOpen + " (file not found or not readable)");
            }
        }
    }

    /**
     * Parses command-line arguments to extract the diagram file path.
     * 
     * @param args command-line arguments
     * @return the file path if provided, null otherwise
     */
    static String parseCommandLineArguments(String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return args[0];
    }

    /**
     * Validates that the given file path exists and is readable.
     * 
     * @param filePath path to the file to validate
     * @return true if file exists and is readable, false otherwise
     */
    static boolean validateFile(String filePath) {
        if (filePath == null) {
            return false;
        }

        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }
}
