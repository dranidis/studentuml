package edu.city.studentuml.model.graphical;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.model.domain.ActorInstance;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.NotifierVector;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.MoveEdit;

/**
 *
 * @author draganbisercic
 * @author dimitris
 */
public abstract class AbstractSDModel extends DiagramModel {

    private static final Logger logger = Logger.getLogger(AbstractSDModel.class.getName());

    // minimum distance that can be kept between messages
    public static final int MINIMUM_MESSAGE_DISTANCE = 10;
    // minimum distance that can be kept between role classifiers
    public static final int MINIMUM_RC_DISTANCE = 60;
    // clone list of role classifiers and messages that is maintained for
    // consistency purposes, ordering, etc.
    protected NotifierVector<RoleClassifierGR> roleClassifiers;
    protected NotifierVector<SDMessageGR> messages;
    private boolean automove = false;
    private CompoundEdit compoundEdit;

    protected AbstractSDModel(String title, UMLProject umlp) {
        super(title, umlp);
        roleClassifiers = new NotifierVector<>();
        messages = new NotifierVector<>();
    }

    // The element type is determined and the appropriate add method is called.
    @Override
    public final void addGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof RoleClassifierGR) {
            addRoleClassifier((RoleClassifierGR) e);
        } else if (e instanceof SDMessageGR) {
            addMessage((SDMessageGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.addGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    // before calling the superclass add element, the role classifier
    // is added to the project repository, then to the roleClassifiers list,
    // which is sorted and finally x positions are decided to keep the minimum distances
    public final void addRoleClassifier(RoleClassifierGR rc) {
        if (rc instanceof ActorInstanceGR) {
            repository.addActorInstance(((ActorInstanceGR) rc).getActorInstance());
        }
        addToRepository(rc);    //subclasses can add other role classifiers to repository

        roleClassifiers.add(rc);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
        super.addGraphicalElement(rc);
    }

    // hook; calls the methods in subclass that need to perform subclass specific tasks
    protected abstract void addToRepository(RoleClassifierGR rc);
    
    // before calling the superclass add element, the message
    // is added to the project repository, then to the messages list,
    // which is validated for consistency,
    // then sorted, ranked, and y positions are decided for minimum distances
    public final void addMessage(SDMessageGR m) {
        repository.addSDMessage(m.getMessage());
        messages.add(m);
        super.addGraphicalElement(m);

        validateMessages();
        // sort the messages, give them ranks, and keep the distances
        sortUpdateRankAndLifeLengthsAndValidateInOutMessages();
        if (automove) {
            if (m instanceof CallMessageGR) {
                int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");
                moveMessagesBelowBy(m, barHeight + MINIMUM_MESSAGE_DISTANCE);
            }
            if (m instanceof CreateMessageGR) {
                int barHeight = ConstantsGR.getInstance().get("SDMessageGR", "initBarHeight");
                moveMessagesBelowBy(m, barHeight + MINIMUM_MESSAGE_DISTANCE + m.getTarget().getHeight());
            }
            restoreMessagesDistances();
        }
        SystemWideObjectNamePool.getInstance().reload();
    }

    public void setAutomove(boolean automove) {
        this.automove = automove;
    }
    
    // to handle cases when the movement of one element affects other
    // elements, rather than simply moving alone
    @Override
    public final void moveGraphicalElement(GraphicalElement e, int x, int y) {
        if (e instanceof RoleClassifierGR) {
            moveRoleClassifier((RoleClassifierGR) e, x, y);
        } else if (e instanceof SDMessageGR) {
            moveMessage((SDMessageGR) e, x, y);
        } else { //UML Notes
            super.moveGraphicalElement(e, x, y);
        }
    }

    private final void moveRoleClassifier(RoleClassifierGR rc, int x, int y) {
        super.moveGraphicalElement(rc, x, y);
        roleClassifiersChanged();
    }

    private final void moveMessage(SDMessageGR m, int x, int y) {
        super.moveGraphicalElement(m, x, y);
        sortUpdateRankAndLifeLengthsAndValidateInOutMessages();
    }

    // apart from just moving the dragged and dropped element
    // this method also rearranges the elements to keep the distance
    @Override
    public final void settleGraphicalElement(GraphicalElement e, int x, int y) {
        if (e instanceof RoleClassifierGR) {
            settleRoleClassifier((RoleClassifierGR) e, x, y);
        } else if (e instanceof SDMessageGR) {
            settleMessage((SDMessageGR) e, x, y);
        }
    }

    private final void settleRoleClassifier(RoleClassifierGR rc, int x, int y) {
        super.moveGraphicalElement(rc, x, y);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
    }

    private final void settleMessage(SDMessageGR m, int x, int y) {
        super.moveGraphicalElement(m, x, y);
        validateMessages();
        // sort the messages, give them ranks, and keep the distances
        sortUpdateRankAndLifeLengthsAndValidateInOutMessages();
    }

    // subclasses that need to validate create and destroy messages need to override this method
    public void validateMessages() {
    }

    // called whenever role classifeirs change, by resorting the list
    // and updating the lifeline lengths
    private final void roleClassifiersChanged() {
        Collections.sort(roleClassifiers, (c1, c2) -> c1.getX() - c2.getX());
        updateLifelineLengths();
    }

    // called whenever messages change, by resorting the list and putting ranks,
    // and updating the lifeline lengths
    public final void sortUpdateRankAndLifeLengthsAndValidateInOutMessages() {
        sortMessagesAndUpdateRanks();
        updateLifelineLengths();
        
        validateInOut();
    }

    // sort the messages list according to their y position
    private void sortMessagesAndUpdateRanks() {
        Collections.sort(messages, (m1, m2) -> m1.getY() - m2.getY());

        // reset the messages numbering according to their rank
        for (int i = 0; i < messages.size(); i++) {
            messages.elementAt(i).getMessage().setRank(i + 1);
        }
    }

    // keeps the same ordering of role classifiers, but may change their x positions
    // in order to keep the minimum distance between them
    private void restoreRoleClassifiersDistances() {
        RoleClassifierGR object1;
        RoleClassifierGR object2;

        for (int i = 0; i < roleClassifiers.size() - 1; i++) {
            object1 = roleClassifiers.elementAt(i);
            object2 = roleClassifiers.elementAt(i + 1);

            if (object2.getX() - object1.getX() < MINIMUM_RC_DISTANCE) {
                object2.move(object1.getX() + MINIMUM_RC_DISTANCE, 0);
            }
        }
    }

    // keeps the same ordering of messages, but may change their y positions
    // in order to keep the minimum distances between them
    private void restoreMessagesDistances() {
        SDMessageGR message1;
        SDMessageGR message2;

        for (int i = 0; i < messages.size() - 1; i++) {
            message1 = messages.elementAt(i);
            message2 = messages.elementAt(i + 1);

            if (message2.getY() - message1.getY() < MINIMUM_MESSAGE_DISTANCE) {
                List<GraphicalElement> movedElements = new ArrayList<>();
                movedElements.add(message2);
                Point2D.Double undoCoordinates = new Point2D.Double(0, 0);
                Point2D.Double redoCoordinates = new Point2D.Double(0, 0);
                redoCoordinates.setLocation(0, message1.getY() + (double) MINIMUM_MESSAGE_DISTANCE); 
                
                message2.move(0, message1.getY() + MINIMUM_MESSAGE_DISTANCE);
                
                UndoableEdit edit = new MoveEdit(movedElements, this, undoCoordinates, redoCoordinates);
                this.compoundEdit.addEdit(edit);
            }
        }

        updateLifelineLengths();
    }

    // retrieves the list of all role classifiers
    public Vector<RoleClassifierGR> getRoleClassifiers() {
        return roleClassifiers;
    }

    // retrieves the list of all messages
    public Vector<SDMessageGR> getMessages() {
        return messages;
    }

    // updates the lifeline lengths of role classifiers whenever
    // a message is added or moved
    private final void updateLifelineLengths() {
        if (!messages.isEmpty()) {
            int highestMessageY = messages.lastElement().getY();

            // extend the life lines if any message is too down the drawing view
            setEndingY(highestMessageY + RoleClassifierGR.MINIMUM_LIFELINE_LENGTH);
        } else {
            setEndingY(RoleClassifierGR.VERTICAL_OFFSET + RoleClassifierGR.MINIMUM_LIFELINE_LENGTH);
        }
    }

    // returns true if the argument object has been destroyed
    public boolean isDestroyed(RoleClassifierGR rc) {
        Iterator<SDMessageGR> iterator = messages.iterator();
        SDMessageGR message;

        while (iterator.hasNext()) {
            message = iterator.next();

            if ((message instanceof DestroyMessageGR) && (message.getTarget() == rc)) {
                return true;
            }
        }

        return false;
    }

    // this changes the lifeline lengths of all the role classifiers,
    // except those that have been destroyed (i.e. have a determined lifeline length
    private final void setEndingY(int y) {
        Vector<RoleClassifierGR> objects = getRoleClassifiers();
        Iterator<RoleClassifierGR> iterator = objects.iterator();

        while (iterator.hasNext()) {
            RoleClassifierGR object = iterator.next();

            if (!isDestroyed(object)) {
                object.setEndingY(y);
            }
        }
    }

    // override superclass method removeGraphicalElement
    @Override
    public final void removeGraphicalElement(GraphicalElement e) {
        SystemWideObjectNamePool.getInstance().loading();
        if (e instanceof RoleClassifierGR) {
            removeRoleClassifier((RoleClassifierGR) e);
        } else if (e instanceof SDMessageGR) {
            removeMessage((SDMessageGR) e);
        } else if (e instanceof UMLNoteGR) {
            super.removeGraphicalElement(e);
        }
        SystemWideObjectNamePool.getInstance().done();
    }

    public List<SDMessageGR> getRoleClaffierGRMessages(RoleClassifierGR rc) {
        return messages.stream().filter(message -> ((message.getSource() == rc) || (message.getTarget() == rc)))
                .collect(Collectors.toList());
    }

    private final void removeRoleClassifier(RoleClassifierGR rc) {
        // Remove messages associated with the RoleClassifier
        getRoleClaffierGRMessages(rc).forEach(this::removeMessage);

        removeFromRepository(rc);

        roleClassifiers.remove(rc);
        roleClassifiersChanged();
        restoreRoleClassifiersDistances();
        super.removeGraphicalElement(rc);
    }

    // removes role classifiers from the repository
    private final void removeFromRepository(RoleClassifierGR rc) {
        if (rc.getRoleClassifier() instanceof ActorInstance) {
            if (!umlProject.isActorReferenced(rc, ((ActorInstance) rc.getRoleClassifier()).getActor())) {
                repository.removeActor(((ActorInstance) rc.getRoleClassifier()).getActor());

            }
            repository.removeActorInstance(((ActorInstance) rc.getRoleClassifier()));
        }

        removeClassifiersFromRepository(rc);
    }

    // hook: remove other classifiers than actor instance from the repository
    protected void removeClassifiersFromRepository(RoleClassifierGR rc) {
    }

    // method template
    private final void removeMessage(SDMessageGR e) {
        if (e instanceof CallMessageGR) {
            removeCallMessage((CallMessageGR) e);
        } else if (e instanceof ReturnMessageGR) {
            removeReturnMessage((ReturnMessageGR) e);
        }
        removeOtherMessages(e); //hook for subclasses

        sortUpdateRankAndLifeLengthsAndValidateInOutMessages();
        super.removeGraphicalElement(e);
    }

    // hook
    public void removeOtherMessages(SDMessageGR e) {
    }

    public void removeCallMessage(CallMessageGR callMessage) {
        callMessage.getCallMessage().clear();
        repository.removeSDMessage(callMessage.getMessage());
        messages.remove(callMessage);
    }

    public void removeReturnMessage(ReturnMessageGR returnMessage) {
        repository.removeSDMessage(returnMessage.getMessage());
        messages.remove(returnMessage);
    }

    private void validateInOut() {

        roleClassifiers.forEach(RoleClassifierGR::clearInOutStacks);
        
        if(!messages.isEmpty()) {
            messages.get(0).source.setActiveIn();
        }
        
        for(SDMessageGR message:messages) {
            message.setOutlineColor(Colors.getOutlineColor());
            message.setErrorMsg("");
            logger.finer(() -> message.message + ": " + message.source + " -> " + message.target);
            String validatedStr = "";
            if (message instanceof CallMessageGR || message instanceof CreateMessageGR) {
                validatedStr += message.source.validateOut(message.target);
                validatedStr += message.target.validateIn(message.source);
                if (validatedStr.length() > 0) {
                    message.setOutlineColor(Colors.getErrorColor());
                    message.setErrorMsg(validatedStr);
                }
            } else if (message instanceof ReturnMessageGR) {
                validatedStr += message.source.validateOutReturn(message.target);
                validatedStr += message.target.validateInReturn(message.source);
                if (validatedStr.length() > 0) {
                    message.setOutlineColor(Colors.getErrorColor());
                    message.setErrorMsg(validatedStr);
                }
            }
            if(message.source ==  message.target)
                message.source.addActivationHeight(message.getY() + 5);
            else
                message.source.addActivationHeight(message.getY());
            
            if(message instanceof CreateMessageGR)
                message.target.addActivationHeight(message.getY() + ((CreateMessageGR) message).target.getHeight() / 2);
            else
                message.target.addActivationHeight(message.getY());
        }
    }

    private void moveMessagesBelowBy(SDMessageGR m, int dis) {
                    
        List<GraphicalElement> movedElements = new ArrayList<>();
        Point2D.Double undoCoordinates = new Point2D.Double(0, 0);
        Point2D.Double redoCoordinates = new Point2D.Double(0, 0);
        for(int i = 0; i< messages.size() - 1; i++) {
            if (m == messages.get(i)) {
                if (m.getMessage().isReflective())
                    dis += 15;
                if (messages.get(i+1).getY() - m.getY() < dis) {
                    logger.fine("MOVING messages below");

                    int moveDis = dis - (messages.get(i+1).getY() - m.getY()); 
                    redoCoordinates.setLocation(0, moveDis); 
                    
                    for(int j = i+1; j < messages.size(); j++) {
                        movedElements.add(messages.get(j));
                        int y = messages.get(j).getY();
                        messages.get(j).move(0, y + moveDis);
                    }
                }
                break;
            }
        }
        UndoableEdit edit = new MoveEdit(movedElements, this, undoCoordinates, redoCoordinates);
        this.compoundEdit.addEdit(edit);
    }
    
    public List<SDMessageGR> getMessagesBelow(SDMessageGR m) {
        List<SDMessageGR> messagesBelow = new ArrayList<>();
        for(int i = 0; i< messages.size() - 1; i++) {
            if (m == messages.get(i)) {
                for(int j = i+1; j < messages.size(); j++) {
                    messagesBelow.add(messages.get(j));
                }
                break;
            }
        }        
        return messagesBelow;
    }

    public void setCompoundEdit(CompoundEdit compoundEdit) {
        this.compoundEdit = compoundEdit;
    }
}
