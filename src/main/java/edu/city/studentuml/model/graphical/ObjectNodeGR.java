package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.undoredo.DesignClassRepositoryOperations;
import edu.city.studentuml.util.undoredo.EditObjectNodeEdit;
import edu.city.studentuml.util.undoredo.TypeRepositoryOperations;
import edu.city.studentuml.view.gui.ObjectNodeEditor;
import edu.city.studentuml.view.gui.TypeOperation;
import edu.city.studentuml.view.gui.TypedEntityEditResult;

/**
 * @author Biser
 * @author Dimitris Dranidis
 */
public class ObjectNodeGR extends LeafNodeGR {

    private int minimumWidth;
    private int minimumHeight;
    private final int objectNameXOffset;
    private final int objectNameYOffset;
    private final int objectStatesXOffset;
    private final int objectStatesYOffset;
    private Font objectNameFont;
    private Font objectStatesFont;

    public ObjectNodeGR(ObjectNode objectNode, int x, int y) {
        super(objectNode, x, y);

        minimumWidth = 70;
        minimumHeight = 40;
        objectNameXOffset = 6;
        objectNameYOffset = 8;
        objectStatesXOffset = 5;
        objectStatesYOffset = 5;

        // initialize the element's width and height to the minimum ones
        width = minimumWidth;
        height = minimumHeight;

        objectNameFont = new Font("SansSerif", Font.PLAIN, 14);
        objectStatesFont = new Font("SansSerif", Font.PLAIN, 11);
    }

    @Override
    public void draw(Graphics2D g) {

        calculateWidth(g);
        calculateHeight(g);

        int startingX = getX();
        int startingY = getY();

        // paint action node
        g.setPaint(getBackgroundColor());
        Shape shape = new Rectangle2D.Double(startingX, startingY, width, height);
        g.fill(shape);

        g.setStroke(GraphicsHelper.makeSolidStroke());
        Stroke originalStroke = g.getStroke();
        if (isSelected()) {
            g.setStroke(GraphicsHelper.makeSelectedSolidStroke());
            g.setPaint(getHighlightColor());
        } else {
            g.setStroke(originalStroke);
            g.setPaint(getOutlineColor());
        }
        // draw the action node
        g.draw(shape);

        g.setStroke(originalStroke);
        g.setPaint(getOutlineColor());

        ObjectNode node = (ObjectNode) component;
        if (!node.hasStates()) {
            // draw object name and type in the center
            drawString(g, node.toString());
        } else {
            // draw object name and type above states
            drawString(g, node.toString(), node.getStatesAsString());
        }
    }

    private void drawString(Graphics2D g, String name) {
        FontRenderContext frc = g.getFontRenderContext();
        // draw object node name and type in the center
        if (!name.equals("")) {
            Rectangle2D nameBounds = GraphicsHelper.getTextBounds(name, objectNameFont, frc);
            int nameX = GraphicsHelper.calculateCenteredTextX(width, nameBounds);
            int nameY = GraphicsHelper.calculateCenteredTextY(height, nameBounds);

            g.setFont(objectNameFont);
            g.drawString(name, getX() + nameX, getY() + nameY);
        }
    }

    private void drawString(Graphics2D g, String name, String states) {
        Rectangle2D nameBounds = null;
        Rectangle2D statesBounds = null;
        FontRenderContext frc = g.getFontRenderContext();
        // draw object node name and type
        if (!name.equals("")) {
            nameBounds = GraphicsHelper.getTextBounds(name, objectNameFont, frc);
            int nameX = GraphicsHelper.calculateCenteredTextX(width, nameBounds);
            int nameY = objectNameYOffset - (int) nameBounds.getY();

            g.setFont(objectNameFont);
            g.drawString(name, getX() + nameX, getY() + nameY);
        } else {
            // if ever get here, do not draw states
            return;
        }

        // draw object node states
        if (!states.equals("")) {
            statesBounds = GraphicsHelper.getTextBounds(states, objectStatesFont, frc);
            int nameX = GraphicsHelper.calculateCenteredTextX(width, statesBounds);
            int nameY = objectStatesYOffset + objectNameYOffset - (int) nameBounds.getY() - (int) statesBounds.getY();

            g.setFont(objectStatesFont);
            g.drawString(states, getX() + nameX, getY() + nameY);
        }
    }

