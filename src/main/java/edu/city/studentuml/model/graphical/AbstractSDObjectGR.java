package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

import edu.city.studentuml.model.domain.RoleClassifier;

/**
 * @author draganbisercic
 */
public abstract class AbstractSDObjectGR extends RoleClassifierGR {

    private static int minimumNameBoxWidth = 50;
    private static int nameBoxHeight = 30;
    protected static final Font NAME_FONT = FontRegistry.SD_OBJECT_NAME_FONT;

    protected AbstractSDObjectGR(RoleClassifier obj, int x) {
        super(obj, x);
        width = minimumNameBoxWidth;
        height = nameBoxHeight;
    }

    // the graphical sd object is selected if it is clicked at the name box
    // or at or near the life line extending from the name box
    public boolean contains(Point2D point) {
        // The portion of the visual object including the name box
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);

        // The portion of the visual object including the life line
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + width / 2.0 - 8.0, getY() + (double) height,
                16, (double) endingY - getY() - height);

        return rectangle1.contains(point) || rectangle2.contains(point);
    }

    @Override
    public void draw(Graphics2D g) {
        Stroke originalStroke = g.getStroke();

        calculateWidth(g);

        int startingX = getX();
        int startingY = getY();

        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeSolidStroke());
            g.setPaint(getOutlineColor());
        }

        drawObjectShape(g, startingX, startingY);

        drawObjectName(g, startingX, startingY);

        drawLifeline(g, startingX, startingY);

        // restore the original stroke
        g.setStroke(originalStroke);

        drawActivationBars(g);
    }

    protected void drawObjectShape(Graphics2D g, int startingX, int startingY) {
        // determine the outline of the rectangle representing the object
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);

        Paint originalPaint = g.getPaint();

        g.setPaint(getFillColor());
        g.fill(shape);
        g.setPaint(originalPaint);

        g.draw(shape);
    }

    protected void drawObjectName(Graphics2D g, int startingX, int startingY) {
        String nameBoxText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();

        // Check if stereotype exists
        String stereotype = getStereotypeText();
        boolean hasStereotype = stereotype != null && !stereotype.isEmpty();

        // Calculate bounds for name
        Rectangle2D nameBounds = GraphicsHelper.getTextBounds(nameBoxText, NAME_FONT, frc);

        // Calculate bounds for stereotype if present
        Rectangle2D stereotypeBounds = null;
        if (hasStereotype) {
            Font stereotypeFont = NAME_FONT.deriveFont(NAME_FONT.getSize() * 0.85f);
            stereotypeBounds = GraphicsHelper.getTextBounds(stereotype, stereotypeFont, frc);
        }

        // Calculate vertical positions
        int nameY;
        int stereotypeY = 0; // Initialize to avoid compilation error
        if (hasStereotype) {
            // Total height needed for both texts with spacing
            double totalTextHeight = stereotypeBounds.getHeight() + nameBounds.getHeight() + 2; // 2px spacing
            // Center both texts vertically in the box
            int topMargin = (int) ((height - totalTextHeight) / 2);
            stereotypeY = topMargin + (int) stereotypeBounds.getHeight();
            nameY = stereotypeY + 2 + (int) nameBounds.getHeight(); // 2px spacing between stereotype and name
        } else {
            // Center name vertically as before
            nameY = GraphicsHelper.calculateCenteredTextY(height, nameBounds);
        }

        // Draw stereotype if present
        if (hasStereotype) {
            Font stereotypeFont = NAME_FONT.deriveFont(NAME_FONT.getSize() * 0.85f);
            g.setFont(stereotypeFont);
            int stereotypeX = GraphicsHelper.calculateCenteredTextX(width, stereotypeBounds);
            g.drawString(stereotype, startingX + stereotypeX, startingY + stereotypeY);
        }

        // Draw name
        int nameX = GraphicsHelper.calculateCenteredTextX(width, nameBounds);
        g.setFont(NAME_FONT);
        g.drawString(nameBoxText, startingX + nameX, startingY + nameY);

        if (ConstantsGR.UNDERLINE_OBJECTS) {
            // underline the name text
            int underlineX = nameX + (int) nameBounds.getX();
            int underlineY = nameY + (int) nameBounds.getY() + (int) nameBounds.getHeight();

            g.drawLine(startingX + underlineX - 2, startingY + underlineY + 2,
                    startingX + underlineX + (int) nameBounds.getWidth() + 2, startingY + underlineY + 2);
        }
    }

    /**
     * Get the stereotype text with guillemets if a stereotype is set.
     * 
     * @return The stereotype formatted as "<<stereotype>>" or null if no stereotype
     */
    private String getStereotypeText() {
        if (roleClassifier instanceof edu.city.studentuml.model.domain.AbstractObject) {
            edu.city.studentuml.model.domain.AbstractObject obj = (edu.city.studentuml.model.domain.AbstractObject) roleClassifier;
            String stereotype = obj.getStereotype();
            if (stereotype != null && !stereotype.isEmpty()) {
                return "<<" + stereotype + ">>";
            }
        }
        return null;
    }

    private void drawLifeline(Graphics2D g, int startingX, int startingY) {
        // draw the dashed lifeline below the name box
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedDashedStroke());
        } else {
            g.setStroke(GraphicsHelper.makeDashedStroke());
        }

        g.drawLine(startingX + width / 2, startingY + height, startingX + width / 2, endingY);
    }

    // Calculates the width of the name box as it appears on the screen.
    // The width will depend on the length of the name string and the graphics context.
    // Also accounts for stereotype text width if present.
    private int calculateWidth(Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        String boxText = roleClassifier.toString();
        Rectangle2D nameBounds = GraphicsHelper.getTextBounds(boxText, NAME_FONT, frc);

        double maxWidth = nameBounds.getWidth();

        // Check if stereotype is present and potentially wider
        String stereotype = getStereotypeText();
        if (stereotype != null && !stereotype.isEmpty()) {
            Font stereotypeFont = NAME_FONT.deriveFont(NAME_FONT.getSize() * 0.85f);
            Rectangle2D stereotypeBounds = GraphicsHelper.getTextBounds(stereotype, stereotypeFont, frc);
            maxWidth = Math.max(maxWidth, stereotypeBounds.getWidth());
        }

        if (maxWidth + 8 > minimumNameBoxWidth) {
            width = ((int) maxWidth) + 8;
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
        for (int i = 0; i < messageYs.size(); i++) {
            int currentActivation = activationAt.get(messageYs.get(i));
            if (currentActivation > previousActivation) {
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
        while (!finalBars.isEmpty()) {
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
        int startingX = getX() + width / 2 + (bar.depth - 1) * barWidth / 2 - barWidth / 2;
        int startingY = bar.fromY;
        int width = barWidth;
        int height = bar.toY - bar.fromY;

        g.setPaint(getFillColor());
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
        g.fill(shape);

        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }

        g.draw(shape);
    }
}
