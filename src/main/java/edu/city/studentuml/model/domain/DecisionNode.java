package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class DecisionNode extends ControlNode implements IXMLCustomStreamable {
	
    private DecisionNode(String name) {
        super(name);
    }

    public DecisionNode() {
        this("Decision");
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public void setName(String name) {
    	this.name= name;
    }
    
    @Override
    public String getName() {
    	return this.name;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    	setName(node.getAttribute("name"));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
    
    @Override
    public NodeComponent clone() {
        NodeComponent copyNode = new DecisionNode();

        String n = this.getName();
        if (n != null && !n.isEmpty()) {
            copyNode.setName(n);
        }

        return copyNode;
    }
}
