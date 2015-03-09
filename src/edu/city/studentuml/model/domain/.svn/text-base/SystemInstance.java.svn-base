package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class SystemInstance extends RoleClassifier implements IXMLCustomStreamable {

    public SystemInstance(String name, System system) {
        super(name, system);
    }

    public void setSystem(System system) {
        classifier = system;
    }

    public System getSystem() {
        return (System) classifier;
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, "system", getSystem());
        //node.setAttribute("system", SystemWideObjectNamePool.getInstance().getNameForObject(getSystem()));
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        setName(node.getAttribute("name"));
    }

    public SystemInstance clone() {
        return new SystemInstance(this.getName(), new System(this.getSystem().getName()));
    }
}
