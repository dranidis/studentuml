package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.city.studentuml.controller.EditContext;
import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.util.undoredo.EditActionNodeEdit;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ActionNodeGR extends LeafNodeGR {

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
            Rectangle2D bounds = GraphicsHelper.getTextBounds(actionName, actionNameFont, frc);
            int nameX = GraphicsHelper.calculateCenteredTextX(width, bounds);
            int nameY = GraphicsHelper.calculateCenteredTextY(height, bounds);

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
            Rectangle2D bounds = GraphicsHelper.getTextBounds(component.toString(), actionNameFont, frc);
            int actionNameWidth = (int) bounds.getWidth() + 2 * actionNameXOffset;

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

    @Override
    public ActionNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ActionNode sameActionNode = (ActionNode) getComponent();

        // Create new graphical wrapper referencing the SAME domain object
        ActionNodeGR clonedGR = new ActionNodeGR(sameActionNode, this.startingPoint.x, this.startingPoint.y);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;

        return clonedGR;
    }

    /**
     * Opens an editor dialog for editing the action node's name. Uses the template
     * method from NodeComponentGR for the common editing workflow.
     * 
     * @param context the edit context providing access to model, repository, parent
     *                component, and undo support
     * @return true if the edit was successful and applied, false if cancelled
     */
    @Override
    public boolean edit(EditContext context) {
        return editNameWithDialog(
                context,
                "Action Node Editor",
                "Action name: ",
                (original, undo, model) -> new EditActionNodeEdit(
                        (ActionNode) original,
                        (ActionNode) undo,
                        model));
    }
}
