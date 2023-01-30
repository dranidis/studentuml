package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.city.studentuml.util.Colors;

public class GraphicsHelper {

    private GraphicsHelper() {
    }

    public static void drawString(String s, int x, int y, double angle, boolean arrow, Graphics2D g) {
        if (s.equals("")) {
            return;
        }

        Font font = new Font("SansSerif", Font.PLAIN, 9);

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

        // draw the name arrow
        if (arrow) {
            drawNameArrow(x, y, angle, textWidth / 2, g);
        }
    }

    public static BasicStroke makeSolidStroke() {
        return new BasicStroke(1);
    }

    public static BasicStroke makeSelectedSolidStroke() {
        return new BasicStroke(3);
    }

    public static BasicStroke makeDashedStroke() {
        return new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0);
    }

    public static BasicStroke makeSelectedDashedStroke() {
        return new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0);
    }

    public static void drawSimpleArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);
        g.drawLine(-8, -4, 0, 0);
        g.drawLine(-8, 4, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    public static void drawWhiteArrowHead(int x, int y, double angle, Graphics2D g) {
        drawFilledArrowHead(x, y, angle, Colors.getBackgroundColor(), g);
    }

    public static void drawBlackArrowHead(int x, int y, double angle, Graphics2D g) {
        drawFilledArrowHead(x, y, angle, Colors.getOutlineColor(), g);
    }

    private static void drawFilledArrowHead(int x, int y, double angle, Paint color, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath triangle = new Triangle().get();

        Paint originalPaint = g.getPaint();

        g.setPaint(color);
        g.fill(triangle);
        g.setPaint(originalPaint);
        g.draw(triangle);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    // draws the solid triangular arrow that can be along with the association name
    public static void drawNameArrow(int x, int y, double angle, int offset, Graphics2D g) {

        // try to always draw the arrow above the association line
        if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
            g.translate(x, y);
            g.rotate(angle);

            GeneralPath triangle = new GeneralPath();

            triangle.moveTo(offset + 5.0, 5.0);
            triangle.lineTo(offset + 5.0, 15.0);
            triangle.lineTo(offset + 15.0, 10.0);
            triangle.closePath();

            g.fill(triangle);
            g.rotate(-angle);
            g.translate(-x, -y);
        } else {
            g.translate(x, y);
            g.rotate(angle);

            GeneralPath triangle = new GeneralPath();

            triangle.moveTo(offset + 5.0, -5.0);
            triangle.lineTo(offset + 5.0, -15.0);
            triangle.lineTo(offset + 15.0, -10.0);
            triangle.closePath();

            g.fill(triangle);
            g.rotate(-angle);
            g.translate(-x, -y);
        }
    }

    public static void drawAggregationArrowHead(int x, int y, boolean isStrong, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);

        GeneralPath diamond = new GeneralPath();

        diamond.moveTo(0, 0);
        diamond.lineTo(-8, -4);
        diamond.lineTo(-16, 0);
        diamond.lineTo(-8, 4);
        diamond.closePath();

        Paint originalPaint = g.getPaint();

        if (!isStrong) {
            g.setPaint(Colors.getBackgroundColor());
        } else {
            g.setPaint(originalPaint);
        }

        g.fill(diamond);
        
        g.setPaint(originalPaint);
        g.draw(diamond);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

}
