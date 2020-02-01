package edu.city.studentuml.util;

import edu.city.studentuml.model.graphical.DiagramModel;
import java.awt.Rectangle;

/**
 *
 * @author dimitris
*/
public class FrameProperties {

    public final Rectangle R;
    public final boolean selected;
    public final boolean iconified;
    public DiagramModel model;

    public FrameProperties(DiagramModel model, Rectangle R, boolean selected, boolean iconified) {
        this.model = model;
        this.R = R;
        this.selected = selected;
        this.iconified = iconified;
    }
}