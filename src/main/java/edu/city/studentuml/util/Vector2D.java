package edu.city.studentuml.util;

import java.awt.Point;

/**
 *
 * @author draganbisercic
 */
public class Vector2D {

    private double x;
    private double y;

    public Vector2D(double xx, double yy) {
        x = xx;
        y = yy;
    }

    public Vector2D(int xx, int yy) {
        x = xx;
        y = yy;
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public Vector2D divide(double scalar) {
        return new Vector2D(x / scalar, y / scalar);
    }

    public Vector2D add(Vector2D otherVector) {
        return new Vector2D(x + otherVector.x, y + otherVector.y);
    }

    public Vector2D subtract(Vector2D otherVector) {
        return new Vector2D(x - otherVector.x, y - otherVector.y);
    }

    public Point add(Point point) {
        return new Point((int)(point.getX() + x), (int)(point.getY() + y));
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D getUnitVector() {
        double length = getLength();
        return new Vector2D(x / length, y / length);
    }

    public void normalize() {
        double length = getLength();
        x /= length;
        y /= length;
    }

    public Vector2D getNormal() {
        return new Vector2D(-y, x);
    }

    public double dot(Vector2D otherVector) {
        return x * otherVector.x + y * otherVector.y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
