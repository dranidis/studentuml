package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.graphical.GraphicalElement;

/**
 * Singleton class that manages the clipboard for copy/paste operations.
 * Stores copies of selected graphical elements for pasting within or across diagrams.
 * 
 * @author Dimitris Dranidis
 */
public class ClipboardManager {
    
    private static final Logger logger = Logger.getLogger(ClipboardManager.class.getName());
    
    private static ClipboardManager instance;
    
    private List<GraphicalElement> clipboardElements;
    private DiagramModel sourceDiagram;
    
    /**
     * Private constructor for singleton pattern
     */
    private ClipboardManager() {
        clipboardElements = new ArrayList<>();
        sourceDiagram = null;
    }
    
    /**
     * Get the singleton instance of ClipboardManager
     * 
     * @return the singleton instance
     */
    public static ClipboardManager getInstance() {
        if (instance == null) {
            instance = new ClipboardManager();
        }
        return instance;
    }
    
    /**
     * Copy selected elements to the clipboard.
     * Stores references to the elements and the source diagram.
     * 
     * @param elements the list of elements to copy
     * @param source the source diagram model
     */
    public void copy(List<GraphicalElement> elements, DiagramModel source) {
        if (elements == null || elements.isEmpty()) {
            logger.warning("Attempted to copy empty or null element list");
            return;
        }
        
        clipboardElements.clear();
        clipboardElements.addAll(elements);
        sourceDiagram = source;
        
        logger.fine(() -> "Copied " + elements.size() + " elements to clipboard");
    }
    
    /**
     * Get the elements currently in the clipboard.
     * Returns a copy of the internal list to prevent external modification.
     * 
     * @return a list of clipboard elements, or empty list if clipboard is empty
     */
    public List<GraphicalElement> getClipboardElements() {
        return new ArrayList<>(clipboardElements);
    }
    
    /**
     * Get the source diagram from which elements were copied.
     * 
     * @return the source diagram model, or null if clipboard is empty
     */
    public DiagramModel getSourceDiagram() {
        return sourceDiagram;
    }
    
    /**
     * Check if the clipboard has content.
     * 
     * @return true if clipboard contains elements, false otherwise
     */
    public boolean hasContent() {
        return !clipboardElements.isEmpty();
    }
    
    /**
     * Get the number of elements in the clipboard.
     * 
     * @return the number of elements
     */
    public int getElementCount() {
        return clipboardElements.size();
    }
    
    /**
     * Clear the clipboard, removing all stored elements.
     */
    public void clear() {
        clipboardElements.clear();
        sourceDiagram = null;
        logger.fine("Clipboard cleared");
    }
}
