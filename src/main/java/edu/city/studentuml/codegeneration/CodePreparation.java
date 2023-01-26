package edu.city.studentuml.codegeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import edu.city.studentuml.model.domain.MethodParameter;
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

/**
 *
 * @author dimitris
 */
public class CodePreparation {

    private UMLProject umlProject = UMLProject.getInstance();
    private static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
    private int genFilesCount;
    private DesignClass designClass;
    private List<DesignClass> dcToGenerate;
    private CodeGenerator javaGenerator = new CodeGenerator();
    private boolean isInUpdateMode;

    private DesignClass processClassGR(GraphicalElement currEl) {
        DesignClass dc = ((ClassGR) currEl).getDesignClass();
        dc.getCcDesignClass().setExtendClass(null);
        dc.getCcDesignClass().resetImplementInterfaces();
        return dc;
    }

    private DesignClass processAssociationClassGR(GraphicalElement currEl) {
        DesignClass dc;
        AssociationClassGR acgr = (AssociationClassGR) currEl;
        dc = (DesignClass) acgr.getAssociationClass().getAssociationClass();
        return dc;
    }

    private DesignClass processRealizationGR(GraphicalElement currEl) {
        Realization realz = ((RealizationGR) currEl).getRealization();
        DesignClass dc = realz.getTheClass();
        dc.getCcDesignClass().addImplementInterfaces(realz.getTheInterface());
        return dc;
    }

    private DesignClass processGeneralizationGR(GraphicalElement currEl) {
        DesignClass dc;
        Generalization genz = ((GeneralizationGR) currEl).getGeneralization();
        dc = (DesignClass) genz.getBaseClass();
        dc.getCcDesignClass().setExtendClass(genz.getSuperClass());
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
        if (association.getRoleA().getName().length() == 0) {
            association.getRoleA().setDefaultName(association.getClassA().getName().toLowerCase());
        }
        if (association.getRoleB().getName().length() == 0) {
            association.getRoleB().setDefaultName(association.getClassB().getName().toLowerCase());
        }
    }

    private DesignClass processAssociation(Association association) {
        DesignClass dcA = null;
        DesignClass dcB = null;
        setDefaultRoleNames(association);
        if (association.getClassA() instanceof DesignClass) {
            dcA = (DesignClass) association.getClassA();
        } 
        if (association.getClassB() instanceof DesignClass) {
            dcB = (DesignClass) association.getClassB();
        } 

        if (association.getDirection() == Association.AB && dcA != null) {
            DataType datatype = null;
            String name = association.getClassB().getName();
            if (isMulti(association.getRoleB().getMultiplicity())) {
                datatype = new DataType("List<" + name + ">");
            } else {
                datatype = new DataType(name);
            }
            dcA.getCcDesignClass().addAttribute(new Attribute(association.getRoleB().getDerivedName(), datatype));

        } else if (association.getDirection() == Association.BA && dcB != null) {
            DataType datatype = null;
            String name = association.getClassA().getName();
            if (isMulti(association.getRoleA().getMultiplicity())) {
                datatype = new DataType("List<" + name + ">");
            } else {
                datatype = new DataType(name);
            }
            dcB.getCcDesignClass().addAttribute(new Attribute(association.getRoleA().getDerivedName(), datatype));

        } else if (dcA != null && dcB != null) {
            DataType datatype = null;
            String name = association.getClassB().getName();
            if (isMulti(association.getRoleB().getMultiplicity())) {
                datatype = new DataType("List<" + name + ">");
            } else {
                datatype = new DataType(name);
            }
            dcA.getCcDesignClass().addAttribute(new Attribute(association.getRoleB().getDerivedName(), datatype));

            name = association.getClassA().getName();
            if (isMulti(association.getRoleA().getMultiplicity())) {
                datatype = new DataType("List<" + name + ">");
            } else {
                datatype = new DataType(name);
            }
            dcB.getCcDesignClass().addAttribute(new Attribute(association.getRoleA().getDerivedName(), datatype));
        }
        return dcA;
    }

