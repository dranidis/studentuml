package edu.city.studentuml.model.domain;

/**
 *
 * @author draganbisercic
 */
public class UCInclude extends UCLink {

    public static final String STEREOTYPE = "<<include>>";

    public UCInclude(UseCase including, UseCase included) {
        super(including, included);
        this.name = STEREOTYPE;
    }

    @Override
    public String toString() {
        return getName();
    }
}
