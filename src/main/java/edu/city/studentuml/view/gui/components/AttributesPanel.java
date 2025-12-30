package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.AttributeEditor;

public class AttributesPanel extends ListPanel<Attribute> {

    public AttributesPanel(String title, CentralRepository repository) {
        super(title, repository);
    }

    @Override
    protected Editor<Attribute> createElementEditor(CentralRepository repository) {
        return new AttributeEditor(repository);
    }
}
