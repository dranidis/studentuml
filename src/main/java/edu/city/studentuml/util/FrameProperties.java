package edu.city.studentuml.util;

import edu.city.studentuml.model.graphical.DiagramModel;
import java.awt.Rectangle;

/**
 *
 * @author dimitris
*/
public class FrameProperties {

    public final Rectangle rectangle;
    public final boolean selected;
    public final boolean iconified;
    public final DiagramModel model;
    public final double scale;

    public FrameProperties(DiagramModel model, Rectangle rectangle, boolean selected, boolean iconified, double scale) {
        this.model = model;
        this.rectangle = rectangle;
        this.selected = selected;
        this.iconified = iconified;
        this.scale = scale;
    }
}