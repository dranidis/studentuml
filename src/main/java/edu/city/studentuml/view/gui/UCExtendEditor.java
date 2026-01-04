package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.domain.UCExtend;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;
import edu.city.studentuml.view.gui.components.ExtensionPointsPanel;
import java.awt.Component;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;

/**
 * Editor for UCExtend domain objects. Allows editing of extension points for
 * use case extend relationships.
 * 
 * @author draganbisercic
 * @author Dimitris Dranidis
 */
public class UCExtendEditor extends OkCancelDialog implements Editor<UCExtend> {

    private ExtensionPointsPanel extensionPointsPanel;
    private UCExtend ucExtend;
    private CentralRepository repository;
    private static final String TITLE = "Use Case Extend Editor";

    public UCExtendEditor(CentralRepository repository) {
        super(null, TITLE); // parent will be set in editDialog
        this.repository = repository;
    }

    // Legacy constructor for backward compatibility during transition
    @Deprecated
    public UCExtendEditor(Component parent, String title, UCExtend ucExtend, CentralRepository repository) {
        super(parent, title);
        this.ucExtend = ucExtend;
        this.repository = repository;

        initializeIfNeeded(); // Ensure UI components are created
        initialize();
    }

    @Override
    protected JPanel makeCenterPanel() {
        extensionPointsPanel = new ExtensionPointsPanel("Extension Points", repository);
        return extensionPointsPanel;
    }

    @Override
    public UCExtend editDialog(UCExtend ucExtend, Component parent) {
        // Set parent for this dialog instance
        this.parent = parent;

        // Ensure UI components are created before initializing fields
        initializeIfNeeded();

        // Initialize fields based on ucExtend
        initialize(ucExtend);

        // Show dialog using OkCancelDialog's showDialog method
        if (!showDialog()) {
            return null; // Cancelled
        }

        // Create a clone with the new extension points
        // This is important: we must NOT modify the original ucExtend in-place
        // because the repository's editUCExtend() method expects originalUCExtend
        // and newUCExtend to be different objects so it can copy extension points
        // from newUCExtend to originalUCExtend.
        UCExtend clonedUCExtend = ucExtend.clone();
        clonedUCExtend.clearPoints();
        for (ExtensionPoint ep : getExtensionPoints()) {
            clonedUCExtend.addExtensionPoint(ep);
        }

        return clonedUCExtend;
    }

    // Legacy method for backward compatibility
    @Deprecated
    public void initialize() {
        initialize(this.ucExtend);
    }

    public void initialize(UCExtend ucExtend) {
        // Convert List to Vector for ListPanel
        Vector<ExtensionPoint> extensionPointsVector = new Vector<>(ucExtend.getExtensionPoints());
        extensionPointsPanel.setElements(extensionPointsVector);
    }

    public List<ExtensionPoint> getExtensionPoints() {
        return extensionPointsPanel.getElements();
    }
}
