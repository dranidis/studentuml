package edu.city.studentuml.model.graphical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.editing.ElementEditHelpers;
import edu.city.studentuml.model.domain.Classifier;
import edu.city.studentuml.model.repository.CentralRepository;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.IXMLCustomStreamable;
import edu.city.studentuml.util.NotStreamable;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.view.gui.StringEditorDialog;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "internalid")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
public abstract class GraphicalElement implements Serializable, IXMLCustomStreamable {

    private static final Logger logger = Logger.getLogger(GraphicalElement.class.getName());

    protected boolean selected = false;

    protected Point startingPoint;
    protected int width;
    protected int height;
    protected String myUid;
    public static final Color DESKTOP_USER_COLOR = new Color(220, 170, 100);

    @JsonIgnore
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    private String getMyUid() {
        if (myUid == null) {
            myUid = SystemWideObjectNamePool.getInstance().getUid();
        }
        return myUid;
    }

    @JsonGetter("internalid")
    public String getInternalid() {
        return SystemWideObjectNamePool.getInstance().getNameForObject(this);
    }

    public static Color lighter(Color sourceColor) {
        return sourceColor.equals(DESKTOP_USER_COLOR) ? new Color(255, 255, 205) : sourceColor.brighter();
    }

    public void objectAdded(GraphicalElement obj) {
    }

