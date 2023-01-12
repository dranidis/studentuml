package edu.city.studentuml.util.validation;

import java.util.Vector;

/**
 * Data class storing a className, a functionName and a vector of arguments.
 * 
 * @author
 */
public class ConsistencyCheckerFact {

    private String className;
    private String functionName;
    private Vector<String> arguments;

    public String getClassName() {
        return className;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Vector<String> getArguments() {
        return arguments;
    }

    public ConsistencyCheckerFact(String className, String functionName, Vector<String> arguments) {
        this.className = className;
        this.functionName = functionName;
        this.arguments = arguments;
    }
}
