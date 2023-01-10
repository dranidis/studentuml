package edu.city.studentuml.model.domain;

import java.io.Serializable;

/**
 * 
 * @author Ervin Ramollari
 */
public class MessageReturnValue implements Serializable {

    private String name;

    public MessageReturnValue(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
