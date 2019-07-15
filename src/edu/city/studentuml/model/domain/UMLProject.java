package edu.city.studentuml.model.domain;

//~--- JDK imports ------------------------------------------------------------
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
//import edu.city.studentuml.applet.Application;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import static java.lang.System.out;

import org.w3c.dom.Element;

import com.sun.istack.internal.logging.Logger;

public class UMLProject extends Observable implements Serializable, Observer, IXMLCustomStreamable {

    private static UMLProject ref = null;
    private NotifierVector diagramModels;
    private CentralRepository repository;
    private Boolean projectSaved = true;
    private String user;
    //for applet
    private int status;
    private int exid;
    private int parentid;
    private String nodeType;
    private String title;
    private String comment;
    private Mode mode;
    //for desktop application
    private String projectFilename = "";
    private String projectFilepath = "";
    private String projectName = "";

    protected UMLProject() {
        ref = this;
        projectInit();
    }

    protected UMLProject(String Filepath, String filename) {
        projectInit();
    }

    private void projectInit() {
        repository = new CentralRepository();
        diagramModels = new NotifierVector();
        repository.addObserver(this);

        //applet
        title = "";
        comment = "";
        status = 0;
        nodeType = "";

        //application
        projectName = "New Project";
    }

    public static UMLProject getInstance() {
        if (ref == null) {
            ref = new UMLProject();
        }
        return ref;
    }

    public void clear() {
        //ApplicationGUI.closeFrames();
        diagramModels.clear();
        repository.clear();
        SystemWideObjectNamePool.getInstance().clear();
        projectChanged();
    }

    public void becomeObserver() {
        DiagramModel model;
        Iterator iterator = diagramModels.iterator();

        while (iterator.hasNext()) {
            model = (DiagramModel) iterator.next();
            model.addObserver(this);
        }

        repository.addObserver(this);
    }

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public DiagramModel getDiagramModel(int index) {
        return (DiagramModel) diagramModels.elementAt(index);
    }

    public Vector getDiagramModels() {
        return diagramModels;
    }

    public Boolean isSaved() {
        return projectSaved;
    }

    public void setSaved(Boolean saved) {
        projectSaved = saved;
        setChanged();
        notifyObservers();
    }

    public void addDiagram(DiagramModel dm) {
        diagramModels.add(dm);
        dm.addObserver(this);
        setSaved(false);
    }

    public void removeDiagram(DiagramModel dm) {
        diagramModels.remove(dm);
        setSaved(false);
    }

    public void projectChanged() {
        setChanged();
        notifyObservers();
    }

    public void update(Observable observable, Object object) {
        projectChanged();
    }

    public void loadFromXML(String filename) {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFile(filename);

        Element e = streamer.getNodeById(null, "project");
        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        streamer.finishedParsing();

        projectChanged();
    }
    // Embed4Auto

    public void loadFromURL(String url) {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadURL(url);

        Element e = streamer.getNodeById(null, "project");

        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        streamer.finishedParsing();

        projectChanged();
    }

    //for undo/redo
    public void loadFromXMLString(String xmlString) {

        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFromString(xmlString);

        Element e = streamer.getNodeById(null, "project");
        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();
        streamer.finishedParsing();

        projectChanged();
    }

    public void streamToXML(String path) {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, "project", this);

