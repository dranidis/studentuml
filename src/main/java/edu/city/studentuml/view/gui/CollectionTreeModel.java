package edu.city.studentuml.view.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 */
public class CollectionTreeModel implements Serializable, TreeModel {

    List root;
    Map allNodes;
    Map parents;
    EventListenerList eventList = new EventListenerList();
    String name;

    /**
     * Inner class that provides the ability to enumerate over this collection.
     */
    class MyEnumeration<E> implements Enumeration<E> {

        Enumeration<E> enumer;
        Iterator<E> itr;
        boolean skip = true;

        MyEnumeration(Iterator<E> iterator) {
            itr = iterator;
        }

        MyEnumeration(Enumeration<E> e) {
            enumer = e;
        }

        public boolean hasMoreElements() {
            if (enumer != null) {
                return enumer.hasMoreElements();
            }
            return itr.hasNext();
        }

        public E nextElement() {
            E obj;
            if (enumer != null) {
                obj = enumer.nextElement();
                enumer.nextElement();  //Drop child collection.
            } else {
                obj = itr.next();
                if (skip) {
                    itr.next();
                }
            }
            return obj;
        }
    }

    /**
     */
    public CollectionTreeModel() {
        root = new ArrayList<>();
        allNodes = new HashMap<>(10);
        parents = new HashMap<>(10);
    }

    /** Adds new elements to the root of the tree.  If the element
     *  is already in the tree, it is moved to the root of the tree.
     */
    public void add(Object key) {
        moveKeyToExtension(key, root);
    }

    /**
     * Implement TreeModel
     */
    @Override
     public void addTreeModelListener(TreeModelListener l) {
        eventList.add(TreeModelListener.class, l);
    }

