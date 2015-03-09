package edu.city.studentuml.util;

// Author: Ervin Ramolari
// ProjectFileIO.java
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.repository.CentralRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

public class ProjectFileIO {

    public static void writeProjectSerialized(CentralRepository cr, Vector diagramModels, String fileName) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
            DiagramModel model;

            // store all project data in one Vector object
            // because reading/writing a single object is easier
            Vector allProject = new Vector();
            allProject.add(cr);
            Iterator diagramsIterator = diagramModels.iterator();
            while (diagramsIterator.hasNext()) {
                model = (DiagramModel) diagramsIterator.next();
                allProject.add(model);
            }
            output.writeObject(allProject);
            output.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static Vector readProjectSerialized(String fileName) {
        Vector allProject = null;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(fileName)));

            allProject = (Vector) input.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        return allProject;
    }

    public static void writeProject(UMLProject umlProject, String fileName) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
            output.writeObject(umlProject);
            output.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static UMLProject readProject(String fileName) {
        UMLProject umlProject = null;

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(fileName)));

            umlProject = (UMLProject) input.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        return umlProject;
    }
}
