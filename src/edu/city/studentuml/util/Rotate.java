package edu.city.studentuml.util;

import java.awt.geom.Point2D;

/**
 *
 * @author dimitris
 */
public class Rotate {
    private double xRotationVector[];
    private double yRotationVector[];
    
    /**
     * Creates a rotate transformation
     * 
     * @param theta angle measures in degrees
     * @param x the x co of the pivot point
     * @param y the y co of the pivot point
     */
    public Rotate(double theta, double x, double y) {
        theta = Math.toRadians(theta);
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        xRotationVector = new double[]{cos,   -1 * sin,   x - x*cos + y*sin};
        yRotationVector = new double[]{sin,   cos,        y - x*sin - y*cos};
    }
    
    /**
     * Applies a rotate transformation to the point x, y
     * 
     * @param x the original x co
     * @param y the original y co
     * @return the transformed point
     */
    public Point2D.Double transform(double x, double y) {
        double[] point = {x, y, 1};
        double tx = dotProduct(xRotationVector, point);
        double ty = dotProduct(yRotationVector, point);
        return new Point2D.Double(tx, ty);
    }

    private double dotProduct(double[] d, double[] point) {
        double cell = 0;
        for (int i = 0; i < point.length; i++) {
            cell += d[i] * point[i];
        }
        return cell;        
    }
    
}
