package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//DataType.java
import java.io.Serializable;

public class DataType implements Serializable, Type {

    // definition of some standard data types
    public static final DataType VOID = new DataType("void");
    public static final DataType STRING = new DataType("String");
    public static final DataType LONG = new DataType("long");
    public static final DataType INTEGER = new DataType("int");
    public static final DataType FLOAT = new DataType("float");
    public static final DataType DOUBLE = new DataType("double");
    public static final DataType BYTE = new DataType("byte");
    public static final DataType BOOLEAN = new DataType("boolean");

    private String name;

    public DataType(String n) {
        name = n;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public static String[] getDataTypeNames() {
        String[] standardDatatypeNames = {
            VOID.getName(), STRING.getName(), LONG.getName(), INTEGER.getName(),
            FLOAT.getName(), DOUBLE.getName(), BYTE.getName(), BOOLEAN.getName()};
        return standardDatatypeNames;
    }
}
