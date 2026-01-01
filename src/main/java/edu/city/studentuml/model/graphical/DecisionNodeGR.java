package edu.city.studentuml.model.graphical;

import edu.city.studentuml.controller.EditContext;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.util.undoredo.EditDecisionNodeEdit;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class DecisionNodeGR extends AbstractDecisionNodeGR {

    public DecisionNodeGR(DecisionNode decisionNode, int x, int y) {
        super(decisionNode, x, y);
    }

    @Override
    protected String getStreamName() {
        return "decisionnode";
    }

    @Override
    public DecisionNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        DecisionNode sameDecisionNode = (DecisionNode) getComponent();

        // Create new graphical wrapper referencing the SAME domain object
        DecisionNodeGR clonedGR = new DecisionNodeGR(sameDecisionNode, this.startingPoint.x, this.startingPoint.y);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;

        return clonedGR;
    }

    /**
     * Opens an editor dialog for editing the decision node's name.
     * Uses the template method from NodeComponentGR for the common editing workflow.
     * 
     * @param context the edit context providing access to model, repository, parent
     *                component, and undo support
     * @return true if the user confirmed the edit, false if cancelled
     */
    @Override
    public boolean edit(EditContext context) {
        return editNameWithDialog(
                context,
                "Decision Node Editor",
                "Decision name: ",
                (original, undo, model) -> new EditDecisionNodeEdit(
                        (DecisionNode) original,
                        (DecisionNode) undo,
                        model));
    }
}