package edu.city.studentuml.model.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//Author: Ervin Ramollari
//Classifier.java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
public interface Classifier {

    public String getName();

    public void setName(String n);
}
