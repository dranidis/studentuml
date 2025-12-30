package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.MethodParameterEditor;

public class MethodParameterPanel extends ListPanel<MethodParameter> {

    public MethodParameterPanel(String title, CentralRepository repository) {
        super(title, repository);
    }

    @Override
    protected Editor<MethodParameter> createElementEditor(CentralRepository repository) {
        return new MethodParameterEditor(repository);
    }
}
