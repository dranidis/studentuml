package edu.city.studentuml.model.graphical;

import java.awt.Point;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class ConceptualClassGR extends AbstractClassGR {

    public ConceptualClassGR(ConceptualClass c, Point start) {
        super(c, start);
    }

    public void setConceptualClass(ConceptualClass cl) {
        abstractClass = cl;
    }

    public ConceptualClass getConceptualClass() {
        return (ConceptualClass) abstractClass;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "conceptualclass", getConceptualClass());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}
