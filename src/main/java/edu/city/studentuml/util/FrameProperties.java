package edu.city.studentuml.util;

import java.awt.Rectangle;

import edu.city.studentuml.model.graphical.DiagramModel;

/**
 * @author dimitris
 */
public class FrameProperties {

    public final Rectangle rectangle;
    public final boolean selected;
    public final boolean iconified;
    public final DiagramModel model;
    public final double scale;
    public final boolean maximized;
    public final int zOrder;

    public FrameProperties(DiagramModel model, Rectangle rectangle, boolean selected, boolean iconified, double scale, boolean maximized, int zOrder) {
        this.model = model;
        this.rectangle = rectangle;
        this.selected = selected;
        this.iconified = iconified;
        this.scale = scale;
        this.maximized = maximized;
        this.zOrder = zOrder;
    }
}