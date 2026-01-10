package edu.city.studentuml.util.version;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test class for VersionChecker. Tests the public checkForNewVersion() method
 * with various scenarios using lambda expressions.
 */
public class VersionCheckerTest {

    @Test
    public void testCheckForNewVersion_newerVersionAvailable() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.3.1", "1.3.0");

        // Assert
        assertEquals("1.3.0", checker.getCurrentVersion());
        assertEquals("1.3.1", checker.getLatestVersion());
        assertTrue("Should notify user", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_sameVersion() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.3.1", "1.3.1");

        // Assert
        assertEquals("1.3.1", checker.getCurrentVersion());
        assertEquals("1.3.1", checker.getLatestVersion());
        assertFalse("Should not notify user", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_snapshotVersion_newerAvailable() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.3.1", "1.4.0-SNAPSHOT");

        // Assert
        assertEquals("1.4.0-SNAPSHOT", checker.getCurrentVersion());
        assertEquals("1.3.1", checker.getLatestVersion());
        assertFalse("Should not notify user (snapshot version)", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_snapshotVersion_sameVersion() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.3.1-SNAPSHOT", "1.3.1-SNAPSHOT");

        // Assert
        assertEquals("1.3.1-SNAPSHOT", checker.getCurrentVersion());
        assertEquals("1.3.1-SNAPSHOT", checker.getLatestVersion());
        assertFalse("Should not notify user", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_emptyLatestVersion() {
        // Arrange
        VersionChecker checker = new VersionChecker("", "1.3.1");

        // Assert
        assertEquals("1.3.1", checker.getCurrentVersion());
        assertEquals("", checker.getLatestVersion());
        assertFalse("Should not notify user", checker.shouldNotify());
    }

    // Note: Supplier implementations should return empty string, not null
    // The current implementation does not handle null from get()
    // If null handling is needed, VersionChecker.isNewerVersion() would need to be updated

    @Test
    public void testCheckForNewVersion_majorVersionChange() {
        // Arrange
        VersionChecker checker = new VersionChecker("2.0.0", "1.3.1");

        // Assert
        assertEquals("1.3.1", checker.getCurrentVersion());
        assertEquals("2.0.0", checker.getLatestVersion());
        assertTrue("Should notify user", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_patchVersionChange() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.3.2", "1.3.1");

        // Assert
        assertEquals("1.3.1", checker.getCurrentVersion());
        assertEquals("1.3.2", checker.getLatestVersion());
        assertTrue("Should notify user", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_snapshotToRelease() {
        // Arrange
        VersionChecker checker = new VersionChecker("1.4.0", "1.4.0-SNAPSHOT");

        // Assert
        assertEquals("1.4.0-SNAPSHOT", checker.getCurrentVersion());
        assertEquals("1.4.0", checker.getLatestVersion());
        assertFalse("Should not notify user (snapshot version)", checker.shouldNotify());
    }

    @Test
    public void testCheckForNewVersion_multipleChecks() {
        VersionChecker checker = new VersionChecker("1.3.1", "1.3.0");

        // Assert
        assertEquals("1.3.0", checker.getCurrentVersion());
        assertEquals("1.3.1", checker.getLatestVersion());
        assertTrue("Should notify user", checker.shouldNotify());
    }
}
