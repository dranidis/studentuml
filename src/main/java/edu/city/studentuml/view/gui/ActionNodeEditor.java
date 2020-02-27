package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.graphical.ActionNodeGR;
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
public class ActionNodeEditor extends JPanel implements ActionListener {

    private ActionNodeGR actionNodeGR;
    private JDialog actionNodeDialog;
    private JPanel centerPanel;
    private JLabel actionNameLabel;
    private JTextField actionNameField;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;

    public ActionNodeEditor(ActionNodeGR actionNodeGR) {
        this.actionNodeGR = actionNodeGR;
        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        actionNameLabel = new JLabel("Action name: ");
        actionNameField = new JTextField(15);
        actionNameField.addActionListener(this);
        centerPanel.add(actionNameLabel);
        centerPanel.add(actionNameField);

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

        actionNodeDialog = new JDialog(owner, true);
        actionNodeDialog.getContentPane().add(this);
        actionNodeDialog.setTitle(title);
        actionNodeDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        actionNodeDialog.pack();
        actionNodeDialog.setResizable(false);
        actionNodeDialog.setLocationRelativeTo(owner);
        actionNodeDialog.show();

        return ok;
    }

    public void initialize() {
        ActionNode node = (ActionNode) actionNodeGR.getNodeComponent();

        actionNameField.setText(node.getName());
    }

    public String getActionName() {
        return actionNameField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == actionNameField) {
            actionNodeDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            actionNodeDialog.setVisible(false);
        }
    }
}
