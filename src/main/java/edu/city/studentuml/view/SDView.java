package edu.city.studentuml.view;

import java.awt.Graphics2D;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.DiagramModel;

public class SDView extends DiagramView {

    public SDView(DiagramModel m) {
        super(m);
    }

    @Override
    public void drawDiagram(Graphics2D g) {
        /*
         * necessary because SD diagrams use colors for validating
         * the messages. When changing the theme colors are not updated correctly
         * outlinecolor remains the same as in the previous theme
         */
        AbstractSDModel m = (AbstractSDModel) model;
        m.sortUpdateRankAndLifeLengthsAndValidateInOutMessages();

        super.drawDiagram(g);
    }
}
