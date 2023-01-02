package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.ActionNode;
import edu.city.studentuml.model.domain.ActivityFinalNode;
import edu.city.studentuml.model.domain.ActivityNode;
import edu.city.studentuml.model.domain.Actor;
import edu.city.studentuml.model.graphical.CCDModel;
import edu.city.studentuml.model.domain.ConceptualClass;
import edu.city.studentuml.model.domain.ControlNode;
import edu.city.studentuml.model.domain.DecisionNode;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.FlowFinalNode;
import edu.city.studentuml.model.domain.ForkNode;
import edu.city.studentuml.model.domain.InitialNode;
import edu.city.studentuml.model.graphical.DiagramModel;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.JoinNode;
import edu.city.studentuml.model.domain.LeafNode;
import edu.city.studentuml.model.domain.LeafUCDElement;
import edu.city.studentuml.model.domain.MergeNode;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.NodeComponent;
import edu.city.studentuml.model.domain.ObjectNode;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.graphical.SSDModel;
import edu.city.studentuml.model.domain.System;
import edu.city.studentuml.model.domain.SystemInstance;
import edu.city.studentuml.model.domain.UCDComponent;
import edu.city.studentuml.model.graphical.UCDModel;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.domain.UseCase;
import edu.city.studentuml.model.graphical.ADModel;
import edu.city.studentuml.util.Constants;
import edu.city.studentuml.model.graphical.ConceptualClassGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.NodeComponentGR;
import edu.city.studentuml.model.graphical.RoleClassifierGR;
import edu.city.studentuml.model.graphical.UCDComponentGR;
import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class RepositoryTreeView extends JPanel implements Observer {

    private static final Logger logger = Logger.getLogger(RepositoryTreeView.class.getName());

    private DefaultMutableTreeNode datamodelnode;
    private DefaultMutableTreeNode diagrammodelnode;
    private UMLProject umlProject = UMLProject.getInstance();
    private ImageIcon rootIcon;
    private DefaultMutableTreeNode rootNode;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private UMLTreeRenderer treeRenderer;

    public RepositoryTreeView() {
        super(new GridLayout(1, 0));
        rootNode = new DefaultMutableTreeNode("Central Repository");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new UMLTreeModelListener());
        tree = new JTree(treeModel);
        rootIcon = createImageIcon("icon.gif");
        treeRenderer = new UMLTreeRenderer(rootIcon);
        tree.setCellRenderer(treeRenderer);
        tree.setRowHeight(20);
        add(tree);
        datamodelnode = addObject("Data Model");
        diagrammodelnode = addObject("Diagram Model");
        tree.addTreeSelectionListener(new RepositoryTreeSelectionListener());
        umlProject.addObserver(this);
    }

    public void update(Observable o, Object arg) {
        DefaultMutableTreeNode dnode;
        Vector<DiagramModel> diagrams;
        Vector<DesignClass> classes;
        Vector<Interface> interfaces;
        Vector<RoleClassifierGR> roleClassifiers;
        DiagramModel diagram;
        Interface designInterface;
        RoleClassifier classifier;

        Vector<ConceptualClassGR> concepts;
        UCDComponent ucdComponent;
        ConceptualClass concept;
        NodeComponent nodeComponent;

        String expState = getExpansionState(tree, 0);
        datamodelnode.removeAllChildren();
        treeModel.reload();
        diagrammodelnode.removeAllChildren();
        treeModel.reload();

        classes = umlProject.getCentralRepository().getClasses();
        classes.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        
        Iterator<DesignClass> designClassIterator = classes.iterator();
        while (designClassIterator.hasNext()) {
            DesignClass dc = designClassIterator.next();
            DefaultMutableTreeNode designClassNode = addObject(datamodelnode, dc);

            DefaultMutableTreeNode generalizationsNode = addObject(designClassNode, "extends");
            umlProject.getCentralRepository().getGeneralizations().stream()
                    .filter(g -> g.getBaseClass().getName().equals(dc.getName()))
                    .collect(Collectors.toList()).forEach(cg -> addObject(generalizationsNode, cg.getSuperClass()));

            DefaultMutableTreeNode raelizationsNode = addObject(designClassNode, "realizes");
            umlProject.getCentralRepository().getRealizations().stream()
                    .filter(g -> g.getTheClass().getName().equals(dc.getName()))
                    .collect(Collectors.toList()).forEach(cg -> addObject(raelizationsNode, cg.getTheInterface()));       
        }

        interfaces = umlProject.getCentralRepository().getInterfaces();
        interfaces.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

        Iterator<Interface> interfaceIterator = interfaces.iterator();
        while (interfaceIterator.hasNext()) {
            designInterface = interfaceIterator.next();
            addObject(datamodelnode, designInterface);
        }

        diagrams = umlProject.getDiagramModels();
        Iterator<DiagramModel> diagramIterator = diagrams.iterator();
        while (diagramIterator.hasNext()) {
            diagram = diagramIterator.next();
            dnode = addObject(diagrammodelnode, diagram);

            if (diagram instanceof UCDModel) {
                Iterator<GraphicalElement> i = ((UCDModel) diagram).getGraphicalElements().iterator();
                while (i.hasNext()) {
                    GraphicalElement e = i.next();
                    if (e instanceof UCDComponentGR) {
                        ucdComponent = ((UCDComponentGR) e).getUCDComponent();
                        addUCDComponent(dnode, ucdComponent);
                    }
                }
            }

            if (diagram instanceof SSDModel) {
                roleClassifiers = ((SSDModel) diagram).getRoleClassifiers();

                if (roleClassifiers != null) {
                    Iterator<RoleClassifierGR> iterator2 = roleClassifiers.iterator();

                    while (iterator2.hasNext()) {
                        classifier = (iterator2.next()).getRoleClassifier();
                        addObject(dnode, classifier);
                    }
                }
            }

            if (diagram instanceof CCDModel) {
                concepts = ((CCDModel) diagram).getConceptualClasses();

                Iterator<ConceptualClassGR> iterator2 = concepts.iterator();

                while (iterator2.hasNext()) {
                    concept = (iterator2.next()).getConceptualClass();
                    addObject(dnode, concept);
                }

            }

            if (diagram instanceof SDModel) {
                roleClassifiers = ((SDModel) diagram).getRoleClassifiers();

                if (roleClassifiers != null) {
                    Iterator<RoleClassifierGR> iterator2 = roleClassifiers.iterator();

                    while (iterator2.hasNext()) {
                        classifier = (iterator2.next()).getRoleClassifier();
                        addObject(dnode, classifier);
                    }
                }
            }

            if (diagram instanceof ADModel) {
                Iterator<GraphicalElement> i = ((ADModel) diagram).getGraphicalElements().iterator();
                while (i.hasNext()) {
                    GraphicalElement e = i.next();
                    if (e instanceof NodeComponentGR) {
                        nodeComponent = ((NodeComponentGR) e).getNodeComponent();
                        addNodeComponent(dnode, nodeComponent);
                    }
                }
            }
        }

        restoreExpanstionState(tree, 0, expState);
    }

    private void addNodeComponent(DefaultMutableTreeNode dnode, NodeComponent nodeComponent) {
        if (nodeComponent instanceof LeafNode) {
            addObject(dnode, nodeComponent);
        } else {
            addObject(dnode, nodeComponent);
            for (int i = 0; i < nodeComponent.getNumberOfNodeComponents(); i++) {
                NodeComponent node = nodeComponent.getNodeComponent(i);
                addNodeComponent(dnode, node);
            }
        }
    }

    private void addUCDComponent(DefaultMutableTreeNode dnode, UCDComponent ucdComponent) {
        if (ucdComponent instanceof LeafUCDElement) {
            addObject(dnode, ucdComponent);
        } else {
            addObject(dnode, ucdComponent);
            for (int i = 0; i < ucdComponent.getNumberOfElements(); i++) {
                UCDComponent node = ucdComponent.getElement(i);
                addUCDComponent(dnode, node);
            }
        }
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String img) {
        java.net.URL imgURL = ApplicationGUI.class.getResource(Constants.IMAGES_DIR + img);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            logger.severe(() -> "[createImageIcon] Couldn't find file: " + img);

            return null;
        }
    }

    void expandDiagrams() {
        tree.expandRow(2);
    }

    class UMLTreeModelListener implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;

            node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed node is the child of the node
             * we've already gotten. Otherwise, the changed node and the specified node are
             * the same.
             */
            try {
                int index = e.getChildIndices()[0];

                node = (DefaultMutableTreeNode) (node.getChildAt(index));
            } catch (NullPointerException exc) {
            }

            logger.fine("The user has finished editing the node.");
            logger.fine("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        }

        public void treeStructureChanged(TreeModelEvent e) {
        }
    }

    private class UMLTreeRenderer extends DefaultTreeCellRenderer {

        private Icon ucdIcon;
        private Icon useCaseIcon;
        private Icon systemIcon;
        private Icon ccdIcon;
        private Icon ssdIcon;
        private Icon systemInstanceIcon;
        private Icon actorIcon;
        private Icon classIcon;
        private Icon dataIcon;
        private Icon dcdIcon;
        private Icon diagramIcon;
        private Icon interfaceIcon;
        private String nodeText;
        private Icon objectIcon;
        private Icon mobjectIcon;
        private Icon sdIcon;
        private Icon treeIcon;
        private Icon adIcon;
        private Icon activityIcon;
        private Icon actionIcon;
        private Icon adObjectIcon;
        private Icon initialNodeIcon;
        private Icon activityFinalNodeIcon;
        private Icon flowFinalNodeIcon;
        private Icon decisionNodeIcon;
        private Icon mergeNodeIcon;
        private Icon forkNodeIcon;
        private Icon joinNodeIcon;
        private Object userObject;

        public UMLTreeRenderer(Icon icon) {
            treeIcon = icon;
            dataIcon = createImageIcon("dcd.gif");
            ucdIcon = createImageIcon("useCaseDiagram.gif");
            useCaseIcon = createImageIcon("useCase.gif");
            systemIcon = createImageIcon("system.gif");
            systemInstanceIcon = createImageIcon("object.gif");
            ccdIcon = createImageIcon("ccd.gif");// ccd
            ssdIcon = createImageIcon("ssd.gif");
            diagramIcon = createImageIcon("sd.gif");
            dcdIcon = createImageIcon("dcd.gif");
            sdIcon = createImageIcon("sd.gif");
            classIcon = createImageIcon("class.gif");
            interfaceIcon = createImageIcon("interface.gif");
            objectIcon = createImageIcon("object.gif");
            mobjectIcon = createImageIcon("multiobject.gif");
            actorIcon = createImageIcon("actor.gif");

            adIcon = createImageIcon("activityDiagram.gif");
            activityIcon = createImageIcon("activityDiagram.gif");
            actionIcon = createImageIcon("action.gif");
            adObjectIcon = createImageIcon("objectNode.gif");
            initialNodeIcon = createImageIcon("initial.gif");
            activityFinalNodeIcon = createImageIcon("final.gif");
            flowFinalNodeIcon = createImageIcon("flowFinal.gif");
            decisionNodeIcon = createImageIcon("decision.gif");
            mergeNodeIcon = createImageIcon("merge.gif");
            forkNodeIcon = createImageIcon("fork.gif");
            joinNodeIcon = createImageIcon("join.gif");
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            userObject = ((DefaultMutableTreeNode) value).getUserObject();

            // if (leaf && isTutorialBook(value)) {
            if (value == rootNode) {
                setIcon(treeIcon);
                setToolTipText("Central Repository");
            } else if (value == datamodelnode) {
                setIcon(dataIcon);
                setToolTipText("Data Model");
            } else if (value == diagrammodelnode) {
                setIcon(diagramIcon);
                setToolTipText("Diagram Model");
            } else if (userObject instanceof UCDModel) {
                nodeText = ((UCDModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(ucdIcon);
                setToolTipText("Use Case Diagram - " + nodeText);
            } else if (userObject instanceof SSDModel) {
                nodeText = ((SSDModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(ssdIcon);
                setToolTipText("System Sequence Diagram - " + nodeText);
            } else if (userObject instanceof CCDModel) {
                nodeText = ((CCDModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(ccdIcon);
                setToolTipText("Use Case Diagram - " + nodeText);
            } else if (userObject instanceof SDModel) {
                nodeText = ((SDModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(sdIcon);
                setToolTipText("Sequence Diagram - " + nodeText);
            } else if (userObject instanceof DCDModel) {
                nodeText = ((DCDModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(dcdIcon);
                setToolTipText("Design Class Diagram - " + nodeText);
            } else if (userObject instanceof ADModel) {
                nodeText = ((ADModel) userObject).getDiagramName();
                setText(nodeText);
                setIcon(adIcon);
                setToolTipText("Activity Diagram - " + nodeText);
            } else if (userObject instanceof UCDComponent) {
                if (userObject instanceof UseCase) {
                    setIcon(useCaseIcon);
                } else if (userObject instanceof System) {
                    setIcon(systemIcon);
                } else if (userObject instanceof Actor) {
                    setIcon(actorIcon);
                }

                nodeText = userObject.toString();
                setText(nodeText);
                setToolTipText("Classifier - " + nodeText);
            } else if (userObject instanceof ConceptualClass) {
                nodeText = ((ConceptualClass) userObject).getName();
                setText(nodeText);
                setIcon(classIcon);
                setToolTipText("Concept - " + nodeText);
            } else if (userObject instanceof RoleClassifier) {
                if (((RoleClassifier) userObject) instanceof SystemInstance) {
                    setIcon(systemInstanceIcon);
                } else if (((RoleClassifier) userObject) instanceof SDObject) {
                    setIcon(objectIcon);
                } else if (((RoleClassifier) userObject) instanceof MultiObject) {
                    setIcon(mobjectIcon);
                } else {
                    setIcon(actorIcon);
                }

                nodeText = ((RoleClassifier) userObject).getName() + " : "
                        + ((RoleClassifier) userObject).getClassifier().getName();
                setText(nodeText);
                setToolTipText("Role Classifier - " + nodeText);
            } else if (userObject instanceof DesignClass) {
                nodeText = ((DesignClass) userObject).getName();
                setText(nodeText);
                setIcon(classIcon);
                setToolTipText("Class - " + nodeText);
            } else if (userObject instanceof Interface) {
                nodeText = ((Interface) userObject).getName();
                setText(nodeText);
                setIcon(interfaceIcon);
                setToolTipText("Interface - " + nodeText);
            } else if (userObject instanceof NodeComponent) {
                if (userObject instanceof ActivityNode) {
                    setIcon(activityIcon);
                } else if (userObject instanceof ActionNode) {
                    setIcon(actionIcon);
                } else if (userObject instanceof ObjectNode) {
                    setIcon(adObjectIcon);
                } else if (userObject instanceof ControlNode) {
                    if (userObject instanceof InitialNode) {
                        setIcon(initialNodeIcon);
                    } else if (userObject instanceof ActivityFinalNode) {
                        setIcon(activityFinalNodeIcon);
                    } else if (userObject instanceof FlowFinalNode) {
                        setIcon(flowFinalNodeIcon);
                    } else if (userObject instanceof DecisionNode) {
                        setIcon(decisionNodeIcon);
                    } else if (userObject instanceof MergeNode) {
                        setIcon(mergeNodeIcon);
                    } else if (userObject instanceof ForkNode) {
                        setIcon(forkNodeIcon);
                    } else if (userObject instanceof JoinNode) {
                        setIcon(joinNodeIcon);
                    }
                } else {
                    setIcon(actorIcon);
                }

                nodeText = userObject.toString();
                setText(nodeText);
                setToolTipText("Activity Node - " + nodeText);
            } else if (userObject.equals("extends")) {
                setIcon(createImageIcon("generalization.gif"));
            } else if (userObject.equals("realizes")) {
                setIcon(createImageIcon("realization.gif"));
            } else {
                setToolTipText(null); // no tool tip
            }

            return this;
        }
    }

    // Below methods are used for remembering the tree expansion state for the Tree
    //
    // is path1 descendant of path2
    public static boolean isDescendant(TreePath path1, TreePath path2) {
        int count1 = path1.getPathCount();
        int count2 = path2.getPathCount();
        if (count1 <= count2) {
            return false;
        }
        while (count1 != count2) {
            path1 = path1.getParentPath();
            count1--;
        }
        return path1.equals(path2);
    }

    public static String getExpansionState(JTree tree, int row) {
        TreePath rowPath = tree.getPathForRow(row);
        StringBuffer buf = new StringBuffer();
        int rowCount = tree.getRowCount();
        for (int i = row; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (i == row || isDescendant(path, rowPath)) {
                if (tree.isExpanded(path)) {
                    buf.append("," + String.valueOf(i - row));
                }
            } else {
                break;
            }
        }
        return buf.toString();
    }

    public static void restoreExpanstionState(JTree tree, int row, String expansionState) {
        StringTokenizer stok = new StringTokenizer(expansionState, ",");
        while (stok.hasMoreTokens()) {
            int token = row + Integer.parseInt(stok.nextToken());
            tree.expandRow(token);
        }
    }

    private class RepositoryTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            /* if nothing is selected */
            if (node == null)
                return;

            /* retrieve the node that was selected */
            Object nodeInfo = node.getUserObject();

            if (DiagramModel.class.isAssignableFrom(nodeInfo.getClass())) {
                logger.finer("Diagram model selected");
                DiagramModel dm = (DiagramModel) nodeInfo;
                try {
                    DiagramInternalFrame frame = dm.getFrame();
                    frame.setIcon(false);
                    frame.moveToFront();
                    frame.show();
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(RepositoryTreeView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
