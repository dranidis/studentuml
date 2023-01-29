package edu.city.studentuml.util.validation;

import java.util.Map;


public class PrologAPIMock implements PrologAPI {

    private String query;

    private StringBuilder sb = new StringBuilder();

    public String getAllQueriesString() {
        return sb.toString();
    }

    @Override
    public void setFailUnknownPredicate(boolean b) {
        // empty
    }

    @Override
    public Map<String, Map<String, ?>> query(String q) {
        // mock behaviour
        this.query = q;
        sb.append(q);
        sb.append("\n");
        return null;
    }

    @Override
    public Map<String,  Map<String, ?>> retry() {
        return null;
    }
    
}
