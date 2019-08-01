package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.graphical.ActivityNodeGR;
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
public class ActivityNodeEditor extends JPanel implements ActionListener {

    private ActivityNodeGR activityNodeGR;
    private JDialog activityNodeDialog;
    private JPanel centerPanel;
    private JLabel activityNameLabel;
    private JTextField activityNameField;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;

    public ActivityNodeEditor(ActivityNodeGR activityNodeGR) {
        this.activityNodeGR = activityNodeGR;
        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        activityNameLabel = new JLabel("Activity name: ");
        activityNameField = new JTextField(15);
        activityNameField.addActionListener(this);
        centerPanel.add(activityNameLabel);
        centerPanel.add(activityNameField);

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

        activityNodeDialog = new JDialog(owner, true);
        activityNodeDialog.getContentPane().add(this);
        activityNodeDialog.setTitle(title);
        activityNodeDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        activityNodeDialog.pack();
        activityNodeDialog.setResizable(false);
        activityNodeDialog.setLocationRelativeTo(owner);
        activityNodeDialog.show();

        return ok;
    }

    public void initialize() {
        ActivityNode node = (ActivityNode) activityNodeGR.getNodeComponent();

        activityNameField.setText(node.getName());
    }

    public String getActivityName() {
        return activityNameField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == activityNameField) {
            activityNodeDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            activityNodeDialog.setVisible(false);
        }
    }
}
