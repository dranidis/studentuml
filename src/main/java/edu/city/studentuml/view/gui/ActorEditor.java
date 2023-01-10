package edu.city.studentuml.view.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.repository.CentralRepository;

/**
 * @author Dimitris Dranidis
 */
public class ActorEditor extends ClassifierEditor {

    public ActorEditor(Actor actor, CentralRepository cr) {
        super(actor, cr);

        repository = cr;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(namePanel);
        add(bottomPanel);
    }

    public Actor getActor() {
        return new Actor(getClassName());
    }

    @Override
    protected void handleRest(ActionEvent event) {
        // empty
    }

    @Override
    protected void handleOK(ActionEvent event) {
        classifierDialog.setVisible(false);
        setReturnToTrue();
    }

    @Override
    protected List<Classifier> getTypes() {
        List<Classifier> actors = new ArrayList<>();
        actors.addAll(repository.getActors());
        return actors;
    }
}