    @Override
    protected int calculateWidth(Graphics2D g) {
        int newWidth = minimumWidth;
        FontRenderContext frc = g.getFontRenderContext();

        // consider object name and type text dimensions
        if (component.toString().length() != 0) {
            Rectangle2D bounds = GraphicsHelper.getTextBounds(component.toString(), objectNameFont, frc);
            int objectNameWidth = (int) bounds.getWidth() + 2 * objectNameXOffset;

            if (objectNameWidth > newWidth) {
                newWidth = objectNameWidth;
            }
        } else {
            // if object node name is empty then no need for states
            // (should never get here anyway [controller's responsibility])
            return minimumWidth;
        }

        // consider object states text dimensions
        String states = ((ObjectNode) component).getStatesAsString();
        if (states.length() != 0) {
            Rectangle2D bounds = GraphicsHelper.getTextBounds(states, objectStatesFont, frc);
            int objectStatesWidth = (int) bounds.getWidth() + 2 * objectStatesXOffset;

            if (objectStatesWidth > newWidth) {
                newWidth = objectStatesWidth;
            }
        }

        width = newWidth;

        return newWidth;
    }

    @Override
    protected int calculateHeight(Graphics2D g) {
        return minimumHeight;
    }

    @Override
    protected String getStreamName() {
        return "objectnode";
    }

    @Override
    public boolean edit(EditContext context) {
        CentralRepository repository = context.getModel().getCentralRepository();
        ObjectNode objectNode = (ObjectNode) getComponent();

        // Create editor and initial result
        ObjectNodeEditor objectNodeEditor = createEditor(context);
        TypedEntityEditResult<DesignClass, ObjectNode> initialResult = new TypedEntityEditResult<>(objectNode,
                new java.util.ArrayList<>());

        TypedEntityEditResult<DesignClass, ObjectNode> result = objectNodeEditor.editDialog(initialResult,
                context.getParentComponent());

        // Check if user cancelled
        if (result == null) {
            return true; // User cancelled, but we handled it
        }

        ObjectNode newObjectNode = result.getDomainObject();

        // do not edit if name and type are both empty
        if (newObjectNode.getName().isEmpty() && newObjectNode.getType() == null) {
            JOptionPane.showMessageDialog(context.getParentComponent(),
                    "Object name and/or type is missing!",
                    "Object Node Error",
                    JOptionPane.ERROR_MESSAGE);
            return true; // Error shown, we handled it
        }

        // Create compound edit for all operations
        CompoundEdit compoundEdit = new CompoundEdit();

        // Apply type operations first and add their undo edits
        TypeRepositoryOperations<DesignClass> typeOps = new DesignClassRepositoryOperations();
        for (TypeOperation<DesignClass> typeOp : result.getTypeOperations()) {
            typeOp.applyTypeOperationsAndAddTheirUndoEdits(repository, typeOps, compoundEdit);
        }

        // Add domain object edit and apply
        UndoableEdit edit = new EditObjectNodeEdit(objectNode, newObjectNode, context.getModel());
        compoundEdit.addEdit(edit);
        repository.editObjectNode(objectNode, newObjectNode);

        // Post the compound edit
        compoundEdit.end();
        if (!compoundEdit.isInProgress() && compoundEdit.canUndo()) {
            context.getParentComponent().getUndoSupport().postEdit(compoundEdit);
        }

        // set observable model to changed in order to notify its views
        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true; // Successfully handled
    }

    /**
     * Creates the editor for this Object Node. Extracted into a protected method to
     * enable testing without UI dialogs (can be overridden to return mock editor).
     * 
     * @param context the edit context containing repository
     * @return the editor instance
     */
    protected ObjectNodeEditor createEditor(EditContext context) {
        return new ObjectNodeEditor(context.getRepository());
    }

    @Override
    public ObjectNodeGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        ObjectNode sameObjectNode = (ObjectNode) getComponent();

        // Create new graphical wrapper referencing the SAME domain object
        ObjectNodeGR clonedGR = new ObjectNodeGR(sameObjectNode, this.startingPoint.x, this.startingPoint.y);

        // Copy visual properties
        clonedGR.width = this.width;
        clonedGR.height = this.height;

        return clonedGR;
    }
}
