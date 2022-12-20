package edu.city.studentuml.applet;

// Author: Ervin Ramollari
// SolutionInputPanel.java
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// JPanel with components for inputting drawing view size
public class SolutionInputPanel extends JPanel {

    private JLabel titleLabel;
    private JTextField titleField;
    private JPanel titlePanel;
    private JLabel commentLabel;
    private JTextArea commentArea;
    private JPanel commentPanel;
    private JLabel nodeTypeLabel;
    private JComboBox nodeTypeBox;
    private JPanel nodeTypePanel;
    private String nodeTypeDelimiter;
    // string lists needed for initializing the status and nodetype combo boxes
    private Vector statusStrings;
    private Vector nodeTypeStrings;
    /* private JLabel statusLabel;
    private JComboBox statusBox;
    private JPanel statusPanel;
    private String statusDelimiter; */
    private JCheckBox privateBox;

    public SolutionInputPanel(Vector statusStrings, Vector nodeTypeStrings) {
        this.statusStrings = statusStrings;
        this.nodeTypeStrings = nodeTypeStrings;

        titleLabel = new JLabel("Title: ");
        titleField = new JTextField(20);

        commentLabel = new JLabel("Comment: ");
        commentArea = new JTextArea(5, 20);
        commentArea.setEditable(true);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(
                commentArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        nodeTypeLabel = new JLabel("Node type: ");
        nodeTypeDelimiter = "-";

        nodeTypeStrings.addAll(statusStrings);

        nodeTypeBox = new JComboBox(nodeTypeStrings);
        // statusBox.setSelectedIndex(0);

        privateBox = new JCheckBox("Save as private");

        titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);

        commentPanel = new JPanel();
        commentPanel.setLayout(new FlowLayout());
        commentPanel.add(commentLabel);
        commentPanel.add(commentScroll);

        nodeTypePanel = new JPanel();
        nodeTypePanel.setLayout(new FlowLayout());
        nodeTypePanel.add(nodeTypeLabel);
        nodeTypePanel.add(nodeTypeBox);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        add(titlePanel, c);

        c.gridx = 0;
        c.gridy = 1;
        add(commentPanel, c);

        /* c.gridx = 0;
        c.gridx = 2;
        add(statusPanel, c); */

        c.gridx = 0;
        c.gridy = 2;
        add(nodeTypePanel, c);

        c.gridx = 0;
        c.gridy = 3;
        add(privateBox, c);
    }

    public void setTitle(String title) {
        titleField.setText(title);
    }

    public void setComment(String comment) {
        commentArea.setText(comment);
    }

    public void setNodeType(int i) {
        nodeTypeBox.setSelectedIndex(i);
    }

    public void setPrivate(boolean selected) {
        privateBox.setSelected(selected);
    }

    public String getTitle() {
        return titleField.getText();
    }

    public String getComment() {
        return commentArea.getText();
    }

    public String getNodeType() {
        /* StringTokenizer tokenizer =
        new StringTokenizer(statusBox.getSelectedItem().toString(),
        statusDelimiter);

        String idString = tokenizer.nextToken().trim();

        return Integer.valueOf(idString); */

        return nodeTypeBox.getSelectedItem().toString();
    }

    public boolean isPrivate() {
        return privateBox.isSelected();
    }
}
