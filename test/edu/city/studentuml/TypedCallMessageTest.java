/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.city.studentuml;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.TypedCallMessage;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.view.gui.ApplicationFrame;
import java.io.File;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dimitris
 */
public class TypedCallMessageTest {
    UMLProject umlProject;
    String filepath = "test";
    String fullpath = filepath + File.separator + "test.xml";            
    
    public TypedCallMessageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setup() {
        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);
        
        umlProject = UMLProject.getInstance();
        umlProject.clear();
        
        umlProject.setFilepath(".");
        umlProject.setFilename("test.xml");     
   }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void addMessage() {
        new ApplicationFrame(StudentUMLFrame.getInstance());
        
        SDModel currDiagram = new SDModel("sd1", umlProject);
        DesignClass dc1 = new DesignClass("Class1");
        DesignClass dc2 = new DesignClass("Class2");
        SDObject sd1 = new SDObject("sd1", dc1);
        SDObject sd2 = new SDObject("sd2", dc2);

        GenericOperation mtd1 = new GenericOperation("mtd1");
        TypedCallMessage cm = new TypedCallMessage(sd1, sd2, mtd1);
        cm.setReturnValue(new MessageReturnValue("c"));
        cm.setReturnType(dc1);
        cm.addParameter(new MethodParameter("par", dc2));
        
        SDObjectGR sd1GR = new SDObjectGR(sd1, 1);
        SDObjectGR sd2GR = new SDObjectGR(sd2, 2);
        currDiagram.addGraphicalElement(sd1GR);
        currDiagram.addGraphicalElement(sd2GR);
        currDiagram.addGraphicalElement(new CallMessageGR(sd1GR, sd2GR, cm, 1));

        Vector sds = umlProject.getCentralRepository().getSDMessages();
        for(Object o: sds) {
            TypedCallMessage m = (TypedCallMessage) o;
            if (m.getName().equals("mtd1")) {
                assertEquals(dc1.getName(), m.getReturnType().getName());
            }
        }


        umlProject.streamToXML(fullpath);
        umlProject.clear();
        umlProject.getCentralRepository().clear();

        sds = umlProject.getCentralRepository().getSDMessages();
        for(Object o: sds) {
            TypedCallMessage m = (TypedCallMessage) o;
            if (m.getName().equals("mtd1")) {
                assertEquals(dc1.getName(), m.getReturnType().getName());
            }
        }

        
        umlProject.loadFromXML(fullpath);
        
        boolean found = false;
        sds = umlProject.getCentralRepository().getSDMessages();
        for(Object o: sds) {
            TypedCallMessage m = (TypedCallMessage) o;
            if (m.getName().equals("mtd1")) {
                found = true;
                assertNotNull(m.getReturnType());
                assertEquals("Return type is correct", dc1.getName(), m.getReturnType().getName());
            }
        }
        assertTrue("Message found", found);

     }
}
