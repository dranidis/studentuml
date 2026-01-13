package edu.city.studentuml.model.graphical;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.undo.UndoableEdit;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.XMLSyntax;
import edu.city.studentuml.util.undoredo.EditAssociationEdit;
import edu.city.studentuml.view.gui.AssociationEditor;
import edu.city.studentuml.view.gui.CCDAssociationEditor;

@JsonIncludeProperties({ "from", "to", "internalid", "association" })
public class AssociationGR extends LinkGR {

    private static final Logger logger = Logger.getLogger(AssociationGR.class.getName());

    private Association association;
    // the graphical classes that the association line connects in the diagram
    private static final Font NAME_FONT = FontRegistry.ASSOCIATION_NAME_FONT;
    private static final Font ROLE_FONT = FontRegistry.ASSOCIATION_ROLE_FONT;

    public AssociationGR(ClassifierGR a, ClassifierGR b, Association assoc) {
        super(a, b);
        this.association = assoc;
    }

    @Override
    protected int getLinkDirection() {
        return association.getDirection();
    }

    @Override
    protected void drawReflective(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        GeneralPath reflective = new GeneralPath();
        float step = getReflectiveStep();
        reflective.moveTo(aX, aY);
        reflective.lineTo(aX, aY - REFLECTIVE_UP * step); // up 2
        reflective.lineTo(aX + REFLECTIVE_RIGHT * step, aY - REFLECTIVE_UP * step); // right 4
        reflective.lineTo(aX + REFLECTIVE_RIGHT * step, bY); // down 4
        reflective.lineTo(bX, bY); // left 2

        g.draw(reflective);

        drawArrowHeadsReflective(g);

        g.setPaint(getOutlineColor());

        // draw the association name string
        g.setFont(NAME_FONT);

        double angle = 0;
        if (association.getLabelDirection() == Association.FROM_A_TO_B) {
            angle = Math.toRadians(0);
        } else {
            angle = Math.toRadians(180);
        }
        drawAssociationName((int) (aX + REFLECTIVE_RIGHT * step / 2), (int) (aY - REFLECTIVE_UP * step), angle,
                association.getName(), association.getShowArrow(), g);

        // draw role names and multiplicities
        g.setFont(ROLE_FONT);
        String roleAName = association.getRoleA().getName();
        String roleAMultiplicity = association.getRoleA().getMultiplicity();
        String roleBName = association.getRoleB().getName();
        String roleBMultiplicity = association.getRoleB().getMultiplicity();
        drawRoleString(aX, getTopLeftYA() + 5, -Math.PI / 2, roleAMultiplicity, roleAName, true, g);
        drawRoleString(getTopLeftXA() + getWidthA() - 5, bY, 0, roleBMultiplicity, roleBName, false, g);
    }

    @Override
    protected void drawRoles(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        // draw role names and multiplicities
        g.setFont(ROLE_FONT);
        drawRoleString(aX, aY, angleA, association.getRoleA().getMultiplicity(), association.getRoleA().getName(),
                true, g);
        drawRoleString(bX, bY, angleB, association.getRoleB().getMultiplicity(), association.getRoleB().getName(),
                true, g);
    }

    @Override
    protected void drawName(int aX, int aY, int bX, int bY, double angleA, double angleB, Graphics2D g) {
        // determine the coordinates of the line center
        int centerX = (aX + bX) / 2;
        int centerY = (aY + bY) / 2;
        String name = association.getName();
        int labelDirection = association.getLabelDirection();
        double angle;
        if (labelDirection == Association.FROM_A_TO_B) {
            angle = angleA;
        } else {
            angle = angleB;
        }
        if (name != null && !name.equals("")) {
            // draw the association name with arrow from role A to role B
            drawAssociationName(centerX, centerY, angle, name, association.getShowArrow(), g);
        }
    }

    protected void drawArrowHeadsReflective(Graphics2D g) {
        int direction = association.getDirection();
        if (direction == Association.AB || direction == Association.BIDIRECTIONAL_FIX) {
            drawArrowHead(getXB(), getYB(), Math.PI, g);
        }
        if (direction == Association.BA || direction == Association.BIDIRECTIONAL_FIX) {
            drawArrowHead(getXA(), getYA(), Math.PI / 2, g);
        }
    }

