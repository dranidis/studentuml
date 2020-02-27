package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//MessageReturnValue.java
import java.io.Serializable;

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
