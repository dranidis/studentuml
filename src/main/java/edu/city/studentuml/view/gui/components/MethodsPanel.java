package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.MethodEditor;

public class MethodsPanel extends ListPanel<Method> {

    public MethodsPanel(String title, CentralRepository repository) {
        super(title, repository);
    }

    @Override
    protected Editor<Method> createElementEditor(CentralRepository repository) {
        return new MethodEditor(repository);
    }
}
