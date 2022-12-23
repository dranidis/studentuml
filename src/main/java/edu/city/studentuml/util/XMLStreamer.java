package edu.city.studentuml.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;

public class XMLStreamer {

    private static final Logger logger = Logger.getLogger(XMLStreamer.class.getName());

    private Document doc = null;
    private boolean fromFile = false;

    public XMLStreamer() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            doc = impl.createDocument("", "uml", null);
            // String packageName = this.getClass().getPackage().getName(); //FIXME: PACKAGE
            // doc.getDocumentElement().setAttribute("package", packageName); //FIXME:
            // PACKAGE
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadString(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlString))); // use parse(InputSource), where
                                                                               // InputSource comes from String

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TO DO: possibly remove
    public void loadURL(String urlString) {
        try {
            // Send data
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer xmlResponse = new StringBuffer();

            while ((line = rd.readLine()) != null) {
                xmlResponse.append(line);
                xmlResponse.append("\r");
            }

            rd.close();

            loadString(xmlResponse.toString());

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.getStackTrace());
        }
    }

    public String saveToString() {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;

        String xmlString = null;

        try {
            StringWriter writer = new StringWriter();

            transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            writer.close();
            xmlString = writer.toString();
        } catch (TransformerConfigurationException e) {
            JOptionPane.showMessageDialog(null, e.toString());

            // e.printStackTrace();
        } catch (TransformerException e) {
            JOptionPane.showMessageDialog(null, e.toString());

            // e.printStackTrace();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.toString());

            // ioe.printStackTrace();
        }

        return xmlString;
    }

    // TO DO: possibly remove
    public void saveToURL(String urlString) {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;

        String xmlString = saveToString();

        try {
            // Send data
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).setRequestMethod("POST");
                ((HttpURLConnection) conn).setRequestProperty("Content-Length",
                        "" + Integer.toString(xmlString.getBytes().length));
            } else {
                System.out.println("Not HTTP!");
            }

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(xmlString);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                JOptionPane.showMessageDialog(null, line);
            }
            wr.close();

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.toString());

            // ioe.printStackTrace();
        }
    }

    public Element getNodeById(Element parent, String id) {
        if (parent == null) {
            parent = doc.getDocumentElement();
        }

        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child instanceof Element) {
                if (id.equals(((Element) child).getAttribute("id"))) {
                    return (Element) child;
                }
            }
        }
        return null;
    }

    public Element addChild(Element parent, String id) {
        Element e = doc.createElement("object");
        e.setAttribute("id", id);
        if (parent == null) {
            parent = doc.getDocumentElement();
        }
        parent.appendChild(e);
        return e;
    }

    public void streamObject(Element node, String id, Object o) {
        if (o == null) {
            return;
        }

        if (o instanceof IXMLCustomStreamable) {
            Element child = addChild(node, id);
            child.setAttribute("class", o.getClass().getSimpleName());
            // child.setAttribute("class", o.getClass().getCanonicalName()); //FIXME:
            // PACKAGE
            child.setAttribute("id", id);
            String internalID = SystemWideObjectNamePool.getInstance().getNameForObject(o);
            if (internalID != null) {
                child.setAttribute("internalid", internalID);
            }
            ((IXMLCustomStreamable) o).streamToXML(child, this);
        }
    }

    public void streamFrom(Element e, Object instance) {
        if (instance instanceof IXMLCustomStreamable) {
            ((IXMLCustomStreamable) instance).streamFromXML(e, this, instance);
        }
    }

    public void streamObjects(Element parent, Iterator i) {
        while (i.hasNext()) {
            Object o = i.next();
            streamObject(parent, "", o);
        }
    }

    public IXMLCustomStreamable readObjectByID(Element node, String id, Object parent) {
        Element child = getNodeById(node, id);
        if (child != null) {
            IXMLCustomStreamable object = ObjectFactory.getInstance()
                    .newInstance(((Element) child).getAttribute("class"), parent, (Element) child, this);
            if (object != null) {
                object.streamFromXML((Element) child, this, object);
                return object;
            }
        }
        return null;
    }

    // Streams objects from node to vector v using instance
    public void streamObjectsFrom(Element node, Vector v, Object instance) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            if (child instanceof Element) {
                IXMLCustomStreamable object = ObjectFactory.getInstance()
                        .newInstance(((Element) child).getAttribute("class"), instance, (Element) child, this);
                if (object != null) {
                    object.streamFromXML((Element) child, this, object);
                }
            }
        }
    }

    // for undo/redo
    public void loadFromString(String data) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            doc = builder.parse(new InputSource(new StringReader(data)));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for undo/redo
    public String streamToString() {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter wri = new StringWriter();
            StreamResult result = new StreamResult(wri);
            transformer.transform(source, result);
            return wri.toString();

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void refreshElements(HashMap objectList) {
        Iterator t = objectList.keySet().iterator();
        while (t.hasNext()) {
            Object o = t.next();

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr;
            try {
                expr = xpath.compile("//object[@internalid='" + (String) objectList.get(o) + "']");
                Object result = expr.evaluate(doc, XPathConstants.NODE);
                Node nodes = (Node) result;

                if (nodes != null) {
                    if (o instanceof IXMLCustomStreamable) {
                        ((IXMLCustomStreamable) o).streamFromXML((Element) nodes, this, o);
                    }
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
    }

    public void streamObjects(Iterator i) {
        streamObjects(doc.getDocumentElement(), i);
    }

    // for application
    public void loadFile(String filename) {
        fromFile = true;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            doc = builder.parse(new File(filename));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(String xml) {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xml));
            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
