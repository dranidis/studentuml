package edu.city.studentuml.util;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

public class PositiveRectangleTest {

    @Test
    public void testPositiveRectangle() {
        PositiveRectangle rect = new PositiveRectangle(10, 20, 30, 40);
        Rectangle2D r2d = rect.getRectangle2D();
        assertEquals(10, r2d.getMinX(), 0.0001);
        assertEquals(20, r2d.getMinY(), 0.0001);
        assertEquals(20, r2d.getWidth(), 0.0001);
        assertEquals(20, r2d.getHeight(), 0.0001);
    }

    @Test
    public void testPositiveRectangleReversed() {
        PositiveRectangle rect = new PositiveRectangle(30, 40, 10, 20);
        Rectangle2D r2d = rect.getRectangle2D();
        assertEquals(10, r2d.getMinX(), 0.0001);
        assertEquals(20, r2d.getMinY(), 0.0001);
        assertEquals(20, r2d.getWidth(), 0.0001);
        assertEquals(20, r2d.getHeight(), 0.0001);
    }
}