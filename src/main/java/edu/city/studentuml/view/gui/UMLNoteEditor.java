package edu.city.studentuml.view.gui;

//~--- JDK imports ------------------------------------------------------------
//Author: Ervin Ramollari
//CallMessageEditor.java
import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class UMLNoteEditor extends JPanel implements ActionListener {

    private UMLNoteGR note;
    private JLabel textLabel;
    private JTextArea textArea;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog noteDialog;
    private boolean ok;

    public UMLNoteEditor(UMLNoteGR note) {
        this.note = note;
        
        textLabel = new JLabel("UML note text: ");
        textArea = new JTextArea(5, 20);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScroll = new JScrollPane(
                textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 15;
        c.anchor = GridBagConstraints.LINE_START;
        add(textLabel, c);
        c.ipady = 0;

        c.gridx = 0;
        c.gridy = 1;
        add(textScroll, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(bottomPanel, c);

        // initialize with the method data to be edited, if any
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

        noteDialog = new JDialog(owner, true);
        noteDialog.getContentPane().add(this);
        noteDialog.setTitle(title);
        noteDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        noteDialog.pack();
        noteDialog.setResizable(false);
        noteDialog.setLocationRelativeTo(owner);
        noteDialog.setVisible(true);

        return ok;
    }

    public void initialize() {
        textArea.setText(note.getText());
    }

    public String getText() {
        return textArea.getText();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton) {
            noteDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            System.out.println(event.getSource());
            noteDialog.setVisible(false);
        }
    }
}
