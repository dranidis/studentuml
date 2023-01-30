package edu.city.studentuml.view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 *
 */
public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener {

    private CheckTreeSelectionModel selectionModel;
    private TreePathSelectable selectable;
    protected JTree tree = new JTree();
    int hotspot = new JCheckBox().getPreferredSize().width;

    public CheckTreeManager(JTree tree, boolean dig, TreePathSelectable selectable) {
        this.tree = tree;
        selectionModel = new CheckTreeSelectionModel(tree.getModel(), dig);
        this.selectable = selectable;

        // note: if largemodel is not set
        // then treenodes are getting truncated.
        // need to debug further to find the problem
        if (selectable != null) {
            tree.setLargeModel(true);
        }

        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel, selectable));
        tree.addMouseListener(this);
        selectionModel.addTreeSelectionListener(this);
    }

    public TreePathSelectable getSelectable(TreePathSelectable selectable) {
        return selectable;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        if (path == null) {
            return;
        }
        if (me.getX() > tree.getPathBounds(path).x + hotspot) {
            return;
        }

        if (selectable != null && !selectable.isSelectable(path)) {
            return;
        }

        boolean selected = selectionModel.isPathSelected(path, selectionModel.isDigged());
        selectionModel.removeTreeSelectionListener(this);

        try {
            if (selected) {
                selectionModel.removeSelectionPath(path);
            } else {
                selectionModel.addSelectionPath(path);
            }
        } finally {
            selectionModel.addTreeSelectionListener(this);
            tree.treeDidChange();
        }
    }

    public CheckTreeSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void valueChanged(TreeSelectionEvent e) {
        tree.treeDidChange();
    }

    class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

        private CheckTreeSelectionModel selectionModel;
        private TreePathSelectable selectable;
        private TreeCellRenderer delegate;
        private TristateCheckBox checkBox = new TristateCheckBox();

        public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel,
                TreePathSelectable selectable) {
            this.delegate = delegate;
            this.selectionModel = selectionModel;
            this.selectable = selectable;
            setLayout(new BorderLayout());
            setOpaque(false);
            checkBox.setOpaque(false);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
                    hasFocus);

            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                if (selectionModel.isPathSelected(path, selectionModel.isDigged())) {
                    checkBox.setState(Boolean.TRUE);
                } else {
                    checkBox.setState(selectionModel.isDigged() && selectionModel.isPartiallySelected(path) ? null
                            : Boolean.FALSE);
                }
            }
            removeAll();
            checkBox.setVisible(path == null || selectable == null || selectable.isSelectable(path));
            add(checkBox, BorderLayout.WEST);
            add(renderer, BorderLayout.CENTER);
            return this;
        }
    }

    class CheckTreeSelectionModel extends DefaultTreeSelectionModel {

        private TreeModel model;
        private boolean dig = true;

        public CheckTreeSelectionModel(TreeModel model, boolean dig) {
            this.model = model;
            this.dig = dig;
            setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }

        public boolean isDigged() {
            return dig;
        }

        // tests whether there is any unselected node in the subtree of given path
        public boolean isPartiallySelected(TreePath path) {
            if (isPathSelected(path, true)) {
                return false;
            }
            TreePath[] selectionPaths = getSelectionPaths();
            if (selectionPaths == null) {
                return false;
            }
            for (int j = 0; j < selectionPaths.length; j++) {
                if (isDescendant(selectionPaths[j], path)) {
                    return true;
                }
            }
            return false;
        }

        // tells whether given path is selected.
        // if dig is true, then a path is assumed to be selected, if
        // one of its ancestor is selected.
        public boolean isPathSelected(TreePath path, boolean dig) {
            if (!dig) {
                return super.isPathSelected(path);
            }
            while (path != null && !super.isPathSelected(path)) {
                path = path.getParentPath();
            }
            return path != null;
        }

        // is path1 descendant of path2
        private boolean isDescendant(TreePath path1, TreePath path2) {
            Object[] obj1 = path1.getPath();
            Object[] obj2 = path2.getPath();
            for (int i = 0; i < obj2.length; i++) {
                if (obj1[i] != obj2[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void setSelectionPaths(TreePath[] paths) {
            if (dig) {
                throw new UnsupportedOperationException("not implemented yet!!!");
            } else {
                super.setSelectionPaths(paths);
            }
        }

        @Override
        public void addSelectionPaths(TreePath[] paths) {
            if (!dig) {
                super.addSelectionPaths(paths);
                return;
            }

            // unselect all descendants of paths[]
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath[] selectionPaths = getSelectionPaths();
                if (selectionPaths == null) {
                    break;
                }
                ArrayList<TreePath> toBeRemoved = new ArrayList<>();
                for (int j = 0; j < selectionPaths.length; j++) {
                    if (isDescendant(selectionPaths[j], path)) {
                        toBeRemoved.add(selectionPaths[j]);
                    }
                }
                super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0]));
            }

            // if all siblings are selected then unselect them and select parent recursively
            // otherwize just select that path.
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath temp = null;
                while (areSiblingsSelected(path)) {
                    temp = path;
                    if (path.getParentPath() == null) {
                        break;
                    }
                    path = path.getParentPath();
                }
                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        addSelectionPath(temp.getParentPath());
                    } else {
                        if (!isSelectionEmpty()) {
                            removeSelectionPaths(getSelectionPaths());
                        }
                        super.addSelectionPaths(new TreePath[]{temp});
                    }
                } else {
                    super.addSelectionPaths(new TreePath[]{path});
                }
            }
        }

        // tells whether all siblings of given path are selected.
        private boolean areSiblingsSelected(TreePath path) {
            TreePath parent = path.getParentPath();
            if (parent == null) {
                return true;
            }
            Object node = path.getLastPathComponent();
            Object parentNode = parent.getLastPathComponent();

            int childCount = model.getChildCount(parentNode);
            for (int i = 0; i < childCount; i++) {
                Object childNode = model.getChild(parentNode, i);
                if (childNode == node) {
                    continue;
                }
                if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void removeSelectionPaths(TreePath[] paths) {
            if (!dig) {
                super.removeSelectionPaths(paths);
                return;
            }

            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() == 1) {
                    super.removeSelectionPaths(new TreePath[]{path});
                } else {
                    toggleRemoveSelection(path);
                }
            }
        }

        // if any ancestor node of given path is selected then unselect it
        //  and selection all its descendants except given path and descendants.
        // otherwise just unselect the given path
        private void toggleRemoveSelection(TreePath path) {
            Deque<TreePath> stack = new ArrayDeque<>();
            TreePath parent = path.getParentPath();
            while (parent != null && !isPathSelected(parent)) {
                stack.push(parent);
                parent = parent.getParentPath();
            }
            if (parent != null) {
                stack.push(parent);
            } else {
                super.removeSelectionPaths(new TreePath[]{path});
                return;
            }

            while (!stack.isEmpty()) {
                TreePath temp = stack.pop();
                TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();
                Object node = temp.getLastPathComponent();
                Object peekNode = peekPath.getLastPathComponent();
                int childCount = model.getChildCount(node);
                for (int i = 0; i < childCount; i++) {
                    Object childNode = model.getChild(node, i);
                    if (childNode != peekNode) {
                        super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)});
                    }
                }
            }
            super.removeSelectionPaths(new TreePath[]{parent});
        }

        public Enumeration<TreePath> getAllSelectedPaths() {
            TreePath[] treePaths = getSelectionPaths();
            if (treePaths == null) {
                return Collections.enumeration(Collections.emptyList());
            }
            Enumeration<TreePath> enumer = Collections.enumeration(Arrays.asList(treePaths));
            if (dig) {
                enumer = new PreorderEnumeration(enumer, model);
            }
            return enumer;
        }
    }

    class TristateCheckBox extends JCheckBox {

        private final TristateDecorator model;

        public TristateCheckBox(String text, Icon icon, Boolean initial) {
            super(text, icon);
            // Add a listener for when the mouse is pressed
            super.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    grabFocus();
                    model.nextState();
                }
            });
            // Reset the keyboard action map
            ActionMap map = new ActionMapUIResource();
            map.put("pressed", new AbstractAction() { //NOI18N

                public void actionPerformed(ActionEvent e) {
                    grabFocus();
                    model.nextState();
                }
            });
            map.put("released", null); //NOI18N
            SwingUtilities.replaceUIActionMap(this, map);
            // set the model to the adapted model
            model = new TristateDecorator(getModel());
            setModel(model);
            setState(initial);
        }

        public TristateCheckBox(String text, Boolean initial) {
            this(text, null, initial);
        }

        public TristateCheckBox(String text) {
            this(text, null);
        }

        public TristateCheckBox() {
            this(null);
        }

        /** No one may add mouse listeners, not even Swing! */
        @Override
        public synchronized void addMouseListener(MouseListener l) {
            // empty
        }

        /**
         * Set the new state to either SELECTED, NOT_SELECTED or
         * DONT_CARE.  If state == null, it is treated as DONT_CARE.
         */
        public void setState(Boolean state) {
            model.setState(state);
        }

        /** Return the current state, which is determined by the
         * selection status of the model. */
        public Boolean getState() {
            return model.getState();
        }

        /**
         * Exactly which Design Pattern is this?  Is it an Adapter,
         * a Proxy or a Decorator?  In this case, my vote lies with the
         * Decorator, because we are extending functionality and
         * "decorating" the original model with a more powerful model.
         */
        private class TristateDecorator implements ButtonModel {

            private final ButtonModel other;

            private TristateDecorator(ButtonModel other) {
                this.other = other;
            }

            private void setState(Boolean state) {
                if (state == Boolean.FALSE) {
                    other.setArmed(false);
                    setPressed(false);
                    setSelected(false);
                } else if (state == Boolean.TRUE) {
                    other.setArmed(false);
                    setPressed(false);
                    setSelected(true);
                } else {
                    other.setArmed(true);
                    setPressed(true);
                    setSelected(true);
                }
            }

            /**
             * The current state is embedded in the selection / armed
             * state of the model.
             *
             * We return the SELECTED state when the checkbox is selected
             * but not armed, DONT_CARE state when the checkbox is
             * selected and armed (grey) and NOT_SELECTED when the
             * checkbox is deselected.
             */
            private Boolean getState() {
                if (isSelected() && !isArmed()) {
                    // normal black tick
                    return Boolean.TRUE;
                } else if (isSelected() && isArmed()) {
                    // don't care grey tick
                    return null;
                } else {
                    // normal deselected
                    return Boolean.FALSE;
                }
            }

            /** We rotate between NOT_SELECTED, SELECTED and DONT_CARE.*/
            private void nextState() {
                Boolean current = getState();
                if (current == Boolean.FALSE) {
                    setState(Boolean.TRUE);
                } else if (current == Boolean.TRUE) {
                    setState(null);
                } else if (current == null) {
                    setState(Boolean.FALSE);
                }
            }

            /** Filter: No one may change the armed status except us. */
            public void setArmed(boolean b) {
            }

            /** We disable focusing on the component when it is not
             * enabled. */
            public void setEnabled(boolean b) {
                other.setEnabled(b);
            }

            /** All these methods simply delegate to the "other" model
             * that is being decorated. */
            public boolean isArmed() {
                return other.isArmed();
            }

            public boolean isSelected() {
                return other.isSelected();
            }

            public boolean isEnabled() {
                return other.isEnabled();
            }

            public boolean isPressed() {
                return other.isPressed();
            }

            public boolean isRollover() {
                return other.isRollover();
            }

            public void setSelected(boolean b) {
                other.setSelected(b);
            }

            public void setPressed(boolean b) {
                other.setPressed(b);
            }

            public void setRollover(boolean b) {
                other.setRollover(b);
            }

            public void setMnemonic(int key) {
                other.setMnemonic(key);
            }

            public int getMnemonic() {
                return other.getMnemonic();
            }

            public void setActionCommand(String s) {
                other.setActionCommand(s);
            }

            public String getActionCommand() {
                return other.getActionCommand();
            }

            public void setGroup(ButtonGroup group) {
                other.setGroup(group);
            }

            public void addActionListener(ActionListener l) {
                other.addActionListener(l);
            }

            public void removeActionListener(ActionListener l) {
                other.removeActionListener(l);
            }

            public void addItemListener(ItemListener l) {
                other.addItemListener(l);
            }

            public void removeItemListener(ItemListener l) {
                other.removeItemListener(l);
            }

            public void addChangeListener(ChangeListener l) {
                other.addChangeListener(l);
            }

            public void removeChangeListener(ChangeListener l) {
                other.removeChangeListener(l);
            }

            public Object[] getSelectedObjects() {
                return other.getSelectedObjects();
            }
        }
    }

    class ChildrenEnumeration implements Enumeration<TreePath> {

        private TreePath path;
        private TreeModel model;
        private int position = 0;
        private int childCount;

        public ChildrenEnumeration(TreePath path, TreeModel model) {
            this.path = path;
            this.model = model;
            childCount = model.getChildCount(path.getLastPathComponent());
        }

        public boolean hasMoreElements() {
            return position < childCount;
        }

        public TreePath nextElement() {
            if (!hasMoreElements()) {
                throw new NoSuchElementException();
            }
            return path.pathByAddingChild(model.getChild(path.getLastPathComponent(), position++));
        }
    }

    class PreorderEnumeration implements Enumeration<TreePath> {

        private TreeModel model;
        protected Deque<Enumeration<TreePath>> stack = new ArrayDeque<>();

        public PreorderEnumeration(TreePath path, TreeModel model) {
            this(Collections.enumeration(Collections.singletonList(path)), model);
        }

        public PreorderEnumeration(Enumeration<TreePath> enumer, TreeModel model) {
            this.model = model;
            stack.push(enumer);
        }

        public boolean hasMoreElements() {
            return (!stack.isEmpty() && (stack.peek()).hasMoreElements());
        }

        public TreePath nextElement() {
            Enumeration<TreePath> enumer = stack.peek();
            TreePath path = enumer.nextElement();

            if (!enumer.hasMoreElements()) {
                stack.pop();
            }

            if (model.getChildCount(path.getLastPathComponent()) > 0) {
                stack.push(new ChildrenEnumeration(path, model));
            }
            return path;
        }
    }
}
