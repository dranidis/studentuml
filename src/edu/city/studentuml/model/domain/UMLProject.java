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
import edu.city.studentuml.util.Mode;
import edu.city.studentuml.model.graphical.AbstractSDModel;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.codegeneration.CodeGenerator;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.SystemInstanceGR;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;


public class UMLProject extends Observable implements Serializable, Observer, IXMLCustomStreamable {
    
    Logger logger = Logger.getLogger(UMLProject.class.getName());

    private static UMLProject ref = null;
    private NotifierVector<DiagramModel> diagramModels;
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
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

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
        diagramModels.clear();
        repository.clear();
        setFilename("");
        setFilepath("");
        setName("");
        SystemWideObjectNamePool.getInstance().clear();
        logger.fine("Notifying observers: " + this.countObservers());
        setSaved(true);
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        logger.fine("OBSERVER added: " + o.toString());
        super.addObserver(o);
    }    

    public CentralRepository getCentralRepository() {
        return repository;
    }

    public DiagramModel getDiagramModel(int index) {
        return (DiagramModel) diagramModels.elementAt(index);
    }

    public Vector<DiagramModel> getDiagramModels() {
        return diagramModels;
    }

    public Boolean isSaved() {
        return projectSaved;
    }

    public void setSaved(Boolean saved) {
        logger.fine("Setting projectSaved: " + saved);
        projectSaved = saved;
    }

    public void addDiagram(DiagramModel dm) {
        diagramModels.add(dm);

        dm.addObserver(this);
        projectChanged();
        }

    public void removeDiagram(DiagramModel dm) {
        dm.deleteObserver(this);
        diagramModels.remove(dm);
        projectChanged();
    }

    public void projectChanged() {
        logger.fine("Project changed");
        setSaved(false);
        
        logger.fine("Notifying observers: " + this.countObservers());
        setChanged();
        notifyObservers();

    }

    public void update(Observable observable, Object object) {
        String objString = "null";
        if (object != null) {
            objString = object.getClass().getSimpleName();
        }
        logger.fine("UPDATE: from: " + observable.getClass().getSimpleName() + " arg: " + objString);
        projectChanged();
    }

    public void loadFromXML(String filename) {
        logger.fine("Loading from XML: " + filename);

        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadFile(filename);

        Element e = streamer.getNodeById(null, "project");
        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        logger.finer(".......end from XML: \n" + filename);
        projectChanged();
        setSaved(true);
    }
    // Embed4Auto

    public void loadFromURL(String url) {
        SystemWideObjectNamePool.getInstance().loading();
        XMLStreamer streamer = new XMLStreamer();
        streamer.loadURL(url);

        Element e = streamer.getNodeById(null, "project");

        streamer.streamFrom(e, this);
        SystemWideObjectNamePool.getInstance().done();

        logger.fine("Loading from URL: " + url);
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

        logger.fine("Loading from XMLString: " + xmlString);
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
    
    
    public int generateCode(boolean isInUpdateMode) {
    	int genFilesCount = 0;
    	Vector projectDiagrams = this.getDiagramModels();
    	CodeGenerator javaGenerator= new CodeGenerator(); 
    	DesignClass dc = null;
    	DesignClass dc2 = null;
    	Interface interfs = null;
    	boolean hasLifeline=false;
    	Method headMethod=null;
//    	DiagramModel currDiagram;
    	Vector projectElements;
    	List<DesignClass> dcToGenerate = new ArrayList<DesignClass>();
        
    	for (DiagramModel currDiagram: getDiagramModels()) {
//    	  currDiagram = (DiagramModel) projectDiagrams.get(y);	
    	  projectElements = currDiagram.getGraphicalElements();
    	  
    	  for (int i = 0; i < projectElements.size(); i++) {
              GraphicalElement currEl = (GraphicalElement) projectElements.get(i);
              if (currEl instanceof ClassGR) {
                  dc = ((ClassGR) currEl).getDesignClass();
                  dc.setExtendClass(null);
                  dc.resetImplementInterfaces();
              }
              if (currEl instanceof AssociationClassGR) {
                  AssociationClassGR acgr = (AssociationClassGR) currEl;
                  dc = (DesignClass) acgr.getAssociationClass().getAssociationClass();
                         
              }
              if (currEl instanceof RealizationGR) {
                  Realization realz = ((RealizationGR) currEl).getRealization();
                  dc = realz.getTheClass();
                  dc.addImplementInterfaces(realz.getTheInterface());
                         
              }
              if (currEl instanceof GeneralizationGR) {
                  Generalization genz = ((GeneralizationGR) currEl).getGeneralization();
                  dc = (DesignClass) genz.getBaseClass();
                  dc.setExtendClass(genz.getSuperClass());
              }
              if (currEl instanceof InterfaceGR) {
                  interfs = ((InterfaceGR) currEl).getInterface();
                  String projectPath = new File(this.getFilepath()).getParent();
                  String genPath = javaGenerator.generateFile(isInUpdateMode,interfs,projectPath,this);
                  if(genPath!=null) {
                	  genFilesCount++;
                  }  
              }
              if (currEl instanceof AssociationGR && !(currEl instanceof AggregationGR)) {
            	  Association association = ((AssociationGR) currEl).getAssociation();
            	  if(association.getRoleA().getName()==null || association.getRoleA().getName().equals("")) {
            		  association.getRoleA().setName(association.getClassA().getName().toLowerCase());
            	  }
            	  if(association.getRoleB().getName()==null || association.getRoleB().getName().equals("")) {
            		  association.getRoleB().setName(association.getClassB().getName().toLowerCase());
            	  }
            	  if (association.getClassA() instanceof DesignClass) {
            		  dc = (DesignClass) association.getClassA();
            	  }else if(association.getClassA() instanceof Interface) {
            		  interfs= (Interface) association.getClassA();
            	  }
            	  if (association.getClassB() instanceof DesignClass) {
            		  dc2 = (DesignClass) association.getClassB();
            	  }else if(association.getClassB() instanceof Interface) {
            		  interfs= (Interface) association.getClassB();
            	  }
        		  if(association.getDirection()==1) {
            		  if(association.getRoleB().getMultiplicity() !=null && association.getRoleB().getMultiplicity().contains("*")) {
            			  if(association.getClassB() instanceof DesignClass) {
            				  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType("List<"+dc2.getName()+">")));
            			  }
            			  if(association.getClassB() instanceof Interface) {
            				  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType("List<"+interfs.getName()+">")));
            			  }
            		  }else {
            			  if(association.getClassB() instanceof DesignClass) {
            				  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType(dc2.getName())));
            			  }
            			  if(association.getClassB() instanceof Interface) {
            				  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType(interfs.getName())));
            			  }
            		  }
            		  
        		  }else
        		  if(association.getDirection()==2) {
            		  if(association.getRoleA().getMultiplicity() !=null && association.getRoleA().getMultiplicity().contains("*")) {
            			  if(association.getClassA() instanceof DesignClass) {
            				  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType("List<"+dc.getName()+">")));  
            			  }
            			  if(association.getClassB() instanceof Interface) {
            				  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType("List<"+interfs.getName()+">")));
            			  }  
            		  }else {
            			  if(association.getClassA() instanceof DesignClass) {
            				  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType(dc.getName())));
            			  }
            			  if(association.getClassA() instanceof Interface) {
            				  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType(interfs.getName())));
            			  }
            		  }
        		  }else
        		   if(association.getDirection()==3 || association.getDirection()==0) {
                	  if(association.getClassA() instanceof DesignClass && association.getClassB() instanceof DesignClass) {
	            		  if(association.getRoleB().getMultiplicity() !=null && association.getRoleB().getMultiplicity().contains("*")) {
	            			  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType("List<"+dc2.getName()+">"))); 
	            		  }else {
	            			  dc.addAttribute(new Attribute(association.getRoleB().getName(),new DataType(dc2.getName())));  
	            		  }
	            		  if(association.getRoleA().getMultiplicity() !=null && association.getRoleA().getMultiplicity().contains("*")) {
	            			  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType("List<"+dc.getName()+">"))); 
	            		  }else {
	            			  dc2.addAttribute(new Attribute(association.getRoleA().getName(),new DataType(dc.getName())));  
	            		  }
                	  }else {
//                		  out.println("Biderectional association not applicable in interfaces");
                	  }
            	  }	   
              }else
              if (currEl instanceof AggregationGR) {
            	  Aggregation aggregation = ((AggregationGR) currEl).getAggregation();
            	  if(aggregation.getRoleA().getName()==null || aggregation.getRoleA().getName().equals("")) {
            		  aggregation.getRoleA().setName(aggregation.getClassA().getName().toLowerCase());
            	  }
            	  if(aggregation.getRoleB().getName()==null || aggregation.getRoleB().getName().equals("")) {
            		  aggregation.getRoleB().setName(aggregation.getClassB().getName().toLowerCase());
            	  }
            	  if (aggregation.getClassA() instanceof DesignClass) {
            		  dc = (DesignClass) aggregation.getClassA();
            	  }else if(aggregation.getClassA() instanceof Interface) {
            		  interfs= (Interface) aggregation.getClassA();
            	  }
            	  if (aggregation.getClassB() instanceof DesignClass) {
            		  dc2 = (DesignClass) aggregation.getClassB();
            	  }else if(aggregation.getClassB() instanceof Interface) {
            		  interfs= (Interface) aggregation.getClassB();
            	  }
        		  if(aggregation.getDirection()==1) {
            		  if(aggregation.getRoleB().getMultiplicity() !=null && aggregation.getRoleB().getMultiplicity().contains("*")) {
            			  if(aggregation.getClassB() instanceof DesignClass) {
            				  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType("List<"+dc2.getName()+">")));
            			  }
            			  if(aggregation.getClassB() instanceof Interface) {
            				  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType("List<"+interfs.getName()+">")));
            			  }
            		  }else {
            			  if(aggregation.getClassB() instanceof DesignClass) {
            				  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType(dc2.getName())));
            			  }
            			  if(aggregation.getClassB() instanceof Interface) {
            				  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType(interfs.getName())));
            			  }
            		  }
            		  
        		  }else
        		  if(aggregation.getDirection()==2 || aggregation.getDirection()==0) {
            		  if(aggregation.getRoleA().getMultiplicity() !=null && aggregation.getRoleA().getMultiplicity().contains("*")) {
            			  if(aggregation.getClassA() instanceof DesignClass) {
            				  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType("List<"+dc.getName()+">")));  
            			  }
            			  if(aggregation.getClassB() instanceof Interface) {
            				  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType("List<"+interfs.getName()+">")));
            			  }  
            		  }else {
            			  if(aggregation.getClassA() instanceof DesignClass) {
            				  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType(dc.getName())));
            			  }
            			  if(aggregation.getClassA() instanceof Interface) {
            				  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType(interfs.getName())));
            			  }
            		  }
        		  }else
        		   if(aggregation.getDirection()==3) {
                	  if(aggregation.getClassA() instanceof DesignClass && aggregation.getClassB() instanceof DesignClass) {
	            		  if(aggregation.getRoleB().getMultiplicity() !=null && aggregation.getRoleB().getMultiplicity().contains("*")) {
	            			  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType("List<"+dc2.getName()+">"))); 
	            		  }else {
	            			  dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),new DataType(dc2.getName())));  
	            		  }
	            		  if(aggregation.getRoleA().getMultiplicity() !=null && aggregation.getRoleA().getMultiplicity().contains("*")) {
	            			  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType("List<"+dc.getName()+">"))); 
	            		  }else {
	            			  dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),new DataType(dc.getName())));  
	            		  }
                	  }else {
//                		  out.println("Biderectional association not applicable in interfaces");
                	  }
            	  }	   
              } 
              if (currEl instanceof SDObjectGR) {
                  dc = ((SDObjectGR) currEl).getSDObject().getDesignClass();
                  dc.resetSDMethods();
                      
              } 
              if (currEl instanceof MultiObjectGR) {
                  dc = ((MultiObjectGR) currEl).getMultiObject().getDesignClass();
	              dc.resetSDMethods();    
              }
              if (dc!=null) {
	              if(dcToGenerate.isEmpty()) {
	            	  dcToGenerate.add(dc);
	              }else {
	            	 if(dcToGenerate.contains(dc)){
	            	 dcToGenerate.set(dcToGenerate.indexOf(dc),dc);
	            	 }else {
	            	   dcToGenerate.add(dc); 
	            	 }
	             }
            }
          }
    	}
    	for (DiagramModel currDiagram: getDiagramModels()) {
//      	  currDiagram = (DiagramModel) projectDiagrams.get(y);	
      	  projectElements = currDiagram.getGraphicalElements();
      	  HashMap <SDMessage,Integer> SDMessages = new HashMap<SDMessage,Integer>();
    	  //sort by rank and add Methods of Message Calls
    	  if(currDiagram instanceof SDModel) {
    		  List<Method> headMethods= new ArrayList<Method>();
    		  for (int i = 0; i < projectElements.size(); i++) {
    			  GraphicalElement currElSD = (GraphicalElement) projectElements.get(i);
    			  if (currElSD instanceof SDMessageGR) {
    				  SDMessage sdmx = ((SDMessageGR) currElSD).getMessage();
    				  SDMessages.put(sdmx,sdmx.getRank());
    			  }  
    		  }
    		  if(!SDMessages.isEmpty()) {
    		  SDMessages = sortByValue(SDMessages);
    		  for (Map.Entry<SDMessage,Integer> SDMessage : SDMessages.entrySet()) {
    			  SDMessage sdm = SDMessage.getKey();
    	           if (sdm.getTarget() != null && sdm.getTarget().getClassifier() instanceof DesignClass) {
                  	 dc = (DesignClass) sdm.getTarget().getClassifier();
                    }
                    dc2 = null;
                    if (sdm.getSource() != null && sdm.getSource().getClassifier() instanceof DesignClass) {
                  	  dc2 = (DesignClass) sdm.getSource().getClassifier();
                    }
                    RoleClassifier dcObject = null;
                    if (sdm.getTarget() instanceof SDObject) {
  	                  dcObject = (SDObject) sdm.getTarget();
                    }else if (sdm.getTarget() instanceof MultiObject) {
                  	  dcObject = (MultiObject) sdm.getTarget();
                    }               	  
                     if (sdm instanceof CreateMessage) {
                  	   Method constructor = new Method(dc.getName());
                  	   constructor.setPriority(sdm.getRank());
                  	   Vector constructorParameters = ((CreateMessage) sdm).getSDMethodParameters();
                  	   if (!constructorParameters.isEmpty() && constructorParameters!=null) {
                  		   constructor.setParameters(constructorParameters);
                  	   }else {
                  		   constructor.setParameters(new Vector());
                  	   }
                  	   if(!dc.getSDMethods().contains(constructor) && (sdm.getTarget() instanceof SDObject)) {
                  		 dc.addSDMethod(constructor);
                  	   }

                  	   if(dc2 !=null) {
                  		 if(headMethods.size() > 0) {
	                		   headMethod=headMethods.get(headMethods.size()-1);
	                	   }
                  		   dc2 = (DesignClass) sdm.getSource().getClassifier();
  	                	  
  	                	   if(hasLifeline && headMethod!=null) {  
  	                		 if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
  	                	
  	                			dc2=addToHeadMethod(dc,dc2,headMethod,constructor,false,dcObject);
 	                		   }
  	                	   }
                  	   }
                  	   hasLifeline=true;
                  	   headMethods.add(constructor);
                     }
                     if (sdm instanceof CallMessage) {
                  	   CallMessage cm = (CallMessage) sdm;
                  	   Method sdMethod = new Method(cm.getName());
                  	   if (sdMethod != null) {
//                  		   sdMethod.setParameters(cm.getSDMethodParameters());
                  		   sdMethod.setParameters(cm.getParameters());
//                  		   String returnValue = cm.getReturnValueAsString();
//                  		   if (returnValue.contains(" ")) {
//                  			   String[] split = returnValue.split("\\s+");
//                  			   returnValue = split[0];
//                  			   if(!returnValue.equals("")) {
//	                  			   if (split.length>1) {
//	                  			   String returnParameter = split[1];
//	                  			   sdMethod.setReturnParameter(returnParameter);
//	                  			   }
//                  			   }else {
//                  				   returnValue="void";
//                  			   }
//                  		   }
//                  		   sdMethod.setReturnType(new DataType(returnValue));
                  		   sdMethod.setReturnType(((CallMessage) sdm).getReturnType());
                                   
  	                	   sdMethod.setPriority(cm.getRank());
  	                	   if(!(cm.getTarget() instanceof MultiObject && !cm.isIterative())){
  	                		   dc.addSDMethod(sdMethod);
  	                	   }
  	                	   sdMethod.setIterative(cm.isIterative());
  	                	   if (dc2 != null) {
  	                		 dc2 = (DesignClass) sdm.getSource().getClassifier();
		                	  
		                	   if(headMethods.size() > 0) {
		                		   headMethod=headMethods.get(headMethods.size()-1);
		                	   }
		                	   if(hasLifeline && headMethod!=null) {
		                		 if (cm.isReflective() && dc2.getSDMethods().contains(headMethod)) {
		         
		                			 dc2=addToHeadMethod(dc,dc2,headMethod,sdMethod,cm.isReflective(),dcObject);
	                			  }
		                		  if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
		               
		                			 dc2=addToHeadMethod(dc,dc2,headMethod,sdMethod,cm.isReflective(),dcObject);
		                		  }
		                	   }
  	                	   }
  	                	 if(!cm.isReflective()) {
                		    hasLifeline=true;
                		    headMethods.add(sdMethod);
	                	 }
                  	   }
                     }
                     if (sdm instanceof DestroyMessage) {
                  	   Method destroyMethod = new Method("destroy");
                  	   destroyMethod.setPriority(sdm.getRank());
                  	   dc.addSDMethod(destroyMethod);
                  	   if (dc2!=null) {
                  		 if(headMethods.size() > 0) {
	                		   headMethod=headMethods.get(headMethods.size()-1);
	                	   }
  	                	   dc2 = (DesignClass) sdm.getSource().getClassifier();
  	                	 
  	                	 if(hasLifeline && headMethod!=null) {
  	                		 if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
  	                			
  	                			dc2=addToHeadMethod(dc,dc2,headMethod,destroyMethod,false,dcObject);
 	                	   }
  	                	 }	 
                  	   }   
                     }
                     if(sdm instanceof ReturnMessage) {
                    	//check for parameter in return message and replace it in called Method 
                       if(hasLifeline) {
                    	 ReturnMessage rm = (ReturnMessage) sdm;
                    	 if(headMethods.size() > 0) {
	                		   headMethod=headMethods.get(headMethods.size()-1);	   
	                	 }
                    	 if(headMethod!=null && rm.getTarget().getClassifier() instanceof DesignClass) {
                    		dc2 = (DesignClass) rm.getTarget().getClassifier();
                    		String returnParameter = rm.getName();
                    		if(!returnParameter.equals("")) {
                    			 if (sdm.getSource() instanceof SDObject) {
                 	                  dcObject = (SDObject) sdm.getSource();
                                 }else if (sdm.getSource() instanceof MultiObject) {
                             	      dcObject = (MultiObject) sdm.getSource();
                                 }
            
                    			if(headMethods.size() > 0) {
	                    			Vector targetSdMethods = dc2.getSDMethods();
	                    			for (int i=0;i<targetSdMethods.size();i++) {
	                    				Method checkMethod = (Method) targetSdMethods.get(i);
	                    				if(checkMethod == headMethods.get(headMethods.size()-2)) {
	                    					List<String> mtdCalledMethods = checkMethod.getCalledMethods();
	                    					for (int c=0;c<mtdCalledMethods.size();c++) {
	                            				if(mtdCalledMethods.get(c).contains(headMethod.getName())) {
	                            					headMethod.setReturnParameter(returnParameter);
	                            					mtdCalledMethods.set(c,generateCalledMethod(dc2,headMethod,dcObject));
	                            					checkMethod.replaceCalledMethod(c, mtdCalledMethods.get(c));
	                            				}
	                            			}
	                    					dc2.replaceSDMethod(i, checkMethod);
	                    				}
	                    			}
                    			}		
                    		}
                    	 }
                       }
                       //check headMethod (method that contains the branched called messages)
                	   if((headMethods.size() > 1) && hasLifeline==true){
	                  	   headMethods.remove(headMethods.size()-1);
                	   }
                	   else if (hasLifeline==true){
                		   headMethods.clear();
                		   hasLifeline=false;
                		   headMethod=null;
                	   }
                     }
    			}
    		  }
    		  
    	  }
    	  
    	  if (dc!=null) {
        	 if(dcToGenerate.contains(dc)){
        	 dcToGenerate.set(dcToGenerate.indexOf(dc),dc);
        	 }else {
        	   dcToGenerate.add(dc); 
        	 }    
    	  }
    	}
    	for (int i=0; i<dcToGenerate.size();i++) {
	    	DesignClass dci =(DesignClass) dcToGenerate.get(i);	
	    	String projectPath = new File(this.getFilepath()).getParent();
	        String genPath = javaGenerator.generateFile(isInUpdateMode,dci,projectPath,this);
		    if(genPath!=null) {
		    	genFilesCount++;
		    }
    	}
    	
    	return genFilesCount;
    }
    
    public String generateCalledMethod(DesignClass homeClass,Method m, RoleClassifier object) {
    	StringBuffer sb = new StringBuffer();
    	if(m.isIterative() && object instanceof SDObject) {
    		sb.append("for(int i=0;i<10;i++){").append(LINE_SEPARATOR);
    		sb.append("     ");
    	}else if (m.isIterative() && object instanceof MultiObject) {
    		sb.append("for(" + object.getClassifier().getName() + " obj : "+object.getName()+") {").append(LINE_SEPARATOR);
    		sb.append("     ");
    	}
    	if (!m.getReturnType().getName().equals("void") && !m.getReturnType().getName().equals("VOID")) {
    		Vector attributes = homeClass.getAttributes();
    		boolean parameterExists = false;
    		Attribute attribute;
    		for(int i=0;i<attributes.size();i++) {
    			attribute= (Attribute) attributes.get(i);
    			if(attribute.getName().toLowerCase().equals(m.getReturnParameter().toString().toLowerCase())){
    				parameterExists = true;
    			}
    		}
    		if(!parameterExists) {
    			sb.append(m.getReturnTypeAsString() + " ");
    		}
    		sb.append(m.getReturnParameter() + " = ");
    	}
    	if (object instanceof SDObject){
    		sb.append(object.getName()).append(".");
    	}else if (object instanceof MultiObject && m.isIterative()) {
    		sb.append("obj.");
    	}else if (object instanceof MultiObject && !m.isIterative()) {
    		sb.append(object.getName() + ".");
    	}
    	sb.append(m.getName()).append("(");
    	sb.append(m.getParametersAsString());
    	sb.append(");");
    	if(m.isIterative()) {
    		sb.append(LINE_SEPARATOR).append(" ");
    		sb.append("   }");
    	}
    	return sb.toString();
    }
    
    public static HashMap<SDMessage,Integer> sortByValue(HashMap<SDMessage,Integer> hm){
    	List<Map.Entry<SDMessage,Integer>> list = new LinkedList<Map.Entry<SDMessage,Integer>>(hm.entrySet());
    	Collections.sort(list, new Comparator<Map.Entry<SDMessage,Integer>>(){
    		public int compare(Map.Entry<SDMessage,Integer> o1, Map.Entry<SDMessage,Integer> o2) {
    			return (o1.getValue()).compareTo(o2.getValue());
    		}
    	});
    	HashMap<SDMessage,Integer> temp = new LinkedHashMap<SDMessage,Integer>();
    	for( Map.Entry<SDMessage,Integer> aa : list) {
    		temp.put(aa.getKey(), aa.getValue());
    	}
    	return temp;
    }
    
    public DesignClass addToHeadMethod(DesignClass targetClass,DesignClass sourceClass,Method headMethod,Method sdMethod,boolean isReflective, RoleClassifier targetObject) {
    	Method methodToChange = (Method) sourceClass.getSDMethods().get(sourceClass.getSDMethods().indexOf(headMethod));
		methodToChange.addCalledMethod(sourceClass,sdMethod, targetClass, targetObject, isReflective);
		sourceClass.replaceSDMethod(sourceClass.getSDMethods().indexOf(headMethod), methodToChange);
		return sourceClass;
    }

    public void createNewProject() {
        clear();
        SystemWideObjectNamePool.getInstance().clear();
        SystemWideObjectNamePool.getInstance().reload();
        setSaved(true);
        setName("New Project");
    }
}
