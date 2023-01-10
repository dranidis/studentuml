package edu.city.studentuml.util;

import java.awt.geom.Rectangle2D;

/**
 * A class that represents a rectangle with positive dimensions.
 * 
 * <p>The {@code PositiveRectangle} class is a wrapper around the {@link Rectangle2D} class
 * that ensures that the rectangle always has positive width and height. It does this by
 * swapping the minimum and maximum x-coordinates and y-coordinates if necessary.
 * 
 * <p>The {@code PositiveRectangle} class provides a single method, {@link #getRectangle2D()},
 * which returns a {@code Rectangle2D} object with the same dimensions as the positive rectangle.
 * 
 * @author Dimitris Dranidis
 */
public class PositiveRectangle {
    private double minX;
    private double maxX; 
    private double minY;
    private double maxY;

    public PositiveRectangle(double startX, double startY, double endX, double endY) {
        if (endX < startX) {
            minX = endX;
            maxX = startX;
        } else {
            minX = startX;
            maxX = endX;
        }
        if (endY < startY) {
            minY = endY;
            maxY = startY;
        } else {
            minY = startY;
            maxY = endY;
        }
    }

    public Rectangle2D getRectangle2D() {
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public String toString() {
        return "[" + minX + ", " + minY + " -> " + maxX + ", " + maxY + "]";
    }
    
}
