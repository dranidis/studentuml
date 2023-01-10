package edu.city.studentuml.model.domain;

import java.io.Serializable;

/**
 * 
 * @author Ervin Ramollari
 */
public class GenericAttribute implements Serializable {

    private String name;

    public GenericAttribute(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }
}
