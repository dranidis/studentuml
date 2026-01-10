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

        ClassGR bookGR = new ClassGR(book, new Point(50, 50));
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

        ClassGR memberGR = new ClassGR(member, new Point(500, 50));
        model.addGraphicalElement(memberGR);

        DesignClass library = new DesignClass("Library");
        library.addAttribute(new Attribute("name", DataType.STRING));
        library.addAttribute(new Attribute("address", DataType.STRING));

        Method addMember = new Method("addMember");
        addMember.setReturnType(DataType.VOID);
        addMember.setVisibility(Method.PUBLIC);
        addMember.addParameter(new MethodParameter("member", member));
        library.addMethod(addMember);

        ClassGR libraryGR = new ClassGR(library, new Point(250, 400));
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

        InterfaceGR borrowableGR = new InterfaceGR(borrowable, new Point(50, 250));
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

        InterfaceGR searchableGR = new InterfaceGR(searchable, new Point(500, 400));
        model.addGraphicalElement(searchableGR);

        // Create third Interface that extends Searchable (interface generalization)
        Interface advancedSearchable = new Interface("AdvancedSearchable");
        Method advancedSearch = new Method("advancedSearch");
        advancedSearch.setReturnType(DataType.BOOLEAN);
        advancedSearch.setVisibility(Method.PUBLIC);
        advancedSearch.addParameter(new MethodParameter("criteria", DataType.STRING));
        advancedSearch.addParameter(new MethodParameter("filters", DataType.STRING));
        advancedSearchable.addMethod(advancedSearch);

        InterfaceGR advancedSearchableGR = new InterfaceGR(advancedSearchable, new Point(500, 600));
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

        // Create Association (Member <-> Book through "borrows") - bidirectional
        Association memberBookAssoc = new Association(member, book);
        memberBookAssoc.setDirection(Association.BIDIRECTIONAL);
        memberBookAssoc.setName("borrows");
        memberBookAssoc.setLabelDirection(Association.FROM_A_TO_B); // Label points A to B
        memberBookAssoc.setShowArrow(true); // Show label direction arrow
        memberBookAssoc.getRoleA().setMultiplicity("0..*");
        memberBookAssoc.getRoleA().setName("borrower");
        memberBookAssoc.getRoleB().setMultiplicity("0..*");
        memberBookAssoc.getRoleB().setName("borrowedItem");
        AssociationGR memberBookAssocGR = new AssociationGR(memberGR, bookGR, memberBookAssoc);
        model.addGraphicalElement(memberBookAssocGR);

        // Create unidirectional Association (Book -> Member) - one way from A to B
        Association bookToMemberAssoc = new Association(book, member);
        bookToMemberAssoc.setDirection(Association.AB); // Direction: A to B
        bookToMemberAssoc.setName("reservedBy");
        bookToMemberAssoc.setLabelDirection(Association.FROM_A_TO_B); // Label points A to B
        bookToMemberAssoc.setShowArrow(true); // Show label direction arrow
        bookToMemberAssoc.getRoleA().setMultiplicity("0..*");
        bookToMemberAssoc.getRoleA().setName("reserved");
        bookToMemberAssoc.getRoleB().setMultiplicity("0..1");
        bookToMemberAssoc.getRoleB().setName("reserver");
        AssociationGR bookToMemberAssocGR = new AssociationGR(bookGR, memberGR, bookToMemberAssoc);
        model.addGraphicalElement(bookToMemberAssocGR);

        // Create unidirectional Association (Library -> Library) - self-association
        Association librarySelfAssoc = new Association(library, library);
        librarySelfAssoc.setDirection(Association.BA); // Direction: B to A
        librarySelfAssoc.setName("parentOf");
        librarySelfAssoc.setLabelDirection(Association.FROM_B_TO_A); // Label points B to A
        librarySelfAssoc.setShowArrow(true); // Show label direction arrow
        librarySelfAssoc.getRoleA().setMultiplicity("0..1");
        librarySelfAssoc.getRoleA().setName("parent");
        librarySelfAssoc.getRoleB().setMultiplicity("0..*");
        librarySelfAssoc.getRoleB().setName("branches");
        AssociationGR librarySelfAssocGR = new AssociationGR(libraryGR, libraryGR, librarySelfAssoc);
        model.addGraphicalElement(librarySelfAssocGR);

        // Create Aggregation (Library <>- Book)
        Aggregation libraryBookAggr = new Aggregation(library, book, false);
        libraryBookAggr.setDirection(Association.BIDIRECTIONAL);
        libraryBookAggr.setName("contains");
        libraryBookAggr.setLabelDirection(Association.FROM_A_TO_B);
        libraryBookAggr.getRoleA().setMultiplicity("1");
        libraryBookAggr.getRoleA().setName("container");
        libraryBookAggr.getRoleB().setMultiplicity("0..*");
        libraryBookAggr.getRoleB().setName("items");
        AggregationGR libraryBookAggrGR = new AggregationGR(libraryGR, bookGR, libraryBookAggr);
        model.addGraphicalElement(libraryBookAggrGR);

        // Create Composition (Library <>- Member) - strong aggregation
        Aggregation libraryMemberComp = new Aggregation(library, member, true);
        libraryMemberComp.setDirection(Association.BIDIRECTIONAL);
        libraryMemberComp.setName("has");
        libraryMemberComp.setLabelDirection(Association.FROM_B_TO_A);
        libraryMemberComp.getRoleA().setMultiplicity("1");
        libraryMemberComp.getRoleA().setName("owner");
        libraryMemberComp.getRoleB().setMultiplicity("0..*");
        libraryMemberComp.getRoleB().setName("members");
        AggregationGR libraryMemberCompGR = new AggregationGR(libraryGR, memberGR, libraryMemberComp);
        model.addGraphicalElement(libraryMemberCompGR);

        // Create Dependency (Member depends on Library) with stereotype
        Dependency memberLibraryDep = new Dependency(member, library);
        memberLibraryDep.setStereotype("use");
        DependencyGR memberLibraryDepGR = new DependencyGR(memberGR, libraryGR, memberLibraryDep);
        model.addGraphicalElement(memberLibraryDepGR);

        // Create another class for generalization
        DesignClass premiumMember = new DesignClass("PremiumMember");
        premiumMember.addAttribute(new Attribute("discount", DataType.DOUBLE));

        Method getDiscount = new Method("getDiscount");
        getDiscount.setReturnType(DataType.DOUBLE);
        getDiscount.setVisibility(Method.PUBLIC);
        premiumMember.addMethod(getDiscount);

        ClassGR premiumMemberGR = new ClassGR(premiumMember, new Point(750, 50));
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
        loan.getRoleA().setName("borrower");
        loan.getRoleB().setMultiplicity("1..*");
        loan.getRoleB().setName("loanedBooks");
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
                new Point(50, 550));
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
        DCDModel loadedModel = (DCDModel) project.getDiagramModels().get(0);
        assertNotNull("Loaded model should not be null", loadedModel);
        assertEquals("Diagram name should match", "Library Management System", loadedModel.getName());

        List<GraphicalElement> elements = loadedModel.getGraphicalElements();
        assertNotNull("Elements list should not be null", elements);

        // Count elements by type
        // 4 classes + 3 interfaces + 3 associations (1 bidirectional + 1 unidirectional + 1 self) + 
        // 2 aggregations + 1 association class + 2 realizations + 2 generalizations + 
        // 1 dependency + 1 note = 19 elements total
        assertEquals("Should have 19 graphical elements", 19, elements.size());

        // Verify Classes
        long classCount = countElementsByType(elements, ClassGR.class);
        assertEquals("Should have 4 design classes", 4, classCount);

        ClassGR loadedBook = findClassByName(elements, "Book");
        assertNotNull("Book class should exist", loadedBook);
        assertEquals("Book x position", 50, loadedBook.getX());
        assertEquals("Book y position", 50, loadedBook.getY());
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
        // Links: 3 associations + 2 aggregations + 1 dependency + 2 generalizations + 
        // 2 realizations + 1 association class = 11 links total
        long linkCount = countElementsByType(elements, LinkGR.class);
        assertEquals("Should have 11 links total", 11, linkCount);

        // Verify specific link types
        // Note: AggregationGR extends AssociationGR, so we need to filter carefully
        long associationCount = elements.stream()
                .filter(e -> e.getClass() == AssociationGR.class) // Exact class match, not subclasses
                .count();
        assertEquals("Should have 3 associations (including self-association)", 3, associationCount);
        long dependencyCount = countElementsByType(elements, DependencyGR.class);
        assertEquals("Should have 1 dependency", 1, dependencyCount);

        // Verify Dependency with stereotype
        DependencyGR loadedDependency = findElementByType(elements, DependencyGR.class);
        assertNotNull("Dependency should exist", loadedDependency);
        assertEquals("Dependency stereotype should be 'use'", "use",
                loadedDependency.getDependency().getStereotype());

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
        assertEquals("Loan roleA should be 'borrower'", "borrower", loanClass.getRoleA().getName());
        assertEquals("Loan roleB should be 'loanedBooks'", "loanedBooks", loanClass.getRoleB().getName());

        // Verify specific associations with roles and directions
        // Note: AggregationGR extends AssociationGR, so filter for exact class
        List<AssociationGR> associations = elements.stream()
                .filter(e -> e.getClass() == AssociationGR.class) // Exact class match
                .map(e -> (AssociationGR) e)
                .collect(java.util.stream.Collectors.toList());
        assertEquals("Should have 3 associations", 3, associations.size());

        // Find bidirectional Member-Book association with role names
        AssociationGR borrowsAssoc = associations.stream()
                .filter(a -> "borrows".equals(a.getAssociation().getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Borrows association should exist", borrowsAssoc);
        assertEquals("Borrows should be bidirectional", Association.BIDIRECTIONAL,
                borrowsAssoc.getAssociation().getDirection());
        assertEquals("Borrows label direction should be FROM_A_TO_B", Association.FROM_A_TO_B,
                borrowsAssoc.getAssociation().getLabelDirection());
        assertTrue("Borrows should show label arrow", borrowsAssoc.getAssociation().getShowArrow());
        assertEquals("Borrows roleA should be 'borrower'", "borrower",
                borrowsAssoc.getAssociation().getRoleA().getName());
        assertEquals("Borrows roleB should be 'borrowedItem'", "borrowedItem",
                borrowsAssoc.getAssociation().getRoleB().getName());

        // Find unidirectional Book->Member association
        AssociationGR reservedByAssoc = associations.stream()
                .filter(a -> "reservedBy".equals(a.getAssociation().getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("ReservedBy association should exist", reservedByAssoc);
        assertEquals("ReservedBy should be unidirectional AB (A to B)", Association.AB,
                reservedByAssoc.getAssociation().getDirection());
        assertEquals("ReservedBy label direction should be FROM_A_TO_B", Association.FROM_A_TO_B,
                reservedByAssoc.getAssociation().getLabelDirection());
        assertTrue("ReservedBy should show label arrow", reservedByAssoc.getAssociation().getShowArrow());
        assertEquals("ReservedBy roleA should be 'reserved'", "reserved",
                reservedByAssoc.getAssociation().getRoleA().getName());
        assertEquals("ReservedBy roleB should be 'reserver'", "reserver",
                reservedByAssoc.getAssociation().getRoleB().getName());

        // Find self-association Library->Library
        AssociationGR parentOfAssoc = associations.stream()
                .filter(a -> "parentOf".equals(a.getAssociation().getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("ParentOf self-association should exist", parentOfAssoc);
        assertEquals("ParentOf should be unidirectional BA (B to A)", Association.BA,
                parentOfAssoc.getAssociation().getDirection());
        assertEquals("ParentOf label direction should be FROM_B_TO_A", Association.FROM_B_TO_A,
                parentOfAssoc.getAssociation().getLabelDirection());
        assertTrue("ParentOf should show label arrow", parentOfAssoc.getAssociation().getShowArrow());
        assertTrue("ParentOf should be a self-association (reflective)",
                parentOfAssoc.getAssociation().isReflective());
        assertEquals("ParentOf roleA should be 'parent'", "parent",
                parentOfAssoc.getAssociation().getRoleA().getName());
        assertEquals("ParentOf roleB should be 'branches'", "branches",
                parentOfAssoc.getAssociation().getRoleB().getName());

        // Verify aggregations also have role names
        List<AggregationGR> aggregations = elements.stream()
                .filter(e -> e instanceof AggregationGR)
                .map(e -> (AggregationGR) e)
                .collect(java.util.stream.Collectors.toList());
        assertEquals("Should have 2 aggregations", 2, aggregations.size());

        AggregationGR containsAggr = aggregations.stream()
                .filter(a -> "contains".equals(a.getAggregation().getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Contains aggregation should exist", containsAggr);
        assertEquals("Contains roleA should be 'container'", "container",
                containsAggr.getAggregation().getRoleA().getName());
        assertEquals("Contains roleB should be 'items'", "items",
                containsAggr.getAggregation().getRoleB().getName());

        AggregationGR hasComp = aggregations.stream()
                .filter(a -> "has".equals(a.getAggregation().getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Has composition should exist", hasComp);
        assertEquals("Has roleA should be 'owner'", "owner",
                hasComp.getAggregation().getRoleA().getName());
        assertEquals("Has roleB should be 'members'", "members",
                hasComp.getAggregation().getRoleB().getName());

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
