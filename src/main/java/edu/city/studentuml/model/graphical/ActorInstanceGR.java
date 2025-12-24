package edu.city.studentuml.model.graphical;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 * 
 * @author Ervin Ramollari
 */
public class ActorInstanceGR extends AbstractSDObjectGR {

    private static final int ACTOR_TEXT_DISTANCE = 8;
    private static final int STICKFIGURE_HEIGHT = 35;
    private static final int STICKFIGURE_WIDTH = 20;

    public ActorInstanceGR(ActorInstance actor, int x) {
        super(actor, x);
        width = STICKFIGURE_WIDTH;
        height = STICKFIGURE_HEIGHT;
    }

    @Override
    public boolean contains(Point2D point) {

        // The portion including the stick figure and description underneath
        Rectangle2D rectangle1 = new Rectangle2D.Double(getX(), getY(), width, height);

        // The portion including the life line
        Rectangle2D rectangle2 = new Rectangle2D.Double(getX() + width / 2.0 - 8, getY() + height + 4.0, 16,
                endingY - (getY() + height + 4.0));

        return rectangle1.contains(point) || rectangle2.contains(point);
    }

    @Override
    protected void drawObjectShape(Graphics2D g, int startingX, int startingY) {
        GraphicsHelper.drawStickFigure(g, startingX + (width / 2), startingY, isSelected(), getFillColor(), getOutlineColor(), getHighlightColor());
    }

    @Override
    protected void drawObjectName(Graphics2D g, int startingX, int startingY) {
        String actorText = roleClassifier.toString();
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(actorText, nameFont, frc);
        int nameY = STICKFIGURE_HEIGHT/2 + ACTOR_TEXT_DISTANCE - (int) bounds.getY();

        super.drawObjectName(g, startingX, startingY + nameY);
    }

    public ActorInstance getActorInstance() {
        return (ActorInstance) roleClassifier;
    }

    public void setActorInstance(ActorInstance ai) {
        roleClassifier = ai;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "actor", getActorInstance());
        node.setAttribute("x", Integer.toString(startingPoint.x));
    }

    @Override
    public ActorInstanceGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ActorInstance sameActorInstance = getActorInstance();
        
        // Create new graphical wrapper referencing the SAME domain object
        ActorInstanceGR clonedGR = new ActorInstanceGR(sameActorInstance, this.startingPoint.x);
        
        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;
        clonedGR.endingY = this.endingY;
        
        return clonedGR;
    }
}
