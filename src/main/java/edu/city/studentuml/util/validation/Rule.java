package edu.city.studentuml.util.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import edu.city.studentuml.util.SystemWideObjectNamePool;

/**
 *
 * @author
 */
public class Rule {

    private static final String RULE_OPENING = "{";
    private static final String RULE_CLOSING = "}";

    private static final Logger logger = Logger.getLogger(Rule.class.getName());

    private String prologExpression = null;
    private String severity = null;
    private String result = "first";
    private String message = null;
    private String ruleName = "";
    private String action = "";
    private String helpurl = null;

    /**
     * The constructor creates a rule from the List of lines
     * parses and remove all the lines until it finds "}"
     *
     */
    public Rule(List<String> lines) {

        ruleName = getRuleName(removeFirst(lines));

        // READ EVERY LINE INSIDE RULE
        for (String line = removeFirst(lines); !line.equals(RULE_CLOSING); line = removeFirst(lines)) {

            // SPLIT AN INSIDE LINE BY WHITESPACES
            StringTokenizer t = new StringTokenizer(line);

            // HEADER takes values 'expression', 'result', 'severity', ...
            String header = t.nextToken();
            // WAIT for ':' character after header to be reached
            while (t.hasMoreTokens() && !t.nextToken().equals(":"));

            // BUILD SENTENCE after ':' character, e.g.
            // "getSDClass(_,SDclass,SD),not(getDCDClass(SDclass)),
            // class(SDclass,CLName),diagram(DCD,DCDname,dcd)"
            StringBuilder sentenceBuilder = new StringBuilder();
            while (t.hasMoreTokens()) {
                sentenceBuilder.append(" " + t.nextToken()); 
            }
            String sentence = sentenceBuilder.toString().trim();

            // invoke Rule method "set"+header, e.g. setexpression, setresult, etc.
            // through JAVA REFLECTION mechanisms
            try {
                Method m = this.getClass().getDeclaredMethod("set" + header, String.class);
                m.invoke(this, sentence);
            } catch (NoSuchMethodException e) {
                logger.severe("ERROR no such method : set" + header);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | SecurityException e) {
                e.printStackTrace();
            } 
        }
    }

    private String getRuleName(String line) {

        StringTokenizer t = new StringTokenizer(line);

        // Read FIRST LINE, which is RULE NAME, before '{' character
        StringBuilder nameBuilder = new StringBuilder();
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            if (token.equals(RULE_OPENING)) {
                break;
            }
            nameBuilder.append(" " + token);
        }

