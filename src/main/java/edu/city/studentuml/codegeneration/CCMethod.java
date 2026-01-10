package edu.city.studentuml.codegeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.util.NotifierVector;

public class CCMethod {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private boolean iterative = false;

    private List<String> calledMethods = new ArrayList<>(); // used by codegeneration; refactor

    private int priority;

    private String returnParameter = "x"; // why x?

    private Method method;

    public CCMethod(Method method) {
        this.method = method;
    }

    public void addCalledMethod(DesignClass homeClass, Method m, DesignClass calledClass, RoleClassifier object,
            boolean isReflective) {
        // create a string with the call message for the method
        StringBuilder sb = new StringBuilder();
        boolean parameterExists = false;
        Attribute attribute;
        NotifierVector<Attribute> attributes = homeClass.getAttributes();

        if (m.getName().equals(calledClass.getName())) {
            for (int i = 0; i < attributes.size(); i++) {
                attribute = attributes.get(i);
                if (attribute.getName().equalsIgnoreCase(object.getName())) {
                    parameterExists = true;
                }
            }
            if (!parameterExists && object instanceof SDObject) {
                sb.append(calledClass.getName() + " ");
            }
            if (!parameterExists && object instanceof MultiObject) {
                sb.append("List<" + calledClass.getName() + "> ");
            }
            if (object instanceof SDObject) {
                sb.append(object.getName()).append(" = ");
                sb.append("new ").append(calledClass.getName() + "(" + m.getCCMethod().getParametersAsString() + ")" + ";");
            } else if (object instanceof MultiObject) {
                sb.append(object.getName() + " = new ArrayList<" + calledClass.getName() + ">();");
            }
        } else if (m.getName().equals("destroy") && object instanceof SDObject) {
            sb.append(object.getName() + ".destroy()").append(";");
        } else if (m.getName().equals("destroy") && object instanceof MultiObject) {
            sb.append(object.getName() + " = null").append(";");
        } else {
            if (m.getCCMethod().isIterative() && object instanceof SDObject) {
                sb.append("for(int i=0;i<10;i++){").append(LINE_SEPARATOR);
                sb.append("     ");
            } else if (m.getCCMethod().isIterative() && object instanceof MultiObject) {
                sb.append("for(" + calledClass.getName() + " obj : " + object.getName() + ") {").append(LINE_SEPARATOR);
                sb.append("     ");
            }
            if (!m.getReturnType().getName().equals("void") && !m.getReturnType().getName().equals("VOID")) {
                parameterExists = false;
                for (int i = 0; i < attributes.size(); i++) {
                    attribute = attributes.get(i);
                    if (attribute.getName().equalsIgnoreCase(m.getCCMethod().getReturnParameter())) {
                        parameterExists = true;
                    }
                }
                if (!parameterExists) {
                    sb.append(m.getReturnTypeAsString() + " ");
                }
                sb.append(m.getCCMethod().getReturnParameter() + " = ");
            }
            if (isReflective && object instanceof SDObject) {
                sb.append("this").append(".");
            } else if (object instanceof SDObject) {
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
        }
        this.calledMethods.add(sb.toString());
    }

    public List<String> getCalledMethods() {
        // sort by rank and return list of call messages
        return this.calledMethods;
    }

    public void clearCalledMethods() {
        this.calledMethods.clear();
    }

    public void replaceCalledMethod(int index, String newCallMethod) {
        this.calledMethods.set(index, newCallMethod);
    }

    public boolean isIterative() {
        return iterative;
    }

    public void setIterative(boolean i) {
        iterative = i;
    }

    public String getParametersAsString() {
        StringJoiner sj = new StringJoiner(", ");
        method.getParameters().forEach(par -> sj.add(par.getName()));

        return sj.toString();
    }

    public void setPriority(int mtdPriority) {
        this.priority = mtdPriority;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setReturnParameter(String newParameter) {
        this.returnParameter = newParameter;
    }

    public String getReturnParameter() {
        return this.returnParameter;
    }

}
