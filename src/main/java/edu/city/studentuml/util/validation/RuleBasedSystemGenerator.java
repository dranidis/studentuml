package edu.city.studentuml.util.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import edu.city.studentuml.util.SystemWideObjectNamePool;

public class RuleBasedSystemGenerator {

    private static final Logger logger = Logger.getLogger(RuleBasedSystemGenerator.class.getName());

    /**
     * Tries to create facts from the fact template list (stored in the template HashMap)
     * the templateHashMap is of the following structure:
     *
     * <p>
     *     ClassName -> Vector(fact1,fact2...) , fact is of type ConsistencyCheckerFacts
     * <p>
     * ex:
     *     DiagramModel -> Vector(belongsTo(..),...).
     * <p>
     *
    *
     * ex:
     *
     * <code>addRules(dcdmodel1,DCDModel,Output,TemplateList(from above))</code>
     *
     * <p>
     * First the method checks whether there are any facts for the DCDModel class (from the templates hashmap)
     *
     * <p>
     * If there aren't any facts for that particular class, the same method (addRules) is called for the super class
     *
     *     <code>addRules(dcdmodel1,DiagramModel,Output,TemplateList).s (DiagramModel = DCDModel.getSuper()s</code>
     *<p>
     * Afterwards if there is a list of facts for that specific class (in this case, DiagramModel) all the facts
     * are returned in a vector factsforClass...
     * <p>
     *     in this case the addFromVector method is called for any member of the factsforClass passing the
     *     object as a instance to that method and using the fact member of (factsforClass) getArguments, as an argument
     *     to the addFromVector method
     *
     *
      * 
      * @param object is a instance for which we will check whether there are fact templates
      * @param templateClass is the class for which we will check whether there is a fact template
      * @param factList
      * @param template
      */
    public void addRules(Object object, Class templateClass, Vector<String> factList, Map<String, Vector<ConsistencyCheckerFact>> template) {

        String className = templateClass.getSimpleName();
        Vector<ConsistencyCheckerFact> factsforClass = template.get(className);

        if (factsforClass == null) {
            // no fact for this class, check the parents....
            // TODO : stop executing after inheritance in classes in this package...
            if (templateClass.getSuperclass() != null) {
                Class parentClass = templateClass.getSuperclass();
                addRules(object, parentClass, factList, template);
            }
            return;
        }

        for (int i = 0; i < factsforClass.size(); i++) {
            ConsistencyCheckerFact fact = factsforClass.get(i);
            addFromVector(object, fact.getFunctionName(), factList, fact.getArguments());
            Class parentClass = templateClass.getSuperclass();
            addRules(object, parentClass, factList, template);
        }
    }

