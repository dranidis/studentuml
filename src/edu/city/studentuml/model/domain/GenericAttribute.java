package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//GenericAttribute.java
import java.io.Serializable;

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
