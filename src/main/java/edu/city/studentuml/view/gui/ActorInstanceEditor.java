package edu.city.studentuml.view.gui;

//Author: Dragan Bisercic
//ActorInstanceEditor.java
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.model.graphical.ActorInstanceGR;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
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

public class ActorInstanceEditor extends JPanel implements ActionListener, ItemListener {

    private JPanel actorPanel;
    private JLabel actorLabel;
    private JComboBox actorComboBox;
    private JDialog actorInstanceDialog;

    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField nameField;

    private final JPanel cardPanel;
    private final JPanel emptyPanel;
    private final JPanel nonemptyPanel;
    private final JLabel editActorLabel;
    
    private JPanel centerPanel;
    private JButton editActorButton;
    
    private JPanel bottomPanel;
    private JButton okButton;
    private JButton cancelButton;    
    
    private boolean ok;

    private Actor actor;
    private Vector actors;
    private ActorInstanceGR actorInstance;
    private CentralRepository repository;

    public ActorInstanceEditor(ActorInstanceGR a, CentralRepository cr) {
        actorInstance = a;
        repository = cr;
        actors = (Vector) repository.getActors().clone();
        setLayout(new BorderLayout());

        centerPanel = new JPanel(new GridLayout(3, 1));

        namePanel = new JPanel(new FlowLayout());
        nameLabel = new JLabel("Actor Instance Name: ");
        nameField = new JTextField(15);
        nameField.addActionListener(this);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        actorPanel = new JPanel(new FlowLayout());
        actorLabel = new JLabel("Actor: ");
        actorComboBox = new JComboBox();
        actorComboBox.setMaximumRowCount(5);
        actorComboBox.addItemListener(this);
        actorPanel.add(actorLabel);
        actorPanel.add(actorComboBox);

        cardPanel = new JPanel(new CardLayout());
        emptyPanel = new JPanel();
        nonemptyPanel = new JPanel(new FlowLayout());
        editActorLabel = new JLabel("Add new actor: ");
        editActorButton = new JButton("Add...");
        editActorButton.addActionListener(this);
        nonemptyPanel.add(editActorLabel);
        nonemptyPanel.add(editActorButton);
        cardPanel.add("empty", emptyPanel);
        cardPanel.add("nonempty", nonemptyPanel);        
                
        centerPanel.add(namePanel);
        centerPanel.add(actorPanel);
        centerPanel.add(cardPanel);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel = new JPanel();
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(20);
        bottomPanel.setLayout(bottomLayout);
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

        actorInstanceDialog = new JDialog(owner, true);
        actorInstanceDialog.getContentPane().add(this);
        actorInstanceDialog.setTitle(title);
        actorInstanceDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        actorInstanceDialog.pack();
        actorInstanceDialog.setResizable(false);
        actorInstanceDialog.setLocationRelativeTo(owner);
        actorInstanceDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        ActorInstance instance = actorInstance.getActorInstance();
        actor = instance.getActor();
        
        nameField.setText(instance.getName());

        // initialize the actor names combo box
        if (!isInList(actor, actors)) {
            actors.add(0, actor);
        }

        Actor a;
        Iterator iterator = actors.iterator();
        while (iterator.hasNext()) {
            a = (Actor) iterator.next();

            if ((a != null) && !a.getName().equals("")) {
                actorComboBox.addItem(a.getName());
            } else {
                actorComboBox.addItem("(unnamed)");
            }
        }

        actorComboBox.setSelectedIndex(actors.indexOf(actor));
        updateEditActorPanel();
    }

    public boolean isInList(Actor actor, Vector list) {
        Iterator iterator = list.iterator();
        Actor a;

        while (iterator.hasNext()) {
            a = (Actor) iterator.next();

            if (a == actor) {
                return true;
            }
        }

        return false;
    }

    public String getActorInstanceName() {
        return nameField.getText();
    }

    public Actor getActor() {
        return (Actor) actors.elementAt(actorComboBox.getSelectedIndex());
    }

    // edits the given actor
    public void editActor(Actor a) {
        // show the actor editor dialog
        String actorName = JOptionPane.showInputDialog("Enter the Actor's Name");

        if (actorName == null) {    // user has pressed cancel
            return;
        }

        // ensure that the to-be-edited actor exists in the repository
        repository.addActor(a);

        Actor newActor = new Actor(actorName);

        // edit the actor if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any conflict
        // or if the new name is blank
        if (!a.getName().equals(newActor.getName())
                && (repository.getActor(newActor.getName()) != null)
                && !newActor.getName().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "There is an existing actor with the given name already!\n",
                    "Cannot Edit", JOptionPane.ERROR_MESSAGE);
        } else {
            repository.editActor(a, newActor);
        }
    }

    // updates the combo box according to the list of actors
    public void updateComboBox() {
        int selectedIndex = actorComboBox.getSelectedIndex();

        actorComboBox.removeAllItems();

        Actor a;
        Iterator iterator = actors.iterator();
        while (iterator.hasNext()) {
            a = (Actor) iterator.next();

            if ((a != null) && !a.getName().equals("")) {
                actorComboBox.addItem(a.getName());
            } else {
                actorComboBox.addItem("(unnamed)");
            }
        }

        actorComboBox.setSelectedIndex(selectedIndex);
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == okButton) || (event.getSource() == nameField)) {
            actorInstanceDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            actorInstanceDialog.setVisible(false);
        } else if (event.getSource() == editActorButton) {
            editActor((Actor) actors.elementAt(actorComboBox.getSelectedIndex()));
            updateComboBox();
            updateEditActorPanel();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        updateEditActorPanel();
    }

    private void updateEditActorPanel() {
        Actor a = actorInstance.getActorInstance().getActor();
        String s = getSelectedItem();

        if (a.getName().equals("")) {
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
        String s = (String) actorComboBox.getSelectedItem();
        if (s == null) {
            return "";
        } else {
            return (String) actorComboBox.getSelectedItem();
        }
    }
}
