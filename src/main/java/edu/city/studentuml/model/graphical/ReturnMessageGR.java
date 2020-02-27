package edu.city.studentuml.model.graphical;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//ReturnMessageGR.java
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.SDMessage;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.prefs.Preferences;

import org.w3c.dom.Element;

public class ReturnMessageGR extends SDMessageGR implements IXMLCustomStreamable {

    public ReturnMessageGR(RoleClassifierGR from, RoleClassifierGR to, ReturnMessage message, int y) {
        super(from, to, message, y);
    }

    public Stroke getStroke() {
        float dashes[] = {8};    // the pattern of dashes for drawing the return line

        return new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashes, 0);
    }

    public void drawMessageArrow(int x, int y, boolean forward, Graphics2D g) {
        if (forward) {
            g.drawLine(x, y, x - 8, y - 4);
            g.drawLine(x, y, x - 8, y + 4);
        } else {
            g.drawLine(x, y, x + 8, y - 4);
            g.drawLine(x, y, x + 8, y + 4);
        }
    }

    public void draw(Graphics2D g) {
        boolean showReturnPref = Preferences.userRoot().get("SHOW_RETURN_SD", "").equals("TRUE") ? true : false;
        
        if (showReturnPref) {
            super.draw(g);
            return;
        }

        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(new BasicStroke(5));
            g.setPaint(highlightColor);
        } else {
            g.setStroke(new BasicStroke(1));
            g.setPaint(outlineColor);
        }

        int startingX = getStartingX();

        g.drawLine(startingX - barWidth/2, getY(), startingX + barWidth/2, getY());
        // restore the original stroke
        g.setStroke(originalStroke);
    }    
    
    public boolean contains(Point2D point) {
        boolean showReturnPref = Preferences.userRoot().get("SHOW_RETURN_SD", "").equals("TRUE") ? true : false;
        
        if (showReturnPref) {
            return super.contains(point);
        }

        if (!getMessage().isReflective()) {
            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX() - 10, getY() - 10, 20, 20);

            return bounds.contains(point);
        } else {

            // construct the rectangle defining the message line
            Rectangle2D bounds = new Rectangle2D.Double(getStartingX(), getY(), 40, 15);

            return bounds.contains(point);
        }
    }
    
    
    public ReturnMessage getReturnMessage() {
        return (ReturnMessage) getMessage();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("from", SystemWideObjectNamePool.getInstance().getNameForObject(getSource()));
        node.setAttribute("to", SystemWideObjectNamePool.getInstance().getNameForObject(getTarget()));
        node.setAttribute("y", Integer.toString(getY()));
        streamer.streamObject(node, "message", getReturnMessage());
    }
}