    /**
     * Naive method checking only for *
     * TODO: for 0..1 and 1
     * 
     * @param multiplicity
     * @return
     */
    private boolean isMulti(String multiplicity) {
        return multiplicity.contains("*");
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
            return processAssociation(((AssociationGR) currEl).getAssociation());
        if (currEl instanceof AggregationGR)
            return processAssociation(((AggregationGR) currEl).getAggregation());

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
        designClass = null;
        DesignClass dc2 = null;
        boolean hasLifeline = false;
        Method headMethod = null;
        Map<SDMessage, Integer> sdMessages = new HashMap<>();

        List<Method> headMethods = new ArrayList<>();
        for (GraphicalElement currElSD : currDiagram.getGraphicalElements()) {
            if (currElSD instanceof SDMessageGR) {
                SDMessage sdmx = ((SDMessageGR) currElSD).getMessage();
                sdMessages.put(sdmx, sdmx.getRank());
            }
        }
        if (!sdMessages.isEmpty()) {
            sdMessages = sortByValue(sdMessages);
            for (Map.Entry<SDMessage, Integer> SDMessage : sdMessages.entrySet()) {
                SDMessage sdm = SDMessage.getKey();
                if (sdm.getTarget() != null && sdm.getTarget().getClassifier() instanceof DesignClass) {
                    designClass = (DesignClass) sdm.getTarget().getClassifier();
                }
                dc2 = null;
                if (sdm.getSource() != null && sdm.getSource().getClassifier() instanceof DesignClass) {
                    dc2 = (DesignClass) sdm.getSource().getClassifier();
                }
                RoleClassifier dcObject = sdm.getTarget();

                if (sdm instanceof CreateMessage) {
                    Method constructor = new Method(designClass.getName());
                    constructor.setPriority(sdm.getRank());
                    Vector<MethodParameter> constructorParameters = ((CreateMessage) sdm).getSDMethodParameters();
                    if (!constructorParameters.isEmpty() && constructorParameters != null) {
                        constructor.setParameters(constructorParameters);
                    } else {
                        constructor.setParameters(new Vector<>());
                    }
                    if (!designClass.getCcDesignClass().getSDMethods().contains(constructor) && (sdm.getTarget() instanceof SDObject)) {
                        designClass.getCcDesignClass().addSDMethod(constructor);
                    }

                    if (dc2 != null) {
                        if (!headMethods.isEmpty()) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        dc2 = (DesignClass) sdm.getSource().getClassifier();

                        if (hasLifeline && headMethod != null) {
                            if (!designClass.getCcDesignClass().getSDMethods().contains(headMethod) && dc2.getCcDesignClass().getSDMethods().contains(headMethod)) {

                                dc2 = addToHeadMethod(designClass, dc2, headMethod, constructor, false, dcObject);
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
                        sdMethod.setParameters(cm.getParameters());

                        sdMethod.setReturnType(((CallMessage) sdm).getReturnType());

                        sdMethod.setPriority(cm.getRank());
                        if (!(cm.getTarget() instanceof MultiObject && !cm.isIterative())) {
                            designClass.getCcDesignClass().addSDMethod(sdMethod);
                        }
                        sdMethod.getCCMethod().setIterative(cm.isIterative());
                        if (dc2 != null) {
                            dc2 = (DesignClass) sdm.getSource().getClassifier();

                            if (!headMethods.isEmpty()) {
                                headMethod = headMethods.get(headMethods.size() - 1);
                            }
                            if (hasLifeline && headMethod != null) {
                                if (cm.isReflective() && dc2.getCcDesignClass().getSDMethods().contains(headMethod)) {

                                    dc2 = addToHeadMethod(designClass, dc2, headMethod, sdMethod, cm.isReflective(), dcObject);
                                }
                                if (!designClass.getCcDesignClass().getSDMethods().contains(headMethod)
                                        && dc2.getCcDesignClass().getSDMethods().contains(headMethod)) {

                                    dc2 = addToHeadMethod(designClass, dc2, headMethod, sdMethod, cm.isReflective(), dcObject);
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
                    designClass.getCcDesignClass().addSDMethod(destroyMethod);
                    if (dc2 != null) {
                        if (!headMethods.isEmpty()) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        dc2 = (DesignClass) sdm.getSource().getClassifier();

                        if (hasLifeline && headMethod != null
                                && !designClass.getCcDesignClass().getSDMethods().contains(headMethod)
                                && dc2.getCcDesignClass().getSDMethods().contains(headMethod)) {

                            dc2 = addToHeadMethod(designClass, dc2, headMethod, destroyMethod, false, dcObject);

                        }
                    }
                }
                if (sdm instanceof ReturnMessage) {
                    // check for parameter in return message and replace it in called Method
                    if (hasLifeline) {
                        ReturnMessage rm = (ReturnMessage) sdm;
                        if (!headMethods.isEmpty()) {
                            headMethod = headMethods.get(headMethods.size() - 1);
                        }
                        if (headMethod != null && rm.getTarget().getClassifier() instanceof DesignClass) {
                            dc2 = (DesignClass) rm.getTarget().getClassifier();
                            String returnParameter = rm.getName();
                            if (!returnParameter.equals("")) {
                                if (sdm.getSource() instanceof SDObject || sdm.getSource() instanceof MultiObject) {
                                    dcObject = sdm.getSource();
                                } 

                                if (!headMethods.isEmpty()) {
                                    List<Method> targetSdMethods = dc2.getCcDesignClass().getSDMethods();
                                    for (int i = 0; i < targetSdMethods.size(); i++) {
                                        Method checkMethod = targetSdMethods.get(i);
                                        if (checkMethod == headMethods.get(headMethods.size() - 2)) {
                                            List<String> mtdCalledMethods = checkMethod.getCCMethod().getCalledMethods();
                                            for (int c = 0; c < mtdCalledMethods.size(); c++) {
                                                if (mtdCalledMethods.get(c).contains(headMethod.getName())) {
                                                    headMethod.setReturnParameter(returnParameter);
                                                    mtdCalledMethods.set(c,
                                                            generateCalledMethod(dc2, headMethod, dcObject));
                                                    checkMethod.getCCMethod().replaceCalledMethod(c, mtdCalledMethods.get(c));
                                                }
                                            }
                                            dc2.getCcDesignClass().replaceSDMethod(i, checkMethod);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // check headMethod (method that contains the branched called messages)
                    if ((headMethods.size() > 1) && hasLifeline) {
                        headMethods.remove(headMethods.size() - 1);
                    } else if (hasLifeline) {
                        headMethods.clear();
                        hasLifeline = false;
                        headMethod = null;
                    }
                }
            }
        }
        return designClass;
    }

    public int generateCode(boolean isInUpdateMode) {
        this.isInUpdateMode = true;
        genFilesCount = 0;
        CodeGenerator generator = new CodeGenerator();
        designClass = null;

        dcToGenerate = new ArrayList<>();

        for (DiagramModel currDiagram : umlProject.getDiagramModels()) {
            for (GraphicalElement currEl : currDiagram.getGraphicalElements()) {
                DesignClass dc = processGraphicalElement(currEl);
                addDesignClass(dc);
            }
        }
        for (DiagramModel currDiagram : umlProject.getDiagramModels()) {
            // sort by rank and add Methods of Message Calls
            if (currDiagram instanceof SDModel) {
                designClass = processSDModel((SDModel) currDiagram);
            }

            // why check? Does the previous loop leave something?
            if (designClass != null) {
                if (dcToGenerate.contains(designClass)) {
                    dcToGenerate.set(dcToGenerate.indexOf(designClass), designClass);
                } else {
                    dcToGenerate.add(designClass);
                }
            }
        }
        for (DesignClass dci : dcToGenerate) {
            String projectPath = new File(umlProject.getFilepath()).getParent();
            String genPath = generator.generateFile(isInUpdateMode, dci, projectPath, umlProject);
            if (genPath != null) {
                genFilesCount++;
            }
        }

        return genFilesCount;
    }

    public String generateCalledMethod(DesignClass homeClass, Method m, RoleClassifier object) {
        StringBuilder sb = new StringBuilder();
        if (m.getCCMethod().isIterative() && object instanceof SDObject) {
            sb.append("for(int i=0;i<10;i++){").append(LINE_SEPARATOR);
            sb.append("     ");
        } else if (m.getCCMethod().isIterative() && object instanceof MultiObject) {
            sb.append("for(" + object.getClassifier().getName() + " obj : " + object.getName() + ") {")
                    .append(LINE_SEPARATOR);
            sb.append("     ");
        }
        if (!m.getReturnType().getName().equals("void") && !m.getReturnType().getName().equals("VOID")) {
            List<Attribute> attributes = homeClass.getAttributes();
            boolean parameterExists = false;
            Attribute attribute;
            for (int i = 0; i < attributes.size(); i++) {
                attribute = attributes.get(i);
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
        } else if (object instanceof MultiObject && m.getCCMethod().isIterative()) {
            sb.append("obj.");
        } else if (object instanceof MultiObject && !m.getCCMethod().isIterative()) {
            sb.append(object.getName() + ".");
        }
        sb.append(m.getName()).append("(");
        sb.append(m.getParametersAsString());
        sb.append(");");
        if (m.getCCMethod().isIterative()) {
            sb.append(LINE_SEPARATOR).append(" ");
            sb.append("   }");
        }
        return sb.toString();
    }

    public static Map<SDMessage, Integer> sortByValue(Map<SDMessage, Integer> hm) {
        List<Map.Entry<SDMessage, Integer>> list = new LinkedList<>(hm.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        Map<SDMessage, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<SDMessage, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public DesignClass addToHeadMethod(DesignClass targetClass, DesignClass sourceClass, Method headMethod,
            Method sdMethod, boolean isReflective, RoleClassifier targetObject) {
        Method methodToChange = sourceClass.getCcDesignClass().getSDMethods().get(sourceClass.getCcDesignClass().getSDMethods().indexOf(headMethod));
        methodToChange.getCCMethod().addCalledMethod(sourceClass, sdMethod, targetClass, targetObject, isReflective);
        sourceClass.getCcDesignClass().replaceSDMethod(sourceClass.getCcDesignClass().getSDMethods().indexOf(headMethod), methodToChange);
        return sourceClass;
    }
}
