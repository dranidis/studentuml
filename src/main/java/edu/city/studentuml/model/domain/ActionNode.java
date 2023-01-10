package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class ActionNode extends LeafNode implements IXMLCustomStreamable {

    public ActionNode(String name) {
        super(name);
    }

    public ActionNode() {
        this("");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public NodeComponent clone() {
        NodeComponent copyNode = new ActionNode();

        String n = this.getName();
        if (n != null && !n.isEmpty()) {
            copyNode.setName(n);
        }

        return copyNode;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}
