package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Repository operations for DesignClass type.
 *
 * @author Dimitris Dranidis
 */
public class DesignClassRepositoryOperations implements TypeRepositoryOperations<DesignClass> {

    @Override
    public void addToRepository(CentralRepository repository, DesignClass type) {
        repository.addClass(type);
    }

    @Override
    public void editInRepository(CentralRepository repository, DesignClass oldType, DesignClass newType) {
        repository.editClass(oldType, newType);
    }

    @Override
    public void removeFromRepository(CentralRepository repository, DesignClass type) {
        repository.removeClass(repository.getDesignClass(type.getName()));
    }

    @Override
    public String getTypeName(DesignClass type) {
        return type.getName();
    }
}
