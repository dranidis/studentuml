package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UCGeneralization;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

/**
 *
 * @author draganbisercic
 */
public class UCGeneralizationGR extends UCLinkGR {

    public UCGeneralizationGR(UCActorGR src, UCActorGR trg, UCGeneralization ucLink) {
        super(src, trg, ucLink);
    }

    public UCGeneralizationGR(UseCaseGR src, UseCaseGR trg, UCGeneralization ucLink) {
        super(src, trg, ucLink);
    }

    @Override
    protected void drawLine(Graphics2D g) {
        source.refreshDimensions(g);
        target.refreshDimensions(g);

        Point base = new Point(getXA(), getYA());
        Point supr = new Point(getXB(), getYB());

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();

        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        g.drawLine(base.x, base.y, supr.x, supr.y);

        g.setStroke(originalStroke);

        double rotationAngle = getAngleRoleA();
        
        drawGeneralizationArrowHead(supr.x, supr.y, rotationAngle, g);
    }

    private void drawGeneralizationArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath triangle = new GeneralPath();

        triangle.moveTo(0, 0);
        triangle.lineTo(-10, -5);
        triangle.lineTo(-10, 5);
        triangle.closePath();

        Paint originalPaint = g.getPaint();

        g.setPaint(Color.white);
        g.fill(triangle);
        g.setPaint(originalPaint);
        g.draw(triangle);
        g.rotate(-angle);
        g.translate(-x, -y);
    }
}
