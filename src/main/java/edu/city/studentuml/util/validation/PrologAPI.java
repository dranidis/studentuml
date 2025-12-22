package edu.city.studentuml.util.validation;

import java.util.Map;

public interface PrologAPI {
    void setFailUnknownPredicate(boolean b);

    Map<String, Map<String, ?>> query(String q);

    Map<String, Map<String, ?>> retry();

    String getAllQueriesString();
}
