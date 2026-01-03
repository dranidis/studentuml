package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.EditActorEdit;

/**
 * @author draganbisercic
 */
public class UCActorGR extends LeafUCDElementGR {

    private static int actorTextDistance = 6;
    private static int stickFigureHeight = 35;
    private static int stickFigureWidth = 20;
    private Font actorNameFont;

    public UCActorGR(Actor actor, int x, int y) {
        super(actor, x, y);

        width = stickFigureWidth;
        height = stickFigureHeight;

        actorNameFont = new Font("Sans Serif", Font.BOLD, 12);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }

        // draw the actor
        GraphicsHelper.drawStickFigure(g, startingX + (width / 2), startingY, isSelected(), getFillColor(),
                getOutlineColor(), getHighlightColor());

        // draw the actor description under the stick figure
        g.setPaint(getOutlineColor());

        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(actorName, actorNameFont, frc);

        // draw the actor name under the figure and center it
        int nameX = GraphicsHelper.calculateCenteredTextX(width, bounds);
        int nameY = stickFigureHeight + actorTextDistance - (int) bounds.getY();

        g.setFont(actorNameFont);
        g.drawString(actorName, startingX + nameX, startingY + nameY);
    }

    public int calculateWidth(Graphics2D g) {
        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(actorName, actorNameFont, frc);
        int newWidth = stickFigureWidth;

        if (bounds.getWidth() > newWidth) {
            newWidth = (int) bounds.getWidth();
        }

        width = newWidth;

        return width;
    }

    public int calculateHeight(Graphics2D g) {
        String actorName = getComponent().getName();
        if (actorName == null || actorName.length() == 0) {
            actorName = " ";
        }
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(actorName, actorNameFont, frc);

        height = stickFigureHeight + actorTextDistance + (int) bounds.getHeight();

        return height;
    }

    @Override
    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(
                startingPoint.getX(), startingPoint.getY(),
                getWidth(), getHeight());

        return rect.contains(p);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "ucActor", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }

    @Override
    public UCActorGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        Actor sameActor = (Actor) getComponent();

        // Create new graphical wrapper referencing the SAME domain object
        UCActorGR clonedGR = new UCActorGR(sameActor,
                this.startingPoint.x, this.startingPoint.y);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;

        return clonedGR;
    }

    @Override
    public boolean edit(EditContext context) {
        Actor originalActor = (Actor) getComponent();

        // Delegate to centralized helper: conflict check handled by Edit class
        return editStringPropertyWithDialog(
                context,
                "Actor Editor",
                "Actor Name:",
                originalActor,
                Actor::getName,
                Actor::setName,
                Actor::clone,
                (original, redo, model) -> new EditActorEdit(
                        original,
                        redo,
                        model),
                newName -> newName != null
                        && !newName.isEmpty()
                        && context.getRepository().getActor(newName) != null,
                "There is an existing actor with the given name already!\n");
    }
}
