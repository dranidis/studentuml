package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * Editor for Object Nodes in Activity Diagrams. Extends TypedEntityEditor to
 * handle type selection and adds states management.
 *
 * @author Biser
 */
public class ObjectNodeEditor extends TypedEntityEditor<DesignClass, ObjectNode> {

    private ObjectNodeGR objectNodeGR;

    // States management components
    private JPanel statesPanel;
    private JList<State> statesList;
    private JButton addStateButton;
    private JButton editStateButton;
    private JButton deleteStateButton;
    private Vector<State> states;

    public ObjectNodeEditor(ObjectNodeGR objectNodeGR, CentralRepository cr) {
        super(cr);
        this.objectNodeGR = objectNodeGR;

        // Create states management panel
        createStatesPanel();

        // Re-arrange layout: move centerPanel (name/type) to NORTH, states to CENTER
        remove(centerPanel);
        add(centerPanel, BorderLayout.NORTH);
        add(statesPanel, BorderLayout.CENTER);

        // bottomPanel is already in SOUTH from base class

        initialize();
    }

    private void createStatesPanel() {
        TitledBorder title = BorderFactory.createTitledBorder("Object States");
        statesPanel = new JPanel(new BorderLayout());
        statesPanel.setBorder(title);

        statesList = new JList<>();
        statesList.setFixedCellWidth(400);
        statesList.setVisibleRowCount(5);

        addStateButton = new JButton("Add...");
        addStateButton.addActionListener(this);
        editStateButton = new JButton("Edit...");
        editStateButton.addActionListener(this);
        deleteStateButton = new JButton("Delete");
        deleteStateButton.addActionListener(this);

        JPanel statesButtonsPanel = new JPanel();
        statesButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        statesButtonsPanel.add(addStateButton);
        statesButtonsPanel.add(editStateButton);
        statesButtonsPanel.add(deleteStateButton);

        statesPanel.add(new JScrollPane(statesList), BorderLayout.CENTER);
        statesPanel.add(statesButtonsPanel, BorderLayout.SOUTH);
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

        // Initialize states - make a copy for editing
        states = new Vector<>();
        for (State originalState : objectNode.getStates()) {
            states.add(new State(originalState.getName()));
        }
        updateStatesList();
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
        return states;
    }

    private void updateStatesList() {
        statesList.setListData(states);
    }

    private void addState() {
        StringEditorDialog sed = new StringEditorDialog(
                this, "State Editor", "State Name:", "");

        if (!sed.showDialog()) {
            return;
        }

        String stateName = sed.getText();
        if (stateName == null || stateName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "State name cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if state with same name already exists
        for (State s : states) {
            if (s.getName().equals(stateName)) {
                JOptionPane.showMessageDialog(this,
                        "A state with this name already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        states.add(new State(stateName));
        updateStatesList();
    }

    private void editState() {
        State selectedState = statesList.getSelectedValue();
        if (selectedState == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a state to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringEditorDialog sed = new StringEditorDialog(
                this, "State Editor", "State Name:", selectedState.getName());

        if (!sed.showDialog()) {
            return;
        }

        String newStateName = sed.getText();
        if (newStateName == null || newStateName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "State name cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if another state with same name exists
        for (State s : states) {
            if (s != selectedState && s.getName().equals(newStateName)) {
                JOptionPane.showMessageDialog(this,
                        "A state with this name already exists!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        selectedState.setName(newStateName);
        updateStatesList();
    }

    private void deleteState() {
        State selectedState = statesList.getSelectedValue();
        if (selectedState == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a state to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        states.remove(selectedState);
        updateStatesList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addStateButton) {
            addState();
        } else if (source == editStateButton) {
            editState();
        } else if (source == deleteStateButton) {
            deleteState();
        } else {
            // Delegate to parent for type-related buttons
            super.actionPerformed(e);
        }
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
