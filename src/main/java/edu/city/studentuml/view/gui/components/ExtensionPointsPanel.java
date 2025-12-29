package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.ExtensionPointEditor;

/**
 * Reusable panel for managing a list of extension points.
 * 
 * @author draganbisercic
 */
public class ExtensionPointsPanel extends ListPanel<ExtensionPoint> {

    public ExtensionPointsPanel(String title, CentralRepository repository) {
        super(title, repository);
    }

    @Override
    protected ElementEditor<ExtensionPoint> createElementEditor(ExtensionPoint extensionPoint,
            CentralRepository repository) {
        return new ExtensionPointEditor(extensionPoint, repository);
    }
}
