package edu.city.studentuml.model.graphical;

import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;

import javax.swing.JLabel;

import org.w3c.dom.Element;

public class UMLNoteGR extends GraphicalElement implements IXMLCustomStreamable {

    static final BreakIterator LINE_BREAK = BreakIterator.getLineInstance();
    private static Font nameFont = new Font("SansSerif", Font.PLAIN, 12);
    private int paddingHorizontal;
    private int paddingVertical;
    private String text = null;
    private JLabel textRender = new JLabel();
    private GraphicalElement to;

    public UMLNoteGR(String textualContent, GraphicalElement connectedTo, Point start) {
        to = connectedTo;
        startingPoint = start;
        text = textualContent;

        width = 150;
        height = 50;
        fillColor = null;
        outlineColor = Color.black;
        highlightColor = Color.blue;

        paddingHorizontal = 10;
        paddingVertical = 15;
    }

    public void draw(Graphics2D g) {

        if (fillColor == null) {
            fillColor = GraphicalElement.lighter(this.myColor());
        }

        // refresh dimensions; first calculate width and then height based on width
        calculateWidth(g);
        calculateHeight(g);

        Stroke originalStroke = g.getStroke();

        super.draw(g);

        // REPLACE super.draw(g) because only UMLNoteGR should show user
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g.drawString("user: " + this.myUid, getX(), getY() - 5);

        g.setStroke(new BasicStroke(0.3f));
        Rectangle2D toBounds = to.getBounds();

        // Draw connecting line
        float dashes[] = {8};
        g.setStroke(new BasicStroke(0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0));
        g.drawLine(getX() + getWidth() / 2, getY() + getHeight() / 2, (int) toBounds.getCenterX(), (int) toBounds.getCenterY());

        // Draw note shape
        GeneralPath shape = new GeneralPath();
        shape.moveTo(getX(), getY());
        shape.lineTo(getX() + getWidth() - 15, getY());
        shape.lineTo(getX() + getWidth(), getY() + 15);
        shape.lineTo(getX() + getWidth(), getY() + getHeight());
        shape.lineTo(getX(), getY() + getHeight());
        shape.closePath();

        shape.moveTo(getX() + getWidth() - 15, getY());
        shape.lineTo(getX() + getWidth() - 15, getY() + 15);
        shape.lineTo(getX() + getWidth(), getY() + 15);

        g.setPaint(fillColor);
        g.fill(shape);

        g.setStroke(new BasicStroke(1.2f));

        originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(2));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(originalStroke);
            g.setPaint(outlineColor);
        }

        g.draw(shape);
        g.setStroke(originalStroke);
        g.setPaint(outlineColor);

        // Draw the text
        Point pen = new Point(getX(), getY() + paddingVertical);
        g.setColor(outlineColor);

        HashMap amap = new HashMap();
        amap.put(TextAttribute.FONT, nameFont);

        String noteText;

        if (getText() == null || getText().equals("")) {
            noteText = " ";
        } else {
            noteText = getText();
        }

        LineBreakMeasurer measurer = new LineBreakMeasurer(
                new AttributedString(noteText, amap).getIterator(),
                LINE_BREAK,
                g.getFontRenderContext());

        // read line after line
        while (true) {
            TextLayout layout = measurer.nextLayout(getWidth() - 2 * paddingHorizontal);

            if (layout == null) {
                break;
            }
            pen.y += layout.getAscent();
            float dx = 0;

            // centered text
            if (layout.isLeftToRight()) {
                dx = getWidth() - layout.getAdvance();
            }
            layout.draw(g, pen.x + dx / 2, pen.y);
            pen.y += layout.getDescent() + layout.getLeading();
        }

        g.setStroke(originalStroke);
    }

    protected int calculateHeight(Graphics2D g) {
        HashMap amap = new HashMap();
        amap.put(TextAttribute.FONT, nameFont);

        String noteText;

        if (getText() == null || getText().equals("")) {
            noteText = " ";
        } else {
            noteText = getText();
        }

        LineBreakMeasurer measurer = new LineBreakMeasurer(
                new AttributedString(noteText, amap).getIterator(),
                LINE_BREAK,
                g.getFontRenderContext());

        int textHeight = 0;

        // read line after line
        while (true) {
            TextLayout layout = measurer.nextLayout(getWidth() - 2 * paddingHorizontal);

            if (layout == null) {
                break;
            }

            textHeight += layout.getAscent() + layout.getDescent() + layout.getLeading();
        }

        int newHeight = textHeight + 2 * paddingVertical;

        if (this.height != newHeight) {
            this.height = newHeight;
        }

        return this.height;
    }

    protected int calculateWidth(Graphics2D g) {
        String noteText;

        if (getText() == null || getText().trim().equals("")) {
            noteText = " ";
        } else {
            noteText = getText();
        }

        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(noteText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        // adjust width according to text width
        // except if it is longer than 150 or shorter than 80
        // Since doubles are truncated and nextLayout has rounding problems
        // add 2 pixels so that it is guaranteed that text fits as expected
        int newWidth = Math.min(150,
                Math.max(80, (int) ((bounds.getWidth()) + 2 * paddingHorizontal))) + 2;

        if (this.width != newWidth) {
            this.width = newWidth;
        }

        return newWidth;
    }

    public void move(int x, int y) {
        startingPoint.setLocation(x, y);
    }

    public boolean contains(Point2D p) {
        Rectangle2D.Double rect = new Rectangle2D.Double(startingPoint.getX(), startingPoint.getY(), getWidth(),
                getHeight());

        return rect.contains(p);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public GraphicalElement getTo() {
        return to;
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
        text = node.getAttribute("textData");
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
        node.setAttribute("textData", text);

        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
    }
}



