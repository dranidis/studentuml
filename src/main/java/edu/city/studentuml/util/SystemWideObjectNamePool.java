package edu.city.studentuml.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import edu.city.studentuml.util.validation.ConsistencyChecker;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.gui.CollectionTreeModel;

/**
 * SystemWideObjectNamePool keeps maps with objects and their unique names.
 * 
 * Unique names are generated every time a new object is added.
 * 
 * When reading from an XML file the unique names are the internalid attributes
 * in the XML file.
 * <p>
 * Also responsible for consistency checking (to be documented).
 */
public class SystemWideObjectNamePool extends Observable {
    private static final Logger logger = Logger.getLogger(SystemWideObjectNamePool.class.getName());

    private static SystemWideObjectNamePool ref = null;
    private String ruleFile = null;
    private static String selectedRule = null;
    private ConsistencyChecker consistencyChecker = null;
    private boolean runtimeChecking = false;
    private static HashSet<String> messageTypes = new HashSet<String>();
    private static CollectionTreeModel messages = null;
    private static CollectionTreeModel facts = null;
    private HashMap<Object, String> objectMap = new HashMap<>();
    private HashMap<String, Object> namedMap = new HashMap<>();
    public static String uid;
    public static HashMap<String, Color> userColorMap = new HashMap<>();
    public static ReentrantLock drawLock = new ReentrantLock();
    // private String LastUndo = null;
    // private Stack<String> undoBuffer = new Stack<String>();
    // private Stack<String> redoBuffer = new Stack<String>();
    private int loading = 0;

    private SystemWideObjectNamePool() {
    }

    public static SystemWideObjectNamePool getInstance() {
        if (ref == null) {
            ref = new SystemWideObjectNamePool();
        }
        return ref;
    }

    public void init(String ruleFile) {
        setRuleFile(ruleFile);

        consistencyChecker = new ConsistencyChecker(ruleFile);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        logger.fine("OBSERVER added: " + o.toString());
        super.addObserver(o);
    }

    // Get runtime consistency checking
    public boolean isRuntimeChecking() {
        return runtimeChecking;
    }

    // Set runtime consistency checking
    public void setRuntimeChecking(boolean runtimeChecking) {
        this.runtimeChecking = runtimeChecking;
    }

    public CollectionTreeModel getMessages() {
        return messages;
    }

    public CollectionTreeModel getFacts() {
        return facts;
    }

    public void setSelectedRule(String newRule) {
        selectedRule = newRule;
    }

    public Rule getRule(String ruleName) {
        return consistencyChecker.getRule(ruleName);
    }

    public void loading() {
        loading++;
        logger.finer("loading: " + loading);
    }

    public void done() {
        loading--;
        if (loading == 0) {
            reloadrules();
        }
        logger.finer("loading: " + loading);
    }

    public boolean isLoading() {
        logger.finer("loading: " + loading);
        return loading > 0;
    }

    public boolean pushToUndoStack() {
        if (loading > 0) {
            return false;
        }
        loading();

        done();
        return true;
    }

    public void addMessage(String messageType, String ruleName, String messagevalue) {
        messageTypes.add(messageType);
        messages.put(messageType, ruleName);
        messages.put(ruleName, messagevalue);
    }

    public void addFact(String messageType) {
        facts.add(messageType);
    }

    private synchronized void generateRuleSet(HashMap<Object, String> map) {
        messages = new CollectionTreeModel();
        facts = new CollectionTreeModel();

        if (consistencyChecker.checkState(map.keySet(), selectedRule, messageTypes, messages, facts)) {
            selectedRule = null;
            return;
        }

        messages.setName("<html><b>Messages</b></html>");
        Iterator<String> it = messageTypes.iterator();
        while (it.hasNext()) {
            String messageType = it.next();
            int countMessages = messages.getChildCount(messageType);
            messages.replace(messageType, countMessages + " " + messageType + "(s)");
        }

        facts.setName("<html><b>Facts</b>" + " [" + facts.size() + "]</html>");

        logger.fine("Notifying observers: " + this.countObservers());
        setChanged();
        notifyObservers(this);
    }

    public void reload() {
        loading();
        done();
    }

    public void reloadRules() {// FIXME: need reload? (loading/done)
        consistencyChecker = null;
        consistencyChecker = new ConsistencyChecker(ruleFile);
        reload();
    }

