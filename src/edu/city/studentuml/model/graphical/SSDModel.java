package edu.city.studentuml.model.graphical;

import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UMLProject;

/**
 *
 * @author draganbisercic
 */
public class SSDModel extends AbstractSDModel {

    public SSDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    @Override
    protected void addToRepository(RoleClassifierGR rc) {
        if (rc.getRoleClassifier() instanceof SystemInstance) {
            if (!umlProject.isSystemReferenced(rc, ((SystemInstance) rc.getRoleClassifier()).getSystem())) {
                repository.removeSystem(((SystemInstance) rc.getRoleClassifier()).getSystem());

            }
            repository.addSystemInstance(((SystemInstance) rc.getRoleClassifier()));
        }
    }

    @Override
    protected void removeClassifiersFromRepository(RoleClassifierGR rc) {
        if (rc.getRoleClassifier() instanceof SystemInstance) {
            repository.removeSystemInstance((SystemInstance) rc.getRoleClassifier());
        }
    }
}
