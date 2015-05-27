package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.State;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.ObjectNodeGR;
import edu.city.studentuml.model.repository.CentralRepository;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Biser
 */
public class ObjectNodeEditor extends JPanel implements ActionListener, ItemListener {

    private ObjectNodeGR objectNodeGR;
    private JDialog objectNodeDialog;
    private JPanel centerPanel;
    private JPanel objectNamePanel;
    private JLabel objectNameLabel;
    private JTextField objectNameField;
    private JPanel objectTypePanel;
    private JPanel cardPanel;
    private JPanel emptyPanel;
    private JPanel nonemptyPanel;
    private JLabel addObjectTypeLabel;
    private JButton addObjectTypeButton;
    private JLabel objectTypeLabel;
    private JComboBox objectTypeComboBox;
    private JPanel statesPanel;
    private JList statesList;
    private JPanel statesButtonsPanel;
    private JButton addStateButton;
    private JButton editStateButton;
    private JButton deleteStateButton;
    private Vector states;
    private JPanel bottomPanel;
    private JButton cancelButton;
    private JButton okButton;
    private boolean ok;         // stores whether the user has pressed ok
    private CentralRepository repository;
    private Vector types;
    private Type type;

    public ObjectNodeEditor(ObjectNodeGR objectNodeGR, CentralRepository cr) {
        this.objectNodeGR = objectNodeGR;
        repository = cr;
        types = repository.getTypes();

        setLayout(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(3, 1));
        objectNameLabel = new JLabel("Object Name: ");
        objectNameField = new JTextField(15);
        objectNameField.addActionListener(this);
        objectNamePanel = new JPanel(new FlowLayout());
        objectNamePanel.add(objectNameLabel);
        objectNamePanel.add(objectNameField);

        objectTypePanel = new JPanel(new FlowLayout());
        objectTypeLabel = new JLabel("Type: ");
        objectTypeComboBox = new JComboBox();
        objectTypeComboBox.setMaximumRowCount(5);
        objectTypeComboBox.addItemListener(this);
        objectTypePanel.add(objectTypeLabel);
        objectTypePanel.add(objectTypeComboBox);

        cardPanel = new JPanel(new CardLayout());
        emptyPanel = new JPanel();
        nonemptyPanel = new JPanel(new FlowLayout());
        addObjectTypeLabel = new JLabel("Add new object type: ");
        addObjectTypeButton = new JButton("Add...");
        addObjectTypeButton.addActionListener(this);
        nonemptyPanel.add(addObjectTypeLabel);
        nonemptyPanel.add(addObjectTypeButton);
        cardPanel.add("empty", emptyPanel);
        cardPanel.add("nonempty", nonemptyPanel);

        centerPanel.add(objectNamePanel);
        centerPanel.add(objectTypePanel);
        centerPanel.add(cardPanel);

        TitledBorder title = BorderFactory.createTitledBorder("Object States");
        statesPanel = new JPanel(new BorderLayout());
        statesPanel.setBorder(title);
        statesList = new JList();
        statesList.setFixedCellWidth(400);
        statesList.setVisibleRowCount(5);
        addStateButton = new JButton("Add...");
        addStateButton.addActionListener(this);
        editStateButton = new JButton("Edit...");
        editStateButton.addActionListener(this);
        deleteStateButton = new JButton("Delete");
        deleteStateButton.addActionListener(this);
        statesButtonsPanel = new JPanel();
        statesButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        statesButtonsPanel.add(addStateButton);
        statesButtonsPanel.add(editStateButton);
        statesButtonsPanel.add(deleteStateButton);
        statesPanel.add(new JScrollPane(statesList), BorderLayout.CENTER);
        statesPanel.add(statesButtonsPanel, BorderLayout.SOUTH);

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

        add(centerPanel, BorderLayout.NORTH);
        add(statesPanel, BorderLayout.CENTER);
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

        objectNodeDialog = new JDialog(owner, true);
        objectNodeDialog.getContentPane().add(this);
        objectNodeDialog.setTitle(title);
        objectNodeDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        objectNodeDialog.pack();
        objectNodeDialog.setResizable(false);
        objectNodeDialog.setLocationRelativeTo(owner);
        objectNodeDialog.show();

        return ok;
    }

    public String getName() {
        return objectNameField.getText();
    }

    public Vector getStates() {
        return states;
    }

    public Type getType() {
        return (Type) types.elementAt(objectTypeComboBox.getSelectedIndex());
    }

