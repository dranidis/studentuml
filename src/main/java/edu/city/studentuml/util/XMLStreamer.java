package edu.city.studentuml.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLStreamer {

    private static final Logger logger = Logger.getLogger(XMLStreamer.class.getName());

    private Document doc = null;

    private List<String> errorStrings = new ArrayList<>();

    public List<String> getErrorStrings() {
        return errorStrings;
    }

    public XMLStreamer() {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            doc = impl.createDocument("", "uml", null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadString(String xmlString) {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
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
            StringBuilder xmlResponse = new StringBuilder();

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
        String xmlString = null;

        try {
            Transformer transformer = getTransformer();

            StringWriter writer = new StringWriter();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            writer.close();
            xmlString = writer.toString();
        } catch (TransformerException | IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return xmlString;
    }

    // TO DO: possibly remove
    public void saveToURL(String urlString) {

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
                logger.severe("Not HTTP!");
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
        }
    }

    /**
     * Returns the first child of parent that matches the id. If called with a null
     * parent the parent is set to the doc top element
     * 
     * @param parent
     * @param id
     * @return
     */
    public Element getNodeById(Element parent, String id) {
        if (parent == null) {
            parent = doc.getDocumentElement();
        }

        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node child = parent.getChildNodes().item(i);
            if (child instanceof Element && id.equals(((Element) child).getAttribute("id"))) {
                return (Element) child;
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
            child.setAttribute(XMLSyntax.CLASS, o.getClass().getSimpleName());
            child.setAttribute("id", id);
            String internalID = SystemWideObjectNamePool.getInstance().getNameForObject(o);
            if (internalID != null) {
                child.setAttribute(XMLSyntax.INTERNALID, internalID);
            } else {
                logger.finer(() -> "Null internalid for " + o.getClass().getName() + " : " + o.toString());
            }
            ((IXMLCustomStreamable) o).streamToXML(child, this);
        }
    }

    public void streamObjects(Element parent, Iterator<?> i) {
        i.forEachRemaining(o -> streamObject(parent, "", o));
    }

    public IXMLCustomStreamable readObjectByID(Element node, String id, Object parent) throws NotStreamable {
        Element child = getNodeById(node, id);
        if (child != null) {
            return createAndReadObject(child, parent);
        }
        return null;
    }

    private IXMLCustomStreamable createAndReadObject(Element child, Object parent) throws NotStreamable {
        IXMLCustomStreamable object = null;

        try {
            object = ObjectFactory.getInstance().newInstance(child.getAttribute(XMLSyntax.CLASS), parent, child, this);
        } catch (NotStreamable e) {
            logger.severe("child: \n" + elementToString(child)
                    + ", internalid: " + child.getAttribute(XMLSyntax.INTERNALID));
            errorStrings.add(elementToString(child));
        }
        if (object != null) {
            object.streamFromXML(child, this, object);
            return object;
        }
        return null;
    }

    public String elementToString(Element node) {
        StringWriter buffer = new StringWriter();
        try {
            getTransformer().transform(new DOMSource(node), new StreamResult(buffer));
        } catch (TransformerException | TransformerFactoryConfigurationError e1) {
            e1.printStackTrace();
        }
        /*
         * remove the first part <?xml .....>
         */
        String xml = buffer.toString();
        int firstIndex = xml.indexOf("<");

        return xml.substring(xml.indexOf("<", firstIndex + 1));
    }

    /**
     * Called with an element which has many children. It creates and reads all the
     * children of the element.
     * 
     * @param element
     * @param parent
     * @throws NotStreamable
     */
    public void streamChildrenFrom(Element element, Object parent) throws NotStreamable {
        if (element == null) {
            String parentInfo = (parent != null ? parent.getClass().getName() : "null");
            logger.severe("Cannot stream children from null element. Parent: " + parentInfo);
            throw new NotStreamable("Invalid XML structure: missing required element for " + parentInfo);
        }

        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            Node child = element.getChildNodes().item(i);
            if (child instanceof Element) {
                createAndReadObject((Element) child, parent);
            }
        }
    }

    // for undo/redo
    public void loadFromString(String data) {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(data)));

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    // for undo/redo
    public String streamToString() {
        try {
            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(doc);
            StringWriter wri = new StringWriter();
            StreamResult result = new StreamResult(wri);
            transformer.transform(source, result);
            return wri.toString();

        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    // for application
    public void loadFile(String filename) throws IOException {
        try {
            DocumentBuilder builder;
            builder = getDocumentBuilder();
            File file = new File(filename);

            doc = builder.parse(file);
        } catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(String xml) {
        try {
            Transformer transformer = getTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xml));
            transformer.transform(source, result);

        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        return factory.newDocumentBuilder();
    }

    private Transformer getTransformer()
            throws TransformerFactoryConfigurationError, TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer;
        transformer = tFactory.newTransformer();
        return transformer;
    }
}
