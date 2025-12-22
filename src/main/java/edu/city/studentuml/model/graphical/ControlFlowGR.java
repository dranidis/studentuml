package edu.city.studentuml.model.graphical;

import java.awt.Point;

import edu.city.studentuml.model.domain.ControlFlow;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ControlFlowGR extends EdgeGR {

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow) {
        super(source, target, flow);
    }

    public ControlFlowGR(NodeComponentGR source, NodeComponentGR target, ControlFlow flow, Point srcPoint, Point trgPoint) {
        super(source, target, flow, srcPoint, trgPoint);
    }

    @Override
    protected String getStreamName() {
        return "controlflow";
    }

    @Override
    public ControlFlowGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Edges connect graphical elements, so we reference the same endpoints
        NodeComponentGR sameSource = (NodeComponentGR) getSource();
        NodeComponentGR sameTarget = (NodeComponentGR) getTarget();
        ControlFlow sameFlow = (ControlFlow) getEdge();
        
        // Create new graphical wrapper referencing the SAME domain object and endpoints
        ControlFlowGR clonedGR = new ControlFlowGR(sameSource, sameTarget, sameFlow);
        
        return clonedGR;
    }
}
