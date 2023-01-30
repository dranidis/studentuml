package edu.city.studentuml;

import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import edu.city.studentuml.model.graphical.AssociationGR;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author dimitris
 */
public class AbstractLinGRTest {

    public AbstractLinGRTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getEndPointFromTest() {

        double myW = 70;
        double myH = 60;
        double myX = 100;
        double myY = 100;

        double d = 200;

        /**
         * Point inside box, relative to box origin
         */
        // center
        double centerX = 35;
        double centerY = 30;

        AssociationGR assoc;
        assoc = new AssociationGR(null, null, null);
        // simulate changing the x inside the box
        for (int yoff = 30; yoff >= -30; yoff -= 10) {

            for (int xoff = 30; xoff >= -30; xoff -= 10) {
                double orX = centerX + xoff;
                double orY = centerY + yoff;
                // simulate a relationship between this class and another in many different
                // angles
                for (int angle = 0; angle < 360; angle += 10) {
                    double angle2 = Math.toRadians(angle);
                    double dx = d * Math.cos(angle2);
                    double dy = d * Math.sin(angle2);
                    // System.out.printf("Angle: %3.2f dx: %3.2f dy: %3.2f ORIGIN: %3f, %3f %n",
                    // Math.toDegrees(angle2), dx, dy, orX, orY);

                    Point2D p = assoc.getEndPointFrom(orX, orY, myW, myH, myX, myY, dx, dy);
                    boolean onBoxX = (p.getY() >= myY && p.getY() <= myY + myH)
                            && (equalDouble(p.getX(), myX) || equalDouble(p.getX(), myX + myW));
                    boolean onBoxY = (p.getX() >= myX && p.getX() <= myX + myW)
                            && (equalDouble(p.getY(), myY) || equalDouble(p.getY(), myY + myH));
                    // System.out.printf("x: %3.2f y: %3.2f %n", p.getX(), p.getY());
                    assertTrue("X, Y dimension is correct p.getX():" + p.getX() + " p.getY():" + p.getY(),
                            onBoxX || onBoxY);
                }
            }
        }

    }

    boolean equalDouble(double x, double y) {
        double precision = 1;
        return (x >= y - precision) && (x <= y + precision);
    }
}
