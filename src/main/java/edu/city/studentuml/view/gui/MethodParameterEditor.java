package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
public class MethodParameterEditor extends JPanel implements ActionListener, ElementEditor<MethodParameter> {

    private static final String TITLE = "Parameter Editor";
    private Vector<String> comboBoxStringList;
    private Vector<Type> comboBoxTypeList;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JPanel centerPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private JPanel namePanel;
    private boolean ok;
    private JButton okButton;
    private MethodParameter parameter;
    private JDialog parameterDialog;
    private JComboBox<String> typeComboBox;
    private JLabel typeLabel;
    private JPanel typePanel;
    CentralRepository repository;

    public MethodParameterEditor(MethodParameter param, CentralRepository cr) {
        parameter = param;
        repository = cr;

        setLayout(new BorderLayout());
        nameLabel = new JLabel("Parameter Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        typeLabel = new JLabel("Data Type: ");

        // AD-HOC replacement of the VOID data type with null
        comboBoxTypeList = new Vector<>(repository.getTypes());
        comboBoxTypeList.remove(DataType.VOID);
        comboBoxTypeList.add(0, null);

        // INITIALIZE THE STRING LIST ACCORDING TO THE ABOVE COMBOBOX TYPE LIST
        comboBoxStringList = new Vector<>();

        comboBoxTypeList.forEach(next -> {
            if (next == null) {
                comboBoxStringList.add("unspecified");
            } else {
                comboBoxStringList.add(next.getName());
            }
        });

        typeComboBox = new JComboBox<>(comboBoxStringList);

        namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout());
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        centerPanel.add(namePanel);
        centerPanel.add(typePanel);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        initialize();
    }

    public boolean showDialog(Component parent) {
        ok = false;

        // find the owner frame
        Frame owner = null;

        if (parent instanceof Frame) {
            owner = (Frame) parent;
        } else {
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        }

        parameterDialog = new JDialog(owner, true);
        parameterDialog.getContentPane().add(this);
        parameterDialog.setTitle(TITLE);
        parameterDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        parameterDialog.pack();
        parameterDialog.setResizable(false);
        parameterDialog.setLocationRelativeTo(owner);
        parameterDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        if (parameter == null) {
            nameField.setText("");
        } else {
            nameField.setText(parameter.getName());
        }

        // initialize the type combo box
        if ((parameter == null) || (parameter.getType() == null)) {
            // unspecified
            typeComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < comboBoxStringList.size(); i++) {
                if (comboBoxStringList.get(i).equals(parameter.getType().getName())) {
                    typeComboBox.setSelectedIndex(i);

                    break;
                }
            }
        }
    }

    public String getParameterName() {
        return nameField.getText();
    }

    public Type getType() {
        try {
            return comboBoxTypeList.get(typeComboBox.getSelectedIndex());
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            if ((nameField.getText() == null) || nameField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "You must provide a name", "Warning", JOptionPane.WARNING_MESSAGE);

                return;
            }

            parameterDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            parameterDialog.setVisible(false);
        }
    }

    @Override
    public MethodParameter createElement() {
        return new MethodParameter(getParameterName(), getType());
    }

    @Override
    public void editElement() {
        parameter.setName(getParameterName());
        parameter.setType(getType());
    }
}
