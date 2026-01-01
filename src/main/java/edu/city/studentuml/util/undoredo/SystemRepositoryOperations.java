package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Repository operations for System type.
 *
 * @author Dimitris Dranidis
 */
public class SystemRepositoryOperations implements TypeRepositoryOperations<System> {

    @Override
    public void addToRepository(CentralRepository repository, System type) {
        repository.addSystem(type);
    }

    @Override
    public void editInRepository(CentralRepository repository, System oldType, System newType) {
        repository.editSystem(oldType, newType);
    }

    @Override
    public void removeFromRepository(CentralRepository repository, System type) {
        repository.removeSystem(repository.getSystem(type.getName()));
    }

    @Override
    public String getTypeName(System type) {
        return type.getName();
    }
}
