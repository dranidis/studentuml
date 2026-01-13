package edu.city.studentuml.view;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.swing.JPanel;

import edu.city.studentuml.controller.SelectionController;
import edu.city.studentuml.model.graphical.AbstractLinkGR;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.EndpointType;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.LinkGR;
import edu.city.studentuml.model.graphical.SDMessageGR;
import edu.city.studentuml.model.graphical.UMLNoteGR;
import edu.city.studentuml.util.Colors;
import edu.city.studentuml.util.Theme;

public abstract class DiagramView extends JPanel implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(DiagramView.class.getName());

    public static final double MAX_SCALE = 10.0; // 1000%;
    public static final double MIN_SCALE = 0.1; // 10%

    protected DiagramModel model;
    protected transient Line2D dragLine = new Line2D.Double(0, 0, 0, 0);
    protected transient Rectangle2D dragRectangle = new Rectangle2D.Double(0, 0, 0, 0);

    private double scale = 1.0;

    // Inline message editor for quick text editing on canvas
    private InlineMessageEditor inlineMessageEditor;

    /**
     * Necessary for remembering the necessary max width and height of the view area
     * in order not to unnecessarily update the size of the panel. Unfortunately
     * getSize() of the panel does not serve because the size changes outside of the
     * program and the values almost never match.
     */
    private int maxWidth;
    private int maxHeight;

    protected ReentrantLock lock = new ReentrantLock();

    protected DiagramView(DiagramModel m) {
        model = m;

        if (m != null) {
            m.addPropertyChangeListener(this);
        }

        setDoubleBuffered(true);

        // Initialize inline message editor - pass this as parent component
        inlineMessageEditor = new InlineMessageEditor(this, this);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        // this.scale = ScaleRound.roundTo05(scale);
        this.scale = scale;

        if (this.scale > MAX_SCALE) {
            this.scale = MAX_SCALE;
        }
        if (this.scale < MIN_SCALE) {
            this.scale = MIN_SCALE;
        }
        changeSizeToFitAllElements();
        repaint();
    }

    public void zoomIn() {
        setScale(scale * 1.1);
    }

    public void zoomOut() {
        setScale(scale * 0.9);
    }

    private int scaleTo(double number) {
        return (int) (number * getScale());
    }

    public Line2D getDragLine() {
        return dragLine;
    }

    public Rectangle2D getDragRectangle() {
        return dragRectangle;
    }

    public void setDragRectangle(Rectangle2D dragRectangle) {
        this.dragRectangle = dragRectangle;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.scale(scale, scale);

        drawDiagram(g2d);
    }

    @Override
    protected void paintChildren(Graphics g) {
        // Create a clean graphics context for children without the scale transform
        // This prevents the text field cursor from appearing at wrong location
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            // Reset to identity transform (no scaling)
            g2d.setTransform(new java.awt.geom.AffineTransform());
            super.paintChildren(g2d);
        } finally {
            g2d.dispose();
        }
    }

    // Original size image
    public BufferedImage getImage() {
        return getImageByScale(1, 1);
    }

    // Image of a specified width in pixels, maintaining aspect ratio
    public BufferedImage getImageByWidth(int width) {
        double scalex = (double) width / getWidth();

        return getImageByScale(scalex, scalex);
    }

    // Image of a specified height in pixels, maintaining aspect ratio
    public BufferedImage getImageByHeight(int height) {
        double scaley = (double) height / getHeight();

        return getImageByScale(scaley, scaley);
    }

    // Image of a specified width and height in pixels
    // without necessarily maintaining aspect ratio
    public BufferedImage getImageByDimensions(int width, int height) {
        double scalex = (double) width / getWidth();
        double scaley = (double) height / getHeight();

        return getImageByScale(scalex, scaley);
    }

    // Image scaled by a factors of "scalex" and "scaley"
    // to maintain aspect ratio, scalex = scaley
    public BufferedImage getImageByScale(double scalex, double scaley) {

        Point2D.Double maxPoint = getMaxPositionOfElements();

        int imageWidth = (int) (maxPoint.getX() * scalex);
        int imageHeight = (int) (maxPoint.getY() * scaley);

        // create a new image with the (scaled) dimensions of the view panel
        BufferedImage image = new BufferedImage(
                imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        // retrieve the graphics context of the image
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        /*
         * set the background color before clearing
         */
        if (Theme.isDark()) {
            g.setBackground(Colors.BLACK);
        } else {
            g.setBackground(Colors.WHITE);
        }

        g.clearRect(0, 0, imageWidth, imageHeight);

        // maintain aspect ratio by using the same scale for x and y
        g.scale(scalex, scaley);

        // call method drawDiagram to draw the uml elements on the image
        drawDiagram(g);

        return image;
    }

    public void drawDiagram(Graphics2D g) {
        lock.lock();

        // First draw all the LinkGR elements
        model.getGraphicalElements().stream()
                .filter(LinkGR.class::isInstance)
                .forEach(ge -> ge.draw(g));

        model.getGraphicalElements().stream()
                .filter(UMLNoteGR.class::isInstance)
                .forEach(ge -> ge.draw(g));

        // .. and then everything else
        model.getGraphicalElements().stream()
                .filter(ge -> !(ge instanceof LinkGR) && !(ge instanceof UMLNoteGR))
                .forEach(ge -> ge.draw(g));

        // ... finally draw the dragline and rectangle
        drawLineAndRectangle(g);

        lock.unlock();
    }

    /**
     * Resizes the panel to fit the maximum dimensions of all the elements in the
     * diagram.
     */
    public void changeSizeToFitAllElements() {

        Point2D.Double maxPoint = getMaxPositionOfElements();
        int maxX = Math.max(scaleTo(maxPoint.getX()), model.getFrame().getDrawingAreaWidth());
        int maxY = Math.max(scaleTo(maxPoint.getY()), model.getFrame().getDrawingAreaHeight());

        if (maxX != this.maxWidth || maxY != this.maxHeight) {
            maxWidth = maxX;
            maxHeight = maxY;
            logger.fine(() -> "Scale: " + scale + " New View size: " + maxWidth + ", " + maxHeight);

            this.setSize(new Dimension(maxWidth, maxHeight));
            revalidate();
        }
    }

    protected void drawLineAndRectangle(Graphics2D g) {
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[] { 2 }, 0));

        g.draw(dragLine);

        float[] rectangularDashes = { 2 };

        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, rectangularDashes, 0));
        g.draw(dragRectangle);

        // Draw endpoint drag line if currently dragging an endpoint
        drawEndpointDragLine(g);
    }

    /**
     * Draws a temporary line from the fixed endpoint to the drag point when
     * dragging a link endpoint.
     * 
     * @param g the graphics context
     */
    protected void drawEndpointDragLine(Graphics2D g) {
        SelectionController selectionController = model.getFrame().getSelectionController();

        if (selectionController == null || !selectionController.isDraggingEndpoint()) {
            return;
        }

        GraphicalElement element = selectionController.getDraggingLink();
        EndpointType draggingEndpoint = selectionController.getDraggingEndpoint();
        Point2D dragPoint = selectionController.getDragPoint();

        if (element == null || dragPoint == null) {
            return;
        }

        // Determine the fixed endpoint (the one not being dragged)
        Point2D fixedEndpoint = null;

        if (element instanceof AbstractLinkGR) {
            AbstractLinkGR link = (AbstractLinkGR) element;
            if (draggingEndpoint == EndpointType.SOURCE) {
                // Dragging source, so target is fixed
                fixedEndpoint = link.getEndPointRoleB();
            } else {
                // Dragging target, so source is fixed
                fixedEndpoint = link.getEndPointRoleA();
            }
        } else if (element instanceof SDMessageGR) {
            SDMessageGR message = (SDMessageGR) element;
            int y = message.getY();
            if (draggingEndpoint == EndpointType.SOURCE) {
                // Dragging source, so target is fixed
                int endingX = message.getEndingX();
                if (!message.getMessage().isReflective()) {
                    fixedEndpoint = new Point2D.Double(endingX, y);
                } else {
                    // For reflective messages, target is at the end of the loop
                    fixedEndpoint = new Point2D.Double(message.getStartingX(), y + 15);
                }
            } else {
                // Dragging target, so source is fixed
                int startingX = message.getStartingX();
                fixedEndpoint = new Point2D.Double(startingX, y);
            }
        }

        if (fixedEndpoint == null) {
            return;
        }

        // Draw dashed line from fixed endpoint to current drag position
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10,
                new float[] { 5, 5 }, 0));
        g.setPaint(Colors.getHighlightColor());
        g.drawLine((int) fixedEndpoint.getX(), (int) fixedEndpoint.getY(),
                (int) dragPoint.getX(), (int) dragPoint.getY());
    }

    public void setModel(DiagramModel m) {
        if (model != null) {
            model.removePropertyChangeListener(this);
        }

        model = m;

        if (m != null) {
            m.addPropertyChangeListener(this);
            repaint();
        }
    }

    public DiagramModel getModel() {
        return model;
    }

    /**
     * Get the inline message editor for this view.
     * 
     * @return the inline message editor
     */
    public InlineMessageEditor getInlineMessageEditor() {
        return inlineMessageEditor;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    public Point2D.Double getMaxPositionOfElements() {
        double maxX = 0;
        double maxY = 0;
        for (GraphicalElement ge : model.getGraphicalElements()) {
            Rectangle2D bounds = ge.getBounds();
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        return new Point2D.Double(maxX + 20, maxY + 20);
    }

}
