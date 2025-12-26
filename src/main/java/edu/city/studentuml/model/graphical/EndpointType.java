package edu.city.studentuml.model.graphical;

/**
 * Enum representing the type of endpoint on a link. Used for endpoint dragging
 * and reconnection operations.
 * 
 * @author Dimitris Dranidis
 */
public enum EndpointType {
    /**
     * No endpoint (point is not near any endpoint)
     */
    NONE,

    /**
     * Source endpoint (Role A / Classifier A)
     */
    SOURCE,

    /**
     * Target endpoint (Role B / Classifier B)
     */
    TARGET
}
