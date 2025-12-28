package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ObjectFlow;
import edu.city.studentuml.model.graphical.ObjectFlowGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
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
 * @author Biser
 */
public class ObjectFlowEditor extends JPanel implements ActionListener {

    private ObjectFlowGR objectFlowGR;
    private JDialog objectFlowDialog;
    private JPanel centerPanel;
    private JPanel guardPanel;
    private JPanel weightPanel;
    private JLabel guardLabel;
    private JTextField guardField;
    private JLabel weightLabel;
    private JTextField weightField;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;

    public ObjectFlowEditor(ObjectFlowGR objectFlowGR) {
        this.objectFlowGR = objectFlowGR;
        setLayout(new BorderLayout());

        centerPanel = new JPanel(new GridLayout(2, 0));
        weightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        guardPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        weightLabel = new JLabel("Weight");
        weightField = new JTextField(15);
        weightField.addActionListener(this);
        weightPanel.add(weightLabel);
        weightPanel.add(weightField);
        guardLabel = new JLabel("Object flow Guard");
        guardField = new JTextField(15);
        guardField.addActionListener(this);
        guardPanel.add(guardLabel);
        guardPanel.add(guardField);
        centerPanel.add(weightPanel);
        centerPanel.add(guardPanel);

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

        objectFlowDialog = new JDialog(owner, true);
        objectFlowDialog.getContentPane().add(this);
        objectFlowDialog.setTitle(title);
        objectFlowDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        objectFlowDialog.pack();
        objectFlowDialog.setResizable(false);
        objectFlowDialog.setLocationRelativeTo(owner);
        objectFlowDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        ObjectFlow flow = (ObjectFlow) objectFlowGR.getEdge();

        weightField.setText(flow.getWeight());
        guardField.setText(flow.getGuard());
    }

    public String getWeight() {
        return weightField.getText();
    }

    public String getGuard() {
        return guardField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == guardField
                || e.getSource() == weightField) {
            objectFlowDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            objectFlowDialog.setVisible(false);
        }
    }
}
