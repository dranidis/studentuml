package edu.city.studentuml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void graphicalClass() {
        ObjectMapper mapper = new ObjectMapper();
        new DesignClass("A");
        ClassGR graphicalClass = new ClassGR(new DesignClass("A"), new java.awt.Point(10, 12));
        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphicalClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertFalse(jsonString.contains("sdmethods"));
    }

    @Test
    public void diagramClass() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        umlProject.clear();

        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        DesignClass dc = new DesignClass("A");
        ClassGR graphicalClass = new ClassGR(dc, new java.awt.Point(10, 12));
        diagramModel.addGraphicalElement(graphicalClass);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("\"name\" : \"dcd diagram\""));
    }

    @Test
    public void diagramClassWithAssociation() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        umlProject.clear();

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
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("association"));
    }

    @Test
    public void diagramClassWithAggregation() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        DesignClass dc1 = new DesignClass("A");
        ClassGR graphicalClass1 = new ClassGR(dc1, new java.awt.Point(10, 12));
        diagramModel.addGraphicalElement(graphicalClass1);
        DesignClass dc2 = new DesignClass("B");
        ClassGR graphicalClass2 = new ClassGR(dc2, new java.awt.Point(20, 22));
        diagramModel.addGraphicalElement(graphicalClass2);

        Aggregation aggregation = new Aggregation(dc1, dc2);
        AggregationGR aggregationGR = new AggregationGR(graphicalClass1, graphicalClass2, aggregation);
        diagramModel.addGraphicalElement(aggregationGR);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("association"));
    }

    @Test
    public void emptyDiagram() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        DCDModel diagramModel = new DCDModel("dcd diagram", umlProject);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diagramModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("dcd diagram"));
    }

    @Test
    public void emptyProject() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("diagramModels"));
    }

    @Test
    public void projectWithTwoDiagrams() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        new DCDModel("dcd diagram", umlProject);
        new DCDModel("dcd2 diagram", umlProject);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("dcd diagram"));
        assertTrue(jsonString.contains("dcd2 diagram"));
    }

    @Test
    public void projectWithTwoDiagramsTwoClassesAndGeneralizations() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        DCDModel dcd = new DCDModel("dcd diagram", umlProject);
        DCDModel dcd2 = new DCDModel("dcd2 diagram", umlProject);

        DesignClass dcA = new DesignClass("A");
        ClassGR cgrA = new ClassGR(dcA, new Point());
        DesignClass dcB = new DesignClass("B");
        ClassGR cgrB = new ClassGR(dcB, new Point());
        Generalization gen = new Generalization(dcA, dcB);
        GeneralizationGR genGR = new GeneralizationGR(cgrA, cgrB, gen);

        dcd.addGraphicalElement(cgrA);
        dcd.addGraphicalElement(cgrB);
        dcd.addGraphicalElement(genGR);

        ClassGR cgrA2 = new ClassGR(dcA, new Point());
        ClassGR cgrB2 = new ClassGR(dcB, new Point());
        Generalization gen2 = new Generalization(dcA, dcB);
        GeneralizationGR genGR2 = new GeneralizationGR(cgrA, cgrB, gen2);

        dcd2.addGraphicalElement(cgrA2);
        dcd2.addGraphicalElement(cgrB2);
        dcd2.addGraphicalElement(genGR2);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);

        int count = 0, index = 0;

        while ((index = jsonString.indexOf("\"internalid\" : \"generalization0\"", index)) != -1) {
            count++;
            index++;
        }

        assertEquals(2, count);
    }

    @Test
    public void projectWithTwoDiagramsAClassAnInterfaceAndRealizations() {
        ObjectMapper mapper = new ObjectMapper();

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        DCDModel dcd = new DCDModel("dcd diagram", umlProject);
        DCDModel dcd2 = new DCDModel("dcd2 diagram", umlProject);

        DesignClass dc = new DesignClass("A");
        ClassGR cgr = new ClassGR(dc, new Point());
        Interface in = new Interface(("In"));
        InterfaceGR ingr = new InterfaceGR(in, new Point());
        Realization r = new Realization(dc, in);
        RealizationGR rgr = new RealizationGR(cgr, ingr, r);

        dcd.addGraphicalElement(cgr);
        dcd.addGraphicalElement(ingr);
        dcd.addGraphicalElement(rgr);

        ClassGR cgr2 = new ClassGR(dc, new Point());
        InterfaceGR ingr2 = new InterfaceGR(in, new Point());
        Realization r2 = new Realization(dc, in);
        // although we add a realizationGR with a new realization r2
        // we expect that both realizationGRs reference the same realization (r)
        RealizationGR rgr2 = new RealizationGR(cgr2, ingr2, r2);

        dcd2.addGraphicalElement(cgr2);
        dcd2.addGraphicalElement(ingr2);
        dcd2.addGraphicalElement(rgr2);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);

        int count = 0, index = 0;

        while ((index = jsonString.indexOf("\"internalid\" : \"realization0\"", index)) != -1) {
            count++;
            index++;
        }

        assertEquals(2, count);
    }

    @Test
    public void realization() {
        ObjectMapper mapper = new ObjectMapper();

        DesignClass dc = new DesignClass("A");
        Interface in = new Interface(("In"));
        Realization r = new Realization(dc, in);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(r);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("theClass"));
        assertTrue(jsonString.contains("theInterface"));
    }

    @Test
    public void realizationGR() {
        ObjectMapper mapper = new ObjectMapper();

        DesignClass dc = new DesignClass("A");
        ClassGR cgr = new ClassGR(dc, new Point(23, 2));
        Interface in = new Interface(("In"));
        InterfaceGR ingr = new InterfaceGR(in, new Point(23, 2));
        Realization r = new Realization(dc, in);
        RealizationGR rgr = new RealizationGR(cgr, ingr, r);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rgr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("RealizationGR"));
    }

    @Test
    public void dependencyGR() {
        ObjectMapper mapper = new ObjectMapper();

        DesignClass dc1 = new DesignClass("A");
        ClassGR cgr1 = new ClassGR(dc1, new Point(23, 2));
        DesignClass dc2 = new DesignClass("B");
        ClassGR cgr2 = new ClassGR(dc2, new Point(23, 2));
        Dependency d = new Dependency(dc1, dc2);
        DependencyGR dgr = new DependencyGR(cgr1, cgr2, d);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dgr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("DependencyGR"));
        assertTrue(jsonString.contains("dependency"));
    }

    @Test
    public void generalizationGR() {
        ObjectMapper mapper = new ObjectMapper();

        DesignClass dc1 = new DesignClass("Super");
        ClassGR cgr1 = new ClassGR(dc1, new Point(23, 2));
        DesignClass dc2 = new DesignClass("Sub");
        ClassGR cgr2 = new ClassGR(dc2, new Point(23, 2));
        Generalization g = new Generalization(dc1, dc2);
        GeneralizationGR ggr = new GeneralizationGR(cgr1, cgr2, g);

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ggr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // System.out.println(jsonString);
        assertTrue(jsonString.contains("GeneralizationGR"));
        assertTrue(jsonString.contains("generalization"));
    }

    @Test
    public void readXML() {
        ObjectMapper mapper = new ObjectMapper();
        String filename = "diagrams" + File.separator + "tests" + File.separator + "classes.xml";

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        try {
            umlProject.loadFromXML(filename);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String jsonString = "";
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonString);
        assertTrue(jsonString.contains("diagramModels"));
    }

    @Test
    public void readStudentXML() {
        ObjectMapper mapper = new ObjectMapper();
        // String filename = "diagrams" + File.separator + "studentuml.xml";
        String filename = "diagrams" + File.separator + "studentuml.xml";

        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().setRuleFileAndCreateConsistencyChecker(simpleRulesFile);
        UMLProject umlProject = UMLProject.getInstance();
        umlProject.clear();

        try {
            umlProject.loadFromXML(filename);
        } catch (IOException e1) {
            e1.printStackTrace();
            fail();
        }

        assertTrue(true);
        // String jsonString = "";
        // try {
        //     jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(umlProject);
        // } catch (JsonProcessingException e) {
        //     e.printStackTrace();
        // }
        // System.out.println(jsonString);
        // assertTrue(jsonString.contains("diagramModels"));
    }

}
