package edu.city.studentuml.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.UCAssociation;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.domain.UCGeneralization;
import edu.city.studentuml.model.domain.UCInclude;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.SystemGR;
import edu.city.studentuml.model.graphical.UCActorGR;
import edu.city.studentuml.model.graphical.UCAssociationGR;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.graphical.UCGeneralizationGR;
import edu.city.studentuml.model.graphical.UCIncludeGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.model.graphical.UseCaseGR;
import edu.city.studentuml.util.NotStreamable;

/**
 * Integration test for Use Case Diagram save/load functionality. Tests that all
 * UCD element types are correctly persisted and restored.
 */
public class UCDSaveLoadTest extends SaveLoadTestBase {

    @Test
    public void testUCDSaveLoadComplete() throws IOException, NotStreamable {
        // ============================================================
        // 1. CREATE - Build diagram with all UCD element types
        // ============================================================
        // Note: UCDModel constructor automatically adds itself to the project
        UCDModel model = new UCDModel("Library System UCD", project);

        // Create Actors
        Actor customer = new Actor("Customer");
        UCActorGR customerGR = new UCActorGR(customer, 50, 100);
        model.addGraphicalElement(customerGR);

        Actor librarian = new Actor("Librarian");
        UCActorGR librarianGR = new UCActorGR(librarian, 50, 300);
        model.addGraphicalElement(librarianGR);

        Actor manager = new Actor("Manager");
        UCActorGR managerGR = new UCActorGR(manager, 50, 500);
        model.addGraphicalElement(managerGR);

        // Create System boundary first (larger to contain all use cases)
        System librarySystem = new System("Library System");
        SystemGR systemGR = new SystemGR(librarySystem, 200, 50);
        // Set a larger size to contain all use cases
        systemGR.setWidth(400);
        systemGR.setHeight(450);
        model.addGraphicalElement(systemGR);

        // Create Use Cases and add them to model
        // Use setContext() to visually place them inside the system boundary
        // Do NOT use systemGR.add() as that would duplicate them in the XML
        UseCase borrowBook = new UseCase("Borrow Book");
        UseCaseGR borrowBookGR = new UseCaseGR(borrowBook, 250, 100);
        model.addGraphicalElement(borrowBookGR);
        borrowBookGR.setContext(systemGR);

        UseCase returnBook = new UseCase("Return Book");
        UseCaseGR returnBookGR = new UseCaseGR(returnBook, 450, 100);
        model.addGraphicalElement(returnBookGR);
        returnBookGR.setContext(systemGR);

        UseCase searchCatalog = new UseCase("Search Catalog");
        UseCaseGR searchCatalogGR = new UseCaseGR(searchCatalog, 250, 250);
        model.addGraphicalElement(searchCatalogGR);
        searchCatalogGR.setContext(systemGR);

        UseCase checkAvailability = new UseCase("Check Availability");
        UseCaseGR checkAvailabilityGR = new UseCaseGR(checkAvailability, 450, 250);
        model.addGraphicalElement(checkAvailabilityGR);
        checkAvailabilityGR.setContext(systemGR);

        UseCase generateReport = new UseCase("Generate Report");
        UseCaseGR generateReportGR = new UseCaseGR(generateReport, 350, 400);
        model.addGraphicalElement(generateReportGR);
        generateReportGR.setContext(systemGR);

        // Create Associations (Actor to Use Case)
        UCAssociation customerBorrows = new UCAssociation(customer, borrowBook);
        UCAssociationGR customerBorrowsGR = new UCAssociationGR(customerGR, borrowBookGR, customerBorrows);
        model.addGraphicalElement(customerBorrowsGR);

        UCAssociation customerReturns = new UCAssociation(customer, returnBook);
        UCAssociationGR customerReturnsGR = new UCAssociationGR(customerGR, returnBookGR, customerReturns);
        model.addGraphicalElement(customerReturnsGR);

        UCAssociation customerSearches = new UCAssociation(customer, searchCatalog);
        UCAssociationGR customerSearchesGR = new UCAssociationGR(customerGR, searchCatalogGR, customerSearches);
        model.addGraphicalElement(customerSearchesGR);

        UCAssociation librarianGenerates = new UCAssociation(librarian, generateReport);
        UCAssociationGR librarianGeneratesGR = new UCAssociationGR(librarianGR, generateReportGR, librarianGenerates);
        model.addGraphicalElement(librarianGeneratesGR);

        // Create Include relationship
        UCInclude borrowIncludesCheck = new UCInclude(borrowBook, checkAvailability);
        UCIncludeGR borrowIncludesCheckGR = new UCIncludeGR(borrowBookGR, checkAvailabilityGR, borrowIncludesCheck);
        model.addGraphicalElement(borrowIncludesCheckGR);

        // Create Extend relationship  
        UCExtend searchExtendsCheck = new UCExtend(checkAvailability, searchCatalog);
        UCExtendGR searchExtendsCheckGR = new UCExtendGR(checkAvailabilityGR, searchCatalogGR, searchExtendsCheck);
        model.addGraphicalElement(searchExtendsCheckGR);

        // Create Actor Generalization
        UCGeneralization managerIsLibrarian = new UCGeneralization(manager, librarian);
        UCGeneralizationGR managerIsLibrarianGR = new UCGeneralizationGR(managerGR, librarianGR, managerIsLibrarian);
        model.addGraphicalElement(managerIsLibrarianGR);

        // Create Note
        UMLNoteGR noteGR = new UMLNoteGR("Customer must have valid library card", borrowBookGR,
                new java.awt.Point(150, 50));
        model.addGraphicalElement(noteGR);

        // ============================================================
        // 2. SAVE - Serialize to XML file
        // ============================================================
        assertEquals("Should have 1 diagram before save", 1, project.getDiagramModels().size());
        saveProject();

        // ============================================================
        // 3. LOAD - Deserialize from XML file
        // ============================================================
        project = loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements are present
        // ============================================================
        assertNotNull("Loaded project should not be null", project);
        assertEquals("Should have 1 diagram", 1, project.getDiagramModels().size());
        UCDModel loadedModel = (UCDModel) project.getDiagramModels().get(0);
        assertNotNull("Loaded model should not be null", loadedModel);
        assertEquals("Diagram name should match", "Library System UCD", loadedModel.getName());

        List<GraphicalElement> elements = loadedModel.getGraphicalElements();
        assertNotNull("Elements list should not be null", elements);

        // Count elements by type
        // When SAVED: 17 elements (3 actors + 5 use cases + 1 system + relationships + note)
        // When LOADED: 12 elements because use cases with system context are grouped as system children
        // This is correct behavior - use cases render inside the system boundary
        assertEquals("Should have 12 top-level graphical elements after load", 12, elements.size());

        // Verify Actors
        assertEquals("Should have 3 actors", 3, countElementsByType(elements, UCActorGR.class));
        UCActorGR loadedCustomer = findActorByName(elements, "Customer");
        assertNotNull("Customer actor should exist", loadedCustomer);
        assertEquals("Customer x position", 50, loadedCustomer.getX());
        assertEquals("Customer y position", 100, loadedCustomer.getY());

        UCActorGR loadedLibrarian = findActorByName(elements, "Librarian");
        assertNotNull("Librarian actor should exist", loadedLibrarian);

        UCActorGR loadedManager = findActorByName(elements, "Manager");
        assertNotNull("Manager actor should exist", loadedManager);

        // Verify System boundary
        assertEquals("Should have 1 system", 1, countElementsByType(elements, SystemGR.class));
        SystemGR loadedSystem = findElementByType(elements, SystemGR.class);
        assertNotNull("System should exist", loadedSystem);
        assertEquals("System name", "Library System",
                ((edu.city.studentuml.model.domain.System) loadedSystem.getComponent()).getName());

        // Verify Use Cases (loaded as system children because they have system context)
        // The loader groups use cases with their containing system
        assertEquals("System should contain 5 use cases", 5, loadedSystem.getNumberOfElements());

        // Get use cases from the system
        UseCaseGR loadedBorrowBook = null;
        UseCaseGR loadedReturnBook = null;
        UseCaseGR loadedSearchCatalog = null;
        UseCaseGR loadedCheckAvailability = null;
        UseCaseGR loadedGenerateReport = null;

        for (int i = 0; i < loadedSystem.getNumberOfElements(); i++) {
            if (loadedSystem.getElement(i) instanceof UseCaseGR) {
                UseCaseGR uc = (UseCaseGR) loadedSystem.getElement(i);
                String name = uc.getComponent().getName();
                if ("Borrow Book".equals(name))
                    loadedBorrowBook = uc;
                else if ("Return Book".equals(name))
                    loadedReturnBook = uc;
                else if ("Search Catalog".equals(name))
                    loadedSearchCatalog = uc;
                else if ("Check Availability".equals(name))
                    loadedCheckAvailability = uc;
                else if ("Generate Report".equals(name))
                    loadedGenerateReport = uc;
            }
        }

        assertNotNull("Borrow Book use case should exist", loadedBorrowBook);
        assertEquals("Borrow Book x position", 250, loadedBorrowBook.getX());
        assertEquals("Borrow Book y position", 100, loadedBorrowBook.getY());
        assertNotNull("Return Book use case should exist", loadedReturnBook);
        assertNotNull("Search Catalog use case should exist", loadedSearchCatalog);
        assertNotNull("Check Availability use case should exist", loadedCheckAvailability);
        assertNotNull("Generate Report use case should exist", loadedGenerateReport);

        // Verify Associations
        assertEquals("Should have 4 associations", 4, countElementsByType(elements, UCAssociationGR.class));

        // Verify Include relationship
        assertEquals("Should have 1 include", 1, countElementsByType(elements, UCIncludeGR.class));
        UCIncludeGR loadedInclude = findElementByType(elements, UCIncludeGR.class);
        assertNotNull("Include relationship should exist", loadedInclude);

        // Verify Extend relationship
        assertEquals("Should have 1 extend", 1, countElementsByType(elements, UCExtendGR.class));
        UCExtendGR loadedExtend = findElementByType(elements, UCExtendGR.class);
        assertNotNull("Extend relationship should exist", loadedExtend);

        // Verify Generalization
        assertEquals("Should have 1 generalization", 1, countElementsByType(elements, UCGeneralizationGR.class));
        UCGeneralizationGR loadedGen = findElementByType(elements, UCGeneralizationGR.class);
        assertNotNull("Generalization should exist", loadedGen);

        // Verify Note
        assertEquals("Should have 1 note", 1, countElementsByType(elements, UMLNoteGR.class));
        UMLNoteGR loadedNote = findElementByType(elements, UMLNoteGR.class);
        assertNotNull("Note should exist", loadedNote);
        assertEquals("Note text", "Customer must have valid library card", loadedNote.getText());

        // ============================================================
        // 5. VERIFY CENTRAL REPOSITORY
        // ============================================================
        assertEquals("Repository should have 3 actors", 3,
                project.getCentralRepository().getActors().size());
        assertEquals("Repository should have 1 system", 1,
                project.getCentralRepository().getSystems().size());

        // TODO: Use cases loaded as system children may not be registered in repository
        // This is a known limitation of the current save/load implementation.
        // The use cases ARE present in the system and relationships work correctly.
    }

    // Helper methods for finding specific elements

    private UCActorGR findActorByName(List<GraphicalElement> elements, String name) {
        return (UCActorGR) elements.stream()
                .filter(e -> e instanceof UCActorGR)
                .filter(e -> ((Actor) ((UCActorGR) e).getComponent()).getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