        return nameBuilder.toString().trim();
    }

    public String getName() {
        return ruleName;
    }

    public String getAction() {
        return action;
    }

    private String removeFirst(List<String> v) {
        String first = v.get(0);
        v.remove(0);
        return first;
    }

    /**
     * called by reflection
     * 
     * @param data
     */
    protected void setexpression(String data) {
        prologExpression = data;
    }

    protected String getexpression() {
        return prologExpression;
    }

    /**
     * called by reflection
     * 
     * @param data
     */
    protected void setresult(String data) {
        result = data;
    }

    protected String getresult() {
        return result;
    }

    /**
     * called by reflection
     * 
     * @param data
     */
    protected void setseverity(String data) {
        severity = data;
    }

    /**
     * called by reflection
     * 
     * @param data
     */
    protected void setmessage(String data) {
        message = data;
    }

    /**
     * called by reflection
     * 
     * @param data
     */
    protected void setaction(String data) {
        action = data;
    }

    public String getSeverity() {
        if (severity != null) {
            return severity;
        } else {
            return "unknown";
        }
    }

    public void sethelpurl(String helpurl) {
        this.helpurl = helpurl;
    }

    public String gethelpurl() {
        return helpurl;
    }

    /**
     * nest three methods parse the message returned after a rule has fired
     *
     * ex.
     * for the message: Class %A has no methods
     *
     * will return:
     * Class designclass0 has no methods
     *
     */
    private String getVariableTokenValue(String variableName, Map<String, ?> solutions) {
        variableName = variableName.substring(1);
        Object o = solutions.get(variableName);

        if (o instanceof Vector) {
            String vectorString = "";
            Vector<?> v = (Vector<?>) o;

            for (int i = 0; i < v.size(); i++) {
                vectorString = vectorString + v.get(i).toString();
                if (i < v.size() - 1) {
                    vectorString = vectorString + ",";
                }
            }

            vectorString = "[" + vectorString + "]";

            return vectorString;
        }
        if (o instanceof Integer) {
            return ((Integer) o).toString();
        }
        String varValue = (String) solutions.get(variableName);
        if (varValue == null) {
            return "INVALID_VARIABLE:" + variableName;
        } else {
            return unEscape(varValue);
        }

    }

    public String messageToString(String message, Map<String, ?> solutions) {
        StringTokenizer st = new StringTokenizer(message);
        String resultString = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String tokenValue = token;
            if (token.charAt(0) == '%') {
                tokenValue = getVariableTokenValue(token, solutions);
            }
            resultString = resultString + " " + tokenValue;
        }

        return resultString.trim();
    }

    public String getMessage(Map<String, ?> result) {
        if (message != null) {
            return messageToString(message, result);
        } else {
            return "Uknown message from " + ruleName + " severity : " + getSeverity();
        }
    }

    private String unEscape(String s) {
        if (s == null) {
            return null;
        }
        if (s.startsWith("'")) {
            s = s.substring(1);
        }
        if (s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * runAction is applied only for one action from the action defined for the rules separated by ";"
     * for example if we have action1;action2;...;
     *
     * this method will be invoked for every action by the executeAction method defined later
     *
     * actionName is the actual string representation of the action which is parsed
     * results is a hash table of all the results returned from prolog for that specific rule...
     *
     * for example designClass(A), will be
     *   solution1 : [A = class1], solution2 : [A = class2] etc.
     *   so it is a hash table of vectors as values and strings as keyss
     *
     *  the action is parsed having separated by ( , ) , . , ","
     *  so that we cover actions of kind Object.method(A,B,C).
     *
     *  first we get the "Object" value and store it in the objectInstanceName
     *  then we get the method name "method" in this case and then
     *  we parse all the arguments for the action added in the arguments vector
     *  so it will become [A,B,C]
     *
     *  from the results hash table we get the actual name for the Object action which will be
     *  a string name for the object in question (returned by prolog) residing in the SystemWidePool
     *
     *  for example if we have a rule something(Model,X), and an action Model.doSomething(X)
     *  and this rule is fired for something(dcdmodel1,'x') then Model will become "dcdmodel1"
     *
     *  afterwards we get the instance for the "dcdmodel1" from the SystemWidePool and store it
     *  as objectInstance variable.
     *
     *  the same process applies for the arguments so we create a vector with the values for the arguments
     *  in which we will have Arguments = ['x'];
     *
     *  then we try to execute the method "doSomething" using the arguments from the newly created vector
     *  using RTTI
     *
     */
    private int runAction(String actionName, Map<String, ?> results) {

        StringTokenizer t = new StringTokenizer(actionName, "().,");

        String objectInstanceName = t.nextToken();
        String methodName = t.nextToken();
        Vector<String> arguments = new Vector<>();
        while (t.hasMoreTokens()) {
            arguments.add(unEscape((String) results.get(t.nextToken())));
        }

        objectInstanceName = unEscape((String) results.get(objectInstanceName));
        if (objectInstanceName == null) {
            return 0;
        }

        Object objectInstance = null;

        Vector<Object> objectArguments = new Vector<>();

        synchronized (SystemWideObjectNamePool.getInstance()) {
            objectInstance = SystemWideObjectNamePool.getInstance().getObjectByName(objectInstanceName);
            for (int i = 0; i < arguments.size(); i++) {
                logger.finer(arguments.get(i));
                Object fromPool = SystemWideObjectNamePool.getInstance().getObjectByName(arguments.get(i));
                if (fromPool == null) {
                    fromPool = arguments.get(i);
                }
                objectArguments.add(fromPool);
            }
        }

        if (objectInstance == null) {
            return 0;
        }
        for (int i = 0; i < objectArguments.size(); i++) {
            if (objectArguments.get(i) == null) {
                return 0;
            }
        }

        Object[] methodParameters = objectArguments.toArray();

        try {
            Method[] methods = objectInstance.getClass().getMethods();
            Method m = null;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    m = methods[i];
                    break;
                }
            }
            if (m == null) {
                logger.severe(() -> "Invalid method name " + methodName + " for action in rule '" + ruleName + "'");
                return 0;
            }
            m.invoke(objectInstance, methodParameters);
            return 1;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return 0;
        } 
    }

    /**
     * Calls runAction for every member of the actions which is separated by ";"
     * ex : action1;action2;actionN...
     *
     * runs runAction(action1),runAction(action2).....
     *
     */
    public boolean executeAction(Map<String, ?> results) {
        //FIXME:TO HANDLE AT HIGHEST LEVEL BUT WITHOUT EXCEPTION
        if (action.equals("") || action == null) {
            JOptionPane.showMessageDialog(null, "No repair action defined for rule: " + ruleName, "", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        String[] actions = action.split(";");
        int rezCount = 0;
        for (int i = 0; i < actions.length; i++) {
            rezCount = rezCount + runAction(actions[i], results);
        }

        return rezCount > 0;
    }

}
