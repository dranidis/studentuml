package edu.city.studentuml.util;

import edu.city.studentuml.util.Mode;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.NotifierVector;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

public class CodeGenerator {
	
	public static final Logger LOG = Logger.getLogger(UMLProject.class);
    private boolean verboseDocs;
    private boolean lfBeforeCurly;
    private static final boolean VERBOSE_DOCS = false;
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
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
    
    public CodeGenerator () {
    	
    }
    
    public String generateFile(Object classObject, String path,UMLProject umlproject) {
    	String name = null;
    	if ( classObject instanceof DesignClass) {
    		DesignClass cls = (DesignClass) classObject;
    		name = cls.getName();
    	}
    	if ( classObject instanceof Interface) {
    		Interface interfs = (Interface) classObject;
    		name = interfs.getName();
    	}
        if (name == null || name.length() == 0) {
            return null;
        }
       // Object classifier = modelElement;
        path =  path + File.separator + umlproject.getName().substring(0,umlproject.getName().lastIndexOf("."));
        String filename = name + ".java";
        StringBuilder sbPath = new StringBuilder(path);
        if (!path.endsWith(File.separator)) {
            sbPath.append(File.separator);
        }

        String packagePath = name;
        
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
    
    private String generateClassifier(Object cls) {
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
    
    StringBuffer generateClassifierStart(Object obj) {
        String sClassifierKeyword;
        StringBuffer sb = new StringBuffer(80);
        // Now add visibility
        sb.append("public ");
        // add base class/interface
        if (obj instanceof DesignClass) {
	        sClassifierKeyword = "class";
	        DesignClass cls = (DesignClass) obj;
	        String classStereotype = cls.getStereotype();
	        if (classStereotype != null && !classStereotype.isEmpty()){
	        	sb.append(classStereotype.toLowerCase()).append(" ");
	        }
	     // add classifier keyword and classifier name
	        
	        sb.append(sClassifierKeyword).append(" ");
			sb.append(cls.getName());
	        // add extended class
			if (cls.getExtendClass() != null) {
				sb.append(" ").append("extends");
				sb.append(" ").append(cls.getExtendClass().getName());
			}
			 // add implemented interfaces, if needed
			if (!cls.getImplementInterfaces().isEmpty()) {
				sb.append(" ").append("implements");
				sb.append(" ");
				List <Interface> implementInterfaces = cls.getImplementInterfaces();
				for (int i=0;i<implementInterfaces.size();i++) {
					sb.append(implementInterfaces.get(i).getName());
					if( i != implementInterfaces.size()-1) {
						sb.append(",");
					}
				}
			}
        } else if (obj instanceof Interface) {
        	sClassifierKeyword = "interface";
        	Interface interfs = (Interface) obj;
        	sb.append(sClassifierKeyword).append(" ");
			sb.append(interfs.getName());    	
        }
        
		// add type parameters
		
        // add opening brace
        sb.append(lfBeforeCurly ? (LINE_SEPARATOR + "{") : " {");

        return sb;
    }
    
    private StringBuffer generateClassifierBody(Object obj) {
        StringBuffer sb = new StringBuffer();
        Vector classMethods = new Vector();
        Vector classSDMethods = new Vector();
        boolean first;
        
        if( obj instanceof DesignClass) {
	        DesignClass cls = (DesignClass) obj;	
	        Vector classAttributes = cls.getAttributes();
	
	        if (!classAttributes.isEmpty()) {
	            sb.append(LINE_SEPARATOR);
	            sb.append(INDENT).append("// Attributes");
	            sb.append(LINE_SEPARATOR);
	        }
	
			
			for (int i = 0; i < classAttributes.size(); i++) {
			   
			    sb.append(INDENT);
	            Attribute classAttribute = (Attribute) classAttributes.get(i);         
	            sb.append(generateAttribute(classAttribute, false));
	
	            }
			
			classMethods = cls.getMethods();
			classSDMethods = cls.getSDMethods();
			HashMap<String,Integer> calledMethods = cls.getCalledMethods();
			if (!calledMethods.isEmpty()) {
				sb.append(LINE_SEPARATOR);
	            sb.append(INDENT).append("// calledMethods");
	            sb.append(LINE_SEPARATOR);
			}
			for (Map.Entry<String,Integer> calledMethod : calledMethods.entrySet()) {
				sb.append(INDENT).append(calledMethod.getKey());
				sb.append(LINE_SEPARATOR);
	/*		for (int i=0; i<calledMethods.size();i++) {
				sb.append(INDENT).append(calledMethods.get(i));
				sb.append(LINE_SEPARATOR);
	*/		}
			
        }
        // add operations
        // TODO: constructors
        if (obj instanceof Interface) {
	        Interface interfs = (Interface) obj;	
	        classMethods = interfs.getMethods();
        }
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
		first = true;
		for (int x = 0; x < classSDMethods.size(); x++) {
			Method classSDMethod = (Method) classSDMethods.get(x);
			boolean equal = false;
			for (int y = 0; y < classMethods.size(); y++) {
				Method tempMethod =  (Method) classMethods.get(y); 
				if (classSDMethod.getName().equals(tempMethod.getName())) {
					equal = true;
				}
			}
			if (!equal) {
				if (!classSDMethod.getName().equals("create")) {
					 if (first) {
		                    sb.append(LINE_SEPARATOR);
		                }
					sb.append(INDENT);
					  sb.append(generateOperation(classSDMethod, false));
	
			            if (lfBeforeCurly) {
			                sb.append(LINE_SEPARATOR).append(INDENT);
			            } else {
			                sb.append(' ');
			            }
			            sb.append('{');
	
			            // there is no ReturnType in behavioral feature (UML)
			            sb.append(LINE_SEPARATOR);
						sb.append(generateMethodBody(classSDMethod));
						sb.append(INDENT);
						sb.append("}").append(LINE_SEPARATOR);
						first = false;
				}
			}
		}   	
        return sb;
    }
    
    String generateOperation(Method op, boolean documented) {
        if (isFileGeneration) {
            documented = true; 
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
        
        if (op != null) {
        	Type returnType = op.getReturnType();

            // pick out return type
        	if (returnType != null) {
        		if(!returnType.getName().equals("void")) {
        			return INDENT + generateDefaultReturnStatement(returnType);
        		}
                return generateDefaultReturnStatement(returnType);
            }
        }
        return generateDefaultReturnStatement(null);
    }
    
    private String generateDefaultReturnStatement(Type type) {
        if (type == null) {
            return "";
        }

        String typeName = type.getName();
        if (typeName.equals("void")) {
            return "";
        }
        if (typeName.equals("char")) {
            return INDENT + "return 'x';" + LINE_SEPARATOR;
        }
        if (typeName.equals("int")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (typeName.equals("boolean")) {
            return INDENT + "return false;" + LINE_SEPARATOR;
        }
        if (typeName.equals("byte")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (typeName.equals("long")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (typeName.equals("float")) {
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        }
        if (typeName.equals("double")) {
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
    
    private StringBuffer generateClassifierEnd(Object obj) {
        StringBuffer sb = new StringBuffer();
        String classifierkeyword;
        if(obj instanceof DesignClass) {
        	classifierkeyword = "class";
        	DesignClass cls = (DesignClass) obj;
        	sb.append(LINE_SEPARATOR);
    		sb.append("//end of ").append(classifierkeyword);
    		sb.append(" ").append(cls.getName());
        	
        }
        if(obj instanceof Interface) {
        	classifierkeyword = "interface";
        	Interface interfs = (Interface) obj;
        	sb.append(LINE_SEPARATOR);
    		sb.append("//end of ").append(classifierkeyword);
    		sb.append(" ").append(interfs.getName());
        	
        }
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
	

}
