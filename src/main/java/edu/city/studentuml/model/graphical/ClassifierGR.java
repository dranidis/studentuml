package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.Classifier;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public interface ClassifierGR {

    Classifier getClassifier();

    Point getStartingPoint();

    int getX();

    int getY();

    int getHeight();

    int getWidth();

    // the graphical classifer has to adjust dimensions
    // according to the graphics drawing context
    void refreshDimensions(Graphics2D g);

    Color getFillColor();

    Color getOutlineColor();

    Color getHighlightColor();

    void setSelected(boolean sel);

    boolean isSelected();
    
}
