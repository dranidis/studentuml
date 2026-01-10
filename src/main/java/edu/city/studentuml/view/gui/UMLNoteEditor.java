package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.graphical.UMLNoteGR;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * @author Ervin Ramollari
 */
public class UMLNoteEditor extends OkCancelDialog {

    private UMLNoteGR note;
    private JLabel textLabel;
    private JTextArea textArea;

    public UMLNoteEditor(Component parent, String title, UMLNoteGR note) {
        super(parent, title);
        this.note = note;

        // Ensure UI components are created
        initializeIfNeeded();

        // initialize with the note data to be edited
        initialize();
    }

    @Override
    protected JPanel makeCenterPanel() {
        textLabel = new JLabel("UML note text: ");
        textArea = new JTextArea(10, 30);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScroll = new JScrollPane(
                textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 15;
        c.anchor = GridBagConstraints.LINE_START;
        centerPanel.add(textLabel, c);
        c.ipady = 0;

        c.gridx = 0;
        c.gridy = 1;
        centerPanel.add(textScroll, c);

        return centerPanel;
    }

    public void initialize() {
        textArea.setText(note.getText());
    }

    public String getText() {
        return textArea.getText();
    }
}
