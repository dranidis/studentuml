package edu.city.studentuml.model.domain;

/**
 * Abstract class for connecting two nodes.
 *
 * @author Biser
 */
public abstract class Edge {

    protected String guard;
    protected NodeComponent source;
    protected NodeComponent target;

    protected Edge(NodeComponent source, NodeComponent target) {
        this.source = source;
        this.target = target;
        guard = "";
    }

    public void setGuard(String guard) {
        if (guard == null) {
            throw new IllegalArgumentException("Guard cannot be null");
        }
        this.guard = guard;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the advancedrules.txt / simplerules.txt file needs to be updated
     */
    public String getGuard() {
        return guard;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the advancedrules.txt / simplerules.txt file needs to be updated
     */
    public NodeComponent getSource() {
        return source;
    }

    /*
     * DO NOT CHANGE THE NAME: CALLED BY REFLECTION IN CONSISTENCY CHECK
     *
     * if name is changed the advancedrules.txt / simplerules.txt file needs to be updated
     */
    public NodeComponent getTarget() {
        return target;
    }

    @Override
    public abstract String toString();

    @Override
    public abstract Edge clone();
}