    /**
     */
    private void buildSubTree(CollectionTreeModel result, Object node) {
        Enumeration children = get(node);
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            result.put(node, child);
            buildSubTree(result, child);
        }
    }

    /**
     * Does the object exist in this collection.
     */
    public boolean contains(Object key) {
        if (key == this) {
            return true;
        }
        return allNodes.containsKey(key);
    }

    /** Return an enumeration of the entire contents of collection.
     */
    public Enumeration elements() {
        Iterator itr = ((HashMap) ((HashMap) allNodes).clone()).keySet().iterator();
        MyEnumeration enumer = new MyEnumeration(itr);
        enumer.skip = false;
        return enumer;
    }
    //End Implement TreeModel

    /**
     */
    protected void fireInsertedEvent(TreeModelEvent e) {
        Object[] listeners = eventList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * e.path() returns the path the parent of the changed node(s).
     * e.childIndices() returns the index(es) of the changed node(s).
     */
    protected void fireTreeNodesChanged(TreeModelEvent e) {
        Object[] listeners = eventList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     *  Returns the children at the specified key.
     */
    public Enumeration get(Object key) {
        if (key == this) {
            key = null;
        }
        if (key == null) {
            return new MyEnumeration(root.iterator());
        }
        List target = getListFor(key);
        if (target == null) {
            return new Vector().elements();
        }
        return new MyEnumeration(target.iterator());
    }

    /**
     */
    public Object getChild(Object parent, int index) {
        List children = null;
        if (parent == null || parent == this) {
            children = root;
        } else {
            children = getListFor(parent);
        }
        if (children == null) {
            return null;
        }
        if (children.size() < (index * 2) + 1) {
            return null;
        }
        return children.get((index * 2));
    }

    /**
     */
    public int getChildCount(Object parent) {
        if (parent == this) {
            parent = null;
        }
        int size = 0;
        if (parent == null) {
            size = root.size();
        } else {
            List target = getListFor(parent);
            if (target == null) {
                return 0;
            }
            size = target.size();
        }
        if (size == 0) {
            return size;
        }
        return size / 2;
    }

    /**
     */
    private List getChildrenCollection(Object key, List from) {
        int idx = from.indexOf(key);
        return (List) from.get(idx + 1);
    }

    /**
     * @param parent If null, look at root values.
     * @return int Index of the child. -1 if child not found at parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        List children = null;
        if (parent == null || parent == this) {
            children = root;
        } else {
            children = getListFor(parent);
        }
        if (children == null) {
            return -1;
        }
        int idx = children.indexOf(child);
        if (idx > -1) {
            return idx / 2;
        }
        return -1;
    }

    /**
     */
    public Object getParent(Object key) {
        return parents.get(key);
    }

    public Object getRoot() {
        return this;
    }

    /**
     */
    public CollectionTreeModel getSubTree(Object key) {
        List target = (List) allNodes.get(key);
        if (target == null) {
            return new CollectionTreeModel();
        }

        CollectionTreeModel result = new CollectionTreeModel();
        Enumeration children = get(key);
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            result.add(child);
            buildSubTree(result, child);
        }
        return result;
    }

    /**
     */
    private List getListFor(Object key) {
        List target = (List) allNodes.get(key);
        if (target == null) {
            return null;
        }
        return getChildrenCollection(key, target);
    }

    public void sortRoot() {
        //List target = (List)allNodes.get(key);
        if (root == null) {
            return;
        }
        //List list = getChildrenCollection(key, target);
        List strList = new ArrayList();

        Iterator i = root.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof String) {
                strList.add(o);
            }
        }

        //System.out.println(list.get( 0).getClass().toString());

        Collections.sort(strList);
        allNodes.clear();
        Iterator it = strList.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            add(o);
        }

        //if(target == null) return null;
        //return getChildrenCollection(key, target);
    }

    /**
     */
    public boolean isEmpty() {
        return allNodes.isEmpty();
    }

    /**
     * Do we have any children at the node.
     */
    public boolean isEmpty(Object key) {
        List target = getListFor(key);
        return target == null ? true : target.isEmpty();
    }

    /**
     */
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }
    /*  The following exist for testing this class */

    /**
     * Does the work of maintaining our view of the tree.
     */
    private synchronized void moveKeyToExtension(Object key, List dest) {
        List target = (List) allNodes.get(key);
        allNodes.put(key, dest);
        if (target == null) {
            dest.add(key);
            dest.add(new ArrayList());

            int[] vals = new int[1];
            vals[0] = dest.indexOf(key) / 2;
            Object[] child = new Object[1];
            child[0] = key;
            TreeModelEvent e = new TreeModelEvent(this, pathTo(key), vals, child);
            fireInsertedEvent(e);
        } else {
            if (target == dest) {
                return;
            }
            List oldContents = getChildrenCollection(key, target);
            int idx = target.indexOf(key);
            target.remove(idx);
            target.remove(idx);
            dest.add(key);
            dest.add(oldContents);
        }
    }

    /**
     * Path to the parent of key.  At minimum this will contain the
     * root node.
     */
    private Object[] pathTo(Object key) {
        List v = new ArrayList();
        Object parent = getParent(key);
        while (parent != null) {
            v.add(parent);
            parent = getParent(parent);
        }
        Object[] result = new Object[v.size() + 1];
        result[0] = this;
//      System.out.println(this);
        for (int i = v.size() - 1; i > -1; i--) {
            result[i + 1] = v.get(i);
//          System.out.println("i: " + (i + 1) + result[i + 1]);
        }
        return result;
    }

    /**
     * Remove the object 'key' and all children of key.
     */
    public void prune(Object key) {
        List target = (List) allNodes.get(key);
        if (target == null) {
            return;
        }
        Enumeration children = get(key);
        while (children.hasMoreElements()) {
            prune(children.nextElement());
        }
        allNodes.remove(key);
        List childs = getChildrenCollection(key, target);
        target.remove(key);
        target.remove(childs);
        parents.remove(key);
    }

    /** Puts the new elment after the existing element.  If the existing
     * element is not in the tree it is just added at the root level.
     * If the new elment is already in the tree, it and it's children are
     * moved to the new elements child.
     */
    public synchronized void put(Object key, Object node) {
        List target = (List) allNodes.get(key);
        List content = null;
        if (target == null) {
            target = root;
            allNodes.put(key, root);
            root.add(key);
            content = new ArrayList();
            root.add(content);
        }
        parents.put(node, key);
        if (content == null) {
            content = getChildrenCollection(key, target);
        }
        moveKeyToExtension(node, content);
    }

    /**
     */
    public void refreshPath(TreePath path) {
        int[] vals = new int[1];
        Object key = path.getLastPathComponent();
        vals[0] = getIndexOfChild(getParent(key), key);
        Object[] child = new Object[1];
        child[0] = key;
        TreeModelEvent e = new TreeModelEvent(this, pathTo(key), vals, child);
        fireTreeNodesChanged(e);
    }

    /**
     * Remove the object 'key' and promote all of it's children to have
     * the same parent that 'key' had.
     */
    public synchronized void remove(Object key) {
        Enumeration contents = get(key);
        if (contents == null) {
            return;
        }
        List original = (List) allNodes.get(key);
        Object originalParent = parents.get(key);
        while (contents.hasMoreElements()) {
            Object childKey = contents.nextElement();
            moveKeyToExtension(childKey, original);
            if (originalParent == null) {
                parents.remove(childKey);
            } else {
                parents.put(childKey, originalParent);
            }
        }
        if (original != null) {
            List target = getChildrenCollection(key, original);
            original.remove(key);
            original.remove(target);
        }
        allNodes.remove(key);
        parents.remove(key);
    }

    /**
     */
    public void removeTreeModelListener(TreeModelListener l) {
        eventList.remove(TreeModelListener.class, l);
    }

    /**
     */
    public synchronized void replace(Object key, Object with) {
        List target = (List) allNodes.get(key);
        if (target == null) {
            return;
        }
        if (with == null) {
            remove(key);
            return;
        }
        int idx = target.indexOf(key);
        target.set(idx, with);
        allNodes.put(with, target);
        allNodes.remove(key);
        Object parent = parents.get(key);
        parents.remove(key);
        if (with != null && parent != null) {
            parents.put(with, parent);
        }
    }

    /**
     */
    public void setName(String val) {
        name = val;
    }

    /**
     */
    public int size() {
        return allNodes.size();
    }

    /**
     * @deprecated Get the child count at the object.
     */
    public int size(Object key) {
        return getChildCount(key);
    }

    /**
     * What is the name of 'this' object.
     */
    public String toString() {
        if (name != null) {
            return name;
        }
        String str = new String();
        str += "";

        return str;
    }

    /**
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("??----TreeCollection>>valueForPathChanged(path,val)----?");
        System.out.println(path);
        System.out.println(newValue);
    }
}
