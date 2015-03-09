package edu.city.studentuml.model.domain;

/**
 *
 * @author draganbisercic
 */
public class UCGeneralization extends UCLink {

    public UCGeneralization(Actor src, Actor trg) {
        super(src, trg);
    }

    public UCGeneralization(UseCase src, UseCase trg) {
        super(src, trg);
    }
    
    @Override
    public String toString() {
        return getName();
    }
}

