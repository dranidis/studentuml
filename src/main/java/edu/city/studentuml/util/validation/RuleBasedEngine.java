package edu.city.studentuml.util.validation;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import edu.city.studentuml.view.gui.CollectionTreeModel;
import ubc.cs.JLog.Foundation.jPrologAPI;


/**
 * A class for performing a high level interface with the prolog engine.
 *
 */
public class RuleBasedEngine {

    private static final Logger logger = Logger.getLogger(RuleBasedEngine.class.getName());

    HashMap<String, Boolean> clauseTable = new HashMap<>();
    private jPrologAPI prolog = new jPrologAPI("");

    public RuleBasedEngine() {
        prolog.setFailUnknownPredicate(true);
    }

    public void addClause(String clause) {

        if (!clauseTable.containsKey(clause)) {
            modifyDatabase("assert", clause);
            clauseTable.put(clause, true);
        }
    }

    public void removeClause(String clause) {
        modifyDatabase("retract", clause);
        if (clauseTable.containsKey(clause)) {
            clauseTable.remove(clause);
        }
    }

    private void modifyDatabase(String action, String clause) {
        prolog.query(action + "(" + clause + ").");
    }

    public void printDatabase(CollectionTreeModel facts) {
        Iterator<String> i = clauseTable.keySet().iterator();
        while (i.hasNext()) {
            String a = i.next();
            logger.finest(() -> a + ".");
            //SystemWideObjectNamePool.getInstance().addFact( a);
            facts.add(a);
        }
    }

    public void printSolution(HashMap<String, Map<String, String>> result) {
        if (result != null) {
            Iterator<String> i = result.keySet().iterator();
            logger.finest(() -> "Rule has (" + result.size() + ") solution: ");
            while (i.hasNext()) {
                String solution = i.next();
                Map<String, String> solutionMap = result.get(solution);
                Iterator<String> b = solutionMap.keySet().iterator();
                logger.finest(() -> " " + solution);
                while (b.hasNext()) {
                    String name = b.next();
                    String variableValue = solutionMap.get(name);
                    logger.finest(() -> "        " + name + "->" + variableValue);
                }
            }
        }
    }

    // WE ARE LOOKING FOR ONLY ONE OCCURANCE OF THE RULE
    // IF A RULE FIRES FOR X,Y !!! THEN WE HAVE AN INVALD UML!!!
    public synchronized Hashtable checkRule(String rule, boolean allSolutions) {
        if (!rule.substring(rule.length() - 1).equals(".")) {
            rule = rule + ".";
        }

        try {
            Hashtable ht = prolog.query(rule);
            if (ht == null) {
                return null;
            }

            Hashtable results = new Hashtable();

            int index = 0;
            while (ht != null) {
                String solutionName = "solution" + index;
                results.put(solutionName, ht);
                if (!allSolutions) {
                    break;
                }
                index++;
                ht = prolog.retry();
            }

            return results;

        } catch (Exception E) {
            logger.severe("prolog error -> " + E.getMessage());
            return null;
        }
    }
}
