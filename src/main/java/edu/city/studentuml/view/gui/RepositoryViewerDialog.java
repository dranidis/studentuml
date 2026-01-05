package edu.city.studentuml.view.gui;

import edu.city.studentuml.model.domain.*;
import edu.city.studentuml.model.repository.CentralRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Non-blocking dialog that displays the current state of the CentralRepository
 * and logs all changes as they occur.
 * 
 * @author Dimitris Dranidis
 */
public class RepositoryViewerDialog extends JDialog implements PropertyChangeListener {

    private final CentralRepository repository;
    private final UMLProject umlProject;
    private final JTextArea changeLogArea;
    private final JTextArea stateArea;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public RepositoryViewerDialog(Frame owner, CentralRepository repository, UMLProject umlProject) {
        super(owner, "Repository Viewer", false); // false = non-modal
        this.repository = repository;
        this.umlProject = umlProject;

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create split pane with current state on left and change log on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left panel: Current repository state
        JPanel statePanel = new JPanel(new BorderLayout());
        statePanel.setBorder(new TitledBorder("Current Repository State"));

        stateArea = new JTextArea(20, 40);
        stateArea.setEditable(false);
        stateArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane stateScroll = new JScrollPane(stateArea);
        statePanel.add(stateScroll, BorderLayout.CENTER);

        // Right panel: Change log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("Change Log"));

        changeLogArea = new JTextArea(20, 40);
        changeLogArea.setEditable(false);
        changeLogArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(changeLogArea);
        logPanel.add(logScroll, BorderLayout.CENTER);

        JButton clearLogButton = new JButton("Clear Log");
        clearLogButton.addActionListener(e -> changeLogArea.setText(""));
        logPanel.add(clearLogButton, BorderLayout.SOUTH);

        splitPane.setLeftComponent(statePanel);
        splitPane.setRightComponent(logPanel);
        splitPane.setDividerLocation(450);

        add(splitPane, BorderLayout.CENTER);

        // Initial state update
        updateState();
        logChange("Repository Viewer opened");

        // Register as property change listener on UMLProject
        // java.lang.System.out.println("[RepositoryViewer] Registering listener..."); // DEBUG
        umlProject.addPropertyChangeListener(this);
        // java.lang.System.out.println("[RepositoryViewer] Listener registered!"); // DEBUG

        setSize(1000, 600);
        setLocationRelativeTo(owner);

        // Cleanup on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                umlProject.removePropertyChangeListener(RepositoryViewerDialog.this);
            }
        });
    }

    /**
     * Updates the current state display.
     */
    public void updateState() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== CENTRAL REPOSITORY STATE ===\n\n");

        appendCollection(sb, "Design Classes", repository.getClasses());
        appendCollection(sb, "Interfaces", repository.getInterfaces());
        appendCollection(sb, "Conceptual Classes", repository.getConceptualClasses());
        appendCollection(sb, "Actors", repository.getActors());
        appendCollection(sb, "Systems", repository.getSystems());
        appendCollection(sb, "Use Cases", repository.getUseCases());
        appendCollection(sb, "SD Objects", repository.getSdObjects());
        appendCollection(sb, "Multi Objects", repository.getMultiObjects());
        appendCollection(sb, "Actor Instances", repository.getActorInstances());
        appendCollection(sb, "System Instances", repository.getSystemInstances());
        appendCollection(sb, "Associations", repository.getAssociations());
        appendCollection(sb, "Aggregations", repository.getAggregations());
        appendCollection(sb, "Generalizations", repository.getGeneralizations());
        appendCollection(sb, "Dependencies", repository.getDependencies());
        appendCollection(sb, "Realizations", repository.getRealizations());
        appendCollection(sb, "SD Messages", repository.getSDMessages());

        sb.append("\n=== TOTAL COUNT ===\n");
        sb.append("Total elements: ").append(getTotalCount()).append("\n");

        stateArea.setText(sb.toString());
        stateArea.setCaretPosition(0);
    }

    /**
     * Logs a change to the change log area.
     */
    public void logChange(String message) {
        String timestamp = timeFormat.format(new Date());
        String logEntry = String.format("[%s] %s\n", timestamp, message);
        changeLogArea.append(logEntry);

        changeLogArea.setCaretPosition(changeLogArea.getDocument().getLength());
    }

    /**
     * gs an entity addition.
     */
    public void logAdd(String entityType, String entityName) {
        logChange(String.format("ADD %s: %s", entityType, entityName));
        updateState();
    }

    /**
     * Logs an entity edit.
     */
    public void logEdit(String entityType, String oldName, String newName) {
        logChange(String.format("EDIT %s: '%s' -> '%s'", entityType, oldName, newName));
        updateState();
    }

    /**
     * Logs an entity removal.
     */
    public void logRemove(String entityType, String entityName) {
        logChange(String.format("REMOVE %s: %s", entityType, entityName));
        updateState();
    }

    /**
     * Logs a type operation (add/remove type to/from repository).
     */
    public void logTypeOperation(String operation, String typeName) {
        logChange(String.format("TYPE %s: %s", operation, typeName));
        updateState();
    }

    private void appendCollection(StringBuilder sb, String label, java.util.Vector<?> collection) {
        // Skip empty collections
        if (collection.isEmpty()) {
            return;
        }

        for (Object obj : collection) {
            sb.append("  - ").append(getEntityDescription(obj)).append("\n");
        }
        sb.append("\n");
    }

    private String getEntityDescription(Object obj) {
        if (obj instanceof DesignClass) {
            DesignClass dc = (DesignClass) obj;
            return String.format("%s [%d methods, %d attributes]",
                    dc.getName(), dc.getMethods().size(), dc.getAttributes().size());
        } else if (obj instanceof Interface) {
            Interface iface = (Interface) obj;
            return String.format("%s [%d methods]", iface.getName(), iface.getMethods().size());
        } else if (obj instanceof ConceptualClass) {
            ConceptualClass cc = (ConceptualClass) obj;
            return String.format("%s [%d attributes]", cc.getName(), cc.getAttributes().size());
        } else if (obj instanceof Actor) {
            return ((Actor) obj).getName();
        } else if (obj instanceof edu.city.studentuml.model.domain.System) {
            return ((edu.city.studentuml.model.domain.System) obj).getName();
        } else if (obj instanceof UseCase) {
            return ((UseCase) obj).getName();
        } else if (obj instanceof SDObject) {
            SDObject sdo = (SDObject) obj;
            return String.format("%s : %s", sdo.getName(),
                    sdo.getDesignClass() != null ? sdo.getDesignClass().getName() : "null");
        } else if (obj instanceof MultiObject) {
            MultiObject mo = (MultiObject) obj;
            return String.format("%s : %s", mo.getName(),
                    mo.getDesignClass() != null ? mo.getDesignClass().getName() : "null");
        } else if (obj instanceof ActorInstance) {
            ActorInstance ai = (ActorInstance) obj;
            return String.format("%s : %s", ai.getName(),
                    ai.getActor() != null ? ai.getActor().getName() : "null");
        } else if (obj instanceof SystemInstance) {
            SystemInstance si = (SystemInstance) obj;
            return String.format("%s : %s", si.getName(),
                    si.getSystem() != null ? si.getSystem().getName() : "null");
        } else if (obj instanceof Association) {
            Association assoc = (Association) obj;
            return String.format("%s -- %s",
                    assoc.getClassA() != null ? assoc.getClassA().getName() : "null",
                    assoc.getClassB() != null ? assoc.getClassB().getName() : "null");
        } else if (obj instanceof Generalization) {
            Generalization gen = (Generalization) obj;
            return String.format("%s -> %s",
                    gen.getBaseClass() != null ? gen.getBaseClass().getName() : "null",
                    gen.getSuperClass() != null ? gen.getSuperClass().getName() : "null");
        } else if (obj instanceof SDMessage) {
            SDMessage msg = (SDMessage) obj;
            return String.format("%s()", msg.getName());
        }
        return obj.toString();
    }

    private int getTotalCount() {
        return repository.getClasses().size() +
                repository.getInterfaces().size() +
                repository.getConceptualClasses().size() +
                repository.getActors().size() +
                repository.getSystems().size() +
                repository.getUseCases().size() +
                repository.getSdObjects().size() +
                repository.getMultiObjects().size() +
                repository.getActorInstances().size() +
                repository.getSystemInstances().size() +
                repository.getAssociations().size() +
                repository.getAggregations().size() +
                repository.getGeneralizations().size() +
                repository.getDependencies().size() +
                repository.getRealizations().size() +
                repository.getSDMessages().size();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            logChange("Repository changed: " + evt.getPropertyName());
            updateState();
        });
    }
}
