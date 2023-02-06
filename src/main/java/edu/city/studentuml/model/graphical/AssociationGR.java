package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

@JsonIncludeProperties({ "from", "to", "internalid", "association" })
public class AssociationGR extends LinkGR {

    private Association association;
    // the graphical classes that the association line connects in the diagram
    private Font nameFont;
    private Font roleFont;

    public AssociationGR(ClassifierGR a, ClassifierGR b, Association assoc) {
        super(a, b);
        association = assoc;

        nameFont = new Font("SansSerif", Font.PLAIN, 12);
        roleFont = new Font("SansSerif", Font.PLAIN, 10);
    }

    @Override
    protected int getLinkDirection() {
        return association.getDirection();
    }

    @Override
    protected void drawReflective(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        GeneralPath reflective = new GeneralPath();
        float step = getReflectiveStep();
        reflective.moveTo(aX, aY);
        reflective.lineTo(aX, aY - REFLECTIVE_UP * step); // up 2
        reflective.lineTo(aX + REFLECTIVE_RIGHT * step, aY - REFLECTIVE_UP * step); // right 4
        reflective.lineTo(aX + REFLECTIVE_RIGHT * step, bY); // down 4
        reflective.lineTo(bX, bY); // left 2

        g.draw(reflective);

        drawArrowHeadsReflective(g);

        g.setPaint(getOutlineColor());

        // draw the association name string
        g.setFont(nameFont);

        double angle = 0;
        if (association.getLabelDirection() == Association.FROM_A_TO_B) {
            angle = Math.toRadians(0);
        } else {
            angle = Math.toRadians(180);
        }
        drawAssociationName((int) (aX + REFLECTIVE_RIGHT * step / 2), (int) (aY - REFLECTIVE_UP * step), angle,
                association.getName(), association.getShowArrow(), g);

        // draw role names and multiplicities
        g.setFont(roleFont);
        String roleAName = association.getRoleA().getName();
        String roleAMultiplicity = association.getRoleA().getMultiplicity();
        String roleBName = association.getRoleB().getName();
        String roleBMultiplicity = association.getRoleB().getMultiplicity();
        drawRoleString(aX, getTopLeftYA() + 5, -Math.PI / 2, roleAMultiplicity, roleAName, true, g);
        drawRoleString(getTopLeftXA() + getWidthA() - 5, bY, 0, roleBMultiplicity, roleBName, false, g);
    }

    @Override
    protected void drawRoles(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        // draw role names and multiplicities
        g.setFont(roleFont);
        drawRoleString(aX, aY, angleA, association.getRoleA().getMultiplicity(), association.getRoleA().getName(),
                true, g);
        drawRoleString(bX, bY, angleB, association.getRoleB().getMultiplicity(), association.getRoleB().getName(),
                true, g);
    }

    @Override
    protected void drawName(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        // determine the coordinates of the line center
        int centerX = (aX + bX) / 2;
        int centerY = (aY + bY) / 2;
        String name = association.getName();
        int labelDirection = association.getLabelDirection();
        double angle;
        if (labelDirection == Association.FROM_A_TO_B) {
            angle = angleA;
        } else {
            angle = angleB;
        }
        if (name != null && !name.equals("")) {
            // draw the association name with arrow from role A to role B
            drawAssociationName(centerX, centerY, angle, name, association.getShowArrow(), g);
        }
    }

    protected void drawArrowHeadsReflective(Graphics2D g) {
        int direction = association.getDirection();
        if (direction == Association.AB || direction == Association.BIDIRECTIONAL_FIX) {
            drawArrowHead(getXB(), getYB(), Math.PI, g);
        }
        if (direction == Association.BA || direction == Association.BIDIRECTIONAL_FIX) {
            drawArrowHead(getXA(), getYA(), Math.PI / 2, g);
        }
    }

    @Override
    protected void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    private void drawAssociationName(int x, int y, double angle, String string, boolean arrow, Graphics2D g) {
        GraphicsHelper.drawString(string, x, y, angle, arrow, g);
    }

    private void drawRoleString(int x, int y, double angle, String multiplicity, String roleName, boolean up,
            Graphics2D g) {
        boolean sameDirection = true;

        if (GraphicsHelper.angleGreaterThanHalfPi(angle)) {
            angle -= Math.PI;
            sameDirection = false;
        }
        StringBuilder sb = new StringBuilder();
        if (sameDirection) {
            sb.append(multiplicity).append(" ").append(roleName);
        } else {
            sb.append(roleName).append(" ").append(multiplicity);
        }
        g.translate(x, y);
        g.rotate(angle);

        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(sb.toString(), roleFont, frc);
        Rectangle2D bounds = layout.getBounds();
        int xOffset = (int) bounds.getX();
        int yOffset = (int) bounds.getY();
        int textWidth = (int) bounds.getWidth();

        if (sameDirection) {
            if (up) {
                g.drawString(sb.toString(), 12 + xOffset, -5);
            } else {
                g.drawString(sb.toString(), 12 + xOffset, 5 - yOffset);
            }
        } else {
            if (up) {
                g.drawString(sb.toString(), -12 - textWidth - xOffset, -5);
            } else {
                g.drawString(sb.toString(), -12 - textWidth - xOffset, 5 - yOffset);
            }
        }

        g.rotate(-angle);
        g.translate(-x, -y);
    }

    public boolean isReflective() {
        return association.isReflective();
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public Association getAssociation() {
        return association;
    }

    @JsonProperty("from")
    public ClassifierGR getClassA() {
        return a;
    }

    @JsonProperty("to")
    public ClassifierGR getClassB() {
        return b;
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "association", association);
    }

    @Override
    public String toString() {
        return "" + a + " ---association---> " + b + " " + super.toString();
    }

}
