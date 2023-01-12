package edu.city.studentuml.util.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
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

    private static final Logger logger = Logger.getLogger(Rule.class.getName());

    private String prologExpression = null;
    private String severity = null;
    private String result = "first";
    private String message = null;
    private String ruleName = "";
    private String action = "";
    private String helpurl = null;
    //private ConsistencyChecker owner = null;

    /**
     * The constructor creates a rule from the text file passed in v
     * only parses all the lines until it finds "}"
     *
     */
    public Rule(Vector<String> v) {

        //owner = aowner;
        //ruleName = "unnamed_rule_" + rulesSize;

        StringTokenizer t = new StringTokenizer(get(v));
        //String token = t.nextToken();

        // Read FIRST LINE, which is RULE NAME, before '{' character
        ruleName = "";
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            if (token.equals("{")) {
                break;
            }
            ruleName = ruleName + " " + token;
        }

        ruleName = ruleName.trim();

        //if (!token.equals("{")) {
        //ruleName = token;
        //}

        // READ EVERY LINE INSIDE RULE
        while (true) {
            String data = get(v);
            if (data.equals("}")) {
                break;
            }

            // SPLIT AN INSIDE LINE BY WHITESPACES
            t = new StringTokenizer(data);

            // HEADER takes values 'expression', 'result', 'severity', ...
            String header = t.nextToken();
            // WAIT for ':' character after header to be reached
            while (t.hasMoreTokens() && !t.nextToken().equals(":"));

            // BUILD SENTENCE after ':' character, e.g.
            // "getSDClass(_,SDclass,SD),not(getDCDClass(SDclass)),
            // class(SDclass,CLName),diagram(DCD,DCDname,dcd)"
            String sentence = "";
            while (t.hasMoreTokens()) {
                sentence = sentence + " " + t.nextToken();
            }
            sentence = sentence.trim();


            // invoke Rule method "set"+header, e.g. setexpression, setresult, etc.
            // through JAVA REFLECTION mechanisms
            try {
                Method m = this.getClass().getDeclaredMethod("set" + header, new Class[]{String.class});
                try {
                    m.invoke(this, new Object[]{sentence});
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
                logger.severe("ERROR no such method : set" + header);
            }
        }
    }

    public String getName() {
        return ruleName;
    }

    private String peek(Vector v) {
        return (String) v.get(0);
    }

    public String getAction() {
        return action;
    }

    private String get(Vector v) {
        String result = peek(v);
        v.remove(0);
        return result;
    }

    protected void setexpression(String data) {
        prologExpression = data;
    }

    protected String getexpression() {
        return prologExpression;
    }

    protected void setresult(String data) {
        result = data;
    }

    protected String getresult() {
        return result;
    }

    protected void setseverity(String data) {
        severity = data;
    }

    protected void setmessage(String data) {
        message = data;
    }

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
    private String getVariableTokenValue(String variableName, Hashtable solutions) {
        variableName = variableName.substring(1);
        Object o = solutions.get(variableName);

        if (o instanceof Vector) {
            String vectorString = "";
            Vector v = (Vector) o;

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

    public String messageToString(String message, Hashtable solutions) {
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

    public String getMessage(Hashtable result) {
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
    private int runAction(String actionName, Hashtable results) {

        StringTokenizer t = new StringTokenizer(actionName, "().,");

        String objectInstanceName = t.nextToken();
        String methodName = t.nextToken();
        Vector<String> arguments = new Vector<String>();
        while (t.hasMoreTokens()) {
            arguments.add(unEscape((String) results.get(t.nextToken())));
        }

        objectInstanceName = unEscape((String) results.get(objectInstanceName));
        if (objectInstanceName == null) {
            return 0;
        }

        Object objectInstance = null;

        Vector<Object> objectArguments = new Vector<Object>();

        synchronized (SystemWideObjectNamePool.getInstance()) {
            objectInstance = SystemWideObjectNamePool.getInstance().getObjectByName(objectInstanceName);
            for (int i = 0; i < arguments.size(); i++) {
                System.out.println(arguments.get(i));
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

        //FIXME: we don't need this??
        Class[] classArray = new Class[objectArguments.size()];
        for (int i = 0; i < methodParameters.length; i++) {
            classArray[i] = methodParameters[i].getClass();
        }

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
                System.out.println("Invalid method name " + methodName + " for action in rule '" + ruleName + "'");
                return 0;
            }
            //Method m = objectInstance.getClass().getMethod(methodName, classArray);
            m.invoke(objectInstance, methodParameters);
            return 1;
        } catch (SecurityException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        } catch (InvocationTargetException e) {
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
    public boolean executeAction(Hashtable results) {
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
    
//    public Hashtable check() {
//    	String res = "all";
//    	Hashtable rez = owner.rbs.checkRule(prologExpression,res.equals(result));
//        return rez ;
//    }
}
