package edu.city.studentuml.view.gui;

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

import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.graphical.DecisionNodeGR;

// @author Spyros Maniopoulos

public class DecisionNodeEditor extends JPanel implements ActionListener{
	
	private DecisionNodeGR decisionNodeGR;
    private JDialog decisionNodeDialog;
    private JPanel centerPanel;
    private JLabel decisionNameLabel;
    private JTextField decisionNameField;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;

    public DecisionNodeEditor(DecisionNodeGR decisionNodeGR) {
        this.decisionNodeGR = decisionNodeGR;
        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        decisionNameLabel = new JLabel("Decision name: ");
        decisionNameField = new JTextField(15);
        decisionNameField.addActionListener(this);
        centerPanel.add(decisionNameLabel);
        centerPanel.add(decisionNameField);

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

        decisionNodeDialog = new JDialog(owner, true);
        decisionNodeDialog.getContentPane().add(this);
        decisionNodeDialog.setTitle(title);
        decisionNodeDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        decisionNodeDialog.pack();
        decisionNodeDialog.setResizable(false);
        decisionNodeDialog.setLocationRelativeTo(owner);
        decisionNodeDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        DecisionNode node = (DecisionNode) decisionNodeGR.getNodeComponent();

        decisionNameField.setText(node.getName());
    }

    public String getActionName() {
        return decisionNameField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == decisionNameField) {
            decisionNodeDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            decisionNodeDialog.setVisible(false);
        }
    }

}
