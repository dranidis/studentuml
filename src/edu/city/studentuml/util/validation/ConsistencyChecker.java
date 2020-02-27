package edu.city.studentuml.util.validation;

import edu.city.studentuml.view.gui.CollectionTreeModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class ConsistencyChecker {
    
    Logger logger = Logger.getLogger(ConsistencyChecker.class.getName());

    private RuleBasedSystemGenerator rbsg = new RuleBasedSystemGenerator();
    protected RuleBasedEngine rbs = new RuleBasedEngine();
    protected HashMap factTemplates = new HashMap();
    protected Vector<String> simplifications = new Vector<String>();
    protected Vector rules = new Vector();

    public ConsistencyChecker(String location) {
        //loadRules("rules/rules.txt");
        loadRules(location);
        logger.finer(location + " (ConsistencyChecker constructor)");
        logger.fine("Consistency checker initialized " + rules.size() + " rules loaded, " + factTemplates.size() + " fact templates.");
    }

    /*
     * stores the rules.txt in a vector of lines removing the commented lines out
     *
     */
    private void loadRules(String location) {

        try {
            URL url = new URL(location);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String str;
            Vector program = new Vector();
            while ((str = in.readLine()) != null) {
                str = str.trim();
                if (str.length() == 0) {
                    continue;
                }
                if (str.charAt(0) == '#') {
                    continue;
                }
                program.add(str);
            }
            in.close();
            loadProgram(program);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // load rules one by one
    private void loadProgram(Vector v) {

        // while the vector is non-empty
        while (v.size() > 0) {

            String line = ((String) v.get(0)).trim();

            if (line.length() > 0) {
                if (line.split(" ")[0].equals(":-")) {
                    v.remove(0);
                    parseFactTemplate(line);
                    continue;
                }
                if (line.split(" ")[0].equals("$")) {
                    v.remove(0);
                    line = line.substring(1).trim();
                    simplifications.add(line);
                    continue;
                }
            }

            Rule rule = new Rule(v);

            rules.add(rule);
        }
    }

    private void parseFactTemplate(String fact) {

        StringTokenizer t = new StringTokenizer(fact);
        t.nextToken();
        String className = t.nextToken();
        String prologFact = t.nextToken();

        StringTokenizer t1 = new StringTokenizer(prologFact, "(),");
        String functionName = null;
        Vector arguments = new Vector();

        while (t1.hasMoreTokens()) {
            String token = t1.nextToken();
            if (functionName == null) {
                functionName = token;
            } else {
                arguments.add(token);
            }
        }

        ConsistencyCheckerFact factObject = new ConsistencyCheckerFact(className, functionName, arguments);

        Vector classVector = (Vector) factTemplates.get(className);
        if (classVector == null) {
            classVector = new Vector();
            factTemplates.put(className, classVector);
        }

        classVector.add(factObject);
    }

    public HashMap getFactTemplates() {
        return factTemplates;
    }

    public Rule getRule(String ruleName) {
        Iterator iterator = rules.iterator();
        Rule rule = null;

        while (iterator.hasNext()) {
            rule = (Rule) iterator.next();

            if (rule.getName().equals(ruleName)) {
                return rule;
            }
        }

        return null;
    }

    // must not throw exceptions!!
    /*
     * Creates new ruleBasedEngine (from prolog) asserts all the facts in to it
     * (the facts are generated from the fact template explained above) and then
     * for every rule that is defined in rules.txt, parsed and stored in the rules vector
     * it executes those rules
     *
     */
    public boolean checkState(Set objects, String executeRule, HashSet messageTypes, CollectionTreeModel messages, CollectionTreeModel facts) {
        //FIXME: za site go povik. so exRu *
        rbs = new RuleBasedEngine();

        Vector factsSet = new Vector();
        Iterator it = objects.iterator();

        while (it.hasNext()) {
            Object o = it.next();
            rbsg.addRules(o, o.getClass(), factsSet, getFactTemplates());
        }

        for (int j = 0; j < factsSet.size(); j++) {
            rbs.addClause((String) factsSet.get(j));
        }

        for (int j = 0; j < simplifications.size(); j++) {
            rbs.addClause("(" + simplifications.get(j) + ")");
        }

        rbs.printDatabase(facts);

        for (int i = 0; i < rules.size(); i++) {
            Rule r = (Rule) rules.get(i);

            String res = "all";
            Hashtable rez = rbs.checkRule(r.getexpression(), res.equals(r.getresult()));

            if (rez != null) {
                Iterator solutionIterator = rez.keySet().iterator();
                while (solutionIterator.hasNext()) {
                    String solutionName = (String) solutionIterator.next();

                    messageTypes.add(r.getSeverity());
                    messages.put(r.getSeverity(), r.getName());
                    messages.put(r.getName(), r.getMessage((Hashtable) rez.get(solutionName)));

                    if (r.getName().equals(executeRule) || "*".equals(executeRule)) {
                        if (r.executeAction((Hashtable) rez.get(solutionName))) {
                            //factsSet = null;
                            return true;
                        }
                    }
                }

                if (r.getSeverity().equals("failure")) {
                    break;
                }
            }
        }

        //factsSet = null;
        return false;
    }
}
