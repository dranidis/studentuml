package edu.city.studentuml.util.validation;

import java.util.Map;

import ubc.cs.JLog.Foundation.jPrologAPI;

public class JLogPrologAPIAdapter implements PrologAPI {

    private jPrologAPI prolog = new jPrologAPI("");

    private StringBuilder sb = new StringBuilder();

    @Override
    public void setFailUnknownPredicate(boolean b) {
        prolog.setFailUnknownPredicate(b);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String,  Map<String, ?>> query(String q) {
        sb.append(q);
        sb.append("\n");
        return prolog.query(q);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String,  Map<String, ?>> retry() {
        return prolog.retry();
    }

    @Override
    public String getAllQueriesString() {
        return sb.toString();
    }
    
}
