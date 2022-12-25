package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//AssociationGR.java
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.w3c.dom.Element;

@JsonIncludeProperties({ "from", "to", "internalid", "association" })
public class AssociationGR extends LinkGR {

    private Association association;
    // the graphical classes that the association line connects in the diagram
    @JsonProperty("from")
    protected ClassifierGR classA;
    @JsonProperty("to")
    protected ClassifierGR classB;
    private Font nameFont;
    private Font roleFont;

    public AssociationGR(ClassifierGR a, ClassifierGR b, Association assoc) {
        super(a, b);
        classA = a;
        classB = b;
        association = assoc;
        outlineColor = Color.black;
        highlightColor = Color.blue;
        nameFont = new Font("SansSerif", Font.PLAIN, 12);
        roleFont = new Font("SansSerif", Font.PLAIN, 10);
    }

    protected ClassifierGR getClassifierA() {
        return this.classA;
    }

    protected ClassifierGR getClassifierB() {
        return this.classB;
    }

    public int getTopLeftXA() {
        return (int) classA.getStartingPoint().getX();
    }

    public int getTopLeftYA() {
        return (int) classA.getStartingPoint().getY();
    }

    public int getTopLeftXB() {
        return (int) classB.getStartingPoint().getX();
    }

    public int getTopLeftYB() {
        return (int) classB.getStartingPoint().getY();
    }

    public int getWidthA() {
        return classA.getWidth();
    }

    public int getWidthB() {
        return classB.getWidth();
    }

    public int getHeightA() {
        return classA.getHeight();
    }

    public int getHeightB() {
        return classB.getHeight();
    }

    public void draw(Graphics2D g) {
        classA.refreshDimensions(g);
        classB.refreshDimensions(g);

        if (!isReflective()) {
            super.draw(g);
        }

        // if (outlineColor == null)
        // outlineColor = this.myColor();
        int xA = getXA();
        int yA = getYA();
        int xB = getXB();
        int yB = getYB();
        double angleA = getAngleRoleA();
        double angleB = getAngleRoleB();

        Stroke originalStroke = g.getStroke();

        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        if (!isReflective()) {
            g.drawLine(xA, yA, xB, yB);

            // METHOD drawArrowHeads(g) TO BE OVERRIDED BY AGGREGATIONGR AND COMPOSITIONGR
            drawArrowHeads(g);

            g.setPaint(Color.black);

            // draw the association name string
            g.setFont(nameFont);

            // determine the coordinates of the line center
            int centerX = (xA + xB) / 2;
            int centerY = (yA + yB) / 2;
            String name = association.getName();
            int labelDirection = association.getLabelDirection();
            double angle;
            if (labelDirection == Association.FROM_A_TO_B) {
                angle = angleA;
            } else {
                angle = angleB;
            }
            if ((name != null) && !name.equals("")) {
                if (association.getDirection() != Association.BA) {

                    // draw the association name with arrow from role A to role B
                    drawAssociationName(centerX, centerY, angle, name, association.getShowArrow(), g);
                } else {

                    // draw the association name with arrow from role B to role A
                    drawAssociationName(centerX, centerY, angle, name, association.getShowArrow(), g);
                }
            }

            // draw role names and multiplicities
            g.setFont(roleFont);
            drawRoleString(xA, yA, angleA, association.getRoleA().getMultiplicity(), association.getRoleA().getName(),
                    true, g);
            drawRoleString(xB, yB, angleB, association.getRoleB().getMultiplicity(), association.getRoleB().getName(),
                    true, g);

        } else // handle rendering of reflective associations in an ad-hoc way
        {
            GeneralPath reflective = new GeneralPath();
            int step = getReflectiveStep();
            reflective.moveTo(xA, yA);
            reflective.lineTo(xA, yA - REFLECTIVE_UP * step); // up 2
            reflective.lineTo(xA + REFLECTIVE_RIGHT * step, yA - REFLECTIVE_UP * step); // right 4
            reflective.lineTo(xA + REFLECTIVE_RIGHT * step, yB); // down 4
            reflective.lineTo(xB, yB); // left 2

            g.draw(reflective);

            drawArrowHeadsReflective(g);

            g.setPaint(Color.black);

            // draw the association name string
            g.setFont(nameFont);
            // g.drawString(association.getName(), getXA(), getTopLeftYA() - REFLECTIVE_UP *
            // getReflectiveStep() - 2);
            double angle = 0;
            if (association.getLabelDirection() == Association.FROM_A_TO_B) {
                angle = Math.toRadians(0);
            } else {
                angle = Math.toRadians(180);
            }
            drawAssociationName(xA + REFLECTIVE_RIGHT * step / 2, yA - REFLECTIVE_UP * step, angle,
                    association.getName(), association.getShowArrow(), g);

            // draw role names and multiplicities
            g.setFont(roleFont);
            String roleAName = association.getRoleA().getName();
            String roleAMultiplicity = association.getRoleA().getMultiplicity();
            String roleBName = association.getRoleB().getName();
            String roleBMultiplicity = association.getRoleB().getMultiplicity();
            drawRoleString(getXA(), getTopLeftYA() + 5, -Math.PI / 2, roleAMultiplicity, roleAName, true, g);
            drawRoleString(getTopLeftXA() + getWidthA() - 5, getYB(), 0, roleBMultiplicity, roleBName, false, g);
        }

        g.setStroke(originalStroke);

    }