    @Override
    protected void drawArrowHead(int x, int y, double angle, Graphics2D g) {
        GraphicsHelper.drawSimpleArrowHead(x, y, angle, g);
    }

    private void drawAssociationName(int x, int y, double angle, String string, boolean arrow, Graphics2D g) {
        GraphicsHelper.drawString(string, x, y, angle, arrow, g);
    }

    private void drawRoleString(int x, int y, double angle, String multiplicity, String roleName, boolean up,
            Graphics2D g) {
        boolean sameDirection = true;

        if (GraphicsHelper.angleGreaterThanHalfPi(angle)) {
            angle -= Math.PI;
            sameDirection = false;
        }
        StringBuilder sb = new StringBuilder();
        if (sameDirection) {
            sb.append(multiplicity).append(" ").append(roleName);
        } else {
            sb.append(roleName).append(" ").append(multiplicity);
        }
        g.translate(x, y);
        g.rotate(angle);

        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = GraphicsHelper.getTextBounds(sb.toString(), ROLE_FONT, frc);
        int xOffset = (int) bounds.getX();
        int yOffset = (int) bounds.getY();
        int textWidth = (int) bounds.getWidth();

        if (sameDirection) {
            if (up) {
                g.drawString(sb.toString(), 12 + xOffset, -5);
            } else {
                g.drawString(sb.toString(), 12 + xOffset, 5 - yOffset);
            }
        } else {
            if (up) {
                g.drawString(sb.toString(), -12 - textWidth - xOffset, -5);
            } else {
                g.drawString(sb.toString(), -12 - textWidth - xOffset, 5 - yOffset);
            }
        }

        g.rotate(-angle);
        g.translate(-x, -y);
    }

