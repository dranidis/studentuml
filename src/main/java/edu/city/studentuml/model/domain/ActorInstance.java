package edu.city.studentuml.model.domain;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import org.w3c.dom.Element;

/**
 * 
 * @author Ervin Ramollari
 */
public class ActorInstance extends RoleClassifier implements IXMLCustomStreamable {

    public ActorInstance(String name, Actor actor) {
        super(name, actor);
    }

    public Actor getActor() {
        return (Actor) classifier;
    }

    public void setActor(Actor actor) {
        classifier = actor;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // emtpy
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("name", getName());
        streamer.streamObject(node, "actor", getActor());
    }

    public ActorInstance clone() {
        return new ActorInstance(this.getName(), new Actor(this.getActor().getName()));
    }
}
