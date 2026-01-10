package edu.city.studentuml.view.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.view.gui.components.Editor;

/**
 * @author Dimitris Dranidis
 */
public class ActorEditor extends ClassifierEditor implements Editor<Actor> {

    private static final String TITLE = "Actor Editor";

    /**
     * Constructor for Editor<Actor> interface pattern.
     * 
     * @param cr the central repository
     */
    public ActorEditor(CentralRepository cr) {
        super(new Actor(""), cr);
        repository = cr;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(namePanel);
        add(bottomPanel);
    }

    @Override
    public Actor editDialog(Actor actor, Component parent) {
        // Set the name in the name field using the protected method from ClassifierEditor
        setClassifierName(actor.getName());

        if (!showDialog(parent, TITLE)) {
            return null; // Cancelled
        }
        return getActor();
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
