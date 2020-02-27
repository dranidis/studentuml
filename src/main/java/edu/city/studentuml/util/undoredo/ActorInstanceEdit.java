package edu.city.studentuml.util.undoredo;

import edu.city.studentuml.model.domain.ActorInstance;

/**
 *
 * @author draganbisercic
 */
public class ActorInstanceEdit {

    ActorInstance actorInstance;
    String actorName;

    public ActorInstanceEdit(ActorInstance actorInstance, String actorName) {
        this.actorInstance = actorInstance;
        this.actorName = actorName;
    }

    public ActorInstance getActorInstance() {
        return actorInstance;
    }

    public void setActorInstance(ActorInstance actorInstance) {
        this.actorInstance = actorInstance;
    }

    public String getActorName() {
        return actorName;
    }

    public void setTypeName(String actorName) {
        this.actorName = actorName;
    }

    public ActorInstanceEdit clone() {
        ActorInstance obj = new ActorInstance(actorInstance.getName(), actorInstance.getActor());
        ActorInstanceEdit copy = new ActorInstanceEdit(obj, actorName);
        return copy;
    }
}
