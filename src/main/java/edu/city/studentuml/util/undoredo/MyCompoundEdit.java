package edu.city.studentuml.util.undoredo;

import javax.swing.undo.CompoundEdit;

/**
 *
 * @author Biser
 */
public abstract class MyCompoundEdit extends CompoundEdit {

    public MyCompoundEdit() {
        super();
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }
}
