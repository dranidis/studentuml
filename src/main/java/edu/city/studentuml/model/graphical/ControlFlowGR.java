package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.util.Vector;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ControlFlowGR extends EdgeGR {

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow) {
        super(source, target, flow);
    }

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        streamer.streamObjectsFrom(streamer.getNodeById(node, "points"), new Vector<>(points), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(ObjectFactory.SOURCE, SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute(ObjectFactory.TARGET, SystemWideObjectNamePool.getInstance().getNameForObject(target));

        streamer.streamObjects(streamer.addChild(node, "points"), getPoints().iterator());

        streamer.streamObject(node, "controlflow", getEdge());
    }
}
