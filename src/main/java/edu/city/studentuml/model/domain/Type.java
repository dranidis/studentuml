package edu.city.studentuml.model.domain;

/**
 * This interface serves as a tag indicating that realizing classes can serve as
 * data types, which can describe attributes, method parameters, and return
 * types. Implementations include classes DataType for basic data types and
 * DesignClass for abstract data types.
 * 
 * @author Ervin Ramollari
 * @author Kristi
 */
public interface Type {
    String getName();    // method returning the name of the data type
    String toString();   // same as getName
}
