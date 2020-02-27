package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class CompositeNode extends NodeComponent {

    private List<NodeComponent> nodeComponents;

    public CompositeNode(String name) {
        super(name);
        nodeComponents = new ArrayList<NodeComponent>();
    }

    @Override
    public void add(NodeComponent nodeComponent) {
        nodeComponents.add(nodeComponent);
    }

    @Override
    public void remove(NodeComponent nodeComponent) {
        nodeComponents.remove(nodeComponent);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Iterator createIterator() {
        return new CompositeNodeIterator(nodeComponents.iterator());
    }

    public int getNumberOfNodeComponents() {
        return nodeComponents.size();
    }

    public NodeComponent getNodeComponent(int index) {
        return nodeComponents.get(index);
    }
}
