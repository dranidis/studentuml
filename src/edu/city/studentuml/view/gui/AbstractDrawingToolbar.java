package edu.city.studentuml.view.gui;

import javax.swing.JToolBar;

/**
 *
 * @author dimitris
 */
abstract class AbstractDrawingToolbar extends JToolBar {
    abstract void setSelectionMode();

    abstract boolean getSelectionMode();

    abstract void refreshUndoRedoButtons();
}
