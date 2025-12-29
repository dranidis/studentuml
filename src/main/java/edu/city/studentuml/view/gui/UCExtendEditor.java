package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.graphical.UCExtendGR;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ExtensionPointsPanel;
import java.awt.Component;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;

/**
 * @author draganbisercic
 */
public class UCExtendEditor extends OkCancelDialog {

    private ExtensionPointsPanel extensionPointsPanel;
    private UCExtendGR ucExtendGR;
    private CentralRepository repository;

    public UCExtendEditor(Component parent, String title, UCExtendGR uc, CentralRepository repository) {
        super(parent, title);
        this.ucExtendGR = uc;
        this.repository = repository;

        initializeIfNeeded(); // Ensure UI components are created
        initialize();
    }

    @Override
    protected JPanel makeCenterPanel() {
        extensionPointsPanel = new ExtensionPointsPanel("Extension Points", repository);
        return extensionPointsPanel;
    }

    public void initialize() {
        // Convert List to Vector for ListPanel
        Vector<ExtensionPoint> extensionPointsVector = new Vector<>(ucExtendGR.getExtensionPoints());
        extensionPointsPanel.setElements(extensionPointsVector);
    }

    public List<ExtensionPoint> getExtensionPoints() {
        return extensionPointsPanel.getElements();
    }
}
