/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml.util;

import java.awt.geom.Point2D;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dimitris
 */
public class RotateTest {
    /**
     * Test of transform method, of class Rotate.
     */
    @Test
    public void testTransformZero() {
        double x = 0.0;
        double y = 0.0;
        Rotate instance = new Rotate(0, 0, 0);
        Point2D.Double expResult = new Point2D.Double(0, 0);

        Point2D.Double result = instance.transform(x, y);
        assertEquals(expResult, result);
        
        x = 5.0;
        y = 11.9;
        expResult = new Point2D.Double(x, y);
        result = instance.transform(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testTransform90() {
        double precision = 0.001;
        double x = 10.0;
        double y = 0.0;
        Rotate instance = new Rotate(90, 0, 0);
        Point2D.Double expResult = new Point2D.Double(0, 10);

        Point2D.Double result = instance.transform(x, y);
        assertEquals(expResult.x, result.x, precision);
        assertEquals(expResult.y, result.y, precision);
    }
    
    @Test
    public void testTransformMinus45() {
        double precision = 0.001;
        double x = 10.0;
        double y = 20.0;
        Rotate instance = new Rotate(-45, -10, 30);
        Point2D.Double expResult = new Point2D.Double(-2.928932188134521, 8.786796564403575);

        Point2D.Double result = instance.transform(x, y);
        assertEquals(expResult.x, result.x, precision);
        assertEquals(expResult.y, result.y, precision);
    }
    
}
