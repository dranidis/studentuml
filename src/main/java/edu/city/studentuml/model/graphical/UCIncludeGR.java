package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.UCInclude;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author draganbisercic
 */
public class UCIncludeGR extends UCLinkGR {

    private Font font = new Font("SansSerif", Font.ITALIC, 9);
    
    public UCIncludeGR(UseCaseGR includingUseCase, UseCaseGR includedUseCase, UCInclude ucLink) {
        super(includingUseCase, includedUseCase, ucLink);
    }

    @Override
    protected void drawLine(Graphics2D g) {
        a.refreshDimensions(g);
        b.refreshDimensions(g);

        Point from = new Point(getXA(), getYA());
        Point to = new Point(getXB(), getYB());

        
        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();

        
        if (isSelected()) {
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
            g.setPaint(outlineColor);
        }

        g.drawLine(from.x, from.y, to.x, to.y);

        g.setStroke(originalStroke);

        double rotationAngle = getAngleRoleA();

        drawArrowHead(to.x, to.y, rotationAngle, g);

        drawString(this.getLink().getName(), (from.x + to.x) / 2, (from.y + to.y) / 2, rotationAngle, g);
    }

    public void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);
        g.drawLine(-8, -4, 0, 0);
        g.drawLine(-8, 4, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    public void drawString(String s, int x, int y, double angle, Graphics2D g) {
        double textAngle = angle;

        if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
            textAngle -= Math.PI;
        }

        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(s, font, frc);
        Rectangle2D bounds = layout.getBounds();
        int textWidth = (int) bounds.getWidth();

        g.setFont(font);
        g.translate(x, y);
        g.rotate(textAngle);
        g.drawString(s, -textWidth / 2, -4);
        g.rotate(-textAngle);
        g.translate(-x, -y);
    }
}
