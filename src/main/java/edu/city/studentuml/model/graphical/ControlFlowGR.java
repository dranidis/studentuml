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
}
