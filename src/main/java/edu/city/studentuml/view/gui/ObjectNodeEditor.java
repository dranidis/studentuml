package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;
import edu.city.studentuml.view.gui.components.ListPanel;
import java.awt.BorderLayout;
import java.util.Vector;

/**
 * Editor for Object Nodes in Activity Diagrams. Extends TypedEntityEditor to
 * handle type selection and adds states management.
 *
 * @author Biser
 */
public class ObjectNodeEditor extends TypedEntityEditor<DesignClass, ObjectNode> {

    private ObjectNodeGR objectNodeGR;
    private ListPanel<State> statesPanel;

    public ObjectNodeEditor(ObjectNodeGR objectNodeGR, CentralRepository cr) {
        super(cr);
        this.objectNodeGR = objectNodeGR;

        // Create states management panel as anonymous inner class
        statesPanel = new ListPanel<State>("Object States", cr) {
            @Override
            protected ElementEditor<State> createElementEditor(State state, CentralRepository repository) {
                StateEditor editor = new StateEditor(state, repository);
                // Pass all states for duplicate checking
                editor.setAllStates(getElements());
                return editor;
            }
        };

        // Re-arrange layout: move centerPanel (name/type) to NORTH, states to CENTER
        remove(centerPanel);
        add(centerPanel, BorderLayout.NORTH);
        add(statesPanel, BorderLayout.CENTER);

        // bottomPanel is already in SOUTH from base class

        initialize();
    }

    public void initialize() {
        ObjectNode objectNode = (ObjectNode) objectNodeGR.getComponent();

        // Initialize type information
        Type type = objectNode.getType();
        if (type instanceof DesignClass) {
            setCurrentType((DesignClass) type);
        } else {
            setCurrentType(null);
        }

        nameField.setText(objectNode.getName());
        initializeTypeComboBox();

        // Initialize states using StatesPanel - convert List to Vector
        statesPanel.setElements(new Vector<>(objectNode.getStates()));
    }

    public String getObjectName() {
        return getEntityName();
    }

    public DesignClass getDesignClass() {
        return getCurrentType();
    }

    /**
     * Returns the type (could be null or a DesignClass). Used by
     * ADSelectionController for backward compatibility.
     */
    public Type getType() {
        return getCurrentType();
    }

    public Vector<State> getStates() {
        return statesPanel.getElements();
    }

    @Override
    protected String getNameLabel() {
        return "Object Name: ";
    }

    @Override
    protected String getTypeLabel() {
        return "Type: ";
    }

    @Override
    protected String getTypeOptionsLabel() {
        return "Object type: ";
    }

    @Override
    protected String getTypeEditorTitle() {
        return "Class Editor";
    }

    @Override
    protected String getTypeEditorLabel() {
        return "Class Name:";
    }

    @Override
    protected String getTypeExistsMessage() {
        return "There is an existing class with the given name already!\n";
    }

    @Override
    protected Vector<DesignClass> loadTypesFromRepository() {
        return repository.getClasses();
    }

    @Override
    protected DesignClass createEmptyType() {
        return new DesignClass("");
    }

    @Override
    protected DesignClass createTypeFromName(String name) {
        return new DesignClass(name);
    }

    @Override
    protected String getTypeName(DesignClass type) {
        return type.getName();
    }

    @Override
    protected DesignClass getTypeFromRepository(String name) {
        return repository.getDesignClass(name);
    }

    @Override
    protected void addTypeToRepository(DesignClass type) {
        repository.addClass(type);
    }

    @Override
    protected void editTypeInRepository(DesignClass oldType, DesignClass newType) {
        repository.editClass(oldType, newType);
    }

    @Override
    protected void removeTypeFromRepository(DesignClass type) {
        repository.removeClass(repository.getDesignClass(type.getName()));
    }
}
