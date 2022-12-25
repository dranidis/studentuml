package edu.city.studentuml.codegeneration;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDMessage;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author dimitris
 */
public class CodePreparation {
    private UMLProject umlProject = UMLProject.getInstance();
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
    private int genFilesCount;
    private DesignClass dc;
    private List<DesignClass> dcToGenerate;
    private CodeGenerator javaGenerator = new CodeGenerator();
    private boolean isInUpdateMode;

    private DesignClass processClassGR(GraphicalElement currEl) {
        DesignClass dc = ((ClassGR) currEl).getDesignClass();
        dc.setExtendClass(null);
        dc.resetImplementInterfaces();
        return dc;
    }

    private DesignClass processAssociationClassGR(GraphicalElement currEl) {
        DesignClass dc;
        AssociationClassGR acgr = (AssociationClassGR) currEl;
        dc = (DesignClass) acgr.getAssociationClass().getAssociationClass();
        return dc;
    }

    private DesignClass processRealizationGR(GraphicalElement currEl) {
        DesignClass dc;
        Realization realz = ((RealizationGR) currEl).getRealization();
        dc = realz.getTheClass();
        dc.addImplementInterfaces(realz.getTheInterface());
        return dc;
    }

    private DesignClass processGeneralizationGR(GraphicalElement currEl) {
        DesignClass dc;
        Generalization genz = ((GeneralizationGR) currEl).getGeneralization();
        dc = (DesignClass) genz.getBaseClass();
        dc.setExtendClass(genz.getSuperClass());
        return dc;
    }

    private DesignClass processInterfaceGR(GraphicalElement currEl) {
        Interface interfs = null;
        DesignClass dc = null;
        interfs = ((InterfaceGR) currEl).getInterface();
        String projectPath = new File(umlProject.getFilepath()).getParent();
        String genPath = javaGenerator.generateFile(isInUpdateMode, interfs, projectPath, umlProject);
        if (genPath != null) {
            genFilesCount++;
        }
        return dc;
    }

    private void setDefaultRoleNames(Association association) {
        if (association.getRoleA().getName() == null || association.getRoleA().getName().length() == 0) {
            association.getRoleA().setName(association.getClassA().getName().toLowerCase());
        }
        if (association.getRoleB().getName() == null || association.getRoleB().getName().length() == 0) {
            association.getRoleB().setName(association.getClassB().getName().toLowerCase());
        }
    }

