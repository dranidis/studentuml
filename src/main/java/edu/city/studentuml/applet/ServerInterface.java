package edu.city.studentuml.applet;

// Author: Igor Janevski, Ervin Ramollari;
import edu.city.studentuml.model.domain.UMLProject;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ServerInterface {

    private String toolkey = "studentuml-toolkey-t63r5nlh";
    private String apiURL = null;
    public String currentAuthToken = null;

    public ServerInterface(String apiURL, String authentication_token) {
        this.currentAuthToken = authentication_token;
        this.apiURL = "http://" + apiURL;
    }

    protected Document callAPI(HashMap arguments) throws APICallException, AuthenticationFailedException {
        return callAPI(arguments, 0);
    }

    protected Document callAPI(HashMap arguments, int api_try) throws APICallException, AuthenticationFailedException {
        Document doc = null;

        // System.out.println("Sending "+arguments.toString().replaceAll(", ",
        // "&").replaceAll("\\{", "").replaceAll("\\}","").trim());

        if (this.currentAuthToken != null) {
            if (arguments.get("auth_token") == null) {
                System.out.println("Using cached authentication token...");
                arguments.put("auth_token", this.currentAuthToken);
            }
        }

        ClientHttpRequest conn;
        try {
            conn = new ClientHttpRequest(this.apiURL);
            // System.out.println(this.apiURL);
            conn.setParameters(arguments);
            InputStream result = conn.post();
            Map<String, List<String>> headers = conn.getHeaderFields();

            if (headers.get("Set-Cookie") != null) {
                List<String> cookies = headers.get("Set-Cookie");
                for (int i = 0; i < cookies.size(); i++) {
                    String cookie = cookies.get(i);
                    if (cookie.startsWith("auth_token")) {
                        String thistoken = cookie.split(";")[0].split("=")[1].trim();
                        if (thistoken != this.currentAuthToken) {
                            System.out.println("Got new token from the connection cookie...");
                            this.currentAuthToken = thistoken;
                        }
                    }
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(result));
            String line;
            StringBuffer xmlResponse = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                xmlResponse.append(line);
                xmlResponse.append("\r");
            }

            reader.close();

            String response = xmlResponse.toString().trim();
            // System.out.println("|||"+response+"|||");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(response)));

            // Process the doc
            Node resultNode = doc.getElementsByTagName("result").item(0);
            String resultString = resultNode.getTextContent().trim();

            if (resultString.equals("error")) {
                Node errorNode = doc.getElementsByTagName("error").item(0);
                String errorString = errorNode.getTextContent().trim();

                throw new APICallException(errorString);
            }
        } catch (IOException e) {
            if (e.getMessage().indexOf("HTTP response code: 401") > 0) {
                if ((arguments.get("auth_username") == null) || (api_try < 3)) {
                    LoginPanel loginPanel = new LoginPanel();

                    if (!loginPanel.showDialog(null, "Authentication try #" + (api_try + 1))) {
                        System.out.println("Authentication Failed, API CALL Failed (Pressed cancel)....");
                        throw new AuthenticationFailedException();
                    }

                    String user = loginPanel.getUsername();
                    String password = loginPanel.getPassword();

                    if ((user != null) && (password != null)) {
                        System.out.println("AUTHENTICATE try# " + api_try);
                        arguments.put("auth_username", user);
                        arguments.put("auth_password", password);
                        return callAPI(arguments, api_try + 1);
                    } else {
                        System.out.println("Authentication Failed, API CALL Failed (Null username/password)....");
                        throw new AuthenticationFailedException();
                    }
                } else {
                    System.out.println("Authentication Failed, API CALL Failed (Null auth token)....");
                    throw new AuthenticationFailedException();
                }

            }
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /*************************
     * GETTING A NODE
     ************************/
    private HashMap getBaseApi(final String module, final String action) {
        HashMap rez = new HashMap<String, String>() {

            {
                put("module", module);
                put("action", action);
                put("toolkey", toolkey);
            }
        };
        return rez;
    }

    private HashMap getBaseNodeAction(String action) {
        HashMap rez = getBaseApi("node", action);
        return rez;
    }

    public Document getNode(int nodeid) throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("getnodeinfo");
        args.put("nodeid", nodeid);
        Document doc = callAPI(args);
        return doc;
    }

    public Document getSolutionFileFromFileID(int fileid) throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseApi("solutionfile", "getsolutionfilebyid");
        args.put("solutionfileid", fileid);
        Document doc = callAPI(args);
        return doc;
    }

    // DO WE NEED THIS?
    public Document getSolutionFileFromNodeID(int nodeid) throws APICallException, AuthenticationFailedException {
        Document doc1 = getNode(nodeid);

        Node fileIDNode = doc1.getElementsByTagName("solutionfileid").item(0);
        String fileIDString = fileIDNode.getTextContent().trim();

        Document doc2 = getSolutionFileFromFileID(Integer.valueOf(fileIDString));

        return doc2;
    }

    /*************************
     * SAVING A NODE
     ************************/
    public Document createSolutionFile(String solution) throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseApi("solutionfile", "create");
        args.put("solutionfile", solution);
        return callAPI(args);
    }

    public Document saveNodeBySolutionFileID(String username, int exid, int parentid, String title, String comment,
            String nodetype, boolean isprivate, int solutionfileid)
            throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("create");
        args.put("exerciseid", exid);
        args.put("parentnodeid", parentid);
        args.put("authornickname", username);
        args.put("nodetitle", title);
        args.put("nodedescription", comment);
        args.put("isprivate", (isprivate ? "1" : "0"));
        args.put("nodetype", nodetype);
        args.put("solutionfileid", solutionfileid);
        return callAPI(args);
    }

    public Document saveNode(String username, int exid, int parentid, String title, String comment, String nodetype,
            boolean isprivate, String solution, String pic) throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("create");
        args.put("exerciseid", exid);
        args.put("parentnodeid", parentid);
        args.put("authornickname", username);
        args.put("nodetitle", title);
        args.put("nodedescription", comment);
        args.put("isprivate", (isprivate ? "1" : "0"));
        args.put("nodetype", nodetype);
        args.put("solutionfile", solution);
        args.put("pic", pic);
        return callAPI(args);
    }

    public Document saveNodeImage(int nodeid, int number, String scr, String thumb)
            throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("savenodeimage");
        args.put("nodeid", nodeid);
        args.put("number", number);
        args.put("screenshot", scr);
        args.put("thumbnail", thumb);
        return callAPI(args);
    }

    /*
     * public Document saveNode(int uid, int parentid, int exid, int status, String
     * title, String comment, boolean isprivate, String solution) { String getString
     * = "?module=node&action=save&toolkey=" + toolkey + "&authorid=" + uid;
     * 
     * String postString = null;
     * 
     * postString = "&parentid=" + parentid + "&exerciseid=" + exid + "&userid=" +
     * uid + "&title=" + title + "&comment=" + comment + "&statusid=" + status +
     * "&isprivate=" + (isprivate ? "1" : "0") + "&solution=" + solution;
     * 
     * return callAPI(getString, postString); }
     */
    public Document getStatuses() throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("getallsolutionstates");
        // quck & dirty patch
        Document result = callAPI(args);
        NodeList statuses = result.getElementsByTagName("status");
        Vector toRemove = new Vector();

        for (int i = 0; i < statuses.getLength(); i++) {
            // if (SystemWideObjectNamePool.umlProject.getParentid() > 0) {
            if (UMLProject.getInstance().getParentid() > 0) {

                // statuses.item(i).getParentNode().removeChild(statuses.item(i));
                toRemove.add(statuses.item(i));

            } else {
                if (!statuses.item(i).getTextContent().trim().equals("opendiscussion")) {
                    // statuses.item(i).getParentNode().removeChild(statuses.item(i));
                    toRemove.add(statuses.item(i));
                }
            }

        }

        for (int i = 0; i < toRemove.size(); i++) {
            Node t = (Node) toRemove.get(i);
            t.getParentNode().removeChild(t);
        }

        return result;
    }

    public Document getNodeTypes() throws APICallException, AuthenticationFailedException {
        HashMap args = this.getBaseNodeAction("getallnodetypes");
        Document result = callAPI(args);
        NodeList statuses = result.getElementsByTagName("type");
        Vector toRemove = new Vector();
        for (int i = 0; i < statuses.getLength(); i++) {
            // if (SystemWideObjectNamePool.umlProject.getParentid() == 0) {
            if (UMLProject.getInstance().getParentid() == 0) {
                toRemove.add(statuses.item(i));
            }
        }

        for (int i = 0; i < toRemove.size(); i++) {
            Node t = (Node) toRemove.get(i);
            t.getParentNode().removeChild(t);
        }

        return result;
    }
}
