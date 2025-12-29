package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ExtensionPointsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author draganbisercic
 */
public class UCExtendEditor extends JPanel {

    private ExtensionPointsPanel extensionPointsPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel bottomPanel;
    private JDialog ucExtendDialog;
    private UCExtendGR ucExtendGR;
    private boolean ok; // stores whether the user has pressed ok

    public UCExtendEditor(UCExtendGR uc, CentralRepository repository) {
        ucExtendGR = uc;

        setLayout(new BorderLayout());

        extensionPointsPanel = new ExtensionPointsPanel("Extension Points", repository);

        okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            ucExtendDialog.setVisible(false);
            ok = true;
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> ucExtendDialog.setVisible(false));

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
        // Convert List to Vector for ListPanel
        Vector<ExtensionPoint> extensionPointsVector = new Vector<>(ucExtendGR.getExtensionPoints());
        extensionPointsPanel.setElements(extensionPointsVector);
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

    public List<ExtensionPoint> getExtensionPoints() {
        return extensionPointsPanel.getElements();
    }
}