    private DesignClass processAssociationGR(GraphicalElement currEl) {
        Interface interfs = null;
        DesignClass dc = null;
        DesignClass dc2 = null;
        Association association = ((AssociationGR) currEl).getAssociation();
        setDefaultRoleNames(association);
        if (association.getClassA() instanceof DesignClass) {
            dc = (DesignClass) association.getClassA();
        } else if (association.getClassA() instanceof Interface) {
            interfs = (Interface) association.getClassA();
        }
        if (association.getClassB() instanceof DesignClass) {
            dc2 = (DesignClass) association.getClassB();
        } else if (association.getClassB() instanceof Interface) {
            interfs = (Interface) association.getClassB();
        }
        if (association.getDirection() == Association.AB) {
            if (association.getRoleB().getMultiplicity() != null
                    && association.getRoleB().getMultiplicity().contains("*")) {
                if (association.getClassB() instanceof DesignClass) {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(),
                            new DataType("List<" + dc2.getName() + ">")));
                }
                if (association.getClassB() instanceof Interface) {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(),
                            new DataType("List<" + interfs.getName() + ">")));
                }
            } else {
                if (association.getClassB() instanceof DesignClass) {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(), new DataType(dc2.getName())));
                }
                if (association.getClassB() instanceof Interface) {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(), new DataType(interfs.getName())));
                }
            }
        } else if (association.getDirection() == Association.BA) {
            if (association.getRoleA().getMultiplicity() != null
                    && association.getRoleA().getMultiplicity().contains("*")) {
                if (association.getClassA() instanceof DesignClass) {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(),
                            new DataType("List<" + dc.getName() + ">")));
                }
                if (association.getClassB() instanceof Interface) {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(),
                            new DataType("List<" + interfs.getName() + ">")));
                }
            } else {
                if (association.getClassA() instanceof DesignClass) {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(), new DataType(dc.getName())));
                }
                if (association.getClassA() instanceof Interface) {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(), new DataType(interfs.getName())));
                }
            }
        } else if (association.getDirection() == 3 || association.getDirection() == 0) {
            if (association.getClassA() instanceof DesignClass && association.getClassB() instanceof DesignClass) {
                if (association.getRoleB().getMultiplicity() != null
                        && association.getRoleB().getMultiplicity().contains("*")) {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(),
                            new DataType("List<" + dc2.getName() + ">")));
                } else {
                    dc.addAttribute(new Attribute(association.getRoleB().getName(), new DataType(dc2.getName())));
                }
                if (association.getRoleA().getMultiplicity() != null
                        && association.getRoleA().getMultiplicity().contains("*")) {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(),
                            new DataType("List<" + dc.getName() + ">")));
                } else {
                    dc2.addAttribute(new Attribute(association.getRoleA().getName(), new DataType(dc.getName())));
                }
            } else {
                // out.println("Biderectional association not applicable in interfaces");
            }
        }
        return dc;
    }

    private DesignClass processAggregationGR(GraphicalElement currEl) {
        Interface interfs = null;
        DesignClass dc = null;
        DesignClass dc2 = null;

        Aggregation aggregation = ((AggregationGR) currEl).getAggregation();
        if (aggregation.getRoleA().getName() == null || aggregation.getRoleA().getName().equals("")) {
            aggregation.getRoleA().setName(aggregation.getClassA().getName().toLowerCase());
        }
        if (aggregation.getRoleB().getName() == null || aggregation.getRoleB().getName().equals("")) {
            aggregation.getRoleB().setName(aggregation.getClassB().getName().toLowerCase());
        }
        if (aggregation.getClassA() instanceof DesignClass) {
            dc = (DesignClass) aggregation.getClassA();
        } else if (aggregation.getClassA() instanceof Interface) {
            interfs = (Interface) aggregation.getClassA();
        }
        if (aggregation.getClassB() instanceof DesignClass) {
            dc2 = (DesignClass) aggregation.getClassB();
        } else if (aggregation.getClassB() instanceof Interface) {
            interfs = (Interface) aggregation.getClassB();
        }
        if (aggregation.getDirection() == 1) {
            if (aggregation.getRoleB().getMultiplicity() != null
                    && aggregation.getRoleB().getMultiplicity().contains("*")) {
                if (aggregation.getClassB() instanceof DesignClass) {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),
                            new DataType("List<" + dc2.getName() + ">")));
                }
                if (aggregation.getClassB() instanceof Interface) {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),
                            new DataType("List<" + interfs.getName() + ">")));
                }
            } else {
                if (aggregation.getClassB() instanceof DesignClass) {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(), new DataType(dc2.getName())));
                }
                if (aggregation.getClassB() instanceof Interface) {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(), new DataType(interfs.getName())));
                }
            }

        } else if (aggregation.getDirection() == 2 || aggregation.getDirection() == 0) {
            if (aggregation.getRoleA().getMultiplicity() != null
                    && aggregation.getRoleA().getMultiplicity().contains("*")) {
                if (aggregation.getClassA() instanceof DesignClass) {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),
                            new DataType("List<" + dc.getName() + ">")));
                }
                if (aggregation.getClassB() instanceof Interface) {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),
                            new DataType("List<" + interfs.getName() + ">")));
                }
            } else {
                if (aggregation.getClassA() instanceof DesignClass) {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(), new DataType(dc.getName())));
                }
                if (aggregation.getClassA() instanceof Interface) {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(), new DataType(interfs.getName())));
                }
            }
        } else if (aggregation.getDirection() == 3) {
            if (aggregation.getClassA() instanceof DesignClass && aggregation.getClassB() instanceof DesignClass) {
                if (aggregation.getRoleB().getMultiplicity() != null
                        && aggregation.getRoleB().getMultiplicity().contains("*")) {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(),
                            new DataType("List<" + dc2.getName() + ">")));
                } else {
                    dc.addAttribute(new Attribute(aggregation.getRoleB().getName(), new DataType(dc2.getName())));
                }
                if (aggregation.getRoleA().getMultiplicity() != null
                        && aggregation.getRoleA().getMultiplicity().contains("*")) {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(),
                            new DataType("List<" + dc.getName() + ">")));
                } else {
                    dc2.addAttribute(new Attribute(aggregation.getRoleA().getName(), new DataType(dc.getName())));
                }
            }
        }

        return dc;
    }

    private DesignClass processGraphicalElement(GraphicalElement currEl) {
        DesignClass dc = null;

        if (currEl instanceof ClassGR)
            return processClassGR(currEl);
        if (currEl instanceof AssociationClassGR)
            return processAssociationClassGR(currEl);
        if (currEl instanceof RealizationGR)
            return processRealizationGR(currEl);
        if (currEl instanceof GeneralizationGR)
            return processGeneralizationGR(currEl);
        if (currEl instanceof InterfaceGR)
            return processInterfaceGR(currEl);
        if (currEl instanceof AssociationGR && !(currEl instanceof AggregationGR))
            return processAssociationGR(currEl);
        if (currEl instanceof AggregationGR)
            return processAggregationGR(currEl);

        if (currEl instanceof SDObjectGR) {
            dc = ((SDObjectGR) currEl).getSDObject().getDesignClass();
            return dc;
        }
        if (currEl instanceof MultiObjectGR) {
            dc = ((MultiObjectGR) currEl).getMultiObject().getDesignClass();
            return dc;
        }
        return dc;
    }

    private void addDesignClass(DesignClass dc) {
        if (dc != null) {
            if (dcToGenerate.contains(dc)) {
                dcToGenerate.set(dcToGenerate.indexOf(dc), dc);
            } else {
                dcToGenerate.add(dc);
            }
        }
    }

    private DesignClass processSDModel(SDModel currDiagram) {
        dc = null;
        DesignClass dc2 = null;
        boolean hasLifeline = false;
        Method headMethod = null;
        HashMap<SDMessage, Integer> SDMessages = new HashMap<SDMessage, Integer>();

        List<Method> headMethods = new ArrayList<Method>();
        for (GraphicalElement currElSD : currDiagram.getGraphicalElements()) {
            if (currElSD instanceof SDMessageGR) {
                SDMessage sdmx = ((SDMessageGR) currElSD).getMessage();
                SDMessages.put(sdmx, sdmx.getRank());
            }
        }
        if (!SDMessages.isEmpty()) {
            SDMessages = sortByValue(SDMessages);
            for (Map.Entry<SDMessage, Integer> SDMessage : SDMessages.entrySet()) {
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
                } else if (sdm.getTarget() instanceof MultiObject) {
                    dcObject = (MultiObject) sdm.getTarget();
                }
                if (sdm instanceof CreateMessage) {
                    Method constructor = new Method(dc.getName());
                    constructor.setPriority(sdm.getRank());
                    Vector constructorParameters = ((CreateMessage) sdm).getSDMethodParameters();
                    if (!constructorParameters.isEmpty() && constructorParameters != null) {
                        constructor.setParameters(constructorParameters);
                    } else {
                        constructor.setParameters(new Vector());
                    }
                    if (!dc.getSDMethods().contains(constructor) && (sdm.getTarget() instanceof SDObject)) {
                        dc.addSDMethod(constructor);
                    }

                    if (dc2 != null) {
                        if (headMethods.size() > 0) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        dc2 = (DesignClass) sdm.getSource().getClassifier();

                        if (hasLifeline && headMethod != null) {
                            if (!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {

                                dc2 = addToHeadMethod(dc, dc2, headMethod, constructor, false, dcObject);
                            }
                        }
                    }
                    hasLifeline = true;
                    headMethods.add(constructor);
                }
                if (sdm instanceof CallMessage) {
                    CallMessage cm = (CallMessage) sdm;
                    Method sdMethod = new Method(cm.getName());
                    if (sdMethod != null) {
                        // sdMethod.setParameters(cm.getSDMethodParameters());
                        sdMethod.setParameters(cm.getParameters());
                        // String returnValue = cm.getReturnValueAsString();
                        // if (returnValue.contains(" ")) {
                        // String[] split = returnValue.split("\\s+");
                        // returnValue = split[0];
                        // if(!returnValue.equals("")) {
                        // if (split.length>1) {
                        // String returnParameter = split[1];
                        // sdMethod.setReturnParameter(returnParameter);
                        // }
                        // }else {
                        // returnValue="void";
                        // }
                        // }
                        // sdMethod.setReturnType(new DataType(returnValue));
                        sdMethod.setReturnType(((CallMessage) sdm).getReturnType());

                        sdMethod.setPriority(cm.getRank());
                        if (!(cm.getTarget() instanceof MultiObject && !cm.isIterative())) {
                            dc.addSDMethod(sdMethod);
                        }
                        sdMethod.setIterative(cm.isIterative());
                        if (dc2 != null) {
                            dc2 = (DesignClass) sdm.getSource().getClassifier();

                            if (headMethods.size() > 0) {
                                headMethod = headMethods.get(headMethods.size() - 1);
                            }
                            if (hasLifeline && headMethod != null) {
                                if (cm.isReflective() && dc2.getSDMethods().contains(headMethod)) {

                                    dc2 = addToHeadMethod(dc, dc2, headMethod, sdMethod, cm.isReflective(), dcObject);
                                }
                                if (!dc.getSDMethods().contains(headMethod)
                                        && dc2.getSDMethods().contains(headMethod)) {

                                    dc2 = addToHeadMethod(dc, dc2, headMethod, sdMethod, cm.isReflective(), dcObject);
                                }
                            }
                        }
                        if (!cm.isReflective()) {
                            hasLifeline = true;
                            headMethods.add(sdMethod);
                        }
                    }
                }
                if (sdm instanceof DestroyMessage) {
                    Method destroyMethod = new Method("destroy");
                    destroyMethod.setPriority(sdm.getRank());
                    dc.addSDMethod(destroyMethod);
                    if (dc2 != null) {
                        if (headMethods.size() > 0) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        dc2 = (DesignClass) sdm.getSource().getClassifier();

                        if (hasLifeline && headMethod != null) {
                            if (!dc.getSDMethods().contains(headMethod) && dc2.getSDMethods().contains(headMethod)) {

                                dc2 = addToHeadMethod(dc, dc2, headMethod, destroyMethod, false, dcObject);
                            }
                        }
                    }
                }
                if (sdm instanceof ReturnMessage) {
                    // check for parameter in return message and replace it in called Method
                    if (hasLifeline) {
                        ReturnMessage rm = (ReturnMessage) sdm;
                        if (headMethods.size() > 0) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        if (headMethod != null && rm.getTarget().getClassifier() instanceof DesignClass) {
                            dc2 = (DesignClass) rm.getTarget().getClassifier();
                            String returnParameter = rm.getName();
                            if (!returnParameter.equals("")) {
                                if (sdm.getSource() instanceof SDObject) {
                                    dcObject = (SDObject) sdm.getSource();
                                } else if (sdm.getSource() instanceof MultiObject) {
                                    dcObject = (MultiObject) sdm.getSource();
                                }

                                if (headMethods.size() > 0) {
                                    Vector targetSdMethods = dc2.getSDMethods();
                                    for (int i = 0; i < targetSdMethods.size(); i++) {
                                        Method checkMethod = (Method) targetSdMethods.get(i);
                                        if (checkMethod == headMethods.get(headMethods.size() - 2)) {
                                            List<String> mtdCalledMethods = checkMethod.getCalledMethods();
                                            for (int c = 0; c < mtdCalledMethods.size(); c++) {
                                                if (mtdCalledMethods.get(c).contains(headMethod.getName())) {
                                                    headMethod.setReturnParameter(returnParameter);
                                                    mtdCalledMethods.set(c,
                                                            generateCalledMethod(dc2, headMethod, dcObject));
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
                    // check headMethod (method that contains the branched called messages)
                    if ((headMethods.size() > 1) && hasLifeline == true) {
                        headMethods.remove(headMethods.size() - 1);
                    } else if (hasLifeline == true) {
                        headMethods.clear();
                        hasLifeline = false;
                        headMethod = null;
                    }
                }
            }
        }
        return dc;
    }

    public int generateCode(boolean isInUpdateMode) {
        this.isInUpdateMode = true;
        genFilesCount = 0;
        CodeGenerator javaGenerator = new CodeGenerator();
        dc = null;

        dcToGenerate = new ArrayList<DesignClass>();

        for (DiagramModel currDiagram : umlProject.getDiagramModels()) {
            for (GraphicalElement currEl : currDiagram.getGraphicalElements()) {
                DesignClass dc = processGraphicalElement(currEl);
                addDesignClass(dc);
            }
        }
        for (DiagramModel currDiagram : umlProject.getDiagramModels()) {
            // sort by rank and add Methods of Message Calls
            if (currDiagram instanceof SDModel) {
                dc = processSDModel((SDModel) currDiagram);
            }

            // why check? Does the previous loop leave something?
            if (dc != null) {
                if (dcToGenerate.contains(dc)) {
                    dcToGenerate.set(dcToGenerate.indexOf(dc), dc);
                } else {
                    dcToGenerate.add(dc);
                }
            }
        }
        for (DesignClass dci : dcToGenerate) {
            String projectPath = new File(umlProject.getFilepath()).getParent();
            String genPath = javaGenerator.generateFile(isInUpdateMode, dci, projectPath, umlProject);
            if (genPath != null) {
                genFilesCount++;
            }
        }

        return genFilesCount;
    }

    public String generateCalledMethod(DesignClass homeClass, Method m, RoleClassifier object) {
        StringBuffer sb = new StringBuffer();
        if (m.isIterative() && object instanceof SDObject) {
            sb.append("for(int i=0;i<10;i++){").append(LINE_SEPARATOR);
            sb.append("     ");
        } else if (m.isIterative() && object instanceof MultiObject) {
            sb.append("for(" + object.getClassifier().getName() + " obj : " + object.getName() + ") {")
                    .append(LINE_SEPARATOR);
            sb.append("     ");
        }
        if (!m.getReturnType().getName().equals("void") && !m.getReturnType().getName().equals("VOID")) {
            Vector attributes = homeClass.getAttributes();
            boolean parameterExists = false;
            Attribute attribute;
            for (int i = 0; i < attributes.size(); i++) {
                attribute = (Attribute) attributes.get(i);
                if (attribute.getName().toLowerCase().equals(m.getReturnParameter().toString().toLowerCase())) {
                    parameterExists = true;
                }
            }
            if (!parameterExists) {
                sb.append(m.getReturnTypeAsString() + " ");
            }
            sb.append(m.getReturnParameter() + " = ");
        }
        if (object instanceof SDObject) {
            sb.append(object.getName()).append(".");
        } else if (object instanceof MultiObject && m.isIterative()) {
            sb.append("obj.");
        } else if (object instanceof MultiObject && !m.isIterative()) {
            sb.append(object.getName() + ".");
        }
        sb.append(m.getName()).append("(");
        sb.append(m.getParametersAsString());
        sb.append(");");
        if (m.isIterative()) {
            sb.append(LINE_SEPARATOR).append(" ");
            sb.append("   }");
        }
        return sb.toString();
    }

    public static HashMap<SDMessage, Integer> sortByValue(HashMap<SDMessage, Integer> hm) {
        List<Map.Entry<SDMessage, Integer>> list = new LinkedList<Map.Entry<SDMessage, Integer>>(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<SDMessage, Integer>>() {
            public int compare(Map.Entry<SDMessage, Integer> o1, Map.Entry<SDMessage, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<SDMessage, Integer> temp = new LinkedHashMap<SDMessage, Integer>();
        for (Map.Entry<SDMessage, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public DesignClass addToHeadMethod(DesignClass targetClass, DesignClass sourceClass, Method headMethod,
            Method sdMethod, boolean isReflective, RoleClassifier targetObject) {
        Method methodToChange = (Method) sourceClass.getSDMethods().get(sourceClass.getSDMethods().indexOf(headMethod));
        methodToChange.addCalledMethod(sourceClass, sdMethod, targetClass, targetObject, isReflective);
        sourceClass.replaceSDMethod(sourceClass.getSDMethods().indexOf(headMethod), methodToChange);
        return sourceClass;
    }
}
