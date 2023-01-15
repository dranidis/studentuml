package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.w3c.dom.Element;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class UseCaseGR extends LeafUCDElementGR {

    private static final String SANS_SERIF = "Sans Serif";
    private Font useCaseFont = new Font(SANS_SERIF, Font.PLAIN, 12);
    private Font extensionPointLabelFont = new Font(SANS_SERIF, Font.BOLD, 10);
    private Font extensionPointFont = new Font(SANS_SERIF, Font.PLAIN, 8);
    private static final Dimension MIN = new Dimension(100, 50);
    private static final int VGAP_BETWEEN_EXTENSION_POINTS = 6;
    private static final int VGAP_BETWEEN_USE_CASE_NAME_AND_EXTENSION_POINTS = 20;
    private static final int HEIGHT_OF_LINE = 2;
    private static final int VGAP_BETWEEN_LINE_AND_EXTENSION_POINTS =
            VGAP_BETWEEN_USE_CASE_NAME_AND_EXTENSION_POINTS / 2
            - HEIGHT_OF_LINE / 2;
    private static final String EXTENSION_POINTS_LABEL = "Extension Points";

    public UseCaseGR(UseCase useCase, int x, int y) {
        super(useCase, x, y);

        width = MIN.width;
        height = MIN.height;

        outlineColor = Color.black;
        highlightColor = Color.blue;
        fillColor = myColor();
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint use case
        g.setPaint(fillColor);
        g.fillOval(startingX, startingY, width, height);

        g.setStroke(new BasicStroke(1.2f));
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(3));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }
        // draw the use case
        g.drawOval(startingX, startingY, width, height);

        if (getNumberOfExtensionPoints() == 0) {
            // draw use case name
            g.setPaint(outlineColor);
            String useCaseName = getComponent().getName();
            if (useCaseName == null || useCaseName.length() == 0) {
                useCaseName = " ";
            }

            Rectangle2D bounds = getBounds(g, useCaseName, useCaseFont);

            int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
            int nameY = ((height - (int) bounds.getHeight()) / 2) - (int) bounds.getY();

            g.setFont(useCaseFont);
            g.drawString(useCaseName, startingX + nameX, startingY + nameY);
        } else {
            drawUseCaseNameAndExtensionPoints(g);
        }
    }

    private int getNumberOfExtensionPoints() {
        int counter = 0;

        for (UCLinkGR link : getIncomingRelations()) {
            if (link instanceof UCExtendGR) {
                UCExtendGR extend = (UCExtendGR) link;
                counter = counter + extend.getNumberOfExtensionPoints();
            }            
        }

        return counter;
    }

    private void drawUseCaseNameAndExtensionPoints(Graphics2D g) {
        // draw line
        g.setPaint(outlineColor);
        g.drawLine(getX(), getY() + height / 2, getX() + width, getY() + height / 2);

        // draw use case name
        String useCaseName = getComponent().getName();
        if (useCaseName == null || useCaseName.length() == 0) {
            useCaseName = " ";
        }

        Rectangle2D bounds = getBounds(g, useCaseName, useCaseFont);

        int nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        int nameY = ((height - (int) bounds.getHeight()) / 2)
                - (int) bounds.getY() - VGAP_BETWEEN_LINE_AND_EXTENSION_POINTS;
        int x = getX() + nameX;
        int y = getY() + nameY;

        g.setFont(useCaseFont);
        g.drawString(useCaseName, x, y);

        // draw extansion points label
        bounds = getBounds(g, EXTENSION_POINTS_LABEL, extensionPointLabelFont);

        nameX = ((width - (int) bounds.getWidth()) / 2) - (int) bounds.getX();
        nameY += VGAP_BETWEEN_USE_CASE_NAME_AND_EXTENSION_POINTS;
        x = getX() + nameX;
        y = getY() + nameY;

        g.setFont(extensionPointLabelFont);
        g.drawString(EXTENSION_POINTS_LABEL, x, y);

        // get the largest width of all extension points
        int largest = 0;

        for (UCLinkGR link : getIncomingRelations()) {
            if (link instanceof UCExtendGR) {
                UCExtendGR extend = (UCExtendGR) link;

                Iterator<ExtensionPoint> j = extend.getExtensionPoints();
                while (j.hasNext()) {
                    String s = (j.next()).getName();
                    if (s.length() > 0) {
                        bounds = getBounds(g, s, extensionPointFont);
                        if (bounds.getWidth() > largest) {
                            largest = (int) bounds.getWidth();
                        }
                    }
                }
            }
        }

        // draw extension points
        g.setFont(extensionPointFont);

        for (UCLinkGR link : getIncomingRelations()) {
            if (link instanceof UCExtendGR) {
                UCExtendGR extend = (UCExtendGR) link;

                Iterator<ExtensionPoint> j = extend.getExtensionPoints();
                while (j.hasNext()) {
                    String s = (j.next()).getName();
                    if (s.length() > 0) {
                        bounds = getBounds(g, s, extensionPointFont);

                        nameX = ((width - largest) / 2) - (int) bounds.getX();
                        nameY += VGAP_BETWEEN_EXTENSION_POINTS + bounds.getHeight();
                        x = getX() + nameX;
                        y = getY() + nameY;

                        g.drawString(s, x, y);
                    }
                }
            }
        }
    }

    public int calculateWidth(Graphics2D g) {
        if (getNumberOfExtensionPoints() == 0) {
            String useCaseName = getComponent().getName();
            if (useCaseName == null || useCaseName.length() == 0) {
                useCaseName = " ";
            }

            Rectangle2D bounds = getBounds(g, useCaseName, useCaseFont);

            int newWidth = MIN.width;
            if (bounds.getWidth() > newWidth) {
                newWidth = ((int) bounds.getWidth()) + ((int) bounds.getWidth()) / 2;
            }

            width = newWidth;

            return width;
        } else {
            return calculateComplexWidth(g);
        }
    }

    private int calculateComplexWidth(Graphics2D g) {
        // check the width of use case name first
        double multiplier = 2;
        String useCaseName = getComponent().getName();
        if (useCaseName == null || useCaseName.length() == 0) {
            useCaseName = " ";
        }

        Rectangle2D bounds = getBounds(g, useCaseName, useCaseFont);

        int newWidth = 0;
        if (bounds.getWidth() > newWidth) {
            newWidth = ((int) bounds.getWidth());
        }

        // check the extension point label
        bounds = getBounds(g, EXTENSION_POINTS_LABEL, extensionPointLabelFont);

        if (bounds.getWidth() > newWidth) {
            newWidth = ((int) bounds.getWidth());
        }

        // check every extension string
        for (UCLinkGR link : getIncomingRelations()) {
            if (link instanceof UCExtendGR) {
                UCExtendGR extend = (UCExtendGR) link;

                Iterator<ExtensionPoint> j = extend.getExtensionPoints();
                while (j.hasNext()) {
                    String s = (j.next()).getName();
                    if (s.length() > 0) {
                        bounds = getBounds(g, s, extensionPointFont);

                        if (bounds.getWidth() > newWidth) {
                            multiplier += 1.5;
                            newWidth = ((int) bounds.getWidth());
                        }
                    }
                }
            }

        }

        width = (int) (newWidth * multiplier);
        return width;
    }

    public int calculateHeight(Graphics2D g) {
        if (getNumberOfExtensionPoints() == 0) {
            String useCaseName = getComponent().getName();
            if (useCaseName == null || useCaseName.length() == 0) {
                useCaseName = " ";
            }

            Rectangle2D bounds = getBounds(g, useCaseName, useCaseFont);

            int newHeight = MIN.height;
            if (bounds.getHeight() > newHeight) {
                newHeight = ((int) bounds.getHeight()) + ((int) bounds.getHeight()) / 2;
            }

            height = newHeight;

            return height;
        } else {
            return calculateComplexHeight(g);
        }
    }

    private int calculateComplexHeight(Graphics2D g) {
        int newHeight = HEIGHT_OF_LINE / 2 + VGAP_BETWEEN_LINE_AND_EXTENSION_POINTS;

        Rectangle2D bounds = getBounds(g, EXTENSION_POINTS_LABEL, extensionPointLabelFont);

        newHeight += ((int) bounds.getHeight()) + VGAP_BETWEEN_EXTENSION_POINTS;

        for (UCLinkGR link : getIncomingRelations()) {
            if (link instanceof UCExtendGR) {
                UCExtendGR extend = (UCExtendGR) link;

                Iterator<ExtensionPoint> j = extend.getExtensionPoints();
                while (j.hasNext()) {
                    String s = (j.next()).getName();
                    if (s.length() > 0) {
                        bounds = getBounds(g, s, extensionPointFont);
                        newHeight += bounds.getHeight() + VGAP_BETWEEN_EXTENSION_POINTS;
                    }
                }
            }
        }

        height = newHeight * 2;

        return height;
    }

    private Rectangle2D getBounds(Graphics2D g, String s, Font f) {
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(s, f, frc);
        return layout.getBounds();
    }

    @Override
    public boolean contains(Point2D p) {
        return new Ellipse2D.Double(getX(), getY(),
                width, height).contains(p);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObject(node, "useCase", getComponent());
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
    }
}
