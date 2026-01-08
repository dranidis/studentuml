package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.repository.CentralRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for RepositoryViewerDialog and CentralRepository change notifications.
 */
public class RepositoryViewerDialogTest {

    private CentralRepository repository;
    private TestChangeListener listener;

    @Before
    public void setUp() {
        repository = new CentralRepository();
        listener = new TestChangeListener();
        repository.addRepositoryChangeListener(listener);
    }

    @Test
    public void testEditObjectNotifiesListener() {
        // Create and add an object
        DesignClass dc = new DesignClass("TestClass");
        repository.addClass(dc);

        SDObject original = new SDObject("obj1", dc);
        repository.addObject(original);

        // Edit the object
        SDObject edited = new SDObject("obj2", dc);
        boolean success = repository.editObject(original, edited);

        // Verify edit was successful
        assertTrue("Edit should succeed", success);

        // Verify notification was sent
        assertEquals("Should have received one edit notification", 1, listener.editCount);
        assertEquals("Entity type should be SDObject", "SDObject", listener.lastEntityType);
        assertEquals("Old name should be obj1", "obj1", listener.lastOldName);
        assertEquals("New name should be obj2", "obj2", listener.lastNewName);
    }

    @Test
    public void testMultipleEditsTracked() {
        DesignClass dc = new DesignClass("TestClass");
        repository.addClass(dc);

        SDObject obj1 = new SDObject("original", dc);
        repository.addObject(obj1);

        // First edit
        SDObject edit1 = new SDObject("edited1", dc);
        repository.editObject(obj1, edit1);

        // Second edit
        SDObject edit2 = new SDObject("edited2", dc);
        repository.editObject(obj1, edit2);

        assertEquals("Should have 2 edit notifications", 2, listener.editCount);
    }

    @Test
    public void testListenerRemoval() {
        repository.removeRepositoryChangeListener(listener);

        DesignClass dc = new DesignClass("TestClass");
        repository.addClass(dc);

        SDObject obj = new SDObject("obj", dc);
        repository.addObject(obj);

        SDObject edited = new SDObject("edited", dc);
        repository.editObject(obj, edited);

        // Should not have received any notifications
        assertEquals("Should not receive notifications after removal", 0, listener.editCount);
    }

    /**
     * Simple test listener that tracks notifications.
     */
    private static class TestChangeListener implements edu.city.studentuml.model.repository.RepositoryChangeListener {
        int editCount = 0;

        String lastEntityType;
        String lastOldName;
        String lastNewName;

        @Override
        public void onAdd(String entityType, String entityName) {
        }

        @Override
        public void onEdit(String entityType, String oldName, String newName) {
            editCount++;
            lastEntityType = entityType;
            lastOldName = oldName;
            lastNewName = newName;
        }

        @Override
        public void onRemove(String entityType, String entityName) {
        }

        @Override
        public void onTypeOperation(String operation, String typeName) {
        }

    }
}
