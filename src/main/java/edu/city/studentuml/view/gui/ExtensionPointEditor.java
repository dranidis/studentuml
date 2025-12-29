package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ExtensionPoint;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.ElementEditor;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Editor for ExtensionPoint domain objects.
 * 
 * @author draganbisercic
 */
public class ExtensionPointEditor implements ElementEditor<ExtensionPoint> {

    private ExtensionPoint extensionPoint;

    public ExtensionPointEditor(ExtensionPoint extensionPoint, CentralRepository repository) {
        this.extensionPoint = extensionPoint;
    }

    @Override
    public boolean showDialog(Component parent) {

        String initialValue = (extensionPoint != null) ? extensionPoint.getName() : "";
        StringEditorDialog stringEditorDialog = new StringEditorDialog(parent,
                "Extension Point Editor", "Extension Point Name:", initialValue);

        if (!stringEditorDialog.showDialog()) {
            return false;
        }

        if (stringEditorDialog.getText().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "You entered invalid extension point name.",
                    "Invalid extension name",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (extensionPoint == null) {
            extensionPoint = new ExtensionPoint(stringEditorDialog.getText());
        } else {
            extensionPoint.setName(stringEditorDialog.getText());
        }

        return true;
    }

    @Override
    public ExtensionPoint createElement() {
        return extensionPoint;
    }

    @Override
    public void editElement() {
        // Element is edited in-place during showDialog
    }
}
