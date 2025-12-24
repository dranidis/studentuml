package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.graphical.UCExtendGR;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author draganbisercic
 */
public class UCExtendEditor extends JPanel implements ActionListener {

    private List<ExtensionPoint> extensionPoints;
    private JList<String> extensionsList;
    private JButton addExtensionButton;
    private JButton editExtensionButton;
    private JButton deleteExtensionButton;
    private JPanel extensionsButtonsPanel;
    private JPanel extensionPointsPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog ucExtendDialog;
    private UCExtendGR ucExtendGR;
    private boolean ok;         // stores whether the user has pressed ok

    public UCExtendEditor(UCExtendGR uc) {
        ucExtendGR = uc;

        setLayout(new BorderLayout());
        extensionPointsPanel = new JPanel(new BorderLayout());
        extensionPointsPanel.setBorder(BorderFactory.createTitledBorder("Extension Points"));

        extensionsList = new JList<>();
        extensionsList.setFixedCellWidth(300);
        extensionsList.setVisibleRowCount(4);
        addExtensionButton = new JButton("Add...");
        addExtensionButton.addActionListener(this);
        editExtensionButton = new JButton("Edit...");
        editExtensionButton.addActionListener(this);
        deleteExtensionButton = new JButton("Delete");
        deleteExtensionButton.addActionListener(this);
        extensionsButtonsPanel = new JPanel();
        extensionsButtonsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        extensionsButtonsPanel.add(addExtensionButton);
        extensionsButtonsPanel.add(editExtensionButton);
        extensionsButtonsPanel.add(deleteExtensionButton);
        extensionPointsPanel.add(new JScrollPane(extensionsList), BorderLayout.CENTER);
        extensionPointsPanel.add(extensionsButtonsPanel, BorderLayout.SOUTH);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        FlowLayout bottomLayout = new FlowLayout();
        bottomLayout.setHgap(30);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(extensionPointsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        initialize();
    }

    public void initialize() {
        extensionPoints = new ArrayList<>();

        // if extensions modified and then canceled
        Iterator<ExtensionPoint> i = ucExtendGR.getExtensionPoints();
        while (i.hasNext()) {
            ExtensionPoint extension = i.next();
            extensionPoints.add(extension);
        }
        updateExtensionsList();
    }

    private void updateExtensionsList() {
        Vector<String> v = new Vector<>();
        Iterator<ExtensionPoint> i = getExtensionPoints();
        while (i.hasNext()) {
            String s = i.next().getName();
            v.add(s);
        }
        extensionsList.setListData(v);
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

        ucExtendDialog = new JDialog(owner, true);
        ucExtendDialog.getContentPane().add(this);
        ucExtendDialog.setTitle(title);
        ucExtendDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        ucExtendDialog.pack();
        ucExtendDialog.setResizable(false);
        ucExtendDialog.setLocationRelativeTo(owner);
        ucExtendDialog.setVisible(true);

        return ok;
    }

    public Iterator<ExtensionPoint> getExtensionPoints() {
        return extensionPoints.iterator();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton) {
            ucExtendDialog.setVisible(false);
            ok = true;
        } else if (event.getSource() == cancelButton) {
            ucExtendDialog.setVisible(false);
        } else if (event.getSource() == addExtensionButton) {
            addExtensionPoint();
        } else if (event.getSource() == editExtensionButton) {
            editExtensionPoint();
        } else if (event.getSource() == deleteExtensionButton) {
            deleteExtensionPoint();
        }
    }

    public void addExtensionPoint() {
        String str = JOptionPane.showInputDialog(this, "Enter extension point name : ");
        if (str == null) {
            return;
        } else if (str.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You entered invalid extension point name.",
                    "Invalid extension name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        extensionPoints.add(new ExtensionPoint(str));
        updateExtensionsList();
    }

    public void editExtensionPoint() {
        if (extensionPoints == null || extensionPoints.isEmpty()
                || extensionsList.getSelectedIndex() < 0) {
            return;
        }

        int index = extensionsList.getSelectedIndex();
        ExtensionPoint ext = extensionPoints.get(index);

        String str = (String) JOptionPane.showInputDialog(this, "Enter extension point name : ",
                "Edit extension name", JOptionPane.QUESTION_MESSAGE,
                null, null, ext.getName());
        if (str == null ) {
            return;
        } else if (str.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You entered invalid extension point name.",
                    "Invalid extension name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        extensionPoints.set(index, new ExtensionPoint(str));
        updateExtensionsList();
    }

    public void deleteExtensionPoint() {
        if (extensionPoints == null || extensionPoints.isEmpty()
                || extensionsList.getSelectedIndex() < 0) {
            return;
        }

        extensionPoints.remove(extensionsList.getSelectedIndex());
        updateExtensionsList();
    }
}
