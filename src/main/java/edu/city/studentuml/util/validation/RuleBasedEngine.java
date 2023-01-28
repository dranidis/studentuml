package edu.city.studentuml.util.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import edu.city.studentuml.view.gui.CollectionTreeModel;


/**
 * A class for performing a high level interface with the prolog engine.
 *
 */
public class RuleBasedEngine {

    private static final Logger logger = Logger.getLogger(RuleBasedEngine.class.getName());

    HashMap<String, Boolean> clauseTable = new HashMap<>();
    private PrologAPI prolog;

    public RuleBasedEngine(PrologAPI prolog) {
        this.prolog = prolog;
        prolog.setFailUnknownPredicate(true);
    }

    public void addClause(String clause) {
        logger.finer(() -> "Adding clause: " + clause);

        if (!clauseTable.containsKey(clause)) {
            modifyDatabase("assert", clause);
            clauseTable.put(clause, true);
        }
    }

    /*
     * currently not called
     */
    public void removeClause(String clause) {
        modifyDatabase("retract", clause);
        if (clauseTable.containsKey(clause)) {
            clauseTable.remove(clause);
        }
    }

    private void modifyDatabase(String action, String clause) {
        String queryString = action + "(" + clause + ").";
        try {
            prolog.query(queryString);
        } catch (Exception e) {
            logger.severe(clause);
            e.printStackTrace();
        }
    }

    public void addClauseTableToFacts(CollectionTreeModel facts) {
        for (String a : clauseTable.keySet()) {
            logger.finest(() -> a + ".");
            facts.add(a);
        }
    }

    public void printSolution(Map<String, Map<String, String>> result) {
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


    /**
     * WE ARE LOOKING FOR ONLY ONE OCCURANCE OF THE RULE
     * IF A RULE FIRES FOR X,Y !!! THEN WE HAVE AN INVALD UML!!!
     * 
     * @param rule
     * @param allSolutions
     * @return
     */
    public synchronized Map<String,  Map<String, ?>> checkRule(String rule, boolean allSolutions) {
        if (!rule.substring(rule.length() - 1).equals(".")) {
            rule = rule + ".";
        }

        try {
            logger.finer("PROLOG Query: " + rule);
            Map<String,  Map<String, ?>> ht = prolog.query(rule);
            if (ht == null) {
                return null;
            }

            Map<String,  Map<String, ?>> results = new HashMap<>();

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
