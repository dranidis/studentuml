package edu.city.studentuml.model.graphical;

import java.awt.Point;

import edu.city.studentuml.model.domain.ObjectFlow;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ObjectFlowGR extends EdgeGR {

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow) {
        super(source, target, flow);
    }

    public ObjectFlowGR(NodeComponentGR source, NodeComponentGR target, ObjectFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    protected String getStreamName() {
        return "objectflow";
    }

    @Override
    public ObjectFlowGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Edges connect graphical elements, so we reference the same endpoints
        NodeComponentGR sameSource = (NodeComponentGR) getSource();
        NodeComponentGR sameTarget = (NodeComponentGR) getTarget();
        ObjectFlow sameFlow = (ObjectFlow) getEdge();
        
        // Create new graphical wrapper referencing the SAME domain object and endpoints
        ObjectFlowGR clonedGR = new ObjectFlowGR(sameSource, sameTarget, sameFlow);
        
        return clonedGR;
    }

}