        if (ApplicationGUI.isApplet()) {
            streamer.saveToURL(path);
        } else {
            streamer.saveToFile(path);
        }
    }

    //for undo/redo
    public String streamToXMLString() {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, "project", this);
        return streamer.streamToString();
    }

    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) {
        diagramModels.clear();
        streamer.streamObjectsFrom(node, diagramModels, instance);
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        streamer.streamObjects(node, diagramModels.iterator());
    }

    public boolean isClassReferenced(GraphicalElement el, AbstractClass abstractClass) {
        if (abstractClass == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof CCDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ConceptualClassGR) {
                        if (currEl != el && ((ConceptualClassGR) currEl).getConceptualClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }

            if (dm instanceof DCDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ClassGR) {
                        if (currEl != el && ((ClassGR) currEl).getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }

            if (dm instanceof SDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof SDObjectGR) {
                        if (currEl != el && ((SDObjectGR) currEl).getSDObject().getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                    if (currEl instanceof MultiObjectGR) {
                        if (currEl != el && ((MultiObjectGR) currEl).getMultiObject().getDesignClass() == abstractClass) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isActorReferenced(GraphicalElement el, Actor actor) {
        if (actor == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof AbstractSDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof ActorInstanceGR) {
                        if (currEl != el && ((ActorInstanceGR) currEl).getActorInstance().getActor() == actor) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isSystemReferenced(GraphicalElement el, System system) {
        if (system == null) {
            return false;
        }

        Iterator it = diagramModels.iterator();
        DiagramModel dm = null;

        while (it.hasNext()) {
            dm = (DiagramModel) it.next();
            NotifierVector grElements = dm.getGraphicalElements();

            if (dm instanceof SSDModel) {
                for (int i = 0; i < grElements.size(); i++) {
                    GraphicalElement currEl = (GraphicalElement) grElements.get(i);
                    if (currEl instanceof SystemInstanceGR) {
                        if (currEl != el && ((SystemInstanceGR) currEl).getSystemInstance().getSystem() == system) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setExid(int exid) {
        this.exid = exid;
    }

    public int getExid() {
        return exid;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getParentid() {
        return parentid;
    }

    // application
    public String getName() {
        return projectName;
    }

    public void setName(String name) {
        projectName = name;
        setSaved(false);
    }

    public String getFilename() {
        return projectFilename;
    }

    public void setFilename(String filename) {
        projectFilename = filename;
        setSaved(false);
    }

    public String getFilepath() {
        return projectFilepath;
    }

    public void setFilepath(String filepath) {
        projectFilepath = filepath;
        setSaved(false);
    }
    
    public static final Logger LOG = Logger.getLogger(UMLProject.class);
    private boolean verboseDocs;
    private boolean lfBeforeCurly;
    private static final boolean VERBOSE_DOCS = false;
    private static final String LINE_SEPARATOR =
	java.lang.System.getProperty("line.separator");
    private static final String LANG_PACKAGE = "java.lang";

    private static final Set<String> JAVA_TYPES;
    static {
	Set<String> types = new HashSet<String>();
	types.add("void");
	types.add("boolean");
	types.add("byte");
	types.add("char");
	types.add("int");
	types.add("short");
	types.add("long");
	types.add("float");
	types.add("double");
	JAVA_TYPES = Collections.unmodifiableSet(types);
    }
    private static boolean isFileGeneration;
    private static boolean isInUpdateMode;
    private static final String INDENT = "  ";
    
    private String generateFile(DesignClass classObject, String path) {
        String name = classObject.getName();
        if (name == null || name.length() == 0) {
            return null;
        }
       // Object classifier = modelElement;
        path =  path + File.separator + this.getName().substring(0,this.getName().lastIndexOf("."));
        String filename = name + ".java";
        StringBuilder sbPath = new StringBuilder(path);
        if (!path.endsWith(File.separator)) {
            sbPath.append(File.separator);
        }

        String packagePath = classObject.getName();
        
        String pathname = sbPath.toString() + filename;
        //cat.info("-----" + pathname + "-----");

        //now decide whether file exist and need an update or is to be
        //newly generated
        File f = new File(pathname);
        if (!f.isDirectory()) {
        	if (!Paths.get(path).toFile().isDirectory()) {
            if (!f.getParentFile().mkdir()) {
                LOG.severe(" could not make directory " + path);
                return null;
            }
          }  
        }
        isFileGeneration = true; // used to produce method javadoc

        //String pathname = path + filename;
        // TODO: package, project basepath, tagged values to configure
        LOG.info("Generating " + f.getPath());
        isFileGeneration = true;
        //String header = generateHeader(classifier, pathname, packagePath);
        String src = generateClassifier(classObject); 
        BufferedWriter fos = null;
        try {
          fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
          //  fos.write(header);
            fos.write(src);
        } catch (IOException exp) {
            LOG.severe("IO Exception: " + exp + ", for file: " + f.getPath());
        } finally {
            isFileGeneration = false;
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException exp) {
                LOG.severe("FAILED: " + f.getPath());
            }
        }

        //cat.info("----- end updating -----");
        return pathname;
    }
    
    private String generateClassifier(DesignClass cls) {
        StringBuffer returnValue = new StringBuffer();
        StringBuffer start = generateClassifierStart(cls);
        if ((start != null) && (start.length() > 0)) {
            StringBuffer body = generateClassifierBody(cls);
            StringBuffer end = generateClassifierEnd(cls);
            returnValue.append(start.toString());
            if ((body != null) && (body.length() > 0)) {
                returnValue.append(LINE_SEPARATOR);
                returnValue.append(body);
                if (lfBeforeCurly) {
                    returnValue.append(LINE_SEPARATOR);
                }
            }
            returnValue.append((end != null) ? end.toString() : "");
        }
        return returnValue.toString();
    }
    
    StringBuffer generateClassifierStart(DesignClass cls) {
        String sClassifierKeyword;
        sClassifierKeyword = "class";

        StringBuffer sb = new StringBuffer(80);

        // Now add visibility, but not for non public top level classifiers
       
        sb.append("public ");

        // add classifier keyword and classifier name
        sb.append(sClassifierKeyword).append(" ");
		sb.append(cls.getName());
		// add type parameters
		
        // add base class/interface
   
        // add implemented interfaces, if needed
        // UML: realizations!
     
        // add opening brace
        sb.append(lfBeforeCurly ? (LINE_SEPARATOR + "{") : " {");

        return sb;
    }
    
    private StringBuffer generateClassifierBody(DesignClass cls) {
        StringBuffer sb = new StringBuffer();
        Vector classAttributes = cls.getAttributes();

            if (!classAttributes.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                sb.append(INDENT).append("// Attributes");
                sb.append(LINE_SEPARATOR);
            }

		boolean first = true;
		for (int i = 0; i < classAttributes.size(); i++) {
		    if (first) {
			sb.append(LINE_SEPARATOR);
		    }
		    sb.append(INDENT);
            Attribute classAttribute = (Attribute) classAttributes.get(i);         
            sb.append(generateAttribute(classAttribute, false));

		    first = false;
            }
		
        // add operations
        // TODO: constructors
        Vector classMethods = cls.getMethods();

        if (!classMethods.isEmpty()) {
            sb.append(LINE_SEPARATOR);
            sb.append(INDENT).append("// Methods");
            sb.append(LINE_SEPARATOR);
        }

	first = true;
	for (int x = 0; x < classMethods.size(); x++) {

	    if (!first) {
                    sb.append(LINE_SEPARATOR);
                }
	    sb.append(INDENT);
	    Method classMethod = (Method) classMethods.get(x); 
        sb.append(generateOperation(classMethod, false));

            if (lfBeforeCurly) {
                sb.append(LINE_SEPARATOR).append(INDENT);
            } else {
                sb.append(' ');
            }
            sb.append('{');

            // there is no ReturnType in behavioral feature (UML)
            sb.append(LINE_SEPARATOR);
			sb.append(generateMethodBody(classMethod));
			sb.append(INDENT);
			sb.append("}").append(LINE_SEPARATOR);

	    first = false;
        }
       	
        return sb;
    }
    
    String generateOperation(Method op, boolean documented) {
        if (isFileGeneration) {
            documented = true; // fix Issue 1506
        }
        StringBuffer sb = new StringBuffer(80);
        String nameStr = null;
        boolean constructor = false;
        nameStr = op.getName();

        sb.append(op.getVisibilityAsString()).append(' ');

        // pick out return type
        
        sb.append(op.getReturnTypeAsString()).append(' ');

        // name and params
        Vector params = op.getParameters();

        sb.append(nameStr).append('(');

        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                MethodParameter param = (MethodParameter) params.get(i);
                sb.append(generateParameter(param));
            }
        }

        sb.append(')');

        return sb.toString();
    }
    
    private String generateMethodBody(Method op) {
        //cat.info("generateMethodBody");
        if (op != null) {
        	Type returnType = op.getReturnType();

            // pick out return type
        	if (returnType != null) {
                return generateDefaultReturnStatement(returnType);
            }
        }
        return generateDefaultReturnStatement(null);
    }
    
    private String generateDefaultReturnStatement(Type cls) {
        if (cls == null) {
            return "";
        }

        String clsName = cls.getName();
        if (clsName.equals("void")) {
            return "";
        }
        if (clsName.equals("char")) {
            return INDENT + "return 'x';" + LINE_SEPARATOR;
        }
        if (clsName.equals("int")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("boolean")) {
            return INDENT + "return false;" + LINE_SEPARATOR;
        }
        if (clsName.equals("byte")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("long")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("float")) {
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("double")) {
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        }
        return INDENT + "return null;" + LINE_SEPARATOR;
    }
    
    
    private String generateParameter(MethodParameter parameter) {
        StringBuffer sb = new StringBuffer(20);
        //TODO: qualifiers (e.g., const)
        //TODO: stereotypes...
        sb.append(parameter.getTypeAsString());
	    sb.append(' ');
        sb.append(parameter.getName());
        //TODO: initial value
        return sb.toString();
    }
    
    private String generateAttribute(Attribute attr, boolean documented) {
        if (isFileGeneration) {
            documented = true; // always "documented" if we generate file.
        }
        StringBuffer sb = new StringBuffer(80);
        sb.append(generateCoreAttribute(attr));
        sb.append(";").append(LINE_SEPARATOR);

        return sb.toString();
    }
    
    String generateCoreAttribute(Attribute attr) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(attr.getVisibilityName()).append(' ');
        sb.append(attr.getType()).append(' ');
        sb.append(attr.getName());
   
        return sb.toString();
    }
    
    private StringBuffer generateClassifierEnd(DesignClass cls) {
        StringBuffer sb = new StringBuffer();
        String classifierkeyword = "class";
        sb.append(LINE_SEPARATOR);
		sb.append("//end of ").append(classifierkeyword);
		sb.append(" ").append(cls.getName());
		sb.append(LINE_SEPARATOR);
        sb.append("}");
        return sb;
    }
    
    public void setLfBeforeCurly(boolean beforeCurl) {
        lfBeforeCurly = beforeCurl;
    }
    
    public boolean isLfBeforeCurly() {
        return lfBeforeCurly;
    }
    
    public void generateCode() {
    	Vector projectDiagrams = this.getDiagramModels();
    	for (int y = 0; y < projectDiagrams.size(); y++) {
    	  DiagramModel currDiagram = (DiagramModel) projectDiagrams.get(y);	
    	  Vector projectElements = currDiagram.getGraphicalElements();
    	  for (int i = 0; i < projectElements.size(); i++) {
              GraphicalElement currEl = (GraphicalElement) projectElements.get(i);
              if (currEl instanceof ClassGR) {
            	  out.println("DCD:");
                  DesignClass dc = ((ClassGR) currEl).getDesignClass();
                  out.println("Class:" + dc.getName());
                  String projectPath = new File(this.getFilepath()).getParent();
                  String genPath = generateFile(dc,projectPath);
                  out.println("Generated in: " + genPath);
              }
              if (currEl instanceof SDObjectGR) {
                  out.println("SD:");
                  DesignClass dc = ((SDObjectGR) currEl).getSDObject().getDesignClass();
                  out.println("Class:" + dc.getName());
                  Vector classMethods = dc.getMethods();
                  for (int x = 0; x < classMethods.size(); x++) {
                	  Method currMethod = (Method) classMethods.get(x);
                	  out.println("Method:" + currMethod.getName());
                  }
              }
              if (currEl instanceof SDMessageGR) {
                  SDMessage sdm = ((SDMessageGR) currEl).getMessage();
                  out.println("Method:" + sdm.toString());
                  out.println("From:" + sdm.getSource().getClassifier().getName());
                  out.println("To:" + sdm.getTarget().getClassifier().getName());
                  out.println("Method:" + sdm.getMethod().getName());
                  out.println("Return Type:" + sdm.getReturnType());
                  out.println("Attributes:" + sdm.getAttributes());
              }
          }
    	}
    }
}
