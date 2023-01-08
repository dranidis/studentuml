package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.MethodsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Ervin Ramollari
 */
public class InterfaceEditor extends JPanel implements ActionListener {

    private JPanel bottomPanel;
    private JButton cancelButton;
    private JDialog interfaceDialog;
    private InterfaceGR interfaceGR; // the interface that the dialog edits
    private MethodsPanel methodsPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private boolean ok; // stores whether the user has pressed ok
    private JButton okButton;
    private JPanel topPanel;

    public InterfaceEditor(InterfaceGR interf, CentralRepository cr) {
        interfaceGR = interf;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Interface Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(nameLabel);
        topPanel.add(nameField);

        methodsPanel = new MethodsPanel("Interface Methods", cr);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();

        FlowLayout bottomLayout = new FlowLayout();

        bottomLayout.setHgap(30);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(topPanel, BorderLayout.NORTH);
        add(methodsPanel, BorderLayout.CENTER);
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

        interfaceDialog = new JDialog(owner, true);
        interfaceDialog.getContentPane().add(this);
        interfaceDialog.setTitle(title);
        interfaceDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        interfaceDialog.pack();
        interfaceDialog.setResizable(false);
        interfaceDialog.setLocationRelativeTo(owner);
        interfaceDialog.setVisible(true);

        return ok;
    }

    public String getInterfaceName() {
        return nameField.getText();
    }

    public Vector<Method> getMethods() {
        return methodsPanel.getElements();
    }

    // initialize the text fields and other components with the
    // data of the interface object to be edited
    public void initialize() {
        Interface coreInterface = interfaceGR.getInterface();

        if (coreInterface != null) {
            nameField.setText(coreInterface.getName());
            methodsPanel.setElements(coreInterface.getMethods());
        }
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide an interface name", "Warning",
                        JOptionPane.WARNING_MESSAGE);

                return;
            }

            interfaceDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            interfaceDialog.setVisible(false);
        }
    }
}
