package edu.city.studentuml.util;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author draganbisercic
 */
public class Size {

        Point startingPosition = new Point();
        Dimension dimension = new Dimension();

        public Dimension getDimension() {
            return dimension;
        }

        public void setDimension(Dimension dimension) {
            this.dimension = dimension;
        }

        public Point getStartingPosition() {
            return startingPosition;
        }

        public void setStartingPosition(Point startingPosition) {
            this.startingPosition = startingPosition;
        }
    }