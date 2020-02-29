package edu.city.studentuml.util.validation;

import java.util.Vector;

/**
 *
 * @author
 */
public class ConsistencyCheckerFact {

    private String _className;
    private String _functionName;
    private Vector _arguments;

    public String getClassName() {
        return _className;
    }

    public String getFunctionName() {
        return _functionName;
    }

    public Vector getArguments() {
        return _arguments;
    }

    public ConsistencyCheckerFact(String className, String functionName, Vector arguments) {
        _className = className;
        _functionName = functionName;
        _arguments = arguments;
    }
}
