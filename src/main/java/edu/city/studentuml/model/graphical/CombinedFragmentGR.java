package edu.city.studentuml.model.graphical;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.w3c.dom.Element;

import edu.city.studentuml.editing.EditContext;
import edu.city.studentuml.model.domain.CombinedFragment;
import edu.city.studentuml.model.domain.InteractionOperator;
import edu.city.studentuml.model.domain.Operand;
import edu.city.studentuml.util.SystemWideObjectNamePool;
import edu.city.studentuml.util.XMLStreamer;
import edu.city.studentuml.util.undoredo.EditCombinedFragmentEdit;
import edu.city.studentuml.view.gui.CombinedFragmentEditor;

/**
 * Graphical representation of a UML 2.x Combined Fragment. Renders a rounded
 * rectangle covering multiple lifelines with: - Pentagon operator label in
 * upper left corner - Guard condition text (for OPT and operands in ALT/LOOP) -
 * Dashed lines separating operands (for ALT) This is a minimal Phase 1
 * implementation supporting the OPT operator.
 * 
 * @author dimitris
 */
public class CombinedFragmentGR extends GraphicalElement implements Resizable {

    /**
     * The domain model combined fragment
     */
    private CombinedFragment combinedFragment;

    /**
     * Pentagon label dimensions
     */
    private static final int PENTAGON_WIDTH = 50;
    private static final int PENTAGON_HEIGHT = 20;
    private static final int ARC_SIZE = 10; // For rounded corners

    /**
     * Minimum dimensions for the fragment
     */
    private static final int MINIMUM_WIDTH = 100;
    private static final int MINIMUM_HEIGHT = 40;

    /**
     * Default width for combined fragments (can span multiple lifelines)
     */
    public static final int DEFAULT_WIDTH = 300;
    /**
     * Padding around messages when auto-resizing
     */
    private static final int MESSAGE_PADDING = 20;

    /**
     * Font for operator label and guard condition
     */
    private static final Font OPERATOR_FONT = new Font("SansSerif", Font.BOLD, 10);
    private static final Font GUARD_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * Resize handles for corner dragging
     */
    private List<ResizeHandle> resizeHandles;

    /**
     * Index of the separator being dragged (-1 if none)
     */
    private int draggingSeparatorIndex = -1;

    /**
     * Y offset when dragging separator started
     */
    private int dragStartY;

    /**
     * Original height ratios before dragging
     */
    private double[] originalHeightRatios;

    /**
     * Tolerance for separator hit detection (pixels)
     */
    private static final int SEPARATOR_HIT_TOLERANCE = 5;

    /**
     * Creates a new CombinedFragmentGR.
     * 
     * @param fragment      the domain model combined fragment
     * @param startingPoint the top-left corner position
     * @param width         the width (should span multiple lifelines)
     */
    public CombinedFragmentGR(CombinedFragment fragment, Point startingPoint, int width) {
        this.combinedFragment = fragment;
        this.startingPoint = startingPoint;
        this.width = Math.max(width, MINIMUM_WIDTH);

        // Handle null fragment case (can happen during XML deserialization)
        int fragmentHeight = (fragment != null) ? fragment.getHeight() : MINIMUM_HEIGHT;
        this.height = Math.max(fragmentHeight, MINIMUM_HEIGHT);

        // Initialize resize handles
        resizeHandles = new ArrayList<>();
        resizeHandles.add(new UpResizeHandle(this));
        resizeHandles.add(new DownResizeHandle(this));
        resizeHandles.add(new LeftResizeHandle(this));
        resizeHandles.add(new RightResizeHandle(this));
    }

    /**
     * Returns the domain model combined fragment.
     * 
     * @return the combined fragment
     */
    public CombinedFragment getCombinedFragment() {
        return combinedFragment;
    }

    @Override
    public void setWidth(int width) {
        this.width = Math.max(width, MINIMUM_WIDTH);
    }

    @Override
    public void setHeight(int height) {
        this.height = Math.max(height, MINIMUM_HEIGHT);
        // Sync with domain model (if it exists)
        if (combinedFragment != null) {
            combinedFragment.setHeight(this.height);
        }
    }

    @Override
    public void move(int x, int y) {
        // Ensure fragment never goes into negative coordinates
        startingPoint.setLocation(Math.max(0, x), Math.max(0, y));
    }

