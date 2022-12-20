package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.State;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author Biser
 */
public class StateEditor extends JPanel implements ActionListener {

    private State state;
    private JDialog stateDialog;
    private JPanel centerPanel;
    private JLabel stateNameLabel;
    private JTextField stateNameField;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;
    private boolean ok;

    public StateEditor(State state) {
        this.state = state;
        setLayout(new BorderLayout());

        centerPanel = new JPanel(new FlowLayout());
        stateNameLabel = new JLabel("State name: ");
        stateNameField = new JTextField(15);
        stateNameField.addActionListener(this);
        centerPanel.add(stateNameLabel);
        centerPanel.add(stateNameField);

        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        initialize();
    }

    public boolean showDialog(Component parent, String title) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        stateDialog = new JDialog(owner, true);
        stateDialog.getContentPane().add(this);
        stateDialog.setTitle(title);
        stateDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        stateDialog.pack();
        stateDialog.setResizable(false);
        stateDialog.setLocationRelativeTo(owner);
        stateDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        stateNameField.setText(state.getName());
    }

    public String getStateName() {
        return stateNameField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == stateNameField) {
            stateDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            stateDialog.setVisible(false);
        }
    }
}
