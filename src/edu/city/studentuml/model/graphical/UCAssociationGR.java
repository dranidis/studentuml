package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UCAssociation;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

/**
 *
 * @author draganbisercic
 */
public class UCAssociationGR extends UCLinkGR {

    public UCAssociationGR(UCActorGR ucActor, UseCaseGR useCase, UCAssociation ucLink) {
        super(ucActor, useCase, ucLink);
    }

    @Override
    protected void drawLine(Graphics2D g) {
        getClassifierA().refreshDimensions(g);
        getClassifierB().refreshDimensions(g);

        Point from = new Point(getXA(), getYA());
        Point to = new Point(getXB(), getYB());

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();

        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        g.drawLine(from.x, from.y, to.x, to.y);

        g.setStroke(originalStroke);
    }
}
