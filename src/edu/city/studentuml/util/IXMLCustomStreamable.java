package edu.city.studentuml.util;

import org.w3c.dom.Element;

public interface IXMLCustomStreamable {

    public void streamToXML(Element node, XMLStreamer streamer);

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance);
}
