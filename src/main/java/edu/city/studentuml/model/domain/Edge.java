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
        this.guard = guard;
    }

    public String getGuard() {
        if (guard.length() > 0) {
            return guard;
        } else {
            return "";
        }
    }

    public NodeComponent getSource() {
        return source;
    }

    public NodeComponent getTarget() {
        return target;
    }

    @Override
    public abstract String toString();

    @Override
    public abstract Edge clone();
}
