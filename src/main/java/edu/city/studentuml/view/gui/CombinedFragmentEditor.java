package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.Operand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Editor dialog for Combined Fragments in Sequence Diagrams. Allows editing of:
 * - Operator type (OPT, ALT, LOOP) - Guard condition (for OPT/LOOP operators) -
 * Loop iterations (min/max) for LOOP operator - Multiple operands with guards
 * (for ALT operator)
 * 
 * @author dimitris
 */
public class CombinedFragmentEditor extends JDialog {

    private CombinedFragment fragment;
    private boolean okPressed = false;

    // UI Components
    private JComboBox<InteractionOperator> operatorCombo;
    private JLabel guardLabel;
    private JTextField guardField;
    private JTextField loopMinField;
    private JTextField loopMaxField;
    private JLabel loopMinLabel;
    private JLabel loopMaxLabel;
    private JPanel loopPanel;
    private JPanel altPanel;
    private JList<String> operandList;
    private DefaultListModel<String> operandListModel;
    private JButton addOperandButton;
    private JButton editOperandButton;
    private JButton removeOperandButton;

    // Temporary list of operands being edited
    private List<Operand> tempOperands;

    /**
     * Creates a new Combined Fragment Editor dialog.
     * 
     * @param parent   the parent component
     * @param fragment the fragment to edit
     */
    public CombinedFragmentEditor(Component parent, CombinedFragment fragment) {
        super(SwingUtilities.getWindowAncestor(parent), "Edit Combined Fragment", ModalityType.APPLICATION_MODAL);
        this.fragment = fragment;

        // Initialize temporary operands list with copies of current operands (preserving height ratios)
        this.tempOperands = new ArrayList<>();
        for (Operand operand : fragment.getOperands()) {
            tempOperands.add(Operand.copy(operand)); // Use static copy method
        }

        initializeUI();
        loadFragmentData();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialize the user interface.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Operator selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Operator:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        operatorCombo = new JComboBox<>(InteractionOperator.values());
        operatorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLoopFieldsVisibility();
            }
        });
        mainPanel.add(operatorCombo, gbc);

        row++;

        // Guard condition
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        guardLabel = new JLabel("Guard Condition:");
        mainPanel.add(guardLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        guardField = new JTextField(20);
        mainPanel.add(guardField, gbc);

        row++;

        // Loop-specific fields panel
        loopPanel = new JPanel(new GridBagLayout());
        loopPanel.setBorder(BorderFactory.createTitledBorder("Loop Iterations (optional)"));

        GridBagConstraints loopGbc = new GridBagConstraints();
        loopGbc.insets = new Insets(5, 5, 5, 5);
        loopGbc.fill = GridBagConstraints.HORIZONTAL;

        // Loop Min
        loopGbc.gridx = 0;
        loopGbc.gridy = 0;
        loopGbc.weightx = 0;
        loopMinLabel = new JLabel("Min:");
        loopPanel.add(loopMinLabel, loopGbc);

        loopGbc.gridx = 1;
        loopGbc.weightx = 1.0;
        loopMinField = new JTextField(10);
        loopPanel.add(loopMinField, loopGbc);

        // Loop Max
        loopGbc.gridx = 0;
        loopGbc.gridy = 1;
        loopGbc.weightx = 0;
        loopMaxLabel = new JLabel("Max (* for unlimited):");
        loopPanel.add(loopMaxLabel, loopGbc);

        loopGbc.gridx = 1;
        loopGbc.weightx = 1.0;
        loopMaxField = new JTextField(10);
        loopPanel.add(loopMaxField, loopGbc);

        // Help text
        loopGbc.gridx = 0;
        loopGbc.gridy = 2;
        loopGbc.gridwidth = 2;
        JLabel helpLabel = new JLabel("<html><i>Examples: loop, loop(3), loop(0,2), loop(3,*)</i></html>");
        helpLabel.setFont(helpLabel.getFont().deriveFont(Font.PLAIN, 10f));
        loopPanel.add(helpLabel, loopGbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(loopPanel, gbc);

        row++;

        // ALT operands panel
        altPanel = new JPanel(new BorderLayout(5, 5));
        altPanel.setBorder(BorderFactory.createTitledBorder("ALT Operands"));

        // Operand list
        operandListModel = new DefaultListModel<>();
        operandList = new JList<>(operandListModel);
        operandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        operandList.setVisibleRowCount(4);
        operandList.addListSelectionListener(e -> updateOperandButtonStates());
        JScrollPane listScroll = new JScrollPane(operandList);
        altPanel.add(listScroll, BorderLayout.CENTER);

        // Buttons panel - horizontal layout matching standard UI pattern
        JPanel operandButtonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        addOperandButton = new JButton("Add...");
        addOperandButton.addActionListener(e -> onAddOperand());
        operandButtonPanel.add(addOperandButton);

        editOperandButton = new JButton("Edit...");
        editOperandButton.addActionListener(e -> onEditOperand());
        operandButtonPanel.add(editOperandButton);

        removeOperandButton = new JButton("Delete");
        removeOperandButton.addActionListener(e -> onRemoveOperand());
        operandButtonPanel.add(removeOperandButton);

        altPanel.add(operandButtonPanel, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(altPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * Load fragment data into the UI fields.
     */
    private void loadFragmentData() {
        operatorCombo.setSelectedItem(fragment.getOperator());
        guardField.setText(fragment.getGuardCondition());

        // Load loop iterations if present
        if (fragment.getLoopMin() != null) {
            loopMinField.setText(String.valueOf(fragment.getLoopMin()));
        }
        if (fragment.getLoopMax() != null) {
            if (fragment.getLoopMax() == -1) {
                loopMaxField.setText("*");
            } else {
                loopMaxField.setText(String.valueOf(fragment.getLoopMax()));
            }
        }

        // Load operands
        refreshOperandList();

        updateLoopFieldsVisibility();
    }

    /**
     * Refresh the operand list from tempOperands.
     */
    private void refreshOperandList() {
        operandListModel.clear();
        for (int i = 0; i < tempOperands.size(); i++) {
            String guard = tempOperands.get(i).getGuardCondition();
            operandListModel.addElement((i + 1) + ": " + (guard.isEmpty() ? "[no guard]" : guard));
        }
        updateOperandButtonStates();
    }

    /**
     * Update operand button enabled states based on selection and list size.
     */
    private void updateOperandButtonStates() {
        boolean hasSelection = operandList.getSelectedIndex() >= 0;
        editOperandButton.setEnabled(hasSelection);
        removeOperandButton.setEnabled(hasSelection);
    }

    /**
     * Show/hide operator-specific fields based on selected operator.
     */
    private void updateLoopFieldsVisibility() {
        InteractionOperator operator = (InteractionOperator) operatorCombo.getSelectedItem();
        boolean isLoop = operator == InteractionOperator.LOOP;
        boolean isAlt = operator == InteractionOperator.ALT;

        loopPanel.setVisible(isLoop);
        altPanel.setVisible(isAlt);

        // Hide guard field for ALT (each operand has its own guard)
        guardLabel.setVisible(!isAlt);
        guardField.setVisible(!isAlt);

        pack(); // Resize dialog to fit content
    }

    /**
     * Handle Add Operand button press.
     */
    private void onAddOperand() {
        String guard = JOptionPane.showInputDialog(this,
                "Enter guard condition for new operand:",
                "Add Operand",
                JOptionPane.PLAIN_MESSAGE);

        if (guard != null) { // null if cancelled
            guard = guard.trim();
            // Auto-add brackets if needed
            if (!guard.isEmpty() && !guard.startsWith("[")) {
                guard = "[" + guard;
            }
            if (!guard.isEmpty() && !guard.endsWith("]")) {
                guard = guard + "]";
            }

            tempOperands.add(new Operand(guard));
            refreshOperandList();
            operandList.setSelectedIndex(tempOperands.size() - 1); // Select new operand
        }
    }

    /**
     * Handle Edit Operand button press.
     */
    private void onEditOperand() {
        int selectedIndex = operandList.getSelectedIndex();
        if (selectedIndex < 0)
            return;

        Operand operand = tempOperands.get(selectedIndex);
        String currentGuard = operand.getGuardCondition();

        String newGuard = (String) JOptionPane.showInputDialog(this,
                "Edit guard condition:",
                "Edit Operand",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                currentGuard);

        if (newGuard != null) { // null if cancelled
            newGuard = newGuard.trim();
            // Auto-add brackets if needed
            if (!newGuard.isEmpty() && !newGuard.startsWith("[")) {
                newGuard = "[" + newGuard;
            }
            if (!newGuard.isEmpty() && !newGuard.endsWith("]")) {
                newGuard = newGuard + "]";
            }

            operand.setGuardCondition(newGuard);
            refreshOperandList();
            operandList.setSelectedIndex(selectedIndex); // Restore selection
        }
    }

    /**
     * Handle Remove Operand button press.
     */
    private void onRemoveOperand() {
        int selectedIndex = operandList.getSelectedIndex();
        if (selectedIndex < 0)
            return;

        tempOperands.remove(selectedIndex);
        refreshOperandList();

        // Select next item or previous if was last
        if (selectedIndex < tempOperands.size()) {
            operandList.setSelectedIndex(selectedIndex);
        } else if (tempOperands.size() > 0) {
            operandList.setSelectedIndex(tempOperands.size() - 1);
        }
    }

    /**
     * Handle OK button press.
     */
    private void onOk() {
        // Validate and apply changes
        if (validateAndApply()) {
            okPressed = true;
            dispose();
        }
    }

    /**
     * Handle Cancel button press.
     */
    private void onCancel() {
        okPressed = false;
        dispose();
    }

    /**
     * Validate input and apply changes to fragment.
     * 
     * @return true if validation passed and changes applied
     */
    private boolean validateAndApply() {
        // Get operator
        InteractionOperator operator = (InteractionOperator) operatorCombo.getSelectedItem();

        // Get guard condition and auto-add brackets if needed
        String guard = guardField.getText().trim();
        if (!guard.isEmpty() && !guard.startsWith("[")) {
            guard = "[" + guard;
        }
        if (!guard.isEmpty() && !guard.endsWith("]")) {
            guard = guard + "]";
        }

        // Validate and parse loop iterations if LOOP operator
        Integer loopMin = null;
        Integer loopMax = null;

        if (operator == InteractionOperator.LOOP) {
            // Parse min
            String minText = loopMinField.getText().trim();
            if (!minText.isEmpty()) {
                try {
                    loopMin = Integer.parseInt(minText);
                    if (loopMin < 0) {
                        JOptionPane.showMessageDialog(this,
                                "Loop minimum must be non-negative!",
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Loop minimum must be a valid integer!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Parse max
            String maxText = loopMaxField.getText().trim();
            if (!maxText.isEmpty()) {
                if (maxText.equals("*")) {
                    loopMax = -1; // Unlimited
                } else {
                    try {
                        loopMax = Integer.parseInt(maxText);
                        if (loopMax < 0) {
                            JOptionPane.showMessageDialog(this,
                                    "Loop maximum must be non-negative or '*'!",
                                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                                "Loop maximum must be a valid integer or '*'!",
                                "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            // Validate min <= max (unless max is unlimited)
            if (loopMin != null && loopMax != null && loopMax != -1 && loopMin > loopMax) {
                JOptionPane.showMessageDialog(this,
                        "Loop minimum cannot be greater than maximum!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Default: if only max provided, min defaults to 0
            if (loopMin == null && loopMax != null) {
                loopMin = 0;
            }
        }

        // Validate ALT operands
        if (operator == InteractionOperator.ALT && tempOperands.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "ALT fragment has no operands. Continue anyway?",
                    "Validation Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        // Apply changes to fragment
        fragment.setOperator(operator);
        fragment.setGuardCondition(guard);
        fragment.setLoopMin(loopMin);
        fragment.setLoopMax(loopMax);

        // Apply operands for ALT (preserving height ratios)
        if (operator == InteractionOperator.ALT) {
            fragment.clearOperands();
            for (Operand operand : tempOperands) {
                fragment.addOperand(Operand.copy(operand)); // Use static copy method to preserve height ratio
            }
        } else {
            // Clear operands for non-ALT operators
            fragment.clearOperands();
        }

        return true;
    }

    /**
     * Show the dialog and return true if OK was pressed.
     * 
     * @return true if OK pressed, false if cancelled
     */
    public boolean showDialog() {
        setVisible(true);
        return okPressed;
    }
}
