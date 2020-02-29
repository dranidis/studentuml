package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFileFilter extends FileFilter {

    public static final String DESCRIPTION = "Student UML XML Project Files (*.xml)";
    public static final String EXTENSION = ".xml";

    public String getDescription() {
        return DESCRIPTION;
    }

    public boolean accept(File file) {
        if (file.getName().toLowerCase().endsWith(EXTENSION) || file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
}

