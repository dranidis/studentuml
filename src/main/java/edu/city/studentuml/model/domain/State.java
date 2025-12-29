package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.components.Copyable;
import org.w3c.dom.Element;

/**
 * @author Biser
 */
public class State implements IXMLCustomStreamable, Copyable<State> {

    private String name;

    public State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public State clone() {
        return new State(this.getName());
    }

    @Override
    public State copyOf(State state) {
        return state.clone();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
    }
}
