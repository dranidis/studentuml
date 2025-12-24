package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.XMLStreamer;

/**
 *
 * @author draganbisercic
 */
public class UCExtend extends UCLink {

    private List<ExtensionPoint> extensionPoints;
    public static final String STEREOTYPE = "<<extend>>";

    public UCExtend(UseCase extending, UseCase extended) {
        super(extending, extended);
        this.name = STEREOTYPE;
        extensionPoints = new ArrayList<>();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int getNumberOfExtensionPoints() {
        return extensionPoints.size();
    }

    @Override
    public void addExtensionPoint(ExtensionPoint extensionPoint) {
        extensionPoints.add(extensionPoint);
    }

    @Override
    public void removeExtensionPoint(ExtensionPoint extensionPoint) {
        extensionPoints.remove(extensionPoint);
    }

    @Override
    public Iterator<ExtensionPoint> getExtensionPoints() {
        return extensionPoints.iterator();
    }

    @Override
    public void clearPoints() {
        extensionPoints.clear();
    }

    @Override
    public int getIndexOfExtensionPoint(ExtensionPoint extensionPoint) {
        return extensionPoints.indexOf(extensionPoint);
    }

    @Override
    public ExtensionPoint getExtensionPointAt(int index) {
        return extensionPoints.get(index);
    }

    @Override
    public UCExtend clone() {
        UCExtend copy = new UCExtend((UseCase) this.getSource(), (UseCase) this.getTarget());

        Iterator<ExtensionPoint> i = getExtensionPoints();
        while (i.hasNext()) {
            copy.addExtensionPoint(i.next().clone());
        }

        return copy;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        super.streamFromXML(node, streamer, instance);
        streamer.streamChildrenFrom(streamer.getNodeById(node, "extensionpoints"), this);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);
        streamer.streamObjects(streamer.addChild(node, "extensionpoints"), getExtensionPoints());
    }
}
