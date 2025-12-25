package edu.city.studentuml.model.domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;

@JsonIncludeProperties({ "diagramModels" })
public class UMLProject implements Serializable, PropertyChangeListener, IXMLCustomStreamable {

    private static final Logger logger = Logger.getLogger(UMLProject.class.getName());

    private static UMLProject instance = null;
    private NotifierVector<DiagramModel> diagramModels;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private CentralRepository repository;
    private boolean projectSaved = true;
    private String user;
    // for applet
    private int status;
    private int exid;
    private int parentid;
    private String nodeType;
    private String title;
    private String comment;
    private Mode mode;
    // for desktop application
    private String projectFilename = "";
    private String projectFilepath = "";
    private String projectName = "";

    protected UMLProject() {
        projectInit();
    }

    private void projectInit() {
        repository = new CentralRepository();
        diagramModels = new NotifierVector<>();
        // applet
        title = "";
        comment = "";
        status = 0;
        nodeType = "";

        // application
        projectName = "New Project";
    }

    public static UMLProject getInstance() {
        if (instance == null) {
            instance = new UMLProject();
        }
        return instance;
    }

    public void clear() {
        diagramModels.clear();
        repository.clear();
        setFilename("");
        setFilepath("");
        setName("");
        SystemWideObjectNamePool.getInstance().clear();
        logger.fine(() -> "Notifying listeners");
        setSaved(true);
        pcs.firePropertyChange("projectCleared", null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        logger.info(() -> "PropertyChangeListener added: " + l.toString());
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        logger.fine(() -> "PropertyChangeListener removed: " + l.toString());
        pcs.removePropertyChangeListener(l);
    }

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public DiagramModel getDiagramModel(int index) {
        return diagramModels.elementAt(index);
    }

    public Vector<DiagramModel> getDiagramModels() {
        return diagramModels;
    }

    public boolean isSaved() {
        return projectSaved;
    }

    public void setSaved(boolean saved) {
        logger.fine(() -> "Setting projectSaved: " + saved);
        projectSaved = saved;
    }

    public void addDiagram(DiagramModel dm) {
        diagramModels.add(dm);
        dm.addPropertyChangeListener(this);
        projectChanged();
    }

    public void removeDiagram(DiagramModel dm) {
        dm.removePropertyChangeListener(this);
        diagramModels.remove(dm);
        projectChanged();
    }

    public void projectChanged() {
        logger.fine("Project changed");
        setSaved(false);
        logger.fine(() -> "Notifying listeners");
        pcs.firePropertyChange("projectChanged", null, null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // logger.info(() -> "propertyChange: from: " + evt.getSource().getClass().getSimpleName() + " property: " + evt.getPropertyName());
        projectChanged();
    }

    /**
     * Loads an XML document from the filename.
     * 
     * @param filename
     * @return a list of the errors (strings) collected during loading the file
     * @throws IOException
     * @throws NotStreamable
     */
    public List<String> loadFromXML(String filename) throws IOException, NotStreamable {
        logger.info(() -> "Loading from XML: " + filename);

        SystemWideObjectNamePool.getInstance().loading();

        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFile(filename);

        Element e = streamer.getNodeById(null, XMLSyntax.PROJECT);
        streamFromXML(e, streamer, this);

        SystemWideObjectNamePool.getInstance().done();

        logger.info(() -> ".......end from XML: " + filename);
        setSaved(true);

        return streamer.getErrorStrings();
    }

    // Embed4Auto

    public void loadFromURL(String url) throws NotStreamable {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadURL(url);

        Element e = streamer.getNodeById(null, XMLSyntax.PROJECT);

        streamFromXML(e, streamer, this);
        SystemWideObjectNamePool.getInstance().done();

        logger.fine(() -> "Loading from URL: " + url);
        projectChanged();
    }

    /**
     * Only used by applet for undo/redo
     * 
     * @param xmlString
     * @throws NotStreamable
     */
    public void loadFromXMLString(String xmlString) throws NotStreamable {

        SystemWideObjectNamePool.getInstance().loading();

        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFromString(xmlString);

        Element e = streamer.getNodeById(null, XMLSyntax.PROJECT);
        streamFromXML(e, streamer, this);

        SystemWideObjectNamePool.getInstance().done();

        logger.finer(() -> "Loading from XMLString: " + xmlString);
        projectChanged();
    }

    public void streamToXML() {
        if (projectFilepath == null || projectFilepath.length() == 0) {
            logger.severe("Empty or NULL projectFilepath");
            return;
        }
        streamToXML(projectFilepath);
        setSaved(true);
    }

    public void streamToXML(String path) {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, XMLSyntax.PROJECT, this);
        streamer.saveToFile(path);
    }

    /**
     * Only used by applet for undo/redo
     * 
     * @return
     */
    public String streamToXMLString() {
        XMLStreamer streamer = new XMLStreamer();
        streamer.streamObject(null, XMLSyntax.PROJECT, this);
        return streamer.streamToString();
    }

    @Override
    public void streamFromXML(Element element, XMLStreamer streamer, Object instance) throws NotStreamable {
        diagramModels.clear();
        streamer.streamChildrenFrom(element, instance);
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        streamer.streamObjects(node, diagramModels.iterator());
    }

    /**
     * Determines if the specified abstract class is referenced by any graphical
     * elements in the diagramModels list, excluding the specified graphical
     * element.
     *
     * @param el            the graphical element to exclude from the search
     * @param abstractClass the abstract class to search for references to
     * @return true if the abstract class is referenced by any graphical element in
     *         the diagramModels list, excluding the specified element; false
     *         otherwise
     */
    public boolean isClassReferenced(GraphicalElement el, AbstractClass abstractClass) {
        for (DiagramModel model : diagramModels) {
            for (GraphicalElement element : model.getGraphicalElements()) {
                if (element == el) {
                    continue;
                }
                if (element instanceof ConceptualClassGR
                        && ((ConceptualClassGR) element).getConceptualClass() == abstractClass) {
                    return true;
                }
                if (element instanceof ClassGR && ((ClassGR) element).getDesignClass() == abstractClass) {
                    return true;
                }
                if (element instanceof SDObjectGR
                        && ((SDObjectGR) element).getSDObject().getDesignClass() == abstractClass) {
                    return true;
                }
                if (element instanceof MultiObjectGR
                        && ((MultiObjectGR) element).getMultiObject().getDesignClass() == abstractClass) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified actor is referenced by any actor instance
     * graphical elements in the diagramModels list, excluding the specified
     * graphical element.
     *
     * @param el    the graphical element to exclude from the search
     * @param actor the actor to search for references to
     * @return true if the actor is referenced by any actor instance graphical
     *         element in the diagramModels list, excluding the specified element;
     *         false otherwise
     */
    public boolean isActorReferenced(GraphicalElement el, Actor actor) {
        for (DiagramModel model : diagramModels) {
            if (!(model instanceof AbstractSDModel)) {
                continue;
            }
            for (GraphicalElement element : model.getGraphicalElements()) {
                if (element == el || !(element instanceof ActorInstanceGR)) {
                    continue;
                }
                if (((ActorInstanceGR) element).getActorInstance().getActor() == actor) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified system is referenced by any system instance
     * graphical elements in the diagramModels list, excluding the specified
     * graphical element.
     *
     * @param el     the graphical element to exclude from the search
     * @param system the system to search for references to
     * @return true if the system is referenced by any system instance graphical
     *         element in the diagramModels list, excluding the specified element;
     *         false otherwise
     */
    public boolean isSystemReferenced(GraphicalElement el, System system) {
        for (DiagramModel model : diagramModels) {
            if (!(model instanceof SSDModel)) {
                continue;
            }
            for (GraphicalElement element : model.getGraphicalElements()) {
                if (element == el || !(element instanceof SystemInstanceGR)) {
                    continue;
                }
                if (((SystemInstanceGR) element).getSystemInstance().getSystem() == system) {
                    return true;
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
    }

    public String getFilename() {
        return projectFilename;
    }

    public void setFilename(String filename) {
        projectFilename = filename;
    }

    public String getFilepath() {
        return projectFilepath;
    }

    public void setFilepath(String filepath) {
        projectFilepath = filepath;
        if (filepath.length() > 0) {
            projectFilename = filepath.substring(filepath.lastIndexOf(File.separatorChar) + 1);
            projectName = projectFilename.substring(0, projectFilename.lastIndexOf("."));
        }
    }

    public void createNewProject() {
        clear();
        SystemWideObjectNamePool.getInstance().clear();
        SystemWideObjectNamePool.getInstance().reload();
        setSaved(true);
        setName("New Project");
    }

}
