package edu.city.studentuml.model.graphical;

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
import java.util.logging.Logger;

import org.w3c.dom.Element;

import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

public class UMLNoteGR extends GraphicalElement {
    private static final Logger logger = Logger.getLogger(UMLNoteGR.class.getName());

    private int paddingHorizontal;
    private int paddingVertical;
    private String text = null;
    private GraphicalElement to;

    private static final int MAXWIDTH = 400;
    private static final int MINWIDTH = 50;
    private static final Font nameFont = new Font("SansSerif", Font.PLAIN, 10);

    public UMLNoteGR(String textualContent, GraphicalElement connectedTo, Point start) {
        if (connectedTo == null) {
            logger.severe(() -> "Note: " + textualContent + " at Point: " + start.getX() + ", " + start.getY() + " not connected to an element!");
        }
        to = connectedTo;
        startingPoint = start;
        text = textualContent;

        width = MAXWIDTH;
        height = 50;
        fillColor = null;
        outlineColor = Color.black;
        highlightColor = Color.blue;

        paddingHorizontal = 10;
        paddingVertical = 15;
    }

    @Override
    public void draw(Graphics2D g) {

        if (fillColor == null) {
            fillColor = GraphicalElement.lighter(this.myColor());
        }

        // refresh dimensions; first calculate width and then height based on width
        this.width = calculateWidth(g);
        this.height = calculateHeight(g);

        // REPLACE super.draw(g) because only UMLNoteGR should show user
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));
        
        if (!this.myUid.equals(Constants.DESKTOP_USER)) {
            g.drawString("user: " + this.myUid, getX(), getY() - 5);
        }

        g.setStroke(new BasicStroke(0.3f));
        Rectangle2D toBounds = to.getBounds();

        // Draw connecting line
        
        g.setStroke(new BasicStroke(0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, ConstantsGR.DASHES, 0));
        g.drawLine(getX() + getWidth() / 2, getY() + getHeight() / 2, (int) toBounds.getCenterX(), (int) toBounds.getCenterY());

        // Draw note shape
        GeneralPath shape = new GeneralPath();
        shape.moveTo(getX(), getY());
        shape.lineTo(getX() + getWidth() - 15.0, getY());
        shape.lineTo(getX() + getWidth() + 0.0, getY() + 15.0);
        shape.lineTo(getX() + getWidth() + 0.0, getY() + getHeight() + 0.0);
        shape.lineTo(getX(), getY() + getHeight() + 0.0);
        shape.closePath();

        shape.moveTo(getX() + getWidth() - 15.0, getY());
        shape.lineTo(getX() + getWidth() - 15.0, getY() + 15.0);
        shape.lineTo(getX() + getWidth() + 0.0, getY() + 15.0);

        g.setPaint(fillColor);
        g.fill(shape);

        g.setStroke(new BasicStroke(1.2f));

        Stroke originalStroke = g.getStroke();
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
        
        String noteText = getNoteText();
        String[] lines = noteText.split("\\R");

        for(int i=0; i < lines.length; i++) {
            drawLineOfText(g, pen, lines[i]);
        }

        g.setStroke(originalStroke);
    }

    private int calculateWidth(Graphics2D g) {
        String noteText = getNoteText();

        String[] lines = noteText.split("\\R");

        int width = 0;
        for(int i=0; i < lines.length; i++) {
            width = Math.max(width, calculateLineWidth(g, lines[i]));
        } 
        
        // adjust width according to text width
        // except if it is longer than MAXWIDTH or shorter than MINWIDTH
        // Since doubles are truncated and nextLayout has rounding problems
        // add 2 pixels so that it is guaranteed that text fits as expected       
        return Math.min(MAXWIDTH, Math.max(MINWIDTH, width)) + 2;
    }

    private int calculateHeight(Graphics2D g) {
        String noteText = getNoteText();

        String[] lines = noteText.split("\\R");

        int height = 0;
        for(int i=0; i < lines.length; i++) {
            height += calculateLineHeight(g, lines[i]);
        }
        return (height + 2 * paddingVertical);
    }

    private int calculateLineHeight(Graphics2D g, String noteText) {
        LineBreakMeasurer measurer = getLineBreakMeasurer(g, noteText);

        int textHeight = 0;

        // read line after line
        while (true) {
            TextLayout layout = measurer.nextLayout(getWidth() - 2.0f * paddingHorizontal);

            if (layout == null) {
                break;
            }

            // The ascent is the distance from the top (right) of the TextLayout to the baseline. 
            // The descent is the distance from the baseline to the bottom (left) of the TextLayout. 
            // The leading is the suggested interline spacing for this TextLayout
            
            // don't merege the two following lines together, due to rounding effects
            textHeight += layout.getAscent() ; 
            textHeight +=  layout.getDescent();
        }

        return textHeight;
    }

    private void drawLineOfText(Graphics2D g, Point pen, String noteText) {
        LineBreakMeasurer measurer = getLineBreakMeasurer(g, noteText);

        // read line after line
        while (true) {
            TextLayout layout = measurer.nextLayout(getWidth() - 2.0f * paddingHorizontal);

            if (layout == null) {
                break;
            }
            pen.y += layout.getAscent();
            float dx = 0;

            dx = paddingHorizontal;
            layout.draw(g, pen.x + dx, pen.y);
            pen.y += layout.getDescent(); 
        }
    }
    
    private int calculateLineWidth(Graphics2D g, String noteText) {
         // AVOID 
        // java.lang.IllegalArgumentException: Can't add attribute to 0-length text      
        noteText += " ";
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(noteText, nameFont, frc);
        Rectangle2D bounds = layout.getBounds();

        return (int) ((bounds.getWidth()) + 2 * paddingHorizontal);
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

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        super.streamFromXML(node, streamer, instance);
        startingPoint.x = Integer.parseInt(node.getAttribute("x"));
        startingPoint.y = Integer.parseInt(node.getAttribute("y"));
        text = node.getAttribute("textData");
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        node.setAttribute("x", Integer.toString(startingPoint.x));
        node.setAttribute("y", Integer.toString(startingPoint.y));
        node.setAttribute("textData", text);

        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(to));
    }

    private String getNoteText() {
        String noteText;
        if (getText() == null || getText().trim().equals("")) {
            noteText = " ";
        } else {
            noteText = getText();
        }
        return noteText;
    }

    private LineBreakMeasurer getLineBreakMeasurer(Graphics2D g, String noteText) {
        // AVOID 
        // java.lang.IllegalArgumentException: Can't add attribute to 0-length text
        noteText += " ";

        HashMap<TextAttribute, Font> amap = new HashMap<>();
        amap.put(TextAttribute.FONT, nameFont);

        return new LineBreakMeasurer(
            new AttributedString(noteText, amap).getIterator(),
            BreakIterator.getLineInstance(),
            g.getFontRenderContext());
    }    

}



