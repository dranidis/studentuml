package edu.city.studentuml.view.gui.components;

import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.MethodEditor;

public class MethodsPanel extends ListPanel<Method> {

    public MethodsPanel(String title, CentralRepository repository) {
        super(title, repository);
    }

    @Override
    protected ElementEditor<Method> createElementEditor(Method method, CentralRepository repository) {
        return new MethodEditor(method, repository);
    }
}
