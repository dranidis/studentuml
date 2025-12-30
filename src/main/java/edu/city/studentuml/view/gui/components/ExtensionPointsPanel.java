package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.StringEditorDialog;
import java.awt.Component;
import javax.swing.JOptionPane;

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
    protected Editor<ExtensionPoint> createElementEditor(CentralRepository repository) {
        return new Editor<ExtensionPoint>() {
            @Override
            public ExtensionPoint editDialog(ExtensionPoint extensionPoint, Component parent) {
                String initialValue = (extensionPoint != null) ? extensionPoint.getName() : "";
                StringEditorDialog stringEditorDialog = new StringEditorDialog(parent,
                        "Extension Point Editor", "Extension Point Name:", initialValue);

                if (!stringEditorDialog.showDialog()) {
                    return null; // Cancelled
                }

                if (stringEditorDialog.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                            "You entered invalid extension point name.",
                            "Invalid extension name",
                            JOptionPane.WARNING_MESSAGE);
                    return null;
                }

                if (extensionPoint == null) {
                    // Create new extension point
                    return new ExtensionPoint(stringEditorDialog.getText());
                } else {
                    // Edit existing extension point
                    extensionPoint.setName(stringEditorDialog.getText());
                    return extensionPoint;
                }
            }
        };
    }
}