    public void objectRemoved(GraphicalElement obj) {
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public int getX() {
        return (int) getStartingPoint().getX();
    }

    public int getY() {
        return (int) getStartingPoint().getY();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Color getFillColor() {
        return Colors.getFillColor();
    }

    public Color getBackgroundColor() {
        return Colors.getBackgroundColor();
    }

    public Color getOutlineColor() {
        return Colors.getOutlineColor();
    }

    public Color getHighlightColor() {
        return Colors.getHighlightColor();
    }

    public Color getErrorColor() {
        return Colors.getErrorColor();
    }

    public void setSelected(boolean sel) {
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public abstract void draw(Graphics2D g);

    public abstract void move(int x, int y);

    public abstract boolean contains(Point2D p);

    /**
     * Creates a deep copy of this graphical element. The clone should include a
     * cloned domain object and copied visual properties, but should not copy
     * relationships to other elements or selection state.
     * 
     * @return a new GraphicalElement that is a copy of this instance
     */
    public abstract GraphicalElement clone();

    /**
     * Opens an editor dialog for this graphical element, allowing the user to
     * modify its properties. This method encapsulates the element-specific edit
     * logic that was previously scattered across selection controller subclasses.
     * <p>
     * The default implementation returns {@code false}, indicating that this
     * element type is not editable. Subclasses should override this method to
     * provide their specific editing behavior.
     * <p>
     * Typical implementation pattern:
     * <ol>
     * <li>Clone the domain object to enable undo/redo</li>
     * <li>Open an editor dialog using {@code context.getParentComponent()}</li>
     * <li>If the user confirms changes, apply them to the domain object</li>
     * <li>Create and add an appropriate UndoableEdit via
     * {@code context.getUndoSupport()}</li>
     * <li>Call {@code context.notifyModelChanged()} to trigger observer
     * updates</li>
     * <li>Return {@code true} to indicate successful edit</li>
     * </ol>
     * <p>
     * Example usage in a controller:
     * 
     * <pre>
     * EditContext context = new EditContext(model, repository, parentComponent, undoSupport);
     * if (element.edit(context)) {
     *     // Edit was successful and applied
     * } else {
     *     // Edit was cancelled or element is not editable
     * }
     * </pre>
     * 
     * @param context the edit context providing access to model, repository, parent
     *                component, and undo support
     * @return {@code true} if the element was successfully edited and changes were
     *         applied, {@code false} if the edit was cancelled or the element is
     *         not editable
     * @since 1.5.0
     */
    public boolean edit(EditContext context) {
        // Default implementation: element is not editable
        return false;
    }

    /**
     * Functional interface for a function that takes three parameters and returns a
     * result. Used to avoid introducing external dependencies for tri-arity
     * functional hooks in helper methods.
     */
    @FunctionalInterface
    protected interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    /**
     * Functional interface for a consumer that takes three parameters and returns
     * no result. Used for repository update hooks where both original and new
     * domain instances are required.
     */
    @FunctionalInterface
    protected interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    /**
     * Generic helper for editing a single string property via a dialog, with
     * undo/redo support and model change notification.
     * <p>
     * This method is type-agnostic and can be reused by any subclass that needs a
     * string edit workflow. Callers provide the domain object, accessors, a cloner,
     * and an {@link UndoableEdit} factory. Optional parameters support duplicate
     * checking.
     * <p>
     * Note: Repository synchronization is handled by the Edit classes during
     * undo/redo, not during the initial edit. The setter directly mutates the
     * domain object which is held by reference in CentralRepository, so no explicit
     * repository update is needed.
     *
     * @param <D>                   the type of the domain object being edited
     * @param context               edit context (model, parent component, undo
     *                              support)
     * @param dialogTitle           title of the dialog window
     * @param fieldLabel            label for the text field
     * @param domainObject          the domain object whose string property is being
     *                              edited (unmutated at call time)
     * @param getter                function to get the current string value from
     *                              the domain object
     * @param setter                consumer to set the new string value on the
     *                              domain object
     * @param cloner                function producing a clone of the domain object
     *                              for creating the new/target state
     * @param undoFactory           tri-function creating an {@link UndoableEdit}
     *                              from the original (unmutated) domain object, the
     *                              new/target state clone, and the
     *                              {@link DiagramModel}
     * @param duplicateExists       optional predicate that returns true if the
     *                              given value already exists (null to skip check)
     * @param duplicateErrorMessage optional message shown in an error dialog when a
     *                              duplicate is detected (null if no duplicate
     *                              check)
     * @return true if the edit was confirmed and applied; false if cancelled or
     *         blocked by conflict
     */
    protected <D> boolean editStringPropertyWithDialog(
            EditContext context,
            String dialogTitle,
            String fieldLabel,
            D domainObject,
            Function<D, String> getter,
            BiConsumer<D, String> setter,
            Function<D, D> cloner,
            TriFunction<D, D, DiagramModel, UndoableEdit> undoFactory,
            Predicate<String> duplicateExists,
            String duplicateErrorMessage) {
        String currentValue = getter.apply(domainObject);
        String newValue = requestStringValueFromUser(context, dialogTitle, fieldLabel, currentValue,
                duplicateExists,
                duplicateErrorMessage);

        if (newValue == null) {
            return false; // cancelled or blocked due to duplicate
        }

        if (Objects.equals(currentValue, newValue)) {
            return true; // No change, nothing to do
        }

        // Clone for new/target state (redo)
        D newDomainObject = cloner.apply(domainObject);

        // Apply new value to the clone
        setter.accept(newDomainObject, newValue);

        // Create and post undoable edit (original is still unmutated)
        UndoableEdit edit = undoFactory.apply(domainObject, newDomainObject, context.getModel());
        context.getUndoSupport().postEdit(edit);

        // Mutate the original for immediate effect
        // Note: CentralRepository holds the same object reference, so this mutation
        // is automatically visible to the repository. Edit classes handle explicit
        // repository synchronization during undo/redo operations.
        setter.accept(domainObject, newValue);

        // Notify observers and refresh name pool
        context.notifyModelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Protected hook method for requesting a string value from the user. This
     * method can be overridden in tests to inject mock values without showing
     * actual dialogs. Production code delegates to
     * ElementEditHelpers.requestStringValue.
     * 
     * @param context               edit context
     * @param dialogTitle           title of the dialog
     * @param fieldLabel            label for the input field
     * @param currentValue          current value to display
     * @param duplicateExists       predicate to check for duplicates (null to skip)
     * @param duplicateErrorMessage error message for duplicates (null to skip)
     * @return the new value from user, or null if cancelled
     */
    private String requestStringValueFromUser(
            EditContext context,
            String dialogTitle,
            String fieldLabel,
            String currentValue,
            Predicate<String> duplicateExists,
            String duplicateErrorMessage) {

        // Open dialog with current value
        StringEditorDialog dialog = createStringDialog(context, dialogTitle, fieldLabel, currentValue);

        if (!dialog.showDialog()) {
            return null; // user cancelled
        }

        String newValue = dialog.getText();

        // If unchanged, return the same value; callers will short-circuit
        if (Objects.equals(currentValue, newValue)) {
            return newValue;
        }

        // Check for duplicates when provided
        if (duplicateExists != null && duplicateExists.test(newValue)) {
            if (duplicateErrorMessage != null && !duplicateErrorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        duplicateErrorMessage,
                        "Cannot Edit", JOptionPane.ERROR_MESSAGE);
            }
            return null; // blocked due to duplicate
        }

        return newValue;
    }

    protected StringEditorDialog createStringDialog(EditContext context, String dialogTitle, String fieldLabel,
            String currentValue) {
        return new StringEditorDialog(
                context.getParentComponent(),
                dialogTitle,
                fieldLabel,
                currentValue);
    }

    /**
     * Shared dialog + validation flow for editing a single string value. Returns
     * the new value, or null if cancelled or blocked by duplicate policy.
     */
    // Shared dialog + validation flow moved to ElementEditHelpers

    /**
     * Generic helper for editing a classifier (class/interface) with name conflict
     * resolution (Pattern 2: Silent Merge on Conflict) and undo/redo support.
     * <p>
     * This method implements the classifier edit workflow used by ClassGR,
     * ConceptualClassGR, and InterfaceGR. Callers provide the domain object,
     * accessors, and hook functions for classifier-specific operations.
     * <p>
     * Algorithm:
     * <ol>
     * <li>Show editor dialog to user via provided editor function</li>
     * <li>If user cancels, return false</li>
     * <li>If name changed AND new name exists in repository AND new name not blank:
     * <ul>
     * <li>Replace this graphical element's reference with existing classifier
     * (silent merge)</li>
     * <li>Remove original classifier if it had blank name</li>
     * </ul>
     * </li>
     * <li>Else (no conflict):
     * <ul>
     * <li>Create UndoableEdit for undo/redo support</li>
     * <li>Update repository with edited classifier</li>
     * <li>Post undo edit</li>
     * </ul>
     * </li>
     * <li>Notify model changed and reload name pool</li>
     * <li>Return true</li>
     * </ol>
     *
     * @param <C>                   the type of the classifier domain object being
     *                              edited
     * @param context               edit context (model, repository, parent
     *                              component, undo support)
     * @param getClassifierFromThis function to get the current classifier from this
     *                              graphical element
     * @param setClassifierInThis   consumer to set the classifier reference in this
     *                              graphical element
     * @param createAndShowEditor   function that creates an editor and shows the
     *                              dialog, returning the edited classifier (or null
     *                              if cancelled)
     * @param getClassifierByName   function to lookup a classifier in the
     *                              repository by name
     * @param removeFromRepository  consumer to remove a classifier from the
     *                              repository
     * @param editInRepository      tri-consumer to update a classifier in the
     *                              repository (original, edited, repository)
     * @param createUndoableEdit    tri-function to create an UndoableEdit from
     *                              original, edited, and model
     * @return true if the edit was successful, false if user cancelled
     * @since 1.5.0
     */
    protected <C extends Classifier> boolean editClassifierWithDialog(
            EditContext context,
            java.util.function.Supplier<C> getClassifierFromThis,
            java.util.function.Consumer<C> setClassifierInThis,
            java.util.function.BiFunction<C, java.awt.Component, C> createAndShowEditor,
            java.util.function.Function<String, C> getClassifierByName,
            java.util.function.Consumer<C> removeFromRepository,
            TriConsumer<CentralRepository, C, C> editInRepository,
            TriFunction<C, C, DiagramModel, UndoableEdit> createUndoableEdit) {

        CentralRepository repository = context.getRepository();
        C originalClassifier = getClassifierFromThis.get();

        // Show the editor dialog and check whether the user has pressed cancel
        C newClassifier = createAndShowEditor.apply(originalClassifier, context.getParentComponent());
        if (newClassifier == null) {
            return false; // User cancelled
        }

        // Pattern 2: Name conflict handling - DCD/CCD variant (silent merge)
        // Edit the classifier if there is no change in the name,
        // or if there is a change in the name but the new name doesn't bring any
        // conflict
        // or if the new name is blank
        String newClassName = newClassifier.getName();
        String oldClassName = originalClassifier.getName();
        if (!newClassName.equals("")
                && !oldClassName.equals(newClassName)
                && getClassifierByName.apply(newClassName) != null) {
            // Name conflict: replace this graphical element's reference with the existing
            // classifier
            setClassifierInThis.accept(getClassifierByName.apply(newClassName));

            // Remove the existing classifier if it has no name
            if (oldClassName.equals("")) {
                removeFromRepository.accept(originalClassifier);
            }
        } else {
            // No conflict: normal edit with undo/redo
            UndoableEdit edit = createUndoableEdit.apply(originalClassifier, newClassifier, context.getModel());
            editInRepository.accept(repository, originalClassifier, newClassifier);
            context.getUndoSupport().postEdit(edit);
        }

        // Set observable model to changed in order to notify its views
        context.getModel().modelChanged();
        SystemWideObjectNamePool.getInstance().reload();

        return true;
    }

    /**
     * Functional interface with four arguments.
     */
    @FunctionalInterface
    protected interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    public boolean containedInArea(int x, int y, int toX, int toY) {
        Rectangle2D b = getBounds();
        int minx = (int) b.getMinX();
        int miny = (int) b.getMinY();
        int maxx = (int) b.getMaxX();
        int maxy = (int) b.getMaxY();
        return minx > x && miny > y && maxx < toX && maxy < toY;
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance) throws NotStreamable {
        String uid = node.getAttribute("uid");

        if (uid != null && uid.equals("")) {
            uid = SystemWideObjectNamePool.getInstance().getUid();
        }

        ((GraphicalElement) instance).myUid = uid;

        logger.finer(() -> "Streaming from " + instance.getClass().getName() + " " + instance.equals(this));
    }

    public void streamToXML(Element node, XMLStreamer streamer) {
        logger.finer(() -> "Streaming to " + this.getClass().getName());
        node.setAttribute("uid", this.getMyUid());
    }

    public String toString() {
        return "[" + getX() + ", " + getY() + "][" + getWidth() + ", " + getHeight() + "]";
    }
}