    @SuppressWarnings("unchecked")
    private synchronized void reloadrules() {
        if (runtimeChecking) {
            synchronized (this) {
                HashMap<Object, String> h = (HashMap<Object, String>) objectMap.clone();
                generateRuleSet(h);
            }
        }
    }

    /**
     * Generates a unique name with lowercase class name + the next available
     * integer index
     * 
     * @param o the object to be named
     * @return the unique name
     */
    private String generateUniqueName(Object o) {
        String objName = o.getClass().getSimpleName().toLowerCase();
        String tempName = "";
        int index = 0;
        while (true) {
            tempName = objName + index;
            if (objectMap.containsValue(tempName)) {
                index++;
            } else {
                break;
            }
        }
        return tempName;
    }

    // returns object by name
    public Object getObjectByName(String name) {
        return namedMap.get(name);
    }

    // returns number of objects
    public int getCount() {
        return objectMap.size();
    }

    private void objectCountChanged() {
        pushToUndoStack();
    }

    // add an object
    public synchronized void objectAdded(Object o) {
        if (objectMap.get(o) == null) {
            String name = generateUniqueName(o);
            namedMap.put(name, o);
            objectMap.put(o, name);

            objectCountChanged();
            logger.finer("ADDED in objectMap :" + o.getClass() + " named: " + name + " toString: " + o.toString());
        } else {
            logger.finer(
                    "ALREADY in objectMap :" + o.getClass() + ": " + objectMap.get(o) + " toString: " + o.toString());
        }
    }

    // remove an object
    public synchronized void objectRemoved(Object object) {
        namedMap.remove(objectMap.get(object));
        objectMap.remove(object);

        objectCountChanged();
    }

    // returns name of object
    public String getNameForObject(Object o) {
        if (o == null) {
            return "NULL";
        }

        Object thisObject = objectMap.get(o);
        if (thisObject != null) {
            return (String) thisObject;
        }

        return null;
    }

    // used for XML streaming
    /**
     * Called by ObjectFactory newinstance that provides the internalid for the name
     * 
     * Usually the object does not exist in the maps (oldName = null) and it behaves
     * like adding the object and its name (internalid) in the maps.
     *
     * @param object the instance created by ObjectFactory
     * @param name   the internalid from the XML file
     */
    public void renameObject(Object object, String name) {
        // remove the old object and the old name
        String oldName = (String) objectMap.remove(object);
        namedMap.remove(oldName);

        objectMap.put(object, name);
        namedMap.put(name, object);
        logger.finer("RENAMED object: " + object.getClass() + " from oldname: " + oldName + " to: " + name);
    }

    public void clear() {
        objectMap = new HashMap<>();
        namedMap = new HashMap<>();
    }

    public void undo() {
        // if (undoBuffer.size() == 0) return;
        //
        // loading();
        // String XML = undoBuffer.pop();
        // if (LastUndo != null)
        // redoBuffer.push(LastUndo);
        //
        // objectMap.clear();
        // namedMap.clear();
        // UMLProject.getInstance().clear();
        // UMLProject.getInstance().loadFromXMLString(XML);
        // LastUndo = null;
        // done();
    }

    public void redo() {
        // if (redoBuffer.size() == 0) return;
        //
        // loading();
        // String XML = redoBuffer.pop();
        // //if (LastUndo != null)
        // undoBuffer.push(LastUndo);
        // objectMap.clear();
        // namedMap.clear();
        // UMLProject.getInstance().clear();
        // UMLProject.getInstance().loadFromXMLString(XML);
        // LastUndo = null;
        // done();
    }

    public void setRuleFile(String ruleFile) {
        this.ruleFile = ruleFile;
    }
    // private boolean isAlphaNumeric(final String s) {
    // final char[] chars = s.toCharArray();
    // for (int x = 0; x < chars.length; x++) {
    // final char c = chars[x];
    // if ((c >= 'a') && (c <= 'z')) continue; // lowercase
    // if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
    // if ((c >= '0') && (c <= '9')) continue; // numeric
    // return false;
    // }
    // return true;
    // }
    //
    // private String isValidName(String newName) {
    // if (newName.length() == 0) return null;
    // if (isAlphaNumeric(newName)) return newName; else
    // return null;
    // }
}
