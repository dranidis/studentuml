package edu.city.studentuml.model.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * @author Ervin Ramollari
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "internalid")
public interface Classifier {

    String getName();
    void setName(String n);
}
