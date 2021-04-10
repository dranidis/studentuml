package edu.city.studentuml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;

public class IgnoreJSONTest {
    @Test
    public void designClass() {
        ObjectMapper mapper = new ObjectMapper();
        DesignClass dc = new DesignClass("A");
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dc);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
        assertTrue(jsonString.contains("attributes"));
        assertTrue(jsonString.contains("methods"));
        assertTrue(jsonString.contains("name"));
        assertTrue(jsonString.contains("internalid"));
        assertTrue(jsonString.contains("\"__type\" : \"DesignClass\""));
    }

    @Test
    public void classGr() {
        ObjectMapper mapper = new ObjectMapper();
        DesignClass dc = new DesignClass("A");
        ClassGR cl = new ClassGR(dc, new Point(23, 2));
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cl);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
        assertTrue(jsonString.contains("attributes"));
        assertTrue(jsonString.contains("methods"));
        assertTrue(jsonString.contains("name"));
        assertTrue(jsonString.contains("internalid"));
        assertTrue(jsonString.contains("\"__type\" : \"ClassGR\""));
        assertTrue(jsonString.contains("\"__type\" : \"DesignClass\""));
    }

    @Test
    public void interface_() {
        ObjectMapper mapper = new ObjectMapper();
        Interface in = new Interface("SomeI");
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(in);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertTrue(jsonString.contains("methods"));
        assertTrue(jsonString.contains("name"));
        assertTrue(jsonString.contains("\"__type\" : \"Interface\""));
    }

    @Test
    public void interfaceGr() {
        ObjectMapper mapper = new ObjectMapper();
        Interface in = new Interface("SomeI");
        InterfaceGR inGr = new InterfaceGR(in, new Point(1, 2));
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inGr);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertTrue(jsonString.contains("methods"));
        assertTrue(jsonString.contains("name"));
        assertTrue(jsonString.contains("interface"));
        assertTrue(jsonString.contains("\"__type\" : \"InterfaceGR\""));
        assertTrue(jsonString.contains("\"__type\" : \"Interface\""));
    }

    @Test
    public void designClassWithAttributesAndMethods() {
        ObjectMapper mapper = new ObjectMapper();
        DesignClass dc = new DesignClass("A");
        dc.addAttribute(new Attribute("length", DataType.INTEGER));
        Method m = new Method("setLength");
        m.addParameter(new MethodParameter("length", DataType.INTEGER));
        dc.addMethod(m);
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dc);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void graphicalClass() {
        ObjectMapper mapper = new ObjectMapper();
        DesignClass dc = new DesignClass("A");
        ClassGR graphicalClass = new ClassGR(new DesignClass("A"), new java.awt.Point(10, 12));
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphicalClass);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void diagramClass() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        DesignClass dc = new DesignClass("A");
        ClassGR graphicalClass = new ClassGR(dc, new java.awt.Point(10, 12));
        diagramModel.addGraphicalElement(graphicalClass);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void diagramClassWithAssociation() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        DesignClass dc1 = new DesignClass("A");
        ClassGR graphicalClass1 = new ClassGR(dc1, new java.awt.Point(10, 12));
        diagramModel.addGraphicalElement(graphicalClass1);
        DesignClass dc2 = new DesignClass("B");
        ClassGR graphicalClass2 = new ClassGR(dc2, new java.awt.Point(20, 22));
        diagramModel.addGraphicalElement(graphicalClass2);

        Association association = new Association(graphicalClass1.getClassifier(), graphicalClass2.getClassifier());
        association.setDirection(Association.AB);
        AssociationGR associationGR = new AssociationGR(graphicalClass1, graphicalClass2, association);
        diagramModel.addGraphicalElement(associationGR);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void emptyDiagram() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void emptyProject() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void projectWithTwoDiagrams() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        DCDModel diagramModel1 = new DCDModel("dcd diagram", umlProject);
        DCDModel diagramModel2 = new DCDModel("dcd2 diagram", umlProject);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void readXML() {
        ObjectMapper mapper = new ObjectMapper();
        String filename = "diagrams" + File.separator + "tests" + File.separator + "classes.xml";

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.loadFromXML(filename);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jsonString);
        // assertFalse(jsonString.contains("sdmethods"));
    }

}
