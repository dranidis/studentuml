package edu.city.studentuml.util.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import edu.city.studentuml.view.gui.CollectionTreeModel;

public class ConsistencyChecker {
    
    private static final String SIMPLIFICATION_STR = "$";
    private static final String FACT_STR = ":-";
    private static final char COMMENT_CHAR = '#';

    private static final Logger logger = Logger.getLogger(ConsistencyChecker.class.getName());

    private RuleBasedSystemGenerator rbsg = new RuleBasedSystemGenerator();
    protected RuleBasedEngine rbs = new RuleBasedEngine();
    protected Map<String, Vector<ConsistencyCheckerFact>> factTemplates = new HashMap<>();
    protected Vector<String> simplifications = new Vector<>();
    protected List<Rule> rules = new ArrayList<>();

    public ConsistencyChecker(String location) {
        logger.finer(() -> "Loading rules from: " + location);
        loadRules(getNotCommentedLinesFromURL(location));
        logger.fine(() -> "Consistency checker initialized. " + rules.size() + " rules loaded, " + factTemplates.size() + " fact templates.");
    }

    /**
     * Returns a vector of lines ignoring the commented and the empty lines.
     *
     */
    private List<String> getNotCommentedLinesFromURL(String urlLocation) {
        List<String> lines = new ArrayList<>();

        try {
            URL url = new URL(urlLocation);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String str;
            while ((str = in.readLine()) != null) {
                str = str.trim();
                if (str.length() == 0 || str.charAt(0) == COMMENT_CHAR) {
                    continue;
                }
                lines.add(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // load rules one by one
    private void loadRules(List<String> lines) {

        // while the vector is non-empty
        while (!lines.isEmpty()) {

            String line = lines.get(0);

            if (line.split(" ")[0].equals(FACT_STR)) {
                lines.remove(0);
                parseFactTemplate(line);
            } else if (line.split(" ")[0].equals(SIMPLIFICATION_STR)) {
                lines.remove(0);
                line = line.substring(1).trim();
                simplifications.add(line);
            } else {
                rules.add(new Rule(lines));
            }
        }
    }

    private void parseFactTemplate(String fact) {

        StringTokenizer t = new StringTokenizer(fact);
        t.nextToken();
        String className = t.nextToken();
        String prologFact = t.nextToken();

        StringTokenizer t1 = new StringTokenizer(prologFact, "(),");
        String functionName = null;
        Vector<String> arguments = new Vector<>();

        while (t1.hasMoreTokens()) {
            String token = t1.nextToken();
            if (functionName == null) {
                functionName = token;
            } else {
                arguments.add(token);
            }
        }

        ConsistencyCheckerFact factObject = new ConsistencyCheckerFact(className, functionName, arguments);

        Vector<ConsistencyCheckerFact> classFacts = factTemplates.get(className);
        if (classFacts == null) {
            classFacts = new Vector<>();
            factTemplates.put(className, classFacts);
        }

        classFacts.add(factObject);
    }

    public Map<String, Vector<ConsistencyCheckerFact>> getFactTemplates() {
        return factTemplates;
    }

    public Rule getRule(String ruleName) {

        Optional<Rule> first = rules.stream().filter(rule -> rule.getName().equals(ruleName)).findFirst();

        if (first.isPresent()) {
            return first.get();
        } else {
            return null;
        }

   }

    // must not throw exceptions!!
    /**
     * Creates new ruleBasedEngine (from prolog) asserts all the facts in to it
     * (the facts are generated from the fact template explained above) and then
     * for every rule that is defined in rules.txt, parsed and stored in the rules vector
     * it executes those rules
     *
     */
    public boolean checkState(Set<Object> objects, String executeRule, Set<String> messageTypes, CollectionTreeModel messages, CollectionTreeModel facts) {
        rbs = new RuleBasedEngine();

        Vector<String> factsSet = new Vector<>();

        objects.forEach(o -> rbsg.addRules(o, o.getClass(), factsSet, getFactTemplates()));

        // factsSet.forEach(f -> logger.finer("FACT: " + f));


        factsSet.forEach(f -> rbs.addClause(f));

        rbs.addClauseTableToFacts(facts);

        simplifications.forEach(f -> rbs.addClause("(" + f + ")"));

        // rbs.addClauseTableToFacts(facts);

        for (Rule rule : rules) {

            String res = "all";
            Hashtable rez = rbs.checkRule(rule.getexpression(), res.equals(rule.getresult()));

            if (rez != null) {
                Iterator<String> solutionIterator = rez.keySet().iterator();
                while (solutionIterator.hasNext()) {
                    String solutionName = solutionIterator.next();

                    messageTypes.add(rule.getSeverity());
                    messages.put(rule.getSeverity(), rule.getName());
                    messages.put(rule.getName(), rule.getMessage((Hashtable) rez.get(solutionName)));

                    if ((rule.getName().equals(executeRule) || "*".equals(executeRule))
                            && rule.executeAction((Hashtable) rez.get(solutionName))) {
                        return true;
                    }

                }

                if (rule.getSeverity().equals("failure")) {
                    break;
                }
            }
        }

        return false;
    }
}
