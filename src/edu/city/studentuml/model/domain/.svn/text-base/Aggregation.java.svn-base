package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
//Author: Ramollari Ervin
//Aggregation.java
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.XMLStreamer;
import java.io.Serializable;

import org.w3c.dom.Element;

public class Aggregation extends Association implements Serializable, IXMLCustomStreamable {

    private boolean isStrong;    // true if composition, false if simple aggregation

    public Aggregation(Classifier w, Classifier p) {
        super(w, p);
    }

    public Aggregation(Classifier w, Classifier p, boolean composition) {
        super(w, p);
        isStrong = composition;
    }

    public Aggregation(Role w, Role p) {
        super(w, p);
    }

    public Aggregation(Role w, Role p, boolean composition) {
        super(w, p);
        isStrong = composition;
    }

    public void setStrong(boolean composition) {
        isStrong = composition;
    }

    public Classifier getWhole() {
        return getClassA();
    }

    public Classifier getPart() {
        return getClassB();
    }

    public boolean isStrong() {
        return isStrong;
    }

    public boolean isReflective() {
        return (getWhole() == getPart());
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        // TODO Auto-generated method stub
        super.streamFromXML(node, streamer, instance);

        setStrong(Boolean.valueOf(node.getAttribute("strong")));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        // TODO Auto-generated method stub
        node.setAttribute("strong", Boolean.toString(isStrong));

        super.streamToXML(node, streamer);
    }
}
