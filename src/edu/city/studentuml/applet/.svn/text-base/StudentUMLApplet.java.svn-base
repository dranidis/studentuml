package edu.city.studentuml.applet;

import edu.city.studentuml.view.gui.ApplicationApplet;
import edu.city.studentuml.view.gui.ApplicationGUI;
import javax.swing.JApplet;

/**
 *
 * @author draganbisercic
 */
public class StudentUMLApplet extends JApplet {

    private static StudentUMLApplet instance = null;
    private ApplicationGUI applicationGUI;
    private ServerInterface serverInterface;
    private String username = null;
    private String exid = null;
    private String nodeid = null;
    private String parentid = null;
    private String diagramno = null;
    private String apiURL = null;

    public static StudentUMLApplet getInstance() {
        return instance;
    }

    public void init() {
        super.init();
        initializeApplet();
        instance = this;
        applicationGUI = new ApplicationApplet(this);
    }
    
    private void initializeApplet() {
        username = getParameter("username");
        exid = getParameter("exid");
        nodeid = getParameter("nodeid");
        parentid = getParameter("parentid");
        diagramno = getParameter("diagramno");
        apiURL = getParameter("api_base");
        
        serverInterface = new ServerInterface(apiURL, getParameter("auth_token"));
    }

    public void start() {

    }

    public void stop() {

    }

    public void destroy() {

    }

    public ServerInterface getServerInterface() {
        return serverInterface;
    }

    public void setServerInterface(ServerInterface serverInterface) {
        this.serverInterface = serverInterface;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getDiagramno() {
        return diagramno;
    }

    public void setDiagramno(String diagramno) {
        this.diagramno = diagramno;
    }

    public String getExid() {
        return exid;
    }

    public void setExid(String exid) {
        this.exid = exid;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
