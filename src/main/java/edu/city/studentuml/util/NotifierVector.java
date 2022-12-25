package edu.city.studentuml.util;

import java.util.Vector;

public class NotifierVector<T> extends Vector<T> {

    public static NotifierVector from(Vector v) {
        NotifierVector newVector = new NotifierVector();

        for (int i = 0; i < v.size(); i++) {
            newVector.add(v.get(i));
        }

        return newVector;
    }

    @Override
    public synchronized boolean add(T o) {
        synchronized (this) {
            SystemWideObjectNamePool.getInstance().objectAdded(o);
            return super.add(o);
        }
    }

    @Override
    public synchronized void insertElementAt(T o, int index) {
        synchronized (this) {
            SystemWideObjectNamePool.getInstance().objectAdded(o);
            super.insertElementAt(o, index);
        }
    }

    @Override
    public synchronized boolean remove(Object o) {
        synchronized (this) {
            SystemWideObjectNamePool.getInstance().objectRemoved(o);
            return super.remove(o);
        }
    }

    @Override
    public synchronized T remove(int index) {
        synchronized (this) {
            SystemWideObjectNamePool.getInstance().objectRemoved(this.get(index));
            return super.remove(index);
        }
    }

    @Override
    public synchronized void clear() {
        synchronized (this) {
            while (size() > 0) {
                remove(0);
            }
        }
    }

    protected void finalize() throws Throwable {
        //do finalization here
        clear();
        super.finalize(); //not necessary if extending Object.
    }
}
