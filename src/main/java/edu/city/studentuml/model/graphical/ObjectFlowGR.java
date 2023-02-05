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

}
