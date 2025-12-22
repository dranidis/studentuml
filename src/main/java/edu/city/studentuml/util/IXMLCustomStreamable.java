package edu.city.studentuml.util;

import org.w3c.dom.Element;

public interface IXMLCustomStreamable {

    void streamToXML(Element node, XMLStreamer streamer);

    void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable;
}