    @Override
    public boolean contains(Point2D p) {
        // First check if clicking on a resize handle (takes priority)
        // This ensures resize handles remain interactive even with limited selection area
        if (isSelected() && isResizeHandleSelected((int) p.getX(), (int) p.getY())) {
            return true;
        }

        // Only allow selection by clicking on the pentagon operator label
        // This allows messages inside the fragment to be selected
        int x = getX();
        int y = getY();

        // Check if point is within the pentagon label area
        // Pentagon is approximately PENTAGON_WIDTH x PENTAGON_HEIGHT
        return p.getX() >= x && p.getX() <= x + PENTAGON_WIDTH &&
                p.getY() >= y && p.getY() <= y + PENTAGON_HEIGHT;
    }

    @Override
    public GraphicalElement clone() {
        // Create a new domain fragment (shallow copy for now)
        CombinedFragment clonedFragment = new CombinedFragment(
                combinedFragment.getOperator(),
                combinedFragment.getGuardCondition());
        clonedFragment.setHeight(combinedFragment.getHeight());
        clonedFragment.setLoopMin(combinedFragment.getLoopMin());
        clonedFragment.setLoopMax(combinedFragment.getLoopMax());

        // Create new graphical element
        return new CombinedFragmentGR(clonedFragment, new Point(startingPoint), width);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the main rounded rectangle
        RoundRectangle2D fragmentBox = new RoundRectangle2D.Double(
                getX(), getY(), getWidth(), getHeight(), ARC_SIZE, ARC_SIZE);

        // Transparent background - only draw border
        // (No fill call means transparent background)

        // Draw border using message stroke color (outline color)
        if (isSelected()) {
            g.setStroke(new BasicStroke(2.0f));
            g.setColor(getHighlightColor());
        } else {
            g.setStroke(new BasicStroke(1.0f));
            g.setColor(getOutlineColor()); // Use message stroke color
        }
        g.draw(fragmentBox);

        // Draw pentagon operator label
        drawPentagonLabel(g);

        // Draw guard condition (for OPT/LOOP) or operand separators (for ALT)
        if (combinedFragment.getOperator() == InteractionOperator.ALT && !combinedFragment.getOperands().isEmpty()) {
            drawAltOperandSeparators(g);
        } else {
            drawGuardCondition(g);
        }

        // Draw resize handles if selected
        if (isSelected()) {
            resizeHandles.forEach(handle -> handle.draw(g));
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    /**
     * Draws the pentagon operator label in the upper left corner.
     */
    private void drawPentagonLabel(Graphics2D g) {
        int x = getX();
        int y = getY();

        // Create pentagon shape (simple rectangle with angled right side for now)
        int[] xPoints = { x, x + PENTAGON_WIDTH, x + PENTAGON_WIDTH, x + PENTAGON_WIDTH - 10, x };
        int[] yPoints = { y, y, y + PENTAGON_HEIGHT - 10, y + PENTAGON_HEIGHT, y + PENTAGON_HEIGHT };

        // Fill pentagon background using object fill color
        Paint originalPaint = g.getPaint();
        g.setPaint(getFillColor()); // Use object fill color
        g.fillPolygon(xPoints, yPoints, 5);
        g.setPaint(originalPaint);

        // Draw pentagon border using message stroke color
        g.setColor(getOutlineColor()); // Use message stroke color
        g.setStroke(new BasicStroke(1.0f));
        g.drawPolygon(xPoints, yPoints, 5);

        // Draw operator text with loop iterations if applicable
        // e.g., "opt", "alt", "loop", "loop(3)", "loop(0,*)"
        g.setFont(OPERATOR_FONT);
        g.setColor(getOutlineColor()); // Use message stroke color for text
        String operatorText = combinedFragment.getOperator().getDisplayName();
        String loopIterations = combinedFragment.getLoopIterationsString();
        String fullText = operatorText + loopIterations;
        g.drawString(fullText, x + 5, y + 14);
    }

    /**
     * Draws the guard condition below the pentagon label.
     */
    private void drawGuardCondition(Graphics2D g) {
        String guard = combinedFragment.getGuardCondition();
        if (guard != null && !guard.isEmpty()) {
            g.setFont(GUARD_FONT);
            g.setColor(getOutlineColor()); // Use message stroke color for text

            // Position below the pentagon
            int x = getX() + 5;
            int y = getY() + PENTAGON_HEIGHT + 15;

            g.drawString(guard, x, y);
        }
    }

    /**
     * Draws the dashed separator lines and guard conditions for ALT operands.
     * Divides the fragment height based on operand height ratios.
     */
    private void drawAltOperandSeparators(Graphics2D g) {
        List<Operand> operands = combinedFragment.getOperands();
        if (operands.isEmpty()) {
            return;
        }

        g.setFont(GUARD_FONT);
        g.setColor(getOutlineColor());

        int x = getX();
        int width = getWidth();
        int totalHeight = getHeight();

        // Calculate total ratio
        double totalRatio = 0;
        for (Operand operand : operands) {
            totalRatio += operand.getHeightRatio();
        }

        // Draw separator lines and guard conditions for each operand
        int currentY = getY();
        for (int i = 0; i < operands.size(); i++) {
            Operand operand = operands.get(i);

            // Calculate this operand's height based on its ratio
            int operandHeight;
            if (i == operands.size() - 1) {
                // Last operand gets remaining height to avoid rounding errors
                operandHeight = getY() + totalHeight - currentY;
            } else {
                operandHeight = (int) Math.round(operand.getHeightRatio() / totalRatio * totalHeight);
            }

            // Draw guard condition at the top-left of this operand
            String guard = operand.getGuardCondition();
            if (guard != null && !guard.isEmpty()) {
                int guardX = x + 5;
                int guardY = currentY + PENTAGON_HEIGHT + 15;
                g.drawString(guard, guardX, guardY);
            }

            // Draw dashed separator line (except before the first operand)
            if (i > 0) {
                // Create dashed stroke
                float[] dashPattern = { 5.0f, 5.0f }; // 5 pixels on, 5 pixels off
                BasicStroke dashedStroke = new BasicStroke(
                        1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f,
                        dashPattern,
                        0.0f);
                Stroke oldStroke = g.getStroke();
                g.setStroke(dashedStroke);
                g.drawLine(x, currentY, x + width, currentY);
                g.setStroke(oldStroke);
            }

            currentY += operandHeight;
        }
    }

    /**
     * Checks if a point is near a separator line (for ALT fragments).
     * 
     * @param p the point to check
     * @return the index of the separator (0-based, separator after operand i), or
     *         -1 if no hit
     */
    public int getSeparatorIndexAt(Point p) {
        if (combinedFragment.getOperator() != InteractionOperator.ALT) {
            return -1;
        }

        List<Operand> operands = combinedFragment.getOperands();
        if (operands.size() < 2) {
            return -1; // Need at least 2 operands to have separators
        }

        // Check if point is within horizontal bounds
        if (p.x < getX() || p.x > getX() + getWidth()) {
            return -1;
        }

        // Calculate separator Y positions
        int totalHeight = getHeight();
        double totalRatio = 0;
        for (Operand operand : operands) {
            totalRatio += operand.getHeightRatio();
        }

        int currentY = getY();
        for (int i = 0; i < operands.size() - 1; i++) {
            Operand operand = operands.get(i);
            int operandHeight = (int) Math.round(operand.getHeightRatio() / totalRatio * totalHeight);
            currentY += operandHeight;

            // Check if point is near this separator
            if (Math.abs(p.y - currentY) <= SEPARATOR_HIT_TOLERANCE) {
                return i; // Return separator index (separator after operand i)
            }
        }

        return -1;
    }

    /**
     * Starts dragging a separator.
     * 
     * @param separatorIndex the index of the separator to drag
     * @param startY         the starting Y position
     */
    public void startDraggingSeparator(int separatorIndex, int startY) {
        List<Operand> operands = combinedFragment.getOperands();
        if (separatorIndex < 0 || separatorIndex >= operands.size() - 1) {
            return;
        }

        draggingSeparatorIndex = separatorIndex;
        dragStartY = startY;

        // Store original height ratios
        originalHeightRatios = new double[operands.size()];
        for (int i = 0; i < operands.size(); i++) {
            originalHeightRatios[i] = operands.get(i).getHeightRatio();
        }
    }

    /**
     * Updates separator position during dragging.
     * 
     * @param currentY the current Y position
     */
    public void dragSeparator(int currentY) {
        if (draggingSeparatorIndex < 0 || originalHeightRatios == null) {
            return;
        }

        List<Operand> operands = combinedFragment.getOperands();
        if (draggingSeparatorIndex >= operands.size() - 1) {
            return;
        }

        // Calculate delta in pixels
        int deltaY = currentY - dragStartY;

        // Convert to ratio change
        int totalHeight = getHeight();
        if (totalHeight == 0)
            return;

        double totalRatio = 0;
        for (double ratio : originalHeightRatios) {
            totalRatio += ratio;
        }

        double deltaRatio = deltaY * totalRatio / totalHeight;

        // Adjust the two operands around the separator
        int i = draggingSeparatorIndex;
        double newRatio1 = originalHeightRatios[i] + deltaRatio;
        double newRatio2 = originalHeightRatios[i + 1] - deltaRatio;

        // Enforce minimum heights based on MINIMUM_HEIGHT constant
        // Calculate what the actual pixel heights would be for each operand
        double minRatio = MINIMUM_HEIGHT * totalRatio / totalHeight;

        if (newRatio1 < minRatio || newRatio2 < minRatio) {
            return; // Don't allow dragging beyond minimum height
        }

        // Apply new ratios
        operands.get(i).setHeightRatio(newRatio1);
        operands.get(i + 1).setHeightRatio(newRatio2);
    }

    /**
     * Finishes dragging a separator.
     */
    public void finishDraggingSeparator() {
        draggingSeparatorIndex = -1;
        originalHeightRatios = null;
    }

    /**
     * Returns true if currently dragging a separator.
     */
    public boolean isDraggingSeparator() {
        return draggingSeparatorIndex >= 0;
    }

    @Override
    public boolean edit(EditContext context) {
        // Store old values for undo/redo (BEFORE showing dialog)
        InteractionOperator oldOperator = combinedFragment.getOperator();
        String oldGuard = combinedFragment.getGuardCondition();
        Integer oldLoopMin = combinedFragment.getLoopMin();
        Integer oldLoopMax = combinedFragment.getLoopMax();

        // Deep copy old operands (preserve guards and height ratios)
        List<Operand> oldOperands = new ArrayList<>();
        for (Operand operand : combinedFragment.getOperands()) {
            oldOperands.add(Operand.copy(operand));
        }

        // Show editor dialog
        CombinedFragmentEditor editor = new CombinedFragmentEditor(
                context.getParentComponent(), combinedFragment);

        if (!editor.showDialog()) {
            // User cancelled - no changes
            return false;
        }

        // Get new values after edit
        InteractionOperator newOperator = combinedFragment.getOperator();
        String newGuard = combinedFragment.getGuardCondition();
        Integer newLoopMin = combinedFragment.getLoopMin();
        Integer newLoopMax = combinedFragment.getLoopMax();

        // Deep copy new operands (preserve guards and height ratios)
        List<Operand> newOperands = new ArrayList<>();
        for (Operand operand : combinedFragment.getOperands()) {
            newOperands.add(Operand.copy(operand));
        }

        // Check if anything changed (including operands)
        boolean operandsChanged = !operandsEqual(oldOperands, newOperands);
        boolean changed = !oldOperator.equals(newOperator) ||
                !oldGuard.equals(newGuard) ||
                !java.util.Objects.equals(oldLoopMin, newLoopMin) ||
                !java.util.Objects.equals(oldLoopMax, newLoopMax) ||
                operandsChanged;

        if (!changed) {
            // No changes made
            return false;
        }

        // Create undoable edit with both old and new operands
        EditCombinedFragmentEdit edit = new EditCombinedFragmentEdit(
                combinedFragment,
                oldOperator, oldGuard, oldLoopMin, oldLoopMax, oldOperands,
                newOperator, newGuard, newLoopMin, newLoopMax, newOperands,
                context.getModel());

        // Add to undo support
        context.getUndoSupport().postEdit(edit);

        // Notify model of changes
        context.notifyModelChanged();

        return true;
    }

    /**
     * Checks if two operand lists are equal by comparing size, guards, and height
     * ratios.
     */
    private boolean operandsEqual(List<Operand> list1, List<Operand> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            Operand op1 = list1.get(i);
            Operand op2 = list2.get(i);

            if (!op1.getGuardCondition().equals(op2.getGuardCondition())) {
                return false;
            }

            if (Math.abs(op1.getHeightRatio() - op2.getHeightRatio()) > 0.0001) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void streamToXML(Element node, XMLStreamer streamer) {
        super.streamToXML(node, streamer);

        node.setAttribute("x", String.valueOf(getX()));
        node.setAttribute("y", String.valueOf(getY()));
        node.setAttribute("width", String.valueOf(getWidth()));
        node.setAttribute("height", String.valueOf(getHeight()));

        // Save domain object as child element
        streamer.streamObject(node, "fragment", combinedFragment);

        // Reference to domain fragment (for lookup during deserialization)
        String fragmentRef = SystemWideObjectNamePool.getInstance().getNameForObject(combinedFragment);
        node.setAttribute("fragmentRef", fragmentRef);
    }

    @Override
    public void streamFromXML(Element node, XMLStreamer streamer, Object instance)
            throws edu.city.studentuml.util.NotStreamable {
        super.streamFromXML(node, streamer, instance);

        startingPoint = new Point(
                Integer.parseInt(node.getAttribute("x")),
                Integer.parseInt(node.getAttribute("y")));
        width = Integer.parseInt(node.getAttribute("width"));
        height = Integer.parseInt(node.getAttribute("height"));

        // Resolve fragment reference (will be handled after all objects loaded)
        String fragmentRef = node.getAttribute("fragmentRef");
        if (fragmentRef != null && !fragmentRef.isEmpty()) {
            combinedFragment = (CombinedFragment) SystemWideObjectNamePool.getInstance().getObjectByName(fragmentRef);
        }

        // Reinitialize resize handles after loading
        resizeHandles = new ArrayList<>();
        resizeHandles.add(new UpResizeHandle(this));
        resizeHandles.add(new DownResizeHandle(this));
        resizeHandles.add(new LeftResizeHandle(this));
        resizeHandles.add(new RightResizeHandle(this));
    }

    // Resizable interface implementation

    @Override
    public boolean isResizeHandleSelected(int x, int y) {
        return resizeHandles.stream().anyMatch(handle -> handle.contains(new Point2D.Double(x, y)));
    }

    @Override
    public ResizeHandle getResizeHandle(int x, int y) {
        Optional<ResizeHandle> resizeHandle = resizeHandles.stream()
                .filter(handle -> handle.contains(new Point2D.Double(x, y))).findFirst();
        if (resizeHandle.isPresent()) {
            return resizeHandle.get();
        }
        return null;
    }

    @Override
    public void setStartingPoint(Point point) {
        // Ensure fragment never goes into negative coordinates
        startingPoint.setLocation(Math.max(0, point.x), Math.max(0, point.y));
    }

    @Override
    public int getLeftBorder() {
        // Return the leftmost X position where the left edge can be
        // This is: (right edge) - (minimum width)
        // Right edge is: startingPoint.x + width
        // So left border is: (startingPoint.x + width) - MINIMUM_WIDTH
        return startingPoint.x + width - MINIMUM_WIDTH;
    }

    @Override
    public int getRightBorder() {
        // Return the rightmost X position where the right edge can be
        // This is: (left edge) + (minimum width)
        return startingPoint.x + MINIMUM_WIDTH;
    }

    @Override
    public int getTopBorder() {
        // Return the topmost Y position where the top edge can be
        // This is: (bottom edge) - (minimum height)
        // Bottom edge is: startingPoint.y + height
        return startingPoint.y + height - MINIMUM_HEIGHT;
    }

    @Override
    public int getBottomBorder() {
        return startingPoint.y + MINIMUM_HEIGHT;
    }

    @Override
    public boolean hasResizableContext() {
        return false; // Combined fragments don't have a resizable container
    }

    @Override
    public Resizable getResizableContext() {
        return null;
    }

    @Override
    public boolean contains(Resizable resizableElement) {
        // Combined fragments don't contain other resizable elements
        return false;
    }

    /**
     * Automatically resizes the fragment to span all messages within its Y range.
     * This method ONLY EXPANDS the fragment, never shrinks it. This method: 1.
     * Finds all messages whose Y position falls within the fragment's Y range 2.
     * Calculates the leftmost and rightmost message X coordinates 3. Includes any
     * objects created within the fragment's Y range 4. Expands the fragment
     * left/right if messages or objects extend beyond current bounds If no messages
     * are found within the Y range, keeps current dimensions.
     * 
     * @param model the sequence diagram model containing the messages
     */
    public void autoResizeToMessages(AbstractSDModel model) {
        autoResizeToMessages(model, false);
    }

    /**
     * Automatically resizes the fragment to span all messages and created objects
     * within its Y range.
     * 
     * @param model       the sequence diagram model containing the messages
     * @param allowShrink if true, allows shrinking the fragment; if false, only
     *                    expands
     */
    public void autoResizeToMessages(AbstractSDModel model, boolean allowShrink) {
        if (model == null) {
            return;
        }

        // Get fragment's Y range
        int fragmentTop = getY();
        int fragmentBottom = getY() + getHeight();

        // Find all messages within the fragment's Y range
        List<SDMessageGR> containedMessages = new ArrayList<>();
        for (SDMessageGR message : model.getMessages()) {
            int messageY = message.getY();
            if (messageY >= fragmentTop && messageY <= fragmentBottom) {
                containedMessages.add(message);
            }
        }

        // If fragment contains a call message, also include its return message
        // This ensures logical message pairs stay together
        Set<SDMessageGR> additionalMessages = new HashSet<>();
        for (SDMessageGR message : containedMessages) {
            if (message instanceof CallMessageGR) {
                ReturnMessageGR returnMsg = findReturnMessageFor((CallMessageGR) message, model);
                if (returnMsg != null) {
                    additionalMessages.add(returnMsg);
                }
            }
        }
        containedMessages.addAll(additionalMessages);

        // If no messages, keep current dimensions
        if (containedMessages.isEmpty()) {
            return;
        }

        // Check if this fragment contains other fragments (nested fragments)
        // If so, we'll add extra horizontal padding
        boolean containsNestedFragments = false;
        for (GraphicalElement element : model.getGraphicalElements()) {
            if (element instanceof CombinedFragmentGR && element != this) {
                CombinedFragmentGR otherFragment = (CombinedFragmentGR) element;
                // Check if the other fragment is within this fragment's Y range
                int otherTop = otherFragment.getY();
                int otherBottom = otherFragment.getY() + otherFragment.getHeight();

                if (otherTop >= fragmentTop && otherBottom <= fragmentBottom) {
                    containsNestedFragments = true;
                    break;
                }
            }
        }

        // Use larger padding for nested fragments to show visual hierarchy
        final int NESTED_FRAGMENT_PADDING = 30;
        int horizontalPadding = containsNestedFragments ? NESTED_FRAGMENT_PADDING : MESSAGE_PADDING;

        // Find leftmost, rightmost X coordinates and bottommost Y coordinate of messages
        int leftmostX = Integer.MAX_VALUE;
        int rightmostX = Integer.MIN_VALUE;
        int bottommostY = Integer.MIN_VALUE;

        for (SDMessageGR message : containedMessages) {
            int startX = message.getStartingX();
            int endX = message.getEndingX();
            int messageY = message.getY();

            leftmostX = Math.min(leftmostX, Math.min(startX, endX));
            rightmostX = Math.max(rightmostX, Math.max(startX, endX));
            bottommostY = Math.max(bottommostY, messageY);

            // If this is a CreateMessage, also include the target object's bounds
            if (message instanceof CreateMessageGR) {
                CreateMessageGR createMsg = (CreateMessageGR) message;
                RoleClassifierGR target = createMsg.getTarget();
                if (target != null) {
                    int objectLeft = target.getX();
                    int objectRight = target.getX() + target.getWidth();

                    leftmostX = Math.min(leftmostX, objectLeft);
                    rightmostX = Math.max(rightmostX, objectRight);
                }
            }
        }

        // Also include nested fragments in the bounds calculation
        if (containsNestedFragments) {
            for (GraphicalElement element : model.getGraphicalElements()) {
                if (element instanceof CombinedFragmentGR && element != this) {
                    CombinedFragmentGR otherFragment = (CombinedFragmentGR) element;
                    int otherTop = otherFragment.getY();
                    int otherBottom = otherFragment.getY() + otherFragment.getHeight();

                    // If nested within this fragment's Y range
                    if (otherTop >= fragmentTop && otherBottom <= fragmentBottom) {
                        int nestedLeft = otherFragment.getX();
                        int nestedRight = otherFragment.getX() + otherFragment.getWidth();
                        int nestedBottom = otherFragment.getY() + otherFragment.getHeight();

                        leftmostX = Math.min(leftmostX, nestedLeft);
                        rightmostX = Math.max(rightmostX, nestedRight);
                        bottommostY = Math.max(bottommostY, nestedBottom);
                    }
                }
            }
        }

        // Calculate required bounds with padding (use larger padding for nested fragments)
        int requiredLeft = leftmostX - horizontalPadding;
        int requiredRight = rightmostX + horizontalPadding;
        int requiredBottom = bottommostY + MESSAGE_PADDING;

        if (allowShrink) {
            // Allow both shrinking and expanding - set exact size
            int newWidth = rightmostX - leftmostX + (2 * horizontalPadding);
            newWidth = Math.max(newWidth, MINIMUM_WIDTH);

            int newHeight = requiredBottom - fragmentTop;
            newHeight = Math.max(newHeight, MINIMUM_HEIGHT);

            startingPoint.x = requiredLeft;
            this.width = newWidth;
            this.height = newHeight;

            // Update domain model height
            if (combinedFragment != null) {
                combinedFragment.setHeight(newHeight);
            }
        } else {
            // ONLY EXPAND, NEVER SHRINK (for user interactions)
            // Current fragment bounds
            int currentLeft = getX();
            int currentRight = getX() + getWidth();
            int currentBottom = getY() + getHeight();

            // Expand left if messages extend beyond left edge
            if (requiredLeft < currentLeft) {
                int widthIncrease = currentLeft - requiredLeft;
                startingPoint.x = requiredLeft;
                this.width += widthIncrease;
            }

            // Expand right if messages extend beyond right edge
            if (requiredRight > currentRight) {
                int widthIncrease = requiredRight - currentRight;
                this.width += widthIncrease;
            }

            // Expand downward if messages extend beyond bottom edge
            if (requiredBottom > currentBottom) {
                int heightIncrease = requiredBottom - currentBottom;
                this.height += heightIncrease;

                // Update domain model height
                if (combinedFragment != null) {
                    combinedFragment.setHeight(this.height);
                }
            }

            // Ensure minimum dimensions
            if (this.width < MINIMUM_WIDTH) {
                this.width = MINIMUM_WIDTH;
            }
            if (this.height < MINIMUM_HEIGHT) {
                this.height = MINIMUM_HEIGHT;
            }
        }
    }

    /**
     * Finds the return message that corresponds to a given call message. The return
     * message should have reversed endpoints (call: A->B, return: B->A) and be the
     * first return message after the call (to handle multiple calls between same
     * objects).
     * 
     * @param callMessage the call message to find the return for
     * @param model       the sequence diagram model
     * @return the corresponding return message, or null if not found
     */
    private ReturnMessageGR findReturnMessageFor(CallMessageGR callMessage, AbstractSDModel model) {
        for (SDMessageGR message : model.getMessages()) {
            if (message instanceof ReturnMessageGR) {
                ReturnMessageGR returnMsg = (ReturnMessageGR) message;

                // Check if this return message corresponds to the call message
                // Return message should have reversed endpoints:
                // Call: A -> B, Return: B -> A
                if (returnMsg.getSource() == callMessage.getTarget() &&
                        returnMsg.getTarget() == callMessage.getSource() &&
                        returnMsg.getY() > callMessage.getY()) {

                    // Also check that it's the first return message after this call
                    // (to handle multiple calls between same objects)
                    boolean isImmediate = true;
                    for (SDMessageGR other : model.getMessages()) {
                        if (other instanceof ReturnMessageGR && other != returnMsg) {
                            ReturnMessageGR otherReturn = (ReturnMessageGR) other;
                            if (otherReturn.getY() > callMessage.getY() &&
                                    otherReturn.getY() < returnMsg.getY() &&
                                    otherReturn.getSource() == returnMsg.getSource() &&
                                    otherReturn.getTarget() == returnMsg.getTarget()) {
                                isImmediate = false;
                                break;
                            }
                        }
                    }

                    if (isImmediate) {
                        return returnMsg;
                    }
                }
            }
        }

        return null;
    }
}
