package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Classifier;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public interface ClassifierGR {

    public abstract Classifier getClassifier();

    public abstract Point getStartingPoint();

    public abstract int getX();

    public abstract int getY();

    public abstract int getHeight();

    public abstract int getWidth();

    // the graphical classifer has to adjust dimensions
    // according to the graphics drawing context
    public abstract void refreshDimensions(Graphics2D g);

    public abstract Color getFillColor();

    public abstract Color getOutlineColor();

    public abstract Color getHighlightColor();

    public abstract void setSelected(boolean sel);

    public abstract boolean isSelected();
    
}
