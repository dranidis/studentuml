package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * Editor for State domain objects.
 */
public class StateEditor implements ElementEditor<State> {

    private State state;
    private Vector<State> allStates; // For duplicate checking

    public StateEditor(State state, CentralRepository repository) {
        this.state = state;
    }

    /**
     * Sets the list of all states for duplicate name checking.
     */
    public void setAllStates(Vector<State> allStates) {
        this.allStates = allStates;
    }

    @Override
    public boolean showDialog(Component parent) {

        String initialValue = (state != null) ? state.getName() : "";
        StringEditorDialog stringEditorDialog = new StringEditorDialog(parent,
                "State Editor", "State Name:", initialValue);

        if (!stringEditorDialog.showDialog()) {
            return false;
        }

        String stateName = stringEditorDialog.getText();
        if (stateName == null || stateName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "State name cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check for duplicate names
        if (allStates != null) {
            for (State s : allStates) {
                if (s != state && s.getName().equals(stateName)) {
                    JOptionPane.showMessageDialog(parent,
                            "A state with this name already exists!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if (state == null) {
            state = new State(stateName);
        } else {
            state.setName(stateName);
        }

        return true;
    }

    @Override
    public State createElement() {
        return state;
    }

    @Override
    public void editElement() {
        // Element is edited in-place during showDialog
    }
}
