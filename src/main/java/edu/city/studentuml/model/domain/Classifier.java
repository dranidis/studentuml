package edu.city.studentuml.model.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

//Author: Ervin Ramollari
//Classifier.java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "internalid")

public interface Classifier {

    public String getName();

    public void setName(String n);
}
