
package edu.city.studentuml.model.graphical;

import java.awt.geom.GeneralPath;

public class Triangle {
    GeneralPath path;

    public Triangle() {
        path = new GeneralPath();

        path.moveTo(0, 0);
        path.lineTo(-10, -5);
        path.lineTo(-10, 5);
        path.closePath();
    }

    public GeneralPath get() {
        return path;
    }

}
