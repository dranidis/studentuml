package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

import edu.city.studentuml.model.domain.RoleClassifier;

/**
 *
 * @author draganbisercic
 */
public abstract class AbstractSDObjectGR extends RoleClassifierGR {

    private static int minimumNameBoxWidth = 50;
    private static int nameBoxHeight = 30;
    private Font nameFont;

    protected AbstractSDObjectGR(RoleClassifier obj, int x) {
        super(obj, x);
        width = minimumNameBoxWidth;
        height = nameBoxHeight;
        nameFont = new Font("SansSerif", Font.BOLD, 12);
        fillColor = null;

    }

    // the graphical sd object is selected if it is clicked at the name box
    // or at or near the life line extending from the name box
    public boolean contains(Point2D point) {
        // The portion of the visual object including the name box
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);

        // The portion of the visual object including the life line
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + width / 2.0 - 8.0, getY() + (double) height, 
                16, (double) endingY - (getY() + height));

        return (rectangle1.contains(point) || rectangle2.contains(point));
    }

    @Override
    public void draw(Graphics2D g) {
        if (fillColor == null) {
            fillColor = this.myColor();
        }

        refreshDimensions(g);

        int startingX = getX();
        int startingY = getY();

        // determine the outline of the rectangle representing the object
        // name box
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);

        g.setPaint(fillColor);
        g.fill(shape);

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        g.draw(shape);
        g.setPaint(outlineColor);

        // draw the object text within the box
        String nameBoxText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(nameBoxText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        // center the name string in the box
        int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        int nameY = ((height - (int) bounds.getHeight()) / 2) - (int) bounds.getY();

        g.setFont(nameFont);
        g.drawString(nameBoxText, startingX + nameX, startingY + nameY);

        if (ConstantsGR.UNDERLINE_OBJECTS) {
            // underline the text
            int underlineX = nameX + (int) bounds.getX();
            int underlineY = nameY + (int) bounds.getY() + (int) bounds.getHeight();

            g.drawLine(startingX + underlineX - 2, startingY + underlineY + 2,
                    startingX + underlineX + (int) bounds.getWidth() + 2, startingY + underlineY + 2);
        }

        // draw the dashed lifeline below the name box
        if (isSelected()) {
            g.setPaint(highlightColor);
        } else {
            g.setPaint(outlineColor);
        }

            // the pattern of dashes for drawing the realization line

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
        g.drawLine(startingX + width / 2, startingY + height, startingX + width / 2, endingY);

        // restore the original stroke
        g.setStroke(originalStroke);
        
        drawActivationBars(g);
    }

    public void refreshDimensions(Graphics2D g) {
        calculateWidth(g);
    }

    // Calculates the width of the name box as it appears on the screen.
    // The width will depend on the length of the name string and the graphics context.
    protected int calculateWidth(Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        String boxText = roleClassifier.toString();
        TextLayout layout = new TextLayout(boxText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        if (bounds.getWidth() + 8 > minimumNameBoxWidth) {
            width = ((int) bounds.getWidth()) + 8;
        } else {
            width = minimumNameBoxWidth;
        }

        return width;
    }

    private void drawActivationBars(Graphics2D g) {
        /**
         * collect all bars in a stack while parsing the messages
         */
        Deque<ActivationBar> bars = new ArrayDeque<>();
        Deque<ActivationBar> finalBars = new ArrayDeque<>();
        
        int previousActivation = 0;
        for(int i=0; i < messageYs.size(); i++) {
            int currentActivation = activationAt.get(messageYs.get(i));
            if( currentActivation > previousActivation ) {
                ActivationBar bar = new ActivationBar();
                bar.fromY = messageYs.get(i);
                bar.depth = activationAt.get(messageYs.get(i));
                bars.push(bar);
            } else if (currentActivation < previousActivation) {
                ActivationBar bar = bars.pop();
                bar.toY = messageYs.get(i);
                finalBars.push(bar);
            }
            previousActivation = currentActivation;
        }
        while(!finalBars.isEmpty()) {
            drawBar(g, finalBars.pop());
        }
    }
    
    private class ActivationBar {
        int fromY;
        int toY;
        int depth;
        public String toString() {
            return "From: " + fromY + " to: " + toY + " depth: " + depth;
        }
    }
    
    private void drawBar(Graphics2D g, ActivationBar bar) {
        int barWidth = ConstantsGR.getInstance().get("SDMessageGR", "barWidth");
        int startingX = getX() + width / 2 + (bar.depth - 1) * barWidth/2 - barWidth/2;
        int startingY = bar.fromY;
        int width = barWidth;
        int height = bar.toY - bar.fromY;

        g.setPaint(getFillColor());
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
        g.fill(shape);

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }

        g.draw(shape);        
   }
    
}
