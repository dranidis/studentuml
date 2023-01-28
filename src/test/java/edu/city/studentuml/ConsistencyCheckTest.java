package edu.city.studentuml;

import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.util.SystemWideObjectNamePool;

public class ConsistencyCheckTest {

    private UMLProject umlProject = UMLProject.getInstance();
    private SystemWideObjectNamePool sw = SystemWideObjectNamePool.getInstance();

    @Before
    public void setup() {
        umlProject.clear();
        String rulesFile = this.getClass().getResource(Constants.RULES_ADVANCED).toString();
        sw.setRuleFileAndCreateConsistencyChecker(rulesFile);
        sw.setRuntimeChecking(true);
        sw.createNewConsistencyCheckerAndReloadRules();

    }

    @Test
    public void thereIsAWarningWhenThereIsUnnamedClass() {
        givenClassWithName("");

        thenWarningsAre();
    }

    private void thenWarningsAre() {
        assertTrue(sw.getMessages().contains("Unnamed class in DCD"));
    }

    private void givenClassWithName(String string) {
        DCDModel currDiagram = new DCDModel("dcd1", umlProject);
        DesignClass dc1 = new DesignClass("");
        currDiagram.addGraphicalElement(new ClassGR(dc1, new Point()));
    }
    
}
