package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 * @author draganbisercic
 */
public class ConceptualClass extends AbstractClass {

    public ConceptualClass(GenericClass gc) {
        super(gc);
    }

    public ConceptualClass(String name) {
        super(new GenericClass(name));
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        clear();
        streamer.streamChildrenFrom(streamer.getNodeById(node, "attributes"), this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, "generic", genericClass);

        streamer.streamObjects(streamer.addChild(node, "attributes"), attributes.iterator());
    }

    public ConceptualClass clone() {
        ConceptualClass copyClass = new ConceptualClass(this.getName());

        attributes.forEach(attribute -> copyClass.addAttribute(attribute.clone()));

        return copyClass;
    }
}
