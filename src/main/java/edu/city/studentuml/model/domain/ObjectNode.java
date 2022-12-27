package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ObjectNode extends LeafNode implements IXMLCustomStreamable {

    private Type type;
    private List<State> states; // required states of the object

    private ObjectNode(String name) {
        super(name);
    }

    public ObjectNode() {
        this("");
        type = null;
        states = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void addState(State state) {
        states.add(state);
    }

    public void removeState(State state) {
        states.remove(state);
    }

    public Iterator<State> getStates() {
        return states.iterator();
    }

    public boolean hasStates() {
        return !states.isEmpty();
    }

    public void clearStates() {
        states.clear();
    }

    @Override
    public String toString() {
        String string = "";

        if (name != null && !name.isEmpty()) {
            string += name;
        }

        if ((type != null) && (type.getName() != null)) {
            if (!string.isEmpty() && !type.getName().isEmpty()) {
                string += " : ";
            }
            string += type.getName();
        }

        return string;
    }

    public String getStatesAsString() {
        if (states.isEmpty()) {
            return "";
        }

        String string = "[";
        Iterator it = states.iterator();
        while (it.hasNext()) {
            State s = (State) it.next();
            string += s.getName() + ", ";
        }
        string = string.trim();
        int i = string.lastIndexOf(',');
        return string.substring(0, i) + "]";
    }

    @Override
    public NodeComponent clone() {
        ObjectNode copyNode = new ObjectNode();

        String n = this.getName();
        if (n != null && !n.isEmpty()) {
            copyNode.setName(n);
        }

        Type t = this.getType();
        if (t != null) {
            copyNode.setType(t);
        }

        State s;
        Iterator stateIterator = states.iterator();
        while (stateIterator.hasNext()) {
            s = (State) stateIterator.next();
            copyNode.addState(s.clone());
        }

        return copyNode;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        String thistype = node.getAttribute("type");
        String typeinstance = node.getAttribute("typeinstance");
        String typeid = node.getAttribute("typeid");

        if (thistype.equals("")) {
            type = null;
        } else {
            if (typeinstance.equals("datatype")) {
                setType(new DataType(thistype));
            } else if (typeinstance.equals("designclass")) {
                if (!typeid.equals("")) {
                    DesignClass dc = (DesignClass) SystemWideObjectNamePool.getInstance().getObjectByName(typeid);
                    if (dc != null) {
                        setType(dc);
                    } else {
                        // FIX THIS: design class not added to the repository
                        setType(new DesignClass(thistype));
                    }
                }
            } else if (typeinstance.equals("interface")) {
                if (!typeid.equals("")) {
                    Interface i = (Interface) SystemWideObjectNamePool.getInstance().getObjectByName(typeid);
                    if (i != null) {
                        setType(i);
                    } else {
                        // FIX THIS: interface not added to the repository
                        setType(new Interface(thistype));
                    }
                }
            } else {
                java.lang.System.err.println("Error in ObjectNode:streamFromXML()!");
            }
        }

        streamer.streamObjectsFrom(streamer.getNodeById(node, "states"), new Vector(states), this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        Type t = getType();

        node.setAttribute("name", getName());
        if (t != null) {
            node.setAttribute("type", t.getName());
            String typeinstance = "Error";
            if (t instanceof DataType) {
                typeinstance = "datatype";
            } else if (t instanceof DesignClass) {
                typeinstance = "designclass";
                DesignClass dc = (DesignClass) t;
                //// dc.streamToXML(node, streamer);
                // streamer.streamObject(node, "type", dc);
                node.setAttribute("typeid", SystemWideObjectNamePool.getInstance().getNameForObject(dc));
            } else if (t instanceof Interface) {
                typeinstance = "interface";
                Interface i = (Interface) t;
                //// i.streamToXML(node, streamer);
                // streamer.streamObject(node, "interface", i);
                node.setAttribute("typeid", SystemWideObjectNamePool.getInstance().getNameForObject(i));
            }

            node.setAttribute("typeinstance", typeinstance);
        } else {
            node.setAttribute("type", "");
            node.setAttribute("typeinstance", "");
            node.setAttribute("typeid", "");
        }

        streamer.streamObjects(streamer.addChild(node, "states"), states.iterator());
    }
}
