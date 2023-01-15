package edu.city.studentuml.model.domain;

import java.io.Serializable;

/**
 * 
 * @author Ervin Ramollari
 */
public abstract class RoleClassifier implements Serializable {

    protected Classifier classifier;
    protected String name;

    protected RoleClassifier(String n, Classifier c) {
        name = n;
        classifier = c;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier c) {
        classifier = c;
    }

    // Return the standard text representation of role classifiers in SDs,
    // in the form "instance : Classifier"
    public String toString() {
        String toString = "";

        if (name != null) {
            toString += name;
        }

        toString += " : ";

        if ((classifier != null) && (classifier.getName() != null)) {
            toString += classifier.getName();
        }

        return toString;
    }
}
