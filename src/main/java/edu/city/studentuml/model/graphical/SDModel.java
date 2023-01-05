package edu.city.studentuml.model.graphical;

//Author: Ervin Ramollari

import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import java.util.Iterator;
import java.util.Vector;

/**
 * Represents the model of a UML sequence diagram. It has to override
 * many of the methods in its superclass DiagramModel, because of many
 * consistency rules that have to apply in sequence diagrams. Many validations
 * have to be conducted when elements are added, moved, or deleted.
 */
public class SDModel extends AbstractSDModel {

    public SDModel(String title, UMLProject umlp) {
        super(title, umlp);
    }

    @Override
    protected void addToRepository(RoleClassifierGR rc) {
        if (rc instanceof SDObjectGR) {
            repository.addObject(((SDObjectGR) rc).getSDObject());
        } else if (rc instanceof MultiObjectGR) {
            repository.addMultiObject(((MultiObjectGR) rc).getMultiObject());
        }
    }

    // override superclass method representing the hook for subclasses
    @Override
    protected void removeClassifiersFromRepository(RoleClassifierGR rc) {
        if (rc.getRoleClassifier() instanceof SDObject) {
            if (!umlProject.isClassReferenced(rc, ((SDObject) rc.getRoleClassifier()).getDesignClass())) {
                ((SDObject) rc.getRoleClassifier()).getDesignClass().clear();
                repository.removeClass(((SDObject) rc.getRoleClassifier()).getDesignClass());

            }
            repository.removeObject(((SDObject) rc.getRoleClassifier()));
        }

        if (rc.getRoleClassifier() instanceof MultiObject) {
            if (!umlProject.isClassReferenced(rc, ((MultiObject) rc.getRoleClassifier()).getDesignClass())) {
                ((MultiObject) rc.getRoleClassifier()).getDesignClass().clear();
                repository.removeClass(((MultiObject) rc.getRoleClassifier()).getDesignClass());

            }
            repository.removeMultiObject((MultiObject) rc.getRoleClassifier());
        }
    }

    // override superclass method representing the hook for subclasses
    @Override
    public void removeOtherMessages(SDMessageGR e) {
        if (e instanceof CreateMessageGR) {
            removeCreateMessage((CreateMessageGR) e);
        } else if (e instanceof DestroyMessageGR) {
            removeDestroyMessage((DestroyMessageGR) e);
        }
    }

    public void removeCreateMessage(CreateMessageGR createMessage) {
        repository.removeSDMessage(createMessage.getMessage());
        messages.remove(createMessage);
        createMessage.getTarget().setBeginningY(RoleClassifierGR.VERTICAL_OFFSET);
    }

    public void removeDestroyMessage(DestroyMessageGR destroyMessage) {
        repository.removeSDMessage(destroyMessage.getMessage());
        messages.remove(destroyMessage);
    }

/**
 * <p>
 * Validates and rearranges the positions of CreateMessageGR and
 * DestroyMessageGR messages in the messages list:
 * </p>
 * <ul>
 * <li>
 * For each CreateMessageGR message, the validateCreateMessagePosition
 * method is called to check if there are any other CreateMessageGR messages
 * with the same target and remove them if necessary.
 * It also checks for other messages with the same target or source as the
 * CreateMessageGR message and moves them down
 * if their y position is lower. This may result in the CreateMessageGR message
 * being moved down in the list
 * as well.
 * </li>
 * <li>
 * For each DestroyMessageGR message, the validateDestroyMessagePosition
 * method is called to check if there are any other
 * DestroyMessageGR messages with the same target and remove them if necessary.
 * It also checks for other messages with
 * the same target or source as the DestroyMessageGR message and moves them up
 * if their y position is higher.
 * This may result in the DestroyMessageGR message being moved up in the list as
 * well.
 * </li>
 * </ul>
 * <p>
 * 
 * @throws IllegalArgumentException if the position of a CreateMessageGR or
 *                                  DestroyMessageGR message is invalid
 *                                  </p>
 * 
 */

    @Override
    public void validateMessages() { 
        // keeps track of messages that have been checked already
        Vector<SDMessageGR> checked = new Vector<>();
        Iterator<SDMessageGR> iterator = messages.iterator();
        SDMessageGR message;

        while (iterator.hasNext()) {
            message = iterator.next();

            if ((message instanceof CreateMessageGR) && !checked.contains(message)) {

                // this method might result in direct changes to the messages list
                // so the iterator is refreshed to be safe from inconsistencies
                validateCreateMessagePosition((CreateMessageGR) message);
                checked.add(message);

                // refresh the iterator
                iterator = messages.iterator();
            } else if ((message instanceof DestroyMessageGR) && !checked.contains(message)) {
                validateDestroyMessagePosition((DestroyMessageGR) message);
                checked.add(message);
                iterator = messages.iterator();
            }
        }
    }

    // checks whether a particular create message is in consistent position
    // If not, modifications are made to bring consistency
    private void validateCreateMessagePosition(CreateMessageGR createMessage) {
        SDMessageGR message;

        for (int i = 0; i < messages.size(); i++) {
            message = messages.elementAt(i);

            if ((message instanceof CreateMessageGR) && (message.getTarget() == createMessage.getTarget())
                    && (message != createMessage)) {

                // delete the existing create message
                
                messages.remove(message);
                super.removeGraphicalElement(message);
                createMessage.refreshTargetPosition();
                i--;
            } else if ((message.getTarget() == createMessage.getTarget()
                    || message.getSource() == createMessage.getTarget()) && message != createMessage
                    && message.getY() <= createMessage.getY()) {
                // move the message down
                message.move(0, (createMessage.getY() + i + 1));
            }

        }
    }

    // checks whether a particular destroy message is in consistent position
    // If not modificatiosn are made to bring consistency
    private void validateDestroyMessagePosition(DestroyMessageGR destroyMessage) {
        SDMessageGR message;

        for (int i = 0; i < messages.size(); i++) {
            message = messages.elementAt(i);

            if ((message instanceof DestroyMessageGR) && (message.getTarget() == destroyMessage.getTarget())
                    && (message != destroyMessage)) {

                // delete the existing destroy message
                messages.remove(message);
                super.removeGraphicalElement(message);
                destroyMessage.refreshTargetPosition();
                i--;
            } else if (((message.getTarget() == destroyMessage.getTarget())
                    || (message.getSource() == destroyMessage.getTarget())) && (message != destroyMessage)
                    && (message.getY() >= destroyMessage.getY())) {

                // move the message up
                message.move(0, (destroyMessage.getY() - (messages.size() - i)));
            }

        }
    }
}
