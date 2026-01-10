package edu.city.studentuml.frame;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for StudentUMLFrame command-line argument handling
 * 
 * @author AI Assistant
 */
public class StudentUMLFrameTest {

    private File tempDiagramFile;

    @Before
    public void setUp() throws IOException {
        // Create a minimal valid StudentUML XML file for testing
        tempDiagramFile = File.createTempFile("test-diagram-", ".xml");
        String minimalXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<uml>\n" +
                "  <object class=\"UMLProject\" id=\"project\">\n" +
                "    <object class=\"DCDModel\" framex=\"0,0,1200,800\" iconified=\"false\" " +
                "id=\"\" internalid=\"dcdmodel0\" maximized=\"false\" name=\"Test Diagram\" " +
                "scale=\"1.0\" selected=\"false\" zorder=\"0\">\n" +
                "    </object>\n" +
                "  </object>\n" +
                "</uml>";
        Files.write(tempDiagramFile.toPath(), minimalXML.getBytes());
    }

    @After
    public void tearDown() {
        if (tempDiagramFile != null && tempDiagramFile.exists()) {
            tempDiagramFile.delete();
        }
    }

    @Test
    public void testParseCommandLineArguments_withValidFile() {
        // Test that command-line arguments are correctly parsed
        String[] args = { tempDiagramFile.getAbsolutePath() };

        String fileName = StudentUMLFrame.parseCommandLineArguments(args);

        assertNotNull("Parsed file name should not be null", fileName);
        assertEquals("Parsed file name should match provided argument",
                tempDiagramFile.getAbsolutePath(), fileName);
    }

    @Test
    public void testParseCommandLineArguments_withNoArguments() {
        // Test normal startup with no arguments
        String[] args = {};

        String fileName = StudentUMLFrame.parseCommandLineArguments(args);

        assertNull("Parsed file name should be null when no arguments provided", fileName);
    }

    @Test
    public void testParseCommandLineArguments_withMultipleArguments() {
        // Test that only first argument is used
        String[] args = { tempDiagramFile.getAbsolutePath(), "extra-arg", "another-arg" };

        String fileName = StudentUMLFrame.parseCommandLineArguments(args);

        assertNotNull("Parsed file name should not be null", fileName);
        assertEquals("Should use only the first argument",
                tempDiagramFile.getAbsolutePath(), fileName);
    }

    @Test
    public void testValidateFile_withValidFile() {
        // Test file validation with existing file
        boolean isValid = StudentUMLFrame.validateFile(tempDiagramFile.getAbsolutePath());

        assertTrue("Valid existing file should return true", isValid);
    }

    @Test
    public void testValidateFile_withNonExistentFile() {
        // Test file validation with non-existent file
        String nonExistentPath = "/path/to/nonexistent/file.xml";

        boolean isValid = StudentUMLFrame.validateFile(nonExistentPath);

        assertFalse("Non-existent file should return false", isValid);
    }

    @Test
    public void testValidateFile_withNullPath() {
        // Test file validation with null path
        boolean isValid = StudentUMLFrame.validateFile(null);

        assertFalse("Null path should return false", isValid);
    }

    @Test
    public void testValidateFile_withRelativePath() throws IOException {
        // Test file validation with relative path
        // Create file in current directory
        File relativeFile = new File("test-relative.xml");
        try {
            Files.write(relativeFile.toPath(),
                    "<?xml version=\"1.0\"?><uml></uml>".getBytes());

            boolean isValid = StudentUMLFrame.validateFile(relativeFile.getName());

            assertTrue("Valid relative path file should return true", isValid);
        } finally {
            if (relativeFile.exists()) {
                relativeFile.delete();
            }
        }
    }
}
