package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//MultiObjectEditor.java
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.MultiObject;
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
    private JPanel emptyPanel;
    private JPanel nonemptyPanel;
    private JLabel addTypeLabel;
    private JButton addTypeButton;
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
        emptyPanel = new JPanel();
        nonemptyPanel = new JPanel(new FlowLayout());
        addTypeLabel = new JLabel("Add new object type: ");
        addTypeButton = new JButton("Add...");
        addTypeButton.addActionListener(this);
        nonemptyPanel.add(addTypeLabel);
        nonemptyPanel.add(addTypeButton);
        cardPanel.add("empty", emptyPanel);
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
        multiObjectDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        multiObjectDialog.pack();
        multiObjectDialog.setResizable(false);
        multiObjectDialog.setLocationRelativeTo(owner);
        multiObjectDialog.show();

        return ok;
    }

    public void initialize() {
        MultiObject multiObject = multiObjectGR.getMultiObject();
        objectType = multiObject.getDesignClass();

        nameField.setText(multiObject.getName());

        // initialize the class names combo box
        if (!isInList(objectType, types)) {
            types.add(0, objectType);
        }

        DesignClass dc;
        Iterator iterator = types.iterator();
        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();

            if ((dc != null) && !dc.getName().equals("")) {
                typeComboBox.addItem(dc.getName());
            } else {
                typeComboBox.addItem("(unnamed)");
            }
        }

        typeComboBox.setSelectedIndex(types.indexOf(objectType));
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

    public String getName() {
        return nameField.getText();
    }

    public DesignClass getDesignClass() {
        return (DesignClass) types.elementAt(typeComboBox.getSelectedIndex());
    }

    // edits the class of object o
    public void editTypeName(DesignClass dc) {
        ClassGR classGR = new ClassGR(dc, new Point(0, 0));
        ClassNameEditor classNameEditor = new ClassNameEditor(classGR, repository);

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classNameEditor.showDialog(this, "Class Editor")) {
            return;
        }

        repository.addClass(dc);

        DesignClass newClass = new DesignClass(classNameEditor.getName());

        // edit the class if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!dc.getName().equals(newClass.getName())
                && (repository.getDesignClass(newClass.getName()) != null)
                && !newClass.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing class with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
        } else {
            repository.editClass(dc, newClass);
        }
    }

    // updates the combo box according to the list of classes
    public void updateComboBox() {
        int selectedIndex = typeComboBox.getSelectedIndex();

        typeComboBox.removeAllItems();

        DesignClass dc;
        Iterator iterator = types.iterator();
        while (iterator.hasNext()) {
            dc = (DesignClass) iterator.next();

            if ((dc != null) && !dc.getName().equals("")) {
                typeComboBox.addItem(dc.getName());
            } else {
                typeComboBox.addItem("(unnamed)");
            }
        }

        typeComboBox.setSelectedIndex(selectedIndex);
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            multiObjectDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            multiObjectDialog.setVisible(false);
        } else if (event.getSource() == addTypeButton) {
            editTypeName((DesignClass) types.elementAt(typeComboBox.getSelectedIndex()));
            updateComboBox();
            updateAddTypePanel();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        updateAddTypePanel();
    }

    private void updateAddTypePanel() {
        DesignClass dc = multiObjectGR.getMultiObject().getDesignClass();
        String s = getSelectedItem();

        if (dc.getName().equals("")) {
            if (s.equals("(unnamed)")) {
                setPanel("nonempty");
            } else {
                setPanel("empty");
            }
        } else {
            setPanel("empty");
        }
    }

    private void setPanel(String s) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, s);
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
