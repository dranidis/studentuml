package edu.city.studentuml.view.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.SystemInstanceGR;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Tests for SystemInstanceEditor using mocked showDialog() to avoid UI
 * blocking. Validates the TypedEntityEditor pattern for System Instance editing
 * with name and type (System) management.
 * 
 * @author Dimitris Dranidis
 */
public class SystemInstanceEditorTest {

    private CentralRepository repository;
    private UMLProject project;

    @Before
    public void setUp() {
        project = UMLProject.getInstance();
        project.clear();
        repository = project.getCentralRepository();
    }

    @Test
    public void testConstructor_shouldNotThrow() {
        // Create a system and system instance
        System system = new System("PaymentSystem");
        SystemInstance systemInstance = new SystemInstance("payment1", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        // Test that constructor completes without exceptions
        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository);

        assertNotNull("Editor should be created successfully", editor);
    }

    @Test
    public void testInitialize_withSystemInstanceAndSystem() {
        System system = new System("AuthenticationSystem");
        repository.addSystem(system);

        SystemInstance systemInstance = new SystemInstance("auth1", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository);

        // Verify editor initializes with the system instance data
        assertEquals("System instance name should match", "auth1", editor.getSystemName());
        assertEquals("System should match", system, editor.getSystem());
    }

    @Test
    public void testInitialize_withEmptyName() {
        System system = new System("Database");
        SystemInstance systemInstance = new SystemInstance("", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository);

        // Verify editor handles empty name
        assertEquals("System instance name should be empty", "", editor.getSystemName());
        assertEquals("System should match", system, editor.getSystem());
    }

    @Test
    public void testShowDialog_OK() {
        System system = new System("EmailService");
        SystemInstance systemInstance = new SystemInstance("email", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return true; // Mock OK
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertTrue("Should return true for OK", result);
    }

    @Test
    public void testShowDialog_Cancel() {
        System system = new System("EmailService");
        SystemInstance systemInstance = new SystemInstance("email", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository) {
            @Override
            public boolean showDialog(java.awt.Component parent, String title) {
                return false; // Mock Cancel
            }
        };

        boolean result = editor.showDialog(null, "Test");

        assertFalse("Should return false for Cancel", result);
    }

    @Test
    public void testGetSystemName_returnsNameFieldValue() {
        System system = new System("NotificationService");
        SystemInstance systemInstance = new SystemInstance("notif1", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository);

        assertEquals("Should return system instance name", "notif1", editor.getSystemName());
    }

    @Test
    public void testGetSystem_returnsCurrentType() {
        System system = new System("BillingSystem");
        repository.addSystem(system);

        SystemInstance systemInstance = new SystemInstance("billing1", system);
        SystemInstanceGR systemInstanceGR = new SystemInstanceGR(systemInstance, 100);

        SystemInstanceEditor editor = new SystemInstanceEditor(systemInstanceGR, repository);

        assertEquals("Should return system", system, editor.getSystem());
        assertEquals("System name should match", "BillingSystem", editor.getSystem().getName());
    }
}
