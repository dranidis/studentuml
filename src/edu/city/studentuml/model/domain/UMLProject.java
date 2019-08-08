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
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.view.gui.ApplicationGUI;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.CodeGenerator;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        setFilename("");
        setFilepath("");
        setName("");
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
    
    
    public int generateCode() {
    	int genFilesCount = 0;
    	Vector projectDiagrams = this.getDiagramModels();
    	CodeGenerator javaGenerator= new CodeGenerator(); 
    	DesignClass dc = null;
    	boolean hasLifeline=false;
    	boolean firstSD=true;
    	Method headMethod=null;
    	List<DesignClass> dcToGenerate = new ArrayList<DesignClass>();
    	for (int y = 0; y < projectDiagrams.size(); y++) {
    	  DiagramModel currDiagram = (DiagramModel) projectDiagrams.get(y);	
    	  Vector projectElements = currDiagram.getGraphicalElements();
    	  HashMap <SDMessage,Integer> SDMessages = new HashMap<SDMessage,Integer>();
    	  
    	  for (int i = 0; i < projectElements.size(); i++) {
              GraphicalElement currEl = (GraphicalElement) projectElements.get(i);
              if (currEl instanceof ClassGR) {
                  dc = ((ClassGR) currEl).getDesignClass();
                  dc.setExtendClass(null);
                  dc.resetImplementInterfaces();
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
                  Interface interfs = ((InterfaceGR) currEl).getInterface();
                  String projectPath = new File(this.getFilepath()).getParent();
                  String genPath = javaGenerator.generateFile(interfs,projectPath,this);
                  out.println("Generated in: " + genPath);
                  if(genPath!=null) {
                	  genFilesCount++;
                  }  
              } 
              if (currEl instanceof SDObjectGR) {
                  dc = ((SDObjectGR) currEl).getSDObject().getDesignClass();
                  if (firstSD) {
	                  dc.resetSDMethods();
	                  dc.clearCalledMethods();
                  }    
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
    	  //sort by rank and add Methods of Message Calls
    	  if(currDiagram instanceof SDModel) {
    		  firstSD=false;
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
    	           if (sdm.getTarget().getClassifier() instanceof DesignClass) {
                  	 dc = (DesignClass) sdm.getTarget().getClassifier();
                    }
                    DesignClass dc2 = null;
                    if (sdm.getSource().getClassifier() instanceof DesignClass) {
                  	  dc2 = (DesignClass) sdm.getSource().getClassifier();
                    }
                    RoleClassifier dcObject = null;
                    if (sdm.getTarget() instanceof SDObject) {
  	                  dcObject = (SDObject) sdm.getTarget();
                    }else if (sdm.getTarget() instanceof MultiObject) {
                  	  dcObject = (MultiObject) sdm.getTarget();
                    }               	  
                     if (sdm instanceof CreateMessage) {
                  	   Method createMethod = new Method("create");
                  	   createMethod.setPriority(sdm.getRank());
                  	   dc.addSDMethod(createMethod);
                  	   if(dc2 !=null) {
  	                	   dc2 = (DesignClass) sdm.getSource().getClassifier();
  	                	   dc2.addCalledMethod(createMethod, dc, false,dcObject);
  	                	   if(hasLifeline && headMethod!=null) {
  	                		   if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
  	                			  Method methodToChange = (Method) dc2.getSDMethods().get(dc2.getSDMethods().indexOf(headMethod));
  	                			  methodToChange.addCalledMethod(createMethod, dc, false, dcObject,false);
  	                			  dc2.replaceSDMethod(dc2.getSDMethods().indexOf(headMethod), methodToChange);	                			  
  	                		   }
  	                	   }
                  	   }
                     }
                     if (sdm instanceof CallMessage) {
                  	   CallMessage cm = (CallMessage) sdm;
                  	   Method sdMethod = new Method(cm.getName());
                  	   if (sdMethod != null) {
                  		   sdMethod.setParameters(cm.getSDMethodParameters());
                  		   String returnValue = cm.getReturnValueAsString();
                  		   if (returnValue.contains(" ")) {
                  			   String[] split = returnValue.split("\\s+");
                  			   returnValue = split[0];
                  			   if (split.length>1) {
                  			   String returnParameter = split[1];
                  			   sdMethod.setReturnParameter(returnParameter);
                  			   }
                  		   }
                  		   sdMethod.setReturnType(new DataType(returnValue));
  	                	   sdMethod.setPriority(cm.getRank());
  	                	   dc.addSDMethod(sdMethod);
  	                	   boolean isIterative = cm.isIterative();
  	                	   out.println("AddedSDMethod: " + sdMethod);
  	                	   if (dc2 != null) {
  		                	   dc2 = (DesignClass) sdm.getSource().getClassifier();
  		                	   dc2.addCalledMethod(sdMethod, dc, isIterative,dcObject);
  		                	   if(headMethods.size() > 0) {
  		                		   headMethod=headMethods.get(headMethods.size()-1);
  		                	   }
  		                	   if(hasLifeline && headMethod!=null) {
  		                		 if (cm.isReflective() && dc2.getSDMethods().contains(headMethod)) {
  		                			Method methodToChange = (Method) dc2.getSDMethods().get(dc2.getSDMethods().indexOf(headMethod));
		                			methodToChange.addCalledMethod(sdMethod, dc, isIterative, dcObject, cm.isReflective());
		                			dc2.replaceSDMethod(dc2.getSDMethods().indexOf(headMethod), methodToChange);	
  	                			  }
  		                		  if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
  		                			 Method methodToChange = (Method) dc2.getSDMethods().get(dc2.getSDMethods().indexOf(headMethod));
  		                			 methodToChange.addCalledMethod(sdMethod, dc, isIterative, dcObject,cm.isReflective());
  		                			 dc2.replaceSDMethod(dc2.getSDMethods().indexOf(headMethod), methodToChange);	                			  
  		                		  }
  		                	   }
  	                	   }
  	                	   if(!sdMethod.getReturnType().getName().equals("void") && !sdMethod.getReturnType().getName().equals("VOID") && !cm.isReflective()) {
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
  	                	   dc2 = (DesignClass) sdm.getSource().getClassifier();
  	                	   dc2.addCalledMethod(destroyMethod, dc, false,dcObject);
  	                	   if(!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {
  	                			  Method methodToChange = (Method) dc2.getSDMethods().get(dc2.getSDMethods().indexOf(headMethod));
  	                			  methodToChange.addCalledMethod(destroyMethod, dc, false, dcObject,false);
  	                			  dc2.replaceSDMethod(dc2.getSDMethods().indexOf(headMethod), methodToChange);	                			  
  	                		   }
                  	   }   
                     }
                     if(sdm instanceof ReturnMessage) {
                    	   if((headMethods.size() > 1)){
		                  	   headMethod=headMethods.remove(headMethods.size()-1);
                    	   }
                    	   else {
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
	        String genPath = javaGenerator.generateFile(dci,projectPath,this);
	        out.println("Generated in: " + genPath);
		    if(genPath!=null) {
		    	genFilesCount++;
		    }
    	}
    	
    	return genFilesCount;
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
}
