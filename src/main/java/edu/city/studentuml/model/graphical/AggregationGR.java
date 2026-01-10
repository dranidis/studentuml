package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

public class AggregationGR extends AssociationGR {

    private static final Logger logger = Logger.getLogger(AggregationGR.class.getName());

    public AggregationGR(ClassifierGR w, ClassifierGR p, Aggregation aggreg) {
        super(w, p, aggreg);
    }

    @Override
    protected void drawArrowHeads(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        GraphicsHelper.drawAggregationArrowHead(aX, aY, getAggregation().isStrong(), angleB, g);
        super.drawArrowHeads(aX, aY, bX, bY, angleA, angleB, g);
    }

    @Override
    public void drawArrowHeadsReflective(Graphics2D g) {
        GraphicsHelper.drawAggregationArrowHead(getXA(), getYA(), getAggregation().isStrong(), Math.PI / 2, g);
        super.drawArrowHeadsReflective(g);
    }

    public Aggregation getAggregation() {
        return (Aggregation) getAssociation();
    }

    @Override
    public boolean isReflective() {
        return getAggregation().isReflective();
    }

    public ClassifierGR getWhole() {
        return getClassA();
    }

    public ClassifierGR getPart() {
        return getClassB();
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(getWhole()));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(getPart()));

        streamer.streamObject(node, "aggregation", getAggregation());
    }

    @Override
    public String toString() {
        return "" + a + " ---aggregation---> " + b;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        // Get the current aggregation to preserve the isStrong property
        Aggregation oldAggregation = getAggregation();

        // Create a new Role with the new classifier, preserving name and multiplicity
        Role oldRoleA = oldAggregation.getRoleA();
        Role newRoleA = new Role(newSource.getClassifier());
        newRoleA.setName(oldRoleA.getName());
        newRoleA.setMultiplicity(oldRoleA.getMultiplicity());

        // Create a new Aggregation (not just Association!) with the new role
        Aggregation newAggregation = new Aggregation(newRoleA, oldAggregation.getRoleB(), oldAggregation.isStrong());
        newAggregation.setName(oldAggregation.getName());
        newAggregation.setDirection(oldAggregation.getDirection());
        newAggregation.setShowArrow(oldAggregation.getShowArrow());
        newAggregation.setLabelDirection(oldAggregation.getLabelDirection());

        // Update the association field (inherited from AssociationGR)
        setAssociation(newAggregation);

        logger.fine(() -> "Prepared aggregation source reconnection to: " + newSource.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        // Get the current aggregation to preserve the isStrong property
        Aggregation oldAggregation = getAggregation();

        // Create a new Role with the new classifier, preserving name and multiplicity
        Role oldRoleB = oldAggregation.getRoleB();
        Role newRoleB = new Role(newTarget.getClassifier());
        newRoleB.setName(oldRoleB.getName());
        newRoleB.setMultiplicity(oldRoleB.getMultiplicity());

        // Create a new Aggregation (not just Association!) with the new role
        Aggregation newAggregation = new Aggregation(oldAggregation.getRoleA(), newRoleB, oldAggregation.isStrong());
        newAggregation.setName(oldAggregation.getName());
        newAggregation.setDirection(oldAggregation.getDirection());
        newAggregation.setShowArrow(oldAggregation.getShowArrow());
        newAggregation.setLabelDirection(oldAggregation.getLabelDirection());

        // Update the association field (inherited from AssociationGR)
        setAssociation(newAggregation);

        logger.fine(() -> "Prepared aggregation target reconnection to: " + newTarget.getClassifier().getName());
        return true;
    }

    @Override
    public AggregationGR createWithNewEndpoints(ClassifierGR newA, ClassifierGR newB) {
        return new AggregationGR(newA, newB, getAggregation());
    }
}