    // initialize the text fields and other components with the
    // data of the object node to be edited
    public void initialize() {
        ObjectNode objectNode = (ObjectNode) objectNodeGR.getNodeComponent();

        // initialize the states to an empty list
        // in order to populate them with COPIES of the object states
        states = new Vector();

        if (objectNode != null) {
            objectNameField.setText(objectNode.getName());

            type = objectNode.getType();
            // initialize the types combo box
            if (!isInList(type, types)) {
                types.add(0, type);
            }
            Type t;
            Iterator iterator = types.iterator();
            while (iterator.hasNext()) {
                t = (Type) iterator.next();

                if ((t != null) && !t.getName().equals("")) {
                    objectTypeComboBox.addItem(t.getName());
                } else {
                    objectTypeComboBox.addItem("(unnamed)");
                }
            }

            objectTypeComboBox.setSelectedIndex(types.indexOf(type));
            updateAddTypePanel();

            // make a copy of the states for editing purposes
            // which may be discarded if the user presses <<Cancel>>
            states = cloneStates(objectNode.getStates());

            // show the states in the list
            updateStatesList();
        }
    }

    private boolean isInList(Type type, Vector list) {
        Iterator iterator = list.iterator();
        Type t;

        while (iterator.hasNext()) {
            t = (Type) iterator.next();

            if (t == type) {
                return true;
            }
        }

        return false;
    }

    public void addNewType() {
        ClassGR classGR = new ClassGR(new DesignClass(""), new Point(0, 0));
        ClassNameEditor classNameEditor = new ClassNameEditor(classGR, repository);

        // show the class editor dialog and check whether the user has pressed cancel
        if (!classNameEditor.showDialog(this, "Class Editor")) {
            return;
        }

        DesignClass newClass = new DesignClass(classNameEditor.getName());

        if ((repository.getDesignClass(newClass.getName()) != null)
                && !newClass.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing class with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
        } else {
            type = newClass;
            types.set(0, type);
            repository.addClass(newClass);
        }
    }

    // make an exact copy of the states
    public Vector cloneStates(Iterator iterator) {
        Vector copyOfStates = new Vector();
        State originalState;
        State copyOfState;

        while (iterator.hasNext()) {
            originalState = (State) iterator.next();
            copyOfState = new State(new String(originalState.getName()));
            copyOfStates.add(copyOfState);
        }

        return copyOfStates;
    }

    public void updateStatesList() {
        statesList.setListData(states);
    }

    public void addState() {
        StateEditor stateEditor = new StateEditor(new State(""));

        if (!stateEditor.showDialog(this, "State Editor")) {
            return;
        }

        State state = new State(stateEditor.getStateName());
        states.add(state);
        updateStatesList();
    }

    public void editState() {
        if ((states == null) || (states.size() == 0)
                || (statesList.getSelectedIndex() < 0)) {
            return;
        }

        State state = (State) states.elementAt(statesList.getSelectedIndex());
        StateEditor stateEditor = new StateEditor(state);

        if (!stateEditor.showDialog(this, "State Editor")) {
            return;
        }

        state.setName(stateEditor.getStateName());
        updateStatesList();
    }

    public void deleteState() {
        if ((states == null) || (states.size() == 0)
                || (statesList.getSelectedIndex() < 0)) {
            return;
        }

        states.remove(statesList.getSelectedIndex());
        updateStatesList();
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton)
                || (event.getSource() == objectNameField)) {
            objectNodeDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            objectNodeDialog.setVisible(false);
        } else if (event.getSource() == addStateButton) {
            addState();
        } else if (event.getSource() == editStateButton) {
            editState();
        } else if (event.getSource() == deleteStateButton) {
            deleteState();
        } else if (event.getSource() == addObjectTypeButton) {
            addNewType();
            updateComboBox();
            updateAddTypePanel();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        updateAddTypePanel();
    }

    // updates the combo box according to the list of classes
    private void updateComboBox() {
        int selectedIndex = objectTypeComboBox.getSelectedIndex();

        objectTypeComboBox.removeAllItems();

        Type t;
        Iterator iterator = types.iterator();
        while (iterator.hasNext()) {
            t = (Type) iterator.next();

            if ((t != null) && !t.getName().equals("")) {
                objectTypeComboBox.addItem(t.getName());
            } else {
                objectTypeComboBox.addItem("(unnamed)");
            }
        }

        objectTypeComboBox.setSelectedIndex(selectedIndex);
    }

    private void updateAddTypePanel() {
        Type t = ((ObjectNode) objectNodeGR.getNodeComponent()).getType();
        String s = getSelectedItem();

        if (t == null || t.getName().equals("")) {
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
        String s = (String) objectTypeComboBox.getSelectedItem();
        if (s == null) {
            return "";
        } else {
            return (String) objectTypeComboBox.getSelectedItem();
        }
    }
}
