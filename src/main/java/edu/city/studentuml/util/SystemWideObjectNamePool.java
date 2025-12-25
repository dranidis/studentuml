package edu.city.studentuml.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

import edu.city.studentuml.util.validation.ConsistencyChecker;
import edu.city.studentuml.util.validation.Rule;
import edu.city.studentuml.view.gui.CollectionTreeModel;

/**
 * SystemWideObjectNamePool keeps maps with objects and their unique names.
 * Unique names are generated every time a new object is added. When reading
 * from an XML file the unique names are the internalid attributes in the XML
 * file.
 * <p>
 * Also responsible for consistency checking (to be documented).
 */
public class SystemWideObjectNamePool {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private static final Logger logger = Logger.getLogger(SystemWideObjectNamePool.class.getName());

    private static SystemWideObjectNamePool instance = null;

    private String ruleFile = null;
    private String selectedRule = null;
    private ConsistencyChecker consistencyChecker = null;
    private boolean runtimeChecking = false;
    private HashSet<String> messageTypes = new HashSet<>();
    private CollectionTreeModel messages = null;
    private CollectionTreeModel facts = null;
    private HashMap<Object, String> objectMap = new HashMap<>();
    private HashMap<String, Object> namedMap = new HashMap<>();
    private String uid;
    private HashMap<String, Color> userColorMap = new HashMap<>();
    private int loading = 0;

    private SystemWideObjectNamePool() {
    }

    public static SystemWideObjectNamePool getInstance() {
        if (instance == null) {
            instance = new SystemWideObjectNamePool();
        }
        return instance;
    }

    public void setRuleFileAndCreateConsistencyChecker(String ruleFile) {
        setRuleFile(ruleFile);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        logger.fine(() -> "PropertyChangeListener added: " + l.toString());
        pcs.addPropertyChangeListener(l);
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
    }

    public void done() {
        logger.finest("DONE");
        loading--;
        if (loading == 0) {
            regenarateRuleSet();
        }
    }

    public boolean isLoading() {
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

    private synchronized void generateRuleSet(HashMap<Object, String> map) {
        messages = new CollectionTreeModel();
        facts = new CollectionTreeModel();

        if (consistencyChecker.checkState(map.keySet(), selectedRule, messageTypes, messages, facts)) {
            selectedRule = null;
            return;
        }

        messages.setName("<html><b>Messages</b></html>");
        messageTypes.forEach(messageType -> {
            int countMessages = messages.getChildCount(messageType);
            messages.replace(messageType, countMessages + " " + messageType + "(s)");
        });

        facts.setName("<html><b>Facts</b>" + " [" + facts.size() + "]</html>");

        logger.fine(() -> "Notifying listeners");
        pcs.firePropertyChange("objectNamePoolChanged", null, this);
    }

    public void reload() {
        loading();
        done();
    }

    public void createNewConsistencyCheckerAndReloadRules() {
        consistencyChecker = new ConsistencyChecker(ruleFile);

        consistencyChecker.setPrologAPI(false);

        reload();

        logger.fine(consistencyChecker.getAllQueriesString());
    }

    @SuppressWarnings("unchecked")
    private synchronized void regenarateRuleSet() {
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
            logger.finest(
                    () -> "ADDED object :" + o.getClass() + " NAMED: " + name + " toString: " + o.toString());
        } else {
            logger.finest(() -> "ALREADY in objectMap :" + o.getClass() + ": " + objectMap.get(o) + " toString: "
                    + o.toString());
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

    /**
     * Called by ObjectFactory newinstance that provides the internalid for the name
     * Usually the object does not exist in the maps (oldName = null) and it behaves
     * like adding the object and its name (internalid) in the maps. used for XML
     * streaming
     *
     * @param object the instance created by ObjectFactory
     * @param name   the internalid from the XML file
     */
    public void renameObject(Object object, String name) {
        // remove the old object and the old name
        String oldName = objectMap.remove(object);
        namedMap.remove(oldName);

        objectMap.put(object, name);
        namedMap.put(name, object);
        logger.finest(() -> "RENAMED object: " + object.getClass() + " from oldname: " + oldName + " to: " + name
                + " toString: " + object.toString());
    }

    public void clear() {
        objectMap = new HashMap<>();
        namedMap = new HashMap<>();
        loading = 0; // Reset loading counter for test isolation
        messageTypes.clear(); // Clear message types for test isolation
    }

    public void setRuleFile(String ruleFile) {
        this.ruleFile = ruleFile;
    }

    public Map<String, Color> getUserColorMap() {
        return userColorMap;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
