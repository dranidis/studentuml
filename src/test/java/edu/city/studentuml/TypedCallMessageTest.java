package edu.city.studentuml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;

/**
 *
 * @author dimitris
 */
public class TypedCallMessageTest {
    UMLProject umlProject;
    String filepath = "target" + File.separator + "codegeneration-test";
    String fullpath = filepath + File.separator + "test.xml";

    @Before
    public void setup() {
        String simpleRulesFile = this.getClass().getResource(Constants.RULES_SIMPLE).toString();
        SystemWideObjectNamePool.getInstance().init(simpleRulesFile);

        umlProject = UMLProject.getInstance();
        umlProject.clear();
        File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
        umlProject.setFilepath(fullpath);
        umlProject.setFilename("test.xml");
    }

    @Test
    public void addMessage() {
        // new ApplicationFrame(StudentUMLFrame.getInstance());

        SDModel currDiagram = new SDModel("sdmodel1", umlProject);
        DesignClass dc1 = new DesignClass("Class1");
        DesignClass dc2 = new DesignClass("Class2");
        SDObject sd1 = new SDObject("sd1", dc1);
        SDObject sd2 = new SDObject("sd2", dc2);

        CreateMessage cr = new CreateMessage(sd1, sd2);
        cr.addParameter(new MethodParameter("crpar", dc2));

        GenericOperation mtd1 = new GenericOperation("mtd1");
        CallMessage cm = new CallMessage(sd1, sd2, mtd1);
        cm.setReturnValue(new MessageReturnValue("c"));
        cm.setReturnType(dc1);
        cm.addParameter(new MethodParameter("mpar", dc2));

        SDObjectGR sd1GR = new SDObjectGR(sd1, 1);
        SDObjectGR sd2GR = new SDObjectGR(sd2, 2);
        currDiagram.addGraphicalElement(sd1GR);
        currDiagram.addGraphicalElement(sd2GR);

        currDiagram.addGraphicalElement(new CreateMessageGR(sd1GR, sd2GR, cr, 10));
        currDiagram.addGraphicalElement(new CallMessageGR(sd1GR, sd2GR, cm, 20));

        // Vector sds = umlProject.getCentralRepository().getSDMessages();
        // for(Object o: sds) {
        // CallMessage m = (CallMessage) o;
        // if (m.getName().equals("mtd1")) {
        // assertEquals(dc1.getName(), m.getReturnType().getName());
        // }
        // }

        umlProject.streamToXML(fullpath);
        umlProject.clear();
        umlProject.getCentralRepository().clear();

        umlProject.loadFromXML(fullpath);

        boolean found = false;
        Vector sds = umlProject.getCentralRepository().getSDMessages();
        for (Object o : sds) {
            if (o instanceof CallMessage) {
                CallMessage m = (CallMessage) o;
                if (m.getName().equals("mtd1")) {
                    found = true;
                    assertEquals(dc1.getName(), m.getReturnType().getName());
                    MethodParameter mp = m.getParameterByName("mpar");
                    assertNotNull(mp);
                    assertEquals("Parameter type is correct", "Class2", mp.getType().getName());
                }
            } else if (o instanceof CreateMessage) {
                CreateMessage m = (CreateMessage) o;
                MethodParameter mp = m.getParameterByName("crpar");
                assertNotNull(mp);
                assertEquals("Parameter type is correct", "Class2", mp.getType().getName());

            }
        }
        assertTrue("Message found", found);

    }
}
