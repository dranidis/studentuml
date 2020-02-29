package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.Point;
import java.util.Vector;
import org.w3c.dom.Element;

/**
 *
 * @author Biser
 */
public class ObjectFlowGR extends EdgeGR implements IXMLCustomStreamable {

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow) {
        super(source, target, flow);
    }

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        streamer.streamObjectsFrom(streamer.getNodeById(node, "points"), new Vector(points), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute("source", SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute("target", SystemWideObjectNamePool.getInstance().getNameForObject(target));

        streamer.streamObjects(streamer.addChild(node, "points"), getPoints());

        streamer.streamObject(node, "objectflow", (ObjectFlow) getEdge());
    }
}
