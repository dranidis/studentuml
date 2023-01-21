package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 * Starts an activity node after previous one has finished.
 *
 * @author Biser
 */
public class ControlFlow extends Edge implements IXMLCustomStreamable {

    public ControlFlow(NodeComponent source, NodeComponent target) {
        super(source, target);
    }

    @Override
    public String toString() {
        if (guard.isEmpty()) {
            return "";
        } else {
            return "[" + guard + "]";
        }
    }

    @Override
    public Edge clone() {
        ControlFlow copyFlow = new ControlFlow(this.getSource(), this.getTarget());

        String g = this.getGuard();
        if (g != null && !g.isEmpty()) {
            copyFlow.setGuard(g);
        }

        return copyFlow;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setGuard(node.getAttribute("guard"));

        source = (NodeComponent) SystemWideObjectNamePool.getInstance().getObjectByName(node.getAttribute(XMLSyntax.SOURCE));
        target = (NodeComponent) SystemWideObjectNamePool.getInstance().getObjectByName(node.getAttribute(XMLSyntax.TARGET));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("guard", getGuard());

        node.setAttribute(XMLSyntax.SOURCE, SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute(XMLSyntax.TARGET, SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
    }
}