    /**
     * The core method for converting fact templates into actual facts for any
     * objects that might exist in the repository (SystemWIdeObjectPool).
     * <p>
     * For every argument in the arguments vector it does the following:
     * <p>
     * checks whether the argument is a simple argument "justname" or a compound
     * argument "something.something"
     * <p>
     * nameArray[] split(".")
     * <p>
     * If it is a compound argument it tries to take a vector using the getter
     * method for example getGraphicalElements for the instance object will return a
     * vector
     * <p>
     * if the result actually is a vector then the same process is repeated (this
     * method) for every member of the vector
     * <p>
     * belongsTo(this,getGraphicalElements.this) will be asserted as many times as
     * there are graphical elements in the instance objects
     * <p>
     * if this is a simple argument then:
     * <p>
     * it checks whether it is a string argument i.e. "Something" in which case it
     * is added as string constant to the new arguments list just 'Something'
     * <p>
     * it checks whether it is enclosed in ' single quotes in which case the actual
     * result will be enclosed in quotes so for example 'getName' will become
     * 'theName' got from the instance of the object
     * <p>
     * otherwise the actual argument is just added to the new list (as if it was an
     * instance object) for example dcddiagram1 (which name is taken from the
     * SystemWide..... later in the add method)
     *
     * @param object       is an instance from the pool...
     * @param functionName is the name of the template i.e. belongsTo
     * @param factList     is the output that will be used by the add() method...
     *                     i.e. the place where the actual facts will be added,
     *                     because for one object we might have more than one fact
     * @param arguments    is a list of "string" names from the fact templates i.e.
     *                     belongsTo(a1,a2), a1 is "this" a2 is
     *                     "getGraphicalElements.this"
     */
    private void addFromVector(Object object, String functionName, Vector<String> factList, Vector<String> arguments) {
        Vector objects = new Vector<>();
        for (int i = 0; i < arguments.size(); i++) {
            if ((arguments.get(i)).split("[.]").length == 2) {

                String[] nameArray = (arguments.get(i)).split("[.]");
                String vectorName = nameArray[0];
                String objectName = nameArray[1];

                Vector v = (Vector) getter(object, vectorName);

                if (v != null) {
                    for (int j = 0; j < v.size(); j++) {
                        Vector newArguments = new Vector<>();
                        for (int z = 0; z < arguments.size(); z++) {
                            if (z == i) {
                                newArguments.add(getter(v.get(j), objectName));
                            } else {
                                String arg = arguments.get(z);
                                boolean enclosed = false;
                                if (arg.startsWith("'") && arg.endsWith("'")) {
                                    enclosed = true;
                                    arg = arg.substring(1, arg.length() - 1);
                                }
                                if (arg.split("[.]").length == 2) {
                                    String[] subArray = arg.split("[.]");
                                    if (subArray[0].equals("this")) {
                                        String value = (String) getter(v.get(j), subArray[1]);
                                        if (enclosed) {
                                            value = "'" + value + "'";
                                        }
                                        newArguments.add(value);
                                    } else {
                                        logger.severe("not implemented!!!");
                                    }
                                } else {
                                    newArguments.add(getter(object, (String) arguments.get(z)));
                                }
                            }
                        }
                        add(functionName, factList, newArguments.toArray());
                    }
                }

                return;
            }

            String arg = arguments.get(i);
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
                objects.add(arg);
                continue;
            }

            boolean enclosed = false;
            if (arg.startsWith("'") && arg.endsWith("'")) {
                enclosed = true;
                arg = arg.substring(1, arg.length() - 1);
            }
            Object val = getter(object, arg);
            if (val instanceof String) {
                String value = (String) getter(object, arg);
                if (enclosed) {
                    value = "'" + value + "'";
                }
                val = value;
            }
            objects.add(val);
        }
        add(functionName, factList, objects.toArray());
    }

    /**
     * objects is a list of object instances got from the rules.txt facts, for example
     * DiagramModel belongsTo(this,getGraphicalElements.this)
     *
     * objects will be (theDiagramModel, and a graphicalElement from that diagram model)
     * output : will be the output for the string fact
     * example : belongsTo(dcdmodel1,classgr1).
     *
     * name is the name of the fact i.e. "belongsTo"
     *
     */
    private void add(String name, Vector output, Object[] objects) {
        String result = name + "(";
        boolean invalid = false;
        for (int i = 0; i < objects.length; i++) {
            String value = null;
            if (objects[i] == null) {
                value = "null";
            }
            if (value == null) {
                value = SystemWideObjectNamePool.getInstance().getNameForObject(objects[i]);
                if (value == null) {
                    value = (String) getter(objects[i], "getName");
                    if (value == null) {
                        value = objects[i].toString();
                        if (value.contains(".")) {
                            value = null;
                        }
                        //System.out.println(value);
                    }
                }
            }
            if (value == null) {
                value = "";
            }
            if (value.equals("null")) {
                value = "";
            }
            if (value.equals("")) {
                invalid = true;
            }
            result = result + value;
            if (i < objects.length - 1) {
                result = result + " , ";
            }
        }
        if (invalid) {
            //System.out.println("Invalid statement : " + result + ")");
        } else {
            output.add(result + ")");
        }
    }

    /**
     * getter is a wrapper for the rtti usage
     * 
     * @param instance   is an instance of any object that might be called via
     *                   getter
     * @param methodName is any method name that might exist in the instance except
     *                   if the methodname is "this" in which case the instance of
     *                   the object is returned....
     * @return the result of the method invocation or instance if methodName is "this"
     */
    private Object getter(Object instance, String methodName) {
        if (methodName.equals("this")) {
            return instance;
        }
        try {
            Method m = instance.getClass().getMethod(methodName);
            return m.invoke(instance);
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
                | InvocationTargetException e) {
            return null;
        }
    }
}
