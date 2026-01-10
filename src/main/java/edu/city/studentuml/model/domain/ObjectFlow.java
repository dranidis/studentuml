package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.view.gui.components.Copyable;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * Used to model flow of objects in an activity.
 *
 * @author Biser
 */
public class ObjectFlow extends Edge implements IXMLCustomStreamable, Copyable<ObjectFlow> {

    protected String weight;

    public ObjectFlow(NodeComponent source, NodeComponent target) {
        super(source, target);
        weight = "";
    }

    public void setWeight(String weight) {
        if (isLegalWeight(weight)) {
            this.weight = weight;
        } else {
            throw new RuntimeException("The value weight=" + weight + " is not allowed");
        }
    }

    public String getWeight() {
        if (weight.length() > 0) {
            return weight;
        } else {
            return "";
        }
    }

    private boolean isLegalWeight(String weight) {
        // if not a constant then true; else check it's positive integer
        if (weight.length() < 1) {
            return true;
        }

        int j = 0;
        if (weight.charAt(j) == '-') {
            if (weight.length() == 1) {
                return true;
            }
            j = j + 1;
        }

        for (int i = j; i < weight.length(); i++) {
            if (weight.charAt(i) < '0' || weight.charAt(i) > '9') {
                return true;
            }
        }

        int temp = Integer.parseInt(weight);

        return temp > 0;
    }

    @Override
    public String toString() {
        if (guard.isEmpty() && weight.isEmpty()) {
            return "";
        } else if (guard.isEmpty() && !weight.isEmpty()) {
            return "{weight=" + weight + "}";
        } else if (!guard.isEmpty() && weight.isEmpty()) {
            return "[" + guard + "]";
        } else {
            return "[" + getGuard() + "] {weight=" + getWeight() + "}";
        }
    }

    @Override
    public ObjectFlow clone() {
        ObjectFlow copyFlow = new ObjectFlow(this.getSource(), this.getTarget());

        String g = this.getGuard();
        if (g != null && !g.isEmpty()) {
            copyFlow.setGuard(g);
        }

        String w = this.getWeight();
        if (w != null && !w.isEmpty()) {
            copyFlow.setWeight(w);
        }

        return copyFlow;
    }

    @Override
    public ObjectFlow copyOf(ObjectFlow source) {
        return source.clone();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setGuard(node.getAttribute("guard"));
        setWeight(node.getAttribute("weight"));

        source = (NodeComponent) SystemWideObjectNamePool.getInstance()
                .getObjectByName(node.getAttribute(XMLSyntax.SOURCE));
        target = (NodeComponent) SystemWideObjectNamePool.getInstance()
                .getObjectByName(node.getAttribute(XMLSyntax.TARGET));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("guard", getGuard());
        node.setAttribute("weight", getWeight());

        node.setAttribute(XMLSyntax.SOURCE, SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute(XMLSyntax.TARGET, SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
    }
}
