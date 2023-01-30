package edu.city.studentuml.model.graphical;

import java.awt.Point;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

/**
 *
 * @author Biser
 */
public class ControlFlowGR extends EdgeGR {

    private static final Logger logger = Logger.getLogger(ControlFlowGR.class.getName());

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow) {
        super(source, target, flow);
    }

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        try {
            streamer.streamChildrenFrom(streamer.getNodeById(node, "points"), this);
        } catch (NotStreamable e) {
            logger.severe("Not streamable");
            e.printStackTrace();
        }
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(XMLSyntax.SOURCE, SystemWideObjectNamePool.getInstance().getNameForObject(source));
        node.setAttribute(XMLSyntax.TARGET, SystemWideObjectNamePool.getInstance().getNameForObject(target));

        streamer.streamObjects(streamer.addChild(node, "points"), getPoints().iterator());

        streamer.streamObject(node, "controlflow", getEdge());
    }
}
