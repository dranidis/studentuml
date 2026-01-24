package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.Stroke;

import edu.city.studentuml.model.domain.ControlNode;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public abstract class AbstractForkNodeGR extends LeafNodeGR {

    private static final int FORK_WIDTH = 60;
    private static final int FORK_HEIGHT = 10;
    protected static final int NAME_X_OFFSET = 5;

    protected AbstractForkNodeGR(ControlNode forkNode, int x, int y) {
        super(forkNode, x, y);

        // initialize the element's width and height to the minimum ones
        width = FORK_WIDTH;
        height = FORK_HEIGHT;
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint fork node
        g.setPaint(getOutlineColor());
        g.fillRect(startingX, startingY, width, height);

        // draw fork node
        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }
        g.drawRect(startingX, startingY, width, height);

        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

        // draw fork node string
        if (!component.toString().equals("")) {
            String decisionName = component.toString();
            int nameX = width + NAME_X_OFFSET;
            int nameY = height;

            g.setFont(FontRegistry.FORK_NODE_FONT);
            g.drawString(decisionName, startingX + nameX, startingY + nameY);
        }
    }
}
