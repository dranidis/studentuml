package edu.city.studentuml.model.domain;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;

/**
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class ConceptualClass extends AbstractClass implements Copyable<ConceptualClass> {

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

    @Override
    public ConceptualClass copyOf(ConceptualClass conceptualClass) {
        return conceptualClass.clone();
    }

    public ConceptualClass clone() {
        ConceptualClass copyClass = new ConceptualClass(this.getName());

        attributes.forEach(attribute -> copyClass.addAttribute(attribute.clone()));

        return copyClass;
    }
}
