package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ControlFlow;
import edu.city.studentuml.model.graphical.ControlFlowGR;
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
public class ControlFlowEditor extends JPanel implements ActionListener {

    private ControlFlowGR controlFlowGR;
    private JDialog controlFlowDialog;
    private JPanel centerPanel;
    private JLabel guardLabel;
    private JTextField guardField;
    private boolean ok;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;

    public ControlFlowEditor(ControlFlowGR controlFlowGR) {
        this.controlFlowGR = controlFlowGR;
        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        guardLabel = new JLabel("Control flow Guard: ");
        guardField = new JTextField(15);
        guardField.addActionListener(this);
        centerPanel.add(guardLabel);
        centerPanel.add(guardField);

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

        controlFlowDialog = new JDialog(owner, true);
        controlFlowDialog.getContentPane().add(this);
        controlFlowDialog.setTitle(title);
        controlFlowDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        controlFlowDialog.pack();
        controlFlowDialog.setResizable(false);
        controlFlowDialog.setLocationRelativeTo(owner);
        controlFlowDialog.show();

        return ok;
    }

    public void initialize() {
        ControlFlow flow = (ControlFlow) controlFlowGR.getEdge();

        guardField.setText(flow.getGuard());
    }

    public String getGuard() {
        return guardField.getText();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton || e.getSource() == guardField) {
            controlFlowDialog.setVisible(false);
            ok = true;
        } else if (e.getSource() == cancelButton) {
            controlFlowDialog.setVisible(false);
        }
    }
}
