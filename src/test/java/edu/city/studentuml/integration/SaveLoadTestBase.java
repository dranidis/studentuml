package edu.city.studentuml.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.util.NotStreamable;

/**
 * Base class for save/load integration tests. Provides common utilities for
 * creating, saving, and loading diagrams.
 */
public abstract class SaveLoadTestBase {

    protected UMLProject project;
    protected File tempFile;
    private static final String XML_TEST_FILES_DIR = "xml-test-files";

    @Before
    public void setUp() throws IOException {
        // Get the singleton and clear any existing state
        project = UMLProject.getInstance();
        project.clear();

        // Create xml-test-files directory if it doesn't exist
        Path xmlTestDir = Paths.get(XML_TEST_FILES_DIR);
        if (!Files.exists(xmlTestDir)) {
            Files.createDirectories(xmlTestDir);
        }

        // Create a temporary file for saving
        tempFile = File.createTempFile("studentuml_test_", ".xml");
        tempFile.deleteOnExit();
    }

    @After
    public void tearDown() {
        // Copy the XML file to xml-test-files directory before deleting
        if (tempFile != null && tempFile.exists()) {
            try {
                // Create a descriptive filename based on the test class name
                String testClassName = this.getClass().getSimpleName();
                String destinationFileName = testClassName + "_" + tempFile.getName();
                Path destination = Paths.get(XML_TEST_FILES_DIR, destinationFileName);

                // Copy the file
                Files.copy(tempFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Add frame properties to make the diagram viewable in the UI
                addFramePropertiesToXML(destination);

                System.out.println("Copied test XML to: " + destination.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to copy XML test file: " + e.getMessage());
            }

            tempFile.delete();
        }
        if (project != null) {
            project.clear();
        }
    }

    /**
     * Saves the current project to the temporary file.
     */
    protected void saveProject() {
        project.streamToXML(tempFile.getAbsolutePath());
    }

    /**
     * Loads a project from the temporary file.
     * 
     * @return The loaded project
     * @throws IOException
     * @throws NotStreamable
     */
    protected UMLProject loadProject() throws IOException, NotStreamable {
        // Clear the project before loading to simulate a fresh load
        project.clear();
        project.getCentralRepository().clear();

        assertEquals("Should have 0 diagrams after clear", 0, project.getDiagramModels().size());

        UMLProject loadedProject = UMLProject.getInstance();
        loadedProject.clear();

        List<String> errors = loadedProject.loadFromXML(tempFile.getAbsolutePath());

        // Assert no errors during loading
        if (!errors.isEmpty()) {
            fail("Errors occurred during loading: " + String.join(", ", errors));
        }

        return loadedProject;
    }

    /**
     * Verifies that a diagram was loaded correctly.
     * 
     * @param model                The loaded diagram model
     * @param expectedElementCount Expected number of graphical elements
     * @param diagramName          Expected diagram name
     */
    protected void verifyDiagramLoaded(DiagramModel model, int expectedElementCount, String diagramName) {
        assertNotNull("Diagram model should not be null", model);
        assertEquals("Diagram name mismatch", diagramName, model.getName());

        List<GraphicalElement> elements = model.getGraphicalElements();
        assertNotNull("Graphical elements list should not be null", elements);
        assertEquals("Wrong number of graphical elements", expectedElementCount, elements.size());
    }

    /**
     * Finds an element in a list by its class type.
     * 
     * @param elements     List of graphical elements
     * @param elementClass The class type to search for
     * @return The first matching element, or null if not found
     */
    @SuppressWarnings("unchecked")
    protected <T extends GraphicalElement> T findElementByType(List<GraphicalElement> elements, Class<T> elementClass) {
        return (T) elements.stream()
                .filter(e -> elementClass.isInstance(e))
                .findFirst()
                .orElse(null);
    }

    /**
     * Counts elements of a specific type in the list.
     * 
     * @param elements     List of graphical elements
     * @param elementClass The class type to count
     * @return The count of matching elements
     */
    protected <T extends GraphicalElement> long countElementsByType(List<GraphicalElement> elements,
            Class<T> elementClass) {
        return elements.stream()
                .filter(elementClass::isInstance)
                .count();
    }

    /**
     * Adds frame properties to the diagram XML to make it viewable in the UI. This
     * method modifies the XML file in-place to add attributes like framex,
     * maximized, selected, etc., which are needed for the diagram window to
     * automatically open when the file is loaded in StudentUML.
     * 
     * @param xmlFilePath Path to the XML file to modify
     * @throws IOException If file reading/writing fails
     */
    private void addFramePropertiesToXML(Path xmlFilePath) throws IOException {
        // Read the XML file
        String content = new String(Files.readAllBytes(xmlFilePath));

        // Add frame properties to diagram model elements
        // Pattern matches: <object class="UCDModel" (or other diagram types)
        // and adds frame attributes before the closing >
        String[] diagramTypes = { "UCDModel", "CCDModel", "DCDModel", "SDModel", "SSDModel", "ADModel" };

        for (String diagramType : diagramTypes) {
            String pattern = "(<object class=\"" + diagramType + "\"[^>]*?)>";
            String replacement = "$1 framex=\"0,0,650,550\" iconified=\"false\" maximized=\"true\" " +
                    "scale=\"1.0\" selected=\"true\" zorder=\"0\">";
            content = content.replaceAll(pattern, replacement);
        }

        // Write the modified content back
        Files.write(xmlFilePath, content.getBytes());
    }
}
