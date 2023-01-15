package edu.city.studentuml.util;

import java.awt.Point;

/**
 *
 * @author draganbisercic
 */
public class Ray {

    private Point origin;
    private Vector2D direction;

    public Ray(Point origin, Vector2D direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }
}
