package edu.city.studentuml.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Biser
 */
public abstract class CompositeUCDElement extends UCDComponent {

    private List<UCDComponent> ucdComponents;

    public CompositeUCDElement(String name) {
        super(name);
        ucdComponents = new ArrayList<UCDComponent>();
    }

    @Override
    public void add(UCDComponent ucdComponent) {
        ucdComponents.add(ucdComponent);
    }

    @Override
    public void remove(UCDComponent ucdComponent) {
        ucdComponents.remove(ucdComponent);
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
        return new CompositeUCDIterator(ucdComponents.iterator());
    }

    public int getNumberOfElements() {
        return ucdComponents.size();
    }

    public UCDComponent getElement(int index) {
        return ucdComponents.get(index);
    }
}
