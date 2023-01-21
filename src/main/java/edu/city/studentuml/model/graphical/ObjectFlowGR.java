package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.ObjectFactory;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author Biser
 */
public class ObjectFlowGR extends EdgeGR {

    private static final Logger logger = Logger.getLogger(ObjectFlowGR.class.getName());

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow) {
        super(source, target, flow);
    }

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        try {
            streamer.streamObjectsFrom(streamer.getNodeById(node, "points"), new Vector<>(points), this);
        } catch (NotStreamable e) {
            logger.severe("Not streamable");
            e.printStackTrace();
        }
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(ObjectFactory.SOURCE, SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute(ObjectFactory.TARGET, SystemWideObjectNamePool.getInstance().getNameForObject(target));

        streamer.streamObjects(streamer.addChild(node, "points"), getPoints().iterator());

        streamer.streamObject(node, "objectflow", getEdge());
    }
}
