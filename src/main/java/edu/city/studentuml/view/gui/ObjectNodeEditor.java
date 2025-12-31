package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.ListPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * Editor for Object Nodes in Activity Diagrams. Extends TypedEntityEditor to
 * handle type selection and adds states management.
 *
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ObjectNodeEditor extends TypedEntityEditor<DesignClass, ObjectNode> {

    private ListPanel<State> statesPanel;

    /**
     * Constructor that accepts domain object.
     * 
     * @param cr The central repository
     */
    public ObjectNodeEditor(CentralRepository cr) {
        super(cr);
        initializeStatesPanel();
    }

    /**
     * Deprecated constructor for backward compatibility.
     * 
     * @param objectNodeGR The graphical object node wrapper
     * @param cr           The central repository
     * @deprecated Use {@link #ObjectNodeEditor(CentralRepository)} and call
     *             {@link #initialize(ObjectNode)} instead
     */
    @Deprecated
    public ObjectNodeEditor(ObjectNodeGR objectNodeGR, CentralRepository cr) {
        super(cr);
        initializeStatesPanel();
        initialize((ObjectNode) objectNodeGR.getComponent());
    }

    /**
     * Initialize the states panel. Called by both constructors.
     */
    private void initializeStatesPanel() {
        // Create states management panel as anonymous inner class
        statesPanel = new ListPanel<State>("Object States", repository) {
            @Override
            protected Editor<State> createElementEditor(CentralRepository repository) {
                // Return anonymous Editor implementation for State editing
                return new Editor<State>() {
                    @Override
                    public State editDialog(State state, Component parent) {
                        String initialValue = (state != null) ? state.getName() : "";
                        StringEditorDialog stringEditorDialog = new StringEditorDialog(parent,
                                "State Editor", "State Name:", initialValue);

                        if (!stringEditorDialog.showDialog()) {
                            return null; // Cancelled
                        }

                        String stateName = stringEditorDialog.getText();
                        if (stateName == null || stateName.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(parent,
                                    "State name cannot be empty!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }

                        // Check for duplicate names in the current list
                        Vector<State> allStates = getElements();
                        if (allStates != null) {
                            for (State s : allStates) {
                                if (s != state && s.getName().equals(stateName)) {
                                    JOptionPane.showMessageDialog(parent,
                                            "A state with this name already exists!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    return null;
                                }
                            }
                        }

                        if (state == null) {
                            // Create new state
                            return new State(stateName);
                        } else {
                            // Edit existing state
                            state.setName(stateName);
                            return state;
                        }
                    }
                };
            }
        };

        // Re-arrange layout: move centerPanel (name/type) to NORTH, states to CENTER
        remove(centerPanel);
        add(centerPanel, BorderLayout.NORTH);
        add(statesPanel, BorderLayout.CENTER);

        // bottomPanel is already in SOUTH from base class
    }

    /**
     * Initialize the editor with an ObjectNode domain object.
     * 
     * @param objectNode The domain object to edit
     */
    public void initialize(ObjectNode objectNode) {
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

    /**
     * @deprecated No-op method for backward compatibility. Use
     *             {@link #initialize(ObjectNode)} instead.
     */
    @Deprecated
    public void initialize() {
        // No-op for backward compatibility
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
    protected String getDialogTitle() {
        return "Object Node Editor";
    }

    @Override
    protected void initializeFromDomainObject(ObjectNode objectNode) {
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

    @Override
    protected ObjectNode buildDomainObject() {
        ObjectNode objectNode = new ObjectNode();
        objectNode.setName(getEntityName());
        objectNode.setType(getCurrentType());

        // Set states from the states panel
        objectNode.clearStates();
        Vector<State> states = statesPanel.getElements();
        if (states != null) {
            for (State state : states) {
                objectNode.addState(state);
            }
        }

        return objectNode;
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
