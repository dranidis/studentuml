package edu.city.studentuml.integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.city.studentuml.model.domain.Aggregation;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.Dependency;
import edu.city.studentuml.model.domain.DesignAssociationClass;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.graphical.AggregationGR;
import edu.city.studentuml.model.graphical.AssociationClassGR;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DependencyGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.util.NotStreamable;

/**
 * Integration test for saving and loading Design Class Diagrams (DCD). Tests
 * all DCD element types: design classes, interfaces, methods, attributes,
 * associations, dependencies, aggregations, compositions, and generalizations.
 */
public class DCDSaveLoadTest extends SaveLoadTestBase {

    @Test
    public void testDCDSaveLoadComplete() throws IOException, NotStreamable {
        // ============================================================
        // 1. CREATE - Build a comprehensive DCD model
        // ============================================================

        // Create DCD model
        // Note: DCDModel constructor automatically adds itself to the project
        DCDModel model = new DCDModel("Library Management System", project);

        // Create Design Classes with methods and attributes
        DesignClass book = new DesignClass("Book");
        book.addAttribute(new Attribute("title", DataType.STRING));
        book.addAttribute(new Attribute("isbn", DataType.STRING));
        book.addAttribute(new Attribute("pages", DataType.INTEGER));

        Method getTitle = new Method("getTitle");
        getTitle.setReturnType(DataType.STRING);
        getTitle.setVisibility(Method.PUBLIC);
        book.addMethod(getTitle);

        Method setTitle = new Method("setTitle");
        setTitle.setReturnType(DataType.VOID);
        setTitle.setVisibility(Method.PUBLIC);
        setTitle.addParameter(new MethodParameter("title", DataType.STRING));
        book.addMethod(setTitle);

        ClassGR bookGR = new ClassGR(book, new Point(100, 100));
        model.addGraphicalElement(bookGR);

        DesignClass member = new DesignClass("Member");
        member.addAttribute(new Attribute("memberId", DataType.STRING));
        member.addAttribute(new Attribute("name", DataType.STRING));
        member.addAttribute(new Attribute("email", DataType.STRING));

        Method borrowBook = new Method("borrowBook");
        borrowBook.setReturnType(DataType.BOOLEAN);
        borrowBook.setVisibility(Method.PUBLIC);
        borrowBook.addParameter(new MethodParameter("book", book));
        member.addMethod(borrowBook);

        Method returnBook = new Method("returnBook");
        returnBook.setReturnType(DataType.VOID);
        returnBook.setVisibility(Method.PUBLIC);
        returnBook.addParameter(new MethodParameter("book", book));
        member.addMethod(returnBook);

        ClassGR memberGR = new ClassGR(member, new Point(400, 100));
        model.addGraphicalElement(memberGR);

        DesignClass library = new DesignClass("Library");
        library.addAttribute(new Attribute("name", DataType.STRING));
        library.addAttribute(new Attribute("address", DataType.STRING));

        Method addMember = new Method("addMember");
        addMember.setReturnType(DataType.VOID);
        addMember.setVisibility(Method.PUBLIC);
        addMember.addParameter(new MethodParameter("member", member));
        library.addMethod(addMember);

        ClassGR libraryGR = new ClassGR(library, new Point(250, 300));
        model.addGraphicalElement(libraryGR);

        // Create Interface
        Interface borrowable = new Interface("Borrowable");
        Method canBorrow = new Method("canBorrow");
        canBorrow.setReturnType(DataType.BOOLEAN);
        canBorrow.setVisibility(Method.PUBLIC);
        borrowable.addMethod(canBorrow);

        Method getBorrowPeriod = new Method("getBorrowPeriod");
        getBorrowPeriod.setReturnType(DataType.INTEGER);
        getBorrowPeriod.setVisibility(Method.PUBLIC);
        borrowable.addMethod(getBorrowPeriod);

        InterfaceGR borrowableGR = new InterfaceGR(borrowable, new Point(100, 300));
        model.addGraphicalElement(borrowableGR);

        // Create Interface realization (Book implements Borrowable)
        Realization bookImplementsBorrowable = new Realization(book, borrowable);
        RealizationGR bookImplementsBorrowableGR = new RealizationGR(bookGR, borrowableGR,
                bookImplementsBorrowable);
        model.addGraphicalElement(bookImplementsBorrowableGR);

        // Create another Interface
        Interface searchable = new Interface("Searchable");
        Method search = new Method("search");
        search.setReturnType(DataType.BOOLEAN);
        search.setVisibility(Method.PUBLIC);
        search.addParameter(new MethodParameter("query", DataType.STRING));
        searchable.addMethod(search);

        InterfaceGR searchableGR = new InterfaceGR(searchable, new Point(400, 300));
        model.addGraphicalElement(searchableGR);

        // Create third Interface that extends Searchable (interface generalization)
        Interface advancedSearchable = new Interface("AdvancedSearchable");
        Method advancedSearch = new Method("advancedSearch");
        advancedSearch.setReturnType(DataType.BOOLEAN);
        advancedSearch.setVisibility(Method.PUBLIC);
        advancedSearch.addParameter(new MethodParameter("criteria", DataType.STRING));
        advancedSearch.addParameter(new MethodParameter("filters", DataType.STRING));
        advancedSearchable.addMethod(advancedSearch);

        InterfaceGR advancedSearchableGR = new InterfaceGR(advancedSearchable, new Point(400, 450));
        model.addGraphicalElement(advancedSearchableGR);

        // Create Interface generalization (AdvancedSearchable extends Searchable)
        Generalization interfaceGen = new Generalization(searchable, advancedSearchable);
        GeneralizationGR interfaceGenGR = new GeneralizationGR(searchableGR, advancedSearchableGR, interfaceGen);
        model.addGraphicalElement(interfaceGenGR);

        // Create Interface realization (Library implements Searchable)
        Realization libraryImplementsSearchable = new Realization(library, searchable);
        RealizationGR libraryImplementsSearchableGR = new RealizationGR(libraryGR, searchableGR,
                libraryImplementsSearchable);
        model.addGraphicalElement(libraryImplementsSearchableGR);

        // Create Association (Member <-> Book through "borrows")
        Association memberBookAssoc = new Association(member, book);
        memberBookAssoc.setDirection(Association.BIDIRECTIONAL);
        memberBookAssoc.setName("borrows");
        memberBookAssoc.getRoleA().setMultiplicity("0..*");
        memberBookAssoc.getRoleB().setMultiplicity("0..*");
        AssociationGR memberBookAssocGR = new AssociationGR(memberGR, bookGR, memberBookAssoc);
        model.addGraphicalElement(memberBookAssocGR);

        // Create Aggregation (Library <>- Book)
        Aggregation libraryBookAggr = new Aggregation(library, book, false);
        libraryBookAggr.setDirection(Association.BIDIRECTIONAL);
        libraryBookAggr.setName("contains");
        libraryBookAggr.getRoleA().setMultiplicity("1");
        libraryBookAggr.getRoleB().setMultiplicity("0..*");
        AggregationGR libraryBookAggrGR = new AggregationGR(libraryGR, bookGR, libraryBookAggr);
        model.addGraphicalElement(libraryBookAggrGR);

        // Create Composition (Library <>- Member) - strong aggregation
        Aggregation libraryMemberComp = new Aggregation(library, member, true);
        libraryMemberComp.setDirection(Association.BIDIRECTIONAL);
        libraryMemberComp.setName("has");
        libraryMemberComp.getRoleA().setMultiplicity("1");
        libraryMemberComp.getRoleB().setMultiplicity("0..*");
        AggregationGR libraryMemberCompGR = new AggregationGR(libraryGR, memberGR, libraryMemberComp);
        model.addGraphicalElement(libraryMemberCompGR);

        // Create Dependency (Member depends on Library)
        Dependency memberLibraryDep = new Dependency(member, library);
        DependencyGR memberLibraryDepGR = new DependencyGR(memberGR, libraryGR, memberLibraryDep);
        model.addGraphicalElement(memberLibraryDepGR);

        // Create another class for generalization
        DesignClass premiumMember = new DesignClass("PremiumMember");
        premiumMember.addAttribute(new Attribute("discount", DataType.DOUBLE));

        Method getDiscount = new Method("getDiscount");
        getDiscount.setReturnType(DataType.DOUBLE);
        getDiscount.setVisibility(Method.PUBLIC);
        premiumMember.addMethod(getDiscount);

        ClassGR premiumMemberGR = new ClassGR(premiumMember, new Point(550, 100));
        model.addGraphicalElement(premiumMemberGR);

        // Create Generalization (PremiumMember extends Member)
        Generalization premiumMemberGen = new Generalization(premiumMember, member);
        GeneralizationGR premiumMemberGenGR = new GeneralizationGR(premiumMemberGR, memberGR, premiumMemberGen);
        model.addGraphicalElement(premiumMemberGenGR);

        // Create Association Class (Loan between Member and Book)
        DesignAssociationClass loan = new DesignAssociationClass(member, book);
        loan.setName("Loan");
        loan.setDirection(Association.BIDIRECTIONAL);
        loan.getRoleA().setMultiplicity("1");
        loan.getRoleB().setMultiplicity("1..*");
        loan.addAttribute(new Attribute("loanDate", DataType.STRING));
        loan.addAttribute(new Attribute("dueDate", DataType.STRING));
        loan.addAttribute(new Attribute("returned", DataType.BOOLEAN));

        Method isOverdue = new Method("isOverdue");
        isOverdue.setReturnType(DataType.BOOLEAN);
        isOverdue.setVisibility(Method.PUBLIC);
        loan.addMethod(isOverdue);

        AssociationClassGR loanGR = new AssociationClassGR(memberGR, bookGR, loan);
        model.addGraphicalElement(loanGR);

        // Create Note
        UMLNoteGR noteGR = new UMLNoteGR("Design follows Repository and Service patterns", libraryGR,
                new Point(50, 50));
        model.addGraphicalElement(noteGR);

        // ============================================================
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
        DCDModel loadedModel = (DCDModel) project.getDiagramModels().get(0);
        assertNotNull("Loaded model should not be null", loadedModel);
        assertEquals("Diagram name should match", "Library Management System", loadedModel.getName());

        List<GraphicalElement> elements = loadedModel.getGraphicalElements();
        assertNotNull("Elements list should not be null", elements);

        // Count elements by type
        // 4 classes + 3 interfaces + 4 associations/aggregations + 1 association class + 
        // 2 realizations (interface implementations) + 2 generalizations (1 class + 1 interface inheritance) +
        // 1 dependency + 1 note = 18 elements total
        // BUT: The association class links contain the association itself, so we don't count it separately
        // Final count: 4 + 3 + 3 + 1 + 2 + 2 + 1 + 1 = 17 elements
        assertEquals("Should have 17 graphical elements", 17, elements.size());

        // Verify Classes
        long classCount = countElementsByType(elements, ClassGR.class);
        assertEquals("Should have 4 design classes", 4, classCount);

        ClassGR loadedBook = findClassByName(elements, "Book");
        assertNotNull("Book class should exist", loadedBook);
        assertEquals("Book x position", 100, loadedBook.getX());
        assertEquals("Book y position", 100, loadedBook.getY());
        DesignClass loadedBookClass = loadedBook.getDesignClass();
        assertEquals("Book should have 3 attributes", 3, loadedBookClass.getAttributes().size());
        assertEquals("Book should have 2 methods", 2, loadedBookClass.getMethods().size());

        ClassGR loadedMember = findClassByName(elements, "Member");
        assertNotNull("Member class should exist", loadedMember);
        DesignClass loadedMemberClass = loadedMember.getDesignClass();
        assertEquals("Member should have 3 attributes", 3, loadedMemberClass.getAttributes().size());
        assertEquals("Member should have 2 methods", 2, loadedMemberClass.getMethods().size());

        ClassGR loadedLibrary = findClassByName(elements, "Library");
        assertNotNull("Library class should exist", loadedLibrary);

        ClassGR loadedPremiumMember = findClassByName(elements, "PremiumMember");
        assertNotNull("PremiumMember class should exist", loadedPremiumMember);

        // Verify Interfaces
        long interfaceCount = countElementsByType(elements, InterfaceGR.class);
        assertEquals("Should have 3 interfaces", 3, interfaceCount);

        InterfaceGR loadedBorrowable = findInterfaceByName(elements, "Borrowable");
        assertNotNull("Borrowable interface should exist", loadedBorrowable);
        assertEquals("Borrowable should have 2 methods", 2, loadedBorrowable.getInterface().getMethods().size());

        InterfaceGR loadedSearchable = findInterfaceByName(elements, "Searchable");
        assertNotNull("Searchable interface should exist", loadedSearchable);

        InterfaceGR loadedAdvancedSearchable = findInterfaceByName(elements, "AdvancedSearchable");
        assertNotNull("AdvancedSearchable interface should exist", loadedAdvancedSearchable);
        assertEquals("AdvancedSearchable should have 1 method", 1,
                loadedAdvancedSearchable.getInterface().getMethods().size());

        // Verify Links (associations, aggregations, dependencies, generalizations, realizations)
        // Links: 1 association + 2 aggregations + 1 dependency + 2 generalizations + 2 realizations + 1 association class = 9 links
        long linkCount = countElementsByType(elements, LinkGR.class);
        assertEquals("Should have 9 links total", 9, linkCount);

        // Verify specific link types
        long dependencyCount = countElementsByType(elements, DependencyGR.class);
        assertEquals("Should have 1 dependency", 1, dependencyCount);

        long generalizationCount = countElementsByType(elements, GeneralizationGR.class);
        assertEquals("Should have 2 generalizations (1 class inheritance + 1 interface inheritance)", 2,
                generalizationCount);

        long realizationCount = countElementsByType(elements, RealizationGR.class);
        assertEquals("Should have 2 realizations (interface implementations)", 2, realizationCount);

        // Verify Association Class
        long assocClassCount = countElementsByType(elements, AssociationClassGR.class);
        assertEquals("Should have 1 association class", 1, assocClassCount);

        AssociationClassGR loadedLoan = findElementByType(elements, AssociationClassGR.class);
        assertNotNull("Loan association class should exist", loadedLoan);
        DesignAssociationClass loanClass = (DesignAssociationClass) loadedLoan.getAssociationClass();
        assertEquals("Loan should have name", "Loan", loanClass.getName());
        assertEquals("Loan should have 3 attributes", 3, loanClass.getAttributes().size());
        assertEquals("Loan should have 1 method", 1, loanClass.getMethods().size());

        // Verify Note
        long noteCount = countElementsByType(elements, UMLNoteGR.class);
        assertEquals("Should have 1 note", 1, noteCount);

        UMLNoteGR loadedNote = findElementByType(elements, UMLNoteGR.class);
        assertNotNull("Note should exist", loadedNote);
        assertTrue("Note should contain text about patterns",
                loadedNote.getText().contains("Repository"));

        // ============================================================
        // 5. VERIFY REPOSITORY - Check domain objects in CentralRepository
        // ============================================================
        assertNotNull("Central repository should exist", project.getCentralRepository());

        // Verify classes are in repository
        DesignClass repoBook = project.getCentralRepository().getDesignClass("Book");
        assertNotNull("Book should be in repository", repoBook);
        assertEquals("Book should have 3 attributes in repository", 3, repoBook.getAttributes().size());
        assertEquals("Book should have 2 methods in repository", 2, repoBook.getMethods().size());

        DesignClass repoMember = project.getCentralRepository().getDesignClass("Member");
        assertNotNull("Member should be in repository", repoMember);

        Interface repoBorrowable = project.getCentralRepository().getInterface("Borrowable");
        assertNotNull("Borrowable interface should be in repository", repoBorrowable);
    }

    /**
     * Helper method to find a design class by name in the elements list.
     */
    private ClassGR findClassByName(List<GraphicalElement> elements, String name) {
        return elements.stream()
                .filter(e -> e instanceof ClassGR)
                .map(e -> (ClassGR) e)
                .filter(c -> c.getDesignClass().getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method to find an interface by name in the elements list.
     */
    private InterfaceGR findInterfaceByName(List<GraphicalElement> elements, String name) {
        return elements.stream()
                .filter(e -> e instanceof InterfaceGR)
                .map(e -> (InterfaceGR) e)
                .filter(i -> i.getInterface().getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
