package edu.city.studentuml.integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.ConceptualAssociationClass;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.util.NotStreamable;

/**
 * Integration test for saving and loading Conceptual Class Diagrams (CCD).
 * Tests all CCD element types: classes, attributes, associations, aggregations,
 * compositions, association classes, and generalizations.
 */
public class CCDSaveLoadTest extends SaveLoadTestBase {
    @Test
    public void testCCDSaveLoadComplete() throws IOException, NotStreamable {
        // ============================================================
        // 1. CREATE - Build a comprehensive CCD model
        // ============================================================

        // Create CCD model
        // Note: CCDModel constructor automatically adds itself to the project
        CCDModel model = new CCDModel("Library Domain Model", project);

        // Create Conceptual Classes with ConceptualClassGR
        ConceptualClass book = new ConceptualClass("Book");
        book.addAttribute(new Attribute("title"));
        book.addAttribute(new Attribute("isbn"));
        book.addAttribute(new Attribute("publicationYear"));
        ConceptualClassGR bookGR = new ConceptualClassGR(book, new Point(100, 100));
        model.addGraphicalElement(bookGR);

        ConceptualClass author = new ConceptualClass("Author");
        author.addAttribute(new Attribute("name"));
        author.addAttribute(new Attribute("birthYear"));
        ConceptualClassGR authorGR = new ConceptualClassGR(author, new Point(400, 100));
        model.addGraphicalElement(authorGR);

        ConceptualClass publisher = new ConceptualClass("Publisher");
        publisher.addAttribute(new Attribute("name"));
        publisher.addAttribute(new Attribute("address"));
        ConceptualClassGR publisherGR = new ConceptualClassGR(publisher, new Point(100, 300));
        model.addGraphicalElement(publisherGR);

        ConceptualClass library = new ConceptualClass("Library");
        library.addAttribute(new Attribute("name"));
        library.addAttribute(new Attribute("location"));
        ConceptualClassGR libraryGR = new ConceptualClassGR(library, new Point(400, 300));
        model.addGraphicalElement(libraryGR);

        ConceptualClass member = new ConceptualClass("Member");
        member.addAttribute(new Attribute("memberId"));
        member.addAttribute(new Attribute("name"));
        ConceptualClassGR memberGR = new ConceptualClassGR(member, new Point(250, 500));
        model.addGraphicalElement(memberGR);

        ConceptualClass loan = new ConceptualClass("Loan");
        loan.addAttribute(new Attribute("loanDate"));
        loan.addAttribute(new Attribute("dueDate"));
        ConceptualClassGR loanGR = new ConceptualClassGR(loan, new Point(550, 500));
        model.addGraphicalElement(loanGR);

        // Create Association (Book <-> Author: "written by")
        Association bookAuthorAssoc = new Association(book, author);
        bookAuthorAssoc.setDirection(Association.BIDIRECTIONAL);
        bookAuthorAssoc.setName("written by");
        bookAuthorAssoc.getRoleA().setMultiplicity("1..*");
        bookAuthorAssoc.getRoleB().setMultiplicity("1..*");
        AssociationGR bookAuthorAssocGR = new AssociationGR(bookGR, authorGR, bookAuthorAssoc);
        model.addGraphicalElement(bookAuthorAssocGR);

        // Create Aggregation (Library <>- Book)
        Aggregation libraryBookAggr = new Aggregation(library, book, false);
        libraryBookAggr.setDirection(Association.BIDIRECTIONAL);
        libraryBookAggr.setName("contains");
        libraryBookAggr.getRoleA().setMultiplicity("1");
        libraryBookAggr.getRoleB().setMultiplicity("0..*");
        AggregationGR libraryBookAggrGR = new AggregationGR(libraryGR, bookGR, libraryBookAggr);
        model.addGraphicalElement(libraryBookAggrGR);

        // Create Composition (Book <>- Publisher) - composition is Aggregation with strong flag
        Aggregation bookPublisherComp = new Aggregation(publisher, book, true);
        bookPublisherComp.setDirection(Association.BIDIRECTIONAL);
        bookPublisherComp.setName("publishes");
        bookPublisherComp.getRoleA().setMultiplicity("1");
        bookPublisherComp.getRoleB().setMultiplicity("1..*");
        AggregationGR bookPublisherCompGR = new AggregationGR(publisherGR, bookGR, bookPublisherComp);
        model.addGraphicalElement(bookPublisherCompGR);

        // Create Association (Library <-> Member)
        Association libraryMemberAssoc = new Association(library, member);
        libraryMemberAssoc.setDirection(Association.BIDIRECTIONAL);
        libraryMemberAssoc.setName("has members");
        libraryMemberAssoc.getRoleA().setMultiplicity("1");
        libraryMemberAssoc.getRoleB().setMultiplicity("0..*");
        AssociationGR libraryMemberAssocGR = new AssociationGR(libraryGR, memberGR, libraryMemberAssoc);
        model.addGraphicalElement(libraryMemberAssocGR);

        // Create Association Class (Member <-> Book through "Borrows")
        // This represents the borrowing relationship with additional attributes
        ConceptualAssociationClass borrowsAssocClass = new ConceptualAssociationClass(member, book);
        borrowsAssocClass.setName("Borrows");
        borrowsAssocClass.setDirection(Association.BIDIRECTIONAL);
        borrowsAssocClass.getRoleA().setMultiplicity("0..*");
        borrowsAssocClass.getRoleB().setMultiplicity("0..*");
        borrowsAssocClass.addAttribute(new Attribute("borrowDate"));
        borrowsAssocClass.addAttribute(new Attribute("dueDate"));
        borrowsAssocClass.addAttribute(new Attribute("returnDate"));
        AssociationClassGR borrowsAssocClassGR = new AssociationClassGR(memberGR, bookGR, borrowsAssocClass);
        model.addGraphicalElement(borrowsAssocClassGR);

        // Create associations for Loan (separate from the association class)
        Association memberLoanAssoc = new Association(member, loan);
        memberLoanAssoc.setDirection(Association.BIDIRECTIONAL);
        memberLoanAssoc.getRoleA().setMultiplicity("1");
        memberLoanAssoc.getRoleB().setMultiplicity("0..*");
        AssociationGR memberLoanAssocGR = new AssociationGR(memberGR, loanGR, memberLoanAssoc);
        model.addGraphicalElement(memberLoanAssocGR);

        Association loanBookAssoc = new Association(loan, book);
        loanBookAssoc.setDirection(Association.BIDIRECTIONAL);
        loanBookAssoc.getRoleA().setMultiplicity("1");
        loanBookAssoc.getRoleB().setMultiplicity("1");
        AssociationGR loanBookAssocGR = new AssociationGR(loanGR, bookGR, loanBookAssoc);
        model.addGraphicalElement(loanBookAssocGR);

        // Create Generalization (Publisher -> Organization)
        ConceptualClass organization = new ConceptualClass("Organization");
        organization.addAttribute(new Attribute("taxId"));
        ConceptualClassGR organizationGR = new ConceptualClassGR(organization, new Point(100, 450));
        model.addGraphicalElement(organizationGR);

        Generalization publisherOrgGen = new Generalization(publisher, organization);
        GeneralizationGR publisherOrgGenGR = new GeneralizationGR(publisherGR, organizationGR, publisherOrgGen);
        model.addGraphicalElement(publisherOrgGenGR);

        // Create Note
        UMLNoteGR noteGR = new UMLNoteGR("Books must have unique ISBN numbers", bookGR,
                new Point(50, 50));
        model.addGraphicalElement(noteGR); // ============================================================
        // 2. SAVE - Serialize to XML file
        // ============================================================
        assertEquals("Should have 1 diagram before save", 1, project.getDiagramModels().size());
        saveProject();

        // Clear the project before loading to simulate a fresh load
        project.clear();
        assertEquals("Should have 0 diagrams after clear", 0, project.getDiagramModels().size());

        // ============================================================
        // 3. LOAD - Deserialize from XML file
        // ============================================================
        project = loadProject();

        // ============================================================
        // 4. VERIFY - Check all elements are present
        // ============================================================
        assertNotNull("Loaded project should not be null", project);
        assertEquals("Should have 1 diagram", 1, project.getDiagramModels().size());
        CCDModel loadedModel = (CCDModel) project.getDiagramModels().get(0);
        assertNotNull("Loaded model should not be null", loadedModel);
        assertEquals("Diagram name should match", "Library Domain Model", loadedModel.getName());

        List<GraphicalElement> elements = loadedModel.getGraphicalElements();
        assertNotNull("Elements list should not be null", elements);

        // Count elements by type
        // 7 classes + 6 associations/aggregations/compositions + 1 association class + 1 generalization + 1 note = 16 elements
        assertEquals("Should have 16 graphical elements", 16, elements.size());

        // Verify Classes
        long classCount = countElementsByType(elements, ConceptualClassGR.class);
        assertEquals("Should have 7 conceptual classes", 7, classCount);

        ConceptualClassGR loadedBook = findClassByName(elements, "Book");
        assertNotNull("Book class should exist", loadedBook);
        assertEquals("Book x position", 100, loadedBook.getX());
        assertEquals("Book y position", 100, loadedBook.getY());
        ConceptualClass loadedBookClass = loadedBook.getConceptualClass();
        assertEquals("Book should have 3 attributes", 3, loadedBookClass.getAttributes().size());

        ConceptualClassGR loadedAuthor = findClassByName(elements, "Author");
        assertNotNull("Author class should exist", loadedAuthor);
        ConceptualClass loadedAuthorClass = loadedAuthor.getConceptualClass();
        assertEquals("Author should have 2 attributes", 2, loadedAuthorClass.getAttributes().size());

        ConceptualClassGR loadedPublisher = findClassByName(elements, "Publisher");
        assertNotNull("Publisher class should exist", loadedPublisher);

        ConceptualClassGR loadedLibrary = findClassByName(elements, "Library");
        assertNotNull("Library class should exist", loadedLibrary);

        ConceptualClassGR loadedMember = findClassByName(elements, "Member");
        assertNotNull("Member class should exist", loadedMember);

        ConceptualClassGR loadedLoan = findClassByName(elements, "Loan");
        assertNotNull("Loan class should exist", loadedLoan);

        ConceptualClassGR loadedOrganization = findClassByName(elements, "Organization");
        assertNotNull("Organization class should exist", loadedOrganization);

        // Verify Links (associations, aggregations, compositions, generalizations)
        long linkCount = countElementsByType(elements, LinkGR.class);
        assertEquals("Should have 8 links total (6 associations + 1 association class + 1 generalization)", 8,
                linkCount);

        // Verify Association Class
        long assocClassCount = countElementsByType(elements, AssociationClassGR.class);
        assertEquals("Should have 1 association class", 1, assocClassCount);

        AssociationClassGR loadedBorrows = findElementByType(elements, AssociationClassGR.class);
        assertNotNull("Borrows association class should exist", loadedBorrows);
        ConceptualAssociationClass borrowsClass = (ConceptualAssociationClass) loadedBorrows.getAssociationClass();
        assertEquals("Borrows should have name", "Borrows", borrowsClass.getName());
        assertEquals("Borrows should have 3 attributes", 3, borrowsClass.getAttributes().size());

        // Verify Note
        long noteCount = countElementsByType(elements, UMLNoteGR.class);
        assertEquals("Should have 1 note", 1, noteCount);
        UMLNoteGR loadedNote = findElementByType(elements, UMLNoteGR.class);
        assertNotNull("Note should exist", loadedNote);
        assertTrue("Note should contain text about ISBN",
                loadedNote.getText().contains("ISBN"));

        // ============================================================
        // 5. VERIFY REPOSITORY - Check domain objects in CentralRepository
        // ============================================================
        assertNotNull("Central repository should exist", project.getCentralRepository());

        // Verify classes are in repository
        ConceptualClass repoBook = project.getCentralRepository().getConceptualClass("Book");
        assertNotNull("Book should be in repository", repoBook);
        assertEquals("Book should have 3 attributes in repository", 3, repoBook.getAttributes().size());

        ConceptualClass repoAuthor = project.getCentralRepository().getConceptualClass("Author");
        assertNotNull("Author should be in repository", repoAuthor);
    }

    /**
     * Helper method to find a conceptual class by name in the elements list.
     */
    private ConceptualClassGR findClassByName(List<GraphicalElement> elements, String name) {
        return elements.stream()
                .filter(e -> e instanceof ConceptualClassGR)
                .map(e -> (ConceptualClassGR) e)
                .filter(c -> c.getConceptualClass().getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
