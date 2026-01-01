package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * Repository operations for Actor type.
 *
 * @author Dimitris Dranidis
 */
public class ActorRepositoryOperations implements TypeRepositoryOperations<Actor> {

    @Override
    public void addToRepository(CentralRepository repository, Actor type) {
        repository.addActor(type);
    }

    @Override
    public void editInRepository(CentralRepository repository, Actor oldType, Actor newType) {
        repository.editActor(oldType, newType);
    }

    @Override
    public void removeFromRepository(CentralRepository repository, Actor type) {
        repository.removeActor(repository.getActor(type.getName()));
    }

    @Override
    public String getTypeName(Actor type) {
        return type.getName();
    }
}
