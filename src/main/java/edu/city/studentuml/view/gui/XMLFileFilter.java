package edu.city.studentuml.view.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFileFilter extends FileFilter {

    public static final String DESCRIPTION = "Student UML XML Project Files (*.xml)";
    public static final String EXTENSION = ".xml";

    public String getDescription() {
        return DESCRIPTION;
    }

    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(EXTENSION) || file.isDirectory();
    }
}

