package edu.city.studentuml.codegeneration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.domain.UMLProject;

//@author Spyros Maniopoulos

public class CodeGenerator {

	private static final Logger logger = Logger.getLogger(CodeGenerator.class.getName());
	private boolean lfBeforeCurly;
	private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
	private boolean isUpdate = false;
	private static final String INDENT = "  ";

	public String generateFile(boolean isInUpdateMode, Classifier classObject, String path, UMLProject umlproject) {
		String name = classObject != null ? classObject.getName() : null;

		if (name == null || name.length() == 0 || path == null) {
			return null;
		}
		String uname = umlproject.getFilename();

		path += File.separator + uname.substring(0, uname.lastIndexOf("."));
		String filename = name + ".java";
		StringBuilder sbPath = new StringBuilder(path);
		if (!path.endsWith(File.separator)) {
			sbPath.append(File.separator);
		}

		String pathname = sbPath.toString() + filename;

		// now decide whether file exist and need an update or is to be
		// newly generated
		BufferedWriter fos = null;
		File f = new File(pathname);
		Map<Integer, String> oldLines = new HashMap<>();
		String line = null;
        if (!f.isDirectory() && !f.exists() && !Paths.get(path).toFile().isDirectory() && !f.getParentFile().mkdir()) {
            logger.severe(" could not make directory " + path);
            return null;
        }
		
		if (!f.isDirectory() && f.exists() && isInUpdateMode) {
			try {
				DesignClass cls = null;
				List<Attribute> classAttributes = new ArrayList<>();
				Vector methods = new Vector();
				Vector sdMethods = new Vector();
				int fileIndex = 0;
				boolean doesNotExist = true;
				if (classObject instanceof DesignClass) {
					cls = (DesignClass) classObject;
					classAttributes = cls.getCcDesignClass().getAttributes();
					methods = cls.getMethods();
					sdMethods = cls.getCcDesignClass().getSDMethods();
				}
				if (classObject instanceof Interface) {
					Interface infs = (Interface) classObject;
					methods = infs.getMethods();
				}
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					doesNotExist = true;
					if (line.contains(" class ") || line.contains(" interface ") || line.contains("//")
							|| line.trim().isEmpty() || line.contains("}") || line.contains("{")
							|| line.contains("return") || line.contains("import") || line.contains("this.")) {
						doesNotExist = false;
					}
					if (cls != null && line.contains(cls.getName())) {
							doesNotExist = false;
						
					}
					for (int i = 0; i < classAttributes.size(); i++) {
						Attribute classAttribute = classAttributes.get(i);
						if (line.contains(classAttribute.getName() + ";")) {
							doesNotExist = false;
						}
					}
					for (int i = 0; i < methods.size(); i++) {
						Method method = (Method) methods.get(i);
						if (line.contains(method.getName() + "(")) {
							doesNotExist = false;
						}
					}
					for (int i = 0; i < sdMethods.size(); i++) {
						Method sdMethod = (Method) sdMethods.get(i);
						if (line.contains(sdMethod.getName() + "(")) {
							doesNotExist = false;
						}
					}
					for (int i = 0; i < sdMethods.size(); i++) {
						Method sdMethod = (Method) sdMethods.get(i);
						List<String> calledMethodsInMethod = sdMethod.getCCMethod().getCalledMethods();
						for (int y = 0; y < calledMethodsInMethod.size(); y++) {
							String calledMethodInMethod = calledMethodsInMethod.get(y);
							if (calledMethodInMethod.contains(".")) {
								calledMethodInMethod = calledMethodInMethod.substring(
										calledMethodInMethod.lastIndexOf(".") + 1,
										calledMethodInMethod.lastIndexOf("("));
							}
							if (line.contains(calledMethodInMethod)) {
								doesNotExist = false;
							}
						}
					}
					if (doesNotExist) {
						isUpdate = true; // set true to enable updating
						oldLines.put(fileIndex, line);
					}
					fileIndex++;
				}
				fr.close();
				br.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		logger.fine(() -> "Generating " + f.getPath());
		String header = generateHeader();
		String src = generateClassifier(classObject);
		try {
			fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			fos.write(header);
			fos.write(src);
		} catch (IOException exp) {
			logger.severe("IO Exception: " + exp + ", for file: " + f.getPath());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException exp) {
				logger.severe("FAILED: " + f.getPath());
			}
		}
		if (isUpdate) {
			try {
				FileReader fr2 = new FileReader(f);
				BufferedReader br2 = new BufferedReader(fr2);
				int currLine = 0;
				List<String> lines = new ArrayList<>();
				while ((line = br2.readLine()) != null) {
					for (Map.Entry<Integer, String> oldLine : oldLines.entrySet()) {
						if (currLine == (oldLine.getKey())) {
							if (line.trim().isEmpty()) {
								line = line.replace(line, oldLine.getValue());
							} else {
								lines.add(oldLine.getValue());
							}
						}
					}
					lines.add(line);
					currLine++;
				}
				br2.close();
				fr2.close();
				BufferedWriter fos2 = new BufferedWriter(new FileWriter(f));
				for (String s : lines) {
					fos2.write(s);
					fos2.write(LINE_SEPARATOR);
				}
				fos2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return pathname;
	}

	private String generateHeader() {
		StringBuilder imports = new StringBuilder();
		imports.append(LINE_SEPARATOR);
		imports.append("import java.util.*;").append(LINE_SEPARATOR);
		imports.append(LINE_SEPARATOR);
		return imports.toString();
	}

	private String generateClassifier(Classifier cls) {
		StringBuilder returnValue = new StringBuilder();
		StringBuilder start = generateClassifierStart(cls);
		if (start.length() > 0) {
			StringBuilder body = generateClassifierBody(cls);
			returnValue.append(start.toString());
			if (body.length() > 0) {
				returnValue.append(LINE_SEPARATOR);
				returnValue.append(body);
				if (lfBeforeCurly) {
					returnValue.append(LINE_SEPARATOR);
				}
			}
			returnValue.append(generateClassifierEnd(cls).toString());
		}
		return returnValue.toString();
	}

	private String getClassifierKeyword(Classifier c) {
		if (c instanceof DesignClass)
			return "class";
		else if (c instanceof Interface)
			return "interface";
		else
			logger.severe("No keyword for classifier " + c.getName());
		return "";
	}

	private String getStereotype(Classifier c) {
		if (c instanceof DesignClass) {
			DesignClass cls = (DesignClass) c;
			if (cls.getStereotype() != null)
				return cls.getStereotype() + " ";
			else
				return "";
		}
		return "";
	}

	public StringBuilder generateClassifierStart(Classifier obj) {
		StringBuilder sb = new StringBuilder();
		// add visibility
		sb.append("public ");
		sb.append(getStereotype(obj));
		sb.append(getClassifierKeyword(obj)).append(" ");
		sb.append(obj.getName());
		// add base class/interface
		if (obj instanceof DesignClass) {
			// add extended class
			DesignClass cls = (DesignClass) obj;
			if (cls.getCcDesignClass().getExtendClass() != null) {
				sb.append(" ").append("extends");
				sb.append(" ").append(cls.getCcDesignClass().getExtendClass().getName());
			}
			// add implemented interfaces, if needed
			if (!cls.getCcDesignClass().getImplementInterfaces().isEmpty()) {
				sb.append(" ").append("implements");
				sb.append(" ");
				List<Interface> implementInterfaces = cls.getCcDesignClass().getImplementInterfaces();
				for (int i = 0; i < implementInterfaces.size(); i++) {
					sb.append(implementInterfaces.get(i).getName());
					if (i != implementInterfaces.size() - 1) {
						sb.append(",");
					}
				}
			}
		}

		// add opening brace
		sb.append(lfBeforeCurly ? (LINE_SEPARATOR + "{") : " {");

		return sb;
	}

	/**
	 * @param obj
	 * @return
	 */
	private StringBuilder generateClassifierBody(Classifier obj) {
		StringBuilder sb = new StringBuilder();
		List<Method> classMethods = new ArrayList<>();
		List<Method> classSDMethods = new ArrayList<>();

        if (obj instanceof DesignClass) {
			DesignClass cls = (DesignClass) obj;
            List<Attribute> classAttributes = cls.getAttributes();
			classAttributes.addAll(cls.getCcDesignClass().getAttributes());

			if (!classAttributes.isEmpty()) {
				sb.append(LINE_SEPARATOR);
				sb.append(INDENT).append("// Generated Attributes");
				sb.append(LINE_SEPARATOR);
			}

			for (Attribute classAttribute : classAttributes) {
				sb.append(INDENT);
				sb.append(generateAttribute(classAttribute, false));
			}

			sb.append(LINE_SEPARATOR);
			classMethods = cls.getMethods();
			classSDMethods = cls.getCcDesignClass().getSDMethods();
		}
		// add operations
		if (obj instanceof Interface) {
			Interface interfs = (Interface) obj;
			classMethods = interfs.getMethods();
		}
		if (!classMethods.isEmpty() || !classSDMethods.isEmpty()) {
			sb.append(LINE_SEPARATOR);
			sb.append(INDENT).append("//Methods");
			sb.append(LINE_SEPARATOR);
		}

		for (Method classSDMethod : classSDMethods) {
			if (!classSDMethod.getName().equals("create") && !classSDMethod.getName().equals("destroy")) {
				for (Method classMethod : classMethods) {
					if (classSDMethod.getName().equals(classMethod.getName())) {
						classSDMethod.setVisibility(classMethod.getVisibility());
					}
				}
				generateMethodCode(obj, sb, classSDMethod);
			}
		}
        for (Method classMethod : classMethods) {
            if (!classMethod.getName().equals("create") && !classMethod.getName().equals("destroy")) {
                boolean equal = false;
                for (Method tempMethod : classSDMethods) {
                    if (classMethod.getName().equals(tempMethod.getName())) {
                        equal = true;
                    }
                }
                if (!equal) {
                    generateMethodCode(obj, sb, classMethod);
                }
            }
        }
		return sb;
	}

    private void generateMethodCode(Classifier obj, StringBuilder sb, Method classSDMethod) {
        sb.append(LINE_SEPARATOR);
        sb.append(INDENT).append("//Generated Method");
        sb.append(LINE_SEPARATOR);
        sb.append(INDENT);
        sb.append(generateOperation(classSDMethod, obj));

        if (lfBeforeCurly) {
        	sb.append(LINE_SEPARATOR).append(INDENT);
        } else {
        	sb.append(' ');
        }
        sb.append('{');

        sb.append(LINE_SEPARATOR);
        sb.append(generateMethodBody(classSDMethod, obj));
        sb.append(INDENT);
        sb.append("}").append(LINE_SEPARATOR);
    }

	public String generateOperation(Method op, Classifier obj) {
		StringBuilder sb = new StringBuilder(80);
		String nameStr = null;
		nameStr = op.getName();
		String className = obj.getName();

		sb.append(op.getVisibilityAsString()).append(' ');

		// return type
		if (op.getReturnTypeAsString() == "VOID" && !nameStr.equals(className)) {
			sb.append("void ");
		} else if (nameStr.equals(className)) {
			// constructor
		} else {
			sb.append(op.getReturnTypeAsString()).append(' ');
		}

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

	private String generateMethodBody(Method op, Object obj) {
		StringBuilder sb = new StringBuilder();
		DesignClass dcx = null;
		List<Attribute> attributes = new ArrayList<>();
		boolean isGetter = false;
		if (obj instanceof DesignClass) {
			dcx = (DesignClass) obj;
			attributes.addAll(dcx.getAttributes());
		}
		sb.append(generateCalledMethods(op));

		if (op != null && dcx != null) {
			Type returnType = op.getReturnType();
			String attributeName;
			String parameterName;
			if (op.getName().equals(dcx.getName()) && !op.getParameters().isEmpty()) {
				sb.append(LINE_SEPARATOR);
				sb.append(INDENT + INDENT).append("//Generated constructor setter");
				sb.append(LINE_SEPARATOR);
			}
			for (Attribute attr : attributes) {
				attributeName = attr.getName();
				String attributeCapitalized = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
				if (op.getName().equals(dcx.getName()) && !op.getParameters().isEmpty()) {
					for (MethodParameter param : op.getParameters()) {
						parameterName = param.getName();
						if (parameterName.equals(attributeName)) {
							sb.append(INDENT + INDENT).append("this." + attributeName + " = " + parameterName + ";");
							sb.append(LINE_SEPARATOR);
						}
					}
				}
				if (op.getName().equals("set" + attributeCapitalized) && !op.getParameters().isEmpty()) {
					sb.append(INDENT + INDENT).append("//Generated setter");
					sb.append(LINE_SEPARATOR);
					sb.append(INDENT + INDENT).append("this." + attributeName + " = " + op.getParameter(0).getName() + ";");
					sb.append(LINE_SEPARATOR);
				}
				if (op.getName().equals("get" + attributeCapitalized)) {
					sb.append(INDENT + INDENT).append("//Generated getter");
					sb.append(LINE_SEPARATOR);
					sb.append(INDENT + INDENT).append("return this." + attributeName + ";");
					sb.append(LINE_SEPARATOR);
					isGetter = true;
				}
			}
	        // pick out return type
            if (returnType != null && !returnType.getName().equals("void") && !returnType.getName().equals("VOID")
                    && !isGetter) {
                sb.append(INDENT + INDENT + "// Generated Return").append(LINE_SEPARATOR);
                sb.append(INDENT + generateDefaultReturnStatement(returnType));
            }
		}

		return sb.toString();
	}

	private String generateDefaultReturnStatement(Type type) {
		if (type == null) {
			return "";
		}

		String typeName = type.getName();
		if (typeName.equals("void") || typeName.equals("VOID")) {
			return "";
		}
		if (typeName.equals("String") || typeName.equals("string")) {
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
		StringBuilder sb = new StringBuilder();
		sb.append(parameter.getTypeAsString());
		sb.append(' ');
		sb.append(parameter.getName());
		// TODO: initial value
		return sb.toString();
	}

	private String generateAttribute(Attribute attr, boolean update) {
		StringBuilder sb = new StringBuilder();
		sb.append(generateCoreAttribute(attr));
		sb.append(";");
		if (!update) {
			sb.append(LINE_SEPARATOR);
		}
		return sb.toString();
	}

	private String generateCoreAttribute(Attribute attr) {
		StringBuilder sb = new StringBuilder();
		sb.append(attr.getVisibilityName()).append(' ');
		sb.append(attr.getType()).append(' ');
		sb.append(attr.getName());

		return sb.toString();
	}

	private StringBuilder generateClassifierEnd(Classifier obj) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR);
        sb.append("//end of ").append(obj instanceof DesignClass ? "class" : "interface");
        sb.append(" ").append(obj.getName());
        sb.append(LINE_SEPARATOR);
        sb.append("}");
        return sb;
    }

	private String generateCalledMethods(Method op) {
		StringBuilder sb = new StringBuilder();
		List<String> calledMethods = op.getCCMethod().getCalledMethods();
		if (!calledMethods.isEmpty()) {
			sb.append(LINE_SEPARATOR);
			sb.append(INDENT + INDENT).append("// Generated called Methods");
			sb.append(LINE_SEPARATOR);
		}
		for (int i = 0; i < calledMethods.size(); i++) {
			sb.append(INDENT + INDENT).append(calledMethods.get(i));
			sb.append(LINE_SEPARATOR);
		}
		return sb.toString();

	}
}