    public boolean isReflective() {
        return association.isReflective();
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the rules.txt / file needs to be updated
     */
    public Association getAssociation() {
        return association;
    }

    /**
     * Protected setter to allow subclasses (like AggregationGR) to update the
     * association.
     */
    protected void setAssociation(Association association) {
        this.association = association;
    }

    @Override
    public boolean edit(EditContext context) {
        // Choose editor based on diagram type
        Association originalAssociation = getAssociation();
        Association editedAssociation = createAndRunEditor(context, originalAssociation);

        if (editedAssociation == null) {
            return true; // User cancelled
        }

        // Undo/Redo - capture original state
        Association undoAssociation = originalAssociation.clone();

        // Apply all changes atomically
        originalAssociation.setName(editedAssociation.getName());
        originalAssociation.setDirection(editedAssociation.getDirection());
        originalAssociation.setShowArrow(editedAssociation.getShowArrow());
        originalAssociation.setLabelDirection(editedAssociation.getLabelDirection());

        // Update roles
        Role roleA = originalAssociation.getRoleA();
        roleA.setName(editedAssociation.getRoleA().getName());
        roleA.setMultiplicity(editedAssociation.getRoleA().getMultiplicity());

        Role roleB = originalAssociation.getRoleB();
        roleB.setName(editedAssociation.getRoleB().getName());
        roleB.setMultiplicity(editedAssociation.getRoleB().getMultiplicity());

        // Undo/Redo
        UndoableEdit edit = new EditAssociationEdit(originalAssociation, undoAssociation, context.getModel());
        context.getParentComponent().getUndoSupport().postEdit(edit);

        // set observable model to changed in order to notify its views
        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Creates and runs the appropriate editor based on diagram type. Extracted into
     * a protected method to enable testing without UI dialogs (can be overridden).
     * 
     * @param context             the edit context
     * @param originalAssociation the association to edit
     * @return the edited association, or null if user cancelled
     */
    protected Association createAndRunEditor(EditContext context, Association originalAssociation) {
        if (context.getModel() instanceof CCDModel) {
            // Conceptual Class Diagram - use CCDAssociationEditor
            CCDAssociationEditor associationEditor = new CCDAssociationEditor();
            return associationEditor.editDialog(originalAssociation, context.getParentComponent());
        } else {
            // Design Class Diagram - use full AssociationEditor
            AssociationEditor associationEditor = new AssociationEditor();
            return associationEditor.editDialog(originalAssociation, context.getParentComponent());
        }
    }

    @JsonProperty("from")
    public ClassifierGR getClassA() {
        return a;
    }

    @JsonProperty("to")
    public ClassifierGR getClassB() {
        return b;
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute(XMLSyntax.CLASSA, SystemWideObjectNamePool.getInstance().getNameForObject(a));
        node.setAttribute(XMLSyntax.CLASSB, SystemWideObjectNamePool.getInstance().getNameForObject(b));

        streamer.streamObject(node, "association", association);
    }

    @Override
    public String toString() {
        return "" + a + " ---association---> " + b + " " + super.toString();
    }

    @Override
    public boolean canReconnect(EndpointType endpoint, GraphicalElement newElement) {
        // Must pass base validation
        if (!super.canReconnect(endpoint, newElement)) {
            return false;
        }

        // Associations require classifiers (classes, conceptual classes, or interfaces)
        if (!(newElement instanceof ClassifierGR)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean reconnectSource(ClassifierGR newSource) {
        // Note: LinkGR fields 'a' and 'b' are final, so we cannot update them directly.
        // The caller (SelectionController) must remove this link and create a new one.
        // Here we just prepare the domain model for the new link.

        // Create a new Role with the new classifier, preserving name and multiplicity
        Role oldRoleA = association.getRoleA();
        Role newRoleA = new Role(newSource.getClassifier());
        newRoleA.setName(oldRoleA.getName());
        newRoleA.setMultiplicity(oldRoleA.getMultiplicity());

        // Create a new association with the updated role
        Association newAssoc = new Association(newRoleA, association.getRoleB());
        newAssoc.setName(association.getName());
        newAssoc.setDirection(association.getDirection());
        newAssoc.setShowArrow(association.getShowArrow());
        newAssoc.setLabelDirection(association.getLabelDirection());

        this.association = newAssoc;

        logger.fine(() -> "Prepared association source reconnection to: " + newSource.getClassifier().getName());
        return true;
    }

    @Override
    public boolean reconnectTarget(ClassifierGR newTarget) {
        // Note: LinkGR fields 'a' and 'b' are final, so we cannot update them directly.
        // The caller (SelectionController) must remove this link and create a new one.
        // Here we just prepare the domain model for the new link.

        // Create a new Role with the new classifier, preserving name and multiplicity
        Role oldRoleB = association.getRoleB();
        Role newRoleB = new Role(newTarget.getClassifier());
        newRoleB.setName(oldRoleB.getName());
        newRoleB.setMultiplicity(oldRoleB.getMultiplicity());

        // Create a new association with the updated role
        Association newAssoc = new Association(association.getRoleA(), newRoleB);
        newAssoc.setName(association.getName());
        newAssoc.setDirection(association.getDirection());
        newAssoc.setShowArrow(association.getShowArrow());
        newAssoc.setLabelDirection(association.getLabelDirection());

        this.association = newAssoc;

        logger.fine(() -> "Prepared association target reconnection to: " + newTarget.getClassifier().getName());
        return true;
    }

    /**
     * Creates a new AssociationGR with updated endpoints. Used for reconnection
     * since LinkGR endpoints are final.
     * 
     * @param newA the new source classifier
     * @param newB the new target classifier
     * @return new AssociationGR with same domain model but new endpoints
     */
    public AssociationGR createWithNewEndpoints(ClassifierGR newA, ClassifierGR newB) {
        return new AssociationGR(newA, newB, this.association);
    }

    @Override
    public AssociationGR clone() {
        // IMPORTANT: Share the domain object reference (do NOT clone it)
        // Links connect graphical elements, so we reference the same endpoints
        ClassifierGR sameA = (ClassifierGR) a;
        ClassifierGR sameB = (ClassifierGR) b;
        Association sameAssociation = getAssociation();

        // Create new graphical wrapper referencing the SAME domain object and endpoints
        AssociationGR clonedGR = new AssociationGR(sameA, sameB, sameAssociation);

        return clonedGR;
    }

}
