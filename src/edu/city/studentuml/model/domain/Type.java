package edu.city.studentuml.model.domain;

//Author: Ervin Ramollari
//Interface Type.java
//This interface serves as a tag indicating that realizing classes can serve as data types,
//which can describe attributes, method parameters, and return types. Implementations include
//classes DataType for basic data types and DesignClass for abstract data types.

/**
 * @author  Kristi
 */
public interface Type {
    public String getName();    // method returning the name of the data type
    public String toString();   // same as getName
}
