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

    public int generateCode(boolean isInUpdateMode) {
        this.isInUpdateMode = true;
        genFilesCount = 0;
        CodeGenerator generator = new CodeGenerator();
        designClass = null;

        dcToGenerate = new ArrayList<>();

        for (DiagramModel diagram : umlProject.getDiagramModels()) {
            for (GraphicalElement element : diagram.getGraphicalElements()) {
                addDesignClass(processGraphicalElement(element));
            }
        }
        for (DiagramModel diagram : umlProject.getDiagramModels()) {
            // sort by rank and add Methods of Message Calls
            if (diagram instanceof SDModel) {
                designClass = processSDModel((SDModel) diagram);
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

    private DesignClass processClassGR(ClassGR currEl) {
        DesignClass dc = currEl.getDesignClass();
        dc.getCcDesignClass().setExtendClass(null);
        dc.getCcDesignClass().resetImplementInterfaces();
        return dc;
    }

    private DesignClass processAssociationClassGR(AssociationClassGR currEl) {
        return (DesignClass) currEl.getAssociationClass().getAssociationClass();
    }

    private DesignClass processRealizationGR(RealizationGR currEl) {
        Realization realz = currEl.getRealization();
        DesignClass dc = realz.getTheClass();
        dc.getCcDesignClass().addImplementInterfaces(realz.getTheInterface());
        return dc;
    }

    private DesignClass processGeneralizationGR(GeneralizationGR currEl) {
        Generalization genz = currEl.getGeneralization();
        DesignClass dc = (DesignClass) genz.getBaseClass();
        dc.getCcDesignClass().setExtendClass(genz.getSuperClass());
        return dc;
    }

    private DesignClass processInterfaceGR(InterfaceGR currEl) {
        Interface interfs = currEl.getInterface();
        String projectPath = new File(umlProject.getFilepath()).getParent();
        String genPath = javaGenerator.generateFile(isInUpdateMode, interfs, projectPath, umlProject);
        if (genPath != null) {
            genFilesCount++;
        }
        return null;
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
                datatype = new DataType(listOf(name));
            } else {
                datatype = new DataType(name);
            }
            dcA.getCcDesignClass().addAttribute(new Attribute(association.getRoleB().getDerivedName(), datatype));

        } else if (association.getDirection() == Association.BA && dcB != null) {
            DataType datatype = null;
            String name = association.getClassA().getName();
            if (isMulti(association.getRoleA().getMultiplicity())) {
                datatype = new DataType(listOf(name));
            } else {
                datatype = new DataType(name);
            }
            dcB.getCcDesignClass().addAttribute(new Attribute(association.getRoleA().getDerivedName(), datatype));

        } else if (dcA != null && dcB != null) {
            DataType datatype = null;
            String name = association.getClassB().getName();
            if (isMulti(association.getRoleB().getMultiplicity())) {
                datatype = new DataType(listOf(name));
            } else {
                datatype = new DataType(name);
            }
            dcA.getCcDesignClass().addAttribute(new Attribute(association.getRoleB().getDerivedName(), datatype));

            name = association.getClassA().getName();
            if (isMulti(association.getRoleA().getMultiplicity())) {
                datatype = new DataType(listOf(name));
            } else {
                datatype = new DataType(name);
            }
            dcB.getCcDesignClass().addAttribute(new Attribute(association.getRoleA().getDerivedName(), datatype));
        }
        return dcA;
    }

    private String listOf(String name) {
        return "List<" + name + ">";
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
            return processClassGR((ClassGR) currEl);
        if (currEl instanceof AssociationClassGR)
            return processAssociationClassGR((AssociationClassGR) currEl);
        if (currEl instanceof RealizationGR)
            return processRealizationGR((RealizationGR) currEl);
        if (currEl instanceof GeneralizationGR)
            return processGeneralizationGR((GeneralizationGR) currEl);
        if (currEl instanceof InterfaceGR)
            return processInterfaceGR((InterfaceGR) currEl);
        if (currEl instanceof AssociationGR && !(currEl instanceof AggregationGR))
            return processAssociation(((AssociationGR) currEl).getAssociation());
        if (currEl instanceof AggregationGR)
            return processAssociation(((AggregationGR) currEl).getAggregation());
        if (currEl instanceof SDObjectGR) 
            return ((SDObjectGR) currEl).getSDObject().getDesignClass();
        if (currEl instanceof MultiObjectGR) 
            return ((MultiObjectGR) currEl).getMultiObject().getDesignClass();
        
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

    private DesignClass processSDModel(SDModel sdModel) {
        designClass = null;
        boolean hasLifeline = false;
        Map<SDMessage, Integer> sdMessages = new HashMap<>();

        /*
         * for collecting all the called methods
         */
        List<Method> headMethods = new ArrayList<>();

        for (GraphicalElement currElSD : sdModel.getGraphicalElements()) {
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
                DesignClass source = null;
                if (sdm.getSource() != null && sdm.getSource().getClassifier() instanceof DesignClass) {
                    source = (DesignClass) sdm.getSource().getClassifier();
                }
                RoleClassifier target = sdm.getTarget();

                if (sdm instanceof CreateMessage) {
                    hasLifeline = processCreateMessage((CreateMessage) sdm, source, target, headMethods, hasLifeline);
                }
                if (sdm instanceof CallMessage) {
                    hasLifeline = processCallMessage((CallMessage) sdm, source, target, headMethods, hasLifeline);
                }
                if (sdm instanceof DestroyMessage) {
                    hasLifeline = processDestroyMessage(sdm, source, target, headMethods, hasLifeline);
                }
                if (sdm instanceof ReturnMessage) {
                    hasLifeline = processReturnMessage((ReturnMessage) sdm, source, target, headMethods, hasLifeline);
                }
            }
        }
        return designClass;
    }

    private boolean processCreateMessage(CreateMessage message, DesignClass source, RoleClassifier target,
            List<Method> headMethods, boolean hasLifeline) {
        Method constructor = new Method(designClass.getName());
        constructor.getCCMethod().setPriority(message.getRank());
        Vector<MethodParameter> constructorParameters = message.getParameters();
        if (!constructorParameters.isEmpty()) {
            constructor.setParameters(constructorParameters);
        }
        if (!designClass.getCcDesignClass().getSDMethods().contains(constructor)
                && (message.getTarget() instanceof SDObject)) {
            designClass.getCcDesignClass().addSDMethod(constructor);
        }

        Method headMethod = null;
        if (source != null) {
            if (!headMethods.isEmpty()) {
                headMethod = headMethods.get(headMethods.size() - 1);
            }
            if (hasLifeline && headMethod != null && !designClass.getCcDesignClass().getSDMethods().contains(headMethod)
                    && source.getCcDesignClass().getSDMethods().contains(headMethod)) {
                addToHeadMethod(designClass, source, headMethod, constructor, false, target);
            }
        }
        hasLifeline = true;
        headMethods.add(constructor);

        return hasLifeline;
    }

    private boolean processCallMessage(CallMessage message, DesignClass source, RoleClassifier target,
            List<Method> headMethods, boolean hasLifeline) {
        Method sdMethod = new Method(message.getName());
        sdMethod.setParameters(message.getParameters());

        sdMethod.setReturnType(message.getReturnType());

        sdMethod.getCCMethod().setPriority(message.getRank());
        if (!(message.getTarget() instanceof MultiObject && !message.isIterative())) {
            designClass.getCcDesignClass().addSDMethod(sdMethod);
        }
        sdMethod.getCCMethod().setIterative(message.isIterative());
        if (source != null) {
            Method headMethod = null;

            if (!headMethods.isEmpty()) {
                headMethod = headMethods.get(headMethods.size() - 1);
            }
            if (hasLifeline && headMethod != null) {
                if (message.isReflective() && source.getCcDesignClass().getSDMethods().contains(headMethod)) {

                    addToHeadMethod(designClass, source, headMethod, sdMethod, message.isReflective(), target);
                }
                if (!designClass.getCcDesignClass().getSDMethods().contains(headMethod)
                        && source.getCcDesignClass().getSDMethods().contains(headMethod)) {

                    addToHeadMethod(designClass, source, headMethod, sdMethod, message.isReflective(), target);
                }
            }
        }
        if (!message.isReflective()) {
            hasLifeline = true;
            headMethods.add(sdMethod);
        }

        return hasLifeline;
    }

    private boolean processDestroyMessage(SDMessage message, DesignClass source, RoleClassifier target,
            List<Method> headMethods, boolean hasLifeline) {
        Method destroyMethod = new Method("destroy");
        destroyMethod.getCCMethod().setPriority(message.getRank());
        designClass.getCcDesignClass().addSDMethod(destroyMethod);
        Method headMethod = null;
        if (source != null) {
            if (!headMethods.isEmpty()) {
                headMethod = headMethods.get(headMethods.size() - 1);
            }

            if (hasLifeline && headMethod != null && !designClass.getCcDesignClass().getSDMethods().contains(headMethod)
                    && source.getCcDesignClass().getSDMethods().contains(headMethod)) {

                addToHeadMethod(designClass, source, headMethod, destroyMethod, false, target);
            }
        }
        return hasLifeline;
    }

    private boolean processReturnMessage(ReturnMessage message, DesignClass source, RoleClassifier target,
            List<Method> headMethods, boolean hasLifeline) {
        // check for parameter in return message and replace it in called Method
        Method headMethod = null;
        String returnParameter = message.getName();

        if (hasLifeline && !headMethods.isEmpty() && message.getTarget().getClassifier() instanceof DesignClass
                && !returnParameter.equals("")) {
            headMethod = headMethods.get(headMethods.size() - 1);
            List<Method> targetSdMethods = source.getCcDesignClass().getSDMethods();
            for (int i = 0; i < targetSdMethods.size(); i++) {
                Method checkMethod = targetSdMethods.get(i);
                if (checkMethod == headMethods.get(headMethods.size() - 2)) {
                    List<String> mtdCalledMethods = checkMethod.getCCMethod().getCalledMethods();
                    for (int c = 0; c < mtdCalledMethods.size(); c++) {
                        if (mtdCalledMethods.get(c).contains(headMethod.getName())) {
                            headMethod.getCCMethod().setReturnParameter(returnParameter);
                            mtdCalledMethods.set(c, generateCalledMethod(source, headMethod, target));
                            checkMethod.getCCMethod().replaceCalledMethod(c, mtdCalledMethods.get(c));
                        }
                    }
                    source.getCcDesignClass().replaceSDMethod(i, checkMethod);
                }
            }
        }
        // check headMethod (method that contains the branched called messages)
        if ((headMethods.size() > 1) && hasLifeline) {
            headMethods.remove(headMethods.size() - 1);
        } else if (hasLifeline) {
            headMethods.clear();
            hasLifeline = false;
        }

        return hasLifeline;
    }



    private String generateCalledMethod(DesignClass homeClass, Method m, RoleClassifier object) {
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
                if (attribute.getName().toLowerCase().equals(m.getCCMethod().getReturnParameter().toString().toLowerCase())) {
                    parameterExists = true;
                }
            }
            if (!parameterExists) {
                sb.append(m.getReturnTypeAsString() + " ");
            }
            sb.append(m.getCCMethod().getReturnParameter() + " = ");
        }
        if (object instanceof SDObject) {
            sb.append(object.getName()).append(".");
        } else if (object instanceof MultiObject && m.getCCMethod().isIterative()) {
            sb.append("obj.");
        } else if (object instanceof MultiObject && !m.getCCMethod().isIterative()) {
            sb.append(object.getName() + ".");
        }
        sb.append(m.getName()).append("(");
        sb.append(m.getCCMethod().getParametersAsString());
        sb.append(");");
        if (m.getCCMethod().isIterative()) {
            sb.append(LINE_SEPARATOR).append(" ");
            sb.append("   }");
        }
        return sb.toString();
    }

    private static Map<SDMessage, Integer> sortByValue(Map<SDMessage, Integer> hm) {
        List<Map.Entry<SDMessage, Integer>> list = new LinkedList<>(hm.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        Map<SDMessage, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<SDMessage, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void addToHeadMethod(DesignClass targetClass, DesignClass sourceClass, Method headMethod,
            Method sdMethod, boolean isReflective, RoleClassifier targetObject) {
        Method methodToChange = sourceClass.getCcDesignClass().getSDMethods().get(sourceClass.getCcDesignClass().getSDMethods().indexOf(headMethod));
        methodToChange.getCCMethod().addCalledMethod(sourceClass, sdMethod, targetClass, targetObject, isReflective);
        sourceClass.getCcDesignClass().replaceSDMethod(sourceClass.getCcDesignClass().getSDMethods().indexOf(headMethod), methodToChange);
    }
}
