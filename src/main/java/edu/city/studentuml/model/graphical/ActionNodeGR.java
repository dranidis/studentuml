package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.city.studentuml.model.domain.ActionNode;

/**
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ActionNodeGR extends LeafNodeGR  {

    private static int minimumWidth = 70;
    private static int minimumHeight = 24;
    protected static int actionNameXOffset = 10;
    protected static int actionNameYOffset = 5;
    private Font actionNameFont;

    public ActionNodeGR(ActionNode actionNode, int x, int y) {
        super(actionNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumHeight;

        actionNameFont = new Font("SansSerif", Font.PLAIN, 14);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint action node
        g.setPaint(getFillColor());
        Shape shape = new RoundRectangle2D.Double(startingX, startingY, width, height, 10, 10);
        g.fill(shape);

        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();

        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(GraphicsHelper.makeSolidStroke());
            g.setPaint(getOutlineColor());
        }
        // draw the action node
        g.draw(shape);


        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

        FontRenderContext frc = g.getFontRenderContext();
        // draw action node name
        if (!component.toString().equals("")) {
            String actionName = component.toString();
            TextLayout layout = new TextLayout(actionName, actionNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = ((height - (int) bounds.getHeight()) / 2) - (int) bounds.getY();

            g.setFont(actionNameFont);
            g.drawString(actionName, startingX + nameX, startingY + nameY);
        }
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();

        // consider action name text dimensions
        if (component.toString().length() != 0) {
            TextLayout layout = new TextLayout(component.toString(), actionNameFont, frc);
            Rectangle2D bounds = layout.getBounds();
            int actionNameWidth = (int) bounds.getWidth() + (2 * actionNameXOffset);

            if (actionNameWidth > newWidth) {
                newWidth = actionNameWidth;
            }
        }

        width = newWidth;

        return newWidth;
    }

    @Override
    protected String getStreamName() {
        return "actionnode";
    }
}
