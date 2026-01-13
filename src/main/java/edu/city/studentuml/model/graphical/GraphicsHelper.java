package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.city.studentuml.util.Colors;

public class GraphicsHelper {

    private GraphicsHelper() {
    }

    public static void drawString(String s, int x, int y, double angle, boolean arrow, Graphics2D g) {
        if (s.equals("")) {
            return;
        }

        Font font = FontRegistry.HELPER_TEXT_FONT;

        double textAngle = angle;

        if (angleGreaterThanHalfPi(angle)) {
            textAngle -= Math.PI;
        }

        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = getTextBounds(s, font, frc);
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
        if (angleGreaterThanHalfPi(angle)) {
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

    public static void drawStickFigure(Graphics2D g, int x, int y, boolean isSelected,
            Color fillColor, Color outlineColor, Color highlightColor) {
        Shape head = new Ellipse2D.Double(x - 6.0, y, 12, 12);

        g.setPaint(fillColor);
        g.fill(head);

        if (isSelected) {
            g.setPaint(highlightColor);
        } else {
            g.setPaint(outlineColor);
        }

        g.draw(head);
        g.drawLine(x, y + 12, x, y + 25);
        g.drawLine(x - 10, y + 16, x + 10, y + 16);
        g.drawLine(x - 10, y + 35, x, y + 25);
        g.drawLine(x, y + 25, x + 10, y + 35);
    }

    public static void clearBorder(JComponent button) {
        button.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public static void highlightBorder(JComponent button) {
        button.setBorder(
                new CompoundBorder(new LineBorder(Colors.getHighlightColor(), 1), new EmptyBorder(4, 4, 4, 4)));
    }

    public static void addHightLightMouseAdapter(JComponent button) {
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                highlightBorder(button);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clearBorder(button);
            }
        });
    }

    public static boolean angleGreaterThanHalfPi(double angle) {
        return angle < 3 * Math.PI / 2 && angle >= Math.PI / 2;

    }

    /**
     * Calculates the X offset to center text horizontally within a given width.
     * 
     * @param containerWidth The width of the container
     * @param textBounds     The bounds of the text to be centered
     * @return The X offset for centered text
     */
    public static int calculateCenteredTextX(int containerWidth, Rectangle2D textBounds) {
        return (containerWidth - (int) textBounds.getWidth()) / 2 - (int) textBounds.getX();
    }

    /**
     * Calculates the Y offset to center text vertically within a given height.
     * 
     * @param containerHeight The height of the container
     * @param textBounds      The bounds of the text to be centered
     * @return The Y offset for centered text
     */
    public static int calculateCenteredTextY(int containerHeight, Rectangle2D textBounds) {
        return (containerHeight - (int) textBounds.getHeight()) / 2 - (int) textBounds.getY();
    }

    /**
     * Draws text centered horizontally within a container.
     * 
     * @param g              Graphics context
     * @param text           The text to draw
     * @param font           The font to use
     * @param containerX     The X coordinate of the container
     * @param containerY     The Y coordinate where text should be drawn
     * @param containerWidth The width of the container for centering
     */
    public static void drawCenteredText(Graphics2D g, String text, Font font,
            int containerX, int containerY, int containerWidth) {
        if (text == null || text.isEmpty()) {
            return;
        }

        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = getTextBounds(text, font, frc);

        int textX = calculateCenteredTextX(containerWidth, bounds);
        g.setFont(font);
        g.drawString(text, containerX + textX, containerY);
    }

    /**
     * Gets the bounding rectangle for text rendered with the specified font. This
     * is a convenience method that encapsulates the common pattern of creating a
     * TextLayout and getting its bounds.
     * 
     * @param text The text to measure
     * @param font The font to use
     * @param frc  The FontRenderContext
     * @return The bounds of the rendered text
     */
    public static Rectangle2D getTextBounds(String text, Font font, FontRenderContext frc) {
        TextLayout layout = new TextLayout(text, font, frc);
        return layout.getBounds();
    }

}
