package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.XMLStreamer;
import java.util.Iterator;
import org.w3c.dom.Element;

/**
 *
 * @author draganbisercic
 */
public class ConceptualClass extends AbstractClass {

    public ConceptualClass(GenericClass gc) {
        super(gc);
    }

    public ConceptualClass(String name) {
        super(new GenericClass(name));
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        clear();
        streamer.streamObjectsFrom(streamer.getNodeById(node, "attributes"), attributes, this);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, "generic", genericClass);

        streamer.streamObjects(streamer.addChild(node, "attributes"), attributes.iterator());
    }

    public ConceptualClass clone() {
        ConceptualClass copyClass = new ConceptualClass(this.getName());

        Iterator<Attribute> attributeIterator = attributes.iterator();
        while (attributeIterator.hasNext()) {
            Attribute attribute = attributeIterator.next();
            copyClass.addAttribute(attribute.clone());
        }

        return copyClass;
    }   
}