    protected void drawArrowHeads(Graphics2D g) {
        if (association.getDirection() == Association.AB) {
            drawAssociationArrowHead(getXB(), getYB(), getAngleRoleA(), g);
        } else if (association.getDirection() == Association.BA) {
            drawAssociationArrowHead(getXA(), getYA(), getAngleRoleB(), g);
        } else if (association.getDirection() == Association.BIDIRECTIONAL_FIX) {
            drawAssociationArrowHead(getXB(), getYB(), getAngleRoleA(), g);
            drawAssociationArrowHead(getXA(), getYA(), getAngleRoleB(), g);
        }
    }

    protected void drawArrowHeadsReflective(Graphics2D g) {
        int direction = association.getDirection();
        if (direction == Association.AB || direction == Association.BIDIRECTIONAL_FIX) {
            drawAssociationArrowHead(getXB(), getYB(), Math.PI, g);
        }
        if (direction == Association.BA || direction == Association.BIDIRECTIONAL_FIX) {
            drawAssociationArrowHead(getXA(), getYA(), Math.PI / 2, g);
        }
    }

    public void drawAssociationArrowHead(int x, int y, double angle, Graphics2D g) {
        g.translate(x, y);
        g.rotate(angle);
        g.drawLine(-8, 4, 0, 0);
        g.drawLine(-8, -4, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    public void drawAssociationName(int x, int y, double angle, String string, boolean arrow, Graphics2D g) {
        if (string.length() == 0)
            return;

        // modify the angle so that the text is always rotated to go from left to right
        double textAngle = angle;

        if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
            textAngle -= Math.PI;
        }

        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(string, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();
        int textWidth = (int) bounds.getWidth();

        g.translate(x, y);
        g.rotate(textAngle);
        g.drawString(string, -textWidth / 2, -4);
        g.rotate(-textAngle);
        g.translate(-x, -y);

        // draw the name arrow
        if (arrow) {
            drawNameArrow(x, y, angle, textWidth / 2, g);
        }
    }

    // draws the solid triangular arrow that can be along with the association name
    public void drawNameArrow(int x, int y, double angle, int offset, Graphics2D g) {

        // try to always draw the arrow above the association line
        if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
            g.translate(x, y);
            g.rotate(angle);

            GeneralPath triangle = new GeneralPath();

            triangle.moveTo(offset + 5, 5);
            triangle.lineTo(offset + 5, 15);
            triangle.lineTo(offset + 15, 10);
            triangle.closePath();
            g.fill(triangle);
            g.rotate(-angle);
            g.translate(-x, -y);
        } else {
            g.translate(x, y);
            g.rotate(angle);

            GeneralPath triangle = new GeneralPath();

            triangle.moveTo(offset + 5, -5);
            triangle.lineTo(offset + 5, -15);
            triangle.lineTo(offset + 15, -10);
            triangle.closePath();
            g.fill(triangle);
            g.rotate(-angle);
            g.translate(-x, -y);
        }
    }

    public void drawRoleString(int x, int y, double angle, String multiplicity, String roleName, boolean up,
            Graphics2D g) {
        boolean sameDirection = true;

        if ((angle < 3 * Math.PI / 2) && (angle >= Math.PI / 2)) {
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
        int textHeight = (int) bounds.getHeight();

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

    public Association getAssociation() {
        return association;
    }

    public ClassifierGR getClassA() {
        return classA;
    }

    public ClassifierGR getClassB() {
        return classB;
    }


    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute("classa", SystemWideObjectNamePool.getInstance().getNameForObject(classA));
        node.setAttribute("classb", SystemWideObjectNamePool.getInstance().getNameForObject(classB));

        streamer.streamObject(node, "association", association);
    }

    public String toString() {
        return "" + classA + " ---association---> " + classB;
    }

}
