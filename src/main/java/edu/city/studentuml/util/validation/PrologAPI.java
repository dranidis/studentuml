package edu.city.studentuml.util.validation;

import java.util.Map;

public interface PrologAPI {
        public void setFailUnknownPredicate(boolean b);
        public Map<String,  Map<String, ?>> query(String q);
        public Map<String,  Map<String, ?>> retry();
		public String getAllQueriesString();
}
