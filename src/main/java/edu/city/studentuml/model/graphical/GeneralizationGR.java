package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;

@JsonIncludeProperties({ "internalid", "from", "to", "generalization" })
public class GeneralizationGR extends LinkGR {

    private Generalization generalization;
    // the graphical classes that the generalization line connects in the diagram

    public GeneralizationGR(ClassifierGR parent, ClassifierGR child, Generalization gener) {
        super(child, parent);
        generalization = gener;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */    
    public Generalization getGeneralization() {
        return generalization;
    }

    @JsonProperty("to")
    public ClassifierGR getSuperClass() {
        return b;
    }

    @JsonProperty("from")
    public ClassifierGR getBaseClass() {
        return a;
    }

    @Override
    protected void drawArrowHead(int bX, int bY, double rotationAngle, Graphics2D g) {
        GraphicsHelper.drawWhiteArrowHead(bX, bY, rotationAngle, g);
    }

    @Override
    protected BasicStroke makeStroke() {
        return GraphicsHelper.makeSolidStroke();
    }

    @Override
    protected BasicStroke makeSelectedStroke() {
        return GraphicsHelper.makeSelectedSolidStroke();
    }

    // generalizations cannot be reflective
    @Override
    public boolean isReflective() {
        return false;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // empty
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        node.setAttribute("base", SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute("super", SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "generalization", generalization);
    }

    @Override
    public String toString() {
        return "" + a + " ---generalization---> " + b;
    }

    public void setGeneralization(Generalization generalization) {
        this.generalization = generalization;
    }      
    
}
