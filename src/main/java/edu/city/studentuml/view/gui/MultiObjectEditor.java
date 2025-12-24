package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.MultiObjectGR;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
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
 * 
 * @author Ervin Ramollari
 */
public class MultiObjectEditor extends JPanel implements ActionListener, ItemListener {

    private JDialog multiObjectDialog;
    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JPanel centerPanel;
    private JPanel typePanel;
    private JComboBox typeComboBox;
    private JLabel typeLabel;
    private JPanel cardPanel;
    private JPanel nonemptyPanel;
    private JLabel addTypeLabel;
    private JButton addTypeButton;
    private JButton editTypeButton;
    private JButton deleteTypeButton;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;
    private boolean ok;
    private MultiObjectGR multiObjectGR;
    private DesignClass objectType;
    private Vector types;
    private CentralRepository repository;

    public MultiObjectEditor(MultiObjectGR obj, CentralRepository cr) {
        multiObjectGR = obj;
        repository = cr;
        types = (Vector) repository.getClasses().clone();

        setLayout(new BorderLayout());

        centerPanel = new JPanel(new GridLayout(3, 1));

        namePanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel("Collection Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        typePanel = new JPanel(new FlowLayout());
        typeLabel = new JLabel("Select objects' type: ");
        typeComboBox = new JComboBox();
        typeComboBox.setMaximumRowCount(5);
        typeComboBox.addItemListener(this);
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);

        cardPanel = new JPanel(new CardLayout());
        nonemptyPanel = new JPanel(new FlowLayout());
        addTypeLabel = new JLabel("Object type: ");
        addTypeButton = new JButton("Add...");
        addTypeButton.addActionListener(this);
        editTypeButton = new JButton("Edit...");
        editTypeButton.addActionListener(this);
        deleteTypeButton = new JButton("Delete");
        deleteTypeButton.addActionListener(this);

        nonemptyPanel.add(addTypeLabel);
        nonemptyPanel.add(addTypeButton);
        nonemptyPanel.add(editTypeButton);
        nonemptyPanel.add(deleteTypeButton);
        cardPanel.add("nonempty", nonemptyPanel);

        centerPanel.add(namePanel);
        centerPanel.add(typePanel);
        centerPanel.add(cardPanel);

        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(20);
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

        multiObjectDialog = new JDialog(owner, true);
        multiObjectDialog.getContentPane().add(this);
        multiObjectDialog.setTitle(title);
        multiObjectDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        multiObjectDialog.pack();
        multiObjectDialog.setResizable(false);
        multiObjectDialog.setLocationRelativeTo(owner);
        multiObjectDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        MultiObject multiObject = multiObjectGR.getMultiObject();
        objectType = multiObject.getDesignClass();

        nameField.setText(multiObject.getName());

        // initialize the class names combo box
        if (!isInList(objectType, types)) {
            types.add(objectType);
        }

        DesignClass dc;
        boolean hasEmpty = false;
        Iterator iterator = types.iterator();
        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();

            if (dc != null && !dc.getName().equals("")) {
                typeComboBox.addItem(dc.getName());
            } else if (dc != null && dc.getName().equals("")) {
                typeComboBox.addItem("(unnamed)");
                hasEmpty = true;
            }
        }
        if (!hasEmpty) {
            types.add(new DesignClass(""));
            typeComboBox.addItem("(unnamed)");
        }
        if (objectType.getName().equals("")) {
            typeComboBox.setSelectedItem("(unnamed)");
        } else {
            typeComboBox.setSelectedItem(objectType.getName());
        }
        updateAddTypePanel();
    }

    public boolean isInList(DesignClass designClass, Vector list) {
        Iterator iterator = list.iterator();
        DesignClass dc;

        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();

            if (dc == designClass) {
                return true;
            }
        }

        return false;
    }

    public String getMultiObjectName() {
        return nameField.getText();
    }

    public String addNewType() {
        ClassGR classGR = new ClassGR(new DesignClass(""), new Point(0, 0));
        ClassNameEditor classNameEditor = new ClassNameEditor(classGR);

        if (!classNameEditor.showDialog(this, "Class Editor")) {
            return "fail";
        }
        DesignClass newClass = new DesignClass(classNameEditor.getClassName());
        if (repository.getDesignClass(newClass.getName()) != null
                && !newClass.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing class with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
            return "fail";
        } else {
            types.add(newClass);
            repository.addClass(newClass);
            return newClass.getName();
        }
    }

    public DesignClass getDesignClass() {
        return objectType;
    }

    // edits the class of object o
    public String editTypeName(DesignClass dc) {
        ClassGR classGR = new ClassGR(dc, new Point(0, 0));
        ClassNameEditor classNameEditor = new ClassNameEditor(classGR);

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classNameEditor.showDialog(this, "Class Editor")) {
            return "fail";
        }

        repository.addClass(dc);

        DesignClass newClass = new DesignClass(classNameEditor.getClassName());

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!dc.getName().equals(newClass.getName())
                && repository.getDesignClass(newClass.getName()) != null
                && !newClass.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing class with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
            return "fail";
        } else {
            repository.editClass(dc, newClass);
            return newClass.getName();
        }
    }

    // updates the combo box according to the list of classes
    public void updateComboBox(String index) {
        typeComboBox.removeAllItems();
        DesignClass dc;
        Iterator iterator = types.iterator();
        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();
            if (dc != null && !dc.getName().equals("")) {
                typeComboBox.addItem(dc.getName());
            } else if (dc != null && dc.getName().equals("")) {
                typeComboBox.addItem("(unnamed)");
            }
        }
        typeComboBox.setSelectedItem(index);
    }

    private void setSelectedType() {
        objectType = (DesignClass) types.get(typeComboBox.getSelectedIndex());
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton || event.getSource() == nameField) {
            setSelectedType();
            multiObjectDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            multiObjectDialog.setVisible(false);
        } else if (event.getSource() == addTypeButton) {
            String index = addNewType();
            if (!index.equals("fail")) {
                updateComboBox(index);
                updateAddTypePanel();
            }
        } else if (event.getSource() == editTypeButton) {
            String index = editTypeName((DesignClass) types.elementAt(typeComboBox.getSelectedIndex()));
            if (!index.equals("fail")) {
                updateComboBox(index);
                updateAddTypePanel();
            }
        } else if (event.getSource() == deleteTypeButton) {
            deleteType();
            updateComboBox("(unnamed)");
        }
    }

    private void deleteType() {
        if (typeComboBox.getSelectedItem().equals("(unnamed)")) {
            return;
        }
        int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this type?",
                "Delete Type",
                JOptionPane.YES_NO_OPTION);
        if (n != JOptionPane.YES_OPTION) {
            return;
        }
        Type t = repository.getDesignClass(typeComboBox.getSelectedItem().toString());
        types.remove(t);
        repository.removeClass(repository.getDesignClass(t.getName()));
    }

    public void itemStateChanged(ItemEvent e) {
        updateAddTypePanel();
    }

    private void updateAddTypePanel() {
        String s = getSelectedItem();

        if (s.equals("(unnamed)")) {
            editTypeButton.setEnabled(false);
            deleteTypeButton.setEnabled(false);
        } else {
            editTypeButton.setEnabled(true);
            deleteTypeButton.setEnabled(true);
        }
    }

    private String getSelectedItem() {
        String s = (String) typeComboBox.getSelectedItem();
        if (s == null) {
            return "";
        } else {
            return (String) typeComboBox.getSelectedItem();
        }
    }
}
