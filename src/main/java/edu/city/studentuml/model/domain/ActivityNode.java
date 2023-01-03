package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class ActivityNode extends CompositeNode implements IXMLCustomStreamable {

    public ActivityNode(String name) {
        super(name);
    }

    public ActivityNode() {
        this("");
    }

    @Override
    public String toString() {
        return getName();
    }

    /*
     * Only clones the name in order to make it editable.
     */
    @Override
    public NodeComponent clone() {
        NodeComponent copyNode = new ActivityNode();

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
